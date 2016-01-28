package im.tox.tox4j.av

import java.io.{DataOutput, DataInputStream}

import im.tox.core.error.DecoderError
import im.tox.core.typesafe.{DiscreteValueCompanion, DiscreteIntCompanion}

import scala.concurrent.duration._
import scala.language.postfixOps
import scalaz.{\/-, -\/, \/}

final class AudioLength private (private val value: Duration) extends AnyVal {
  def toMicros: Int = value.toMicros.toInt
}

// scalastyle:off magic.number
object AudioLength extends DiscreteValueCompanion[AudioLength, Duration](
  value => new AudioLength(value)
)(
  2500 microseconds,
  5000 microseconds,
  10000 microseconds,
  20000 microseconds,
  40000 microseconds,
  60000 microseconds
) {

  val Length2_5 = new AudioLength(values(0))
  val Length5 = new AudioLength(values(1))
  val Length10 = new AudioLength(values(2))
  val Length20 = new AudioLength(values(3))
  val Length40 = new AudioLength(values(4))
  val Length60 = new AudioLength(values(5))

  override def write(self: AudioLength, packetData: DataOutput): Unit = {
    packetData.writeShort(self.value.toMicros.toShort)
  }

  override def read(packetData: DataInputStream): \/[DecoderError, AudioLength] = {
    val value = packetData.readUnsignedShort().microseconds
    fromValue(value) match {
      case None       => -\/(DecoderError.InvalidFormat(s"Invalid duration for $this: $value"))
      case Some(self) => \/-(self)
    }
  }

}
