# Etapa 1: Construcción con Maven
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Copia archivos de construcción primero para cachear dependencias
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Da permisos de ejecución al wrapper de Maven
RUN chmod +x ./mvnw

# Pre-descarga dependencias
RUN ./mvnw dependency:go-offline

# Copia el código fuente y recursos necesarios para el build
COPY src ./src
COPY media ./media

# Compila el proyecto sin correr tests
RUN ./mvnw clean package -DskipTests

# Etapa 2: Imagen final ligera
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copia el JAR desde la etapa de construcción
COPY --from=builder /app/target/*.jar ./app.jar

# Copia media si se necesita en tiempo de ejecución
COPY --from=builder /app/media ./media

# Expone el puerto de la aplicación
EXPOSE 8080

# Ejecuta la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
