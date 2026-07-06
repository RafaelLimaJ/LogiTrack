# Dockerfile simples para rodar o serviço Java 17
FROM openjdk:17-jdk-slim
WORKDIR /app
# Copia o arquivo .jar compilado para a imagem
COPY target/*.jar app.jar
# Expõe a porta que o serviço vai escutar
EXPOSE 8081
# Comando pra rodar o app
ENTRYPOINT ["java", "-jar", "app.jar"]
