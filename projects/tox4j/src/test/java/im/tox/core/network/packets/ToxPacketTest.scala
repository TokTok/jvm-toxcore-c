package im.tox.core.network.packets

import im.tox.core.ModuleCompanionTest
import im.tox.core.crypto.PlainText
import im.tox.core.crypto.PlainTextTest._
import im.tox.core.network.PacketKind
import im.tox.core.network.PacketKindTest._
import org.scalacheck.Arbitrary._
import org.scalacheck.{Arbitrary, Gen}

object ToxPacketTest {

  implicit val arbPacket: Arbitrary[ToxPacket[PacketKind]] =
    Arbitrary(
      Gen.zip(
        arbitrary[PacketKind],
        arbitrary[PlainText]
      ).map {
        case (kind, payload) =>
          ToxPacket(kind, payload)
      }
    )

}

final class ToxPacketTest extends ModuleCompanionTest[ToxPacket[PacketKind]](ToxPacket) {

  override val arbT = ToxPacketTest.arbPacket

}
