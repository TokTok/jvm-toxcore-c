package im.tox.tox4j.impl.jni.codegen.cxx

// scalastyle:ignore number.of.types
object Ast {

  /**
   * Types.
   */
  sealed trait Type

  final case class Typename(name: String) extends Type
  final case class Const(inner: Type) extends Type
  final case class Pointer(inner: Type) extends Type
  final case class Reference(inner: Type) extends Type

  object Type {
    val void = Typename("void")
    val jint = Typename("jint")
    val jboolean = Typename("jboolean")
  }

  /**
   * Pre-processing directives.
   */
  sealed trait Preproc extends Decl with Stmt

  final case class Include(header: String) extends Preproc
  final case class Ifdef(name: String) extends Preproc
  final case class Comment(text: String) extends Preproc
  final case class MacroCall(expr: FunCall) extends Preproc
  final case class MacroFun(init: FunCall, body: CompoundStmt) extends Preproc
  final case class ToxFun(returnType: Type, name: String, params: Seq[Param], body: CompoundStmt) extends Preproc
  case object Endif extends Preproc

  /**
   * Statements.
   */
  sealed trait Stmt

  final case class Switch(cond: Expr, body: CompoundStmt) extends Stmt
  final case class Case(expr: Expr, body: Stmt) extends Stmt
  final case class Default(body: Stmt) extends Stmt
  case object Break extends Stmt
  final case class Return(expr: Option[Expr] = None) extends Stmt
  final case class ExprStmt(expr: Expr) extends Stmt
  final case class CompoundStmt(body: Seq[Stmt]) extends Stmt
  final case class Oneliner(stmts: Stmt*) extends Stmt

  object Return {
    def apply(expr: Expr): Return = apply(Some(expr))
  }

  object CompoundStmt {
    def apply(s1: Stmt): CompoundStmt = apply(Seq(s1))
    def apply(s1: Stmt, s2: Stmt): CompoundStmt = apply(Seq(s1, s2))
    def apply(s1: Stmt, s2: Stmt, s3: Stmt): CompoundStmt = apply(Seq(s1, s2, s3))
  }

  /**
   * Expressions.
   */
  sealed trait Expr

  final case class Identifier(name: String) extends Expr
  final case class IntegerLiteral(value: Int) extends Expr
  final case class StringLiteral(value: String) extends Expr
  final case class FunCall(callee: Expr, args: Seq[Expr]) extends Expr
  final case class BinaryOperator(op: String, lhs: Expr, rhs: Expr) extends Expr

  final case class Access(expr: Expr, name: String) extends Expr
  final case class Equals(lhs: Expr, rhs: Expr) extends Expr

  /**
   * Declarations.
   */
  sealed trait Decl

  type TranslationUnit = Seq[Decl]
  final case class Fun(returnType: Type, name: String, params: Seq[Param], body: CompoundStmt) extends Decl
  final case class TemplateFun(typeParams: Seq[String], typeArgs: Seq[Type], fun: Fun) extends Decl
  final case class Param(paramType: Type, name: String) extends Decl

}
