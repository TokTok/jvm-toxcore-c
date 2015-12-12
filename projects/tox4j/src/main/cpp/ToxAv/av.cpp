#include "ToxAv.h"

#ifdef TOXAV_VERSION_MAJOR

using namespace av;

/*
 * Class:     im_tox_tox4j_impl_ToxAvJni
 * Method:    toxavIterationInterval
 * Signature: (I)I
 */
TOX_METHOD (jint, IterationInterval,
  jint instanceNumber)
{
  return instances.with_instance_noerr (env, instanceNumber,
    toxav_iteration_interval);
}

/*
 * Class:     im_tox_tox4j_impl_ToxAvJni
 * Method:    toxavIterate
 * Signature: (I)[B
 */
TOX_METHOD (jbyteArray, Iterate,
  jint instanceNumber)
{
  return instances.with_instance (env, instanceNumber,
    [=] (ToxAV *av, Events &events) -> jbyteArray
      {
        LogEntry log_entry (instanceNumber, toxav_iterate, av);

        log_entry.print_result (toxav_iterate, av);
        if (events.ByteSize () == 0)
          return nullptr;

        std::vector<char> buffer (events.ByteSize ());
        events.SerializeToArray (buffer.data (), buffer.size ());
        events.Clear ();

        return toJavaArray (env, buffer);
      }
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxAvJni
 * Method:    toxavCall
 * Signature: (IIII)V
 */
TOX_METHOD (void, Call,
  jint instanceNumber, jint friendNumber, jint audioBitRate, jint videoBitRate)
{
  return instances.with_instance_ign (env, instanceNumber,
    toxav_call, friendNumber, audioBitRate, videoBitRate
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxAvJni
 * Method:    toxavAnswer
 * Signature: (IIII)V
 */
TOX_METHOD (void, Answer,
  jint instanceNumber, jint friendNumber, jint audioBitRate, jint videoBitRate)
{
  return instances.with_instance_ign (env, instanceNumber,
    toxav_answer, friendNumber, audioBitRate, videoBitRate
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxAvJni
 * Method:    toxavCallControl
 * Signature: (III)V
 */
TOX_METHOD (void, CallControl,
  jint instanceNumber, jint friendNumber, jint control)
{
  return instances.with_instance_ign (env, instanceNumber,
    toxav_call_control, friendNumber, Enum::valueOf<TOXAV_CALL_CONTROL> (env, control)
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxAvJni
 * Method:    toxavBitRateSet
 * Signature: (IIII)V
 */
TOX_METHOD (void, BitRateSet,
  jint instanceNumber, jint friendNumber, jint audioBitRate, jint videoBitRate)
{
  return instances.with_instance_ign (env, instanceNumber,
    toxav_bit_rate_set, friendNumber, audioBitRate, videoBitRate
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxAvJni
 * Method:    toxavAudioSendFrame
 * Signature: (II[SIII)V
 */
TOX_METHOD (void, AudioSendFrame,
  jint instanceNumber, jint friendNumber, jshortArray pcm, jint sampleCount, jint channels, jint samplingRate)
{
  tox4j_assert (sampleCount >= 0);
  tox4j_assert (channels >= 0);
  tox4j_assert (channels <= 255);
  tox4j_assert (samplingRate >= 0);

  auto pcmData = fromJavaArray (env, pcm);
  if (pcmData.size () != size_t (sampleCount * channels))
    return throw_tox_exception<ToxAV> (env, TOXAV_ERR_SEND_FRAME_INVALID);

  return instances.with_instance_ign (env, instanceNumber,
    toxav_audio_send_frame, friendNumber, pcmData.data (), sampleCount, channels, samplingRate
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxAvJni
 * Method:    toxavVideoSendFrame
 * Signature: (IIII[B[B[B[B)V
 */
TOX_METHOD (void, VideoSendFrame,
  jint instanceNumber, jint friendNumber, jint width, jint height, jbyteArray y, jbyteArray u, jbyteArray v)
{
  auto yData = fromJavaArray (env, y);
  auto uData = fromJavaArray (env, u);
  auto vData = fromJavaArray (env, v);
  if (yData.size () != width * height ||
      uData.size () != (width / 2) * (height / 2) ||
      vData.size () != (width / 2) * (height / 2))
    return throw_tox_exception<ToxAV> (env, TOXAV_ERR_SEND_FRAME_INVALID);

  return instances.with_instance_ign (env, instanceNumber,
    toxav_video_send_frame, friendNumber, width, height, yData.data (), uData.data (), vData.data ()
  );
}

#endif
