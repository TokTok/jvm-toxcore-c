package im.tox.tox4j.core

import im.tox.tox4j.core.options.ToxOptions
import im.tox.tox4j.impl.jni.ToxCoreImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

@kotlin.ExperimentalStdlibApi
private class TestContext : CoroutineContext.Element {
    private val list: MutableList<ToxCore> = mutableListOf()

    override val key = TestContext

    companion object : CoroutineContext.Key<TestContext> {
        suspend fun add(makeTox: () -> ToxCore): ToxCore {
            val ctx = coroutineContext.get(TestContext)
            if (ctx == null) {
                throw IllegalStateException("coroutine context has no TestContext object")
            }
            val tox = makeTox()
            ctx.list.add(tox)
            return tox
        }

        suspend fun close() {
            val ctx = coroutineContext.get(TestContext)
            if (ctx == null) {
                throw IllegalStateException("coroutine context has no TestContext object")
            }
            for (tox in ctx.list) {
                tox.close()
            }
        }
    }
}

@kotlin.ExperimentalStdlibApi
suspend fun newToxCore(options: ToxOptions): ToxCore = TestContext.add { ToxCoreImpl(options) }

@kotlin.ExperimentalStdlibApi
fun runTox(block: suspend CoroutineScope.() -> Unit): Unit =
    runBlocking(TestContext()) {
        try {
            block()
        } finally {
            TestContext.close()
        }
    }
