package im.tox.toktok.app.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.{CompoundButton, Switch}
import im.tox.toktok.R
import im.tox.toktok.app.models.ApplicationPreferences
import io.realm.Realm

class Settings extends AppCompatActivity {

  var db: Realm = _
  var ui_color: ApplicationPreferences = _
  var ui_dark_mode: ApplicationPreferences = _
  var ui_text_dark: ApplicationPreferences = _

  var theme_switch: Switch = _

  override protected def onCreate(savedInstanceState: Bundle): Unit = {

    db = Realm.getInstance(this)

    val ui_list = db.allObjects(classOf[ApplicationPreferences])
    val i = 0

    for (i <- 0 until ui_list.size()) {

      val setting = ui_list.get(i)

      setting.getField match {
        case "ui_color"      => ui_color = setting
        case "ui_dark_text"  => ui_text_dark = setting
        case "ui_dark_theme" => ui_dark_mode = setting

      }
    }

    if (ui_dark_mode.getFieldValue == 1) {
      setTheme(R.style.AppTheme_Dark)
    } else {
      setTheme(R.style.AppTheme)
    }

    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_settings)

    theme_switch = findViewById(R.id.settings_dark_theme_switch).asInstanceOf[Switch]

    theme_switch.setChecked(ui_dark_mode.getFieldValue == 1)

    theme_switch.setOnCheckedChangeListener(new OnCheckedChangeListener {
      override def onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean): Unit = {

        db.beginTransaction()

        if (isChecked) {
          ui_dark_mode.setFieldValue(1)
        } else {
          ui_dark_mode.setFieldValue(0)
        }

        db.commitTransaction()

        startActivity(new Intent(Settings.this, classOf[Settings]))
        finish()

      }
    })
  }

}
