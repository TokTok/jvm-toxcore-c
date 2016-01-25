package im.tox.core.dht.packets

import java.io.{DataInputStream, DataOutput}

import im.tox.core.ModuleCompanion
import im.tox.core.crypto._
import im.tox.core.error.DecoderError

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

  final case class Make[Payload](module: ModuleCompanion[Payload])
      extends ModuleCompanion[DhtEncryptedPacket[Payload]] {

    private val CipherTextModule = CipherText.Make(module)

    override def write(self: DhtEncryptedPacket[Payload], packetData: DataOutput): Unit = {
      PublicKey.write(self.senderPublicKey, packetData)
      Nonce.write(self.nonce, packetData)
      CipherTextModule.write(self.payload, packetData)
    }

    override def read(packetData: DataInputStream): DecoderError \/ DhtEncryptedPacket[Payload] = {
      for {
        senderKey <- PublicKey.read(packetData)
        nonce <- Nonce.read(packetData)
        payload <- CipherTextModule.read(packetData)
      } yield {
        DhtEncryptedPacket(
          senderKey,
          nonce,
          payload
        )
      }
    }

    def encrypt(
      receiverPublicKey: PublicKey,
      senderKeyPair: KeyPair,
      nonce: Nonce,
      payload: Payload
    ): DhtEncryptedPacket[Payload] = {
      /**
       * [Encrypted with the nonce, private DHT key of the sender and public DHT key of the receiver: payload]
       */
      val cipherText = CryptoCore.encrypt(module)(
        receiverPublicKey,
        senderKeyPair.secretKey,
        nonce,
        payload
      )
      DhtEncryptedPacket(
        senderKeyPair.publicKey,
        nonce,
        cipherText
      )
    }

    def decrypt(
      dhtPacket: DhtEncryptedPacket[Payload],
      receiverSecretKey: SecretKey
    ): DecoderError \/ Payload = {
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
