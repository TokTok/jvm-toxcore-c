package im.tox.tox4j.core

import im.tox.tox4j.core.callbacks.ToxCoreEventListener
import im.tox.tox4j.core.data.Port
import im.tox.tox4j.core.data.ToxConferenceId
import im.tox.tox4j.core.data.ToxConferenceMessage
import im.tox.tox4j.core.data.ToxConferenceNumber
import im.tox.tox4j.core.data.ToxConferenceOfflinePeerNumber
import im.tox.tox4j.core.data.ToxConferencePeerName
import im.tox.tox4j.core.data.ToxConferencePeerNumber
import im.tox.tox4j.core.data.ToxConferenceTitle
import im.tox.tox4j.core.data.ToxConferenceUid
import im.tox.tox4j.core.data.ToxFileId
import im.tox.tox4j.core.data.ToxFilename
import im.tox.tox4j.core.data.ToxFriendAddress
import im.tox.tox4j.core.data.ToxFriendMessage
import im.tox.tox4j.core.data.ToxFriendMessageId
import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.core.data.ToxFriendRequestMessage
import im.tox.tox4j.core.data.ToxGroupChatId
import im.tox.tox4j.core.data.ToxGroupMessage
import im.tox.tox4j.core.data.ToxGroupName
import im.tox.tox4j.core.data.ToxGroupNumber
import im.tox.tox4j.core.data.ToxGroupPartMessage
import im.tox.tox4j.core.data.ToxGroupPassword
import im.tox.tox4j.core.data.ToxGroupPeerNumber
import im.tox.tox4j.core.data.ToxGroupTopic
import im.tox.tox4j.core.data.ToxLosslessPacket
import im.tox.tox4j.core.data.ToxLossyPacket
import im.tox.tox4j.core.data.ToxNickname
import im.tox.tox4j.core.data.ToxPublicKey
import im.tox.tox4j.core.data.ToxSecretKey
import im.tox.tox4j.core.data.ToxStatusMessage
import im.tox.tox4j.core.enums.ToxConferenceType
import im.tox.tox4j.core.enums.ToxConnection
import im.tox.tox4j.core.enums.ToxFileControl
import im.tox.tox4j.core.enums.ToxGroupPrivacyState
import im.tox.tox4j.core.enums.ToxGroupRole
import im.tox.tox4j.core.enums.ToxGroupTopicLock
import im.tox.tox4j.core.enums.ToxGroupVoiceState
import im.tox.tox4j.core.enums.ToxMessageType
import im.tox.tox4j.core.enums.ToxUserStatus
import im.tox.tox4j.core.options.ToxOptions

/**
 * Interface for a basic wrapper of tox chat functionality.
 *
 * This interface is designed to be thread-safe. However, once [[ToxCore.close]] has been called,
 * all subsequent calls will result in [[im.tox.tox4j.exceptions.ToxKilledException]] being thrown.
 * When one thread invokes [[ToxCore.close]], all other threads with pending calls will throw. The
 * exception is unchecked, as it should not occur in a normal execution flow. To prevent it from
 * occurring in a multi-threaded environment, all additional threads should be stopped or stop using
 * the instance before one thread invokes [[ToxCore.close]] on it, or appropriate exception handlers
 * should be installed in all threads.
 */
@kotlin.ExperimentalStdlibApi
interface ToxCore : AutoCloseable {
    /**
     * Store all information associated with the tox instance to a byte array.
     *
     * The data in the byte array can be used to create a new instance with [[load]] by passing it
     * to the [[ToxOptions]] constructor. The concrete format in this serialised instance is
     * implementation-defined. Passing save data created by one class to a different class may not
     * work.
     *
     * @return a byte array containing the serialised tox instance.
     */
    val getSavedata: ByteArray

    /**
     * Create a new [[ToxCore]] instance with different options. The implementation may choose to
     * create an object of its own class or a different class. If the implementation was compatible
     * with another subsystem implementation (e.g. [[im.tox.tox4j.av.ToxAv]]), then the new object
     * must be compatible with the same implementation.
     *
     * This function will bring the instance into a valid state. Running the event loop with a new
     * instance will operate correctly.
     *
     * If the [[ToxOptions.saveData]] field is not empty, this function will load the Tox instance
     * from a byte array previously filled by [[getSavedata]].
     *
     * If loading failed or succeeded only partially, an exception will be thrown.
     *
     * @return a new [[ToxCore]] instance.
     * @throws ToxNewException
     */
    fun load(options: ToxOptions): ToxCore

    /**
     * Shut down the tox instance.
     *
     * Releases all resources associated with the Tox instance and disconnects from the network.
     *
     * Once this method has been called, all other calls on this instance will throw
     * [[im.tox.tox4j.exceptions.ToxKilledException]]. A closed instance cannot be reused; a new
     * instance must be created.
     */
    override fun close(): Unit

    /**
     * Bootstrap into the tox network.
     *
     * Sends a "get nodes" request to the given bootstrap node with IP, port, and public key to
     * setup connections.
     *
     * This function will only attempt to connect to the node using UDP. If you want to additionally
     * attempt to connect using TCP, use [[addTcpRelay]] together with this function.
     *
     * @param address the hostname, or an IPv4/IPv6 address of the node.
     * @param port the port of the node.
     * @param publicKey the public key of the node.
     * @throws ToxBootstrapException
     */
    fun bootstrap(
        address: String,
        port: Port,
        publicKey: ToxPublicKey,
    ): Unit

    /**
     * Connect to a TCP relay to forward traffic.
     *
     * This function can be used to initiate TCP connections to different ports on the same
     * bootstrap node, or to add TCP relays without using them as bootstrap nodes.
     *
     * @param address the hostname, or an IPv4/IPv6 address of the node.
     * @param port the TCP port the node is running a relay on.
     * @param publicKey the public key of the node.
     * @throws ToxBootstrapException
     */
    fun addTcpRelay(
        address: String,
        port: Port,
        publicKey: ToxPublicKey,
    ): Unit

    /**
     * Get the UDP port this instance is bound to.
     *
     * @return a port number between 1 and 65535.
     * @throws ToxGetPortException
     */
    val getUdpPort: Port

