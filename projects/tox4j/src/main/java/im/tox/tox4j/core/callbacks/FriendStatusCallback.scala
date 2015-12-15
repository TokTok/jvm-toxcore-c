package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.core.enums.ToxUserStatus
import org.jetbrains.annotations.NotNull

/**
 * This event is triggered when a friend changes their user status.
 */
trait FriendStatusCallback[ToxCoreState] {
  /**
   * @param friendNumber The friend number of the friend whose user status changed.
   * @param status The new user status.
   */
  def friendStatus(
    friendNumber: ToxFriendNumber, @NotNull status: ToxUserStatus
  )(state: ToxCoreState): ToxCoreState = state
}
