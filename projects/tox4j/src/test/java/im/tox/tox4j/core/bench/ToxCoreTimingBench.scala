package im.tox.tox4j.core.bench

import im.tox.core.network.Port
import im.tox.tox4j.bench.TimingReport
import im.tox.tox4j.bench.ToxBenchBase._
import im.tox.tox4j.core.data.ToxPublicKey
import im.tox.tox4j.core.{ToxCore, ToxCoreConstants}
import im.tox.tox4j.testing.GetDisjunction._

final class ToxCoreTimingBench extends TimingReport {

  val port = Port.fromInt(8080).get
  val publicKey = ToxPublicKey.fromValue(Array.ofDim(ToxCoreConstants.PublicKeySize)).get

  timing of classOf[ToxCore] in {

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

  }

}
