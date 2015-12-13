package im.tox.tox4j.av.callbacks.video

// scalastyle:off method.name
object VideoConversions {

  final case class YuvPixel(y: Byte, u: Byte, v: Byte)

  object YuvPixel {
    def ofRgb(r: Int, g: Int, b: Int): YuvPixel = {
      YuvPixel(
        y = clamp(((66 * r + 129 * g + 25 * b + 128) >> 8) + 16),
        u = clamp(((-38 * r - 74 * g + 112 * b + 128) >> 8) + 128),
        v = clamp(((112 * r - 94 * g - 18 * b + 128) >> 8) + 128)
      )
    }

    def ofRgb(rgb: Int): YuvPixel = {
      ofRgb(
        (rgb >> 16) & 0xff,
        (rgb >> 8) & 0xff,
        rgb & 0xff
      )
    }
  }

  final case class RgbPixel(r: Byte, g: Byte, b: Byte)

  object RgbPixel {

    def ofYuv(y: Int, u: Int, v: Int): RgbPixel = {
      RgbPixel(
        r = YUVtoR(y, u, v),
        g = YUVtoG(y, u, v),
        b = YUVtoB(y, u, v)
      )
    }

  }

  private def clamp(n: Int): Byte = (255 min n max 0).toByte

  private def C(y: Int) = y - 16
  private def D(u: Int) = u - 128
  private def E(v: Int) = v - 128

  private def unsigned(b: Byte): Int = b & 0xff

  private def YUVtoR(y: Int, u: Int, v: Int): Byte = {
    clamp((298 * C(y) + 409 * E(v) + 128) >> 8)
  }

  private def YUVtoG(y: Int, u: Int, v: Int): Byte = {
    clamp((298 * C(y) - 100 * D(u) - 208 * E(v) + 128) >> 8)
  }

  private def YUVtoB(y: Int, u: Int, v: Int): Byte = {
    clamp((298 * C(y) + 516 * D(u) + 128) >> 8)
  }

  def YUVtoRGB( // scalastyle:ignore parameter.number
    width: Int, height: Int,
    y: Array[Byte], u: Array[Byte], v: Array[Byte],
    yStride: Int, uStride: Int, vStride: Int
  ): (Array[Byte], Array[Byte], Array[Byte]) = {
    val r = Array.ofDim[Byte](width * height)
    val g = Array.ofDim[Byte](width * height)
    val b = Array.ofDim[Byte](width * height)

    assert(r.length >= width * height)
    assert(g.length >= width * height)
    assert(b.length >= width * height)

    for {
      yPos <- 0 until height
      xPos <- 0 until width
    } {
      val yx = unsigned(y((yPos * yStride) + xPos))
      val ux = unsigned(u(((yPos / 2) * uStride) + (xPos / 2)))
      val vx = unsigned(v(((yPos / 2) * vStride) + (xPos / 2)))

      val currPos = (yPos * width) + xPos

      r(currPos) = YUVtoR(yx, ux, vx)
      g(currPos) = YUVtoG(yx, ux, vx)
      b(currPos) = YUVtoB(yx, ux, vx)
    }

    (r, g, b)
  }

}
