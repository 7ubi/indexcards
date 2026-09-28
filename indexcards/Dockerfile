FROM maven:3.9-eclipse-temurin-21

WORKDIR /app
COPY . .
RUN mvn clean install -DskipTests
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "target/indexcards-2.2.1.jar"]