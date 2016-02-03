package im.tox.tox4j

import im.tox.core.network.Port
import im.tox.core.random.RandomCore
import im.tox.tox4j.TestConstants.Iterations
import im.tox.tox4j.core._
import im.tox.tox4j.core.data.{ToxFriendRequestMessage, ToxNickname, ToxPublicKey, ToxStatusMessage}
import im.tox.tox4j.core.enums.ToxUserStatus
import im.tox.tox4j.core.options.{ProxyOptions, ToxOptions}
import im.tox.tox4j.impl.jni.ToxCoreImplFactory
import im.tox.tox4j.impl.jni.ToxCoreImplFactory.withToxUnit
import im.tox.tox4j.testing.ToxTestMixin
import org.scalatest.FunSuite

final class ToxCoreTest extends FunSuite with ToxTestMixin {

  val publicKey = ToxPublicKey.fromValue(Array.ofDim(ToxCoreConstants.PublicKeySize)).get

  test("ToxNew") {
    withToxUnit(ToxOptions()) { _ => }
  }

  test("ToxNew00") {
    withToxUnit(ipv6Enabled = false, udpEnabled = false) { _ => }
  }

  test("ToxNew01") {
    withToxUnit(ipv6Enabled = false, udpEnabled = true) { _ => }
  }

  test("ToxNew10") {
    withToxUnit(ipv6Enabled = true, udpEnabled = false) { _ => }
  }

  test("ToxNew11") {
    withToxUnit(ipv6Enabled = true, udpEnabled = true) { _ => }
  }

  test("ToxNewProxyGood") {
    withToxUnit(ipv6Enabled = true, udpEnabled = true, ProxyOptions.Socks5("localhost", 1)) { _ => }
    withToxUnit(ipv6Enabled = true, udpEnabled = true, ProxyOptions.Socks5("localhost", 0xffff)) { _ => }
  }

  test("ToxCreationAndImmediateDestruction") {
    (0 until Iterations) foreach { _ => withToxUnit { _ => } }
  }

  test("ToxCreationAndDelayedDestruction") {
    ToxCoreImplFactory.withToxes(30) { _ => }
  }

  test("DoubleClose") {
    withToxUnit(_.close())
  }

  test("BootstrapBorderlinePort1") {
    withToxUnit { tox =>
      tox.bootstrap(
        DhtNodeSelector.node.ipv4,
        Port.fromInt(1).get,
        publicKey
      )
    }
  }

  test("BootstrapBorderlinePort2") {
    withToxUnit { tox =>
      tox.bootstrap(
        DhtNodeSelector.node.ipv4,
        Port.fromInt(65535).get,
        publicKey
      )
    }
  }

  test("IterationInterval") {
    withToxUnit { tox =>
      assert(tox.iterationInterval > 0)
      assert(tox.iterationInterval <= 50)
    }
  }

  test("Close") {
    withToxUnit { _ => }
  }

  test("Iteration") {
    withToxUnit(_.iterate(()))
  }

  test("GetPublicKey") {
    withToxUnit { tox =>
      val id = tox.getPublicKey
      assert(id.value.length == ToxCoreConstants.PublicKeySize)
      assert(tox.getPublicKey.value sameElements id.value)
    }
  }

  test("GetSecretKey") {
    withToxUnit { tox =>
      val key = tox.getSecretKey
      assert(key.value.length == ToxCoreConstants.SecretKeySize)
      assert(tox.getSecretKey.value sameElements key.value)
    }
  }

  test("PublicKeyEntropy") {
    withToxUnit { tox =>
      val entropy = RandomCore.entropy(tox.getPublicKey.value)
      assert(entropy >= 0.5, s"Entropy of public key should be >= 0.5, but was $entropy")
    }
  }

  test("SecretKeyEntropy") {
    withToxUnit { tox =>
      val entropy = RandomCore.entropy(tox.getSecretKey.value)
      assert(entropy >= 0.5, s"Entropy of secret key should be >= 0.5, but was $entropy")
    }
  }

  test("GetAddress") {
    withToxUnit { tox =>
      assert(tox.getAddress.value.length == ToxCoreConstants.AddressSize)
      assert(tox.getAddress.value sameElements tox.getAddress.value)
    }
  }

