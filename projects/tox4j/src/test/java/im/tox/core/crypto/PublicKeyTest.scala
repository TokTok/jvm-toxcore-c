package im.tox.core.crypto

import im.tox.core.random.RandomCore
import im.tox.core.typesafe.KeyCompanionTest
import org.scalacheck.{Arbitrary, Gen}

object PublicKeyTest {

  def take(publicKey: PublicKey, maxSize: Int): PublicKey = {
    new PublicKey(publicKey.value.take(maxSize))
  }

  implicit val arbPublicKey: Arbitrary[PublicKey] =
    Arbitrary(Gen.resultOf[Unit, PublicKey] { case () => new PublicKey(RandomCore.randomBytes(PublicKey.Size)) })

}

final class PublicKeyTest extends KeyCompanionTest(PublicKey)(PublicKeyTest.arbPublicKey)
