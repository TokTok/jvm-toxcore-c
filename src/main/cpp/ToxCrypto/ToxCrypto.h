// Instance manager, JNI utilities.
#include "tox4j/Tox4j.h"

// JNI declarations from javah.
#include "im_tox_tox4j_impl_jni_ToxCryptoJni.h"

// Header from toxcore.
#include <tox/toxencryptsave.h>

#define SUBSYSTEM TOX
#define CLASS     ToxCrypto
#define PREFIX    tox


struct ToxCrypto;
