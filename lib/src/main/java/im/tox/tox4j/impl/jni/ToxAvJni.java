package im.tox.tox4j.impl.jni;

import im.tox.tox4j.av.exceptions.ToxAvAnswerException;
import im.tox.tox4j.av.exceptions.ToxAvBitRateSetException;
import im.tox.tox4j.av.exceptions.ToxAvCallControlException;
import im.tox.tox4j.av.exceptions.ToxAvCallException;
import im.tox.tox4j.av.exceptions.ToxAvNewException;
import im.tox.tox4j.av.exceptions.ToxAvSendFrameException;

@SuppressWarnings({"checkstyle:emptylineseparator", "checkstyle:linelength"})
public final class ToxAvJni {
    static {
      System.loadLibrary("tox4j-c");
    }

    static native int toxavNew(int instanceNumber) throws ToxAvNewException;
    static native void toxavKill(int instanceNumber);
    static native int toxavIterationInterval(int instanceNumber);
    static native byte[] toxavIterate(int instanceNumber);
    static native void toxavCall(int instanceNumber, int friendNumber, int audioBitRate, int videoBitRate) throws ToxAvCallException;
    static native void toxavAnswer(int instanceNumber, int friendNumber, int audioBitRate, int videoBitRate) throws ToxAvAnswerException;
    static native void toxavCallControl(int instanceNumber, int friendNumber, int control) throws ToxAvCallControlException;
    static native void toxavAudioSendFrame(int instanceNumber, int friendNumber, short[] pcm, long sampleCount, byte channels, int samplingRate) throws ToxAvSendFrameException;
    static native void toxavAudioSetBitRate(int instanceNumber, int friendNumber, int bitRate) throws ToxAvBitRateSetException;
    static native void toxavVideoSendFrame(int instanceNumber, int friendNumber, int width, int height, byte[] y, byte[] u, byte[] v) throws ToxAvSendFrameException;
    static native void toxavVideoSetBitRate(int instanceNumber, int friendNumber, int bitRate) throws ToxAvBitRateSetException;
    static native void toxavFinalize(int instanceNumber);
}
