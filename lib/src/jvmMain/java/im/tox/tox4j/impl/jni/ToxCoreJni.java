package im.tox.tox4j.impl.jni;

import im.tox.tox4j.core.exceptions.ToxBootstrapException;
import im.tox.tox4j.core.exceptions.ToxConferenceByIdException;
import im.tox.tox4j.core.exceptions.ToxConferenceDeleteException;
import im.tox.tox4j.core.exceptions.ToxConferenceGetTypeException;
import im.tox.tox4j.core.exceptions.ToxConferenceInviteException;
import im.tox.tox4j.core.exceptions.ToxConferenceJoinException;
import im.tox.tox4j.core.exceptions.ToxConferenceNewException;
import im.tox.tox4j.core.exceptions.ToxConferencePeerQueryException;
import im.tox.tox4j.core.exceptions.ToxConferenceSendMessageException;
import im.tox.tox4j.core.exceptions.ToxConferenceSetMaxOfflineException;
import im.tox.tox4j.core.exceptions.ToxConferenceTitleException;
import im.tox.tox4j.core.exceptions.ToxFileControlException;
import im.tox.tox4j.core.exceptions.ToxFileGetException;
import im.tox.tox4j.core.exceptions.ToxFileSeekException;
import im.tox.tox4j.core.exceptions.ToxFileSendChunkException;
import im.tox.tox4j.core.exceptions.ToxFileSendException;
import im.tox.tox4j.core.exceptions.ToxFriendAddException;
import im.tox.tox4j.core.exceptions.ToxFriendByPublicKeyException;
import im.tox.tox4j.core.exceptions.ToxFriendCustomPacketException;
import im.tox.tox4j.core.exceptions.ToxFriendDeleteException;
import im.tox.tox4j.core.exceptions.ToxFriendGetPublicKeyException;
import im.tox.tox4j.core.exceptions.ToxFriendSendMessageException;
import im.tox.tox4j.core.exceptions.ToxGetPortException;
import im.tox.tox4j.core.exceptions.ToxGroupDisconnectException;
import im.tox.tox4j.core.exceptions.ToxGroupInviteAcceptException;
import im.tox.tox4j.core.exceptions.ToxGroupInviteFriendException;
import im.tox.tox4j.core.exceptions.ToxGroupIsConnectedException;
import im.tox.tox4j.core.exceptions.ToxGroupJoinException;
import im.tox.tox4j.core.exceptions.ToxGroupKickPeerException;
import im.tox.tox4j.core.exceptions.ToxGroupLeaveException;
import im.tox.tox4j.core.exceptions.ToxGroupNewException;
import im.tox.tox4j.core.exceptions.ToxGroupPeerQueryException;
import im.tox.tox4j.core.exceptions.ToxGroupSelfNameSetException;
import im.tox.tox4j.core.exceptions.ToxGroupSelfQueryException;
import im.tox.tox4j.core.exceptions.ToxGroupSelfStatusSetException;
import im.tox.tox4j.core.exceptions.ToxGroupSendCustomPacketException;
import im.tox.tox4j.core.exceptions.ToxGroupSendCustomPrivatePacketException;
import im.tox.tox4j.core.exceptions.ToxGroupSendMessageException;
import im.tox.tox4j.core.exceptions.ToxGroupSendPrivateMessageException;
import im.tox.tox4j.core.exceptions.ToxGroupSetIgnoreException;
import im.tox.tox4j.core.exceptions.ToxGroupSetPasswordException;
import im.tox.tox4j.core.exceptions.ToxGroupSetPeerLimitException;
import im.tox.tox4j.core.exceptions.ToxGroupSetPrivacyStateException;
import im.tox.tox4j.core.exceptions.ToxGroupSetRoleException;
import im.tox.tox4j.core.exceptions.ToxGroupSetTopicLockException;
import im.tox.tox4j.core.exceptions.ToxGroupSetVoiceStateException;
import im.tox.tox4j.core.exceptions.ToxGroupStateQueryException;
import im.tox.tox4j.core.exceptions.ToxGroupTopicSetException;
import im.tox.tox4j.core.exceptions.ToxNewException;
import im.tox.tox4j.core.exceptions.ToxSetInfoException;
import im.tox.tox4j.core.exceptions.ToxSetTypingException;