    /**
     * Return the TCP port this Tox instance is bound to. This is only relevant if the instance is
     * acting as a TCP relay.
     *
     * @return a port number between 1 and 65535.
     * @throws ToxGetPortException
     */
    val getTcpPort: Port

    /**
     * Writes the temporary DHT public key of this instance to a byte array.
     *
     * This can be used in combination with an externally accessible IP address and the bound port
     * (from [[getUdpPort]]}) to run a temporary bootstrap node.
     *
     * Be aware that every time a new instance is created, the DHT public key changes, meaning this
     * cannot be used to run a permanent bootstrap node.
     *
     * @return a byte array of size [[ToxCoreConstants.PUBLIC_KEY_SIZE]]
     */
    val getDhtId: ToxPublicKey

    /**
     * Get the time in milliseconds until [[iterate]] should be called again for optimal
     * performance.
     *
     * @return the time in milliseconds until [[iterate]] should be called again.
     */
    val iterationInterval: Int

    /**
     * The main loop.
     *
     * This should be invoked every [[iterationInterval]] milliseconds.
     */
    fun <S> iterate(
        handler: ToxCoreEventListener<S>,
        state: S,
    ): S

    /**
     * Copy the Tox Public Key (long term) from the Tox object.
     *
     * @return a byte array of size [[ToxCoreConstants.PUBLIC_KEY_SIZE]]
     */
    val getPublicKey: ToxPublicKey

    /**
     * Copy the Tox Secret Key from the Tox object.
     *
     * @return a byte array of size [[ToxCoreConstants.SECRET_KEY_SIZE]]
     */
    val getSecretKey: ToxSecretKey

    /**
     * Set the 4-byte nospam part of the address.
     *
     * Setting the nospam makes it impossible for others to send us friend requests that contained
     * the old nospam number.
     *
     * @param nospam the new nospam number.
     */
    fun setNospam(nospam: Int): Unit

    /** Get our current nospam number. */
    val getNospam: Int

    /**
     * Get our current tox address to give to friends.
     *
     * The format is the following: [Public Key (32 bytes)][noSpam number (4 bytes)][checksum (2
     * bytes)]. After a call to [[setNospam]], the old address can no longer be used to send friend
     * requests to this instance.
     *
     * Note that it is not in a human-readable format. To display it to users, it needs to be
     * formatted.
     *
     * @return a byte array of size [[ToxCoreConstants.ADDRESS_SIZE]]
     */
    val getAddress: ToxFriendAddress

    /**
     * Set the nickname for the Tox client.
     *
     * Cannot be longer than [[ToxCoreConstants.MAX_NAME_LENGTH]] bytes. Can be empty (zero-length).
     *
     * @param name A byte array containing the new nickname..
     * @throws ToxSetInfoException
     */
    fun setName(name: ToxNickname): Unit

    /** Get our own nickname. */
    val getName: ToxNickname

    /**
     * Set our status message.
     *
     * Cannot be longer than [[ToxCoreConstants.MAX_STATUS_MESSAGE_LENGTH]] bytes.
     *
     * @param message the status message to set.
     * @throws ToxSetInfoException
     */
    fun setStatusMessage(message: ToxStatusMessage): Unit

    /** Gets our own status message. May be null if the status message was empty. */
    val getStatusMessage: ToxStatusMessage

    /**
     * Set our status.
     *
     * @param status status to set.
     */
    fun setStatus(status: ToxUserStatus): Unit

    /** Get our status. */
    val getStatus: ToxUserStatus

    /**
     * Add a friend to the friend list and send a friend request.
     *
     * A friend request message must be at least 1 byte long and at most
     * [[ToxCoreConstants.MAX_FRIEND_REQUEST_LENGTH]].
     *
     * Friend numbers are unique identifiers used in all functions that operate on friends. Once
     * added, a friend number is stable for the lifetime of the Tox object. After saving the state
     * and reloading it, the friend numbers may not be the same as before. Deleting a friend creates
     * a gap in the friend number set, which is filled by the next adding of a friend. Any pattern
     * in friend numbers should not be relied on.
     *
     * If more than [[Int.MaxValue]] friends are added, this function throws an exception.
     *
     * @param address the address to add as a friend ([[ToxCoreConstants.ADDRESS_SIZE]] bytes).
     *
     * ```
     *                This is the byte array the friend got from their own [[getAddress]].
     * @param message
     * ```
     *
     * the message to send with the friend request (must not be empty).
     *
     * @return the new friend's friend number.
     * @throws ToxFriendAddException
     * @throws IllegalArgumentException
     */
    fun addFriend(
        address: ToxFriendAddress,
        message: ToxFriendRequestMessage,
    ): ToxFriendNumber

    /**
     * Add a friend without sending a friend request.
     *
     * This function is used to add a friend in response to a friend request. If the client receives
     * a friend request, it can be reasonably sure that the other client added this client as a
     * friend, eliminating the need for a friend request.
     *
     * This function is also useful in a situation where both instances are controlled by the same
     * entity, so that this entity can perform the mutual friend adding. In this case, there is no
     * need for a friend request, either.
     *
     * @param publicKey the Public Key to add as a friend ([[ToxCoreConstants.PUBLIC_KEY_SIZE]]
     *   bytes).
     * @return the new friend's friend number.
     * @throws ToxFriendAddException
     * @throws IllegalArgumentException
     */
    fun addFriendNorequest(publicKey: ToxPublicKey): ToxFriendNumber

    /**
     * Remove a friend from the friend list.
     *
     * This does not notify the friend of their deletion. After calling this function, this client
     * will appear offline to the friend and no communication can occur between the two.
     *
     * @param friendNumber the friend number to delete.
     * @throws ToxFriendDeleteException
     */
    fun deleteFriend(friendNumber: ToxFriendNumber): Unit

    /**
     * Gets the friend number for the specified Public Key.
     *
     * @param publicKey the Public Key.
     * @return the friend number that is associated with the Public Key.
     * @throws ToxFriendByPublicKeyException
     */
    fun friendByPublicKey(publicKey: ToxPublicKey): ToxFriendNumber

