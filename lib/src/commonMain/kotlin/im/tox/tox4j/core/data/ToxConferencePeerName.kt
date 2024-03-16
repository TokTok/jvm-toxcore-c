package im.tox.tox4j.core.data

import kotlin.jvm.JvmInline

/**
 * A peer name in a conference.
 */
@JvmInline value class ToxConferencePeerName(
    val value: ByteArray,
)
