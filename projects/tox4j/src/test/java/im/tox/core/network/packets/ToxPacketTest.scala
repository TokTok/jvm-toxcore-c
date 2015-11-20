package im.tox.core.network.packets

import im.tox.core.ModuleCompanionTest
import im.tox.core.crypto.PlainText
import im.tox.core.crypto.PlainTextTest._
import im.tox.core.network.PacketKind
import im.tox.core.network.PacketKindTest._
import im.tox.core.typesafe.Security
import org.scalacheck.Arbitrary._
import org.scalacheck.{Arbitrary, Gen}

object ToxPacketTest {

  implicit val arbPacket: Arbitrary[ToxPacket[PacketKind]] =
    Arbitrary(
      Gen.resultOf[(PacketKind, PlainText[Security.NonSensitive]), ToxPacket[PacketKind]] {
        case (kind, payload) =>
          ToxPacket(kind, payload)
      }
    )

}

final class ToxPacketTest extends ModuleCompanionTest(ToxPacket) {

  override val arbT = ToxPacketTest.arbPacket

}
