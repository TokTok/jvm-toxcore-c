cmake_minimum_required(VERSION 2.8.7)
enable_testing()
set(CMAKE_MODULE_PATH "${CMAKE_SOURCE_DIR}/tools/cmake")

if (NOT DEFINED CMAKE_MACOSX_RPATH)
        set(CMAKE_MACOSX_RPATH true)
endif()

# Common CFLAGS/CXXFLAGS, etc.
include(${COMMON_FILE})

# pkg-config requirements from SBT.
include(${DEPENDENCIES_FILE})

# Optional dependencies.
find_package(Glog)
if(GLOG_FOUND)
        set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -DHAVE_GLOG")
        include_directories(SYSTEM ${GLOG_INCLUDE_DIRS})
        link_libraries(${GLOG_LIBRARY})
else()
        message(STATUS "Did not find glog; logging is disabled")
endif()

# Include directory for our config.h.
include_directories(${CMAKE_CURRENT_BINARY_DIR}/src/main/cpp)

# If we are crosscompiling, we have to add our own jni headers.
if(NOT DEFINED CMAKE_TOOLCHAIN_FILE)
	find_package(JNI REQUIRED)
	set(NEED_JNI_MD y)
else()
	# On most platforms, there is a jni_md.h that is included by jni.h. On
	# other platforms (e.g. Android), jni_md.h does not exist.
	# If we are crosscompiling for such a platform, we have to set
	# -DNEED_JNI_MD=n, so it is not included.
	if(NOT DEFINED NEED_JNI_MD)
		set(NEED_JNI_MD y)
	endif()
	if(DEFINED JNI_H)
		set(JAVA_INCLUDE_PATH ${JNI_H})
	else()
		message(FATAL_ERROR "Need to specify jni.h location when "
			"cross-compiling. Please specify location with "
			"-DJNI_H=/path/to/jni.h")
	endif()
	if(DEFINED JNI_MD_H)
		set(JAVA_INCLUDE_PATH2 ${JNI_MD_H})
	else()
		if(NOT ${NEED_JNI_MD} MATCHES n)
			message(FATAL_ERROR "Need to specify jni_md.h "
				"location when cross-compiling. Please "
				"specify location with "
				"-DJNI_MD_H=/path/to/jni_md.h. When "
				"building for a platform without jni_md.h "
				"(e.g. Android), set -DNEED_JNI_MD=n")
		endif()
	endif()
endif()

# Depending on whether we need jni_md.h or not, define the include directories.
include_directories("${JAVA_INCLUDE_PATH}")
if(DEFINED JAVA_INCLUDE_PATH2)
	include_directories("${JAVA_INCLUDE_PATH2}")
endif()

subdirs(src/main/cpp)
subdirs(src/test/cpp)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -include cpp14compat.h")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O3")
