package im.tox.core.dht

import com.typesafe.scalalogging.Logger
import im.tox.core.crypto._
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import scala.language.postfixOps

/**
 * Nodes can be in more than one list for example if the DHT public key of the
 * peer is very close to the DHT public key of a friend being searched.
 */
final case class Dht private (
    options: Dht.Options,
    keyPair: KeyPair,
    searchLists: Map[PublicKey, NodeSet]
) {

  /**
   * Class invariant: Nodes that appear in multiple search lists are physically equal.
   */
  assert {
    searchLists.values.foldLeft((true, Map.empty[PublicKey, NodeInfo])) { (validation, nodeSet) =>
      nodeSet.values.foldLeft(validation) {
        case ((valid, nodes), nodeInfo) =>
          nodes.get(nodeInfo.publicKey) match {
            case None =>
              (valid, nodes.updated(nodeInfo.publicKey, nodeInfo))
            case Some(existing) =>
              (valid && (existing eq nodeInfo), nodes)
          }
      }
    }._1
  }

  /**
   * Class invariant: The [[Dht]] always contains a [[NodeSet]] searching for our
   * own [[PublicKey]].
   */
  assert(searchLists.contains(keyPair.publicKey))

  /**
   * Class invariant: All search lists that are not searching for our own
   * [[PublicKey]] have capacity [[options.maxFriendNodes]]. The one searching for
   * our own key has capacity [[options.maxClosestNodes]].
   */
  assert {
    searchLists.forall {
      case (key, nodeSet) =>
        if (key == keyPair.publicKey) {
          nodeSet.capacity == options.maxClosestNodes
        } else {
          nodeSet.capacity == options.maxFriendNodes
        }
    }
  }

  /**
   * The total number of known nodes in all the node search lists.
   */
  def size: Int = {
    searchLists.values.map(_.size).sum
  }

  /**
   * Instruct the DHT to start searching for a node with a given [[PublicKey]].
   *
   * If there is already a search list for the given key, no change occurs.
   */
  def addSearchKey(publicKey: PublicKey): Dht = {
    if (searchLists.contains(publicKey)) {
      this
    } else {
      copy(
        searchLists = searchLists.updated(publicKey, NodeSet(options.maxFriendNodes, publicKey))
      )
    }
  }

  /**
   * Stop searching for the node with the given [[PublicKey]] and remove the associated
   * search list. Note that since [[NodeInfo]]s may appear on multiple lists, this does
   * not necessarily mean that we completely forgot about the node with that [[PublicKey]].
   * It may still be on another list, if it happened to be close to that list's search key.
   */
  def removeSearchKey(publicKey: PublicKey): Dht = {
    copy(searchLists = searchLists - publicKey)
  }

  /**
   * Determine whether any of the node lists will accept the node.
   *
   * This will return true as soon as a node list is found that would accept the node,
   * so it is faster than adding it and checking for modification.
   */
  def canAddNode(nodeInfo: NodeInfo): Boolean = {
    searchLists.exists(_._2.canAdd(nodeInfo))
  }

  /**
   * Add the given node to all node lists that accept it. If no node list
   * accepts the node, no modification occurs.
   */
  def addNode(nodeInfo: NodeInfo): Dht = {
    copy(searchLists = searchLists.mapValues(_.add(nodeInfo)))
  }

  /**
   * Remove a node from all node lists that contain it. If no node list
   * contains the node, no modification occurs.
   */
  def removeNode(nodeInfo: NodeInfo): Dht = {
    copy(searchLists = searchLists.mapValues(_.remove(nodeInfo)))
  }

  /**
   * Find a [[NodeInfo]] by [[PublicKey]].
   *
   * The first match is returned, as the class invariant guarantees that all
   * other matches are exactly equal to the first.
   */
  def getNode(nodeId: PublicKey): Option[NodeInfo] = {
    for {
      nodeSet <- searchLists.values.find(_.contains(nodeId))
      nodeInfo <- nodeSet.get(nodeId)
    } yield {
      nodeInfo
    }
  }

  /**
   * Get the set of nodes out of all the nodes this [[Dht]] knows that are
   * closest to the given [[PublicKey]]. The count parameter limits the number
   * of nodes returned.
   */
  def getNearNodes(count: Int, publicKey: PublicKey): Iterable[NodeInfo] = {
    // A NodeSet keeps a list of nodes closest to its base public key, so we
    // simply try to add every node we know, and at the end we'll have the
    // closest nodes in that NodeSet.
    searchLists.values.foldLeft(NodeSet(count, publicKey)) {
      case (nearNodes, nodeSet) =>
        nearNodes.addAll(nodeSet)
    }.values
  }

}

/**
 * The timeouts and number of nodes in lists for toxcore where picked by feeling
 * alone and are probably not the best values. This also applies to the behavior
 * which is simple and should be improved in order to make the network resist
 * better to sybil attacks.
 */
case object Dht {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  /**
   * @param maxClosestNodes
   * Toxcore stores the 32 nodes closest to its DHT public key and 8 nodes closest
   * to each of the public keys in its DHT friends list (or list of DHT public
   * keys that it actively tries to find and connect to).
   *
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
   *
   * @param maxFriendNodes
   * If the 8 nodes closest to each public key were increased to 16 it would
   * increase the bandwidth usage, might increase hole punching efficiency on
   * symmetric NATs (more ports to guess from, see Hole punching) and might
   * increase the reliability. Lowering this number would have the opposite
   * effect.
   *
   * @param nodesRequestInterval
   * It also sends get node requests to a random node
   * (random makes it unpredictable, predictability or knowing which node a node
   * will ping next could make some attacks that disrupt the network more easy as
   * it adds a possible attack vector) in each of these lists of nodes every 20
   * seconds, with the search public key being its public key for the closest node
   * and the public key being searched for being the ones in the DHT friends list.
   *
   * @param pingInterval
   * The DHT pings them every 60 seconds to see if they are alive.
   *
   * @param pingTimeout
   * Nodes are removed after 122 seconds of no response.
   *
   * If the ping timeouts and delays between pings were higher it would decrease
   * the bandwidth usage but increase the amount of disconnected nodes that
   * are still being stored in the lists. Decreasing these delays would do the
   * opposite.
   */
  final case class Options(
    maxClosestNodes: Int = 32, // scalastyle:ignore magic.number
    maxFriendNodes: Int = 8, // scalastyle:ignore magic.number
    nodesRequestInterval: FiniteDuration = 20 seconds,
    pingInterval: FiniteDuration = 60 seconds,
    pingTimeout: FiniteDuration = 122 seconds
  )

  /**
   * Every peer in the Tox DHT has an address which is a public key called the
   * temporary DHT public key. This address is temporary and is wiped every time
   * the tox instance is closed/restarted.
   */
  def apply(
    options: Options = Options(),
    keyPair: KeyPair = CryptoCore.keyPair()
  ): Dht = {
    Dht(
      options,
      keyPair,
      Map(keyPair.publicKey -> NodeSet(options.maxClosestNodes, keyPair.publicKey))
    )
  }

}
