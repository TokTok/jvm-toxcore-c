package im.tox.tox4j.core.options

import im.tox.tox4j.core.enums.ToxSavedataType
import im.tox.tox4j.core.{ToxCore, ToxSecretKey}

/**
 * Base type for all save data kinds.
 */
sealed trait SaveDataOptions {
  /**
   * The low level [[ToxSavedataType]] enum to pass to [[ToxCore.load]].
   */
  def kind: ToxSavedataType

  /**
   * Serialised save data. The format depends on [[kind]].
   */
  def data: Array[Byte]
}

/**
 * The various kinds of save data that can be loaded by [[ToxCore.load]].
 */
object SaveDataOptions {

  /**
   * No save data.
   */
  case object None extends SaveDataOptions {
    override def kind: ToxSavedataType = ToxSavedataType.NONE
    override def data: Array[Byte] = Array.empty
  }

  /**
   * Full save data containing friend list, last seen DHT nodes, name, and all other information
   * contained within a Tox instance.
   */
  final case class ToxSave(data: Array[Byte]) extends SaveDataOptions {
    override def kind: ToxSavedataType = ToxSavedataType.TOX_SAVE
  }

  /**
   * Minimal save data with just the secret key. The public key can be derived from it. Saving this
   * secret key, the friend list, name, and noSpam value is sufficient to restore the observable
   * behaviour of a Tox instance without the full save data in [[ToxSave]].
   */
  final case class SecretKey(key: ToxSecretKey) extends SaveDataOptions {
    override def kind: ToxSavedataType = ToxSavedataType.SECRET_KEY
    override def data: Array[Byte] = key.value
  }

}
