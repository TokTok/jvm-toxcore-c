package im.tox.tox4j.core.data

import kotlin.jvm.JvmInline

/** IP_Port stores an IP datastructure with a port. */
@JvmInline value class Port constructor(
    val value: UShort,
)
