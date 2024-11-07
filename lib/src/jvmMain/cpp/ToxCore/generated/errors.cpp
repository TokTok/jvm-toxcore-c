#include "../ToxCore.h"

HANDLE ("Bootstrap", Bootstrap)
{
  switch (error)
    {
    success_case (BOOTSTRAP);
    failure_case (BOOTSTRAP, BAD_HOST);
    failure_case (BOOTSTRAP, BAD_PORT);
    failure_case (BOOTSTRAP, NULL);
    }
  return unhandled ();
}

HANDLE ("FileControl", File_Control)
{
  switch (error)
    {
    success_case (FILE_CONTROL);
    failure_case (FILE_CONTROL, ALREADY_PAUSED);
    failure_case (FILE_CONTROL, DENIED);
    failure_case (FILE_CONTROL, FRIEND_NOT_CONNECTED);
    failure_case (FILE_CONTROL, FRIEND_NOT_FOUND);
    failure_case (FILE_CONTROL, NOT_FOUND);
    failure_case (FILE_CONTROL, NOT_PAUSED);
    failure_case (FILE_CONTROL, SENDQ);
    }
  return unhandled ();
}

HANDLE ("FileGet", File_Get)
{
  switch (error)
    {
    success_case (FILE_GET);
    failure_case (FILE_GET, FRIEND_NOT_FOUND);
    failure_case (FILE_GET, NOT_FOUND);
    failure_case (FILE_GET, NULL);
    }
  return unhandled ();
}

HANDLE ("FileSeek", File_Seek)
{
  switch (error)
    {
    success_case (FILE_SEEK);
    failure_case (FILE_SEEK, DENIED);
    failure_case (FILE_SEEK, FRIEND_NOT_CONNECTED);
    failure_case (FILE_SEEK, FRIEND_NOT_FOUND);
    failure_case (FILE_SEEK, INVALID_POSITION);
    failure_case (FILE_SEEK, NOT_FOUND);
    failure_case (FILE_SEEK, SENDQ);
    }
  return unhandled ();
}

HANDLE ("FileSendChunk", File_Send_Chunk)
{
  switch (error)
    {
    success_case (FILE_SEND_CHUNK);
    failure_case (FILE_SEND_CHUNK, FRIEND_NOT_CONNECTED);
    failure_case (FILE_SEND_CHUNK, FRIEND_NOT_FOUND);
    failure_case (FILE_SEND_CHUNK, INVALID_LENGTH);
    failure_case (FILE_SEND_CHUNK, NOT_FOUND);
    failure_case (FILE_SEND_CHUNK, NOT_TRANSFERRING);
    failure_case (FILE_SEND_CHUNK, NULL);
    failure_case (FILE_SEND_CHUNK, SENDQ);
    failure_case (FILE_SEND_CHUNK, WRONG_POSITION);
    }
  return unhandled ();
}

HANDLE ("FileSend", File_Send)
{
  switch (error)
    {
    success_case (FILE_SEND);
    failure_case (FILE_SEND, FRIEND_NOT_CONNECTED);
    failure_case (FILE_SEND, FRIEND_NOT_FOUND);
    failure_case (FILE_SEND, NAME_TOO_LONG);
    failure_case (FILE_SEND, NULL);
    failure_case (FILE_SEND, TOO_MANY);
    }
  return unhandled ();
}

HANDLE ("FriendAdd", Friend_Add)
{
  switch (error)
    {
    success_case (FRIEND_ADD);
    failure_case (FRIEND_ADD, ALREADY_SENT);
    failure_case (FRIEND_ADD, BAD_CHECKSUM);
    failure_case (FRIEND_ADD, MALLOC);
    failure_case (FRIEND_ADD, NO_MESSAGE);
    failure_case (FRIEND_ADD, NULL);
    failure_case (FRIEND_ADD, OWN_KEY);
    failure_case (FRIEND_ADD, SET_NEW_NOSPAM);
    failure_case (FRIEND_ADD, TOO_LONG);
    }
  return unhandled ();
}

HANDLE ("FriendByPublicKey", Friend_By_Public_Key)
{
  switch (error)
    {
    success_case (FRIEND_BY_PUBLIC_KEY);
    failure_case (FRIEND_BY_PUBLIC_KEY, NOT_FOUND);
    failure_case (FRIEND_BY_PUBLIC_KEY, NULL);
    }
  return unhandled ();
}

