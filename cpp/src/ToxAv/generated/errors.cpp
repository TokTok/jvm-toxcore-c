#include "../ToxAv.h"

HANDLE ("Answer", ANSWER)
{
  switch (error)
    {
    success_case (ANSWER);
    failure_case (ANSWER, CODEC_INITIALIZATION);
    failure_case (ANSWER, FRIEND_NOT_CALLING);
    failure_case (ANSWER, FRIEND_NOT_FOUND);
    failure_case (ANSWER, INVALID_BIT_RATE);
    failure_case (ANSWER, SYNC);
    }
  return unhandled ();
}

HANDLE ("BitRateSet", BIT_RATE_SET)
{
  switch (error)
    {
    success_case (BIT_RATE_SET);
    failure_case (BIT_RATE_SET, FRIEND_NOT_FOUND);
    failure_case (BIT_RATE_SET, FRIEND_NOT_IN_CALL);
    failure_case (BIT_RATE_SET, INVALID_AUDIO_BIT_RATE);
    failure_case (BIT_RATE_SET, INVALID_VIDEO_BIT_RATE);
    failure_case (BIT_RATE_SET, SYNC);
    }
  return unhandled ();
}

HANDLE ("CallControl", CALL_CONTROL)
{
  switch (error)
    {
    success_case (CALL_CONTROL);
    failure_case (CALL_CONTROL, FRIEND_NOT_FOUND);
    failure_case (CALL_CONTROL, FRIEND_NOT_IN_CALL);
    failure_case (CALL_CONTROL, INVALID_TRANSITION);
    failure_case (CALL_CONTROL, SYNC);
    }
  return unhandled ();
}

HANDLE ("Call", CALL)
{
  switch (error)
    {
    success_case (CALL);
    failure_case (CALL, FRIEND_ALREADY_IN_CALL);
    failure_case (CALL, FRIEND_NOT_CONNECTED);
    failure_case (CALL, FRIEND_NOT_FOUND);
    failure_case (CALL, INVALID_BIT_RATE);
    failure_case (CALL, MALLOC);
    failure_case (CALL, SYNC);
    }
  return unhandled ();
}

HANDLE ("New", NEW)
{
  switch (error)
    {
    success_case (NEW);
    failure_case (NEW, MALLOC);
    failure_case (NEW, MULTIPLE);
    failure_case (NEW, NULL);
    }
  return unhandled ();
}

HANDLE ("SendFrame", SEND_FRAME)
{
  switch (error)
    {
    success_case (SEND_FRAME);
    failure_case (SEND_FRAME, FRIEND_NOT_FOUND);
    failure_case (SEND_FRAME, FRIEND_NOT_IN_CALL);
    failure_case (SEND_FRAME, INVALID);
    failure_case (SEND_FRAME, NULL);
    failure_case (SEND_FRAME, PAYLOAD_TYPE_DISABLED);
    failure_case (SEND_FRAME, RTP_FAILED);
    failure_case (SEND_FRAME, SYNC);
    }
  return unhandled ();
}
