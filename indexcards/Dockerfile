FROM maven:3.6.3-jdk-11

WORKDIR /app
COPY . .
RUN mvn clean install -DskipTests
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "target/indexcards-1.0.0.jar"]