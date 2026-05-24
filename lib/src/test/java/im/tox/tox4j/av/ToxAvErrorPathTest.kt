package im.tox.tox4j.av

import im.tox.tox4j.av.data.ToxAvBitRate
import im.tox.tox4j.av.data.ToxAvChannels
import im.tox.tox4j.av.data.ToxAvHeight
import im.tox.tox4j.av.data.ToxAvSamplingRate
import im.tox.tox4j.av.data.ToxAvWidth
import im.tox.tox4j.av.enums.ToxAvCallControl
import im.tox.tox4j.av.exceptions.ToxAvAnswerException
import im.tox.tox4j.av.exceptions.ToxAvBitRateSetException
import im.tox.tox4j.av.exceptions.ToxAvCallControlException
import im.tox.tox4j.av.exceptions.ToxAvCallException
import im.tox.tox4j.av.exceptions.ToxAvSendFrameException
import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.core.options.ToxOptions
import im.tox.tox4j.impl.jni.ToxAvImpl
import im.tox.tox4j.impl.jni.ToxCoreImpl
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/** The friend is never connected. */
class ToxAvErrorPathTest {
    private val options = ToxOptions(localDiscoveryEnabled = false)

    private inline fun withAvAndFriend(block: (ToxAvImpl, ToxFriendNumber) -> Unit) {
        ToxCoreImpl(options).use { peer ->
            ToxCoreImpl(options).use { tox ->
                val friend = tox.friendAddNorequest(peer.publicKey)
                ToxAvImpl(tox).use { av ->
                    block(av, friend)
                }
            }
        }
    }

    @Test
    fun call_unknownFriend_throws() {
        ToxCoreImpl(options).use { tox ->
            ToxAvImpl(tox).use { av ->
                val ex =
                    assertFailsWith<ToxAvCallException> {
                        av.call(ToxFriendNumber(999), ToxAvBitRate(48), ToxAvBitRate(0))
                    }
                assertEquals(ToxAvCallException.Code.FRIEND_NOT_FOUND, ex.code)
            }
        }
    }

    @Test
    fun call_unconnectedFriend_throws() {
        withAvAndFriend { av, friend ->
            val ex =
                assertFailsWith<ToxAvCallException> {
                    av.call(friend, ToxAvBitRate(48), ToxAvBitRate(0))
                }
            assertEquals(ToxAvCallException.Code.FRIEND_NOT_CONNECTED, ex.code)
        }
    }

    @Test
    fun call_invalidBitRate_throws() {
        withAvAndFriend { av, friend ->
            // ToxAvBitRate(-2) reaches C as 0xFFFFFFFE. (-1 means "unchanged" only to the
            // *_set_bit_rate calls.)
            val ex =
                assertFailsWith<ToxAvCallException> {
                    av.call(friend, ToxAvBitRate(-2), ToxAvBitRate(0))
                }
            assertEquals(ToxAvCallException.Code.INVALID_BIT_RATE, ex.code)
        }
    }

    @Test
    fun answer_unknownFriend_throws() {
        ToxCoreImpl(options).use { tox ->
            ToxAvImpl(tox).use { av ->
                val ex =
                    assertFailsWith<ToxAvAnswerException> {
                        av.answer(ToxFriendNumber(999), ToxAvBitRate(48), ToxAvBitRate(0))
                    }
                assertEquals(ToxAvAnswerException.Code.FRIEND_NOT_FOUND, ex.code)
            }
        }
    }

    @Test
    fun answer_notCalling_throws() {
        withAvAndFriend { av, friend ->
            val ex =
                assertFailsWith<ToxAvAnswerException> {
                    av.answer(friend, ToxAvBitRate(48), ToxAvBitRate(0))
                }
            assertEquals(ToxAvAnswerException.Code.FRIEND_NOT_CALLING, ex.code)
        }
    }

    @Test
    fun callControl_unknownFriend_throws() {
        ToxCoreImpl(options).use { tox ->
            ToxAvImpl(tox).use { av ->
                val ex =
                    assertFailsWith<ToxAvCallControlException> {
                        av.callControl(ToxFriendNumber(999), ToxAvCallControl.CANCEL)
                    }
                assertEquals(ToxAvCallControlException.Code.FRIEND_NOT_FOUND, ex.code)
            }
        }
    }

