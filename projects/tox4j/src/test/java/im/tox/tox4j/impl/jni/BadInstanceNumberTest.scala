package im.tox.tox4j.impl.jni

import im.tox.tox4j.core.options.ToxOptions
import im.tox.tox4j.exceptions.ToxKilledException
import org.scalacheck.Gen
import org.scalatest.FunSuite
import org.scalatest.prop.PropertyChecks

/**
 * This class tests whether the C++ code is resilient against memory corruption
 * and bad people using reflection to forge invalid Tox instances.
 */
final class BadInstanceNumberTest extends FunSuite with PropertyChecks {

  private def callWithInstanceNumber(instanceNumber: Int): Unit = {
    val tox = new ToxCoreImpl(ToxOptions())

    val field = tox.getClass.getDeclaredField("instanceNumber")
    field.setAccessible(true)
    val oldInstanceNumber = field.get(tox).asInstanceOf[Int]
    field.set(tox, instanceNumber)

    val exception =
      try {
        tox.iterationInterval
        null
      } catch {
        case e: Throwable => e
      }

    // Set it back to the good one, so close() works.
    field.set(tox, oldInstanceNumber)
    tox.close()

    if (exception != null) {
      throw exception
    }
  }

  test("negative or zero instance numbers") {
    forAll(Gen.choose(Int.MinValue, 0)) { instanceNumber =>
      intercept[IllegalStateException] {
        callWithInstanceNumber(instanceNumber)
      }
    }
  }

  test("very large instance numbers") {
    forAll(Gen.choose(0xffff, Int.MaxValue)) { instanceNumber =>
      intercept[IllegalStateException] {
        callWithInstanceNumber(instanceNumber)
      }
    }
  }

  test("any invalid instance numbers") {
    // This could be fine if there is another Tox instance lingering around, but we assume there isn't.
    // So, it's either killed (ToxKilledException) or never existed (IllegalStateException).
    System.gc() // After this, there should be no lingering instances.

    forAll { (instanceNumber: Int) =>
      whenever(instanceNumber != 1) {
        try {
          callWithInstanceNumber(instanceNumber)
          fail("No exception thrown. Expected IllegalStateException or ToxKilledException.")
        } catch {
          case _: IllegalStateException =>
          case _: ToxKilledException    => // Both fine.
        }
      }
    }
  }

}
