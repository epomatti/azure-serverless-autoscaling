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

Add the database connection string to the session:

```sh
export SQLSERVER_JDBC_URL="<copy from terraform output>"
```

Start the application:

```sh
docker run -e "ACCEPT_EULA=Y" -e "MSSQL_SA_PASSWORD=P4ssw0rd#777" -p 1433:1433 -d mcr.microsoft.com/mssql/server:2022-latest

mvn spring-boot:run -Dspring-boot.run.profiles=dev
```


```sh
k6 run \
    -e HOST_URL="http://localhost:8080" \
    --vus 1 \
    --duration 30s \
    http_post.js
```

```sh
cd app

docker build . -t epomatti/azure-sqlserverless-books
docker login --username=epomatti
docker push epomatti/azure-sqlserverless-books
```



```sh
docker run -it --rm \
    -e SQLSERVER_JDBC_URL="jdbc:sqlserver://sql-autoscale-2996.database.windows.net:1433;database=sqldb-autoscale-2996;user=dbadmin@sql-autoscale-2996;password=P4ssw0rd#777;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;" \
    -p 8080:8080 \
    -t epomatti/azure-sqlserverless-books
```


https://www.docker.com/blog/9-tips-for-containerizing-your-spring-boot-code/

https://techcommunity.microsoft.com/t5/apps-on-azure-blog/azure-container-apps-virtual-network-integration/ba-p/3096932

https://learn.microsoft.com/en-us/rest/api/containerapps/managed-environments/create-or-update?tabs=HTTP#vnetconfiguration

https://sameeraman.wordpress.com/2019/10/30/azure-private-link-vs-azure-service-endpoints/