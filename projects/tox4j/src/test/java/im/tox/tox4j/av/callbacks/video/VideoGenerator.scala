package im.tox.tox4j.av.callbacks.video

import im.tox.tox4j.av.callbacks.video.VideoConversions.YuvPixel
import org.scalatest.Assertions

abstract class VideoGenerator(val width: Int, val height: Int, val length: Int) {
  def yuv(t: Int): (Array[Byte], Array[Byte], Array[Byte])
}

object VideoGenerator extends Assertions {

  abstract class Arithmetic(width: Int, height: Int, length: Int) extends VideoGenerator(width, height, length) {

    def yuv(t: Int, y: Int, x: Int): YuvPixel

    override def yuv(t: Int): (Array[Byte], Array[Byte], Array[Byte]) = {
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

  private def resizeNearestNeighbour(pixels: Array[Byte], oldWidth: Int, oldHeight: Int, newWidth: Int, newHeight: Int): Array[Byte] = {
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

  def scaleNearestNeighbour(wScale: Int, hScale: Int, gen: VideoGenerator): VideoGenerator = {
    if (wScale == 1 && hScale == 1) {
      gen
    } else {
      new VideoGenerator(gen.width * wScale, gen.height * hScale, gen.length) {
        override def toString: String = s"scaleNearestNeighbour($wScale, $hScale, $gen)"

        override def yuv(t: Int): (Array[Byte], Array[Byte], Array[Byte]) = {
          val yuv = gen.yuv(t)
          (
            resizeNearestNeighbour(yuv._1, gen.width, gen.height, width, height),
            resizeNearestNeighbour(yuv._2, gen.width / 2, gen.height / 2, width / 2, height / 2),
            resizeNearestNeighbour(yuv._3, gen.width / 2, gen.height / 2, width / 2, height / 2)
          )
        }
      }
    }
  }

}

