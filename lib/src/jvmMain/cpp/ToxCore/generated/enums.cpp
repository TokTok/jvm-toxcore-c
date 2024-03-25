#include "../ToxCore.h"

template<>
jint
Enum::ordinal<Tox_Connection> (JNIEnv *env, Tox_Connection valueOf)
{
  switch (valueOf)
    {
    case TOX_CONNECTION_NONE: return 0;
    case TOX_CONNECTION_TCP: return 1;
    case TOX_CONNECTION_UDP: return 2;
    }
  tox4j_fatal ("Invalid enumerator from toxcore");
}

template<>
Tox_Connection
Enum::valueOf<Tox_Connection> (JNIEnv *env, jint ordinal)
{
  switch (ordinal)
    {
    case 0: return TOX_CONNECTION_NONE;
    case 1: return TOX_CONNECTION_TCP;
    case 2: return TOX_CONNECTION_UDP;
    }
  tox4j_fatal ("Invalid enumerator from Java");
}

template<>
jint
Enum::ordinal<Tox_File_Control> (JNIEnv *env, Tox_File_Control valueOf)
{
  switch (valueOf)
    {
    case TOX_FILE_CONTROL_RESUME: return 0;
    case TOX_FILE_CONTROL_PAUSE: return 1;
    case TOX_FILE_CONTROL_CANCEL: return 2;
    }
  tox4j_fatal ("Invalid enumerator from toxcore");
}

template<>
Tox_File_Control
Enum::valueOf<Tox_File_Control> (JNIEnv *env, jint ordinal)
{
  switch (ordinal)
    {
    case 0: return TOX_FILE_CONTROL_RESUME;
    case 1: return TOX_FILE_CONTROL_PAUSE;
    case 2: return TOX_FILE_CONTROL_CANCEL;
    }
  tox4j_fatal ("Invalid enumerator from Java");
}

template<>
jint
Enum::ordinal<Tox_Message_Type> (JNIEnv *env, Tox_Message_Type valueOf)
{
  switch (valueOf)
    {
    case TOX_MESSAGE_TYPE_NORMAL: return 0;
    case TOX_MESSAGE_TYPE_ACTION: return 1;
    }
  tox4j_fatal ("Invalid enumerator from toxcore");
}

template<>
Tox_Message_Type
Enum::valueOf<Tox_Message_Type> (JNIEnv *env, jint ordinal)
{
  switch (ordinal)
    {
    case 0: return TOX_MESSAGE_TYPE_NORMAL;
    case 1: return TOX_MESSAGE_TYPE_ACTION;
    }
  tox4j_fatal ("Invalid enumerator from Java");
}

template<>
jint
Enum::ordinal<Tox_Proxy_Type> (JNIEnv *env, Tox_Proxy_Type valueOf)
{
  switch (valueOf)
    {
    case TOX_PROXY_TYPE_NONE: return 0;
    case TOX_PROXY_TYPE_HTTP: return 1;
    case TOX_PROXY_TYPE_SOCKS5: return 2;
    }
  tox4j_fatal ("Invalid enumerator from toxcore");
}

template<>
Tox_Proxy_Type
Enum::valueOf<Tox_Proxy_Type> (JNIEnv *env, jint ordinal)
{
  switch (ordinal)
    {
    case 0: return TOX_PROXY_TYPE_NONE;
    case 1: return TOX_PROXY_TYPE_HTTP;
    case 2: return TOX_PROXY_TYPE_SOCKS5;
    }
  tox4j_fatal ("Invalid enumerator from Java");
}

template<>
jint
Enum::ordinal<Tox_Savedata_Type> (JNIEnv *env, Tox_Savedata_Type valueOf)
{
  switch (valueOf)
    {
    case TOX_SAVEDATA_TYPE_NONE: return 0;
    case TOX_SAVEDATA_TYPE_TOX_SAVE: return 1;
    case TOX_SAVEDATA_TYPE_SECRET_KEY: return 2;
    }
  tox4j_fatal ("Invalid enumerator from toxcore");
}

template<>
Tox_Savedata_Type
Enum::valueOf<Tox_Savedata_Type> (JNIEnv *env, jint ordinal)
{
  switch (ordinal)
    {
    case 0: return TOX_SAVEDATA_TYPE_NONE;
    case 1: return TOX_SAVEDATA_TYPE_TOX_SAVE;
    case 2: return TOX_SAVEDATA_TYPE_SECRET_KEY;
    }
  tox4j_fatal ("Invalid enumerator from Java");
}

