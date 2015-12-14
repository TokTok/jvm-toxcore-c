#include "ToxAv.h"
#include "../ToxCore/ToxCore.h"

#ifdef TOXAV_VERSION_MAJOR

using namespace av;


static void
tox4j_call_cb (uint32_t friend_number, bool audio_enabled, bool video_enabled, Events *events)
{
  auto msg = events->add_call ();
  msg->set_friend_number (friend_number);
  msg->set_audio_enabled (audio_enabled);
  msg->set_video_enabled (video_enabled);
}


static void
tox4j_call_state_cb (uint32_t friend_number, uint32_t state, Events *events)
{
  auto msg = events->add_call_state ();
  msg->set_friend_number (friend_number);

  using proto::CallState;
#define call_state_case(STATE)                  \
  if (state & TOXAV_FRIEND_CALL_STATE_##STATE)  \
    msg->add_call_state (CallState::STATE)
  call_state_case (ERROR);
  call_state_case (FINISHED);
  call_state_case (SENDING_A);
  call_state_case (SENDING_V);
  call_state_case (ACCEPTING_A);
  call_state_case (ACCEPTING_V);
#undef call_state_case
}


static void
tox4j_bit_rate_status_cb (uint32_t friend_number,
                          uint32_t audio_bit_rate,
                          uint32_t video_bit_rate,
                          Events *events)
{
  auto msg = events->add_bit_rate_status ();
  msg->set_friend_number (friend_number);
  msg->set_audio_bit_rate (audio_bit_rate);
  msg->set_video_bit_rate (video_bit_rate);
}


static void
tox4j_audio_receive_frame_cb (uint32_t friend_number,
                              int16_t const *pcm,
                              size_t sample_count,
                              uint8_t channels,
                              uint32_t sampling_rate,
                              Events *events)
{
  auto msg = events->add_audio_receive_frame ();
  msg->set_friend_number (friend_number);

  std::vector<uint8_t> pcm_bytes = to_bytes (pcm, pcm + sample_count * channels);
  msg->set_pcm (pcm_bytes.data (), pcm_bytes.size ());

  msg->set_channels (channels);
  msg->set_sampling_rate (sampling_rate);
}


static void
tox4j_video_receive_frame_cb (uint32_t friend_number,
                              uint16_t width, uint16_t height,
                              uint8_t const *y, uint8_t const *u, uint8_t const *v,
                              int32_t ystride, int32_t ustride, int32_t vstride,
                              Events *events)
{
  assert (ystride < 0 == ustride < 0);
  assert (ystride < 0 == vstride < 0);

  auto msg = events->add_video_receive_frame ();
  msg->set_friend_number (friend_number);
  msg->set_width (width);
  msg->set_height (height);
  msg->set_y (y, std::max<std::size_t> (width    , std::abs (ystride)) * height);
  msg->set_u (u, std::max<std::size_t> (width / 2, std::abs (ustride)) * (height / 2));
  msg->set_v (v, std::max<std::size_t> (width / 2, std::abs (vstride)) * (height / 2));
  msg->set_y_stride (ystride);
  msg->set_u_stride (ustride);
  msg->set_v_stride (vstride);
}


static tox::av_ptr
toxav_new_unique (Tox *tox, TOXAV_ERR_NEW *error)
{
  return tox::av_ptr (toxav_new (tox, error));
}


static void
toxav_finalize ()
{
  assert (!"This function is only here for register_funcs and should never be called.");
}


REGISTER_FUNCS (
#define JAVA_METHOD_REF(x)
#define CXX_FUNCTION_REF(func)  REGISTER_FUNC (func),
#include "generated/natives.h"
#undef CXX_FUNCTION_REF
#undef JAVA_METHOD_REF

#define CALLBACK(NAME)          REGISTER_FUNC (tox4j_##NAME##_cb),
#include "tox/generated/av.h"
#undef CALLBACK

  REGISTER_FUNC (toxav_new_unique)
);


/*
 * Class:     im_tox_tox4j_impl_ToxAvJni
 * Method:    toxavNew
 * Signature: (ZZILjava/lang/String;I)I
 */
TOX_METHOD (jint, New,
  jint toxInstanceNumber)
{
  return core::instances.with_instance (env, toxInstanceNumber,
    [=] (Tox *tox, core::Events &)
      {
        return instances.with_error_handling (env,
          [env] (tox::av_ptr toxav)
            {
              tox4j_assert (toxav != nullptr);

              // Create the master events object and set up our callbacks.
              auto events = tox::callbacks<ToxAV> (std::make_unique<Events> ())
#define CALLBACK(NAME)   .set<tox::callback_##NAME, tox4j_##NAME##_cb> ()
#include "tox/generated/av.h"
#undef CALLBACK
                .set (toxav.get ());

              // We can create the new instance outside instance_manager's critical section.
              // This call locks the instance manager.
              return instances.add (
                env,
                std::move (toxav),
                std::move (events)
              );
            },
          toxav_new_unique, tox
        );
      }
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxAvJni
 * Method:    toxavKill
 * Signature: (I)I
 */
TOX_METHOD (void, Kill,
  jint instanceNumber)
{
  instances.kill (env, instanceNumber);
}

/*
 * Class:     im_tox_tox4j_impl_ToxAvJni
 * Method:    toxavFinalize
 * Signature: (I)V
 */
TOX_METHOD (void, Finalize,
  jint instanceNumber)
{
  instances.finalize (env, instanceNumber);
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxAvJni
 * Method:    invokeBitRateStatus
 * Signature: (IIII)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxAvJni_invokeBitRateStatus
  (JNIEnv *env, jclass, jint instanceNumber, jint friendNumber, jint audioBitRate, jint videoBitRate)
{
  return instances.with_instance (env, instanceNumber,
    [=] (ToxAV *av, Events &events)
      {
        assert (av != nullptr);
        tox4j_bit_rate_status_cb (friendNumber, audioBitRate, videoBitRate, &events);
      }
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxAvJni
 * Method:    invokeAudioReceiveFrame
 * Signature: (II[SII)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxAvJni_invokeAudioReceiveFrame
  (JNIEnv *env, jclass, jint instanceNumber, jint friendNumber, jshortArray pcm, jint channels, jint samplingRate)
{
  return instances.with_instance (env, instanceNumber,
    [=] (ToxAV *av, Events &events)
      {
        assert (av != nullptr);
        auto pcmData = fromJavaArray (env, pcm);
        tox4j_assert (pcmData.size () % channels == 0);
        tox4j_audio_receive_frame_cb (friendNumber, pcmData.data (), pcmData.size () / channels, channels, samplingRate, &events);
      }
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxAvJni
 * Method:    invokeCall
 * Signature: (IIZZ)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxAvJni_invokeCall
  (JNIEnv *env, jclass, jint instanceNumber, jint friendNumber, jboolean audioEnabled, jboolean videoEnabled)
{
  return instances.with_instance (env, instanceNumber,
    [=] (ToxAV *av, Events &events)
      {
        assert (av != nullptr);
        tox4j_call_cb (friendNumber, audioEnabled, videoEnabled, &events);
      }
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxAvJni
 * Method:    invokeCallState
 * Signature: (IILjava/util/Collection;)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxAvJni_invokeCallState
  (JNIEnv *env, jclass, jint instanceNumber, jint friendNumber, jint callState)
{
  return instances.with_instance (env, instanceNumber,
    [=] (ToxAV *av, Events &events)
      {
        assert (av != nullptr);
        tox4j_call_state_cb (friendNumber, callState, &events);
      }
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxAvJni
 * Method:    invokeVideoReceiveFrame
 * Signature: (IIII[B[B[B[BIIII)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxAvJni_invokeVideoReceiveFrame
  (JNIEnv *env, jclass, jint instanceNumber, jint friendNumber, jint width, jint height, jbyteArray y, jbyteArray u, jbyteArray v, jint yStride, jint uStride, jint vStride)
{
  return instances.with_instance (env, instanceNumber,
    [=] (ToxAV *av, Events &events)
      {
        assert (av != nullptr);
        auto yData = fromJavaArray (env, y);
        auto uData = fromJavaArray (env, u);
        auto vData = fromJavaArray (env, v);
        tox4j_assert (yData.size () == std::max<std::size_t> (width    , std::abs (yStride)) * height);
        tox4j_assert (uData.size () == std::max<std::size_t> (width / 2, std::abs (uStride)) * (height / 2));
        tox4j_assert (vData.size () == std::max<std::size_t> (width / 2, std::abs (vStride)) * (height / 2));
        tox4j_video_receive_frame_cb (friendNumber, width, height, yData.data (), uData.data (), vData.data (), yStride, uStride, vStride, &events);
      }
  );
}

#endif
