package im.tox.core.network

import org.scalatest.FunSuite

import scala.util.Try

final class NetworkCoreTest extends FunSuite {

  test("bootstrapping and communicating with the DHT") {
    System.out.println(Try(NetworkCore.start()))
  }

}
