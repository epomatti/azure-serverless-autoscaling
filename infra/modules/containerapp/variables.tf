### Container App ###

variable "group_id" {
  type = string
}

variable "location" {
  type = string
}

variable "name" {
  type = string
}

variable "environment" {
  type = string
}

### Ingress ###

variable "external" {
  type    = bool
  default = true
}

variable "ingress_target_port" {
  type = number
}

### Dapr ###

# variable "dapr_appId" {
#   type = string
# }

# variable "dapr_appPort" {
#   type = number
# }

### Resources ###
variable "cpu" {
  type = number
}

variable "memory" {
  type = string
}

variable "min_replicas" {
  type = number
}

variable "max_replicas" {
  type = number
}

variable "auto_scale_concurrent_requests" {
  type = string
}

variable "auto_scale_cpu" {
  type = string
}

### Container ###

variable "container_image" {
  type = string
}

variable "container_envs" {
  type = list(object({
    name  = string
    value = string
  }))
  default = []
}
