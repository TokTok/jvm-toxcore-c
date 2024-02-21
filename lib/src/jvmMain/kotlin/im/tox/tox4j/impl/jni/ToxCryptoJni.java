package im.tox.tox4j.impl.jni;

@SuppressWarnings({"checkstyle:emptylineseparator", "checkstyle:linelength"})
public final class ToxCryptoJni {
  static {
    System.loadLibrary("tox4j-c");
  }

  static native byte[] toxPassKeyEncrypt(byte[] data, byte[] passKey);
  static native byte[] toxGetSalt(byte[] data);
  static native boolean toxIsDataEncrypted(byte[] data);
  static native byte[] toxPassKeyDeriveWithSalt(byte[] passphrase, byte[] salt);
  static native byte[] toxPassKeyDerive(byte[] passphrase);
  static native byte[] toxPassKeyDecrypt(byte[] data, byte[] passKey);
  static native byte[] toxHash(byte[] data);
}
