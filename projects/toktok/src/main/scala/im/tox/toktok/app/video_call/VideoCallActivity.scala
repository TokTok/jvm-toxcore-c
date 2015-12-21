package im.tox.toktok.app.video_call

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.View.OnClickListener
import android.view.animation.Animation.AnimationListener
import android.view.animation.{Animation, AnimationUtils}
import im.tox.toktok.TypedResource._
import im.tox.toktok.{R, TR}

final class VideoCallActivity extends AppCompatActivity {

  protected override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_video_call)

    val bundle = getIntent.getExtras

    val contactColor = bundle.getInt("contactPhotoReference")

    this.findView(TR.call_background).setImageResource(contactColor)

    getWindow.getDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    getWindow.setStatusBarColor(getResources.getColor(R.color.contactsTransparentBar, null))

    this.findView(TR.call_ongoing_fab).setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = {
        finish()
      }
    })

    val bottom = this.findView(TR.videocall_bar)

    val startAnimation = AnimationUtils.loadAnimation(bottom.getContext, R.anim.abc_slide_in_bottom)

    startAnimation.setAnimationListener(new AnimationListener {
      override def onAnimationEnd(animation: Animation): Unit = {
        bottom.setVisibility(View.VISIBLE)
      }

      override def onAnimationStart(animation: Animation): Unit = {}

      override def onAnimationRepeat(animation: Animation): Unit = {}
    })

    startAnimation.setDuration(500)
    startAnimation.setStartOffset(500)

    bottom.startAnimation(startAnimation)
  }

  override def onBackPressed(): Unit = {
  }

}
