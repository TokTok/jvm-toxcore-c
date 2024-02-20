package im.tox.tox4j.impl.jni

import im.tox.tox4j.av.*
import im.tox.tox4j.av.callbacks.*
import im.tox.tox4j.av.data.*
import im.tox.tox4j.av.enums.*
import im.tox.tox4j.av.exceptions.*
import im.tox.tox4j.core.ToxCore
import im.tox.tox4j.core.data.ToxFriendNumber
import java.util.EnumSet

/**
 * Initialise an A/V session for the existing Tox instance.
 *
 * @param tox An instance of the C-backed ToxCore implementation.
 */
// @throws[ToxavNewException]("If there was already an A/V session.")
final class ToxAvImpl(private val tox: ToxCoreImpl) : ToxAv {

  internal val instanceNumber = ToxAvJni.toxavNew(tox.instanceNumber)

  override fun create(tox: ToxCore): ToxAv =
      try {
        ToxAvImpl(tox as ToxCoreImpl)
      } catch (_: ClassCastException) {
        throw ToxavNewException(
            ToxavNewException.Code.INCOMPATIBLE, tox::class.java.getCanonicalName())
      }

  override fun close(): Unit = ToxAvJni.toxavKill(instanceNumber)

  protected fun finalize(): Unit = ToxAvJni.toxavFinalize(instanceNumber)

  override fun <S> iterate(handler: ToxAvEventListener<S>, state: S): S =
      ToxAvEventDispatch.dispatch(handler, ToxAvJni.toxavIterate(instanceNumber), state)

  override val iterationInterval: Int
    get() = ToxAvJni.toxavIterationInterval(instanceNumber)

  // @throws[ToxavCallException]
  override fun call(
      friendNumber: ToxFriendNumber,
      audioBitRate: BitRate,
      videoBitRate: BitRate
  ): Unit =
      ToxAvJni.toxavCall(instanceNumber, friendNumber.value, audioBitRate.value, videoBitRate.value)

  // @throws[ToxavAnswerException]
  override fun answer(
      friendNumber: ToxFriendNumber,
      audioBitRate: BitRate,
      videoBitRate: BitRate
  ): Unit =
      ToxAvJni.toxavAnswer(
          instanceNumber, friendNumber.value, audioBitRate.value, videoBitRate.value)

  // @throws[ToxavCallControlException]
  override fun callControl(friendNumber: ToxFriendNumber, control: ToxavCallControl): Unit =
      ToxAvJni.toxavCallControl(instanceNumber, friendNumber.value, control.ordinal)

  // @throws[ToxavBitRateSetException]
  override fun setAudioBitRate(friendNumber: ToxFriendNumber, audioBitRate: BitRate): Unit =
      ToxAvJni.toxavAudioSetBitRate(instanceNumber, friendNumber.value, audioBitRate.value)

  // @throws[ToxavBitRateSetException]
  override fun setVideoBitRate(friendNumber: ToxFriendNumber, videoBitRate: BitRate): Unit =
      ToxAvJni.toxavVideoSetBitRate(instanceNumber, friendNumber.value, videoBitRate.value)

  // @throws[ToxavSendFrameException]
  override fun audioSendFrame(
      friendNumber: ToxFriendNumber,
      pcm: ShortArray,
      sampleCount: SampleCount,
      channels: AudioChannels,
      samplingRate: SamplingRate
  ): Unit =
      ToxAvJni.toxavAudioSendFrame(
          instanceNumber,
          friendNumber.value,
          pcm,
          sampleCount.value,
          channels.value,
          samplingRate.value)

  // @throws[ToxavSendFrameException]
  override fun videoSendFrame(
      friendNumber: ToxFriendNumber,
      width: Int,
      height: Int,
      y: ByteArray,
      u: ByteArray,
      v: ByteArray
  ): Unit = ToxAvJni.toxavVideoSendFrame(instanceNumber, friendNumber.value, width, height, y, u, v)

  fun invokeAudioReceiveFrame(
      friendNumber: ToxFriendNumber,
      pcm: ShortArray,
      channels: AudioChannels,
      samplingRate: SamplingRate
  ): Unit =
      ToxAvJni.invokeAudioReceiveFrame(
          instanceNumber, friendNumber.value, pcm, channels.value, samplingRate.value)

  fun invokeAudioBitRate(friendNumber: ToxFriendNumber, audioBitRate: BitRate): Unit =
      ToxAvJni.invokeAudioBitRate(instanceNumber, friendNumber.value, audioBitRate.value)

  fun invokeVideoBitRate(friendNumber: ToxFriendNumber, videoBitRate: BitRate): Unit =
      ToxAvJni.invokeVideoBitRate(instanceNumber, friendNumber.value, videoBitRate.value)

  fun invokeCall(
      friendNumber: ToxFriendNumber,
      audioEnabled: Boolean,
      videoEnabled: Boolean
  ): Unit = ToxAvJni.invokeCall(instanceNumber, friendNumber.value, audioEnabled, videoEnabled)

  fun invokeCallState(
      friendNumber: ToxFriendNumber,
      callState: EnumSet<ToxavFriendCallState>
  ): Unit =
      ToxAvJni.invokeCallState(
          instanceNumber, friendNumber.value, ToxAvEventDispatch.convert(callState))

  fun invokeVideoReceiveFrame(
      friendNumber: ToxFriendNumber,
      width: Width,
      height: Height,
      y: ByteArray,
      u: ByteArray,
      v: ByteArray,
      yStride: Int,
      uStride: Int,
      vStride: Int
  ): Unit =
      ToxAvJni.invokeVideoReceiveFrame(
          instanceNumber,
          friendNumber.value,
          width.value,
          height.value,
          y,
          u,
          v,
          yStride,
          uStride,
          vStride)
}
