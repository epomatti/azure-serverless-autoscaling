terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "3.24.0"
    }
    azapi = {
      source  = "Azure/azapi"
      version = "0.6.0"
    }
  }
  backend "local" {
    path = ".workspace/terraform.tfstate"
  }
}

provider "azurerm" {
  features {
    resource_group {
      prevent_deletion_if_contains_resources = false
    }
  }
}

### Locals ###

locals {
  name          = "serverless-autoscale"
  project_affix = "${local.name}-${random_integer.affix.result}"
}

resource "random_integer" "affix" {
  min = 1000
  max = 9999
}

### Group ###

resource "azurerm_resource_group" "default" {
  name     = "rg-${local.project_affix}"
  location = var.location
}

### Network ###

module "network" {
  source              = "./modules/network"
  project             = local.project_affix
  location            = var.location
  resource_group_name = azurerm_resource_group.default.name
}

### SQL Server ###

module "mssql" {
  source = "./modules/mssql"

  name     = local.project_affix
  location = var.location
  group    = azurerm_resource_group.default.name

  sqlserver_version                     = var.sqlserver_version
  sqlserver_max_size_gb                 = var.sqlserver_max_size_gb
  sqlserver_sku_name                    = var.sqlserver_sku_name
  sqlserver_auto_pause_delay_in_minutes = var.sqlserver_auto_pause_delay_in_minutes
  sqlserver_min_capacity                = var.sqlserver_min_capacity
  sqlserver_zone_redundant              = var.sqlserver_zone_redundant
  sqlserver_allow_subnet_id             = module.network.runtime_subnet_id
}

### Azure Monitor ###

resource "azurerm_log_analytics_workspace" "default" {
  name                = "log-${local.project_affix}"
  location            = var.location
  resource_group_name = azurerm_resource_group.default.name
  sku                 = "PerGB2018"
}

resource "azurerm_application_insights" "default" {
  name                = "appi-${local.project_affix}"
  location            = var.location
  resource_group_name = azurerm_resource_group.default.name
  application_type    = "web"
  workspace_id        = azurerm_log_analytics_workspace.default.id
}

### Container Apps - Environment ###

resource "azapi_resource" "managed_environment" {
  name      = "managedenvironment-${local.project_affix}"
  location  = var.location
  parent_id = azurerm_resource_group.default.id
  type      = "Microsoft.App/managedEnvironments@2022-03-01"

  body = jsonencode({
    properties = {
      daprAIConnectionString = azurerm_application_insights.default.connection_string
      appLogsConfiguration = {
        destination = "log-analytics"
        logAnalyticsConfiguration = {
          customerId = azurerm_log_analytics_workspace.default.workspace_id
          sharedKey  = azurerm_log_analytics_workspace.default.primary_shared_key
        }
      }
      vnetConfiguration = {
        internal               = false
        infrastructureSubnetId = module.network.infrastructure_subnet_id
        runtimeSubnetId        = module.network.runtime_subnet_id
      }
    }
  })
}

### Application Apps - Services ###

module "containerapp_books" {
  source = "./modules/containerapp"

  # Container App
  name        = "app-books"
  location    = var.location
  group_id    = azurerm_resource_group.default.id
  environment = azapi_resource.managed_environment.id

  # Ingress
  external            = true
  ingress_target_port = 8080

  # Resources
  cpu    = var.app_cpu
  memory = var.app_memory

  # Container
  container_image = "epomatti/azure-sqlserverless-books"
  container_envs = [
    { name = "SQLSERVER_JDBC_URL", value = module.mssql.jdbc_url }
  ]
}

### Outputs ###

output "sqlserver_jdbc_url" {
  value = module.mssql.jdbc_url
}

output "app_url" {
  value = "https://${module.containerapp_books.fqdn}"
}
