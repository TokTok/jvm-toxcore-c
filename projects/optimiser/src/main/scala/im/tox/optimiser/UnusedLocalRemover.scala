package im.tox.optimiser

import soot._

import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer

/**
 * A BodyTransformer that removes all unused local variables from a given Body.
 */
@SuppressWarnings(Array("org.brianmckenna.wartremover.warts.Any"))
final class UnusedLocalRemover extends BodyTransformer {

  protected override def internalTransform(
    body: Body,
    phaseName: String,
    options: java.util.Map[String, String]
  ): scala.Unit = {
    G.v.out.println(s"[${body.getMethod.getName}] Eliminating unused locals...")

    var i = 0
    val n = body.getLocals.size
    val oldNumbers = new Array[Int](n)
    val locals = body.getLocals.asScala

    for (local <- locals) {
      oldNumbers(i) = local.getNumber
      local.setNumber(i)
      i += 1
    }

    val usedLocals = for {
      s <- body.getUnits.asScala
      vb <- s.getUseBoxes.asScala ++ s.getDefBoxes.asScala
      number <- vb.getValue match {
        case l: Local => Some(l.getNumber)
        case _        => None
      }
    } yield {
      number
    }

    var keep: Seq[Local] = Nil
    for (local <- locals) {
      val lno = local.getNumber
      local.setNumber(oldNumbers(lno))
      if (usedLocals.exists(_ == lno)) {
        keep :+= local
      }
    }

    G.v.out.println(s"[${body.getMethod.getName}] Removed locals: ${locals.toSet -- keep.toSet}")
    body.getLocals.clear()
    body.getLocals.addAll(keep.asJava)
  }

}