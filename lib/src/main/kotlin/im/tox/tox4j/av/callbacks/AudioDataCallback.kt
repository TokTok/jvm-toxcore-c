package im.tox.tox4j.av.callbacks

import im.tox.tox4j.av.data.ToxAvChannels
import im.tox.tox4j.av.data.ToxAvSamplingRate
import im.tox.tox4j.core.data.ToxConferenceNumber
import im.tox.tox4j.core.data.ToxConferencePeerNumber

interface AudioDataCallback<ToxCoreState> {
    /**
     * The AV conference audio callback. Its parameters have the same
     * types as `audioReceiveFrame`'s, under different C names.
     */
    fun audioData(
        conferenceNumber: ToxConferenceNumber,
        peerNumber: ToxConferencePeerNumber,
        pcm: ShortArray,
        sampleCount: Int,
        channels: ToxAvChannels,
        samplingRate: ToxAvSamplingRate,
        state: ToxCoreState,
    ): ToxCoreState = state
}
