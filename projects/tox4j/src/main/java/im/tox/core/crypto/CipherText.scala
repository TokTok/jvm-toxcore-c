package im.tox.core.crypto

import im.tox.core.ModuleCompanion
import im.tox.core.typesafe.Security
import scodec.bits.ByteVector
import scodec.codecs._

final case class CipherText[Payload] private[crypto] (data: ByteVector) extends AnyVal

object CipherText {

  final case class Make[Payload, S <: Security](module: ModuleCompanion[Payload, S])
      extends ModuleCompanion[CipherText[Payload], Security.NonSensitive] {

    override val codec = bytes.xmap[CipherText[Payload]](CipherText.apply, _.data)

  }

}
