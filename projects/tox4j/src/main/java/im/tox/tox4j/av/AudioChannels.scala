package im.tox.tox4j.av

import im.tox.core.typesafe.DiscreteIntCompanion

final class AudioChannels private (val value: Int) extends AnyVal

object AudioChannels extends DiscreteIntCompanion[AudioChannels](1, 2) {

  val Mono = new AudioChannels(1)
  val Stereo = new AudioChannels(2)

  override def unsafeFromInt(value: Int): AudioChannels = new AudioChannels(value)
  override def toInt(self: AudioChannels): Int = self.value

}
