package im.tox.core.settings

import im.tox.core.settings.Universal.Value

object Settings {

  abstract class Key[T, A](
      final val section: T,
      final val default: A
  ) {
    def :=(value: A)(implicit U: Universal[A]): Override[T, A] = { // scalastyle:ignore method.name
      Override(this, U.inject(value))
    }
  }

  final case class Override[T, A](key: Key[T, A], value: Value[A]) {
    override def toString: String = s"$key := $value"
  }

  def apply(settings: Settings.Override[_, _]*): Settings = new Settings(settings.reverse)

}

final class Settings(settings: Seq[Settings.Override[_, _]]) {

  def apply[T, A](key: Settings.Key[T, A])(implicit U: Universal[A]): A = {
    val value = for {
      setting <- settings.find(_.key == key)
      value <- U.project(setting.value)
    } yield {
      value
    }

    value.getOrElse(key.default)
  }

  override def toString: String = {
    s"${getClass.getSimpleName}(${settings.mkString(", ")})"
  }

}
