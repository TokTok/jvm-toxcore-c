package im.tox.tox4j.av.callbacks

import im.tox.tox4j.av.data.ToxAvBitRate
import im.tox.tox4j.core.data.ToxFriendNumber

interface AudioBitRateCallback<ToxCoreState> {
    /**
     * The function type for the audio_bit_rate callback. The event is triggered when the network becomes too saturated for current bit rates at which point ToxAV suggests new bit rates.
     *
     * @param friendNumber The friend number of the friend for which to set the bit rate.
     * @param audioBitRate Suggested maximum audio bit rate in kbit/sec.
     */
    fun audioBitRate(
        friendNumber: ToxFriendNumber,
        audioBitRate: ToxAvBitRate,
        state: ToxCoreState,
    ): ToxCoreState = state
}
