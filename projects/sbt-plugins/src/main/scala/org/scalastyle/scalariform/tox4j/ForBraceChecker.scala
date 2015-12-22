package org.scalastyle.scalariform.tox4j

import org.scalastyle._
import org.scalastyle.scalariform.{AstChecker, VisitorHelper}

import _root_.scalariform.lexer.{Token, Tokens}
import _root_.scalariform.parser.{CompilationUnit, ForExpr}

final class ForBraceChecker extends AstChecker("for.brace") {

  override def verify(ast: CompilationUnit, lines: Lines): List[ScalastyleError] = {
    for {
      firstChild <- ast.immediateChildren.take(1)
      t <- VisitorHelper.getAll[ForExpr](firstChild)
      if t.lParenOrBrace.tokenType == Tokens.LBRACE && sameLine(t, lines) ||
        t.lParenOrBrace.tokenType == Tokens.LPAREN && !sameLine(t, lines)
    } yield {
      PositionError(t.lParenOrBrace.offset)
    }
  }

  private def sameLine(t: ForExpr, lines: Lines): Boolean = {
    val startLine = getLine(t.lParenOrBrace, lines)
    val endLine = getLine(t.rParenOrBrace, lines)
    startLine == endLine
  }

  private def getLine(t: Token, lines: Lines): Int = {
    lines.toLineColumn(t.offset).map(_.line).getOrElse(0)
  }

}
