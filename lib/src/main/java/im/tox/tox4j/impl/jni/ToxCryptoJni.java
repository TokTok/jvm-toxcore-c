package im.tox.tox4j.impl.jni;

import im.tox.tox4j.crypto.exceptions.ToxDecryptionException;
import im.tox.tox4j.crypto.exceptions.ToxEncryptionException;
import im.tox.tox4j.crypto.exceptions.ToxGetSaltException;
import im.tox.tox4j.crypto.exceptions.ToxKeyDerivationException;

@SuppressWarnings({"checkstyle:emptylineseparator", "checkstyle:linelength"})
public final class ToxCryptoJni {
    static {
      System.loadLibrary("tox4j-c");
    }

    static native void toxPassKeyFree(int instanceNumber);
    static native int toxPassKeyDerive(byte[] passphrase) throws ToxKeyDerivationException;
    static native int toxPassKeyDeriveWithSalt(byte[] passphrase, byte[] salt) throws ToxKeyDerivationException;
    static native byte[] toxPassKeyEncrypt(int instanceNumber, byte[] plaintext) throws ToxEncryptionException;
    static native byte[] toxPassKeyDecrypt(int instanceNumber, byte[] ciphertext) throws ToxDecryptionException;
    static native byte[] toxGetSalt(byte[] ciphertext) throws ToxGetSaltException;
    static native boolean toxIsDataEncrypted(byte[] data);
    static native byte[] toxHash(byte[] data);
}
