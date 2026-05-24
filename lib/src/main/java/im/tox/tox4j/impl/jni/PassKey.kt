package im.tox.tox4j.impl.jni

class PassKey internal constructor(
    internal val instanceNumber: Int,
) : AutoCloseable {
    override fun close(): Unit = ToxCryptoJni.toxPassKeyFree(instanceNumber)
}
