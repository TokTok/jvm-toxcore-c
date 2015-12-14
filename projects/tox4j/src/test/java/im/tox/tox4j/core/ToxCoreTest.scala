package im.tox.tox4j.core

import im.tox.tox4j.core.SmallNat._
import im.tox.tox4j.core.callbacks.ToxCoreEventListener
import im.tox.tox4j.core.data.ToxFriendRequestMessage
import im.tox.tox4j.core.enums.ToxConnection
import im.tox.tox4j.impl.jni.{ToxCoreImpl, ToxCoreImplFactory}
import im.tox.tox4j.impl.jni.ToxCoreImplFactory.withToxUnit
import im.tox.tox4j.testing.GetDisjunction._
import org.scalatest.FlatSpec
import org.scalatest.prop.PropertyChecks

final class ToxCoreTest extends FlatSpec with PropertyChecks {

  "addFriend" should "return increasing friend numbers and increment the friend list size" in {
    forAll { (count: SmallNat, message: Array[Byte]) =>
      whenever(message.length >= 1 && message.length <= ToxCoreConstants.MaxFriendRequestLength) {
        withToxUnit { tox =>
          (0 until count) foreach { i =>
            withToxUnit { friend =>
              val friendNumber = tox.addFriend(
                friend.getAddress,
                ToxFriendRequestMessage.fromValue(message).get
              )
              assert(friendNumber == i)
            }
          }
          assert(tox.getFriendList.length == count.self)
        }
      }
    }
  }

  "iterate" should "not be stopped by exceptions" in {
    withToxUnit(fatalErrors = false) { tox =>
      tox.callback(new ToxCoreEventListener[Unit] {
        override def selfConnectionStatus(connectionStatus: ToxConnection)(state: Unit): Unit = {
          throw new RuntimeException("This exception is expected; ignore it")
        }
      })
      tox.asInstanceOf[ToxCoreImpl[Unit]].invokeSelfConnectionStatus(ToxConnection.NONE)
      tox.iterate(())
    }
  }

  it should "be stopped by fatal VM errors" in {
    withToxUnit(fatalErrors = false) { tox =>
      tox.callback(new ToxCoreEventListener[Unit] {
        override def selfConnectionStatus(connectionStatus: ToxConnection)(state: Unit): Unit = {
          throw new StackOverflowError
        }
      })
      tox.asInstanceOf[ToxCoreImpl[Unit]].invokeSelfConnectionStatus(ToxConnection.NONE)
      intercept[StackOverflowError] {
        tox.iterate(())
      }
    }
  }

  "onClose callbacks" should "have been called after close" in {
    var called = false
    withToxUnit { tox =>
      tox.asInstanceOf[ToxCoreImpl[Unit]].addOnCloseCallback { () =>
        called = true
      }
    }
    assert(called)
  }

  they should "not be called before close" in {
    var called = false
    withToxUnit { tox =>
      tox.asInstanceOf[ToxCoreImpl[Unit]].addOnCloseCallback { () =>
        called = true
      }
      assert(!called)
    }
  }

  they should "not be called if they were unregistered" in {
    var called = false
    withToxUnit { tox =>
      val toxImpl = tox.asInstanceOf[ToxCoreImpl[Unit]]
      val id = toxImpl.addOnCloseCallback { () =>
        called = true
      }
      toxImpl.removeOnCloseCallback(id)
    }
    assert(!called)
  }

}
