package im.tox.tox4j.core.options

import im.tox.tox4j.core.data.ToxSecretKey
import im.tox.tox4j.core.enums.ToxSavedataType

/** Base type for all save data kinds. */
object SaveDataOptions {
  sealed interface Type {
    /** The low level [[ToxSavedataType]] enum to pass to [[ToxCore.load]]. */
    val kind: ToxSavedataType

    /** Serialised save data. The format depends on [[kind]]. */
    val data: ByteArray
  }

  /** The various kinds of save data that can be loaded by [[ToxCore.load]]. */

  /** No save data. */
  object None : Type {
    override val kind: ToxSavedataType = ToxSavedataType.NONE
    override val data: ByteArray = byteArrayOf()
  }

  /**
   * Full save data containing friend list, last seen DHT nodes, name, and all other information
   * contained within a Tox instance.
   */
  final data class ToxSave(override val data: ByteArray) : Type {
    override val kind: ToxSavedataType = ToxSavedataType.TOX_SAVE
  }

  /**
   * Minimal save data with just the secret key. The public key can be derived from it. Saving this
   * secret key, the friend list, name, and noSpam value is sufficient to restore the observable
   * behaviour of a Tox instance without the full save data in [[ToxSave]].
   */
  final data class SecretKey(private val key: ToxSecretKey) : Type {
    override val kind: ToxSavedataType = ToxSavedataType.SECRET_KEY
    override val data: ByteArray = key.value
  }
}
