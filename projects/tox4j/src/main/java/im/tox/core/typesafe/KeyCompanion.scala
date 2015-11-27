package im.tox.core.typesafe

import im.tox.core.Functional
import im.tox.core.error.CoreError

import scalaz.{-\/, \/, \/-}

abstract class KeyCompanion[T <: AnyVal, S <: Security](
    Size: Int,
    toValue: T => Array[Byte]
) extends FixedSizeByteArrayCompanion[T, S](Size, toValue) {

  final def toHexString(self: T): String = {
    toValue(self).iterator.map(c => f"$c%02X").mkString
  }

  /**
   * Parses a human-readable hex string as binary key.
   */
  final def fromHexString(string: String): CoreError \/ T = {
    for {
      bytes <- parseHexEncodedString {
        // Class name is SomeClass$ because this is the companion object, but T
        // is SomeClass, so we drop the $.
        val className = getClass.getSimpleName.dropRight(1)
        if (string.startsWith(className)) {
          string.substring(className.length + 1, string.length - 1)
        } else {
          string
        }
      }
      self <- fromValue(bytes)
    } yield {
      self
    }
  }

  private def parseHexEncodedString(id: String): CoreError \/ Array[Byte] = {
    Functional.foldDisjunctionList((0 until id.length / 2).reverseMap { i =>
      for {
        hiNibble <- fromHexDigit(id, i * 2)
        loNibble <- fromHexDigit(id, i * 2 + 1)
      } yield {
        ((hiNibble << 4) + loNibble).toByte
      }
    }).map(_.toArray)
  }

  private def fromHexDigit(id: String, position: Int): CoreError \/ Byte = {
    val c = id.charAt(position)
    val digit =
      if (false) { \/-(0) }
      else if ('0' to '9' contains c) { \/-(c - '0') }
      else if ('A' to 'F' contains c) { \/-(c - 'A' + 10) }
      else if ('a' to 'f' contains c) { \/-(c - 'a' + 10) }
      else { -\/(CoreError.InvalidFormat(s"Non-hex digit character at position $position: $c")) }
    digit.map(_.toByte)
  }

}
