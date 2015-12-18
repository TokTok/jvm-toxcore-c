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
 * Signature: (I[B)[B
 */
TOX_METHOD (jbyteArray, Iterate,
  jint instanceNumber, jbyteArray data)
{
  tox4j_assert (data != nullptr);

  return instances.with_instance (env, instanceNumber,
    [=] (ToxAV *av, Events &events) mutable -> jbyteArray
      {
        LogEntry log_entry (instanceNumber, toxav_iterate, av);

        log_entry.print_result (toxav_iterate, av);

        // Java array length.
        jsize dataLength = env->GetArrayLength (data);

        // No data => data[0] = 0.
        if (events.ByteSize () == 0)
          {
            if (dataLength != 0)
              {
                jbyte empty = 0;
                env->SetByteArrayRegion (data, 0, 1, &empty);
              }
            return data;
          }

        // Array too small => allocate new array.
        if (events.ByteSize () > dataLength)
          data = env->NewByteArray (events.ByteSize ());

        // Serialise events to the Java array.
        void *dataPointer = env->GetPrimitiveArrayCritical (data, nullptr);
        events.SerializeToArray (dataPointer, events.ByteSize ());
        env->ReleasePrimitiveArrayCritical (data, dataPointer, JNI_COMMIT);

        // Clear the events list.
        events.Clear ();

        return data;
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

#endif
