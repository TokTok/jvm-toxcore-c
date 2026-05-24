package im.tox.tox4j.e2e

import im.tox.tox4j.core.callbacks.ToxCoreEventListener
import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.core.data.ToxPort
import im.tox.tox4j.core.data.ToxPublicKey
import im.tox.tox4j.core.enums.ToxConnection
import im.tox.tox4j.core.options.ToxOptions
import im.tox.tox4j.exceptions.ToxKilledException
import im.tox.tox4j.impl.jni.ToxCoreImpl
import java.lang.Thread.sleep

/** Loopback toxes for multi-tox scenarios, a port of `rs-toxcore-c/toxcore/tests/suite/setup.rs`. */
class Tox4jHarness(
    private val baseOptions: ToxOptions = ToxOptions(localDiscoveryEnabled = false),
) : AutoCloseable {
    private val managed: MutableList<ToxCoreImpl> = mutableListOf()

    val toxes: List<ToxCoreImpl> get() = managed

    private fun publicKey(i: Int): ToxPublicKey = managed[i].publicKey

    fun addTox() {
        // Port 0: the OS picks a free one.
        managed.add(ToxCoreImpl(baseOptions.copy(startPort = ToxPort(0), endPort = ToxPort(0))))
    }

    /** Mutually befriend toxes `i` and `j`, then have `j` bootstrap off `i`. */
    fun connect(
        i: Int,
        j: Int,
    ) {
        val pkI = publicKey(i)
        val pkJ = publicKey(j)
        managed[i].friendAddNorequest(pkJ)
        managed[j].friendAddNorequest(pkI)

        val dhtKeyI = managed[i].dhtId
        val portI = managed[i].udpPort
        managed[j].addTcpRelay("127.0.0.1", portI, dhtKeyI)
        managed[j].bootstrap("127.0.0.1", portI, dhtKeyI)
    }

    fun <S> iterate(
        handler: ToxCoreEventListener<S>,
        state: S,
    ): S {
        // Else minInterval stays Int.MAX_VALUE and we'd sleep for weeks.
        if (managed.isEmpty()) return state
        var s = state
        var minInterval = Int.MAX_VALUE
        for (tox in managed) {
            s = tox.iterate(handler, s)
            val interval = tox.iterationInterval
            if (interval < minInterval) minInterval = interval
        }
        sleep(minInterval.coerceAtLeast(5).toLong())
        return s
    }

    fun <S> waitFor(
        initial: S,
        handler: ToxCoreEventListener<S>,
        timeoutMs: Long = 30_000L,
        message: String = "waitFor timed out after ${timeoutMs}ms",
        predicate: (S) -> Boolean,
    ): S {
        val deadline = System.currentTimeMillis() + timeoutMs
        var state = initial
        while (System.currentTimeMillis() < deadline) {
            state = iterate(handler, state)
            if (predicate(state)) return state
        }
        throw AssertionError(message)
    }

    fun waitForFriendConnected(
        i: Int,
        j: Int,
        timeoutMs: Long = 30_000L,
    ) {
        val pkJ = publicKey(j)
        val friendNumberJ = managed[i].friendByPublicKey(pkJ)
        val handler =
            object : ToxCoreEventListener<Boolean> {
                override fun friendConnectionStatus(
                    friendNumber: ToxFriendNumber,
                    connectionStatus: ToxConnection,
                    state: Boolean,
                ): Boolean = state || (friendNumber.value == friendNumberJ.value && connectionStatus != ToxConnection.NONE)
            }
        waitFor(
            initial = false,
            handler = handler,
            timeoutMs = timeoutMs,
            message = "tox $i never saw tox $j as connected within ${timeoutMs}ms",
        ) { it }
    }

    override fun close() {
        for (tox in managed) {
            try {
                tox.close()
            } catch (_: ToxKilledException) {
                // The scenario closed it already.
            }
        }
        managed.clear()
    }
}
