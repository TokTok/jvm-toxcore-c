package im.tox.client

import im.tox.client.proto.Profile
import im.tox.tox4j.av.ToxAv
import im.tox.tox4j.av.callbacks.AudioGenerator
import im.tox.tox4j.av.callbacks.video.VideoGenerator
import im.tox.tox4j.core.ToxCore
import im.tox.tox4j.core.data.ToxFriendNumber

import scalaz.Lens

final case class ToxClientState(
    // Persistent state.
    profile: Profile = Profile.defaultInstance,
    // Temporary state.
    friends: Map[ToxFriendNumber, Friend] = Map.empty,
    // Tasks to run on the next iteration.
    tasks: List[ToxClientState.Task[ToxClientState]] = Nil
) {

  def addTask(task: ToxClientState.Task[ToxClientState]): ToxClientState = {
    copy(tasks = task :: tasks)
  }

}

object ToxClientState {

  type Task[S] = (ToxCore[S], ToxAv[S], S) => S

  def friend(friendNumber: ToxFriendNumber): Lens[ToxClientState, Friend] = Lens.lensu[ToxClientState, Friend](
    (state, friend) => state.copy(friends = state.friends + (friendNumber -> friend)),
    _.friends(friendNumber)
  )

  def friendAudioTime(friendNumber: ToxFriendNumber): Lens[ToxClientState, Option[Int]] = friend(friendNumber) >=> Friend.audioTime
  def friendAudio(friendNumber: ToxFriendNumber): Lens[ToxClientState, AudioGenerator] = friend(friendNumber) >=> Friend.audio

  def friendVideoFrame(friendNumber: ToxFriendNumber): Lens[ToxClientState, Option[Int]] = friend(friendNumber) >=> Friend.videoFrame
  def friendVideo(friendNumber: ToxFriendNumber): Lens[ToxClientState, VideoGenerator] = friend(friendNumber) >=> Friend.video

}
