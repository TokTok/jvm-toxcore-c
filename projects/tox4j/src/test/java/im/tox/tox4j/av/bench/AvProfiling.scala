package im.tox.tox4j.av.bench

import im.tox.tox4j.av.callbacks.ToxAvEventAdapter
import im.tox.tox4j.av.data.{AudioChannels, SamplingRate}
import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.core.options.ToxOptions
import im.tox.tox4j.impl.jni.{ToxAvImpl, ToxCoreImpl}

import scala.util.Random

object AvProfiling extends App {

  val tox = new ToxCoreImpl(ToxOptions())
  val toxAv = new ToxAvImpl(tox)

  val friendNumber = ToxFriendNumber.fromInt(1).get
  val pcm = Array.ofDim[Short](200)

  val random = new Random
  pcm.indices.foreach { i =>
    pcm(i) = random.nextInt().toShort
  }

  val channels = AudioChannels.Mono
  val samplingRate = SamplingRate.Rate8k

  val eventListener = new ToxAvEventAdapter[Unit]

  while (true) {
    toxAv.invokeAudioReceiveFrame(friendNumber, pcm, channels, samplingRate)
    toxAv.iterate(eventListener)(())
  }

  toxAv.close()
  tox.close()

}
