package im.tox.client

import java.net.InetAddress

import im.tox.core.network.{NetworkCoreTest, Port}
import im.tox.tox4j.core.ToxCoreConstants
import im.tox.tox4j.core.data.{ToxPublicKey, ToxSecretKey}
import im.tox.tox4j.testing.GetDisjunction._
import scopt.Read

case object TestClientOptions {

  val DefaultBootstrapNode = NetworkCoreTest.nodes.headOption.get

  private final case class Config(
    count: Int = 1,
    load: List[ToxSecretKey] = Nil,
    address: Option[InetAddress] = None,
    port: Port = Port.fromInt(ToxCoreConstants.DefaultStartPort).get,
    key: Option[ToxPublicKey] = None
  )

  implicit val inetAddressRead: Read[Option[InetAddress]] = Read.stringRead.map(InetAddress.getByName).map(Option.apply)
  implicit val portRead: Read[Port] = Read.intRead.map(Port.fromInt(_).get)
  implicit val toxPublicKeyRead: Read[Option[ToxPublicKey]] = Read.stringRead.map(ToxPublicKey.fromHexString(_).toOption)
  implicit val toxSecretKeyRead: Read[ToxSecretKey] = Read.stringRead.map(ToxSecretKey.fromHexString(_).get)

  private val optionParser = new scopt.OptionParser[Config](toString) {
    head(
      "Run a number of Tox instances.\n\n",
      "- All instances run with the same arguments.\n",
      "- An optional list of secret keys can be used to initialise the instances.\n",
      "- If the number of instances is greater than the list of keys,\n",
      "  the remaining instances will be initialised with random keys.\n"
    )

    help("help") text "Prints this usage text"

    arg[ToxSecretKey]("<key>...") unbounded () optional () action { (x, c) =>
      c.copy(load = c.load :+ x)
    } text "Optional secret keys for the instances"

    opt[Int]('c', "count") action { (x, c) =>
      c.copy(count = x)
    } text "Number of test clients to spawn"

    opt[Unit]('b', "bootstrap") action { (x, c) =>
      c.copy(
        address = Some(DefaultBootstrapNode._1.getAddress),
        port = Port.fromInt(DefaultBootstrapNode._1.getPort).get,
        key = Some(ToxPublicKey.fromHexString(DefaultBootstrapNode._2.readable).get)
      )
    } text s"Bootstrap to the default bootstrap node $DefaultBootstrapNode"

    opt[Option[InetAddress]]('a', "address") action { (x, c) =>
      c.copy(address = x)
    } text "Address of the bootstrap node"

    opt[Port]('p', "port") action { (x, c) =>
      c.copy(port = x)
    } text "Port of the bootstrap node"

    opt[Option[ToxPublicKey]]('k', "key") action { (x, c) =>
      c.copy(key = x)
    } text "DHT public key of the bootstrap node"

    checkConfig { c =>
      if (c.address.isDefined != c.key.isDefined) {
        failure("If one of address and key is specified, the other must be specified as well")
      } else if (c.count <= 0) {
        failure("The number of toxes must be greater than 0")
      } else {
        success
      }
    }
  }

  def apply(args: Seq[String])(f: Config => Unit): Unit = {
    optionParser.parse(args, Config()).foreach(f)
  }

}
