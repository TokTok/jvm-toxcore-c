package im.tox.tox4j.core.options

import org.scalatest.FlatSpec

final class ToxOptionsTest extends FlatSpec {

  "tox options" should "not allow negative ports" in {
    intercept[IllegalArgumentException] {
      ToxOptions(startPort = -1)
    }
    intercept[IllegalArgumentException] {
      ToxOptions(endPort = -1)
    }
    intercept[IllegalArgumentException] {
      ToxOptions(tcpPort = -1)
    }
  }

  it should "allow the port to be 0" in {
    ToxOptions(startPort = 0, endPort = 0, tcpPort = 0)
  }

  it should "allow the port to be 65535" in {
    ToxOptions(startPort = 65535, endPort = 65535, tcpPort = 65535)
  }

  it should "not allow the port to be greater than 65535" in {
    intercept[IllegalArgumentException] {
      ToxOptions(startPort = 65536)
    }
    intercept[IllegalArgumentException] {
      ToxOptions(endPort = 65536)
    }
    intercept[IllegalArgumentException] {
      ToxOptions(tcpPort = 65536)
    }
  }

  it should "require startPort <= endPort" in {
    intercept[IllegalArgumentException] {
      ToxOptions(startPort = 2, endPort = 1)
    }
  }

}
