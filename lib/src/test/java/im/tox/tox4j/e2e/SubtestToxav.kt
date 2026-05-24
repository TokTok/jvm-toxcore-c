package im.tox.tox4j.e2e

import im.tox.tox4j.av.callbacks.ToxAvEventListener
import im.tox.tox4j.av.data.ToxAvBitRate
import im.tox.tox4j.av.data.ToxAvChannels
import im.tox.tox4j.av.data.ToxAvHeight
import im.tox.tox4j.av.data.ToxAvSamplingRate
import im.tox.tox4j.av.data.ToxAvWidth
import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.impl.jni.ToxAvImpl
import java.lang.Thread.sleep
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * A call with an audio and a video frame. The codecs are lossy, so this
 * compares the frames' shape, not their bytes.
 */
object SubtestToxav {
    // 20 ms of a 1 kHz sine at 48 kHz mono.
    private val audioFrame: ShortArray =
        ShortArray(960) { i ->
            (Short.MAX_VALUE.toInt() * kotlin.math.sin(2.0 * Math.PI * 1000.0 * i / 48000.0)).toInt().toShort()
        }
    private const val AUDIO_SAMPLE_COUNT = 960L
    private val audioChannels = ToxAvChannels(1)
    private val audioSr = ToxAvSamplingRate(48000)

    private const val VIDEO_W = 64
    private const val VIDEO_H = 64
    private const val CHROMA_W = VIDEO_W / 2
    private const val CHROMA_H = VIDEO_H / 2
    private val yPlane: ByteArray = ByteArray(VIDEO_W * VIDEO_H) { i -> (i % 256).toByte() }
    private val uPlane: ByteArray = ByteArray(CHROMA_W * CHROMA_H) { 128.toByte() }
    private val vPlane: ByteArray = ByteArray(CHROMA_W * CHROMA_H) { 128.toByte() }

    fun run(harness: Tox4jHarness) {
        check(harness.toxes.size >= 2) { "SubtestToxav needs at least 2 toxes" }
        val alice = harness.toxes[0]
        val bob = harness.toxes[1]
        val bobFromAlice = alice.friendByPublicKey(bob.publicKey)
        val aliceFromBob = bob.friendByPublicKey(alice.publicKey)

        val coreIdle = object : im.tox.tox4j.core.callbacks.ToxCoreEventListener<Unit> { }
        val aliceAvIdle = object : ToxAvEventListener<Unit> { }

        ToxAvImpl(alice).use { aliceAv ->
            ToxAvImpl(bob).use { bobAv ->
                aliceAv.call(bobFromAlice, ToxAvBitRate(48), ToxAvBitRate(500))

                val callSeen = BooleanArray(1)
                val bobAvCallHandler =
                    object : ToxAvEventListener<Unit> {
                        override fun call(
                            friendNumber: ToxFriendNumber,
                            audioEnabled: Boolean,
                            videoEnabled: Boolean,
                            state: Unit,
                        ) {
                            if (friendNumber.value == aliceFromBob.value &&
                                audioEnabled &&
                                videoEnabled
                            ) {
                                callSeen[0] = true
                            }
                        }
                    }
                run {
                    val deadline = System.currentTimeMillis() + 30_000L
                    while (!callSeen[0] && System.currentTimeMillis() < deadline) {
                        harness.iterate(coreIdle, Unit)
                        aliceAv.iterate(aliceAvIdle, Unit)
                        bobAv.iterate(bobAvCallHandler, Unit)
                    }
                }
                assertTrue(callSeen[0], "Bob never received Alice's audio+video call within 30s")

                bobAv.answer(aliceFromBob, ToxAvBitRate(48), ToxAvBitRate(500))

                data class Received(
                    val audioSampleCount: Long = -1,
                    val audioChannels: ToxAvChannels? = null,
                    val audioSamplingRate: ToxAvSamplingRate? = null,
                    val videoWidth: Int = -1,
                    val videoHeight: Int = -1,
                    val ySize: Int = -1,
                    val uSize: Int = -1,
                    val vSize: Int = -1,
                ) {
                    val audioSeen get() = audioChannels != null
                    val videoSeen get() = videoWidth > 0
                }

                val received = arrayOf(Received())
                val bobAvFrameHandler =
                    object : ToxAvEventListener<Unit> {
                        override fun audioReceiveFrame(
                            friendNumber: ToxFriendNumber,
                            pcm: ShortArray,
                            sampleCount: Long,
                            channels: ToxAvChannels,
                            samplingRate: ToxAvSamplingRate,
                            state: Unit,
                        ) {
                            if (friendNumber.value != aliceFromBob.value) return
                            if (received[0].audioSeen) return
                            received[0] =
                                received[0].copy(
                                    audioSampleCount = sampleCount,
                                    audioChannels = channels,
                                    audioSamplingRate = samplingRate,
                                )
                        }

                        override fun videoReceiveFrame(
                            friendNumber: ToxFriendNumber,
                            width: ToxAvWidth,
                            height: ToxAvHeight,
                            y: ByteArray,
                            u: ByteArray,
                            v: ByteArray,
                            ystride: Int,
                            ustride: Int,
                            vstride: Int,
                            state: Unit,
                        ) {
                            if (friendNumber.value != aliceFromBob.value) return
                            if (received[0].videoSeen) return
                            received[0] =
                                received[0].copy(
                                    videoWidth = width.value,
                                    videoHeight = height.value,
                                    ySize = y.size,
                                    uSize = u.size,
                                    vSize = v.size,
                                )
                        }
                    }

                run {
                    val deadline = System.currentTimeMillis() + 30_000L
                    while ((!received[0].audioSeen || !received[0].videoSeen) &&
                        System.currentTimeMillis() < deadline
                    ) {
                        harness.iterate(coreIdle, Unit)
                        aliceAv.iterate(aliceAvIdle, Unit)
                        bobAv.iterate(bobAvFrameHandler, Unit)
                        try {
                            aliceAv.audioSendFrame(bobFromAlice, audioFrame, AUDIO_SAMPLE_COUNT, audioChannels, audioSr)
                        } catch (_: Exception) {
                            // Call state may not yet be SENDING_A.
                        }
                        try {
                            aliceAv.videoSendFrame(bobFromAlice, ToxAvWidth(VIDEO_W), ToxAvHeight(VIDEO_H), yPlane, uPlane, vPlane)
                        } catch (_: Exception) {
                            // Call state may not yet be SENDING_V.
                        }
                        sleep(20)
                    }
                }

                val r = received[0]
                assertTrue(r.audioSeen, "Bob never received any audio frame within 30s")
                assertTrue(r.videoSeen, "Bob never received any video frame within 30s")

                assertEquals(AUDIO_SAMPLE_COUNT, r.audioSampleCount, "audio sample count mismatch")
                assertEquals(audioChannels, r.audioChannels, "audio channel count mismatch")
                assertEquals(audioSr, r.audioSamplingRate, "audio sampling rate mismatch")

                // Planes can be padded to the stride.
                assertEquals(VIDEO_W, r.videoWidth, "video width mismatch")
                assertEquals(VIDEO_H, r.videoHeight, "video height mismatch")
                assertTrue(r.ySize >= VIDEO_W * VIDEO_H, "Y plane too small: ${r.ySize}")
                assertTrue(r.uSize >= CHROMA_W * CHROMA_H, "U plane too small: ${r.uSize}")
                assertTrue(r.vSize >= CHROMA_W * CHROMA_H, "V plane too small: ${r.vSize}")
            }
        }
    }
}
