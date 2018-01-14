package im.tox.core.random

import java.nio.ByteBuffer
import im.tox.core.typesafe.Equals._

import org.jetbrains.annotations.NotNull

object RandomCore {

  def entropy(@NotNull data: Seq[Byte]): Double = {
    val frequencies = new Array[Int](-Byte.MinValue * 2)
    for (b <- data) {
      frequencies(Byte.MaxValue - b) += 1
    }

    val probabilities =
      for (frequency <- frequencies) yield {
        if (frequency =/= 0) {
          val probability = frequency.toDouble / data.length
          -probability * (Math.log(probability) / Math.log(-Byte.MinValue * 2))
        } else {
          0
        }
      }
    probabilities.sum
  }

}
