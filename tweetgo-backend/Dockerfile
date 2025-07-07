# Stage 1: Build the application using Maven
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml /build/pom.xml
COPY .mvn /build/.mvn
RUN mvn dependency:go-offline -B
COPY src /build/src
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image using a slim JRE
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/target/tweetgo-backend-0.0.1-SNAPSHOT.jar tweetgo-backend.jar
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java \
-Dspring.datasource.url=${SPRING_DATASOURCE_URL} \
-Dspring.datasource.username=${SPRING_DATASOURCE_USERNAME} \
-Dspring.datasource.password=${SPRING_DATASOURCE_PASSWORD} \
-jar tweetgo-backend.jar"]