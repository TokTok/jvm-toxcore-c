package im.tox.tox4j.core.enums

/**
 * Represents types of failed group join attempts. These are used in the tox_callback_group_rejected
 * callback when a peer fails to join a group.
 */
enum class ToxGroupJoinFail {
    /** The group peer limit has been reached. */
    PEER_LIMIT,

    /** You have supplied an invalid password. */
    INVALID_PASSWORD,

    /**
     * The join attempt failed due to an unspecified error. This often occurs when the group is not
     * found in the DHT.
     */
    UNKNOWN,
}
