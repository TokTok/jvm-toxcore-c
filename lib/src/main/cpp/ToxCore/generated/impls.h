// im.tox.tox4j.impl.jni.ToxCoreJni

JAVA_METHOD (jint, toxConferenceNew,
  jint instanceNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity, tox_conference_new
  );
}

JAVA_METHOD (void, toxConferenceDelete,
  jint instanceNumber, jint conferenceNumber)
{
  return instances.with_instance_ign (env, instanceNumber,
    tox_conference_delete, conferenceNumber
  );
}

JAVA_METHOD (jint, toxConferencePeerCount,
  jint instanceNumber, jint conferenceNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity, tox_conference_peer_count, conferenceNumber
  );
}

JAVA_METHOD (jint, toxConferenceOfflinePeerCount,
  jint instanceNumber, jint conferenceNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity, tox_conference_offline_peer_count, conferenceNumber
  );
}

JAVA_METHOD (void, toxConferenceSetMaxOffline,
  jint instanceNumber, jint conferenceNumber, jint maxOffline)
{
  return instances.with_instance_ign (env, instanceNumber,
    tox_conference_set_max_offline, conferenceNumber, maxOffline
  );
}

JAVA_METHOD (jint, toxConferenceJoin,
  jint instanceNumber, jint friendNumber, jbyteArray cookie)
{
  auto cookieData = fromJavaArray (env, cookie);
  return instances.with_instance_err (env, instanceNumber,
    identity, tox_conference_join, friendNumber, cookieData.data (), cookieData.size ()
  );
}

JAVA_METHOD (void, toxConferenceSendMessage,
  jint instanceNumber, jint conferenceNumber, jint type, jbyteArray message)
{
  auto messageData = fromJavaArray (env, message);
  return instances.with_instance_ign (env, instanceNumber,
    tox_conference_send_message, conferenceNumber, Enum::valueOf<Tox_Message_Type> (env, type), messageData.data (), messageData.size ()
  );
}

JAVA_METHOD (jbyteArray, toxConferenceGetTitle,
  jint instanceNumber, jint conferenceNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity,
    get_vector_err<jbyte, decltype(tox_conference_get_title_size), decltype(tox_conference_get_title)>::make<
      tox_conference_get_title_size,
      tox_conference_get_title>, conferenceNumber
  );
}

JAVA_METHOD (void, toxConferenceSetTitle,
  jint instanceNumber, jint conferenceNumber, jbyteArray title)
{
  auto titleData = fromJavaArray (env, title);
  return instances.with_instance_ign (env, instanceNumber,
    tox_conference_set_title, conferenceNumber, titleData.data (), titleData.size ()
  );
}

JAVA_METHOD (jint, toxConferenceGetType,
  jint instanceNumber, jint conferenceNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity, tox_conference_get_type, conferenceNumber
  );
}

// toxConferenceGetId is hand-written.

JAVA_METHOD (jint, toxConferenceById,
  jint instanceNumber, jbyteArray id)
{
  auto idData = fromJavaArray (env, id);
  tox4j_assert (!id || idData.size () == TOX_CONFERENCE_ID_SIZE);
  return instances.with_instance_err (env, instanceNumber,
    identity, tox_conference_by_id, idData
  );
}

JAVA_METHOD (jbyteArray, toxConferenceOfflinePeerGetName,
  jint instanceNumber, jint conferenceNumber, jint offlinePeerNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity,
    get_vector_err<jbyte, decltype(tox_conference_offline_peer_get_name_size), decltype(tox_conference_offline_peer_get_name)>::make<
      tox_conference_offline_peer_get_name_size,
      tox_conference_offline_peer_get_name>, conferenceNumber, offlinePeerNumber
  );
}

JAVA_METHOD (jbyteArray, toxConferenceOfflinePeerGetPublicKey,
  jint instanceNumber, jint conferenceNumber, jint offlinePeerNumber)
{
  uint8_t result[TOX_PUBLIC_KEY_SIZE];
  return instances.with_instance_err (env, instanceNumber,
    [&] (bool) { return toJavaArray (env, result); },
    tox_conference_offline_peer_get_public_key, conferenceNumber, offlinePeerNumber, result
  );
}

