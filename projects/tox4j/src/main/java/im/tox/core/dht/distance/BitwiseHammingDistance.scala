package im.tox.core.dht.distance

import im.tox.core.crypto.PublicKey
import scodec.bits.BitVector

import scala.annotation.tailrec

final case class BitwiseHammingDistance(x: PublicKey, y: PublicKey) extends DistanceMetric[BitwiseHammingDistance] {

  private def toInt: Int = {
    @tailrec
    def go(i: Int, distance: Int, x: IndexedSeq[Byte], y: IndexedSeq[Byte]): Int = {
      if (i == x.length) {
        distance
      } else {
        val difference = Integer.bitCount(x(i) ^ y(i))
        go(i + 1, distance + difference, x, y)
      }
    }
    go(0, 0, x.value, y.value)
  }

  protected[distance] override def value: BigInt = {
    x.value.zip(y.value).map(c => Integer.bitCount(c._1 ^ c._2)).sum
  }

  override def <(rhs: BitwiseHammingDistance): Boolean = { // scalastyle:ignore method.name
    this.toInt < rhs.toInt
  }

}

object BitwiseHammingDistance extends DistanceMetricCompanion[BitwiseHammingDistance]
