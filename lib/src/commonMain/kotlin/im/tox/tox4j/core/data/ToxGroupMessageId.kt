package im.tox.tox4j.core.data

import kotlin.jvm.JvmInline

/**
 * A group message ID (within a session).
 */
@JvmInline value class ToxGroupMessageId(
    val value: Int,
)
