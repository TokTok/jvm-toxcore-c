package im.tox.tox4j.core.enums

/**
 * Represents the group voice state, which determines which Group Roles have permission to speak in
 * the group chat. The voice state does not have any effect private messages or topic setting.
 */
enum class ToxGroupVoiceState {
    /** All group roles above Observer have permission to speak. */
    ALL,

    /** Moderators and Founders have permission to speak. */
    MODERATOR,

    /** Only the founder may speak. */
    FOUNDER,
}
