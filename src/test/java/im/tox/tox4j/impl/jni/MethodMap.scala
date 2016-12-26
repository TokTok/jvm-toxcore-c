package im.tox.tox4j.impl.jni

import java.lang.reflect.{ Method, Modifier }

import im.tox.tox4j.impl.jni.codegen.NameConversions

import scalaz.Scalaz._

object MethodMap {

  private val actions = Seq("get", "set", "add", "delete")

  private def removeSelf(name: Seq[String]): Seq[String] = {
    name.headOption match {
      case Some("self") => name.drop(1)
      case _ => name
    }
  }

  private def moveActionToFront(name: Seq[String]): Seq[String] = {
    name.indexWhere(actions.contains) match {
      case -1 =>
        name
      case actionIndex =>
        name(actionIndex) +: (name.slice(0, actionIndex) ++ name.slice(actionIndex + 1, name.length))
    }
  }

  def apply(jniClass: Class[_]): Map[String, Method] = {
    jniClass
      .getDeclaredMethods.toSeq
      .filter { method =>
        Modifier.isNative(method.getModifiers) &&
          method.getName.startsWith("tox")
      }
      .map { method =>
        val expectedName = (method.getName
          |> NameConversions.cxxVarName
          |> (_.split("_").toSeq.drop(1))
          |> removeSelf
          |> moveActionToFront
          |> (_.mkString("_"))
          |> NameConversions.javaVarName)
        (expectedName, method)
      } |> { pairs => Map(pairs: _*) }
  }

}
