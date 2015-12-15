CACHEDIR ?= $(HOME)/cache

install:
	TEST_GOAL= buildscripts/04_build

check:
	TEST_GOAL=coverage buildscripts/04_build

heroku:
	CACHEDIR=$(CACHE_DIR) buildscripts/00_dependencies_host

setup:
	buildscripts/00_dependencies_host
	buildscripts/01_ndk
	buildscripts/02_toolchain
	buildscripts/03_dependencies_target

clean:
	rm -rf projects/*/project/project
	rm -rf projects/*/project/target
	rm -rf projects/*/target

distclean: clean
	rm -rf toolchains

cacheclean:
	rm -rf $(CACHEDIR)

allclean: distclean cacheclean
