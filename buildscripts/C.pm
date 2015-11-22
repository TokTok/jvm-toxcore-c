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


sub CACHEDIR { $ENV{CACHEDIR} || "$ENV{HOME}/cache" }

sub ALWAYS_BUILD { $ENV{ALWAYS_BUILD} eq $_[1] }
sub TEST_GOAL { $ENV{TEST_GOAL} }

sub HOST { $Config{myarchname} }
sub TARGET { $ENV{TARGET} || HOST }

sub NDK_HOME {
   my ($ndk_home) = grep { -f } (
      $ENV{NDK_HOME},
      "$ENV{HOME}/android-ndk-r10e",
      "$ENV{HOME}/android-ndk",
      "$ENV{HOME}/usr/android-ndk-r10e",
      "$ENV{HOME}/usr/android-ndk",
      "/opt/android-ndk-r10e",
      "/opt/android-ndk",
   );
   $ndk_home || $ENV{NDK_HOME} || "$ENV{HOME}/android-ndk"
}

sub SBT { split /\s+/, ($ENV{SBT} || 'sbt') }
sub JOBS { $ENV{TOX4J_JOBS} || 4 }
sub PREFIX { CACHEDIR . "/usr" }

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
make_path CACHEDIR;

# Load and store the persistent program state.
my $statefile = CACHEDIR . "/state.pst";
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
$ENV{CPPFLAGS} = "-I$PREFIX/include -isystem $PREFIX/include";
$ENV{LDFLAGS} = "-L$PREFIX/lib -Wl,-rpath,$PREFIX/lib";


__PACKAGE__
