package im.tox.tox4j.av.callbacks.video

abstract class RgbVideoGenerator extends ArithmeticVideoGenerator {
  def rgb(t: Int, y: Int, x: Int): Int
  protected final def y(t: Int, x: Int, y: Int): Int = VideoConversions.RGBtoY(rgb(t, y, x))
  protected final def u(t: Int, x: Int, y: Int): Int = VideoConversions.RGBtoY(rgb(t, y, x))
  protected final def v(t: Int, x: Int, y: Int): Int = VideoConversions.RGBtoY(rgb(t, y, x))
}
