package im.tox.tox4j.av.enums

object ToxAvFriendCallState {
    /** The empty bit mask. None of the bits specified below are set. */
    const val NONE = 0

    /** Set by the AV core if an error occurred on the remote end or if friend timed out. This is the final state after which no more state transitions can occur for the call. This call state will never be triggered in combination with other call states. */
    const val ERROR = 1

    /** The call has finished. This is the final state after which no more state transitions can occur for the call. This call state will never be triggered in combination with other call states. */
    const val FINISHED = 2

    /** The flag that marks that friend is sending audio. */
    const val SENDING_A = 4

    /** The flag that marks that friend is sending video. */
    const val SENDING_V = 8

    /** The flag that marks that friend is receiving audio. */
    const val ACCEPTING_A = 16

    /** The flag that marks that friend is receiving video. */
    const val ACCEPTING_V = 32
}
