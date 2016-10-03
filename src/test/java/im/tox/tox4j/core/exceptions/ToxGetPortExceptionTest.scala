package im.tox.tox4j.core.exceptions

import im.tox.tox4j.testing.ToxTestMixin
import org.scalatest.FunSuite

final class ToxGetPortExceptionTest extends FunSuite with ToxTestMixin {

  test("GetTcpPort_NotBound") {
    interceptWithTox(ToxGetPortException.Code.NOT_BOUND)(
      _.getTcpPort
    )
  }

}
