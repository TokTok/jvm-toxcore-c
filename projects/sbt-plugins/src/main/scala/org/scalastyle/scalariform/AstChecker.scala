package org.scalastyle.scalariform

import org.scalastyle.{CombinedAst, CombinedChecker, Lines, ScalastyleError}

import scalariform.parser.CompilationUnit

abstract class AstChecker(override val errorKey: String) extends CombinedChecker {
  def verify(ast: CompilationUnit, lines: Lines): List[ScalastyleError]

  override def verify(input: CombinedAst): List[ScalastyleError] = {
    verify(input.compilationUnit, input.lines)
  }
}
