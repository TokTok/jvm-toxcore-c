package im.tox.tox4j.core.exceptions

import im.tox.tox4j.core._
import im.tox.tox4j.core.data.{ ToxFriendAddress, ToxFriendRequestMessage }
import im.tox.tox4j.impl.jni.ToxCoreImplFactory
import im.tox.tox4j.testing.ToxTestMixin
import org.scalatest.FunSuite

final class ToxFriendAddExceptionTest extends FunSuite with ToxTestMixin {

  private val validAddress = ToxCoreImplFactory.withToxUnit(_.getAddress)

  test("InvalidAddress1") {
    intercept[IllegalArgumentException] {
      ToxCoreImplFactory.withToxUnit(
        _.addFriend(
          ToxFriendAddress.unsafeFromValue(Array.ofDim(1)),
          ToxFriendRequestMessage.fromValue(Array.ofDim(1)).toOption.get
        )
      )
    }
  }

  test("InvalidAddress2") {
    intercept[IllegalArgumentException] {
      ToxCoreImplFactory.withToxUnit(
        _.addFriend(
          ToxFriendAddress.unsafeFromValue(Array.ofDim(ToxCoreConstants.AddressSize - 1)),
          ToxFriendRequestMessage.fromValue(Array.ofDim(1)).toOption.get
        )
      )
    }
  }

  test("InvalidAddress3") {
    intercept[IllegalArgumentException] {
      ToxCoreImplFactory.withToxUnit(
        _.addFriend(
          ToxFriendAddress.unsafeFromValue(Array.ofDim(ToxCoreConstants.AddressSize + 1)),
          ToxFriendRequestMessage.fromValue(Array.ofDim(1)).toOption.get
        )
      )
    }
  }

  test("Null1") {
    interceptWithTox(ToxFriendAddException.Code.NULL)(
      _.addFriend(
        ToxFriendAddress.unsafeFromValue(null),
        ToxFriendRequestMessage.fromValue(Array.ofDim(1)).toOption.get
      )
    )
  }

  test("Null2") {
    interceptWithTox(ToxFriendAddException.Code.NULL)(
      _.addFriend(validAddress, ToxFriendRequestMessage.unsafeFromValue(null))
    )
  }

  test("Not_TooLong1") {
    ToxCoreImplFactory.withToxUnit(
      _.addFriend(
        validAddress,
        ToxFriendRequestMessage.fromValue(Array.ofDim(ToxCoreConstants.MaxFriendRequestLength - 1)).toOption.get
      )
    )
  }

  test("Not_TooLong2") {
    ToxCoreImplFactory.withToxUnit(
      _.addFriend(
        validAddress,
        ToxFriendRequestMessage.fromValue(Array.ofDim(ToxCoreConstants.MaxFriendRequestLength)).toOption.get
      )
    )
  }

  test("TooLong") {
    interceptWithTox(ToxFriendAddException.Code.TOO_LONG)(
      _.addFriend(
        validAddress,
        ToxFriendRequestMessage.unsafeFromValue(Array.ofDim(ToxCoreConstants.MaxFriendRequestLength + 1))
      )
    )
  }

  test("NoMessage") {
    interceptWithTox(ToxFriendAddException.Code.NO_MESSAGE)(
      _.addFriend(
        validAddress,
        ToxFriendRequestMessage.fromString("").toOption.get
      )
    )
  }

  test("OwnKey") {
    interceptWithTox(ToxFriendAddException.Code.OWN_KEY) { tox =>
      tox.addFriend(
        tox.getAddress,
        ToxFriendRequestMessage.fromString("hello").toOption.get
      )
    }
  }

  test("AlreadySent") {
    interceptWithTox(ToxFriendAddException.Code.ALREADY_SENT) { tox =>
      tox.addFriend(
        validAddress,
        ToxFriendRequestMessage.fromString("hello").toOption.get
      )
      tox.addFriend(
        validAddress,
        ToxFriendRequestMessage.fromString("hello").toOption.get
      )
    }
  }

  test("BadChecksum") {
    interceptWithTox(ToxFriendAddException.Code.BAD_CHECKSUM)(
      _.addFriend(
        ToxFriendAddress.unsafeFromValue(validAddress.value.updated(0, (validAddress.value(0) + 1).toByte)),
        ToxFriendRequestMessage.fromString("hello").toOption.get
      )
    )
  }

  test("SetNewNospam") {
    interceptWithTox(ToxFriendAddException.Code.SET_NEW_NOSPAM) { tox =>
      ToxCoreImplFactory.withToxUnit { friend =>
        friend.setNospam(12345678)
        tox.addFriend(friend.getAddress, ToxFriendRequestMessage.fromString("hello").toOption.get)
        friend.setNospam(87654321)
        tox.addFriend(friend.getAddress, ToxFriendRequestMessage.fromString("hello").toOption.get)
      }
    }
  }

}
