package im.tox.core.crypto

import im.tox.core.ModuleCompanion
import im.tox.core.typesafe.Security
import scodec.bits.{BitVector, ByteVector}
import scodec.codecs._

import scala.language.implicitConversions

final case class PlainText[+S <: Security](private val value: ByteVector) extends AnyVal {

  private[core] def unsafeIgnoreSecurity: PlainText[Security.NonSensitive] = PlainText(value)

}

case object PlainText extends ModuleCompanion[PlainText[Security.NonSensitive], Security.NonSensitive] {

  override val codec = bytes.xmap[PlainText[Security.NonSensitive]](PlainText.apply, _.value)

  object Conversions {

    final case class NonSensitiveConversions(private val value: ByteVector) extends AnyVal {
      def toByteVector: ByteVector = value
      def toBitVector: BitVector = toByteVector.toBitVector
      def toByteArray: Array[Byte] = toByteVector.toArray
      def toSeq: Seq[Byte] = toByteVector.toSeq
      def size: Int = value.size
    }

    implicit def plainTextNonSensitiveConversions(plainText: PlainText[Security.NonSensitive]): NonSensitiveConversions = {
      NonSensitiveConversions(plainText.value)
    }

    final case class SensitiveConversions(private val value: ByteVector) extends AnyVal {
      def size: Int = value.size
    }

    implicit def plainTextSensitiveConversions(plainText: PlainText[Security.Sensitive]): SensitiveConversions = {
      SensitiveConversions(plainText.value)
    }

  }

}
