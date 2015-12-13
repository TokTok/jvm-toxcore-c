package im.tox.tox4j.av.callbacks.video

abstract class RgbVideoDisplay[Parsed, Canvas] extends VideoDisplay[Parsed, Canvas] {

  protected def parse(r: Array[Byte], g: Array[Byte], b: Array[Byte]): Parsed

  override protected final def parse(
    y: Array[Byte], u: Array[Byte], v: Array[Byte],
    yStride: Int, uStride: Int, vStride: Int
  ): Parsed = {
    val (r, g, b) = VideoConversions.YUVtoRGB(width, height, y, u, v, yStride, uStride, vStride)
    assert(r.length == width * height)
    assert(g.length == width * height)
    assert(b.length == width * height)

    parse(r, g, b)
  }

}
