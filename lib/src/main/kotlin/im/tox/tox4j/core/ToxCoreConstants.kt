package im.tox.tox4j.core

object ToxCoreConstants {
    /**
     * The size of a Tox address in bytes.
     *
     * Tox addresses are in the format `[Public Key ([ToxCoreConstants.PUBLIC_KEY_SIZE] bytes)][nospam (4 bytes)][checksum (2 bytes)]`.
     *
     * The checksum is computed over the Public Key and the nospam value. The first byte is an XOR of all the even bytes (0, 2, 4, ...), the second byte is an XOR of all the odd bytes (1, 3, 5, ...) of the Public Key and nospam.
     */
    const val ADDRESS_SIZE = 38

    /** The size of a Tox Conference unique id in bytes. */
    const val CONFERENCE_ID_SIZE = 32

    /**
     * The size of a Tox DHT Public Key in bytes.
     *
     * A DHT public key is never the same as the long term public key part of the Tox address. It can be long-term (for DHT bootstrap nodes) or short term ephemeral (for Tox client nodes).
     */
    const val DHT_ID_SIZE = 32

    /** The number of bytes in a file id. */
    const val FILE_ID_LENGTH = 32

    /** Number of bytes in a group Chat ID. */
    const val GROUP_CHAT_ID_SIZE = 32

    /** Maximum length of a group custom lossless packet. */
    const val GROUP_MAX_CUSTOM_LOSSLESS_PACKET_LENGTH = 1373

    /** Maximum length of a group custom lossy packet. */
    const val GROUP_MAX_CUSTOM_LOSSY_PACKET_LENGTH = 1373

    /** Maximum length of a group name. */
    const val GROUP_MAX_GROUP_NAME_LENGTH = 48

    /** Maximum length of a group text message. */
    const val GROUP_MAX_MESSAGE_LENGTH = 1372

    /** Maximum length of a peer part message. */
    const val GROUP_MAX_PART_LENGTH = 128

    /** Maximum length of a group password. */
    const val GROUP_MAX_PASSWORD_SIZE = 32

    /** Maximum length of a group topic. */
    const val GROUP_MAX_TOPIC_LENGTH = 512

    /** Size of a peer public key. */
    const val GROUP_PEER_PUBLIC_KEY_SIZE = 32

    /**
     * Maximum size of custom packets. TODO(iphydf): should be LENGTH?
     *
     * @deprecated The macro will be removed in 0.3.0. Use the function instead.
     */
    const val MAX_CUSTOM_PACKET_SIZE = 1373

    /**
     * Maximum file name length for file transfers.
     *
     * @deprecated The macro will be removed in 0.3.0. Use the function instead.
     */
    const val MAX_FILENAME_LENGTH = 255

    /**
     * Maximum length of a friend request message in bytes.
     *
     * @deprecated The macro will be removed in 0.3.0. Use the function instead.
     */
    const val MAX_FRIEND_REQUEST_LENGTH = 921

    /**
     * Maximum length of a hostname, e.g. proxy or bootstrap node names.
     *
     * This length does not include the NUL byte. Hostnames are NUL-terminated C strings, so they are 255 characters plus one NUL byte.
     *
     * @deprecated The macro will be removed in 0.3.0. Use the function instead.
     */
    const val MAX_HOSTNAME_LENGTH = 255

    /**
     * Maximum length of a single message after which it should be split.
     *
     * @deprecated The macro will be removed in 0.3.0. Use the function instead.
     */
    const val MAX_MESSAGE_LENGTH = 1372

    /**
     * Maximum length of a nickname in bytes.
     *
     * @deprecated The macro will be removed in 0.3.0. Use the function instead.
     */
    const val MAX_NAME_LENGTH = 128

    /**
     * Maximum length of a status message in bytes.
     *
     * @deprecated The macro will be removed in 0.3.0. Use the function instead.
     */
    const val MAX_STATUS_MESSAGE_LENGTH = 1007

    /** The size of the nospam in bytes when written in a Tox address. */
    const val NOSPAM_SIZE = 4

    /** The size of a long term Tox Public Key in bytes. */
    const val PUBLIC_KEY_SIZE = 32

    /** The size of a Tox Secret Key in bytes. */
    const val SECRET_KEY_SIZE = 32

    /**
     * The major version number.
     *
     * Incremented when the API or ABI changes in an incompatible way.
     *
     * The function variants of these constants return the version number of the library. They can be used to display the Tox library version or to check whether the client is compatible with the dynamically linked version of Tox.
     */
    const val VERSION_MAJOR = 0

    /**
     * The minor version number.
     *
     * Incremented when functionality is added without  breaking the API or ABI. Set to 0 when the major version number is incremented.
     */
    const val VERSION_MINOR = 2

    /**
     * The patch or revision number.
     *
     * Incremented when bugfixes are applied without changing any functionality or API or ABI.
     */
    const val VERSION_PATCH = 25

    /** Default port for HTTP proxies. */
    const val DEFAULT_PROXY_PORT: Int = 8080

    /** Default start port for Tox UDP sockets. */
    const val DEFAULT_START_PORT: Int = 33445

    /** Default end port for Tox UDP sockets. */
    const val DEFAULT_END_PORT: Int = DEFAULT_START_PORT + 100

    /** Default port for Tox TCP relays. A value of 0 means disabled. */
    const val DEFAULT_TCP_PORT: Int = 0
}
