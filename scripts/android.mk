NDK_HOME	:= $(SRCDIR)/$(NDK_DIR)/$(TARGET)

DLLEXT		:= .so
TOOLCHAIN	:= $(DESTDIR)/$(TARGET)
SYSROOT		:= $(TOOLCHAIN)/sysroot
PREFIX		:= $(SYSROOT)/usr
TOOLCHAIN_FILE	:= $(SRCDIR)/$(TARGET).cmake
PROTOC		:= $(DESTDIR)/host/bin/protoc

export CC		:= $(TOOLCHAIN)/bin/$(TARGET)-clang
export CXX		:= $(TOOLCHAIN)/bin/$(TARGET)-clang++
export LDFLAGS		:= -llog
export PKG_CONFIG_LIBDIR:= $(PREFIX)/lib/pkgconfig
export PKG_CONFIG_PATH	:= $(PREFIX)/lib/pkgconfig
export PATH		:= $(TOOLCHAIN)/bin:$(PATH)
export TOX4J_PLATFORM	:= $(TARGET)

protobuf_CONFIGURE	:= --prefix=$(PREFIX) --host=$(TARGET) --with-sysroot=$(SYSROOT) --disable-shared --with-protoc=$(PROTOC)
libsodium_CONFIGURE	:= --prefix=$(PREFIX) --host=$(TARGET) --with-sysroot=$(SYSROOT) --disable-shared
opus_CONFIGURE		:= --prefix=$(PREFIX) --host=$(TARGET) --with-sysroot=$(SYSROOT) --disable-shared
libvpx_CONFIGURE	:= --prefix=$(PREFIX) --sdk-path=$(NDK_HOME) --libc=$(SYSROOT) --target=$(VPX_ARCH) --disable-examples --disable-unit-tests --enable-pic
toxcore_CONFIGURE	:= -DCMAKE_INSTALL_PREFIX:PATH=$(PREFIX) -DCMAKE_TOOLCHAIN_FILE=$(TOOLCHAIN_FILE) -DANDROID_CPU_FEATURES=$(NDK_HOME)/sources/android/cpufeatures/cpu-features.c -DENABLE_STATIC=ON -DENABLE_SHARED=OFF
tox4j_CONFIGURE		:= -DCMAKE_INSTALL_PREFIX:PATH=$(PREFIX) -DCMAKE_TOOLCHAIN_FILE=$(TOOLCHAIN_FILE) -DANDROID_CPU_FEATURES=$(NDK_HOME)/sources/android/cpufeatures/cpu-features.c

build: $(TOOLCHAIN)/tox4j.stamp

test: build
	@echo "No tests for Android builds"

$(NDK_HOME):
	@$(PRE_RULE)
	@mkdir -p $(@D)
	# This is put into the root dir, not into $(SRCDIR), because it's huge and
	# clutters the Travis CI cache.
	test -f $(NDK_PACKAGE) || curl -s $(NDK_URL) -o $(NDK_PACKAGE)
	7z x $(NDK_PACKAGE) $(foreach x,$(NDK_FILES),'-ir!$(NDK_DIR)/$x') > /dev/null
	rm -rf $@
	mv $(NDK_DIR) $@
	@$(POST_RULE)

$(TOOLCHAIN)/AndroidVersion.txt: $(NDK_HOME)
	@$(PRE_RULE)
	$</build/tools/make_standalone_toolchain.py	\
		--arch $(NDK_ARCH)			\
		--install-dir $(@D)			\
		--api $(NDK_API)			\
		--force
	@$(POST_RULE)
	@touch $@

$(TOOLCHAIN_FILE): $(TOOLCHAIN)/AndroidVersion.txt
	@$(PRE_RULE)
	mkdir -p $(@D)
	echo 'set(CMAKE_SYSTEM_NAME Linux)' > $@
	echo >> $@
	echo 'set(CMAKE_SYSROOT $(SYSROOT))' >> $@
	echo >> $@
	echo 'set(CMAKE_C_COMPILER $(CC))' >> $@
	echo 'set(CMAKE_CXX_COMPILER $(CXX))' >> $@
	echo >> $@
	echo 'set(CMAKE_FIND_ROOT_PATH_MODE_PROGRAM NEVER)' >> $@
	echo 'set(CMAKE_FIND_ROOT_PATH_MODE_LIBRARY ONLY)' >> $@
	echo 'set(CMAKE_FIND_ROOT_PATH_MODE_INCLUDE ONLY)' >> $@
	echo 'set(CMAKE_FIND_ROOT_PATH_MODE_PACKAGE ONLY)' >> $@
	@$(POST_RULE)

include scripts/release.mk