    /**
     * Gets the Public Key for the specified friend number.
     *
     * @param friendNumber the friend number.
     * @return the Public Key associated with the friend number.
     * @throws ToxFriendGetPublicKeyException
     */
    fun getFriendPublicKey(friendNumber: ToxFriendNumber): ToxPublicKey

    /**
     * Checks whether a friend with the specified friend number exists.
     *
     * If this function returns <code>true</code>, the return value is valid until the friend is
     * deleted. If <code>false</code> is returned, the return value is valid until either of
     * [[addFriend]] or [[addFriendNorequest]] is invoked.
     *
     * @param friendNumber the friend number to check.
     * @return true if such a friend exists.
     */
    fun friendExists(friendNumber: ToxFriendNumber): Boolean

    /**
     * Get an array of currently valid friend numbers.
     *
     * This list is valid until either of the following is invoked: [[deleteFriend]], [[addFriend]],
     * [[addFriendNorequest]].
     *
     * @return an array containing the currently valid friend numbers, the empty int array if there
     *   are no friends.
     */
    val getFriendList: IntArray

    /**
     * Get an array of [[ToxFriendNumber]] objects with the same values as [[getFriendList]].
     *
     * This method exists for Java compatibility, because [[getFriendList]] must return an int
     * array.
     *
     * @return [[getFriendList]] mapped to [[ToxFriendNumber]].
     */
    val getFriendNumbers: List<ToxFriendNumber>
        get() = getFriendList.map { ToxFriendNumber(it) }

    /**
     * Tell friend number whether or not we are currently typing.
     *
     * The client is responsible for turning it on or off.
     *
     * @param friendNumber the friend number to set typing status for.
     * @param typing <code>true</code> if we are currently typing.
     * @throws ToxSetTypingException
     */
    fun setTyping(
        friendNumber: ToxFriendNumber,
        typing: Boolean,
    ): Unit

    /**
     * Send a text chat message to an online friend.
     *
     * This function creates a chat message packet and pushes it into the send queue.
     *
     * The message length may not exceed [[ToxCoreConstants.MAX_MESSAGE_LENGTH]]. Larger messages
     * must be split by the client and sent as separate messages. Other clients can then reassemble
     * the fragments. Messages may not be empty.
     *
     * The return value of this function is the message ID. If a read receipt is received, the
     * triggered [[FriendReadReceiptCallback]] event will be passed this message ID.
     *
     * Message IDs are unique per friend per instance. The first message ID is 0. Message IDs are
     * incremented by 1 each time a message is sent. If [[Int.MaxValue]] messages were sent, the
     * next message ID is [[Int.MinValue]].
     *
     * Message IDs are not stored in the array returned by [[getSavedata]].
     *
     * @param friendNumber The friend number of the friend to send the message to.
     * @param messageType Message type (normal, action, ...).
     * @param message The message text
     * @return the message ID.
     * @throws ToxFriendSendMessageException
     */
    fun friendSendMessage(
        friendNumber: ToxFriendNumber,
        messageType: ToxMessageType,
        message: ToxFriendMessage,
    ): ToxFriendMessageId

    /**
     * Sends a file control command to a friend for a given file transfer.
     *
     * @param friendNumber The friend number of the friend the file is being transferred to or
     *   received from.
     * @param fileNumber The friend-specific identifier for the file transfer.
     * @param control The control command to send.
     * @throws ToxFileControlException
     */
    fun fileControl(
        friendNumber: ToxFriendNumber,
        fileNumber: Int,
        control: ToxFileControl,
    ): Unit

    /**
     * Sends a file seek control command to a friend for a given file transfer.
     *
     * This function can only be called to resume a file transfer right before
     * [[ToxFileControl.RESUME]] is sent.
     *
     * @param friendNumber The friend number of the friend the file is being received from.
     * @param fileNumber The friend-specific identifier for the file transfer.
     * @param position The position that the file should be seeked to.
     * @throws ToxFileSeekException
     */
    fun fileSeek(
        friendNumber: ToxFriendNumber,
        fileNumber: Int,
        position: Long,
    ): Unit

    /**
     * Return the file id associated to the file transfer as a byte array.
     *
     * @param friendNumber The friend number of the friend the file is being transferred to or
     *   received from.
     * @param fileNumber The friend-specific identifier for the file transfer.
     * @throws ToxFileGetException
     */
    fun getFileFileId(
        friendNumber: ToxFriendNumber,
        fileNumber: Int,
    ): ToxFileId

    /**
     * Send a file transmission request.
     *
     * Maximum filename length is [[ToxCoreConstants.MAX_FILENAME_LENGTH]] bytes. The filename
     * should generally just be a file name, not a path with directory names.
     *
     * If a non-negative file size is provided, it can be used by both sides to determine the
     * sending progress. File size can be set to a negative value for streaming data of unknown
     * size.
     *
     * File transmission occurs in chunks, which are requested through the
     * [[FileChunkRequestCallback] ] event.
     *
     * When a friend goes offline, all file transfers associated with the friend are purged from
     * core.
     *
     * If the file contents change during a transfer, the behaviour is unspecified in general. What
     * will actually happen depends on the mode in which the file was modified and how the client
     * determines the file size.
     * - If the file size was increased
     * - and sending mode was streaming (fileSize = -1), the behaviour
     *
     * ```
     *     will be as expected.
     * ```
     * - and sending mode was file (fileSize != -1), the
     *
     * ```
     *     [[FileChunkRequestCallback]] callback will receive length = 0 when Core thinks
     *     the file transfer has finished. If the client remembers the file size as
     *     it was when sending the request, it will terminate the transfer normally.
     *     If the client re-reads the size, it will think the friend cancelled the
     *     transfer.
     * ```
     * - If the file size was decreased
     * - and sending mode was streaming, the behaviour is as expected.
     * - and sending mode was file, the callback will return 0 at the new
     *
     * ```
     *     (earlier) end-of-file, signalling to the friend that the transfer was
     *     cancelled.
     * ```
     * - If the file contents were modified
     * - at a position before the current read, the two files (local and remote)
     *
     * ```
     *     will differ after the transfer terminates.
     * ```
     * - at a position after the current read, the file transfer will succeed as
     *
     * ```
     *     expected.
     * ```
     * - In either case, both sides will regard the transfer as complete and
     *
     * ```
     *     successful.
     *
     * @param friendNumber
     * ```
     *
     * The friend number of the friend the file send request should be sent to.
     *
     * @param kind The meaning of the file to be sent.
     * @param fileSize Size in bytes of the file the client wants to send, -1 if unknown or
     *   streaming.
     * @param fileId A file identifier of length [[ToxCoreConstants.FILE_ID_LENGTH]] that can be
     *   used to
     *
     * ```
     *               uniquely identify file transfers across core restarts. If empty, a random one will
     *               be generated by core. It can then be obtained by using [[getFileFileId]]
     * @param filename
     * ```
     *
     * Name of the file. Does not need to be the actual name. This
     *
     * ```
     *                 name will be sent along with the file send request.
     * @return
     * ```
     *
     * A file number used as an identifier in subsequent callbacks. This
     *
     * ```
     *         number is per friend. File numbers are reused after a transfer terminates.
     *         Any pattern in file numbers should not be relied on.
     * ```
     *
     * @throws ToxFileSendException
     */
    fun fileSend(
        friendNumber: ToxFriendNumber,
        kind: Int,
        fileSize: Long,
        fileId: ToxFileId,
        filename: ToxFilename,
    ): Int

