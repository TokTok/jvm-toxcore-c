package im.tox.tox4j.av.callbacks

import java.util

import im.tox.tox4j.ToxAvTestBase
import im.tox.tox4j.av.callbacks.AvInvokeTest._
import im.tox.tox4j.av.enums.ToxavFriendCallState
import im.tox.tox4j.av.{AudioChannels, BitRate, SamplingRate}
import im.tox.tox4j.core.SmallNat
import im.tox.tox4j.core.callbacks.InvokeTest.{ByteArray, ShortArray}
import im.tox.tox4j.core.options.ToxOptions
import im.tox.tox4j.impl.jni.{ToxAvImpl, ToxCoreImpl}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.FunSuite
import org.scalatest.prop.PropertyChecks

import scala.collection.JavaConverters._
import scala.language.implicitConversions
import scala.util.Random

final class AvInvokeTest extends FunSuite with PropertyChecks {

  final class TestEventListener extends ToxAvEventListener[Event] {
    private def setEvent(event: Event)(state: Event): Event = {
      assert(state == null)
      event
    }

    // scalastyle:off line.size.limit
    override def audioReceiveFrame(friendNumber: Int, pcm: Array[Short], channels: AudioChannels, samplingRate: SamplingRate)(state: Event): Event = setEvent(AudioReceiveFrame(friendNumber, pcm, channels, samplingRate))(state)
    override def bitRateStatus(friendNumber: Int, audioBitRate: BitRate, videoBitRate: BitRate)(state: Event): Event = setEvent(BitRateStatus(friendNumber, audioBitRate, videoBitRate))(state)
    override def call(friendNumber: Int, audioEnabled: Boolean, videoEnabled: Boolean)(state: Event): Event = setEvent(Call(friendNumber, audioEnabled, videoEnabled))(state)
    override def callState(friendNumber: Int, callState: util.Collection[ToxavFriendCallState])(state: Event): Event = setEvent(CallState(friendNumber, callState.asScala.toSet))(state)
    override def videoReceiveFrame(friendNumber: Int, width: Int, height: Int, y: Array[Byte], u: Array[Byte], v: Array[Byte], yStride: Int, uStride: Int, vStride: Int)(state: Event): Event = setEvent(VideoReceiveFrame(friendNumber, width, height, y, u, v, yStride, uStride, vStride))(state)
    // scalastyle:on line.size.limit
  }

  def callbackTest(invoke: ToxAvImpl[Event] => Unit, expected: Event): Unit = {
    val tox = new ToxCoreImpl[Event](ToxOptions())
    val toxav = new ToxAvImpl[Event](tox)

    try {
      val listener = new TestEventListener
      toxav.callback(listener)
      invoke(toxav)
      val event = toxav.iterate(null)
      assert(event == expected)
    } finally {
      toxav.close()
      tox.close()
    }
  }

  private val random = new Random

  private implicit val arbToxavFriendCallState: Arbitrary[ToxavFriendCallState] = {
    Arbitrary(Arbitrary.arbInt.arbitrary.map { i => ToxavFriendCallState.values()(Math.abs(i % ToxavFriendCallState.values().length)) })
  }

  private implicit val arbSamplingRate: Arbitrary[SamplingRate] = {
    Arbitrary(Gen.oneOf(
      SamplingRate.Rate8k,
      SamplingRate.Rate12k,
      SamplingRate.Rate16k,
      SamplingRate.Rate24k,
      SamplingRate.Rate48k
    ))
  }

  private implicit val arbBitRate: Arbitrary[BitRate] = {
    Arbitrary(Gen.oneOf(
      Gen.const(BitRate.Unchanged),
      Gen.const(BitRate.Disabled),
      arbitrary[Int].map(BitRate.fromInt).map(_.getOrElse(BitRate.Unchanged))
    ))
  }

  test("AudioReceiveFrame") {
    assume(ToxAvTestBase.enabled)
    forAll { (friendNumber: Int, pcm: Array[Short], samplingRate: SamplingRate) =>
      val channels =
        pcm.length match {
          case length if length % 2 == 0 => AudioChannels.Stereo
          case length                    => AudioChannels.Mono
        }
      callbackTest(
        _.invokeAudioReceiveFrame(friendNumber, pcm, channels, samplingRate),
        AudioReceiveFrame(friendNumber, pcm, channels, samplingRate)
      )
    }
  }

  test("BitRateStatus") {
    assume(ToxAvTestBase.enabled)
    forAll { (friendNumber: Int, audioBitRate: BitRate, videoBitRate: BitRate) =>
      callbackTest(
        _.invokeBitRateStatus(friendNumber, audioBitRate, videoBitRate),
        BitRateStatus(friendNumber, audioBitRate, videoBitRate)
      )
    }
  }

  test("Call") {
    assume(ToxAvTestBase.enabled)
    forAll { (friendNumber: Int, audioEnabled: Boolean, videoEnabled: Boolean) =>
      callbackTest(
        _.invokeCall(friendNumber, audioEnabled, videoEnabled),
        Call(friendNumber, audioEnabled, videoEnabled)
      )
    }
  }

  test("CallState") {
    assume(ToxAvTestBase.enabled)
    forAll { (friendNumber: Int, callState: Set[ToxavFriendCallState]) =>
      callbackTest(
        _.invokeCallState(friendNumber, callState.asJavaCollection),
        CallState(friendNumber, callState)
      )
    }
  }

  test("VideoReceiveFrame") {
    assume(ToxAvTestBase.enabled)
    forAll { (friendNumber: Int, width: SmallNat, height: SmallNat, yStride: SmallNat, uStride: SmallNat, vStride: SmallNat) =>
      whenever(width > 0 && height > 0) {
        val y = Array.ofDim[Byte]((width max yStride) * height)
        val u = Array.ofDim[Byte](((width / 2) max Math.abs(uStride)) * (height / 2))
        val v = Array.ofDim[Byte](((width / 2) max Math.abs(vStride)) * (height / 2))
        random.nextBytes(y)
        random.nextBytes(u)
        random.nextBytes(v)
        callbackTest(
          _.invokeVideoReceiveFrame(friendNumber, width, height, y, u, v, yStride, uStride, vStride),
          VideoReceiveFrame(friendNumber, width, height, y, u, v, yStride, uStride, vStride)
        )
      }
    }
  }

}

object AvInvokeTest {
  sealed trait Event
  private final case class AudioReceiveFrame(friendNumber: Int, pcm: ShortArray, channels: AudioChannels, samplingRate: SamplingRate) extends Event
  private final case class BitRateStatus(friendNumber: Int, audioBitRate: BitRate, videoBitRate: BitRate) extends Event
  private final case class Call(friendNumber: Int, audioEnabled: Boolean, videoEnabled: Boolean) extends Event
  private final case class CallState(friendNumber: Int, callState: Set[ToxavFriendCallState]) extends Event
  private final case class VideoReceiveFrame(friendNumber: Int, width: Int, height: Int, y: ByteArray, u: ByteArray, v: ByteArray, yStride: Int, uStride: Int, vStride: Int) extends Event // scalastyle:ignore line.size.limit
}
