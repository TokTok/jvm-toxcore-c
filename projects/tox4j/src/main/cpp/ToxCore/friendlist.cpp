#include "ToxCore.h"

#ifdef TOX_VERSION_MAJOR

using namespace core;


/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxFriendAdd
 * Signature: (I[B[B)I
 */
TOX_METHOD (jint, FriendAdd,
  jint instanceNumber, jbyteArray address, jbyteArray message)
{
  auto messageData = fromJavaArray (env, message);
  auto addressData = fromJavaArray (env, address);
  tox4j_assert (!address || addressData.size () == TOX_ADDRESS_SIZE);
  return instances.with_instance_err (env, instanceNumber,
    identity,
    tox_friend_add, addressData.data (), messageData.data (), messageData.size ()
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxFriendAddNorequest
 * Signature: (I[B)I
 */
TOX_METHOD (jint, FriendAddNorequest,
  jint instanceNumber, jbyteArray publicKey)
{
  auto public_key = fromJavaArray (env, publicKey);
  tox4j_assert (!publicKey || public_key.size () == TOX_PUBLIC_KEY_SIZE);
  return instances.with_instance_err (env, instanceNumber,
    identity,
    tox_friend_add_norequest, public_key.data ()
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxFriendDelete
 * Signature: (II)V
 */
TOX_METHOD (void, FriendDelete,
  jint instanceNumber, jint friendNumber)
{
  return instances.with_instance_ign (env, instanceNumber,
    tox_friend_delete, friendNumber
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxFriendByPublicKey
 * Signature: (I[B)I
 */
TOX_METHOD (jint, FriendByPublicKey,
  jint instanceNumber, jbyteArray publicKey)
{
  auto public_key = fromJavaArray (env, publicKey);
  tox4j_assert (!publicKey || public_key.size () == TOX_PUBLIC_KEY_SIZE);
  return instances.with_instance_err (env, instanceNumber,
    identity,
    tox_friend_by_public_key, public_key.data ()
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxFriendGetPublicKey
 * Signature: (II)[B
 */
TOX_METHOD (jbyteArray, FriendGetPublicKey,
  jint instanceNumber, jint friendNumber)
{
  uint8_t public_key[TOX_PUBLIC_KEY_SIZE];
  return instances.with_instance_err (env, instanceNumber,
    [&] (bool)
      {
        return toJavaArray (env, public_key);
      },
    tox_friend_get_public_key, friendNumber, public_key
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxSelfGetFriendList
 * Signature: (I)[I
 */
TOX_METHOD (jintArray, SelfGetFriendList,
  jint instanceNumber)
{
  return instances.with_instance_noerr (env, instanceNumber,
    get_vector<uint32_t,
      tox_self_get_friend_list_size,
      tox_self_get_friend_list>,
    env
  );
}

#endif
