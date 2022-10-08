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
docker build . -t ghcr.io/epomatti/azure-serverless-bookstore-store --build-arg GITHUB_REGISTRY_TOKEN="$GITHUB_REGISTRY_TOKEN"
```


docker build -t epomatti/azure-serverless-bookstore-store -f store/Dockerfile .



echo $GITHUB_REGISTRY_TOKEN | docker login ghcr.io -u epomatti --password-stdin
docker login ghcr.io -u epomatti --password-stdin
docker login ghcr.io -u epomatti

login with pat blalbjasd

docker push ghcr.io/epomatti/azure-serverless-bookstore-store:latest