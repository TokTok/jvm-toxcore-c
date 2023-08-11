package im.tox.tox4j

import java.io.IOException
import java.net.{ InetAddress, Socket }
import java.util.Random

import org.jetbrains.annotations.NotNull
import org.scalatest.Assertions

object ToxCoreTestBase extends Assertions {

  private[tox4j] val nodeCandidates = Seq(
    new DhtNode("tox.kurnevsky.net", "tox.kurnevsky.net", 33445, "82EF82BA33445A1F91A7DB27189ECFC0C013E06E3DA71F588ED692BED625EC23"),
    new DhtNode("tox.initramfs.io", "tox.initramfs.io", 33445, "3F0A45A268367C1BEA652F258C85F4A66DA76BCAA667A49E770BCC4917AB6A25"),
    new DhtNode("tox.verdict.gg", null, 33445, "1C5293AEF2114717547B39DA8EA6F1E331E5E358B35F9B6B5F19317911C5F976")
  )

  @NotNull def randomBytes(length: Int): Array[Byte] = {
    val array = new Array[Byte](length)
    new Random().nextBytes(array)
    array
  }

  @NotNull
  def readablePublicKey(@NotNull id: Array[Byte]): String = {
    val str = new StringBuilder
    id foreach { c => str.append(f"$c%02X") }
    str.toString()
  }

  @NotNull
  def parsePublicKey(@NotNull id: String): Array[Byte] = {
    val publicKey = new Array[Byte](id.length / 2)
    publicKey.indices foreach { i =>
      publicKey(i) =
        ((fromHexDigit(id.charAt(i * 2)) << 4) +
          fromHexDigit(id.charAt(i * 2 + 1))).toByte
    }
    publicKey
  }

  private def fromHexDigit(c: Char): Byte = {
    val digit =
      if (false) { 0 }
      else if ('0' to '9' contains c) { c - '0' }
      else if ('A' to 'F' contains c) { c - 'A' + 10 }
      else if ('a' to 'f' contains c) { c - 'a' + 10 }
      else { throw new IllegalArgumentException(s"Non-hex digit character: $c") }
    digit.toByte
  }

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  private def hasConnection(ip: String, port: Int): Option[String] = {
    var socket: Socket = null
    try {
      socket = new Socket(InetAddress.getByName(ip), port)
      if (socket.getInputStream == null) {
        Some("Socket input stream is null")
      } else {
        None
      }
    } catch {
      case e: IOException =>
        Some(s"A network connection can't be established to $ip:$port: ${e.getMessage}")
    } finally {
      if (socket != null) {
        socket.close()
      }
    }
  }

  def checkIPv4: Option[String] = {
    hasConnection("8.8.8.8", 53)
  }

  def checkIPv6: Option[String] = {
    hasConnection("2001:4860:4860::8888", 53)
  }

  protected[tox4j] def assumeIPv4(): Unit = {
    assume(checkIPv4.isEmpty)
  }

  protected[tox4j] def assumeIPv6(): Unit = {
    assume(checkIPv6.isEmpty)
  }

}
