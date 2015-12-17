package im.tox.client.commands

import im.tox.client.{Say, ToxClientState}
import im.tox.tox4j.OptimisedIdOps._
import im.tox.tox4j.av.callbacks.AudioGenerator
import im.tox.tox4j.av.data.SamplingRate
import im.tox.tox4j.core.data.ToxFriendNumber

object AudioCommandHandler extends Say {

  def apply(audioSamplingRate: SamplingRate)(friendNumber: ToxFriendNumber, state: ToxClientState, request: String): ToxClientState = {
    val newAudio = request match {
      case "itcrowd"      => AudioGenerator.ItCrowd(audioSamplingRate.value)
      case "mortalkombat" => AudioGenerator.MortalKombat(audioSamplingRate.value)
      case "songofstorms" => AudioGenerator.SongOfStorms(audioSamplingRate.value)
      case _              => AudioGenerator(audioSamplingRate.value)
    }

    val audioTime = ToxClientState.friendAudioTime(friendNumber)
    val audio = ToxClientState.friendAudio(friendNumber)
    audio.set(audioTime.mod(_.map(_ => 0), state), newAudio) |> say(friendNumber, "changing audio track")
  }

}
