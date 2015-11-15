#include "ToxCore.h"

#ifdef TOX_VERSION_MAJOR

using namespace core;


/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxSelfSetTyping
 * Signature: (IIZ)V
 */
TOX_METHOD (void, SelfSetTyping,
  jint instanceNumber, jint friendNumber, jboolean isTyping)
{
  return instances.with_instance_ign (env, instanceNumber,
    tox_self_set_typing, friendNumber, isTyping
  );
}


/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxFriendSendMessage
 * Signature: (IIII[B)I
 */
TOX_METHOD (jint, FriendSendMessage,
  jint instanceNumber, jint friendNumber, jint messageType, jint timeDelta, jbyteArray message)
{
  auto message_array = fromJavaArray (env, message);

  return instances.with_instance_err (env, instanceNumber,
    identity,
    tox_friend_send_message, friendNumber, Enum::valueOf<TOX_MESSAGE_TYPE> (env, messageType), message_array.data (), message_array.size ()
  );
}

#endif