template<>
jint
Enum::ordinal<Tox_User_Status> (JNIEnv *env, Tox_User_Status valueOf)
{
  switch (valueOf)
    {
    case TOX_USER_STATUS_NONE: return 0;
    case TOX_USER_STATUS_AWAY: return 1;
    case TOX_USER_STATUS_BUSY: return 2;
    }
  tox4j_fatal ("Invalid enumerator from toxcore");
}

template<>
Tox_User_Status
Enum::valueOf<Tox_User_Status> (JNIEnv *env, jint ordinal)
{
  switch (ordinal)
    {
    case 0: return TOX_USER_STATUS_NONE;
    case 1: return TOX_USER_STATUS_AWAY;
    case 2: return TOX_USER_STATUS_BUSY;
    }
  tox4j_fatal ("Invalid enumerator from Java");
}

template<>
jint
Enum::ordinal<Tox_Conference_Type> (JNIEnv *env, Tox_Conference_Type valueOf)
{
  switch (valueOf)
    {
    case TOX_CONFERENCE_TYPE_TEXT: return 0;
    case TOX_CONFERENCE_TYPE_AV: return 1;
    }
  tox4j_fatal ("Invalid enumerator from toxcore");
}

template<>
Tox_Conference_Type
Enum::valueOf<Tox_Conference_Type> (JNIEnv *env, jint ordinal)
{
  switch (ordinal)
    {
    case 0: return TOX_CONFERENCE_TYPE_TEXT;
    case 1: return TOX_CONFERENCE_TYPE_AV;
    }
  tox4j_fatal ("Invalid enumerator from Java");
}

template<>
jint
Enum::ordinal<Tox_Group_Privacy_State> (JNIEnv *env, Tox_Group_Privacy_State valueOf)
{
  switch (valueOf)
    {
    case TOX_GROUP_PRIVACY_STATE_PUBLIC: return 0;
    case TOX_GROUP_PRIVACY_STATE_PRIVATE: return 1;
    }
  tox4j_fatal ("Invalid enumerator from toxcore");
}

template<>
Tox_Group_Privacy_State
Enum::valueOf<Tox_Group_Privacy_State> (JNIEnv *env, jint ordinal)
{
  switch (ordinal)
    {
    case 0: return TOX_GROUP_PRIVACY_STATE_PUBLIC;
    case 1: return TOX_GROUP_PRIVACY_STATE_PRIVATE;
    }
  tox4j_fatal ("Invalid enumerator from Java");
}

template<>
jint
Enum::ordinal<Tox_Group_Topic_Lock> (JNIEnv *env, Tox_Group_Topic_Lock valueOf)
{
  switch (valueOf)
    {
    case TOX_GROUP_TOPIC_LOCK_ENABLED: return 0;
    case TOX_GROUP_TOPIC_LOCK_DISABLED: return 1;
    }
  tox4j_fatal ("Invalid enumerator from toxcore");
}

template<>
Tox_Group_Topic_Lock
Enum::valueOf<Tox_Group_Topic_Lock> (JNIEnv *env, jint ordinal)
{
  switch (ordinal)
    {
    case 0: return TOX_GROUP_TOPIC_LOCK_ENABLED;
    case 1: return TOX_GROUP_TOPIC_LOCK_DISABLED;
    }
  tox4j_fatal ("Invalid enumerator from Java");
}

template<>
jint
Enum::ordinal<Tox_Group_Voice_State> (JNIEnv *env, Tox_Group_Voice_State valueOf)
{
  switch (valueOf)
    {
    case TOX_GROUP_VOICE_STATE_ALL: return 0;
    case TOX_GROUP_VOICE_STATE_MODERATOR: return 1;
    case TOX_GROUP_VOICE_STATE_FOUNDER: return 2;
    }
  tox4j_fatal ("Invalid enumerator from toxcore");
}

template<>
Tox_Group_Voice_State
Enum::valueOf<Tox_Group_Voice_State> (JNIEnv *env, jint ordinal)
{
  switch (ordinal)
    {
    case 0: return TOX_GROUP_VOICE_STATE_ALL;
    case 1: return TOX_GROUP_VOICE_STATE_MODERATOR;
    case 2: return TOX_GROUP_VOICE_STATE_FOUNDER;
    }
  tox4j_fatal ("Invalid enumerator from Java");
}

