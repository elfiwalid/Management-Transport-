FROM openjdk:17-jdk-slim

WORKDIR /app

# Copier les fichiers Maven wrapper
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Télécharger les dépendances
RUN ./mvnw dependency:go-offline

# Copier le code source
COPY src/ src/

# Construire l'application
RUN ./mvnw clean package -DskipTests

# Exposer le port
EXPOSE 8761

# Commande de démarrage
CMD ["java", "-jar", "target/Discovery-0.0.1-SNAPSHOT.jar"]