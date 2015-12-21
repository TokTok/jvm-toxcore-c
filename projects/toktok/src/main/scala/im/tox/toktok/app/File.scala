package im.tox.toktok.app

final case class File(
  name: String,
  date: String
)

object File {
  val file = File("Movie_2015-02-01.mp4", "Downloaded 2015-05-21")
}
