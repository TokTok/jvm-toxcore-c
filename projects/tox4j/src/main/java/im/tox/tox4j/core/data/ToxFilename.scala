package im.tox.tox4j.core.data

import im.tox.core.typesafe.VariableSizeByteArrayCompanion
import im.tox.tox4j.core.ToxCoreConstants

final case class ToxFilename private (value: Array[Byte]) extends AnyVal {
  override def toString: String = new String(value)
  def toHexString: String = ToxFilename.toHexString(this)
}

case object ToxFilename extends VariableSizeByteArrayCompanion[ToxFilename](
  ToxCoreConstants.MaxFilenameLength,
  _.value
) {

  override def unsafeFromValue(value: Array[Byte]): ToxFilename = new ToxFilename(value)

}
