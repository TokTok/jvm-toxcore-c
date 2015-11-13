package im.tox.tox4j.core.bench

import im.tox.core.network.Port
import im.tox.tox4j.bench.ToxBenchBase._
import im.tox.tox4j.bench.TimingReport
import im.tox.tox4j.core.callbacks.ToxEventAdapter
import im.tox.tox4j.core.{ToxCore, ToxCoreConstants, ToxPublicKey}
import org.scalameter.api._

final class ToxCoreTimingBench extends TimingReport {

  val port = Port.unsafeFromInt(8080)
  val publicKey = ToxPublicKey.unsafeFromByteArray(Array.ofDim[Byte](ToxCoreConstants.PublicKeySize))

  timing of classOf[ToxCore[Unit]] in {

    measure method "bootstrap" in {
      usingTox(nodes) in {
        case (sz, tox) =>
          (0 until sz) foreach (_ => tox.bootstrap("localhost", port, publicKey))
      }
    }

    measure method "addTcpRelay" in {
      usingTox(nodes) in {
        case (sz, tox) =>
          (0 until sz) foreach (_ => tox.addTcpRelay("localhost", port, publicKey))
      }
    }

    measure method "callback" in {
      val ignoreEvents = new ToxEventAdapter[Unit]
      usingTox(iterations1000k) config (exec.benchRuns -> 100) in {
        case (sz, tox) =>
          (0 until sz) foreach (_ => tox.callback(ignoreEvents))
      }
    }

  }

}
