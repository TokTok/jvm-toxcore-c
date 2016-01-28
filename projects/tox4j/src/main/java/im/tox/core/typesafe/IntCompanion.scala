package im.tox.core.typesafe

import java.io.{DataInputStream, DataOutput}

import im.tox.core.ModuleCompanion
import im.tox.core.error.DecoderError

import scalaz.{-\/, \/, \/-}

abstract class IntCompanion[T <: AnyVal] extends ModuleCompanion[T] {

  def fromInt(value: Int): Option[T]
  def toInt(self: T): Int

  override def write(self: T, packetData: DataOutput): Unit = {
    packetData.writeInt(toInt(self))
  }

  override def read(packetData: DataInputStream): DecoderError \/ T = {
    val value = packetData.readInt()
    fromInt(value) match {
      case None       => -\/(DecoderError.InvalidFormat(s"Invalid value for $this: $value"))
      case Some(self) => \/-(self)
    }
  }

}
