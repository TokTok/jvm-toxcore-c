package im.tox.tox4j.core.data

import kotlin.jvm.JvmInline

/**
 * A lossless packet payload.
 */
@JvmInline value class ToxLosslessPacket(
    val value: ByteArray,
)
