package im.tox.tox4j.impl.jni

import com.google.protobuf.InvalidProtocolBufferException
import com.typesafe.scalalogging.Logger
import im.tox.tox4j.impl.jni.proto._
import org.slf4j.LoggerFactory

import scala.concurrent.duration.{Duration, SECONDS}

/**
 * The JNI bridge logs every call made to toxcore and toxav functions along
 * with the time taken to execute in microseconds. See the message definitions
 * in ProtoLog.proto to get an idea of what can be done with this log.
 */
// scalastyle:off non.ascii.character.disallowed
object ToxJniLog extends (() => JniLog) {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  /**
   * Enable or disable logging. Logging is enabled by default, as it won't affect
   * performance adversely after the first [[maxSize]] messages.
   */
  def enabled(enabled: Boolean): Unit = ToxCoreJni.tox4jSetLogging(enabled)

  /**
   * Set the maximum number of entries in the log. After this limit is reached,
   * logging stops and ignores any further calls until the log is fetched and cleared.
   *
   * Set to 0 to disable logging.
   */
  def maxSize_=(maxSize: Int): Unit = ToxCoreJni.tox4jSetMaxLogSize(maxSize)
  def maxSize: Int = ToxCoreJni.tox4jGetMaxLogSize

  /**
   * Retrieve and clear the current call log. Calling [[ToxJniLog]] twice with no
   * native calls in between will return the empty log the second time. If logging
   * is disabled, this will always return the empty log.
   */
  def apply(): JniLog = {
    fromBytes(ToxCoreJni.tox4jLastLog())
  }

  /**
   * Parse a protobuf message from bytes to [[JniLog]].
   */
  def fromBytes(bytes: Array[Byte]): JniLog = {
    try {
      Option(bytes).map(JniLog.parseFrom).getOrElse(JniLog.defaultInstance)
    } catch {
      case e: InvalidProtocolBufferException =>
        logger.error(s"${e.getMessage}; unfinished message: ${e.getUnfinishedMessage}")
        JniLog.defaultInstance
    }
  }

  /**
   * Pretty-print the log as function calls with time offset from the first message. E.g.
   * [0.000000] tox_new_unique({udp_enabled=1; ipv6_enabled=0; ...}) [20 µs, #1]
   *
   * The last part is the time spent in the native function and the instance number.
   */
  def toString(log: JniLog): String = {
    log.entries.headOption match {
      case None => ""
      case Some(first) =>
        log.entries.map(toString(first.timestamp)).mkString("\n")
    }
  }

  private def formattedTimeDiff(a: TimeVal, b: TimeVal): String = {
    val timeDiff = {
      val seconds = a.seconds - b.seconds
      val micros = a.micros - b.micros
      if (micros < 0) {
        TimeVal(seconds - 1, micros + Duration(1, SECONDS).toMicros.toInt)
      } else {
        TimeVal(seconds, micros)
      }
    }

    f"${timeDiff.seconds}%d.${timeDiff.micros}%06d"
  }

  def toString(startTime: TimeVal)(entry: JniLogEntry): String = {
    s"[${formattedTimeDiff(entry.timestamp, startTime)}] ${entry.name}(${entry.arguments.map(toString).mkString(", ")}) = " +
      s"${toString(entry.result)} [${entry.elapsedMicros} µs" + {
        entry.instanceNumber match {
          case None                 => ""
          case Some(instanceNumber) => s", #$instanceNumber"
        }
      } + "]"
  }

  @SuppressWarnings(Array("org.brianmckenna.wartremover.warts.IsInstanceOf", "org.brianmckenna.wartremover.warts.AsInstanceOf"))
  def toString(value: Value): String = {
    value match {
      case Value(Some(sint64), None, None, Nil) => sint64.toString
      case Value(None, Some(string), None, Nil) => string
      case Value(None, None, Some(bytes), Nil)  => s"bytes[${bytes.size}]"
      case Value(None, None, None, Nil)         => "void"
      case Value(None, None, None, members)     => s"{${members.map(toString).mkString("; ")}}"
      case invalid                              => logger.error("Invalid oneof message: " + invalid); "<error>"
    }
  }

  def toString(member: Member): String = {
    s"${member.name}=${toString(member.value)}"
  }

}
