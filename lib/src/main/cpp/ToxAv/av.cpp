#include "ToxAv.h"

using namespace av;

/*
 * Class:     im_tox_tox4j_impl_ToxAvJni
 * Method:    toxavAudioSendFrame
 * Signature: (II[SJBI)V
 */
TOX_METHOD (void, AudioSendFrame,
  jint instanceNumber, jint friendNumber, jshortArray pcm, jlong sampleCount, jbyte channels, jint samplingRate)
{
  // A negative number would be a huge one in C.
  if (sampleCount < 0 || channels < 0 || samplingRate < 0)
    return throw_tox_exception<ToxAV> (env, TOXAV_ERR_SEND_FRAME_INVALID);

  // In 64 bits, so the product can't wrap in a 32-bit size_t.
  uint64_t expected = static_cast<uint64_t> (sampleCount)
                    * static_cast<uint64_t> (channels);
  if (expected > SIZE_MAX)
    return throw_tox_exception<ToxAV> (env, TOXAV_ERR_SEND_FRAME_INVALID);

  auto pcmData = fromJavaArray (env, pcm);
  if (pcmData.size () != static_cast<size_t> (expected))
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
  // Java callers can bypass the Kotlin range checks.
  if (width < 0 || height < 0)
    return throw_tox_exception<ToxAV> (env, TOXAV_ERR_SEND_FRAME_INVALID);

  // 65535 * 65535 overflows int.
  size_t ySize = static_cast<size_t> (width) * static_cast<size_t> (height);
  size_t uvSize = static_cast<size_t> (width / 2) * static_cast<size_t> (height / 2);

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
