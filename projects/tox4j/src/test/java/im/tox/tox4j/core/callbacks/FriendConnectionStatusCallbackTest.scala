package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.core.enums.ToxConnection
import im.tox.tox4j.testing.autotest.AutoTestSuite

final class FriendConnectionStatusCallbackTest extends AutoTestSuite {

  type S = Unit

  object Handler extends EventListener(()) {

    override def friendConnectionStatus(friendNumber: ToxFriendNumber, connectionStatus: ToxConnection)(state: State): State = {
      super.friendConnectionStatus(friendNumber, connectionStatus)(state)
      if (connectionStatus != ToxConnection.NONE) {
        state.finish
      } else {
        state
      }
    }

  }

}
