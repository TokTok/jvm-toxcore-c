package im.tox.gui.domain

import java.io.{ Closeable, File, FileNotFoundException, IOException }

abstract class FileTransfer(val file: File) extends Closeable {
  @throws[FileNotFoundException]
  def resume()

  @throws[IOException]
  def read(position: Long, length: Int): Array[Byte]

  @throws[IOException]
  def close()

  @throws[IOException]
  def write(position: Long, data: Array[Byte])
}
