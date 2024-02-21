package im.tox.tox4j.core

import im.tox.tox4j.core.callbacks.ToxCoreEventListener
import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.core.enums.ToxConnection
import im.tox.tox4j.core.options.ToxOptions
import im.tox.tox4j.impl.jni.ToxCoreImpl
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.test.Test
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class ToxCoreTest {
  private class Toxes : CoroutineContext.Element {
    private val list: MutableList<ToxCore> = mutableListOf()

    override val key = Toxes

    companion object : CoroutineContext.Key<Toxes> {
      suspend fun add(makeTox: () -> ToxCore): ToxCore {
        val ctx = coroutineContext.get(Toxes)
        if (ctx == null) {
          throw IllegalStateException("coroutine context has no Toxes object")
        }
        val tox = makeTox()
        ctx.list.add(tox)
        return tox
      }

      suspend fun close(): Unit {
        val ctx = coroutineContext.get(Toxes)
        if (ctx == null) {
          throw IllegalStateException("coroutine context has no Toxes object")
        }
        for (tox in ctx.list) {
          tox.close()
        }
      }
    }
  }

  private suspend fun newToxCore(options: ToxOptions): ToxCore = Toxes.add { ToxCoreImpl(options) }

  private fun runTox(block: suspend CoroutineScope.() -> Unit): Unit =
      runBlocking(Toxes()) {
        try {
          block()
        } finally {
          Toxes.close()
        }
      }

  @Test
  fun addFriendNorequest_shouldConnectTwoToxes() = runTox {
    val tox1 = newToxCore(ToxOptions())
    val tox2 = newToxCore(ToxOptions())

    tox2.bootstrap("localhost", tox1.getUdpPort, tox1.getDhtId)

    tox1.addFriendNorequest(tox2.getPublicKey)
    tox2.addFriendNorequest(tox1.getPublicKey)

    var connected1 = false
    var connected2 = false

    while (!connected1 && !connected2) {
      connected1 =
          tox1.iterate(
              object : ToxCoreEventListener<Boolean> {
                override fun friendConnectionStatus(
                    friendNumber: ToxFriendNumber,
                    connectionStatus: ToxConnection,
                    state: Boolean,
                ) = connectionStatus != ToxConnection.NONE
              },
              connected1,
          )
      connected2 =
          tox2.iterate(
              object : ToxCoreEventListener<Boolean> {
                override fun friendConnectionStatus(
                    friendNumber: ToxFriendNumber,
                    connectionStatus: ToxConnection,
                    state: Boolean,
                ) = connectionStatus != ToxConnection.NONE
              },
              connected2,
          )
      delay(tox1.iterationInterval.toLong())
    }
  }
}
