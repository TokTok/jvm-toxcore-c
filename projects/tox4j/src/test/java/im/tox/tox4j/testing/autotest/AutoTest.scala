package im.tox.tox4j.testing.autotest

import com.typesafe.scalalogging.Logger
import im.tox.tox4j.av.callbacks.ToxAvEventListener
import im.tox.tox4j.av.{ToxAv, ToxAvFactory}
import im.tox.tox4j.core.callbacks.ToxEventListener
import im.tox.tox4j.core.options.ToxOptions
import im.tox.tox4j.core.{ToxCore, ToxCoreFactory}
import im.tox.tox4j.testing.autotest.AutoTest.{ParticipantId, ClientState, EventListener, Participant}
import org.slf4j.LoggerFactory

import scala.annotation.tailrec
import scalaz.Scalaz.ToIdOps

object AutoTest {

  type Task[ToxCoreState] = (ToxCore[ToxCoreState], ToxAv[ToxCoreState], ToxCoreState) => ToxCoreState

  final case class ParticipantId(private val value: Int) extends AnyVal {
    def prev: ParticipantId = copy(value - 1)
    def next: ParticipantId = copy(value + 1)
    override def toString: String = s"#$value"
  }

  final case class ClientState[ToxCoreState](
      id: ParticipantId,
      friendList: Map[Int, ParticipantId],
      state: ToxCoreState,
      tasks: List[Task[ClientState[ToxCoreState]]] = Nil,
      running: Boolean = true
  ) {

    def finish: ClientState[ToxCoreState] = {
      copy(running = false)
    }

    def addTask(task: Task[ClientState[ToxCoreState]]): ClientState[ToxCoreState] = {
      copy(tasks = task :: tasks)
    }

    def get: ToxCoreState = state
    def put(state: ToxCoreState): ClientState[ToxCoreState] = copy(state = state)

    def modify(f: ToxCoreState => ToxCoreState): ClientState[ToxCoreState] = {
      copy(state = f(state))
    }

    def id(friendNumber: Int): ParticipantId = {
      friendList(friendNumber)
    }

  }

  final case class Participant[ToxCoreState](
    tox: ToxCore[ClientState[ToxCoreState]],
    av: ToxAv[ClientState[ToxCoreState]],
    state: ClientState[ToxCoreState]
  )

  abstract class EventListener[ToxCoreState]
      extends ToxEventListener[ClientState[ToxCoreState]]
      with ToxAvEventListener[ClientState[ToxCoreState]] {
    type State = ClientState[ToxCoreState]
    def initial: ToxCoreState
  }

}

final case class AutoTest(
    coreFactory: ToxCoreFactory,
    avFactory: ToxAvFactory
) {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  private def perform[ToxCoreState](
    tox: ToxCore[ToxCoreState],
    av: ToxAv[ToxCoreState],
    tasks: List[(ToxCore[ToxCoreState], ToxAv[ToxCoreState], ToxCoreState) => ToxCoreState]
  )(state: ToxCoreState): ToxCoreState = {
    tasks match {
      case Nil          => state
      case task :: tail => perform(tox, av, tail)(task(tox, av, state))
    }
  }

  @tailrec
  private def mainLoop[ToxCoreState](clients: List[Participant[ToxCoreState]], iteration: Int = 0): List[ToxCoreState] = {
    val nextClients = clients.map {
      case Participant(tox, av, state) =>
        Participant(
          tox, av,
          state
            |> tox.iterate
            |> av.iterate
            |> (state => perform(tox, av, state.tasks.reverse)(state.copy(tasks = Nil)))
        )
    }

    val interval = (nextClients.map(_.tox.iterationInterval) ++ nextClients.map(_.av.iterationInterval)).min
    logger.trace(s"Iteration $iteration, interval $interval")
    Thread.sleep(interval)

    if (nextClients.exists(_.state.running)) {
      mainLoop(nextClients, iteration + 1)
    } else {
      nextClients.map(_.state.state)
    }
  }

  def run[ToxCoreState](
    count: Int,
    options: ToxOptions,
    handler: EventListener[ToxCoreState]
  ): List[ToxCoreState] = {
    coreFactory.withToxN[ClientState[ToxCoreState], List[ToxCoreState]](count, options) { toxes =>
      avFactory.withToxAvN[ClientState[ToxCoreState], List[ToxCoreState]](toxes) { avs =>
        val states = {
          val avsWithIds =
            for (((tox, av), id) <- avs.zipWithIndex) yield {
              (tox, av, ParticipantId(id))
            }

          for ((tox, av, id) <- avsWithIds) yield {
            // Everybody adds everybody else as friend.
            val friendList =
              for ((friendTox, friendAv, friendId) <- avsWithIds if friendId != id) yield {
                tox.addFriendNorequest(friendTox.getPublicKey) -> friendId
              }
            (tox, av, id, Map(friendList: _*))
          }
        }

        val participants =
          for ((tox, av, id, friendList) <- states) yield {
            logger.debug(s"Participant $id's friends: $friendList")
            assert(!friendList.valuesIterator.contains(id))
            tox.callback(handler)
            av.callback(handler)
            Participant(tox, av, ClientState(id, friendList, handler.initial))
          }

        mainLoop(participants)
      }
    }
  }

}
