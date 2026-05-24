package im.tox.tox4j.e2e

import im.tox.tox4j.core.callbacks.ToxCoreEventListener
import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.core.enums.ToxUserStatus
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

@OptIn(kotlin.ExperimentalStdlibApi::class)
object SubtestFriendInfo {
    fun run(harness: Tox4jHarness) {
        check(harness.toxes.size >= 2) { "SubtestFriendInfo needs at least 2 toxes" }
        val alice = harness.toxes[0]
        val bob = harness.toxes[1]
        val aliceFromBob = bob.friendByPublicKey(alice.publicKey)
        val bobFromAlice = alice.friendByPublicKey(bob.publicKey)

        alice.setTyping(bobFromAlice, true)
        val typingHandler =
            object : ToxCoreEventListener<Boolean> {
                override fun friendTyping(
                    friendNumber: ToxFriendNumber,
                    typing: Boolean,
                    state: Boolean,
                ): Boolean = state || (friendNumber.value == aliceFromBob.value && typing)
            }
        harness.waitFor(
            initial = false,
            handler = typingHandler,
            message = "Bob never saw Alice's typing status",
        ) { it }

        alice.setStatus(ToxUserStatus.BUSY)
        val statusHandler =
            object : ToxCoreEventListener<ToxUserStatus?> {
                override fun friendStatus(
                    friendNumber: ToxFriendNumber,
                    status: ToxUserStatus,
                    state: ToxUserStatus?,
                ): ToxUserStatus? = state ?: status.takeIf { friendNumber.value == aliceFromBob.value }
            }
        val seenStatus =
            harness.waitFor(
                initial = null as ToxUserStatus?,
                handler = statusHandler,
                message = "Bob never saw Alice's status change",
            ) { it != null }
        assertEquals(ToxUserStatus.BUSY, seenStatus)

        val statusMsg = "Busy coding".encodeToByteArray()
        alice.setStatusMessage(statusMsg)
        val statusMsgHandler =
            object : ToxCoreEventListener<ByteArray?> {
                override fun friendStatusMessage(
                    friendNumber: ToxFriendNumber,
                    message: ByteArray,
                    state: ByteArray?,
                ): ByteArray? = state ?: message.takeIf { friendNumber.value == aliceFromBob.value }
            }
        val seenMsg =
            harness.waitFor(
                initial = null as ByteArray?,
                handler = statusMsgHandler,
                message = "Bob never saw Alice's status message",
            ) { it != null }
        assertContentEquals(statusMsg, seenMsg)

        val nick = "Alice".encodeToByteArray()
        alice.setName(nick)
        val nameHandler =
            object : ToxCoreEventListener<ByteArray?> {
                override fun friendName(
                    friendNumber: ToxFriendNumber,
                    name: ByteArray,
                    state: ByteArray?,
                ): ByteArray? = state ?: name.takeIf { friendNumber.value == aliceFromBob.value }
            }
        val seenName =
            harness.waitFor(
                initial = null as ByteArray?,
                handler = nameHandler,
                message = "Bob never saw Alice's name change",
            ) { it != null }
        assertContentEquals(nick, seenName)
    }
}
