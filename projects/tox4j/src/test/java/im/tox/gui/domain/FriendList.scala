package im.tox.gui.domain

import javax.swing._

import im.tox.tox4j.core.data.{ToxFriendNumber, ToxPublicKey}
import im.tox.tox4j.core.enums.{ToxConnection, ToxUserStatus}

import scala.collection.mutable.ArrayBuffer

final class FriendList extends AbstractListModel[Friend] {

  private val friends = new ArrayBuffer[Friend]

  /**
   * Add a friend to the friend list with the associated public key.
   *
   * @param friendNumber Friend number from toxcore.
   * @param publicKey Public key as stable identifier for the friend.
   */
  def add(friendNumber: ToxFriendNumber, publicKey: ToxPublicKey): Unit = {
    while (friends.size <= friendNumber.value) {
      friends += null
    }

    val oldFriend = friends(friendNumber.value)
    if (oldFriend == null || oldFriend.publicKey.value.deep != publicKey.value.deep) {
      friends(friendNumber.value) = new Friend(publicKey)
    }

    fireIntervalAdded(this, friendNumber.value, friendNumber.value)
  }

  override def getSize: Int = friends.size
  override def getElementAt(index: Int): Friend = friends(index)

  def setName(friendNumber: ToxFriendNumber, name: String): Unit = {
    friends(friendNumber.value).name = name
    fireContentsChanged(this, friendNumber.value, friendNumber.value)
  }

  def setConnectionStatus(friendNumber: ToxFriendNumber, connectionStatus: ToxConnection): Unit = {
    friends(friendNumber.value).connectionStatus = connectionStatus
    fireContentsChanged(this, friendNumber.value, friendNumber.value)
  }

  def setStatus(friendNumber: ToxFriendNumber, status: ToxUserStatus): Unit = {
    friends(friendNumber.value).status = status
    fireContentsChanged(this, friendNumber.value, friendNumber.value)
  }

  def setStatusMessage(friendNumber: ToxFriendNumber, message: String): Unit = {
    friends(friendNumber.value).statusMessage = message
    fireContentsChanged(this, friendNumber.value, friendNumber.value)
  }

  def setTyping(friendNumber: ToxFriendNumber, isTyping: Boolean): Unit = {
    friends(friendNumber.value).typing = isTyping
    fireContentsChanged(this, friendNumber.value, friendNumber.value)
  }

}
