package im.tox.tox4j.av.callbacks

import java.util

import im.tox.tox4j.av.data._
import im.tox.tox4j.av.enums.ToxavFriendCallState
import im.tox.tox4j.core.data.ToxFriendNumber

trait ToxAvEventSynth {

  def invokeAudioReceiveFrame(friendNumber: ToxFriendNumber, pcm: Array[Short], channels: AudioChannels, samplingRate: SamplingRate): Unit
  def invokeBitRateStatus(friendNumber: ToxFriendNumber, audioBitRate: BitRate, videoBitRate: BitRate): Unit
  def invokeCall(friendNumber: ToxFriendNumber, audioEnabled: Boolean, videoEnabled: Boolean): Unit
  def invokeCallState(friendNumber: ToxFriendNumber, callState: util.EnumSet[ToxavFriendCallState]): Unit
  def invokeVideoReceiveFrame(friendNumber: ToxFriendNumber, width: Width, height: Height, y: Array[Byte], u: Array[Byte], v: Array[Byte], yStride: Int, uStride: Int, vStride: Int): Unit // scalastyle:ignore line.size.limit

}
