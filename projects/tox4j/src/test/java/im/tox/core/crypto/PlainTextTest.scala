package im.tox.core.crypto

import im.tox.core.ModuleCompanionTest
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import scodec.bits.ByteVector

object PlainTextTest {

  implicit val arbPlainText: Arbitrary[PlainText] =
    Arbitrary(arbitrary[Array[Byte]].map(bytes => PlainText(ByteVector.view(bytes))))

}

final class PlainTextTest extends ModuleCompanionTest[PlainText](PlainText) {

  override val arbT = PlainTextTest.arbPlainText

}
