package im.tox.toktok.app.message_activity

import android.support.v7.widget.{DefaultItemAnimator, RecyclerView}
import android.view.animation.AnimationUtils
import im.tox.toktok.R

final class MessageItemAnimator extends DefaultItemAnimator() {

  override def animateAdd(holder: RecyclerView.ViewHolder): Boolean = {
    val a = AnimationUtils.loadAnimation(holder.itemView.getContext, R.anim.slide_in_bottom)
    a.setDuration(500)
    holder.itemView.startAnimation(a)
    dispatchAddFinished(holder)
    true
  }

  override def animateRemove(holder: RecyclerView.ViewHolder): Boolean = {
    val a = AnimationUtils.loadAnimation(holder.itemView.getContext, R.anim.abc_fade_out)
    holder.itemView.startAnimation(a)
    dispatchRemoveFinished(holder)
    true
  }

}