    /**
     * Send a chunk of file data to a friend.
     *
     * This function is called in response to the [[FileChunkRequestCallback]] callback. The length
     * parameter should be equal to the one received though the callback. If it is zero, the
     * transfer is assumed complete. For files with known size, Core will know that the transfer is
     * complete after the last byte has been received, so it is not necessary (though not harmful)
     * to send a zero-length chunk to terminate. For streams, core will know that the transfer is
     * finished if a chunk with length less than the length requested in the callback is sent.
     *
     * @param friendNumber The friend number of the receiving friend for this file.
     * @param fileNumber The file transfer identifier returned by [[fileSend]].
     * @param position The file or stream position from which the friend should continue writing.
     * @param data The chunk data.
     * @throws ToxFileSendChunkException
     */
    fun fileSendChunk(
        friendNumber: ToxFriendNumber,
        fileNumber: Int,
        position: Long,
        data: ByteArray,
    ): Unit

    /**
     * Send a custom lossy packet to a friend.
     *
     * The first byte of data must be in the range 200-254. Maximum length of a custom packet is
     * [[ToxCoreConstants.MAX_CUSTOM_PACKET_SIZE]].
     *
     * Lossy packets behave like UDP packets, meaning they might never reach the other side or might
     * arrive more than once (if someone is messing with the connection) or might arrive in the
     * wrong order.
     *
     * Unless latency is an issue, it is recommended that you use lossless custom packets instead.
     *
     * @param friendNumber The friend number of the friend this lossy packet should be sent to.
     * @param data A byte array containing the packet data including packet id.
     * @throws ToxFriendCustomPacketException
     */
    fun friendSendLossyPacket(
        friendNumber: ToxFriendNumber,
        data: ToxLossyPacket,
    ): Unit

    /**
     * Send a custom lossless packet to a friend.
     *
     * The first byte of data must be in the range 160-191. Maximum length of a custom packet is
     * [[ToxCoreConstants.MAX_CUSTOM_PACKET_SIZE]].
     *
     * Lossless packet behaviour is comparable to TCP (reliability, arrive in order) but with
     * packets instead of a stream.
     *
     * @param friendNumber The friend number of the friend this lossless packet should be sent to.
     * @param data A byte array containing the packet data including packet id.
     * @throws ToxFriendCustomPacketException
     */
    fun friendSendLosslessPacket(
        friendNumber: ToxFriendNumber,
        data: ToxLosslessPacket,
    ): Unit

    /**
     * Creates a new conference.
     *
     * This function creates and connects to a new text conference.
     *
     * @return
     *   - conference number on success
     *   - an unspecified value on failure
     */
    fun conferenceNew(): ToxConferenceNumber

    /**
     * This function deletes a conference.
     *
     * @param conferenceNumber The conference number of the conference to be
     *   deleted.
     */
    fun conferenceDelete(conferenceNumber: ToxConferenceNumber): Unit

    /**
     * Return the number of online peers in the conference.
     *
     * The unsigned integers less than this number are the valid values of
     * peerNumber for the functions querying these peers. Return value is
     * unspecified on failure.
     */
    fun conferencePeerCount(conferenceNumber: ToxConferenceNumber): Int

    /**
     * @return the name of [[peerNumber]] who is in [[conferenceNumber]].
     */
    fun conferencePeerGetName(
        conferenceNumber: ToxConferenceNumber,
        peerNumber: ToxConferencePeerNumber,
    ): ToxConferencePeerName

    /**
     * @return public key of [[peerNumber]] who is in [[conferenceNumber]].
     */
    fun conferencePeerGetPublicKey(
        conferenceNumber: ToxConferenceNumber,
        peerNumber: ToxConferencePeerNumber,
    ): ToxPublicKey

    /**
     * Return true if passed [[peerNumber]] corresponds to our own.
     */
    fun conferencePeerNumberIsOurs(
        conferenceNumber: ToxConferenceNumber,
        peerNumber: ToxConferencePeerNumber,
    ): Boolean

    /**
     * Return the number of offline peers in the conference.
     *
     * The unsigned integers less than this number are the valid values of
     * offlinePeerNumber for the functions querying these peers.
     *
     * Return value is unspecified on failure.
     */
    fun conferenceOfflinePeerCount(conferenceNumber: ToxConferenceNumber): Int

    /**
     * @return the name of [[offlinePeerNumber]] who is in [[conferenceNumber]].
     */
    fun conferenceOfflinePeerGetName(
        conferenceNumber: ToxConferenceNumber,
        offlinePeerNumber: ToxConferenceOfflinePeerNumber,
    ): ToxConferencePeerName

