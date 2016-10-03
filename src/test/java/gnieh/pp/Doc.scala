/*
* This file is part of the gnieh-pp project.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package gnieh.pp

/**
 * A document that can then be laid out.
 *
 *  @author Lucas Satabin
 */
// scalastyle:off method.name
sealed abstract class Doc {

  // for all operators, the empty document is a right and left unit
  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  private def withUnit(f: Doc => Doc)(that: Doc): Doc = {
    if (this == EmptyDoc) {
      that
    } else if (that == EmptyDoc) {
      this
    } else {
      f(that)
    }
  }

  /**
   * Concatenates two documents.
   *  Is left associative with [[gnieh.pp.empty]] as left and right unit.
   */
  def ::(that: Doc): Doc =
    withUnit(ConsDoc(_, this))(that)

  /** Equivalent to `that :: space :: this` */
  def :+:(that: Doc): Doc =
    withUnit(_ :: space :: this)(that)

  /** Equivalent to `that :: line :: this` */
  def :|:(that: Doc): Doc =
    withUnit(_ :: line :: this)(that)

  /** Equivalent to `that :: softline :: this` */
  def :\:(that: Doc): Doc =
    withUnit(_ :: softline :: this)(that)

  /** Equivalent to `that :: linebreak :: this` */
  def :||:(that: Doc): Doc =
    withUnit(_ :: linebreak :: this)(that)

  /** Equivalent to `that :: softbreak :: this` */
  def :\\:(that: Doc): Doc =
    withUnit(_ :: softbreak :: this)(that)

  /** Equivalent to `align(this :|: that)` */
  def ||(that: Doc): Doc =
    align(this :|: that)

  /** A flatten (no new lines) version of this document */
  def flatten: Doc

}

/**
 * Nest document: new lines are indented by the given indentation.
 *  @author Lucas Satabin
 */
final case class NestDoc(indent: Int, inner: Doc) extends Doc {
  override lazy val flatten = NestDoc(indent, inner.flatten)
}

/**
 * Union document: two variations of the same document.
 *  '''Note''': The `long`-document's first lines must be longer that the `short`-document's ones.
 *  @author Lucas Satabin
 */
final case class UnionDoc(long: Doc, short: Doc) extends Doc {
  override lazy val flatten = long.flatten
}

/**
 * Empty document.
 *  @author Lucas Satabin
 */
case object EmptyDoc extends Doc {
  override def flatten: Doc = this
}

/**
 * Text document: shall not contain any new lines.
 *  @author Lucas Satabin
 */
final case class TextDoc(text: String) extends Doc {
  override def flatten: Doc = this
}

/**
 * Line document: renders as a new line except if discarded by a group.
 *  @author Lucas Satabin
 */
final case class LineDoc(repl: Doc) extends Doc {
  override def flatten: Doc = repl
}

/**
 * Cons document: Concatenation of two documents.
 *  @author Lucas Satabin
 */
final case class ConsDoc(first: Doc, second: Doc) extends Doc {
  override lazy val flatten = ConsDoc(first.flatten, second.flatten)
}

/**
 * Align document: aligns the document on the current column.
 *  @author Lucas Satabin
 */
final case class AlignDoc(inner: Doc) extends Doc {
  override lazy val flatten = AlignDoc(inner.flatten)
}

/**
 * Column document: creates a document depending on the current column.
 *  @author Lucas Satabin
 */
final case class ColumnDoc(f: Int => Doc) extends Doc {
  override lazy val flatten = ColumnDoc(f.andThen(_.flatten))
}
