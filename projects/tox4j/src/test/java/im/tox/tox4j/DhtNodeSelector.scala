package im.tox.tox4j

import java.io.IOException
import java.net.{InetAddress, Socket}

import com.typesafe.scalalogging.Logger
import im.tox.tox4j.core.ToxCore
import im.tox.tox4j.impl.jni.ToxCoreImplFactory
import org.scalatest.Assertions
import org.slf4j.LoggerFactory

object DhtNodeSelector extends Assertions {

  private val logger = Logger(LoggerFactory.getLogger(this.getClass))
  private var selectedNode: Option[DhtNode] = Some(ToxCoreTestBase.nodeCandidates(1))

  private def tryConnect(node: DhtNode): Option[DhtNode] = {
    var socket: Socket = null
    try {
      socket = new Socket(InetAddress.getByName(node.ipv4), node.udpPort.value)
      assume(socket.getInputStream != null)
      Some(node)
    } catch {
      case e: IOException =>
        logger.info(s"TCP connection failed (${e.getMessage})")
        None
    } finally {
      if (socket != null) {
        socket.close()
      }
    }
  }

  private def tryBootstrap(
    withTox: (Boolean, Boolean) => (ToxCore[Unit] => Option[DhtNode]) => Option[DhtNode],
    node: DhtNode,
    udpEnabled: Boolean
  ): Option[DhtNode] = {
    val protocol = if (udpEnabled) "UDP" else "TCP"
    val port = if (udpEnabled) node.udpPort else node.tcpPort
    logger.info(s"Trying to bootstrap with ${node.ipv4}:$port using $protocol")

    withTox(true, udpEnabled) { tox =>
      val status = new ConnectedListener
      tox.callback(status)
      if (!udpEnabled) {
        tox.addTcpRelay(node.ipv4, port, node.dhtId)
      }
      tox.bootstrap(node.ipv4, port, node.dhtId)

      // Try bootstrapping for 10 seconds.
      (0 to 10000 / tox.iterationInterval) find { _ =>
        tox.iterate(())
        Thread.sleep(tox.iterationInterval)
        status.isConnected
      } match {
        case Some(time) =>
          logger.info(s"Bootstrapped successfully after ${time * tox.iterationInterval}ms using $protocol")
          Some(node)
        case None =>
          logger.info(s"Unable to bootstrap with $protocol")
          None
      }
    }
  }

  private def findNode(withTox: (Boolean, Boolean) => (ToxCore[Unit] => Option[DhtNode]) => Option[DhtNode]): DhtNode = {
    DhtNodeSelector.selectedNode match {
      case Some(node) => node
      case None =>
        logger.info("Looking for a working bootstrap node")

        DhtNodeSelector.selectedNode = ToxCoreTestBase.nodeCandidates find { node =>
          logger.info(s"Trying to establish a TCP connection to ${node.ipv4}")

          (for {
            node <- tryConnect(node)
            node <- tryBootstrap(withTox, node, udpEnabled = true)
            node <- tryBootstrap(withTox, node, udpEnabled = false)
          } yield node).isDefined
        }

        assume(DhtNodeSelector.selectedNode.nonEmpty, "No viable nodes for bootstrap found; cannot test")
        DhtNodeSelector.selectedNode.get
    }
  }

  def node: DhtNode = findNode(ToxCoreImplFactory.withToxUnit[Option[DhtNode]])

}
