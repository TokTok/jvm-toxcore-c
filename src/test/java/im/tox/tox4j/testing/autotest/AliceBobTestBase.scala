package im.tox.tox4j.testing.autotest

import com.typesafe.scalalogging.Logger
import im.tox.tox4j.av.ToxAv
import im.tox.tox4j.core.ToxCore
import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.testing.ToxTestMixin
import im.tox.tox4j.testing.autotest.AliceBobTestBase.Chatter
import org.scalatest.FunSuite
import org.slf4j.LoggerFactory

import scala.annotation.tailrec
import scalaz.Scalaz._

object AliceBobTestBase {
  val FriendNumber = ToxFriendNumber.fromInt(10).get

  final case class Chatter[T](
    tox: ToxCore,
    av: ToxAv,
    client: ChatClientT[T],
    state: ChatStateT[T]
  )
}

abstract class AliceBobTestBase extends FunSuite with ToxTestMixin {

  protected val logger = Logger(LoggerFactory.getLogger(classOf[AliceBobTestBase]))

  protected type State
  protected type ChatState = ChatStateT[State]
  protected type ChatClient = ChatClientT[State]

  protected def initialState: State

  protected def newChatClient(name: String, expectedFriendName: String): ChatClient

  private def getTopLevelMethod(stackTrace: Seq[StackTraceElement]): String = {
    stackTrace
      .filter(_.getClassName == classOf[AliceBobTest].getName)
      .lastOption
      .fold("<unknown>")(_.getMethodName)
  }

  @tailrec
  private def mainLoop(clients: Seq[Chatter[State]]): Unit = {
    val nextState = clients.map {
      case Chatter(tox, av, client, state) =>
        Chatter[State](tox, av, client, state |> tox.iterate(client) |> (_.runTasks(tox, av)))
    }

    val interval = (nextState.map(_.tox.iterationInterval) ++ nextState.map(_.av.iterationInterval)).min
    Thread.sleep(interval)

    if (nextState.exists(_.state.chatting)) {
      mainLoop(nextState)
    }
  }

  protected def runAliceBobTest(
    withTox: (ToxCore => Unit) => Unit,
    withToxAv: ToxCore => (ToxAv => Unit) => Unit
  ): Unit = {
    val method = getTopLevelMethod(Thread.currentThread.getStackTrace)
    logger.info(s"[${Thread.currentThread.getId}] --- ${getClass.getSimpleName}.$method")

    val aliceChat = newChatClient("Alice", "Bob")
    val bobChat = newChatClient("Bob", "Alice")

    withTox { alice =>
      withTox { bob =>
        withToxAv(alice) { aliceAv =>
          withToxAv(bob) { bobAv =>
            assert(alice ne bob)

            addFriends(alice, AliceBobTestBase.FriendNumber.value)
            addFriends(bob, AliceBobTestBase.FriendNumber.value)

            alice.addFriendNorequest(bob.getPublicKey)
            bob.addFriendNorequest(alice.getPublicKey)

            aliceChat.expectedFriendAddress = bob.getAddress
            bobChat.expectedFriendAddress = alice.getAddress

            val aliceState = aliceChat.setup(alice)(ChatStateT[State](initialState))
            val bobState = bobChat.setup(bob)(ChatStateT[State](initialState))

            mainLoop(Seq(
              Chatter(alice, aliceAv, aliceChat, aliceState),
              Chatter(bob, bobAv, bobChat, bobState)
            ))
          }
        }
      }
    }
  }
}