JAVA_METHOD (jlong, toxConferenceOfflinePeerGetLastActive,
  jint instanceNumber, jint conferenceNumber, jint offlinePeerNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity, tox_conference_offline_peer_get_last_active, conferenceNumber, offlinePeerNumber
  );
}

JAVA_METHOD (jbyteArray, toxConferencePeerGetName,
  jint instanceNumber, jint conferenceNumber, jint peerNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity,
    get_vector_err<jbyte, decltype(tox_conference_peer_get_name_size), decltype(tox_conference_peer_get_name)>::make<
      tox_conference_peer_get_name_size,
      tox_conference_peer_get_name>, conferenceNumber, peerNumber
  );
}

JAVA_METHOD (jbyteArray, toxConferencePeerGetPublicKey,
  jint instanceNumber, jint conferenceNumber, jint peerNumber)
{
  uint8_t result[TOX_PUBLIC_KEY_SIZE];
  return instances.with_instance_err (env, instanceNumber,
    [&] (bool) { return toJavaArray (env, result); },
    tox_conference_peer_get_public_key, conferenceNumber, peerNumber, result
  );
}

JAVA_METHOD (jboolean, toxConferencePeerNumberIsOurs,
  jint instanceNumber, jint conferenceNumber, jint peerNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity, tox_conference_peer_number_is_ours, conferenceNumber, peerNumber
  );
}

JAVA_METHOD (jint, toxFileSend,
  jint instanceNumber, jint friendNumber, jint kind, jlong fileSize, jbyteArray fileId, jbyteArray filename)
{
  auto fileIdData = fromJavaArray (env, fileId);
  tox4j_assert (!fileId || fileIdData.size () == TOX_FILE_ID_LENGTH);
  auto filenameData = fromJavaArray (env, filename);
  return instances.with_instance_err (env, instanceNumber,
    identity, tox_file_send, friendNumber, kind, fileSize, fileIdData, filenameData.data (), filenameData.size ()
  );
}

JAVA_METHOD (jint, toxFriendAdd,
  jint instanceNumber, jbyteArray address, jbyteArray message)
{
  auto addressData = fromJavaArray (env, address);
  tox4j_assert (!address || addressData.size () == TOX_ADDRESS_SIZE);
  auto messageData = fromJavaArray (env, message);
  return instances.with_instance_err (env, instanceNumber,
    identity, tox_friend_add, addressData, messageData.data (), messageData.size ()
  );
}

JAVA_METHOD (jint, toxFriendAddNorequest,
  jint instanceNumber, jbyteArray publicKey)
{
  auto publicKeyData = fromJavaArray (env, publicKey);
  tox4j_assert (!publicKey || publicKeyData.size () == TOX_PUBLIC_KEY_SIZE);
  return instances.with_instance_err (env, instanceNumber,
    identity, tox_friend_add_norequest, publicKeyData
  );
}

JAVA_METHOD (void, toxFriendDelete,
  jint instanceNumber, jint friendNumber)
{
  return instances.with_instance_ign (env, instanceNumber,
    tox_friend_delete, friendNumber
  );
}

JAVA_METHOD (jint, toxFriendByPublicKey,
  jint instanceNumber, jbyteArray publicKey)
{
  auto publicKeyData = fromJavaArray (env, publicKey);
  tox4j_assert (!publicKey || publicKeyData.size () == TOX_PUBLIC_KEY_SIZE);
  return instances.with_instance_err (env, instanceNumber,
    identity, tox_friend_by_public_key, publicKeyData
  );
}

JAVA_METHOD (jboolean, toxFriendExists,
  jint instanceNumber, jint friendNumber)
{
  return instances.with_instance_noerr (env, instanceNumber,
    tox_friend_exists, friendNumber
  );
}

JAVA_METHOD (jbyteArray, toxFriendGetPublicKey,
  jint instanceNumber, jint friendNumber)
{
  uint8_t result[TOX_PUBLIC_KEY_SIZE];
  return instances.with_instance_err (env, instanceNumber,
    [&] (bool) { return toJavaArray (env, result); },
    tox_friend_get_public_key, friendNumber, result
  );
}

JAVA_METHOD (jlong, toxFriendGetLastOnline,
  jint instanceNumber, jint friendNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity, tox_friend_get_last_online, friendNumber
  );
}

