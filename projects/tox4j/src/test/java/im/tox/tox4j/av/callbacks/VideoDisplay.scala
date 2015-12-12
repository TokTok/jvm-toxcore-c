package im.tox.tox4j.av.callbacks

import java.awt.image.{BufferedImage, DataBufferByte}
import java.io.PrintStream
import javax.swing.{ImageIcon, JDialog, JFrame, JLabel}

import com.typesafe.scalalogging.Logger
import org.scalatest.Assertions
import org.slf4j.LoggerFactory

import scala.util.{Success, Try}

sealed abstract class VideoDisplay[Parsed, Canvas] extends Assertions {

  def width: Int
  def height: Int

  protected def canvas: Try[Canvas]
  protected def parse(
    y: Array[Byte], u: Array[Byte], v: Array[Byte],
    yStride: Int, uStride: Int, vStride: Int
  ): Parsed
  protected def display(canvas: Canvas, parsed: Parsed): Unit

  final def display(
    y: Array[Byte], u: Array[Byte], v: Array[Byte],
    yStride: Int, uStride: Int, vStride: Int
  ): Unit = {
    canvas.foreach(display(_, parse(y, u, v, yStride, uStride, vStride)))
  }

}

object VideoDisplay {

  sealed abstract class RgbDisplay[Parsed, Canvas] extends VideoDisplay[Parsed, Canvas] {

    private val r: Array[Byte] = Array.ofDim[Byte](width * height)
    private val g: Array[Byte] = Array.ofDim[Byte](width * height)
    private val b: Array[Byte] = Array.ofDim[Byte](width * height)

    protected def parse(r: Array[Byte], g: Array[Byte], b: Array[Byte]): Parsed

    override protected final def parse(
      y: Array[Byte], u: Array[Byte], v: Array[Byte],
      yStride: Int, uStride: Int, vStride: Int
    ): Parsed = {
      VideoConversions.YUVtoRGB(width, height, y, u, v, yStride, uStride, vStride)(r, g, b)
      assert(r.length == width * height)
      assert(g.length == width * height)
      assert(b.length == width * height)

      parse(r, g, b)
    }

  }

  final case class Gui(width: Int, height: Int) extends RgbDisplay[ImageIcon, (JDialog, JLabel)] {

    private val logger = Logger(LoggerFactory.getLogger(getClass))

    private val image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR)

    override protected val canvas: Try[(JDialog, JLabel)] = Try {
      val dialog = new JDialog(new JFrame)
      val label = new JLabel
      dialog.add(label)
      dialog.setSize(width, height)
      (dialog, label)
    }

    override protected def parse(r: Array[Byte], g: Array[Byte], b: Array[Byte]): ImageIcon = {
      val data = image.getRaster.getDataBuffer.asInstanceOf[DataBufferByte].getData
      assert(data.length == r.length + g.length + b.length)
      for (index <- data.indices) {
        index % 3 match {
          case 0 => data(index) = b(index / 3)
          case 1 => data(index) = g(index / 3)
          case 2 => data(index) = r(index / 3)
        }
      }

      new ImageIcon(image)
    }

    override protected def display(canvas: (JDialog, JLabel), icon: ImageIcon): Unit = {
      val (dialog, label) = canvas
      if (!dialog.isVisible) {
        dialog.setVisible(true)
      }
      label.setIcon(icon)
    }

  }

  final case class Console(width: Int, height: Int) extends VideoDisplay[Seq[String], PrintStream] {

    override protected def canvas: Try[PrintStream] = Success(System.out)

    override protected def display(canvas: PrintStream, parsed: Seq[String]): Unit = {
      canvas.print("\u001b[H\u001b[2J")
      parsed.foreach(canvas.println)
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

  }

}
