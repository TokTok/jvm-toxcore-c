package im.tox.client

import java.util

import com.typesafe.scalalogging.Logger
import im.tox.tox4j.ToxEventAdapter
import im.tox.tox4j.av.data.{AudioChannels, BitRate, SamplingRate}
import im.tox.tox4j.av.enums.ToxavFriendCallState
import im.tox.tox4j.core.data._
import im.tox.tox4j.core.enums._
import org.slf4j.LoggerFactory

final class LoggingEventListener(id: Int) extends ToxEventAdapter[Unit] {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  private def info(message: String): Unit = {
    logger.info(s"[$id] $message")
  }

  override def friendStatus(friendNumber: Int, status: ToxUserStatus)(state: Unit): Unit = {
    info(s"friendStatus($friendNumber, $status)")
  }

  override def friendTyping(friendNumber: Int, isTyping: Boolean)(state: Unit): Unit = {
    info(s"friendTyping($friendNumber, $isTyping)")
  }

  override def selfConnectionStatus(connectionStatus: ToxConnection)(state: Unit): Unit = {
    info(s"selfConnectionStatus($connectionStatus)")
  }

  override def friendName(friendNumber: Int, name: ToxNickname)(state: Unit): Unit = {
    info(s"friendName($friendNumber, ${new String(name.value)})")
  }

  override def friendMessage(friendNumber: Int, messageType: ToxMessageType, timeDelta: Int, message: ToxFriendMessage)(state: Unit): Unit = {
    info(s"friendMessage($friendNumber, $messageType, $timeDelta, ${new String(message.value)})")
  }

  override def friendLossyPacket(friendNumber: Int, data: ToxLossyPacket)(state: Unit): Unit = {
    info(s"friendLossyPacket($friendNumber, ${new String(data.value)})")
  }

  override def fileRecv(friendNumber: Int, fileNumber: Int, kind: Int, fileSize: Long, filename: ToxFilename)(state: Unit): Unit = {
    if (kind == ToxFileKind.AVATAR) {
      info(s"fileRecv($friendNumber, $fileNumber, $kind, $fileSize, ${filename.toHexString})")
    } else {
      info(s"fileRecv($friendNumber, $fileNumber, $kind, $fileSize, ${new String(filename.value)}})")
    }
  }

  override def friendRequest(publicKey: ToxPublicKey, timeDelta: Int, message: ToxFriendRequestMessage)(state: Unit): Unit = {
    info(s"friendRequest($publicKey, $timeDelta, ${new String(message.value)})")
  }

  override def fileChunkRequest(friendNumber: Int, fileNumber: Int, position: Long, length: Int)(state: Unit): Unit = {
    info(s"fileChunkRequest($friendNumber, $fileNumber, $position, $length)")
  }

  override def fileRecvChunk(friendNumber: Int, fileNumber: Int, position: Long, data: Array[Byte])(state: Unit): Unit = {
    info(s"fileRecvChunk($friendNumber, $fileNumber, $position, ${new String(data)})")
  }

  override def friendLosslessPacket(friendNumber: Int, data: ToxLosslessPacket)(state: Unit): Unit = {
    info(s"friendLosslessPacket($friendNumber, ${new String(data.value)})")
  }

  override def friendConnectionStatus(friendNumber: Int, connectionStatus: ToxConnection)(state: Unit): Unit = {
    info(s"friendConnectionStatus($friendNumber, $connectionStatus)")
  }

  override def fileRecvControl(friendNumber: Int, fileNumber: Int, control: ToxFileControl)(state: Unit): Unit = {
    info(s"fileRecvControl($friendNumber, $fileNumber, $control)")
  }

  override def friendStatusMessage(friendNumber: Int, message: ToxStatusMessage)(state: Unit): Unit = {
    info(s"friendStatusMessage($friendNumber, ${new String(message.value)})")
  }

  override def friendReadReceipt(friendNumber: Int, messageId: Int)(state: Unit): Unit = {
    info(s"friendReadReceipt($friendNumber, $messageId)")
  }

  override def call(friendNumber: Int, audioEnabled: Boolean, videoEnabled: Boolean)(state: Unit): Unit = {
    info(s"call($friendNumber, $audioEnabled, $videoEnabled)")
  }

  override def callState(friendNumber: Int, callState: util.Collection[ToxavFriendCallState])(state: Unit): Unit = {
    info(s"callState($friendNumber, $callState)")
  }

  override def bitRateStatus(friendNumber: Int, audioBitRate: BitRate, videoBitRate: BitRate)(state: Unit): Unit = {
    info(s"bitRateStatus($friendNumber, $audioBitRate, $videoBitRate)")
  }

  override def audioReceiveFrame(friendNumber: Int, pcm: Array[Short], channels: AudioChannels, samplingRate: SamplingRate)(state: Unit): Unit = {
    info(s"audioReceiveFrame($friendNumber, short[${pcm.length}], $channels, $samplingRate)")
  }

  override def videoReceiveFrame(
    friendNumber: Int,
    width: Int, height: Int,
    y: Array[Byte], u: Array[Byte], v: Array[Byte],
    yStride: Int, uStride: Int, vStride: Int
  )(state: Unit): Unit = {
    info(s"videoReceiveFrame($friendNumber, $width, $height, byte[${y.length}], byte[${u.length}], byte[${v.length}], $yStride, $uStride, $vStride)")
  }

}