JAVA_METHOD (jboolean, toxFriendGetTyping,
  jint instanceNumber, jint friendNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity, tox_friend_get_typing, friendNumber
  );
}

JAVA_METHOD (jint, toxFriendSendMessage,
  jint instanceNumber, jint friendNumber, jint type, jbyteArray message)
{
  auto messageData = fromJavaArray (env, message);
  return instances.with_instance_err (env, instanceNumber,
    identity, tox_friend_send_message, friendNumber, Enum::valueOf<Tox_Message_Type> (env, type), messageData.data (), messageData.size ()
  );
}

JAVA_METHOD (void, toxFriendSendLossyPacket,
  jint instanceNumber, jint friendNumber, jbyteArray data)
{
  auto dataData = fromJavaArray (env, data);
  return instances.with_instance_ign (env, instanceNumber,
    tox_friend_send_lossy_packet, friendNumber, dataData.data (), dataData.size ()
  );
}

JAVA_METHOD (void, toxFriendSendLosslessPacket,
  jint instanceNumber, jint friendNumber, jbyteArray data)
{
  auto dataData = fromJavaArray (env, data);
  return instances.with_instance_ign (env, instanceNumber,
    tox_friend_send_lossless_packet, friendNumber, dataData.data (), dataData.size ()
  );
}

JAVA_METHOD (jint, toxGroupNew,
  jint instanceNumber, jint privacyState, jbyteArray groupName, jbyteArray name)
{
  auto groupNameData = fromJavaArray (env, groupName);
  auto nameData = fromJavaArray (env, name);
  return instances.with_instance_err (env, instanceNumber,
    identity, tox_group_new, Enum::valueOf<Tox_Group_Privacy_State> (env, privacyState), groupNameData.data (), groupNameData.size (), nameData.data (), nameData.size ()
  );
}

JAVA_METHOD (jint, toxGroupJoin,
  jint instanceNumber, jbyteArray chatId, jbyteArray name, jbyteArray password)
{
  auto chatIdData = fromJavaArray (env, chatId);
  tox4j_assert (!chatId || chatIdData.size () == TOX_GROUP_CHAT_ID_SIZE);
  auto nameData = fromJavaArray (env, name);
  auto passwordData = fromJavaArray (env, password);
  return instances.with_instance_err (env, instanceNumber,
    identity, tox_group_join, chatIdData, nameData.data (), nameData.size (), passwordData.data (), passwordData.size ()
  );
}

JAVA_METHOD (jboolean, toxGroupIsConnected,
  jint instanceNumber, jint groupNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity, tox_group_is_connected, groupNumber
  );
}

JAVA_METHOD (void, toxGroupDisconnect,
  jint instanceNumber, jint groupNumber)
{
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_disconnect, groupNumber
  );
}

JAVA_METHOD (void, toxGroupLeave,
  jint instanceNumber, jint groupNumber, jbyteArray partMessage)
{
  auto partMessageData = fromJavaArray (env, partMessage);
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_leave, groupNumber, partMessageData.data (), partMessageData.size ()
  );
}

JAVA_METHOD (void, toxGroupSelfSetName,
  jint instanceNumber, jint groupNumber, jbyteArray name)
{
  auto nameData = fromJavaArray (env, name);
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_self_set_name, groupNumber, nameData.data (), nameData.size ()
  );
}

JAVA_METHOD (jbyteArray, toxGroupSelfGetName,
  jint instanceNumber, jint groupNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity,
    get_vector_err<jbyte, decltype(tox_group_self_get_name_size), decltype(tox_group_self_get_name)>::make<
      tox_group_self_get_name_size,
      tox_group_self_get_name>, groupNumber
  );
}

JAVA_METHOD (void, toxGroupSelfSetStatus,
  jint instanceNumber, jint groupNumber, jint status)
{
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_self_set_status, groupNumber, Enum::valueOf<Tox_User_Status> (env, status)
  );
}

JAVA_METHOD (jint, toxGroupSelfGetStatus,
  jint instanceNumber, jint groupNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity, tox_group_self_get_status, groupNumber
  );
}

JAVA_METHOD (jint, toxGroupSelfGetRole,
  jint instanceNumber, jint groupNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity, tox_group_self_get_role, groupNumber
  );
}

