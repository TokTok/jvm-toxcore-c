package org.scalastyle.scalariform.tox4j

import org.scalastyle._
import org.scalastyle.scalariform.{AstChecker, VisitorHelper}

import _root_.scalariform.lexer.{Token, TokenType}
import _root_.scalariform.parser._

/**
 * Checks that case classes don't use the "val" keyword redundantly in their member declarator list.
 *
 * E.g. case class Foo(val i: Int) is the same as case class Foo(i: Int). We prefer the latter.
 */
final class RedundantValChecker extends AstChecker("redundant.val") {

  override def verify(ast: CompilationUnit, lines: Lines): List[ScalastyleError] = {
    for {
      firstChild <- ast.immediateChildren.take(1)
      fullDefOrDcl <- VisitorHelper.getAll[FullDefOrDcl](firstChild)
      errors <- fullDefOrDcl.defOrDcl match {
        case tmplDef: TmplDef if isCaseClass(tmplDef) =>
          for {
            param <- VisitorHelper.getAll[Param](tmplDef.paramClausesOpt)
            if (param.valOrVarOpt match {
              case Some(Token(TokenType("VAL", _), _, _, _)) => true
              case _                                         => false
            })
          } yield {
            PositionError(param.firstToken.offset)
          }
        case _ =>
          Nil
      }
    } yield errors
  }

  private def isCaseClass(tmplDef: TmplDef): Boolean = {
    hasMarkerToken(tmplDef, "CASE") && hasMarkerToken(tmplDef, "CLASS")
  }

  private def hasMarkerToken(definition: TmplDef, tokenType: String): Boolean = {
    definition.markerTokens.exists { token => token.tokenType.name == tokenType }
  }

}
