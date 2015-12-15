package im.tox.client

import java.net.URL

/**
 * Contains some information about the host the client is running on.
 */
object HostInfo {

  val ipv4 = new Curl(new URL("http://64.182.208.184/"))
  val ipv6 = new Curl(new URL("http://[2604:7780:200:300::60]/"))

}
