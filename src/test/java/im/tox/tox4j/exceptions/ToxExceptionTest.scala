package im.tox.tox4j.exceptions

import im.tox.tox4j.core.exceptions.ToxBootstrapException
import org.scalatest.FlatSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

@SuppressWarnings(Array("org.wartremover.warts.Equals"))
final class ToxExceptionTest extends FlatSpec with ScalaCheckPropertyChecks {

  "getMessage" should "contain the error code name" in {
    ToxBootstrapException.Code.values().foreach { code =>
      val exn = new ToxBootstrapException(code)
      assert(exn.getMessage.contains(code.name()))
    }
  }

  it should "contain the exception message" in {
    forAll { (message: String) =>
      val exn = new ToxBootstrapException(ToxBootstrapException.Code.NULL, message)
      assert(exn.getMessage.contains(message))
    }
  }

  "code" should "be the passed code" in {
    ToxBootstrapException.Code.values().foreach { code =>
      val exn = new ToxBootstrapException(code)
      assert(exn.code == code)
    }
  }

}
