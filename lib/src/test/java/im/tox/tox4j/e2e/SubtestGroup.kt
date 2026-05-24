package im.tox.tox4j.e2e

import im.tox.tox4j.core.callbacks.ToxCoreEventListener
import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.core.data.ToxGroupMessageId
import im.tox.tox4j.core.data.ToxGroupNumber
import im.tox.tox4j.core.data.ToxGroupPeerNumber
import im.tox.tox4j.core.enums.ToxGroupPrivacyState
import im.tox.tox4j.core.enums.ToxMessageType
import kotlin.test.assertContentEquals

@OptIn(kotlin.ExperimentalStdlibApi::class)
object SubtestGroup {
    private val payload = "NgcHello".encodeToByteArray()

    fun run(harness: Tox4jHarness) {
        check(harness.toxes.size >= 2) { "SubtestGroup needs at least 2 toxes" }
        val alice = harness.toxes[0]
        val bob = harness.toxes[1]
        val bobFromAlice = alice.friendByPublicKey(bob.publicKey)
        val aliceFromBob = bob.friendByPublicKey(alice.publicKey)

        val aliceGroup =
            alice.groupNew(
                ToxGroupPrivacyState.PRIVATE,
                "NgcTest".encodeToByteArray(),
                "Alice".encodeToByteArray(),
            )
        alice.groupInviteFriend(aliceGroup, bobFromAlice)

        data class Invite(
            val friend: ToxFriendNumber,
            val inviteData: ByteArray,
        )
        val inviteHandler =
            object : ToxCoreEventListener<Invite?> {
                override fun groupInvite(
                    friendNumber: ToxFriendNumber,
                    inviteData: ByteArray,
                    groupName: ByteArray,
                    state: Invite?,
                ): Invite? = state ?: Invite(friendNumber, inviteData)
            }
        val invite =
            harness.waitFor(
                initial = null as Invite?,
                handler = inviteHandler,
                timeoutMs = 30_000L,
                message = "Bob never received Alice's NGC invite",
            ) { it != null }!!
        check(invite.friend.value == aliceFromBob.value) {
            "NGC invite came from an unexpected friend: ${invite.friend}"
        }

        val bobGroup =
            bob.groupInviteAccept(
                invite.friend,
                invite.inviteData,
                "Bob".encodeToByteArray(),
                ByteArray(0),
            )

        // Messages only arrive once the group connects, so resend until one does.
        data class MsgState(
            val received: ByteArray? = null,
        )
        val messageHandler =
            object : ToxCoreEventListener<MsgState> {
                override fun groupMessage(
                    groupNumber: ToxGroupNumber,
                    peerId: ToxGroupPeerNumber,
                    messageType: ToxMessageType,
                    message: ByteArray,
                    messageId: ToxGroupMessageId,
                    state: MsgState,
                ): MsgState =
                    if (state.received != null) {
                        state
                    } else if (groupNumber.value == bobGroup.value && message.contentEquals(payload)) {
                        state.copy(received = message)
                    } else {
                        state
                    }
            }
        val finalState =
            harness.waitFor(
                initial = MsgState(),
                handler = messageHandler,
                timeoutMs = 60_000L,
                message = "Bob never received Alice's NGC message",
            ) { state ->
                if (state.received == null) {
                    try {
                        alice.groupSendMessage(
                            aliceGroup,
                            ToxMessageType.NORMAL,
                            payload,
                        )
                    } catch (_: Exception) {
                        // Not connected yet.
                    }
                }
                state.received != null
            }
        assertContentEquals(payload, finalState.received)

        val newTopic = "JVM NGC Topic".encodeToByteArray()
        alice.groupSetTopic(aliceGroup, newTopic)
        val topicHandler =
            object : ToxCoreEventListener<ByteArray?> {
                override fun groupTopic(
                    groupNumber: ToxGroupNumber,
                    peerId: ToxGroupPeerNumber,
                    topic: ByteArray,
                    state: ByteArray?,
                ): ByteArray? =
                    state ?: topic.takeIf {
                        groupNumber.value == bobGroup.value && topic.contentEquals(newTopic)
                    }
            }
        val seenTopic =
            harness.waitFor(
                initial = null as ByteArray?,
                handler = topicHandler,
                timeoutMs = 30_000L,
                message = "Bob never saw Alice's group topic change",
            ) { it != null }
        assertContentEquals(newTopic, seenTopic)

        val newName = "Alice2".encodeToByteArray()
        alice.groupSelfSetName(aliceGroup, newName)
        val peerNameHandler =
            object : ToxCoreEventListener<ByteArray?> {
                override fun groupPeerName(
                    groupNumber: ToxGroupNumber,
                    peerId: ToxGroupPeerNumber,
                    name: ByteArray,
                    state: ByteArray?,
                ): ByteArray? = state ?: name.takeIf { groupNumber.value == bobGroup.value && name.contentEquals(newName) }
            }
        val seenName =
            harness.waitFor(
                initial = null as ByteArray?,
                handler = peerNameHandler,
                timeoutMs = 30_000L,
                message = "Bob never saw Alice's NGC nickname change",
            ) { it != null }
        assertContentEquals(newName, seenName)
    }
}
