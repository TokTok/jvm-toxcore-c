/*
 * This file is part of the gnieh-pp project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gnieh.pp

/**
 * A pretty printer, that tries to make the document fit in the page width
 *
 *  @author Lucas Satabin
 */
final class PrettyRenderer(width: Int) extends (Doc => SimpleDoc) {

  private type Docs = List[(Int, Doc)]

  override def apply(doc: Doc): SimpleDoc = {
    best(width, 0, List((0, doc)))
  }

  private def best(width: Int, column: Int, docs: Docs): SimpleDoc = {
    docs match {
      case Nil =>
        SEmpty
      case (_, EmptyDoc) :: tail =>
        best(width, column, tail)
      case (i, ConsDoc(first, second)) :: tail =>
        best(width, column, (i, first) :: (i, second) :: tail)
      case (i, NestDoc(j, inner)) :: tail =>
        best(width, column, (i + j, inner) :: tail)
      case (i, TextDoc(text)) :: tail =>
        SText(text, best(width, column + text.length, tail))
      case (i, LineDoc(_)) :: tail =>
        SLine(i, best(width, i, tail))
      case (i, UnionDoc(l, s)) :: tail =>
        better(width, column,
          best(width, column, (i, l) :: tail),
          best(width, column, (i, s) :: tail))
      case (i, AlignDoc(inner)) :: tail =>
        best(width, column, (column, inner) :: tail)
      case (i, ColumnDoc(f)) :: tail =>
        best(width, column, (i, f(column)) :: tail)
    }
  }

  private def better(width: Int, column: Int, d1: SimpleDoc, d2: => SimpleDoc): SimpleDoc = {
    if (d1.fits(width - column)) {
      d1
    } else {
      // d2 is computed only if needed...
      d2
    }
  }

}

/**
 * This printer is not really pretty (but should be faster than pretty printers!):
 *  it does not insert any indentation and discards all groups, just renders everything as compact as possible
 *
 *  @author Lucas Satabin
 */
object CompactRenderer extends (Doc => SimpleDoc) {

  override def apply(doc: Doc): SimpleDoc = {
    scan(0, List(doc))
  }

  private def scan(column: Int, docs: List[Doc]): SimpleDoc = docs match { // scalastyle:ignore cyclomatic.complexity
    case Nil => SEmpty
    case doc :: docs =>
      doc match {
        case EmptyDoc => SEmpty
        case TextDoc(text) => SText(text, scan(column + text.length, docs))
        case LineDoc(_) => scan(column, doc.flatten :: docs)
        case ConsDoc(first, second) => scan(column, first :: second :: docs)
        case NestDoc(j, doc) => scan(column, doc :: docs)
        case UnionDoc(long, _) => scan(column, long :: docs)
        case AlignDoc(inner) => scan(column, inner :: docs)
        case ColumnDoc(f) => scan(column, f(column) :: docs)
      }
  }

}

sealed abstract class CountUnit
/** Truncates after the count in non space characters */
case object Characters extends CountUnit
/** Truncates after the count in words */
case object Words extends CountUnit
/** Truncates after the count in lines */
case object Lines extends CountUnit

/**
 * A renderer that truncates the result (once rendered by the inner renderer) with the given
 *  limit. It makes the assumption that the following invariants are respected:
 *   - a [[gnieh.pp.SText]] contains either only spaces or only a word
 *   - indentation characters are all modeled with the indentation in [[gnieh.pp.SLine]]
 *
 *  @author Lucas Satabin
 */
final class TruncateRenderer(max: Int, unit: CountUnit, inner: Doc => SimpleDoc) extends (Doc => SimpleDoc) {

  override def apply(doc: Doc): SimpleDoc = {
    truncate(inner(doc))
  }

  def apply(doc: SimpleDoc): SimpleDoc = {
    truncate(doc)
  }

  /** Truncates the simple document, depending on the constructor criterion. */
  def truncate(doc: SimpleDoc): SimpleDoc = {
    unit match {
      case Lines => firstLines(max, doc)
      case Characters => firstChars(max, doc)
      case Words => firstWords(max, doc)
    }
  }

  private def firstLines(maxLines: Int, doc: SimpleDoc): SimpleDoc = {
    doc match {
      case SText(text, next) if maxLines >= 1 => SText(text, firstLines(maxLines, next))
      case SLine(indent, next) if maxLines > 1 => SLine(indent, firstLines(maxLines - 1, next))
      case _ => SEmpty
    }
  }

  private def firstChars(maxChars: Int, doc: SimpleDoc): SimpleDoc = {
    doc match {
      case SText(text, next) if maxChars >= text.replaceAll(" ", "").length => SText(text, firstChars(maxChars - text.length, next))
      case SLine(indent, next) if maxChars > 0 => SLine(indent, firstChars(maxChars, next))
      case _ => SEmpty
    }
  }

  private def firstWords(maxWords: Int, doc: SimpleDoc): SimpleDoc = {
    doc match {
      case SText(text, next) if text.trim.isEmpty && maxWords > 0 => SText(text, firstWords(maxWords, next))
      case SText(text, next) if maxWords > 0 => SText(text, firstWords(maxWords - 1, next))
      case SLine(indent, next) if maxWords > 0 => SLine(indent, firstWords(maxWords, next))
      case _ => SEmpty
    }
  }

}
