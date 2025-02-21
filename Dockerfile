# Use a smaller base image (JRE-only, optimized for production)
FROM eclipse-temurin:17-jre-alpine

# Information around who maintains the image
# LABEL basyir

# Set the working directory
WORKDIR /app

# Copy the JAR file into the image (avoid hardcoding the version)
COPY target/*.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080 8081

# Command to execute the application
ENTRYPOINT ["java", "-jar", "app.jar"]