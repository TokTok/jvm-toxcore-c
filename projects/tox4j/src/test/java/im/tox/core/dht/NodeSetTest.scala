package im.tox.core.dht

import com.github.nscala_time.time.Imports._
import im.tox.core.crypto.PublicKey
import im.tox.core.crypto.PublicKeyTest._
import im.tox.core.dht.NodeInfoTest._
import im.tox.core.dht.NodeSetTest._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.FunSuite
import org.scalatest.prop.PropertyChecks
import scodec.bits.BitVector

import scala.util.Random

object NodeSetTest {

  private def addNodes(
    nodeSet: NodeSet,
    nodeInfos: TraversableOnce[NodeInfo]
  ): NodeSet = {
    nodeInfos.foldLeft(nodeSet) { (nodeSet, nodeInfo) => nodeSet.add(nodeInfo) }
  }

  implicit val arbNodeSet: Arbitrary[NodeSet] =
    Arbitrary(
      Gen.zip(Gen.choose(0, 100), arbitrary[PublicKey], arbitrary[Set[NodeInfo]]).map {
        case (capacity, publicKey, nodeInfos) =>
          addNodes(NodeSet(capacity, publicKey), nodeInfos)
      }
    )

}

final class NodeSetTest extends FunSuite with PropertyChecks {

  val genEmptyNodeSet = Gen.zip(Gen.choose(1, 100), arbitrary[PublicKey]).map {
    case (capacity, publicKey) =>
      NodeSet(capacity, publicKey)
  }

  def genFullNodeSet(freeCapacity: Int = 0): Gen[NodeSet] = {
    Gen.resultOf[(Set[NodeInfo], PublicKey), NodeSet] {
      case (nodeInfos, publicKey) =>
        addNodes(NodeSet(nodeInfos.size + freeCapacity, publicKey), nodeInfos)
    }
  }

  test("node sets behave like sets") {
    forAll(genEmptyNodeSet, arbitrary[NodeInfo]) { (emptyNodeSet, nodeInfo) =>
      assert(
        emptyNodeSet
          .add(nodeInfo)
          .size == 1
      )
    }
  }

  test("an empty non-zero sized node set always accepts a node") {
    forAll(genEmptyNodeSet, arbitrary[NodeInfo]) { (emptyNodeSet, nodeInfo) =>
      assert(emptyNodeSet.canAdd(nodeInfo))
    }
  }

  test("nodes already in the node set are always accepted") {
    forAll { (nodeSet: NodeSet, index: Int) =>
      whenever(nodeSet.nonEmpty) {
        val nodeInfo = nodeSet.get(index).get
        assert(nodeSet.canAdd(nodeInfo))
      }
    }
  }

  test("adding a node after removing it is allowed") {
    forAll { (nodeSet: NodeSet, index: Int) =>
      whenever(nodeSet.nonEmpty) {
        val nodeInfo = nodeSet.get(index).get
        assert(
          nodeSet
            .remove(nodeInfo)
            .canAdd(nodeInfo)
        )
      }
    }
  }

  test("removing a node twice has no effect") {
    forAll { (nodeSet: NodeSet, index: Int) =>
      whenever(nodeSet.nonEmpty) {
        val nodeInfo = nodeSet.get(index).get
        assert(
          {
            nodeSet
              .remove(nodeInfo)
              .remove(nodeInfo)
          } == {
            nodeSet
              .remove(nodeInfo)
          }
        )
      }
    }
  }

  test("removing a contained node affects set equality") {
    forAll { (nodeSet: NodeSet, index: Int) =>
      whenever(nodeSet.nonEmpty) {
        val nodeInfo = nodeSet.get(index).get
        assert(
          {
            nodeSet.remove(nodeInfo)
          } != {
            nodeSet
          }
        )
      }
    }
  }

  test("removing a node makes it no longer be contained") {
    forAll { (nodeSet: NodeSet, index: Int) =>
      whenever(nodeSet.nonEmpty) {
        val nodeInfo = nodeSet.get(index).get
        assert(
          nodeSet.remove(nodeInfo)
            !=
            nodeSet
        )
        assert(nodeSet.contains(nodeInfo))
        assert(!nodeSet.remove(nodeInfo).contains(nodeInfo))
      }
    }
  }

  test("attempting to get any node in a non-empty node set succeeds") {
    forAll { (nodeSet: NodeSet, index: Int) =>
      whenever(nodeSet.nonEmpty) {
        assert(nodeSet.get(index).nonEmpty)
      }
    }
  }

  test("getting a node from an empty set fails") {
    forAll(genEmptyNodeSet, arbitrary[Int]) { (nodeSet, index) =>
      assert(nodeSet.get(index).isEmpty)
    }
  }

  test("node set size limits are respected") {
    forAll(genEmptyNodeSet, arbitrary[Set[NodeInfo]]) { (emptyNodeSet, nodeInfos) =>
      val nodeSet = addNodes(emptyNodeSet, nodeInfos)

      if (nodeSet.capacity >= nodeInfos.size) {
        assert(nodeSet.size == nodeInfos.size)
      } else {
        assert(nodeSet.size == nodeSet.capacity)
      }
    }
  }

  test("insertion order does not affect set equality") {
    forAll(arbitrary[PublicKey], arbitrary[Set[NodeInfo]]) { (publicKey, nodeInfos) =>
      val nodeSet = NodeSet(nodeInfos.size, publicKey)

      if (nodeSet.capacity != 0) {
        assert(addNodes(nodeSet, nodeInfos) != nodeSet)
      }
      assert(
        {
          addNodes(nodeSet, nodeInfos)
        } == {
          addNodes(nodeSet, Random.shuffle(nodeInfos.toSeq))
        }
      )
    }
  }

  /**
   * Cheat the type system by deserialising a public key from a byte array.
   * Creates an all-zero key except for the first byte.
   */
  def makePublicKey(firstByte: Byte): PublicKey = {
    val bytes = firstByte +: (0 until PublicKey.Size - 1).map(_ => 0.toByte)
    PublicKey.fromBits(BitVector(bytes)).getOrElse(fail("Public key creation failed"))
  }

  def genCustomNodeInfo(firstByte: Byte): Gen[NodeInfo] = {
    arbitrary[NodeInfo].map { (nodeInfo) =>
      nodeInfo.copy(publicKey = makePublicKey(firstByte))
    }
  }

  test("adding a node that's further away to a full set will do nothing") {
    forAll(genCustomNodeInfo(0x01), genCustomNodeInfo(0x02)) { (nearNode, farNode) =>
      val ownKey = makePublicKey(0x00)

      val nodeSet = NodeSet(1, ownKey).add(nearNode)
      assert(!nodeSet.canAdd(farNode))
      assert(nodeSet.add(farNode) == nodeSet)
    }
  }

  test("adding a node that's nearer to a full set will replace an existing one") {
    forAll(genCustomNodeInfo(0x01), genCustomNodeInfo(0x02)) { (nearNode, farNode) =>
      val ownKey = makePublicKey(0x00)

      val nodeSet = NodeSet(1, ownKey).add(farNode)
      assert(nodeSet.canAdd(nearNode))
      assert(nodeSet.add(nearNode) != nodeSet)
    }
  }

}
