package im.tox.tox4j.impl.jni.codegen.cxx

import gnieh.pp._
import im.tox.tox4j.impl.jni.codegen.cxx.Ast._
import org.apache.commons.lang3.StringEscapeUtils

object Print {

  private def flatten(docs: Seq[Doc], sep: Doc = empty): Doc = {
    docs match {
      case Nil => empty
      case head +: tail =>
        tail.foldLeft(head)((a, b) => a :: sep :: b)
    }
  }

  private def printSeq[T](nodes: Seq[T], sep: Doc = empty)(printT: T => Doc): Doc = {
    flatten(nodes.map(printT), sep)
  }

  /**
   * Types.
   */
  private def printTypeInner(ty: Type): Doc = {
    ty match {
      case Typename(name)   => text(name) :: space
      case Const(inner)     => printTypeInner(inner) :: "const "
      case Pointer(inner)   => printTypeInner(inner) :: "*"
      case Reference(inner) => printTypeInner(inner) :: "&"
    }
  }

  def printType(ty: Type, spacing: Doc = empty): Doc = {
    ty match {
      case Typename(name) => text(name) :: spacing
      case _              => printTypeInner(ty)
    }
  }

  /**
   * Pre-processing directives.
   */
  def printPreproc(pp: Preproc): Doc = {
    pp match {
      case Include(header)      => "#include \"" :: header :: "\""
      case Ifdef(name)          => "#ifdef " :: name
      case Comment(text)        => "// " :: text
      case Endif                => "#endif"
      case MacroCall(expr)      => printExpr(expr)
      case MacroFun(init, body) => printExpr(init) :|: printStmt(body)
      case ToxFun(returnType, name, params, body) =>
        "JAVA_METHOD" :+: nest(2)("(" :: printType(returnType) :: "," :+: name :: "," :|:
          printSeq(params, "," :: space)(printDecl) :: ")") :|:
          printStmt(body)
    }
  }

  /**
   * Statements.
   */
  def printStmt(stmt: Stmt, spacing: Doc = line): Doc = { // scalastyle:ignore cyclomatic.complexity
    stmt match {
      case Switch(cond, CompoundStmt(cases)) =>
        "switch (" :: printExpr(cond) :: nest(2)(")" :|:
          "{" :|:
          printSeq(cases)(printStmt(_)) ::
          "}") ::
          line
      case Switch(cond, body)       => sys.error("Invalid switch-body: " + body)
      case Case(expr, body)         => "case" :+: printExpr(expr) :: ":" :+: printStmt(body)
      case Default(body)            => "default:" :+: printStmt(body)
      case Break                    => "break;"
      case Return(expr)             => "return" :: expr.fold(empty)(expr => space :: printExpr(expr)) :: ";" :: spacing
      case ExprStmt(expr)           => printExpr(expr) :: ";" :: spacing
      case CompoundStmt(Seq(stmt0)) => nest(2)("{" :|: printStmt(stmt0, empty)) :|: "}"
      case CompoundStmt(stmts)      => nest(2)("{" :|: printSeq(stmts.slice(0, stmts.length - 1))(printStmt(_))) :: printStmt(stmts.last) :: "}"
      case stmt: Oneliner           => group(printSeq(stmt.stmts)(printStmt(_, empty))) :: spacing
      case pp: Preproc              => printPreproc(pp)
    }
  }

  /**
   * Expressions.
   */
  def printExpr(expr: Expr): Doc = {
    expr match {
      case Identifier(name)             => name
      case IntegerLiteral(value)        => value
      case StringLiteral(value)         => "\"" :: StringEscapeUtils.escapeJava(value) :: "\""
      case FunCall(callee, args)        => printExpr(callee) :+: "(" :: printSeq(args, "," :: space)(printExpr) :: ")"
      case BinaryOperator(op, lhs, rhs) => printExpr(lhs) :+: op :+: printExpr(rhs)
      case Equals(lhs, rhs)             => printExpr(lhs) :+: "==" :+: printExpr(rhs)
      case Access(lhs, name)            => printExpr(lhs) :: "." :: name
    }
  }

  /**
   * Declarations.
   */
  def printDecl(decl: Decl): Doc = {
    decl match {
      case Fun(ty, name, params, body) =>
        printType(ty) :|: name :+: "(" :: printSeq(params, "," :: space)(printDecl) :: ")" :|: printStmt(body)

      case TemplateFun(tparams, targs, Fun(ty, name, params, body)) =>
        "template<" :: flatten(tparams.map("typename" :+: _)) :: ">" :|:
          printType(ty) :|:
          name :: "<" :: flatten(targs.map(printType(_))) :: "> (" :: printSeq(params, "," :: space)(printDecl) :: ")" :|:
          printStmt(body)

      case Param(ty, name) => printType(ty, space) :: name
      case pp: Preproc     => printPreproc(pp)
    }
  }

}
