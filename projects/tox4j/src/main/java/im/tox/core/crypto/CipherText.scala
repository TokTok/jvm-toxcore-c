package im.tox.core.crypto

import java.io.{DataInputStream, DataOutput}

import com.google.common.io.ByteStreams
import im.tox.core.ModuleCompanion
import im.tox.core.error.DecoderError
import scodec.bits.ByteVector

import scalaz.{\/, \/-}

final case class CipherText[Payload] private[crypto] (data: ByteVector) extends AnyVal

object CipherText {

  final case class Make[Payload](module: ModuleCompanion[Payload]) extends ModuleCompanion[CipherText[Payload]] {

    override def write(self: CipherText[Payload], packetData: DataOutput): Unit = {
      packetData.write(self.data.toArray)
    }

    override def read(packetData: DataInputStream): DecoderError \/ CipherText[Payload] = {
      \/-(CipherText(ByteVector.view(ByteStreams.toByteArray(packetData))))
    }

  }

}
