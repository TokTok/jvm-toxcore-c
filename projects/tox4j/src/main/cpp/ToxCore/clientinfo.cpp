#include "ToxCore.h"

#ifdef TOX_VERSION_MAJOR

using namespace core;


/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxSelfGetPublicKey
 * Signature: (I)[B
 */
TOX_METHOD (jbyteArray, SelfGetPublicKey,
  jint instanceNumber)
{
  return instances.with_instance_noerr (env, instanceNumber,
    get_vector<uint8_t,
      constant_size<TOX_PUBLIC_KEY_SIZE>::make,
      tox_self_get_public_key>::make,
    env
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxSelfGetSecretKey
 * Signature: (I)[B
 */
TOX_METHOD (jbyteArray, SelfGetSecretKey,
  jint instanceNumber)
{
  return instances.with_instance_noerr (env, instanceNumber,
    get_vector<uint8_t,
      constant_size<TOX_SECRET_KEY_SIZE>::make,
      tox_self_get_secret_key>::make,
    env
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxSelfGetAddress
 * Signature: (I)[B
 */
TOX_METHOD (jbyteArray, SelfGetAddress,
  jint instanceNumber)
{
  return instances.with_instance_noerr (env, instanceNumber,
    get_vector<uint8_t,
      constant_size<TOX_ADDRESS_SIZE>::make,
      tox_self_get_address>::make,
    env
  );
}


/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxSelfSetName
 * Signature: (I[B)V
 */
TOX_METHOD (void, SelfSetName,
  jint instanceNumber, jbyteArray name)
{
  auto name_array = fromJavaArray (env, name);
  return instances.with_instance_ign (env, instanceNumber,
    tox_self_set_name, name_array.data (), name_array.size ());
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxSelfGetName
 * Signature: (I)[B
 */
TOX_METHOD (jbyteArray, SelfGetName,
  jint instanceNumber)
{
  return instances.with_instance_noerr (env, instanceNumber,
    get_vector<uint8_t,
      tox_self_get_name_size,
      tox_self_get_name>::make,
    env
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxSelfSetStatusMessage
 * Signature: (I[B)V
 */
TOX_METHOD (void, SelfSetStatusMessage,
  jint instanceNumber, jbyteArray statusMessage)
{
  auto status_message_array = fromJavaArray (env, statusMessage);
  return instances.with_instance_ign (env, instanceNumber,
    tox_self_set_status_message, status_message_array.data (), status_message_array.size ());
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxSelfGetStatusMessage
 * Signature: (I)[B
 */
TOX_METHOD (jbyteArray, SelfGetStatusMessage,
  jint instanceNumber)
{
  return instances.with_instance_noerr (env, instanceNumber,
    get_vector<uint8_t,
      tox_self_get_status_message_size,
      tox_self_get_status_message>::make,
    env
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxSelfSetStatus
 * Signature: (II)V
 */
TOX_METHOD (void, SelfSetStatus,
  jint instanceNumber, jint status)
{
  return instances.with_instance_noerr (env, instanceNumber,
    tox_self_set_status, Enum::valueOf<TOX_USER_STATUS> (env, status));
}

#endif
