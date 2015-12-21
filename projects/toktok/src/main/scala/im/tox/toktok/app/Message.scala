package im.tox.toktok.app

final case class Message(
  msgType: Int,
  msgContent: String,
  msgDetails: String,
  imageSrc: Int
)
