package im.tox.core.crypto

import im.tox.core.ModuleCompanion
import im.tox.core.typesafe.Security
import scodec.bits.{BitVector, ByteVector}
import scodec.codecs._

import scala.language.implicitConversions

final case class PlainText[+S <: Security](private val value: ByteVector) extends AnyVal {

  /**
   * Convert a [[Security.Sensitive]] [[PlainText]] to a [[Security.NonSensitive]]
   * one. This method can only be called from within a class derived from a
   * [[Security.EvidenceCompanion]] with security type argument [[Security.Sensitive]].
   * In other words, it is safe to convert a sensitive [[PlainText]] to non-sensitive
   * if and only if the containing class is itself security sensitive. As always, it is
   * still up to that class not to leak its information.
   *
   * @param evidence Implicit evidence parameter to prove that the caller is security-sensitive.
   */
  def toNonSensitive(implicit evidence: Security.Evidence[Security.Sensitive]): PlainText[Security.NonSensitive] = {
    PlainText(value)
  }

  private[core] def unsafeIgnoreSecurity: PlainText[Security.NonSensitive] = PlainText(value)

}

object PlainText extends ModuleCompanion[PlainText[Security.NonSensitive], Security.NonSensitive] {

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
