package im.tox.core.network

import java.net.InetSocketAddress

import im.tox.core.crypto.{KeyPair, Nonce, PublicKey}
import im.tox.core.dht.Dht
import im.tox.core.dht.packets.dht.PingRequestPacket
import im.tox.core.dht.packets.{DhtEncryptedPacket, DhtUnencryptedPacket}
import im.tox.core.error.CoreError
import im.tox.core.network.PacketKind.PingRequest
import im.tox.core.network.packets.ToxPacket
import im.tox.tox4j.core.ToxCoreConstants
import org.scalatest.FunSuite

import scala.util.Try
import scalaz.\/

object NetworkCoreTest {

  val nodes = Seq(
    ("192.210.149.121", "F404ABAA1C99A9D37D61AB54898F56793E1DEF8BD46B1038B9D822E8460FAB67"), // nurupo
    ("178.62.250.138", "788236D34978D1D5BD822F0A5BEBD2C53C64CC31CD3149350EE27D4D9A2F9B6B"), // Impyy
    ("144.76.60.215", "04119E835DF3E78BACF0F84235B300546AF8B936F035185E2A8E9E0A67C8924F"), // sonOfRa
    ("23.226.230.47", "A09162D68618E742FFBCA1C2C70385E6679604B2D80EA6E84AD0996A1AC8A074"), // stal
    ("localhost", "9570FFA4644F8B6AF6DEBDCF3BE2E50553182C8D148F5AB1B4D292F293E5413D") // TestClient
  )

  val EncryptedPingRequestPacket = DhtEncryptedPacket.Make(DhtUnencryptedPacket.Make(PingRequestPacket))

  def makePingRequest(
    senderKeyPair: KeyPair,
    receiverPublicKey: PublicKey,
    pingId: Long
  ): \/[CoreError, ToxPacket[PingRequest.type]] = {
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

  def start(): \/[CoreError, Unit] = {
    val node = NetworkCoreTest.nodes.head
    val address = new InetSocketAddress(node._1, ToxCoreConstants.DefaultStartPort)

    for {
      receiverPublicKey <- PublicKey.fromHexString(node._2)
      result <- {
        val dht = Dht()

        for {
          packet <- NetworkCoreTest.makePingRequest(dht.keyPair, receiverPublicKey, 0)
        } yield {
          NetworkCore.client(dht, address, receiverPublicKey, packet).run.run
        }
      }
    } yield {
      result
    }
  }

  ignore("bootstrapping and communicating with the DHT") {
    System.out.println(Try(start()))
  }

}
