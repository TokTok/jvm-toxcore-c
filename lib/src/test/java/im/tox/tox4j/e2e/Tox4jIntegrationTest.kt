package im.tox.tox4j.e2e

import kotlin.test.Test

/** Connects once, then runs every subtest, like `rs-toxcore-c/toxcore/tests/integration_test.rs`. */
class Tox4jIntegrationTest {
    @Test
    fun integrationSuite() {
        Tox4jHarness().use { h ->
            h.addTox()
            h.addTox()
            h.connect(0, 1)
            h.waitForFriendConnected(0, 1)
            h.waitForFriendConnected(1, 0)

            SubtestMessage.run(h)
            SubtestCustomPacket.run(h)
            SubtestFile.run(h)
            SubtestFileCancel.run(h)
            SubtestFriendInfo.run(h)
            SubtestConference.run(h)
            SubtestGroup.run(h)
            SubtestToxav.run(h)
            SubtestPersistence.run(h)
        }
    }
}
