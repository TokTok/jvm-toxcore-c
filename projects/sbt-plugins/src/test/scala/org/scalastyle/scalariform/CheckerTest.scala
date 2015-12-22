package org.scalastyle.scalariform

import org.scalastyle._
import org.scalatest.FunSuite

abstract class CheckerTest(checker: Checker[_] { val errorKey: String }) extends FunSuite { // scalastyle:ignore structural.type

  protected def key = checker.errorKey
  private def classUnderTest: Class[_ <: Checker[_]] = checker.getClass

  object NullFileSpec extends FileSpec {
    def name: String = ""
  }

  def assertErrors[T <: FileSpec](
    expected: Seq[Message[T]],
    source: String,
    params: Map[String, String] = Map(),
    customMessage: Option[String] = None,
    commentFilter: Boolean = true,
    customId: Option[String] = None
  ): Unit = {
    val classes = List(ConfigurationChecker(
      classUnderTest.getName,
      WarningLevel,
      enabled = true,
      params,
      customMessage,
      customId
    ))
    val configuration = ScalastyleConfiguration("", commentFilter, classes)

    val actual = new CheckerUtils().verifySource(configuration, classes, NullFileSpec, source)
    assert(actual.mkString(System.lineSeparator) == expected.mkString(System.lineSeparator))
  }

  def assertErrors(count: Int, source: String): Unit = {
    val marker = "!!"

    val expected = source.lines.zipWithIndex.flatMap {
      case (line, lineNumber) =>
        assert(line.indexOf(marker) == line.lastIndexOf(marker), "Currently only one error per line is supported")
        line.indexOf(marker) match {
          case -1                => Nil
          case columnNumber: Int => List(columnError(lineNumber + 1, columnNumber))
        }
    }.toSeq

    assertErrors(expected, source.replace(marker, ""))
    assert(expected.length == count)
  }

  def fileError(args: List[String] = List(), customMessage: Option[String] = None): StyleError[FileSpec] = {
    StyleError(NullFileSpec, classUnderTest, key, WarningLevel, args, None, None, customMessage)
  }

  def lineError(line: Int, args: List[String] = List()): StyleError[FileSpec] = {
    StyleError(NullFileSpec, classUnderTest, key, WarningLevel, args, Some(line), None)
  }

  def columnError(line: Int, column: Int, args: List[String] = List()): StyleError[FileSpec] = {
    StyleError(NullFileSpec, classUnderTest, key, WarningLevel, args, Some(line), Some(column))
  }

}