HANDLE ("FriendCustomPacket", Friend_Custom_Packet)
{
  switch (error)
    {
    success_case (FRIEND_CUSTOM_PACKET);
    failure_case (FRIEND_CUSTOM_PACKET, EMPTY);
    failure_case (FRIEND_CUSTOM_PACKET, FRIEND_NOT_CONNECTED);
    failure_case (FRIEND_CUSTOM_PACKET, FRIEND_NOT_FOUND);
    failure_case (FRIEND_CUSTOM_PACKET, INVALID);
    failure_case (FRIEND_CUSTOM_PACKET, NULL);
    failure_case (FRIEND_CUSTOM_PACKET, SENDQ);
    failure_case (FRIEND_CUSTOM_PACKET, TOO_LONG);
    }
  return unhandled ();
}

HANDLE ("FriendDelete", Friend_Delete)
{
  switch (error)
    {
    success_case (FRIEND_DELETE);
    failure_case (FRIEND_DELETE, FRIEND_NOT_FOUND);
    }
  return unhandled ();
}

HANDLE ("FriendGetPublicKey", Friend_Get_Public_Key)
{
  switch (error)
    {
    success_case (FRIEND_GET_PUBLIC_KEY);
    failure_case (FRIEND_GET_PUBLIC_KEY, FRIEND_NOT_FOUND);
    }
  return unhandled ();
}

HANDLE ("FriendSendMessage", Friend_Send_Message)
{
  switch (error)
    {
    success_case (FRIEND_SEND_MESSAGE);
    failure_case (FRIEND_SEND_MESSAGE, EMPTY);
    failure_case (FRIEND_SEND_MESSAGE, FRIEND_NOT_CONNECTED);
    failure_case (FRIEND_SEND_MESSAGE, FRIEND_NOT_FOUND);
    failure_case (FRIEND_SEND_MESSAGE, NULL);
    failure_case (FRIEND_SEND_MESSAGE, SENDQ);
    failure_case (FRIEND_SEND_MESSAGE, TOO_LONG);
    }
  return unhandled ();
}

HANDLE ("GetPort", Get_Port)
{
  switch (error)
    {
    success_case (GET_PORT);
    failure_case (GET_PORT, NOT_BOUND);
    }
  return unhandled ();
}

HANDLE ("New", New)
{
  switch (error)
    {
    success_case (NEW);
    failure_case (NEW, LOAD_BAD_FORMAT);
    failure_case (NEW, LOAD_ENCRYPTED);
    failure_case (NEW, MALLOC);
    failure_case (NEW, NULL);
    failure_case (NEW, PORT_ALLOC);
    failure_case (NEW, PROXY_BAD_HOST);
    failure_case (NEW, PROXY_BAD_PORT);
    failure_case (NEW, PROXY_BAD_TYPE);
    failure_case (NEW, PROXY_NOT_FOUND);
    }
  return unhandled ();
}

HANDLE ("SetInfo", Set_Info)
{
  switch (error)
    {
    success_case (SET_INFO);
    failure_case (SET_INFO, NULL);
    failure_case (SET_INFO, TOO_LONG);
    }
  return unhandled ();
}

HANDLE ("SetTyping", Set_Typing)
{
  switch (error)
    {
    success_case (SET_TYPING);
    failure_case (SET_TYPING, FRIEND_NOT_FOUND);
    }
  return unhandled ();
}

HANDLE ("ConferenceNew", Conference_New)
{
  switch (error)
    {
    success_case (CONFERENCE_NEW);
    failure_case (CONFERENCE_NEW, INIT);
    }
  return unhandled ();
}

HANDLE ("ConferenceDelete", Conference_Delete)
{
  switch (error)
    {
    success_case (CONFERENCE_DELETE);
    failure_case (CONFERENCE_DELETE, CONFERENCE_NOT_FOUND);
    }
  return unhandled ();
}

HANDLE ("ConferencePeerQuery", Conference_Peer_Query)
{
  switch (error)
    {
    success_case (CONFERENCE_PEER_QUERY);
    failure_case (CONFERENCE_PEER_QUERY, CONFERENCE_NOT_FOUND);
    failure_case (CONFERENCE_PEER_QUERY, NO_CONNECTION);
    failure_case (CONFERENCE_PEER_QUERY, PEER_NOT_FOUND);
    }
  return unhandled ();
}

