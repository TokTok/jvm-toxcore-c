package im.tox.tox4j.core.enums

/** Conference types for the conference_invite event. */
enum class ToxConferenceType {
    /** Text-only conferences that must be accepted with the tox_conference_join function. */
    TEXT,

    /** Video conference. The function to accept these is in toxav. */
    AV,
}
