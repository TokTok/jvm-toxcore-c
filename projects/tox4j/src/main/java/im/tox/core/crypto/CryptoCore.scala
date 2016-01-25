package im.tox.core.crypto

import im.tox.core.ModuleCompanion
import im.tox.core.error.DecoderError
import im.tox.tox4j.crypto.ToxCryptoConstants
import im.tox.tox4j.impl.jni.ToxCryptoJni
import scodec.bits.ByteVector

import scala.collection.mutable
import scalaz.{-\/, \/}

/**
 * Crypto core contains all the crypto related functions used by toxcore that
 * relate to random numbers, encryption and decryption, key generation, nonces
 * and random nonces. Not all crypto library functions are wrapped, only those
 * that needed to be are. For example the NaCl functions that require a zero
 * byte buffer before the data. You'll see functions that are provided by the
 * crypto library used in the toxcore code, not just crypto_core functions.
 *
 * The create and handle request functions are the encrypt and decrypt functions
 * for a type of DHT packets used to send data directly to other DHT peers. To
 * be honest they should probably be in the DHT module but they seem to fit
 * better here.
 *
 * The goal of this module is to provide nice interfaces to some crypto related
 * functions.
 *
 * The shared key generation is the most resource intensive part of the
 * encryption/decryption which means that resource usage can be reduced
 * considerably by saving the shared keys and reusing them later as much as
 * possible.
 *
 * Once the shared key is generated, the encryption is done using xsalsa20 and
 * authenticated with poly1305. This is authenticated symmetric cryptography.
 */
object CryptoCore {

  private val cache = new mutable.HashMap[KeyPair, SharedKey]

  private def getSharedKey(publicKey: PublicKey, secretKey: SecretKey): SharedKey = {
    val keyPair = KeyPair(publicKey, secretKey)
    cache.get(keyPair) match {
      case Some(sharedKey) =>
        sharedKey
      case None =>
        val sharedKey = SharedKey(Array.ofDim[Byte](SharedKey.Size))
        ToxCryptoJni.cryptoBoxBeforenm(
          sharedKey.data,
          publicKey.value.toArray,
          secretKey.value.toArray
        )
        cache.put(keyPair, sharedKey)
        sharedKey
    }
  }

  /**
   * All public keys in toxcore are 32 bytes and are generated with the
   * crypto_box_keypair() function of the NaCl crypto library.
   */
  def keyPair(): KeyPair = {
    val publicKey = Array.ofDim[Byte](PublicKey.Size)
    val secretKey = Array.ofDim[Byte](PublicKey.Size)
    ToxCryptoJni.cryptoBoxKeypair(publicKey, secretKey)
    KeyPair(new PublicKey(publicKey), new SecretKey(secretKey))
  }

  /**
   * The crypto_box*() functions of the NaCl crypto library are used to encrypt
   * and decrypt all packets.
   *
   * As explained in the NaCl documentation, crypto_box is public key cryptography
   * that uses Curve25519 to generate a shared encryption key by using the
   * Curve25519 public key of the one that will be receiving the packet and the
   * Curve25519 private key of the one sending the packet. When the receiver
   * receives the packet, they will pass the counterparts of both keys used to
   * generate the shared key used to encrypt the packet (The receivers private
   * key counterpart of the receivers public key and the senders public key
   * counterpart of the senders private key) to the function/algorithm and get the
   * same shared key.
   *
   * This 32 byte shared key is what is used to encrypt and decrypt packets.
   * This fact must be taken into account because it means since the key used
   * to encrypt and decrypt packets is the same either side can both encrypt
   * and decrypt valid packets. It also means that packets being sent could be
   * replayed back to the sender if there is nothing to prevent it.
   */
  def encrypt[Payload](
    module: ModuleCompanion[Payload]
  )(
    publicKey: PublicKey,
    secretKey: SecretKey,
    nonce: Nonce,
    payload: Payload,
    useKeyCache: Boolean = true
  ): CipherText[Payload] = {
    val plainTextData = Array.ofDim[Byte](ToxCryptoConstants.ZeroBytes) ++ module.toBytes(payload).toSeq
    val cipherTextData = Array.ofDim[Byte](plainTextData.length)
    if (useKeyCache) {
      val sharedKey = getSharedKey(publicKey, secretKey)
      ToxCryptoJni.cryptoBoxAfternm(
        cipherTextData,
        plainTextData,
        nonce.data.toArray,
        sharedKey.data
      )
    } else {
      ToxCryptoJni.cryptoBox(
        cipherTextData,
        plainTextData,
        nonce.data.toArray,
        publicKey.value.toArray,
        secretKey.value.toArray
      )
    }
    CipherText(ByteVector.view(cipherTextData.drop(ToxCryptoConstants.BoxZeroBytes)))
  }

  def decrypt[Payload](
    module: ModuleCompanion[Payload]
  )(
    publicKey: PublicKey,
    secretKey: SecretKey,
    nonce: Nonce,
    cipherText: CipherText[Payload],
    useKeyCache: Boolean = true
  ): DecoderError \/ Payload = {
    val cipherTextData = Array.ofDim[Byte](ToxCryptoConstants.BoxZeroBytes) ++ cipherText.data.toSeq
    val plainTextData = Array.ofDim[Byte](cipherTextData.length)

    val result = {
      if (useKeyCache) {
        val sharedKey = getSharedKey(publicKey, secretKey)
        ToxCryptoJni.cryptoBoxOpenAfternm(
          plainTextData,
          cipherTextData,
          nonce.data.toArray,
          sharedKey.data
        )
      } else {
        ToxCryptoJni.cryptoBoxOpen(
          plainTextData,
          cipherTextData,
          nonce.data.toArray,
          publicKey.value.toArray,
          secretKey.value.toArray
        )
      }
    }

    if (result != 0) {
      -\/(DecoderError.DecryptionError())
    } else {
      module.fromBytes(ByteVector.view(plainTextData.drop(ToxCryptoConstants.ZeroBytes)))
    }
  }

}