HANDLE ("ConferenceSetMaxOffline", Conference_Set_Max_Offline)
{
  switch (error)
    {
    success_case (CONFERENCE_SET_MAX_OFFLINE);
    failure_case (CONFERENCE_SET_MAX_OFFLINE, CONFERENCE_NOT_FOUND);
    }
  return unhandled ();
}

HANDLE ("ConferenceInvite", Conference_Invite)
{
  switch (error)
    {
    success_case (CONFERENCE_INVITE);
    failure_case (CONFERENCE_INVITE, CONFERENCE_NOT_FOUND);
    failure_case (CONFERENCE_INVITE, FAIL_SEND);
    failure_case (CONFERENCE_INVITE, NO_CONNECTION);
    }
  return unhandled ();
}

HANDLE ("ConferenceJoin", Conference_Join)
{
  switch (error)
    {
    success_case (CONFERENCE_JOIN);
    failure_case (CONFERENCE_JOIN, DUPLICATE);
    failure_case (CONFERENCE_JOIN, FAIL_SEND);
    failure_case (CONFERENCE_JOIN, FRIEND_NOT_FOUND);
    failure_case (CONFERENCE_JOIN, INIT_FAIL);
    failure_case (CONFERENCE_JOIN, INVALID_LENGTH);
    failure_case (CONFERENCE_JOIN, WRONG_TYPE);
    }
  return unhandled ();
}

HANDLE ("ConferenceSendMessage", Conference_Send_Message)
{
  switch (error)
    {
    success_case (CONFERENCE_SEND_MESSAGE);
    failure_case (CONFERENCE_SEND_MESSAGE, CONFERENCE_NOT_FOUND);
    failure_case (CONFERENCE_SEND_MESSAGE, FAIL_SEND);
    failure_case (CONFERENCE_SEND_MESSAGE, NO_CONNECTION);
    failure_case (CONFERENCE_SEND_MESSAGE, TOO_LONG);
    }
  return unhandled ();
}

HANDLE ("ConferenceTitle", Conference_Title)
{
  switch (error)
    {
    success_case (CONFERENCE_TITLE);
    failure_case (CONFERENCE_TITLE, CONFERENCE_NOT_FOUND);
    failure_case (CONFERENCE_TITLE, FAIL_SEND);
    failure_case (CONFERENCE_TITLE, INVALID_LENGTH);
    }
  return unhandled ();
}

HANDLE ("ConferenceGetType", Conference_Get_Type)
{
  switch (error)
    {
    success_case (CONFERENCE_GET_TYPE);
    failure_case (CONFERENCE_GET_TYPE, CONFERENCE_NOT_FOUND);
    }
  return unhandled ();
}

HANDLE ("ConferenceById", Conference_By_Id)
{
  switch (error)
    {
    success_case (CONFERENCE_BY_ID);
    failure_case (CONFERENCE_BY_ID, NULL);
    failure_case (CONFERENCE_BY_ID, NOT_FOUND);
    }
  return unhandled ();
}

HANDLE ("ConferenceByUid", Conference_By_Uid)
{
  switch (error)
    {
    success_case (CONFERENCE_BY_UID);
    failure_case (CONFERENCE_BY_UID, NULL);
    failure_case (CONFERENCE_BY_UID, NOT_FOUND);
    }
  return unhandled ();
}

HANDLE ("GroupNew", Group_New)
{
  switch (error)
    {
    success_case (GROUP_NEW);
    failure_case (GROUP_NEW, ANNOUNCE);
    failure_case (GROUP_NEW, EMPTY);
    failure_case (GROUP_NEW, INIT);
    failure_case (GROUP_NEW, STATE);
    failure_case (GROUP_NEW, TOO_LONG);
    }
  return unhandled ();
}

HANDLE ("GroupJoin", Group_Join)
{
  switch (error)
    {
    success_case (GROUP_JOIN);
    failure_case (GROUP_JOIN, BAD_CHAT_ID);
    failure_case (GROUP_JOIN, CORE);
    failure_case (GROUP_JOIN, EMPTY);
    failure_case (GROUP_JOIN, INIT);
    failure_case (GROUP_JOIN, PASSWORD);
    failure_case (GROUP_JOIN, TOO_LONG);
    }
  return unhandled ();
}

HANDLE ("GroupIsConnected", Group_Is_Connected)
{
  switch (error)
    {
    success_case (GROUP_IS_CONNECTED);
    failure_case (GROUP_IS_CONNECTED, GROUP_NOT_FOUND);
    }
  return unhandled ();
}

