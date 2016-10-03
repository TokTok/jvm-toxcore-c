package im.tox.tox4j.core

import im.tox.tox4j.core.callbacks.ToxCoreEventListener
import im.tox.tox4j.core.enums.ToxConnection
import im.tox.tox4j.core.exceptions.ToxNewException

import scala.collection.mutable.ArrayBuffer

object ToxList {
  final case class Entry(tox: ToxCore, var connected: ToxConnection)
}

final class ToxList(newTox: () => ToxCore, count: Int) {

  private val handler = new ToxCoreEventListener[ToxList.Entry] {
    override def selfConnectionStatus(connectionStatus: ToxConnection)(state: ToxList.Entry): ToxList.Entry = {
      state.connected = connectionStatus
      state
    }
  }

  private val toxes = {
    val temporary = new ArrayBuffer[ToxCore]
    val instances = try {
      (0 until count) map { i =>
        val instance = ToxList.Entry(newTox(), ToxConnection.NONE)
        temporary += instance.tox
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

  def iterate(): Unit = toxes.foreach(entry => entry.tox.iterate(handler)(entry))

  def iterationInterval: Int = toxes.map(_.tox.iterationInterval).max

  def get(index: Int): ToxCore = toxes(index).tox
  def size: Int = toxes.length

}
