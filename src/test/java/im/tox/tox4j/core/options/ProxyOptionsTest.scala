package im.tox.tox4j.core.options

import im.tox.tox4j.core.enums.ToxProxyType
import org.scalatest.FlatSpec

@SuppressWarnings(Array("org.wartremover.warts.Equals"))
final class ProxyOptionsTest extends FlatSpec {

  "proxy options" should "not allow negative ports" in {
    intercept[IllegalArgumentException] {
      ProxyOptions.Http("localhost", -1)
    }
    intercept[IllegalArgumentException] {
      ProxyOptions.Socks5("localhost", -1)
    }
  }

  it should "allow the port to be 0" in {
    ProxyOptions.Http("localhost", 0)
    ProxyOptions.Socks5("localhost", 0)
  }

  it should "allow the port to be 65535" in {
    ProxyOptions.Http("localhost", 65535)
    ProxyOptions.Socks5("localhost", 65535)
  }

  it should "not allow the port to be greater than 65535" in {
    intercept[IllegalArgumentException] {
      ProxyOptions.Http("localhost", 65536)
    }
    intercept[IllegalArgumentException] {
      ProxyOptions.Socks5("localhost", 65536)
    }
  }

  it should "produce the right low level enum values" in {
    assert(ProxyOptions.None.proxyType == ToxProxyType.NONE)
    assert(ProxyOptions.Http("localhost", 1).proxyType == ToxProxyType.HTTP)
    assert(ProxyOptions.Socks5("localhost", 1).proxyType == ToxProxyType.SOCKS5)
  }

}