  test("NoSpam") {
    val tests = Array(0x12345678, 0xffffffff, 0x00000000, 0x00000001, 0xfffffffe, 0x7fffffff)
    withToxUnit { tox =>
      assert(tox.getNospam == tox.getNospam)
      for (test <- tests) {
        tox.setNospam(test)
        assert(tox.getNospam == test)
        assert(tox.getNospam == tox.getNospam)
        val check = Array(
          (test >> 8 * 0).toByte,
          (test >> 8 * 1).toByte,
          (test >> 8 * 2).toByte,
          (test >> 8 * 3).toByte
        )
        val nospam: Array[Byte] = tox.getAddress.value.slice(ToxCoreConstants.PublicKeySize, ToxCoreConstants.PublicKeySize + 4)
        assert(nospam sameElements check)
      }
    }
  }

  test("GetAndSetName") {
    withToxUnit { tox =>
      assert(tox.getName.value.isEmpty)
      tox.setName(ToxNickname.fromString("myname").get)
      assert(new String(tox.getName.value) == "myname")
    }
  }

  test("SetNameMinSize") {
    withToxUnit { tox =>
      val array = ToxCoreTestBase.randomBytes(1)
      tox.setName(ToxNickname.fromValue(array).get)
      assert(tox.getName.value sameElements array)
    }
  }

  test("SetNameMaxSize") {
    withToxUnit { tox =>
      val array = ToxCoreTestBase.randomBytes(ToxCoreConstants.MaxNameLength)
      tox.setName(ToxNickname.fromValue(array).get)
      assert(tox.getName.value sameElements array)
    }
  }

  test("SetNameExhaustive") {
    withToxUnit { tox =>
      (1 to ToxCoreConstants.MaxNameLength) foreach { i =>
        val array = ToxCoreTestBase.randomBytes(i)
        tox.setName(ToxNickname.fromValue(array).get)
        assert(tox.getName.value sameElements array)
      }
    }
  }

  test("UnsetName") {
    withToxUnit { tox =>
      assert(tox.getName.value.isEmpty)
      tox.setName(ToxNickname.fromString("myname").get)
      assert(tox.getName.value.nonEmpty)
      tox.setName(ToxNickname.fromString("").get)
      assert(tox.getName.value.isEmpty)
    }
  }

  test("GetAndSetStatusMessage") {
    withToxUnit { tox =>
      assert(tox.getStatusMessage.value.isEmpty)
      tox.setStatusMessage(ToxStatusMessage.fromString("message").get)
      assert(new String(tox.getStatusMessage.value) == "message")
    }
  }

  test("SetStatusMessageMinSize") {
    withToxUnit { tox =>
      val array = ToxCoreTestBase.randomBytes(1)
      tox.setStatusMessage(ToxStatusMessage.fromValue(array).get)
      assert(tox.getStatusMessage.value sameElements array)
    }
  }

  test("SetStatusMessageMaxSize") {
    withToxUnit { tox =>
      val array = ToxCoreTestBase.randomBytes(ToxCoreConstants.MaxStatusMessageLength)
      tox.setStatusMessage(ToxStatusMessage.fromValue(array).get)
      assert(tox.getStatusMessage.value sameElements array)
    }
  }

  test("SetStatusMessageExhaustive") {
    withToxUnit { tox =>
      (1 to ToxCoreConstants.MaxStatusMessageLength) foreach { i =>
        val array = ToxCoreTestBase.randomBytes(i)
        tox.setStatusMessage(ToxStatusMessage.fromValue(array).get)
        assert(tox.getStatusMessage.value sameElements array)
      }
    }
  }

  test("UnsetStatusMessage") {
    withToxUnit { tox =>
      assert(tox.getStatusMessage.value.isEmpty)
      tox.setStatusMessage(ToxStatusMessage.fromString("message").get)
      assert(tox.getStatusMessage.value.nonEmpty)
      tox.setStatusMessage(ToxStatusMessage.fromString("").get)
      assert(tox.getStatusMessage.value.isEmpty)
    }
  }

  test("GetAndSetStatus") {
    withToxUnit { tox =>
      assert(tox.getStatus == ToxUserStatus.NONE)
      ToxUserStatus.values.foreach { status =>
        tox.setStatus(status)
        assert(tox.getStatus == status)
      }
    }
  }

  test("AddFriend") {
    withToxUnit { tox =>
      (0 until Iterations) foreach { i =>
        withToxUnit { friend =>
          val friendNumber = tox.addFriend(
            friend.getAddress,
            ToxFriendRequestMessage.fromString("heyo").get
          )
          assert(friendNumber == i)
        }
      }
      assert(tox.getFriendList.length == Iterations)
    }
  }

