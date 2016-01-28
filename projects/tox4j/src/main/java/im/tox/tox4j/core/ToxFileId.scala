package im.tox.tox4j.core

import im.tox.core.typesafe.FixedSizeByteArrayCompanion

final class ToxFileId private (val value: Array[Byte]) extends AnyVal {
  override def toString: String = {
    "ToxFileId(" + value.map(c => f"$c%02X").mkString + ")"
  }
}

object ToxFileId extends FixedSizeByteArrayCompanion[ToxFileId](ToxCoreConstants.FileIdLength) {

  val empty = new ToxFileId(Array.empty)

  override def unsafeFromByteArray(value: Array[Byte]): ToxFileId = new ToxFileId(value)
  override def toByteArray(self: ToxFileId): Array[Byte] = self.value.toArray

}
