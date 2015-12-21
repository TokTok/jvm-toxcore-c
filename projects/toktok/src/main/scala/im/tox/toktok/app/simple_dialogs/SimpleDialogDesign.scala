package im.tox.toktok.app.simple_dialogs

import android.app.{Activity, Dialog}
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View.OnClickListener
import android.view.{View, Window}
import im.tox.toktok.TypedResource._
import im.tox.toktok.{R, TR}

final class SimpleDialogDesign(
    activity: Activity,
    question: String,
    color: Int,
    icon: Int,
    clickAction: OnClickListener
) extends Dialog(activity, R.style.DialogSlideAnimation) {

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    requestWindowFeature(Window.FEATURE_NO_TITLE)
    setContentView(R.layout.simple_dialog_design)
    getWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT))

    this.findView(TR.simple_dialog_color).setBackgroundTintList(ColorStateList.valueOf(color))
    this.findView(TR.simple_dialog_img).setImageResource(icon)

    this.findView(TR.simple_dialog_text).setText(question)

    val confirmButton = this.findView(TR.simple_dialog_confirm)
    confirmButton.setOnClickListener(clickAction)

    val cancelButton = this.findView(TR.simple_dialog_cancel)
    cancelButton.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = {
        dismiss()
      }
    })
  }

}
