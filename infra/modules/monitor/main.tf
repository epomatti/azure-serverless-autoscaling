variable "name" {
  type = string
}

variable "location" {
  type = string
}

variable "group" {
  type = string
}

resource "azurerm_log_analytics_workspace" "default" {
  name                = "log-${var.name}"
  location            = var.location
  resource_group_name = var.group
  sku                 = "PerGB2018"
}

resource "azurerm_application_insights" "default" {
  name                = "appi-${local.name}"
  location            = var.location
  resource_group_name = var.group
  application_type    = "web"
  workspace_id        = azurerm_log_analytics_workspace.default.id
}
