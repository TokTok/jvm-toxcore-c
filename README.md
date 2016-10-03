# Tox4j (C backend)

This repository contains the [c-toxcore](https://github.com/TokTok/c-toxcore)
backed implementation of the generic backend-agnostic JVM
[toxcore-api](https://github.com/TokTok/jvm-toxcore-api).


## Contributing

We're using the standard Github workflow for our code reviews. Just open Pull
Requests and the reviewer will guide you through the process.


## Build status

|      Build      |   Status  |
|-----------------|-----------|
| Travis CI       | [![Travis Build Status](https://api.travis-ci.org/tox4j/tox4j.svg)](https://travis-ci.org/tox4j/tox4j) | 
| Coverage        | [![Coverage Status](https://coveralls.io/repos/tox4j/tox4j/badge.svg?branch=master)](https://coveralls.io/r/tox4j/tox4j?branch=master) |
| Android arm64   | [![Android arm64 Status](https://build.tox.chat/buildStatus/icon?job=tox4j_build_android_arm64_release)](https://build.tox.chat/job/tox4j_build_android_arm64_release/) |
| Android armeabi | [![Android armeabi Status](https://build.tox.chat/buildStatus/icon?job=tox4j_build_android_armel_release)](https://build.tox.chat/job/tox4j_build_android_armel_release/) |
| Android x86     | [![Android x86 Status](https://build.tox.chat/buildStatus/icon?job=tox4j_build_android_x86_release)](https://build.tox.chat/job/tox4j_build_android_x86_release/) |


# Building Tox4j

## Dependencies

### Java

Required Java libraries are downloaded automatically by sbt, which needs to be
installed.

### Native code

* Toxcore
* Toxav
  * We require the latest git version of these libraries, so you will need to build them yourself.
* CMake (>= 2.8.7)
  * Debian/Ubuntu: cmake
* protobuf 3.0.0
  * Debian/Ubuntu: You need to build this from source.
* Clang 3.5 or newer (older versions of clang segfault. G++ support is untested, the build script enforces clang-3.5 for now. If you do not have clang 3.5 installed, your build may fail.)
  * Debian/Ubuntu: clang-3.5

## Building

- Build and install toxcore and toxav.
- Run the sbt console with `sbt`.

Now you can use `compile` to build, `test` to run unit tests (these are a lot of
tests with high timeouts, might take 10 minutes or longer), and `package` to
create a jar and the native library.

### Developing on Mac OS X

Getting the required tools for development on OS X is very easy. If you have
XCode installed, you will already be able to compile the C++ part of tox4j,

### Importing in IDEA

To import the project in IDEA, launch IDEA and:

- On the "Welcome to IntelliJ IDEA" screen, select "Import Project".
- Select the directory jvm-toxcore-c (this git repository).
- Press "OK".
- Select "Import project from external model".
- Select "SBT".
- Press "Next".
- Select "Use auto-import", "Download sources and docs", and "Download SBT sources and docs".
- Create and select a JDK if you don't have one, yet.
- Press "Finish".

Now you need to wait while IDEA builds the project info from the SBT project.
When it is done, it shows a list of modules, all are selected. Leave them
selected and press "OK". After IDEA opens, it will say "Unregistered VCS root
detected". Press "Add root" so you can use git from IDEA. If you have the
protobuf extension installed, you can register the proto files with that as
well (another notification will show for that).
