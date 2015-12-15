package im.tox.core.network

import java.net.InetSocketAddress

import com.typesafe.scalalogging.Logger
import im.tox.core.crypto.{KeyPair, Nonce, PublicKey}
import im.tox.core.dht.packets.dht.PingRequestPacket
import im.tox.core.dht.packets.{DhtEncryptedPacket, DhtUnencryptedPacket}
import im.tox.core.dht.{Dht, NodeInfo, Protocol}
import im.tox.core.error.CoreError
import im.tox.core.io.IO
import im.tox.core.io.IO.TimerId
import im.tox.core.network.PacketKind.PingRequest
import im.tox.core.network.packets.ToxPacket
import im.tox.tox4j.testing.GetDisjunction._
import org.scalatest.FunSuite
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Try
import scalaz.{-\/, \/, \/-}

object NetworkCoreTest {

  val nodes = List(
    ("23.226.230.47", 33445, "A09162D68618E742FFBCA1C2C70385E6679604B2D80EA6E84AD0996A1AC8A074"), // stal
    ("biribiri.org", 33445, "F404ABAA1C99A9D37D61AB54898F56793E1DEF8BD46B1038B9D822E8460FAB67"), // nurupo
    ("144.76.60.215", 33445, "04119E835DF3E78BACF0F84235B300546AF8B936F035185E2A8E9E0A67C8924F"), // sonOfRa
    ("178.62.250.138", 33445, "788236D34978D1D5BD822F0A5BEBD2C53C64CC31CD3149350EE27D4D9A2F9B6B"), // Impyy
    ("localhost", 33445, "9570FFA4644F8B6AF6DEBDCF3BE2E50553182C8D148F5AB1B4D292F293E5413D") // TestClient
  ).map {
      case (address, port, key) =>
        (new InetSocketAddress(address, port), PublicKey.fromHexString(key).get)
    }

  val EncryptedPingRequestPacket = DhtEncryptedPacket.Make(DhtUnencryptedPacket.Make(PingRequestPacket))

  def makePingRequest(
    senderKeyPair: KeyPair,
    receiverPublicKey: PublicKey,
    pingId: Long
  ): CoreError \/ ToxPacket[PingRequest.type] = {
    for {
      request <- EncryptedPingRequestPacket.encrypt(
        receiverPublicKey,
        senderKeyPair,
        Nonce.random(),
        DhtUnencryptedPacket(PingRequestPacket, pingId)
      )
      request <- EncryptedPingRequestPacket.toBytes(request)
    } yield {
      ToxPacket(
        PingRequestPacket.packetKind,
        request
      )
    }
  }

}

final class NetworkCoreTest extends FunSuite {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  def start(): Unit = {
    val (address, receiverPublicKey) = NetworkCoreTest.nodes.headOption.get

    val dht = for {
      dht <- Dht(Dht.Options(
        nodesRequestInterval = 1 seconds,
        maxClosestNodes = 256
      ))
      _ <- IO.startTimer(TimerId("Shutdown"), 60 seconds, Some(1))(_ => Some(IO.Event.Shutdown))
      _ <- {
        NetworkCoreTest.makePingRequest(dht.keyPair, receiverPublicKey, 0) match {
          case -\/(failure) =>
            logger.error(s"Error trying to create initial ping request: $failure")
            IO(())
          case \/-(packet) =>
            IO.sendTo(NodeInfo(Protocol.Udp, address, receiverPublicKey), packet)
        }
      }
    } yield {
      // Add a random other node to search for as initial DHT friend.
      dht
        // .addSearchKey(PublicKey.random())
        // .addSearchKey(PublicKey.random())
        // .addSearchKey(PublicKey.random())
        .addSearchKey(PublicKey.fromHexString("0764FBE9440078718A24D9E4111B480BE8981170ACF30E669C96EDD076AD6752").get)
    }

    NetworkCore.client(dht).run.run
  }

  ignore("bootstrapping and communicating with the DHT") {
    System.out.println(Try(start()))
  }

}
