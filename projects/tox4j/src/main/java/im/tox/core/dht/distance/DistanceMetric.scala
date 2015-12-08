package im.tox.core.dht.distance

import im.tox.core.crypto.PublicKey

// scalastyle:off method.name
abstract class DistanceMetric[This <: DistanceMetric[This]] {

  protected[distance] def value: BigInt

  def <(rhs: This): Boolean = value < rhs.value

  final def <=(rhs: BigInt): Boolean = value <= rhs

  final def +(rhs: This): BigInt = value + rhs.value

  final def toHexString: String = {
    value.toByteArray.map(c => f"$c%02X").mkString
  }

  final override def toString: String = {
    s"${getClass.getSimpleName}($toHexString=$value)"
  }

  @SuppressWarnings(Array("org.brianmckenna.wartremover.warts.Any"))
  final override def equals(rhs: Any): Boolean = {
    rhs match {
      case rhs: DistanceMetric[_] =>
        assert(getClass == rhs.getClass)
        value == rhs.value
      case _ =>
        false
    }
  }

  final override def hashCode: Int = value.hashCode

}

abstract class DistanceMetricCompanion[Metric <: DistanceMetric[Metric]] {

  def apply(x: PublicKey, y: PublicKey): Metric

}
