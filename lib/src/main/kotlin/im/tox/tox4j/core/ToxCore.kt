package im.tox.tox4j.core

import im.tox.tox4j.core.data.ToxAddress
import im.tox.tox4j.core.data.ToxConferenceId
import im.tox.tox4j.core.data.ToxConferenceNumber
import im.tox.tox4j.core.data.ToxConferenceOfflinePeerNumber
import im.tox.tox4j.core.data.ToxConferencePeerNumber
import im.tox.tox4j.core.data.ToxDhtId
import im.tox.tox4j.core.data.ToxFileId
import im.tox.tox4j.core.data.ToxFileNumber
import im.tox.tox4j.core.data.ToxFriendMessageId
import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.core.data.ToxGroupChatId
import im.tox.tox4j.core.data.ToxGroupMessageId
import im.tox.tox4j.core.data.ToxGroupNumber
import im.tox.tox4j.core.data.ToxGroupPeerNumber
import im.tox.tox4j.core.data.ToxPort
import im.tox.tox4j.core.data.ToxPublicKey
import im.tox.tox4j.core.data.ToxSecretKey
import im.tox.tox4j.core.enums.ToxConferenceType
import im.tox.tox4j.core.enums.ToxConnection
import im.tox.tox4j.core.enums.ToxFileControl
import im.tox.tox4j.core.enums.ToxGroupPrivacyState
import im.tox.tox4j.core.enums.ToxGroupRole
import im.tox.tox4j.core.enums.ToxGroupTopicLock
import im.tox.tox4j.core.enums.ToxGroupVoiceState
import im.tox.tox4j.core.enums.ToxMessageType
import im.tox.tox4j.core.enums.ToxUserStatus

interface ToxCore : AutoCloseable {
    val iterationInterval: Int

    fun <ToxCoreState> iterate(
        handler: im.tox.tox4j.core.callbacks.ToxCoreEventListener<ToxCoreState>,
        state: ToxCoreState,
    ): ToxCoreState

    /**
     * Creates a new conference.
     *
     * This function creates and connects to a new text conference.
     *
     * @return - conference number on success - an unspecified value on failure
     */
    fun conferenceNew(): ToxConferenceNumber

    /**
     * This function deletes a conference.
     *
     * @param conferenceNumber The conference number of the conference to be deleted.
     *
     * @return true on success.
     */
    fun conferenceDelete(conferenceNumber: ToxConferenceNumber)

    /**
     * Return the number of online peers in the conference.
     *
     * The unsigned integers less than this number are the valid values of peer_number for the functions querying these peers. Return value is unspecified on failure.
     */
    fun conferencePeerCount(conferenceNumber: ToxConferenceNumber): Int

    /**
     * Return the number of offline peers in the conference.
     *
     * The unsigned integers less than this number are the valid values of offline_peer_number for the functions querying these peers.
     *
     * Return value is unspecified on failure.
     */
    fun conferenceOfflinePeerCount(conferenceNumber: ToxConferenceNumber): Int

    /**
     * Set maximum number of offline peers to store, overriding the default.
     */
    fun conferenceSetMaxOffline(
        conferenceNumber: ToxConferenceNumber,
        maxOffline: Int,
    )

    /**
     * Joins a conference that the client has been invited to.
     *
     * After successfully joining the conference, the client will not be "connected" to it until a handshaking procedure has been completed. A `conference_connected` event will then occur for the conference. The client will then remain connected to the conference until the conference is deleted, even across Tox restarts. Many operations on a conference will fail with a corresponding error if attempted on a conference to which the client is not yet connected.
     *
     * @param friendNumber The friend number of the friend who sent the invite.
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
     * This function creates a conference message packet and pushes it into the send queue.
     *
     * The message length may not exceed [ToxCoreConstants.MAX_MESSAGE_LENGTH]. Larger messages must be split by the client and sent as separate messages. Other clients can then reassemble the fragments.
     *
     * @param conferenceNumber The conference number of the conference the message is intended for.
     * @param type Message type (normal, action, ...).
     * @param message A non-NULL pointer to the first element of a byte array containing the message text.
     *
     * @return true on success.
     */
    fun conferenceSendMessage(
        conferenceNumber: ToxConferenceNumber,
        type: ToxMessageType,
        message: ByteArray,
    )

    /**
     * Write the title designated by the given conference number to a byte array.
     *
     * Call [conferenceGetTitle] to determine the allocation size for the `title` parameter.
     *
     * The data written to `title` is equal to the data received by the last `conference_title` callback.
     *
     * @param title A valid memory region large enough to store the title. If this parameter is null, this function has no effect.
     *
     * @return true on success.
     */
    fun conferenceGetTitle(conferenceNumber: ToxConferenceNumber): ByteArray

    /**
     * Set the conference title and broadcast it to the rest of the conference.
     *
     * Title length cannot be longer than [ToxCoreConstants.MAX_NAME_LENGTH].
     *
     * @return true on success.
     */
    fun conferenceSetTitle(
        conferenceNumber: ToxConferenceNumber,
        title: ByteArray,
    )

    /**
     * Get the type (text or A/V) for the conference.
     */
    fun conferenceGetType(conferenceNumber: ToxConferenceNumber): ToxConferenceType

    /**
     * Get the conference unique ID.
     *
     * If id is null, this function has no effect.
     *
     * @param id A memory region large enough to store [ToxCoreConstants.CONFERENCE_ID_SIZE] bytes.
     *
     * @return true on success.
     */
    fun conferenceGetId(conferenceNumber: ToxConferenceNumber): ToxConferenceId

    /**
     * Return the conference number associated with the specified id.
     *
     * @param id A byte array containing the conference id ([ToxCoreConstants.CONFERENCE_ID_SIZE]).
     *
     * @return the conference number on success, an unspecified value on failure.
     */
    fun conferenceById(id: ToxConferenceId): ToxConferenceNumber

