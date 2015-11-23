package im.tox.core.crypto

import im.tox.core.random.RandomCore
import org.scalacheck.{Arbitrary, Gen}

object SecretKeyTest {

  def take(secretKey: SecretKey, maxSize: Int): SecretKey = {
    new SecretKey(secretKey.value.take(maxSize))
  }

  implicit val arbSecretKey: Arbitrary[SecretKey] =
    Arbitrary(Gen.resultOf[Unit, SecretKey] { case () => new SecretKey(RandomCore.randomBytes(SecretKey.Size)) })

}
