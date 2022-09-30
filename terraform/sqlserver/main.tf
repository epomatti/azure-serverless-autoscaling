terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "3.24.0"
    }
    backend "local" {
      path = ".workspace/terraform.tfstate"
    }
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
  project = "eventprocessor"
}

### Group ###

resource "azurerm_resource_group" "default" {
  name     = "rg-${local.project}"
  location = var.location
}

### Service Bus ###

resource "azurerm_servicebus_namespace" "default" {
  name                = "bus-${local.project}"
  location            = azurerm_resource_group.default.location
  resource_group_name = azurerm_resource_group.default.name

  # Standard is required for Dapr to use topics
  sku = "Standard"
}

resource "azurerm_servicebus_topic" "default" {
  name                = "orders"
  namespace_id        = azurerm_servicebus_namespace.default.id
  enable_partitioning = true
}


### SQL Server ###

resource "azurerm_mssql_server" "default" {
  name                         = "sql-${local.project}"
  resource_group_name          = azurerm_resource_group.default.name
  location                     = azurerm_resource_group.default.location
  version                      = "12.0"
  administrator_login          = "dbadmin"
  administrator_login_password = "P4ssw0rd#777"
  minimum_tls_version          = "1.2"
}

resource "azurerm_mssql_database" "default" {
  name                        = "sqldb-${local.project}"
  server_id                   = azurerm_mssql_server.default.id
  max_size_gb                 = var.sqlserver_max_size_gb
  sku_name                    = var.sqlserver_sku_name
  auto_pause_delay_in_minutes = var.sqlserver_auto_pause_delay_in_minutes
  min_capacity                = var.sqlserver_min_capacity
  zone_redundant              = false
}


