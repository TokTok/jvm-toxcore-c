package im.tox.tox4j.av.callbacks

import im.tox.tox4j.av.data.{AudioChannels, SamplingRate}
import im.tox.tox4j.core.data.ToxFriendNumber
import org.jetbrains.annotations.NotNull

/**
 * Called when an audio frame is received.
 */
trait AudioReceiveFrameCallback[ToxCoreState] {
  /**
   * @param friendNumber The friend number of the friend who sent an audio frame.
   * @param pcm An array of audio samples (sample_count * channels elements).
   * @param channels Number of audio channels.
   * @param samplingRate Sampling rate used in this frame.
   */
  def audioReceiveFrame(
    friendNumber: ToxFriendNumber, @NotNull pcm: Array[Short], channels: AudioChannels, samplingRate: SamplingRate
  )(state: ToxCoreState): ToxCoreState = state
}
