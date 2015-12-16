package im.tox.tox4j.av.callbacks.video

import java.io.Closeable

import im.tox.tox4j.av.data.{Height, Width}
import im.tox.tox4j.testing.autotest.AutoTestSuite.timed
import org.scalatest.Assertions

import scala.util.Try

abstract class VideoDisplay[Parsed, Canvas] extends Assertions with Closeable {

  def width: Width
  def height: Height

  protected def canvas: Try[Canvas]
  protected def parse(
    y: Array[Byte], u: Array[Byte], v: Array[Byte],
    yStride: Int, uStride: Int, vStride: Int
  ): Parsed
  protected def displaySent(canvas: Canvas, frameNumber: Int, parsed: Parsed): Unit
  protected def displayReceived(canvas: Canvas, frameNumber: Int, parsed: Parsed): Unit

  final def displaySent(frameNumber: Int, y: Array[Byte], u: Array[Byte], v: Array[Byte]): Unit = {
    val width = this.width.value
    canvas.foreach(displaySent(_, frameNumber, parse(y, u, v, width, width / 2, width / 2)))
  }

  /**
   * @return (parseTime, displayTime)
   */
  final def displayReceived(
    frameNumber: Int,
    y: Array[Byte], u: Array[Byte], v: Array[Byte],
    yStride: Int, uStride: Int, vStride: Int
  ): Option[(Int, Int)] = {
    canvas.toOption.map { canvas =>
      val (parseTime, parsed) = timed(parse(y, u, v, yStride, uStride, vStride))
      val displayTime = timed(displayReceived(canvas, frameNumber, parsed))

      (parseTime, displayTime)
    }
  }

}
