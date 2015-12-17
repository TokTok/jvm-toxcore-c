package im.tox.tox4j.av.callbacks.video

import im.tox.tox4j.av.data.{Height, Width}
import org.scalatest.Assertions

object VideoGenerators extends Assertions {

  val DefaultWidth = Width.fromInt(400).get
  val DefaultHeight = Height.fromInt(400).get

  // TODO(iphydf): Several of these break with the following error in
  // libtoxcore.log, especially at higher resolutions:
  //
  // toxcore  12:13 04:53:51  3343345408   ERROR  video.c:155          - Error decoding video: Unspecified internal error

  /**
   * Shifting colours in xor pattern.
   */
  final case class Xor1(width: Width = DefaultWidth, height: Height = DefaultHeight, length: Int = 100) extends ArithmeticVideoGenerator {
    def resize(width: Width, height: Height): VideoGenerator = copy(width = width, height = height)

    protected def y(t: Int, x: Int, y: Int): Int = x ^ y
    protected def u(t: Int, x: Int, y: Int): Int = x ^ y + t + 1
    protected def v(t: Int, x: Int, y: Int): Int = x ^ y - t - 1
  }

  /**
   * Rapidly changing xor patterns.
   */
  final case class Xor2(width: Width, height: Height, length: Int = 100) extends RgbVideoGenerator {
    def resize(width: Width, height: Height): VideoGenerator = copy(width = width, height = height)

    def rgb(t: Int, y: Int, x: Int): Int = (x ^ y) * t
  }

  /**
   * Slowly right-shifting and colour-shifting xor.
   */
  final case class Xor3(width: Width, height: Height, length: Int = 100) extends RgbVideoGenerator {
    def resize(width: Width, height: Height): VideoGenerator = copy(width = width, height = height)

    def rgb(t: Int, y: Int, x: Int): Int = (x - (t * Math.log(t)).toInt ^ y + (t * Math.log(t)).toInt) * t
  }

  /**
   * Slowly colour-shifting xor patterns.
   */
  final case class Xor4(width: Width, height: Height, length: Int = 100) extends ArithmeticVideoGenerator {
    def resize(width: Width, height: Height): VideoGenerator = copy(width = width, height = height)

    protected def y(t: Int, x: Int, y: Int): Int = (x ^ y) + t
    protected def u(t: Int, x: Int, y: Int): Int = t * 2
    protected def v(t: Int, x: Int, y: Int): Int = -t * 2 - 1
  }

  final case class Xor5(width: Width, height: Height, length: Int = 100) extends ArithmeticVideoGenerator {
    def resize(width: Width, height: Height): VideoGenerator = copy(width = width, height = height)

    protected def y(t: Int, x: Int, y: Int): Int = t + x ^ y
    protected def u(t: Int, x: Int, y: Int): Int = x ^ y + t + 1
    protected def v(t: Int, x: Int, y: Int): Int = x ^ y - t - 1
  }

  private def gradient(top: Boolean, left: Boolean, x: Int, y: Int, w: Int, h: Int): Double = {
    val value =
      if (top && left) {
        (x * 512 / w) min (y * 512 / h)
      } else if (top && !left) {
        ((w - x) * 512 / w) min (y * 512 / h)
      } else if (!top && left) {
        (x * 512 / w) min ((h - y) * 512 / h)
      } else {
        ((w - x) * 512 / w) min ((h - y) * 512 / h)
      }
    assert(value >= 0)
    assert(value <= 256)
    value.toDouble / 256
  }

  final case class XorGradient(width: Width, height: Height, length: Int = 100) extends ArithmeticVideoGenerator {
    def resize(width: Width, height: Height): VideoGenerator = copy(width = width, height = height)

    protected def y(t: Int, x: Int, y: Int): Int = {
      val top = y <= h / 2
      val left = x <= w / 2

      (gradient(top, left, x, y, w, h) * (x ^ y) + t).toInt
    }

    protected def u(t: Int, x: Int, y: Int): Int = t * 2
    protected def v(t: Int, x: Int, y: Int): Int = -t * 2 - 1
  }

