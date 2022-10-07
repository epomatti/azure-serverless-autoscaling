# syntax=docker/dockerfile:1

FROM eclipse-temurin:17-jdk-jammy as builder
WORKDIR /opt/app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY ./src ./src
RUN ./mvnw clean install
# Application Insights
RUN mkdir /opt/appi
RUN curl -L https://github.com/microsoft/ApplicationInsights-Java/releases/download/3.4.1/applicationinsights-agent-3.4.1.jar --output /opt/appi/appi-agent.jar


FROM eclipse-temurin:17-jre-jammy
WORKDIR /opt/app
EXPOSE 8080
COPY --from=builder /opt/app/target/*.jar /opt/app/*.jar
COPY --from=builder /opt/appi /opt/appi
ENTRYPOINT ["java", "-javaagent:/opt/appi/applicationinsights-agent-3.4.1.jar", "-jar", "/opt/app/*.jar" ]
