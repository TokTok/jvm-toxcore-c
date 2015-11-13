package im.tox.tox4j

import im.tox.core.network.Port
import im.tox.core.random.RandomCore
import im.tox.tox4j.TestConstants.Iterations
import im.tox.tox4j.core.ToxCoreFactory.withTox
import im.tox.tox4j.core._
import im.tox.tox4j.core.enums.ToxUserStatus
import im.tox.tox4j.core.options.{ProxyOptions, ToxOptions}
import im.tox.tox4j.testing.ToxTestMixin
import org.scalatest.FunSuite

final class ToxCoreTest extends FunSuite with ToxTestMixin {

  val publicKey = ToxPublicKey.fromByteArray(Array.ofDim(ToxCoreConstants.PublicKeySize)).get

  test("ToxNew") {
    withTox(ToxOptions()) { _ => }
  }

  test("ToxNew00") {
    withTox(ipv6Enabled = false, udpEnabled = false) { _ => }
  }

  test("ToxNew01") {
    withTox(ipv6Enabled = false, udpEnabled = true) { _ => }
  }

  test("ToxNew10") {
    withTox(ipv6Enabled = true, udpEnabled = false) { _ => }
  }

  test("ToxNew11") {
    withTox(ipv6Enabled = true, udpEnabled = true) { _ => }
  }

  test("ToxNewProxyGood") {
    withTox(ipv6Enabled = true, udpEnabled = true, ProxyOptions.Socks5("localhost", 1)) { _ => }
    withTox(ipv6Enabled = true, udpEnabled = true, ProxyOptions.Socks5("localhost", 0xffff)) { _ => }
  }

  test("ToxCreationAndImmediateDestruction") {
    (0 until Iterations) foreach { _ => withTox { _ => } }
  }

  test("ToxCreationAndDelayedDestruction") {
    ToxCoreFactory.withToxes(30) { _ => }
  }

  test("DoubleClose") {
    withTox(_.close())
  }

  test("BootstrapBorderlinePort1") {
    withTox { tox =>
      tox.bootstrap(
        DhtNodeSelector.node.ipv4,
        Port.fromInt(1).get,
        publicKey
      )
    }
  }

  test("BootstrapBorderlinePort2") {
    withTox { tox =>
      tox.bootstrap(
        DhtNodeSelector.node.ipv4,
        Port.fromInt(65535).get,
        publicKey
      )
    }
  }

  test("IterationInterval") {
    withTox { tox =>
      assert(tox.iterationInterval > 0)
      assert(tox.iterationInterval <= 50)
    }
  }

  test("Close") {
    withTox { _ => }
  }

  test("Iteration") {
    withTox(_.iterate(()))
  }

  test("GetPublicKey") {
    withTox { tox =>
      val id = tox.getPublicKey
      assert(id.value.length == ToxCoreConstants.PublicKeySize)
      assert(tox.getPublicKey.value sameElements id.value)
    }
  }

  test("GetSecretKey") {
    withTox { tox =>
      val key = tox.getSecretKey
      assert(key.value.length == ToxCoreConstants.SecretKeySize)
      assert(tox.getSecretKey.value sameElements key.value)
    }
  }

  test("PublicKeyEntropy") {
    withTox { tox =>
      val entropy = RandomCore.entropy(tox.getPublicKey.value)
      assert(entropy >= 0.5, s"Entropy of public key should be >= 0.5, but was $entropy")
    }
  }

  test("SecretKeyEntropy") {
    withTox { tox =>
      val entropy = RandomCore.entropy(tox.getSecretKey.value)
      assert(entropy >= 0.5, s"Entropy of secret key should be >= 0.5, but was $entropy")
    }
  }

  test("GetAddress") {
    withTox { tox =>
      assert(tox.getAddress.value.length == ToxCoreConstants.AddressSize)
      assert(tox.getAddress.value sameElements tox.getAddress.value)
    }
  }

