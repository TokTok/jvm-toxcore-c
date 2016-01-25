package im.tox.core.crypto

import java.io.{DataInputStream, DataOutput}

import com.google.common.io.ByteStreams
import im.tox.core.ModuleCompanion
import im.tox.core.error.DecoderError
import scodec.bits.ByteVector

import scalaz.{\/, \/-}

final case class PlainText(data: ByteVector) extends AnyVal

object PlainText extends ModuleCompanion[PlainText] {

  override def write(self: PlainText, packetData: DataOutput): Unit = {
    packetData.write(self.data.toArray)
  }

  override def read(packetData: DataInputStream): DecoderError \/ PlainText = {
    \/-(PlainText(ByteVector.view(ByteStreams.toByteArray(packetData))))
  }

}
