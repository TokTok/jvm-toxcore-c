package im.tox.tox4j.core.exceptions

import im.tox.tox4j.core.{ToxCoreConstants, ToxLosslessPacket, ToxLossyPacket}
import im.tox.tox4j.testing.ToxTestMixin
import org.scalatest.FunSuite

final class ToxFriendCustomPacketExceptionTest extends FunSuite with ToxTestMixin {

  test("SendLosslessPacketNotConnected") {
    interceptWithTox(ToxFriendCustomPacketException.Code.FRIEND_NOT_CONNECTED)(
      _.friendSendLosslessPacket(0, ToxLosslessPacket.fromByteArray(Array[Byte](160.toByte, 0, 1, 2, 3)).get)
    )
  }

  test("SendLossyPacketNotConnected") {
    interceptWithTox(ToxFriendCustomPacketException.Code.FRIEND_NOT_CONNECTED)(
      _.friendSendLossyPacket(0, ToxLossyPacket.fromByteArray(200.toByte +: Array.ofDim[Byte](4)).get)
    )
  }

  test("SendLosslessPacketNotFound") {
    interceptWithTox(ToxFriendCustomPacketException.Code.FRIEND_NOT_FOUND)(
      _.friendSendLosslessPacket(1, ToxLosslessPacket.fromByteArray(Array[Byte](160.toByte, 0, 1, 2, 3)).get)
    )
  }

  test("SendLossyPacketNotFound") {
    interceptWithTox(ToxFriendCustomPacketException.Code.FRIEND_NOT_FOUND)(
      _.friendSendLossyPacket(1, ToxLossyPacket.fromByteArray(Array[Byte](200.toByte, 0, 1, 2, 3)).get)
    )
  }

  test("SendLosslessPacketInvalid") {
    interceptWithTox(ToxFriendCustomPacketException.Code.INVALID)(
      _.friendSendLosslessPacket(0, ToxLosslessPacket.unsafeFromByteArray(Array[Byte](159.toByte)))
    )
  }

  test("SendLossyPacketInvalid") {
    interceptWithTox(ToxFriendCustomPacketException.Code.INVALID)(
      _.friendSendLossyPacket(0, ToxLossyPacket.unsafeFromByteArray(Array[Byte](199.toByte)))
    )
  }

  test("SendLosslessPacketEmpty") {
    interceptWithTox(ToxFriendCustomPacketException.Code.EMPTY)(
      _.friendSendLosslessPacket(1, ToxLosslessPacket.unsafeFromByteArray(Array[Byte]()))
    )
  }

  test("SendLossyPacketEmpty") {
    interceptWithTox(ToxFriendCustomPacketException.Code.EMPTY)(
      _.friendSendLossyPacket(1, ToxLossyPacket.unsafeFromByteArray(Array[Byte]()))
    )
  }

  test("SendLosslessPacketNull") {
    interceptWithTox(ToxFriendCustomPacketException.Code.NULL)(
      _.friendSendLosslessPacket(1, ToxLosslessPacket.unsafeFromByteArray(null))
    )
  }

  test("SendLossyPacketNull") {
    interceptWithTox(ToxFriendCustomPacketException.Code.NULL)(
      _.friendSendLossyPacket(1, ToxLossyPacket.unsafeFromByteArray(null))
    )
  }

  test("SendLosslessPacketTooLong") {
    interceptWithTox(ToxFriendCustomPacketException.Code.TOO_LONG)(
      _.friendSendLosslessPacket(
        0,
        ToxLosslessPacket.unsafeFromByteArray(160.toByte +: Array.ofDim[Byte](ToxCoreConstants.MaxCustomPacketSize))
      )
    )
  }

  test("SendLossyPacketTooLong") {
    interceptWithTox(ToxFriendCustomPacketException.Code.TOO_LONG)(
      _.friendSendLossyPacket(
        0,
        ToxLossyPacket.unsafeFromByteArray(200.toByte +: Array.ofDim[Byte](ToxCoreConstants.MaxCustomPacketSize))
      )
    )
  }

}
