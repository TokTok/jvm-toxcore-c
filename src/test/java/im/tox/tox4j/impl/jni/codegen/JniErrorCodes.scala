package im.tox.tox4j.impl.jni.codegen

import im.tox.tox4j.av.exceptions._
import im.tox.tox4j.core.exceptions._
import im.tox.tox4j.crypto.exceptions._
import im.tox.tox4j.exceptions.JavaOnly
import im.tox.tox4j.impl.jni.codegen.NameConversions.{ cxxTypeName, javaTypeName }
import im.tox.tox4j.impl.jni.codegen.cxx.Ast._

object JniErrorCodes extends CodeGenerator {

  def generateErrorCode[E <: Enum[E]](values: Array[E]): Decl = {
    val exceptionClass = {
      val name = cxxTypeName(values(0).getClass.getEnclosingClass.getSimpleName)
      name.substring(name.indexOf('_') + 1, name.lastIndexOf('_'))
    }

    val javaEnum = values(0).getClass.getSimpleName
    val cxxEnum = cxxTypeName(javaEnum)

    val failureCases = values filter { value =>
      value.getClass.getField(value.name).getAnnotation(classOf[JavaOnly]) == null
    } map { value =>
      FunCall(Identifier("failure_case"), Seq(Identifier(exceptionClass), Identifier(value.name)))
    } map ExprStmt

    MacroFun(
      init = FunCall(
        callee = Identifier("HANDLE"),
        args = Seq(
          StringLiteral(javaTypeName(exceptionClass)),
          Identifier(exceptionClass)
        )
      ),
      body = CompoundStmt(
        Switch(
          cond = Identifier("error"),
          body = CompoundStmt(
            ExprStmt(FunCall(Identifier("success_case"), Seq(Identifier(exceptionClass)))) +:
              failureCases
          )
        ),
        Return(FunCall(Identifier("unhandled"), Nil))
      )
    )
  }

  writeCode("ToxAv/generated/errors.cpp") {
    Include("../ToxAv.h") +:
      Seq(
        generateErrorCode(ToxavAnswerException.Code.values),
        generateErrorCode(ToxavBitRateSetException.Code.values),
        generateErrorCode(ToxavCallControlException.Code.values),
        generateErrorCode(ToxavCallException.Code.values),
        generateErrorCode(ToxavNewException.Code.values),
        generateErrorCode(ToxavSendFrameException.Code.values)
      )
  }

  writeCode("ToxCore/generated/errors.cpp") {
    Include("../ToxCore.h") +:
      Seq(
        generateErrorCode(ToxBootstrapException.Code.values),
        generateErrorCode(ToxFileControlException.Code.values),
        generateErrorCode(ToxFileGetException.Code.values),
        generateErrorCode(ToxFileSeekException.Code.values),
        generateErrorCode(ToxFileSendChunkException.Code.values),
        generateErrorCode(ToxFileSendException.Code.values),
        generateErrorCode(ToxFriendAddException.Code.values),
        generateErrorCode(ToxFriendByPublicKeyException.Code.values),
        generateErrorCode(ToxFriendCustomPacketException.Code.values),
        generateErrorCode(ToxFriendDeleteException.Code.values),
        generateErrorCode(ToxFriendGetPublicKeyException.Code.values),
        generateErrorCode(ToxFriendSendMessageException.Code.values),
        generateErrorCode(ToxGetPortException.Code.values),
        generateErrorCode(ToxNewException.Code.values),
        generateErrorCode(ToxSetInfoException.Code.values),
        generateErrorCode(ToxSetTypingException.Code.values)
      )
  }

  writeCode("ToxCrypto/generated/errors.cpp") {
    Include("../ToxCrypto.h") +:
      Seq(
        generateErrorCode(ToxDecryptionException.Code.values),
        generateErrorCode(ToxEncryptionException.Code.values),
        generateErrorCode(ToxGetSaltException.Code.values),
        generateErrorCode(ToxKeyDerivationException.Code.values)
      )
  }

}
