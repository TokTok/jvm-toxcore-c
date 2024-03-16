package im.tox.tox4j.core.data

import kotlin.jvm.JvmInline

/**
 * A title of a conference.
 */
@JvmInline value class ToxConferenceTitle(
    val value: ByteArray,
)
