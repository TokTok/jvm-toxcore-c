package im.tox.tox4j.av.callbacks.video

import java.io.PrintStream

import scala.util.{Success, Try}

final case class ConsoleVideoDisplay(width: Int, height: Int) extends VideoDisplay[Seq[String], PrintStream] {

  override protected def canvas: Try[PrintStream] = Success(System.out)

  override protected def displaySent(canvas: PrintStream, frameNumber: Int, senderImage: Seq[String]): Unit = {
    // Don't display the sent image in text mode.
  }

  override protected def displayReceived(canvas: PrintStream, frameNumber: Int, receiverImage: Seq[String]): Unit = {
    canvas.print("\u001b[H\u001b[2J")
    receiverImage.foreach(canvas.println)
  }

  override protected def parse(
    y: Array[Byte], u: Array[Byte], v: Array[Byte],
    yStride: Int, uStride: Int, vStride: Int
  ): Seq[String] = {
    val printable = ".-~:;/<>=()ot%!?@&O8SX$#"

    for (yPos <- 0 until height) yield {
      new String(y.slice(yPos * yStride, yPos * yStride + width).map {
        case b =>
          printable(((b & 0xff) / 255.0 * (printable.length - 1)).toInt)
      })
    }
  }

  override def close(): Unit = ()

}
