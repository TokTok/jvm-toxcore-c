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

my @common_android = only_on (qr/android/,
   '--host=$TARGET',
   '--with-sysroot=$SYSROOT',
   '--disable-shared',
   '--disable-soname-versions',
);

# External dependencies.
(
   ["https://github.com/yasm", "yasm", "master", @common_android],
   ["https://chromium.googlesource.com/webm", "libvpx", "master",
      (not_on qr/^arm/, "--enable-pic"),
      (not_on qr/darwin|android/, "--enable-shared"),
      (only_on qr/android/,
         '--disable-examples',
         '--disable-unit-tests',
         '--disable-vp9',
         '--sdk-path=$NDK_HOME'
      ),
      (only_on qr/arm-linux-androideabi/, '--target=armv7-android-gcc'),
      (only_on qr/i686-linux-android/   , '--target=x86-android-gcc'  ),
   ],
   ["git://git.xiph.org", "opus", "master", @common_android],
   ["https://github.com/jedisct1", "libsodium", "stable", @common_android,
      '--enable-minimal',
      '--disable-pie',
   ],
   ["https://github.com/irungentoo", "toxcore", "master", @common_android,
      (only_on qr/android/,
         '--disable-rt',
         '--disable-testing',
         '--disable-tests'
      ),
   ],
   ["https://github.com/google", "protobuf", "master", @common_android,
      (only_on qr/android/, '--with-protoc=protoc'),
   ],
)
