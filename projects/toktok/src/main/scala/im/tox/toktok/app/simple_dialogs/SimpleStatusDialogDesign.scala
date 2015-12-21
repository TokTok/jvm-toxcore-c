package im.tox.toktok.app.simple_dialogs

import android.app.{Activity, Dialog}
import android.graphics.Color
import android.graphics.drawable.{ColorDrawable, TransitionDrawable}
import android.os.Bundle
import android.view.View.OnClickListener
import android.view.{View, Window}
import im.tox.toktok.TypedResource._
import im.tox.toktok.{R, TR}

final class SimpleStatusDialogDesign(activity: Activity, statusActive: Int) extends Dialog(activity, R.style.DialogSlideAnimation) {

  private var activeBackgroundTransition: TransitionDrawable = null

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    requestWindowFeature(Window.FEATURE_NO_TITLE)
    setContentView(R.layout.simple_status_chooser_dialog_design)
    getWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT))

    statusActive match {
      case 0 =>
        activeBackgroundTransition = this.findView(TR.simple_dialog_status_online).getBackground.asInstanceOf[TransitionDrawable]
      case 1 =>
        activeBackgroundTransition = this.findView(TR.simple_dialog_status_away).getBackground.asInstanceOf[TransitionDrawable]
      case 2 =>
        activeBackgroundTransition = this.findView(TR.simple_dialog_status_busy).getBackground.asInstanceOf[TransitionDrawable]
    }

    activeBackgroundTransition.startTransition(250)

    val onlineStatus = this.findView(TR.simple_dialog_status_online)

    onlineStatus.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = {
        activeBackgroundTransition.reverseTransition(250)
        activeBackgroundTransition = onlineStatus.getBackground.asInstanceOf[TransitionDrawable]
        activeBackgroundTransition.startTransition(250)
      }
    })

    val awayStatus = this.findView(TR.simple_dialog_status_away)

    awayStatus.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = {
        activeBackgroundTransition.reverseTransition(250)
        activeBackgroundTransition = awayStatus.getBackground.asInstanceOf[TransitionDrawable]
        activeBackgroundTransition.startTransition(250)
      }
    })

    val busyStatus = this.findView(TR.simple_dialog_status_busy)

    busyStatus.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = {
        activeBackgroundTransition.reverseTransition(250)
        activeBackgroundTransition = busyStatus.getBackground.asInstanceOf[TransitionDrawable]
        activeBackgroundTransition.startTransition(250)

      }
    })

    val cancelButton = this.findView(TR.simple_dialog_cancel)
    cancelButton.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = {
        dismiss()
      }
    })

  }

}
