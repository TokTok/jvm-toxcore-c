package im.tox.tox4j.core;

import im.tox.tox4j.core.callbacks.ToxCoreEventAdapter;
import im.tox.tox4j.core.callbacks.ToxCoreEventListener;
import im.tox.tox4j.core.enums.ToxFileControl;
import im.tox.tox4j.core.enums.ToxMessageType;
import im.tox.tox4j.core.enums.ToxUserStatus;
import im.tox.tox4j.core.exceptions.ToxFriendAddException;
import im.tox.tox4j.core.options.ProxyOptions;
import im.tox.tox4j.core.options.SaveDataOptions;
import im.tox.tox4j.core.options.ToxOptions;
import im.tox.tox4j.exceptions.ToxException;
import im.tox.tox4j.impl.jni.ToxCoreImpl;
import org.junit.Assert;
import org.junit.Test;
import org.scalatest.junit.JUnitSuite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public final class JToxCoreTest extends JUnitSuite {

  private final ToxCoreEventListener<Void> handler = new ToxCoreEventAdapter<>();
  private final ToxOptions options = new ToxOptions(
      true, true,
      new ProxyOptions.Http("localhost", 1234),
      ToxCoreConstants.DefaultStartPort(),
      ToxCoreConstants.DefaultEndPort(),
      0,
      // TODO(iphydf): This is kind of ugly. Do we want to live with this?
      SaveDataOptions.None$.MODULE$,
      true
  );

  private void expectBoolean(boolean bool) {}

  private void expectInt(int integer) {}

  private void expectBytes(byte[] bytes) {}

  private void expectInts(int[] ints) {}

  @Test
  public void testJavaUsability() {
    try (ToxCore<Void> tox = new ToxCoreImpl<>(options)) {
      tox.callback(handler);
      tox.iterate(null);
      int friendNumber = tox.addFriend(null, null);
      Assert.fail("No exception thrown, null friend added as " + friendNumber);
    } catch (ToxFriendAddException e) {
      assertEquals(ToxFriendAddException.Code.NULL, e.code());
    }
  }

  /**
   * Call every ToxCore method with Java types and check whether their return
   * types conform to the expected API. This test is here to verify that the
   * Scala type changes don't break Java code.
   */
  @Test
  public void testJavaApi() {
    final byte[] bytes = "hello".getBytes();

    try (ToxCore<Void> tox = new ToxCoreImpl<>(options)) {
      expectInt(tox.addFriend(bytes, bytes));
      expectInt(tox.addFriendNorequest(bytes));
      tox.addTcpRelay("hello", 0, bytes);
      tox.bootstrap("hello", 0, bytes);
      tox.callback(handler);
      tox.close();
      tox.deleteFriend(0);
      tox.fileControl(0, 0, ToxFileControl.CANCEL);
      tox.fileSeek(0, 0, 0L);
      expectInt(tox.fileSend(0, 0, 0L, bytes, bytes));
      tox.fileSendChunk(0, 0, 0L, bytes);
      expectInt(tox.friendByPublicKey(bytes));
      expectBoolean(tox.friendExists(0));
      tox.friendSendLosslessPacket(0, bytes);
      tox.friendSendLossyPacket(0, bytes);
      expectInt(tox.friendSendMessage(0, ToxMessageType.NORMAL, 0, bytes));
      expectBytes(tox.getAddress());
      expectBytes(tox.getDhtId());
      expectBytes(tox.getFileFileId(0, 0));
      expectInts(tox.getFriendList());
      expectBytes(tox.getFriendPublicKey(0));
      expectBytes(tox.getName());
      expectInt(tox.getNospam());
      expectBytes(tox.getPublicKey());
      expectBytes(tox.getSavedata());
      expectBytes(tox.getSecretKey());
      expectBytes(tox.getStatusMessage());
      ToxUserStatus status = tox.getStatus();
      expectInt(tox.getTcpPort());
      expectInt(tox.getUdpPort());
      Void nothing = tox.iterate(null);
      expectInt(tox.iterationInterval());
      ToxCore<Void> tox2 = tox.load(options);
      tox.setName(bytes);
      tox.setNospam(0);
      tox.setStatus(ToxUserStatus.AWAY);
      tox.setStatusMessage(bytes);
      tox.setTyping(0, true);
    } catch (ToxException<?> | IllegalArgumentException e) {
      assertNotNull(e);
    }
  }

}