    @Test
    fun callControl_notInCall_throws() {
        withAvAndFriend { av, friend ->
            val ex =
                assertFailsWith<ToxAvCallControlException> {
                    av.callControl(friend, ToxAvCallControl.PAUSE)
                }
            assertEquals(ToxAvCallControlException.Code.FRIEND_NOT_IN_CALL, ex.code)
        }
    }

    @Test
    fun audioSetBitRate_unknownFriend_throws() {
        ToxCoreImpl(options).use { tox ->
            ToxAvImpl(tox).use { av ->
                val ex =
                    assertFailsWith<ToxAvBitRateSetException> {
                        av.audioSetBitRate(ToxFriendNumber(999), ToxAvBitRate(48))
                    }
                assertEquals(ToxAvBitRateSetException.Code.FRIEND_NOT_FOUND, ex.code)
            }
        }
    }

    @Test
    fun audioSetBitRate_notInCall_throws() {
        withAvAndFriend { av, friend ->
            val ex =
                assertFailsWith<ToxAvBitRateSetException> {
                    av.audioSetBitRate(friend, ToxAvBitRate(48))
                }
            assertEquals(ToxAvBitRateSetException.Code.FRIEND_NOT_IN_CALL, ex.code)
        }
    }

    @Test
    fun videoSetBitRate_unknownFriend_throws() {
        ToxCoreImpl(options).use { tox ->
            ToxAvImpl(tox).use { av ->
                val ex =
                    assertFailsWith<ToxAvBitRateSetException> {
                        av.videoSetBitRate(ToxFriendNumber(999), ToxAvBitRate(500))
                    }
                assertEquals(ToxAvBitRateSetException.Code.FRIEND_NOT_FOUND, ex.code)
            }
        }
    }

    @Test
    fun audioSendFrame_unknownFriend_throws() {
        ToxCoreImpl(options).use { tox ->
            ToxAvImpl(tox).use { av ->
                val pcm = ShortArray(960)
                val ex =
                    assertFailsWith<ToxAvSendFrameException> {
                        av.audioSendFrame(
                            ToxFriendNumber(999),
                            pcm,
                            960L,
                            ToxAvChannels(1),
                            ToxAvSamplingRate(48000),
                        )
                    }
                assertEquals(ToxAvSendFrameException.Code.FRIEND_NOT_FOUND, ex.code)
            }
        }
    }

    @Test
    fun videoSendFrame_unknownFriend_throws() {
        ToxCoreImpl(options).use { tox ->
            ToxAvImpl(tox).use { av ->
                val plane = ByteArray(100 * 100)
                val chroma = ByteArray(50 * 50)
                val ex =
                    assertFailsWith<ToxAvSendFrameException> {
                        av.videoSendFrame(ToxFriendNumber(999), ToxAvWidth(100), ToxAvHeight(100), plane, chroma, chroma)
                    }
                assertEquals(ToxAvSendFrameException.Code.FRIEND_NOT_FOUND, ex.code)
            }
        }
    }

    @Test
    fun audioSendFrame_pcmSizeMismatch_throws() {
        // pcm must hold sample_count * channels samples.
        withAvAndFriend { av, friend ->
            val claimedSamples = 480
            // Half of what stereo needs.
            val undersizedPcm = ShortArray(claimedSamples)
            val ex =
                assertFailsWith<ToxAvSendFrameException> {
                    av.audioSendFrame(
                        friend,
                        undersizedPcm,
                        claimedSamples.toLong(),
                        ToxAvChannels(2),
                        ToxAvSamplingRate(48000),
                    )
                }
            assertEquals(ToxAvSendFrameException.Code.INVALID, ex.code)
        }
    }

    @Test
    fun audioSendFrame_negativeNumbers_throw() {
        // Not a crash: Kotlin has no unsigned numbers here.
        withAvAndFriend { av, friend ->
            val pcm = ShortArray(480)
            val cases =
                listOf(
                    Triple(-1L, ToxAvChannels(1), ToxAvSamplingRate(48000)),
                    Triple(480L, ToxAvChannels(-1), ToxAvSamplingRate(48000)),
                    Triple(480L, ToxAvChannels(1), ToxAvSamplingRate(-1)),
                )
            for ((sampleCount, channels, samplingRate) in cases) {
                val ex =
                    assertFailsWith<ToxAvSendFrameException> {
                        av.audioSendFrame(friend, pcm, sampleCount, channels, samplingRate)
                    }
                assertEquals(ToxAvSendFrameException.Code.INVALID, ex.code)
            }
        }
    }
}
