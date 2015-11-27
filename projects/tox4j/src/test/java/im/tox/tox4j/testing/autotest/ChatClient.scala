package im.tox.tox4j.testing.autotest

import com.typesafe.scalalogging.Logger
import im.tox.tox4j.core._
import im.tox.tox4j.core.callbacks.ToxEventAdapter
import im.tox.tox4j.core.data.{ToxFriendAddress, ToxPublicKey}
import im.tox.tox4j.core.enums.ToxConnection
import im.tox.tox4j.exceptions.ToxException
import im.tox.tox4j.testing.GetDisjunction._
import org.slf4j.LoggerFactory

import scala.annotation.tailrec

final case class ChatStateT[T](
    state: T,
    tasks: Seq[((ToxCore[ChatStateT[T]], ChatStateT[T]) => ChatStateT[T], Array[StackTraceElement])] = Nil,
    chatting: Boolean = true
) {

  private def assembleStackTrace(e: ToxException[_], creationTrace: Array[StackTraceElement]): ToxException[_] = {
    // The stack until the performTasks method call.
    val untilPerformTasks = e.getStackTrace
      .reverse
      .dropWhile { callSite =>
        !((callSite.getClassName == classOf[ChatClientT[_]].getName) &&
          (callSite.getMethodName == "performTasks"))
      }
      .reverse

    // After that, add the task creation trace, minus the "addTask" method.
    val trace = untilPerformTasks ++ creationTrace

    // Put the assembled trace into the exception and return it.
    e.setStackTrace(trace)

    e
  }

  private[autotest] def runTasks(tox: ToxCore[ChatStateT[T]]): ChatStateT[T] = {
    tasks.reverse.foldLeft(copy[T](tasks = Nil)) {
      case (nextState, (task, stacktrace)) =>
        try {
          task(tox, nextState)
        } catch {
          case e: ToxException[_] =>
            throw assembleStackTrace(e, stacktrace)
        }
    }
  }

  def addTask(task: (ToxCore[ChatStateT[T]], ChatStateT[T]) => ChatStateT[T]): ChatStateT[T] = {
    val creationTrace = Thread.currentThread.getStackTrace
    copy[T](tasks = (task, creationTrace.slice(2, creationTrace.length)) +: tasks)
  }

  def finish: ChatStateT[T] = {
    copy[T](chatting = false)
  }

  def get: T = state

  def set(value: T): ChatStateT[T] = {
    copy[T](state = value)
  }

  def map(f: T => T): ChatStateT[T] = {
    copy[T](state = f(state))
  }

  def flatMap(f: T => ChatStateT[T]): ChatStateT[T] = {
    f(state)
  }

}

class ChatClientT[T](val selfName: String, val expectedFriendName: String) extends ToxEventAdapter[ChatStateT[T]] {

  private val logger = Logger(LoggerFactory.getLogger(getOuterClass(getClass)))

  @tailrec
  private def getOuterClass(clazz: Class[_]): Class[_] = {
    Option(clazz.getEnclosingClass) match {
      case None            => clazz
      case Some(enclosing) => getOuterClass(enclosing)
    }
  }

  protected def debug(message: String): Unit = {
    logger.info(s"[${Thread.currentThread.getId}] $selfName: $message")
  }

  var expectedFriendAddress = ToxFriendAddress.unsafeFromValue(null)
  protected def expectedFriendPublicKey: ToxPublicKey = {
    ToxPublicKey.fromValue(expectedFriendAddress.value.slice(0, ToxPublicKey.Size)).get
  }

  protected def isAlice = selfName == "Alice"
  protected def isBob = selfName == "Bob"

  def setup(tox: ToxCore[ChatStateT[T]])(state: ChatStateT[T]): ChatStateT[T] = state

  override def selfConnectionStatus(connectionStatus: ToxConnection)(state: ChatStateT[T]): ChatStateT[T] = {
    if (connectionStatus != ToxConnection.NONE) {
      debug("is now connected to the network with " + connectionStatus)
    } else {
      debug("is now disconnected from the network")
    }
    state
  }

  override def friendConnectionStatus(friendNumber: Int, connection: ToxConnection)(state: ChatStateT[T]): ChatStateT[T] = {
    assert(friendNumber == AliceBobTestBase.FriendNumber)
    if (connection != ToxConnection.NONE) {
      debug(s"is now connected to friend $friendNumber with " + connection)
    } else {
      debug(s"is now disconnected from friend $friendNumber")
    }
    state
  }

}
