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
