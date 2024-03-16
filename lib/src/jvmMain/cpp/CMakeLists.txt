project(tox4j-c)
cmake_minimum_required(VERSION 3.16)

if(NOT DEFINED CMAKE_MACOSX_RPATH)
  set(CMAKE_MACOSX_RPATH true)
endif()

#
# Dependencies
#

find_package(PkgConfig REQUIRED)

pkg_search_module(TOXCORE REQUIRED toxcore)
find_package(protobuf CONFIG REQUIRED)
find_package(JNI)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++17")

#
# Build
#

add_library(
  ${PROJECT_NAME} SHARED
  ${ANDROID_CPU_FEATURES}
  ToxAv/Av.proto
  ToxCore/Core.proto
  ToxAv/generated/enums.cpp
  ToxAv/generated/errors.cpp
  ToxAv/generated/impls.h
  ToxAv/generated/im_tox_tox4j_impl_jni_ToxAvJni.h
  ToxAv/generated/natives.h
  ToxAv/av.cpp
  ToxAv/lifecycle.cpp
  ToxAv/ToxAv.cpp
  ToxAv/ToxAv.h
  ToxCore/generated/constants.h
  ToxCore/generated/enums.cpp
  ToxCore/generated/errors.cpp
  ToxCore/generated/impls.h
  ToxCore/generated/im_tox_tox4j_impl_jni_ToxCoreJni.h
  ToxCore/generated/natives.h
  ToxCore/connection.cpp
  ToxCore/filetransfer.cpp
  ToxCore/friendlist.cpp
  ToxCore/lifecycle.cpp
  ToxCore/ToxCore.cpp
  ToxCore/ToxCore.h
  ToxCrypto/generated/constants.h
  ToxCrypto/generated/errors.cpp
  ToxCrypto/generated/im_tox_tox4j_impl_jni_ToxCryptoJni.h
  ToxCrypto/generated/natives.h
  ToxCrypto/encryptsave.cpp
  ToxCrypto/hash.cpp
  ToxCrypto/ToxCrypto.cpp
  ToxCrypto/ToxCrypto.h
  Tox4j.cpp
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
  util/exceptions.cpp
  util/exceptions.h
  util/instance_manager.h
  util/pp_cat.h
  util/to_bytes.cpp
  util/to_bytes.h
  util/unused.h
  util/wrap_void.h)

if(ANDROID_CPU_FEATURES)
  target_compile_definitions(${PROJECT_NAME} PRIVATE -Dtypeof=__typeof__)
endif()

target_include_directories(${PROJECT_NAME} PUBLIC ${PROTOBUF_INCLUDE_DIRS})
target_link_libraries(${PROJECT_NAME} protobuf::libprotobuf-lite)

if(JAVA_INCLUDE_PATH)
  target_include_directories(${PROJECT_NAME} PUBLIC ${JAVA_INCLUDE_PATH})
endif()
if(JAVA_INCLUDE_PATH2)
  target_include_directories(${PROJECT_NAME} PUBLIC ${JAVA_INCLUDE_PATH2})
endif()

target_link_directories(${PROJECT_NAME} PUBLIC ${TOXCORE_LIBRARY_DIRS})
target_link_libraries(${PROJECT_NAME} ${TOXCORE_STATIC_LIBRARIES})
target_include_directories(${PROJECT_NAME} PUBLIC ${TOXCORE_INCLUDE_DIRS})

target_include_directories(${PROJECT_NAME} PUBLIC ${CMAKE_SOURCE_DIR})
target_include_directories(${PROJECT_NAME} PUBLIC ${CMAKE_BINARY_DIR})
target_include_directories(${PROJECT_NAME} PUBLIC ${CMAKE_BINARY_DIR}/ToxAv)
target_include_directories(${PROJECT_NAME} PUBLIC ${CMAKE_BINARY_DIR}/ToxCore)
target_include_directories(${PROJECT_NAME} PUBLIC ${CMAKE_BINARY_DIR}/util)

protobuf_generate(TARGET ${PROJECT_NAME})

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
