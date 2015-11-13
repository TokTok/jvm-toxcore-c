package im.tox.tox4j.core.callbacks

import im.tox.tox4j.TestConstants.Iterations
import im.tox.tox4j.core.ToxFriendMessage
import im.tox.tox4j.core.enums.{ToxConnection, ToxMessageType}
import im.tox.tox4j.testing.autotest.{AliceBobTest, AliceBobTestBase}

final class FriendReadReceiptCallbackTest extends AliceBobTest {

  protected override def ignoreTimeout = true

  override type State = Set[Int]
  override def initialState: State = Set.empty[Int]

  protected override def newChatClient(name: String, expectedFriendName: String) = new ChatClient(name, expectedFriendName) {

    override def friendConnectionStatus(friendNumber: Int, connectionStatus: ToxConnection)(state: ChatState): ChatState = {
      super.friendConnectionStatus(friendNumber, connectionStatus)(state)
      if (connectionStatus != ToxConnection.NONE) {
        state.addTask { (tox, state) =>
          debug(s"Sending $Iterations messages")
          assert(state.get.isEmpty)
          val pendingIds = (0 until Iterations).foldLeft(state.get) {
            case (receipts, i) =>
              val receipt = tox.friendSendMessage(
                friendNumber, ToxMessageType.NORMAL, 0,
                ToxFriendMessage.fromString(String.valueOf(i)).get
              )
              assert(!receipts.contains(receipt))
              receipts + receipt
          }
          assert(pendingIds.size == Iterations)
          state.set(pendingIds)
        }
      } else {
        state
      }
    }

    override def friendReadReceipt(friendNumber: Int, messageId: Int)(state: ChatState): ChatState = {
      assert(friendNumber == AliceBobTestBase.FriendNumber)

      assert(state.get.contains(messageId))
      val pendingIds = state.get - messageId
      if (pendingIds.isEmpty) {
        state.finish
      } else {
        state.set(pendingIds)
      }
    }

  }

}
