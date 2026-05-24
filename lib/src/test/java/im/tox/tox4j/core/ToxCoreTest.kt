package im.tox.tox4j.core

import im.tox.tox4j.core.callbacks.ToxCoreEventListener
import im.tox.tox4j.core.data.ToxDhtId
import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.core.data.ToxPort
import im.tox.tox4j.core.enums.ToxConnection
import im.tox.tox4j.core.exceptions.ToxBootstrapException
import im.tox.tox4j.core.options.ToxOptions
import kotlinx.coroutines.delay
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ToxCoreTest {
    private val options = ToxOptions(localDiscoveryEnabled = false)

    @Test
    fun bootstrap_withWrongHost_shouldFail() =
        runTox {
            val tox = newToxCore(options)
            val ex =
                assertFailsWith<ToxBootstrapException> {
                    tox.bootstrap("host-does-not-exist", tox.udpPort, tox.dhtId)
                }
            // c-toxcore reports an unresolvable host as BAD_HOST.
            assertEquals(ToxBootstrapException.Code.BAD_HOST, ex.code)
        }

    @Test
    fun bootstrap_withCorrectHost_shouldSucceed() =
        runTox {
            val tox = newToxCore(options)
            tox.bootstrap(
                "tox.abilinski.com",
                ToxPort(33445),
                ToxDhtId(fromHex("10C00EB250C3233E343E2AEBA07115A5C28920E9C8D29492F6D00B29049EDC7E")),
            )

            var connected = false

            val isConnected =
                object : ToxCoreEventListener<Boolean> {
                    override fun selfConnectionStatus(
                        connectionStatus: ToxConnection,
                        state: Boolean,
                    ) = connectionStatus != ToxConnection.NONE
                }

            while (!connected) {
                connected = tox.iterate(isConnected, connected)
                delay(tox.iterationInterval.toLong())
            }
        }

    @Test
    fun addFriendNorequest_shouldConnectTwoToxes() =
        runTox {
            val tox1 = newToxCore(options)
            val tox2 = newToxCore(options)

            tox2.bootstrap("localhost", tox1.udpPort, tox1.dhtId)

            tox1.friendAddNorequest(tox2.publicKey)
            tox2.friendAddNorequest(tox1.publicKey)

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

    private companion object {
        fun fromHex(hex: String): ByteArray {
            val bytes = ByteArray(hex.length / 2)
            for (i in bytes.indices) {
                bytes[i] = hex.substring(i * 2, i * 2 + 2).toInt(16).toByte()
            }
            return bytes
        }
    }
}
