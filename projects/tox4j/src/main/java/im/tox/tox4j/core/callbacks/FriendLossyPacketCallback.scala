package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.{ToxFriendNumber, ToxLossyPacket}
import org.jetbrains.annotations.NotNull

/**
 * This event is triggered when a custom lossy packet arrives from a friend.
 */
trait FriendLossyPacketCallback[ToxCoreState] {
  /**
   * @param friendNumber The friend number of the friend who sent a lossy packet.
   * @param data A byte array containing the received packet data. The first byte is the packet id.
   */
  def friendLossyPacket(
    friendNumber: ToxFriendNumber, @NotNull data: ToxLossyPacket
  )(state: ToxCoreState): ToxCoreState = state
}
