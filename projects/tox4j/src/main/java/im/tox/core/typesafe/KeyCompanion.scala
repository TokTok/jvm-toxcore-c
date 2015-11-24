package im.tox.core.typesafe

abstract class KeyCompanion[T <: AnyVal, S <: Security](Size: Int)
    extends FixedSizeByteArrayCompanion[T, S](Size) {

  def toString(bytes: Seq[Byte]): String = {
    bytes.map(c => f"$c%02X").mkString
  }

  override def fromString(string: String): Option[T] = {
    fromValue(parsePublicKey(string))
  }

  private def parsePublicKey(id: String): Array[Byte] = {
    val publicKey = new Array[Byte](id.length / 2)
    publicKey.indices foreach { i =>
      publicKey(i) =
        ((fromHexDigit(id.charAt(i * 2)) << 4) +
          fromHexDigit(id.charAt(i * 2 + 1))).toByte
    }
    publicKey
  }

  private def fromHexDigit(c: Char): Byte = {
    val digit =
      if (false) { 0 }
      else if ('0' to '9' contains c) { c - '0' }
      else if ('A' to 'F' contains c) { c - 'A' + 10 }
      else if ('a' to 'f' contains c) { c - 'a' + 10 }
      else { throw new IllegalArgumentException("Non-hex digit character: " + c) }
    digit.toByte
  }

}
