package im.tox.tox4j.core

import im.tox.core.typesafe.{Security, FixedSizeByteArrayCompanion}

final class ToxFileId private (val value: Array[Byte]) extends AnyVal {
  override def toString: String = {
    "ToxFileId(" + value.map(c => f"$c%02X").mkString + ")"
  }
}

object ToxFileId extends FixedSizeByteArrayCompanion[ToxFileId, Security.Sensitive](ToxCoreConstants.FileIdLength) {

  val empty = new ToxFileId(Array.empty)

  override def unsafeFromValue(value: Array[Byte]): ToxFileId = new ToxFileId(value)
  override def toValue(self: ToxFileId): Array[Byte] = self.value.toArray

}
