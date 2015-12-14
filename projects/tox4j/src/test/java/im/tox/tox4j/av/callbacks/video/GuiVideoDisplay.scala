package im.tox.tox4j.av.callbacks.video

import java.awt.image.{BufferedImage, DataBufferByte}
import java.awt.{Color, GridBagConstraints, GridBagLayout}
import java.io.File
import javax.imageio.ImageIO
import javax.swing.{ImageIcon, JDialog, JFrame, JLabel}

import com.typesafe.scalalogging.Logger
import im.tox.tox4j.av.callbacks.video.GuiVideoDisplay.{UI, newImage}
import org.slf4j.LoggerFactory

import scala.util.Try

object GuiVideoDisplay {

  private val capturePath = Some(new File("capture/video")).filter(_.isDirectory)
  capturePath.foreach(_.listFiles.foreach(_.delete()))

  private def newImage(width: Int, height: Int): BufferedImage = {
    new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR)
  }

  final class UI(width: Int, height: Int) {

    val senderImageView, receiverImageView = new JLabel(new ImageIcon(newImage(width, height)))

    val dialog = new JDialog(new JFrame)
    dialog.setSize(width * 2 + 20, height + 40)
    dialog.setLayout(new GridBagLayout)

    private val constraints = new GridBagConstraints

    constraints.ipadx = 0
    constraints.gridx = 0
    constraints.gridy = 0
    val senderLabel = new JLabel("Sent")
    dialog.add(senderLabel, constraints)

    constraints.ipadx = 10
    constraints.gridx = 0
    constraints.gridy = 1
    dialog.add(senderImageView, constraints)

    constraints.ipadx = 0
    constraints.gridx = 1
    constraints.gridy = 0
    val receiverLabel = new JLabel("Received")
    dialog.add(receiverLabel, constraints)

    constraints.ipadx = 10
    constraints.gridx = 1
    constraints.gridy = 1
    dialog.add(receiverImageView, constraints)

    def screenshot(frameNumber: Int): Unit = {
      capturePath.foreach { capturePath =>
        val image = new BufferedImage(
          dialog.getWidth,
          dialog.getHeight,
          BufferedImage.TYPE_INT_RGB
        )

        dialog.paint(image.getGraphics)

        ImageIO.write(image, "jpg", new File(capturePath, f"$frameNumber%03d.jpg"))
      }
    }

  }

}

final case class GuiVideoDisplay(width: Int, height: Int) extends RgbVideoDisplay[ImageIcon, UI] {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  override protected val canvas: Try[UI] = Try(new GuiVideoDisplay.UI(width, height))

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
    if (!canvas.dialog.isVisible) {
      canvas.dialog.setVisible(true)
    }
    canvas.senderLabel.setText(s"Sent frame #$frameNumber")
    canvas.senderImageView.setIcon(senderImage)
  }

  override protected def displayReceived(canvas: UI, frameNumber: Int, receiverImage: ImageIcon): Unit = {
    if (!canvas.dialog.isVisible) {
      canvas.dialog.setVisible(true)
    }
    canvas.receiverLabel.setText(s"Received frame #$frameNumber")
    canvas.receiverImageView.setIcon(receiverImage)

    val FrameNumber = "Sent frame #(\\d+)".r
    canvas.senderLabel.getText match {
      case FrameNumber(number) =>
        if (number.toInt != frameNumber) {
          canvas.receiverLabel.setForeground(Color.RED)
          canvas.receiverLabel.setText(canvas.receiverLabel.getText + s" (${number.toInt - frameNumber} behind)")
        }
    }

    canvas.screenshot(frameNumber)
  }

}
