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
  project = "autoscale"
  affix   = "${local.project}-${random_integer.affix.result}"

  username = "dbadmin"
  password = "P4ssw0rd#777"
}

resource "random_integer" "affix" {
  min = 1000
  max = 9999
}

### Group ###

resource "azurerm_resource_group" "default" {
  name     = "rg-sqldatabase-serverless-autoscale-${random_integer.affix.result}"
  location = var.location
}

### Network ###

module "network" {
  source              = "./modules/network"
  project             = local.affix
  location            = var.location
  resource_group_name = azurerm_resource_group.default.name
}

### SQL Server ###

resource "azurerm_mssql_server" "default" {
  name                          = "sql-${local.affix}"
  location                      = azurerm_resource_group.default.location
  resource_group_name           = azurerm_resource_group.default.name
  version                       = var.sqlserver_version
  administrator_login           = local.username
  administrator_login_password  = local.password
  minimum_tls_version           = "1.2"
  public_network_access_enabled = true
}

resource "azurerm_mssql_firewall_rule" "allow_internal" {
  name             = "FirewallRuleAllowAzureInternalAll"
  server_id        = azurerm_mssql_server.default.id
  start_ip_address = "0.0.0.0"
  end_ip_address   = "0.0.0.0"
}

resource "azurerm_mssql_database" "default" {
  name                        = "sqldb-${local.affix}"
  server_id                   = azurerm_mssql_server.default.id
  max_size_gb                 = var.sqlserver_max_size_gb
  sku_name                    = var.sqlserver_sku_name
  auto_pause_delay_in_minutes = var.sqlserver_auto_pause_delay_in_minutes
  min_capacity                = var.sqlserver_min_capacity
  zone_redundant              = false
}

resource "azurerm_mssql_virtual_network_rule" "containerapps_runtime" {
  name      = "sql-vnet-rule-containerapps-runtime"
  server_id = azurerm_mssql_server.default.id
  subnet_id = module.network.runtime_subnet_id
}

### Azure Monitor ###

resource "azurerm_log_analytics_workspace" "default" {
  name                = "log-${local.project}"
  location            = azurerm_resource_group.default.location
  resource_group_name = azurerm_resource_group.default.name
  sku                 = "PerGB2018"
}

resource "azurerm_application_insights" "dapr" {
  name                = "appi-${local.project}-dapr"
  location            = azurerm_resource_group.default.location
  resource_group_name = azurerm_resource_group.default.name
  application_type    = "web"
  workspace_id        = azurerm_log_analytics_workspace.default.id
}

# resource "azurerm_application_insights" "apps" {
#   name                = "appi-${local.project}-apps"
#   location            = azurerm_resource_group.default.location
#   resource_group_name = azurerm_resource_group.default.name
#   application_type    = "web"
#   workspace_id        = azurerm_log_analytics_workspace.default.id
# }

### Container Apps - Environment ###

resource "azapi_resource" "managed_environment" {
  name      = "env-${local.project}-${random_integer.affix.result}"
  location  = azurerm_resource_group.default.location
  parent_id = azurerm_resource_group.default.id
  type      = "Microsoft.App/managedEnvironments@2022-03-01"

  body = jsonencode({
    properties = {
      daprAIConnectionString = azurerm_application_insights.dapr.connection_string
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

output "order_url" {
  value = "https://${module.containerapp_books.fqdn}"
}
