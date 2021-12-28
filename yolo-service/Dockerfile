FROM gradle:6.6.1-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN --mount=type=cache,id=gradle,target=/home/gradle/.gradle  gradle build --no-daemon -x test
################################################################################
FROM adoptopenjdk/openjdk11-openj9:x86_64-debian-jdk-11.0.5_10_openj9-0.17.0
RUN mkdir /app

RUN mkdir /yolo

COPY ./best.torchscript /yolo/best.torchscript

RUN apt-get update && apt-get install -y --no-install-recommends \
            git build-essential cmake pkg-config unzip libgtk2.0-dev \
            curl ca-certificates libcurl4-openssl-dev libssl-dev \
            libavcodec-dev libavformat-dev libswscale-dev libtbb2 libtbb-dev \
            libjpeg-dev libpng-dev libtiff-dev libdc1394-22-dev && \
            rm -rf /var/lib/apt/lists/*

ARG OPENCV_VERSION="4.5.4"
ENV OPENCV_VERSION $OPENCV_VERSION
RUN mkdir /yolo2
RUN curl -Lo opencv.zip https://github.com/opencv/opencv/archive/${OPENCV_VERSION}.zip && \
    unzip -q opencv.zip && \
    curl -Lo opencv_contrib.zip https://github.com/opencv/opencv_contrib/archive/${OPENCV_VERSION}.zip && \
    unzip -q opencv_contrib.zip && \
    rm opencv.zip opencv_contrib.zip

RUN cd opencv-${OPENCV_VERSION} && \
    mkdir build && cd build && \
    cmake -D CMAKE_BUILD_TYPE=RELEASE \
           -D OPENCV_ENABLE_NONFREE=ON \
           -D ENABLE_PRECOMPILED_HEADERS=OFF \
              -D WITH_JPEG=ON \
              -D WITH_PNG=ON \
              -D WITH_TIFF=ON \
              -D WITH_WEBP=ON \
              -D WITH_JASPER=ON \
              -D WITH_EIGEN=ON \
              -D WITH_TBB=ON \
              -D WITH_LAPACK=ON \
              -D WITH_PROTOBUF=ON \
              -D WITH_V4L=OFF \
              -D WITH_GSTREAMER=OFF \
              -D WITH_GTK=OFF \
              -D WITH_QT=OFF \
              -D WITH_CUDA=OFF \
              -D WITH_VTK=OFF \
              -D WITH_OPENEXR=OFF \
              -D WITH_FFMPEG=OFF \
              -D WITH_OPENCL=OFF \
              -D WITH_OPENNI=OFF \
              -D WITH_XINE=OFF \
              -D WITH_GDAL=OFF \
              -D WITH_IPP=OFF \
              -D BUILD_OPENCV_PYTHON3=OFF \
              -D BUILD_OPENCV_PYTHON2=OFF \
              -D BUILD_OPENCV_JAVA=ON \
              -D BUILD_TESTS=OFF \
              -D BUILD_IPP_IW=OFF \
              -D BUILD_PERF_TESTS=OFF \
              -D BUILD_EXAMPLES=OFF \
              -D BUILD_ANDROID_EXAMPLES=OFF \
              -D BUILD_DOCS=OFF \
              -D BUILD_ITT=ON \
              -D INSTALL_PYTHON_EXAMPLES=OFF \
              -D INSTALL_C_EXAMPLES=OFF \
              -D INSTALL_TESTS=OFF .. && \
    make -j4 && \
    make preinstall && make install && ldconfig

RUN cp -a /opencv-4.5.4/build/lib/. /lib/ # &&#
#    cd / && rm -rf opencv*
#
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
  "-Djava.security.egd=file:/dev/./urandom",\
  "-jar",\
  "/app/spring-boot-application.jar"]
