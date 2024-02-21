package im.tox.tox4j.impl.jni

import im.tox.tox4j.av.ToxAv
import im.tox.tox4j.av.callbacks.ToxAvEventListener
import im.tox.tox4j.av.data.AudioChannels
import im.tox.tox4j.av.data.BitRate
import im.tox.tox4j.av.data.SampleCount
import im.tox.tox4j.av.data.SamplingRate
import im.tox.tox4j.av.enums.ToxavCallControl
import im.tox.tox4j.av.exceptions.ToxavNewException
import im.tox.tox4j.core.ToxCore
import im.tox.tox4j.core.data.ToxFriendNumber

/**
 * Initialise an A/V session for the existing Tox instance.
 *
 * @param tox An instance of the C-backed ToxCore implementation.
 * @throws ToxavNewException If there was already an A/V session.
 */
final class ToxAvImpl(private val tox: ToxCoreImpl) : ToxAv {
    internal val instanceNumber = ToxAvJni.toxavNew(tox.instanceNumber)

    override fun create(tox: ToxCore): ToxAv =
        try {
            ToxAvImpl(tox as ToxCoreImpl)
        } catch (_: ClassCastException) {
            throw ToxavNewException(
                ToxavNewException.Code.INCOMPATIBLE,
                tox::class.java.getCanonicalName(),
            )
        }

    override fun close(): Unit = ToxAvJni.toxavKill(instanceNumber)

    protected fun finalize(): Unit = ToxAvJni.toxavFinalize(instanceNumber)

    override fun <S> iterate(
        handler: ToxAvEventListener<S>,
        state: S,
    ): S = ToxAvEventDispatch.dispatch(handler, ToxAvJni.toxavIterate(instanceNumber), state)

    override val iterationInterval: Int
        get() = ToxAvJni.toxavIterationInterval(instanceNumber)

    // @throws[ToxavCallException]
    override fun call(
        friendNumber: ToxFriendNumber,
        audioBitRate: BitRate,
        videoBitRate: BitRate,
    ): Unit =
        ToxAvJni.toxavCall(
            instanceNumber,
            friendNumber.value,
            audioBitRate.value,
            videoBitRate.value,
        )

    // @throws[ToxavAnswerException]
    override fun answer(
        friendNumber: ToxFriendNumber,
        audioBitRate: BitRate,
        videoBitRate: BitRate,
    ): Unit =
        ToxAvJni.toxavAnswer(
            instanceNumber,
            friendNumber.value,
            audioBitRate.value,
            videoBitRate.value,
        )

    // @throws[ToxavCallControlException]
    override fun callControl(
        friendNumber: ToxFriendNumber,
        control: ToxavCallControl,
    ): Unit = ToxAvJni.toxavCallControl(instanceNumber, friendNumber.value, control.ordinal)

    // @throws[ToxavBitRateSetException]
    override fun setAudioBitRate(
        friendNumber: ToxFriendNumber,
        audioBitRate: BitRate,
    ): Unit = ToxAvJni.toxavAudioSetBitRate(instanceNumber, friendNumber.value, audioBitRate.value)

    // @throws[ToxavBitRateSetException]
    override fun setVideoBitRate(
        friendNumber: ToxFriendNumber,
        videoBitRate: BitRate,
    ): Unit = ToxAvJni.toxavVideoSetBitRate(instanceNumber, friendNumber.value, videoBitRate.value)

    // @throws[ToxavSendFrameException]
    override fun audioSendFrame(
        friendNumber: ToxFriendNumber,
        pcm: ShortArray,
        sampleCount: SampleCount,
        channels: AudioChannels,
        samplingRate: SamplingRate,
    ): Unit =
        ToxAvJni.toxavAudioSendFrame(
            instanceNumber,
            friendNumber.value,
            pcm,
            sampleCount.value,
            channels.value,
            samplingRate.value,
        )

    // @throws[ToxavSendFrameException]
    override fun videoSendFrame(
        friendNumber: ToxFriendNumber,
        width: Int,
        height: Int,
        y: ByteArray,
        u: ByteArray,
        v: ByteArray,
    ): Unit = ToxAvJni.toxavVideoSendFrame(instanceNumber, friendNumber.value, width, height, y, u, v)
}