template<>
jint
Enum::ordinal<Tox_Group_Role> (JNIEnv *env, Tox_Group_Role valueOf)
{
  switch (valueOf)
    {
    case TOX_GROUP_ROLE_FOUNDER: return 0;
    case TOX_GROUP_ROLE_MODERATOR: return 1;
    case TOX_GROUP_ROLE_USER: return 2;
    case TOX_GROUP_ROLE_OBSERVER: return 3;
    }
  tox4j_fatal ("Invalid enumerator from toxcore");
}

template<>
Tox_Group_Role
Enum::valueOf<Tox_Group_Role> (JNIEnv *env, jint ordinal)
{
  switch (ordinal)
    {
    case 0: return TOX_GROUP_ROLE_FOUNDER;
    case 1: return TOX_GROUP_ROLE_MODERATOR;
    case 2: return TOX_GROUP_ROLE_USER;
    case 3: return TOX_GROUP_ROLE_OBSERVER;
    }
  tox4j_fatal ("Invalid enumerator from Java");
}

template<>
jint
Enum::ordinal<Tox_Group_Exit_Type> (JNIEnv *env, Tox_Group_Exit_Type valueOf)
{
  switch (valueOf)
    {
    case TOX_GROUP_EXIT_TYPE_QUIT: return 0;
    case TOX_GROUP_EXIT_TYPE_TIMEOUT: return 1;
    case TOX_GROUP_EXIT_TYPE_DISCONNECTED: return 2;
    case TOX_GROUP_EXIT_TYPE_SELF_DISCONNECTED: return 3;
    case TOX_GROUP_EXIT_TYPE_KICK: return 4;
    case TOX_GROUP_EXIT_TYPE_SYNC_ERROR: return 5;
    }
  tox4j_fatal ("Invalid enumerator from toxcore");
}

template<>
Tox_Group_Exit_Type
Enum::valueOf<Tox_Group_Exit_Type> (JNIEnv *env, jint ordinal)
{
  switch (ordinal)
    {
    case 0: return TOX_GROUP_EXIT_TYPE_QUIT;
    case 1: return TOX_GROUP_EXIT_TYPE_TIMEOUT;
    case 2: return TOX_GROUP_EXIT_TYPE_DISCONNECTED;
    case 3: return TOX_GROUP_EXIT_TYPE_SELF_DISCONNECTED;
    case 4: return TOX_GROUP_EXIT_TYPE_KICK;
    case 5: return TOX_GROUP_EXIT_TYPE_SYNC_ERROR;
    }
  tox4j_fatal ("Invalid enumerator from Java");
}

template<>
jint
Enum::ordinal<Tox_Group_Join_Fail> (JNIEnv *env, Tox_Group_Join_Fail valueOf)
{
  switch (valueOf)
    {
    case TOX_GROUP_JOIN_FAIL_PEER_LIMIT: return 0;
    case TOX_GROUP_JOIN_FAIL_INVALID_PASSWORD: return 1;
    case TOX_GROUP_JOIN_FAIL_UNKNOWN: return 2;
    }
  tox4j_fatal ("Invalid enumerator from toxcore");
}

template<>
Tox_Group_Join_Fail
Enum::valueOf<Tox_Group_Join_Fail> (JNIEnv *env, jint ordinal)
{
  switch (ordinal)
    {
    case 0: return TOX_GROUP_JOIN_FAIL_PEER_LIMIT;
    case 1: return TOX_GROUP_JOIN_FAIL_INVALID_PASSWORD;
    case 2: return TOX_GROUP_JOIN_FAIL_UNKNOWN;
    }
  tox4j_fatal ("Invalid enumerator from Java");
}

template<>
jint
Enum::ordinal<Tox_Group_Mod_Event> (JNIEnv *env, Tox_Group_Mod_Event valueOf)
{
  switch (valueOf)
    {
    case TOX_GROUP_MOD_EVENT_KICK: return 0;
    case TOX_GROUP_MOD_EVENT_OBSERVER: return 1;
    case TOX_GROUP_MOD_EVENT_USER: return 2;
    case TOX_GROUP_MOD_EVENT_MODERATOR: return 3;
    }
  tox4j_fatal ("Invalid enumerator from toxcore");
}

template<>
Tox_Group_Mod_Event
Enum::valueOf<Tox_Group_Mod_Event> (JNIEnv *env, jint ordinal)
{
  switch (ordinal)
    {
    case 0: return TOX_GROUP_MOD_EVENT_KICK;
    case 1: return TOX_GROUP_MOD_EVENT_OBSERVER;
    case 2: return TOX_GROUP_MOD_EVENT_USER;
    case 3: return TOX_GROUP_MOD_EVENT_MODERATOR;
    }
  tox4j_fatal ("Invalid enumerator from Java");
}
