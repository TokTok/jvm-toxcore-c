package im.tox.tox4j.impl.jni.codegen

import im.tox.tox4j.av.callbacks.ToxAvEventAdapter
import im.tox.tox4j.core.callbacks.ToxCoreEventAdapter
import im.tox.tox4j.impl.jni.codegen.NameConversions.{cxxTypeName, cxxVarName, javaVarName}
import im.tox.tox4j.impl.jni.codegen.cxx.Ast._

object JniCallbacks extends CodeGenerator {

  def getAllInterfaces(clazz: Class[_]): List[Class[_]] = {
    (Option(clazz.getSuperclass).toList.flatMap(getAllInterfaces)
      ++ clazz.getInterfaces
      ++ clazz.getInterfaces.flatMap(getAllInterfaces))
  }

  def generateCallbacks(clazz: Class[_]): TranslationUnit = {
    getAllInterfaces(clazz).filter(_.getSimpleName.endsWith("Callback"))
      .sortBy(_.getSimpleName)
      .flatMap { interface =>
        val expectedMethodName = {
          val name = cxxTypeName(interface.getSimpleName)
          javaVarName(name.substring(0, name.lastIndexOf('_')).toLowerCase)
        }

        val method = interface.getDeclaredMethods.filter(_.getName == expectedMethodName) match {
          case Array()             => sys.error(s"Callback interfaces $interface does not provide a method '$expectedMethodName'")
          case Array(singleMethod) => singleMethod
          case methods             => sys.error(s"Callback interfaces $interface contains multiple overloads for '$expectedMethodName'")
        }

        Seq(
          Comment(interface.getName + "#" + expectedMethodName),
          MacroCall(FunCall(Identifier("CALLBACK"), Seq(Identifier(cxxVarName(method.getName)))))
        )
      }
  }

  writeCode("tox/generated/av.h", "\n") {
    generateCallbacks(classOf[ToxAvEventAdapter[_]])
  }

  writeCode("tox/generated/core.h", "\n") {
    generateCallbacks(classOf[ToxCoreEventAdapter[_]])
  }

}
