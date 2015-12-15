package im.tox.tox4j.core.exceptions

import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.testing.ToxTestMixin
import org.scalatest.FunSuite

final class ToxFriendDeleteExceptionTest extends FunSuite with ToxTestMixin {

  test("DeleteFriendTwice") {
    interceptWithTox(ToxFriendDeleteException.Code.FRIEND_NOT_FOUND) { tox =>
      addFriends(tox, 4)
      assert(tox.getFriendList sameElements Array(0, 1, 2, 3, 4))
      tox.deleteFriend(ToxFriendNumber.fromInt(2).get)
      assert(tox.getFriendList sameElements Array(0, 1, 3, 4))
      tox.deleteFriend(ToxFriendNumber.fromInt(2).get)
    }
  }

  test("DeleteNonExistentFriend") {
    interceptWithTox(ToxFriendDeleteException.Code.FRIEND_NOT_FOUND)(
      _.deleteFriend(ToxFriendNumber.fromInt(1).get)
    )
  }

}