JAVA_METHOD (jint, toxGroupSelfGetPeerId,
  jint instanceNumber, jint groupNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity, tox_group_self_get_peer_id, groupNumber
  );
}

JAVA_METHOD (jbyteArray, toxGroupSelfGetPublicKey,
  jint instanceNumber, jint groupNumber)
{
  uint8_t result[TOX_PUBLIC_KEY_SIZE];
  return instances.with_instance_err (env, instanceNumber,
    [&] (bool) { return toJavaArray (env, result); },
    tox_group_self_get_public_key, groupNumber, result
  );
}

JAVA_METHOD (void, toxGroupSetTopic,
  jint instanceNumber, jint groupNumber, jbyteArray topic)
{
  auto topicData = fromJavaArray (env, topic);
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_set_topic, groupNumber, topicData.data (), topicData.size ()
  );
}

JAVA_METHOD (jbyteArray, toxGroupGetTopic,
  jint instanceNumber, jint groupNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity,
    get_vector_err<jbyte, decltype(tox_group_get_topic_size), decltype(tox_group_get_topic)>::make<
      tox_group_get_topic_size,
      tox_group_get_topic>, groupNumber
  );
}

JAVA_METHOD (jbyteArray, toxGroupGetName,
  jint instanceNumber, jint groupNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity,
    get_vector_err<jbyte, decltype(tox_group_get_name_size), decltype(tox_group_get_name)>::make<
      tox_group_get_name_size,
      tox_group_get_name>, groupNumber
  );
}

JAVA_METHOD (jbyteArray, toxGroupGetChatId,
  jint instanceNumber, jint groupNumber)
{
  uint8_t result[TOX_GROUP_CHAT_ID_SIZE];
  return instances.with_instance_err (env, instanceNumber,
    [&] (bool) { return toJavaArray (env, result); },
    tox_group_get_chat_id, groupNumber, result
  );
}

JAVA_METHOD (jint, toxGroupGetPrivacyState,
  jint instanceNumber, jint groupNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity, tox_group_get_privacy_state, groupNumber
  );
}

JAVA_METHOD (jint, toxGroupGetVoiceState,
  jint instanceNumber, jint groupNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity, tox_group_get_voice_state, groupNumber
  );
}

JAVA_METHOD (jint, toxGroupGetTopicLock,
  jint instanceNumber, jint groupNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity, tox_group_get_topic_lock, groupNumber
  );
}

JAVA_METHOD (jint, toxGroupGetPeerLimit,
  jint instanceNumber, jint groupNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity, tox_group_get_peer_limit, groupNumber
  );
}

JAVA_METHOD (jbyteArray, toxGroupGetPassword,
  jint instanceNumber, jint groupNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity,
    get_vector_err<jbyte, decltype(tox_group_get_password_size), decltype(tox_group_get_password)>::make<
      tox_group_get_password_size,
      tox_group_get_password>, groupNumber
  );
}

JAVA_METHOD (jint, toxGroupSendMessage,
  jint instanceNumber, jint groupNumber, jint messageType, jbyteArray message)
{
  auto messageData = fromJavaArray (env, message);
  return instances.with_instance_err (env, instanceNumber,
    identity, tox_group_send_message, groupNumber, Enum::valueOf<Tox_Message_Type> (env, messageType), messageData.data (), messageData.size ()
  );
}

JAVA_METHOD (jint, toxGroupSendPrivateMessage,
  jint instanceNumber, jint groupNumber, jint peerId, jint messageType, jbyteArray message)
{
  auto messageData = fromJavaArray (env, message);
  return instances.with_instance_err (env, instanceNumber,
    identity, tox_group_send_private_message, groupNumber, peerId, Enum::valueOf<Tox_Message_Type> (env, messageType), messageData.data (), messageData.size ()
  );
}

JAVA_METHOD (void, toxGroupSendCustomPacket,
  jint instanceNumber, jint groupNumber, jboolean lossless, jbyteArray data)
{
  auto dataData = fromJavaArray (env, data);
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_send_custom_packet, groupNumber, lossless, dataData.data (), dataData.size ()
  );
}

JAVA_METHOD (void, toxGroupSendCustomPrivatePacket,
  jint instanceNumber, jint groupNumber, jint peerId, jboolean lossless, jbyteArray data)
{
  auto dataData = fromJavaArray (env, data);
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_send_custom_private_packet, groupNumber, peerId, lossless, dataData.data (), dataData.size ()
  );
}

