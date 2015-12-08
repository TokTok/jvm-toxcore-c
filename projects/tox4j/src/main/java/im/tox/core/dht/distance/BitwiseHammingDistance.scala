package im.tox.core.dht.distance

import im.tox.core.crypto.PublicKey
import scodec.bits.BitVector

final case class BitwiseHammingDistance(x: PublicKey, y: PublicKey) extends DistanceMetric[BitwiseHammingDistance] {

  protected[distance] override def value: BigInt = {
    BitVector(x.value).toBin.zip(BitVector(y.value).toBin).count(c => c._1 != c._2)
  }

}

object BitwiseHammingDistance extends DistanceMetricCompanion[BitwiseHammingDistance]
