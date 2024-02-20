FROM ubuntu:22.04

RUN apt-get update \
 && DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends \
 autoconf \
 automake \
 build-essential \
 ca-certificates \
 cmake \
 default-jdk \
 git \
 libtool \
 make \
 pkg-config \
 yasm \
 && apt-get clean \
 && rm -rf /var/lib/apt/lists/*

WORKDIR /work/jvm-toxcore-c
COPY scripts/ /work/jvm-toxcore-c/scripts/
RUN scripts/build-host "$PWD/_install/host/protobuf.stamp" "-j$(nproc)"
RUN scripts/build-host "$PWD/_install/host/toxcore.stamp" "-j$(nproc)"
COPY lib/src/main/cpp/ /work/jvm-toxcore-c/lib/src/main/cpp/
COPY lib/src/main/proto/ /work/jvm-toxcore-c/lib/src/main/proto/
RUN scripts/build-host "$PWD/_install/host/tox4j.stamp" "-j$(nproc)"
