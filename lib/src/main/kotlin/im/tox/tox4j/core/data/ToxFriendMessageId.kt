package im.tox.tox4j.core.data

import kotlin.jvm.JvmInline

/**
 * A friend message ID. Used to associate read receipts with sent messages.
 */
@JvmInline value class ToxFriendMessageId(
    val value: Int,
)
