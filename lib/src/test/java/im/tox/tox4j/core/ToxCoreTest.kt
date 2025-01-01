package im.tox.tox4j.core

import im.tox.tox4j.core.callbacks.ToxCoreEventListener
import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.core.enums.ToxConnection
import im.tox.tox4j.core.exceptions.ToxBootstrapException
import im.tox.tox4j.core.options.ToxOptions
import kotlinx.coroutines.delay
import kotlin.test.Test
import kotlin.test.assertFailsWith

@kotlin.ExperimentalStdlibApi
class ToxCoreTest {
    @Test
    fun bootstrap_withWrongHost_shouldFail() =
        runTox {
            val tox = newToxCore(ToxOptions())
            assertFailsWith<ToxBootstrapException> {
                tox.bootstrap("host-does-not-exist", tox.getUdpPort, tox.getDhtId)
            }
        }

    @Test
    fun addFriendNorequest_shouldConnectTwoToxes() =
        runTox {
            val tox1 = newToxCore(ToxOptions())
            val tox2 = newToxCore(ToxOptions())

            tox2.bootstrap("localhost", tox1.getUdpPort, tox1.getDhtId)

            tox1.addFriendNorequest(tox2.getPublicKey)
            tox2.addFriendNorequest(tox1.getPublicKey)

            var connected1 = false
            var connected2 = false

            val isConnected =
                object : ToxCoreEventListener<Boolean> {
                    override fun friendConnectionStatus(
                        friendNumber: ToxFriendNumber,
                        connectionStatus: ToxConnection,
                        state: Boolean,
                    ) = connectionStatus != ToxConnection.NONE
                }

            while (!connected1 && !connected2) {
                connected1 = tox1.iterate(isConnected, connected1)
                connected2 = tox2.iterate(isConnected, connected2)
                delay(tox1.iterationInterval.toLong())
            }
        }
}
