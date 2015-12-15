package im.tox.client.callbacks

import java.util

import im.tox.tox4j.av.data.{AudioChannels, BitRate, SamplingRate}
import im.tox.tox4j.av.enums.ToxavFriendCallState
import im.tox.tox4j.core.data._
import im.tox.tox4j.core.enums.{ToxConnection, ToxFileControl, ToxMessageType, ToxUserStatus}
import im.tox.tox4j.{ToxEventAdapter, ToxEventListener}

final class MappingEventListener[ToxCoreState](mappers: ToxEventListener[ToxCoreState]*)
    extends ToxEventAdapter[ToxCoreState] {

  override def friendStatus(
    friendNumber: ToxFriendNumber,
    status: ToxUserStatus
  )(state: ToxCoreState): ToxCoreState = {
    mappers.foldRight(state)(_.friendStatus(friendNumber, status)(_))
  }

  override def friendTyping(
    friendNumber: ToxFriendNumber,
    isTyping: Boolean
  )(state: ToxCoreState): ToxCoreState = {
    mappers.foldRight(state)(_.friendTyping(friendNumber, isTyping)(_))
  }

  override def selfConnectionStatus(
    connectionStatus: ToxConnection
  )(state: ToxCoreState): ToxCoreState = {
    mappers.foldRight(state)(_.selfConnectionStatus(connectionStatus)(_))
  }

  override def friendName(
    friendNumber: ToxFriendNumber,
    name: ToxNickname
  )(state: ToxCoreState): ToxCoreState = {
    mappers.foldRight(state)(_.friendName(friendNumber, name)(_))
  }

  override def friendMessage(
    friendNumber: ToxFriendNumber,
    messageType: ToxMessageType,
    timeDelta: Int,
    message: ToxFriendMessage
  )(state: ToxCoreState): ToxCoreState = {
    mappers.foldRight(state)(_.friendMessage(friendNumber, messageType, timeDelta, message)(_))
  }

  override def friendLossyPacket(
    friendNumber: ToxFriendNumber,
    data: ToxLossyPacket
  )(state: ToxCoreState): ToxCoreState = {
    mappers.foldRight(state)(_.friendLossyPacket(friendNumber, data)(_))
  }

  override def fileRecv(
    friendNumber: ToxFriendNumber,
    fileNumber: Int,
    kind: Int,
    fileSize: Long,
    filename: ToxFilename
  )(state: ToxCoreState): ToxCoreState = {
    mappers.foldRight(state)(_.fileRecv(friendNumber, fileNumber, kind, fileSize, filename)(_))
  }

  override def friendRequest(
    publicKey: ToxPublicKey,
    timeDelta: Int,
    message: ToxFriendRequestMessage
  )(state: ToxCoreState): ToxCoreState = {
    mappers.foldRight(state)(_.friendRequest(publicKey, timeDelta, message)(_))
  }

  override def fileChunkRequest(
    friendNumber: ToxFriendNumber,
    fileNumber: Int,
    position: Long,
    length: Int
  )(state: ToxCoreState): ToxCoreState = {
    mappers.foldRight(state)(_.fileChunkRequest(friendNumber, fileNumber, position, length)(_))
  }

  override def fileRecvChunk(
    friendNumber: ToxFriendNumber,
    fileNumber: Int,
    position: Long,
    data: Array[Byte]
  )(state: ToxCoreState): ToxCoreState = {
    mappers.foldRight(state)(_.fileRecvChunk(friendNumber, fileNumber, position, data)(_))
  }

  override def friendLosslessPacket(
    friendNumber: ToxFriendNumber,
    data: ToxLosslessPacket
  )(state: ToxCoreState): ToxCoreState = {
    mappers.foldRight(state)(_.friendLosslessPacket(friendNumber, data)(_))
  }

  override def friendConnectionStatus(
    friendNumber: ToxFriendNumber,
    connectionStatus: ToxConnection
  )(state: ToxCoreState): ToxCoreState = {
    mappers.foldRight(state)(_.friendConnectionStatus(friendNumber, connectionStatus)(_))
  }

  override def fileRecvControl(
    friendNumber: ToxFriendNumber,
    fileNumber: Int,
    control: ToxFileControl
  )(state: ToxCoreState): ToxCoreState = {
    mappers.foldRight(state)(_.fileRecvControl(friendNumber, fileNumber, control)(_))
  }

  override def friendStatusMessage(
    friendNumber: ToxFriendNumber,
    message: ToxStatusMessage
  )(state: ToxCoreState): ToxCoreState = {
    mappers.foldRight(state)(_.friendStatusMessage(friendNumber, message)(_))
  }

  override def friendReadReceipt(
    friendNumber: ToxFriendNumber,
    messageId: Int
  )(state: ToxCoreState): ToxCoreState = {
    mappers.foldRight(state)(_.friendReadReceipt(friendNumber, messageId)(_))
  }

  override def call(
    friendNumber: ToxFriendNumber,
    audioEnabled: Boolean,
    videoEnabled: Boolean
  )(state: ToxCoreState): ToxCoreState = {
    mappers.foldRight(state)(_.call(friendNumber, audioEnabled, videoEnabled)(_))
  }

  override def callState(
    friendNumber: ToxFriendNumber,
    callState: util.Collection[ToxavFriendCallState]
  )(state: ToxCoreState): ToxCoreState = {
    mappers.foldRight(state)(_.callState(friendNumber, callState)(_))
  }

  override def bitRateStatus(
    friendNumber: ToxFriendNumber,
    audioBitRate: BitRate,
    videoBitRate: BitRate
  )(state: ToxCoreState): ToxCoreState = {
    mappers.foldRight(state)(_.bitRateStatus(friendNumber, audioBitRate, videoBitRate)(_))
  }

  override def audioReceiveFrame(
    friendNumber: ToxFriendNumber,
    pcm: Array[Short],
    channels: AudioChannels,
    samplingRate: SamplingRate
  )(state: ToxCoreState): ToxCoreState = {
    mappers.foldRight(state)(_.audioReceiveFrame(friendNumber, pcm, channels, samplingRate)(_))
  }

  override def videoReceiveFrame(
    friendNumber: ToxFriendNumber,
    width: Int, height: Int,
    y: Array[Byte], u: Array[Byte], v: Array[Byte],
    yStride: Int, uStride: Int, vStride: Int
  )(state: ToxCoreState): ToxCoreState = {
    mappers.foldRight(state)(_.videoReceiveFrame(friendNumber, width, height, y, u, v, yStride, uStride, vStride)(_))
  }

}
