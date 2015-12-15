package im.tox.gui.domain

import java.io.{File, IOException}
import java.util.NoSuchElementException
import javax.swing.AbstractListModel

import im.tox.tox4j.core.data.ToxFriendNumber

import scala.collection.mutable.ArrayBuffer

final class FileTransferModel extends AbstractListModel[FileTransfer] {

  private val transfers = new ArrayBuffer[ArrayBuffer[FileTransfer]]

  override def getSize: Int = {
    var size = 0
    for (list <- transfers) {
      size += list.length
    }
    size
  }

  @SuppressWarnings(Array("org.brianmckenna.wartremover.warts.Return"))
  override def getElementAt(index: Int): FileTransfer = {
    var position = 0
    for (list <- transfers) {
      if (position + list.length > index) {
        return list(index - position) // scalastyle:ignore return
      }
      position += list.length
    }
    throw new NoSuchElementException(String.valueOf(index))
  }

  private def ensureFileNumber(friendNumber: ToxFriendNumber, fileNumber: Int): ArrayBuffer[FileTransfer] = {
    while (transfers.length <= friendNumber.value) {
      transfers += new ArrayBuffer[FileTransfer]
    }
    val list = transfers(friendNumber.value)
    while (list.size <= fileNumber) {
      list += null
    }
    list
  }

  def addOutgoing(friendNumber: ToxFriendNumber, file: File, fileNumber: Int): Unit = {
    val list = ensureFileNumber(friendNumber, fileNumber)
    list(fileNumber) = new FileTransferOutgoing(file)
  }

  def addIncoming(friendNumber: ToxFriendNumber, fileNumber: Int, kind: Int, fileSize: Long, file: File): Unit = {
    val list = ensureFileNumber(friendNumber, fileNumber)
    list(fileNumber) = new FileTransferIncoming(file, kind, fileSize)
  }

  def get(friendNumber: ToxFriendNumber, fileNumber: Int): FileTransfer = {
    transfers(friendNumber.value)(fileNumber)
  }

  @throws[IOException]
  def remove(friendNumber: ToxFriendNumber, fileNumber: Int): Unit = {
    get(friendNumber, fileNumber).close()
  }

}
