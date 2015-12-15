package im.tox.tox4j.core.exceptions

import im.tox.tox4j.core.ToxCoreConstants
import im.tox.tox4j.core.data.{ToxFriendNumber, ToxLosslessPacket, ToxLossyPacket}
import im.tox.tox4j.testing.ToxTestMixin
import org.scalatest.FunSuite

final class ToxFriendCustomPacketExceptionTest extends FunSuite with ToxTestMixin {

  private val friendNumber = ToxFriendNumber.fromInt(0).get
  private val badFriendNumber = ToxFriendNumber.fromInt(1).get

  test("SendLosslessPacketNotConnected") {
    interceptWithTox(ToxFriendCustomPacketException.Code.FRIEND_NOT_CONNECTED)(
      _.friendSendLosslessPacket(friendNumber, ToxLosslessPacket.fromValue(Array[Byte](160.toByte, 0, 1, 2, 3)).get)
    )
  }

  test("SendLossyPacketNotConnected") {
    interceptWithTox(ToxFriendCustomPacketException.Code.FRIEND_NOT_CONNECTED)(
      _.friendSendLossyPacket(friendNumber, ToxLossyPacket.fromValue(200.toByte +: Array.ofDim[Byte](4)).get)
    )
  }

  test("SendLosslessPacketNotFound") {
    interceptWithTox(ToxFriendCustomPacketException.Code.FRIEND_NOT_FOUND)(
      _.friendSendLosslessPacket(badFriendNumber, ToxLosslessPacket.fromValue(Array[Byte](160.toByte, 0, 1, 2, 3)).get)
    )
  }

  test("SendLossyPacketNotFound") {
    interceptWithTox(ToxFriendCustomPacketException.Code.FRIEND_NOT_FOUND)(
      _.friendSendLossyPacket(badFriendNumber, ToxLossyPacket.fromValue(Array[Byte](200.toByte, 0, 1, 2, 3)).get)
    )
  }

  test("SendLosslessPacketInvalid") {
    interceptWithTox(ToxFriendCustomPacketException.Code.INVALID)(
      _.friendSendLosslessPacket(friendNumber, ToxLosslessPacket.unsafeFromValue(Array[Byte](159.toByte)))
    )
  }

  test("SendLossyPacketInvalid") {
    interceptWithTox(ToxFriendCustomPacketException.Code.INVALID)(
      _.friendSendLossyPacket(friendNumber, ToxLossyPacket.unsafeFromValue(Array[Byte](199.toByte)))
    )
  }

  test("SendLosslessPacketEmpty") {
    interceptWithTox(ToxFriendCustomPacketException.Code.EMPTY)(
      _.friendSendLosslessPacket(friendNumber, ToxLosslessPacket.unsafeFromValue(Array[Byte]()))
    )
  }

  test("SendLossyPacketEmpty") {
    interceptWithTox(ToxFriendCustomPacketException.Code.EMPTY)(
      _.friendSendLossyPacket(friendNumber, ToxLossyPacket.unsafeFromValue(Array[Byte]()))
    )
  }

  test("SendLosslessPacketNull") {
    interceptWithTox(ToxFriendCustomPacketException.Code.NULL)(
      _.friendSendLosslessPacket(friendNumber, ToxLosslessPacket.unsafeFromValue(null))
    )
  }

  test("SendLossyPacketNull") {
    interceptWithTox(ToxFriendCustomPacketException.Code.NULL)(
      _.friendSendLossyPacket(friendNumber, ToxLossyPacket.unsafeFromValue(null))
    )
  }

  test("SendLosslessPacketTooLong") {
    interceptWithTox(ToxFriendCustomPacketException.Code.TOO_LONG)(
      _.friendSendLosslessPacket(
        friendNumber,
        ToxLosslessPacket.unsafeFromValue(160.toByte +: Array.ofDim[Byte](ToxCoreConstants.MaxCustomPacketSize))
      )
    )
  }

  test("SendLossyPacketTooLong") {
    interceptWithTox(ToxFriendCustomPacketException.Code.TOO_LONG)(
      _.friendSendLossyPacket(
        friendNumber,
        ToxLossyPacket.unsafeFromValue(200.toByte +: Array.ofDim[Byte](ToxCoreConstants.MaxCustomPacketSize))
      )
    )
  }

}
