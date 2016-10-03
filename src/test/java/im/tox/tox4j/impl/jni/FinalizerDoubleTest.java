package im.tox.tox4j.impl.jni;

import im.tox.tox4j.core.ToxCoreConstants;
import im.tox.tox4j.core.options.ProxyOptions;
import im.tox.tox4j.core.options.SaveDataOptions;
import im.tox.tox4j.core.options.ToxOptions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.scalatest.junit.JUnitSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This test is just here to check whether calling {@link ToxCoreImpl#finalize} twice causes a
 * hard crash or not. It should be handled gracefully, although it should never happen. This test
 * should print an exception trace to the log.
 *
 * <p/>
 * Thus: This test will cause exceptions talking about a 'serious problem in native code'.
 * This is expected behaviour. It is not actually a serious problem.
 */
public class FinalizerDoubleTest extends JUnitSuite {

  private static final Logger logger = LoggerFactory.getLogger(FinalizerDoubleTest.class);

  private static final ToxOptions OPTIONS = new ToxOptions(
      true,
      true,
      ProxyOptions.None$.MODULE$,
      ToxCoreConstants.DefaultStartPort(),
      ToxCoreConstants.DefaultEndPort(),
      ToxCoreConstants.DefaultTcpPort(),
      SaveDataOptions.None$.MODULE$,
      true
  );

  @Before
  public void setUp() {
    logger.info("Exceptions about 'a serious problem in native code' are expected.");
  }

  /**
   * This is ran after every test to clean up the broken Tox instances, so that a future GC
   * doesn't cause exception traces to be printed.
   */
  @After
  public void tearDown() {
    System.gc();
  }

  @Test
  @SuppressWarnings("FinalizeCalledExplicitly")
  public void testFinalizeTwice() {
    ToxCoreImpl tox = new ToxCoreImpl(OPTIONS);

    tox.finalize();
    tox.finalize();
  }

  @Test(expected = IllegalStateException.class)
  @SuppressWarnings("FinalizeCalledExplicitly")
  public void testCloseAfterFinalize() {
    ToxCoreImpl tox = new ToxCoreImpl(OPTIONS);
    tox.finalize();
    tox.close();
  }

  @Test(expected = IllegalStateException.class)
  @SuppressWarnings("FinalizeCalledExplicitly")
  public void testAnyMethodAfterFinalize() {
    ToxCoreImpl tox = new ToxCoreImpl(OPTIONS);
    tox.finalize();
    tox.iterationInterval();
  }

}
