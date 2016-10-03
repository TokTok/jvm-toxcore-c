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
 * A simple document is the normalized form of a document.
 *
 *  This is what is produced by the renderers depending on their rendering algorithm.
 *
 *  @author Lucas Satabin
 */
sealed abstract class SimpleDoc {

  def fits(width: Int): Boolean

  def layout: String

  override def toString: String = layout

}

/**
 * An empty document.
 *  @author Lucas Satabin
 */
case object SEmpty extends SimpleDoc {
  override def fits(width: Int): Boolean = width >= 0 // always fits if there is enough place

  override def layout: String = ""
}

/**
 * A text document. Should never contain new lines
 *  @author Lucas Satabin
 */
final case class SText(text: String, next: SimpleDoc) extends SimpleDoc {
  def fits(width: Int): Boolean = {
    next.fits(width - text.length)
  }

  override def layout: String = {
    text + next.layout
  }
}

/**
 * A new line document with the indentation level to print right after.
 *  If the next document is empty, the indentation is not printed.
 *  @author Lucas Satabin
 */
final case class SLine(indent: Int, next: SimpleDoc) extends SimpleDoc {
  override def fits(width: Int): Boolean = width >= 0 // always fits if there is enough place

  override def layout: String = {
    if (next.layout.isEmpty) {
      ""
    } else {
      "\n" + (" " * indent) + next.layout
    }
  }
}
