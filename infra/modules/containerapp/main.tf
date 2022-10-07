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
}

resource "azapi_resource" "container_app" {
  name      = var.name
  location  = var.location
  parent_id = var.group_id
  type      = "Microsoft.App/containerApps@2022-03-01"

  response_export_values = ["properties.configuration.ingress.fqdn"]

  body = jsonencode({
    properties : {
      managedEnvironmentId = var.environment
      configuration = {
        ingress = {
          external   = var.external
          targetPort = var.ingress_target_port
        }
        # dapr = {
        #   enabled     = true
        #   appId       = var.dapr_appId
        #   appPort     = var.dapr_appPort
        #   appProtocol = "http"
        # }
      }
      template = {
        containers = [
          {
            name  = var.name
            image = var.container_image
            resources = {
              cpu    = var.cpu
              memory = var.memory
            }
            env = var.container_envs
            probes = [
              {
                type = "Liveness"
                httpGet = {
                  path = "/actuator/health"
                  port = var.ingress_target_port
                  httpHeaders = [
                    {
                      name  = "Custom-Header"
                      value = "Awesome"
                    }
                  ]
                }
                initialDelaySeconds = 60
                periodSeconds       = 20
              }
            ]
          }
        ]
        scale = {
          minReplicas = var.min_replicas
          maxReplicas = var.max_replicas
          rules = [
            # {
            #   name = "httpscalingrule"
            #   custom = {
            #     type = "http"
            #     metadata = {
            #       concurrentRequests = var.auto_scale_concurrent_requests
            #     }
            #   }
            # },
            {
              name = "cpuscalingrule"
              custom = {
                type = "cpu"
                metadata = {
                  type  = "Utilization"
                  value = var.auto_scale_cpu
                }
              }
            }
          ]
        }
      }
    }
  })
}
