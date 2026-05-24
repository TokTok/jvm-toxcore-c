package im.tox.tox4j.av.callbacks

import im.tox.tox4j.av.data.ToxAvHeight
import im.tox.tox4j.av.data.ToxAvWidth
import im.tox.tox4j.core.data.ToxFriendNumber

interface VideoReceiveFrameCallback<ToxCoreState> {
    /**
     * The function type for the video_receive_frame callback.
     *
     * The size of plane data is derived from width and height as documented below.
     *
     * Strides represent padding for each plane that may or may not be present. You must handle strides in your image processing code. Strides are negative if the image is bottom-up hence why you MUST `abs()` it when calculating plane buffer size.
     *
     * @param friendNumber The friend number of the friend who sent a video frame.
     * @param width Width of the frame in pixels.
     * @param height Height of the frame in pixels.
     * @param y Luminosity plane. `Size = MAX(width, abs(ystride)) * height`.
     * @param u U chroma plane. `Size = MAX((width+1)/2, abs(ustride)) * ((height+1)/2)`.
     * @param v V chroma plane. `Size = MAX((width+1)/2, abs(vstride)) * ((height+1)/2)`.
     * @param ystride Luminosity plane stride.
     * @param ustride U chroma plane stride.
     * @param vstride V chroma plane stride.
     */
    fun videoReceiveFrame(
        friendNumber: ToxFriendNumber,
        width: ToxAvWidth,
        height: ToxAvHeight,
        y: ByteArray,
        u: ByteArray,
        v: ByteArray,
        ystride: Int,
        ustride: Int,
        vstride: Int,
        state: ToxCoreState,
    ): ToxCoreState = state
}
