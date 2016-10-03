package im.tox.tox4j.exceptions

import im.tox.tox4j.impl.jni.ToxCoreImplFactory.withToxUnit
import org.scalatest.FunSuite

final class ToxKilledExceptionTest extends FunSuite {

  test("UseAfterCloseInOrder") {
    intercept[ToxKilledException] {
      withToxUnit { tox1 =>
        withToxUnit { tox2 =>
          tox1.close()
          tox1.iterationInterval
        }
      }
    }
  }

  test("UseAfterCloseReverseOrder") {
    intercept[ToxKilledException] {
      withToxUnit { tox1 =>
        withToxUnit { tox2 =>
          tox2.close()
          tox2.iterationInterval
        }
      }
    }
  }

}
