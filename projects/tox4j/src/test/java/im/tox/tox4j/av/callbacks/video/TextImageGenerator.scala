package im.tox.tox4j.av.callbacks.video

import im.tox.tox4j.av.data.{Height, Width}

class TextImageGenerator(rows: String*) extends VideoGenerator {

  override final def yuv(t: Int): (Array[Byte], Array[Byte], Array[Byte]) = {
    val width = this.width.value
    val height = this.height.value

    val y = rows.mkString.getBytes
    val u = Array.fill((width / 2) * (height / 2))((t * 4).toByte)
    val v = Array.fill((width / 2) * (height / 2))((-t * 4 - 1).toByte)
    (y, u, v)
  }

  override final def resize(width: Width, height: Height): VideoGenerator = {
    VideoGenerator.resizeNearestNeighbour(width, height, this)
  }

  override def width: Width = Width.fromInt(rows.head.length).get
  override def height: Height = Height.fromInt(rows.size).get
  override def length: Int = 64

}
