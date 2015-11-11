import com.typesafe.scalalogging.Logger
import im.tox.core.network.Port
import im.tox.tox4j.core._
import im.tox.tox4j.core.callbacks.ToxEventListener
import im.tox.tox4j.core.enums.{ToxConnection, ToxFileControl, ToxMessageType, ToxUserStatus}
import im.tox.tox4j.core.options.ToxOptions
import im.tox.tox4j.impl.jni.ToxCoreImpl
import org.slf4j.LoggerFactory

object TestClient extends App {

  private val logger = Logger(LoggerFactory.getLogger(TestClient.getClass))

  private def readablePublicKey(id: Array[Byte]): String = {
    val str: StringBuilder = new StringBuilder
    for (b <- id) {
      str.append(f"$b%02X")
    }
    str.toString()
  }

  private def parsePublicKey(id: String): ToxPublicKey = {
    val publicKey = Array.ofDim[Byte](ToxCoreConstants.PublicKeySize)

    for (i <- 0 until ToxCoreConstants.PublicKeySize) {
      publicKey(i) = (
        (fromHexDigit(id.charAt(i * 2)) << 4) +
        fromHexDigit(id.charAt(i * 2 + 1))
      ).toByte
    }
    ToxPublicKey.unsafeFromByteArray(publicKey)
  }

  private def fromHexDigit(c: Char): Byte = {
    val subtract =
      if (c >= '0' && c <= '9') {
        '0'
      } else if (c >= 'a' && c <= 'f') {
        'A' + 10
      } else if (c >= 'A' && c <= 'F') {
        'A' + 10
      } else {
        throw new IllegalArgumentException("Non-hex digit character: " + c)
      }
    (c - subtract).toByte
  }

  (args match {
    case Array("--bootstrap", host, port, key, count) =>
      (Some((host, Integer.parseInt(port), key)), Integer.parseInt(count))
    case Array("--bootstrap", host, port, key) =>
      (Some((host, Integer.parseInt(port), key)), 1)
    case Array("--bootstrap", count) =>
      (Some(("144.76.60.215", 33445, "04119E835DF3E78BACF0F84235B300546AF8B936F035185E2A8E9E0A67C8924F")), Integer.parseInt(count))
    case Array(count) =>
      (None, Integer.parseInt(count))
    case _ =>
      (None, 1)
  }) match {
    case (bootstrap, count) =>
      logger.info(s"Creating $count toxes")

      val toxes = (1 to count) map { id =>
        val tox = new ToxCoreImpl[Unit](new ToxOptions(true, bootstrap.isEmpty))

        tox.callback(new TestEventListener(id))
        logger.info(s"[$id] Key: ${readablePublicKey(tox.getPublicKey.value)}")

        bootstrap match {
          case Some((host, port, key)) =>
            logger.info(s"[$id] Bootstrapping to $host:$port")
            tox.bootstrap(host, Port.unsafeFromInt(port), parsePublicKey(key))
          case None =>
        }
        tox
      }

      if (count > 0) {
        logger.info("Starting event loop")
        while (true) {
          toxes.foreach(_.iterate(()))
          Thread.sleep(toxes.map(_.iterationInterval).max)
        }
      }
  }

  private sealed class TestEventListener(id: Int) extends ToxEventListener[Unit] {

    override def friendStatus(friendNumber: Int, status: ToxUserStatus)(state: Unit): Unit = {
      logger.info(s"[$id] friendStatus($friendNumber, $status)")
    }

    override def friendTyping(friendNumber: Int, isTyping: Boolean)(state: Unit): Unit = {
      logger.info(s"[$id] friendTyping($friendNumber, $isTyping)")
    }

    override def selfConnectionStatus(connectionStatus: ToxConnection)(state: Unit): Unit = {
      logger.info(s"[$id] selfConnectionStatus($connectionStatus)")
    }

    override def friendName(friendNumber: Int, name: ToxNickname)(state: Unit): Unit = {
      logger.info(s"[$id] friendName($friendNumber, ${new String(name.value)})")
    }

    override def friendMessage(friendNumber: Int, messageType: ToxMessageType, timeDelta: Int, message: ToxFriendMessage)(state: Unit): Unit = {
      logger.info(s"[$id] friendMessage($friendNumber, $timeDelta, ${new String(message.value)})")
    }

    override def friendLossyPacket(friendNumber: Int, data: ToxLossyPacket)(state: Unit): Unit = {
      logger.info(s"[$id] friendLossyPacket($friendNumber, ${new String(data.value)})")
    }

    override def fileRecv(friendNumber: Int, fileNumber: Int, kind: Int, fileSize: Long, filename: ToxFilename)(state: Unit): Unit = {
      logger.info(s"[$id] fileRecv($friendNumber, $fileNumber, $kind, $fileSize, ${new String(filename.value)}})")
    }

    override def friendRequest(publicKey: ToxPublicKey, timeDelta: Int, message: ToxFriendRequestMessage)(state: Unit): Unit = {
      logger.info(s"[$id] friendRequest($publicKey, $timeDelta, ${new String(message.value)})")
    }

    override def fileChunkRequest(friendNumber: Int, fileNumber: Int, position: Long, length: Int)(state: Unit): Unit = {
      logger.info(s"[$id] fileChunkRequest($friendNumber, $fileNumber, $position, $length)")
    }

    override def fileRecvChunk(friendNumber: Int, fileNumber: Int, position: Long, data: Array[Byte])(state: Unit): Unit = {
      logger.info(s"[$id] fileRecvChunk($friendNumber, $fileNumber, $position, ${new String(data)})")
    }

    override def friendLosslessPacket(friendNumber: Int, data: ToxLosslessPacket)(state: Unit): Unit = {
      logger.info(s"[$id] friendLosslessPacket($friendNumber, ${new String(data.value)})")
    }

    override def friendConnectionStatus(friendNumber: Int, connectionStatus: ToxConnection)(state: Unit): Unit = {
      logger.info(s"[$id] friendConnectionStatus($friendNumber, $connectionStatus)")
    }

    override def fileRecvControl(friendNumber: Int, fileNumber: Int, control: ToxFileControl)(state: Unit): Unit = {
      logger.info(s"[$id] fileRecvControl($friendNumber, $fileNumber, $control)")
    }

    override def friendStatusMessage(friendNumber: Int, message: ToxStatusMessage)(state: Unit): Unit = {
      logger.info(s"[$id] friendStatusMessage($friendNumber, ${new String(message.value)})")
    }

    override def friendReadReceipt(friendNumber: Int, messageId: Int)(state: Unit): Unit = {
      logger.info(s"[$id] friendReadReceipt($friendNumber, $messageId)")
    }

  }

}
