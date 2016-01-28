package im.tox.tox4j.crypto

import im.tox.core.random.RandomCore
import im.tox.tox4j.crypto.ToxCryptoTest.{EncryptedData, Salt}
import im.tox.tox4j.crypto.exceptions.{ToxDecryptionException, ToxEncryptionException, ToxKeyDerivationException}
import im.tox.tox4j.testing.ToxTestMixin
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.WordSpec
import org.scalatest.prop.PropertyChecks

import scala.language.implicitConversions
import scala.util.Random

object ToxCryptoTest {
  private final case class Salt(data: Seq[Byte]) extends AnyVal
  private final case class EncryptedData(data: Seq[Byte]) extends AnyVal
}

abstract class ToxCryptoTest(private val toxCrypto: ToxCrypto) extends WordSpec with PropertyChecks with ToxTestMixin {

  private val random = new Random

  private implicit val arbSalt: Arbitrary[Salt] =
    Arbitrary(Gen.const(ToxCryptoConstants.SaltLength).map(Array.ofDim[Byte]).map { array =>
      random.nextBytes(array)
      new Salt(array)
    })

  private implicit val arbEncryptedData: Arbitrary[EncryptedData] =
    Arbitrary(Gen.zip(arbitrary[Array[Byte]], Gen.nonEmptyContainerOf[Array, Byte](arbitrary[Byte])).map {
      case (passphrase, data) =>
        val passKey = toxCrypto.deriveKeyFromPass(passphrase)
        new EncryptedData(toxCrypto.encrypt(data, passKey))
    })

  private implicit val arbPassKey: Arbitrary[toxCrypto.PassKey] =
    Arbitrary(arbitrary[Array[Byte]].map(x => toxCrypto.deriveKeyFromPass(x)))

  "PassKey serialisation" should {
    "produce a byte sequence of the right length" in {
      forAll { (passKey: toxCrypto.PassKey) =>
        val serialised = toxCrypto.passKeyToBytes(passKey)
        assert(serialised.length == ToxCryptoConstants.KeyLength + ToxCryptoConstants.SaltLength)
      }
    }

    "produce the same PassKey after deserialisation" in {
      forAll { (passKey: toxCrypto.PassKey) =>
        val serialised = toxCrypto.passKeyToBytes(passKey)
        assert(toxCrypto.passKeyFromBytes(serialised).contains(passKey))
      }
    }
  }

  "PassKey deserialisation" should {
    "fail for byte sequences of the wrong length" in {
      forAll { (serialised: Seq[Byte]) =>
        whenever(serialised.length != ToxCryptoConstants.KeyLength + ToxCryptoConstants.SaltLength) {
          assert(toxCrypto.passKeyFromBytes(serialised).isEmpty)
        }
      }
    }

    "produce the same PassKey after deserialisation" in {
      forAll { (passKey: toxCrypto.PassKey) =>
        val serialised = toxCrypto.passKeyToBytes(passKey)
        assert(toxCrypto.passKeyFromBytes(serialised).contains(passKey))
      }
    }
  }

  "encryption with a PassKey" should {
    import ToxEncryptionException.Code._

    "produce high entropy results (> 0.7)" in {
      val passKey = toxCrypto.deriveKeyFromPass(Array.ofDim(0))
      forAll { (data: Array[Byte]) =>
        whenever(data.nonEmpty) {
          assert(RandomCore.entropy(toxCrypto.encrypt(data, passKey)) > 0.7)
        }
      }
    }

    "produce different data each time with the same PassKey" in {
      forAll { (data: Array[Byte], passKey: toxCrypto.PassKey) =>
        whenever(data.nonEmpty) {
          val encrypted1 = toxCrypto.encrypt(data, passKey)
          val encrypted2 = toxCrypto.encrypt(data, passKey)
          assert(encrypted1.deep != encrypted2.deep)
        }
      }
    }

    "produce different encrypted data with a different PassKey" in {
      forAll { (data: Array[Byte], passKey1: toxCrypto.PassKey, passKey2: toxCrypto.PassKey) =>
        whenever(data.nonEmpty && !toxCrypto.passKeyEquals(passKey1, passKey2)) {
          assert(toxCrypto.encrypt(data, passKey1).deep != toxCrypto.encrypt(data, passKey2).deep)
        }
      }
    }

    s"fail with $NULL for zero-length data" in {
      val passKey = toxCrypto.deriveKeyFromPass(Array.ofDim(0))
      intercept(NULL) {
        toxCrypto.encrypt(Array.ofDim(0), passKey)
      }
    }
  }

  "isDataEncrypted" should {
    "identify encrypted data as such" in {
      forAll { (data: EncryptedData) =>
        assert(toxCrypto.isDataEncrypted(data.data.toArray))
      }
    }

    "not identify arbitrary other data as encrypted" in {
      forAll { (data: Array[Byte]) =>
        assert(!toxCrypto.isDataEncrypted(data))
      }
    }
  }

