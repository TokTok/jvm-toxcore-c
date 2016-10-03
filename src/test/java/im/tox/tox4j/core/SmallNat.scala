package im.tox.tox4j.core

import org.scalacheck.{ Arbitrary, Gen }

import scala.language.implicitConversions
import scala.runtime.IntegralProxy

/**
 * A wrapper class for [[Int]] to be used in property based testing where a
 * small positive integer is required (e.g. for array bounds).
 *
 * arbitrary[Int] generates numbers from the full range of integers, and is
 * thus unfit for allocation sizes and iteration counts.
 */
final case class SmallNat(self: Int) extends AnyVal with IntegralProxy[Int] {
  override protected def num = scala.math.Numeric.IntIsIntegral
  override protected def ord = scala.math.Ordering.Int
  override def isWhole(): Boolean = true
}

@SuppressWarnings(Array("org.wartremover.warts.ImplicitConversion"))
object SmallNat {
  val MinValue = 0
  val MaxValue = 100

  implicit val arbSmallNat: Arbitrary[SmallNat] = Arbitrary(Gen.choose(MinValue, MaxValue).map(apply))
  implicit def smallNatToInt(smallNat: SmallNat): Int = smallNat.self
}
