package im.tox.tox4j.core.enums

/** Represents the state of the group topic lock. */
enum class ToxGroupTopicLock {
    /**
     * The topic lock is enabled. Only peers with the founder and moderator roles may set the topic.
     */
    ENABLED,

    /**
     * The topic lock is disabled. All peers except those with the observer role may set the topic.
     */
    DISABLED,
}
