package im.tox.tox4j.core

import im.tox.tox4j.core.callbacks.ToxCoreEventListener
import im.tox.tox4j.core.options.ToxOptions
import im.tox.tox4j.exceptions.ToxKilledException
import im.tox.tox4j.impl.jni.ToxCoreImpl
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ToxCoreIterateTest {
    private val options = ToxOptions(localDiscoveryEnabled = false)

    private object Noop : ToxCoreEventListener<Int>

    @Test
    fun iterate_returnsStateUnchanged() {
        ToxCoreImpl(options).use { tox ->
            val result = tox.iterate(Noop, 42)
            assertEquals(42, result)
        }
    }

    @Test
    fun iterate_manyTimes_stable() {
        ToxCoreImpl(options).use { tox ->
            var state = 0
            repeat(50) { state = tox.iterate(Noop, state) }
            assertEquals(0, state)
        }
    }

    @Test
    fun iterate_afterClose_throws() {
        val tox = ToxCoreImpl(options)
        tox.close()
        assertFailsWith<ToxKilledException> { tox.iterate(Noop, 0) }
    }

    @Test
    fun iterationInterval_isPositive() {
        ToxCoreImpl(options).use { tox ->
            assertTrue(tox.iterationInterval > 0)
        }
    }

    @Test
    fun iterationInterval_isBounded() {
        ToxCoreImpl(options).use { tox ->
            // Normally tens of ms. Far outside that means the wrong field was read.
            val interval = tox.iterationInterval
            assertTrue(interval in 1..999, "iterationInterval=$interval out of range")
        }
    }

    @Test
    fun iterationInterval_afterClose_throws() {
        val tox = ToxCoreImpl(options)
        tox.close()
        assertFailsWith<ToxKilledException> { tox.iterationInterval }
    }

    @Test
    fun iterate_withSelfConnectionStatusListener_isInvokedAtMostOnce() {
        // When it's delivered isn't fixed, but with no peers there's at most one call.
        var calls = 0
        val handler =
            object : ToxCoreEventListener<Int> {
                override fun selfConnectionStatus(
                    connectionStatus: im.tox.tox4j.core.enums.ToxConnection,
                    state: Int,
                ): Int {
                    calls += 1
                    return state
                }
            }
        ToxCoreImpl(options).use { tox ->
            tox.iterate(handler, 0)
            assertTrue(calls <= 1, "selfConnectionStatus fired $calls times in one iterate")
        }
    }
}
