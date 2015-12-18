package im.tox.tox4j.av.bench

import im.tox.tox4j.av.callbacks.ToxAvEventAdapter
import im.tox.tox4j.av.data.{AudioChannels, SamplingRate}
import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.core.options.ToxOptions
import im.tox.tox4j.impl.jni.{ToxAvImplFactory, ToxCoreImplFactory}

import scala.collection.mutable
import scala.util.Random

object AvProfiling extends App {

  ToxCoreImplFactory.withTox(ToxOptions()) { tox =>
    ToxAvImplFactory.withToxAv(tox) { toxAv =>
      val friendNumber = ToxFriendNumber.fromInt(1).get
      val pcm = Array.ofDim[Short](288000)

      val random = new Random
      pcm.indices.foreach { i =>
        pcm(i) = random.nextInt().toShort
      }

      val channels = AudioChannels.Mono
      val samplingRate = SamplingRate.Rate8k

      val eventListener = new ToxAvEventAdapter[Unit]

      val times = new mutable.Queue[Long]
      while (true) {
        val start = System.currentTimeMillis()
        var i = 0
        while (i < 100) {
          toxAv.invokeAudioReceiveFrame(friendNumber, pcm, channels, samplingRate)
          toxAv.iterate(eventListener)(())
          i += 1
        }
        val end = System.currentTimeMillis()
        times.enqueue(end - start)
        if (times.length > 20) {
          times.dequeue()
        }

        System.out.print("\u001b[2J\u001b[H")
        System.out.println(times.sum / times.length)
        Thread.sleep(500)
      }
    }
  }

}
