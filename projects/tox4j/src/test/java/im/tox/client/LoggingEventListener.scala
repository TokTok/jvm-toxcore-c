package im.tox.client

import java.util

import im.tox.tox4j.ToxEventListener
import im.tox.tox4j.av.data.{AudioChannels, BitRate, SamplingRate}
import im.tox.tox4j.av.enums.ToxavFriendCallState
import im.tox.tox4j.core.data._
import im.tox.tox4j.core.enums._

final class LoggingEventListener(id: Int)
    extends IdLogging(id) with ToxEventListener[Unit] {

  override def friendStatus(
    friendNumber: ToxFriendNumber,
    status: ToxUserStatus
  )(state: Unit): Unit = {
    logInfo(s"friendStatus($friendNumber, $status)")
  }

  override def friendTyping(
    friendNumber: ToxFriendNumber,
    isTyping: Boolean
  )(state: Unit): Unit = {
    logInfo(s"friendTyping($friendNumber, $isTyping)")
  }

  override def selfConnectionStatus(
    connectionStatus: ToxConnection
  )(state: Unit): Unit = {
    logInfo(s"selfConnectionStatus($connectionStatus)")
  }

  override def friendName(
    friendNumber: ToxFriendNumber,
    name: ToxNickname
  )(state: Unit): Unit = {
    logInfo(s"friendName($friendNumber, ${new String(name.value)})")
  }

  override def friendMessage(
    friendNumber: ToxFriendNumber,
    messageType: ToxMessageType,
    timeDelta: Int,
    message: ToxFriendMessage
  )(state: Unit): Unit = {
    logInfo(s"friendMessage($friendNumber, $messageType, $timeDelta, ${new String(message.value)})")
  }

  override def friendLossyPacket(
    friendNumber: ToxFriendNumber,
    data: ToxLossyPacket
  )(state: Unit): Unit = {
    logInfo(s"friendLossyPacket($friendNumber, ${new String(data.value)})")
  }

  override def fileRecv(
    friendNumber: ToxFriendNumber,
    fileNumber: Int,
    kind: Int,
    fileSize: Long,
    filename: ToxFilename
  )(state: Unit): Unit = {
    if (kind == ToxFileKind.AVATAR) {
      logInfo(s"fileRecv($friendNumber, $fileNumber, $kind, $fileSize, ${filename.toHexString})")
    } else {
      logInfo(s"fileRecv($friendNumber, $fileNumber, $kind, $fileSize, ${new String(filename.value)})")
    }
  }

  override def friendRequest(
    publicKey: ToxPublicKey,
    timeDelta: Int,
    message: ToxFriendRequestMessage
  )(state: Unit): Unit = {
    logInfo(s"friendRequest($publicKey, $timeDelta, ${new String(message.value)})")
  }

  override def fileChunkRequest(
    friendNumber: ToxFriendNumber,
    fileNumber: Int,
    position: Long,
    length: Int
  )(state: Unit): Unit = {
    logInfo(s"fileChunkRequest($friendNumber, $fileNumber, $position, $length)")
  }

  override def fileRecvChunk(
    friendNumber: ToxFriendNumber,
    fileNumber: Int,
    position: Long,
    data: Array[Byte]
  )(state: Unit): Unit = {
    logInfo(s"fileRecvChunk($friendNumber, $fileNumber, $position, ${new String(data)})")
  }

  override def friendLosslessPacket(
    friendNumber: ToxFriendNumber,
    data: ToxLosslessPacket
  )(state: Unit): Unit = {
    logInfo(s"friendLosslessPacket($friendNumber, ${new String(data.value)})")
  }

  override def friendConnectionStatus(
    friendNumber: ToxFriendNumber,
    connectionStatus: ToxConnection
  )(state: Unit): Unit = {
    logInfo(s"friendConnectionStatus($friendNumber, $connectionStatus)")
  }

  override def fileRecvControl(
    friendNumber: ToxFriendNumber,
    fileNumber: Int,
    control: ToxFileControl
  )(state: Unit): Unit = {
    logInfo(s"fileRecvControl($friendNumber, $fileNumber, $control)")
  }

  override def friendStatusMessage(
    friendNumber: ToxFriendNumber,
    message: ToxStatusMessage
  )(state: Unit): Unit = {
    logInfo(s"friendStatusMessage($friendNumber, ${new String(message.value)})")
  }

  override def friendReadReceipt(
    friendNumber: ToxFriendNumber,
    messageId: Int
  )(state: Unit): Unit = {
    logInfo(s"friendReadReceipt($friendNumber, $messageId)")
  }

  override def call(
    friendNumber: ToxFriendNumber,
    audioEnabled: Boolean,
    videoEnabled: Boolean
  )(state: Unit): Unit = {
    logInfo(s"call($friendNumber, $audioEnabled, $videoEnabled)")
  }

  override def callState(
    friendNumber: ToxFriendNumber,
    callState: util.Collection[ToxavFriendCallState]
  )(state: Unit): Unit = {
    logInfo(s"callState($friendNumber, $callState)")
  }

  override def bitRateStatus(
    friendNumber: ToxFriendNumber,
    audioBitRate: BitRate,
    videoBitRate: BitRate
  )(state: Unit): Unit = {
    logInfo(s"bitRateStatus($friendNumber, $audioBitRate, $videoBitRate)")
  }

  override def audioReceiveFrame(
    friendNumber: ToxFriendNumber,
    pcm: Array[Short],
    channels: AudioChannels,
    samplingRate: SamplingRate
  )(state: Unit): Unit = {
    logInfo(s"audioReceiveFrame($friendNumber, short[${pcm.length}], $channels, $samplingRate)")
  }

  override def videoReceiveFrame(
    friendNumber: ToxFriendNumber,
    width: Int, height: Int,
    y: Array[Byte], u: Array[Byte], v: Array[Byte],
    yStride: Int, uStride: Int, vStride: Int
  )(state: Unit): Unit = {
    logInfo(
      s"videoReceiveFrame($friendNumber, $width, $height, " +
        s"byte[${y.length}], byte[${u.length}], byte[${v.length}], " +
        s"$yStride, $uStride, $vStride)"
    )
  }

}
