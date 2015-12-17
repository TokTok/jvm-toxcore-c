package im.tox.tox4j.av.callbacks.video

abstract class RgbVideoGenerator extends ArithmeticVideoGenerator {
  def rgb(t: Int, y: Int, x: Int): Int
  final def y(t: Int, y: Int, x: Int): Byte = VideoConversions.RGBtoY(rgb(t, y, x))
  final def u(t: Int, y: Int, x: Int): Byte = VideoConversions.RGBtoY(rgb(t, y, x))
  final def v(t: Int, y: Int, x: Int): Byte = VideoConversions.RGBtoY(rgb(t, y, x))
}
