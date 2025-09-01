FROM maven:3.9-eclipse-temurin-21

# Establece el directorio de trabajo
WORKDIR /app

# Copia los archivos de configuración de Maven
COPY pom.xml .

# Copia todo el código fuente del proyecto
COPY src ./src

EXPOSE 8080

# Este es el comando que inicia la aplicación en modo desarrollo
CMD ["./mvnw", "spring-boot:run"]
