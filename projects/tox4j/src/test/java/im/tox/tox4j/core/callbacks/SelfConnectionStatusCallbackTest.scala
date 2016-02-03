package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.enums.ToxConnection
import im.tox.tox4j.testing.autotest.AutoTestSuite

final class SelfConnectionStatusCallbackTest extends AutoTestSuite {

  type S = ToxConnection

  object Handler extends EventListener(ToxConnection.NONE) {

    override def selfConnectionStatus(connection: ToxConnection)(state: State): State = {
      state.finish
    }

  }

}
