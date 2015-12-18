package im.tox.tox4j.core.data

import im.tox.core.typesafe.{Security, KeyCompanionTimingBench}

final class ToxPublicKeyTimingBench extends KeyCompanionTimingBench[ToxPublicKey, Security.NonSensitive] {
  protected def companion = ToxPublicKey
  protected def arbT = ToxPublicKeyTest.arbToxPublicKey
}
