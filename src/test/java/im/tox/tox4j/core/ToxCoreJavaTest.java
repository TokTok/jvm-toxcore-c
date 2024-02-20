package im.tox.tox4j.core;

import im.tox.tox4j.core.callbacks.ToxCoreEventAdapter;
import im.tox.tox4j.core.data.ToxFriendNumber;
import im.tox.tox4j.core.enums.ToxConnection;
import im.tox.tox4j.core.options.ToxOptions;
import im.tox.tox4j.impl.jni.ToxCoreImpl;
import org.junit.Test;

public class ToxCoreJavaTest {
  @Test
  public void addFriendNorequest_shouldConnectTwoToxes() throws InterruptedException {
    ToxCore tox1 = new ToxCoreImpl(ToxOptions.Companion.getDefaultInstance());
    ToxCore tox2 = new ToxCoreImpl(ToxOptions.Companion.getDefaultInstance());

    tox2.bootstrap("localhost", tox1.getUdpPort(), tox1.getDhtId());

    tox1.addFriendNorequest(tox2.getPublicKey());
    tox2.addFriendNorequest(tox1.getPublicKey());

    boolean connected1 = false;
    boolean connected2 = false;

    while (!connected1 && !connected2) {
      connected1 = tox1.iterate(new ToxCoreEventAdapter<Boolean>() {
        @Override
        public Boolean friendConnectionStatus(
            int friendNumber, ToxConnection connectionStatus, Boolean state) {
          return connectionStatus != ToxConnection.NONE;
        }
      }, connected1);
      connected2 = tox2.iterate(new ToxCoreEventAdapter<Boolean>() {
        @Override
        public Boolean friendConnectionStatus(
            int friendNumber, ToxConnection connectionStatus, Boolean state) {
          return connectionStatus != ToxConnection.NONE;
        }
      }, connected2);
      Thread.sleep(tox1.getIterationInterval());
    }
  }
}
