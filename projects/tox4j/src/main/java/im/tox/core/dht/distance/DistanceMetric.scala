package im.tox.core.dht.distance

import im.tox.core.crypto.PublicKey

abstract class DistanceMetric[This <: DistanceMetric[This]] {

  protected[distance] def value: BigInt

  // scalastyle:off method.name
  def <(rhs: This): Boolean = value < rhs.value

  final def <=(rhs: BigInt): Boolean = value <= rhs

  final def +(rhs: This): BigInt = value + rhs.value
  // scalastyle:on method.name

  final def showBytes: String = {
    value.toByteArray.map(c => f"$c%02X").mkString
  }

  final def toHexString: String = {
    value.toString(16) // scalastyle:ignore magic.number
  }

  final override def toString: String = {
    s"${getClass.getSimpleName}($toHexString=$value)"
  }

  @SuppressWarnings(Array("org.brianmckenna.wartremover.warts.Any"))
  final override def equals(rhs: Any): Boolean = {
    rhs match {
      case rhs: DistanceMetric[_] =>
        assert(getClass == rhs.getClass)
        (this eq rhs) || value == rhs.value
      case _ =>
        false
    }
  }

  final override def hashCode: Int = value.hashCode

}

abstract class DistanceMetricCompanion[Metric <: DistanceMetric[Metric]] extends ((PublicKey, PublicKey) => Metric) {

  implicit val ordDistanceMetric: Ordering[Metric] = Ordering.fromLessThan(_ < _)

  def apply(x: PublicKey, y: PublicKey): Metric

}
