#include "ToxCrypto.h"

#include <tox/tox.h>
#include <sodium.h>
#define TOX_PASS_HASH_LENGTH       TOX_HASH_LENGTH
#define TOX_PASS_PUBLIC_KEY_LENGTH crypto_box_PUBLICKEYBYTES
#define TOX_PASS_SECRET_KEY_LENGTH crypto_box_SECRETKEYBYTES
#define TOX_PASS_SHARED_KEY_LENGTH crypto_box_BEFORENMBYTES
#define TOX_PASS_NONCE_LENGTH      crypto_box_NONCEBYTES
#define TOX_PASS_ZERO_BYTES        crypto_box_ZEROBYTES
#define TOX_PASS_BOX_ZERO_BYTES    crypto_box_BOXZEROBYTES
#include "generated/constants.h"

template<> extern char const *const module_name<ToxCrypto> = "crypto";
template<> extern char const *const exn_prefix<ToxCrypto> = "";

void
reference_symbols_crypto ()
{
#define JAVA_METHOD_REF(NAME)  unused (JAVA_METHOD_NAME (NAME));
#define CXX_FUNCTION_REF(NAME) unused (NAME);
#include "generated/natives.h"
#undef CXX_FUNCTION_REF
#undef JAVA_METHOD_REF
}
