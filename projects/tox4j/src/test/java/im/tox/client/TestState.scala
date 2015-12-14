package im.tox.client

import im.tox.client.proto.Profile
import im.tox.tox4j.av.callbacks.AudioGenerator
import im.tox.tox4j.av.callbacks.video.{VideoGenerators, VideoGenerator}
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
  audio: AudioGenerator = AudioGenerator(8000),

  // The frame number for video generators.
  videoFrame: Option[Int] = None,
  // The current video generator for this friend
  video: VideoGenerator = VideoGenerators.Selected
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

final case class TestState(
    // Persistent state.
    profile: Profile = Profile.defaultInstance,
    // Temporary state.
    friends: Map[Int, Friend] = Map.empty,
    // Tasks to run on the next iteration.
    tasks: List[TestClient.Task[TestState]] = Nil
) {

  def addTask(task: TestClient.Task[TestState]): TestState = {
    copy(tasks = task :: tasks)
  }

}

object TestState {

  def friend(friendNumber: Int): Lens[TestState, Friend] = Lens.lensu[TestState, Friend](
    (state, friend) => state.copy(friends = state.friends + (friendNumber -> friend)),
    _.friends(friendNumber)
  )

  def friendAudioTime(friendNumber: Int): Lens[TestState, Option[Int]] = friend(friendNumber) >=> Friend.audioTime
  def friendAudio(friendNumber: Int): Lens[TestState, AudioGenerator] = friend(friendNumber) >=> Friend.audio

  def friendVideoFrame(friendNumber: Int): Lens[TestState, Option[Int]] = friend(friendNumber) >=> Friend.videoFrame
  def friendVideo(friendNumber: Int): Lens[TestState, VideoGenerator] = friend(friendNumber) >=> Friend.video

}
