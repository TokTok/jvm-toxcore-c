package im.tox.tox4j.core.data

import kotlin.jvm.JvmInline

/**
 * A tox public key (no nospam/checksum).
 */
@JvmInline value class ToxPublicKey(
    val value: ByteArray,
)
