#include "ToxCrypto.h"


using pass_key_impl = uint8_t[TOX_PASS_SALT_LENGTH + TOX_PASS_KEY_LENGTH];


struct pass_key_deleter
{
  void operator () (Tox_Pass_Key *pass_key)
  {
    delete[] reinterpret_cast<pass_key_impl *> (pass_key);
  }
};

typedef std::unique_ptr<Tox_Pass_Key, pass_key_deleter> pass_key_ptr;


static jbyteArray
pass_key_to_java (JNIEnv *env, Tox_Pass_Key const &out_key)
{
  return toJavaArray (env,
    *reinterpret_cast<pass_key_impl const *> (&out_key));
}

static pass_key_ptr
pass_key_from_java (JNIEnv *env, jbyteArray passKeyArray)
{
  pass_key_ptr pass_key (reinterpret_cast<Tox_Pass_Key *> (new pass_key_impl));

  auto passKey = fromJavaArray (env, passKeyArray);
  tox4j_assert (passKey.size () == TOX_PASS_SALT_LENGTH + TOX_PASS_KEY_LENGTH);
  std::copy (
    passKey.begin (),
    passKey.end (),
    reinterpret_cast<uint8_t *> (pass_key.get ()));

  return pass_key;
}


/*
 * Class:     im_tox_tox4j_impl_jni_ToxCryptoJni
 * Method:    toxGetSalt
 * Signature: ([B)[B
 */
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_impl_jni_ToxCryptoJni_toxGetSalt
  (JNIEnv *env, jclass, jbyteArray dataArray)
{
  auto data = fromJavaArray (env, dataArray);
  uint8_t salt[TOX_PASS_SALT_LENGTH] = { 0 };

  return with_error_handling<ToxCrypto> (env,
    [env, &salt] (bool er)
      {
        return toJavaArray (env, salt);
      },
    tox_get_salt,
    data.data (), salt
  );
}


/*
 * Class:     im_tox_tox4j_impl_jni_ToxCryptoJni
 * Method:    toxIsDataEncrypted
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
 * Method:    toxPassKeyDeriveWithSalt
 * Signature: ([B[B)[B
 */
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_impl_jni_ToxCryptoJni_toxPassKeyDeriveWithSalt
  (JNIEnv *env, jclass, jbyteArray passphraseArray, jbyteArray saltArray)
{
  auto passphrase = fromJavaArray (env, passphraseArray);
  auto salt = fromJavaArray (env, saltArray);

  if (salt.size () != TOX_PASS_SALT_LENGTH)
    {
      throw_tox_exception<ToxCrypto, TOX_ERR_KEY_DERIVATION> (env, "INVALID_LENGTH");
      return nullptr;
    }

  return with_error_handling<ToxCrypto> (env,
    [env] (Tox_Pass_Key *out_key)
      {
        pass_key_ptr deleter (out_key);
        return pass_key_to_java (env, *out_key);
      },
    tox_pass_key_derive_with_salt,
    passphrase.data (), passphrase.size (),
    salt.data ()
  );
}


/*
 * Class:     im_tox_tox4j_impl_jni_ToxCryptoJni
 * Method:    toxPassKeyDerive
 * Signature: ([B)[B
 */
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_impl_jni_ToxCryptoJni_toxPassKeyDerive
  (JNIEnv *env, jclass, jbyteArray passphraseArray)
{
  auto passphrase = fromJavaArray (env, passphraseArray);

  return with_error_handling<ToxCrypto> (env,
    [env] (Tox_Pass_Key *out_key)
      {
        pass_key_ptr deleter (out_key);
        return pass_key_to_java (env, *out_key);
      },
    tox_pass_key_derive,
    passphrase.data (), passphrase.size ()
  );
}


/*
 * Class:     im_tox_tox4j_impl_jni_ToxCryptoJni
 * Method:    toxPassKeyDecrypt
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

  pass_key_ptr pass_key = pass_key_from_java (env, passKeyArray);

  return with_error_handling<ToxCrypto> (env,
    [env, &out] (bool)
      {
        return toJavaArray (env, out);
      },
    tox_pass_key_decrypt,
    pass_key.get (),
    data.data (), data.size (),
    out.data ()
  );
}


/*
 * Class:     im_tox_tox4j_impl_jni_ToxCryptoJni
 * Method:    toxPassKeyEncrypt
 * Signature: ([B[B)[B
 */
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_impl_jni_ToxCryptoJni_toxPassKeyEncrypt
  (JNIEnv *env, jclass, jbyteArray dataArray, jbyteArray passKeyArray)
{
  auto data = fromJavaArray (env, dataArray);
  std::vector<uint8_t> out (data.size () + TOX_PASS_ENCRYPTION_EXTRA_LENGTH);

  pass_key_ptr pass_key = pass_key_from_java (env, passKeyArray);

  return with_error_handling<ToxCrypto> (env,
    [env, &out] (bool)
      {
        return toJavaArray (env, out);
      },
    tox_pass_key_encrypt,
    pass_key.get (),
    data.data (), data.size (),
    out.data ()
  );
}
