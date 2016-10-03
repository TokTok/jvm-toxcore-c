package im.tox.tox4j.core.exceptions

import im.tox.core.network.Port
import im.tox.tox4j.core.ToxCoreConstants
import im.tox.tox4j.core.data.ToxPublicKey
import im.tox.tox4j.testing.ToxTestMixin
import org.scalatest.FunSuite

final class ToxBootstrapExceptionTest extends FunSuite with ToxTestMixin {

  val host = "192.254.75.98"
  val publicKey = ToxPublicKey.fromValue(Array.ofDim(ToxCoreConstants.PublicKeySize)).get
  val port = Port.fromInt(ToxCoreConstants.DefaultStartPort).get

  test("BootstrapBadPort1") {
    interceptWithTox(ToxBootstrapException.Code.BAD_PORT)(
      _.bootstrap(
        host,
        Port.unsafeFromInt(0),
        publicKey
      )
    )
  }

  test("BootstrapBadPort2") {
    interceptWithTox(ToxBootstrapException.Code.BAD_PORT)(
      _.bootstrap(
        host,
        Port.unsafeFromInt(-10),
        publicKey
      )
    )
  }

  test("BootstrapBadPort3") {
    interceptWithTox(ToxBootstrapException.Code.BAD_PORT)(
      _.bootstrap(
        host,
        Port.unsafeFromInt(65536),
        publicKey
      )
    )
  }

  test("BootstrapBadHost") {
    interceptWithTox(ToxBootstrapException.Code.BAD_HOST)(
      _.bootstrap(
        ".",
        port,
        publicKey
      )
    )
  }

  test("BootstrapNullHost") {
    interceptWithTox(ToxBootstrapException.Code.NULL)(
      _.bootstrap(
        null,
        port,
        publicKey
      )
    )
  }

  test("BootstrapNullKey") {
    interceptWithTox(ToxBootstrapException.Code.NULL)(
      _.bootstrap(
        host,
        port,
        ToxPublicKey.unsafeFromValue(null)
      )
    )
  }

  test("BootstrapKeyTooShort") {
    interceptWithTox(ToxBootstrapException.Code.BAD_KEY)(
      _.bootstrap(
        host,
        port,
        ToxPublicKey.unsafeFromValue(Array.ofDim(ToxCoreConstants.PublicKeySize - 1))
      )
    )
  }

  test("BootstrapKeyTooLong") {
    interceptWithTox(ToxBootstrapException.Code.BAD_KEY)(
      _.bootstrap(
        host,
        port,
        ToxPublicKey.unsafeFromValue(Array.ofDim(ToxCoreConstants.PublicKeySize + 1))
      )
    )
  }

}
