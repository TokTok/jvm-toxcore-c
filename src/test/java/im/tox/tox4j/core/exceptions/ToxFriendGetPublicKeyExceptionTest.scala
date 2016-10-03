package im.tox.tox4j.core.exceptions

import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.testing.ToxTestMixin
import org.scalatest.FunSuite

final class ToxFriendGetPublicKeyExceptionTest extends FunSuite with ToxTestMixin {

  private val friendNumber = ToxFriendNumber.fromInt(0).get
  private val badFriendNumber = ToxFriendNumber.fromInt(1).get

  test("FriendNotFound") {
    interceptWithTox(ToxFriendGetPublicKeyException.Code.FRIEND_NOT_FOUND)(
      _.getFriendPublicKey(badFriendNumber)
    )
  }

}
