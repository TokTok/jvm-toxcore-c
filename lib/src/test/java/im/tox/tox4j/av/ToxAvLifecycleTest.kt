package im.tox.tox4j.av

import im.tox.tox4j.av.callbacks.ToxAvEventListener
import im.tox.tox4j.core.options.ToxOptions
import im.tox.tox4j.exceptions.ToxKilledException
import im.tox.tox4j.impl.jni.ToxAvImpl
import im.tox.tox4j.impl.jni.ToxCoreImpl
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ToxAvLifecycleTest {
    private val options = ToxOptions(localDiscoveryEnabled = false)

    private object Noop : ToxAvEventListener<Int>

    @Test
    fun construct_succeeds() {
        ToxCoreImpl(options).use { tox ->
            val av = ToxAvImpl(tox)
            av.close()
        }
    }

    @Test
    fun construct_thenUseAsAutoCloseable() {
        ToxCoreImpl(options).use { tox ->
            ToxAvImpl(tox).use { }
        }
    }

    @Test
    fun secondAv_afterFirstClosed_succeeds() {
        ToxCoreImpl(options).use { tox ->
            ToxAvImpl(tox).use { }
            ToxAvImpl(tox).use { }
        }
    }

    @Test
    fun iterate_noOp_returnsState() {
        ToxCoreImpl(options).use { tox ->
            ToxAvImpl(tox).use { av ->
                assertEquals(99, av.iterate(Noop, 99))
            }
        }
    }

    @Test
    fun iterate_afterClose_throws() {
        ToxCoreImpl(options).use { tox ->
            val av = ToxAvImpl(tox)
            av.close()
            assertFailsWith<ToxKilledException> { av.iterate(Noop, 0) }
        }
    }

    @Test
    fun iterationInterval_isPositive() {
        ToxCoreImpl(options).use { tox ->
            ToxAvImpl(tox).use { av ->
                assertTrue(av.iterationInterval > 0)
            }
        }
    }

    @Test
    fun iterationInterval_afterClose_throws() {
        ToxCoreImpl(options).use { tox ->
            val av = ToxAvImpl(tox)
            av.close()
            assertFailsWith<ToxKilledException> { av.iterationInterval }
        }
    }
}
