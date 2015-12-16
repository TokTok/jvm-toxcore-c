package im.tox.tox4j.core.data

import im.tox.core.typesafe.{Security, KeyCompanionBench}

final class ToxPublicKeyBench extends KeyCompanionBench[ToxPublicKey, Security.NonSensitive] {
  protected def companion = ToxPublicKey
  protected def arbT = ToxPublicKeyTest.arbToxPublicKey
}
