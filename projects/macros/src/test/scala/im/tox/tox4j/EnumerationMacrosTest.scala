package im.tox.tox4j

import org.scalatest.FunSuite

final class EnumerationMacrosTest extends FunSuite {

  test("non-sealed base type") {
    trait Base
    object Base {
      case object A extends Base
      case object B extends Base
      case object C extends Base

      implicit val ordBase: Ordering[Base] = Ordering.by(_.##)

      assertTypeError("val values: TreeSet[Base] = EnumerationMacros.sealedInstancesOf[Base]")
    }
  }

  test("non-object derived types") {
    sealed trait Base
    object Base {
      case object A extends Base
      final case class B() extends Base
      case object C extends Base

      implicit val ordBase: Ordering[Base] = Ordering.by(_.##)

      assertTypeError("val values: TreeSet[Base] = EnumerationMacros.sealedInstancesOf[Base]")
    }
  }

  test("Ordering not defined") {
    sealed trait Base
    object Base {
      case object A extends Base
      case object B extends Base
      case object C extends Base

      assertTypeError("val values: TreeSet[Base] = EnumerationMacros.sealedInstancesOf[Base]")
    }
  }

  test("success for enumeration-style sets of case objects") {
    sealed trait Base
    object Base {
      case object A extends Base
      case object B extends Base
      case object C extends Base

      implicit val ordBase: Ordering[Base] = Ordering.by(_.##)

      assertTypeError("val values: TreeSet[Base] = EnumerationMacros.sealedInstancesOf[Base]")
    }
  }

}
