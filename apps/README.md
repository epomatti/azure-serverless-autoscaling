# App Development

Start the local database to be shared across all apps (local only):

```sh
docker run -e "ACCEPT_EULA=Y" -e "MSSQL_SA_PASSWORD=P4ssw0rd#777" -p 1433:1433 -d mcr.microsoft.com/mssql/server:2022-latest
```

To run an app:

```sh
mvn spring-boot:run -Dspring-boot.run.profiles=dev -Dspring-boot.run.jvmArguments="-Dreactor.schedulers.defaultBoundedElasticSize=1000"
```

https://github.com/marcelohweb/netflix-microservices


https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry
https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-container-registry

```sh
GITHUB_REGISTRY_TOKEN="ghp_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
echo $GITHUB_REGISTRY_TOKEN | docker login ghcr.io -u epomatti --password-stdin

# Store
docker build . -t ghcr.io/epomatti/azure-serverless-bookstore-store --build-arg GITHUB_REGISTRY_TOKEN="$GITHUB_REGISTRY_TOKEN"
docker push ghcr.io/epomatti/azure-serverless-bookstore-store:latest

# Delivery
docker build . -t ghcr.io/epomatti/azure-serverless-bookstore-delivery --build-arg GITHUB_REGISTRY_TOKEN="$GITHUB_REGISTRY_TOKEN"
docker push ghcr.io/epomatti/azure-serverless-bookstore-delivery:latest
```



https://azuresdkdocs.blob.core.windows.net/$web/java/azure-messaging-servicebus/7.11.0/index.html


```sh
az group create -n "rg-dev" -l "eastus2"
az servicebus namespace create -n "bus-serverless-bookstore-dev" -g "rg-dev" -l "eastus2"
az servicebus queue create -n "invoice-create" --namespace-name "bus-serverless-bookstore-dev" -g "rg-dev" --enable-partitioning
az servicebus queue create -n "invoice-authorize" --namespace-name "bus-serverless-bookstore-dev" -g "rg-dev" --enable-partitioning
az servicebus queue create -n "invoice-authorized" --namespace-name "bus-serverless-bookstore-dev" -g "rg-dev" --enable-partitioning

export AZURE_SERVICEBUS_CONNECTION_STRING=$(az servicebus namespace authorization-rule keys list -g "rg-dev" --namespace-name "bus-serverless-bookstore-dev" --name "RootManageSharedAccessKey" --query "primaryConnectionString" -o tsv)
```

```sh
export AZURE_SERVICEBUS_CONNECTION_STRING="Endpoint=sb://{NAMESPACE}.servicebus.windows.net/;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey={SHARED_ACCESS_KEY}"
```

https://learn.microsoft.com/en-us/rest/api/servicebus/get-azure-active-directory-token
https://learn.microsoft.com/en-us/azure/service-bus-messaging/service-bus-prefetch?tabs=java
https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/servicebus/azure-messaging-servicebus/docs/SyncReceiveAndPrefetch.md

## Test

### Create Invoices Test

```json
{
  "orderId": 1,
  "items": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10] 
}
```

### Authorize Invoices


```sql
select id, order_id, status from invoice;

select count(*) from invoice where status = 'PENDING';

select count(*) from invoice where status = 'AUTHORIZED';

INSERT INTO invoice
  ( id, order_id, status )
VALUES
  (1, 1, 'PENDING'), 
  (2, 1, 'PENDING'), 
  (3, 1, 'PENDING'),
  (4, 1, 'PENDING'), 
  (5, 1, 'PENDING'), 
  (6, 1, 'PENDING'),
  (7, 1, 'PENDING'), 
  (8, 1, 'PENDING'), 
  (9, 1, 'PENDING'),
  (10, 1, 'PENDING');

delete from invoice where 1 = 1;
```

```sh
azurite -s -l /tmp/azurite
```