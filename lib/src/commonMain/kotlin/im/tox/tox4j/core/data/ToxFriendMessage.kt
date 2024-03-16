package im.tox.tox4j.core.data

import kotlin.jvm.JvmInline

/**
 * A message text sent from/to a friend.
 */
@JvmInline value class ToxFriendMessage(
    val value: ByteArray,
)
