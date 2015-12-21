package im.tox.toktok.app.call

import android.content.Context
import android.util.AttributeSet
import android.view.{LayoutInflater, MotionEvent, View}
import android.widget.{ImageView, RelativeLayout, TextView}
import im.tox.toktok.TypedResource._
import im.tox.toktok.{R, TR}

final class CallSliderDecline(
    context: Context,
    attrs: AttributeSet,
    defStyle: Int
) extends RelativeLayout(context, attrs, defStyle) with View.OnTouchListener {

  private var mContext: Context = null
  private var mCallImage: ImageView = null
  private var _X: Int = 0
  private var buttonWidth: Int = 0
  private var barWidth: Int = 0
  private var answered: Boolean = false
  private var mCallText: TextView = null
  private var startPosition: Int = 0
  private var listener: CallListener = null

  def this(context: Context, attrs: AttributeSet) { this(context, attrs, 0) }
  def this(context: Context) { this(context, null) }

  {
    val inflater = LayoutInflater.from(getContext)
    inflater.inflate(TR.layout.call_slider_decline, this, true)
    mCallImage = findViewById(R.id.call_slider_img).asInstanceOf[ImageView]
    if (mCallImage != null) {
      mCallImage.setOnTouchListener(this)
    }
    mCallText = findViewById(R.id.call_slider_text).asInstanceOf[TextView]
  }

  def onTouch(v: View, motion: MotionEvent): Boolean = {
    val x: Int = motion.getRawX.toInt
    val y: Int = motion.getRawY.toInt
    val buttonPayoutParams: RelativeLayout.LayoutParams = mCallImage.getLayoutParams.asInstanceOf[RelativeLayout.LayoutParams]
    motion.getAction match {
      case MotionEvent.ACTION_DOWN =>
        _X = x
        buttonWidth = mCallImage.getWidth
        barWidth = getWidth - buttonWidth - getPaddingRight - getPaddingLeft
        mCallImage.setImageResource(R.drawable.call_decline_hold)
        startPosition = mCallImage.getX.toInt
        listener.onStart()
      case MotionEvent.ACTION_MOVE =>
        if ((_X - x) >= 0 && (_X - x) <= barWidth) {
          buttonPayoutParams.rightMargin = _X - x
          mCallText.setAlpha((_X - x).toFloat / (barWidth * 1.3f))
          answered = false
        } else if ((_X - x) >= barWidth) {
          buttonPayoutParams.rightMargin = barWidth
          answered = true
        } else if ((_X - x) <= 0) {
          buttonPayoutParams.rightMargin = 0
          mCallText.setAlpha(0)
        }
      case MotionEvent.ACTION_UP =>
        if (answered) {
          listener.onCompleted()
        } else {
          listener.onReleased()
          mCallImage.setImageResource(R.drawable.call_decline_button)
          buttonPayoutParams.rightMargin = 0
          mCallText.setAlpha(0)
        }
    }
    mCallImage.setLayoutParams(buttonPayoutParams)
    true
  }

  def setOnCallListener(listener: CallListener) {
    this.listener = listener
  }

}
