package im.tox.tox4j.impl.jni.codegen

import java.io.{ File, PrintWriter }

import com.google.common.base.CaseFormat
import gnieh.pp.PrettyRenderer
import im.tox.tox4j.impl.jni.codegen.cxx.Ast._
import im.tox.tox4j.impl.jni.codegen.cxx.{ Ast, Print }

object NameConversions {

  def cxxVarName(name: String): String = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name)
  def cxxTypeName(name: String): String = CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, name)
  def javaVarName(name: String): String = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name)
  def javaTypeName(name: String): String = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name)

}

abstract class CodeGenerator extends App {

  def writeCode(path: String, sep: String = "\n\n")(code: Ast.TranslationUnit): Unit = {
    val renderer = new PrettyRenderer(130)

    val writer = new PrintWriter(new File("src/main/cpp", path))
    try {
      writer.println(code.map(Print.printDecl).map(renderer).mkString(sep))
    } finally {
      writer.close()
    }
  }

  def ifdef(header: String, guard: String, code: TranslationUnit*): TranslationUnit = {
    Include(header) +:
      Ifdef(guard) +:
      code.flatten :+
      Endif
  }

}
