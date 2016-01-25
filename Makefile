PREFIX		?= $(HOME)
GOAL		?= coverage
TARGET		?= arm-linux-androideabi

ifeq ($(TOXCORE_REPO),)
TOXCORE_REPO	:= irungentoo
endif
ifeq ($(TOXCORE_BRANCH),)
TOXCORE_BRANCH	:= master
endif

export PATH		:= $(PREFIX)/.usr/bin:$(PATH)
export PKG_CONFIG_PATH	:= $(PREFIX)/.usr/lib/pkgconfig:$(PKG_CONFIG_PATH)
export CPPFLAGS		+= -I$(PREFIX)/.usr/include -isystem $(PREFIX)/.usr/include
export LDFLAGS		+= -L$(PREFIX)/.usr/lib


COMMANDS :=		\
	scalastyle	\
	test:scalastyle	\
	checkstyle	\
	test:checkstyle	\
	publishLocal	\
	publishM2	\
	makeScripts

ifeq ($(GOAL),coverage)
COMMANDS += clean coverage test
endif

ifeq ($(GOAL),performance)
COMMANDS += "testOnly *TravisBenchSuite"
endif

install:
	cd projects/build-basic  && sbt -batch publishLocal publishM2 coverage test
	cd projects/build-extra  && sbt -batch $(COMMANDS)
	cd projects/linters	 && sbt -batch $(COMMANDS)
	cd projects/macros	 && sbt -batch $(COMMANDS)
	cd projects/tox4j	 && sbt -batch $(COMMANDS)
	cd projects/tox4j	 && for i in bin/Jni*; do $$i; done
	git diff --exit-code
	file projects/tox4j/target/cpp/bin/*
	curl -s -i -F file="@`echo projects/tox4j/target/cpp/bin/libtox4j.*`" "https://uguu.se/api.php?d=upload-tool" | grep https
	curl -s -i -F file="@projects/tox4j/config.log" "https://uguu.se/api.php?d=upload-tool" | grep https

setup:
	# Install external packages from git.
	tools/git-install $(PREFIX)/.usr https://github.com/yasm		yasm		master
	tools/git-install $(PREFIX)/.usr https://chromium.googlesource.com/webm	libvpx		master		--enable-pic --enable-shared
	tools/git-install $(PREFIX)/.usr git://git.xiph.org			opus		master
	tools/git-install $(PREFIX)/.usr https://github.com/jedisct1		libsodium	stable
	tools/git-install $(PREFIX)/.usr https://github.com/$(TOXCORE_REPO)	toxcore		$(TOXCORE_BRANCH)
	tools/git-install $(PREFIX)/.usr https://github.com/google		protobuf	master

setup-host: setup ;

setup-%: setup
	tools/ndk-install $(PREFIX)
	projects/tox4j/android/build-deps.sh $(TARGET)
	cp projects/tox4j/android/local.$*.sbt projects/tox4j/local.sbt

clean:
	rm -rf projects/*/project/project
	rm -rf projects/*/project/target
	rm -rf projects/*/target

show:
	@echo "PREFIX	= $(PREFIX)"
	@echo "GOAL	= $(GOAL)"
	@echo "COMMANDS = $(COMMANDS)"
