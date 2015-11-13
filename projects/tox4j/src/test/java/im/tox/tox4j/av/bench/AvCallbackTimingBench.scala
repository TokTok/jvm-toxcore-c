package im.tox.tox4j.av.bench

import im.tox.tox4j.av.{AudioChannels, SamplingRate, ToxAv}
import im.tox.tox4j.bench.ToxBenchBase._
import im.tox.tox4j.bench.TimingReport
import im.tox.tox4j.core.ToxCoreConstants
import im.tox.tox4j.impl.jni.ToxAvImpl

final class AvCallbackTimingBench extends TimingReport {

  val publicKey = Array.ofDim[Byte](ToxCoreConstants.PublicKeySize)
  val data = Array.ofDim[Byte](ToxCoreConstants.MaxCustomPacketSize)

  def invokePerformance(method: String, f: ToxAvImpl[Unit] => Unit): Unit = {
    performance of method in {
      usingToxAv(iterations1k) in {
        case (sz, toxAv: ToxAvImpl[Unit]) =>
          (0 until sz) foreach { _ =>
            f(toxAv)
            toxAv.iterate(())
          }
      }
    }
  }

  timing of classOf[ToxAv[Unit]] in {

    val pcm = range("samples")(100).map(Array.ofDim[Short])

    // invokePerformance("invokeAudioBitRateStatus", _.invokeAudioBitRateStatus(1, stable = true, 1))
    // invokePerformance("invokeAudioReceiveFrame", _.invokeAudioReceiveFrame(1, pcm, 1, 1))

    performance of "invokeAudioReceiveFrame" in {
      usingToxAv(iterations1k, pcm) in {
        case (sz, pcm: Array[Short], toxAv: ToxAvImpl[Unit]) =>
          (0 until sz) foreach { _ =>
            toxAv.invokeAudioReceiveFrame(1, pcm, AudioChannels.Mono, SamplingRate.Rate8k)
            toxAv.iterate(())
          }
      }
    }

  }

}
