FROM ubuntu:22.04

RUN apt-get update \
 && DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends \
 autoconf \
 automake \
 build-essential \
 ca-certificates \
 cmake \
 curl \
 default-jdk \
 git \
 libtool \
 make \
 pkg-config \
 yasm \
 && apt-get clean \
 && rm -rf /var/lib/apt/lists/*

WORKDIR /opt
RUN curl -L -o kotlin-native.tar.gz https://github.com/JetBrains/kotlin/releases/download/v1.9.22/kotlin-native-linux-x86_64-1.9.22.tar.gz \
 && tar zxf kotlin-native.tar.gz \
 && mv kotlin-native-linux-x86_64-1.9.22 kotlin \
 && rm kotlin-native.tar.gz
ENV PATH=$PATH:/opt/kotlin/bin

RUN ["touch", "Boot.kt"]
RUN ["kotlinc-native", "-p", "library", "Boot.kt"]

WORKDIR /work/jvm-toxcore-c
COPY lib/ /work/jvm-toxcore-c/lib/
COPY docker/build-native /work/jvm-toxcore-c/docker/
RUN ["docker/build-native"]
