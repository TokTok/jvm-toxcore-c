_build/$(TARGET)/tox4j/libtox4j-c$(DLLEXT): $(PREFIX)/tox4j.stamp
	ls -l $@
	touch $@

ifeq ($(BINTRAY_USER),)
release: _build/$(TARGET)/tox4j/libtox4j-c$(DLLEXT)
	@echo 'Not releasing without BINTRAY_USER'
else
ifneq ($(TRAVIS_BRANCH),master)
release: _build/$(TARGET)/tox4j/libtox4j-c$(DLLEXT)
	@echo 'No release done on branch "$(TRAVIS_BRANCH)"'
else
release: _build/$(TARGET)/tox4j/libtox4j-c$(DLLEXT) $(HOME)/.bintray/.credentials
	rm -rf $(wildcard cpp/src/main/resources/im/tox/tox4j/impl/jni/*/)
	mkdir -p cpp/src/main/resources/im/tox/tox4j/impl/jni/$(TOX4J_PLATFORM)/
	cp $< cpp/src/main/resources/im/tox/tox4j/impl/jni/$(TOX4J_PLATFORM)/
	-cd cpp && sbt bintrayUnpublish
	cd cpp && sbt publish

$(HOME)/.bintray/.credentials:
	mkdir -p $(@D)
	@echo 'Writing $@'
	@echo 'realm = Bintray API Realm' > $@
	@echo 'host = api.bintray.com' >> $@
	@echo 'user = $(BINTRAY_USER)' >> $@
	@echo 'password = $(BINTRAY_KEY)' >> $@
endif
endif