@SuppressWarnings({"checkstyle:emptylineseparator", "checkstyle:linelength"})
public final class ToxCoreJni {
    static {
      System.loadLibrary("tox4j-c");
    }

    static native int toxNew(boolean ipv6Enabled, boolean udpEnabled, boolean localDiscoveryEnabled,
        int proxyType, String proxyAddress, int proxyPort, int startPort, int endPort, int tcpPort,
        int saveDataType, byte[] saveData) throws ToxNewException;

    // void tox_kill(Tox *tox);
    static native void toxKill(int instanceNumber);
    static native void toxFinalize(int instanceNumber);
    // void tox_get_savedata(const Tox *tox, uint8_t savedata[]);
    static native byte[] toxGetSavedata(int instanceNumber);
    // bool tox_bootstrap(Tox *tox, const char *host, uint16_t port, const uint8_t public_key[TOX_PUBLIC_KEY_SIZE], Tox_Err_Bootstrap *error);
    static native void toxBootstrap(int instanceNumber, String address, int port, byte[] publicKey)
        throws ToxBootstrapException;
    // bool tox_add_tcp_relay(Tox *tox, const char *host, uint16_t port, const uint8_t public_key[TOX_PUBLIC_KEY_SIZE], Tox_Err_Bootstrap *error);
    static native void toxAddTcpRelay(int instanceNumber, String address, int port, byte[] publicKey)
        throws ToxBootstrapException;
    // uint16_t tox_self_get_udp_port(const Tox *tox, Tox_Err_Get_Port *error);
    static native int toxSelfGetUdpPort(int instanceNumber) throws ToxGetPortException;
    // uint16_t tox_self_get_tcp_port(const Tox *tox, Tox_Err_Get_Port *error);
    static native int toxSelfGetTcpPort(int instanceNumber) throws ToxGetPortException;
    // void tox_self_get_dht_id(const Tox *tox, uint8_t dht_id[TOX_PUBLIC_KEY_SIZE]);
    static native byte[] toxSelfGetDhtId(int instanceNumber);
    // uint32_t tox_iteration_interval(const Tox *tox);
    static native int toxIterationInterval(int instanceNumber);
    // void tox_iterate(Tox *tox, void *user_data);
    static native byte[] toxIterate(int instanceNumber);
    // void tox_self_get_public_key(const Tox *tox, uint8_t public_key[TOX_PUBLIC_KEY_SIZE]);
    static native byte[] toxSelfGetPublicKey(int instanceNumber);
    // void tox_self_get_secret_key(const Tox *tox, uint8_t secret_key[TOX_SECRET_KEY_SIZE]);
    static native byte[] toxSelfGetSecretKey(int instanceNumber);
    // void tox_self_set_nospam(Tox *tox, uint32_t nospam);
    static native void toxSelfSetNospam(int instanceNumber, int nospam);
    // uint32_t tox_self_get_nospam(const Tox *tox);
    static native int toxSelfGetNospam(int instanceNumber);
    // void tox_self_get_address(const Tox *tox, uint8_t address[TOX_ADDRESS_SIZE]);
    static native byte[] toxSelfGetAddress(int instanceNumber);
    // bool tox_self_set_name(Tox *tox, const uint8_t name[], size_t length, Tox_Err_Set_Info *error);
    static native void toxSelfSetName(int instanceNumber, byte[] name) throws ToxSetInfoException;
    // void tox_self_get_name(const Tox *tox, uint8_t name[]);
    static native byte[] toxSelfGetName(int instanceNumber);
    // bool tox_self_set_status_message(
    //    Tox *tox, const uint8_t status_message[], size_t length, Tox_Err_Set_Info *error);
    static native void toxSelfSetStatusMessage(int instanceNumber, byte[] message)
        throws ToxSetInfoException;
    // void tox_self_get_status_message(const Tox *tox, uint8_t status_message[]);
    static native byte[] toxSelfGetStatusMessage(int instanceNumber);
    // void tox_self_set_status(Tox *tox, Tox_User_Status status);
    static native void toxSelfSetStatus(int instanceNumber, int status);
    // Tox_User_Status tox_self_get_status(const Tox *tox);
    static native int toxSelfGetStatus(int instanceNumber);
    // Tox_Friend_Number tox_friend_add(
    //     Tox *tox, const uint8_t address[TOX_ADDRESS_SIZE],
    //     const uint8_t message[], size_t length,
    //     Tox_Err_Friend_Add *error);
    static native int toxFriendAdd(int instanceNumber, byte[] address, byte[] message)
        throws ToxFriendAddException;
    // Tox_Friend_Number tox_friend_add_norequest(
    //     Tox *tox, const uint8_t public_key[TOX_PUBLIC_KEY_SIZE], Tox_Err_Friend_Add *error);
    static native int toxFriendAddNorequest(int instanceNumber, byte[] publicKey)
        throws ToxFriendAddException;
    // bool tox_friend_delete(Tox *tox, Tox_Friend_Number friend_number, Tox_Err_Friend_Delete *error);
    static native void toxFriendDelete(int instanceNumber, int friendNumber)
        throws ToxFriendDeleteException;
    // Tox_Friend_Number tox_friend_by_public_key(const Tox *tox, const uint8_t public_key[TOX_PUBLIC_KEY_SIZE], Tox_Err_Friend_By_Public_Key *error);
    static native int toxFriendByPublicKey(int instanceNumber, byte[] publicKey)
        throws ToxFriendByPublicKeyException;
    // bool tox_friend_get_public_key(
    //     const Tox *tox, Tox_Friend_Number friend_number, uint8_t public_key[TOX_PUBLIC_KEY_SIZE],
    //     Tox_Err_Friend_Get_Public_Key *error);
    static native byte[] toxFriendGetPublicKey(int instanceNumber, int friendNumber)
        throws ToxFriendGetPublicKeyException;
    // bool tox_friend_exists(const Tox *tox, Tox_Friend_Number friend_number);
    static native boolean toxFriendExists(int instanceNumber, int friendNumber);
    // void tox_self_get_friend_list(const Tox *tox, Tox_Friend_Number friend_list[]);
    static native int[] toxSelfGetFriendList(int instanceNumber);
    // bool tox_self_set_typing(
    //     Tox *tox, Tox_Friend_Number friend_number, bool typing, Tox_Err_Set_Typing *error);
    static native void toxSelfSetTyping(int instanceNumber, int friendNumber, boolean typing)
        throws ToxSetTypingException;
    // Tox_Friend_Message_Id tox_friend_send_message(
    //     Tox *tox, Tox_Friend_Number friend_number, Tox_Message_Type type,
    //     const uint8_t message[], size_t length, Tox_Err_Friend_Send_Message *error);
    static native int toxFriendSendMessage(int instanceNumber, int friendNumber, int type,
        byte[] message) throws ToxFriendSendMessageException;
    // bool tox_file_control(
    //     Tox *tox, Tox_Friend_Number friend_number, Tox_File_Number file_number, Tox_File_Control control,
    //     Tox_Err_File_Control *error);
    static native void toxFileControl(int instanceNumber, int friendNumber, int fileNumber,
        int control) throws ToxFileControlException;
    // bool tox_file_seek(
    //     Tox *tox, Tox_Friend_Number friend_number, Tox_File_Number file_number, uint64_t position, Tox_Err_File_Seek *error);
    static native void toxFileSeek(int instanceNumber, int friendNumber, int fileNumber,
        long position) throws ToxFileSeekException;
    // Tox_File_Number tox_file_send(
    //     Tox *tox, Tox_Friend_Number friend_number, uint32_t kind, uint64_t file_size,
    //     const uint8_t file_id[TOX_FILE_ID_LENGTH], const uint8_t filename[], size_t filename_length,
    //     Tox_Err_File_Send *error);
    static native int toxFileSend(int instanceNumber, int friendNumber, int kind, long fileSize,
        byte[] fileId, byte[] filename) throws ToxFileSendException;
    // bool tox_file_send_chunk(
    //     Tox *tox, Tox_Friend_Number friend_number, Tox_File_Number file_number, uint64_t position,
    //     const uint8_t data[], size_t length, Tox_Err_File_Send_Chunk *error);
    static native void toxFileSendChunk(int instanceNumber, int friendNumber, int fileNumber,
        long position, byte[] data) throws ToxFileSendChunkException;
    // bool tox_file_get_file_id(
    //     const Tox *tox, Tox_Friend_Number friend_number, Tox_File_Number file_number,
    //     uint8_t file_id[TOX_FILE_ID_LENGTH],
    //     Tox_Err_File_Get *error);
    static native byte[] toxFileGetFileId(int instanceNumber, int friendNumber, int fileNumber)
        throws ToxFileGetException;
    // bool tox_friend_send_lossy_packet(
    //     Tox *tox, Tox_Friend_Number friend_number,
    //     const uint8_t data[], size_t length,
    //     Tox_Err_Friend_Custom_Packet *error);
    static native void toxFriendSendLossyPacket(int instanceNumber, int friendNumber, byte[] data)
        throws ToxFriendCustomPacketException;
    // bool tox_friend_send_lossless_packet(
    //     Tox *tox, Tox_Friend_Number friend_number,
    //     const uint8_t data[], size_t length,
    //     Tox_Err_Friend_Custom_Packet *error);
    static native void toxFriendSendLosslessPacket(int instanceNumber, int friendNumber, byte[] data)
        throws ToxFriendCustomPacketException;

