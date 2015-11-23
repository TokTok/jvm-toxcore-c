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
   ["https://github.com/yasm", "yasm", "master", @common],
   ["https://chromium.googlesource.com/webm", "libvpx", "master",
      '--disable-examples',
      '--disable-unit-tests',
      (not_on qr/^arm/, "--enable-pic"),
      (only_on qr/android/,
         '--disable-vp9',
         '--sdk-path=$NDK_HOME'
      ),
      (only_on qr/arm-linux-androideabi/, '--target=armv7-android-gcc'),
      (only_on qr/i686-linux-android/   , '--target=x86-android-gcc'  ),
   ],
   ["git://git.xiph.org", "opus", "master", @common],
   ["https://github.com/jedisct1", "libsodium", "stable", @common,
      '--enable-minimal',
      '--disable-pie',
   ],
   ["https://github.com/irungentoo", "toxcore", "master", @common,
      '--disable-testing',
      '--disable-tests',
      only_on (qr/android/, '--disable-rt'),
   ],
   ["https://github.com/google", "protobuf", "master", @common,
      (only_on qr/android/, '--with-protoc=protoc'),
   ],
)
