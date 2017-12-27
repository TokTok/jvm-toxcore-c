#!/usr/bin/make -f

TARGET := arm-linux-androideabi

include scripts/common.mk

NDK_FILES := $(NDK_COMMON_FILES)			\
	platforms/android-9				\
	sources/cxx-stl/gnu-libstdc++/4.9/libs/armeabi*	\
	toolchains/arm-linux-androideabi-4.9

NDK_API := 9
NDK_ARCH := arm
VPX_ARCH := armv7-android-gcc

include scripts/android.mk
include scripts/dependencies.mk
