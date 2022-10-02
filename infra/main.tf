terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "3.24.0"
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
}

resource "random_integer" "affix" {
  min = 1000
  max = 9999
}

### Group ###

resource "azurerm_resource_group" "default" {
  name     = "rg-sqldatabase-serverless-autoscale"
  location = var.location
}

### SQL Server ###

resource "azurerm_mssql_server" "default" {
  name                          = "sql-${local.affix}"
  location                      = azurerm_resource_group.default.location
  resource_group_name           = azurerm_resource_group.default.name
  version                       = var.sqlserver_version
  administrator_login           = "dbadmin"
  administrator_login_password  = "P4ssw0rd#777"
  minimum_tls_version           = "1.2"
  public_network_access_enabled = true
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