    /**
     * Copy the name of offline_peer_number who is in conference_number to name.
     *
     * Call [conferenceOfflinePeerGetName] to determine the allocation size for the `name` parameter.
     *
     * @param name A valid memory region large enough to store the peer's name.
     *
     * @return true on success.
     */
    fun conferenceOfflinePeerGetName(
        conferenceNumber: ToxConferenceNumber,
        offlinePeerNumber: ToxConferenceOfflinePeerNumber,
    ): ByteArray

    /**
     * Copy the public key of offline_peer_number who is in conference_number to public_key.
     *
     * public_key must be [ToxCoreConstants.PUBLIC_KEY_SIZE] long.
     *
     * @return true on success.
     */
    fun conferenceOfflinePeerGetPublicKey(
        conferenceNumber: ToxConferenceNumber,
        offlinePeerNumber: ToxConferenceOfflinePeerNumber,
    ): ToxPublicKey

    /**
     * Return a unix-time timestamp of the last time offline_peer_number was seen to be active.
     */
    fun conferenceOfflinePeerGetLastActive(
        conferenceNumber: ToxConferenceNumber,
        offlinePeerNumber: ToxConferenceOfflinePeerNumber,
    ): Long

    /**
     * Copy the name of peer_number who is in conference_number to name.
     *
     * Call [conferencePeerGetName] to determine the allocation size for the `name` parameter.
     *
     * @param name A valid memory region large enough to store the peer's name.
     *
     * @return true on success.
     */
    fun conferencePeerGetName(
        conferenceNumber: ToxConferenceNumber,
        peerNumber: ToxConferencePeerNumber,
    ): ByteArray

    /**
     * Copy the public key of peer_number who is in conference_number to public_key.
     *
     * public_key must be [ToxCoreConstants.PUBLIC_KEY_SIZE] long.
     *
     * @return true on success.
     */
    fun conferencePeerGetPublicKey(
        conferenceNumber: ToxConferenceNumber,
        peerNumber: ToxConferencePeerNumber,
    ): ToxPublicKey

    /**
     * Return true if passed peer_number corresponds to our own.
     */
    fun conferencePeerNumberIsOurs(
        conferenceNumber: ToxConferenceNumber,
        peerNumber: ToxConferencePeerNumber,
    ): Boolean

    /**
     * Send a file transmission request.
     *
     * Maximum filename length is [ToxCoreConstants.MAX_FILENAME_LENGTH] bytes. The filename should generally just be a file name, not a path with directory names.
     *
     * If a non-[ULong.MAX_VALUE] file size is provided, it can be used by both sides to determine the sending progress. File size can be set to [ULong.MAX_VALUE] for streaming data of unknown size.
     *
     * File transmission occurs in chunks, which are requested through the `file_chunk_request` event.
     *
     * When a friend goes offline, all file transfers associated with the friend get purged.
     *
     * If the file contents change during a transfer, the behaviour is unspecified in general. What will actually happen depends on the mode in which the file was modified and how the client determines the file size.
     *
     * - If the file size was increased - and sending mode was streaming (file_size = [ULong.MAX_VALUE]), the behaviour will be as expected. - and sending mode was file (file_size != [ULong.MAX_VALUE]), the file_chunk_request callback will receive length = 0 when Tox thinks the file transfer has finished. If the client remembers the file size as it was when sending the request, it will terminate the transfer normally. If the client re-reads the size, it will think the friend cancelled the transfer. - If the file size was decreased - and sending mode was streaming, the behaviour is as expected. - and sending mode was file, the callback will return 0 at the new (earlier) end-of-file, signaling to the friend that the transfer was cancelled. - If the file contents were modified - at a position before the current read, the two files (local and remote) will differ after the transfer terminates. - at a position after the current read, the file transfer will succeed as expected. - In either case, both sides will regard the transfer as complete and successful.
     *
     * @param friendNumber The friend number of the friend the file send request should be sent to.
     * @param kind The meaning of the file to be sent.
     * @param fileSize Size in bytes of the file the client wants to send, [ULong.MAX_VALUE] if unknown or streaming.
     * @param fileId A file identifier of length [ToxCoreConstants.FILE_ID_LENGTH] that can be used to uniquely identify file transfers across Tox restarts. If null, a random one will be generated by the library. It can then be obtained by using `[fileGetFileId]()`.
     * @param filename Name of the file. Does not need to be the actual name. This name will be sent along with the file send request.
     *
     * @return A file number used as an identifier in subsequent callbacks. This number is per friend. File numbers are reused after a transfer terminates. On failure, this function returns an unspecified value. Any pattern in file numbers should not be relied on.
     */
    fun fileSend(
        friendNumber: ToxFriendNumber,
        kind: Int,
        fileSize: Long,
        fileId: ToxFileId,
        filename: ByteArray,
    ): ToxFileNumber

    /**
     * Add a friend to the friend list and send a friend request.
     *
     * A friend request message must be at least 1 byte long and at most [ToxCoreConstants.MAX_FRIEND_REQUEST_LENGTH].
     *
     * Friend numbers are unique identifiers used in all functions that operate on friends. Once added, a friend number is stable for the lifetime of the Tox object. After saving the state and reloading it, the friend numbers may not be the same as before. Deleting a friend creates a gap in the friend number set, which is filled by the next adding of a friend. Any pattern in friend numbers should not be relied on.
     *
     * If more than [Int.MAX_VALUE] friends are added, this function causes undefined behaviour.
     *
     * @param address The address of the friend (returned by [address] of the friend you wish to add) it must be [ToxCoreConstants.ADDRESS_SIZE] bytes.
     * @param message The message that will be sent along with the friend request.
     *
     * @return the friend number on success, an unspecified value on failure.
     */
    fun friendAdd(
        address: ToxAddress,
        message: ByteArray,
    ): ToxFriendNumber

