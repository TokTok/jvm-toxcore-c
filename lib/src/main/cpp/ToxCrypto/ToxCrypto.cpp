#include "ToxCrypto.h"

template<> char const *module_name<ToxCrypto>() { return "crypto"; }
template<> char const *exn_prefix<ToxCrypto>() { return ""; }

#include <tox/tox.h>
#include <sodium.h>

// libsodium sizes the encryptsave layer relies on.
static_assert (crypto_box_PUBLICKEYBYTES == 32, "libsodium pubkey size changed");
static_assert (crypto_box_SECRETKEYBYTES == 32, "libsodium seckey size changed");
static_assert (crypto_box_BEFORENMBYTES  == 32, "libsodium shared-key size changed");
static_assert (crypto_box_NONCEBYTES     == 24, "libsodium nonce size changed");
static_assert (crypto_box_ZEROBYTES      == 32, "libsodium zero-byte prefix changed");
static_assert (crypto_box_BOXZEROBYTES   == 16, "libsodium boxzero-byte prefix changed");

#include "generated/constants.h"

void
reference_symbols_crypto ()
{
  checkToxCryptoConstants ();
#define JAVA_METHOD_REF(NAME)  unused (JAVA_METHOD_NAME (NAME));
#define CXX_FUNCTION_REF(NAME) unused (NAME);
#include "generated/natives.h"
#undef CXX_FUNCTION_REF
#undef JAVA_METHOD_REF
}
