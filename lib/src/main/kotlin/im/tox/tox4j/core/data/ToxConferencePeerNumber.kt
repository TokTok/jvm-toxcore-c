package im.tox.tox4j.core.data

import kotlin.jvm.JvmInline

/**
 * An unstable integer identifier for an online peer in a conference.
 */
@JvmInline value class ToxConferencePeerNumber(
    val value: Int,
)
