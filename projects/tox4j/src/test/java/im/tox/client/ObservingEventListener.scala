package im.tox.client

import java.util

import im.tox.tox4j.{ToxEventAdapter, ToxEventListener}
import im.tox.tox4j.av.data.{AudioChannels, BitRate, SamplingRate}
import im.tox.tox4j.av.enums.ToxavFriendCallState
import im.tox.tox4j.core.data._
import im.tox.tox4j.core.enums.{ToxConnection, ToxFileControl, ToxMessageType, ToxUserStatus}

final class ObservingEventListener[ToxCoreState](next: ToxEventListener[ToxCoreState], observers: ToxEventListener[Unit]*)
    extends ToxEventAdapter[ToxCoreState] {

  override def friendStatus(
    friendNumber: Int,
    status: ToxUserStatus
  )(state: ToxCoreState): ToxCoreState = {
    observers.foreach(_.friendStatus(friendNumber, status)(()))
    next.friendStatus(friendNumber, status)(state)
  }

  override def friendTyping(
    friendNumber: Int,
    isTyping: Boolean
  )(state: ToxCoreState): ToxCoreState = {
    observers.foreach(_.friendTyping(friendNumber, isTyping)(()))
    next.friendTyping(friendNumber, isTyping)(state)
  }

  override def selfConnectionStatus(
    connectionStatus: ToxConnection
  )(state: ToxCoreState): ToxCoreState = {
    observers.foreach(_.selfConnectionStatus(connectionStatus)(()))
    next.selfConnectionStatus(connectionStatus)(state)
  }

  override def friendName(
    friendNumber: Int,
    name: ToxNickname
  )(state: ToxCoreState): ToxCoreState = {
    observers.foreach(_.friendName(friendNumber, name)(()))
    next.friendName(friendNumber, name)(state)
  }

  override def friendMessage(
    friendNumber: Int,
    messageType: ToxMessageType,
    timeDelta: Int,
    message: ToxFriendMessage
  )(state: ToxCoreState): ToxCoreState = {
    observers.foreach(_.friendMessage(friendNumber, messageType, timeDelta, message)(()))
    next.friendMessage(friendNumber, messageType, timeDelta, message)(state)
  }

  override def friendLossyPacket(
    friendNumber: Int,
    data: ToxLossyPacket
  )(state: ToxCoreState): ToxCoreState = {
    observers.foreach(_.friendLossyPacket(friendNumber, data)(()))
    next.friendLossyPacket(friendNumber, data)(state)
  }

  override def fileRecv(
    friendNumber: Int,
    fileNumber: Int,
    kind: Int,
    fileSize: Long,
    filename: ToxFilename
  )(state: ToxCoreState): ToxCoreState = {
    observers.foreach(_.fileRecv(friendNumber, fileNumber, kind, fileSize, filename)(()))
    next.fileRecv(friendNumber, fileNumber, kind, fileSize, filename)(state)
  }

  override def friendRequest(
    publicKey: ToxPublicKey,
    timeDelta: Int,
    message: ToxFriendRequestMessage
  )(state: ToxCoreState): ToxCoreState = {
    observers.foreach(_.friendRequest(publicKey, timeDelta, message)(()))
    next.friendRequest(publicKey, timeDelta, message)(state)
  }

  override def fileChunkRequest(
    friendNumber: Int,
    fileNumber: Int,
    position: Long,
    length: Int
  )(state: ToxCoreState): ToxCoreState = {
    observers.foreach(_.fileChunkRequest(friendNumber, fileNumber, position, length)(()))
    next.fileChunkRequest(friendNumber, fileNumber, position, length)(state)
  }

  override def fileRecvChunk(
    friendNumber: Int,
    fileNumber: Int,
    position: Long,
    data: Array[Byte]
  )(state: ToxCoreState): ToxCoreState = {
    observers.foreach(_.fileRecvChunk(friendNumber, fileNumber, position, data)(()))
    next.fileRecvChunk(friendNumber, fileNumber, position, data)(state)
  }

  override def friendLosslessPacket(
    friendNumber: Int,
    data: ToxLosslessPacket
  )(state: ToxCoreState): ToxCoreState = {
    observers.foreach(_.friendLosslessPacket(friendNumber, data)(()))
    next.friendLosslessPacket(friendNumber, data)(state)
  }

  override def friendConnectionStatus(
    friendNumber: Int,
    connectionStatus: ToxConnection
  )(state: ToxCoreState): ToxCoreState = {
    observers.foreach(_.friendConnectionStatus(friendNumber, connectionStatus)(()))
    next.friendConnectionStatus(friendNumber, connectionStatus)(state)
  }

  override def fileRecvControl(
    friendNumber: Int,
    fileNumber: Int,
    control: ToxFileControl
  )(state: ToxCoreState): ToxCoreState = {
    observers.foreach(_.fileRecvControl(friendNumber, fileNumber, control)(()))
    next.fileRecvControl(friendNumber, fileNumber, control)(state)
  }

  override def friendStatusMessage(
    friendNumber: Int,
    message: ToxStatusMessage
  )(state: ToxCoreState): ToxCoreState = {
    observers.foreach(_.friendStatusMessage(friendNumber, message)(()))
    next.friendStatusMessage(friendNumber, message)(state)
  }

  override def friendReadReceipt(
    friendNumber: Int,
    messageId: Int
  )(state: ToxCoreState): ToxCoreState = {
    observers.foreach(_.friendReadReceipt(friendNumber, messageId)(()))
    next.friendReadReceipt(friendNumber, messageId)(state)
  }

  override def call(
    friendNumber: Int,
    audioEnabled: Boolean,
    videoEnabled: Boolean
  )(state: ToxCoreState): ToxCoreState = {
    observers.foreach(_.call(friendNumber, audioEnabled, videoEnabled)(()))
    next.call(friendNumber, audioEnabled, videoEnabled)(state)
  }

  override def callState(
    friendNumber: Int,
    callState: util.Collection[ToxavFriendCallState]
  )(state: ToxCoreState): ToxCoreState = {
    observers.foreach(_.callState(friendNumber, callState)(()))
    next.callState(friendNumber, callState)(state)
  }

  override def bitRateStatus(
    friendNumber: Int,
    audioBitRate: BitRate,
    videoBitRate: BitRate
  )(state: ToxCoreState): ToxCoreState = {
    observers.foreach(_.bitRateStatus(friendNumber, audioBitRate, videoBitRate)(()))
    next.bitRateStatus(friendNumber, audioBitRate, videoBitRate)(state)
  }

  override def audioReceiveFrame(
    friendNumber: Int,
    pcm: Array[Short],
    channels: AudioChannels,
    samplingRate: SamplingRate
  )(state: ToxCoreState): ToxCoreState = {
    observers.foreach(_.audioReceiveFrame(friendNumber, pcm, channels, samplingRate)(()))
    next.audioReceiveFrame(friendNumber, pcm, channels, samplingRate)(state)
  }

  override def videoReceiveFrame(
    friendNumber: Int,
    width: Int, height: Int,
    y: Array[Byte], u: Array[Byte], v: Array[Byte],
    yStride: Int, uStride: Int, vStride: Int
  )(state: ToxCoreState): ToxCoreState = {
    observers.foreach(_.videoReceiveFrame(friendNumber, width, height, y, u, v, yStride, uStride, vStride)(()))
    next.videoReceiveFrame(friendNumber, width, height, y, u, v, yStride, uStride, vStride)(state)
  }

}
