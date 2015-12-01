package im.tox.core

import codes.reactive.scalatime.Scalatime._
import codes.reactive.scalatime._
import im.tox.tox4j.core.SmallNat
import im.tox.tox4j.core.SmallNat._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.WordSpec
import org.scalatest.prop.PropertyChecks

import scalaz.{-\/, \/-}

final class PingArrayTest extends WordSpec with PropertyChecks {

  implicit val arbEncryptedText: Arbitrary[Duration] =
    Arbitrary(arbitrary[Long].map(time => Math.abs(time).seconds))

  "adding an element" should {
    "let us retrieve it once" in {
      forAll { (size: SmallNat, expiryDelay: Duration) =>
        whenever(size > 0 && expiryDelay.getSeconds > 0) {
          val array = new PingArray[Int](size, expiryDelay)
          val id = array.add(123)
          val data = array.remove(id)
          assert(data == \/-(123))
        }
      }
    }

    "not let us retrieve it twice" in {
      forAll { (size: SmallNat, expiryDelay: Duration) =>
        whenever(size > 0 && expiryDelay.getSeconds > 0) {
          val array = new PingArray[Int](size, expiryDelay)
          val id = array.add(123)
          array.remove(id)
          val data = array.remove(id)
          assert(data == -\/(PingArray.ErrorCode.NoData))
        }
      }
    }

    "not let us retrieve it after it timed out" in {
      forAll { (size: SmallNat) =>
        whenever(size > 0) {
          val array = new PingArray[Int](size, 1L.milli)
          val id = array.add(123)
          Thread.sleep(10)
          val data = array.remove(id)
          assert(data == -\/(PingArray.ErrorCode.Timeout))
        }
      }
    }
  }

  "retrieving an element" should {
    "fail with the wrong ping id" in {
      forAll { (size: SmallNat, expiryDelay: Duration) =>
        whenever(size > 0 && expiryDelay.getSeconds > 0) {
          val array = new PingArray[Int](size, expiryDelay)
          val id = array.add(123)
          val data = array.remove(id.copy(id.value + size))
          assert(data == -\/(PingArray.ErrorCode.InvalidEntry))
        }
      }
    }
  }

}
