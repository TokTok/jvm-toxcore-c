package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxLosslessPacket
import im.tox.tox4j.core.enums.ToxConnection
import im.tox.tox4j.testing.autotest.{AliceBobTest, AliceBobTestBase}

final class FriendLosslessPacketCallbackTest extends AliceBobTest {

  override type State = Unit
  override def initialState: State = ()

  protected override def newChatClient(name: String, expectedFriendName: String) = new ChatClient(name, expectedFriendName) {

    override def friendConnectionStatus(friendNumber: Int, connectionStatus: ToxConnection)(state: ChatState): ChatState = {
      super.friendConnectionStatus(friendNumber, connectionStatus)(state)
      if (connectionStatus != ToxConnection.NONE) {
        state.addTask { (tox, state) =>
          val packet = s"_My name is $selfName".getBytes
          packet(0) = 160.toByte
          tox.friendSendLosslessPacket(friendNumber, ToxLosslessPacket.fromValue(packet).get)
          state
        }
      } else {
        state
      }
    }

    override def friendLosslessPacket(friendNumber: Int, packet: ToxLosslessPacket)(state: ChatState): ChatState = {
      val message = new String(packet.value, 1, packet.value.length - 1)
      debug(s"received a lossless packet[id=${packet.value(0)}]: $message")
      assert(friendNumber == AliceBobTestBase.FriendNumber)
      assert(packet.value(0) == 160.toByte)
      assert(message == s"My name is $expectedFriendName")
      state.finish
    }
  }

}
