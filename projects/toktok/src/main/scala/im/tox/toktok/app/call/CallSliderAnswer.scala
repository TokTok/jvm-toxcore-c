package im.tox.toktok.app.call

import android.content.Context
import android.util.AttributeSet
import android.view.{LayoutInflater, MotionEvent, View}
import android.widget.{ImageView, RelativeLayout, TextView}
import im.tox.toktok.TypedResource._
import im.tox.toktok.{R, TR}

final class CallSliderAnswer(
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
    inflater.inflate(TR.layout.call_slider_answer, this, true)
    mCallImage = findViewById(R.id.call_slider_img).asInstanceOf[ImageView]
    if (mCallImage != null) {
      mCallImage.setOnTouchListener(this)
    }
    mCallText = findViewById(R.id.call_slider_text).asInstanceOf[TextView]
  }

  def onTouch(v: View, motion: MotionEvent): Boolean = {
    val x = motion.getRawX.toInt
    val y = motion.getRawY.toInt
    val buttonPayoutParams = mCallImage.getLayoutParams.asInstanceOf[RelativeLayout.LayoutParams]

    motion.getAction match {
      case MotionEvent.ACTION_DOWN =>
        _X = x
        buttonWidth = mCallImage.getWidth
        barWidth = getWidth - buttonWidth - getPaddingRight - getPaddingLeft
        mCallImage.setImageResource(R.drawable.call_answer_hold)
        startPosition = mCallImage.getX.toInt
        listener.onStart()

      case MotionEvent.ACTION_MOVE =>
        if ((x - _X) >= 0 && (x - _X) <= barWidth) {
          buttonPayoutParams.leftMargin = x - _X
          mCallText.setAlpha((x - _X).toFloat / (barWidth * 1.3f))
          answered = false
        } else if ((x - _X) < 0) {
          buttonPayoutParams.leftMargin = 0
          mCallText.setAlpha(0)
        } else if ((x - _X) >= barWidth) {
          buttonPayoutParams.leftMargin = barWidth
          answered = true
        }

      case MotionEvent.ACTION_UP =>
        if (answered) {
          listener.onCompleted()
        } else {
          listener.onReleased()
          mCallImage.setImageResource(R.drawable.call_answer_button)
          buttonPayoutParams.leftMargin = 0
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
