project(tox4j-c)
cmake_minimum_required(VERSION 2.8.7)
set(CMAKE_MODULE_PATH "${CMAKE_SOURCE_DIR}/tools/cmake")

if(NOT DEFINED CMAKE_MACOSX_RPATH)
  set(CMAKE_MACOSX_RPATH true)
endif()

#
# Dependencies
#

find_package(PkgConfig REQUIRED)

pkg_search_module(LIBTOXCORE libtoxcore)
if(LIBTOXCORE_FOUND)
  pkg_search_module(LIBTOXAV REQUIRED libtoxav)
  link_directories(${LIBTOXCORE_LIBRARY_DIRS})
  include_directories(${LIBTOXCORE_INCLUDE_DIRS})
  foreach(flag ${LIBTOXCORE_CFLAGS_OTHER})
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${flag}")
  endforeach()

  link_directories(${LIBTOXAV_LIBRARY_DIRS})
  include_directories(${LIBTOXAV_INCLUDE_DIRS})
  foreach(flag ${LIBTOXAV_CFLAGS_OTHER})
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${flag}")
  endforeach()
else()
  pkg_search_module(TOXCORE REQUIRED toxcore)
  link_directories(${TOXCORE_LIBRARY_DIRS})
  include_directories(${TOXCORE_INCLUDE_DIRS})
  foreach(flag ${TOXCORE_CFLAGS_OTHER})
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${flag}")
  endforeach()
endif()

find_package(Protobuf REQUIRED)
include_directories(${PROTOBUF_INCLUDE_DIRS})

find_package(JNI)
if(JAVA_INCLUDE_PATH)
  include_directories(${JAVA_INCLUDE_PATH})
endif()
if(JAVA_INCLUDE_PATH2)
  include_directories(${JAVA_INCLUDE_PATH2})
endif()

include_directories("src")
include_directories(${CMAKE_BINARY_DIR})

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++14")

#
# C++ standard library features
#

include(CheckCXXSourceCompiles)

# ::gets
check_cxx_source_compiles(
  "
#include <cstdio>
using ::gets;
int main() {}
"
  HAVE_GETS)
if(HAVE_GETS)
  set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -DHAVE_GETS=1")
endif()

# std::make_unique
check_cxx_source_compiles(
  "
#include <memory>
using std::make_unique;
int main() {}
"
  HAVE_MAKE_UNIQUE)
if(HAVE_MAKE_UNIQUE)
  set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -DHAVE_MAKE_UNIQUE=1")
endif()

# std::to_string
check_cxx_source_compiles(
  "
#include <string>
using std::to_string;
int main() {}
"
  HAVE_TO_STRING)
if(HAVE_TO_STRING)
  set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -DHAVE_TO_STRING=1")
endif()

#
# Build
#

protobuf_generate_cpp(PROTO_SRCS PROTO_HDRS src/ToxAv/Av.proto
                      src/ToxCore/Core.proto src/util/ProtoLog.proto)

add_library(
  ${PROJECT_NAME} SHARED
  ${ANDROID_CPU_FEATURES}
  ${PROTO_SRCS}
  ${PROTO_HDRS}
  src/ToxAv/generated/enums.cpp
  src/ToxAv/generated/errors.cpp
  src/ToxAv/generated/impls.h
  src/ToxAv/generated/im_tox_tox4j_impl_jni_ToxAvJni.h
  src/ToxAv/generated/natives.h
  src/ToxAv/av.cpp
  src/ToxAv/debug.cpp
  src/ToxAv/lifecycle.cpp
  src/ToxAv/ToxAv.cpp
  src/ToxAv/ToxAv.h
  src/ToxCore/generated/constants.h
  src/ToxCore/generated/enums.cpp
  src/ToxCore/generated/errors.cpp
  src/ToxCore/generated/impls.h
  src/ToxCore/generated/im_tox_tox4j_impl_jni_ToxCoreJni.h
  src/ToxCore/generated/natives.h
  src/ToxCore/clientinfo.cpp
  src/ToxCore/connection.cpp
  src/ToxCore/custom.cpp
  src/ToxCore/debug.cpp
  src/ToxCore/filetransfer.cpp
  src/ToxCore/friendlist.cpp
  src/ToxCore/interaction.cpp
  src/ToxCore/lifecycle.cpp
  src/ToxCore/ToxCore.cpp
  src/ToxCore/ToxCore.h
  src/ToxCrypto/generated/constants.h
  src/ToxCrypto/generated/errors.cpp
  src/ToxCrypto/generated/im_tox_tox4j_impl_jni_ToxCryptoJni.h
  src/ToxCrypto/generated/natives.h
  src/ToxCrypto/debug.cpp
  src/ToxCrypto/encryptsave.cpp
  src/ToxCrypto/hash.cpp
  src/ToxCrypto/ToxCrypto.cpp
  src/ToxCrypto/ToxCrypto.h
  src/Tox4j.cpp
  src/cpp14compat.h
  src/tox4j/ToxInstances.h
  src/tox4j/Tox4j.h
  src/tox/av.cpp
  src/tox/av.h
  src/tox/common.h
  src/tox/core.cpp
  src/tox/core.h
  src/tox/generated/av.h
  src/tox/generated/core.h
  src/util/jni/ArrayFromJava.cpp
  src/util/jni/ArrayFromJava.h
  src/util/jni/ArrayToJava.cpp
  src/util/jni/ArrayToJava.h
  src/util/jni/Enum.h
  src/util/jni/UTFChars.cpp
  src/util/jni/UTFChars.h
  src/util/debug_log.cpp
  src/util/debug_log.h
  src/util/exceptions.cpp
  src/util/exceptions.h
  src/util/instance_manager.h
  src/util/logging.cpp
  src/util/logging.h
  src/util/pp_attributes.h
  src/util/pp_cat.h
  src/util/to_bytes.cpp
  src/util/to_bytes.h
  src/util/unused.h
  src/util/wrap_void.h)

if(ANDROID_CPU_FEATURES)
  target_compile_definitions(${PROJECT_NAME} PRIVATE -Dtypeof=__typeof__)
endif()

target_link_libraries(${PROJECT_NAME} ${PROTOBUF_LIBRARIES})

if(LIBTOXCORE_FOUND)
  target_link_libraries(${PROJECT_NAME} ${LIBTOXAV_STATIC_LIBRARIES}
                        ${LIBTOXCORE_STATIC_LIBRARIES})
else()
  target_link_libraries(${PROJECT_NAME} ${TOXCORE_STATIC_LIBRARIES})
endif()

# Windows and OSX don't have this linker functionality.
if(NOT WIN32 AND NOT APPLE)
  set_target_properties(
    ${PROJECT_NAME}
    PROPERTIES
      LINK_FLAGS
      "-Wl,-z,defs -Wl,--version-script,${CMAKE_SOURCE_DIR}/src/libtox4j-c.ld")
endif()

#
# Install
#

install(TARGETS ${PROJECT_NAME} LIBRARY DESTINATION "lib")
