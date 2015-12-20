package im.tox.toktok.app.utils

import android.util.Log

object ColorUtils {

  def mixTwoColors(color1: Int, color2: Int, amount: Float): Int = {
    val ALPHA_CHANNEL = 24
    val RED_CHANNEL = 16
    val GREEN_CHANNEL = 8
    val BLUE_CHANNEL = 0
    val inverseAmount = 1.0f - amount
    val a = (((color1 >> ALPHA_CHANNEL & 0xff).toFloat * amount) + ((color2 >> ALPHA_CHANNEL & 0xff).toFloat * inverseAmount)).toInt & 0xff
    val r = (((color1 >> RED_CHANNEL & 0xff).toFloat * amount) + ((color2 >> RED_CHANNEL & 0xff).toFloat * inverseAmount)).toInt & 0xff
    val g = (((color1 >> GREEN_CHANNEL & 0xff).toFloat * amount) + ((color2 >> GREEN_CHANNEL & 0xff).toFloat * inverseAmount)).toInt & 0xff
    val b = (((color1 & 0xff).toFloat * amount) + ((color2 & 0xff).toFloat * inverseAmount)).toInt & 0xff

    a << ALPHA_CHANNEL | r << RED_CHANNEL | g << GREEN_CHANNEL | b << BLUE_CHANNEL

  }

}
