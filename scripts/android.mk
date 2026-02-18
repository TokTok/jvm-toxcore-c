NDK_HOME	:= $(SRCDIR)/$(NDK_DIR)/$(TARGET)

DLLEXT		:= .so
TOOLCHAIN	:= $(NDK_HOME)/toolchains/llvm/prebuilt/linux-x86_64
SYSROOT		:= $(TOOLCHAIN)/sysroot
PREFIX		:= $(DESTDIR)/$(TARGET)
TOOLCHAIN_FILE	:= $(SRCDIR)/$(TARGET).cmake
PROTOC		:= $(DESTDIR)/host/bin/protoc

export CC		:= $(TOOLCHAIN)/bin/$(TARGET)$(NDK_API)-clang
export CXX		:= $(TOOLCHAIN)/bin/$(TARGET)$(NDK_API)-clang++
export PKG_CONFIG_LIBDIR:= $(PREFIX)/lib/pkgconfig
export PKG_CONFIG_PATH	:= $(PREFIX)/lib/pkgconfig
export PATH		:= $(TOOLCHAIN)/bin:$(PATH)
export TOX4J_PLATFORM	:= $(TARGET)

protobuf_CONFIGURE	:= -D CMAKE_TOOLCHAIN_FILE=$(TOOLCHAIN_FILE) -D WITH_PROTOC=$(PROTOC) -D CMAKE_EXE_LINKER_FLAGS=-llog
libsodium_CONFIGURE	:= --host=$(TARGET) --with-sysroot=$(SYSROOT)
opus_CONFIGURE		:= --host=$(TARGET) --with-sysroot=$(SYSROOT)
libvpx_CONFIGURE	:= --libc=$(SYSROOT) --target=$(VPX_ARCH)
toxcore_CONFIGURE	:= -D CMAKE_TOOLCHAIN_FILE=$(TOOLCHAIN_FILE) -D ANDROID_CPU_FEATURES=$(NDK_HOME)/sources/android/cpufeatures/cpu-features.c
tox4j_CONFIGURE		:= -D CMAKE_TOOLCHAIN_FILE=$(TOOLCHAIN_FILE) -D ANDROID_CPU_FEATURES=$(NDK_HOME)/sources/android/cpufeatures/cpu-features.c -D protobuf_DIR=$(PREFIX)/lib/cmake/protobuf -D absl_DIR=$(PREFIX)/lib/cmake/absl -D utf8_range_DIR=$(PREFIX)/lib/cmake/utf8_range

libvpx_PATCH	:=							\
	sed -i -e 's!^AS=as!AS=$(CC) -c!' $(BUILDDIR)/libvpx/*.mk &&	\
	sed -i -e 's!^STRIP=strip!STRIP=$(TOOLCHAIN)/bin/llvm-strip!' $(BUILDDIR)/libvpx/*.mk

build: $(PREFIX)/tox4j.stamp

test: build
	@echo "No tests for Android builds"

$(NDK_HOME):
	@$(PRE_RULE)
	@mkdir -p $(@D)
	# This is put into the root dir, not into $(SRCDIR), because it's huge and
	# clutters the CI cache.
	test -f $(NDK_PACKAGE) || curl -s $(NDK_URL) -o $(NDK_PACKAGE)
	$(SEVEN_ZIP) x -snld $(NDK_PACKAGE)
	mv $(NDK_DIR) $@
	mkdir -p $(TOOLCHAIN)/bin
	ln -f $(CC) $(TOOLCHAIN)/bin/$(TARGET)-gcc
	ln -f $(CXX) $(TOOLCHAIN)/bin/$(TARGET)-g++
	find $@ -exec chmod -w {} \;
	@$(POST_RULE)

$(TOOLCHAIN_FILE): $(NDK_HOME) scripts/android.mk
	@$(PRE_RULE)
	mkdir -p $(@D)
	echo 'set(CMAKE_SYSTEM_NAME Linux)' > $@
	echo >> $@
	echo 'set(CMAKE_SYSROOT $(SYSROOT))' >> $@
	echo >> $@
	echo 'set(CMAKE_C_COMPILER $(TOOLCHAIN)/bin/$(TARGET)-gcc)' >> $@
	echo 'set(CMAKE_CXX_COMPILER $(TOOLCHAIN)/bin/$(TARGET)-g++)' >> $@
	echo >> $@
	echo 'set(CMAKE_FIND_ROOT_PATH_MODE_PROGRAM NEVER)' >> $@
	echo 'set(CMAKE_FIND_ROOT_PATH_MODE_LIBRARY ONLY)' >> $@
	echo 'set(CMAKE_FIND_ROOT_PATH_MODE_INCLUDE ONLY)' >> $@
	echo 'set(CMAKE_FIND_ROOT_PATH_MODE_PACKAGE ONLY)' >> $@
	@$(POST_RULE)

include scripts/release.mk
