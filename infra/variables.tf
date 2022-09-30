variable "location" {
  type = string
}

# SQL Server

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
