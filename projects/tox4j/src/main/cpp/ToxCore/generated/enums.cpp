#include "../ToxCore.h"

#ifdef TOX_VERSION_MAJOR

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
void
print_arg<TOX_CONNECTION> (protolog::Value &value, TOX_CONNECTION arg)
{
  switch (arg)
    {
    case TOX_CONNECTION_NONE: value.set_v_string ("TOX_CONNECTION_NONE"); return;
    case TOX_CONNECTION_TCP: value.set_v_string ("TOX_CONNECTION_TCP"); return;
    case TOX_CONNECTION_UDP: value.set_v_string ("TOX_CONNECTION_UDP"); return;
    }
  value.set_v_string ("(TOX_CONNECTION)" + std::to_string (arg));
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
void
print_arg<TOX_FILE_CONTROL> (protolog::Value &value, TOX_FILE_CONTROL arg)
{
  switch (arg)
    {
    case TOX_FILE_CONTROL_RESUME: value.set_v_string ("TOX_FILE_CONTROL_RESUME"); return;
    case TOX_FILE_CONTROL_PAUSE: value.set_v_string ("TOX_FILE_CONTROL_PAUSE"); return;
    case TOX_FILE_CONTROL_CANCEL: value.set_v_string ("TOX_FILE_CONTROL_CANCEL"); return;
    }
  value.set_v_string ("(TOX_FILE_CONTROL)" + std::to_string (arg));
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
void
print_arg<TOX_MESSAGE_TYPE> (protolog::Value &value, TOX_MESSAGE_TYPE arg)
{
  switch (arg)
    {
    case TOX_MESSAGE_TYPE_NORMAL: value.set_v_string ("TOX_MESSAGE_TYPE_NORMAL"); return;
    case TOX_MESSAGE_TYPE_ACTION: value.set_v_string ("TOX_MESSAGE_TYPE_ACTION"); return;
    }
  value.set_v_string ("(TOX_MESSAGE_TYPE)" + std::to_string (arg));
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
void
print_arg<TOX_PROXY_TYPE> (protolog::Value &value, TOX_PROXY_TYPE arg)
{
  switch (arg)
    {
    case TOX_PROXY_TYPE_NONE: value.set_v_string ("TOX_PROXY_TYPE_NONE"); return;
    case TOX_PROXY_TYPE_HTTP: value.set_v_string ("TOX_PROXY_TYPE_HTTP"); return;
    case TOX_PROXY_TYPE_SOCKS5: value.set_v_string ("TOX_PROXY_TYPE_SOCKS5"); return;
    }
  value.set_v_string ("(TOX_PROXY_TYPE)" + std::to_string (arg));
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
void
print_arg<TOX_SAVEDATA_TYPE> (protolog::Value &value, TOX_SAVEDATA_TYPE arg)
{
  switch (arg)
    {
    case TOX_SAVEDATA_TYPE_NONE: value.set_v_string ("TOX_SAVEDATA_TYPE_NONE"); return;
    case TOX_SAVEDATA_TYPE_TOX_SAVE: value.set_v_string ("TOX_SAVEDATA_TYPE_TOX_SAVE"); return;
    case TOX_SAVEDATA_TYPE_SECRET_KEY: value.set_v_string ("TOX_SAVEDATA_TYPE_SECRET_KEY"); return;
    }
  value.set_v_string ("(TOX_SAVEDATA_TYPE)" + std::to_string (arg));
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

template<>
void
print_arg<TOX_USER_STATUS> (protolog::Value &value, TOX_USER_STATUS arg)
{
  switch (arg)
    {
    case TOX_USER_STATUS_NONE: value.set_v_string ("TOX_USER_STATUS_NONE"); return;
    case TOX_USER_STATUS_AWAY: value.set_v_string ("TOX_USER_STATUS_AWAY"); return;
    case TOX_USER_STATUS_BUSY: value.set_v_string ("TOX_USER_STATUS_BUSY"); return;
    }
  value.set_v_string ("(TOX_USER_STATUS)" + std::to_string (arg));
}

#endif
