package im.tox.toktok.app

import android.test.ActivityInstrumentationTestCase2
import im.tox.core.network.Port
import im.tox.toktok.app.activities.Main
import im.tox.tox4j.core.data.ToxPublicKey
import im.tox.tox4j.core.options.ToxOptions
import im.tox.tox4j.impl.jni.ToxCoreImpl
import junit.framework.Assert.assertTrue
import junit.framework.AssertionFailedError

/**
 * This is a simple framework for a test of an Application.  See
 * [[android.test.ApplicationTestCase]] for more information on
 * how to write and extend Application tests.
 *
 * To run this test, you can type:
 *   adb shell am instrument -w \
 *   -e class im.tox.toktok.MainTest \
 *   im.tox.toktok.tests/android.test.InstrumentationTestRunner
 */
final class MainActivityTest extends ActivityInstrumentationTestCase2[Main](classOf[Main]) {

  private val TestNetwork = false

  def testTox4j(): Unit = {
    val tox1 = new ToxCoreImpl(ToxOptions())
    val tox2 = new ToxCoreImpl(ToxOptions())
    try {
      val bootHost = "biribiri.org"
      val bootPort = Port.fromInt(33445).get
      val bootKey = ToxPublicKey.fromHexString("F404ABAA1C99A9D37D61AB54898F56793E1DEF8BD46B1038B9D822E8460FAB67")
        .getOrElse(throw new AssertionFailedError("Unable to parse public key"))
      tox1.bootstrap(bootHost, bootPort, bootKey)
      tox1.addTcpRelay(bootHost, bootPort, bootKey)
      tox2.bootstrap(bootHost, bootPort, bootKey)
      tox2.addTcpRelay(bootHost, bootPort, bootKey)
      if (TestNetwork) {
        assertTrue(
          s"No connection established within ${Tox4jMain.IterationTimeout} iterations",
          Tox4jMain.waitForConnection(tox1, tox2)
        )
      }
    } finally {
      tox2.close()
      tox1.close()
    }
  }

}