    /**
     * @return public key of [[offlinePeerNumber]] who is in [[conferenceNumber]].
     */
    fun conferenceOfflinePeerGetPublicKey(
        conferenceNumber: ToxConferenceNumber,
        offlinePeerNumber: ToxConferenceOfflinePeerNumber,
    ): ToxPublicKey

    /**
     * Return the last time [[offlinePeerNumber]] was active in the conference.
     *
     * Return value is unspecified on failure.
     */
    fun conferenceOfflinePeerGetLastActive(
        conferenceNumber: ToxConferenceNumber,
        offlinePeerNumber: ToxConferenceOfflinePeerNumber,
    ): Long

    /**
     * Set the maximum number of offline peers to store.
     *
     * The default value is 100.
     */
    fun conferenceSetMaxOffline(
        conferenceNumber: ToxConferenceNumber,
        maxOffline: Int,
    ): Unit

    /**
     * Invites a friend to a conference.
     *
     * @param friendNumber The friend number of the friend we want to invite.
     * @param conferenceNumber The conference number of the conference we want to
     *   invite the friend to.
     */
    fun conferenceInvite(
        friendNumber: ToxFriendNumber,
        conferenceNumber: ToxConferenceNumber,
    ): Unit

    /**
     * Joins a conference that the client has been invited to.
     *
     * After successfully joining the conference, the client will not be "connected"
     * to it until a handshaking procedure has been completed. A
     * `conference_connected` event will then occur for the conference. The client
     * will then remain connected to the conference until the conference is deleted,
     * even across Tox restarts. Many operations on a conference will fail with a
     * corresponding error if attempted on a conference to which the client is not
     * yet connected.
     *
     * @param friendNnumber The friend number of the friend who sent the invite.
     * @param cookie Received via the `conference_invite` event.
     *
     * @return conference number on success, an unspecified value on failure.
     */
    fun conferenceJoin(
        friendNumber: ToxFriendNumber,
        cookie: ByteArray,
    ): ToxConferenceNumber

    /**
     * Send a text chat message to the conference.
     *
     * This function creates a conference message packet and pushes it into the send
     * queue.
     *
     * The message length may not exceed [[ToxCoreConstants.TOX_MAX_MESSAGE_LENGTH]].
     * Larger messages must be split by the client and sent as separate messages.
     * Other clients can then reassemble the fragments.
     *
     * @param conferenceNnumber The conference number of the conference the message
     *   is intended for.
     * @param type Message type (normal, action, ...).
     * @param message A byte array containing the message text.
     */
    fun conferenceSendMessage(
        conferenceNumber: ToxConferenceNumber,
        messageType: ToxMessageType,
        message: ToxConferenceMessage,
    ): Unit

    /**
     * Get the title of the conference.
     *
     * The data returned is equal to the data received by the last `conferenceTitle` callback.
     *
     * @param conferenceNumber The conference number of the conference to get the title of.
     */
    fun conferenceGetTitle(conferenceNumber: ToxConferenceNumber): ToxConferenceTitle

    /**
     * Set the conference title and broadcast it to the rest of the conference.
     *
     * Title length cannot be longer than [[ToxCoreConstants.MAX_NAME_LENGTH]].
     */
    fun conferenceSetTitle(
        conferenceNumber: ToxConferenceNumber,
        title: ToxConferenceTitle,
    ): Unit

    /**
     * Return a list of valid conference numbers.
     *
     * Note that [[getSavedata]] saves all connected conferences; when a Tox instance is created
     * from savedata in which conferences were saved, those conferences will be connected at startup,
     * and will be listed by [[conferenceGetChatlist]].
     *
     * The conference number of a loaded conference may differ from the conference number it had when
     * it was saved.
     */
    val conferenceGetChatlist: IntArray

    /**
     * Return a list of valid conference numbers.
     *
     * Note that [[getSavedata]] saves all connected conferences; when a Tox instance is created
     * from savedata in which conferences were saved, those conferences will be connected at startup,
     * and will be listed by [[conferenceGetChatlist]].
     *
     * The conference number of a loaded conference may differ from the conference number it had when
     * it was saved.
     */
    val conferenceGetChatNumbers: List<ToxConferenceNumber>
        get() = conferenceGetChatlist.map { ToxConferenceNumber(it) }

    /**
     * Get the type (text or A/V) for the conference.
     */
    fun conferenceGetType(conferenceNumber: ToxConferenceNumber): ToxConferenceType

    /**
     * Get the conference unique ID.
     */
    fun conferenceGetId(conferenceNumber: ToxConferenceNumber): ToxConferenceId

    /**
     * Return the conference number associated with the specified id.
     */
    fun conferenceById(conferenceId: ToxConferenceId): ToxConferenceNumber

    /**
     * Get the conference unique ID.
     */
    fun conferenceGetUid(conferenceNumber: ToxConferenceNumber): ToxConferenceUid

    /**
     * Return the conference number associated with the specified uid.
     */
    fun conferenceByUid(conferenceUid: ToxConferenceUid): ToxConferenceNumber

    /**
     * Creates a new group chat.
     *
     * This function creates a new group chat object and adds it to the chats array.
     *
     * The caller of this function has Founder role privileges.
     *
     * The client should initiate its peer list with self info after calling this
     * function, as the [[peerJoin]] callback will not be triggered.
     *
     * @param privacyState The privacy state of the group. If this is set to
     *   [[ToxGroupPrivacyState.PUBLIC]], the group will attempt to announce itself
     *   to the DHT and anyone with the Chat ID may join. Otherwise a friend invite
     *   will be required to join the group.
     * @param groupName The name of the group.
     * @param name The name of the peer creating the group.
     */
    fun groupNew(
        privacyState: ToxGroupPrivacyState,
        groupName: ToxGroupName,
        name: ToxGroupName,
    ): ToxGroupNumber

    /**
     * Joins a group chat with specified Chat ID.
     *
     * This function creates a new group chat object, adds it to the chats array,
     * and sends a DHT announcement to find peers in the group associated with
     * chatId. Once a peer has been found a join attempt will be initiated.
     *
     * @param chatId The Chat ID of the group you wish to join.
     * @param password The password required to join the group. Set to null if no
     *   password is required.
     * @param name The name of the peer joining the group.
     */
    fun groupJoin(
        chatId: ToxGroupChatId,
        name: ToxGroupName,
        password: ToxGroupPassword,
    ): ToxGroupNumber

