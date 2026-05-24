package im.tox.tox4j.impl.jni

import im.tox.tox4j.av.ToxAv
import im.tox.tox4j.av.callbacks.ToxAvEventListener
import im.tox.tox4j.av.data.ToxAvBitRate
import im.tox.tox4j.av.data.ToxAvChannels
import im.tox.tox4j.av.data.ToxAvHeight
import im.tox.tox4j.av.data.ToxAvSamplingRate
import im.tox.tox4j.av.data.ToxAvWidth
import im.tox.tox4j.av.enums.ToxAvCallControl
import im.tox.tox4j.core.data.ToxFriendNumber

class ToxAvImpl(
    private val tox: ToxCoreImpl,
) : ToxAv {
    internal val instanceNumber = ToxAvJni.toxavNew(tox.instanceNumber)

    override fun close(): Unit = ToxAvJni.toxavKill(instanceNumber)

    override val iterationInterval: Int
        get() = ToxAvJni.toxavIterationInterval(instanceNumber)

    override fun <ToxCoreState> iterate(
        handler: ToxAvEventListener<ToxCoreState>,
        state: ToxCoreState,
    ): ToxCoreState = ToxAvEventDispatch.dispatch(handler, ToxAvJni.toxavIterate(instanceNumber), state)

    override fun call(
        friendNumber: ToxFriendNumber,
        audioBitRate: ToxAvBitRate,
        videoBitRate: ToxAvBitRate,
    ) = ToxAvJni.toxavCall(instanceNumber, friendNumber.value, audioBitRate.value, videoBitRate.value)

    override fun answer(
        friendNumber: ToxFriendNumber,
        audioBitRate: ToxAvBitRate,
        videoBitRate: ToxAvBitRate,
    ) = ToxAvJni.toxavAnswer(instanceNumber, friendNumber.value, audioBitRate.value, videoBitRate.value)

    override fun callControl(
        friendNumber: ToxFriendNumber,
        control: ToxAvCallControl,
    ) = ToxAvJni.toxavCallControl(instanceNumber, friendNumber.value, control.ordinal)

    override fun audioSendFrame(
        friendNumber: ToxFriendNumber,
        pcm: ShortArray,
        sampleCount: Long,
        channels: ToxAvChannels,
        samplingRate: ToxAvSamplingRate,
    ) = ToxAvJni.toxavAudioSendFrame(instanceNumber, friendNumber.value, pcm, sampleCount, channels.value, samplingRate.value)

    override fun audioSetBitRate(
        friendNumber: ToxFriendNumber,
        bitRate: ToxAvBitRate,
    ) = ToxAvJni.toxavAudioSetBitRate(instanceNumber, friendNumber.value, bitRate.value)

    override fun videoSendFrame(
        friendNumber: ToxFriendNumber,
        width: ToxAvWidth,
        height: ToxAvHeight,
        y: ByteArray,
        u: ByteArray,
        v: ByteArray,
    ) = ToxAvJni.toxavVideoSendFrame(instanceNumber, friendNumber.value, width.value, height.value, y, u, v)

    override fun videoSetBitRate(
        friendNumber: ToxFriendNumber,
        bitRate: ToxAvBitRate,
    ) = ToxAvJni.toxavVideoSetBitRate(instanceNumber, friendNumber.value, bitRate.value)

    protected fun finalize() {
        runCatching {
            ToxAvJni.toxavKill(instanceNumber)
            ToxAvJni.toxavFinalize(instanceNumber)
        }
    }
}
