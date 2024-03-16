package im.tox.tox4j.core.data

import kotlin.jvm.JvmInline

/**
 * A group password.
 */
@JvmInline value class ToxGroupPassword(
    val value: ByteArray,
)
