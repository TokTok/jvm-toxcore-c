package im.tox.toktok.app.call

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.graphics.{Color, Point}
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup.LayoutParams
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.Animation.AnimationListener
import android.view.animation.{Animation, AnimationUtils}
import android.widget._
import im.tox.toktok.TypedResource._
import im.tox.toktok.app.Friend
import im.tox.toktok.app.util.ActivityAdapter
import im.tox.toktok.app.video_call.VideoCallActivity
import im.tox.toktok.{R, TR}

final class CallActivity extends ActivityAdapter[CallActivityViewHolder](TR.layout.activity_call_layout) {

  private val viewType = 2

  private var backgroundInitialised = false

  protected override def onCreateViewHolder(): CallActivityViewHolder = {
    new CallActivityViewHolder(this)
  }

  protected override def onCreate(holder: CallActivityViewHolder): Unit = {
    getWindow.getDecorView.setSystemUiVisibility(
      View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    )
    getWindow.setStatusBarColor(getResources.getColor(R.color.contactsTransparentBar, null))

    holder.bottomPanel.setBackgroundColor(Color.argb(
      165,
      Color.red(holder.friendColor),
      Color.green(holder.friendColor),
      Color.blue(holder.friendColor)
    ))

    initBackground(holder, holder.friendImgSrc)

    viewType match {
      case 0 | 2 =>
        initReceiveCall(holder)
      case 1 =>
        initOnGoingCall(holder)
    }
  }

