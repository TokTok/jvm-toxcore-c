package im.tox.tox4j.av.data

import im.tox.core.typesafe.DiscreteIntCompanion

final class SamplingRate private (val value: Int) extends AnyVal

// scalastyle:off magic.number
case object SamplingRate extends DiscreteIntCompanion[SamplingRate](
  8000,
  12000,
  16000,
  24000,
  48000
) {

  val Rate8k = new SamplingRate(values(0))
  val Rate12k = new SamplingRate(values(1))
  val Rate16k = new SamplingRate(values(2))
  val Rate24k = new SamplingRate(values(3))
  val Rate48k = new SamplingRate(values(4))

  override def unsafeFromInt(value: Int): SamplingRate = new SamplingRate(value)
  override def toInt(self: SamplingRate): Int = self.value

}
