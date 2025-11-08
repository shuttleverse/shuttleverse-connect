FROM --platform=linux/arm64 maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy the pom.xml and .mvn separately to cache dependencies
COPY ./pom.xml ./
COPY .mvn .mvn/
COPY mvnw mvnw.cmd ./

RUN mvn dependency:go-offline

COPY . .

RUN mvn clean package -DskipTests

# Runtime image
FROM --platform=linux/arm64 eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/shuttleverse-connect.jar /app/shuttleverse-connect.jar

EXPOSE 8084

CMD ["java", "-jar", "/app/shuttleverse-connect.jar"]