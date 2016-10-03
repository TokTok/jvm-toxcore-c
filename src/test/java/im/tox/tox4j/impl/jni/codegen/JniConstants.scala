package im.tox.tox4j.impl.jni.codegen

import im.tox.tox4j.core.ToxCoreConstants
import im.tox.tox4j.crypto.ToxCryptoConstants
import im.tox.tox4j.impl.jni.codegen.NameConversions.cxxTypeName
import im.tox.tox4j.impl.jni.codegen.cxx.Ast._

object JniConstants extends CodeGenerator {

  def generateNativeDecls[T](prefix: String, constants: T): Decl = {
    val clazz = constants.getClass

    Fun(
      returnType = Type.void,
      name = "check" + clazz.getSimpleName.replace("$", ""),
      params = Seq(),
      body = CompoundStmt(
        body = clazz.getDeclaredMethods
          .sortBy(method => method.getName)
          .map { method =>
            val value = method.invoke(constants).asInstanceOf[Int]
            ExprStmt(FunCall(Identifier("static_assert"), Seq(
              Equals(Identifier(prefix + cxxTypeName(method.getName)), IntegerLiteral(value)),
              StringLiteral("Java constant out of sync with C")
            )))
          }.toSeq
      )
    )
  }

  writeCode("ToxCore/generated/constants.h", "\n") {
    Seq(
      Comment(ToxCoreConstants.getClass.getName),
      generateNativeDecls("TOX_", ToxCoreConstants)
    )
  }

  writeCode("ToxCrypto/generated/constants.h", "\n") {
    Seq(
      Comment(ToxCryptoConstants.getClass.getName),
      generateNativeDecls("TOX_PASS_", ToxCryptoConstants)
    )
  }

}
