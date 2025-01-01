package im.tox.tox4j.core.data

import kotlin.jvm.JvmInline

/**
 * A friend request message.
 */
@JvmInline value class ToxFriendRequestMessage(
    val value: ByteArray,
)
