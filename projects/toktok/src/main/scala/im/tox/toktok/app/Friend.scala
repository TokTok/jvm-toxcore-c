package im.tox.toktok.app

import android.graphics.Color
import im.tox.toktok.R

final case class Friend(
  id: Int,
  userName: String,
  userMessage: String,
  userStatus: Int,
  color: Int,
  secondColor: Int,
  photoReference: Int
)

object Friend {

  val lorem = Friend(1, "Lorem Ipsum", "Trying to TokTok", 0, Color.parseColor("#E91E63"), Color.parseColor("#C2185B"), R.drawable.lorem)
  val john = Friend(2, "John Doe", "Up!", 0, Color.parseColor("#3F51B5"), Color.parseColor("#303F9F"), R.drawable.john)
  val jane = Friend(3, "Jane Norman", "New Photo!", 0, Color.parseColor("#CDDC39"), Color.parseColor("#AFB42B"), R.drawable.jane)
  val bart = Friend(4, "Bart Simpson", "In vacation \uD83D\uDEA2", 0, Color.parseColor("#FF9800"), Color.parseColor("#F57C00"), R.drawable.bart)

}
