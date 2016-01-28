package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.callbacks.CoreInvokeTest._
import im.tox.tox4j.core.callbacks.InvokeTest.ByteArray
import im.tox.tox4j.core.enums.{ToxConnection, ToxFileControl, ToxMessageType, ToxUserStatus}
import im.tox.tox4j.core.options.ToxOptions
import im.tox.tox4j.core._
import im.tox.tox4j.impl.jni.ToxCoreImpl
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.FunSuite
import org.scalatest.prop.PropertyChecks

import scala.language.implicitConversions
import scala.util.Random

final class CoreInvokeTest extends FunSuite with PropertyChecks {

  final class TestEventListener extends ToxEventListener[Event] {
    private def setEvent(event: Event)(state: Event): Event = {
      assert(state == null)
      event
    }

    // scalastyle:off line.size.limit
    override def friendTyping(friendNumber: Int, isTyping: Boolean)(state: Event): Event = setEvent(FriendTyping(friendNumber, isTyping))(state)
    override def friendStatusMessage(friendNumber: Int, message: ToxStatusMessage)(state: Event): Event = setEvent(FriendStatusMessage(friendNumber, message.value))(state)
    override def fileChunkRequest(friendNumber: Int, fileNumber: Int, position: Long, length: Int)(state: Event): Event = setEvent(FileChunkRequest(friendNumber, fileNumber, position, length))(state)
    override def fileRecvChunk(friendNumber: Int, fileNumber: Int, position: Long, data: Array[Byte])(state: Event): Event = setEvent(FileRecvChunk(friendNumber, fileNumber, position, data))(state)
    override def friendConnectionStatus(friendNumber: Int, connectionStatus: ToxConnection)(state: Event): Event = setEvent(FriendConnectionStatus(friendNumber, connectionStatus))(state)
    override def friendRequest(publicKey: ToxPublicKey, timeDelta: Int, message: ToxFriendRequestMessage)(state: Event): Event = setEvent(FriendRequest(publicKey.value, timeDelta, message.value))(state)
    override def friendLossyPacket(friendNumber: Int, data: ToxLossyPacket)(state: Event): Event = setEvent(FriendLossyPacket(friendNumber, data.value))(state)
    override def friendStatus(friendNumber: Int, status: ToxUserStatus)(state: Event): Event = setEvent(FriendStatus(friendNumber, status))(state)
    override def selfConnectionStatus(connectionStatus: ToxConnection)(state: Event): Event = setEvent(SelfConnectionStatus(connectionStatus))(state)
    override def friendReadReceipt(friendNumber: Int, messageId: Int)(state: Event): Event = setEvent(FriendReadReceipt(friendNumber, messageId))(state)
    override def friendName(friendNumber: Int, name: ToxNickname)(state: Event): Event = setEvent(FriendName(friendNumber, name.value))(state)
    override def friendLosslessPacket(friendNumber: Int, data: ToxLosslessPacket)(state: Event): Event = setEvent(FriendLosslessPacket(friendNumber, data.value))(state)
    override def friendMessage(friendNumber: Int, messageType: ToxMessageType, timeDelta: Int, message: ToxFriendMessage)(state: Event): Event = setEvent(FriendMessage(friendNumber, messageType, timeDelta, message.value))(state)
    override def fileRecv(friendNumber: Int, fileNumber: Int, kind: Int, fileSize: Long, filename: ToxFilename)(state: Event): Event = setEvent(FileRecv(friendNumber, fileNumber, kind, fileSize, filename.value))(state)
    override def fileRecvControl(friendNumber: Int, fileNumber: Int, control: ToxFileControl)(state: Event): Event = setEvent(FileRecvControl(friendNumber, fileNumber, control))(state)
    // scalastyle:on line.size.limit
  }

