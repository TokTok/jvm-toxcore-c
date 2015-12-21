package im.tox.toktok

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.view.{LayoutInflater, WindowManager}

final case class TypedService[A](id: String)

object TContext {
  val INPUT_METHOD_SERVICE = TypedService[InputMethodManager](Context.INPUT_METHOD_SERVICE)
  val LAYOUT_INFLATER_SERVICE = TypedService[LayoutInflater](Context.LAYOUT_INFLATER_SERVICE)
  val WINDOW_SERVICE = TypedService[WindowManager](Context.WINDOW_SERVICE)
}

trait TypedFindService extends Any {
  protected def getSystemService(id: String): Any
  def getSystemService[A](ts: TypedService[A]): A = getSystemService(ts.id).asInstanceOf[A]
}

object TypedService {
  implicit class TypedContext(val c: Context) extends AnyVal with TypedFindService {
    override def getSystemService(id: String) = c.getSystemService(id)
  }
}
