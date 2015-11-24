package im.tox.core.crypto

import im.tox.core.crypto.CryptoCoreTest._
import im.tox.core.crypto.NonceTest._
import im.tox.core.crypto.PlainText.Conversions._
import im.tox.core.crypto.PlainTextTest._
import im.tox.core.random.RandomCore
import im.tox.core.typesafe.Security
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.WordSpec
import org.scalatest.prop.PropertyChecks
import scodec.bits.ByteVector

import scalaz.\/-

object CryptoCoreTest {

  implicit val arbKeyPair: Arbitrary[KeyPair] =
    Arbitrary(Gen.resultOf[Unit, KeyPair](_ => CryptoCore.keyPair()))

}

final class CryptoCoreTest extends WordSpec with PropertyChecks {

  "corruption" should {
    "be detectable" in {
      forAll { (keyPair: KeyPair, nonce: Nonce, plainText: PlainText[Security.NonSensitive], extraData: Array[Byte], useKeyCache: Boolean) =>
        whenever(extraData.nonEmpty) {
          val cipherText =
            CryptoCore
              .encrypt(PlainText)(keyPair.publicKey, keyPair.secretKey, nonce, plainText, useKeyCache)
              .getOrElse(fail("Encryption failed"))

          val decrypted = CryptoCore.decrypt(PlainText)(
            keyPair.publicKey, keyPair.secretKey, nonce,
            cipherText.copy(cipherText.data ++ ByteVector.view(extraData)),
            useKeyCache
          )

          assert(decrypted.isLeft)
        }
      }
    }
  }

  "two distinct key pairs" should {
    "not be equal" in {
      forAll { (keyPair1: KeyPair, keyPair2: KeyPair) =>
        assert(keyPair1 == keyPair1)
        assert(keyPair2 == keyPair2)
        assert(keyPair1 != keyPair2)
      }
    }
  }

  "key pairs" should {
    "have different values for public and secret key" in {
      forAll { (keyPair: KeyPair) =>
        assert(keyPair.publicKey.value != keyPair.secretKey.value)
      }
    }

    "have high entropy in both key parts" in {
      forAll { (keyPair: KeyPair) =>
        assert(RandomCore.entropy(keyPair.publicKey.value) > 0.5)
        assert(RandomCore.entropy(keyPair.secretKey.value) > 0.5)
      }
    }
  }

