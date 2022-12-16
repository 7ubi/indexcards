FROM maven:3.6.3-jdk-11

WORKDIR /app
COPY . .
RUN mvn clean install -DskipTests
ADD target/indexcards-0.0.1-SNAPSHOT.jar indexcards-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "indexcards-0.0.1-SNAPSHOT.jar"]