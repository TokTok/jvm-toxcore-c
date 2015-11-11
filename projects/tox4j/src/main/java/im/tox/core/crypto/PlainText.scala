package im.tox.core.crypto

import im.tox.core.ModuleCompanion
import scodec.bits.ByteVector
import scodec.codecs._

final case class PlainText(data: ByteVector) extends AnyVal

object PlainText extends ModuleCompanion[PlainText] {

  override val codec = bytes.xmap[PlainText](PlainText.apply, _.data)

}
