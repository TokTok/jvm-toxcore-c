package im.tox.tox4j.core.data

import kotlin.jvm.JvmInline

/**
 * A lossy packet payload.
 */
@JvmInline value class ToxLossyPacket(
    val value: ByteArray,
)
