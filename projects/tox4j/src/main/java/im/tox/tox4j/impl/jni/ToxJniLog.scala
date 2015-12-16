package im.tox.tox4j.impl.jni

import com.google.protobuf.InvalidProtocolBufferException
import com.typesafe.scalalogging.Logger
import im.tox.tox4j.impl.jni.proto.Value.V
import im.tox.tox4j.impl.jni.proto._
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import scala.language.postfixOps

/**
 * The JNI bridge logs every call made to toxcore and toxav functions along
 * with the time taken to execute in microseconds. See the message definitions
 * in ProtoLog.proto to get an idea of what can be done with this log.
 */
// scalastyle:off non.ascii.character.disallowed
case object ToxJniLog {

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
   * Parse a protobuf message from bytes to [[JniLog]]. Logs an error and returns
   * [[JniLog.defaultInstance]] if $bytes is invalid. Returns [[JniLog.defaultInstance]]
   * if $bytes is null.
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
   * The last part is the time spent in the native function followed by the instance number.
   */
  def toString(log: JniLog): String = {
    log.entries.headOption match {
      case None => ""
      case Some(first) =>
        log.entries.map(toString(first.timestamp.getOrElse(Timestamp.defaultInstance))).mkString("\n")
    }
  }

  private def formattedTimeDiff(a: Timestamp, b: Timestamp): String = {
    assert(a.nanos < 1000000000)
    assert(b.nanos < 1000000000)

    val timeDiff = {
      val seconds = a.seconds - b.seconds
      val nanos = a.nanos - b.nanos
      if (nanos < 0) {
        Timestamp(seconds - 1, nanos + (1 second).toNanos.toInt)
      } else {
        Timestamp(seconds, nanos)
      }
    }

    f"${timeDiff.seconds}%d.${(timeDiff.nanos nanos).toMicros}%06d"
  }

  def toString(startTime: Timestamp)(entry: JniLogEntry): String = {
    val time = formattedTimeDiff(entry.timestamp.getOrElse(Timestamp.defaultInstance), startTime)
    s"[$time] ${entry.name}(${entry.arguments.map(toString).mkString(", ")}) = " +
      s"${toString(entry.result.getOrElse(Value.defaultInstance))} [${(entry.elapsedNanos nanos).toMicros} µs" + {
        entry.instanceNumber match {
          case 0              => ""
          case instanceNumber => s", #$instanceNumber"
        }
      } + "]"
  }

  def toString(value: Value): String = {
    value.v match {
      case V.VBytes(bytes) =>
        if (value.truncated == 0) {
          s"byte[${bytes.size}]"
        } else {
          s"byte[${value.truncated}]"
        }
      case V.VSint64(sint64)          => sint64.toString
      case V.VString(string)          => string
      case V.VObject(Struct(members)) => s"{${members.map(toString).mkString("; ")}}"
      case V.Empty                    => "void"
    }
  }

  def toString(member: (String, Value)): String = {
    s"${member._1}=${toString(member._2)}"
  }

}