  "key derivation" should {
    import ToxKeyDerivationException.Code._

    s"fail with $INVALID_LENGTH for salt of wrong size" in {
      forAll { (salt: Array[Byte]) =>
        whenever(salt.length != ToxCryptoConstants.SaltLength) {
          intercept(INVALID_LENGTH) {
            toxCrypto.deriveKeyWithSalt(Array.ofDim(100), salt)
          }
        }
      }
    }

    "succeed for any passphrase" in {
      forAll { (passphrase: Array[Byte]) =>
        assert(Option(toxCrypto.deriveKeyFromPass(passphrase)).nonEmpty)
      }
    }

    "produce the same key given the same salt" in {
      forAll { (passphrase: Array[Byte], salt: Salt) =>
        val key1 = toxCrypto.deriveKeyWithSalt(passphrase, salt.data.toArray)
        val key2 = toxCrypto.deriveKeyWithSalt(passphrase, salt.data.toArray)
        assert(toxCrypto.passKeyEquals(key1, key2))
      }
    }

    "produce a different key given a different salt" in {
      forAll { (passphrase: Array[Byte], salt1: Salt, salt2: Salt) =>
        whenever(salt1 != salt2) {
          val key1 = toxCrypto.deriveKeyWithSalt(passphrase, salt1.data.toArray)
          val key2 = toxCrypto.deriveKeyWithSalt(passphrase, salt2.data.toArray)
          assert(!toxCrypto.passKeyEquals(key1, key2))
        }
      }
    }
  }

  "decryption" should {
    import ToxDecryptionException.Code._

    s"fail with $INVALID_LENGTH for zero-length data" in {
      val passKey = toxCrypto.deriveKeyFromPass(Array.ofDim(0))
      intercept(INVALID_LENGTH) {
        toxCrypto.decrypt(Array.ofDim(0), passKey)
      }
    }

    s"fail with $INVALID_LENGTH for 0 to ${ToxCryptoConstants.EncryptionExtraLength} bytes" in {
      val passKey = toxCrypto.deriveKeyFromPass(Array.ofDim(0))
      (0 to ToxCryptoConstants.EncryptionExtraLength) foreach { length =>
        intercept(INVALID_LENGTH) {
          toxCrypto.decrypt(Array.ofDim(length), passKey)
        }
      }
    }

    s"fail with $BAD_FORMAT for an array of more than ${ToxCryptoConstants.EncryptionExtraLength} 0-bytes" in {
      val passKey = toxCrypto.deriveKeyFromPass(Array.ofDim(0))
      intercept(BAD_FORMAT) {
        toxCrypto.decrypt(Array.ofDim(ToxCryptoConstants.EncryptionExtraLength + 1), passKey)
      }
    }

    "succeed with the same PassKey" in {
      forAll { (data: Array[Byte], passKey: toxCrypto.PassKey) =>
        whenever(data.nonEmpty) {
          val encrypted = toxCrypto.encrypt(data, passKey)
          val decrypted = toxCrypto.decrypt(encrypted, passKey)
          assert(data.deep == decrypted.deep)
        }
      }
    }

    "succeed with the same passphrase and salt" in {
      forAll { (data: Array[Byte], passphrase: Array[Byte]) =>
        whenever(data.nonEmpty) {
          val passKey1 = toxCrypto.deriveKeyFromPass(passphrase)
          val encrypted = toxCrypto.encrypt(data, passKey1)
          val salt = toxCrypto.getSalt(encrypted)

          val passKey2 = toxCrypto.deriveKeyWithSalt(passphrase, salt)
          val decrypted = toxCrypto.decrypt(encrypted, passKey2)

          assert(data.deep == decrypted.deep)
        }
      }
    }

    s"fail with $FAILED for the same passphrase but different salt" in {
      forAll { (data: Array[Byte], passphrase: Array[Byte], salt1: Salt, salt2: Salt) =>
        whenever(data.nonEmpty && salt1 != salt2) {
          val passKey1 = toxCrypto.deriveKeyWithSalt(passphrase, salt1.data.toArray)
          val passKey2 = toxCrypto.deriveKeyWithSalt(passphrase, salt2.data.toArray)
          val encrypted = toxCrypto.encrypt(data, passKey1)
          intercept(FAILED) {
            toxCrypto.decrypt(encrypted, passKey2)
          }
        }
      }
    }

    s"fail with $FAILED for a different PassKey" in {
      forAll { (data: Array[Byte], passKey1: toxCrypto.PassKey, passKey2: toxCrypto.PassKey) =>
        whenever(data.nonEmpty && !toxCrypto.passKeyEquals(passKey1, passKey2)) {
          val encrypted = toxCrypto.encrypt(data, passKey1)
          intercept(FAILED) {
            toxCrypto.decrypt(encrypted, passKey2)
          }
        }
      }
    }
  }

  "hash computation" should {
    "produce the same output when called twice on the same input" in {
      forAll { (data: Array[Byte]) =>
        assert(toxCrypto.hash(data).deep == toxCrypto.hash(data).deep)
      }
    }

    "produce a different output for different inputs" in {
      forAll { (data1: Array[Byte], data2: Array[Byte]) =>
        whenever(data1.deep != data2.deep) {
          assert(toxCrypto.hash(data1).deep != toxCrypto.hash(data2).deep)
        }
      }
    }

    "create constant-length output" in {
      forAll { (data: Array[Byte]) =>
        assert(toxCrypto.hash(data).length == ToxCryptoConstants.HashLength)
      }
    }

    "produce high entropy results (> 0.5)" in {
      forAll { (data: Array[Byte]) =>
        assert(RandomCore.entropy(toxCrypto.hash(data)) > 0.5)
      }
    }
  }

}
