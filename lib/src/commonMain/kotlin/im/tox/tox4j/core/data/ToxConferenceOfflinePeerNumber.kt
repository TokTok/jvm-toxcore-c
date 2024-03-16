package im.tox.tox4j.core.data

import kotlin.jvm.JvmInline

/**
 * An unstable integer identifier for an offline peer in a conference.
 */
@JvmInline value class ToxConferenceOfflinePeerNumber(
    val value: Int,
)
