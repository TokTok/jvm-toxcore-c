package im.tox.tox4j.impl.jni.codegen

import im.tox.tox4j.av.callbacks.ToxAvEventAdapter
import im.tox.tox4j.core.callbacks.ToxCoreEventAdapter
import im.tox.tox4j.impl.jni.codegen.NameConversions.{cxxTypeName, cxxVarName, javaVarName}
import im.tox.tox4j.impl.jni.codegen.cxx.Ast._

import scala.util.{Failure, Success, Try}

object JniCallbacks extends CodeGenerator {

  def generateCallbacks(clazz: Class[_]): TranslationUnit = {
    clazz.getDeclaredMethods
      .sortBy(method => method.getName)
      .flatMap { method =>
        val baseType = clazz.getInterfaces.toList.flatMap { interface =>
          Try(interface.getMethod(method.getName, method.getParameterTypes: _*)) match {
            case Failure(_)          => Nil
            case Success(baseMethod) => List(baseMethod.getDeclaringClass): List[Class[_]]
          }
        } match {
          case interface :: Nil =>
            interface
          case multiple =>
            val names = multiple.map(_.getSimpleName).mkString(", ")
            sys.error(s"Multiple callback interfaces provide the same method '${method.getName}': $names")
        }

        val expectedMethodName = {
          val name = cxxTypeName(baseType.getSimpleName)
          javaVarName(name.substring(0, name.lastIndexOf('_')).toLowerCase)
        }

        assert(
          method.getName == expectedMethodName,
          s"Method name ${method.getName} does not match its interface name ${baseType.getName}."
        )

        Seq(
          Comment(baseType.getName + "#" + expectedMethodName),
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