    /**
     * Add a friend without sending a friend request.
     *
     * This function is used to add a friend in response to a friend request. If the client receives a friend request, it can be reasonably sure that the other client added this client as a friend, eliminating the need for a friend request.
     *
     * This function is also useful in a situation where both instances are controlled by the same entity, so that this entity can perform the mutual friend adding. In this case, there is no need for a friend request, either.
     *
     * @param publicKey A byte array of length [ToxCoreConstants.PUBLIC_KEY_SIZE] containing the Public Key (not the Address) of the friend to add.
     *
     * @return the friend number on success, an unspecified value on failure.
     * @see [friendAdd] for a more detailed description of friend numbers.
     */
    fun friendAddNorequest(publicKey: ToxPublicKey): ToxFriendNumber

    /**
     * Remove a friend from the friend list.
     *
     * This does not notify the friend of their deletion. After calling this function, this client will appear offline to the friend and no communication can occur between the two.
     *
     * @param friendNumber Friend number for the friend to be deleted.
     *
     * @return true on success.
     */
    fun friendDelete(friendNumber: ToxFriendNumber)

    /**
     * Return the friend number associated with that Public Key.
     *
     * @param publicKey A byte array containing the Public Key.
     *
     * @return the friend number on success, an unspecified value on failure.
     */
    fun friendByPublicKey(publicKey: ToxPublicKey): ToxFriendNumber

    /**
     * Checks if a friend with the given friend number exists and returns true if it does.
     */
    fun friendExists(friendNumber: ToxFriendNumber): Boolean

    /**
     * Copies the Public Key associated with a given friend number to a byte array.
     *
     * @param friendNumber The friend number you want the Public Key of.
     * @param publicKey A memory region of at least [ToxCoreConstants.PUBLIC_KEY_SIZE] bytes. If this parameter is null, this function has no effect.
     *
     * @return true on success.
     */
    fun friendGetPublicKey(friendNumber: ToxFriendNumber): ToxPublicKey

    /**
     * Return a unix-time timestamp of the last time the friend associated with a given friend number was seen online.
     *
     * This function will return [ULong.MAX_VALUE] on error.
     *
     * @param friendNumber The friend number you want to query.
     */
    fun friendGetLastOnline(friendNumber: ToxFriendNumber): Long

    /**
     * Check whether a friend is currently typing a message.
     *
     * @param friendNumber The friend number for which to query the typing status.
     *
     * @return true if the friend is typing.
     * @return false if the friend is not typing, or the friend number was invalid. Inspect the error code to determine which case it is.
     *
     * @deprecated This getter is deprecated. Use the event and store the status in the client state.
     */
    fun friendGetTyping(friendNumber: ToxFriendNumber): Boolean

    /**
     * Send a text chat message to an online friend.
     *
     * This function creates a chat message packet and pushes it into the send queue.
     *
     * The message length may not exceed [ToxCoreConstants.MAX_MESSAGE_LENGTH]. Larger messages must be split by the client and sent as separate messages. Other clients can then reassemble the fragments. Messages may not be empty.
     *
     * The return value of this function is the message ID. If a read receipt is received, the triggered `friend_read_receipt` event will be passed this message ID.
     *
     * Message IDs are unique per friend. The first message ID is 0. Message IDs are incremented by 1 each time a message is sent. If [UInt.MAX_VALUE] messages were sent, the next message ID is 0.
     *
     * @param type Message type (normal, action, ...).
     * @param friendNumber The friend number of the friend to send the message to.
     * @param message A non-NULL pointer to the first element of a byte array containing the message text.
     */
    fun friendSendMessage(
        friendNumber: ToxFriendNumber,
        type: ToxMessageType,
        message: ByteArray,
    ): ToxFriendMessageId

    /**
     * Send a custom lossy packet to a friend.
     *
     * The first byte of data must be in the range 192-254. Maximum length of a custom packet is [ToxCoreConstants.MAX_CUSTOM_PACKET_SIZE].
     *
     * Lossy packets behave like UDP packets, meaning they might never reach the other side or might arrive more than once (if someone is messing with the connection) or might arrive in the wrong order.
     *
     * Unless latency is an issue, it is recommended that you use lossless custom packets instead.
     *
     * @param friendNumber The friend number of the friend this lossy packet should be sent to.
     * @param data A byte array containing the packet data.
     *
     * @return true on success.
     */
    fun friendSendLossyPacket(
        friendNumber: ToxFriendNumber,
        data: ByteArray,
    )

    /**
     * Send a custom lossless packet to a friend.
     *
     * The first byte of data must be either 69 or in the range 160-191. Maximum length of a custom packet is [ToxCoreConstants.MAX_CUSTOM_PACKET_SIZE].
     *
     * Lossless packet behaviour is comparable to TCP (reliability, arrive in order) but with packets instead of a stream.
     *
     * @param friendNumber The friend number of the friend this lossless packet should be sent to.
     * @param data A byte array containing the packet data.
     *
     * @return true on success.
     */
    fun friendSendLosslessPacket(
        friendNumber: ToxFriendNumber,
        data: ByteArray,
    )

    /**
     * Creates a new group chat.
     *
     * This function creates a new group chat object and adds it to the chats array.
     *
     * The caller of this function has Founder role privileges.
     *
     * The client should initiate its peer list with self info after calling this function, as the peer_join callback will not be triggered.
     *
     * @param privacyState The privacy state of the group. If this is set to [ToxCoreConstants.GROUP_PRIVACY_STATE_PUBLIC], the group will attempt to announce itself to the DHT and anyone with the Chat ID may join. Otherwise a friend invite will be required to join the group.
     * @param groupName The name of the group. The name must be non-NULL.
     * @param name The name of the peer creating the group.
     *
     * @return group_number on success, [UInt.MAX_VALUE] on failure.
     */
    fun groupNew(
        privacyState: ToxGroupPrivacyState,
        groupName: ByteArray,
        name: ByteArray,
    ): ToxGroupNumber

