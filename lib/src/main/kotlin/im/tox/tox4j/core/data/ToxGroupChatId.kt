package im.tox.tox4j.core.data

import kotlin.jvm.JvmInline

/**
 * A group chat ID (stable).
 */
@JvmInline value class ToxGroupChatId(
    val value: ByteArray,
)
