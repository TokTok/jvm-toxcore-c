package im.tox.tox4j.core.data

import kotlin.jvm.JvmInline

/**
 * A Tox friend address (with nospam and checksum).
 */
@JvmInline value class ToxFriendAddress(
    val value: ByteArray,
)
