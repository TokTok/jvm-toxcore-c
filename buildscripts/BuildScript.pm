package BuildScript;

use strict;
use utf8;
use base 'Exporter';

use Cwd 'abs_path', 'cwd';
use Data::Dumper;
use File::Temp 'tempdir';

our @EXPORT = qw/
   flatten_flags
   pushd
   tempd
   must_system
   system2
   must_popen
   git_install
/;


sub show {
   local $Data::Dumper::Indent = 0;
   local $Data::Dumper::Terse = 1;
   print Dumper @_;
   print "\n";
}


##############################################################################
#
# :: Target-specific flag handling.
#
##############################################################################


sub flatten_flags {
   my $ostype = shift;
   my @flags;

   for (@_) {
      if (ref eq 'HASH') {
         push @flags, flatten_flags ($ostype, @{ $_->{flags} }) if (
            defined $_->{only_on} and $ostype =~ $_->{only_on} or
            defined $_->{not_on}  and $ostype !~ $_->{not_on}
         );
      } else {
         push @flags, $_;
      }
   }

   @flags
}


##############################################################################
#
# :: File-system and process execution utilities.
#
##############################################################################


sub pushd(&;$) {
   my ($block, $dir) = @_;
   my $orig = cwd;
   chdir $dir
      or die "Could not change to directory '$dir': $!";
   $block->();
   chdir $orig
      or die "Could not change back to directory '$dir': $!";
}

# Run a code block in a temporary directory that is cleaned up after the
# block returns normally.
sub tempd(&) {
   my ($block) = @_;
   my $dir = tempdir CLEANUP => 1;
   pushd { $block->() } $dir;
}

sub must_system {
   show \@_;
   
   system("echo @_ >> /tmp/inst.log");
   
   (system @_) == 0
      or die "Error while executing '@_'";
}

sub system2 {
    system("echo @_ >> /tmp/inst.log");
    system @_;
}

sub must_popen {
   if ($_[0] eq "-q") {
      shift;
   } else {
      show \@_;
   }
   open my $fh, '-|', @_
      or die "Could not exec '@_': $!";
   my @result = <$fh>;
   s/[\r\n]*//g for @result;
   @result
}


##############################################################################
#
# :: Common dependency installing code for both host and target builds.
#
##############################################################################


sub git_install {
   my ($state, $jobs, $baseurl, $repo, $branch, @flags) = @_;

   # Apply patch if one exists.
   my $patch = "buildscripts/patches/$repo.patch";

   $patch = abs_path $patch;

   local $ENV{CPPFLAGS} = "$ENV{CPPFLAGS} -fPIC";

   tempd {
      must_system "git", "clone", "--depth=1", "--branch=$branch", "$baseurl/$repo.git";
      chdir $repo;

      my ($rev) = must_popen "git", "rev-parse", "HEAD";
      if ($state->{$repo} eq $rev and not C::ALWAYS_BUILD (undef, $repo)) {
         print "Dependency '$repo' already up-to-date.\n";
      } else {
         # Apply patch if it exists.
         if (-f $patch) {
            my $patch = do { local $/; open my $fh, '<', $patch or die "$patch: $!"; <$fh> };
            open my $fh, '|-', 'patch', '-p1'
               or die "Could not execute 'patch': $!";
            print $fh $patch;
         }

         # Generate autotools stuff. Don't use autogen.sh here, because
         # protobuf's autogen.sh tries to download GMock with curl.
         must_system "autoreconf", "-fi"
            unless -f 'configure';

         # Run ./configure to generate Makefiles.
         mkdir '_build'
            or die "Could not create build directory";
         chdir '_build';

         print "PATH     = $ENV{PATH}\n";
         print "CFLAGS   = $ENV{CFLAGS}\n";
         print "CPPFLAGS = $ENV{CPPFLAGS}\n";
         print "CXXFLAGS = $ENV{CXXFLAGS}\n";
         print "LDFLAGS  = $ENV{LDFLAGS}\n";

         # Run configure in the vpath build directory.
         eval {
            must_system "../configure", @flags;
         };
         if ($@) {
            must_system "cat", "config.log";
            die $@;
         }

         # Then build and install.
         must_system "make", "-j$jobs";
         must_system "make", "install";

         # Update commit hash in persistent state.
         $state->{$repo} = $rev;
      }
   };
}


__PACKAGE__
