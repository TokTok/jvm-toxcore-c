package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.{ToxFriendNumber, ToxLosslessPacket}
import org.jetbrains.annotations.NotNull

/**
 * This event is triggered when a custom lossless packet arrives from a friend.
 */
trait FriendLosslessPacketCallback[ToxCoreState] {
  /**
   * @param friendNumber The friend number of the friend who sent a lossless packet.
   * @param data A byte array containing the received packet data. The first byte is the packet id.
   */
  def friendLosslessPacket(
    friendNumber: ToxFriendNumber, @NotNull data: ToxLosslessPacket
  )(state: ToxCoreState): ToxCoreState = state
}
