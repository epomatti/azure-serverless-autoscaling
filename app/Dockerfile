# syntax=docker/dockerfile:1

FROM eclipse-temurin:17-jdk-jammy

RUN wget -O dd-java-agent.jar 'https://dtdg.co/latest-java-tracer'

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:resolve

COPY src ./src
COPY application-prod.yaml ./

ENV MAVEN_OPTS="-javaagent:$PWD/dd-java-agent.jar"
CMD ["./mvnw", "spring-boot:run", "-Dspring-boot.run.profiles=prod"]
