variable "location" {
  type = string
  default = "eastus2"
}

# SQL Server

variable "sqlserver_version" {
  type    = string
  default = "12.0"
}

variable "sqlserver_max_size_gb" {
  type    = number
  default = 10
}

variable "sqlserver_sku_name" {
  type    = string
  default = "GP_S_Gen5_2"
}

variable "sqlserver_auto_pause_delay_in_minutes" {
  type    = number
  default = 60
}

variable "sqlserver_min_capacity" {
  type    = number
  default = 0.5
}

variable "sqlserver_zone_redundant" {
  type    = bool
  default = false
}

### App Resources ###
variable "app_cpu" {
  type    = number
  default = 1
}

variable "app_memory" {
  type    = string
  default = "2.0Gi"
}

variable "app_min_replicas" {
  type    = number
  default = 1
}

variable "app_max_replicas" {
  type    = number
  default = 20
}

variable "app_auto_scale_concurrent_requests" {
  type    = string
  default = "50"
}

variable "auto_scale_cpu" {
  type    = string
  default = "50"
}
