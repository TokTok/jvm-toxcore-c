package im.tox.tox4j.core.exceptions

import im.tox.tox4j.core.options.{ProxyOptions, SaveDataOptions}
import im.tox.tox4j.impl.jni.ToxCoreImplFactory
import im.tox.tox4j.impl.jni.ToxCoreImplFactory.{withToxUnit, withToxes}
import im.tox.tox4j.testing.ToxTestMixin
import org.scalatest.FunSuite

final class ToxNewExceptionTest extends FunSuite with ToxTestMixin {

  test("ToxNewProxyNull") {
    intercept(ToxNewException.Code.PROXY_BAD_HOST) {
      withToxUnit(ipv6Enabled = true, udpEnabled = true, new ProxyOptions.Socks5(null, 1)) { _ => }
    }
  }

  test("ToxNewProxyEmpty") {
    intercept(ToxNewException.Code.PROXY_BAD_HOST) {
      withToxUnit(ipv6Enabled = true, udpEnabled = true, new ProxyOptions.Socks5("", 1)) { _ => }
    }
  }

  test("ToxNewProxyBadPort0") {
    intercept(ToxNewException.Code.PROXY_BAD_PORT) {
      withToxUnit(ipv6Enabled = true, udpEnabled = true, new ProxyOptions.Socks5("localhost", 0)) { _ => }
    }
  }

  test("ToxNewProxyBadPortNegative") {
    intercept[IllegalArgumentException] {
      withToxUnit(ipv6Enabled = true, udpEnabled = true, new ProxyOptions.Socks5("localhost", -10)) { _ => }
    }
  }

  test("ToxNewProxyBadPortTooLarge") {
    intercept[IllegalArgumentException] {
      withToxUnit(ipv6Enabled = true, udpEnabled = true, new ProxyOptions.Socks5("localhost", 0x10000)) { _ => }
    }
  }

  test("ToxNewProxyBadAddress1") {
    intercept(ToxNewException.Code.PROXY_BAD_HOST) {
      val host = "\u2639" // scalastyle:ignore non.ascii.character.disallowed
      withToxUnit(ipv6Enabled = true, udpEnabled = true, new ProxyOptions.Socks5(host, 1)) { _ => }
    }
  }

  test("ToxNewProxyBadAddress2") {
    intercept(ToxNewException.Code.PROXY_BAD_HOST) {
      withToxUnit(ipv6Enabled = true, udpEnabled = true, new ProxyOptions.Socks5(".", 1)) { _ => }
    }
  }

  test("TooManyToxCreations") {
    intercept(ToxNewException.Code.PORT_ALLOC) {
      withToxes(102) { _ => }
    }
  }

  test("LoadEncrypted") {
    intercept(ToxNewException.Code.LOAD_ENCRYPTED) {
      withToxUnit(SaveDataOptions.ToxSave("toxEsave blah blah blah".getBytes)) { _ => }
    }
  }

  test("LoadBadFormat") {
    intercept(ToxNewException.Code.LOAD_BAD_FORMAT) {
      withToxUnit(SaveDataOptions.ToxSave("blah blah blah".getBytes)) { _ => }
    }
  }

}
