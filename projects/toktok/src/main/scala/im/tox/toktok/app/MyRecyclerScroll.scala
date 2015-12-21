package im.tox.toktok.app

import android.support.v7.widget.RecyclerView

object MyRecyclerScroll {
  private val HIDE_THRESHOLD = 100
  private val SHOW_THRESHOLD = 50
}

abstract class MyRecyclerScroll extends RecyclerView.OnScrollListener {

  private var scrollDist = 0
  private var isVisible = true

  override def onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
    super.onScrolled(recyclerView, dx, dy)
    if (isVisible && scrollDist > MyRecyclerScroll.HIDE_THRESHOLD) {
      hide()
      scrollDist = 0
      isVisible = false
    } else if (!isVisible && scrollDist < -MyRecyclerScroll.SHOW_THRESHOLD) {
      show()
      scrollDist = 0
      isVisible = true
    }
    if ((isVisible && dy > 0) || (!isVisible && dy < 0)) {
      scrollDist += dy
    }
  }

  def show(): Unit
  def hide(): Unit

}
