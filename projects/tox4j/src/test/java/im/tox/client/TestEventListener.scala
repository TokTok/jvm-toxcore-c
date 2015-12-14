package im.tox.client

import java.util

import im.tox.tox4j.OptimisedIdOps._
import im.tox.tox4j.ToxEventListener
import im.tox.tox4j.av.ToxAv
import im.tox.tox4j.av.callbacks.AudioGenerator
import im.tox.tox4j.av.callbacks.video.VideoGenerators
import im.tox.tox4j.av.data._
import im.tox.tox4j.av.enums.{ToxavCallControl, ToxavFriendCallState}
import im.tox.tox4j.av.exceptions.ToxavSendFrameException
import im.tox.tox4j.core.ToxCore
import im.tox.tox4j.core.data._
import im.tox.tox4j.core.enums.{ToxConnection, ToxFileControl, ToxMessageType, ToxUserStatus}
import im.tox.tox4j.testing.GetDisjunction._

final class TestEventListener(id: Int)
    extends IdLogging(id) with ToxEventListener[TestState] {

  private val audioBitRate = BitRate.fromInt(8).get
  private val audioLength = AudioLength.Length60
  private val audioSamplingRate = SamplingRate.Rate8k
  private val audioFrameSize = (audioLength.value.toMillis * audioSamplingRate.value / 1000).toInt
  private val audioFramesPerIteration = 1

  private val videoBitRate = BitRate.fromInt(1).get

  private def updateFriend(friendNumber: Int, state: TestState)(update: Friend => Friend): TestState = {
    val updated = update(state.friends.getOrElse(friendNumber, Friend()))
    state.copy(friends = state.friends + (friendNumber -> updated))
  }

  override def selfConnectionStatus(connectionStatus: ToxConnection)(state: TestState): TestState = {
    state
  }

  override def friendStatus(friendNumber: Int, status: ToxUserStatus)(state: TestState): TestState = {
    updateFriend(friendNumber, state)(_.copy(status = status))
  }

  override def friendTyping(friendNumber: Int, isTyping: Boolean)(state: TestState): TestState = {
    updateFriend(friendNumber, state)(_.copy(typing = isTyping))
  }

  override def friendName(friendNumber: Int, name: ToxNickname)(state: TestState): TestState = {
    updateFriend(friendNumber, state)(_.copy(name = name))
  }

  override def friendStatusMessage(friendNumber: Int, message: ToxStatusMessage)(state: TestState): TestState = {
    updateFriend(friendNumber, state)(_.copy(statusMessage = message))
  }

  override def friendConnectionStatus(friendNumber: Int, connectionStatus: ToxConnection)(state: TestState): TestState = {
    updateFriend(friendNumber, state)(_.copy(connection = connectionStatus))
  }

  private def say(friendNumber: Int, message: String)(state: TestState): TestState = {
    state.addTask { (tox, av, state) =>
      tox.friendSendMessage(friendNumber, ToxMessageType.NORMAL, 0, ToxFriendMessage.fromString(message).get)
      state
    }
  }

  override def friendMessage(friendNumber: Int, messageType: ToxMessageType, timeDelta: Int, message: ToxFriendMessage)(state: TestState): TestState = {
    val AudioCommand = "audio (\\w+)".r
    val VideoCommand = "video (\\w+)".r

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

      case _ =>
        say(friendNumber, s"unrecognised command: '$command'; try 'call me'")(state)
    }
  }

  private def processAudioCommand(friendNumber: Int, state: TestState, request: String): TestState = {
    val newAudio = request match {
      case "itcrowd"      => AudioGenerator.itCrowd(audioSamplingRate.value)
      case "mortalkombat" => AudioGenerator.mortalKombat(audioSamplingRate.value)
      case "songofstorms" => AudioGenerator.songOfStorms(audioSamplingRate.value)
      case _              => AudioGenerator(audioSamplingRate.value)
    }

    val audioTime = TestState.friendAudioTime(friendNumber)
    val audio = TestState.friendAudio(friendNumber)
    audio.set(audioTime.mod(_.map(_ => 0), state), newAudio) |> say(friendNumber, "changing audio track")
  }

  private def processVideoCommand(friendNumber: Int, state: TestState, request: String): TestState = {
    val newVideo = request match {
      case "xor1"          => VideoGenerators.Xor1
      case "xor2"          => VideoGenerators.Xor2
      case "xor3"          => VideoGenerators.Xor3
      case "xor4"          => VideoGenerators.Xor4
      case "gradientboxes" => VideoGenerators.GradientBoxes
      case "multiplyup"    => VideoGenerators.MultiplyUp
      case _               => VideoGenerators.Selected
    }

    val videoFrame = TestState.friendVideoFrame(friendNumber)
    val video = TestState.friendVideo(friendNumber)
    video.set(videoFrame.mod(_.map(_ => 0), state), newVideo) |> say(friendNumber, "changing video")
  }

  override def friendLossyPacket(friendNumber: Int, data: ToxLossyPacket)(state: TestState): TestState = {
    state
  }

  override def fileRecv(friendNumber: Int, fileNumber: Int, kind: Int, fileSize: Long, filename: ToxFilename)(state: TestState): TestState = {
    state
  }

  override def friendRequest(publicKey: ToxPublicKey, timeDelta: Int, message: ToxFriendRequestMessage)(state: TestState): TestState = {
    state.addTask { (tox, av, state) =>
      logInfo(s"Adding $publicKey as friend")
      tox.addFriendNorequest(publicKey)
      state.copy(
        profile = state.profile.addFriendKeys(publicKey.toHexString)
      )
    }
  }

  override def fileChunkRequest(friendNumber: Int, fileNumber: Int, position: Long, length: Int)(state: TestState): TestState = {
    state
  }

  override def fileRecvChunk(friendNumber: Int, fileNumber: Int, position: Long, data: Array[Byte])(state: TestState): TestState = {
    state
  }

  override def friendLosslessPacket(friendNumber: Int, data: ToxLosslessPacket)(state: TestState): TestState = {
    state
  }

  override def fileRecvControl(friendNumber: Int, fileNumber: Int, control: ToxFileControl)(state: TestState): TestState = {
    state
  }

  override def friendReadReceipt(friendNumber: Int, messageId: Int)(state: TestState): TestState = {
    state
  }

  override def call(friendNumber: Int, audioEnabled: Boolean, videoEnabled: Boolean)(state: TestState): TestState = {
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

  private def sendNextAudioFrame(friendNumber: Int)(tox: ToxCore[TestState], av: ToxAv[TestState], state: TestState): TestState = {
    val audioTime = TestState.friendAudioTime(friendNumber)

    audioTime.get(state) match {
      case None =>
        state // finished
      case Some(t) =>
        // Get next audio frames and send them.
        val audio = TestState.friendAudio(friendNumber).get(state)
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

  private def sendNextVideoFrame(friendNumber: Int)(tox: ToxCore[TestState], av: ToxAv[TestState], state: TestState): TestState = {
    val videoFrame = TestState.friendVideoFrame(friendNumber)

    videoFrame.get(state) match {
      case None =>
        state // finished
      case Some(t) =>
        // Get next frame and send it.
        val video = TestState.friendVideo(friendNumber).get(state)
        val (y, u, v) = video.yuv(t)
        av.videoSendFrame(friendNumber, video.width, video.height, y, u, v)
        videoFrame.set(state, Some(t + 1)).addTask(sendNextVideoFrame(friendNumber))
    }
  }

  override def callState(friendNumber: Int, callState: util.Collection[ToxavFriendCallState])(state: TestState): TestState = {
    val audioTime = TestState.friendAudioTime(friendNumber)
    val videoFrame = TestState.friendVideoFrame(friendNumber)

    val stateWithAudio =
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

    val stateWithVideo =
      if (callState.contains(ToxavFriendCallState.ACCEPTING_V)) {
        logInfo(s"Sending video to friend $friendNumber")
        videoFrame.set(stateWithAudio, Some(0))
          .addTask(sendNextVideoFrame(friendNumber))
      } else {
        videoFrame.mod({
          case None => None
          case Some(_) =>
            logInfo(s"Stopped sending video to friend $friendNumber")
            None
        }, state)
      }

    stateWithVideo
  }

  override def bitRateStatus(friendNumber: Int, audioBitRate: BitRate, videoBitRate: BitRate)(state: TestState): TestState = {
    state.addTask { (tox, av, state) =>
      av.setBitRate(friendNumber, audioBitRate, videoBitRate)

      val audioTime = TestState.friendAudioTime(friendNumber)
      val videoFrame = TestState.friendVideoFrame(friendNumber)

      val stateWithAudio =
        if (audioBitRate == BitRate.Disabled) {
          // Disable audio sending.
          audioTime.set(state, None)
        } else {
          audioTime.set(state, Some(0))
        }

      val stateWithVideo =
        if (videoBitRate == BitRate.Disabled) {
          // Disable video sending.
          videoFrame.set(stateWithAudio, None)
        } else {
          videoFrame.set(stateWithAudio, Some(0))
        }

      stateWithVideo
    }
  }

  override def audioReceiveFrame(friendNumber: Int, pcm: Array[Short], channels: AudioChannels, samplingRate: SamplingRate)(state: TestState): TestState = {
    state
  }

  override def videoReceiveFrame(
    friendNumber: Int,
    width: Int, height: Int,
    y: Array[Byte], u: Array[Byte], v: Array[Byte],
    yStride: Int, uStride: Int, vStride: Int
  )(state: TestState): TestState = {
    state
  }

}
