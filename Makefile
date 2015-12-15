export CACHE_DIR ?= $(HOME)/cache

install:
	TEST_GOAL= buildscripts/04_build

check:
	TEST_GOAL=coverage buildscripts/04_build

cache:
	rm -rf $(HOME)/.m2 $(HOME)/.ivy2 $(HOME)/.sbt
	mkdir -p $(CACHE_DIR)/m2 $(CACHE_DIR)/ivy2 $(CACHE_DIR)/sbt
	ln -s $(CACHE_DIR)/m2	$(HOME)/.m2
	ln -s $(CACHE_DIR)/ivy2	$(HOME)/.ivy2
	ln -s $(CACHE_DIR)/sbt	$(HOME)/.sbt

heroku: cache
	buildscripts/00_dependencies_host
	JAVA_HOME=/usr/lib/jvm/java-1.7.0-openjdk-amd64 buildscripts/04_build
	JAVA_HOME=/usr/lib/jvm/java-1.7.0-openjdk-amd64 buildscripts/05_heroku

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
	rm -rf $(CACHE_DIR)

allclean: distclean cacheclean
