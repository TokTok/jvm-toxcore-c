package im.tox.tox4j.av.callbacks

import im.tox.tox4j.core.data.ToxFriendNumber

interface CallCallback<ToxCoreState> {
    /**
     * The function type for the call callback.
     *
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