    // Tox_Conference_Number tox_conference_new(Tox *tox, Tox_Err_Conference_New *error);
    static native int toxConferenceNew(int instanceNumber)
        throws ToxConferenceNewException;
    // bool tox_conference_delete(Tox *tox, Tox_Conference_Number conference_number, Tox_Err_Conference_Delete *error);
    static native void toxConferenceDelete(int instanceNumber, int conferenceNumber)
        throws ToxConferenceDeleteException;
    // uint32_t tox_conference_peer_count(
    //     const Tox *tox, Tox_Conference_Number conference_number, Tox_Err_Conference_Peer_Query *error);
    static native int toxConferencePeerCount(int instanceNumber, int conferenceNumber)
        throws ToxConferencePeerQueryException;
    // bool tox_conference_peer_get_name(
    //     const Tox *tox, Tox_Conference_Number conference_number, Tox_Conference_Peer_Number peer_number,
    //     uint8_t name[], Tox_Err_Conference_Peer_Query *error);
    static native byte[] toxConferencePeerGetName(int instanceNumber, int conferenceNumber,
        int peerNumber) throws ToxConferencePeerQueryException;
    // bool tox_conference_peer_get_public_key(
    //     const Tox *tox, Tox_Conference_Number conference_number, Tox_Conference_Peer_Number peer_number,
    //     uint8_t public_key[TOX_PUBLIC_KEY_SIZE], Tox_Err_Conference_Peer_Query *error);
    static native byte[] toxConferencePeerGetPublicKey(int instanceNumber, int conferenceNumber,
        int peerNumber) throws ToxConferencePeerQueryException;
    // bool tox_conference_peer_number_is_ours(
    //     const Tox *tox, Tox_Conference_Number conference_number, Tox_Conference_Peer_Number peer_number,
    //     Tox_Err_Conference_Peer_Query *error);
    static native boolean toxConferencePeerNumberIsOurs(int instanceNumber, int conferenceNumber,
        int peerNumber) throws ToxConferencePeerQueryException;
    // uint32_t tox_conference_offline_peer_count(
    //     const Tox *tox, Tox_Conference_Number conference_number,
    //     Tox_Err_Conference_Peer_Query *error);
    static native int toxConferenceOfflinePeerCount(int instanceNumber, int conferenceNumber)
        throws ToxConferencePeerQueryException;
    // bool tox_conference_offline_peer_get_name(
    //     const Tox *tox, Tox_Conference_Number conference_number, Tox_Conference_Offline_Peer_Number offline_peer_number,
    //     uint8_t name[], Tox_Err_Conference_Peer_Query *error);
    static native byte[] toxConferenceOfflinePeerGetName(int instanceNumber, int conferenceNumber,
        int offlinePeerNumber) throws ToxConferencePeerQueryException;
    // bool tox_conference_offline_peer_get_public_key(
    //     const Tox *tox, Tox_Conference_Number conference_number,
    //     Tox_Conference_Offline_Peer_Number offline_peer_number, uint8_t public_key[TOX_PUBLIC_KEY_SIZE], Tox_Err_Conference_Peer_Query *error);
    static native byte[] toxConferenceOfflinePeerGetPublicKey(int instanceNumber, int conferenceNumber,
        int offlinePeerNumber) throws ToxConferencePeerQueryException;
    // uint64_t tox_conference_offline_peer_get_last_active(
    //     const Tox *tox, Tox_Conference_Number conference_number,
    //     Tox_Conference_Offline_Peer_Number offline_peer_number, Tox_Err_Conference_Peer_Query *error);
    static native long toxConferenceOfflinePeerGetLastActive(int instanceNumber, int conferenceNumber,
        int offlinePeerNumber) throws ToxConferencePeerQueryException;
    // bool tox_conference_set_max_offline(
    //     Tox *tox, Tox_Conference_Number conference_number, uint32_t max_offline,
    //     Tox_Err_Conference_Set_Max_Offline *error);
    static native void toxConferenceSetMaxOffline(int instanceNumber, int conferenceNumber,
        int maxOffline) throws ToxConferenceSetMaxOfflineException;
    // bool tox_conference_invite(
    //     Tox *tox, Tox_Friend_Number friend_number, Tox_Conference_Number conference_number,
    //     Tox_Err_Conference_Invite *error);
    static native void toxConferenceInvite(int instanceNumber, int friendNumber, int conferenceNumber)
        throws ToxConferenceInviteException;
    // Tox_Conference_Number tox_conference_join(
    //     Tox *tox, Tox_Friend_Number friend_number,
    //     const uint8_t cookie[], size_t length,
    //     Tox_Err_Conference_Join *error);
    static native int toxConferenceJoin(int instanceNumber, int friendNumber, byte[] cookie)
        throws ToxConferenceJoinException;
    // bool tox_conference_send_message(
    //     Tox *tox, Tox_Conference_Number conference_number, Tox_Message_Type type,
    //     const uint8_t message[], size_t length,
    //     Tox_Err_Conference_Send_Message *error);
    static native void toxConferenceSendMessage(int instanceNumber, int conferenceNumber, int type,
        byte[] message) throws ToxConferenceSendMessageException;
    // bool tox_conference_get_title(
    //     const Tox *tox, Tox_Conference_Number conference_number,
    //     uint8_t title[],
    //     Tox_Err_Conference_Title *error);
    static native byte[] toxConferenceGetTitle(int instanceNumber, int conferenceNumber)
        throws ToxConferenceTitleException;
    // bool tox_conference_set_title(
    //     Tox *tox, Tox_Conference_Number conference_number,
    //     const uint8_t title[], size_t length,
    //     Tox_Err_Conference_Title *error);
    static native void toxConferenceSetTitle(int instanceNumber, int conferenceNumber, byte[] title)
        throws ToxConferenceTitleException;
    // void tox_conference_get_chatlist(const Tox *tox, Tox_Conference_Number chatlist[]);
    static native int[] toxConferenceGetChatlist(int instanceNumber);
    // Tox_Conference_Type tox_conference_get_type(
    //     const Tox *tox, Tox_Conference_Number conference_number,
    //     Tox_Err_Conference_Get_Type *error);
    static native int toxConferenceGetType(int instanceNumber, int conferenceNumber)
        throws ToxConferenceGetTypeException;
    // bool tox_conference_get_id(
    //     const Tox *tox, Tox_Conference_Number conference_number, uint8_t id[TOX_CONFERENCE_ID_SIZE]);
    static native byte[] toxConferenceGetId(int instanceNumber, int conferenceNumber);
    // Tox_Conference_Number tox_conference_by_id(
    //     const Tox *tox, const uint8_t id[TOX_CONFERENCE_ID_SIZE], Tox_Err_Conference_By_Id *error);
    static native int toxConferenceById(int instanceNumber, byte[] id) throws ToxConferenceByIdException;

