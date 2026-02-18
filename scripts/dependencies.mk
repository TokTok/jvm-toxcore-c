PRE_RULE = (echo "=== Building $@ ==="; ls -ld $@; true) && ls -ld $+
POST_RULE = ls -ld $@

GIT_CLONE = git clone --depth=1 --recurse-submodules --shallow-submodules

$(BUILDDIR)/tox4j/Makefile: $(CURDIR)/lib/src/main/cpp/CMakeLists.txt $(TOOLCHAIN_FILE) $(foreach i,protobuf toxcore,$(PREFIX)/$i.stamp)
	@$(PRE_RULE)
	mkdir -p $(@D)
	cd $(@D) && cmake $(<D) $($(notdir $(@D))_CONFIGURE) -D CMAKE_INSTALL_PREFIX=$(PREFIX)
	@$(POST_RULE)

$(PREFIX)/tox4j.stamp: $(BUILDDIR)/tox4j/Makefile
	@$(PRE_RULE)
	$(MAKE) -C $(<D) install
	mkdir -p $(@D) && touch $@
	@$(POST_RULE)

#############################################################################
# protobuf

protobuf: $(PREFIX)/protobuf.stamp
$(PREFIX)/protobuf.stamp: $(TOOLCHAIN_FILE)
	@$(PRE_RULE)
	$(GIT_CLONE) --branch=v25.6 https://github.com/protocolbuffers/protobuf $(SRCDIR)/protobuf
	mkdir -p $(BUILDDIR)/protobuf
	cd $(BUILDDIR)/protobuf && cmake $(SRCDIR)/protobuf $(protobuf_CONFIGURE) -D CMAKE_INSTALL_PREFIX=$(PREFIX) -D BUILD_SHARED_LIBS=OFF -D protobuf_BUILD_TESTS=OFF -D protobuf_DISABLE_RTTI=ON -D protobuf_BUILD_SHARED_LIBS=OFF
	$(MAKE) -C $(BUILDDIR)/protobuf install
	mkdir -p $(@D) && touch $@
	@$(POST_RULE)

#############################################################################
# toxcore

toxcore: $(PREFIX)/toxcore.stamp
$(PREFIX)/toxcore.stamp: $(TOOLCHAIN_FILE) $(foreach i,libsodium opus libvpx,$(PREFIX)/$i.stamp)
	@$(PRE_RULE)
	if [ -e $(SRCDIR)/toxcore ]; then					\
	  echo "toxcore already linked/downloaded";				\
	elif [ -e ../c-toxcore ]; then						\
	  ln -s $(realpath ../c-toxcore) $@;					\
	else									\
	  $(GIT_CLONE) https://github.com/TokTok/c-toxcore $(SRCDIR)/toxcore;	\
	fi
	mkdir -p $(BUILDDIR)/toxcore
	cd $(BUILDDIR)/toxcore && cmake $(SRCDIR)/toxcore $(toxcore_CONFIGURE) -D CMAKE_INSTALL_PREFIX=$(PREFIX) -D ENABLE_STATIC=ON -D ENABLE_SHARED=OFF -D MUST_BUILD_TOXAV=ON -D BOOTSTRAP_DAEMON=OFF
	$(MAKE) -C $(BUILDDIR)/toxcore install
	mkdir -p $(@D) && touch $@
	@$(POST_RULE)

#############################################################################
# libsodium

libsodium: $(PREFIX)/libsodium.stamp
$(PREFIX)/libsodium.stamp: $(TOOLCHAIN_FILE)
	@$(PRE_RULE)
	test -d $(SRCDIR)/libsodium || $(GIT_CLONE) --branch=1.0.19 https://github.com/jedisct1/libsodium $(SRCDIR)/libsodium
	cd $(SRCDIR)/libsodium && autoreconf -fi
	mkdir -p $(BUILDDIR)/libsodium
	cd $(BUILDDIR)/libsodium && $(SRCDIR)/libsodium/configure $(libsodium_CONFIGURE) --prefix=$(PREFIX) --disable-shared
	$(MAKE) -C $(BUILDDIR)/libsodium install V=0
	mkdir -p $(@D) && touch $@
	@$(POST_RULE)

#############################################################################
# opus

opus: $(PREFIX)/opus.stamp
$(PREFIX)/opus.stamp: $(TOOLCHAIN_FILE)
	@$(PRE_RULE)
	test -d $(SRCDIR)/opus || $(GIT_CLONE) https://github.com/xiph/opus $(SRCDIR)/opus
	cd $(SRCDIR)/opus && autoreconf -fi
	mkdir -p $(BUILDDIR)/opus
	cd $(BUILDDIR)/opus && $(SRCDIR)/opus/configure $(opus_CONFIGURE) --prefix=$(PREFIX) --disable-shared
	$(MAKE) -C $(BUILDDIR)/opus install V=1
	mkdir -p $(@D) && touch $@
	@$(POST_RULE)

#############################################################################
# libvpx

libvpx: $(PREFIX)/libvpx.stamp
$(PREFIX)/libvpx.stamp: $(TOOLCHAIN_FILE)
	@$(PRE_RULE)
	test -d $(SRCDIR)/libvpx || $(GIT_CLONE) --branch=v1.15.0 https://github.com/webmproject/libvpx $(SRCDIR)/libvpx
	echo "$(PATH)"
	mkdir -p $(BUILDDIR)/libvpx
	cd $(BUILDDIR)/libvpx && $(SRCDIR)/libvpx/configure $(libvpx_CONFIGURE) --prefix=$(PREFIX) --disable-examples --disable-unit-tests --enable-pic || (cat config.log && false)
	$(libvpx_PATCH)
	$(MAKE) -C $(BUILDDIR)/libvpx install
	mkdir -p $(@D) && touch $@
	@$(POST_RULE)
