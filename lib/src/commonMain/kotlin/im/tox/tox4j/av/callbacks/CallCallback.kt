package im.tox.tox4j.av.callbacks

import im.tox.tox4j.core.data.ToxFriendNumber

/** Triggered when a friend calls us. */
interface CallCallback<ToxCoreState> {
    /**
     * @param friendNumber The friend number from which the call is incoming.
     * @param audioEnabled True if friend is sending audio.
     * @param videoEnabled True if friend is sending video.
     */
    fun call(
        friendNumber: ToxFriendNumber,
        audioEnabled: Boolean,
        videoEnabled: Boolean,
        state: ToxCoreState,
    ): ToxCoreState = state
}
