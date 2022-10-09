# App Development

Start the local database to be shared across all apps (local only):

```sh
docker run -e "ACCEPT_EULA=Y" -e "MSSQL_SA_PASSWORD=P4ssw0rd#777" -p 1433:1433 -d mcr.microsoft.com/mssql/server:2022-latest
```

To run an app:

```sh
mvn spring-boot:run -Dspring-boot.run.profiles=dev
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
az servicebus queue create -n 'orders' --namespace-name "bus-serverless-bookstore-dev" -g "rg-dev" --enable-partitioning
az servicebus queue create -n 'healthcheck' --namespace-name "bus-serverless-bookstore-dev" -g "rg-dev" --default-message-time-to-live "00:00:05"  --enable-partitioning

az servicebus namespace show -n "bus-serverless-bookstore-dev" -g "rg-dev"

export AZURE_SERVICEBUS_CONNECTION_STRING=$(az servicebus namespace authorization-rule keys list -g "rg-dev" --namespace-name "bus-serverless-bookstore-dev" --name "RootManageSharedAccessKey" --query "primaryConnectionString" -o tsv)
```

```sh
export AZURE_SERVICEBUS_CONNECTION_STRING="Endpoint=sb://{NAMESPACE}.servicebus.windows.net/;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey={SHARED_ACCESS_KEY"
```






https://learn.microsoft.com/en-us/azure/service-bus-messaging/service-bus-prefetch?tabs=java
https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/servicebus/azure-messaging-servicebus/docs/SyncReceiveAndPrefetch.md