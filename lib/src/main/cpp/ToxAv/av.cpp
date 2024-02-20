#include "ToxAv.h"

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
        InstanceLogEntry log_entry (instanceNumber, toxav_iterate, av);

#if 0
        log_entry.print_result (toxav_iterate, av, &events);
#else
        log_entry.print_result (toxav_iterate, av);
#endif
        if (events.ByteSizeLong () == 0)
          return nullptr;

        std::vector<char> buffer (events.ByteSizeLong ());
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
 * Method:    toxavAudioSetBitRate
 * Signature: (III)V
 */
TOX_METHOD (void, AudioSetBitRate,
  jint instanceNumber, jint friendNumber, jint audioBitRate)
{
  return instances.with_instance_ign (env, instanceNumber,
    toxav_audio_set_bit_rate, friendNumber, audioBitRate
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxAvJni
 * Method:    toxavVideoSetBitRate
 * Signature: (III)V
 */
TOX_METHOD (void, VideoSetBitRate,
  jint instanceNumber, jint friendNumber, jint videoBitRate)
{
  return instances.with_instance_ign (env, instanceNumber,
    toxav_video_set_bit_rate, friendNumber, videoBitRate
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
    toxav_audio_send_frame, friendNumber, pcmData, sampleCount, channels, samplingRate
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
  size_t ySize = width * height;
  size_t uvSize = (width / 2) * (height / 2);

  auto yData = fromJavaArray (env, y);
  auto uData = fromJavaArray (env, u);
  auto vData = fromJavaArray (env, v);
  if (yData.size () != ySize ||
      uData.size () != uvSize ||
      vData.size () != uvSize)
    return throw_tox_exception<ToxAV> (env, TOXAV_ERR_SEND_FRAME_INVALID);

  return instances.with_instance_ign (env, instanceNumber,
    toxav_video_send_frame, friendNumber, width, height, yData, uData, vData
  );
}
