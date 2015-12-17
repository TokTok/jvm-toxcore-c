package im.tox.tox4j.av.callbacks.video

import im.tox.tox4j.av.data.{Height, Width}
import org.scalatest.Assertions

abstract class VideoGenerator {

  def width: Width
  def height: Height
  def length: Int

  def yuv(t: Int): (Array[Byte], Array[Byte], Array[Byte])
  def resize(width: Width, height: Height): VideoGenerator

  final def size: Int = width.value * height.value

}

object VideoGenerator extends Assertions {

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

  def resizeNearestNeighbour(newWidth: Width, newHeight: Height, gen: VideoGenerator): VideoGenerator = {
    if (newWidth == gen.width && newHeight == gen.height) {
      gen
    } else {
      new VideoGenerator {

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

        override def width: Width = newWidth
        override def height: Height = newHeight
        override def length: Int = gen.length

      }
    }
  }

}

