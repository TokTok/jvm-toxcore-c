package im.tox.core.dht.packets

import im.tox.core.ModuleCompanionTest
import im.tox.core.crypto.CryptoCoreTest._
import im.tox.core.crypto.NonceTest._
import im.tox.core.crypto.PlainText.Conversions._
import im.tox.core.crypto.PlainTextTest._
import im.tox.core.crypto._
import im.tox.core.typesafe.Security
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

import scalaz.\/-

object DhtEncryptedPacketTest {

  implicit val arbDhtEncryptedPacketWithSecretKey: Arbitrary[(DhtEncryptedPacket[PlainText[Security.NonSensitive]], SecretKey)] =
    Arbitrary(
      Gen.zip(
        arbitrary[KeyPair],
        arbitrary[Nonce],
        arbitrary[PlainText[Security.NonSensitive]]
      ).map {
        case (keyPair, nonce, payload) =>
          val encryptedPacket = DhtEncryptedPacket.Make(PlainText).encrypt(
            keyPair.publicKey,
            keyPair,
            nonce,
            payload
          ).getOrElse(throw new AssertionError("Encryption failed"))
          (encryptedPacket, keyPair.secretKey)
      }
    )

}

final class DhtEncryptedPacketTest extends ModuleCompanionTest(DhtEncryptedPacket.Make(PlainText)) {

  override val arbT =
    Arbitrary(DhtEncryptedPacketTest.arbDhtEncryptedPacketWithSecretKey.arbitrary.map(_._1))

  test("encryption and decryption") {
    forAll { (
      senderKeyPair: KeyPair,
      receiverKeyPair: KeyPair,
      nonce: Nonce,
      payload: PlainText[Security.NonSensitive]
    ) =>
      val encrypted = DhtEncryptedPacket.Make(PlainText).encrypt(
        receiverKeyPair.publicKey,
        senderKeyPair,
        nonce,
        payload
      ).getOrElse(fail("Encryption failed"))
      val decryptedPayload = DhtEncryptedPacket.Make(PlainText).decrypt(encrypted, receiverKeyPair.secretKey)

      assert(decryptedPayload == \/-(payload))
    }
  }

  test("protocol overhead is constant 72 bytes") {
    forAll { (
      senderKeyPair: KeyPair,
      nonce: Nonce,
      payload: PlainText[Security.NonSensitive]
    ) =>
      val encrypted = DhtEncryptedPacket.Make(PlainText).encrypt(
        senderKeyPair.publicKey,
        senderKeyPair,
        nonce,
        payload
      ).getOrElse(fail("Encryption failed"))

      val packet =
        DhtEncryptedPacket.Make(PlainText)
          .toBytes(encrypted)
          .getOrElse(fail("Encoding failed"))
          .toByteVector
      assert(packet.length - payload.toByteVector.length == 72)
    }
  }

}
