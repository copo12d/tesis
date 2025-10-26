FROM maven:3.9-eclipse-temurin-21

WORKDIR /app

COPY pom.xml .

COPY mvnw .
COPY .mvn .mvn

COPY src ./src

COPY media ./media

EXPOSE 8080

CMD ["./mvnw", "spring-boot:run"]
