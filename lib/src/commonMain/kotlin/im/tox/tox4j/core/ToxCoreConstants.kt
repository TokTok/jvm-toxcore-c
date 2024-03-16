package im.tox.tox4j.core

import im.tox.tox4j.crypto.ToxCryptoConstants

object ToxCoreConstants {
    /** The size of a Tox Public Key in bytes. */
    const val PUBLIC_KEY_SIZE = ToxCryptoConstants.PUBLIC_KEY_LENGTH

    /** The size of a Tox Secret Key in bytes. */
    const val SECRET_KEY_SIZE = ToxCryptoConstants.SECRET_KEY_LENGTH

    /**
     * The size of a Tox address in bytes. Tox addresses are in the format
     * [Public Key ([ [PUBLIC_KEY_SIZE]] bytes)][nospam (4 bytes)][checksum (2 bytes)].
     *
     * The checksum is computed over the Public Key and the nospam value. The first byte is an XOR
     * of all the odd bytes, the second byte is an XOR of all the even bytes of the Public Key and
     * nospam.
     */
    const val ADDRESS_SIZE = PUBLIC_KEY_SIZE + 4 + 2

    /** Maximum length of a nickname in bytes. */
    const val MAX_NAME_LENGTH = 128

    /** Maximum length of a status message in bytes. */
    const val MAX_STATUS_MESSAGE_LENGTH = 1007

    /** Maximum length of a friend request message in bytes. */
    const val MAX_FRIEND_REQUEST_LENGTH = 1016

    /** Maximum length of a single message after which it should be split. */
    const val MAX_MESSAGE_LENGTH = 1372

    /** Maximum size of custom packets. */
    const val MAX_CUSTOM_PACKET_SIZE = 1373

    /** Maximum file name length for file transfers. */
    const val MAX_FILENAME_LENGTH = 255

    /**
     * Maximum hostname length. This is determined by calling `getconf HOST_NAME_MAX` on the
     * console. The value presented here is valid for most systems.
     */
    const val MAX_HOSTNAME_LENGTH = 255

    /** The number of bytes in a file id. */
    const val FILE_ID_LENGTH = ToxCryptoConstants.HASH_LENGTH

    /** Default port for HTTP proxies. */
    const val DEFAULT_PROXY_PORT: UShort = 8080u

    /** Default start port for Tox UDP sockets. */
    const val DEFAULT_START_PORT: UShort = 33445u

    /** Default end port for Tox UDP sockets. */
    val DEFAULT_END_PORT: UShort = (DEFAULT_START_PORT + 100u).toUShort()

    /** Default port for Tox TCP relays. A value of 0 means disabled. */
    const val DEFAULT_TCP_PORT: UShort = 0u
}