  /**
   * More and more gradient boxes.
   */
  final case class GradientBoxes(width: Width, height: Height, length: Int = 100) extends RgbVideoGenerator {
    def resize(width: Width, height: Height): VideoGenerator = copy(width = width, height = height)

    def rgb(t: Int, y: Int, x: Int): Int = (x * Math.log(t) + ((y * Math.log(t)).toInt << 8)).toInt
  }

  /**
   * Multiplication (x * y) pattern moving up.
   */
  final case class MultiplyUp(width: Width, height: Height, length: Int = 100) extends RgbVideoGenerator {
    def resize(width: Width, height: Height): VideoGenerator = copy(width = width, height = height)

    def rgb(t: Int, y: Int, x: Int): Int = x * (y + t)
  }

  case object Smiley extends TextImageGenerator(
    "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "00000000000011zzzzzzzzzzzzzzzzz000000000000000000000000000000000000zzzzzzzzzzzzzzzzzzz00000000000000",
    "00000000000011zzzzzzzzzzzzzzzzz000000000000000000000000000000000000zzzzzzzzzzzzzzzzzzz00000000000000",
    "00000000000011zzzzzzzzzzzzzzzzz000000000000000000000000000000000000zzzzzzzzzzzzzzzzzzz00000000000000",
    "00000000000011zzzzzzzzzzzzzzzzz000000000000000000000000000000000000zzzzzzzzzzzzzzzzzzz00000000000000",
    "00000000000011zzzzzzzzzzzzzzzzz000000000000000000000000000000000000zzzzzzzzzzzzzzzzzzz00000000000000",
    "00000000000011zzzzzzzzzzzzzzzzz000000000000000000000000000000000000zzzzzzzzzzzzzzzzzzz00000000000000",
    "00000000000011zzzzzzzzzzzzzzzzz000000000000000000000000000000000000zzzzzzzzzzzzzzzzzzz00000000000000",
    "00000000000011zzzzzzzzzzzzzzzzz000000000000000000000000000000000000zzzzzzzzzzzzzzzzzzz00000000000000",
    "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "000000000000000000000000000000000000000011zzzzzzzzzzzz0000000000000000000000000000000000000000000000",
    "000000000000000000000000000000000000000011zzzzzzzzzzzz0000000000000000000000000000000000000000000000",
    "000000000000000000000000000000000000000011zzzzzzzzzzzz0000000000000000000000000000000000000000000000",
    "000000000000000000000000000000000000000011zzzzzzzzzzzz0000000000000000000000000000000000000000000000",
    "000000000000000000000000000000000000000011zzzzzzzzzzzz0000000000000000000000000000000000000000000000",
    "000000000000000000000000000000000000000011zzzzzzzzzzzz0000000000000000000000000000000000000000000000",
    "000000000000000000000000000000000000000011zzzzzzzzzzzz0000000000000000000000000000000000000000000000",
    "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "00000000000000001zzzzzzzz000000000000000000000000000000000000000000000000zzzzzzzzz000000000000000000",
    "000000000000000011zzzzzzzzzz000000000000000000000000000000000000000011zzzzzzzzzz00000000000000000000",
    "000000000000000000001zzzzzzzzzz00000000000000000000000000000000111zzzzzzzzzz000000000000000000000000",
    "000000000000000000000000zzzzzzzzzzzz00000000000000000000000011zzzzzzzzzzzz00000000000000000000000000",
    "0000000000000000000000000000zzzzzzzzzzzzzzzz000000001zzzzzzzzzzzzzzzzz000000000000000000000000000000",
    "00000000000000000000000000000000zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz0000000000000000000000000000000000",
    "000000000000000000000000000000000000111zzzzzzzzzzzzzzzzzzzz00000000000000000000000000000000000000000",
    "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"
  )

  val all = Map[String, (Width, Height, Int) => VideoGenerator](
    "xor1" -> Xor1,
    "xor2" -> Xor2,
    "xor3" -> Xor3,
    "xor4" -> Xor4,
    "xor5" -> Xor5,
    "xorgradient" -> XorGradient,
    "gradientboxes" -> GradientBoxes,
    "multiplyup" -> MultiplyUp,
    "smiley" -> { (w, h, _) => VideoGenerator.resizeNearestNeighbour(w, h, Smiley) }
  )

  val default = VideoGenerator.resizeNearestNeighbour(DefaultWidth, DefaultHeight, Smiley)
  // val default = Xor4(DefaultWidth, DefaultHeight)

}
