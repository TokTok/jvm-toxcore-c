#include "../ToxAv.h"

#ifdef TOXAV_VERSION_MAJOR

template<>
jint
Enum::ordinal<TOXAV_CALL_CONTROL> (JNIEnv *env, TOXAV_CALL_CONTROL valueOf)
{
  switch (valueOf)
    {
    case TOXAV_CALL_CONTROL_RESUME: return 0;
    case TOXAV_CALL_CONTROL_PAUSE: return 1;
    case TOXAV_CALL_CONTROL_CANCEL: return 2;
    case TOXAV_CALL_CONTROL_MUTE_AUDIO: return 3;
    case TOXAV_CALL_CONTROL_UNMUTE_AUDIO: return 4;
    case TOXAV_CALL_CONTROL_HIDE_VIDEO: return 5;
    case TOXAV_CALL_CONTROL_SHOW_VIDEO: return 6;
    }
  tox4j_fatal ("Invalid enumerator from toxcore");
}

template<>
TOXAV_CALL_CONTROL
Enum::valueOf<TOXAV_CALL_CONTROL> (JNIEnv *env, jint ordinal)
{
  switch (ordinal)
    {
    case 0: return TOXAV_CALL_CONTROL_RESUME;
    case 1: return TOXAV_CALL_CONTROL_PAUSE;
    case 2: return TOXAV_CALL_CONTROL_CANCEL;
    case 3: return TOXAV_CALL_CONTROL_MUTE_AUDIO;
    case 4: return TOXAV_CALL_CONTROL_UNMUTE_AUDIO;
    case 5: return TOXAV_CALL_CONTROL_HIDE_VIDEO;
    case 6: return TOXAV_CALL_CONTROL_SHOW_VIDEO;
    }
  tox4j_fatal ("Invalid enumerator from Java");
}

template<>
void
print_arg<TOXAV_CALL_CONTROL> (protolog::Value &value, TOXAV_CALL_CONTROL arg)
{
  switch (arg)
    {
    case TOXAV_CALL_CONTROL_RESUME: value.set_v_string ("TOXAV_CALL_CONTROL_RESUME"); return;
    case TOXAV_CALL_CONTROL_PAUSE: value.set_v_string ("TOXAV_CALL_CONTROL_PAUSE"); return;
    case TOXAV_CALL_CONTROL_CANCEL: value.set_v_string ("TOXAV_CALL_CONTROL_CANCEL"); return;
    case TOXAV_CALL_CONTROL_MUTE_AUDIO: value.set_v_string ("TOXAV_CALL_CONTROL_MUTE_AUDIO"); return;
    case TOXAV_CALL_CONTROL_UNMUTE_AUDIO: value.set_v_string ("TOXAV_CALL_CONTROL_UNMUTE_AUDIO"); return;
    case TOXAV_CALL_CONTROL_HIDE_VIDEO: value.set_v_string ("TOXAV_CALL_CONTROL_HIDE_VIDEO"); return;
    case TOXAV_CALL_CONTROL_SHOW_VIDEO: value.set_v_string ("TOXAV_CALL_CONTROL_SHOW_VIDEO"); return;
    }
  value.set_v_string ("(TOXAV_CALL_CONTROL)" + std::to_string (arg));
}

template<>
jint
Enum::ordinal<TOXAV_FRIEND_CALL_STATE> (JNIEnv *env, TOXAV_FRIEND_CALL_STATE valueOf)
{
  switch (valueOf)
    {
    case TOXAV_FRIEND_CALL_STATE_ERROR: return 0;
    case TOXAV_FRIEND_CALL_STATE_FINISHED: return 1;
    case TOXAV_FRIEND_CALL_STATE_SENDING_A: return 2;
    case TOXAV_FRIEND_CALL_STATE_SENDING_V: return 3;
    case TOXAV_FRIEND_CALL_STATE_ACCEPTING_A: return 4;
    case TOXAV_FRIEND_CALL_STATE_ACCEPTING_V: return 5;
    }
  tox4j_fatal ("Invalid enumerator from toxcore");
}

template<>
TOXAV_FRIEND_CALL_STATE
Enum::valueOf<TOXAV_FRIEND_CALL_STATE> (JNIEnv *env, jint ordinal)
{
  switch (ordinal)
    {
    case 0: return TOXAV_FRIEND_CALL_STATE_ERROR;
    case 1: return TOXAV_FRIEND_CALL_STATE_FINISHED;
    case 2: return TOXAV_FRIEND_CALL_STATE_SENDING_A;
    case 3: return TOXAV_FRIEND_CALL_STATE_SENDING_V;
    case 4: return TOXAV_FRIEND_CALL_STATE_ACCEPTING_A;
    case 5: return TOXAV_FRIEND_CALL_STATE_ACCEPTING_V;
    }
  tox4j_fatal ("Invalid enumerator from Java");
}

template<>
void
print_arg<TOXAV_FRIEND_CALL_STATE> (protolog::Value &value, TOXAV_FRIEND_CALL_STATE arg)
{
  switch (arg)
    {
    case TOXAV_FRIEND_CALL_STATE_ERROR: value.set_v_string ("TOXAV_FRIEND_CALL_STATE_ERROR"); return;
    case TOXAV_FRIEND_CALL_STATE_FINISHED: value.set_v_string ("TOXAV_FRIEND_CALL_STATE_FINISHED"); return;
    case TOXAV_FRIEND_CALL_STATE_SENDING_A: value.set_v_string ("TOXAV_FRIEND_CALL_STATE_SENDING_A"); return;
    case TOXAV_FRIEND_CALL_STATE_SENDING_V: value.set_v_string ("TOXAV_FRIEND_CALL_STATE_SENDING_V"); return;
    case TOXAV_FRIEND_CALL_STATE_ACCEPTING_A: value.set_v_string ("TOXAV_FRIEND_CALL_STATE_ACCEPTING_A"); return;
    case TOXAV_FRIEND_CALL_STATE_ACCEPTING_V: value.set_v_string ("TOXAV_FRIEND_CALL_STATE_ACCEPTING_V"); return;
    }
  value.set_v_string ("(TOXAV_FRIEND_CALL_STATE)" + std::to_string (arg));
}

#endif
