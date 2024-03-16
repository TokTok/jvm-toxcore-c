package im.tox.tox4j.core.data

import kotlin.jvm.JvmInline

/**
 * A user (friend or self) nickname.
 */
@JvmInline value class ToxNickname(
    val value: ByteArray,
)
