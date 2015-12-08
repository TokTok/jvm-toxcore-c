package im.tox.core.dht.distance

import im.tox.core.crypto.PublicKey
import im.tox.core.dht.distance.XorDistance.{lessThan, signedXor, toBigInt}

final case class HammingDistance(x: PublicKey, y: PublicKey) extends DistanceMetric[HammingDistance] {

  protected[distance] override def value: BigInt = x.value.zip(y.value).count(c => c._1 != c._2)

}

object HammingDistance extends DistanceMetricCompanion[HammingDistance]