  def callbackTest(invoke: ToxCoreImpl[Event] => Unit, expected: Event): Unit = {
    val tox = new ToxCoreImpl[Event](ToxOptions())

    try {
      val listener = new TestEventListener
      tox.callback(listener)
      invoke(tox)
      val event = tox.iterate(null)
      assert(event == expected)
    } finally {
      tox.close()
    }
  }

  private val random = new Random

  private implicit val arbPublicKey: Arbitrary[ToxPublicKey] = {
    Arbitrary(Gen.const(ToxCoreConstants.PublicKeySize).map(Array.ofDim[Byte]).map { array =>
      random.nextBytes(array)
      array(array.length - 1) = 0
      ToxPublicKey.unsafeFromByteArray(array)
    })
  }

  private implicit val arbNickname: Arbitrary[ToxNickname] = {
    Arbitrary(arbitrary[Array[Byte]].map(ToxNickname.unsafeFromByteArray))
  }

  private implicit val arbToxConnection: Arbitrary[ToxConnection] = {
    Arbitrary(Arbitrary.arbInt.arbitrary.map { i => ToxConnection.values()(Math.abs(i % ToxConnection.values().length)) })
  }

  private implicit val arbToxFileControl: Arbitrary[ToxFileControl] = {
    Arbitrary(Arbitrary.arbInt.arbitrary.map { i => ToxFileControl.values()(Math.abs(i % ToxFileControl.values().length)) })
  }

  private implicit val arbToxUserStatus: Arbitrary[ToxUserStatus] = {
    Arbitrary(Arbitrary.arbInt.arbitrary.map { i => ToxUserStatus.values()(Math.abs(i % ToxUserStatus.values().length)) })
  }

  private implicit val arbToxMessageType: Arbitrary[ToxMessageType] = {
    Arbitrary(Arbitrary.arbInt.arbitrary.map { i => ToxMessageType.values()(Math.abs(i % ToxMessageType.values().length)) })
  }

  test("FriendTyping") {
    forAll { (friendNumber: Int, isTyping: Boolean) =>
      callbackTest(
        _.invokeFriendTyping(friendNumber, isTyping),
        FriendTyping(friendNumber, isTyping)
      )
    }
  }

  test("FriendStatusMessage") {
    forAll { (friendNumber: Int, message: Array[Byte]) =>
      callbackTest(
        _.invokeFriendStatusMessage(friendNumber, message),
        FriendStatusMessage(friendNumber, message)
      )
    }
  }

  test("FileChunkRequest") {
    forAll { (friendNumber: Int, fileNumber: Int, position: Long, length: Int) =>
      callbackTest(
        _.invokeFileChunkRequest(friendNumber, fileNumber, position, length),
        FileChunkRequest(friendNumber, fileNumber, position, length)
      )
    }
  }

  test("FileRecvChunk") {
    forAll { (friendNumber: Int, fileNumber: Int, position: Long, data: Array[Byte]) =>
      callbackTest(
        _.invokeFileRecvChunk(friendNumber, fileNumber, position, data),
        FileRecvChunk(friendNumber, fileNumber, position, data)
      )
    }
  }

  test("FriendConnectionStatus") {
    forAll { (friendNumber: Int, connectionStatus: ToxConnection) =>
      callbackTest(
        _.invokeFriendConnectionStatus(friendNumber, connectionStatus),
        FriendConnectionStatus(friendNumber, connectionStatus)
      )
    }
  }

  test("FriendRequest") {
    forAll { (publicKey: ToxPublicKey, timeDelta: Int, message: Array[Byte]) =>
      callbackTest(
        _.invokeFriendRequest(publicKey, timeDelta, message),
        FriendRequest(publicKey.value, /* timeDelta */ 0, message)
      )
    }
  }

  test("FriendLossyPacket") {
    forAll { (friendNumber: Int, data: Array[Byte]) =>
      callbackTest(
        _.invokeFriendLossyPacket(friendNumber, data),
        FriendLossyPacket(friendNumber, data)
      )
    }
  }

