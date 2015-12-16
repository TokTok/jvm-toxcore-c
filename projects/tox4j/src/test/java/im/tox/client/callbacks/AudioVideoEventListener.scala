package im.tox.client.callbacks

import java.util

import im.tox.client.{HostInfo, ToxClientState}
import im.tox.tox4j.OptimisedIdOps._
import im.tox.tox4j.ToxEventListener
import im.tox.tox4j.av.ToxAv
import im.tox.tox4j.av.callbacks.AudioGenerator
import im.tox.tox4j.av.callbacks.video.{VideoGenerator, VideoGenerators}
import im.tox.tox4j.av.data._
import im.tox.tox4j.av.enums.{ToxavCallControl, ToxavFriendCallState}
import im.tox.tox4j.core.ToxCore
import im.tox.tox4j.core.data._
import im.tox.tox4j.core.enums.ToxMessageType
import im.tox.tox4j.testing.GetDisjunction._

import scalaz.Lens

/**
 * Handles audio/video calls.
 */
final class AudioVideoEventListener(id: Int)
    extends IdLogging(id) with ToxEventListener[ToxClientState] {

  private val audioBitRate = BitRate.fromInt(8).get
  private val audioLength = AudioLength.Length60
  private val audioSamplingRate = SamplingRate.Rate8k
  private val audioFrameSize = (audioLength.value.toMillis * audioSamplingRate.value / 1000).toInt
  private val audioFramesPerIteration = 1

  private val videoBitRate = BitRate.fromInt(1).get

  private def say(friendNumber: ToxFriendNumber, message: String)(state: ToxClientState): ToxClientState = {
    state.addTask { (tox, av, state) =>
      tox.friendSendMessage(friendNumber, ToxMessageType.NORMAL, 0, ToxFriendMessage.fromString(message).get)
      state
    }
  }

  override def friendMessage(
    friendNumber: ToxFriendNumber,
    messageType: ToxMessageType,
    timeDelta: Int,
    message: ToxFriendMessage
  )(state: ToxClientState): ToxClientState = {
    val AudioCommand = "audio\\s+(.+)".r
    val VideoCommand = "video\\s+(.+)".r
    val ShowCommand = "show\\s+(.+)".r

    val command = message.toString.toLowerCase
    command match {
      case "call me" =>
        state.addTask { (tox, av, state) =>
          logInfo(s"Ringing ${state.friends(friendNumber).name}")
          av.call(friendNumber, audioBitRate, videoBitRate)
          state
        }

      case AudioCommand(request) => processAudioCommand(friendNumber, state, request)
      case VideoCommand(request) => processVideoCommand(friendNumber, state, request)
      case ShowCommand(request)  => processShowCommand(friendNumber, state, request)

      case _ =>
        say(friendNumber, s"unrecognised command: '$command'; try 'call me'")(state)
    }
  }

  private def processAudioCommand(friendNumber: ToxFriendNumber, state: ToxClientState, request: String): ToxClientState = {
    val newAudio = request match {
      case "itcrowd"      => AudioGenerator.ItCrowd(audioSamplingRate.value)
      case "mortalkombat" => AudioGenerator.MortalKombat(audioSamplingRate.value)
      case "songofstorms" => AudioGenerator.SongOfStorms(audioSamplingRate.value)
      case _              => AudioGenerator(audioSamplingRate.value)
    }

    val audioTime = ToxClientState.friendAudioTime(friendNumber)
    val audio = ToxClientState.friendAudio(friendNumber)
    audio.set(audioTime.mod(_.map(_ => 0), state), newAudio) |> say(friendNumber, "changing audio track")
  }

  private def processVideoCommand(friendNumber: ToxFriendNumber, state: ToxClientState, request: String): ToxClientState = {
    val video = ToxClientState.friendVideo(friendNumber)
    val videoFrame = ToxClientState.friendVideoFrame(friendNumber)

    val oldVideo = video.get(state)
    val newVideo = selectNewVideo(request, oldVideo)

    video.set(videoFrame.mod(_.map(_ => 0), state), newVideo) |> say(friendNumber, "changing video")
  }

  private def selectNewVideo(request: String, oldVideo: VideoGenerator): VideoGenerator = {
    val ResizeCommand = "size (\\d+)\\s+(\\d+)".r

    request match {
      case ResizeCommand(width, height) => oldVideo.resize(width.toInt, height.toInt)
      case changeVideo                  => selectNewVideo(changeVideo).resize(oldVideo.width, oldVideo.height)
    }
  }

  private def selectNewVideo(changeVideo: String): VideoGenerator = {
    changeVideo match {
      case "xor1"          => VideoGenerators.Xor1
      case "xor2"          => VideoGenerators.Xor2
      case "xor3"          => VideoGenerators.Xor3
      case "xor4"          => VideoGenerators.Xor4
      case "gradientboxes" => VideoGenerators.GradientBoxes
      case "multiplyup"    => VideoGenerators.MultiplyUp
      case "smiley"        => VideoGenerators.Selected
      case _               => VideoGenerators.Selected
    }
  }

  private def processShowCommand(friendNumber: ToxFriendNumber, state: ToxClientState, request: String): ToxClientState = {
    def const(response: String)(tox: ToxCore[ToxClientState]): String = response

    val response: (ToxCore[ToxClientState] => String) = request match {
      case "address" => _.getAddress.toString
      case "dhtid"   => _.getDhtId.toString
      case "udpport" => _.getUdpPort.toString
      case "ipv4"    => const(HostInfo.ipv4.toString)
      case "ipv6"    => const(HostInfo.ipv6.toString)
      case other     => const(s"I don't know about '${other.take(200)}'")
    }

    state.addTask { (tox, av, state) =>
      tox.friendSendMessage(friendNumber, ToxMessageType.NORMAL, 0, ToxFriendMessage.fromString(response(tox)).get)
      state
    }
  }

  override def call(
    friendNumber: ToxFriendNumber,
    audioEnabled: Boolean,
    videoEnabled: Boolean
  )(state: ToxClientState): ToxClientState = {
    state.addTask { (tox, av, state) =>
      logInfo(s"Answering call from $friendNumber")
      av.answer(friendNumber, audioBitRate, videoBitRate)
      av.callControl(friendNumber, ToxavCallControl.MUTE_AUDIO)
      av.callControl(friendNumber, ToxavCallControl.HIDE_VIDEO)

      // val callState = new util.ArrayList[ToxavFriendCallState]
      // if (audioEnabled) callState.add(ToxavFriendCallState.ACCEPTING_A)
      // if (videoEnabled) callState.add(ToxavFriendCallState.ACCEPTING_V)
      // av.invokeCallState(friendNumber, callState)

      state
    }
  }

  private def sendNextAudioFrame(
    friendNumber: ToxFriendNumber
  )(
    tox: ToxCore[ToxClientState],
    av: ToxAv[ToxClientState],
    state: ToxClientState
  ): ToxClientState = {
    val audioTime = ToxClientState.friendAudioTime(friendNumber)

    audioTime.get(state) match {
      case None =>
        state // finished
      case Some(t) =>
        // Get next audio frames and send them.
        val audio = ToxClientState.friendAudio(friendNumber).get(state)
        val nextT = t + audioFrameSize * audioFramesPerIteration

        for (t <- t until nextT by audioFrameSize) {
          val pcm = audio.nextFrame16(t, audioFrameSize)
          av.audioSendFrame(
            friendNumber,
            pcm,
            SampleCount(audioLength, audioSamplingRate),
            AudioChannels.Mono,
            audioSamplingRate
          )
        }
        audioTime.set(state, Some(nextT)).addTask(sendNextAudioFrame(friendNumber))
    }
  }

  private def sendNextVideoFrame(
    friendNumber: ToxFriendNumber
  )(
    tox: ToxCore[ToxClientState],
    av: ToxAv[ToxClientState],
    state: ToxClientState
  ): ToxClientState = {
    val videoFrame = ToxClientState.friendVideoFrame(friendNumber)

    videoFrame.get(state) match {
      case None =>
        state // finished
      case Some(t) =>
        // Get next frame and send it.
        val video = ToxClientState.friendVideo(friendNumber).get(state)
        val (y, u, v) = video.yuv(t)
        av.videoSendFrame(friendNumber, video.width, video.height, y, u, v)
        videoFrame.set(state, Some(t + 1)).addTask(sendNextVideoFrame(friendNumber))
    }
  }

  override def callState(
    friendNumber: ToxFriendNumber,
    callState: util.Collection[ToxavFriendCallState]
  )(state: ToxClientState): ToxClientState = {
    (state
      |> stopAvReceiving(friendNumber, callState)
      |> startStopAudioSending(friendNumber, callState)
      |> startStopVideoSending(friendNumber, callState))
  }

  private def stopAvReceiving(
    friendNumber: ToxFriendNumber,
    callState: util.Collection[ToxavFriendCallState]
  )(state: ToxClientState): ToxClientState = {
    state.addTask { (tox, av, state) =>
      if (callState.contains(ToxavFriendCallState.SENDING_A)) {
        av.callControl(friendNumber, ToxavCallControl.MUTE_AUDIO)
      }
      if (callState.contains(ToxavFriendCallState.SENDING_V)) {
        av.callControl(friendNumber, ToxavCallControl.HIDE_VIDEO)
      }
      state
    }
  }

  private def startStopAudioSending(
    friendNumber: ToxFriendNumber,
    callState: util.Collection[ToxavFriendCallState]
  )(state: ToxClientState): ToxClientState = {
    val audioTime = ToxClientState.friendAudioTime(friendNumber)

    if (callState.contains(ToxavFriendCallState.ACCEPTING_A)) {
      logInfo(s"Sending audio to friend $friendNumber")
      audioTime.set(state, Some(0))
        .addTask(sendNextAudioFrame(friendNumber))
    } else {
      audioTime.mod({
        case None => None
        case Some(_) =>
          logInfo(s"Stopped sending audio to friend $friendNumber")
          None
      }, state)
    }
  }

  private def startStopVideoSending(
    friendNumber: ToxFriendNumber,
    callState: util.Collection[ToxavFriendCallState]
  )(state: ToxClientState): ToxClientState = {
    val videoFrame = ToxClientState.friendVideoFrame(friendNumber)

    if (callState.contains(ToxavFriendCallState.ACCEPTING_V)) {
      logInfo(s"Sending video to friend $friendNumber")
      videoFrame.set(state, Some(0))
        .addTask(sendNextVideoFrame(friendNumber))
    } else {
      videoFrame.mod({
        case None => None
        case Some(_) =>
          logInfo(s"Stopped sending video to friend $friendNumber")
          None
      }, state)
    }

  }

  override def bitRateStatus(
    friendNumber: ToxFriendNumber,
    audioBitRate: BitRate,
    videoBitRate: BitRate
  )(state: ToxClientState): ToxClientState = {
    state.addTask { (tox, av, state) =>
      av.setBitRate(friendNumber, audioBitRate, videoBitRate)

      (state
        |> setAudioBitRate(friendNumber, audioBitRate)
        |> setVideoBitRate(friendNumber, videoBitRate))
    }
  }

  private def setAudioBitRate(
    friendNumber: ToxFriendNumber,
    bitRate: BitRate
  )(state: ToxClientState): ToxClientState = {
    setBitRate(ToxClientState.friendAudioTime(friendNumber), bitRate)(state)
  }

  private def setVideoBitRate(
    friendNumber: ToxFriendNumber,
    bitRate: BitRate
  )(state: ToxClientState): ToxClientState = {
    setBitRate(ToxClientState.friendVideoFrame(friendNumber), bitRate)(state)
  }

  private def setBitRate(
    lens: Lens[ToxClientState, Option[Int]],
    bitRate: BitRate
  )(state: ToxClientState): ToxClientState = {
    if (bitRate == BitRate.Disabled) {
      // Disable audio sending.
      lens.set(state, None)
    } else {
      lens.set(state, Some(0))
    }
  }

  override def audioReceiveFrame(
    friendNumber: ToxFriendNumber,
    pcm: Array[Short],
    channels: AudioChannels,
    samplingRate: SamplingRate
  )(state: ToxClientState): ToxClientState = {
    state
  }

  override def videoReceiveFrame(
    friendNumber: ToxFriendNumber,
    width: Int, height: Int,
    y: Array[Byte], u: Array[Byte], v: Array[Byte],
    yStride: Int, uStride: Int, vStride: Int
  )(state: ToxClientState): ToxClientState = {
    state
  }

}