    /**
     * Returns true if the group chat is currently connected or attempting to
     * connect to other peers in the group.
     */
    fun groupIsConnected(groupNumber: ToxGroupNumber): Boolean

    /**
     * Disconnects from a group chat while retaining the group state and
     * credentials.
     *
     * @param groupNumber The group number of the group we wish to disconnect from.
     */
    fun groupDisconnect(groupNumber: ToxGroupNumber): Unit

    /**
     * Reconnects to a group.
     *
     * This function disconnects from all peers in the group, then attempts to
     * reconnect with the group. The caller's state is not changed (i.e. name,
     * status, role, chat public key etc.).
     *
     * @param groupNumber The group number of the group we wish to reconnect to.
     */
    fun groupReconnect(groupNumber: ToxGroupNumber): Unit

    /**
     * Leaves a group.
     *
     * This function sends a parting packet containing a custom (non-obligatory)
     * message to all peers in a group, and deletes the group from the chat array.
     * All group state information is permanently lost, including keys and role
     * credentials.
     *
     * @param groupNumber The group number of the group we wish to leave.
     * @param partMessage The parting message to be sent to all the peers.
     */
    fun groupLeave(
        groupNumber: ToxGroupNumber,
        partMessage: ToxGroupPartMessage,
    ): Unit

    /**
     * Set the client's nickname for the group instance designated by the given
     * group number.
     *
     * Nickname length cannot exceed TOX_MAX_NAME_LENGTH. If length is equal to
     * zero or name is a NULL pointer, the function call will fail.
     *
     * @param groupNumber The group number of the group to set the nickname for.
     * @param name A byte array containing the new nickname.
     *
     * @return true on success.
     */
    fun groupSelfSetName(
        groupNumber: ToxGroupNumber,
        name: ToxGroupName,
    ): Unit

    /**
     * Return the nickname set by [[groupSelfSetName]].
     *
     * If no nickname was set before calling this function, the name is empty,
     * and this function will return an empty string.
     *
     * @param groupNumber The group number of the group we wish to query.
     */
    fun groupSelfGetName(groupNumber: ToxGroupNumber): ToxGroupName

    /**
     * Set the client's status for the group instance.
     *
     * @param groupNumber The group number of the group we wish to set the status for.
     * @param status The status to set.
     */
    fun groupSelfSetStatus(
        groupNumber: ToxGroupNumber,
        status: ToxUserStatus,
    ): Unit

    /**
     * Returns the client's status for the group instance on success.
     */
    fun groupSelfGetStatus(groupNumber: ToxGroupNumber): ToxUserStatus

    /**
     * Returns the client's role for the group instance on success.
     */
    fun groupSelfGetRole(groupNumber: ToxGroupNumber): ToxGroupRole

    /**
     * Returns the client's peer id for the group instance on success.
     */
    fun groupSelfGetPeerId(groupNumber: ToxGroupNumber): ToxGroupPeerNumber

    /**
     * Return the client's group public key designated by the given group number as
     * a byte array.
     *
     * This key will be permanently tied to the client's identity for this
     * particular group until the client explicitly leaves the group. This key is
     * the only way for other peers to reliably identify the client across client
     * restarts.
     *
     * @param groupNumber The group number of the group we wish to query.
     */
    fun groupSelfGetPublicKey(groupNumber: ToxGroupNumber): ToxPublicKey

    /**
     * Return the name of the peer designated by the given ID as a byte array.
     *
     * The data returned is equal to the data received by the last
     * `groupPeerName` callback.
     *
     * @param groupNumber The group number of the group we wish to query.
     * @param peerId The ID of the peer whose name we wish to retrieve.
     */
    fun groupPeerGetName(
        groupNumber: ToxGroupNumber,
        peerId: ToxGroupPeerNumber,
    ): ToxGroupName

    /**
     * Return the peer's user status (away/busy/...).
     *
     * The status returned is equal to the last status received through the
     * `groupPeerStatus` callback.
     *
     * @param groupNumber The group number of the group we wish to query.
     * @param peerId The ID of the peer whose status we wish to query.
     */
    fun groupPeerGetStatus(
        groupNumber: ToxGroupNumber,
        peerId: ToxGroupPeerNumber,
    ): ToxUserStatus

    /**
     * Return the peer's role (user/moderator/founder...).
     *
     * The role returned is equal to the last role received through the
     * `groupModeration` callback.
     *
     * @param groupNumber The group number of the group we wish to query.
     * @param peerId The ID of the peer whose role we wish to query.
     */
    fun groupPeerGetRole(
        groupNumber: ToxGroupNumber,
        peerId: ToxGroupPeerNumber,
    ): ToxGroupRole

    /**
     * Return the type of connection we have established with a peer.
     *
     * If [[peerId]] designates ourself, the return value indicates whether we're
     * capable of making UDP connections with other peers, or are limited to TCP
     * connections.
     *
     * @param groupNumber The group number of the group we wish to query.
     * @param peerId The ID of the peer whose connection status we wish to query.
     */
    fun groupPeerGetConnectionStatus(
        groupNumber: ToxGroupNumber,
        peerId: ToxGroupPeerNumber,
    ): ToxConnection

    /**
     * Return the group public key with the designated peerId for the designated
     * groupNumber.
     *
     * This key will be permanently tied to a particular peer until they explicitly
     * leave the group and is the only way to reliably identify the same peer across
     * client restarts.
     *
     * @param groupNumber The group number of the group we wish to query.
     * @param peerId The ID of the peer whose public key we wish to retrieve.
     *
     * @return The public key of the peer.
     */
    fun groupPeerGetPublicKey(
        groupNumber: ToxGroupNumber,
        peerId: ToxGroupPeerNumber,
    ): ToxPublicKey

