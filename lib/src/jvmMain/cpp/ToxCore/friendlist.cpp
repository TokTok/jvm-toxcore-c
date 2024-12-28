#include "ToxCore.h"

using namespace core;


/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxFriendAdd
 * Signature: (I[B[B)I
 */
JNIEXPORT jint JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxFriendAdd
  (JNIEnv *env, jclass, jint instanceNumber, jbyteArray address, jbyteArray message)
{
  auto messageData = fromJavaArray (env, message);
  auto addressData = fromJavaArray (env, address);
  tox4j_assert (!address || addressData.size () == TOX_ADDRESS_SIZE);
  return instances.with_instance_err (env, instanceNumber,
    identity,
    tox_friend_add, addressData, messageData.data (), messageData.size ()
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxFriendAddNorequest
 * Signature: (I[B)I
 */
JNIEXPORT jint JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxFriendAddNorequest
  (JNIEnv *env, jclass, jint instanceNumber, jbyteArray publicKey)
{
  auto public_key = fromJavaArray (env, publicKey);
  tox4j_assert (!publicKey || public_key.size () == TOX_PUBLIC_KEY_SIZE);
  return instances.with_instance_err (env, instanceNumber,
    identity,
    tox_friend_add_norequest, public_key
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxFriendDelete
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxFriendDelete
  (JNIEnv *env, jclass, jint instanceNumber, jint friendNumber)
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
JNIEXPORT jint JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxFriendByPublicKey
  (JNIEnv *env, jclass, jint instanceNumber, jbyteArray publicKey)
{
  auto public_key = fromJavaArray (env, publicKey);
  tox4j_assert (!publicKey || public_key.size () == TOX_PUBLIC_KEY_SIZE);
  return instances.with_instance_err (env, instanceNumber,
    identity,
    tox_friend_by_public_key, public_key
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxFriendGetPublicKey
 * Signature: (II)[B
 */
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxFriendGetPublicKey
  (JNIEnv *env, jclass, jint instanceNumber, jint friendNumber)
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
JNIEXPORT jintArray JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxSelfGetFriendList
  (JNIEnv *env, jclass, jint instanceNumber)
{
  return instances.with_instance_noerr (env, instanceNumber,
    get_vector<uint32_t,
      tox_self_get_friend_list_size,
      tox_self_get_friend_list,
      jint>::make
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxSelfSetTyping
 * Signature: (IIZ)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxSelfSetTyping
  (JNIEnv *env, jclass, jint instanceNumber, jint friendNumber, jboolean isTyping)
{
  return instances.with_instance_ign (env, instanceNumber,
    tox_self_set_typing, friendNumber, isTyping
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxFriendSendMessage
 * Signature: (III[B)I
 */
JNIEXPORT jint JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxFriendSendMessage
  (JNIEnv *env, jclass, jint instanceNumber, jint friendNumber, jint messageType, jbyteArray message)
{
  auto message_array = fromJavaArray (env, message);

  return instances.with_instance_err (env, instanceNumber,
    identity,
    tox_friend_send_message, friendNumber, Enum::valueOf<Tox_Message_Type> (env, messageType), message_array.data (), message_array.size ()
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxFriendSendLossyPacket
 * Signature: (II[B)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxFriendSendLossyPacket
  (JNIEnv *env, jclass, jint instanceNumber, jint friendNumber, jbyteArray packet)
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
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxFriendSendLosslessPacket
  (JNIEnv *env, jclass, jint instanceNumber, jint friendNumber, jbyteArray packet)
{
  auto packetData = fromJavaArray (env, packet);
  return instances.with_instance_ign (env, instanceNumber,
    tox_friend_send_lossless_packet, friendNumber, packetData.data (), packetData.size ()
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxSelfGetPublicKey
 * Signature: (I)[B
 */
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxSelfGetPublicKey
  (JNIEnv *env, jclass, jint instanceNumber)
{
  return instances.with_instance_noerr (env, instanceNumber,
    get_vector<uint8_t,
      constant_size<TOX_PUBLIC_KEY_SIZE>::make,
      tox_self_get_public_key>::make
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxSelfGetSecretKey
 * Signature: (I)[B
 */
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxSelfGetSecretKey
  (JNIEnv *env, jclass, jint instanceNumber)
{
  return instances.with_instance_noerr (env, instanceNumber,
    get_vector<uint8_t,
      constant_size<TOX_SECRET_KEY_SIZE>::make,
      tox_self_get_secret_key>::make
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxSelfGetAddress
 * Signature: (I)[B
 */
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxSelfGetAddress
  (JNIEnv *env, jclass, jint instanceNumber)
{
  return instances.with_instance_noerr (env, instanceNumber,
    get_vector<uint8_t,
      constant_size<TOX_ADDRESS_SIZE>::make,
      tox_self_get_address>::make
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxSelfSetName
 * Signature: (I[B)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxSelfSetName
  (JNIEnv *env, jclass, jint instanceNumber, jbyteArray name)
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
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxSelfGetName
  (JNIEnv *env, jclass, jint instanceNumber)
{
  return instances.with_instance_noerr (env, instanceNumber,
    get_vector<uint8_t,
      tox_self_get_name_size,
      tox_self_get_name>::make
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxSelfSetStatusMessage
 * Signature: (I[B)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxSelfSetStatusMessage
  (JNIEnv *env, jclass, jint instanceNumber, jbyteArray statusMessage)
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
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxSelfGetStatusMessage
  (JNIEnv *env, jclass, jint instanceNumber)
{
  return instances.with_instance_noerr (env, instanceNumber,
    get_vector<uint8_t,
      tox_self_get_status_message_size,
      tox_self_get_status_message>::make
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxSelfSetStatus
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxSelfSetStatus
  (JNIEnv *env, jclass, jint instanceNumber, jint status)
{
  return instances.with_instance_noerr (env, instanceNumber,
    tox_self_set_status, Enum::valueOf<Tox_User_Status> (env, status));
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxConferenceNew
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxConferenceNew
  (JNIEnv *env, jclass, jint instanceNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity,
    tox_conference_new
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxConferenceDelete
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxConferenceDelete
  (JNIEnv *env, jclass, jint instanceNumber, jint conferenceNumber)
{
  return instances.with_instance_ign (env, instanceNumber,
    tox_conference_delete, conferenceNumber
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxConferencePeerCount
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxConferencePeerCount
  (JNIEnv *env, jclass, jint instanceNumber, jint conferenceNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity,
    tox_conference_peer_count, conferenceNumber
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxConferencePeerGetName
 * Signature: (III)[B
 */
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxConferencePeerGetName
  (JNIEnv *env, jclass, jint instanceNumber, jint conferenceNumber, jint peerNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity,
    get_vector_err<jbyte,
      decltype(tox_conference_peer_get_name_size),
      decltype(tox_conference_peer_get_name)>::make<
        tox_conference_peer_get_name_size,
        tox_conference_peer_get_name>, conferenceNumber, peerNumber
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxConferencePeerGetPublicKey
 * Signature: (III)[B
 */
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxConferencePeerGetPublicKey
  (JNIEnv *env, jclass, jint instanceNumber, jint conferenceNumber, jint peerNumber);

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxConferencePeerNumberIsOurs
 * Signature: (III)Z
 */
JNIEXPORT jboolean JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxConferencePeerNumberIsOurs
  (JNIEnv *env, jclass, jint instanceNumber, jint conferenceNumber, jint peerNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity,
    tox_conference_peer_number_is_ours, conferenceNumber, peerNumber
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxConferenceOfflinePeerCount
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxConferenceOfflinePeerCount
  (JNIEnv *env, jclass, jint instanceNumber, jint conferenceNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity,
    tox_conference_offline_peer_count, conferenceNumber
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxConferenceOfflinePeerGetName
 * Signature: (III)[B
 */
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxConferenceOfflinePeerGetName
  (JNIEnv *env, jclass, jint instanceNumber, jint conferenceNumber, jint offlinePeerNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity,
    get_vector_err<jbyte,
      decltype(tox_conference_offline_peer_get_name_size),
      decltype(tox_conference_offline_peer_get_name)>::make<
        tox_conference_offline_peer_get_name_size,
        tox_conference_offline_peer_get_name>, conferenceNumber, offlinePeerNumber
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxConferenceOfflinePeerGetPublicKey
 * Signature: (III)[B
 */
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxConferenceOfflinePeerGetPublicKey
  (JNIEnv *env, jclass, jint instanceNumber, jint conferenceNumber, jint offlinePeerNumber);

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxConferenceOfflinePeerGetLastActive
 * Signature: (III)J
 */
JNIEXPORT jlong JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxConferenceOfflinePeerGetLastActive
  (JNIEnv *env, jclass, jint instanceNumber, jint conferenceNumber, jint offlinePeerNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity,
    tox_conference_offline_peer_get_last_active, conferenceNumber, offlinePeerNumber
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxConferenceSetMaxOffline
 * Signature: (III)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxConferenceSetMaxOffline
  (JNIEnv *env, jclass, jint instanceNumber, jint conferenceNumber, jint maxOffline)
{
  return instances.with_instance_ign (env, instanceNumber,
    tox_conference_set_max_offline, conferenceNumber, maxOffline
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxConferenceInvite
 * Signature: (III)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxConferenceInvite
  (JNIEnv *env, jclass, jint instanceNumber, jint friendNumber, jint conferenceNumber)
{
  return instances.with_instance_ign (env, instanceNumber,
    tox_conference_invite, friendNumber, conferenceNumber
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxConferenceJoin
 * Signature: (II[B)I
 */
JNIEXPORT jint JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxConferenceJoin
  (JNIEnv *env, jclass, jint instanceNumber, jint friendNumber, jbyteArray cookie)
{
  auto cookieData = fromJavaArray (env, cookie);
  return instances.with_instance_err (env, instanceNumber,
    identity,
    tox_conference_join, friendNumber, cookieData.data (), cookieData.size ()
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxConferenceSendMessage
 * Signature: (III[B)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxConferenceSendMessage
  (JNIEnv *env, jclass, jint instanceNumber, jint conferenceNumber, jint type, jbyteArray message)
{
  auto messageData = fromJavaArray (env, message);
  return instances.with_instance_ign (env, instanceNumber,
    tox_conference_send_message, conferenceNumber, Enum::valueOf<Tox_Message_Type> (env, type), messageData.data (), messageData.size ()
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxConferenceGetTitle
 * Signature: (II)[B
 */
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxConferenceGetTitle
  (JNIEnv *env, jclass, jint instanceNumber, jint conferenceNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity,
    get_vector_err<jbyte,
      decltype(tox_conference_get_title_size),
      decltype(tox_conference_get_title)>::make<
        tox_conference_get_title_size,
        tox_conference_get_title>, conferenceNumber
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxConferenceSetTitle
 * Signature: (II[B)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxConferenceSetTitle
  (JNIEnv *env, jclass, jint instanceNumber, jint conferenceNumber, jbyteArray title)
{
  auto titleData = fromJavaArray (env, title);
  return instances.with_instance_ign (env, instanceNumber,
    tox_conference_set_title, conferenceNumber, titleData.data (), titleData.size ()
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxConferenceGetChatlist
 * Signature: (I)[I
 */
JNIEXPORT jintArray JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxConferenceGetChatlist
  (JNIEnv *env, jclass, jint instanceNumber)
{
  return instances.with_instance_noerr (env, instanceNumber,
    get_vector<uint32_t,
      tox_conference_get_chatlist_size,
      tox_conference_get_chatlist,
      jint>::make
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxConferenceGetType
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxConferenceGetType
  (JNIEnv *env, jclass, jint instanceNumber, jint conferenceNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity,
    tox_conference_get_type, conferenceNumber
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxConferenceGetId
 * Signature: (II)[B
 */
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxConferenceGetId
  (JNIEnv *env, jclass, jint instanceNumber, jint conferenceNumber);

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxConferenceById
 * Signature: (I[B)I
 */
JNIEXPORT jint JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxConferenceById
  (JNIEnv *env, jclass, jint instanceNumber, jbyteArray id)
{
  auto idData = fromJavaArray (env, id);
  return instances.with_instance_err (env, instanceNumber,
    identity,
    tox_conference_by_id, idData.data ()
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxConferenceGetUid
 * Signature: (II)[B
 */
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxConferenceGetUid
  (JNIEnv *env, jclass, jint instanceNumber, jint conferenceNumber);

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxGroupNew
 * Signature: (II[B[B)I
 */
JNIEXPORT jint JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxGroupNew
  (JNIEnv *env, jclass, jint instanceNumber, jint privacyState, jbyteArray groupName, jbyteArray name)
{
  auto groupNameData = fromJavaArray (env, groupName);
  auto nameData = fromJavaArray (env, name);
  return instances.with_instance_err (env, instanceNumber,
    identity,
    tox_group_new, Enum::valueOf<Tox_Group_Privacy_State> (env, privacyState), groupNameData.data (), groupNameData.size (), nameData.data (), nameData.size ()
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxGroupJoin
 * Signature: (I[B[B[B)I
 */
JNIEXPORT jint JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxGroupJoin
  (JNIEnv *env, jclass, jint instanceNumber, jbyteArray chatId, jbyteArray name, jbyteArray password)
{
  auto chatIdData = fromJavaArray (env, chatId);
  tox4j_assert (!chatId || chatIdData.size () == TOX_GROUP_CHAT_ID_SIZE);
  auto nameData = fromJavaArray (env, name);
  auto passwordData = fromJavaArray (env, password);
  return instances.with_instance_err (env, instanceNumber,
    identity,
    tox_group_join, chatIdData, nameData.data (), nameData.size (), passwordData.data (), passwordData.size ()
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxGroupIsConnected
 * Signature: (II)Z
 */
JNIEXPORT jboolean JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxGroupIsConnected
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity,
    tox_group_is_connected, groupNumber
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxGroupDisconnect
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxGroupDisconnect
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber)
{
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_disconnect, groupNumber
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxGroupLeave
 * Signature: (II[B)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxGroupLeave
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber, jbyteArray partMessage)
{
  auto partMessageData = fromJavaArray (env, partMessage);
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_leave, groupNumber, partMessageData.data (), partMessageData.size ()
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxGroupSelfSetName
 * Signature: (II[B)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxGroupSelfSetName
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber, jbyteArray name)
{
  auto nameData = fromJavaArray (env, name);
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_self_set_name, groupNumber, nameData.data (), nameData.size ()
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxGroupSelfGetName
 * Signature: (II)[B
 */
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxGroupSelfGetName
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity,
    get_vector_err<jbyte,
      decltype(tox_group_self_get_name_size),
      decltype(tox_group_self_get_name)>::make<
        tox_group_self_get_name_size,
        tox_group_self_get_name>, groupNumber
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxGroupSelfSetStatus
 * Signature: (III)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxGroupSelfSetStatus
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber, jint status)
{
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_self_set_status, groupNumber, Enum::valueOf<Tox_User_Status> (env, status)
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxGroupSelfGetStatus
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxGroupSelfGetStatus
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity,
    tox_group_self_get_status, groupNumber
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxGroupSelfGetRole
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxGroupSelfGetRole
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity,
    tox_group_self_get_role, groupNumber
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxGroupSelfGetPeerId
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxGroupSelfGetPeerId
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity,
    tox_group_self_get_peer_id, groupNumber
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxGroupSelfGetPublicKey
 * Signature: (II)[B
 */
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxGroupSelfGetPublicKey
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber)
{
  uint8_t public_key[TOX_PUBLIC_KEY_SIZE];
  return instances.with_instance_err (env, instanceNumber,
    [&] (bool)
      {
        return toJavaArray (env, public_key);
      },
    tox_group_self_get_public_key, groupNumber, public_key
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxGroupPeerGetName
 * Signature: (III)[B
 */
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxGroupPeerGetName
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber, jint peerId)
{
  return instances.with_instance_err (env, instanceNumber,
    identity,
    get_vector_err<jbyte,
      decltype(tox_group_peer_get_name_size),
      decltype(tox_group_peer_get_name)>::make<
        tox_group_peer_get_name_size,
        tox_group_peer_get_name>, groupNumber, peerId
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxGroupPeerGetStatus
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxGroupPeerGetStatus
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber, jint peerId)
{
  return instances.with_instance_err (env, instanceNumber,
    identity,
    tox_group_peer_get_status, groupNumber, peerId
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxGroupPeerGetRole
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxGroupPeerGetRole
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber, jint peerId)
{
  return instances.with_instance_err (env, instanceNumber,
    identity,
    tox_group_peer_get_role, groupNumber, peerId
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxGroupPeerGetConnectionStatus
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxGroupPeerGetConnectionStatus
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber, jint peerId)
{
  return instances.with_instance_err (env, instanceNumber,
    identity,
    tox_group_peer_get_connection_status, groupNumber, peerId
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxGroupPeerGetPublicKey
 * Signature: (III)[B
 */
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxGroupPeerGetPublicKey
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber, jint peerId)
{
  uint8_t public_key[TOX_PUBLIC_KEY_SIZE];
  return instances.with_instance_err (env, instanceNumber,
    [&] (bool)
      {
        return toJavaArray (env, public_key);
      },
    tox_group_peer_get_public_key, groupNumber, peerId, public_key
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxGroupSetTopic
 * Signature: (II[B)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxGroupSetTopic
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber, jbyteArray topic)
{
  auto topicData = fromJavaArray (env, topic);
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_set_topic, groupNumber, topicData.data (), topicData.size ()
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxGroupGetTopic
 * Signature: (II)[B
 */
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxGroupGetTopic
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity,
    get_vector_err<jbyte,
      decltype(tox_group_get_topic_size),
      decltype(tox_group_get_topic)>::make<
        tox_group_get_topic_size,
        tox_group_get_topic>, groupNumber
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxGroupGetName
 * Signature: (II)[B
 */
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxGroupGetName
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity,
    get_vector_err<jbyte,
      decltype(tox_group_get_name_size),
      decltype(tox_group_get_name)>::make<
        tox_group_get_name_size,
        tox_group_get_name>, groupNumber
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxGroupGetChatId
 * Signature: (II)[B
 */
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxGroupGetChatId
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber)
{
  uint8_t chat_id[TOX_GROUP_CHAT_ID_SIZE];
  return instances.with_instance_err (env, instanceNumber,
    [&] (bool)
      {
        return toJavaArray (env, chat_id);
      },
    tox_group_get_chat_id, groupNumber, chat_id
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxGroupGetPrivacyState
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxGroupGetPrivacyState
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity,
    tox_group_get_privacy_state, groupNumber
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxGroupGetVoiceState
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxGroupGetVoiceState
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity,
    tox_group_get_voice_state, groupNumber
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxGroupGetTopicLock
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxGroupGetTopicLock
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity,
    tox_group_get_topic_lock, groupNumber
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxGroupGetPeerLimit
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxGroupGetPeerLimit
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity,
    tox_group_get_peer_limit, groupNumber
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxGroupGetPassword
 * Signature: (II)[B
 */
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxGroupGetPassword
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity,
    get_vector_err<jbyte,
      decltype(tox_group_get_password_size),
      decltype(tox_group_get_password)>::make<
        tox_group_get_password_size,
        tox_group_get_password>, groupNumber
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxGroupSendMessage
 * Signature: (III[B)I
 */
JNIEXPORT jint JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxGroupSendMessage
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber, jint type, jbyteArray message)
{
  auto messageData = fromJavaArray (env, message);
  return instances.with_instance_err (env, instanceNumber,
    identity,
    tox_group_send_message, groupNumber, Enum::valueOf<Tox_Message_Type> (env, type), messageData.data (), messageData.size ()
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxGroupSendPrivateMessage
 * Signature: (IIII[B)I
 */
JNIEXPORT jint JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxGroupSendPrivateMessage
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber, jint peerId, jint type, jbyteArray message)
{
  auto messageData = fromJavaArray (env, message);
  return instances.with_instance_err (env, instanceNumber,
    identity,
    tox_group_send_private_message, groupNumber, peerId, Enum::valueOf<Tox_Message_Type> (env, type), messageData.data (), messageData.size ()
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxGroupSendCustomPacket
 * Signature: (IIZ[B)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxGroupSendCustomPacket
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber, jboolean lossless, jbyteArray data)
{
  auto dataData = fromJavaArray (env, data);
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_send_custom_packet, groupNumber, lossless, dataData.data (), dataData.size ()
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxGroupSendCustomPrivatePacket
 * Signature: (IIIZ[B)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxGroupSendCustomPrivatePacket
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber, jint peerId, jboolean lossless, jbyteArray data)
{
  auto dataData = fromJavaArray (env, data);
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_send_custom_private_packet, groupNumber, peerId, lossless, dataData.data (), dataData.size ()
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxGroupInviteFriend
 * Signature: (III)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxGroupInviteFriend
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber, jint friendNumber)
{
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_invite_friend, groupNumber, friendNumber
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxGroupInviteAccept
 * Signature: (II[B[B[B)I
 */
JNIEXPORT jint JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxGroupInviteAccept
  (JNIEnv *env, jclass, jint instanceNumber, jint friendNumber, jbyteArray inviteData, jbyteArray name, jbyteArray password)
{
  auto inviteDataData = fromJavaArray (env, inviteData);
  auto nameData = fromJavaArray (env, name);
  auto passwordData = fromJavaArray (env, password);
  return instances.with_instance_err (env, instanceNumber,
    identity,
    tox_group_invite_accept, friendNumber, inviteDataData.data (), inviteDataData.size (), nameData.data (), nameData.size (), passwordData.data (), passwordData.size ()
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxGroupSetPassword
 * Signature: (II[B)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxGroupSetPassword
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber, jbyteArray password)
{
  auto passwordData = fromJavaArray (env, password);
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_set_password, groupNumber, passwordData.data (), passwordData.size ()
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxGroupSetTopicLock
 * Signature: (III)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxGroupSetTopicLock
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber, jint topicLock)
{
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_set_topic_lock, groupNumber, Enum::valueOf<Tox_Group_Topic_Lock> (env, topicLock)
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxGroupSetVoiceState
 * Signature: (III)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxGroupSetVoiceState
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber, jint voiceState)
{
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_set_voice_state, groupNumber, Enum::valueOf<Tox_Group_Voice_State> (env, voiceState)
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxGroupSetPrivacyState
 * Signature: (III)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxGroupSetPrivacyState
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber, jint privacyState)
{
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_set_privacy_state, groupNumber, Enum::valueOf<Tox_Group_Privacy_State> (env, privacyState)
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxGroupSetPeerLimit
 * Signature: (III)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxGroupSetPeerLimit
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber, jint peerLimit)
{
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_set_peer_limit, groupNumber, peerLimit
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxGroupSetIgnore
 * Signature: (IIIZ)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxGroupSetIgnore
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber, jint peerId, jboolean ignore)
{
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_set_ignore, groupNumber, peerId, ignore
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxGroupSetRole
 * Signature: (IIII)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxGroupSetRole
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber, jint peerId, jint role)
{
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_set_role, groupNumber, peerId, Enum::valueOf<Tox_Group_Role> (env, role)
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxGroupKickPeer
 * Signature: (III)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxGroupKickPeer
  (JNIEnv *env, jclass, jint instanceNumber, jint groupNumber, jint peerId)
{
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_kick_peer, groupNumber, peerId
  );
}
