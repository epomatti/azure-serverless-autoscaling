locals {
  username = "dbadmin"
  password = "P4ssw0rd#777"
}

resource "azurerm_mssql_server" "default" {
  name                          = "sql-${var.name}"
  location                      = var.location
  resource_group_name           = var.group
  version                       = var.sqlserver_version
  administrator_login           = local.username
  administrator_login_password  = local.password
  minimum_tls_version           = "1.2"
  public_network_access_enabled = true
}

resource "azurerm_mssql_firewall_rule" "allow_internal" {
  name             = "FirewallRuleAllowAzureInternalAll"
  server_id        = azurerm_mssql_server.default.id
  start_ip_address = "0.0.0.0"
  end_ip_address   = "0.0.0.0"
}

resource "azurerm_mssql_database" "default" {
  name                        = "sqldb-${var.name}"
  server_id                   = azurerm_mssql_server.default.id
  max_size_gb                 = var.sqlserver_max_size_gb
  sku_name                    = var.sqlserver_sku_name
  auto_pause_delay_in_minutes = var.sqlserver_auto_pause_delay_in_minutes
  min_capacity                = var.sqlserver_min_capacity
  zone_redundant              = false
}

resource "azurerm_mssql_virtual_network_rule" "allow" {
  name      = "sql-vnet-rule"
  server_id = azurerm_mssql_server.default.id
  subnet_id = var.sqlserver_allow_subnet_id
}

### Outputs ###

output "jdbc_url" {
  value = "jdbc:sqlserver://${azurerm_mssql_server.default.name}.database.windows.net:1433;database=${azurerm_mssql_database.default.name};user=${local.username}@${azurerm_mssql_server.default.name};password=${local.password};encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"
}
