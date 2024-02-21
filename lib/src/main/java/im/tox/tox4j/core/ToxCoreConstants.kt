package im.tox.tox4j.core

import im.tox.tox4j.crypto.ToxCryptoConstants

object ToxCoreConstants {
    /** The size of a Tox Public Key in bytes. */
    const val PublicKeySize = ToxCryptoConstants.PublicKeyLength

    /** The size of a Tox Secret Key in bytes. */
    const val SecretKeySize = ToxCryptoConstants.SecretKeyLength

    /**
     * The size of a Tox address in bytes. Tox addresses are in the format
     * [Public Key ([ [PublicKeySize]] bytes)][nospam (4 bytes)][checksum (2 bytes)].
     *
     * The checksum is computed over the Public Key and the nospam value. The first byte is an XOR of
     * all the odd bytes, the second byte is an XOR of all the even bytes of the Public Key and
     * nospam.
     */
    const val AddressSize = PublicKeySize + 4 + 2

    /** Maximum length of a nickname in bytes. */
    const val MaxNameLength = 128

    /** Maximum length of a status message in bytes. */
    const val MaxStatusMessageLength = 1007

    /** Maximum length of a friend request message in bytes. */
    const val MaxFriendRequestLength = 1016

    /** Maximum length of a single message after which it should be split. */
    const val MaxMessageLength = 1372

    /** Maximum size of custom packets. */
    const val MaxCustomPacketSize = 1373

    /** Maximum file name length for file transfers. */
    const val MaxFilenameLength = 255

    /**
     * Maximum hostname length. This is determined by calling `getconf HOST_NAME_MAX` on the console.
     * The value presented here is valid for most systems.
     */
    const val MaxHostnameLength = 255

    /** The number of bytes in a file id. */
    const val FileIdLength = ToxCryptoConstants.HashLength

    /** Default port for HTTP proxies. */
    const val DefaultProxyPort: UShort = 8080u

    /** Default start port for Tox UDP sockets. */
    const val DefaultStartPort: UShort = 33445u

    /** Default end port for Tox UDP sockets. */
    val DefaultEndPort: UShort = (DefaultStartPort + 100u).toUShort()

    /** Default port for Tox TCP relays. A value of 0 means disabled. */
    const val DefaultTcpPort: UShort = 0u
}
