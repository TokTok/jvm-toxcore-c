package im.tox.tox4j.core.data

import kotlin.jvm.JvmInline

/**
 * An unstable integer identifier for a conference in a tox instance.
 */
@JvmInline value class ToxConferenceNumber(
    val value: Int,
)
