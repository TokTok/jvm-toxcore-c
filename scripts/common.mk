SRCDIR			:= $(CURDIR)/_git
DESTDIR			:= $(CURDIR)/_install
BUILDDIR		:= $(CURDIR)/_build/$(TARGET)

export CFLAGS		:= -O3 -pipe
export CXXFLAGS		:= -O3 -pipe -std=c++20
export LDFLAGS		:=

export PATH		:= $(DESTDIR)/host/bin:$(PATH)

# Android NDK
NDK_DIR		:= android-ndk-r27c
ifeq ($(shell uname -s),Darwin)
NDK_PACKAGE	:= $(NDK_DIR)-darwin.dmg
SEVEN_ZIP	:= 7zz
else
NDK_PACKAGE	:= $(NDK_DIR)-linux.zip
SEVEN_ZIP	:= 7z
endif
NDK_URL		:= http://dl.google.com/android/repository/$(NDK_PACKAGE)