JAVA_METHOD (void, toxGroupInviteFriend,
  jint instanceNumber, jint groupNumber, jint friendNumber)
{
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_invite_friend, groupNumber, friendNumber
  );
}

JAVA_METHOD (jint, toxGroupInviteAccept,
  jint instanceNumber, jint friendNumber, jbyteArray inviteData, jbyteArray name, jbyteArray password)
{
  auto inviteDataData = fromJavaArray (env, inviteData);
  auto nameData = fromJavaArray (env, name);
  auto passwordData = fromJavaArray (env, password);
  return instances.with_instance_err (env, instanceNumber,
    identity, tox_group_invite_accept, friendNumber, inviteDataData.data (), inviteDataData.size (), nameData.data (), nameData.size (), passwordData.data (), passwordData.size ()
  );
}

JAVA_METHOD (void, toxGroupSetPassword,
  jint instanceNumber, jint groupNumber, jbyteArray password)
{
  auto passwordData = fromJavaArray (env, password);
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_set_password, groupNumber, passwordData.data (), passwordData.size ()
  );
}

JAVA_METHOD (void, toxGroupSetTopicLock,
  jint instanceNumber, jint groupNumber, jint topicLock)
{
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_set_topic_lock, groupNumber, Enum::valueOf<Tox_Group_Topic_Lock> (env, topicLock)
  );
}

JAVA_METHOD (void, toxGroupSetVoiceState,
  jint instanceNumber, jint groupNumber, jint voiceState)
{
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_set_voice_state, groupNumber, Enum::valueOf<Tox_Group_Voice_State> (env, voiceState)
  );
}

JAVA_METHOD (void, toxGroupSetPrivacyState,
  jint instanceNumber, jint groupNumber, jint privacyState)
{
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_set_privacy_state, groupNumber, Enum::valueOf<Tox_Group_Privacy_State> (env, privacyState)
  );
}

JAVA_METHOD (void, toxGroupSetPeerLimit,
  jint instanceNumber, jint groupNumber, jint peerLimit)
{
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_set_peer_limit, groupNumber, peerLimit
  );
}

JAVA_METHOD (void, toxGroupSetIgnore,
  jint instanceNumber, jint groupNumber, jint peerId, jboolean ignore)
{
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_set_ignore, groupNumber, peerId, ignore
  );
}

JAVA_METHOD (void, toxGroupSetRole,
  jint instanceNumber, jint groupNumber, jint peerId, jint role)
{
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_set_role, groupNumber, peerId, Enum::valueOf<Tox_Group_Role> (env, role)
  );
}

JAVA_METHOD (void, toxGroupKickPeer,
  jint instanceNumber, jint groupNumber, jint peerId)
{
  return instances.with_instance_ign (env, instanceNumber,
    tox_group_kick_peer, groupNumber, peerId
  );
}

JAVA_METHOD (jbyteArray, toxGroupPeerGetName,
  jint instanceNumber, jint groupNumber, jint peerNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity,
    get_vector_err<jbyte, decltype(tox_group_peer_get_name_size), decltype(tox_group_peer_get_name)>::make<
      tox_group_peer_get_name_size,
      tox_group_peer_get_name>, groupNumber, peerNumber
  );
}

JAVA_METHOD (jint, toxGroupPeerGetStatus,
  jint instanceNumber, jint groupNumber, jint peerNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity, tox_group_peer_get_status, groupNumber, peerNumber
  );
}

JAVA_METHOD (jint, toxGroupPeerGetRole,
  jint instanceNumber, jint groupNumber, jint peerNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity, tox_group_peer_get_role, groupNumber, peerNumber
  );
}

JAVA_METHOD (jint, toxGroupPeerGetConnectionStatus,
  jint instanceNumber, jint groupNumber, jint peerNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity, tox_group_peer_get_connection_status, groupNumber, peerNumber
  );
}

JAVA_METHOD (jbyteArray, toxGroupPeerGetPublicKey,
  jint instanceNumber, jint groupNumber, jint peerNumber)
{
  uint8_t result[TOX_PUBLIC_KEY_SIZE];
  return instances.with_instance_err (env, instanceNumber,
    [&] (bool) { return toJavaArray (env, result); },
    tox_group_peer_get_public_key, groupNumber, peerNumber, result
  );
}

