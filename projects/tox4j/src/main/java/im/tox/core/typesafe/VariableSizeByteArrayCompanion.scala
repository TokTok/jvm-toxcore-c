package im.tox.core.typesafe

import java.io.DataInputStream

import im.tox.core.error.DecoderError

import scalaz.{-\/, \/, \/-}

abstract class VariableSizeByteArrayCompanion[T <: AnyVal](val MaxSize: Int) extends ByteArrayCompanion[T] {

  def validate(value: Array[Byte]): Boolean = true

  final override def fromByteArray(value: Array[Byte]): Option[T] = {
    for {
      () <- require(value.length <= MaxSize)
      () <- require(validate(value))
    } yield {
      unsafeFromByteArray(value)
    }
  }

  final override def read(packetData: DataInputStream): DecoderError \/ T = {
    val size = packetData.readUnsignedShort()
    if (size <= MaxSize) {
      val data = Array.ofDim[Byte](size)
      packetData.read(data)
      \/-(unsafeFromByteArray(data))
    } else {
      -\/(DecoderError.InvalidFormat(s"Message too large for $this; MaxSize = $MaxSize"))
    }
  }

}
