package im.tox.core.crypto

import im.tox.core.ModuleCompanionTest
import im.tox.core.random.RandomCore
import org.scalacheck.{Arbitrary, Gen}

object PublicKeyTest {

  implicit val arbPublicKey: Arbitrary[PublicKey] =
    Arbitrary(Gen.const(()).map(_ => new PublicKey(RandomCore.randomBytes(PublicKey.Size))))

}

final class PublicKeyTest extends ModuleCompanionTest[PublicKey](PublicKey) {

  override val arbT = PublicKeyTest.arbPublicKey

}
