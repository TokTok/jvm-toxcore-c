package im.tox.tox4j.core.options

import im.tox.tox4j.core.ToxCore
import im.tox.tox4j.core.data.ToxSecretKey
import im.tox.tox4j.core.enums.ToxSavedataType

/** Base type for all save data kinds. */
@Suppress("ktlint:standard:no-consecutive-comments")
object SavedataOptions {
    sealed interface Type {
        /** The low level [ToxSavedataType] enum to pass to [ToxCore]. */
        val savedataType: ToxSavedataType

        /** Serialised save data. The format depends on [savedataType]. */
        val savedata: ByteArray
    }

    /** The various kinds of save data that can be loaded by [ToxCore]. */

    /** No save data. */
    object None : Type {
        override val savedataType: ToxSavedataType = ToxSavedataType.NONE
        override val savedata: ByteArray = byteArrayOf()
    }

    /**
     * Full save data containing friend list, last seen DHT nodes, name, and all other information
     * contained within a Tox instance.
     */
    class ToxSave(
        override val savedata: ByteArray,
    ) : Type {
        override val savedataType: ToxSavedataType = ToxSavedataType.TOX_SAVE

        // A data class would compare the arrays by reference. `toString`
        // leaves out the data, which holds the secret key.
        override fun equals(other: Any?): Boolean = this === other || (other is ToxSave && savedata.contentEquals(other.savedata))

        override fun hashCode(): Int = savedata.contentHashCode()

        override fun toString(): String = "ToxSave(savedata=<${savedata.size} bytes>)"
    }

    /**
     * Minimal save data with just the secret key. The public key can be derived from it. Saving
     * this secret key, the friend list, name, and noSpam value is sufficient to restore the
     * observable behaviour of a Tox instance without the full save data in [ToxSave].
     */
    class SecretKey(
        private val key: ToxSecretKey,
    ) : Type {
        override val savedataType: ToxSavedataType = ToxSavedataType.SECRET_KEY
        override val savedata: ByteArray = key.value

        override fun equals(other: Any?): Boolean = this === other || (other is SecretKey && savedata.contentEquals(other.savedata))

        override fun hashCode(): Int = savedata.contentHashCode()

        override fun toString(): String = "SecretKey(<${savedata.size} bytes>)"
    }
}
