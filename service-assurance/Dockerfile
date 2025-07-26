FROM openjdk:17-jdk-slim

WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline

COPY src/ src/
RUN ./mvnw clean package -DskipTests

EXPOSE 8093

CMD ["java", "-jar", "target/service-assurance-0.0.1-SNAPSHOT.jar"]
