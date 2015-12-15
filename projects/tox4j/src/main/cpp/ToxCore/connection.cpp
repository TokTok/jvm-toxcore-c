#include "ToxCore.h"

#ifdef TOX_VERSION_MAJOR

using namespace core;


static void
toxBootstrapLike (bool function (Tox *tox,
                                 char const *host,
                                 uint16_t port,
                                 uint8_t const *public_key,
                                 TOX_ERR_BOOTSTRAP *error),
                  JNIEnv *env,
                  jint instanceNumber,
                  jstring address,
                  jint port,
                  jbyteArray publicKey)
{
  tox4j_assert (port >= 0);
  tox4j_assert (port <= 65535);

  auto public_key = fromJavaArray (env, publicKey);
  tox4j_assert (!publicKey || public_key.size () == TOX_PUBLIC_KEY_SIZE);

  return instances.with_instance_ign (env, instanceNumber,
    function, UTFChars (env, address).data (), port, public_key.data ()
  );
}


/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxBootstrap
 * Signature: (ILjava/lang/String;I[B)V
 */
TOX_METHOD (void, Bootstrap,
   jint instanceNumber, jstring address, jint port, jbyteArray publicKey)
{
  return toxBootstrapLike (tox_bootstrap, env, instanceNumber, address, port, publicKey);
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxAddTcpRelay
 * Signature: (ILjava/lang/String;I[B)V
 */
TOX_METHOD (void, AddTcpRelay,
   jint instanceNumber, jstring address, jint port, jbyteArray publicKey)
{
  return toxBootstrapLike (tox_add_tcp_relay, env, instanceNumber, address, port, publicKey);
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxSelfGetUdpPort
 * Signature: (I)I
 */
TOX_METHOD (jint, SelfGetUdpPort,
  jint instanceNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity,
    tox_self_get_udp_port
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxSelfGetTcpPort
 * Signature: (I)I
 */
TOX_METHOD (jint, SelfGetTcpPort,
  jint instanceNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity,
    tox_self_get_tcp_port
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxSelfGetDhtId
 * Signature: (I)[B
 */
TOX_METHOD (jbyteArray, SelfGetDhtId,
  jint instanceNumber)
{
  return instances.with_instance_noerr (env, instanceNumber,
    get_vector<uint8_t,
      constant_size<TOX_PUBLIC_KEY_SIZE>::make,
      tox_self_get_dht_id>::make,
    env
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxIterate
 * Signature: (I)[B
 */
TOX_METHOD (jbyteArray, Iterate,
  jint instanceNumber)
{
  return instances.with_instance (env, instanceNumber,
    [=] (Tox *tox, Events &events) -> jbyteArray
      {
        LogEntry log_entry (instanceNumber, tox_iterate, tox);

        log_entry.print_result (tox_iterate, tox);
        if (events.ByteSize () == 0)
          return nullptr;

        std::vector<char> buffer (events.ByteSize ());
        events.SerializeToArray (buffer.data (), buffer.size ());
        events.Clear ();

        return toJavaArray (env, buffer);
      }
  );
}

#endif
