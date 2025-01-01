package im.tox.tox4j.core.data

import kotlin.jvm.JvmInline

/**
 * A message text sent in a conference.
 */
@JvmInline value class ToxConferenceMessage(
    val value: ByteArray,
)
