package im.tox.tox4j.testing.autotest

import com.typesafe.scalalogging.Logger
import im.tox.tox4j.OptimisedIdOps._
import im.tox.tox4j.av.callbacks.ToxAvEventListener
import im.tox.tox4j.av.{ToxAv, ToxAvFactory}
import im.tox.tox4j.core.callbacks.ToxCoreEventListener
import im.tox.tox4j.core.options.ToxOptions
import im.tox.tox4j.core.{ToxCore, ToxCoreFactory}
import im.tox.tox4j.testing.autotest.AutoTest._
import im.tox.tox4j.testing.autotest.AutoTestSuite.timed
import org.slf4j.LoggerFactory

import scala.annotation.tailrec

object AutoTest {

  type Core[S] = ToxCore[ClientState[S]]
  type Av[S] = ToxAv[ClientState[S]]
  type Task[S] = (Core[S], Av[S], ClientState[S]) => ClientState[S]

  /**
   * A participant in the test network. These are unique across all instances,
   * so they are useful for logging and to put oneself into relation with other
   * instances using the instance map [[ClientState.friendList]].
   */
  final case class ParticipantId(private val value: Int) extends AnyVal {
    def prev: ParticipantId = copy(value - 1)
    def next: ParticipantId = copy(value + 1)
    override def toString: String = s"#$value"
  }

  final case class ClientState[S](
      id: ParticipantId,
      friendList: Map[Int, ParticipantId],
      state: S,
      tasks: List[(Int, Task[S])] = Nil,
      running: Boolean = true
  ) {

    def finish: ClientState[S] = {
      copy(running = false)
    }

    private[AutoTest] def updateTasks(interval: Int, tasks: List[(Int, Task[S])]): ClientState[S] = {
      copy(tasks = tasks.map {
        case (delay, task) => (delay - interval, task)
      })
    }

    def addTask(task: Task[S]): ClientState[S] = {
      copy(tasks = (0, task) :: tasks)
    }

    def addTask(delay: Int)(task: Task[S]): ClientState[S] = {
      copy(tasks = (delay, task) :: tasks)
    }

    def get: S = state
    def put(state: S): ClientState[S] = copy(state = state)

    def modify(f: S => S): ClientState[S] = {
      copy(state = f(state))
    }

    def id(friendNumber: Int): ParticipantId = {
      friendList(friendNumber)
    }

  }

  final case class Participant[S](
    tox: Core[S],
    av: Av[S],
    state: ClientState[S]
  )

  abstract class EventListener[S]
      extends ToxCoreEventListener[ClientState[S]]
      with ToxAvEventListener[ClientState[S]] {
    type State = ClientState[S]
    def initial: S
  }

}

final case class AutoTest(
    coreFactory: ToxCoreFactory,
    avFactory: ToxAvFactory
) {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  private def performTasks[S](
    tox: Core[S],
    av: Av[S],
    interval: Int
  )(state: ClientState[S]): ClientState[S] = {
    val (delayed, runnable) = state.tasks.partition(_._1 >= 0)
    logger.trace(s"Running tasks: ${runnable.size} runnable, ${delayed.size} delayed")

    runnable.foldRight(state.updateTasks(interval, delayed)) { (task, state) =>
      assert(task._1 <= 0)
      task._2(tox, av, state)
    }
  }

  @tailrec
  private def mainLoop[S](clients: List[Participant[S]], iteration: Int = 0): List[S] = {
    val interval = (clients.map(_.tox.iterationInterval) ++ clients.map(_.av.iterationInterval)).min
    assert(interval > 0)

    val (iterationTime, nextClients) = timed {
      clients.map {
        case Participant(tox, av, state) =>
          Participant(
            tox, av,
            state
              |> tox.iterate
              |> av.iterate
              |> performTasks(tox, av, interval)
          )
      }
    }

    val sleepTime = (interval - iterationTime) max 0
    logger.trace(s"Iteration $iteration, interval=$interval, iterationTime=$iterationTime, sleepTime=$sleepTime")
    Thread.sleep(sleepTime)

    if (nextClients.exists(_.state.running)) {
      mainLoop(nextClients, iteration + 1)
    } else {
      nextClients.map(_.state.state)
    }
  }

  def run[S](
    count: Int,
    options: ToxOptions,
    handler: EventListener[S]
  ): List[S] = {
    coreFactory.withToxN[ClientState[S], List[S]](count, options) { toxes =>
      avFactory.withToxAvN[ClientState[S], List[S]](toxes) { avs =>
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
