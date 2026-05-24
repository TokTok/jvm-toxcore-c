package im.tox.tox4j.e2e

import im.tox.tox4j.core.callbacks.ToxCoreEventListener
import im.tox.tox4j.core.data.ToxConferenceNumber
import im.tox.tox4j.core.data.ToxConferencePeerNumber
import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.core.enums.ToxConferenceType
import im.tox.tox4j.core.enums.ToxMessageType
import kotlin.test.assertContentEquals

@OptIn(kotlin.ExperimentalStdlibApi::class)
object SubtestConference {
    private val payload = "ConfHello".encodeToByteArray()
    private val newTitle = "JVM Conference".encodeToByteArray()

    fun run(harness: Tox4jHarness) {
        check(harness.toxes.size >= 2) { "SubtestConference needs at least 2 toxes" }
        val alice = harness.toxes[0]
        val bob = harness.toxes[1]
        val bobFromAlice = alice.friendByPublicKey(bob.publicKey)
        val aliceFromBob = bob.friendByPublicKey(alice.publicKey)

        val aliceConf = alice.conferenceNew()
        alice.conferenceInvite(bobFromAlice, aliceConf)

        data class Invite(
            val friend: ToxFriendNumber,
            val cookie: ByteArray,
        )
        val inviteHandler =
            object : ToxCoreEventListener<Invite?> {
                override fun conferenceInvite(
                    friendNumber: ToxFriendNumber,
                    type: ToxConferenceType,
                    cookie: ByteArray,
                    state: Invite?,
                ): Invite? = state ?: Invite(friendNumber, cookie)
            }
        val invite =
            harness.waitFor(
                initial = null as Invite?,
                handler = inviteHandler,
                timeoutMs = 30_000L,
                message = "Bob never received Alice's conference invite",
            ) { it != null }!!
        check(invite.friend.value == aliceFromBob.value) {
            "Conference invite came from an unexpected friend: ${invite.friend}"
        }

        val bobConf = bob.conferenceJoin(invite.friend, invite.cookie)

        // Messages only arrive once the conference connects, so resend until one does.
        data class MsgState(
            val received: ByteArray? = null,
        )
        val messageHandler =
            object : ToxCoreEventListener<MsgState> {
                override fun conferenceMessage(
                    conferenceNumber: ToxConferenceNumber,
                    peerNumber: ToxConferencePeerNumber,
                    type: ToxMessageType,
                    message: ByteArray,
                    state: MsgState,
                ): MsgState =
                    if (state.received != null) {
                        state
                    } else if (conferenceNumber.value == bobConf.value && message.contentEquals(payload)) {
                        state.copy(received = message)
                    } else {
                        state
                    }
            }
        val finalState =
            harness.waitFor(
                initial = MsgState(),
                handler = messageHandler,
                timeoutMs = 30_000L,
                message = "Bob never received Alice's conference message",
            ) { state ->
                if (state.received == null) {
                    try {
                        alice.conferenceSendMessage(
                            aliceConf,
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

        alice.conferenceSetTitle(aliceConf, newTitle)
        val titleHandler =
            object : ToxCoreEventListener<ByteArray?> {
                override fun conferenceTitle(
                    conferenceNumber: ToxConferenceNumber,
                    peerNumber: ToxConferencePeerNumber,
                    title: ByteArray,
                    state: ByteArray?,
                ): ByteArray? = state ?: title.takeIf { conferenceNumber.value == bobConf.value && title.contentEquals(newTitle) }
            }
        val seenTitle =
            harness.waitFor(
                initial = null as ByteArray?,
                handler = titleHandler,
                timeoutMs = 30_000L,
                message = "Bob never saw Alice's conference title change",
            ) { it != null }
        assertContentEquals(newTitle, seenTitle)
    }
}
