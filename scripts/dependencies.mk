PRE_RULE = (echo "=== Building $@ ==="; ls -ld $@; true) && ls -ld $+
POST_RULE = ls -ld $@

$(BUILDDIR)/tox4j/Makefile: $(CURDIR)/cpp/CMakeLists.txt $(TOOLCHAIN_FILE) $(foreach i,protobuf toxcore,$(PREFIX)/$i.stamp)
	@$(PRE_RULE)
	mkdir -p $(@D)
	cd $(@D) && cmake $(<D) $($(notdir $(@D))_CONFIGURE)
	@$(POST_RULE)

$(PREFIX)/tox4j.stamp: $(BUILDDIR)/tox4j/Makefile
	@$(PRE_RULE)
	$(MAKE) -C $(<D) install
	mkdir -p $(@D) && touch $@
	@$(POST_RULE)

#############################################################################
# protobuf

$(SRCDIR)/protobuf:
	git clone --depth=1 --branch=v3.11.1 https://github.com/google/protobuf $@

$(PREFIX)/protobuf.stamp: $(SRCDIR)/protobuf $(TOOLCHAIN_FILE) $(PROTOC)
	@$(PRE_RULE)
	cd $< && autoreconf -fi
	mkdir -p $(BUILDDIR)/$(notdir $<)
	cd $(BUILDDIR)/$(notdir $<) && $(SRCDIR)/$(notdir $<)/configure $($(notdir $<)_CONFIGURE)
	$(MAKE) -C $(BUILDDIR)/$(notdir $<) install V=0
	mkdir -p $(@D) && touch $@
	@$(POST_RULE)

#############################################################################
# toxcore

$(SRCDIR)/toxcore:
	if [ -e ../c-toxcore ]; then					\
	  ln -s $(realpath ../c-toxcore) $@;				\
	else								\
	  git clone --depth=1 https://github.com/TokTok/c-toxcore $@;	\
	fi

$(PREFIX)/toxcore.stamp: $(foreach f,$(shell cd $(SRCDIR)/toxcore && git ls-files),$(SRCDIR)/toxcore/$f)
$(PREFIX)/toxcore.stamp: $(SRCDIR)/toxcore $(TOOLCHAIN_FILE) $(foreach i,libsodium opus libvpx,$(PREFIX)/$i.stamp)
	@$(PRE_RULE)
	mkdir -p $(BUILDDIR)/$(notdir $<)
	cd $(BUILDDIR)/$(notdir $<) && cmake $(SRCDIR)/$(notdir $<) $($(notdir $<)_CONFIGURE) -DMUST_BUILD_TOXAV=ON -DBOOTSTRAP_DAEMON=OFF
	$(MAKE) -C $(BUILDDIR)/$(notdir $<) install
	mkdir -p $(@D) && touch $@
	@$(POST_RULE)

#############################################################################
# libsodium

$(SRCDIR)/libsodium:
	git clone --depth=1 --branch=1.0.18 https://github.com/jedisct1/libsodium $@

$(PREFIX)/libsodium.stamp: $(SRCDIR)/libsodium $(TOOLCHAIN_FILE)
	@$(PRE_RULE)
	cd $< && autoreconf -fi
	mkdir -p $(BUILDDIR)/$(notdir $<)
	cd $(BUILDDIR)/$(notdir $<) && $(SRCDIR)/$(notdir $<)/configure $($(notdir $<)_CONFIGURE)
	$(MAKE) -C $(BUILDDIR)/$(notdir $<) install V=0
	mkdir -p $(@D) && touch $@
	@$(POST_RULE)

#############################################################################
# opus

$(SRCDIR)/opus:
	git clone --depth=1 https://github.com/xiph/opus $@

$(PREFIX)/opus.stamp: $(SRCDIR)/opus $(TOOLCHAIN_FILE)
	@$(PRE_RULE)
	cd $< && autoreconf -fi
	mkdir -p $(BUILDDIR)/$(notdir $<)
	cd $(BUILDDIR)/$(notdir $<) && $(SRCDIR)/$(notdir $<)/configure $($(notdir $<)_CONFIGURE)
	$(MAKE) -C $(BUILDDIR)/$(notdir $<) install V=0
	mkdir -p $(@D) && touch $@
	@$(POST_RULE)

#############################################################################
# libvpx

$(SRCDIR)/libvpx:
	git clone --depth=1 --branch=v1.6.0 https://github.com/webmproject/libvpx $@
	cd $@ && patch -p1 < $(CURDIR)/scripts/patches/libvpx.patch

$(PREFIX)/libvpx.stamp: $(SRCDIR)/libvpx $(TOOLCHAIN_FILE)
	@$(PRE_RULE)
	mkdir -p $(BUILDDIR)/$(notdir $<)
	cd $(BUILDDIR)/$(notdir $<) && $(SRCDIR)/$(notdir $<)/configure $($(notdir $<)_CONFIGURE)
	$(MAKE) -C $(BUILDDIR)/$(notdir $<) install
	mkdir -p $(@D) && touch $@
	@$(POST_RULE)
