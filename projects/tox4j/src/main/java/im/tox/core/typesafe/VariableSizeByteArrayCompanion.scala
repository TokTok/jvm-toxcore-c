package im.tox.core.typesafe

import im.tox.core.error.CoreError
import scodec.codecs._
import scodec.{Attempt, Err}

abstract class VariableSizeByteArrayCompanion[T <: AnyVal](val MaxSize: Int) extends ByteArrayCompanion[T] {

  final override val codec = variableSizeBytes(uint16, bytes).exmap[T](
    { bytes => Attempt.fromOption(fromByteArray(bytes.toArray), new Err.General(s"Validation failed for $this")) },
    { self => CoreError.toAttempt(toBytes(self)) }
  )

  def validate(value: Array[Byte]): Boolean = true

  final override def fromByteArray(value: Array[Byte]): Option[T] = {
    for {
      () <- require(value.length <= MaxSize)
      () <- require(validate(value))
    } yield {
      unsafeFromByteArray(value)
    }
  }

}
