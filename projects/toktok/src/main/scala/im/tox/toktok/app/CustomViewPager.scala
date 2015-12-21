package im.tox.toktok.app

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

final class CustomViewPager(context: Context, attrs: AttributeSet) extends ViewPager(context, attrs) {

  private var isSwipeEnable: Boolean = true

  override def onTouchEvent(event: MotionEvent): Boolean = {
    this.isSwipeEnable && super.onTouchEvent(event)
  }

  override def onInterceptTouchEvent(event: MotionEvent): Boolean = {
    this.isSwipeEnable && super.onInterceptTouchEvent(event)
  }

  def setSwipingEnabled(b: Boolean): Unit = {
    isSwipeEnable = b
  }

}
