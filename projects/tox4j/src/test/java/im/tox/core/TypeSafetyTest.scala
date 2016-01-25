package im.tox.core

import im.tox.core.crypto._
import org.scalatest.WordSpec
import scodec.bits.ByteVector

final class TypeSafetyTest extends WordSpec {

  s"no classes except ${PlainText.getClass}" should {
    "be instantiated outside the crypto module" in {
      val byteVector = ByteVector.empty
      val byteArray = Array.empty[Byte]
      assertCompiles("""new PlainText(byteVector)""")
      assertDoesNotCompile("""new CipherText(byteVector)""")
      assertDoesNotCompile("""new PublicKey(byteArray)""")
      assertDoesNotCompile("""new SecretKey(byteArray)""")
      assertDoesNotCompile("""new Nonce(byteArray)""")
    }
  }

}
