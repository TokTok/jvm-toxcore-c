package im.tox.tox4j.core.exceptions

import im.tox.tox4j.core._
import im.tox.tox4j.testing.ToxTestMixin
import org.scalatest.FunSuite

final class ToxFriendAddExceptionTest extends FunSuite with ToxTestMixin {
  private val validAddress = ToxCoreFactory.withTox(_.getAddress)

  test("InvalidAddress1") {
    intercept[IllegalArgumentException] {
      ToxCoreFactory.withTox(
        _.addFriend(
          ToxFriendAddress.unsafeFromByteArray(Array.ofDim[Byte](1)),
          ToxFriendRequestMessage.unsafeFromByteArray(Array.ofDim[Byte](1))
        )
      )
    }
  }

  test("InvalidAddress2") {
    intercept[IllegalArgumentException] {
      ToxCoreFactory.withTox(
        _.addFriend(
          ToxFriendAddress.unsafeFromByteArray(new Array[Byte](ToxCoreConstants.AddressSize - 1)),
          ToxFriendRequestMessage.unsafeFromByteArray(new Array[Byte](1))
        )
      )
    }
  }

  test("InvalidAddress3") {
    intercept[IllegalArgumentException] {
      ToxCoreFactory.withTox(
        _.addFriend(
          ToxFriendAddress.unsafeFromByteArray(new Array[Byte](ToxCoreConstants.AddressSize + 1)),
          ToxFriendRequestMessage.unsafeFromByteArray(new Array[Byte](1))
        )
      )
    }
  }

  test("Null1") {
    interceptWithTox(ToxFriendAddException.Code.NULL)(
      _.addFriend(
        ToxFriendAddress.unsafeFromByteArray(null),
        ToxFriendRequestMessage.unsafeFromByteArray(new Array[Byte](1))
      )
    )
  }

  test("Null2") {
    interceptWithTox(ToxFriendAddException.Code.NULL)(
      _.addFriend(validAddress, ToxFriendRequestMessage.unsafeFromByteArray(null))
    )
  }

  test("Not_TooLong1") {
    ToxCoreFactory.withTox(
      _.addFriend(
        validAddress,
        ToxFriendRequestMessage.unsafeFromByteArray(new Array[Byte](ToxCoreConstants.MaxFriendRequestLength - 1))
      )
    )
  }

  test("Not_TooLong2") {
    ToxCoreFactory.withTox(
      _.addFriend(
        validAddress,
        ToxFriendRequestMessage.unsafeFromByteArray(new Array[Byte](ToxCoreConstants.MaxFriendRequestLength))
      )
    )
  }

  test("TooLong") {
    interceptWithTox(ToxFriendAddException.Code.TOO_LONG)(
      _.addFriend(
        validAddress,
        ToxFriendRequestMessage.unsafeFromByteArray(new Array[Byte](ToxCoreConstants.MaxFriendRequestLength + 1))
      )
    )
  }

  test("NoMessage") {
    interceptWithTox(ToxFriendAddException.Code.NO_MESSAGE)(
      _.addFriend(
        validAddress,
        ToxFriendRequestMessage.unsafeFromByteArray("".getBytes)
      )
    )
  }

  test("OwnKey") {
    interceptWithTox(ToxFriendAddException.Code.OWN_KEY) { tox =>
      tox.addFriend(
        tox.getAddress,
        ToxFriendRequestMessage.unsafeFromByteArray("hello".getBytes)
      )
    }
  }

  test("AlreadySent") {
    interceptWithTox(ToxFriendAddException.Code.ALREADY_SENT) { tox =>
      tox.addFriend(
        validAddress,
        ToxFriendRequestMessage.unsafeFromByteArray("hello".getBytes)
      )
      tox.addFriend(
        validAddress,
        ToxFriendRequestMessage.unsafeFromByteArray("hello".getBytes)
      )
    }
  }

  test("BadChecksum") {
    interceptWithTox(ToxFriendAddException.Code.BAD_CHECKSUM)(
      _.addFriend(
        ToxFriendAddress.unsafeFromByteArray(validAddress.value.updated(0, (validAddress.value(0) + 1).toByte)),
        ToxFriendRequestMessage.unsafeFromByteArray("hello".getBytes)
      )
    )
  }

  test("SetNewNospam") {
    interceptWithTox(ToxFriendAddException.Code.SET_NEW_NOSPAM) { tox =>
      ToxCoreFactory.withTox { friend =>
        friend.setNospam(12345678)
        tox.addFriend(friend.getAddress, ToxFriendRequestMessage.unsafeFromByteArray("hello".getBytes))
        friend.setNospam(87654321)
        tox.addFriend(friend.getAddress, ToxFriendRequestMessage.unsafeFromByteArray("hello".getBytes))
      }
    }
  }

}
