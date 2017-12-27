SRCDIR			:= $(CURDIR)/_git
DESTDIR			:= $(CURDIR)/_install
BUILDDIR		:= $(CURDIR)/_build/$(TARGET)

export CFLAGS		:= -O3 -pipe
export CXXFLAGS		:= -O3 -pipe
export LDFLAGS		:=

export PATH		:= $(DESTDIR)/host/bin:$(PATH)

# Android NDK
NDK_DIR		:= android-ndk-r13b
NDK_PACKAGE	:= $(NDK_DIR)-$(shell perl -e 'print $$^O')-x86_64.zip
NDK_URL		:= http://dl.google.com/android/repository/$(NDK_PACKAGE)

NDK_COMMON_FILES :=					\
	build						\
	sources/android/cpufeatures			\
	sources/cxx-stl/gnu-libstdc++/4.9/include	\
	prebuilt/linux-x86_64				\
	prebuilt/darwin-x86_64				\
	toolchains/llvm
