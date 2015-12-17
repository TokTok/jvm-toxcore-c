package im.tox.tox4j.av.callbacks.video

abstract class ArithmeticVideoGenerator extends VideoGenerator {

  def productPrefix: String

  override final def toString: String = s"$productPrefix($width, $height)"

  private final val yArray = Array.ofDim[Byte](size)
  private final val uArray = Array.ofDim[Byte](size / 4)
  private final val vArray = Array.ofDim[Byte](size / 4)

  protected def y(t: Int, x: Int, y: Int): Int
  protected def u(t: Int, x: Int, y: Int): Int
  protected def v(t: Int, x: Int, y: Int): Int

  final def yuv(t: Int): (Array[Byte], Array[Byte], Array[Byte]) = {
    yuv(t, width.value, height.value)
  }

  private def yuv(t: Int, width: Int, height: Int): (Array[Byte], Array[Byte], Array[Byte]) = {
    yLoop(t, yArray, height, width)
    uvLoop(t, uArray, vArray, height / 2, width / 2)

    (yArray, uArray, vArray)
  }

  private def yLoop(t: Int, yArray: Array[Byte], height: Int, width: Int): Unit = {
    var yPos = 0
    while (yPos < height) {
      var xPos = 0
      while (xPos < width) {
        yArray(yPos * width + xPos) = y(t, xPos, yPos).toByte

        xPos += 1
      }

      yPos += 1
    }
  }

  private def uvLoop(t: Int, uArray: Array[Byte], vArray: Array[Byte], height: Int, width: Int): Unit = {
    var yPos = 0
    while (yPos < height) {
      var xPos = 0
      while (xPos < width) {
        uArray(yPos * width + xPos) = u(t, xPos * 2 + 1, yPos * 2 + 1).toByte
        vArray(yPos * width + xPos) = v(t, xPos * 2 + 1, yPos * 2 + 1).toByte

        xPos += 1
      }

      yPos += 1
    }
  }

}
