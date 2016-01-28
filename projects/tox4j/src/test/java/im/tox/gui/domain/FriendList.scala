package im.tox.gui.domain

import javax.swing._

import im.tox.tox4j.core.ToxPublicKey
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
  def add(friendNumber: Int, publicKey: ToxPublicKey): Unit = {
    while (friends.size <= friendNumber) {
      friends += null
    }

    val oldFriend = friends(friendNumber)
    if (oldFriend == null || oldFriend.publicKey.value.deep != publicKey.value.deep) {
      friends(friendNumber) = new Friend(publicKey)
    }

    fireIntervalAdded(this, friendNumber, friendNumber)
  }

  override def getSize: Int = friends.size
  override def getElementAt(index: Int): Friend = friends(index)

  def setName(friendNumber: Int, name: String): Unit = {
    friends(friendNumber).name = name
    fireContentsChanged(this, friendNumber, friendNumber)
  }

  def setConnectionStatus(friendNumber: Int, connectionStatus: ToxConnection): Unit = {
    friends(friendNumber).connectionStatus = connectionStatus
    fireContentsChanged(this, friendNumber, friendNumber)
  }

  def setStatus(friendNumber: Int, status: ToxUserStatus): Unit = {
    friends(friendNumber).status = status
    fireContentsChanged(this, friendNumber, friendNumber)
  }

  def setStatusMessage(friendNumber: Int, message: String): Unit = {
    friends(friendNumber).statusMessage = message
    fireContentsChanged(this, friendNumber, friendNumber)
  }

  def setTyping(friendNumber: Int, isTyping: Boolean): Unit = {
    friends(friendNumber).typing = isTyping
    fireContentsChanged(this, friendNumber, friendNumber)
  }
}
