# Utilizamos una imagen base con Maven y Java 17 para construir el .jar
FROM maven:3.6.3-openjdk-17 AS build

# Configuramos el directorio de trabajo
WORKDIR /app

# Copiamos los archivos de proyecto Maven al contenedor
COPY pom.xml .
COPY src ./src

# Compilamos el proyecto y construimos el .jar
RUN mvn -X clean package

# Utilizamos una imagen más ligera para ejecutar la aplicación
FROM openjdk:17-jdk-slim

# Copiamos el .jar generado desde la fase de compilación al contenedor final
COPY --from=build /app/target/*.jar app.jar

# Especificamos el comando para ejecutar la aplicación
CMD java -jar /app.jar $APP_ARGS
