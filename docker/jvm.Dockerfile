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

WORKDIR /work/jvm-toxcore-c
# Rarely changing external dependencies.
COPY scripts/ /work/jvm-toxcore-c/scripts/
RUN scripts/build-host "$PWD/_install/host/protobuf.stamp" "-j$(nproc)"
RUN scripts/build-host "$PWD/_install/host/toxcore.stamp" "-j$(nproc)"

# Native code, changes less frequently.
COPY lib/src/jvmMain/cpp/ /work/jvm-toxcore-c/lib/src/jvmMain/cpp/
COPY lib/src/jvmMain/proto/ /work/jvm-toxcore-c/lib/src/jvmMain/proto/
RUN touch "$PWD/_install/host/.stamp" \
 && touch "$PWD/_install/host/libsodium.stamp" \
 && touch "$PWD/_install/host/libvpx.stamp" \
 && touch "$PWD/_install/host/opus.stamp" \
 && touch "$PWD/_install/host/protobuf.stamp" \
 && touch "$PWD/_install/host/toxcore.stamp" \
 && scripts/build-host "$PWD/_install/host/tox4j.stamp" "-j$(nproc)"
RUN ["ls", "-lh", "/work/jvm-toxcore-c/_install/host/lib/libtox4j-c.so"]

# Java/Kotlin code changes a lot.
COPY gradlew gradle.properties settings.gradle.kts /work/jvm-toxcore-c/
COPY gradle /work/jvm-toxcore-c/gradle/
COPY lib/ /work/jvm-toxcore-c/lib/
ENV LD_LIBRARY_PATH=/work/jvm-toxcore-c/_install/host/lib
ENV PATH=/work/jvm-toxcore-c/_install/host/bin:$PATH
RUN ./gradlew build

RUN javac -h . -cp /work/jvm-toxcore-c/lib/build/classes/kotlin/jvm/main:/work/jvm-toxcore-c/lib/build/classes/java/jvm/main \
 lib/src/jvmMain/java/im/tox/tox4j/impl/jni/ToxAvJni.java \
 lib/src/jvmMain/java/im/tox/tox4j/impl/jni/ToxCoreJni.java \
 lib/src/jvmMain/java/im/tox/tox4j/impl/jni/ToxCryptoJni.java
RUN diff -u lib/src/jvmMain/cpp/ToxCore/generated/im_tox_tox4j_impl_jni_ToxCoreJni.h im_tox_tox4j_impl_jni_ToxCoreJni.h \
 && diff -u lib/src/jvmMain/cpp/ToxAv/generated/im_tox_tox4j_impl_jni_ToxAvJni.h im_tox_tox4j_impl_jni_ToxAvJni.h \
 && diff -u lib/src/jvmMain/cpp/ToxCrypto/generated/im_tox_tox4j_impl_jni_ToxCryptoJni.h im_tox_tox4j_impl_jni_ToxCryptoJni.h
