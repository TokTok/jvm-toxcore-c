package im.tox.tox4j.impl.jni;

import im.tox.tox4j.core.exceptions.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"checkstyle:emptylineseparator", "checkstyle:linelength"})
public final class ToxCoreJni {
  static {
    System.loadLibrary("tox4j-c");
  }

  static native int toxNew(boolean ipv6Enabled, boolean udpEnabled, boolean localDiscoveryEnabled,
      int proxyType, @NotNull String proxyAddress, int proxyPort, int startPort, int endPort,
      int tcpPort, int saveDataType, @NotNull byte[] saveData) throws ToxNewException;

  static native void toxKill(int instanceNumber);
  static native void toxFinalize(int instanceNumber);
  @NotNull static native byte[] toxGetSavedata(int instanceNumber);
  static native void toxBootstrap(int instanceNumber, @NotNull String address, int port,
      @NotNull byte[] publicKey) throws ToxBootstrapException;
  static native void toxAddTcpRelay(int instanceNumber, @NotNull String address, int port,
      @NotNull byte[] publicKey) throws ToxBootstrapException;
  static native int toxSelfGetUdpPort(int instanceNumber) throws ToxGetPortException;
  static native int toxSelfGetTcpPort(int instanceNumber) throws ToxGetPortException;
  @NotNull static native byte[] toxSelfGetDhtId(int instanceNumber);
  static native int toxIterationInterval(int instanceNumber);
  @Nullable static native byte[] toxIterate(int instanceNumber);
  @NotNull static native byte[] toxSelfGetPublicKey(int instanceNumber);
  @NotNull static native byte[] toxSelfGetSecretKey(int instanceNumber);
  static native void toxSelfSetNospam(int instanceNumber, int nospam);
  static native int toxSelfGetNospam(int instanceNumber);
  @NotNull static native byte[] toxSelfGetAddress(int instanceNumber);
  static native void toxSelfSetName(int instanceNumber, @NotNull byte[] name)
      throws ToxSetInfoException;
  @NotNull static native byte[] toxSelfGetName(int instanceNumber);
  static native void toxSelfSetStatusMessage(int instanceNumber, byte[] message)
      throws ToxSetInfoException;
  @NotNull static native byte[] toxSelfGetStatusMessage(int instanceNumber);
  static native void toxSelfSetStatus(int instanceNumber, int status);
  static native int toxSelfGetStatus(int instanceNumber);
  static native int toxFriendAdd(int instanceNumber, @NotNull byte[] address,
      @NotNull byte[] message) throws ToxFriendAddException;
  static native int toxFriendAddNorequest(int instanceNumber, @NotNull byte[] publicKey)
      throws ToxFriendAddException;
  static native void toxFriendDelete(int instanceNumber, int friendNumber)
      throws ToxFriendDeleteException;
  static native int toxFriendByPublicKey(int instanceNumber, @NotNull byte[] publicKey)
      throws ToxFriendByPublicKeyException;
  @NotNull
  static native byte[] toxFriendGetPublicKey(int instanceNumber, int friendNumber)
      throws ToxFriendGetPublicKeyException;
  static native boolean toxFriendExists(int instanceNumber, int friendNumber);
  @NotNull static native int[] toxSelfGetFriendList(int instanceNumber);
  static native void toxSelfSetTyping(int instanceNumber, int friendNumber, boolean typing)
      throws ToxSetTypingException;
  static native int toxFriendSendMessage(int instanceNumber, int friendNumber, int type,
      int timeDelta, @NotNull byte[] message) throws ToxFriendSendMessageException;
  static native void toxFileControl(int instanceNumber, int friendNumber, int fileNumber,
      int control) throws ToxFileControlException;
  static native void toxFileSeek(int instanceNumber, int friendNumber, int fileNumber,
      long position) throws ToxFileSeekException;
  static native int toxFileSend(int instanceNumber, int friendNumber, int kind, long fileSize,
      @NotNull byte[] fileId, @NotNull byte[] filename) throws ToxFileSendException;
  static native void toxFileSendChunk(int instanceNumber, int friendNumber, int fileNumber,
      long position, @NotNull byte[] data) throws ToxFileSendChunkException;
  @NotNull
  static native byte[] toxFileGetFileId(int instanceNumber, int friendNumber, int fileNumber)
      throws ToxFileGetException;
  static native void toxFriendSendLossyPacket(int instanceNumber, int friendNumber,
      @NotNull byte[] data) throws ToxFriendCustomPacketException;
  static native void toxFriendSendLosslessPacket(int instanceNumber, int friendNumber,
      @NotNull byte[] data) throws ToxFriendCustomPacketException;

  static native byte[] tox4jLastLog();
  static native int tox4jGetCurrentLogSize();
  static native void tox4jSetMaxLogSize(int maxSize);
  static native int tox4jGetMaxLogSize();
  static native void tox4jSetLogFilter(String[] filter);
}