JAVA_METHOD (jbyteArray, toxGetSavedata,
  jint instanceNumber)
{
  return instances.with_instance_noerr (env, instanceNumber,
    get_vector<uint8_t,
      tox_get_savedata_size,
      tox_get_savedata>::make
  );
}

JAVA_METHOD (void, toxBootstrap,
  jint instanceNumber, jstring host, jint port, jbyteArray publicKey)
{
  auto publicKeyData = fromJavaArray (env, publicKey);
  tox4j_assert (!publicKey || publicKeyData.size () == TOX_DHT_ID_SIZE);
  return instances.with_instance_ign (env, instanceNumber,
    tox_bootstrap, UTFChars (env, host).data (), port, publicKeyData
  );
}

JAVA_METHOD (void, toxAddTcpRelay,
  jint instanceNumber, jstring host, jint port, jbyteArray publicKey)
{
  auto publicKeyData = fromJavaArray (env, publicKey);
  tox4j_assert (!publicKey || publicKeyData.size () == TOX_DHT_ID_SIZE);
  return instances.with_instance_ign (env, instanceNumber,
    tox_add_tcp_relay, UTFChars (env, host).data (), port, publicKeyData
  );
}

JAVA_METHOD (jint, toxIterationInterval,
  jint instanceNumber)
{
  return instances.with_instance_noerr (env, instanceNumber,
    tox_iteration_interval
  );
}


JAVA_METHOD (jbyteArray, toxIterate,
  jint instanceNumber)
{
  return instances.with_instance (env, instanceNumber,
    [=] (Tox *self, Events &events) -> jbyteArray
      {
        tox_iterate(self, &events);
        if (events.ByteSizeLong () == 0)
          return nullptr;

        std::vector<char> buffer (events.ByteSizeLong ());
        if (!events.SerializeToArray (buffer.data (), buffer.size ()))
          return nullptr;
        events.Clear ();

        return toJavaArray (env, buffer);
      }
  );
}

JAVA_METHOD (jbyteArray, toxSelfGetAddress,
  jint instanceNumber)
{
  return instances.with_instance_noerr (env, instanceNumber,
    get_vector<uint8_t, constant_size<TOX_ADDRESS_SIZE>::make, tox_self_get_address>::make
  );
}

JAVA_METHOD (void, toxSelfSetNospam,
  jint instanceNumber, jint nospam)
{
  return instances.with_instance_noerr (env, instanceNumber,
    tox_self_set_nospam, nospam
  );
}

JAVA_METHOD (jint, toxSelfGetNospam,
  jint instanceNumber)
{
  return instances.with_instance_noerr (env, instanceNumber,
    tox_self_get_nospam
  );
}

JAVA_METHOD (jbyteArray, toxSelfGetPublicKey,
  jint instanceNumber)
{
  return instances.with_instance_noerr (env, instanceNumber,
    get_vector<uint8_t, constant_size<TOX_PUBLIC_KEY_SIZE>::make, tox_self_get_public_key>::make
  );
}

JAVA_METHOD (jbyteArray, toxSelfGetSecretKey,
  jint instanceNumber)
{
  return instances.with_instance_noerr (env, instanceNumber,
    get_vector<uint8_t, constant_size<TOX_SECRET_KEY_SIZE>::make, tox_self_get_secret_key>::make
  );
}

JAVA_METHOD (void, toxSelfSetName,
  jint instanceNumber, jbyteArray name)
{
  auto nameData = fromJavaArray (env, name);
  return instances.with_instance_ign (env, instanceNumber,
    tox_self_set_name, nameData.data (), nameData.size ()
  );
}

JAVA_METHOD (jbyteArray, toxSelfGetName,
  jint instanceNumber)
{
  return instances.with_instance_noerr (env, instanceNumber,
    get_vector<uint8_t,
      tox_self_get_name_size,
      tox_self_get_name>::make
  );
}

JAVA_METHOD (void, toxSelfSetStatusMessage,
  jint instanceNumber, jbyteArray statusMessage)
{
  auto statusMessageData = fromJavaArray (env, statusMessage);
  return instances.with_instance_ign (env, instanceNumber,
    tox_self_set_status_message, statusMessageData.data (), statusMessageData.size ()
  );
}

