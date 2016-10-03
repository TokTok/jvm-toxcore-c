#include "ToxCrypto.h"


template<typename Iterator, std::size_t N>
static void
byte_copy_to (Iterator begin, uint8_t const (&data)[N])
{
  std::copy (data, data + N, begin);
}

template<typename Iterator, std::size_t N>
static void
byte_copy_from (uint8_t (&data)[N], Iterator begin)
{
  std::copy (begin, begin + N, data);
}


static jbyteArray
pass_key_to_java (JNIEnv *env, TOX_PASS_KEY const &out_key)
{
  std::vector<uint8_t> pass_key;
  byte_copy_to (std::back_inserter (pass_key), out_key.salt);
  byte_copy_to (std::back_inserter (pass_key), out_key.key);
  return toJavaArray (env, pass_key);
}

static TOX_PASS_KEY
pass_key_from_java (JNIEnv *env, jbyteArray passKeyArray)
{
  TOX_PASS_KEY pass_key;

  auto passKey = fromJavaArray (env, passKeyArray);
  tox4j_assert (passKey.size () == sizeof pass_key.salt + sizeof pass_key.key);
  byte_copy_from (pass_key.salt, passKey.data ());
  byte_copy_from (pass_key.key , passKey.data () + sizeof pass_key.salt);

  return pass_key;
}


/*
 * Class:     im_tox_tox4j_impl_jni_ToxCryptoJni
 * Method:    getSalt
 * Signature: ([B)[B
 */
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_impl_jni_ToxCryptoJni_toxGetSalt
  (JNIEnv *env, jclass, jbyteArray dataArray)
{
  auto data = fromJavaArray (env, dataArray);
  uint8_t salt[TOX_PASS_SALT_LENGTH];

  if (tox_get_salt (data.data (), salt))
    return toJavaArray (env, salt);

  return toJavaArray (env, std::vector<uint8_t> ());
}


/*
 * Class:     im_tox_tox4j_impl_jni_ToxCryptoJni
 * Method:    isDataEncrypted
 * Signature: ([B)Z
 */
JNIEXPORT jboolean JNICALL Java_im_tox_tox4j_impl_jni_ToxCryptoJni_toxIsDataEncrypted
  (JNIEnv *env, jclass, jbyteArray dataArray)
{
  auto data = fromJavaArray (env, dataArray);
  if (data.size () < TOX_PASS_ENCRYPTION_EXTRA_LENGTH)
    return false;
  return tox_is_data_encrypted (data.data ());
}


/*
 * Class:     im_tox_tox4j_impl_jni_ToxCryptoJni
 * Method:    deriveKeyWithSalt
 * Signature: ([B[B)[B
 */
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_impl_jni_ToxCryptoJni_toxDeriveKeyWithSalt
  (JNIEnv *env, jclass, jbyteArray passphraseArray, jbyteArray saltArray)
{
  auto passphrase = fromJavaArray (env, passphraseArray);
  auto salt = fromJavaArray (env, saltArray);
  TOX_PASS_KEY out_key;

  if (salt.size () != TOX_PASS_SALT_LENGTH)
    {
      throw_tox_exception<ToxCrypto, TOX_ERR_KEY_DERIVATION> (env, "INVALID_LENGTH");
      return nullptr;
    }

  LogEntry log_entry (tox_derive_key_with_salt);
  return with_error_handling<ToxCrypto> (log_entry, env,
    [env, &out_key] (bool)
      {
        return pass_key_to_java (env, out_key);
      },
    tox_derive_key_with_salt,
    (unsigned char *)passphrase.data (), passphrase.size (),
    (unsigned char *)salt.data (),
    &out_key
  );
}


/*
 * Class:     im_tox_tox4j_impl_jni_ToxCryptoJni
 * Method:    deriveKeyFromPass
 * Signature: ([B)[B
 */
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_impl_jni_ToxCryptoJni_toxDeriveKeyFromPass
  (JNIEnv *env, jclass, jbyteArray passphraseArray)
{
  auto passphrase = fromJavaArray (env, passphraseArray);
  TOX_PASS_KEY out_key;

  LogEntry log_entry (tox_derive_key_from_pass);
  return with_error_handling<ToxCrypto> (log_entry, env,
    [env, &out_key] (bool)
      {
        return pass_key_to_java (env, out_key);
      },
    tox_derive_key_from_pass,
    (unsigned char *)passphrase.data (), passphrase.size (),
    &out_key
  );
}


/*
 * Class:     im_tox_tox4j_impl_jni_ToxCryptoJni
 * Method:    decrypt
 * Signature: ([B[B)[B
 */
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_impl_jni_ToxCryptoJni_toxPassKeyDecrypt
  (JNIEnv *env, jclass, jbyteArray dataArray, jbyteArray passKeyArray)
{
  auto data = fromJavaArray (env, dataArray);
  std::vector<uint8_t> out (
    // If size is too small, the library will throw INVALID_LENGTH, but we need
    // to ensure that we don't end up with negative (or very large) output arrays here.
    std::max (
      0l,
      static_cast<long> (data.size ()) - TOX_PASS_ENCRYPTION_EXTRA_LENGTH
    )
  );

  TOX_PASS_KEY const pass_key = pass_key_from_java (env, passKeyArray);

  LogEntry log_entry (tox_pass_key_decrypt);
  return with_error_handling<ToxCrypto> (log_entry, env,
    [env, &out] (bool)
      {
        return toJavaArray (env, out);
      },
    tox_pass_key_decrypt,
    data.data (), data.size (),
    &pass_key,
    out.data ()
  );
}


/*
 * Class:     im_tox_tox4j_impl_jni_ToxCryptoJni
 * Method:    encrypt
 * Signature: ([B[B)[B
 */
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_impl_jni_ToxCryptoJni_toxPassKeyEncrypt
  (JNIEnv *env, jclass, jbyteArray dataArray, jbyteArray passKeyArray)
{
  auto data = fromJavaArray (env, dataArray);
  std::vector<uint8_t> out (data.size () + TOX_PASS_ENCRYPTION_EXTRA_LENGTH);

  TOX_PASS_KEY const pass_key = pass_key_from_java (env, passKeyArray);

  LogEntry log_entry (tox_pass_key_encrypt);
  return with_error_handling<ToxCrypto> (log_entry, env,
    [env, &out] (bool)
      {
        return toJavaArray (env, out);
      },
    tox_pass_key_encrypt,
    data.data (), data.size (),
    &pass_key,
    out.data ()
  );
}
