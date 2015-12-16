package im.tox.client

import java.net.InetAddress

import im.tox.core.network.{NetworkCoreTest, Port}
import im.tox.tox4j.core.ToxCoreConstants
import im.tox.tox4j.core.data.{ToxPublicKey, ToxSecretKey}
import im.tox.tox4j.testing.GetDisjunction._
import scopt.Read

case object ToxClientOptions {

  val DefaultBootstrapNode = NetworkCoreTest.nodes.headOption.get

  final case class Config(
    count: Int = 0,
    load: List[ToxSecretKey] = Nil,
    nospam: Option[Int] = None,
    address: Option[InetAddress] = None,
    bootstrapPort: Port = Port.fromInt(ToxCoreConstants.DefaultStartPort).get,
    key: Option[ToxPublicKey] = None,
    httpPort: Port = Port.fromInt(8080).get
  )

  implicit val inetAddressRead: Read[InetAddress] = Read.stringRead.map(InetAddress.getByName)
  implicit val portRead: Read[Port] = Read.intRead.map(Port.fromInt(_).get)
  implicit val toxPublicKeyRead: Read[ToxPublicKey] = Read.stringRead.map(ToxPublicKey.fromHexString(_).get)
  implicit val toxSecretKeyRead: Read[ToxSecretKey] = Read.stringRead.map(ToxSecretKey.fromHexString(_).get)

  private def bootstrap(c: Config): Config = {
    c.copy(
      address = Some(DefaultBootstrapNode._1.getAddress),
      bootstrapPort = Port.fromInt(DefaultBootstrapNode._1.getPort).get,
      key = Some(ToxPublicKey.fromHexString(DefaultBootstrapNode._2.toHexString).get)
    )
  }

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

    opt[Int]('n', "nospam") action { (x, c) =>
      c.copy(nospam = Some(x))
    } text "Nospam for new tox instances"

    opt[Unit]('b', "bootstrap") action { (x, c) =>
      bootstrap(c)
    } text s"Bootstrap to the default bootstrap node $DefaultBootstrapNode"

    opt[InetAddress]('a', "bootstrap-address") action { (x, c) =>
      c.copy(address = Some(x))
    } text "Address of the bootstrap node"

    opt[Port]('p', "bootstrap-port") action { (x, c) =>
      c.copy(bootstrapPort = x)
    } text "Port of the bootstrap node"

    opt[ToxPublicKey]('k', "bootstrap-key") action { (x, c) =>
      c.copy(key = Some(x))
    } text "DHT public key of the bootstrap node"

    opt[Port]('P', "port") action { (x, c) =>
      c.copy(httpPort = x)
    } text s"Port to run HTTP web interface on (default: ${Config().httpPort})"
    checkConfig { c =>
      if (c.address.isDefined != c.key.isDefined) {
        failure("If one of address and key is specified, the other must be specified as well")
      } else if (c.count < 0) {
        failure("The number of toxes must be positive")
      } else {
        success
      }
    }
  }

  def apply(args: Seq[String])(main: Config => Unit): Unit = {
    optionParser.parse(args, Config()).foreach { c =>
      main(
        if (c.count == 0 && c.load.isEmpty) {
          bootstrap(c.copy(
            count = 1,
            load = List(ToxSecretKey.fromValue(Array.ofDim(ToxSecretKey.Size)).get)
          ))
        } else {
          c
        }
      )
    }
  }

}
