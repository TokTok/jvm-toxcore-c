#!/usr/bin/env bash

set -eux -o pipefail

GIT_ROOT=$(git rev-parse --show-toplevel)
cd "$GIT_ROOT"

ABI=$1
shift

case $ABI in
  arm64-v8a)
    SCRIPT_ARCH=aarch64-linux-android
    ;;
  armeabi-v7a)
    SCRIPT_ARCH=armv7a-linux-androideabi
    ;;
  x86)
    SCRIPT_ARCH=i686-linux-android
    ;;
  x86_64)
    SCRIPT_ARCH=x86_64-linux-android
    ;;
  *)
    echo "Unknown ABI: $ABI"
    exit 1
    ;;
esac

"scripts/build-$SCRIPT_ARCH" libvpx "$@"
"scripts/build-$SCRIPT_ARCH" "$@"
