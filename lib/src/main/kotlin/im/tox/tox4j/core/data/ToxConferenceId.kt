package im.tox.tox4j.core.data

import kotlin.jvm.JvmInline

/**
 * A stable unique identifier for a conference.
 */
@JvmInline value class ToxConferenceId(
    val value: ByteArray,
)
