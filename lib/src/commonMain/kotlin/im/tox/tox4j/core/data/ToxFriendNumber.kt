package im.tox.tox4j.core.data

import kotlin.jvm.JvmInline

/**
 * A friend number. Unstable across sessions.
 */
@JvmInline value class ToxFriendNumber(
    val value: Int,
)
