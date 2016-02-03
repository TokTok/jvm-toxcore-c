package im.tox.core.dht

import im.tox.core.crypto.PublicKey
import im.tox.core.dht.distance.XorDistance

import scala.annotation.tailrec
import scala.collection.immutable.SortedMap

/**
 * Ordered set of [[NodeInfo]] objects. Always keeps the list of nodes closest
 * to a given [[PublicKey]] in terms of [[XorDistance]].
 *
 * The set size is kept below or equal to its capacity.
 *
 * @param capacity Maximum number of nodes in this set.
 * @param nodes The underlying [[NodeInfo]] set with associated last-active times.
 * @param ord The ordering definition made from the public key using the distance metric.
 */
final case class NodeSet private (
    capacity: Int,
    private val nodes: SortedMap[PublicKey, NodeInfo]
)(implicit val ord: Ordering[PublicKey]) {

  require(capacity >= 0)
  require(capacity >= nodes.size)
  assert(nodes eq NodeSet.truncate(nodes, capacity)) // This follows from the requirement above.

  /**
   * A node will be accepted by a node set if it is already in the set
   * (= update) or if the node is smaller (closer to the base key) than the
   * largest node in the set (= add).
   */
  def canAdd(nodeInfo: NodeInfo): Boolean = {
    add(nodeInfo).nodes.contains(nodeInfo.publicKey)
  }

  def add(nodeInfo: NodeInfo): NodeSet = {
    copy(nodes = NodeSet.truncate(nodes.updated(nodeInfo.publicKey, nodeInfo), capacity))
  }

  def addAll(nodeSet: NodeSet): NodeSet = {
    copy(nodes = NodeSet.truncate(nodes ++ nodeSet.nodes, capacity))
  }

  def remove(nodeInfo: NodeInfo): NodeSet = {
    copy(nodes = nodes - nodeInfo.publicKey)
  }

  def contains(nodeId: PublicKey): Boolean = {
    nodes.contains(nodeId)
  }

  /**
   * Circular get: any index is good, as long as there is at least 1 node in
   * the set.
   */
  def get(index: Int): Option[NodeInfo] = {
    if (isEmpty) {
      None
    } else {
      val iterator = nodes.valuesIterator.drop(Math.abs(index % size))
      assert(iterator.hasNext)
      Some(iterator.next)
    }
  }

  def get(nodeId: PublicKey): Option[NodeInfo] = {
    nodes.get(nodeId)
  }

  def values: Iterable[NodeInfo] = nodes.values
  def size: Int = nodes.size
  def nonEmpty: Boolean = nodes.nonEmpty
  def isEmpty: Boolean = nodes.isEmpty

}

object NodeSet {

  def apply(capacity: Int, publicKey: PublicKey): NodeSet = {
    val ord = NodeInfo.distanceOrdering(publicKey)
    NodeSet(capacity, SortedMap.empty(ord))(ord)
  }

  /**
   * Remove the largest elements from the map until the size is less than or
   * equal to capacity.
   *
   * @param capacity Maximum size of the map.
   */
  @tailrec
  private def truncate[A, B](nodes: SortedMap[A, B], capacity: Int)(implicit ord: Ordering[A]): SortedMap[A, B] = {
    require(capacity >= 0)

    if (nodes.size <= capacity) {
      nodes
    } else {
      truncate(nodes - nodes.keys.max, capacity)
    }
  }

}
