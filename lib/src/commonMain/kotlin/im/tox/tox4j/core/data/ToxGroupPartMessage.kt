package im.tox.tox4j.core.data

import kotlin.jvm.JvmInline

/**
 * A leaving message when a user leaves a group chat.
 */
@JvmInline value class ToxGroupPartMessage(
    val value: ByteArray,
)