    /**
     * Joins a group chat with specified Chat ID or reconnects to an existing group.
     *
     * This function creates a new group chat object, adds it to the chats array, and sends a DHT announcement to find peers in the group associated with chat_id. Once a peer has been found a join attempt will be initiated.
     *
     * If a group with the specified Chat ID already exists, this function will attempt to reconnect to the group.
     *
     * @param chatId The Chat ID of the group you wish to join. This must be [ToxCoreConstants.GROUP_CHAT_ID_SIZE] bytes.
     * @param password The password required to join the group. Set to null if no password is required.
     * @param name The name of the peer joining the group.
     *
     * @return group_number on success, [UInt.MAX_VALUE] on failure.
     */
    fun groupJoin(
        chatId: ToxGroupChatId,
        name: ByteArray,
        password: ByteArray,
    ): ToxGroupNumber

    /**
     * Returns true if the group chat is currently connected or attempting to connect to other peers in the group.
     *
     * @param groupNumber The group number of the designated group.
     */
    fun groupIsConnected(groupNumber: ToxGroupNumber): Boolean

    /**
     * Disconnects from a group chat while retaining the group state and credentials.
     *
     * Returns true if we successfully disconnect from the group.
     *
     * @param groupNumber The group number of the designated group.
     */
    fun groupDisconnect(groupNumber: ToxGroupNumber)

    /**
     * Leaves a group.
     *
     * This function sends a parting packet containing a custom (non-obligatory) message to all peers in a group, and deletes the group from the chat array. All group state information is permanently lost, including keys and role credentials.
     *
     * @param groupNumber The group number of the group we wish to leave.
     * @param partMessage The parting message to be sent to all the peers. Set to null if we do not wish to send a parting message.
     *
     * @return true if the group chat instance is successfully deleted.
     */
    fun groupLeave(
        groupNumber: ToxGroupNumber,
        partMessage: ByteArray,
    )

    /**
     * Set the client's nickname for the group instance designated by the given group number.
     *
     * Nickname length cannot exceed [ToxCoreConstants.MAX_NAME_LENGTH]. If length is equal to zero or name is a null pointer, the function call will fail.
     *
     * @param name A byte array containing the new nickname.
     *
     * @return true on success.
     */
    fun groupSelfSetName(
        groupNumber: ToxGroupNumber,
        name: ByteArray,
    )

    /**
     * Write the nickname set by [groupSelfSetName] to a byte array.
     *
     * If no nickname was set before calling this function, the name is empty, and this function has no effect.
     *
     * Call [groupSelfGetName] to find out how much memory to allocate for the result.
     *
     * @param name A valid memory location large enough to hold the nickname. If this parameter is null, the function has no effect.
     *
     * @return true on success.
     */
    fun groupSelfGetName(groupNumber: ToxGroupNumber): ByteArray

    /**
     * Set the client's status for the group instance. Status must be a Tox_User_Status.
     *
     * @return true on success.
     */
    fun groupSelfSetStatus(
        groupNumber: ToxGroupNumber,
        status: ToxUserStatus,
    )

    /**
     * returns the client's status for the group instance on success. return value is unspecified on failure.
     */
    fun groupSelfGetStatus(groupNumber: ToxGroupNumber): ToxUserStatus

    /**
     * returns the client's role for the group instance on success. return value is unspecified on failure.
     */
    fun groupSelfGetRole(groupNumber: ToxGroupNumber): ToxGroupRole

    /**
     * returns the client's peer id for the group instance on success. return value is unspecified on failure.
     */
    fun groupSelfGetPeerId(groupNumber: ToxGroupNumber): ToxGroupPeerNumber

    /**
     * Write the client's group public key designated by the given group number to a byte array.
     *
     * This key will be permanently tied to the client's identity for this particular group until the client explicitly leaves the group. This key is the only way for other peers to reliably identify the client across client restarts.
     *
     * `public_key` should have room for at least [ToxCoreConstants.GROUP_PEER_PUBLIC_KEY_SIZE] bytes.
     *
     * @param publicKey A valid memory region large enough to store the public key. If this parameter is null, this function call has no effect.
     *
     * @return true on success.
     */
    fun groupSelfGetPublicKey(groupNumber: ToxGroupNumber): ToxPublicKey

    /**
     * Set the group topic and broadcast it to the rest of the group.
     *
     * Topic length cannot be longer than [ToxCoreConstants.GROUP_MAX_TOPIC_LENGTH]. If the length is equal to zero or topic is set to null, the topic will be unset.
     *
     * @return true on success.
     */
    fun groupSetTopic(
        groupNumber: ToxGroupNumber,
        topic: ByteArray,
    )

    /**
     * Write the topic designated by the given group number to a byte array.
     *
     * Call [groupGetTopic] to determine the allocation size for the `topic` parameter.
     *
     * The data written to `topic` is equal to the data received by the last `group_topic` callback.
     *
     * @param topic A valid memory region large enough to store the topic. If this parameter is null, this function has no effect.
     *
     * @return true on success.
     */
    fun groupGetTopic(groupNumber: ToxGroupNumber): ByteArray

    /**
     * Write the name of the group designated by the given group number to a byte array.
     *
     * Call [groupGetName] to determine the allocation size for the `name` parameter.
     *
     * @param name A valid memory region large enough to store the group name. If this parameter is null, this function call has no effect.
     *
     * @return true on success.
     */
    fun groupGetName(groupNumber: ToxGroupNumber): ByteArray

    /**
     * Write the Chat ID designated by the given group number to a byte array.
     *
     * `chat_id` should have room for at least [ToxCoreConstants.GROUP_CHAT_ID_SIZE] bytes.
     *
     * @param chatId A valid memory region large enough to store the Chat ID. If this parameter is null, this function call has no effect.
     *
     * @return true on success.
     */
    fun groupGetChatId(groupNumber: ToxGroupNumber): ToxGroupChatId

