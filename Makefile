CACHEDIR ?= $(HOME)/cache

install:
	buildscripts/04_build

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
