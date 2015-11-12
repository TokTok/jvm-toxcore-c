package im.tox.core.dht.packets

import im.tox.core.ModuleCompanion
import im.tox.core.crypto._
import im.tox.core.error.CoreError
import im.tox.core.typesafe.Security

import scalaz.\/

/**
 * [byte with value: 00 for request, 01 for response]
 * [DHT public key of sender, length=32 bytes]
 * [random 24 byte nonce]
 * [Encrypted with the nonce, private DHT key of the sender and public DHT key of the receiver: payload]
 */
final case class DhtEncryptedPacket[Payload] private (
  senderPublicKey: PublicKey,
  nonce: Nonce,
  payload: CipherText[Payload]
)

object DhtEncryptedPacket {

  final case class Make[Payload, S <: Security](module: ModuleCompanion[Payload, S])
      extends ModuleCompanion[DhtEncryptedPacket[Payload], Security.NonSensitive] {

    private val CipherTextModule = CipherText.Make(module)

    override val codec =
      (PublicKey.codec ~ Nonce.codec ~ CipherTextModule.codec)
        .xmap[DhtEncryptedPacket[Payload]](
          { case ((publicKey, nonce), payload) => DhtEncryptedPacket(publicKey, nonce, payload) },
          { packet => ((packet.senderPublicKey, packet.nonce), packet.payload) }
        )

    def encrypt(
      receiverPublicKey: PublicKey,
      senderKeyPair: KeyPair,
      nonce: Nonce,
      payload: Payload
    ): CoreError \/ DhtEncryptedPacket[Payload] = {
      /**
       * [Encrypted with the nonce, private DHT key of the sender and public DHT key of the receiver: payload]
       */
      for {
        cipherText <- CryptoCore.encrypt(module)(
          receiverPublicKey,
          senderKeyPair.secretKey,
          nonce,
          payload
        )
      } yield {
        DhtEncryptedPacket(
          senderKeyPair.publicKey,
          nonce,
          cipherText
        )
      }
    }

    def decrypt(
      dhtPacket: DhtEncryptedPacket[Payload],
      receiverSecretKey: SecretKey
    ): CoreError \/ Payload = {
      /**
       * [Encrypted with the nonce, private DHT key of the sender and public DHT key of the receiver: payload]
       */
      CryptoCore.decrypt(module)(
        dhtPacket.senderPublicKey,
        receiverSecretKey,
        dhtPacket.nonce,
        dhtPacket.payload
      )
    }

  }

}
