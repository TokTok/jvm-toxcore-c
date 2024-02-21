package im.tox.tox4j.impl.jni

import com.google.protobuf.ByteString
import im.tox.tox4j.av.callbacks.AudioBitRateCallback
import im.tox.tox4j.av.callbacks.AudioReceiveFrameCallback
import im.tox.tox4j.av.callbacks.CallCallback
import im.tox.tox4j.av.callbacks.CallStateCallback
import im.tox.tox4j.av.callbacks.ToxAvEventListener
import im.tox.tox4j.av.callbacks.VideoBitRateCallback
import im.tox.tox4j.av.callbacks.VideoReceiveFrameCallback
import im.tox.tox4j.av.data.AudioChannels
import im.tox.tox4j.av.data.BitRate
import im.tox.tox4j.av.data.Height
import im.tox.tox4j.av.data.SamplingRate
import im.tox.tox4j.av.data.Width
import im.tox.tox4j.av.enums.ToxavFriendCallState
import im.tox.tox4j.av.proto.AudioBitRate
import im.tox.tox4j.av.proto.AudioReceiveFrame
import im.tox.tox4j.av.proto.AvEvents
import im.tox.tox4j.av.proto.Call
import im.tox.tox4j.av.proto.CallState
import im.tox.tox4j.av.proto.VideoBitRate
import im.tox.tox4j.av.proto.VideoReceiveFrame
import im.tox.tox4j.core.data.ToxFriendNumber
import java.util.EnumSet

object ToxAvEventDispatch {
    fun convert(channels: Int): AudioChannels =
        when (channels) {
            1 -> AudioChannels.Mono
            2 -> AudioChannels.Stereo
            else -> AudioChannels.Stereo
        }

    fun convert(kind: CallState.Kind): ToxavFriendCallState =
        when (kind) {
            CallState.Kind.ERROR -> ToxavFriendCallState.ERROR
            CallState.Kind.FINISHED -> ToxavFriendCallState.FINISHED
            CallState.Kind.SENDING_A -> ToxavFriendCallState.SENDING_A
            CallState.Kind.SENDING_V -> ToxavFriendCallState.SENDING_V
            CallState.Kind.ACCEPTING_A -> ToxavFriendCallState.ACCEPTING_A
            CallState.Kind.ACCEPTING_V -> ToxavFriendCallState.ACCEPTING_V
            CallState.Kind.UNRECOGNIZED -> ToxavFriendCallState.ERROR
        }

    fun convert(callState: EnumSet<ToxavFriendCallState>): Int =
        callState.fold(
            0,
            { bitMask, bit ->
                val nextMask =
                    when (bit) {
                        ToxavFriendCallState.ERROR -> 1 shl 0
                        ToxavFriendCallState.FINISHED -> 1 shl 1
                        ToxavFriendCallState.SENDING_A -> 1 shl 2
                        ToxavFriendCallState.SENDING_V -> 1 shl 3
                        ToxavFriendCallState.ACCEPTING_A -> 1 shl 4
                        ToxavFriendCallState.ACCEPTING_V -> 1 shl 5
                        null -> 0
                    }
                bitMask or nextMask
            },
        )

    private fun <S> dispatchCall(
        handler: CallCallback<S>,
        call: List<Call>,
        state: S,
    ): S =
        call.fold(
            state,
            { next, ev ->
                handler.call(
                    ToxFriendNumber(ev.getFriendNumber()),
                    ev.getAudioEnabled(),
                    ev.getVideoEnabled(),
                    next,
                )
            },
        )

    private fun <S> dispatchCallState(
        handler: CallStateCallback<S>,
        callState: List<CallState>,
        state: S,
    ): S =
        callState.fold(
            state,
            { next, ev ->
                val bits = ev.getCallStateList().map { convert(it) }
                handler.callState(
                    ToxFriendNumber(ev.getFriendNumber()),
                    EnumSet.of(bits[0], *bits.drop(1).toTypedArray()),
                    next,
                )
            },
        )

