package im.tox.tox4j.core.data

import im.tox.core.typesafe.{KeyCompanion, Security}
import im.tox.tox4j.core.ToxCoreConstants

final case class ToxFileId private (value: Array[Byte]) extends AnyVal {
  def toHexString: String = ToxFileId.toHexString(this)
  override def toString: String = s"$productPrefix($toHexString)"
}

case object ToxFileId extends KeyCompanion[ToxFileId, Security.Sensitive](
  ToxCoreConstants.FileIdLength,
  _.value
) {

  val empty = new ToxFileId(Array.empty)

  override def unsafeFromValue(value: Array[Byte]): ToxFileId = new ToxFileId(value)

}
