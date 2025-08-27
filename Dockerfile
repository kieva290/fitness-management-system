FROM openjdk:17
ADD target/fitness-management-system-0.0.1-SNAPSHOT.jar fitness-management-system-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "fitness-management-system-0.0.1-SNAPSHOT.jar"]