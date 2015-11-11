package im.tox.core.crypto

import im.tox.core.ModuleCompanion
import scodec.bits.ByteVector
import scodec.codecs._

final case class CipherText[Payload] private[crypto] (data: ByteVector) extends AnyVal

object CipherText {

  final case class Make[Payload](module: ModuleCompanion[Payload]) extends ModuleCompanion[CipherText[Payload]] {

    override val codec = bytes.xmap[CipherText[Payload]](CipherText.apply, _.data)

  }

}
