package im.tox.tox4j.core.data

import kotlin.jvm.JvmInline

/**
 * A status message (self or friend).
 */
@JvmInline value class ToxStatusMessage(
    val value: ByteArray,
)
