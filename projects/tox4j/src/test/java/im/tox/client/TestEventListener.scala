package im.tox.client

import java.util

import com.typesafe.scalalogging.Logger
import im.tox.tox4j.ToxEventAdapter
import im.tox.tox4j.av.ToxAv
import im.tox.tox4j.av.callbacks.video.VideoGenerators
import im.tox.tox4j.av.data.{AudioChannels, BitRate, SamplingRate}
import im.tox.tox4j.av.enums.{ToxavCallControl, ToxavFriendCallState}
import im.tox.tox4j.core.ToxCore
import im.tox.tox4j.core.data._
import im.tox.tox4j.core.enums.{ToxConnection, ToxFileControl, ToxMessageType, ToxUserStatus}
import org.slf4j.LoggerFactory

final class TestEventListener(id: Int) extends ToxEventAdapter[TestState] {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

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

  override def friendMessage(friendNumber: Int, messageType: ToxMessageType, timeDelta: Int, message: ToxFriendMessage)(state: TestState): TestState = {
    val command = message.toString.toLowerCase
    command match {
      case "start video" =>
        state.addTask { (tox, av, state) =>
          logger.info(s"Ringing ${state.friends(friendNumber).name}")
          av.call(friendNumber, BitRate.Disabled, BitRate(1))
          state
        }

      case _ =>
        val newVideo = command match {
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
        video.set(videoFrame.mod(_.map(_ => 0), state), newVideo)
    }
  }

  override def friendLossyPacket(friendNumber: Int, data: ToxLossyPacket)(state: TestState): TestState = {
    state
  }

  override def fileRecv(friendNumber: Int, fileNumber: Int, kind: Int, fileSize: Long, filename: ToxFilename)(state: TestState): TestState = {
    state
  }

  override def friendRequest(publicKey: ToxPublicKey, timeDelta: Int, message: ToxFriendRequestMessage)(state: TestState): TestState = {
    state.addTask { (tox, av, state) =>
      logger.info(s"Adding $publicKey as friend")
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
      av.answer(friendNumber, BitRate.Disabled, BitRate(1))
      av.callControl(friendNumber, ToxavCallControl.MUTE_AUDIO)
      av.callControl(friendNumber, ToxavCallControl.HIDE_VIDEO)
      state
    }
  }

  private def sendNextFrame(friendNumber: Int)(tox: ToxCore[TestState], av: ToxAv[TestState], state: TestState): TestState = {
    val videoFrame = TestState.friendVideoFrame(friendNumber)

    videoFrame.get(state) match {
      case None =>
        state // finished
      case Some(t) =>
        // Get next frame and send it.
        val video = TestState.friendVideo(friendNumber).get(state)
        val (y, u, v) = video.yuv(t)
        av.videoSendFrame(friendNumber, video.width, video.height, y, u, v)
        videoFrame.set(state, Some(t + 1)).addTask(sendNextFrame(friendNumber))
    }
  }

  override def callState(friendNumber: Int, callState: util.Collection[ToxavFriendCallState])(state: TestState): TestState = {
    val videoFrame = TestState.friendVideoFrame(friendNumber)

    if (callState.contains(ToxavFriendCallState.ACCEPTING_V)) {
      logger.debug(s"Sending video to friend $friendNumber")
      videoFrame.set(state, Some(0)).addTask(sendNextFrame(friendNumber))
    } else {
      videoFrame.set(state, None)
    }
  }

  override def bitRateStatus(friendNumber: Int, audioBitRate: BitRate, videoBitRate: BitRate)(state: TestState): TestState = {
    state.addTask { (tox, av, state) =>
      av.setBitRate(friendNumber, audioBitRate, videoBitRate)

      val videoFrame = TestState.friendVideoFrame(friendNumber)
      if (videoBitRate == BitRate.Disabled) {
        // Disable video sending.
        videoFrame.set(state, None)
      } else {
        videoFrame.set(state, Some(0))
      }
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
