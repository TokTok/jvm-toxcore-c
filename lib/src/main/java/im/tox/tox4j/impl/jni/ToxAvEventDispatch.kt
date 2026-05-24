package im.tox.tox4j.impl.jni

import im.tox.tox4j.av.callbacks.ToxAvEventListener
import im.tox.tox4j.av.data.ToxAvBitRate
import im.tox.tox4j.av.data.ToxAvChannels
import im.tox.tox4j.av.data.ToxAvHeight
import im.tox.tox4j.av.data.ToxAvSamplingRate
import im.tox.tox4j.av.data.ToxAvWidth
import im.tox.tox4j.av.proto.AvEvents
import im.tox.tox4j.core.data.ToxConferenceNumber
import im.tox.tox4j.core.data.ToxConferencePeerNumber
import im.tox.tox4j.core.data.ToxFriendNumber

object ToxAvEventDispatch {
    private fun toShortArray(bytes: com.google.protobuf.ByteString): ShortArray {
        val sb =
            bytes
                .asReadOnlyByteBuffer()
                .order(java.nio.ByteOrder.LITTLE_ENDIAN)
                .asShortBuffer()
        return ShortArray(sb.remaining()).also(sb::get)
    }

    fun <S> dispatch(
        handler: ToxAvEventListener<S>,
        eventData: ByteArray?,
        state: S,
    ): S {
        if (eventData == null || eventData.isEmpty()) return state
        val events = AvEvents.parseFrom(eventData)
        var next = state
        for (event in events.eventsList) {
            next =
                when (event.eventTypeCase) {
                    AvEvents.Event.EventTypeCase.AUDIO_BIT_RATE ->
                        handler.audioBitRate(
                            ToxFriendNumber(event.audioBitRate.friendNumber),
                            ToxAvBitRate(event.audioBitRate.audioBitRate),
                            next,
                        )
                    AvEvents.Event.EventTypeCase.AUDIO_DATA ->
                        handler.audioData(
                            ToxConferenceNumber(event.audioData.conferenceNumber),
                            ToxConferencePeerNumber(event.audioData.peerNumber),
                            toShortArray(event.audioData.pcm),
                            event.audioData.sampleCount,
                            ToxAvChannels(event.audioData.channels.toByte()),
                            ToxAvSamplingRate(event.audioData.samplingRate),
                            next,
                        )
                    AvEvents.Event.EventTypeCase.AUDIO_RECEIVE_FRAME ->
                        handler.audioReceiveFrame(
                            ToxFriendNumber(event.audioReceiveFrame.friendNumber),
                            toShortArray(event.audioReceiveFrame.pcm),
                            event.audioReceiveFrame.sampleCount,
                            ToxAvChannels(event.audioReceiveFrame.channels.toByte()),
                            ToxAvSamplingRate(event.audioReceiveFrame.samplingRate),
                            next,
                        )
                    AvEvents.Event.EventTypeCase.CALL ->
                        handler.call(
                            ToxFriendNumber(event.call.friendNumber),
                            event.call.audioEnabled,
                            event.call.videoEnabled,
                            next,
                        )
                    AvEvents.Event.EventTypeCase.CALL_STATE ->
                        handler.callState(
                            ToxFriendNumber(event.callState.friendNumber),
                            event.callState.state,
                            next,
                        )
                    AvEvents.Event.EventTypeCase.VIDEO_BIT_RATE ->
                        handler.videoBitRate(
                            ToxFriendNumber(event.videoBitRate.friendNumber),
                            ToxAvBitRate(event.videoBitRate.videoBitRate),
                            next,
                        )
                    AvEvents.Event.EventTypeCase.VIDEO_RECEIVE_FRAME ->
                        handler.videoReceiveFrame(
                            ToxFriendNumber(event.videoReceiveFrame.friendNumber),
                            ToxAvWidth(event.videoReceiveFrame.width.coerceIn(0, 0xFFFF)),
                            ToxAvHeight(event.videoReceiveFrame.height.coerceIn(0, 0xFFFF)),
                            event.videoReceiveFrame.y.toByteArray(),
                            event.videoReceiveFrame.u.toByteArray(),
                            event.videoReceiveFrame.v.toByteArray(),
                            event.videoReceiveFrame.ystride,
                            event.videoReceiveFrame.ustride,
                            event.videoReceiveFrame.vstride,
                            next,
                        )
                    AvEvents.Event.EventTypeCase.EVENTTYPE_NOT_SET -> next
                }
        }
        return next
    }
}
