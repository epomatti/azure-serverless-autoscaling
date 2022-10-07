# syntax=docker/dockerfile:1

FROM eclipse-temurin:17-jdk-jammy as builder
WORKDIR /opt/app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY ./src ./src
RUN ./mvnw clean install


FROM eclipse-temurin:17-jre-jammy
WORKDIR /opt/app
EXPOSE 8080
COPY --from=builder /opt/app/target/*.jar /opt/app/*.jar
RUN mkdir /opt/appi
RUN curl -L https://github.com/microsoft/ApplicationInsights-Java/releases/download/3.4.1/applicationinsights-agent-3.4.1.jar --output /opt/appi/applicationinsights-agent-3.4.1.jar
ENTRYPOINT ["java", "-javaagent:/opt/appi/applicationinsights-agent-3.4.1.jar", "-jar", "/opt/app/*.jar" ]