HANDLE ("GroupDisconnect", Group_Disconnect)
{
  switch (error)
    {
    success_case (GROUP_DISCONNECT);
    failure_case (GROUP_DISCONNECT, ALREADY_DISCONNECTED);
    failure_case (GROUP_DISCONNECT, GROUP_NOT_FOUND);
    }
  return unhandled ();
}

HANDLE ("GroupReconnect", Group_Reconnect)
{
  switch (error)
    {
    success_case (GROUP_RECONNECT);
    failure_case (GROUP_RECONNECT, CORE);
    failure_case (GROUP_RECONNECT, GROUP_NOT_FOUND);
    }
  return unhandled ();
}

HANDLE ("GroupLeave", Group_Leave)
{
  switch (error)
    {
    success_case (GROUP_LEAVE);
    failure_case (GROUP_LEAVE, FAIL_SEND);
    failure_case (GROUP_LEAVE, GROUP_NOT_FOUND);
    failure_case (GROUP_LEAVE, TOO_LONG);
    }
  return unhandled ();
}

HANDLE ("GroupSelfQuery", Group_Self_Query)
{
  switch (error)
    {
    success_case (GROUP_SELF_QUERY);
    failure_case (GROUP_SELF_QUERY, GROUP_NOT_FOUND);
    }
  return unhandled ();
}

HANDLE ("GroupSelfNameSet", Group_Self_Name_Set)
{
  switch (error)
    {
    success_case (GROUP_SELF_NAME_SET);
    failure_case (GROUP_SELF_NAME_SET, FAIL_SEND);
    failure_case (GROUP_SELF_NAME_SET, GROUP_NOT_FOUND);
    failure_case (GROUP_SELF_NAME_SET, INVALID);
    failure_case (GROUP_SELF_NAME_SET, TOO_LONG);
    }
  return unhandled ();
}

HANDLE ("GroupSelfStatusSet", Group_Self_Status_Set)
{
  switch (error)
    {
    success_case (GROUP_SELF_STATUS_SET);
    failure_case (GROUP_SELF_STATUS_SET, FAIL_SEND);
    failure_case (GROUP_SELF_STATUS_SET, GROUP_NOT_FOUND);
    }
  return unhandled ();
}

HANDLE ("GroupPeerQuery", Group_Peer_Query)
{
  switch (error)
    {
    success_case (GROUP_PEER_QUERY);
    failure_case (GROUP_PEER_QUERY, GROUP_NOT_FOUND);
    failure_case (GROUP_PEER_QUERY, PEER_NOT_FOUND);
    }
  return unhandled ();
}

HANDLE ("GroupStateQuery", Group_State_Query)
{
  switch (error)
    {
    success_case (GROUP_STATE_QUERY);
    failure_case (GROUP_STATE_QUERY, GROUP_NOT_FOUND);
    }
  return unhandled ();
}

HANDLE ("GroupTopicSet", Group_Topic_Set)
{
  switch (error)
    {
    success_case (GROUP_TOPIC_SET);
    failure_case (GROUP_TOPIC_SET, DISCONNECTED);
    failure_case (GROUP_TOPIC_SET, FAIL_CREATE);
    failure_case (GROUP_TOPIC_SET, FAIL_SEND);
    failure_case (GROUP_TOPIC_SET, GROUP_NOT_FOUND);
    failure_case (GROUP_TOPIC_SET, PERMISSIONS);
    failure_case (GROUP_TOPIC_SET, TOO_LONG);
    }
  return unhandled ();
}

HANDLE ("GroupSendMessage", Group_Send_Message)
{
  switch (error)
    {
    success_case (GROUP_SEND_MESSAGE);
    failure_case (GROUP_SEND_MESSAGE, BAD_TYPE);
    failure_case (GROUP_SEND_MESSAGE, DISCONNECTED);
    failure_case (GROUP_SEND_MESSAGE, EMPTY);
    failure_case (GROUP_SEND_MESSAGE, FAIL_SEND);
    failure_case (GROUP_SEND_MESSAGE, GROUP_NOT_FOUND);
    failure_case (GROUP_SEND_MESSAGE, PERMISSIONS);
    failure_case (GROUP_SEND_MESSAGE, TOO_LONG);
    }
  return unhandled ();
}