  "encryption" should {
    final case class EncryptedText(
      publicKey: PublicKey,
      secretKey: SecretKey,
      nonce: Nonce,
      plainText: PlainText[Security.NonSensitive],
      cipherText: CipherText[PlainText[Security.NonSensitive]]
    )

    implicit val arbEncryptedText: Arbitrary[EncryptedText] =
      Arbitrary(Gen.zip(arbitrary[KeyPair], arbitrary[Nonce], arbitrary[PlainText[Security.NonSensitive]], arbitrary[Boolean]).map {
        case (KeyPair(publicKey, secretKey), nonce, plainText, useKeyCache) =>
          val cipherText = CryptoCore.encrypt(PlainText)(publicKey, secretKey, nonce, plainText, useKeyCache)
          EncryptedText(
            publicKey,
            secretKey,
            nonce,
            plainText,
            cipherText.getOrElse(fail("Encryption failed"))
          )
      })

    "be reversible" in {
      forAll { (encryptedText: EncryptedText) =>
        val originalText = CryptoCore.decrypt(PlainText)(
          encryptedText.publicKey,
          encryptedText.secretKey,
          encryptedText.nonce,
          encryptedText.cipherText
        )
        assert(originalText == \/-(encryptedText.plainText))
      }
    }

    "produce a different text than the input" in {
      forAll { (encryptedText: EncryptedText) =>
        assert(encryptedText.cipherText.data != encryptedText.plainText.toByteVector)
      }
    }

    "have a constant overhead" in {
      forAll { (encryptedText1: EncryptedText, encryptedText2: EncryptedText) =>
        val overhead1 = encryptedText1.cipherText.data.length - encryptedText1.plainText.toByteVector.length
        val overhead2 = encryptedText2.cipherText.data.length - encryptedText2.plainText.toByteVector.length
        assert(overhead1 == overhead2)
      }
    }

    "produce high entropy output" in {
      forAll { (encryptedText: EncryptedText) =>
        val length = encryptedText.plainText.toByteVector.length
        val entropy = RandomCore.entropy(encryptedText.cipherText.data.toSeq)

        if (length > 60) assert(entropy > 0.7)
        if (length > 40) assert(entropy > 0.6)
        if (length > 20) assert(entropy > 0.5)
        if (length > 10) assert(entropy > 0.4)
      }
    }

    "produce the same output for the same input" in {
      forAll { (plainText: PlainText[Security.NonSensitive], nonce: Nonce, keyPair: KeyPair) =>
        val cipherText1 = CryptoCore.encrypt(PlainText)(keyPair.publicKey, keyPair.secretKey, nonce, plainText)
        val cipherText2 = CryptoCore.encrypt(PlainText)(keyPair.publicKey, keyPair.secretKey, nonce, plainText)
        assert(cipherText1 == cipherText2)
      }
    }

    "produce the same output with and without key cache" in {
      forAll { (plainText: PlainText[Security.NonSensitive], nonce: Nonce, keyPair: KeyPair) =>
        val cipherText1 = CryptoCore.encrypt(PlainText)(keyPair.publicKey, keyPair.secretKey, nonce, plainText, useKeyCache = true)
        val cipherText2 = CryptoCore.encrypt(PlainText)(keyPair.publicKey, keyPair.secretKey, nonce, plainText, useKeyCache = false)
        assert(cipherText1 == cipherText2)
      }
    }

    "produce different output for different input" in {
      forAll { (plainText1: PlainText[Security.NonSensitive], plainText2: PlainText[Security.NonSensitive], nonce: Nonce, keyPair: KeyPair) =>
        whenever(plainText1 != plainText2) {
          val cipherText1 = CryptoCore.encrypt(PlainText)(keyPair.publicKey, keyPair.secretKey, nonce, plainText1)
          val cipherText2 = CryptoCore.encrypt(PlainText)(keyPair.publicKey, keyPair.secretKey, nonce, plainText2)
          assert(cipherText1 == cipherText1)
          assert(cipherText2 == cipherText2)
          assert(cipherText1 != cipherText2)
        }
      }
    }

    "produce different output for the same input with different keys" in {
      forAll { (plainText: PlainText[Security.NonSensitive], nonce: Nonce, keyPair1: KeyPair, keyPair2: KeyPair) =>
        val cipherText1 = CryptoCore.encrypt(PlainText)(keyPair1.publicKey, keyPair1.secretKey, nonce, plainText)
        val cipherText2 = CryptoCore.encrypt(PlainText)(keyPair2.publicKey, keyPair2.secretKey, nonce, plainText)
        // Arbitrary keys should always be different.
        assert(keyPair1 != keyPair2)
        assert(cipherText1 != cipherText2)
      }
    }

    "produce different output for the same input with different nonces" in {
      forAll { (plainText: PlainText[Security.NonSensitive], nonce1: Nonce, nonce2: Nonce, keyPair: KeyPair) =>
        val cipherText1 = CryptoCore.encrypt(PlainText)(keyPair.publicKey, keyPair.secretKey, nonce1, plainText)
        val cipherText2 = CryptoCore.encrypt(PlainText)(keyPair.publicKey, keyPair.secretKey, nonce2, plainText)
        // Arbitrary nonces should always be different.
        assert(nonce1 != nonce2)
        assert(cipherText1 != cipherText2)
      }
    }

    "be symmetric" in {
      forAll { (plainText: PlainText[Security.NonSensitive], nonce: Nonce, keyPair1: KeyPair, keyPair2: KeyPair) =>
        val cipherText1 = CryptoCore.encrypt(PlainText)(keyPair1.publicKey, keyPair2.secretKey, nonce, plainText)
        val cipherText2 = CryptoCore.encrypt(PlainText)(keyPair2.publicKey, keyPair1.secretKey, nonce, plainText)
        assert(cipherText1 == cipherText2)
      }
    }
  }

  "random nonces" should {
    "be random (two random nonces should not be equal)" in {
      forAll { (nonce1: Nonce, nonce2: Nonce) =>
        assert(nonce1 == nonce1)
        assert(nonce2 == nonce2)
        assert(nonce1 != nonce2)
      }
    }

    "have high entropy" in {
      forAll { (nonce: Nonce) =>
        assert(RandomCore.entropy(nonce.value) > 0.5)
      }
    }
  }

}
