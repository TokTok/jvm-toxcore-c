package im.tox.core.dht

import com.github.nscala_time.time.Imports._
import im.tox.core.SortedLruSet
import im.tox.core.crypto.PublicKey

/**
 * Ordered set of [[NodeInfo]] objects. Always keeps the list of nodes closest
 * to a given [[PublicKey]] in terms of [[XorDistance]].
 *
 * The set size is kept below or equal to its capacity, but client code must
 * explicitly call [[removeStale]] to remove old elements.
 *
 * @param capacity Maximum number of nodes in this set.
 * @param nodes The underlying [[NodeInfo]] set with associated last-active times.
 * @param ord The ordering definition made from the public key using the distance metric.
 */
final case class NodeSet private (
    capacity: Int,
    private val nodes: SortedLruSet[NodeInfo]
)(implicit val ord: Ordering[NodeInfo]) {

  require(capacity >= 0)
  require(capacity >= nodes.size)

  /**
   * A node will be accepted by a node set if it is already in the set
   * (= update) or if the node is smaller (closer to the base key) than the
   * largest node in the set (= add).
   */
  def canAdd(nodeInfo: NodeInfo): Boolean = {
    add(nodeInfo).nodes.contains(nodeInfo)
  }

  def add(nodeInfo: NodeInfo, lastActive: DateTime = DateTime.now()): NodeSet = {
    copy(nodes = nodes + (nodeInfo, lastActive, capacity))
  }

  def ++(nodeSet: NodeSet): NodeSet = { // scalastyle:ignore method.name
    copy(nodes = nodes ++ (nodeSet.nodes, capacity))
  }

  def remove(nodeInfo: NodeInfo): NodeSet = {
    copy(nodes = nodes - nodeInfo)
  }

  def contains(nodeInfo: NodeInfo): Boolean = {
    nodes.contains(nodeInfo)
  }

  def contains(nodeId: PublicKey): Boolean = {
    nodes.exists(_.publicKey == nodeId)
  }

  def removeStale(timeHorizon: DateTime): NodeSet = {
    copy(nodes = nodes.removeStale(timeHorizon))
  }

  /**
   * Circular get: any index is good, as long as there is at least 1 node in
   * the set.
   */
  def get(index: Int): Option[NodeInfo] = {
    if (isEmpty) {
      None
    } else {
      val iterator = nodes.iterator.drop(Math.abs(index % size))
      assert(iterator.hasNext)
      Some(iterator.next._1)
    }
  }

  def get(nodeId: PublicKey): Option[NodeInfo] = {
    nodes.find(_.publicKey == nodeId)
  }

  def toSet: Set[NodeInfo] = nodes.keySet
  def size: Int = nodes.size
  def nonEmpty: Boolean = nodes.nonEmpty
  def isEmpty: Boolean = nodes.isEmpty

}

object NodeSet {

  def apply(capacity: Int, publicKey: PublicKey): NodeSet = {
    implicit val ord = NodeInfo.distanceOrdering(publicKey)
    NodeSet(capacity, SortedLruSet.empty(ord))
  }

}
