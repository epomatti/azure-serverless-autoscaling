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
  }
}

### Locals ###

locals {
  project = "eventprocessor"
}

### Group ###

data "azurerm_resource_group" "default" {
  name = var.group
}

### Event Hub ###

resource "azurerm_eventhub_namespace" "default" {
  name                = "evhns-${local.project}"
  location            = data.azurerm_resource_group.default.location
  resource_group_name = data.azurerm_resource_group.default.name
  sku                 = var.sku
  capacity            = var.capacity
}

resource "azurerm_eventhub" "default" {
  name                = "evh-${local.project}"
  namespace_name      = azurerm_eventhub_namespace.default.name
  resource_group_name = data.azurerm_resource_group.default.name
  partition_count     = var.partition_count
  message_retention   = var.message_retention
}
