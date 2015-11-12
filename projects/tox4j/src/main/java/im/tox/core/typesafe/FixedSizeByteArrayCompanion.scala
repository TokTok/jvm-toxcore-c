package im.tox.core.typesafe

import scodec.bits.ByteVector
import scodec.codecs._

abstract class FixedSizeByteArrayCompanion[T <: AnyVal, S <: Security](val Size: Int) extends ByteArrayCompanion[T, S] {

  /**
   * [char array (node_id), length=32 bytes]
   */
  override val codec = bytes(Size).xmap[T](
    byteVector => unsafeFromByteArray(byteVector.toArray),
    self => ByteVector(toByteArray(self))
  )

  def validate(value: Array[Byte]): Boolean = true

  final override def fromByteArray(value: Array[Byte]): Option[T] = {
    for {
      () <- require(value.length == Size)
      () <- require(validate(value))
    } yield {
      unsafeFromByteArray(value)
    }
  }

}