    /**
     * Return the privacy state of the group designated by the given group number. If group number is invalid, the return value is unspecified.
     *
     * The value returned is equal to the data received by the last `group_privacy_state` callback.
     *
     * @see the `Group chat Founder controls` section for the respective set function.
     */
    fun groupGetPrivacyState(groupNumber: ToxGroupNumber): ToxGroupPrivacyState

    /**
     * Return the voice state of the group designated by the given group number. If group number is invalid, the return value is unspecified.
     *
     * The value returned is equal to the data received by the last `group_voice_state` callback.
     *
     * @see the `Group chat Founder controls` section for the respective set function.
     */
    fun groupGetVoiceState(groupNumber: ToxGroupNumber): ToxGroupVoiceState

    /**
     * Return the topic lock status of the group designated by the given group number. If group number is invalid, the return value is unspecified.
     *
     * The value returned is equal to the data received by the last `group_topic_lock` callback.
     *
     * @see the `Group chat Founder controls` section for the respective set function.
     */
    fun groupGetTopicLock(groupNumber: ToxGroupNumber): ToxGroupTopicLock

    /**
     * Return the maximum number of peers allowed for the group designated by the given group number. If the group number is invalid, the return value is unspecified.
     *
     * The value returned is equal to the data received by the last `group_peer_limit` callback.
     *
     * @see the `Group chat Founder controls` section for the respective set function.
     */
    fun groupGetPeerLimit(groupNumber: ToxGroupNumber): Int

    /**
     * Write the password for the group designated by the given group number to a byte array.
     *
     * Call [groupGetPassword] to determine the allocation size for the `password` parameter.
     *
     * The data received is equal to the data received by the last `group_password` callback.
     *
     * @see the `Group chat Founder controls` section for the respective set function.
     *
     * @param password A valid memory region large enough to store the group password. If this parameter is null, this function call has no effect.
     *
     * @return true on success.
     */
    fun groupGetPassword(groupNumber: ToxGroupNumber): ByteArray

    /**
     * Send a text chat message to the group.
     *
     * This function creates a group message packet and pushes it into the send queue.
     *
     * The message length may not exceed [ToxCoreConstants.GROUP_MAX_MESSAGE_LENGTH]. Larger messages must be split by the client and sent as separate messages. Other clients can then reassemble the fragments. Messages may not be empty.
     *
     * @param groupNumber The group number of the group the message is intended for.
     * @param messageType Message type (normal, action, ...).
     * @param message A non-NULL pointer to the first element of a byte array containing the message text.
     *
     * @return The message_id of this message. If this function has an error, the returned message ID value will be undefined.
     */
    fun groupSendMessage(
        groupNumber: ToxGroupNumber,
        messageType: ToxMessageType,
        message: ByteArray,
    ): ToxGroupMessageId

    /**
     * Send a text chat message to the specified peer in the specified group.
     *
     * This function creates a group private message packet and pushes it into the send queue.
     *
     * The message length may not exceed [ToxCoreConstants.GROUP_MAX_MESSAGE_LENGTH]. Larger messages must be split by the client and sent as separate messages. Other clients can then reassemble the fragments. Messages may not be empty.
     *
     * @param groupNumber The group number of the group the message is intended for.
     * @param peerId The ID of the peer the message is intended for.
     * @param messageType The type of message (normal, action, ...).
     * @param message A non-NULL pointer to the first element of a byte array containing the message text.
     *
     * @return The message_id of this message. If this function has an error, the returned message ID value will be undefined.
     */
    fun groupSendPrivateMessage(
        groupNumber: ToxGroupNumber,
        peerId: ToxGroupPeerNumber,
        messageType: ToxMessageType,
        message: ByteArray,
    ): ToxGroupMessageId

    /**
     * Send a custom packet to the group.
     *
     * If lossless is true the packet will be lossless. Lossless packet behaviour is comparable to TCP (reliability, arrive in order) but with packets instead of a stream.
     *
     * If lossless is false, the packet will be lossy. Lossy packets behave like UDP packets, meaning they might never reach the other side or might arrive more than once (if someone is messing with the connection) or might arrive in the wrong order.
     *
     * Unless latency is an issue or message reliability is not important, it is recommended that you use lossless packets.
     *
     * The message length may not exceed [ToxCoreConstants.MAX_CUSTOM_PACKET_SIZE]. Larger packets must be split by the client and sent as separate packets. Other clients can then reassemble the fragments. Packets may not be empty.
     *
     * Start each packet with a 16-bit packet ID from docs/Group-Custom-Packets.md, so that clients can tell their packets apart.
     *
     * @param groupNumber The group number of the group the packet is intended for.
     * @param lossless True if the packet should be lossless.
     * @param data A byte array containing the packet data.
     *
     * @return true on success.
     */
    fun groupSendCustomPacket(
        groupNumber: ToxGroupNumber,
        lossless: Boolean,
        data: ByteArray,
    )

    /**
     * Send a custom private packet to a designated peer in the group.
     *
     * If lossless is true the packet will be lossless. Lossless packet behaviour is comparable to TCP (reliability, arrive in order) but with packets instead of a stream.
     *
     * If lossless is false, the packet will be lossy. Lossy packets behave like UDP packets, meaning they might never reach the other side or might arrive more than once (if someone is messing with the connection) or might arrive in the wrong order.
     *
     * Unless latency is an issue or message reliability is not important, it is recommended that you use lossless packets.
     *
     * The packet length may not exceed [ToxCoreConstants.MAX_CUSTOM_PACKET_SIZE]. Larger packets must be split by the client and sent as separate packets. Other clients can then reassemble the fragments. Packets may not be empty.
     *
     * Start each packet with a 16-bit packet ID from docs/Group-Custom-Packets.md, so that clients can tell their packets apart.
     *
     * @param groupNumber The group number of the group the packet is intended for.
     * @param peerId The ID of the peer the packet is intended for.
     * @param lossless True if the packet should be lossless.
     * @param data A byte array containing the packet data.
     *
     * @return true on success.
     */
    fun groupSendCustomPrivatePacket(
        groupNumber: ToxGroupNumber,
        peerId: ToxGroupPeerNumber,
        lossless: Boolean,
        data: ByteArray,
    )

