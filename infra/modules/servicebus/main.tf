variable "location" {
  type = string
}

variable "group" {
  type = string
}

variable "project_affix" {
  type = string
}

resource "azurerm_servicebus_namespace" "default" {
  name                = "bus-${var.project_affix}"
  location            = var.location
  resource_group_name = var.group
  sku                 = "Standard"
}

resource "azurerm_servicebus_queue" "orders" {
  name                = "orders"
  namespace_id        = azurerm_servicebus_namespace.default.id
  enable_partitioning = true
}

resource "azurerm_servicebus_queue" "healthcheck" {
  name                = "healthcheck"
  namespace_id        = azurerm_servicebus_namespace.default.id
  enable_partitioning = true
}

resource "azurerm_servicebus_namespace_authorization_rule" "store_app" {
  name         = "store-app"
  namespace_id = azurerm_servicebus_namespace.default.id

  listen = false
  send   = true
  manage = false
}

resource "azurerm_servicebus_namespace_authorization_rule" "delivery_app" {
  name         = "delivery-app"
  namespace_id = azurerm_servicebus_namespace.default.id

  listen = true
  send   = false
  manage = false
}

output "store_app_connection_string" {
  value = azurerm_servicebus_namespace_authorization_rule.store_app.primary_connection_string
}

output "delivery_app_connection_string" {
  value = azurerm_servicebus_namespace_authorization_rule.delivery_app.primary_connection_string
}
