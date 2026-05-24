package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.enums.ToxConnection

interface SelfConnectionStatusCallback<ToxCoreState> {
    /** @param connectionStatus Whether we are connected to the DHT. */
    fun selfConnectionStatus(
        connectionStatus: ToxConnection,
        state: ToxCoreState,
    ): ToxCoreState = state
}
