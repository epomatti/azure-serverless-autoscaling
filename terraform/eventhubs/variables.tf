variable "location" {
  type    = string
  default = "eastus"
}

variable "group" {
  type    = string
  default = "rg-eventprocessor"
}

variable "sku" {
  type    = string
  default = "Basic"
}

variable "capacity" {
  type    = number
  default = 1
}

variable "partition_count" {
  type    = number
  default = 2
}

variable "message_retention" {
  type    = number
  default = 1
}
