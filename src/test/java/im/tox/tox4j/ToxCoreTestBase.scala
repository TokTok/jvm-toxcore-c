
package im.tox.tox4j

import java.io.IOException
import java.net.{ InetAddress, Socket }
import java.util.Random

import org.jetbrains.annotations.NotNull
import org.scalatest.Assertions

object ToxCoreTestBase extends Assertions {

  private[tox4j] val nodeCandidates = Seq(
    new DhtNode("biribiri.org", null, 33445, "F404ABAA1C99A9D37D61AB54898F56793E1DEF8BD46B1038B9D822E8460FAB67"),
    new DhtNode("192.254.75.102", "2607:5600:284::2", 33445, "951C88B7E75C867418ACDB5D273821372BB5BD652740BCDF623A4FA293E75D2F"),
    new DhtNode("144.76.60.215", "2a01:4f8:191:64d6::1", 33445, "04119E835DF3E78BACF0F84235B300546AF8B936F035185E2A8E9E0A67C8924F"),
    new DhtNode("23.226.230.47", "2604:180:1::3ded:b280", 33445, "A09162D68618E742FFBCA1C2C70385E6679604B2D80EA6E84AD0996A1AC8A074"),
    new DhtNode("178.62.125.224", "2a03:b0c0:1:d0::178:6001", 33445, "10B20C49ACBD968D7C80F2E8438F92EA51F189F4E70CFBBB2C2C8C799E97F03E"),
    new DhtNode("178.21.112.187", "2a02:2308::216:3eff:fe82:eaef", 33445, "4B2C19E924972CB9B57732FB172F8A8604DE13EEDA2A6234E348983344B23057"),
    new DhtNode("195.154.119.113", "2001:bc8:3698:101::1", 33445, "E398A69646B8CEACA9F0B84F553726C1C49270558C57DF5F3C368F05A7D71354"),
    new DhtNode("104.219.184.206", null, 443, "8CD087E31C67568103E8C2A28653337E90E6B8EDA0D765D57C6B5172B4F1F04C"),
    new DhtNode("76.191.23.96", null, 33445, "93574A3FAB7D612FEA29FD8D67D3DD10DFD07A075A5D62E8AF3DD9F5D0932E11"),
    new DhtNode("178.62.250.138", "2a03:b0c0:2:d0::16:1", 33445, "788236D34978D1D5BD822F0A5BEBD2C53C64CC31CD3149350EE27D4D9A2F9B6B"),
    new DhtNode("78.225.128.39", null, 33445, "7A2306BFBA665E5480AE59B31E116BE9C04DCEFE04D9FE25082316FA34B4DA0C"),
    new DhtNode("130.133.110.14", "2001:6f8:1c3c:babe::14:1", 33445, "461FA3776EF0FA655F1A05477DF1B3B614F7D6B124F7DB1DD4FE3C08B03B640F"),
    new DhtNode("104.167.101.29", null, 33445, "5918AC3C06955962A75AD7DF4F80A5D7C34F7DB9E1498D2E0495DE35B3FE8A57"),
    new DhtNode("195.154.109.148", null, 33445, "391C96CB67AE893D4782B8E4495EB9D89CF1031F48460C06075AA8CE76D50A21 "),
    new DhtNode("192.3.173.88", null, 33445, "3E1FFDEB667BFF549F619EC6737834762124F50A89C8D0DBF1DDF64A2DD6CD1B"),
    new DhtNode("205.185.116.116", null, 33445, "A179B09749AC826FF01F37A9613F6B57118AE014D4196A0E1105A98F93A54702"),
    new DhtNode("198.98.51.198", "2605:6400:1:fed5:22:45af:ec10:f329", 33445, "1D5A5F2F5D6233058BF0259B09622FB40B482E4FA0931EB8FD3AB8E7BF7DAF6F"),
    new DhtNode("80.232.246.79", null, 33445, "0B8DCEAA7BDDC44BB11173F987CAE3566A2D7057D8DD3CC642BD472B9391002A")
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
