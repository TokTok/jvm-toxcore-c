package im.tox.tox4j.av.callbacks

import org.jetbrains.annotations.NotNull

/**
 * Triggered when a video frame is received.
 */
trait VideoReceiveFrameCallback[ToxCoreState] {
  /**
   * @param friendNumber The friend number of the friend who sent a video frame.
   * @param width Width of the frame in pixels.
   * @param height Height of the frame in pixels.
   * @param y Y-plane.
   * @param u U-plane.
   * @param v V-plane.
   *          The size of plane data is derived from width and height where
   *          Y = max(width,   abs(yStride)) * height
   *          U = max(width/2, abs(uStride)) * (height/2)
   *          V = max(width/2, abs(vStride)) * (height/2).
   * @param yStride Stride length for Y-plane.
   * @param uStride Stride length for U-plane.
   * @param vStride Stride length for V-plane. Strides represent padding for
   *                each plane that may or may not be present. You must handle
   *                strides in your image processing code. Strides are negative
   *                if the image is bottom-up hence why you must abs() it when
   *                calculating plane buffer size.
   */
  def videoReceiveFrame(
    friendNumber: Int,
    width: Int, height: Int,
    @NotNull y: Array[Byte], @NotNull u: Array[Byte], @NotNull v: Array[Byte],
    yStride: Int, uStride: Int, vStride: Int
  )(state: ToxCoreState): ToxCoreState = state
}
