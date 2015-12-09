package im.tox.core.dht.distance

import im.tox.core.crypto.PublicKey

import scala.annotation.tailrec

final case class HammingDistance(x: PublicKey, y: PublicKey) extends DistanceMetric[HammingDistance] {

  private def toInt: Int = {
    @tailrec
    def go(i: Int, distance: Int, x: IndexedSeq[Byte], y: IndexedSeq[Byte]): Int = {
      if (i == x.length) {
        distance
      } else {
        val difference = if (x(i) != y(i)) 1 else 0
        go(i + 1, distance + difference, x, y)
      }
    }
    go(0, 0, x.value, y.value)
  }

  protected[distance] override def value: BigInt = {
    x.value.zip(y.value).count(c => c._1 != c._2)
  }

  override def <(rhs: HammingDistance): Boolean = { // scalastyle:ignore method.name
    this.toInt < rhs.toInt
  }

}

object HammingDistance extends DistanceMetricCompanion[HammingDistance]
