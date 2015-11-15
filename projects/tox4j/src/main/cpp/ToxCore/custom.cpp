#include "ToxCore.h"

#ifdef TOX_VERSION_MAJOR

using namespace core;


/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxFriendSendLossyPacket
 * Signature: (II[B)V
 */
TOX_METHOD (void, FriendSendLossyPacket,
  jint instanceNumber, jint friendNumber, jbyteArray packet)
{
  auto packetData = fromJavaArray (env, packet);
  return instances.with_instance_ign (env, instanceNumber,
    tox_friend_send_lossy_packet, friendNumber, packetData.data (), packetData.size ()
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxFriendSendLosslessPacket
 * Signature: (II[B)V
 */
TOX_METHOD (void, FriendSendLosslessPacket,
  jint instanceNumber, jint friendNumber, jbyteArray packet)
{
  auto packetData = fromJavaArray (env, packet);
  return instances.with_instance_ign (env, instanceNumber,
    tox_friend_send_lossless_packet, friendNumber, packetData.data (), packetData.size ()
  );
}

#endif
