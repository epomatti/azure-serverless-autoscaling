# syntax=docker/dockerfile:1

FROM eclipse-temurin:17-jdk-jammy as builder
WORKDIR /opt/app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
# Set up Github registry
ARG GITHUB_REGISTRY_TOKEN
ENV GITHUB_REGISTRY_TOKEN=$GITHUB_REGISTRY_TOKEN
COPY settings.xml ./
# Build the thing
RUN ./mvnw dependency:go-offline -s settings.xml
COPY ./src ./src
RUN ./mvnw clean install -s settings.xml
# Application Insights
RUN mkdir /opt/appi
RUN curl -L https://github.com/microsoft/ApplicationInsights-Java/releases/download/3.4.1/applicationinsights-agent-3.4.1.jar --output /opt/appi/appi-agent.jar
COPY ./applicationinsights.json /opt/appi/


FROM eclipse-temurin:17-jre-jammy
LABEL org.opencontainers.image.source https://github.com/epomatti/azure-serverless-autoscaling
WORKDIR /opt/app
EXPOSE 8081
COPY --from=builder /opt/app/target/*.jar /opt/app/*.jar
COPY --from=builder /opt/appi /opt/appi
ENTRYPOINT ["java", "-javaagent:/opt/appi/appi-agent.jar", "-jar", "/opt/app/*.jar" ]
