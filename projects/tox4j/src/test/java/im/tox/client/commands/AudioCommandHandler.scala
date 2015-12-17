package im.tox.client.commands

import im.tox.client.{Say, ToxClientState}
import im.tox.tox4j.OptimisedIdOps._
import im.tox.tox4j.av.callbacks.audio.AudioGenerators
import im.tox.tox4j.core.data.ToxFriendNumber

object AudioCommandHandler extends Say {

  val Pattern = "audio\\s+(.+)".r

  def apply(
    friendNumber: ToxFriendNumber,
    state: ToxClientState,
    request: String
  ): ToxClientState = {
    val newAudio = request match {
      case "itcrowd"      => AudioGenerators.ItCrowd
      case "mortalkombat" => AudioGenerators.MortalKombat
      case "songofstorms" => AudioGenerators.SongOfStorms
      case _              => AudioGenerators.default
    }

    val audioTime = ToxClientState.friendAudioTime(friendNumber)
    val audio = ToxClientState.friendAudio(friendNumber)
    audio.set(audioTime.mod(_.map(_ => 0), state), newAudio) |> say(friendNumber, "changing audio track")
  }

}