    /**
     * Invite a friend to a group.
     *
     * This function creates an invite request packet and pushes it to the send queue.
     *
     * @param groupNumber The group number of the group the message is intended for.
     * @param friendNumber The friend number of the friend the invite is intended for.
     *
     * @return true on success.
     */
    fun groupInviteFriend(
        groupNumber: ToxGroupNumber,
        friendNumber: ToxFriendNumber,
    )

    /**
     * Accept an invite to a group chat that the client previously received from a friend. The invite is only valid while the inviter is present in the group.
     *
     * @param inviteData The invite data received from the `group_invite` event.
     * @param name The name of the peer joining the group.
     * @param password The password required to join the group. Set to null if no password is required.
     *
     * @return the group_number on success, [UInt.MAX_VALUE] on failure.
     */
    fun groupInviteAccept(
        friendNumber: ToxFriendNumber,
        inviteData: ByteArray,
        name: ByteArray,
        password: ByteArray,
    ): ToxGroupNumber

    /**
     * Set or unset the group password.
     *
     * This function allows Founders to set or unset a group password. It will create a new group shared state including the change and distribute it to the rest of the group.
     *
     * @param groupNumber The group number of the group for which we wish to set the password.
     * @param password The password we want to set. Set password to null to unset the password.
     *
     * @return true on success.
     */
    fun groupSetPassword(
        groupNumber: ToxGroupNumber,
        password: ByteArray,
    )

    /**
     * Set the group topic lock state.
     *
     * This function allows Founders to enable or disable the group's topic lock. It will create a new shared state including the change and distribute it to the rest of the group.
     *
     * When the topic lock is enabled, only the group founder and moderators may set the topic.  When disabled, all peers except those with the observer role may set the topic.
     *
     * @param groupNumber The group number of the group for which we wish to change the topic lock state.
     * @param topicLock The state we wish to set the topic lock to.
     *
     * @return true on success.
     */
    fun groupSetTopicLock(
        groupNumber: ToxGroupNumber,
        topicLock: ToxGroupTopicLock,
    )

    /**
     * Set the group voice state.
     *
     * This function allows Founders to set the group's voice state. It will create a new group shared state including the change and distribute it to the rest of the group.
     *
     * If an attempt is made to set the voice state to the same state that the group is already in, the function call will be successful and no action will be taken.
     *
     * @param groupNumber The group number of the group for which we wish to change the voice state.
     * @param voiceState The voice state we wish to set the group to.
     *
     * @return true on success.
     */
    fun groupSetVoiceState(
        groupNumber: ToxGroupNumber,
        voiceState: ToxGroupVoiceState,
    )

    /**
     * Set the group privacy state.
     *
     * This function allows Founders to set the group's privacy state. It will create a new group shared state including the change and distribute it to the rest of the group.
     *
     * If an attempt is made to set the privacy state to the same state that the group is already in, the function call will be successful and no action will be taken.
     *
     * @param groupNumber The group number of the group for which we wish to change the privacy state.
     * @param privacyState The privacy state we wish to set the group to.
     *
     * @return true on success.
     */
    fun groupSetPrivacyState(
        groupNumber: ToxGroupNumber,
        privacyState: ToxGroupPrivacyState,
    )

    /**
     * Set the group peer limit.
     *
     * This function allows Founders to set a limit for the number of peers who may be in the group. It will create a new group shared state including the change and distribute it to the rest of the group.
     *
     * @param groupNumber The group number of the group for which we wish to set the peer limit.
     * @param peerLimit The maximum number of peers to allow in the group.
     *
     * @return true on success.
     */
    fun groupSetPeerLimit(
        groupNumber: ToxGroupNumber,
        peerLimit: Int,
    )

    /**
     * Ignore or unignore a peer.
     *
     * @param groupNumber The group number of the group in which you wish to ignore a peer.
     * @param peerId The ID of the peer who shall be ignored or unignored.
     * @param ignore True to ignore the peer, false to unignore the peer.
     *
     * @return true on success.
     */
    fun groupSetIgnore(
        groupNumber: ToxGroupNumber,
        peerId: ToxGroupPeerNumber,
        ignore: Boolean,
    )

    /**
     * Set a peer's role.
     *
     * This function will first remove the peer's previous role and then assign them a new role. It will also send a packet to the rest of the group, requesting that they perform the role reassignment.
     *
     * Only Founders may promote peers to the Moderator role, and only Founders and Moderators may set peers to the Observer or User role. Moderators may not set the role of other Moderators or the Founder. Peers may not be promoted to the Founder role.
     *
     * @param groupNumber The group number of the group the in which you wish set the peer's role.
     * @param peerId The ID of the peer whose role you wish to set.
     * @param role The role you wish to set the peer to.
     *
     * @return true on success.
     */
    fun groupSetRole(
        groupNumber: ToxGroupNumber,
        peerId: ToxGroupPeerNumber,
        role: ToxGroupRole,
    )

    /**
     * Kick a peer.
     *
     * This function allows peers with the Founder or Moderator role to silently instruct all other peers in the group to remove a particular peer from their peer list.
     *
     * Note: This function will not trigger the `group_peer_exit` event for the caller.
     *
     * @param groupNumber The group number of the group the action is intended for.
     * @param peerId The ID of the peer who will be kicked.
     *
     * @return true on success.
     */
    fun groupKickPeer(
        groupNumber: ToxGroupNumber,
        peerId: ToxGroupPeerNumber,
    )

