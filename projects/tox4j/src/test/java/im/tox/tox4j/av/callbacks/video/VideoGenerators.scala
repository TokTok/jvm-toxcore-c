package im.tox.tox4j.av.callbacks.video

import im.tox.tox4j.av.data.{Height, Width}

object VideoGenerators {

  private val DefaultWidth = Width.fromInt(400).get
  private val DefaultHeight = Height.fromInt(400).get

  // TODO(iphydf): Several of these break with the following error in
  // libtoxcore.log, especially at higher resolutions:
  //
  // toxcore  12:13 04:53:51  3343345408   ERROR  video.c:155          - Error decoding video: Unspecified internal error

  /**
   * Shifting colours in xor pattern.
   */
  final case class Xor1(width: Width = DefaultWidth, height: Height = DefaultHeight, length: Int = 100) extends ArithmeticVideoGenerator {
    def resize(width: Width, height: Height): VideoGenerator = copy(width = width, height = height)

    def y(t: Int, y: Int, x: Int): Byte = (x ^ y).toByte
    def u(t: Int, y: Int, x: Int): Byte = (x ^ y + t + 1).toByte
    def v(t: Int, y: Int, x: Int): Byte = (x ^ y - t - 1).toByte
  }

  /**
   * Rapidly changing xor patterns.
   */
  final case class Xor2(width: Width = DefaultWidth, height: Height = DefaultHeight, length: Int = 100) extends RgbVideoGenerator {
    def resize(width: Width, height: Height): VideoGenerator = copy(width = width, height = height)

    def rgb(t: Int, y: Int, x: Int): Int = (x ^ y) * t
  }

  /**
   * Slowly right-shifting and colour-shifting xor.
   */
  final case class Xor3(width: Width = DefaultWidth, height: Height = DefaultHeight, length: Int = 100) extends RgbVideoGenerator {
    def resize(width: Width, height: Height): VideoGenerator = copy(width = width, height = height)

    def rgb(t: Int, y: Int, x: Int): Int = (x - (t * Math.log(t)).toInt ^ y + (t * Math.log(t)).toInt) * t
  }

  /**
   * Slowly colour-shifting xor patterns.
   */
  final case class Xor4(width: Width = DefaultWidth, height: Height = DefaultHeight, length: Int = 100) extends ArithmeticVideoGenerator {
    def resize(width: Width, height: Height): VideoGenerator = copy(width = width, height = height)

    def y(t: Int, y: Int, x: Int): Byte = ((x ^ y) + t).toByte
    def u(t: Int, y: Int, x: Int): Byte = (t * 2).toByte
    def v(t: Int, y: Int, x: Int): Byte = (-t * 2 - 1).toByte
  }

  final case class Xor5(width: Width = DefaultWidth, height: Height = DefaultHeight, length: Int = 100) extends ArithmeticVideoGenerator {
    def resize(width: Width, height: Height): VideoGenerator = copy(width = width, height = height)

    def y(t: Int, y: Int, x: Int): Byte = (t + x ^ y).toByte
    def u(t: Int, y: Int, x: Int): Byte = (x ^ y + t + 1).toByte
    def v(t: Int, y: Int, x: Int): Byte = (x ^ y - t - 1).toByte
  }

  /**
   * More and more gradient boxes.
   */
  final case class GradientBoxes(width: Width = DefaultWidth, height: Height = DefaultHeight, length: Int = 100) extends RgbVideoGenerator {
    def resize(width: Width, height: Height): VideoGenerator = copy(width = width, height = height)

    def rgb(t: Int, y: Int, x: Int): Int = (x * Math.log(t) + ((y * Math.log(t)).toInt << 8)).toInt
  }

  /**
   * Multiplication (x * y) pattern moving up.
   */
  final case class MultiplyUp(width: Width = DefaultWidth, height: Height = DefaultHeight, length: Int = 100) extends RgbVideoGenerator {
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

  val Selected = VideoGenerator.resizeNearestNeighbour(DefaultWidth, DefaultHeight, Smiley)
  // val Selected = Xor5

}
