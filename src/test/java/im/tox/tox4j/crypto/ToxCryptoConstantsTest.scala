package im.tox.tox4j.crypto

import org.scalatest.FunSuite

final class ToxCryptoConstantsTest extends FunSuite {

  test("HashLength") {
    assert(ToxCryptoConstants.HashLength >= 32)
  }

  test("EncryptionExtraLength contains at least a hash") {
    assert(ToxCryptoConstants.EncryptionExtraLength >= ToxCryptoConstants.HashLength)
  }

  test("SaltLength") {
    assert(ToxCryptoConstants.SaltLength >= 32)
  }

}
