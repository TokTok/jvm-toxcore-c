package im.tox.gui.domain

import java.io.{ File, FileNotFoundException, IOException, RandomAccessFile }

final class FileTransferOutgoing(file: File) extends FileTransfer(file) {
  private var input: RandomAccessFile = null

  @throws[FileNotFoundException]
  def resume(): Unit = {
    input = new RandomAccessFile(file, "r")
  }

  @throws[IOException]
  def read(position: Long, length: Int): Array[Byte] = {
    input.seek(position)

    val data = Array.ofDim[Byte](length)
    val readLength = input.read(data)

    if (data.length > readLength) {
      data.slice(0, readLength)
    } else {
      data
    }
  }

  @throws[IOException]
  def close(): Unit = {
    input.close()
  }

  @throws[IOException]
  def write(position: Long, data: Array[Byte]): Unit = {
    throw new IOException("Cannot write to outgoing file")
  }
}
