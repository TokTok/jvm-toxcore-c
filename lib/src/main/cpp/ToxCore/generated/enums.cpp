#include "../ToxCore.h"

template<>
jint
Enum::ordinal<TOX_CONNECTION> (JNIEnv *env, TOX_CONNECTION valueOf)
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
TOX_CONNECTION
Enum::valueOf<TOX_CONNECTION> (JNIEnv *env, jint ordinal)
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
Enum::ordinal<TOX_FILE_CONTROL> (JNIEnv *env, TOX_FILE_CONTROL valueOf)
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
TOX_FILE_CONTROL
Enum::valueOf<TOX_FILE_CONTROL> (JNIEnv *env, jint ordinal)
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
Enum::ordinal<TOX_MESSAGE_TYPE> (JNIEnv *env, TOX_MESSAGE_TYPE valueOf)
{
  switch (valueOf)
    {
    case TOX_MESSAGE_TYPE_NORMAL: return 0;
    case TOX_MESSAGE_TYPE_ACTION: return 1;
    }
  tox4j_fatal ("Invalid enumerator from toxcore");
}

template<>
TOX_MESSAGE_TYPE
Enum::valueOf<TOX_MESSAGE_TYPE> (JNIEnv *env, jint ordinal)
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
Enum::ordinal<TOX_PROXY_TYPE> (JNIEnv *env, TOX_PROXY_TYPE valueOf)
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
TOX_PROXY_TYPE
Enum::valueOf<TOX_PROXY_TYPE> (JNIEnv *env, jint ordinal)
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
Enum::ordinal<TOX_SAVEDATA_TYPE> (JNIEnv *env, TOX_SAVEDATA_TYPE valueOf)
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
TOX_SAVEDATA_TYPE
Enum::valueOf<TOX_SAVEDATA_TYPE> (JNIEnv *env, jint ordinal)
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
Enum::ordinal<TOX_USER_STATUS> (JNIEnv *env, TOX_USER_STATUS valueOf)
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
TOX_USER_STATUS
Enum::valueOf<TOX_USER_STATUS> (JNIEnv *env, jint ordinal)
{
  switch (ordinal)
    {
    case 0: return TOX_USER_STATUS_NONE;
    case 1: return TOX_USER_STATUS_AWAY;
    case 2: return TOX_USER_STATUS_BUSY;
    }
  tox4j_fatal ("Invalid enumerator from Java");
}
