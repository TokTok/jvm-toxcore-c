package im.tox.tox4j.core

import im.tox.tox4j.ToxCoreTestBase
import im.tox.tox4j.core.data.{ToxFriendNumber, ToxFriendRequestMessage, ToxNickname, ToxStatusMessage}
import im.tox.tox4j.core.enums.ToxUserStatus
import im.tox.tox4j.core.options.{SaveDataOptions, ToxOptions}
import im.tox.tox4j.impl.jni.ToxCoreImplFactory.{withTox, withToxUnit}
import im.tox.tox4j.testing.GetDisjunction._
import org.scalatest.FunSuite

import scala.annotation.tailrec

final class LoadSaveTest extends FunSuite {

  private trait Check {
    def change(tox: ToxCore): Boolean
    def check(tox: ToxCore): Unit
  }

  @tailrec
  private def testLoadSave(check: Check): Unit = {
    val (continue, data) = withToxUnit { tox =>
      (check.change(tox), tox.getSavedata)
    }

    withToxUnit(SaveDataOptions.ToxSave(data)) { tox =>
      check.check(tox)
    }

    if (continue) {
      testLoadSave(check)
    }
  }

  test("Name") {
    testLoadSave(new Check() {
      private var expected = ToxNickname.unsafeFromValue(null)

      override def change(tox: ToxCore): Boolean = {
        expected =
          if (expected.value == null) {
            ToxNickname.fromString("").get
          } else {
            ToxNickname.fromValue(ToxCoreTestBase.randomBytes(expected.value.length + 1)).get
          }
        tox.setName(expected)
        expected.value.length < ToxCoreConstants.MaxNameLength
      }

      override def check(tox: ToxCore): Unit = {
        assert(tox.getName.value sameElements expected.value)
      }
    })
  }

  test("StatusMessage") {
    testLoadSave(new Check() {
      private var expected = ToxStatusMessage.unsafeFromValue(null)

      override def change(tox: ToxCore): Boolean = {
        if (expected.value == null) {
          expected = ToxStatusMessage.fromString("").get
        } else {
          expected = ToxStatusMessage.fromValue(ToxCoreTestBase.randomBytes(expected.value.length + 1)).get
        }
        tox.setStatusMessage(expected)
        expected.value.length < ToxCoreConstants.MaxNameLength
      }

      override def check(tox: ToxCore): Unit = {
        assert(tox.getStatusMessage.value sameElements expected.value)
      }
    })
  }

  test("Status") {
    testLoadSave(new Check() {
      private var expected = ToxUserStatus.values()

      override def change(tox: ToxCore): Boolean = {
        tox.setStatus(expected.head)
        expected.length > 1
      }

      override def check(tox: ToxCore): Unit = {
        assert(tox.getStatus == expected.head)
        expected = expected.tail
      }
    })
  }

  test("NoSpam") {
    testLoadSave(new Check() {
      private var expected = -1

      override def change(tox: ToxCore): Boolean = {
        expected += 1
        tox.setNospam(expected)
        expected < 100
      }

      override def check(tox: ToxCore): Unit = {
        assert(tox.getNospam == expected)
      }
    })
  }

  test("Friend") {
    testLoadSave(new Check() {
      private var expected = ToxFriendNumber.fromInt(1).get

      override def change(tox: ToxCore): Boolean = {
        withToxUnit { toxFriend =>
          expected = tox.addFriend(
            toxFriend.getAddress,
            ToxFriendRequestMessage.fromString("hello").get
          )
        }
        false
      }

      override def check(tox: ToxCore): Unit = {
        assert(tox.getFriendNumbers.length == 1)
        assert(tox.getFriendNumbers(0) == expected)
      }
    })
  }

  test("SaveNotEmpty") {
    withToxUnit { tox =>
      val data = tox.getSavedata
      assert(data != null)
      assert(data.nonEmpty)
    }
  }

  test("SaveRepeatable") {
    withToxUnit { tox =>
      assert(tox.getSavedata sameElements tox.getSavedata)
    }
  }

  test("LoadSave1") {
    withToxUnit { tox =>
      val data = tox.getSavedata
      val data1 = withToxUnit(SaveDataOptions.ToxSave(data)) { tox1 =>
        tox1.getSavedata
      }
      val data2 = withToxUnit(SaveDataOptions.ToxSave(data)) { tox2 =>
        tox2.getSavedata
      }
      assert(data1 sameElements data2)
    }
  }

  test("LoadSave2") {
    withToxUnit { tox =>
      val data = tox.getSavedata
      withToxUnit(SaveDataOptions.ToxSave(data)) { tox1 =>
        assert(tox1.getSavedata.length == data.length)
      }
    }
  }

  test("LoadSave3") {
    withToxUnit { tox =>
      val data = tox.getSavedata
      withToxUnit(SaveDataOptions.ToxSave(data)) { tox1 =>
        assert(tox1.getSavedata sameElements data)
      }
    }
  }

  test("LoadSave4") {
    withToxUnit { tox1 =>
      val data = tox1.getSecretKey
      withToxUnit(SaveDataOptions.SecretKey(data)) { tox2 =>
        assert(tox1.getSecretKey.value sameElements tox2.getSecretKey.value)
        assert(tox1.getPublicKey.value sameElements tox2.getPublicKey.value)
      }
    }
  }

  test("LoadSave5") {
    withToxUnit { tox1 =>
      val data = tox1.getSecretKey
      withTox(tox1.load(ToxOptions(saveData = SaveDataOptions.SecretKey(data)))) { tox2 =>
        assert(tox1.getSecretKey.value sameElements tox2.getSecretKey.value)
        assert(tox1.getPublicKey.value sameElements tox2.getPublicKey.value)
      }
    }
  }

}
