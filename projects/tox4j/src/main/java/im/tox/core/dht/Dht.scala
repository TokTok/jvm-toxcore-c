package im.tox.core.dht

import com.github.nscala_time.time.Imports._
import com.typesafe.scalalogging.Logger
import im.tox.core.crypto._
import org.slf4j.LoggerFactory

/**
 * Toxcore stores the 32 nodes closest to its DHT public key and 8 nodes closest
 * to each of the public keys in its DHT friends list (or list of DHT public
 * keys that it actively tries to find and connect to) and pings them every 60
 * seconds to see if they are alive. Nodes can be in more than one list for
 * example if the DHT public key of the peer is very close to the DHT public key
 * of a friend being searched. It also sends get node requests to a random node
 * (random makes it unpredictable, predictability or knowing which node a node
 * will ping next could make some attacks that disrupt the network more easy as
 * it adds a possible attack vector) in each of these lists of nodes every 20
 * seconds, with the search public key being its public key for the closest node
 * and the public key being searched for being the ones in the DHT friends list.
 * Nodes are removed after 122 seconds of no response. Nodes are only added to
 * the lists after a valid ping response of send node packet is received from
 * them.
 */
final case class Dht private (
    keyPair: KeyPair,
    nodeSets: Map[PublicKey, NodeSet]
) {

  def size: Int = nodeSets.values.map(_.size).sum

  def canAddFriend(publicKey: PublicKey): Boolean = {
    !nodeSets.contains(publicKey)
  }

  def addFriend(publicKey: PublicKey): Dht = {
    assert(canAddFriend(publicKey), s"Public key $publicKey is already a friend")
    copy(
      nodeSets = nodeSets + (publicKey -> NodeSet(Dht.MaxFriendNodes, publicKey))
    )
  }

  def tryAddFriend(publicKey: PublicKey): Dht = {
    if (canAddFriend(publicKey)) {
      addFriend(publicKey)
    } else {
      this
    }
  }

  def canAddNode(nodeInfo: NodeInfo): Boolean = {
    nodeInfo.publicKey != keyPair.publicKey &&
      nodeSets.exists(_._2.canAdd(nodeInfo))
  }

  def addNode(nodeInfo: NodeInfo): Dht = {
    assert(
      canAddNode(nodeInfo),
      s"Node with key ${nodeInfo.publicKey} can not be added to any node list of node ${keyPair.publicKey}"
    )
    copy(
      nodeSets = nodeSets.mapValues(_.add(nodeInfo))
    )
  }

  def tryAddNode(nodeInfo: NodeInfo): Dht = {
    if (canAddNode(nodeInfo)) {
      addNode(nodeInfo)
    } else {
      this
    }
  }

  def getNode(nodeId: PublicKey): Option[NodeInfo] = {
    for {
      nodeSet <- nodeSets.values.find(_.contains(nodeId))
      nodeInfo <- nodeSet.get(nodeId)
    } yield {
      nodeInfo
    }
  }

  def getNearNodes(count: Int, publicKey: PublicKey): Set[NodeInfo] = {
    // A NodeSet keeps a list of nodes closest to its base public key, so we
    // simply try to add every node we know, and at the end we'll have the
    // closest nodes in that NodeSet.
    nodeSets.values.foldLeft(NodeSet(count, publicKey)) {
      case (nearNodes, nodeSet) =>
        nearNodes ++ nodeSet
    }.toSet
  }

  def removeStale(timeHorizon: DateTime): Dht = {
    copy(
      nodeSets = nodeSets.mapValues(_.removeStale(timeHorizon))
    )
  }

}

/**
 * The timeouts and number of nodes in lists for toxcore where picked by feeling
 * alone and are probably not the best values. This also applies to the behavior
 * which is simple and should be improved in order to make the network resist
 * better to sybil attacks.
 */
object Dht {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  /**
   * If the 32 nodes number where increased, it would increase the amount of
   * packets needed to check if each of them are still alive which would increase
   * the bandwidth usage but reliability would go up. If the number of nodes were
   * decreased, reliability would go down along with bandwidth usage. The reason
   * for this relationship between reliability and number of nodes is that if we
   * assume that not every node has its UDP ports open or is behind a cone NAT
   * it means that each of these nodes must be able to store a certain number
   * of nodes behind restrictive NATs in order for others to be able to find
   * those nodes behind restrictive NATs. For example if 7/8 nodes were behind
   * restrictive NATs, using 8 nodes would not be enough because the chances of
   * some of these nodes being impossible to find in the network would be too
   * high.
   */
  val MaxClosestNodes = 32

  /**
   * If the ping timeouts and delays between pings were higher it would decrease
   * the bandwidth usage but increase the amount of disconnected nodes that
   * are still being stored in the lists. Decreasing these delays would do the
   * opposite.
   */
  val PingTimeout = 122.seconds
  val PingInterval = 20.seconds

  /**
   * If the 8 nodes closest to each public key were increased to 16 it would
   * increase the bandwidth usage, might increase hole punching efficiency on
   * symmetric NATs (more ports to guess from, see Hole punching) and might
   * increase the reliability. Lowering this number would have the opposite
   * effect.
   */
  val MaxFriendNodes = 8

  /**
   * Every peer in the Tox DHT has an address which is a public key called the
   * temporary DHT public key. This address is temporary and is wiped every time
   * the tox instance is closed/restarted.
   */
  def apply(
    keyPair: KeyPair = CryptoCore.keyPair(),
    maxClosestNodes: Int = MaxClosestNodes,
    maxFriendNodes: Int = MaxFriendNodes
  ): Dht = {
    Dht(
      keyPair,
      Map(keyPair.publicKey -> NodeSet(maxClosestNodes, keyPair.publicKey))
    )
  }

}
