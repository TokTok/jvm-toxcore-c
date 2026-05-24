package im.tox.tox4j.av.callbacks

import im.tox.tox4j.av.data.ToxAvChannels
import im.tox.tox4j.av.data.ToxAvSamplingRate
import im.tox.tox4j.core.data.ToxFriendNumber

interface AudioReceiveFrameCallback<ToxCoreState> {
    /**
     * The function type for the audio_receive_frame callback. The callback can be called multiple times per single iteration depending on the amount of queued frames in the buffer. The received format is the same as in send function.
     *
     * @param friendNumber The friend number of the friend who sent an audio frame.
     * @param pcm An array of audio samples (`sample_count * channels` elements).
     * @param sampleCount The number of audio samples per channel in the PCM array.
     * @param channels Number of audio channels.
     * @param samplingRate Sampling rate used in this frame.
     */
    fun audioReceiveFrame(
        friendNumber: ToxFriendNumber,
        pcm: ShortArray,
        sampleCount: Long,
        channels: ToxAvChannels,
        samplingRate: ToxAvSamplingRate,
        state: ToxCoreState,
    ): ToxCoreState = state
}
