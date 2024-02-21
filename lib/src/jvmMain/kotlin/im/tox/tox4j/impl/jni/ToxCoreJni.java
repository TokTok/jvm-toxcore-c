package im.tox.tox4j.impl.jni;

import im.tox.tox4j.core.exceptions.ToxBootstrapException;
import im.tox.tox4j.core.exceptions.ToxFileControlException;
import im.tox.tox4j.core.exceptions.ToxFileGetException;
import im.tox.tox4j.core.exceptions.ToxFileSeekException;
import im.tox.tox4j.core.exceptions.ToxFileSendChunkException;
import im.tox.tox4j.core.exceptions.ToxFileSendException;
import im.tox.tox4j.core.exceptions.ToxFriendAddException;
import im.tox.tox4j.core.exceptions.ToxFriendByPublicKeyException;
import im.tox.tox4j.core.exceptions.ToxFriendCustomPacketException;
import im.tox.tox4j.core.exceptions.ToxFriendDeleteException;
import im.tox.tox4j.core.exceptions.ToxFriendGetPublicKeyException;
import im.tox.tox4j.core.exceptions.ToxFriendSendMessageException;
import im.tox.tox4j.core.exceptions.ToxGetPortException;
import im.tox.tox4j.core.exceptions.ToxNewException;
import im.tox.tox4j.core.exceptions.ToxSetInfoException;
import im.tox.tox4j.core.exceptions.ToxSetTypingException;

@SuppressWarnings({"checkstyle:emptylineseparator", "checkstyle:linelength"})
public final class ToxCoreJni {
  static {
    System.loadLibrary("tox4j-c");
  }

  static native int toxNew(boolean ipv6Enabled, boolean udpEnabled, boolean localDiscoveryEnabled,
      int proxyType, String proxyAddress, int proxyPort, int startPort, int endPort, int tcpPort,
      int saveDataType, byte[] saveData) throws ToxNewException;

  static native void toxKill(int instanceNumber);
  static native void toxFinalize(int instanceNumber);
  static native byte[] toxGetSavedata(int instanceNumber);
  static native void toxBootstrap(int instanceNumber, String address, int port, byte[] publicKey)
      throws ToxBootstrapException;
  static native void toxAddTcpRelay(int instanceNumber, String address, int port, byte[] publicKey)
      throws ToxBootstrapException;
  static native int toxSelfGetUdpPort(int instanceNumber) throws ToxGetPortException;
  static native int toxSelfGetTcpPort(int instanceNumber) throws ToxGetPortException;
  static native byte[] toxSelfGetDhtId(int instanceNumber);
  static native int toxIterationInterval(int instanceNumber);
  static native byte[] toxIterate(int instanceNumber);
  static native byte[] toxSelfGetPublicKey(int instanceNumber);
  static native byte[] toxSelfGetSecretKey(int instanceNumber);
  static native void toxSelfSetNospam(int instanceNumber, int nospam);
  static native int toxSelfGetNospam(int instanceNumber);
  static native byte[] toxSelfGetAddress(int instanceNumber);
  static native void toxSelfSetName(int instanceNumber, byte[] name) throws ToxSetInfoException;
  static native byte[] toxSelfGetName(int instanceNumber);
  static native void toxSelfSetStatusMessage(int instanceNumber, byte[] message)
      throws ToxSetInfoException;
  static native byte[] toxSelfGetStatusMessage(int instanceNumber);
  static native void toxSelfSetStatus(int instanceNumber, int status);
  static native int toxSelfGetStatus(int instanceNumber);
  static native int toxFriendAdd(int instanceNumber, byte[] address, byte[] message)
      throws ToxFriendAddException;
  static native int toxFriendAddNorequest(int instanceNumber, byte[] publicKey)
      throws ToxFriendAddException;
  static native void toxFriendDelete(int instanceNumber, int friendNumber)
      throws ToxFriendDeleteException;
  static native int toxFriendByPublicKey(int instanceNumber, byte[] publicKey)
      throws ToxFriendByPublicKeyException;
  static native byte[] toxFriendGetPublicKey(int instanceNumber, int friendNumber)
      throws ToxFriendGetPublicKeyException;
  static native boolean toxFriendExists(int instanceNumber, int friendNumber);
  static native int[] toxSelfGetFriendList(int instanceNumber);
  static native void toxSelfSetTyping(int instanceNumber, int friendNumber, boolean typing)
      throws ToxSetTypingException;
  static native int toxFriendSendMessage(int instanceNumber, int friendNumber, int type,
      int timeDelta, byte[] message) throws ToxFriendSendMessageException;
  static native void toxFileControl(int instanceNumber, int friendNumber, int fileNumber,
      int control) throws ToxFileControlException;
  static native void toxFileSeek(int instanceNumber, int friendNumber, int fileNumber,
      long position) throws ToxFileSeekException;
  static native int toxFileSend(int instanceNumber, int friendNumber, int kind, long fileSize,
      byte[] fileId, byte[] filename) throws ToxFileSendException;
  static native void toxFileSendChunk(int instanceNumber, int friendNumber, int fileNumber,
      long position, byte[] data) throws ToxFileSendChunkException;
  static native byte[] toxFileGetFileId(int instanceNumber, int friendNumber, int fileNumber)
      throws ToxFileGetException;
  static native void toxFriendSendLossyPacket(int instanceNumber, int friendNumber, byte[] data)
      throws ToxFriendCustomPacketException;
  static native void toxFriendSendLosslessPacket(int instanceNumber, int friendNumber, byte[] data)
      throws ToxFriendCustomPacketException;
}