  test("AddFriendNoRequest") {
    withToxUnit { tox =>
      (0 until Iterations) foreach { i =>
        withToxUnit { friend =>
          val friendNumber = tox.addFriendNorequest(friend.getPublicKey)
          assert(friendNumber == i)
        }
      }
      assert(tox.getFriendList.length == Iterations)
    }
  }

  test("FriendListSize") {
    withToxUnit { tox =>
      addFriends(tox, Iterations)
      assert(tox.getFriendList.length == Iterations)
    }
  }

  test("FriendList") {
    withToxUnit { tox =>
      addFriends(tox, 5)
      assert(tox.getFriendList sameElements Array(0, 1, 2, 3, 4))
    }
  }

  test("FriendList_Empty") {
    withToxUnit { tox =>
      assert(tox.getFriendList.isEmpty)
    }
  }

  test("DeleteAndReAddFriend") {
    withToxUnit { tox =>
      addFriends(tox, 5)
      assert(tox.getFriendList sameElements Array[Int](0, 1, 2, 3, 4))
      tox.deleteFriend(2)
      assert(tox.getFriendList sameElements Array[Int](0, 1, 3, 4))
      tox.deleteFriend(3)
      assert(tox.getFriendList sameElements Array[Int](0, 1, 4))
      addFriends(tox, 1)
      assert(tox.getFriendList sameElements Array[Int](0, 1, 2, 4))
      addFriends(tox, 1)
      assert(tox.getFriendList sameElements Array[Int](0, 1, 2, 3, 4))
    }
  }

  test("FriendExists") {
    withToxUnit { tox =>
      addFriends(tox, 3)
      assert(tox.friendExists(0))
      assert(tox.friendExists(1))
      assert(tox.friendExists(2))
      assert(!tox.friendExists(3))
      assert(!tox.friendExists(4))
    }
  }

  test("FriendExists2") {
    withToxUnit { tox =>
      addFriends(tox, 3)
      assert(tox.friendExists(0))
      assert(tox.friendExists(1))
      assert(tox.friendExists(2))
      tox.deleteFriend(1)
      assert(tox.friendExists(0))
      assert(!tox.friendExists(1))
      assert(tox.friendExists(2))
    }
  }

  test("GetFriendPublicKey") {
    withToxUnit { tox =>
      addFriends(tox, 1)
      assert(tox.getFriendPublicKey(0).value.length == ToxCoreConstants.PublicKeySize)
      assert(tox.getFriendPublicKey(0).value sameElements tox.getFriendPublicKey(0).value)
      val entropy = RandomCore.entropy(tox.getFriendPublicKey(0).value)
      assert(entropy >= 0.5, s"Entropy of friend's public key should be >= 0.5, but was $entropy")
    }
  }

  test("GetFriendByPublicKey") {
    withToxUnit { tox =>
      addFriends(tox, 10)
      (0 until 10) foreach { i =>
        assert(tox.friendByPublicKey(tox.getFriendPublicKey(i)) == i)
      }
    }
  }

  test("SetTyping") {
    withToxUnit { tox =>
      addFriends(tox, 1)
      tox.setTyping(0, false)
      tox.setTyping(0, true)
      tox.setTyping(0, false)
      tox.setTyping(0, false)
      tox.setTyping(0, true)
      tox.setTyping(0, true)
    }
  }

  test("GetUdpPort") {
    withToxUnit { tox =>
      assert(tox.getUdpPort.value > 0)
      assert(tox.getUdpPort.value <= 65535)
    }
  }

  test("GetTcpPort") {
    withToxUnit(ToxOptions(tcpPort = 33444)) { tox =>
      assert(tox.getTcpPort.value == 33444)
    }
  }

  test("GetDhtId") {
    withToxUnit { tox =>
      val key = tox.getDhtId
      assert(key.value.length == ToxCoreConstants.PublicKeySize)
      assert(tox.getDhtId.value sameElements key.value)
    }
  }

  test("DhtIdEntropy") {
    withToxUnit { tox =>
      val entropy = RandomCore.entropy(tox.getDhtId.value)
      assert(entropy >= 0.5, s"Entropy of public key should be >= 0.5, but was $entropy")
    }
  }

}
