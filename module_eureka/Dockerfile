FROM openjdk:17

# Set environment variables
ENV APP_DIR=/opt/app/eureka/classes

# Set working directory
WORKDIR ${APP_DIR}

# Copy the built JAR file into the container
COPY build/libs/app.jar ${APP_DIR}/app.jar

# Expose the port on which the application will run
EXPOSE 8761

# Command to run the application
CMD ["java", "-jar", "app.jar"]

