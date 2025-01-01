package im.tox.tox4j.core.data

import kotlin.jvm.JvmInline

/**
 * A file ID.
 */
@JvmInline value class ToxFileId(
    val value: ByteArray,
)
