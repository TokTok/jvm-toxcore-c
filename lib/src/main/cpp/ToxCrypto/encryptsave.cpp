// Tox_Pass_Key* values live in the pool below; JNI only sees instance
// numbers.

#include <memory>
#include <mutex>
#include <vector>

#include "ToxCrypto.h"


namespace {

struct pass_key_deleter
{
  void operator () (Tox_Pass_Key *pass_key)
  {
    tox_pass_key_free (pass_key);
  }
};

// Shared, so a concurrent PassKey.close() can't free a key that an
// encrypt or decrypt is still using.
typedef std::shared_ptr<Tox_Pass_Key> pass_key_ptr;

// Slot N is instance number N+1; freed slots are in the free list.
std::mutex pass_key_mutex;
std::vector<pass_key_ptr> pass_key_pool;
std::vector<size_t> pass_key_free_list;

jint
add_pass_key (pass_key_ptr pass_key)
{
  std::lock_guard<std::mutex> lock (pass_key_mutex);
  if (!pass_key_free_list.empty ())
    {
      size_t idx = pass_key_free_list.back ();
      pass_key_free_list.pop_back ();
      pass_key_pool[idx] = std::move (pass_key);
      return static_cast<jint> (idx + 1);
    }
  pass_key_pool.push_back (std::move (pass_key));
  return static_cast<jint> (pass_key_pool.size ());
}

pass_key_ptr
get_pass_key (jint instance_number)
{
  std::lock_guard<std::mutex> lock (pass_key_mutex);
  if (instance_number < 1
      || static_cast<size_t> (instance_number) > pass_key_pool.size ())
    return nullptr;
  return pass_key_pool[instance_number - 1];
}

void
free_pass_key (jint instance_number)
{
  std::lock_guard<std::mutex> lock (pass_key_mutex);
  if (instance_number < 1
      || static_cast<size_t> (instance_number) > pass_key_pool.size ())
    return;
  size_t idx = static_cast<size_t> (instance_number) - 1;
  if (pass_key_pool[idx])
    {
      pass_key_pool[idx].reset ();
      pass_key_free_list.push_back (idx);
    }
}

}  // namespace


/*
 * Class:     im_tox_tox4j_impl_jni_ToxCryptoJni
 * Method:    toxPassKeyFree
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCryptoJni_toxPassKeyFree
  (JNIEnv *, jclass, jint instance_number)
{
  free_pass_key (instance_number);
}


/*
 * Class:     im_tox_tox4j_impl_jni_ToxCryptoJni
 * Method:    toxPassKeyDerive
 * Signature: ([B)I
 */
JNIEXPORT jint JNICALL Java_im_tox_tox4j_impl_jni_ToxCryptoJni_toxPassKeyDerive
  (JNIEnv *env, jclass, jbyteArray passphraseArray)
{
  auto passphrase = fromJavaArray (env, passphraseArray);

  return with_error_handling<ToxCrypto> (env,
    [] (Tox_Pass_Key *out_key)
      {
        return add_pass_key (pass_key_ptr (out_key, pass_key_deleter ()));
      },
    tox_pass_key_derive,
    passphrase.data (), passphrase.size ()
  );
}


/*
 * Class:     im_tox_tox4j_impl_jni_ToxCryptoJni
 * Method:    toxPassKeyDeriveWithSalt
 * Signature: ([B[B)I
 */
JNIEXPORT jint JNICALL Java_im_tox_tox4j_impl_jni_ToxCryptoJni_toxPassKeyDeriveWithSalt
  (JNIEnv *env, jclass, jbyteArray passphraseArray, jbyteArray saltArray)
{
  auto passphrase = fromJavaArray (env, passphraseArray);
  auto salt = fromJavaArray (env, saltArray);

  if (salt.size () != TOX_PASS_SALT_LENGTH)
    {
      throw_tox_exception<ToxCrypto, TOX_ERR_KEY_DERIVATION> (env, "INVALID_LENGTH");
      return 0;
    }

  return with_error_handling<ToxCrypto> (env,
    [] (Tox_Pass_Key *out_key)
      {
        return add_pass_key (pass_key_ptr (out_key, pass_key_deleter ()));
      },
    tox_pass_key_derive_with_salt,
    passphrase.data (), passphrase.size (),
    salt.data ()
  );
}


/*
 * Class:     im_tox_tox4j_impl_jni_ToxCryptoJni
 * Method:    toxPassKeyEncrypt
 * Signature: (I[B)[B
 */
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_impl_jni_ToxCryptoJni_toxPassKeyEncrypt
  (JNIEnv *env, jclass, jint instance_number, jbyteArray dataArray)
{
  pass_key_ptr pass_key = get_pass_key (instance_number);
  if (!pass_key)
    {
      throw_tox_exception<ToxCrypto, TOX_ERR_ENCRYPTION> (env, "NULL");
      return nullptr;
    }

  auto data = fromJavaArray (env, dataArray);
  std::vector<uint8_t> out (data.size () + TOX_PASS_ENCRYPTION_EXTRA_LENGTH);

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


/*
 * Class:     im_tox_tox4j_impl_jni_ToxCryptoJni
 * Method:    toxPassKeyDecrypt
 * Signature: (I[B)[B
 */
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_impl_jni_ToxCryptoJni_toxPassKeyDecrypt
  (JNIEnv *env, jclass, jint instance_number, jbyteArray dataArray)
{
  pass_key_ptr pass_key = get_pass_key (instance_number);
  if (!pass_key)
    {
      throw_tox_exception<ToxCrypto, TOX_ERR_DECRYPTION> (env, "NULL");
      return nullptr;
    }

  auto data = fromJavaArray (env, dataArray);
  std::vector<uint8_t> out (
    // If size is too small, the library will throw INVALID_LENGTH, but we need
    // to ensure that we don't end up with negative (or very large) output arrays here.
    std::max (
      0l,
      static_cast<long> (data.size ()) - TOX_PASS_ENCRYPTION_EXTRA_LENGTH
    )
  );

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
 * Method:    toxGetSalt
 * Signature: ([B)[B
 */
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_impl_jni_ToxCryptoJni_toxGetSalt
  (JNIEnv *env, jclass, jbyteArray dataArray)
{
  auto data = fromJavaArray (env, dataArray);
  // tox_get_salt reads the header before checking it.
  if (data.size () < TOX_PASS_ENCRYPTION_EXTRA_LENGTH)
    {
      throw_tox_exception<ToxCrypto, TOX_ERR_GET_SALT> (env, "BAD_FORMAT");
      return nullptr;
    }
  uint8_t salt[TOX_PASS_SALT_LENGTH] = { 0 };

  return with_error_handling<ToxCrypto> (env,
    [env, &salt] (bool)
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
