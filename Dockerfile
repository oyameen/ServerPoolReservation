FROM gradle:jdk21-alpine

WORKDIR /app

COPY build.gradle settings.gradle ./
COPY src/ ./src/

RUN gradle clean build -x test --parallel

#COPY build/libs/server-pool-reservation-1.0.jar output/app.jar

ADD src/main/resources/application.properties config/

EXPOSE 9091

#ENTRYPOINT ["java", "-jar", "output/app.jar"]
ENTRYPOINT ["java", "-jar", "build/libs/server-pool-reservation-1.0.jar"]