    /**
     * Write the name of the peer designated by the given ID to a byte array.
     *
     * Call [groupPeerGetName] to determine the allocation size for the `name` parameter.
     *
     * The data written to `name` is equal to the data received by the last `group_peer_name` callback.
     *
     * @param groupNumber The group number of the group we wish to query.
     * @param peerId The ID of the peer whose name we wish to retrieve.
     * @param name A valid memory region large enough to store the friend's name.
     *
     * @return true on success.
     */
    fun groupPeerGetName(
        groupNumber: ToxGroupNumber,
        peerNumber: ToxGroupPeerNumber,
    ): ByteArray

    /**
     * Return the peer's user status (away/busy/...). If the ID or group number is invalid, the return value is unspecified.
     *
     * @param groupNumber The group number of the group we wish to query.
     * @param peerId The ID of the peer whose status we wish to query.
     *
     * The status returned is equal to the last status received through the `group_peer_status` callback.
     */
    fun groupPeerGetStatus(
        groupNumber: ToxGroupNumber,
        peerNumber: ToxGroupPeerNumber,
    ): ToxUserStatus

    /**
     * Return the peer's role (user/moderator/founder...). If the ID or group number is invalid, the return value is unspecified.
     *
     * @param groupNumber The group number of the group we wish to query.
     * @param peerId The ID of the peer whose role we wish to query.
     *
     * The role returned is equal to the last role received through the `group_moderation` callback.
     */
    fun groupPeerGetRole(
        groupNumber: ToxGroupNumber,
        peerNumber: ToxGroupPeerNumber,
    ): ToxGroupRole

    /**
     * Return the type of connection we have established with a peer.
     *
     * If `peer_id` designates ourself, the return value indicates whether we're capable of making UDP connections with other peers, or are limited to TCP connections.
     *
     * @param groupNumber The group number of the group we wish to query.
     * @param peerId The ID of the peer whose connection status we wish to query.
     */
    fun groupPeerGetConnectionStatus(
        groupNumber: ToxGroupNumber,
        peerNumber: ToxGroupPeerNumber,
    ): ToxConnection

    /**
     * Write the group public key with the designated peer_id for the designated group number to public_key.
     *
     * This key will be permanently tied to a particular peer until they explicitly leave the group and is the only way to reliably identify the same peer across client restarts.
     *
     * `public_key` should have room for at least [ToxCoreConstants.GROUP_PEER_PUBLIC_KEY_SIZE] bytes. If `public_key` is null this function has no effect.
     *
     * @param groupNumber The group number of the group we wish to query.
     * @param peerId The ID of the peer whose public key we wish to retrieve.
     * @param publicKey A valid memory region large enough to store the public key. If this parameter is null, this function call has no effect.
     *
     * @return true on success.
     */
    fun groupPeerGetPublicKey(
        groupNumber: ToxGroupNumber,
        peerNumber: ToxGroupPeerNumber,
    ): ToxPublicKey

    /**
     * Store all information associated with the Tox instance to a byte array.
     */
    val savedata: ByteArray

    /**
     * Sends a "nodes request" to the given bootstrap node with IP, port, and DHT public key to setup connections.
     *
     * This function will attempt to connect to the node using UDP. You must use this function even if Tox_Options.udp_enabled was set to false.
     *
     * @param host The hostname or IP address (IPv4 or IPv6) of the node. Must be at most [ToxCoreConstants.MAX_HOSTNAME_LENGTH] chars, including the NUL byte.
     * @param port The port on the host on which the bootstrap Tox instance is listening.
     * @param publicKey The DHT public key of the bootstrap node ([ToxCoreConstants.DHT_ID_SIZE] bytes).
     * @return true on success.
     */
    fun bootstrap(
        host: String,
        port: ToxPort,
        publicKey: ToxDhtId,
    )

    /**
     * Adds additional host:port pair as TCP relay.
     *
     * This function can be used to initiate TCP connections to different ports on the same bootstrap node, or to add TCP relays without using them as bootstrap nodes.
     *
     * @param host The hostname or IP address (IPv4 or IPv6) of the TCP relay. Must be at most [ToxCoreConstants.MAX_HOSTNAME_LENGTH] chars, including the NUL byte.
     * @param port The port on the host on which the TCP relay is listening.
     * @param publicKey The DHT public key of the TCP relay ([ToxCoreConstants.DHT_ID_SIZE] bytes).
     * @return true on success.
     */
    fun addTcpRelay(
        host: String,
        port: ToxPort,
        publicKey: ToxDhtId,
    )

    /**
     * Writes the Tox friend address of the client to a byte array.
     *
     * The address is not in human-readable format. If a client wants to display the address, formatting is required.
     *
     * @see [ToxCoreConstants.ADDRESS_SIZE] for the address format.
     */
    val address: ToxAddress

    /**
     * Set the 4-byte nospam part of the address.
     *
     * This value is expected in host byte order. I.e. 0x12345678 will form the bytes `[12, 34, 56, 78]` in the nospam part of the Tox friend address.
     *
     * @param nospam Any 32 bit unsigned integer.
     */
    fun setNospam(nospam: Int)

    /**
     * Get the 4-byte nospam part of the address.
     *
     * This value is returned in host byte order.
     */
    val nospam: Int

    /**
     * Copy the Tox Public Key (long term) from the Tox object.
     */
    val publicKey: ToxPublicKey

    /**
     * Copy the Tox Secret Key from the Tox object.
     */
    val secretKey: ToxSecretKey

