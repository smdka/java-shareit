FROM amazoncorretto:11
COPY target/*.jar app.jar
RUN bin/bash -c 'touch /server.log'
ENTRYPOINT ["java","-jar","/app.jar"]