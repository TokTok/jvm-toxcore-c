package im.tox.tox4j.core.exceptions

import im.tox.tox4j.core.ToxFriendMessage
import im.tox.tox4j.core.enums.ToxMessageType
import im.tox.tox4j.testing.ToxTestMixin
import org.scalatest.FunSuite

final class ToxFriendSendMessageExceptionTest extends FunSuite with ToxTestMixin {

  test("SendMessageNotFound") {
    interceptWithTox(ToxFriendSendMessageException.Code.FRIEND_NOT_FOUND)(
      _.friendSendMessage(1, ToxMessageType.NORMAL, 0, ToxFriendMessage.fromString("hello").get)
    )
  }

  test("SendMessageNotConnected") {
    interceptWithTox(ToxFriendSendMessageException.Code.FRIEND_NOT_CONNECTED)(
      _.friendSendMessage(0, ToxMessageType.NORMAL, 0, ToxFriendMessage.fromString("hello").get)
    )
  }

  test("SendMessageNull") {
    interceptWithTox(ToxFriendSendMessageException.Code.NULL)(
      _.friendSendMessage(0, ToxMessageType.NORMAL, 0, ToxFriendMessage.unsafeFromValue(null))
    )
  }

  test("SendMessageEmpty") {
    interceptWithTox(ToxFriendSendMessageException.Code.EMPTY)(
      _.friendSendMessage(0, ToxMessageType.NORMAL, 0, ToxFriendMessage.unsafeFromValue("".getBytes))
    )
  }

}