    private fun <S> dispatchAudioBitRate(
        handler: AudioBitRateCallback<S>,
        audioBitRate: List<AudioBitRate>,
        state: S,
    ): S =
        audioBitRate.fold(
            state,
            { next, ev ->
                handler.audioBitRate(
                    ToxFriendNumber(ev.getFriendNumber()),
                    BitRate(ev.getAudioBitRate()),
                    next,
                )
            },
        )

    private fun <S> dispatchVideoBitRate(
        handler: VideoBitRateCallback<S>,
        videoBitRate: List<VideoBitRate>,
        state: S,
    ): S =
        videoBitRate.fold(
            state,
            { next, ev ->
                handler.videoBitRate(
                    ToxFriendNumber(ev.getFriendNumber()),
                    BitRate(ev.getVideoBitRate()),
                    next,
                )
            },
        )

    private fun toShortArray(bytes: ByteString): ShortArray {
        val shortBuffer = bytes.asReadOnlyByteBuffer().asShortBuffer()
        val shortArray = ShortArray(shortBuffer.capacity())
        shortBuffer.get(shortArray)
        return shortArray
    }

    private fun <S> dispatchAudioReceiveFrame(
        handler: AudioReceiveFrameCallback<S>,
        audioReceiveFrame: List<AudioReceiveFrame>,
        state: S,
    ): S =
        audioReceiveFrame.fold(
            state,
            { next, ev ->
                handler.audioReceiveFrame(
                    ToxFriendNumber(ev.getFriendNumber()),
                    toShortArray(ev.getPcm()),
                    convert(ev.getChannels()),
                    SamplingRate.values().filter { it.value == ev.getSamplingRate() }[0],
                    next,
                )
            },
        )

    private fun convert(
        arrays: Triple<ByteArray, ByteArray, ByteArray>?,
        y: ByteString,
        u: ByteString,
        v: ByteString,
    ): Triple<ByteArray, ByteArray, ByteArray> =
        if (arrays == null) {
            Triple(y.toByteArray(), u.toByteArray(), v.toByteArray())
        } else {
            y.copyTo(arrays.first, 0)
            u.copyTo(arrays.second, 0)
            v.copyTo(arrays.third, 0)
            arrays
        }

    private fun <S> dispatchVideoReceiveFrame(
        handler: VideoReceiveFrameCallback<S>,
        videoReceiveFrame: List<VideoReceiveFrame>,
        state: S,
    ): S =
        videoReceiveFrame.fold(
            state,
            { next, ev ->
                val w = Width(ev.getWidth())
                val h = Height(ev.getHeight())
                val (yArray, uArray, vArray) =
                    convert(
                        handler.videoFrameCachedYUV(
                            h,
                            ev.getYStride(),
                            ev.getUStride(),
                            ev.getVStride(),
                        ),
                        ev.getY(),
                        ev.getU(),
                        ev.getV(),
                    )

                handler.videoReceiveFrame(
                    ToxFriendNumber(ev.getFriendNumber()),
                    w,
                    h,
                    yArray,
                    uArray,
                    vArray,
                    ev.getYStride(),
                    ev.getUStride(),
                    ev.getVStride(),
                    next,
                )
            },
        )

    private fun <S> dispatchEvents(
        handler: ToxAvEventListener<S>,
        events: AvEvents,
        state: S,
    ): S =
        dispatchCall(
            handler,
            events.getCallList(),
            dispatchCallState(
                handler,
                events.getCallStateList(),
                dispatchAudioBitRate(
                    handler,
                    events.getAudioBitRateList(),
                    dispatchVideoBitRate(
                        handler,
                        events.getVideoBitRateList(),
                        dispatchAudioReceiveFrame(
                            handler,
                            events.getAudioReceiveFrameList(),
                            dispatchVideoReceiveFrame(
                                handler,
                                events.getVideoReceiveFrameList(),
                                state,
                            ),
                        ),
                    ),
                ),
            ),
        )

    fun <S> dispatch(
        handler: ToxAvEventListener<S>,
        eventData: ByteArray?,
        state: S,
    ): S =
        if (eventData == null) {
            state
        } else {
            dispatchEvents(handler, AvEvents.parseFrom(eventData), state)
        }
}
