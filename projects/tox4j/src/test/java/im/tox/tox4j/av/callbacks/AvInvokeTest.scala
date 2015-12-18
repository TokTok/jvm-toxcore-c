package im.tox.tox4j.av.callbacks

import java.util

import im.tox.tox4j.ToxAvTestBase
import im.tox.tox4j.av.callbacks.AvInvokeTest._
import im.tox.tox4j.av.data._
import im.tox.tox4j.av.enums.ToxavFriendCallState
import im.tox.tox4j.core.SmallNat
import im.tox.tox4j.core.callbacks.InvokeTest.{ByteArray, ShortArray}
import im.tox.tox4j.core.data.ToxFriendNumber
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

  final class TestEventListener extends ToxAvEventListener[Option[Event]] {
    private def setEvent(event: Event)(state: Option[Event]): Option[Event] = {
      assert(state.isEmpty)
      Some(event)
    }

    // scalastyle:off line.size.limit
    override def audioReceiveFrame(friendNumber: ToxFriendNumber, pcm: Array[Short], channels: AudioChannels, samplingRate: SamplingRate)(state: Option[Event]): Option[Event] = setEvent(AudioReceiveFrame(friendNumber, pcm, channels, samplingRate))(state)
    override def bitRateStatus(friendNumber: ToxFriendNumber, audioBitRate: BitRate, videoBitRate: BitRate)(state: Option[Event]): Option[Event] = setEvent(BitRateStatus(friendNumber, audioBitRate, videoBitRate))(state)
    override def call(friendNumber: ToxFriendNumber, audioEnabled: Boolean, videoEnabled: Boolean)(state: Option[Event]): Option[Event] = setEvent(Call(friendNumber, audioEnabled, videoEnabled))(state)
    override def callState(friendNumber: ToxFriendNumber, callState: util.EnumSet[ToxavFriendCallState])(state: Option[Event]): Option[Event] = setEvent(CallState(friendNumber, callState.asScala.toSet))(state)
    override def videoReceiveFrame(friendNumber: ToxFriendNumber, width: Width, height: Height, y: Array[Byte], u: Array[Byte], v: Array[Byte], yStride: Int, uStride: Int, vStride: Int)(state: Option[Event]): Option[Event] = setEvent(VideoReceiveFrame(friendNumber, width, height, y, u, v, yStride, uStride, vStride))(state)
    // scalastyle:on line.size.limit
  }

  def callbackTest(invoke: ToxAvEventSynth => Unit, expected: Event): Unit = {
    val tox = new ToxCoreImpl(ToxOptions())
    val toxav = new ToxAvImpl(tox)

    try {
      invoke(toxav)
      val listener = new TestEventListener
      val event = toxav.iterate(listener)(None)
      assert(event.contains(expected))
    } finally {
      toxav.close()
      tox.close()
    }
  }

  private val random = new Random

  private implicit val arbToxFriendNumber: Arbitrary[ToxFriendNumber] = {
    Arbitrary(arbitrary[Int].map(ToxFriendNumber.unsafeFromInt))
  }

  private implicit val arbToxavFriendCallState: Arbitrary[ToxavFriendCallState] = {
    Arbitrary(Arbitrary.arbInt.arbitrary.map { i => ToxavFriendCallState.values()(Math.abs(i % ToxavFriendCallState.values().length)) })
  }

  private implicit val arbWidth: Arbitrary[Width] = {
    Arbitrary(Arbitrary.arbInt.arbitrary.map(Width.clamp))
  }

  private implicit val arbHeight: Arbitrary[Height] = {
    Arbitrary(Arbitrary.arbInt.arbitrary.map(Height.clamp))
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
    forAll { (friendNumber: ToxFriendNumber, pcm: Array[Short], samplingRate: SamplingRate) =>
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
    forAll { (friendNumber: ToxFriendNumber, audioBitRate: BitRate, videoBitRate: BitRate) =>
      callbackTest(
        _.invokeBitRateStatus(friendNumber, audioBitRate, videoBitRate),
        BitRateStatus(friendNumber, audioBitRate, videoBitRate)
      )
    }
  }

  test("Call") {
    assume(ToxAvTestBase.enabled)
    forAll { (friendNumber: ToxFriendNumber, audioEnabled: Boolean, videoEnabled: Boolean) =>
      callbackTest(
        _.invokeCall(friendNumber, audioEnabled, videoEnabled),
        Call(friendNumber, audioEnabled, videoEnabled)
      )
    }
  }

  test("CallState") {
    assume(ToxAvTestBase.enabled)
    forAll { (friendNumber: ToxFriendNumber, callState: Set[ToxavFriendCallState]) =>
      whenever(callState.nonEmpty) {
        callbackTest(
          _.invokeCallState(friendNumber, util.EnumSet.copyOf(callState.asJavaCollection)),
          CallState(friendNumber, callState)
        )
      }
    }
  }

  test("VideoReceiveFrame") {
    assume(ToxAvTestBase.enabled)
    forAll { (friendNumber: ToxFriendNumber, width: Width, height: Height, yStride: SmallNat, uStride: SmallNat, vStride: SmallNat) =>
      val w = width.value
      val h = height.value
      whenever(w > 0 && h > 0) {
        val y = Array.ofDim[Byte]((w max yStride) * h)
        val u = Array.ofDim[Byte](((w / 2) max Math.abs(uStride)) * (h / 2))
        val v = Array.ofDim[Byte](((w / 2) max Math.abs(vStride)) * (h / 2))
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
  private final case class AudioReceiveFrame(friendNumber: ToxFriendNumber, pcm: ShortArray, channels: AudioChannels, samplingRate: SamplingRate) extends Event
  private final case class BitRateStatus(friendNumber: ToxFriendNumber, audioBitRate: BitRate, videoBitRate: BitRate) extends Event
  private final case class Call(friendNumber: ToxFriendNumber, audioEnabled: Boolean, videoEnabled: Boolean) extends Event
  private final case class CallState(friendNumber: ToxFriendNumber, callState: Set[ToxavFriendCallState]) extends Event
  private final case class VideoReceiveFrame(friendNumber: ToxFriendNumber, width: Width, height: Height, y: ByteArray, u: ByteArray, v: ByteArray, yStride: Int, uStride: Int, vStride: Int) extends Event // scalastyle:ignore line.size.limit
}