  test("NoSpam") {
    val tests = Array(0x12345678, 0xffffffff, 0x00000000, 0x00000001, 0xfffffffe, 0x7fffffff)
    withTox { tox =>
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
    withTox { tox =>
      assert(tox.getName.value.isEmpty)
      tox.setName(ToxNickname.fromString("myname").get)
      assert(new String(tox.getName.value) == "myname")
    }
  }

  test("SetNameMinSize") {
    withTox { tox =>
      val array = ToxCoreTestBase.randomBytes(1)
      tox.setName(ToxNickname.fromByteArray(array).get)
      assert(tox.getName.value sameElements array)
    }
  }

  test("SetNameMaxSize") {
    withTox { tox =>
      val array = ToxCoreTestBase.randomBytes(ToxCoreConstants.MaxNameLength)
      tox.setName(ToxNickname.fromByteArray(array).get)
      assert(tox.getName.value sameElements array)
    }
  }

  test("SetNameExhaustive") {
    withTox { tox =>
      (1 to ToxCoreConstants.MaxNameLength) foreach { i =>
        val array = ToxCoreTestBase.randomBytes(i)
        tox.setName(ToxNickname.fromByteArray(array).get)
        assert(tox.getName.value sameElements array)
      }
    }
  }

  test("UnsetName") {
    withTox { tox =>
      assert(tox.getName.value.isEmpty)
      tox.setName(ToxNickname.fromString("myname").get)
      assert(tox.getName.value.nonEmpty)
      tox.setName(ToxNickname.fromString("").get)
      assert(tox.getName.value.isEmpty)
    }
  }

  test("GetAndSetStatusMessage") {
    withTox { tox =>
      assert(tox.getStatusMessage.value.isEmpty)
      tox.setStatusMessage(ToxStatusMessage.fromString("message").get)
      assert(new String(tox.getStatusMessage.value) == "message")
    }
  }

  test("SetStatusMessageMinSize") {
    withTox { tox =>
      val array = ToxCoreTestBase.randomBytes(1)
      tox.setStatusMessage(ToxStatusMessage.fromByteArray(array).get)
      assert(tox.getStatusMessage.value sameElements array)
    }
  }

  test("SetStatusMessageMaxSize") {
    withTox { tox =>
      val array = ToxCoreTestBase.randomBytes(ToxCoreConstants.MaxStatusMessageLength)
      tox.setStatusMessage(ToxStatusMessage.fromByteArray(array).get)
      assert(tox.getStatusMessage.value sameElements array)
    }
  }

  test("SetStatusMessageExhaustive") {
    withTox { tox =>
      (1 to ToxCoreConstants.MaxStatusMessageLength) foreach { i =>
        val array = ToxCoreTestBase.randomBytes(i)
        tox.setStatusMessage(ToxStatusMessage.fromByteArray(array).get)
        assert(tox.getStatusMessage.value sameElements array)
      }
    }
  }

  test("UnsetStatusMessage") {
    withTox { tox =>
      assert(tox.getStatusMessage.value.isEmpty)
      tox.setStatusMessage(ToxStatusMessage.fromString("message").get)
      assert(tox.getStatusMessage.value.nonEmpty)
      tox.setStatusMessage(ToxStatusMessage.fromString("").get)
      assert(tox.getStatusMessage.value.isEmpty)
    }
  }

  test("GetAndSetStatus") {
    withTox { tox =>
      assert(tox.getStatus == ToxUserStatus.NONE)
      ToxUserStatus.values.foreach { status =>
        tox.setStatus(status)
        assert(tox.getStatus == status)
      }
    }
  }

  test("AddFriend") {
    withTox { tox =>
      (0 until Iterations) foreach { i =>
        withTox { friend =>
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
    withTox { tox =>
      (0 until Iterations) foreach { i =>
        withTox { friend =>
          val friendNumber = tox.addFriendNorequest(friend.getPublicKey)
          assert(friendNumber == i)
        }
      }
      assert(tox.getFriendList.length == Iterations)
    }
  }

  test("FriendListSize") {
    withTox { tox =>
      addFriends(tox, Iterations)
      assert(tox.getFriendList.length == Iterations)
    }
  }

  test("FriendList") {
    withTox { tox =>
      addFriends(tox, 5)
      assert(tox.getFriendList sameElements Array(0, 1, 2, 3, 4))
    }
  }

  test("FriendList_Empty") {
    withTox { tox =>
      assert(tox.getFriendList.isEmpty)
    }
  }

  test("DeleteAndReAddFriend") {
    withTox { tox =>
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
    withTox { tox =>
      addFriends(tox, 3)
      assert(tox.friendExists(0))
      assert(tox.friendExists(1))
      assert(tox.friendExists(2))
      assert(!tox.friendExists(3))
      assert(!tox.friendExists(4))
    }
  }

  test("FriendExists2") {
    withTox { tox =>
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
    withTox { tox =>
      addFriends(tox, 1)
      assert(tox.getFriendPublicKey(0).value.length == ToxCoreConstants.PublicKeySize)
      assert(tox.getFriendPublicKey(0).value sameElements tox.getFriendPublicKey(0).value)
      val entropy = RandomCore.entropy(tox.getFriendPublicKey(0).value)
      assert(entropy >= 0.5, s"Entropy of friend's public key should be >= 0.5, but was $entropy")
    }
  }

  test("GetFriendByPublicKey") {
    withTox { tox =>
      addFriends(tox, 10)
      (0 until 10) foreach { i =>
        assert(tox.friendByPublicKey(tox.getFriendPublicKey(i)) == i)
      }
    }
  }

  test("SetTyping") {
    withTox { tox =>
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
    withTox { tox =>
      assert(tox.getUdpPort.value > 0)
      assert(tox.getUdpPort.value <= 65535)
    }
  }

  test("GetTcpPort") {
    withTox(ToxOptions(tcpPort = 33444)) { tox =>
      assert(tox.getTcpPort.value == 33444)
    }
  }

  test("GetDhtId") {
    withTox { tox =>
      val key = tox.getDhtId
      assert(key.value.length == ToxCoreConstants.PublicKeySize)
      assert(tox.getDhtId.value sameElements key.value)
    }
  }

  test("DhtIdEntropy") {
    withTox { tox =>
      val entropy = RandomCore.entropy(tox.getDhtId.value)
      assert(entropy >= 0.5, s"Entropy of public key should be >= 0.5, but was $entropy")
    }
  }

}
