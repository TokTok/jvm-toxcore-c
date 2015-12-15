package im.tox.tox4j.av.callbacks.video

import java.awt.Color
import java.awt.image.{BufferedImage, DataBufferByte}
import java.io.File
import javax.imageio.ImageIO
import javax.swing.border.EtchedBorder
import javax.swing.{BorderFactory, ImageIcon}

import com.typesafe.scalalogging.Logger
import im.tox.tox4j.av.callbacks.video.GuiVideoDisplay.{UI, newImage}
import org.slf4j.LoggerFactory

import scala.swing._
import scala.util.Try

object GuiVideoDisplay {

  private val capturePath = Some(new File("capture/video")).filter(_.isDirectory)
  capturePath.foreach(_.listFiles.foreach(_.delete()))

  private def newImage(width: Int, height: Int): BufferedImage = {
    new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR)
  }

  final class UI(width: Int, height: Int) {

    val senderImageView, receiverImageView = new Label {
      border = BorderFactory.createEtchedBorder(EtchedBorder.RAISED)
      icon = new ImageIcon(newImage(width, height))
    }

    val senderLabel = new Label("No frames sent yet")
    val receiverLabel = new Label("No frames received yet")

    val dialog = new Dialog(new Frame) {
      contents = new BoxPanel(Orientation.Vertical) {
        contents += new BoxPanel(Orientation.Horizontal) {
          contents += senderImageView
          contents += receiverImageView
        }
        contents += new BoxPanel(Orientation.Horizontal) {
          contents += Swing.HGlue
          contents += senderLabel
          contents += Swing.HGlue
          contents += Swing.HGlue
          contents += receiverLabel
          contents += Swing.HGlue
        }
      }
    }

    dialog.pack()

    /**
     * Align a width or height number to the next multiple of 2.
     * This is required so the dumped screenshots can be turned into a video by ffmpeg.
     */
    private def align(dimension: Int): Int = {
      dimension / 2 * 2 + 2
    }

    def screenshot(frameNumber: Int): Unit = {
      capturePath.foreach { capturePath =>
        val image = new BufferedImage(
          align(dialog.bounds.width),
          align(dialog.bounds.height),
          BufferedImage.TYPE_INT_RGB
        )

        dialog.self.paint(image.getGraphics)

        ImageIO.write(image, "jpg", new File(capturePath, f"$frameNumber%03d.jpg"))
      }
    }

  }

}

final case class GuiVideoDisplay(width: Int, height: Int) extends RgbVideoDisplay[ImageIcon, UI] {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  override protected lazy val canvas: Try[UI] = Try(new GuiVideoDisplay.UI(width, height))

  override protected def parse(r: Array[Byte], g: Array[Byte], b: Array[Byte]): ImageIcon = {
    val image = newImage(width, height)

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

  override protected def displaySent(canvas: UI, frameNumber: Int, senderImage: ImageIcon): Unit = {
    Swing.onEDTWait {
      if (!canvas.dialog.visible) {
        canvas.dialog.visible = true
      }
      canvas.senderLabel.text = s"Sent frame #$frameNumber"
      canvas.senderImageView.icon = senderImage

      canvas.screenshot(frameNumber)
    }
  }

  override protected def displayReceived(canvas: UI, frameNumber: Int, receiverImage: ImageIcon): Unit = {
    Swing.onEDTWait {
      canvas.receiverLabel.text = s"Received frame #$frameNumber"
      canvas.receiverImageView.icon = receiverImage

      val FrameNumber = "Sent frame #(\\d+)".r
      val sentFrameNumber =
        canvas.senderLabel.text match {
          case FrameNumber(number) =>
            if (number.toInt != frameNumber) {
              canvas.receiverLabel.foreground = Color.RED
              canvas.receiverLabel.text += s" (${number.toInt - frameNumber} behind)"
            }
            number.toInt
        }

      // Overwrite the screenshot if this frame was received.
      canvas.screenshot(sentFrameNumber)
    }
  }

  override def close(): Unit = canvas.foreach(_.dialog.close())

}
