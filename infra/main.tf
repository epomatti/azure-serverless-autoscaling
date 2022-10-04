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
  sqlserver_allow_subnet_id             = var.sqlserver_allow_subnet_id
}

### Azure Monitor ###

module "monitor" {
  source   = "./modules/monitor"
  name     = local.project_affix
  location = var.location
  group    = azurerm_resource_group.default.name
}

### Container Apps - Environment ###

module "monitor" {
  source   = "./modules/managedenvironment"
  name     = local.project_affix
  location = var.location
  group    = azurerm_resource_group.default.name
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

  # Dapr
  # dapr_appId   = "books"
  # dapr_appPort = 8080

  # Container
  container_image = "epomatti/azure-sqlserverless-books"
  container_envs = [
    # { name = "DAPR_APP_PORT", value = "8080" },
    # { name = "DAPR_HTTP_PORT", value = "3500" },
    { name = "SQLSERVER_JDBC_URL", value = "jdbc:sqlserver://${azurerm_mssql_server.default.name}.database.windows.net:1433;database=${azurerm_mssql_database.default.name};user=${local.username}@${azurerm_mssql_server.default.name};password=${local.password};encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;" }
  ]
}

### Outputs ###

output "sqlserver_jdbc_url" {
  value = "jdbc:sqlserver://${azurerm_mssql_server.default.name}.database.windows.net:1433;database=${azurerm_mssql_database.default.name};user=${local.username}@${azurerm_mssql_server.default.name};password=${local.password};encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"
}

output "app_url" {
  value = "https://${module.containerapp_books.fqdn}"
}
