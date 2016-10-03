export CACHE_DIR ?= $(HOME)/cache

default: install

all:
	$(MAKE) clean
	$(MAKE) setup
	$(MAKE) install

setup:
	buildscripts/00_dependencies_host
	buildscripts/01_ndk
	buildscripts/02_toolchain
	buildscripts/03_dependencies_target

install:
	buildscripts/04_build
	buildscripts/05_android

check:
	TEST_GOAL=coverage buildscripts/04_build

upload:
	buildscripts/06_upload
	buildscripts/07_coverage

cache:
	rm -rf $(HOME)/.m2 $(HOME)/.ivy2 $(HOME)/.sbt
	mkdir -p $(CACHE_DIR)/m2 $(CACHE_DIR)/ivy2 $(CACHE_DIR)/sbt
	ln -s $(CACHE_DIR)/m2	$(HOME)/.m2
	ln -s $(CACHE_DIR)/ivy2	$(HOME)/.ivy2
	ln -s $(CACHE_DIR)/sbt	$(HOME)/.sbt

heroku: cache
	buildscripts/heroku

heroku-start:
	heroku maintenance:off; heroku scale web=1

heroku-stop:
	heroku maintenance:on; heroku scale web=0

clean:
	rm -rf project/project
	rm -rf project/target
	rm -rf target

distclean: clean
	rm -rf toolchains

cacheclean:
	rm -rf $(CACHE_DIR)

allclean: distclean cacheclean
