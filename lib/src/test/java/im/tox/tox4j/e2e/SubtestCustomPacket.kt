package im.tox.tox4j.e2e

import im.tox.tox4j.core.callbacks.ToxCoreEventListener
import im.tox.tox4j.core.data.ToxFriendNumber
import kotlin.test.assertContentEquals

/** Custom packet IDs: lossy 200-254, lossless 160-191. */
object SubtestCustomPacket {
    fun run(harness: Tox4jHarness) {
        check(harness.toxes.size >= 2) { "SubtestCustomPacket needs at least 2 toxes" }
        val alice = harness.toxes[0]
        val bob = harness.toxes[1]
        val aliceFromBob = bob.friendByPublicKey(alice.publicKey)
        val bobFromAlice = alice.friendByPublicKey(bob.publicKey)

        val lossy = byteArrayOf(200.toByte(), 1, 2, 3)
        val lossless = byteArrayOf(160.toByte(), 4, 5, 6)

        alice.friendSendLossyPacket(bobFromAlice, lossy)
        alice.friendSendLosslessPacket(bobFromAlice, lossless)

        data class Received(
            val lossy: ByteArray? = null,
            val lossless: ByteArray? = null,
        )
        val handler =
            object : ToxCoreEventListener<Received> {
                override fun friendLossyPacket(
                    friendNumber: ToxFriendNumber,
                    data: ByteArray,
                    state: Received,
                ): Received =
                    if (friendNumber.value == aliceFromBob.value && data.isNotEmpty() && data[0] == 200.toByte()) {
                        state.copy(lossy = state.lossy ?: data)
                    } else {
                        state
                    }

                override fun friendLosslessPacket(
                    friendNumber: ToxFriendNumber,
                    data: ByteArray,
                    state: Received,
                ): Received =
                    if (friendNumber.value == aliceFromBob.value && data.isNotEmpty() && data[0] == 160.toByte()) {
                        state.copy(lossless = state.lossless ?: data)
                    } else {
                        state
                    }
            }
        val received =
            harness.waitFor(
                initial = Received(),
                handler = handler,
                message = "Bob never received Alice's custom packets",
            ) { it.lossy != null && it.lossless != null }

        assertContentEquals(lossy, received.lossy)
        assertContentEquals(lossless, received.lossless)
    }
}
