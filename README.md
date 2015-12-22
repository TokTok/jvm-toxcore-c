# Tox4j

Combined repository for several Tox4j projects. These can be found in the
`projects` directory and currently include:

- build-basic: A collection of SBT plugins used by all Tox4j projects.
- build-extra: Additional SBT plugins that require a specific version of Scala.
- linters: Extra WartRemover linters with knowledge of our standards.
- macros: Macros used by the tox4j project. These need to be compiled separately.
- optimiser: An experimental Java byte code optimiser.
- tox4j: The core and high level client libraries.


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

* sbt
* com.google.protobuf : protobuf-java : 2.6.1
* com.trueaccord.scalapb : scalapb-runtime
* See [build.sbt](build.sbt) for more dependencies.

Protobuf support libraries are pulled in by sbt, to be the exact same version
as protoc. protoc needs to be on $PATH for this to work correctly.

### Tox4j commons

The tox4j build requires the following common packages to be installed:

* im.tox:build-basic
* im.tox:build-extra
* im.tox:linters

These can be found in the toplevel directory of this repository and installed
using `sbt publishLocal`.

### Native code

* Toxcore
* Toxav
  * We require the latest git version of these libraries, so you will need to build them yourself.
* CMake (>= 2.8.7)
  * Debian/Ubuntu: cmake
* protobuf (The version used in development is currently 2.6.1, other versions might work as well)
  * Debian/Ubuntu: protobuf-compiler, libprotobuf-dev
* Clang 3.5 (older versions of clang segfault. G++ support is untested, the build script enforces clang-3.5 for now. If you do not have clang 3.5 installed, your build may fail.)
  * Debian/Ubuntu: clang-3.5

## Building

Build and install toxcore and toxav. Run the sbt console with ```sbt```, and
then use ```compile``` to build, ```test``` to run unit tests (these are a lot
of tests with high timeouts, might take 40minutes or longer), and
```package``` to create a jar and the needed native library.

### Developing on Mac OS X

Getting the required tools for development on OS X is very easy. If you have
XCode installed, you will already be ready to compile the C++ part of tox4j,
