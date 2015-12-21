package im.tox.toktok.app.message_activity

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar.SnackbarLayout
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout

class MessageInputBarBehavior(context: Context, attrs: AttributeSet) extends CoordinatorLayout.Behavior[RelativeLayout] {

  override def layoutDependsOn(parent: CoordinatorLayout, child: RelativeLayout, dependency: View): Boolean = {
    dependency.isInstanceOf[SnackbarLayout]
  }

  override def onDependentViewChanged(parent: CoordinatorLayout, child: RelativeLayout, dependency: View): Boolean = {
    val translationY = Math.min(0, dependency.getTranslationY - dependency.getHeight)
    child.setTranslationY(translationY)
    true
  }

}
