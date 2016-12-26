# Support for target-specific flags.
sub filter_os {
   my ($when, $os, @flags) = @_;
   return {
      $when => $os,
      flags => \@flags,
   }
}

sub not_on  { filter_os 'not_on' , @_ }
sub only_on { filter_os 'only_on', @_ }

my @common = (
   '--disable-shared',
   '--disable-soname-versions',
   only_on (qr/android/,
      '--host=$TARGET',
      '--with-sysroot=$SYSROOT',
   ),
);

# External dependencies.
(
   ["https://github.com/yasm", "yasm", "v1.3.0", @common],
   ["https://github.com/webmproject", "libvpx", "v1.6.0",
      '--disable-examples',
      '--disable-unit-tests',
      (not_on qr/^arm/, "--enable-pic"),
      (only_on qr/android/,
         '--sdk-path=$NDK_HOME'
      ),
      (only_on qr/aarch64-linux-android/,
         '--libc=$SYSROOT'
      ),
      (only_on qr/arm-linux-androideabi/, '--target=armv7-android-gcc'),
      (only_on qr/aarch64-linux-android/, '--target=arm64-android-gcc'),
      (only_on qr/i686-linux-android/   , '--target=x86-android-gcc'  ),
      (only_on qr/x86_64-linux-android/ , '--target=x86_64-android-gcc'  ),
   ],
   ["https://github.com/xiph", "opus", "v1.1.3", @common],
   ["https://github.com/jedisct1", "libsodium", "1.0.11", @common,
      '--enable-minimal',
      '--disable-pie',
   ],
   ["https://github.com/TokTok", "c-toxcore", "v0.1.2", @common,
      '--disable-testing',
      '--disable-tests',
      only_on (qr/android/, '--disable-rt'),
   ],
   ["https://github.com/google", "protobuf", "v3.1.0", @common,
      (only_on qr/android/, '--with-protoc=protoc'),
      (only_on qr/arm-linux-androideabi/,
         'LDFLAGS=-latomic', # For __atomic_fetch_add_4.
         'CXXFLAGS=-O0', # Disable optimisation to avoid relocation error.
      ),
   ],
)
