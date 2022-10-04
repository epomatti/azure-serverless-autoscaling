variable "name" {
  type = string
}

variable "location" {
  type = string
}

variable "group" {
  type = string
}

variable "dapr_appi_connection_string" {
  type = string
}

variable "log_analytics_workspace_id" {
  type = string
}

variable "log_analytics_workspace_shared_key" {
  type = string
}

variable "subnet_id" {
  type = string
}

resource "azapi_resource" "managed_environment" {
  name      = "managedenvironment-${var.name}"
  location  = var.location
  parent_id = var.group
  type      = "Microsoft.App/managedEnvironments@2022-03-01"

  body = jsonencode({
    properties = {
      daprAIConnectionString = var.dapr_appi_connection_string
      appLogsConfiguration = {
        destination = "log-analytics"
        logAnalyticsConfiguration = {
          customerId = var.log_analytics_workspace_id
          sharedKey  = var.log_analytics_workspace_shared_key
        }
      }
      vnetConfiguration = {
        internal               = false
        infrastructureSubnetId = var.subnet_id
        runtimeSubnetId        = var.subnet_id
      }
    }
  })
}