    // Tox_Group_Number tox_group_new(
    //     Tox *tox, Tox_Group_Privacy_State privacy_state,
    //     const uint8_t group_name[], size_t group_name_length,
    //     const uint8_t name[], size_t name_length, Tox_Err_Group_New *error);
    static native int toxGroupNew(int instanceNumber, int privacyState, byte[] groupName, byte[] name)
        throws ToxGroupNewException;
    // Tox_Group_Number tox_group_join(
    //     Tox *tox, const uint8_t chat_id[TOX_GROUP_CHAT_ID_SIZE],
    //     const uint8_t name[], size_t name_length,
    //     const uint8_t password[], size_t password_length,
    //     Tox_Err_Group_Join *error);
    static native int toxGroupJoin(int instanceNumber, byte[] chatId, byte[] name, byte[] password)
        throws ToxGroupJoinException;
    // bool tox_group_is_connected(const Tox *tox, Tox_Group_Number group_number, Tox_Err_Group_Is_Connected *error);
    static native boolean toxGroupIsConnected(int instanceNumber, int groupNumber)
        throws ToxGroupIsConnectedException;
    // bool tox_group_disconnect(const Tox *tox, Tox_Group_Number group_number, Tox_Err_Group_Disconnect *error);
    static native void toxGroupDisconnect(int instanceNumber, int groupNumber)
        throws ToxGroupDisconnectException;
    // bool tox_group_leave(
    //     Tox *tox, Tox_Group_Number group_number,
    //     const uint8_t part_message[], size_t length,
    //     Tox_Err_Group_Leave *error);
    static native void toxGroupLeave(int instanceNumber, int groupNumber, byte[] partMessage)
        throws ToxGroupLeaveException;
    // bool tox_group_self_set_name(
    //     Tox *tox, Tox_Group_Number group_number,
    //     const uint8_t name[], size_t length,
    //     Tox_Err_Group_Self_Name_Set *error);
    static native void toxGroupSelfSetName(int instanceNumber, int groupNumber, byte[] name)
        throws ToxGroupSelfNameSetException;
    // bool tox_group_self_get_name(
    //     const Tox *tox, Tox_Group_Number group_number,
    //     uint8_t name[], Tox_Err_Group_Self_Query *error);
    static native byte[] toxGroupSelfGetName(int instanceNumber, int groupNumber)
        throws ToxGroupSelfQueryException;
    // bool tox_group_self_set_status(Tox *tox, Tox_Group_Number group_number, Tox_User_Status status,
    //                                Tox_Err_Group_Self_Status_Set *error);
    static native void toxGroupSelfSetStatus(int instanceNumber, int groupNumber, int status)
        throws ToxGroupSelfStatusSetException;
    // Tox_User_Status tox_group_self_get_status(const Tox *tox, Tox_Group_Number group_number, Tox_Err_Group_Self_Query *error);
    static native int toxGroupSelfGetStatus(int instanceNumber, int groupNumber)
        throws ToxGroupSelfQueryException;
    // Tox_Group_Role tox_group_self_get_role(const Tox *tox, Tox_Group_Number group_number, Tox_Err_Group_Self_Query *error);
    static native int toxGroupSelfGetRole(int instanceNumber, int groupNumber)
        throws ToxGroupSelfQueryException;
    // Tox_Group_Peer_Number tox_group_self_get_peer_id(const Tox *tox, Tox_Group_Number group_number, Tox_Err_Group_Self_Query *error);
    static native int toxGroupSelfGetPeerId(int instanceNumber, int groupNumber)
        throws ToxGroupSelfQueryException;
    // bool tox_group_self_get_public_key(const Tox *tox, Tox_Group_Number group_number, uint8_t public_key[TOX_PUBLIC_KEY_SIZE],
    //                                    Tox_Err_Group_Self_Query *error);
    static native byte[] toxGroupSelfGetPublicKey(int instanceNumber, int groupNumber)
        throws ToxGroupSelfQueryException;
    // bool tox_group_peer_get_name(
    //     const Tox *tox, Tox_Group_Number group_number, Tox_Group_Peer_Number peer_id,
    //     uint8_t name[], Tox_Err_Group_Peer_Query *error);
    static native byte[] toxGroupPeerGetName(int instanceNumber, int groupNumber, int peerId)
        throws ToxGroupPeerQueryException;
    // Tox_User_Status tox_group_peer_get_status(const Tox *tox, Tox_Group_Number group_number, Tox_Group_Peer_Number peer_id,
    //         Tox_Err_Group_Peer_Query *error);
    static native int toxGroupPeerGetStatus(int instanceNumber, int groupNumber, int peerId)
        throws ToxGroupPeerQueryException;
    // Tox_Group_Role tox_group_peer_get_role(const Tox *tox, Tox_Group_Number group_number, Tox_Group_Peer_Number peer_id,
    //                                        Tox_Err_Group_Peer_Query *error);
    static native int toxGroupPeerGetRole(int instanceNumber, int groupNumber, int peerId)
        throws ToxGroupPeerQueryException;
    // Tox_Connection tox_group_peer_get_connection_status(const Tox *tox, Tox_Group_Number group_number, Tox_Group_Peer_Number peer_id,
    //         Tox_Err_Group_Peer_Query *error);
    static native int toxGroupPeerGetConnectionStatus(int instanceNumber, int groupNumber, int peerId)
        throws ToxGroupPeerQueryException;
    // bool tox_group_peer_get_public_key(
    //     const Tox *tox, Tox_Group_Number group_number, Tox_Group_Peer_Number peer_id,
    //     uint8_t public_key[TOX_PUBLIC_KEY_SIZE], Tox_Err_Group_Peer_Query *error);
    static native byte[] toxGroupPeerGetPublicKey(int instanceNumber, int groupNumber, int peerId)
        throws ToxGroupPeerQueryException;
    // bool tox_group_set_topic(
    //     Tox *tox, Tox_Group_Number group_number,
    //     const uint8_t topic[], size_t length,
    //     Tox_Err_Group_Topic_Set *error);
    static native void toxGroupSetTopic(int instanceNumber, int groupNumber, byte[] topic)
        throws ToxGroupTopicSetException;
    // bool tox_group_get_topic(
    //     const Tox *tox, Tox_Group_Number group_number,
    //     uint8_t topic[], Tox_Err_Group_State_Query *error);
    static native byte[] toxGroupGetTopic(int instanceNumber, int groupNumber)
        throws ToxGroupStateQueryException;
    // bool tox_group_get_name(
    //     const Tox *tox, Tox_Group_Number group_number,
    //     uint8_t name[], Tox_Err_Group_State_Query *error);
    static native byte[] toxGroupGetName(int instanceNumber, int groupNumber)
        throws ToxGroupStateQueryException;
    // bool tox_group_get_chat_id(
    //     const Tox *tox, Tox_Group_Number group_number, uint8_t chat_id[TOX_GROUP_CHAT_ID_SIZE],
    //     Tox_Err_Group_State_Query *error);
    static native byte[] toxGroupGetChatId(int instanceNumber, int groupNumber)
        throws ToxGroupStateQueryException;
    // Tox_Group_Privacy_State tox_group_get_privacy_state(const Tox *tox, Tox_Group_Number group_number,
    //         Tox_Err_Group_State_Query *error);
    static native int toxGroupGetPrivacyState(int instanceNumber, int groupNumber)
        throws ToxGroupStateQueryException;
    // Tox_Group_Voice_State tox_group_get_voice_state(const Tox *tox, Tox_Group_Number group_number,
    //         Tox_Err_Group_State_Query *error);
    static native int toxGroupGetVoiceState(int instanceNumber, int groupNumber)
        throws ToxGroupStateQueryException;
    // Tox_Group_Topic_Lock tox_group_get_topic_lock(const Tox *tox, Tox_Group_Number group_number,
    //         Tox_Err_Group_State_Query *error);
    static native int toxGroupGetTopicLock(int instanceNumber, int groupNumber)
        throws ToxGroupStateQueryException;
    // uint16_t tox_group_get_peer_limit(const Tox *tox, Tox_Group_Number group_number, Tox_Err_Group_State_Query *error);
    static native int toxGroupGetPeerLimit(int instanceNumber, int groupNumber)
        throws ToxGroupStateQueryException;
    // bool tox_group_get_password(
    //     const Tox *tox, Tox_Group_Number group_number, uint8_t password[],
    //     Tox_Err_Group_State_Query *error);
    static native byte[] toxGroupGetPassword(int instanceNumber, int groupNumber)
        throws ToxGroupStateQueryException;
    // Tox_Group_Message_Id tox_group_send_message(
    //     const Tox *tox, Tox_Group_Number group_number, Tox_Message_Type message_type,
    //     const uint8_t message[], size_t length,
    //     Tox_Err_Group_Send_Message *error);
    static native int toxGroupSendMessage(int instanceNumber, int groupNumber, int type,
        byte[] message) throws ToxGroupSendMessageException;
    // Tox_Group_Message_Id tox_group_send_private_message(
    //     const Tox *tox, Tox_Group_Number group_number, Tox_Group_Peer_Number peer_id, Tox_Message_Type message_type,
    //     const uint8_t message[], size_t length,
    //     Tox_Err_Group_Send_Private_Message *error);
    static native int toxGroupSendPrivateMessage(int instanceNumber, int groupNumber, int peerId,
        int type, byte[] message) throws ToxGroupSendPrivateMessageException;
    // bool tox_group_send_custom_packet(
    //     const Tox *tox, Tox_Group_Number group_number, bool lossless,
    //     const uint8_t data[], size_t length,
    //     Tox_Err_Group_Send_Custom_Packet *error);
    static native void toxGroupSendCustomPacket(int instanceNumber, int groupNumber, boolean lossless,
        byte[] data) throws ToxGroupSendCustomPacketException;
    // bool tox_group_send_custom_private_packet(const Tox *tox, Tox_Group_Number group_number, Tox_Group_Peer_Number peer_id, bool lossless,
    //         const uint8_t data[], size_t length,
    //         Tox_Err_Group_Send_Custom_Private_Packet *error);
    static native void toxGroupSendCustomPrivatePacket(int instanceNumber, int groupNumber, int peerId,
        boolean lossless, byte[] data) throws ToxGroupSendCustomPrivatePacketException;
    // bool tox_group_invite_friend(
    //     const Tox *tox, Tox_Group_Number group_number, Tox_Friend_Number friend_number,
    //     Tox_Err_Group_Invite_Friend *error);
    static native void toxGroupInviteFriend(int instanceNumber, int groupNumber, int friendNumber)
        throws ToxGroupInviteFriendException;
    // Tox_Group_Number tox_group_invite_accept(
    //     Tox *tox, Tox_Friend_Number friend_number,
    //     const uint8_t invite_data[], size_t length,
    //     const uint8_t name[], size_t name_length,
    //     const uint8_t password[], size_t password_length,
    //     Tox_Err_Group_Invite_Accept *error);
    static native int toxGroupInviteAccept(int instanceNumber, int friendNumber, byte[] inviteData,
        byte[] name, byte[] password) throws ToxGroupInviteAcceptException;
    // bool tox_group_set_password(
    //     Tox *tox, Tox_Group_Number group_number,
    //     const uint8_t password[], size_t length,
    //     Tox_Err_Group_Set_Password *error);
    static native void toxGroupSetPassword(int instanceNumber, int groupNumber, byte[] password)
        throws ToxGroupSetPasswordException;
    // bool tox_group_set_topic_lock(Tox *tox, Tox_Group_Number group_number, Tox_Group_Topic_Lock topic_lock,
    //                               Tox_Err_Group_Set_Topic_Lock *error);
    static native void toxGroupSetTopicLock(int instanceNumber, int groupNumber, int topicLock)
        throws ToxGroupSetTopicLockException;
    // bool tox_group_set_voice_state(Tox *tox, Tox_Group_Number group_number, Tox_Group_Voice_State voice_state,
    //                                Tox_Err_Group_Set_Voice_State *error);
    static native void toxGroupSetVoiceState(int instanceNumber, int groupNumber, int voiceState)
        throws ToxGroupSetVoiceStateException;
    // bool tox_group_set_privacy_state(Tox *tox, Tox_Group_Number group_number, Tox_Group_Privacy_State privacy_state,
    //                                  Tox_Err_Group_Set_Privacy_State *error);
    static native void toxGroupSetPrivacyState(int instanceNumber, int groupNumber, int privacyState)
        throws ToxGroupSetPrivacyStateException;
    // bool tox_group_set_peer_limit(Tox *tox, Tox_Group_Number group_number, uint16_t peer_limit,
    //                               Tox_Err_Group_Set_Peer_Limit *error);
    static native void toxGroupSetPeerLimit(int instanceNumber, int groupNumber, int peerLimit)
        throws ToxGroupSetPeerLimitException;
    // bool tox_group_set_ignore(Tox *tox, Tox_Group_Number group_number, Tox_Group_Peer_Number peer_id, bool ignore,
    //                           Tox_Err_Group_Set_Ignore *error);
    static native void toxGroupSetIgnore(int instanceNumber, int groupNumber, int peerId, boolean ignore)
        throws ToxGroupSetIgnoreException;
    // bool tox_group_set_role(Tox *tox, Tox_Group_Number group_number, Tox_Group_Peer_Number peer_id, Tox_Group_Role role,
    //                         Tox_Err_Group_Set_Role *error);
    static native void toxGroupSetRole(int instanceNumber, int groupNumber, int peerId, int role)
        throws ToxGroupSetRoleException;
    // bool tox_group_kick_peer(const Tox *tox, Tox_Group_Number group_number, Tox_Group_Peer_Number peer_id,
    //                          Tox_Err_Group_Kick_Peer *error);
    static native void toxGroupKickPeer(int instanceNumber, int groupNumber, int peerId)
        throws ToxGroupKickPeerException;
}
