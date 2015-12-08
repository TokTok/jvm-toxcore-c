package im.tox.core.settings

import org.scalatest.FunSuite

final class SettingsTest extends FunSuite {

  sealed abstract class SettingKey[A](default: A) extends Settings.Key(this, default)

  test("default Int") {
    case object IntSetting extends SettingKey(10)

    val settings = Settings()
    assert(settings(IntSetting) == 10)
  }

  test("inject Int, project Int => override value") {
    case object IntSetting extends SettingKey(10)

    val settings = Settings(IntSetting := 20)
    assert(settings(IntSetting) == 20)
  }

  test("inject Int, project String => default value") {
    val settings = {
      case object IntSetting extends SettingKey(10)
      Settings(IntSetting := 20)
    }
    case object IntSetting extends SettingKey("10")
    assert(settings(IntSetting) == "10")
  }

  test(s"type without $Universal instance") {
    final class NewType
    case object NewSetting extends SettingKey(new NewType)

    assertTypeError("NewSetting := new NewType")
  }

  test("toString contains the setting object names") {
    case object IntSetting extends SettingKey(10)

    val settings = Settings(IntSetting := 20)
    assert(settings.toString.contains("IntSetting"))
  }

}
