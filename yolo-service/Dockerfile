FROM gradle:6.6.1-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN --mount=type=cache,id=gradle,target=/home/gradle/.gradle  gradle build --no-daemon -x test
################################################################################
FROM ubuntu:18.04

RUN apt-get -y update &&\
    apt-get -y upgrade &&\
    apt-get install -y git &&\
    apt-get install -y ffmpeg &&\
    apt-get install -y default-jre &&\
    apt-get install -y openjdk-11-jdk

RUN mkdir /app

RUN mkdir /yolo

COPY ./best.torchscript /yolo/best.torchscript
COPY ./face_mask.yaml /yolo/face_mask.yaml

COPY ./lib /opencvlib

COPY --from=build /home/gradle/src/build/libs/*.jar \
      /app/spring-boot-application.jar
#
#
#
ENTRYPOINT [\
  "java",\
  "-XX:+UseContainerSupport",\
  "-XX:-UseAdaptiveSizePolicy",\
  "-Xmx128m",\
  "-Xms32m",\
  "-Xss256k",\
  "-Djava.library.path=/opencvlib/",\
  "-Djava.security.egd=file:/dev/./urandom",\
  "-jar",\
  "/app/spring-boot-application.jar"]
