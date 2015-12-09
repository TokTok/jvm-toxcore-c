package im.tox.core.dht

import java.io.File
import java.net.InetSocketAddress
import java.nio.charset.Charset

import com.google.common.io.Files
import com.typesafe.scalalogging.Logger
import im.tox.core.crypto.KeyPairTest._
import im.tox.core.crypto._
import im.tox.core.dht.DhtTest._
import im.tox.core.dht.distance.XorDistance
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.FunSuite
import org.scalatest.prop.PropertyChecks
import org.slf4j.LoggerFactory

import scalax.collection.GraphEdge.DiEdge
import scalax.collection.immutable.Graph
import scalax.collection.io.dot._

object DhtTest {

  val AllowPartitions = true
  val MaxClosestNodes = 2
  val ExtraNodeCount = 4

  def genDht(maxClosestNodes: Int = Dht.Settings().maxClosestNodes): Gen[Dht] = {
    Arbitrary.arbitrary[KeyPair].map { keyPair =>
      val settings = Dht.Settings(
        maxClosestNodes = maxClosestNodes
      )
      Dht(settings, keyPair)
    }
  }

  implicit val arbDht: Arbitrary[Dht] = Arbitrary(genDht(MaxClosestNodes))

}

final class DhtTest extends FunSuite with PropertyChecks {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  private val localhost = new InetSocketAddress("127.0.0.1", 33445)

  test("get 4 closest nodes") {
    forAll(genDht()) { dht =>
    }
  }

  test("prevent partitioning") {
    def edgeTransformer(rootGraph: DotRootGraph): EdgeTransformer[PublicKey, DiEdge] = { edge =>
      edge.edge match {
        case DiEdge(from, to) =>
          Some((
            rootGraph,
            DotEdgeStmt(
              NodeId(from.toString),
              NodeId(to.toString),
              List(DotAttr(Id("label"), Id('"' + XorDistance(from.value, to.value).toHexString + '"')))
            )
          ))
      }
    }

    def graphToDot(graph: Graph[PublicKey, DiEdge]): Unit = {
      val root = DotRootGraph(directed = true, id = Some(Id("G")))
      val dot = graph.toDot(root, edgeTransformer(root))
      Files.write(dot, new File("graph.dot"), Charset.forName("UTF-8"))
      new ProcessBuilder("dot", "-Tpng", "graph.dot", "-o", "graph.png").inheritIO().start().waitFor()
    }

    forAll { (emptyDhts: Set[Dht]) =>
      val dhts =
        for {
          dht <- emptyDhts.toSeq
        } yield {
          emptyDhts
            .map(dht => NodeInfo(Protocol.Udp, localhost, dht.keyPair.publicKey))
            .foldLeft(dht) { (dht, node) =>
              if (dht.keyPair.publicKey == node.publicKey) {
                // The node may know itself, but that is not interesting for this test.
                dht
              } else {
                dht.addNode(node)
              }
            }
        }

      if (dhts.size >= 2) {
        val edges =
          for {
            dht <- dhts
            nodeSet <- dht.searchLists.values
            knownNode <- nodeSet.values
          } yield {
            // The DHT node may know itself, but we don't want to graph that here.
            assert(dht.keyPair.publicKey != knownNode.publicKey)
            DiEdge(dht.keyPair.publicKey, knownNode.publicKey)
          }

        val graph = Graph[PublicKey, DiEdge](edges: _*)

        val maxOutDegree = graph.nodes.map(_.outDegree).max
        assert(maxOutDegree <= DhtTest.MaxClosestNodes)

        // The DHT can sometimes get partitions when using small keys and small node lists and no friends.
        if (!DhtTest.AllowPartitions) {
          if (!graph.isConnected) {
            graphToDot(graph)
          }
          assert(graph.isConnected)
        }
      }
    }
  }

}
