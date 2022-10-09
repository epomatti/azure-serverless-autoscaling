locals {
  username = "dbadmin"
  password = "P4ssw0rd#777"
}

### SQL Server ###

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

### Databases ###

resource "azurerm_mssql_database" "store_app" {
  name                        = "sqldb-${var.name}-store"
  server_id                   = azurerm_mssql_server.default.id
  max_size_gb                 = var.sqlserver_max_size_gb
  sku_name                    = var.sqlserver_sku_name
  auto_pause_delay_in_minutes = var.sqlserver_auto_pause_delay_in_minutes
  min_capacity                = var.sqlserver_min_capacity
  zone_redundant              = false
}

resource "azurerm_mssql_database" "delivery_app" {
  name                        = "sqldb-${var.name}-delivery"
  server_id                   = azurerm_mssql_server.default.id
  max_size_gb                 = var.sqlserver_max_size_gb
  sku_name                    = var.sqlserver_sku_name
  auto_pause_delay_in_minutes = var.sqlserver_auto_pause_delay_in_minutes
  min_capacity                = var.sqlserver_min_capacity
  zone_redundant              = false
}

### Private DNS Zone ###

resource "azurerm_private_dns_zone" "default" {
  name                = "privatelink.database.windows.net"
  resource_group_name = var.group
}

resource "azurerm_private_dns_zone_virtual_network_link" "default" {
  name                  = "sqldatabaselink"
  resource_group_name   = var.group
  private_dns_zone_name = azurerm_private_dns_zone.default.name
  virtual_network_id    = var.virtual_network_id
  registration_enabled  = true
}

### Private Endpoint ###

resource "azurerm_private_endpoint" "default" {
  name                = "pep-sqlserver"
  location            = var.location
  resource_group_name = var.group
  subnet_id           = var.sqlserver_infrastructure_subnet_id

  private_dns_zone_group {
    name = azurerm_private_dns_zone.default.name
    private_dns_zone_ids = [
      azurerm_private_dns_zone.default.id
    ]
  }

  private_service_connection {
    name                           = "sqlserver"
    private_connection_resource_id = azurerm_mssql_server.default.id
    is_manual_connection           = false
    subresource_names              = ["sqlServer"]
  }
}

### Outputs ###

output "jdbc_public_url_store" {
  value = "jdbc:sqlserver://${azurerm_mssql_server.default.name}.database.windows.net:1433;database=${azurerm_mssql_database.store_app.name};user=${local.username}@${azurerm_mssql_server.default.name};password=${local.password};encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"
}

output "jdbc_private_url_store" {
  value = "jdbc:sqlserver://${azurerm_mssql_server.default.name}.privatelink.database.windows.net:1433;database=${azurerm_mssql_database.store_app.name};user=${local.username}@${azurerm_mssql_server.default.name};password=${local.password};encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"
}

output "jdbc_public_url_delivery" {
  value = "jdbc:sqlserver://${azurerm_mssql_server.default.name}.database.windows.net:1433;database=${azurerm_mssql_database.delivery_app.name};user=${local.username}@${azurerm_mssql_server.default.name};password=${local.password};encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"
}

output "jdbc_private_url_delivery" {
  value = "jdbc:sqlserver://${azurerm_mssql_server.default.name}.privatelink.database.windows.net:1433;database=${azurerm_mssql_database.delivery_app.name};user=${local.username}@${azurerm_mssql_server.default.name};password=${local.password};encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"
}
