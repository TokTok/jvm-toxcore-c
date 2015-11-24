package im.tox.tox4j.core

import im.tox.core.typesafe.{KeyCompanion, Security}

final case class ToxFileId private (value: Array[Byte]) extends AnyVal {
  def readable: String = ToxFileId.toString(value)
  override def toString: String = {
    s"${getClass.getSimpleName}($readable)"
  }
}

object ToxFileId extends KeyCompanion[ToxFileId, Security.Sensitive](ToxCoreConstants.FileIdLength) {

  val empty = new ToxFileId(Array.empty)

  override def unsafeFromValue(value: Array[Byte]): ToxFileId = new ToxFileId(value)
  override def toValue(self: ToxFileId): Array[Byte] = self.value.toArray

}
