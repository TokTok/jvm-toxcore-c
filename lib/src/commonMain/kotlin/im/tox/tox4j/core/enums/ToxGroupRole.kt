package im.tox.tox4j.core.enums

/**
 * Represents group roles.
 *
 * Roles are hierarchical in that each role has a set of privileges plus all the privileges of the
 * roles below it.
 */
enum class ToxGroupRole {
    /**
     * May kick all other peers as well as set their role to anything (except founder). Founders may
     * also set the group password, toggle the privacy state, and set the peer limit.
     */
    FOUNDER,

    /**
     * May kick and set the user and observer roles for peers below this role. May also set the
     * group topic.
     */
    MODERATOR,

    /** May communicate with other peers normally. */
    USER,

    /**
     * May observe the group and ignore peers; may not communicate with other peers or with the
     * group.
     */
    OBSERVER,
}