JAVA_METHOD (jbyteArray, toxSelfGetStatusMessage,
  jint instanceNumber)
{
  return instances.with_instance_noerr (env, instanceNumber,
    get_vector<uint8_t,
      tox_self_get_status_message_size,
      tox_self_get_status_message>::make
  );
}

JAVA_METHOD (void, toxSelfSetStatus,
  jint instanceNumber, jint status)
{
  return instances.with_instance_noerr (env, instanceNumber,
    tox_self_set_status, Enum::valueOf<Tox_User_Status> (env, status)
  );
}

JAVA_METHOD (jint, toxSelfGetStatus,
  jint instanceNumber)
{
  return instances.with_instance_noerr (env, instanceNumber,
    tox_self_get_status
  );
}

JAVA_METHOD (jintArray, toxSelfGetFriendList,
  jint instanceNumber)
{
  return instances.with_instance_noerr (env, instanceNumber,
    get_vector<uint32_t, tox_self_get_friend_list_size, tox_self_get_friend_list, jint>::make
  );
}

JAVA_METHOD (void, toxSelfSetTyping,
  jint instanceNumber, jint friendNumber, jboolean typing)
{
  return instances.with_instance_ign (env, instanceNumber,
    tox_self_set_typing, friendNumber, typing
  );
}

JAVA_METHOD (void, toxFileControl,
  jint instanceNumber, jint friendNumber, jint fileNumber, jint control)
{
  return instances.with_instance_ign (env, instanceNumber,
    tox_file_control, friendNumber, fileNumber, Enum::valueOf<Tox_File_Control> (env, control)
  );
}

JAVA_METHOD (void, toxFileSeek,
  jint instanceNumber, jint friendNumber, jint fileNumber, jlong position)
{
  return instances.with_instance_ign (env, instanceNumber,
    tox_file_seek, friendNumber, fileNumber, position
  );
}

JAVA_METHOD (jbyteArray, toxFileGetFileId,
  jint instanceNumber, jint friendNumber, jint fileNumber)
{
  uint8_t result[TOX_FILE_ID_LENGTH];
  return instances.with_instance_err (env, instanceNumber,
    [&] (bool) { return toJavaArray (env, result); },
    tox_file_get_file_id, friendNumber, fileNumber, result
  );
}

JAVA_METHOD (void, toxFileSendChunk,
  jint instanceNumber, jint friendNumber, jint fileNumber, jlong position, jbyteArray data)
{
  auto dataData = fromJavaArray (env, data);
  return instances.with_instance_ign (env, instanceNumber,
    tox_file_send_chunk, friendNumber, fileNumber, position, dataData.data (), dataData.size ()
  );
}

JAVA_METHOD (void, toxConferenceInvite,
  jint instanceNumber, jint friendNumber, jint conferenceNumber)
{
  return instances.with_instance_ign (env, instanceNumber,
    tox_conference_invite, friendNumber, conferenceNumber
  );
}

JAVA_METHOD (jintArray, toxConferenceGetChatlist,
  jint instanceNumber)
{
  return instances.with_instance_noerr (env, instanceNumber,
    get_vector<uint32_t, tox_conference_get_chatlist_size, tox_conference_get_chatlist, jint>::make
  );
}

JAVA_METHOD (jbyteArray, toxSelfGetDhtId,
  jint instanceNumber)
{
  return instances.with_instance_noerr (env, instanceNumber,
    get_vector<uint8_t, constant_size<TOX_DHT_ID_SIZE>::make, tox_self_get_dht_id>::make
  );
}

JAVA_METHOD (jint, toxSelfGetUdpPort,
  jint instanceNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity, tox_self_get_udp_port
  );
}

JAVA_METHOD (jint, toxSelfGetTcpPort,
  jint instanceNumber)
{
  return instances.with_instance_err (env, instanceNumber,
    identity, tox_self_get_tcp_port
  );
}

JAVA_METHOD (jintArray, toxGroupGetGroupList,
  jint instanceNumber)
{
  return instances.with_instance_noerr (env, instanceNumber,
    get_vector<uint32_t, tox_group_get_group_list_size, tox_group_get_group_list, jint>::make
  );
}

