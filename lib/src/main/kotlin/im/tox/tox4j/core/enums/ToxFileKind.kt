package im.tox.tox4j.core.enums

object ToxFileKind {
    /** Arbitrary file data. Clients can choose to handle it based on the file name or magic or any other way they choose. */
    const val DATA = 0

    /**
     * Avatar file_id. This consists of [hash](image). Avatar data. This consists of the image data.
     *
     * Avatars can be sent at any time the client wishes. Generally, a client will send the avatar to a friend when that friend comes online, and to all friends when the avatar changed. A client can save some traffic by remembering which friend received the updated avatar already and only send it if the friend has an out of date avatar.
     *
     * Clients who receive avatar send requests can reject it (by sending [ToxCoreConstants.FILE_CONTROL_CANCEL] before any other controls), or accept it (by sending [ToxCoreConstants.FILE_CONTROL_RESUME]). The file_id of length [ToxCryptoConstants.HASH_LENGTH] bytes (same length as [ToxCoreConstants.FILE_ID_LENGTH]) will contain the hash. A client can compare this hash with a saved hash and send [ToxCoreConstants.FILE_CONTROL_CANCEL] to terminate the avatar transfer if it matches.
     *
     * When file_size is set to 0 in the transfer request it means that the client has no avatar.
     */
    const val AVATAR = 1

    /** To be specified. */
    const val STICKER = 2

    /** Here the file_id is the specified hash of the data. */
    const val SHA1 = 3

    const val SHA256 = 4
}
