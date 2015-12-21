package im.tox.toktok.app.main.friends

import android.app.Activity
import android.content.{Context, Intent}
import android.content.res.ColorStateList
import android.graphics.drawable.TransitionDrawable
import android.os.{Bundle, Handler}
import android.support.design.widget.{CollapsingToolbarLayout, FloatingActionButton, Snackbar}
import android.support.v4.view.{MotionEventCompat, ViewCompat}
import android.support.v4.widget.ViewDragHelper
import android.support.v7.widget.{CardView, Toolbar}
import android.util.{AttributeSet, Log}
import android.view.View.MeasureSpec
import android.view.{MotionEvent, View, ViewGroup}
import android.view.animation.{Animation, AnimationUtils}
import android.widget.{ImageView, RelativeLayout, TextView}
import im.tox.toktok.TypedResource._
import im.tox.toktok.app.Friend
import im.tox.toktok.app.call.CallActivity
import im.tox.toktok.app.contacts.FileSendActivity
import im.tox.toktok.app.message_activity.MessageActivity
import im.tox.toktok.app.simple_dialogs.{SimpleColorDialogDesign, SimpleDialogDesign, SimpleTextDialogDesign}
import im.tox.toktok.app.video_call.VideoCallActivity
import im.tox.toktok.{R, TR}

