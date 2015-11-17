package im.tox.core.crypto

object SecretKeyTest {

  def take(secretKey: SecretKey, maxSize: Int): SecretKey = {
    new SecretKey(secretKey.value.take(maxSize))
  }

}
