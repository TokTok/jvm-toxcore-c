package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.core.data.ToxNickname

/** This event is triggered when a friend changes their name. */
interface FriendNameCallback<ToxCoreState> {
  /**
   * @param friendNumber The friend number of the friend whose name changed.
   * @param name The new nickname.
   */
  fun friendName(
      friendNumber: ToxFriendNumber,
      name: ToxNickname,
      state: ToxCoreState
  ): ToxCoreState = state
}
