package C;

use strict;
use utf8;

use Config;
use Data::Dumper;
use File::Path 'make_path';
use Storable;

$Data::Dumper::Indent = 1;


##############################################################################
#
# :: Flags.
#
##############################################################################


sub CACHE_DIR { $ENV{CACHE_DIR} || "$ENV{HOME}/cache" }

sub ALWAYS_BUILD { grep { $_ eq $_[1] } (split /,/, $ENV{ALWAYS_BUILD}) }
sub TEST_GOAL { $ENV{TEST_GOAL} }

sub HOST { $Config{myarchname} }
sub TARGET { $ENV{TARGET} || HOST }

sub NDK_HOME {
   my ($ndk_home) = grep { -d } (
      $ENV{ANDROID_NDK_HOME},
      $ENV{NDK_HOME},
      "$ENV{HOME}/android-ndk-r11b",
      "$ENV{HOME}/android-ndk",
      "$ENV{HOME}/usr/android-ndk-r11b",
      "$ENV{HOME}/usr/android-ndk",
      "/opt/android-ndk-r11b",
      "/opt/android-ndk",
   );
   $ndk_home || $ENV{NDK_HOME} || "$ENV{HOME}/android-ndk"
}

sub SBT { split /\s+/, ($ENV{SBT} || 'sbt') }
sub JOBS { $ENV{TOX4J_JOBS} || 4 }
sub PREFIX { CACHE_DIR . "/usr" }

sub GITHUB_KEY {
   $ENV{GITHUB_KEY} || do {
      my $keyfile = "$ENV{HOME}/.github";
      if (-f $keyfile) {
         open my $fh, $keyfile or die "Could not open $keyfile for reading: $!";
         my ($key) = <$fh>;
         chomp $key;
         $key
      }
   };
}

# Ensure that the cache path exists.
make_path CACHE_DIR;

# Load and store the persistent program state.
my $statefile = CACHE_DIR . "/state.pst";
my $STATE = eval { Storable::retrieve $statefile } || {};
END { Storable::store $STATE, $statefile }

sub STATE { $STATE }


##############################################################################
#
# :: Environment.
#
##############################################################################


# Set up environment for finding tools and include/lib paths.
my $PREFIX = PREFIX;
$ENV{PATH} = "$PREFIX/bin:$ENV{PATH}";
$ENV{PKG_CONFIG_PATH} = "$PREFIX/lib/pkgconfig";
$ENV{CPPFLAGS} = "-I$PREFIX/include -isystem$PREFIX/include";
$ENV{LDFLAGS} = "-L$PREFIX/lib -Wl,-rpath,$PREFIX/lib";

# Compile everything with clang.
($ENV{CC}) = grep { `which $_` } qw/clang clang-3.8 clang-3.7 clang-3.6/;
($ENV{CXX}) = grep { `which $_` } qw/clang++ clang++-3.8 clang++-3.7 clang++-3.6/;

# Enable optimisation on dependencies.
#$ENV{CFLAGS} = $ENV{CXXFLAGS} = "-O3";

if ($ARGV[0] eq "show-env") {
   print <<EOF;
PATH="$ENV{PATH}"
PKG_CONFIG_PATH="$ENV{PKG_CONFIG_PATH}"
CPPFLAGS="$ENV{CPPFLAGS}"
LDFLAGS="$ENV{LDFLAGS}"
CXX="$ENV{CXX}"
CC="$ENV{CC}"
EOF
}

__PACKAGE__
