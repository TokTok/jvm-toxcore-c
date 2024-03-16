package im.tox.tox4j.core.data

import kotlin.jvm.JvmInline

/**
 * A group message.
 */
@JvmInline value class ToxGroupMessage(
    val value: ByteArray,
)
