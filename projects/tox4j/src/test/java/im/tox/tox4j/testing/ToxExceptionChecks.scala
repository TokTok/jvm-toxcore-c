package im.tox.tox4j.testing

import im.tox.tox4j.exceptions.ToxException
import org.scalatest.Assertions

trait ToxExceptionChecks extends Assertions {

  protected def intercept[E <: Enum[E]](code: E)(f: => Unit) = {
    try {
      f
      fail(s"Expected exception with code ${code.name}")
    } catch {
      case e: ToxException[_] =>
        assert(e.code eq code)
    }
  }

}
