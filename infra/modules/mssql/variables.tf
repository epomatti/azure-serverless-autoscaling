variable "name" {
  type = string
}

variable "group" {
  type = string
}

variable "location" {
  type = string
}

variable "sqlserver_version" {
  type = string
}

variable "sqlserver_max_size_gb" {
  type = number
}

variable "sqlserver_sku_name" {
  type = string
}

variable "sqlserver_auto_pause_delay_in_minutes" {
  type = number
}

variable "sqlserver_min_capacity" {
  type = number
}

variable "sqlserver_zone_redundant" {
  type = bool
}

variable "virtual_network_id" {
  type        = string
  description = "Required to be referenced by the Private Endpoint"
}

variable "sqlserver_infrastructure_subnet_id" {
  type = string
}
