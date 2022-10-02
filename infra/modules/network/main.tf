terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "3.24.0"
    }
  }
}

### Variables ###

variable "project" {
  type = string
}

variable "location" {
  type = string
}

variable "resource_group_name" {
  type = string
}

### Resources ###

resource "azurerm_virtual_network" "default" {
  name                = "vnet-${var.project}"
  location            = var.location
  resource_group_name = var.resource_group_name
  address_space       = ["10.0.0.0/8"]
}

resource "azurerm_subnet" "all" {
  name                 = "subnet-all"
  resource_group_name  = var.resource_group_name
  virtual_network_name = azurerm_virtual_network.default.name
  address_prefixes     = ["10.200.0.0/16"]

  # Enable for SQL
  service_endpoints = ["Microsoft.Sql"]
}

### Output ###

output "subnet_all_id" {
  value = azurerm_subnet.all.id
}
