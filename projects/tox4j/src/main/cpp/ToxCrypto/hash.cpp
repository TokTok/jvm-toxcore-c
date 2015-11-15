#include "ToxCrypto.h"

#include <tox/tox.h> // For TOX_HASH_LENGTH


/*
 * Class:     im_tox_tox4j_impl_jni_ToxCryptoJni
 * Method:    toxHash
 * Signature: ([B)[B
 */
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_impl_jni_ToxCryptoJni_toxHash
  (JNIEnv *env, jclass, jbyteArray dataArray)
{
  auto data = fromJavaArray (env, dataArray);
  uint8_t hash[TOX_HASH_LENGTH];
  bool result = tox_hash (hash, data.data (), data.size ());
  tox4j_assert (result);
  return toJavaArray (env, hash);
}
