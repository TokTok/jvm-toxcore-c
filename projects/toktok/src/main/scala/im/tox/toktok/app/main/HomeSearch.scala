package im.tox.toktok.app.main

import android.content.Context
import android.graphics.drawable.TransitionDrawable
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.View.MeasureSpec
import android.view.animation.{Animation, AnimationUtils}
import android.view.{View, ViewGroup}
import android.widget.{EditText, LinearLayout}
import im.tox.toktok.TypedResource._
import im.tox.toktok.{R, TR}

final class HomeSearch(
    context: Context,
    attrs: AttributeSet,
    defStyle: Int
) extends ViewGroup(context, attrs, defStyle) {

  private var background: TransitionDrawable = null
  private var mBase: LinearLayout = null
  private var mInput: EditText = null
  private var mCardView: CardView = null
  private var mRecycler: NestedScrollView = null

  def this(context: Context, attrs: AttributeSet) { this(context, attrs, 0) }
  def this(context: Context) { this(context, null) }

  protected override def onFinishInflate() {
    background = getBackground.asInstanceOf[TransitionDrawable]
    background.startTransition(500)
    mBase = this.findView(TR.home_search_layout)
    mCardView = this.findView(TR.home_search_bar)
    mRecycler = this.findView(TR.home_search_bar_recycler)
    val searchBarAnimation = AnimationUtils.loadAnimation(mCardView.getContext, R.anim.abc_fade_in)
    mCardView.startAnimation(searchBarAnimation)
    mInput = this.findView(TR.home_search_input)
    super.onFinishInflate()
  }

  protected override def onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    mBase.layout(0, getStatusBarHeight, getWidth, getHeight)
  }

  private def getStatusBarHeight: Int = {
    val resourceId = getResources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
      getResources.getDimensionPixelSize(resourceId)
    } else {
      0
    }
  }

  protected override def onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    measureChildren(widthMeasureSpec, heightMeasureSpec)
    val maxWidth: Int = MeasureSpec.getSize(widthMeasureSpec)
    val maxHeight: Int = MeasureSpec.getSize(heightMeasureSpec)
    setMeasuredDimension(View.resolveSizeAndState(maxWidth, widthMeasureSpec, 0), View.resolveSizeAndState(maxHeight, heightMeasureSpec, 0))
  }

  def finish() {
    if (mInput.isFocusable) {
      mInput.clearFocus()
    }
    val searchBarAnimation: Animation = AnimationUtils.loadAnimation(mCardView.getContext, R.anim.abc_fade_out)
    searchBarAnimation.setAnimationListener(new Animation.AnimationListener() {
      def onAnimationStart(animation: Animation) {
      }

      def onAnimationEnd(animation: Animation) {
        mCardView.setVisibility(View.INVISIBLE)
        background.reverseTransition(500)
      }

      def onAnimationRepeat(animation: Animation) {
      }
    })
    mCardView.startAnimation(searchBarAnimation)
  }

}
