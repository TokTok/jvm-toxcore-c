package im.tox.tox4j.core

import im.tox.tox4j.crypto.ToxCryptoConstants
import org.scalatest.FunSuite

@SuppressWarnings(Array("org.wartremover.warts.Equals"))
final class ToxCoreConstantsTest extends FunSuite {

  test("MaxFriendRequestLength") {
    assert(ToxCoreConstants.MaxFriendRequestLength > 0)
    assert(ToxCoreConstants.MaxFriendRequestLength < ToxCoreConstants.MaxCustomPacketSize)
  }

  test("PublicKeySize") {
    assert(ToxCoreConstants.PublicKeySize == ToxCoreConstants.SecretKeySize)
    assert(ToxCoreConstants.PublicKeySize >= 32)
    assert(ToxCoreConstants.SecretKeySize >= 32)
  }

  test("MaxNameLength") {
    assert(ToxCoreConstants.MaxNameLength > 0)
    assert(ToxCoreConstants.MaxNameLength < ToxCoreConstants.MaxCustomPacketSize)
  }

  test("MaxFilenameLength") {
    assert(ToxCoreConstants.MaxFilenameLength > 0)
    assert(ToxCoreConstants.MaxFilenameLength < ToxCoreConstants.MaxCustomPacketSize)
  }

  test("MaxCustomPacketSize") {
    assert(ToxCoreConstants.MaxCustomPacketSize > 0)
    assert(ToxCoreConstants.MaxCustomPacketSize <= 1500) // Ethernet MTU
  }

  test("MaxHostnameLength") {
    assert(ToxCoreConstants.MaxHostnameLength > 0)
  }

  test("AddressSize") {
    assert(ToxCoreConstants.AddressSize >= ToxCoreConstants.AddressSize)
  }

  test("FileIdLength") {
    assert(ToxCoreConstants.FileIdLength >= ToxCryptoConstants.HashLength)
  }

  test("DefaultEndPort") {
    assert(ToxCoreConstants.DefaultEndPort >= 1)
    assert(ToxCoreConstants.DefaultEndPort <= 65535)
  }

  test("MaxMessageLength") {
    assert(ToxCoreConstants.MaxMessageLength > 0)
    assert(ToxCoreConstants.MaxMessageLength < ToxCoreConstants.MaxCustomPacketSize)
  }

  test("MaxStatusMessageLength") {
    assert(ToxCoreConstants.MaxStatusMessageLength > 0)
    assert(ToxCoreConstants.MaxStatusMessageLength < ToxCoreConstants.MaxCustomPacketSize)
  }

  test("DefaultTcpPort") {
    assert(ToxCoreConstants.DefaultTcpPort >= 0) // 0 means no TCP
    assert(ToxCoreConstants.DefaultTcpPort <= 65535)
  }

  test("DefaultStartPort") {
    assert(ToxCoreConstants.DefaultStartPort >= 1)
    assert(ToxCoreConstants.DefaultStartPort <= 65535)
  }

  test("DefaultProxyPort") {
    assert(ToxCoreConstants.DefaultProxyPort >= 1)
    assert(ToxCoreConstants.DefaultProxyPort <= 65535)
  }

}
