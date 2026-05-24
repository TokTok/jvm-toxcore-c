#include "../ToxAv.h"

template<>
jint
Enum::ordinal<Toxav_Call_Control> (JNIEnv *env, Toxav_Call_Control valueOf)
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
Toxav_Call_Control
Enum::valueOf<Toxav_Call_Control> (JNIEnv *env, jint ordinal)
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
jint
Enum::ordinal<Toxav_Friend_Call_State> (JNIEnv *env, Toxav_Friend_Call_State valueOf)
{
  switch (valueOf)
    {
    case TOXAV_FRIEND_CALL_STATE_NONE: return 0;
    case TOXAV_FRIEND_CALL_STATE_ERROR: return 1;
    case TOXAV_FRIEND_CALL_STATE_FINISHED: return 2;
    case TOXAV_FRIEND_CALL_STATE_SENDING_A: return 3;
    case TOXAV_FRIEND_CALL_STATE_SENDING_V: return 4;
    case TOXAV_FRIEND_CALL_STATE_ACCEPTING_A: return 5;
    case TOXAV_FRIEND_CALL_STATE_ACCEPTING_V: return 6;
    }
  tox4j_fatal ("Invalid enumerator from toxcore");
}

template<>
Toxav_Friend_Call_State
Enum::valueOf<Toxav_Friend_Call_State> (JNIEnv *env, jint ordinal)
{
  switch (ordinal)
    {
    case 0: return TOXAV_FRIEND_CALL_STATE_NONE;
    case 1: return TOXAV_FRIEND_CALL_STATE_ERROR;
    case 2: return TOXAV_FRIEND_CALL_STATE_FINISHED;
    case 3: return TOXAV_FRIEND_CALL_STATE_SENDING_A;
    case 4: return TOXAV_FRIEND_CALL_STATE_SENDING_V;
    case 5: return TOXAV_FRIEND_CALL_STATE_ACCEPTING_A;
    case 6: return TOXAV_FRIEND_CALL_STATE_ACCEPTING_V;
    }
  tox4j_fatal ("Invalid enumerator from Java");
}

