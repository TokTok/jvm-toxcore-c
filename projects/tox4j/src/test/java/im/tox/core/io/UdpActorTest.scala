package im.tox.core.io

import java.net.InetSocketAddress

import com.typesafe.scalalogging.Logger
import im.tox.core.crypto.{CryptoCore, PlainText, PublicKey}
import im.tox.core.dht.{NodeInfo, Protocol}
import im.tox.core.network.{NetworkCoreTest, ToxHandler}
import im.tox.tox4j.core.ToxCoreConstants
import im.tox.tox4j.testing.GetDisjunction._
import org.scalatest.FunSuite
import org.slf4j.LoggerFactory

import scala.collection.mutable.ArrayBuffer
import scala.language.postfixOps
import scalaz.concurrent.Task
import scalaz.stream.{Process, Sink, async, udp}

final class UdpActorTest extends FunSuite {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  // TODO(iphydf): https://github.com/scalaz/scalaz-stream/issues/488
  def delayed(process: Process[Task, Unit]): Process[Task, Unit] = {
    Process.eval(Task { Thread.sleep(100) }).flatMap(_ => process)
  }

  val keyPair = CryptoCore.keyPair()

  def sendPingAction(pingId: Int): IO.Action.SendTo = {
    val node = NetworkCoreTest.nodes.head
    val address = new InetSocketAddress(node._1, ToxCoreConstants.DefaultStartPort)
    val receiverPublicKey = PublicKey.fromHexString(node._2).get

    val pingRequestPacket = NetworkCoreTest.makePingRequest(keyPair, receiverPublicKey, pingId)
      .getOrElse(fail("Unable to construct ping request packet"))

    logger.debug("Action: Ping request #" + pingId)
    IO.Action.SendTo(NodeInfo(Protocol.Udp, address, receiverPublicKey), pingRequestPacket)
  }

  val packetCount = 1
  val minCompletionRatio = 1.0
  val minResponses = (packetCount * minCompletionRatio).toInt max 1

  def runTest(actionSink: Sink[Task, IO.Action], actionSource: Process[Task, IO.Action]): Unit = {
    val eventQueue = async.boundedQueue[IO.Event](1)

    val actionActor = delayed(Process.range(0, packetCount).map(sendPingAction).toSource.to(actionSink))

    val udpActor = UdpActor.make(actionSource, eventQueue.enqueue)

    val testActor =
      eventQueue.dequeue
        .scan(0) { (count, event) =>
          event match {
            case IO.Event.Network(packet) =>
              val string = ToxHandler.toString(keyPair, PlainText(packet.bytes))
              logger.debug(s"Packet from ${packet.origin}: $string")
            case _ =>
              logger.error("Unexpected event: " + event)
          }
          count + 1
        }
        .flatMap { count =>
          if (count == minResponses) {
            logger.debug("Test finished successfully")
            Process.emit(IO.Action.Shutdown).toSource.to(actionSink) ++ actionActor.kill
          } else {
            Process.empty[Task, Unit]
          }
        }

    actionActor.merge(udpActor).merge(testActor).run.run
  }

  test("process ints with pubsub in Task context") {
    val actionQueue = async.topic[Int]()

    val numbers = Seq(1, 2, 3)
    val publisher = Process.emitAll(numbers).toSource.to(actionQueue.publish)

    val received = new ArrayBuffer[Int]
    val subscriber = actionQueue.subscribe.flatMap { i =>
      received += i
      if (i == 3) {
        publisher.kill ++ Process.halt
      } else {
        Process.empty[Task, Unit]
      }
    }

    publisher.merge(subscriber).run.run
    assert(received.toSeq == numbers)
  }

  test("process ints with pubsub in udp.Connection context") {
    val actionQueue = async.topic[Int]()

    val numbers = Seq(1, 2, 3)
    val publisher = delayed(Process.emitAll(numbers).toSource.to(actionQueue.publish))

    val received = new ArrayBuffer[Int]
    val subscriber = udp.listen(12345) {
      udp.lift(
        actionQueue.subscribe.flatMap { i =>
          received += i
          if (i == 3) {
            publisher.kill ++ Process.halt
          } else {
            Process.empty[Task, Unit]
          }
        }
      )
    }

    publisher.merge(subscriber).run.run
    assert(received.toSeq == numbers)
  }

  test("process actions with queue") {
    val actionQueue = async.boundedQueue[IO.Action](1)

    runTest(actionQueue.enqueue, actionQueue.dequeue)
  }

  test("process actions with pubsub") {
    val actionQueue = async.topic[IO.Action]()

    runTest(actionQueue.publish, actionQueue.subscribe)
  }

}
