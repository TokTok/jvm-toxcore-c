package im.tox.tox4j.core.enums

/** Represents moderation events. These should be used with the `group_moderation` event. */
enum class ToxGroupModEvent {
    /** A peer has been kicked from the group. */
    KICK,

    /** A peer as been given the observer role. */
    OBSERVER,

    /** A peer has been given the user role. */
    USER,

    /** A peer has been given the moderator role. */
    MODERATOR,
}
