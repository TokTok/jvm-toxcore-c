package im.tox.tox4j.core.enums

/** Represents peer exit events. These should be used with the `group_peer_exit` event. */
enum class ToxGroupExitType {
    /** The peer has quit the group. */
    QUIT,

    /** Your connection with this peer has timed out. */
    TIMEOUT,

    /** Your connection with this peer has been severed. */
    DISCONNECTED,

    /**
     * Your connection with all peers has been severed. This will occur when you are kicked from a
     * group, rejoin a group, or manually disconnect from a group.
     */
    SELF_DISCONNECTED,

    /** The peer has been kicked. */
    KICK,

    /** The peer provided invalid group sync information. */
    SYNC_ERROR,
}
