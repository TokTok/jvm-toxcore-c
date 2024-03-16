package im.tox.tox4j.core.data

import kotlin.jvm.JvmInline

/**
 * A secret key.
 */
@JvmInline value class ToxSecretKey(
    val value: ByteArray,
)
