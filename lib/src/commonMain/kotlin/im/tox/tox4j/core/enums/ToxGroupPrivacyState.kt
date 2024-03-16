package im.tox.tox4j.core.enums

/** Represents the group privacy state. */
enum class ToxGroupPrivacyState {
    /**
     * The group is considered to be public. Anyone may join the group using the Chat ID.
     *
     * If the group is in this state, even if the Chat ID is never explicitly shared with someone
     * outside of the group, information including the Chat ID, IP addresses, and peer ID's (but not
     * Tox ID's) is visible to anyone with access to a node storing a DHT entry for the given group.
     */
    PUBLIC,

    /**
     * The group is considered to be private. The only way to join the group is by having someone in
     * your contact list send you an invite.
     *
     * If the group is in this state, no group information (mentioned above) is present in the DHT;
     * the DHT is not used for any purpose at all. If a public group is set to private, all DHT
     * information related to the group will expire shortly.
     */
    PRIVATE,
}
