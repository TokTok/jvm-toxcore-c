package im.tox.toktok.app.simple_dialogs

import android.app.{Activity, Dialog}
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View.OnClickListener
import android.view.{View, Window}
import im.tox.toktok.TypedResource._
import im.tox.toktok.app.MaterialColors
import im.tox.toktok.{R, TR}

final class SimpleColorDialogDesign(
    activity: Activity,
    title: String,
    contact_color: Int,
    icon: Int,
    color: Int,
    clickAction: OnClickListener
) extends Dialog(activity, R.style.DialogSlideAnimation) {

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    requestWindowFeature(Window.FEATURE_NO_TITLE)
    setContentView(R.layout.simple_color_dialog_design)
    getWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT))

    this.findView(TR.simple_dialog_color).setBackgroundTintList(ColorStateList.valueOf(contact_color))
    this.findView(TR.simple_dialog_img).setImageResource(icon)
    this.findView(TR.simple_dialog_text).setText(title)

    val color_recycler = this.findView(TR.simple_color_dialog_recyclerview)

    color_recycler.setAdapter(new SimpleColorDialogDesignAdapter(MaterialColors.colors))
    color_recycler.setLayoutManager(new LinearLayoutManager(activity))

    val cancelButton = this.findView(TR.simple_dialog_cancel)
    cancelButton.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = {
        dismiss()
      }
    })

  }

}
