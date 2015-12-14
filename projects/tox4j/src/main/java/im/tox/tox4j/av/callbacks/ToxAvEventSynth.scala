package im.tox.tox4j.av.callbacks

import java.util

import im.tox.tox4j.av.data.{BitRate, SamplingRate, AudioChannels}
import im.tox.tox4j.av.enums.ToxavFriendCallState
import im.tox.tox4j.impl.jni.ToxAvJni

trait ToxAvEventSynth {

  def invokeAudioReceiveFrame(friendNumber: Int, pcm: Array[Short], channels: AudioChannels, samplingRate: SamplingRate): Unit
  def invokeBitRateStatus(friendNumber: Int, audioBitRate: BitRate, videoBitRate: BitRate): Unit
  def invokeCall(friendNumber: Int, audioEnabled: Boolean, videoEnabled: Boolean): Unit
  def invokeCallState(friendNumber: Int, callState: util.Collection[ToxavFriendCallState]): Unit
  def invokeVideoReceiveFrame(friendNumber: Int, width: Int, height: Int, y: Array[Byte], u: Array[Byte], v: Array[Byte], yStride: Int, uStride: Int, vStride: Int): Unit // scalastyle:ignore line.size.limit

}
