package im.tox.core.typesafe

import im.tox.core.crypto.PlainText
import im.tox.core.crypto.PlainText.Conversions._
import im.tox.core.error.CoreError
import scodec.codecs._
import scodec.{Attempt, Err}

abstract class VariableSizeByteArrayCompanion[T <: AnyVal](val MaxSize: Int)
    extends ByteArrayCompanion[T, Security.Sensitive] {

  /**
   * The codec here drops the [[Security]] attribute because the wrapped
   * array itself is protected by [[Security.Sensitive]] in this companion's
   * [[toBytes]].
   */
  final override val codec = variableSizeBytes(uint16, PlainText.codec).exmap[T](
    { bytes =>
      Attempt.fromOption(
        fromByteArray(bytes.toNonSensitive.toByteArray),
        new Err.General(s"Validation failed for $this")
      )
    }, {
      self => CoreError.toAttempt(toBytes(self).map(_.toNonSensitive))
    }
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
