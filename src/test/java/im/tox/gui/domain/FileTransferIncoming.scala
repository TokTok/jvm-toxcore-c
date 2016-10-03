package im.tox.gui.domain

import java.io.{ File, FileNotFoundException, IOException, RandomAccessFile }

import org.slf4j.LoggerFactory

@SuppressWarnings(Array("org.wartremover.warts.Equals"))
final class FileTransferIncoming(file: File, kind: Int, size: Long) extends FileTransfer(file) {

  private val logger = LoggerFactory.getLogger(classOf[FileTransferIncoming])

  private var input: RandomAccessFile = null

  @throws[FileNotFoundException]
  def resume(): Unit = {
    if (this.input != null) {
      logger.warn("RESUME received with open file; not re-opening")
    } else {
      this.input = new RandomAccessFile(file, "rw")
    }
  }

  @throws[IOException]
  def read(position: Long, length: Int): Array[Byte] = {
    throw new IOException("Cannot read from incoming file")
  }

  @throws[IOException]
  def close(): Unit = {
    input.close()
  }

  @throws[IOException]
  def write(position: Long, data: Array[Byte]): Unit = {
    if (input == null) {
      logger.warn("Writing before receiving a RESUME control; opening file anyway")
      input = new RandomAccessFile(file, "w")
    }
    input.seek(position)
    input.write(data)
  }

}
