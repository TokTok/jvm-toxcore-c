package im.tox.tox4j.impl.jni.codegen

import com.google.common.base.CaseFormat
import im.tox.tox4j.av.enums.{ToxavCallControl, ToxavFriendCallState}
import im.tox.tox4j.core.enums._
import im.tox.tox4j.impl.jni.codegen.cxx.Ast._

object JniEnums extends CodeGenerator {

  final case class Side[E <: Enum[E]](
    ty: Type,
    name: String,
    origin: String,
    expr: E => Expr
  )

  def enumMapping[E <: Enum[E]](values: Seq[E], cxxEnum: String, from: Side[E], to: Side[E]): Decl = {
    TemplateFun(
      typeParams = Nil,
      typeArgs = Seq(Typename(cxxEnum)),
      fun = Fun(
        returnType = to.ty,
        name = "Enum::" + to.name,
        params = Seq(
          Param(Pointer(Typename("JNIEnv")), "env"),
          Param(from.ty, from.name)
        ),
        body = CompoundStmt(
          Switch(Identifier(from.name), CompoundStmt(values.map { value =>
            Case(from.expr(value), Return(to.expr(value)))
          })),
          ExprStmt(FunCall(Identifier("tox4j_fatal"), Seq(StringLiteral(s"Invalid enumerator from ${from.origin}"))))
        )
      )
    )
  }

  def enumMappings[E <: Enum[E]](values: Seq[E], cxxEnum: String): TranslationUnit = {
    val from = Side[E](Typename(cxxEnum), "valueOf", "toxcore", value => Identifier(cxxEnum + "_" + value.name))
    val to = Side[E](Type.jint, "ordinal", "Java", value => IntegerLiteral(value.ordinal))

    Seq(
      enumMapping(values, cxxEnum, from, to),
      enumMapping(values, cxxEnum, to, from)
    )
  }

  def debugOut(expr: Expr): Stmt = {
    ExprStmt(
      FunCall(
        Access(Identifier("value"), "set_v_string"),
        Seq(expr)
      )
    )
  }

  def printArg[E <: Enum[E]](values: Seq[E], cxxEnum: String): Decl = {
    TemplateFun(
      typeParams = Nil,
      typeArgs = Seq(Typename(cxxEnum)),
      fun = Fun(
        returnType = Type.void,
        name = "print_arg",
        params = Seq(
          Param(Reference(Typename("protolog::Value")), "value"),
          Param(Typename(cxxEnum), "arg")
        ),
        body = CompoundStmt(
          Switch(Identifier("arg"), CompoundStmt(values.flatMap { value =>
            Seq(Oneliner(
              Case(
                Identifier(cxxEnum + "_" + value.name),
                debugOut(StringLiteral(cxxEnum + "_" + value.name))
              ),
              Return()
            ))
          })),
          debugOut(BinaryOperator(
            "+",
            StringLiteral(s"($cxxEnum)"),
            Identifier("std::to_string (arg)")
          ))
        )
      )
    )
  }

  def generateEnumConversions[E <: Enum[E]](values: Array[E]): TranslationUnit = {
    val javaEnum = values(0).getClass.getSimpleName
    val cxxEnum = CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, javaEnum)

    enumMappings(values, cxxEnum) :+ printArg(values, cxxEnum)
  }

  writeCode("ToxAv/generated/enums.cpp") {
    ifdef(
      "../ToxAv.h",
      "TOXAV_VERSION_MAJOR",
      generateEnumConversions(ToxavCallControl.values),
      generateEnumConversions(ToxavFriendCallState.values)
    )
  }

  writeCode("ToxCore/generated/enums.cpp") {
    ifdef(
      "../ToxCore.h",
      "TOX_VERSION_MAJOR",
      generateEnumConversions(ToxConnection.values),
      generateEnumConversions(ToxFileControl.values),
      generateEnumConversions(ToxMessageType.values),
      generateEnumConversions(ToxProxyType.values),
      generateEnumConversions(ToxSavedataType.values),
      generateEnumConversions(ToxUserStatus.values)
    )
  }

}
