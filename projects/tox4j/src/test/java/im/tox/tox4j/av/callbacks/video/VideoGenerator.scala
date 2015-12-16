package im.tox.tox4j.av.callbacks.video

import im.tox.tox4j.av.callbacks.video.VideoConversions.YuvPixel
import im.tox.tox4j.av.data.{Width, Height}
import org.scalatest.Assertions

abstract class VideoGenerator(val width: Width, val height: Height, val length: Int) {
  def size: Int = width.value * height.value
  def yuv(t: Int): (Array[Byte], Array[Byte], Array[Byte])
  def resize(width: Width, height: Height): VideoGenerator
}

object VideoGenerator extends Assertions {

  abstract class Arithmetic(width: Width, height: Height, length: Int) extends VideoGenerator(width, height, length) {

    def productPrefix: String

    override final def toString: String = {
      s"$productPrefix($width, $height)"
    }

    protected def yuv(t: Int, y: Int, x: Int): YuvPixel

    override final def yuv(t: Int): (Array[Byte], Array[Byte], Array[Byte]) = {
      val width = this.width.value
      val height = this.height.value

      val y = Array.ofDim[Byte](width * height)
      val u = Array.ofDim[Byte](width * height / 4)
      val v = Array.ofDim[Byte](width * height / 4)

      for {
        yPos <- 0 until height
        xPos <- 0 until width
      } {
        val pixel = yuv(t, yPos, xPos)
        y(yPos * width + xPos) = pixel.y
        u((yPos / 2) * (width / 2) + xPos / 2) = pixel.u
        v((yPos / 2) * (width / 2) + xPos / 2) = pixel.v
      }

      (y, u, v)
    }

  }

  private def resizeNearestNeighbour(
    pixels: Array[Byte],
    oldWidth: Int,
    oldHeight: Int,
    newWidth: Int,
    newHeight: Int
  ): Array[Byte] = {
    val result = Array.ofDim[Byte](newWidth * newHeight)

    val xRatio = oldWidth / newWidth.toDouble
    val yRatio = oldHeight / newHeight.toDouble

    for {
      yPos <- 0 until newHeight
      xPos <- 0 until newWidth
    } {
      val px = Math.floor(xPos * xRatio)
      val py = Math.floor(yPos * yRatio)
      result((yPos * newWidth) + xPos) = pixels(((py * oldWidth) + px).toInt)
    }

    result
  }

  def resizeNearestNeighbour(width: Width, height: Height, gen: VideoGenerator): VideoGenerator = {
    if (width == gen.width && height == gen.height) {
      gen
    } else {
      new VideoGenerator(width, height, gen.length) {
        override def toString: String = s"resizeNearestNeighbour($width, $height, $gen)"

        override def resize(width: Width, height: Height): VideoGenerator = gen.resize(width, height)

        override def yuv(t: Int): (Array[Byte], Array[Byte], Array[Byte]) = {
          val yuv = gen.yuv(t)
          (
            resizeNearestNeighbour(yuv._1, gen.width.value, gen.height.value, width.value, height.value),
            resizeNearestNeighbour(yuv._2, gen.width.value / 2, gen.height.value / 2, width.value / 2, height.value / 2),
            resizeNearestNeighbour(yuv._3, gen.width.value / 2, gen.height.value / 2, width.value / 2, height.value / 2)
          )
        }
      }
    }
  }

}

