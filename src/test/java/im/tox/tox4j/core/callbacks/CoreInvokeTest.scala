package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.callbacks.CoreInvokeTest._
import im.tox.tox4j.core.callbacks.InvokeTest.ByteArray
import im.tox.tox4j.core.data._
import im.tox.tox4j.core.enums.{ ToxConnection, ToxFileControl, ToxMessageType, ToxUserStatus }
import im.tox.tox4j.core.options.ToxOptions
import im.tox.tox4j.impl.jni.ToxCoreImpl
import im.tox.tox4j.testing.GetDisjunction._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{ Arbitrary, Gen }
import org.scalatest.FunSuite
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import scala.language.implicitConversions
import scala.util.Random

final class CoreInvokeTest extends FunSuite with ScalaCheckPropertyChecks {

  final class TestEventListener extends ToxCoreEventListener[Option[Event]] {
    private def setEvent(event: Event)(state: Option[Event]): Option[Event] = {
      assert(state.isEmpty)
      Some(event)
    }

    // scalastyle:off line.size.limit
    override def friendTyping(friendNumber: ToxFriendNumber, isTyping: Boolean)(state: Option[Event]): Option[Event] = setEvent(FriendTyping(friendNumber, isTyping))(state)
    override def friendStatusMessage(friendNumber: ToxFriendNumber, message: ToxStatusMessage)(state: Option[Event]): Option[Event] = setEvent(FriendStatusMessage(friendNumber, message.value))(state)
    override def fileChunkRequest(friendNumber: ToxFriendNumber, fileNumber: Int, position: Long, length: Int)(state: Option[Event]): Option[Event] = setEvent(FileChunkRequest(friendNumber, fileNumber, position, length))(state)
    override def fileRecvChunk(friendNumber: ToxFriendNumber, fileNumber: Int, position: Long, data: Array[Byte])(state: Option[Event]): Option[Event] = setEvent(FileRecvChunk(friendNumber, fileNumber, position, data))(state)
    override def friendConnectionStatus(friendNumber: ToxFriendNumber, connectionStatus: ToxConnection)(state: Option[Event]): Option[Event] = setEvent(FriendConnectionStatus(friendNumber, connectionStatus))(state)
    override def friendRequest(publicKey: ToxPublicKey, timeDelta: Int, message: ToxFriendRequestMessage)(state: Option[Event]): Option[Event] = setEvent(FriendRequest(publicKey.value, timeDelta, message.value))(state)
    override def friendLossyPacket(friendNumber: ToxFriendNumber, data: ToxLossyPacket)(state: Option[Event]): Option[Event] = setEvent(FriendLossyPacket(friendNumber, data.value))(state)
    override def friendStatus(friendNumber: ToxFriendNumber, status: ToxUserStatus)(state: Option[Event]): Option[Event] = setEvent(FriendStatus(friendNumber, status))(state)
    override def selfConnectionStatus(connectionStatus: ToxConnection)(state: Option[Event]): Option[Event] = setEvent(SelfConnectionStatus(connectionStatus))(state)
    override def friendReadReceipt(friendNumber: ToxFriendNumber, messageId: Int)(state: Option[Event]): Option[Event] = setEvent(FriendReadReceipt(friendNumber, messageId))(state)
    override def friendName(friendNumber: ToxFriendNumber, name: ToxNickname)(state: Option[Event]): Option[Event] = setEvent(FriendName(friendNumber, name.value))(state)
    override def friendLosslessPacket(friendNumber: ToxFriendNumber, data: ToxLosslessPacket)(state: Option[Event]): Option[Event] = setEvent(FriendLosslessPacket(friendNumber, data.value))(state)
    override def friendMessage(friendNumber: ToxFriendNumber, messageType: ToxMessageType, timeDelta: Int, message: ToxFriendMessage)(state: Option[Event]): Option[Event] = setEvent(FriendMessage(friendNumber, messageType, timeDelta, message.value))(state)
    override def fileRecv(friendNumber: ToxFriendNumber, fileNumber: Int, kind: Int, fileSize: Long, filename: ToxFilename)(state: Option[Event]): Option[Event] = setEvent(FileRecv(friendNumber, fileNumber, kind, fileSize, filename.value))(state)
    override def fileRecvControl(friendNumber: ToxFriendNumber, fileNumber: Int, control: ToxFileControl)(state: Option[Event]): Option[Event] = setEvent(FileRecvControl(friendNumber, fileNumber, control))(state)
    // scalastyle:on line.size.limit
  }

  def callbackTest(invoke: ToxCoreEventSynth => Unit, expected: Event): Unit = {
    val tox = new ToxCoreImpl(ToxOptions())

    try {
      invoke(tox)
      val listener = new TestEventListener
      val event = tox.iterate(listener)(None)
      assert(event.contains(expected))
    } finally {
      tox.close()
    }
  }

  private val random = new Random

  private implicit val arbToxPublicKey: Arbitrary[ToxPublicKey] = {
    Arbitrary(Gen.const(ToxPublicKey.Size).map(Array.ofDim[Byte]).map { array =>
      random.nextBytes(array)
      array(array.length - 1) = 0
      ToxPublicKey.fromValue(array).toOption.get
    })
  }

  private implicit val arbToxFriendNumber: Arbitrary[ToxFriendNumber] = {
    Arbitrary(arbitrary[Int].map(ToxFriendNumber.unsafeFromInt))
  }

