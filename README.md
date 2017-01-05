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

## Dependencies: compile

To build the package itself, the following dependencies are required:

- com.chuusai:shapeless_2.11:2.3.2
- com.google.code.findbugs:jsr305:3.0.0
- com.google.guava:guava:19.0
- com.google.protobuf:protobuf-java:3.1.0
- com.intellij:annotations:12.0
- com.lihaoyi:fastparse-utils_2.11:0.4.2
- com.lihaoyi:fastparse_2.11:0.4.2
- com.lihaoyi:sourcecode_2.11:0.1.3
- com.trueaccord.lenses:lenses_2.11:0.4.9
- com.trueaccord.scalapb:scalapb-runtime-grpc_2.11:0.5.46
- com.trueaccord.scalapb:scalapb-runtime_2.11:0.5.46
- com.typesafe.scala-logging:scala-logging_2.11:3.5.0
- io.grpc:grpc-context:1.0.1
- io.grpc:grpc-core:1.0.1
- io.grpc:grpc-stub:1.0.1
- org.scala-lang.modules:scala-parser-combinators_2.11:1.0.4
- org.scala-lang.modules:scala-xml_2.11:1.0.4
- org.scala-lang:scala-compiler:2.11.7
- org.scala-lang:scala-reflect:2.11.7
- org.scalaz:scalaz-core_2.11:7.2.8
- org.scodec:scodec-bits_2.11:1.1.2
- org.scodec:scodec-core_2.11:1.10.3
- org.slf4j:slf4j-api:1.7.21
- org.toktok:macros_2.11:0.1.0
- org.toktok:tox4j-api_2.11:0.1.2
- org.toktok:tox4j-c_2.11:0.1.2-SNAPSHOT
- org.typelevel:macro-compat_2.11:1.1.1

## Dependencies: test

For testing, the following additional dependencies are required:

- asm:asm-commons:3.0
- asm:asm-tree:3.0
- asm:asm:3.0
- com.fasterxml.jackson.core:jackson-annotations:2.5.2
- com.fasterxml.jackson.core:jackson-core:2.5.2
- com.fasterxml.jackson.core:jackson-databind:2.5.2
- com.fasterxml.jackson.module:jackson-module-scala_2.11:2.5.2
- com.github.wookietreiber:scala-chart_2.11:0.4.2
- com.intellij:forms_rt:7.0.3
- com.jgoodies:forms:1.1-preview
- com.storm-enroute:scalameter-core_2.11:0.7
- com.storm-enroute:scalameter_2.11:0.7
- com.thoughtworks.paranamer:paranamer:2.6
- jdom:jdom:1.0
- jline:jline:2.14.2
- junit:junit:4.12
- log4j:log4j:1.2.17
- org.apache.commons:commons-lang3:3.4
- org.apache.commons:commons-math3:3.2
- org.hamcrest:hamcrest-core:1.3
- org.jfree:jcommon:1.0.21
- org.jfree:jfreechart:1.0.17
- org.ow2.asm:asm:5.0.4
- org.scala-lang.modules:scala-swing_2.11:1.0.1
- org.scala-lang.modules:scala-xml_2.11:1.0.5
- org.scala-sbt:test-interface:1.0
- org.scala-tools.testing:test-interface:0.5
- org.scalacheck:scalacheck_2.11:1.13.4
- org.scalactic:scalactic_2.11:3.0.1
- org.scalatest:scalatest_2.11:3.0.1
- org.scalaz:scalaz-concurrent_2.11:7.2.8
- org.scalaz:scalaz-effect_2.11:7.2.8
- org.slf4j:slf4j-api:1.7.22
- org.slf4j:slf4j-log4j12:1.7.22
- xml-apis:xml-apis:1.3.04

## C/C++ dependencies

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
