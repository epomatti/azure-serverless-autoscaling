# azure-events-autoscaling


```hcl
# Resources
location = "eastus"

# SQL Server
sqlserver_version                     = "12.0"
sqlserver_max_size_gb                 = 1
sqlserver_sku_name                    = "GP_S_Gen5_1"
sqlserver_auto_pause_delay_in_minutes = 60
sqlserver_min_capacity                = 0.5
sqlserver_zone_redundant              = false
```

After the database is created, add you IP to the firewall to enable external access.

Start the application:

```sh
mvn spring-boot:run
```