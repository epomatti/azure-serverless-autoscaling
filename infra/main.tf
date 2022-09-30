terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "3.24.0"
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
  max_size_gb                 = 1
  sku_name                    = "GP_S_Gen5_1"
  auto_pause_delay_in_minutes = 60
  min_capacity                = 1
  zone_redundant              = false
}


