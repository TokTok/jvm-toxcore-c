package im.tox.tox4j.core

import im.tox.tox4j.core.callbacks.ToxEventListener
import im.tox.tox4j.core.enums.ToxConnection
import im.tox.tox4j.core.exceptions.ToxNewException

import scala.collection.mutable.ArrayBuffer

final class ToxList[ToxCoreState](newTox: () => ToxCore[ToxCoreState], count: Int) {

  private final case class Instance(tox: ToxCore[ToxCoreState], var connected: ToxConnection)

  private val toxes = {
    val temporary = new ArrayBuffer[ToxCore[ToxCoreState]]
    val instances = try {
      (0 until count) map { i =>
        val instance = Instance(newTox(), ToxConnection.NONE)
        temporary += instance.tox
        instance.tox.callback(new ToxEventListener[ToxCoreState] {
          override def selfConnectionStatus(connectionStatus: ToxConnection)(state: ToxCoreState): ToxCoreState = {
            instance.connected = connectionStatus
            state
          }
        })
        instance
      }
    } catch {
      case e: ToxNewException =>
        temporary.foreach(_.close())
        throw e
    }

    instances
  }

  def close(): Unit = toxes.foreach(_.tox.close())

  def isAllConnected: Boolean = toxes.forall(_.connected != ToxConnection.NONE)
  def isAnyConnected: Boolean = toxes.exists(_.connected != ToxConnection.NONE)

  def iterate(state: ToxCoreState): Unit = toxes.foreach(_.tox.iterate(state))

  def iterationInterval: Int = toxes.map(_.tox.iterationInterval).max

  def get(index: Int): ToxCore[ToxCoreState] = toxes(index).tox
  def size: Int = toxes.length

}
