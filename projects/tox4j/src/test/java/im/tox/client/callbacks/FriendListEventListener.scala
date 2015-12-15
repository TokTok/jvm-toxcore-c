package im.tox.client.callbacks

import im.tox.client.{Friend, TestState}
import im.tox.tox4j.ToxEventListener
import im.tox.tox4j.core.data._
import im.tox.tox4j.core.enums.{ToxConnection, ToxUserStatus}

/**
 * Handles friend requests and friend information updates like nickname and status message.
 */
final class FriendListEventListener(id: Int)
    extends IdLogging(id) with ToxEventListener[TestState] {

  private def updateFriend(friendNumber: ToxFriendNumber, state: TestState)(update: Friend => Friend): TestState = {
    val updated = update(state.friends.getOrElse(friendNumber, Friend()))
    state.copy(friends = state.friends + (friendNumber -> updated))
  }

  override def friendStatus(friendNumber: ToxFriendNumber, status: ToxUserStatus)(state: TestState): TestState = {
    updateFriend(friendNumber, state)(_.copy(status = status))
  }

  override def friendTyping(friendNumber: ToxFriendNumber, isTyping: Boolean)(state: TestState): TestState = {
    updateFriend(friendNumber, state)(_.copy(typing = isTyping))
  }

  override def friendName(friendNumber: ToxFriendNumber, name: ToxNickname)(state: TestState): TestState = {
    updateFriend(friendNumber, state)(_.copy(name = name))
  }

  override def friendStatusMessage(friendNumber: ToxFriendNumber, message: ToxStatusMessage)(state: TestState): TestState = {
    updateFriend(friendNumber, state)(_.copy(statusMessage = message))
  }

  override def friendConnectionStatus(friendNumber: ToxFriendNumber, connectionStatus: ToxConnection)(state: TestState): TestState = {
    updateFriend(friendNumber, state)(_.copy(connection = connectionStatus))
  }

  override def friendRequest(publicKey: ToxPublicKey, timeDelta: Int, message: ToxFriendRequestMessage)(state: TestState): TestState = {
    state.addTask { (tox, av, state) =>
      logInfo(s"Adding $publicKey as friend")
      tox.addFriendNorequest(publicKey)
      state.copy(
        profile = state.profile.addFriendKeys(publicKey.toHexString)
      )
    }
  }

}
