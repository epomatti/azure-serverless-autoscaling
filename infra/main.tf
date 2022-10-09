terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "3.26.0"
    }
    azapi = {
      source  = "Azure/azapi"
      version = "1.0.0"
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
  project_affix = "serverless-bookstore${random_integer.affix.result}"
}

resource "random_integer" "affix" {
  min = 100
  max = 999
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
  virtual_network_id                    = module.network.virtual_network_id
  sqlserver_infrastructure_subnet_id    = module.network.infrastructure_subnet_id
}

### Service Bus ###

module "servicebus" {
  source        = "./modules/servicebus"
  location      = var.location
  group         = azurerm_resource_group.default.name
  project_affix = local.project_affix
}

### Azure Monitor ###

resource "azurerm_log_analytics_workspace" "default" {
  name                = "log-${local.project_affix}"
  location            = var.location
  resource_group_name = azurerm_resource_group.default.name
  sku                 = "PerGB2018"
}

resource "azurerm_application_insights" "containerapps_dapr" {
  name                = "appi-${local.project_affix}-containerapps-dapr"
  location            = var.location
  resource_group_name = azurerm_resource_group.default.name
  application_type    = "web"
  workspace_id        = azurerm_log_analytics_workspace.default.id
}

resource "azurerm_application_insights" "bookstore" {
  name                = "appi-${local.project_affix}-bookstore-microservices"
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
      daprAIConnectionString = azurerm_application_insights.containerapps_dapr.connection_string
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

module "containerapp_store" {
  source = "./modules/containerapp"

  # Container App
  name        = "app-store"
  location    = var.location
  group_id    = azurerm_resource_group.default.id
  environment = azapi_resource.managed_environment.id

  # Ingress
  external            = true
  ingress_target_port = 8080

  # Resources
  cpu                            = var.app_cpu
  memory                         = var.app_memory
  min_replicas                   = var.app_min_replicas
  max_replicas                   = var.app_max_replicas
  auto_scale_concurrent_requests = var.app_auto_scale_concurrent_requests
  auto_scale_cpu                 = var.auto_scale_cpu

  # Container
  container_image = "ghcr.io/epomatti/azure-serverless-bookstore-store:latest"
  container_envs = [
    { name = "SQLSERVER_JDBC_URL", value = module.mssql.jdbc_private_url_store },
    { name = "AZURE_SERVICEBUS_CONNECTION_STRING", value = module.servicebus.store_app_connection_string },
    { name = "APPLICATIONINSIGHTS_CONNECTION_STRING", value = azurerm_application_insights.bookstore.connection_string }
  ]
}

module "containerapp_delivery" {
  source = "./modules/containerapp"

  # Container App
  name        = "app-delivery"
  location    = var.location
  group_id    = azurerm_resource_group.default.id
  environment = azapi_resource.managed_environment.id

  # Ingress
  external            = true
  ingress_target_port = 8080

  # Resources
  cpu                            = var.app_cpu
  memory                         = var.app_memory
  min_replicas                   = var.app_min_replicas
  max_replicas                   = var.app_max_replicas
  auto_scale_concurrent_requests = var.app_auto_scale_concurrent_requests
  auto_scale_cpu                 = var.auto_scale_cpu

  # Container
  container_image = "ghcr.io/epomatti/azure-serverless-bookstore-delivery:latest"
  container_envs = [
    { name = "SQLSERVER_JDBC_URL", value = module.mssql.jdbc_private_url_delivery },
    { name = "AZURE_SERVICEBUS_CONNECTION_STRING", value = module.servicebus.delivery_app_connection_string },
    { name = "AZURE_SERVICEBUS_PREFETCH_COUNT", value = var.azure_servicebus_prefetch_count },
    { name = "AZURE_SERVICEBUS_MAX_CONCURRENT_CALLS", value = var.azure_servicebus_max_concurrent_calls },
    { name = "APPLICATIONINSIGHTS_CONNECTION_STRING", value = azurerm_application_insights.bookstore.connection_string }
  ]
}

### Outputs ###

output "sqlserver_jdbc_public_url_store" {
  value = module.mssql.jdbc_public_url_store
}

output "app_url" {
  value = "https://${module.containerapp_store.fqdn}"
}