  private implicit val arbToxNickname: Arbitrary[ToxNickname] = {
    Arbitrary(arbitrary[Array[Byte]].map(ToxNickname.unsafeFromValue))
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
    forAll { (friendNumber: ToxFriendNumber, isTyping: Boolean) =>
      callbackTest(
        _.invokeFriendTyping(friendNumber, isTyping),
        FriendTyping(friendNumber, isTyping)
      )
    }
  }

  test("FriendStatusMessage") {
    forAll { (friendNumber: ToxFriendNumber, message: Array[Byte]) =>
      callbackTest(
        _.invokeFriendStatusMessage(friendNumber, message),
        FriendStatusMessage(friendNumber, message)
      )
    }
  }

  test("FileChunkRequest") {
    forAll { (friendNumber: ToxFriendNumber, fileNumber: Int, position: Long, length: Int) =>
      callbackTest(
        _.invokeFileChunkRequest(friendNumber, fileNumber, position, length),
        FileChunkRequest(friendNumber, fileNumber, position, length)
      )
    }
  }

  test("FileRecvChunk") {
    forAll { (friendNumber: ToxFriendNumber, fileNumber: Int, position: Long, data: Array[Byte]) =>
      callbackTest(
        _.invokeFileRecvChunk(friendNumber, fileNumber, position, data),
        FileRecvChunk(friendNumber, fileNumber, position, data)
      )
    }
  }

  test("FriendConnectionStatus") {
    forAll { (friendNumber: ToxFriendNumber, connectionStatus: ToxConnection) =>
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
    forAll { (friendNumber: ToxFriendNumber, data: Array[Byte]) =>
      callbackTest(
        _.invokeFriendLossyPacket(friendNumber, data),
        FriendLossyPacket(friendNumber, data)
      )
    }
  }

  test("FriendStatus") {
    forAll { (friendNumber: ToxFriendNumber, status: ToxUserStatus) =>
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
    forAll { (friendNumber: ToxFriendNumber, messageId: Int) =>
      callbackTest(
        _.invokeFriendReadReceipt(friendNumber, messageId),
        FriendReadReceipt(friendNumber, messageId)
      )
    }
  }

  test("FriendName") {
    forAll { (friendNumber: ToxFriendNumber, name: ToxNickname) =>
      callbackTest(
        _.invokeFriendName(friendNumber, name),
        FriendName(friendNumber, name.value)
      )
    }
  }

  test("FriendLosslessPacket") {
    forAll { (friendNumber: ToxFriendNumber, data: Array[Byte]) =>
      callbackTest(
        _.invokeFriendLosslessPacket(friendNumber, data),
        FriendLosslessPacket(friendNumber, data)
      )
    }
  }

  test("FriendMessage") {
    forAll { (friendNumber: ToxFriendNumber, messageType: ToxMessageType, timeDelta: Int, message: Array[Byte]) =>
      callbackTest(
        _.invokeFriendMessage(friendNumber, messageType, timeDelta, message),
        FriendMessage(friendNumber, messageType, /* timeDelta */ 0, message)
      )
    }
  }

  test("FileRecv") {
    forAll { (friendNumber: ToxFriendNumber, fileNumber: Int, kind: Int, fileSize: Long, filename: Array[Byte]) =>
      callbackTest(
        _.invokeFileRecv(friendNumber, fileNumber, kind, fileSize, filename),
        FileRecv(friendNumber, fileNumber, kind, fileSize, filename)
      )
    }
  }

  test("FileRecvControl") {
    forAll { (friendNumber: ToxFriendNumber, fileNumber: Int, control: ToxFileControl) =>
      callbackTest(
        _.invokeFileRecvControl(friendNumber, fileNumber, control),
        FileRecvControl(friendNumber, fileNumber, control)
      )
    }
  }

}

object CoreInvokeTest {
  sealed trait Event
  private final case class FriendTyping(friendNumber: ToxFriendNumber, isTyping: Boolean) extends Event
  private final case class FriendStatusMessage(friendNumber: ToxFriendNumber, message: ByteArray) extends Event
  private final case class FileChunkRequest(friendNumber: ToxFriendNumber, fileNumber: Int, position: Long, length: Int) extends Event
  private final case class FileRecvChunk(friendNumber: ToxFriendNumber, fileNumber: Int, position: Long, data: ByteArray) extends Event
  private final case class FriendConnectionStatus(friendNumber: ToxFriendNumber, connectionStatus: ToxConnection) extends Event
  private final case class FriendRequest(publicKey: ByteArray, timeDelta: Int, message: ByteArray) extends Event
  private final case class FriendLossyPacket(friendNumber: ToxFriendNumber, data: ByteArray) extends Event
  private final case class FriendStatus(friendNumber: ToxFriendNumber, status: ToxUserStatus) extends Event
  private final case class SelfConnectionStatus(connectionStatus: ToxConnection) extends Event
  private final case class FriendReadReceipt(friendNumber: ToxFriendNumber, messageId: Int) extends Event
  private final case class FriendName(friendNumber: ToxFriendNumber, name: ByteArray) extends Event
  private final case class FriendLosslessPacket(friendNumber: ToxFriendNumber, data: ByteArray) extends Event
  private final case class FriendMessage(friendNumber: ToxFriendNumber, messageType: ToxMessageType, timeDelta: Int, message: ByteArray) extends Event
  private final case class FileRecv(friendNumber: ToxFriendNumber, fileNumber: Int, kind: Int, fileSize: Long, filename: ByteArray) extends Event
  private final case class FileRecvControl(friendNumber: ToxFriendNumber, fileNumber: Int, control: ToxFileControl) extends Event
}
