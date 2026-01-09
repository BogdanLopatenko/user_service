FROM maven:3.9.6-eclipse-temurin-22 AS build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

RUN apt-get update && apt-get install -y curl

COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:22-jre-alpine

WORKDIR /app

RUN apk add --no-cache curl

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]