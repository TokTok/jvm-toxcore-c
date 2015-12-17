package im.tox.client.commands

import im.tox.client.{Say, ToxClientState}
import im.tox.tox4j.OptimisedIdOps._
import im.tox.tox4j.av.callbacks.video.{VideoGenerator, VideoGenerators}
import im.tox.tox4j.av.data.{Height, Width}
import im.tox.tox4j.core.data.ToxFriendNumber

object VideoCommandHandler extends Say {

  def apply(friendNumber: ToxFriendNumber, state: ToxClientState, request: String): ToxClientState = {
    val video = ToxClientState.friendVideo(friendNumber)

    val oldVideo = video.get(state)
    selectNewVideo(request, oldVideo) match {
      case None =>
        showVideoOptions(friendNumber, request)(state)
      case Some(newVideo) =>
        setVideo(friendNumber, newVideo)(state)
    }
  }

  private def selectNewVideo(request: String, oldVideo: VideoGenerator): Option[VideoGenerator] = {
    val ResizeCommand = "size (\\d+)\\s+(\\d+)".r

    request match {
      case ResizeCommand(width, height) => Some(oldVideo.resize(Width.clamp(width.toInt), Height.clamp(height.toInt)))
      case changeVideo                  => VideoGenerators.All.get(changeVideo).map(_(oldVideo.width, oldVideo.height, 0))
    }
  }

  private def setVideo(friendNumber: ToxFriendNumber, newVideo: VideoGenerator)(state: ToxClientState): ToxClientState = {
    val video = ToxClientState.friendVideo(friendNumber)
    val videoFrame = ToxClientState.friendVideoFrame(friendNumber)

    (videoFrame.mod(_.map(_ => 0), state)
      |> (state => video.set(state, newVideo))
      |> say(friendNumber, "Changing video."))
  }

  private def showVideoOptions(friendNumber: ToxFriendNumber, request: String)(state: ToxClientState): ToxClientState = {
    say(friendNumber, s"No such video '$request'. Options: " + VideoGenerators.All.keys.mkString(", "))(state)
  }

}