final class SlideInContactsLayout(
    context: Context,
    attrs: AttributeSet,
    defStyle: Int
) extends ViewGroup(context, attrs, defStyle) {

  private val mDragHelper = ViewDragHelper.create(this, 1f, new DragHelperCallback)
  private var mCoordinator: View = null
  private var mCollapsingToolbarLayout: CollapsingToolbarLayout = null
  private var mFloatingActionButton: FloatingActionButton = null
  private var mUserImage: ImageView = null
  private var mSubtitle: TextView = null
  private var mTitle: TextView = null
  private var mSettingsTitle: TextView = null
  private var mToolbar: Toolbar = null
  private var mStatusBar: View = null
  private var mEditNameButton: RelativeLayout = null
  private var activity: Activity = null
  private var friend: Friend = null
  private var mInitialMotionY = .0
  private var mDragRange: Int = 0
  private var mTop: Int = 0
  private var scrollActive: Boolean = false
  private var mDragOffset = .0
  private var backgroundTransition: TransitionDrawable = null
  private var mVoiceCall: TextView = null
  private var mVideoCall: TextView = null
  private var mMessage: CardView = null
  private var mSaveProfile: CardView = null
  private var mFilesSend: CardView = null
  private var mDeleteFriend: RelativeLayout = null
  private var mBlockFriend: RelativeLayout = null
  private var mChangeColor: RelativeLayout = null
  private var scrollTop: Float = 0
  private var bundle: Bundle = null
  private val icons = Array(
    TR.contacts_icon_call,
    TR.contacts_icon_message,
    TR.contacts_icon_image,
    TR.contacts_icon_download,
    TR.contacts_icon_palette,
    TR.contacts_icon_edit,
    TR.contacts_icon_trash,
    TR.contacts_icon_lock
  )

  def this(context: Context, attrs: AttributeSet) { this(context, attrs, 0) }
  def this(context: Context) { this(context, null) }

  protected override def onFinishInflate(): Unit = {
    mCoordinator = this.findView(TR.contacts_coordinator_layout)
    mCollapsingToolbarLayout = this.findView(TR.contacts_collapsing_toolbar)
    mFloatingActionButton = this.findView(TR.contacts_FAB)
    mUserImage = this.findView(TR.contact_image)
    mTitle = findViewById(R.id.contact_title).asInstanceOf[TextView]
    mSubtitle = this.findView(TR.contact_subtitle)
    mSettingsTitle = this.findView(TR.contacts_other_title)
    mToolbar = this.findView(TR.contacts_toolbar)
    mVoiceCall = this.findView(TR.contacts_item_voice_call)
    mVideoCall = this.findView(TR.contacts_item_video_call)
    mEditNameButton = this.findView(TR.contacts_edit_alias)
    mStatusBar = this.findView(TR.contacts_status_bar_color)
    mStatusBar.getLayoutParams.height = getStatusBarHeight
    mMessage = this.findView(TR.contacts_message)
    mSaveProfile = this.findView(TR.contacts_save_photo)
    mFilesSend = this.findView(TR.contacts_file_download)
    mDeleteFriend = this.findView(TR.contacts_delete)
    mBlockFriend = this.findView(TR.contacts_block_friend)
    mChangeColor = this.findView(TR.contacts_edit_color)
    super.onFinishInflate()
  }

  def start(activity: Activity, friend: Friend, actionBarHeight: Int) {
    this.activity = activity
    mTitle.setText(friend.userName)
    mCollapsingToolbarLayout.setBackgroundColor(friend.color)
    mCollapsingToolbarLayout.setContentScrimColor(friend.color)
    mCollapsingToolbarLayout.setStatusBarScrimColor(friend.secondColor)
    mUserImage.setImageResource(friend.photoReference)
    mFloatingActionButton.setBackgroundTintList(ColorStateList.valueOf(friend.color))
    mSubtitle.setText(friend.userMessage)
    mSettingsTitle.setTextColor(ColorStateList.valueOf(friend.color))
    val b: CollapsingToolbarLayout.LayoutParams = mToolbar.getLayoutParams.asInstanceOf[CollapsingToolbarLayout.LayoutParams]
    b.height = actionBarHeight + getStatusBarHeight
    mToolbar.setLayoutParams(b)
    mToolbar.setPadding(0, getStatusBarHeight, 0, 0)
    for (item <- icons) {
      val icon = this.findView(item)
      icon.setImageTintList(ColorStateList.valueOf(friend.color))
    }
    initListeners(friend)
    this.friend = friend
    setVisibility(View.VISIBLE)
    backgroundTransition = getBackground.asInstanceOf[TransitionDrawable]
    backgroundTransition.startTransition(500)
    val a: Animation = AnimationUtils.loadAnimation(getContext, R.anim.slide_in_bottom)
    a.setAnimationListener(new Animation.AnimationListener() {
      def onAnimationStart(animation: Animation) {
      }

      def onAnimationEnd(animation: Animation) {
        mCoordinator.setVisibility(View.VISIBLE)
      }

      def onAnimationRepeat(animation: Animation) {
      }
    })
    mCoordinator.startAnimation(a)
  }

  private[friends] def smoothSlideTo(slideOffset: Float): Boolean = {
    val topBound: Int = getPaddingTop
    val y: Int = (topBound + slideOffset * mDragRange).toInt
    if (mDragHelper.smoothSlideViewTo(mCoordinator, mCoordinator.getLeft, y)) {
      ViewCompat.postInvalidateOnAnimation(this)
      true
    } else {
      false
    }
  }

  override def computeScroll() {
    if (mDragHelper.continueSettling(true)) {
      ViewCompat.postInvalidateOnAnimation(this)
    }
  }

  override def onInterceptTouchEvent(ev: MotionEvent): Boolean = {
    if (scrollActive) {
      mDragHelper.cancel()
      return false
    }
    val action: Int = MotionEventCompat.getActionMasked(ev)
    val y: Float = ev.getY
    action match {
      case MotionEvent.ACTION_DOWN =>
        Log.d("Asda", "Intercept Touch DOWN")
      case MotionEvent.ACTION_MOVE =>
        Log.d("Asda", "Intercept Touch MOVE")
      case MotionEvent.ACTION_CANCEL | MotionEvent.ACTION_UP =>
        Log.d("Asda", "Intercept Touch UP")
    }
    mDragHelper.shouldInterceptTouchEvent(ev)
  }

  override def onTouchEvent(ev: MotionEvent): Boolean = {
    try {
      mDragHelper.processTouchEvent(ev)
      true
    } catch {
      case ex: Exception =>
        false
    }
  }

  override def dispatchTouchEvent(ev: MotionEvent): Boolean = {
    val y = ev.getY
    val v = this.findView(TR.contacts_nested)

    ev.getAction match {
      case MotionEvent.ACTION_DOWN =>
        mInitialMotionY = y
      case MotionEvent.ACTION_MOVE =>
        val dy = mInitialMotionY - y
      case MotionEvent.ACTION_UP =>
        val dy = mInitialMotionY - y
        if (dy > 0) {
          if (mDragOffset < 0.5 && !scrollActive) {
            smoothSlideTo(0)
            scrollActive = true
            mStatusBar.setVisibility(View.VISIBLE)
            mStatusBar.bringToFront()
            scrollTop = v.getBottom
          }
        } else {
          if (!scrollActive && Math.abs(dy) > 20) {
            if (mDragOffset > 0.5f) {
              finish()
            } else {
              smoothSlideTo(0.5f)
              mStatusBar.setVisibility(View.INVISIBLE)
            }
          } else {
            if (v.getBottom >= scrollTop.toInt) {
              scrollActive = false
            }
          }
        }
    }
    super.dispatchTouchEvent(ev)
  }

  protected override def onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    measureChildren(widthMeasureSpec, heightMeasureSpec)
    val maxWidth: Int = MeasureSpec.getSize(widthMeasureSpec)
    val maxHeight: Int = MeasureSpec.getSize(heightMeasureSpec)
    setMeasuredDimension(View.resolveSizeAndState(maxWidth, widthMeasureSpec, 0), View.resolveSizeAndState(maxHeight, heightMeasureSpec, 0))
  }

  protected override def onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    mDragRange = getHeight
    if (changed) {
      mTop = getHeight / 2
      mCoordinator.layout(0, getHeight / 2, r, mTop + mCoordinator.getMeasuredHeight)
    } else {
      mCoordinator.layout(0, mTop, r, mTop + mCoordinator.getMeasuredHeight)
    }
  }

  def finish() {
    smoothSlideTo(1f)
    backgroundTransition.reverseTransition(500)
    val handler: Handler = new Handler
    handler.postDelayed(new Runnable() {
      def run() {
        mCoordinator.setVisibility(View.INVISIBLE)
        setVisibility(View.GONE)
      }
    }, 500)
  }

  private final class DragHelperCallback extends ViewDragHelper.Callback {
    def tryCaptureView(child: View, pointerId: Int): Boolean = {
      child == mCoordinator
    }

    override def onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
      mTop = top
      mDragOffset = top.toFloat / mDragRange
      requestLayout()
    }

    override def onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
      var top: Int = getPaddingTop
      if (yvel > 0 || (yvel == 0 && mDragOffset > 0.5f)) {
        top += mDragRange
      }
    }

    override def getViewVerticalDragRange(child: View): Int = {
      mDragRange
    }

    override def clampViewPositionVertical(child: View, top: Int, dy: Int): Int = {
      val topBound: Int = 0
      val bottomBound: Int = getHeight
      Math.min(Math.max(top, topBound), bottomBound)
    }
  }

  private def initListeners(friend: Friend) {
    mEditNameButton.setOnClickListener(new View.OnClickListener() {
      def onClick(v: View) {
        val dial = new SimpleTextDialogDesign(activity, getResources.getString(R.string.contact_popup_edit_alias), friend.color, R.drawable.ic_person_black_48dp, friend.userName, null)
        dial.show()
      }
    })
    mVoiceCall.setOnClickListener(new View.OnClickListener() {
      def onClick(v: View) {
        bundle = new Bundle
        bundle.putString("contactName", friend.userName)
        bundle.putInt("contactColorPrimary", friend.color)
        bundle.putInt("contactPhotoReference", friend.photoReference)
        val newIntent: Intent = new Intent(activity, classOf[CallActivity])
        newIntent.putExtras(bundle)
        activity.startActivity(newIntent)
      }
    })
    mVideoCall.setOnClickListener(new View.OnClickListener() {
      def onClick(v: View) {
        bundle = new Bundle
        bundle.putInt("contactPhotoReference", friend.photoReference)
        val newIntent: Intent = new Intent(activity, classOf[VideoCallActivity])
        newIntent.putExtras(bundle)
        activity.overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out)
        activity.startActivity(newIntent)
      }
    })
    mMessage.setOnClickListener(new View.OnClickListener() {
      def onClick(v: View) {
        bundle = new Bundle
        bundle.putString("messageTitle", friend.userName)
        bundle.putInt("contactColorPrimary", friend.color)
        bundle.putInt("contactColorStatus", friend.secondColor)
        bundle.putInt("imgResource", friend.photoReference)
        bundle.putInt("messageType", 0)
        val newIntent: Intent = new Intent(activity, classOf[MessageActivity])
        newIntent.putExtras(bundle)
        activity.startActivity(newIntent)
      }
    })
    mSaveProfile.setOnClickListener(new View.OnClickListener() {
      def onClick(v: View) {
        val snack: Snackbar = Snackbar.make(mCoordinator, getResources.getString(R.string.contact_save_photo_snackbar), Snackbar.LENGTH_LONG)
        val snackView: View = snack.getView
        snackView.setBackgroundResource(R.color.snackBarColor)
        val snackText: TextView = snackView.findView(TR.snackbar_text)
        snackText.setTextColor(getResources.getColor(R.color.textDarkColor, null))
        snack.show()
      }
    })
    mFilesSend.setOnClickListener(new View.OnClickListener() {
      def onClick(v: View) {
        bundle = new Bundle
        bundle.putString("contactName", friend.userName)
        bundle.putInt("contactColorPrimary", friend.color)
        bundle.putInt("contactColorStatus", friend.secondColor)
        val newIntent: Intent = new Intent(activity, classOf[FileSendActivity])
        newIntent.putExtras(bundle)
        activity.startActivity(newIntent)
      }
    })
    mDeleteFriend.setOnClickListener(new View.OnClickListener() {
      def onClick(v: View) {
        val dial = new SimpleDialogDesign(activity, getResources.getString(R.string.dialog_delete_friend) + " " + friend.userName + " " + getResources.getString(R.string.dialog_delete_friend_end), friend.color, R.drawable.ic_person_black_48dp, null)
        dial.show()
      }
    })
    mBlockFriend.setOnClickListener(new View.OnClickListener() {
      def onClick(v: View) {
        val snack = Snackbar.make(mCoordinator, getResources.getString(R.string.contact_blocked), Snackbar.LENGTH_LONG)
        val snackView = snack.getView
        snackView.setBackgroundResource(R.color.snackBarColor)
        val snackText = snackView.findView(TR.snackbar_text)
        snackText.setTextColor(getResources.getColor(R.color.textDarkColor, null))
        snack.show()
      }
    })
    mChangeColor.setOnClickListener(new View.OnClickListener() {
      def onClick(v: View) {
        val dial = new SimpleColorDialogDesign(activity, getResources.getString(R.string.dialog_change_color) + " " + friend.userName + " " + getResources.getString(R.string.dialog_change_color_end), friend.color, R.drawable.ic_image_color_lens, 0, null)
        dial.show()
      }
    })
  }

  def getStatusBarHeight: Int = {
    val resourceId = getResources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
      getResources.getDimensionPixelSize(resourceId)
    } else {
      0
    }
  }
}
