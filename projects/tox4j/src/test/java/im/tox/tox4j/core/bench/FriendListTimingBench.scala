package im.tox.tox4j.core.bench

import im.tox.tox4j.bench.PerformanceReportBase._
import im.tox.tox4j.bench.{Confidence, TimingReport}
import im.tox.tox4j.core.{ToxCore, ToxFriendRequestMessage}
import org.scalameter.Gen
import org.scalameter.picklers.Implicits._

final class FriendListTimingBench extends TimingReport {

  /**
   * Deletes all but 1 friends.
   */
  private def clearFriendList(pair: (_, ToxCore[Unit])): Unit = {
    val tox = pair._2
    tox.getFriendList.tail foreach tox.deleteFriend
  }

  /**
   * Fills the friend list back up with the expected number of friends.
   */
  private def refillFriendList(pair: (Seq[Int], ToxCore[Unit])): Unit = {
    val (friendList, tox) = pair
    val missing = friendList.length - tox.getFriendList.length
    assert(missing >= 0)
    friendKeys(missing) foreach tox.addFriendNorequest
  }

  timing of classOf[ToxCore[Unit]] in {

    measure method "addFriend" in {
      using(friends(100) map friendAddresses, toxInstance.cached) tearDown clearFriendList in {
        case (friendList, tox) =>
          friendList foreach (tox.addFriend(_, ToxFriendRequestMessage.unsafeFromByteArray(Array.ofDim(1))))
      }
    }

    measure method "addFriendNoRequest" in {
      using(friends(100) map friendKeys, toxInstance.cached) tearDown clearFriendList in {
        case (friendList, tox) =>
          friendList foreach tox.addFriendNorequest
      }
    }

    performance of "deleting all friends" in {
      using(friends1k.map(makeToxWithFriends).map(toxAndFriendNumbers())) tearDown refillFriendList config (Confidence.high: _*) in {
        case (friendList, tox) =>
          assert(friendList.length >= 100)
          assert(friendList.length <= 1000)
          assert(friendList.length % 100 == 0)
          friendList foreach tox.deleteFriend
      }
    }

    measure method "getFriendList" in {
      using(toxWithFriends1k, Gen.single("iterations")(100)) in {
        case (tox, sz) =>
          (0 until sz) foreach (_ => tox.getFriendList)
      }
    }

    measure method "friendExists" in {
      using(toxWithFriends1k map toxAndFriendNumbers(100), Gen.single("iterations")(100000)) in {
        case ((friendNumbers, tox), iterations) =>
          (0 until iterations / friendNumbers.length) foreach { _ =>
            friendNumbers foreach { i =>
              tox.friendExists(i)
            }
          }
      }
    }

    measure method "getFriendPublicKey" in {
      using(toxWithFriends1k map toxAndFriendNumbers(100), Gen.single("iterations")(100000)) in {
        case ((friendNumbers, tox), iterations) =>
          // Divide iterations by the number of friends we look up, so we do $iterations calls to getFriendPublicKey.
          (0 until iterations / friendNumbers.length) foreach { _ =>
            // Look up a random 100 friends. The toxWithFriends1k generator produces at least 100 friends.
            friendNumbers foreach { i =>
              tox.getFriendPublicKey(i)
            }
          }
      }
    }

    measure method "getFriendByPublicKey" in {
      using(toxWithFriends1k map toxAndFriendKeys(limit = 100), Gen.single("iterations")(100)) in {
        case ((friendList, tox), iterations) =>
          (0 until iterations) foreach { _ =>
            friendList foreach (key => tox.friendByPublicKey(key))
          }
      }
    }

  }

}