    /**
     * Set the group topic and broadcast it to the rest of the group.
     *
     * Topic length cannot be longer than [[ToxCoreConstants.MAX_TOPIC_LENGTH]]. If the length
     * is equal to zero or topic is set to null, the topic will be unset.
     *
     * @param groupNumber The group number of the group we wish to set the topic for.
     * @param topic The topic to set.
     */
    fun groupSetTopic(
        groupNumber: ToxGroupNumber,
        topic: ToxGroupTopic,
    ): Unit

    /**
     * Return the topic designated by the given group number.
     *
     * The data written to `topic` is equal to the data received by the last
     * `groupTopic` callback.
     *
     * @param groupNumber The group number of the group we wish to query.
     */
    fun groupGetTopic(groupNumber: ToxGroupNumber): ToxGroupTopic

    /**
     * Return the name of the group designated by the given group number.
     *
     * @param groupNumber The group number of the group we wish to query.
     */
    fun groupGetName(groupNumber: ToxGroupNumber): ToxGroupName

    /**
     * Return the Chat ID designated by the given group number.
     *
     * @param groupNumber The group number of the group we wish to query.
     */
    fun groupGetChatId(groupNumber: ToxGroupNumber): ToxGroupChatId

    /**
     * Return the privacy state of the group designated by the given group number.
     *
     * The value returned is equal to the data received by the last
     * `groupPrivacyState` callback.
     */
    fun groupGetPrivacyState(groupNumber: ToxGroupNumber): ToxGroupPrivacyState

    /**
     * Return the voice state of the group designated by the given group number.
     *
     * The value returned is equal to the data received by the last
     * `groupVoiceState` callback.
     */
    fun groupGetVoiceState(groupNumber: ToxGroupNumber): ToxGroupVoiceState

    /**
     * Return the topic lock status of the group designated by the given group
     * number.
     *
     * The value returned is equal to the data received by the last
     * `groupTopicLock` callback.
     */
    fun groupGetTopicLock(groupNumber: ToxGroupNumber): ToxGroupTopicLock

    /**
     * Return the maximum number of peers allowed for the group designated by the
     * given group number.
     *
     * The value returned is equal to the data received by the last
     * `groupPeerLimit` callback.
     */
    fun groupGetPeerLimit(groupNumber: ToxGroupNumber): Int

    /**
     * Return the password for the group designated by the given group number.
     *
     * The data received is equal to the data received by the last `groupPassword`
     * callback.
     *
     * @param groupNumber The group number of the group we wish to query.
     */
    fun groupGetPassword(groupNumber: ToxGroupNumber): ToxGroupPassword

    /**
     * Send a text chat message to the group.
     *
     * This function creates a group message packet and pushes it into the send
     * queue.
     *
     * The message length may not exceed [[ToxCoreConstants.MAX_MESSAGE_LENGTH]]. Larger
     * messages must be split by the client and sent as separate messages. Other
     * clients can then reassemble the fragments. Messages may not be empty.
     *
     * @param groupNumber The group number of the group the message is intended
     *   for.
     * @param messageType Message type (normal, action, ...).
     * @param message A byte array containing the message text.
     */
    fun groupSendMessage(
        groupNumber: ToxGroupNumber,
        messageType: ToxMessageType,
        message: ToxGroupMessage,
    ): Int

    /**
     * Send a text chat message to the specified peer in the specified group.
     *
     * This function creates a group private message packet and pushes it into the
     * send queue.
     *
     * The message length may not exceed [[ToxCoreConstants.MAX_MESSAGE_LENGTH]]. Larger
     * messages must be split by the client and sent as separate messages. Other
     * clients can then reassemble the fragments. Messages may not be empty.
     *
     * @param groupNumber The group number of the group the message is intended
     *   for.
     * @param peerId The ID of the peer the message is intended for.
     * @param messageType The type of message (normal, action, ...).
     * @param message A byte array containing the message text.
     */
    fun groupSendPrivateMessage(
        groupNumber: ToxGroupNumber,
        peerId: ToxGroupPeerNumber,
        messageType: ToxMessageType,
        message: ToxGroupMessage,
    ): Int

    /**
     * Send a custom packet to the group.
     *
     * If lossless is true the packet will be lossless. Lossless packet behaviour is
     * comparable to TCP (reliability, arrive in order) but with packets instead of
     * a stream.
     *
     * If lossless is false, the packet will be lossy. Lossy packets behave like UDP
     * packets, meaning they might never reach the other side or might arrive more
     * than once (if someone is messing with the connection) or might arrive in the
     * wrong order.
     *
     * Unless latency is an issue or message reliability is not important, it is
     * recommended that you use lossless packets.
     *
     * The message length may not exceed [[ToxCoreConstants.MAX_CUSTOM_PACKET_SIZE]]. Larger packets
     * must be split by the client and sent as separate packets. Other clients can
     * then reassemble the fragments. Packets may not be empty.
     *
     * @param groupNumber The group number of the group the packet is intended for.
     * @param lossless True if the packet should be lossless.
     * @param data A byte array containing the packet data.
     */
    fun groupSendCustomPacket(
        groupNumber: ToxGroupNumber,
        lossless: Boolean,
        data: ByteArray,
    ): Unit

    /**
     * Send a custom private packet to a designated peer in the group.
     *
     * If lossless is true the packet will be lossless. Lossless packet behaviour is
     * comparable to TCP (reliability, arrive in order) but with packets instead of
     * a stream.
     *
     * If lossless is false, the packet will be lossy. Lossy packets behave like UDP
     * packets, meaning they might never reach the other side or might arrive more
     * than once (if someone is messing with the connection) or might arrive in the
     * wrong order.
     *
     * Unless latency is an issue or message reliability is not important, it is
     * recommended that you use lossless packets.
     *
     * The packet length may not exceed [[ToxCoreConstants.MAX_CUSTOM_PACKET_SIZE]]. Larger packets
     * must be split by the client and sent as separate packets. Other clients can
     * then reassemble the fragments. Packets may not be empty.
     *
     * @param groupNumber The group number of the group the packet is intended for.
     * @param peerId The ID of the peer the packet is intended for.
     * @param lossless True if the packet should be lossless.
     * @param data A byte array containing the packet data.
     */
    fun groupSendCustomPrivatePacket(
        groupNumber: ToxGroupNumber,
        peerId: ToxGroupPeerNumber,
        lossless: Boolean,
        data: ByteArray,
    ): Unit

