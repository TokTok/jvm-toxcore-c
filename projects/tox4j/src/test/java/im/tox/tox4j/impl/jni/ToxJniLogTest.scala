package im.tox.tox4j.impl.jni

import im.tox.tox4j.impl.jni.proto.JniLog
import org.scalacheck.Gen
import org.scalatest.FunSuite
import org.scalatest.prop.PropertyChecks

final class ToxJniLogTest extends FunSuite with PropertyChecks {

  private val TestMaxSize = 100

  test("constructing and destroying a Tox instance with logging enabled should result in a non-empty log") {
    ToxJniLog() // clear

    ToxJniLog.maxSize = TestMaxSize
    assert(ToxJniLog.maxSize == TestMaxSize)
    assert(ToxJniLog().entries.isEmpty)
    // Construct and destroy a Tox instance to cause something (tox_new) to be logged and the log
    // will be non-empty.
    ToxCoreImplFactory.withToxUnit { tox => }
    assert(ToxJniLog().entries.nonEmpty)
  }

  test("constructing and destroying a Tox instance with logging disabled should result in an empty log") {
    ToxJniLog() // clear

    ToxJniLog.maxSize = 0
    assert(ToxJniLog.maxSize == 0)
    assert(ToxJniLog().entries.isEmpty)
    ToxCoreImplFactory.withToxUnit { tox => }
    assert(ToxJniLog().entries.isEmpty)
  }

  test("one log entry per native call") {
    ToxJniLog() // clear

    ToxJniLog.maxSize = TestMaxSize
    assert(ToxJniLog().entries.isEmpty)

    ToxCoreImplFactory.withToxUnit { tox => }
    val count1 = ToxJniLog().entries.size

    ToxCoreImplFactory.withToxUnit { tox => tox.friendExists(0) }
    val count2 = ToxJniLog().entries.size

    assert(count2 == count1 + 1)
  }

  test("null protobufs are ignored") {
    assert(ToxJniLog.fromBytes(null) == JniLog.defaultInstance)
  }

  test("invalid protobufs are ignored") {
    forAll { (bytes: Array[Byte]) =>
      assert(ToxJniLog.fromBytes(bytes) == JniLog.defaultInstance)
    }
  }

  // TODO(iphydf): Enable this when javaConversions in protobufConfig can be enabled.
  /*
  test("Java conversions") {
    ToxJniLog() // clear

    ToxJniLog.maxSize = TestMaxSize
    assert(ToxJniLog().entries.isEmpty)
    ToxCoreFactory.withTox { tox => }

    val log = ToxJniLog()
    assert(JniLog.fromJavaProto(JniLog.toJavaProto(log)) == log)
    assert(JniLog.fromAscii(log.toString) == log)
  }
  */

  test("concurrent logging works") {
    ToxJniLog() // clear
    ToxJniLog.maxSize = 10000

    forAll(Gen.choose(1, 99), Gen.choose(1, 100)) { (threadCount, iterations) =>
      val threads = for (_ <- 1 to threadCount) yield {
        new Thread {
          override def run(): Unit = {
            ToxCoreImplFactory.withToxUnit { tox =>
              for (_ <- 0 until iterations) {
                tox.friendExists(0)
              }
            }
          }
        }
      }

      threads.foreach(_.start())
      threads.foreach(_.join())

      val log = ToxJniLog()
      assert(log.entries.size < 10000)
      assert(log.entries.size == threadCount + threadCount * iterations)
      assert(ToxJniLog.toString(log).count(_ == '\n') == log.entries.size - 1)
    }

    assert(ToxJniLog().entries.isEmpty)
    ToxJniLog.maxSize = 0
  }

}
