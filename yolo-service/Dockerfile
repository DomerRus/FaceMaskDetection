FROM gradle:6.6.1-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN --mount=type=cache,id=gradle,target=/home/gradle/.gradle  gradle build --no-daemon -x test
################################################################################
FROM adoptopenjdk/openjdk11-openj9:x86_64-debian-jdk-11.0.5_10_openj9-0.17.0
RUN mkdir /app

RUN mkdir /yolo

COPY ./best.torchscript /yolo/best.torchscript

RUN apt-get -y update \
    && apt-get -y dist-upgrade \
    && apt-get install -y sudo

RUN apt-get install -y wget unzip \
    && mkdir /opt/local \
    && cd /opt/local \
    && wget https://archive.apache.org/dist/ant/binaries/apache-ant-1.10.7-bin.zip \
    && mv apache-ant-1.10.7-bin.zip ant.zip \
    && unzip ant.zip \
    && rm ant.zip \
    && mv apache-ant-1.10.7 ant

RUN apt-get install -y unzip build-essential cmake python-dev python-numpy

RUN apt-get install -y zlib1g-dev libjpeg-dev libwebp-dev libpng-dev libtiff5-dev libopenexr-dev libgdal-dev

RUN apt-get install -y libtbb-dev libeigen3-dev

RUN cd /tmp \
    && wget https://github.com/opencv/opencv/archive/4.5.4.zip \
    && unzip 4.5.4.zip && rm 4.5.4.zip \
    && mv opencv-4.5.4 OpenCV \
    && cd OpenCV && mkdir build && cd build

WORKDIR /tmp/OpenCV/build

RUN cmake -D CMAKE_BUILD_TYPE=RELEASE \
      -D CMAKE_INSTALL_PREFIX=/usr/local \
      -D WITH_TBB=ON \
      -D WITH_V4L=ON \
      -D WITH_QT=ON \
      -D WITH_OPENGL=ON \
      -D WITH_CUDA=ON \
      -D WITH_NVCUVID=OFF \
 #     -D OPENCV_EXTRA_MODULES_PATH=../../opencv_contrib-4.5.4/modules \
      -D OPENCV_ENABLE_NONFREE=ON \
      ..

RUN make -j8

RUN mkdir /opt/local/lib

RUN cp bin/opencv*.jar lib/*.so /lib

COPY --from=build /home/gradle/src/build/libs/*.jar\
      /app/spring-boot-application.jar



ENTRYPOINT [\
  "java",\
  "-XX:+UseContainerSupport",\
  "-XX:-UseAdaptiveSizePolicy",\
  "-Xmx128m",\
  "-Xms32m",\
  "-Xss256k",\
  "-Djava.security.egd=file:/dev/./urandom",\
  "-jar",\
  "/app/spring-boot-application.jar"]
