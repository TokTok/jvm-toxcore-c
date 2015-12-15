package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.ToxCore
import im.tox.tox4j.core.data.ToxFriendNumber

/**
 * This event is triggered when the friend receives the message sent with
 * [[ToxCore.friendSendMessage]] with the corresponding message ID.
 */
trait FriendReadReceiptCallback[ToxCoreState] {
  /**
   * @param friendNumber The friend number of the friend who received the message.
   * @param messageId The message ID as returned from [[ToxCore.friendSendMessage]] corresponding to the message sent.
   */
  def friendReadReceipt(
    friendNumber: ToxFriendNumber, messageId: Int
  )(state: ToxCoreState): ToxCoreState = state
}
