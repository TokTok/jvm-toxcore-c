package im.tox.core

import java.io._

import im.tox.core.error.DecoderError
import scodec.bits.ByteVector

import scalaz.\/

object ModuleCompanion {
  private val noneValue = None
  private val someValue = Some(())
}

abstract class ModuleCompanion[T] {

  final def require(condition: Boolean): Option[Unit] = {
    if (!condition) {
      ModuleCompanion.noneValue
    } else {
      ModuleCompanion.someValue
    }
  }

  def write(self: T, packetData: DataOutput): Unit
  def read(packetData: DataInputStream): DecoderError \/ T

  final def toBytes(self: T): ByteVector = {
    val packetData = new ByteArrayOutputStream()
    write(self, new DataOutputStream(packetData))
    ByteVector(packetData.toByteArray)
  }

  final def fromBytes(bytes: ByteVector): DecoderError \/ T = {
    read(new DataInputStream(new ByteArrayInputStream(bytes.toArray)))
  }

  final override def toString: String = {
    getClass.getSimpleName
  }

}
