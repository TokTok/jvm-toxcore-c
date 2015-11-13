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
          ToxFriendAddress.unsafeFromByteArray(Array.ofDim(1)),
          ToxFriendRequestMessage.fromByteArray(Array.ofDim(1)).get
        )
      )
    }
  }

  test("InvalidAddress2") {
    intercept[IllegalArgumentException] {
      ToxCoreFactory.withTox(
        _.addFriend(
          ToxFriendAddress.unsafeFromByteArray(Array.ofDim(ToxCoreConstants.AddressSize - 1)),
          ToxFriendRequestMessage.fromByteArray(Array.ofDim(1)).get
        )
      )
    }
  }

  test("InvalidAddress3") {
    intercept[IllegalArgumentException] {
      ToxCoreFactory.withTox(
        _.addFriend(
          ToxFriendAddress.unsafeFromByteArray(Array.ofDim(ToxCoreConstants.AddressSize + 1)),
          ToxFriendRequestMessage.fromByteArray(Array.ofDim(1)).get
        )
      )
    }
  }

  test("Null1") {
    interceptWithTox(ToxFriendAddException.Code.NULL)(
      _.addFriend(
        ToxFriendAddress.unsafeFromByteArray(null),
        ToxFriendRequestMessage.fromByteArray(Array.ofDim(1)).get
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
        ToxFriendRequestMessage.fromByteArray(Array.ofDim(ToxCoreConstants.MaxFriendRequestLength - 1)).get
      )
    )
  }

  test("Not_TooLong2") {
    ToxCoreFactory.withTox(
      _.addFriend(
        validAddress,
        ToxFriendRequestMessage.fromByteArray(Array.ofDim(ToxCoreConstants.MaxFriendRequestLength)).get
      )
    )
  }

  test("TooLong") {
    interceptWithTox(ToxFriendAddException.Code.TOO_LONG)(
      _.addFriend(
        validAddress,
        ToxFriendRequestMessage.unsafeFromByteArray(Array.ofDim(ToxCoreConstants.MaxFriendRequestLength + 1))
      )
    )
  }

  test("NoMessage") {
    interceptWithTox(ToxFriendAddException.Code.NO_MESSAGE)(
      _.addFriend(
        validAddress,
        ToxFriendRequestMessage.fromString("").get
      )
    )
  }

  test("OwnKey") {
    interceptWithTox(ToxFriendAddException.Code.OWN_KEY) { tox =>
      tox.addFriend(
        tox.getAddress,
        ToxFriendRequestMessage.fromString("hello").get
      )
    }
  }

  test("AlreadySent") {
    interceptWithTox(ToxFriendAddException.Code.ALREADY_SENT) { tox =>
      tox.addFriend(
        validAddress,
        ToxFriendRequestMessage.fromString("hello").get
      )
      tox.addFriend(
        validAddress,
        ToxFriendRequestMessage.fromString("hello").get
      )
    }
  }

  test("BadChecksum") {
    interceptWithTox(ToxFriendAddException.Code.BAD_CHECKSUM)(
      _.addFriend(
        ToxFriendAddress.unsafeFromByteArray(validAddress.value.updated(0, (validAddress.value(0) + 1).toByte)),
        ToxFriendRequestMessage.fromString("hello").get
      )
    )
  }

  test("SetNewNospam") {
    interceptWithTox(ToxFriendAddException.Code.SET_NEW_NOSPAM) { tox =>
      ToxCoreFactory.withTox { friend =>
        friend.setNospam(12345678)
        tox.addFriend(friend.getAddress, ToxFriendRequestMessage.fromString("hello").get)
        friend.setNospam(87654321)
        tox.addFriend(friend.getAddress, ToxFriendRequestMessage.fromString("hello").get)
      }
    }
  }

}
