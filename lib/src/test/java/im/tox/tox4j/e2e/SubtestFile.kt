package im.tox.tox4j.e2e

import im.tox.tox4j.core.ToxCoreConstants
import im.tox.tox4j.core.callbacks.ToxCoreEventListener
import im.tox.tox4j.core.data.ToxFileId
import im.tox.tox4j.core.data.ToxFileNumber
import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.core.enums.ToxFileControl
import im.tox.tox4j.core.enums.ToxFileKind
import kotlin.test.assertContentEquals

@OptIn(kotlin.ExperimentalStdlibApi::class)
object SubtestFile {
    private val payload = "Hello File Transfer World".encodeToByteArray()

    fun run(harness: Tox4jHarness) {
        check(harness.toxes.size >= 2) { "SubtestFile needs at least 2 toxes" }
        val alice = harness.toxes[0]
        val bob = harness.toxes[1]
        val bobFromAlice = alice.friendByPublicKey(bob.publicKey)
        val aliceFromBob = bob.friendByPublicKey(alice.publicKey)

        val aliceFileNumber =
            alice.fileSend(
                bobFromAlice,
                ToxFileKind.DATA,
                payload.size.toLong(),
                ToxFileId(ByteArray(ToxCoreConstants.FILE_ID_LENGTH)),
                "test.txt".encodeToByteArray(),
            )

        data class State(
            val bobFileNumber: ToxFileNumber? = null,
            val received: ByteArray = ByteArray(0),
            val completed: Boolean = false,
        )

        val handler =
            object : ToxCoreEventListener<State> {
                override fun fileRecv(
                    friendNumber: ToxFriendNumber,
                    fileNumber: ToxFileNumber,
                    kind: Int,
                    fileSize: Long,
                    filename: ByteArray,
                    state: State,
                ): State =
                    if (state.bobFileNumber == null && friendNumber.value == aliceFromBob.value) {
                        bob.fileControl(friendNumber, fileNumber, ToxFileControl.RESUME)
                        state.copy(bobFileNumber = fileNumber)
                    } else {
                        state
                    }

                override fun fileRecvChunk(
                    friendNumber: ToxFriendNumber,
                    fileNumber: ToxFileNumber,
                    position: Long,
                    data: ByteArray,
                    state: State,
                ): State =
                    if (friendNumber.value == aliceFromBob.value && fileNumber == state.bobFileNumber) {
                        if (data.isEmpty()) {
                            state.copy(completed = true)
                        } else {
                            state.copy(received = state.received + data)
                        }
                    } else {
                        state
                    }

                override fun fileChunkRequest(
                    friendNumber: ToxFriendNumber,
                    fileNumber: ToxFileNumber,
                    position: Long,
                    length: Long,
                    state: State,
                ): State {
                    if (friendNumber.value == bobFromAlice.value && fileNumber == aliceFileNumber) {
                        val from = position.toInt()
                        val len = length.toInt()
                        if (len == 0 || from >= payload.size) {
                            alice.fileSendChunk(friendNumber, fileNumber, position, ByteArray(0))
                        } else {
                            val end = minOf(from + len, payload.size)
                            alice.fileSendChunk(friendNumber, fileNumber, position, payload.copyOfRange(from, end))
                        }
                    }
                    return state
                }
            }

        val finished =
            harness.waitFor(
                initial = State(),
                handler = handler,
                timeoutMs = 30_000L,
                message = "File transfer did not complete within timeout",
            ) { it.completed }

        assertContentEquals(payload, finished.received)
    }
}
