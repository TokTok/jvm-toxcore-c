package im.tox.client

import im.tox.tox4j.av.callbacks.audio.{AudioGenerator, AudioGenerators}
import im.tox.tox4j.av.callbacks.video.{VideoGenerator, VideoGenerators}
import im.tox.tox4j.core.data.{ToxNickname, ToxStatusMessage}
import im.tox.tox4j.core.enums.{ToxConnection, ToxUserStatus}

import scalaz.Lens

final case class Friend(
  connection: ToxConnection = ToxConnection.NONE,
  name: ToxNickname = ToxNickname(Array.empty),
  statusMessage: ToxStatusMessage = ToxStatusMessage(Array.empty),
  status: ToxUserStatus = ToxUserStatus.NONE,
  typing: Boolean = false,

  // The 't' argument to audio generators.
  audioTime: Option[Int] = None,
  // The current audio generator for this friend.
  audio: AudioGenerator = AudioGenerators.default,

  // The frame number for video generators.
  videoFrame: Option[Int] = None,
  // The current video generator for this friend
  video: VideoGenerator = VideoGenerators.default
)

object Friend {

  val audioTime = Lens.lensu[Friend, Option[Int]](
    (friend, audioTime) => friend.copy(audioTime = audioTime),
    _.audioTime
  )

  val audio = Lens.lensu[Friend, AudioGenerator](
    (friend, audio) => friend.copy(audio = audio),
    _.audio
  )

  val videoFrame = Lens.lensu[Friend, Option[Int]](
    (friend, videoFrame) => friend.copy(videoFrame = videoFrame),
    _.videoFrame
  )

  val video = Lens.lensu[Friend, VideoGenerator](
    (friend, video) => friend.copy(video = video),
    _.video
  )

}