HANDLE ("GroupSendPrivateMessage", Group_Send_Private_Message)
{
  switch (error)
    {
    success_case (GROUP_SEND_PRIVATE_MESSAGE);
    failure_case (GROUP_SEND_PRIVATE_MESSAGE, BAD_TYPE);
    failure_case (GROUP_SEND_PRIVATE_MESSAGE, DISCONNECTED);
    failure_case (GROUP_SEND_PRIVATE_MESSAGE, EMPTY);
    failure_case (GROUP_SEND_PRIVATE_MESSAGE, FAIL_SEND);
    failure_case (GROUP_SEND_PRIVATE_MESSAGE, GROUP_NOT_FOUND);
    failure_case (GROUP_SEND_PRIVATE_MESSAGE, PERMISSIONS);
    failure_case (GROUP_SEND_PRIVATE_MESSAGE, PEER_NOT_FOUND);
    failure_case (GROUP_SEND_PRIVATE_MESSAGE, TOO_LONG);
    }
  return unhandled ();
}

HANDLE ("GroupSendCustomPacket", Group_Send_Custom_Packet)
{
  switch (error)
    {
    success_case (GROUP_SEND_CUSTOM_PACKET);
    failure_case (GROUP_SEND_CUSTOM_PACKET, DISCONNECTED);
    failure_case (GROUP_SEND_CUSTOM_PACKET, EMPTY);
    failure_case (GROUP_SEND_CUSTOM_PACKET, FAIL_SEND);
    failure_case (GROUP_SEND_CUSTOM_PACKET, GROUP_NOT_FOUND);
    failure_case (GROUP_SEND_CUSTOM_PACKET, TOO_LONG);
    }
  return unhandled ();
}

HANDLE ("GroupSendCustomPrivatePacket", Group_Send_Custom_Private_Packet)
{
  switch (error)
    {
    success_case (GROUP_SEND_CUSTOM_PRIVATE_PACKET);
    failure_case (GROUP_SEND_CUSTOM_PRIVATE_PACKET, DISCONNECTED);
    failure_case (GROUP_SEND_CUSTOM_PRIVATE_PACKET, EMPTY);
    failure_case (GROUP_SEND_CUSTOM_PRIVATE_PACKET, FAIL_SEND);
    failure_case (GROUP_SEND_CUSTOM_PRIVATE_PACKET, GROUP_NOT_FOUND);
    failure_case (GROUP_SEND_CUSTOM_PRIVATE_PACKET, PEER_NOT_FOUND);
    failure_case (GROUP_SEND_CUSTOM_PRIVATE_PACKET, TOO_LONG);
    }
  return unhandled ();
}

HANDLE ("GroupInviteFriend", Group_Invite_Friend)
{
  switch (error)
    {
    success_case (GROUP_INVITE_FRIEND);
    failure_case (GROUP_INVITE_FRIEND, DISCONNECTED);
    failure_case (GROUP_INVITE_FRIEND, FAIL_SEND);
    failure_case (GROUP_INVITE_FRIEND, FRIEND_NOT_FOUND);
    failure_case (GROUP_INVITE_FRIEND, GROUP_NOT_FOUND);
    failure_case (GROUP_INVITE_FRIEND, INVITE_FAIL);
    }
  return unhandled ();
}

HANDLE ("GroupInviteAccept", Group_Invite_Accept)
{
  switch (error)
    {
    success_case (GROUP_INVITE_ACCEPT);
    failure_case (GROUP_INVITE_ACCEPT, BAD_INVITE);
    failure_case (GROUP_INVITE_ACCEPT, EMPTY);
    failure_case (GROUP_INVITE_ACCEPT, FAIL_SEND);
    failure_case (GROUP_INVITE_ACCEPT, FRIEND_NOT_FOUND);
    failure_case (GROUP_INVITE_ACCEPT, INIT_FAILED);
    failure_case (GROUP_INVITE_ACCEPT, PASSWORD);
    failure_case (GROUP_INVITE_ACCEPT, TOO_LONG);
    }
  return unhandled ();
}

HANDLE ("GroupSetPassword", Group_Set_Password)
{
  switch (error)
    {
    success_case (GROUP_SET_PASSWORD);
    failure_case (GROUP_SET_PASSWORD, DISCONNECTED);
    failure_case (GROUP_SET_PASSWORD, FAIL_SEND);
    failure_case (GROUP_SET_PASSWORD, GROUP_NOT_FOUND);
    failure_case (GROUP_SET_PASSWORD, MALLOC);
    failure_case (GROUP_SET_PASSWORD, PERMISSIONS);
    failure_case (GROUP_SET_PASSWORD, TOO_LONG);
    }
  return unhandled ();
}