    /**
     * Invite a friend to a group.
     *
     * This function creates an invite request packet and pushes it to the send
     * queue.
     *
     * @param groupNumber The group number of the group the message is intended
     *   for.
     * @param friendNumber The friend number of the friend the invite is intended
     *   for.
     */
    fun groupInviteFriend(
        groupNumber: ToxGroupNumber,
        friendNumber: ToxFriendNumber,
    ): Unit

    /**
     * Accept an invite to a group chat that the client previously received from a
     * friend. The invite is only valid while the inviter is present in the group.
     *
     * @param friendNumber The friend number of the friend who invited us.
     * @param inviteData The invite data received from the `groupInvite` event.
     * @param name The name of the peer joining the group. Length must be no larger
     *   than [[ToxCoreConstants.MAX_NAME_LENGTH]].
     * @param password The password required to join the group. Set to empty if no
     *   password is required. Length must be no larger than
     *   [[ToxCoreConstants.GROUP_MAX_PASSWORD_SIZE]].
     */
    fun groupInviteAccept(
        friendNumber: ToxFriendNumber,
        inviteData: ByteArray,
        name: ToxGroupName,
        password: ToxGroupPassword,
    ): ToxGroupNumber

    /**
     * Set or unset the group password.
     *
     * This function allows Founders to set or unset a group password. It will
     * create a new group shared state including the change and distribute it to the
     * rest of the group.
     *
     * @param groupNumber The group number of the group for which we wish to set
     *   the password.
     * @param password The password we want to set. Set password to empty to unset
     *   the password. Length must be no longer than [[ToxCoreConstants.GROUP_MAX_PASSWORD_SIZE]].
     */
    fun groupSetPassword(
        groupNumber: ToxGroupNumber,
        password: ToxGroupPassword,
    ): Unit

    /**
     * Set the group topic lock state.
     *
     * This function allows Founders to enable or disable the group's topic lock. It
     * will create a new shared state including the change and distribute it to the
     * rest of the group.
     *
     * When the topic lock is enabled, only the group founder and moderators may set
     * the topic.  When disabled, all peers except those with the observer role may
     * set the topic.
     *
     * @param groupNumber The group number of the group for which we wish to change
     *   the topic lock state.
     * @param topicLock The state we wish to set the topic lock to.
     */
    fun groupSetTopicLock(
        groupNumber: ToxGroupNumber,
        topicLock: ToxGroupTopicLock,
    ): Unit

    /**
     * Set the group voice state.
     *
     * This function allows Founders to set the group's voice state. It will create
     * a new group shared state including the change and distribute it to the rest
     * of the group.
     *
     * If an attempt is made to set the voice state to the same state that the group
     * is already in, the function call will be successful and no action will be
     * taken.
     *
     * @param groupNumber The group number of the group for which we wish to change
     *   the voice state.
     * @param voiceState The voice state we wish to set the group to.
     */
    fun groupSetVoiceState(
        groupNumber: ToxGroupNumber,
        voiceState: ToxGroupVoiceState,
    ): Unit

    /**
     * Set the group privacy state.
     *
     * This function allows Founders to set the group's privacy state. It will
     * create a new group shared state including the change and distribute it to the
     * rest of the group.
     *
     * If an attempt is made to set the privacy state to the same state that the
     * group is already in, the function call will be successful and no action will
     * be taken.
     *
     * @param groupNumber The group number of the group for which we wish to change
     *   the privacy state.
     * @param privacyState The privacy state we wish to set the group to.
     */
    fun groupSetPrivacyState(
        groupNumber: ToxGroupNumber,
        privacyState: ToxGroupPrivacyState,
    ): Unit

    /**
     * Set the group peer limit.
     *
     * This function allows Founders to set a limit for the number of peers who may
     * be in the group. It will create a new group shared state including the change
     * and distribute it to the rest of the group.
     *
     * @param groupNumber The group number of the group for which we wish to set
     *   the peer limit.
     * @param peerLimit The maximum number of peers to allow in the group.
     */
    fun groupSetPeerLimit(
        groupNumber: ToxGroupNumber,
        peerLimit: Int,
    ): Unit

    /**
     * Ignore or unignore a peer.
     *
     * @param groupNumber The group number of the group in which you wish to ignore
     *   a peer.
     * @param peerId The ID of the peer who shall be ignored or unignored.
     * @param ignore True to ignore the peer, false to unignore the peer.
     */
    fun groupSetIgnore(
        groupNumber: ToxGroupNumber,
        peerId: ToxGroupPeerNumber,
        ignore: Boolean,
    ): Unit

    /**
     * Set a peer's role.
     *
     * This function will first remove the peer's previous role and then assign them
     * a new role. It will also send a packet to the rest of the group, requesting
     * that they perform the role reassignment.
     *
     * Only Founders may promote peers to the Moderator role, and only Founders and
     * Moderators may set peers to the Observer or User role. Moderators may not set
     * the role of other Moderators or the Founder. Peers may not be promoted to the
     * Founder role.
     *
     * @param groupNumber The group number of the group the in which you wish set
     *   the peer's role.
     * @param peerId The ID of the peer whose role you wish to set.
     * @param role The role you wish to set the peer to.
     */
    fun groupSetRole(
        groupNumber: ToxGroupNumber,
        peerId: ToxGroupPeerNumber,
        role: ToxGroupRole,
    ): Unit

    /**
     * Kick a peer.
     *
     * This function allows peers with the Founder or Moderator role to silently
     * instruct all other peers in the group to remove a particular peer from their
     * peer list.
     *
     * Note: This function will not trigger the `groupPeerExit` event for the
     * caller.
     *
     * @param groupNumber The group number of the group the action is intended for.
     * @param peerId The ID of the peer who will be kicked.
     */
    fun groupKickPeer(
        groupNumber: ToxGroupNumber,
        peerId: ToxGroupPeerNumber,
    ): Unit
}
