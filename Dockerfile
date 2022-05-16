FROM openjdk:8
COPY ./src/sensor1/Sensor1.java/ /tmp
WORKDIR /tmp
ENTRYPOINT ["java","Sensor1"]