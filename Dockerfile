FROM eclipse-temurin:21.0.4_7-jre-jammy
EXPOSE 80
COPY build/libs/field-reservation-system.jar /field-reservation-system.jar
ENTRYPOINT ["java", "-jar", "-Dserver.port=80", "field-reservation-system.jar"]
