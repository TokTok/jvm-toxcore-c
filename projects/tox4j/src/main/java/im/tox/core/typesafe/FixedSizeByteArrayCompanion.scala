package im.tox.core.typesafe

import java.io.DataInputStream

import im.tox.core.error.DecoderError

import scalaz.{\/, \/-}

abstract class FixedSizeByteArrayCompanion[T <: AnyVal](val Size: Int) extends ByteArrayCompanion[T] {

  def validate(value: Array[Byte]): Boolean = true

  final override def fromByteArray(value: Array[Byte]): Option[T] = {
    for {
      () <- require(value.length == Size)
      () <- require(validate(value))
    } yield {
      unsafeFromByteArray(value)
    }
  }

  final override def read(packetData: DataInputStream): DecoderError \/ T = {
    val data = Array.ofDim[Byte](Size)
    packetData.read(data)
    \/-(unsafeFromByteArray(data))
  }

}
