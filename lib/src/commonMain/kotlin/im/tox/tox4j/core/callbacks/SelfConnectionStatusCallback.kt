package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.enums.ToxConnection

/**
 * This event is triggered whenever there is a change in the DHT connection state. When
 * disconnected, a client may choose to call tox_bootstrap again, to reconnect to the DHT. Note that
 * this state may frequently change for short amounts of time. Clients should therefore not
 * immediately bootstrap on receiving a disconnect.
 *
 * TODO(iphydf): how long should a client wait before bootstrapping again?
 */
interface SelfConnectionStatusCallback<ToxCoreState> {
    /** @param connectionStatus Whether we are connected to the DHT. */
    fun selfConnectionStatus(
        connectionStatus: ToxConnection,
        state: ToxCoreState,
    ): ToxCoreState = state
}
