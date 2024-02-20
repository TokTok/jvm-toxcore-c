project(tox4j-c)
cmake_minimum_required(VERSION 2.8.12)

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

include_directories(${CMAKE_SOURCE_DIR})
include_directories(${CMAKE_BINARY_DIR})

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++17")

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

protobuf_generate_cpp(PROTO_SRCS PROTO_HDRS ToxAv/Av.proto
                      ToxCore/Core.proto util/ProtoLog.proto)

add_library(
  ${PROJECT_NAME} SHARED
  ${ANDROID_CPU_FEATURES}
  ${PROTO_SRCS}
  ${PROTO_HDRS}
  ToxAv/generated/enums.cpp
  ToxAv/generated/errors.cpp
  ToxAv/generated/impls.h
  ToxAv/generated/im_tox_tox4j_impl_jni_ToxAvJni.h
  ToxAv/generated/natives.h
  ToxAv/av.cpp
  ToxAv/debug.cpp
  ToxAv/lifecycle.cpp
  ToxAv/ToxAv.cpp
  ToxAv/ToxAv.h
  ToxCore/generated/constants.h
  ToxCore/generated/enums.cpp
  ToxCore/generated/errors.cpp
  ToxCore/generated/impls.h
  ToxCore/generated/im_tox_tox4j_impl_jni_ToxCoreJni.h
  ToxCore/generated/natives.h
  ToxCore/clientinfo.cpp
  ToxCore/connection.cpp
  ToxCore/custom.cpp
  ToxCore/debug.cpp
  ToxCore/filetransfer.cpp
  ToxCore/friendlist.cpp
  ToxCore/interaction.cpp
  ToxCore/lifecycle.cpp
  ToxCore/ToxCore.cpp
  ToxCore/ToxCore.h
  ToxCrypto/generated/constants.h
  ToxCrypto/generated/errors.cpp
  ToxCrypto/generated/im_tox_tox4j_impl_jni_ToxCryptoJni.h
  ToxCrypto/generated/natives.h
  ToxCrypto/debug.cpp
  ToxCrypto/encryptsave.cpp
  ToxCrypto/hash.cpp
  ToxCrypto/ToxCrypto.cpp
  ToxCrypto/ToxCrypto.h
  Tox4j.cpp
  cpp14compat.h
  tox4j/ToxInstances.h
  tox4j/Tox4j.h
  tox/av.cpp
  tox/av.h
  tox/common.h
  tox/core.cpp
  tox/core.h
  tox/generated/av.h
  tox/generated/core.h
  util/jni/ArrayFromJava.cpp
  util/jni/ArrayFromJava.h
  util/jni/ArrayToJava.cpp
  util/jni/ArrayToJava.h
  util/jni/Enum.h
  util/jni/UTFChars.cpp
  util/jni/UTFChars.h
  util/debug_log.cpp
  util/debug_log.h
  util/exceptions.cpp
  util/exceptions.h
  util/instance_manager.h
  util/logging.cpp
  util/logging.h
  util/pp_attributes.h
  util/pp_cat.h
  util/to_bytes.cpp
  util/to_bytes.h
  util/unused.h
  util/wrap_void.h)

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
      "-Wl,-z,defs -Wl,--version-script,${CMAKE_SOURCE_DIR}/libtox4j-c.ld")
endif()

#
# Install
#

install(TARGETS ${PROJECT_NAME} LIBRARY DESTINATION "lib")
