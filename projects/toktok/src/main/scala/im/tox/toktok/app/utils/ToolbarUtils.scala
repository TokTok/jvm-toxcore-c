package im.tox.toktok.app.utils

import android.content.res.Resources

object ToolbarUtils {

  def setToolbarSize(resources: Resources): Int = {

    var result = 0
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")

    if (resourceId > 0) {
      result = resources.getDimensionPixelSize(resourceId)
    }

    result

  }

}