  private def initOnGoingCall(holder: CallActivityViewHolder): Unit = {
    holder.topPanel.setBackgroundColor(getResources.getColor(R.color.callTopColor, null))
    holder.topPanel.addView(getLayoutInflater.inflate(TR.layout.call_top_on_going, null))
    holder.bottomPanel.addView(getLayoutInflater.inflate(TR.layout.call_bottom_on_going, null))

    val contactsView = this.findView(TR.call_ongoing_contacts)

    val friends = Seq(
      Friend(
        -1,
        holder.friendTitle,
        "Trying the TokTok",
        0,
        Color.parseColor("#DC4254"),
        Color.parseColor("#7D2530"),
        holder.friendImgSrc
      )
    )

    val contactsLayoutManager = new LinearLayoutManager(getBaseContext)
    contactsView.setLayoutManager(contactsLayoutManager)
    contactsView.setAdapter(new CallOnGoingContactsAdapter(friends))

    this.findView(TR.call_ongoing_fab).setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = {
        finish()
      }
    })
  }

  private def initReceiveCall(holder: CallActivityViewHolder): Unit = {
    holder.topPanel.addView(getLayoutInflater.inflate(TR.layout.call_top_receive, null))
    holder.bottomPanel.addView(getLayoutInflater.inflate(TR.layout.call_bottom_receive, null))

    val friendPhotoView = this.findView(TR.call_img)
    friendPhotoView.setImageResource(holder.friendImgSrc)

    this.findView(TR.call_friend).setText(holder.friendTitle)
    this.findView(TR.call_message_input).setHint(getResources.getString(R.string.call_input_message) + " " + holder.friendTitle)

    val callMessageView = this.findView(TR.call_messages_recycler)
    val excuses = Seq(
      "Sorry I’m In Class, Call you later",
      "I’m at a meeting, can’t talk",
      "Call you later",
      "I’m driving, call you when I stop",
      "Not available"
    )

    val callMessageLayoutManager = new LinearLayoutManager(getBaseContext)
    callMessageView.setLayoutManager(callMessageLayoutManager)
    callMessageView.setAdapter(new CallMessageAdapter(excuses))

    val callAnswerSlider = this.findView(TR.call_answer)
    val callDeclineSlider = this.findView(TR.call_decline)

    callAnswerSlider.setOnCallListener(new CallListener {
      override def onCompleted(): Unit = {
        viewType match {
          case 0 =>
            val fadeOutAnimationBottom = AnimationUtils.loadAnimation(holder.bottomPanel.getContext, R.anim.abc_fade_out)
            fadeOutAnimationBottom.setDuration(250)
            fadeOutAnimationBottom.setAnimationListener(new AnimationListener {
              override def onAnimationEnd(animation: Animation): Unit = {
                holder.bottomPanel.removeAllViews()
              }

              override def onAnimationStart(animation: Animation): Unit = {
              }

              override def onAnimationRepeat(animation: Animation): Unit = {
              }
            })

            holder.bottomPanel.getChildAt(0).startAnimation(fadeOutAnimationBottom)

            val fadeOutAnimationTop = AnimationUtils.loadAnimation(holder.bottomPanel.getContext, R.anim.abc_fade_out)
            fadeOutAnimationTop.setDuration(250)
            fadeOutAnimationTop.setAnimationListener(new AnimationListener {
              override def onAnimationEnd(animation: Animation): Unit = {
                holder.topPanel.removeAllViews()
                initOnGoingCall(holder)
              }

              override def onAnimationStart(animation: Animation): Unit = {
              }

              override def onAnimationRepeat(animation: Animation): Unit = {
              }
            })

            holder.topPanel.getChildAt(0).startAnimation(fadeOutAnimationTop)

          case 2 =>
            val videoIntent = new Intent(CallActivity.this, classOf[VideoCallActivity])
            videoIntent.putExtras(holder.bundle)
            startActivity(videoIntent)
            overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out)
            finish()
        }
      }

      override def onStart(): Unit = {
        val animation = AnimationUtils.loadAnimation(callDeclineSlider.getContext, R.anim.abc_fade_out)
        animation.setDuration(250)
        animation.setAnimationListener(new AnimationListener {
          override def onAnimationEnd(animation: Animation): Unit = {
            callDeclineSlider.setVisibility(View.INVISIBLE)
          }

          override def onAnimationStart(animation: Animation): Unit = {}

          override def onAnimationRepeat(animation: Animation): Unit = {}
        })
        callDeclineSlider.startAnimation(animation)
      }

      override def onReleased(): Unit = {
        val animation = AnimationUtils.loadAnimation(callDeclineSlider.getContext, R.anim.abc_fade_in)
        animation.setDuration(250)
        animation.setAnimationListener(new AnimationListener {
          override def onAnimationEnd(animation: Animation): Unit = {
            callDeclineSlider.setVisibility(View.VISIBLE)
          }

          override def onAnimationStart(animation: Animation): Unit = {}

          override def onAnimationRepeat(animation: Animation): Unit = {}
        })
        callDeclineSlider.startAnimation(animation)
      }

    })

    callDeclineSlider.setOnCallListener(new CallListener {

      override def onCompleted(): Unit = {
        finish()
      }

      override def onReleased(): Unit = {
        val animation = AnimationUtils.loadAnimation(callAnswerSlider.getContext, R.anim.abc_fade_in)
        animation.setDuration(250)
        animation.setAnimationListener(new AnimationListener {
          override def onAnimationEnd(animation: Animation): Unit = {
            callAnswerSlider.setVisibility(View.VISIBLE)
          }

          override def onAnimationStart(animation: Animation): Unit = {}

          override def onAnimationRepeat(animation: Animation): Unit = {}
        })
        callAnswerSlider.startAnimation(animation)
      }

      override def onStart(): Unit = {
        val animation = AnimationUtils.loadAnimation(callAnswerSlider.getContext, R.anim.abc_fade_out)
        animation.setDuration(250)
        animation.setAnimationListener(new AnimationListener {
          override def onAnimationEnd(animation: Animation): Unit = {
            callAnswerSlider.setVisibility(View.INVISIBLE)
          }

          override def onAnimationStart(animation: Animation): Unit = {}

          override def onAnimationRepeat(animation: Animation): Unit = {}
        })
        callAnswerSlider.startAnimation(animation)
      }
    })

    val bottomTextBar = this.findView(TR.call_message_bottom_bar)

    bottomTextBar.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = {
        Log.d("asdasd", "asda")
      }
    })
  }

  private def initBackground(holder: CallActivityViewHolder, imgResource: Int): Unit = {
    val background = this.findView(TR.call_background)
    background.setImageResource(imgResource)

    val screen = getWindowManager.getDefaultDisplay
    val screenSize = new Point()
    screen.getSize(screenSize)

    val content = findViewById(android.R.id.content).getRootView

    content.getViewTreeObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener {
      override def onGlobalLayout(): Unit = {
        if (!backgroundInitialised) {
          val picture = BlurBuilder.blur(background)
          background.setImageDrawable(new BitmapDrawable(getResources, picture))
          backgroundInitialised = true
        }
      }
    })

    val midHeight = screenSize.y * 0.5

    val params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, midHeight.toInt)
    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
    holder.bottomPanel.setLayoutParams(params)
    holder.topPanel.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, midHeight.toInt))
  }

  override def onBackPressed(): Unit = {
  }

}

final class CallActivityViewHolder(activity: CallActivity) {
  val topPanel = activity.findView(TR.call_top_panel)
  val bottomPanel = activity.findView(TR.call_bottom_panel)

  val bundle = activity.getIntent.getExtras
  val friendTitle = bundle.getString("contactName")
  val friendColor = bundle.getInt("contactColorPrimary")
  val friendImgSrc = bundle.getInt("contactPhotoReference")
}

trait CallListener {
  def onCompleted(): Unit
  def onStart(): Unit
  def onReleased(): Unit
}
