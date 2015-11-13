package im.tox.tox4j.core.bench

import im.tox.tox4j.bench.ToxBenchBase._
import im.tox.tox4j.bench.TimingReport
import im.tox.tox4j.core.ToxCore

final class GettersTimingBench extends TimingReport {

  timing of classOf[ToxCore[Unit]] in {

    measure method "getAddress" in {
      usingTox(iterations100k) in {
        case (sz, tox) =>
          (0 until sz) foreach (_ => tox.getAddress)
      }
    }

    measure method "getDhtId" in {
      usingTox(iterations100k) in {
        case (sz, tox) =>
          (0 until sz) foreach (_ => tox.getDhtId)
      }
    }

    measure method "getNoSpam" in {
      usingTox(iterations100k) in {
        case (sz, tox) =>
          (0 until sz) foreach (_ => tox.getNospam)
      }
    }

    measure method "name" in {
      usingTox(iterations100k) in {
        case (sz, tox) =>
          (0 until sz) foreach (_ => tox.getName)
      }
    }

    measure method "getPublicKey" in {
      usingTox(iterations100k) in {
        case (sz, tox) =>
          (0 until sz) foreach (_ => tox.getPublicKey)
      }
    }

    measure method "getSecretKey" in {
      usingTox(iterations100k) in {
        case (sz, tox) =>
          (0 until sz) foreach (_ => tox.getSecretKey)
      }
    }

    measure method "getStatus" in {
      usingTox(iterations100k) in {
        case (sz, tox) =>
          (0 until sz) foreach (_ => tox.getStatus)
      }
    }

    measure method "getStatusMessage" in {
      usingTox(iterations100k) in {
        case (sz, tox) =>
          (0 until sz) foreach (_ => tox.getStatusMessage)
      }
    }

    measure method "getUdpPort" in {
      usingTox(iterations100k) in {
        case (sz, tox) =>
          (0 until sz) foreach (_ => tox.getUdpPort)
      }
    }

    measure method "getNoSpam" in {
      usingTox(iterations100k) in {
        case (sz, tox) =>
          (0 until sz) foreach (_ => tox.getNospam)
      }
    }

    measure method "getStatus" in {
      usingTox(iterations100k) in {
        case (sz, tox) =>
          (0 until sz) foreach (_ => tox.getStatus)
      }
    }

    measure method "getUdpPort" in {
      usingTox(iterations100k) in {
        case (sz, tox) =>
          (0 until sz) foreach (_ => tox.getUdpPort)
      }
    }

  }

}