  test("FriendStatus") {
    forAll { (friendNumber: Int, status: ToxUserStatus) =>
      callbackTest(
        _.invokeFriendStatus(friendNumber, status),
        FriendStatus(friendNumber, status)
      )
    }
  }

  test("SelfConnectionStatus") {
    forAll { (connectionStatus: ToxConnection) =>
      callbackTest(
        _.invokeSelfConnectionStatus(connectionStatus),
        SelfConnectionStatus(connectionStatus)
      )
    }
  }

  test("FriendReadReceipt") {
    forAll { (friendNumber: Int, messageId: Int) =>
      callbackTest(
        _.invokeFriendReadReceipt(friendNumber, messageId),
        FriendReadReceipt(friendNumber, messageId)
      )
    }
  }

  test("FriendName") {
    forAll { (friendNumber: Int, name: ToxNickname) =>
      callbackTest(
        _.invokeFriendName(friendNumber, name),
        FriendName(friendNumber, name.value)
      )
    }
  }

  test("FriendLosslessPacket") {
    forAll { (friendNumber: Int, data: Array[Byte]) =>
      callbackTest(
        _.invokeFriendLosslessPacket(friendNumber, data),
        FriendLosslessPacket(friendNumber, data)
      )
    }
  }

  test("FriendMessage") {
    forAll { (friendNumber: Int, messageType: ToxMessageType, timeDelta: Int, message: Array[Byte]) =>
      callbackTest(
        _.invokeFriendMessage(friendNumber, messageType, timeDelta, message),
        FriendMessage(friendNumber, messageType, /* timeDelta */ 0, message)
      )
    }
  }

  test("FileRecv") {
    forAll { (friendNumber: Int, fileNumber: Int, kind: Int, fileSize: Long, filename: Array[Byte]) =>
      callbackTest(
        _.invokeFileRecv(friendNumber, fileNumber, kind, fileSize, filename),
        FileRecv(friendNumber, fileNumber, kind, fileSize, filename)
      )
    }
  }

  test("FileRecvControl") {
    forAll { (friendNumber: Int, fileNumber: Int, control: ToxFileControl) =>
      callbackTest(
        _.invokeFileRecvControl(friendNumber, fileNumber, control),
        FileRecvControl(friendNumber, fileNumber, control)
      )
    }
  }

}

object CoreInvokeTest {
  sealed trait Event
  private final case class FriendTyping(friendNumber: Int, isTyping: Boolean) extends Event
  private final case class FriendStatusMessage(friendNumber: Int, message: ByteArray) extends Event
  private final case class FileChunkRequest(friendNumber: Int, fileNumber: Int, position: Long, length: Int) extends Event
  private final case class FileRecvChunk(friendNumber: Int, fileNumber: Int, position: Long, data: ByteArray) extends Event
  private final case class FriendConnectionStatus(friendNumber: Int, connectionStatus: ToxConnection) extends Event
  private final case class FriendRequest(publicKey: ByteArray, timeDelta: Int, message: ByteArray) extends Event
  private final case class FriendLossyPacket(friendNumber: Int, data: ByteArray) extends Event
  private final case class FriendStatus(friendNumber: Int, status: ToxUserStatus) extends Event
  private final case class SelfConnectionStatus(connectionStatus: ToxConnection) extends Event
  private final case class FriendReadReceipt(friendNumber: Int, messageId: Int) extends Event
  private final case class FriendName(friendNumber: Int, name: ByteArray) extends Event
  private final case class FriendLosslessPacket(friendNumber: Int, data: ByteArray) extends Event
  private final case class FriendMessage(friendNumber: Int, messageType: ToxMessageType, timeDelta: Int, message: ByteArray) extends Event
  private final case class FileRecv(friendNumber: Int, fileNumber: Int, kind: Int, fileSize: Long, filename: ByteArray) extends Event
  private final case class FileRecvControl(friendNumber: Int, fileNumber: Int, control: ToxFileControl) extends Event
}