HANDLE ("GroupSetTopicLock", Group_Set_Topic_Lock)
{
  switch (error)
    {
    success_case (GROUP_SET_TOPIC_LOCK);
    failure_case (GROUP_SET_TOPIC_LOCK, DISCONNECTED);
    failure_case (GROUP_SET_TOPIC_LOCK, FAIL_SEND);
    failure_case (GROUP_SET_TOPIC_LOCK, FAIL_SET);
    failure_case (GROUP_SET_TOPIC_LOCK, GROUP_NOT_FOUND);
    failure_case (GROUP_SET_TOPIC_LOCK, INVALID);
    failure_case (GROUP_SET_TOPIC_LOCK, PERMISSIONS);
    }
  return unhandled ();
}

HANDLE ("GroupSetVoiceState", Group_Set_Voice_State)
{
  switch (error)
    {
    success_case (GROUP_SET_VOICE_STATE);
    failure_case (GROUP_SET_VOICE_STATE, DISCONNECTED);
    failure_case (GROUP_SET_VOICE_STATE, FAIL_SEND);
    failure_case (GROUP_SET_VOICE_STATE, FAIL_SET);
    failure_case (GROUP_SET_VOICE_STATE, GROUP_NOT_FOUND);
    failure_case (GROUP_SET_VOICE_STATE, PERMISSIONS);
    }
  return unhandled ();
}

HANDLE ("GroupSetPrivacyState", Group_Set_Privacy_State)
{
  switch (error)
    {
    success_case (GROUP_SET_PRIVACY_STATE);
    failure_case (GROUP_SET_PRIVACY_STATE, DISCONNECTED);
    failure_case (GROUP_SET_PRIVACY_STATE, FAIL_SEND);
    failure_case (GROUP_SET_PRIVACY_STATE, FAIL_SET);
    failure_case (GROUP_SET_PRIVACY_STATE, GROUP_NOT_FOUND);
    failure_case (GROUP_SET_PRIVACY_STATE, PERMISSIONS);
    }
  return unhandled ();
}

HANDLE ("GroupSetPeerLimit", Group_Set_Peer_Limit)
{
  switch (error)
    {
    success_case (GROUP_SET_PEER_LIMIT);
    failure_case (GROUP_SET_PEER_LIMIT, DISCONNECTED);
    failure_case (GROUP_SET_PEER_LIMIT, FAIL_SEND);
    failure_case (GROUP_SET_PEER_LIMIT, FAIL_SET);
    failure_case (GROUP_SET_PEER_LIMIT, GROUP_NOT_FOUND);
    failure_case (GROUP_SET_PEER_LIMIT, PERMISSIONS);
    }
  return unhandled ();
}

HANDLE ("GroupSetIgnore", Group_Set_Ignore)
{
  switch (error)
    {
    success_case (GROUP_SET_IGNORE);
    failure_case (GROUP_SET_IGNORE, GROUP_NOT_FOUND);
    failure_case (GROUP_SET_IGNORE, PEER_NOT_FOUND);
    failure_case (GROUP_SET_IGNORE, SELF);
    }
  return unhandled ();
}

HANDLE ("GroupSetRole", Group_Set_Role)
{
  switch (error)
    {
    success_case (GROUP_SET_ROLE);
    failure_case (GROUP_SET_ROLE, ASSIGNMENT);
    failure_case (GROUP_SET_ROLE, FAIL_ACTION);
    failure_case (GROUP_SET_ROLE, GROUP_NOT_FOUND);
    failure_case (GROUP_SET_ROLE, PEER_NOT_FOUND);
    failure_case (GROUP_SET_ROLE, PERMISSIONS);
    failure_case (GROUP_SET_ROLE, SELF);
    }
  return unhandled ();
}

HANDLE ("GroupKickPeer", Group_Kick_Peer)
{
  switch (error)
    {
    success_case (GROUP_KICK_PEER);
    failure_case (GROUP_KICK_PEER, FAIL_ACTION);
    failure_case (GROUP_KICK_PEER, FAIL_SEND);
    failure_case (GROUP_KICK_PEER, GROUP_NOT_FOUND);
    failure_case (GROUP_KICK_PEER, PEER_NOT_FOUND);
    failure_case (GROUP_KICK_PEER, PERMISSIONS);
    failure_case (GROUP_KICK_PEER, SELF);
    }
  return unhandled ();
}