    /**
     * Set the nickname for the Tox client.
     *
     * Nickname length cannot exceed [ToxCoreConstants.MAX_NAME_LENGTH]. If length is 0, the name parameter is ignored (it can be null), and the nickname is set back to empty.
     *
     * @param name A byte array containing the new nickname.
     *
     * @return true on success.
     */
    fun setName(name: ByteArray)

    /**
     * Write the nickname set by [setName] to a byte array.
     *
     * If no nickname was set before calling this function, the name is empty, and this function has no effect.
     *
     * Call [name] to find out how much memory to allocate for the result.
     */
    val name: ByteArray

    /**
     * Set the client's status message.
     *
     * Status message length cannot exceed [ToxCoreConstants.MAX_STATUS_MESSAGE_LENGTH]. If length is 0, the status parameter is ignored (it can be null), and the user status is set back to empty.
     */
    fun setStatusMessage(statusMessage: ByteArray)

    /**
     * Write the status message set by [setStatusMessage] to a byte array.
     *
     * If no status message was set before calling this function, the status is empty, and this function has no effect.
     *
     * Call [statusMessage] to find out how much memory to allocate for the result.
     */
    val statusMessage: ByteArray

    /**
     * Set the client's user status.
     *
     * @param status One of the user statuses listed in the enumeration above.
     */
    fun setStatus(status: ToxUserStatus)

    /**
     * Returns the client's user status.
     */
    val status: ToxUserStatus

    /**
     * Copy a list of valid friend numbers into an array.
     *
     * Call [friendList] to determine the number of elements to allocate.
     */
    val friendList: List<ToxFriendNumber>

    /**
     * Set the client's typing status for a friend.
     *
     * The client is responsible for turning it on or off.
     *
     * @param friendNumber The friend to which the client is typing a message.
     * @param typing The typing status. True means the client is typing.
     *
     * @return true on success.
     */
    fun setTyping(
        friendNumber: ToxFriendNumber,
        typing: Boolean,
    )

    /**
     * Sends a file control command to a friend for a given file transfer.
     *
     * @param friendNumber The friend number of the friend the file is being transferred to or received from.
     * @param fileNumber The friend-specific identifier for the file transfer.
     * @param control The control command to send.
     *
     * @return true on success.
     */
    fun fileControl(
        friendNumber: ToxFriendNumber,
        fileNumber: ToxFileNumber,
        control: ToxFileControl,
    )

    /**
     * Sends a file seek control command to a friend for a given file transfer.
     *
     * This function can only be called to resume a file transfer right before [ToxCoreConstants.FILE_CONTROL_RESUME] is sent.
     *
     * @param friendNumber The friend number of the friend the file is being received from.
     * @param fileNumber The friend-specific identifier for the file transfer.
     * @param position The position that the file should be seeked to.
     */
    fun fileSeek(
        friendNumber: ToxFriendNumber,
        fileNumber: ToxFileNumber,
        position: Long,
    )

    /**
     * Copy the file id associated to the file transfer to a byte array.
     *
     * @param friendNumber The friend number of the friend the file is being transferred to or received from.
     * @param fileNumber The friend-specific identifier for the file transfer.
     * @param fileId A memory region of at least [ToxCoreConstants.FILE_ID_LENGTH] bytes. If this parameter is null, this function has no effect.
     *
     * @return true on success.
     */
    fun fileGetFileId(
        friendNumber: ToxFriendNumber,
        fileNumber: ToxFileNumber,
    ): ToxFileId

    /**
     * Send a chunk of file data to a friend.
     *
     * This function is called in response to the `file_chunk_request` callback. The length parameter should be equal to the one received though the callback. If it is zero, the transfer is assumed complete. For files with known size, Tox will know that the transfer is complete after the last byte has been received, so it is not necessary (though not harmful) to send a zero-length chunk to terminate. For streams, Tox will know that the transfer is finished if a chunk with length less than the length requested in the callback is sent.
     *
     * @param friendNumber The friend number of the receiving friend for this file.
     * @param fileNumber The file transfer identifier returned by [fileSend].
     * @param position The file or stream position from which to continue reading.
     * @return true on success.
     */
    fun fileSendChunk(
        friendNumber: ToxFriendNumber,
        fileNumber: ToxFileNumber,
        position: Long,
        data: ByteArray,
    )

    /**
     * Invites a friend to a conference.
     *
     * @param friendNumber The friend number of the friend we want to invite.
     * @param conferenceNumber The conference number of the conference we want to invite the friend to.
     *
     * @return true on success.
     */
    fun conferenceInvite(
        friendNumber: ToxFriendNumber,
        conferenceNumber: ToxConferenceNumber,
    )

    /**
     * Copy a list of valid conference numbers into the array chatlist.
     *
     * Determine how much space to allocate for the array with the [conferenceChatlist] function.
     *
     * Note that [savedata] saves all connected conferences; when a Tox instance is created from savedata in which conferences were saved, those conferences will be connected at startup, and will be listed by [conferenceChatlist].
     *
     * The conference number of a loaded conference may differ from the conference number it had when it was saved.
     */
    val conferenceChatlist: List<ToxConferenceNumber>

    /**
     * Writes the temporary DHT public key of this instance to a byte array.
     *
     * This can be used in combination with an externally accessible IP address and the bound port (from [udpPort]) to run a temporary bootstrap node.
     *
     * Be aware that every time a new instance is created, the DHT public key changes, meaning this cannot be used to run a permanent bootstrap node.
     */
    val dhtId: ToxDhtId

    /**
     * Return the UDP port this Tox instance is bound to.
     */
    val udpPort: ToxPort

    /**
     * Return the TCP port this Tox instance is bound to.
     *
     * This is only relevant if the instance is acting as a TCP relay.
     */
    val tcpPort: ToxPort

    /**
     * Copy a list of valid group numbers into the array group_list.
     *
     * The array must be large enough to hold the number of groups returned by the [groupList] function.
     */
    val groupList: List<ToxGroupNumber>

    override fun close(): Unit
}
