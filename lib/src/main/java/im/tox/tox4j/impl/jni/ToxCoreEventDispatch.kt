package im.tox.tox4j.impl.jni

import im.tox.tox4j.core.callbacks.ToxCoreEventListener
import im.tox.tox4j.core.data.*
import im.tox.tox4j.core.enums.*
import im.tox.tox4j.core.proto.*

object ToxCoreEventDispatch {

  fun convert(status: Connection.Type): ToxConnection =
      when (status) {
        Connection.Type.NONE -> ToxConnection.NONE
        Connection.Type.TCP -> ToxConnection.TCP
        Connection.Type.UDP -> ToxConnection.UDP
        Connection.Type.UNRECOGNIZED -> ToxConnection.NONE
      }

  fun convert(status: UserStatus.Type): ToxUserStatus =
      when (status) {
        UserStatus.Type.NONE -> ToxUserStatus.NONE
        UserStatus.Type.AWAY -> ToxUserStatus.AWAY
        UserStatus.Type.BUSY -> ToxUserStatus.BUSY
        UserStatus.Type.UNRECOGNIZED -> ToxUserStatus.NONE
      }

  fun convert(status: ToxUserStatus): UserStatus.Type =
      when (status) {
        ToxUserStatus.NONE -> UserStatus.Type.NONE
        ToxUserStatus.AWAY -> UserStatus.Type.AWAY
        ToxUserStatus.BUSY -> UserStatus.Type.BUSY
      }

  fun convert(control: FileControl.Type): ToxFileControl =
      when (control) {
        FileControl.Type.RESUME -> ToxFileControl.RESUME
        FileControl.Type.PAUSE -> ToxFileControl.PAUSE
        FileControl.Type.CANCEL -> ToxFileControl.CANCEL
        FileControl.Type.UNRECOGNIZED -> ToxFileControl.CANCEL
      }

  fun convert(messageType: MessageType.Type): ToxMessageType =
      when (messageType) {
        MessageType.Type.NORMAL -> ToxMessageType.NORMAL
        MessageType.Type.ACTION -> ToxMessageType.ACTION
        MessageType.Type.UNRECOGNIZED -> ToxMessageType.NORMAL
      }

  private fun <S> dispatchSelfConnectionStatus(
      handler: ToxCoreEventListener<S>,
      selfConnectionStatus: List<SelfConnectionStatus>,
      state: S
  ): S =
      selfConnectionStatus.fold(
          state,
          { next, ev -> handler.selfConnectionStatus(convert(ev.getConnectionStatus()), next) })

  private fun <S> dispatchFriendName(
      handler: ToxCoreEventListener<S>,
      friendName: List<FriendName>,
      state: S
  ): S =
      friendName.fold(
          state,
          { next, ev ->
            handler.friendName(
                ToxFriendNumber(ev.getFriendNumber()),
                ToxNickname(ev.getName().toByteArray()),
                next)
          })

  private fun <S> dispatchFriendStatusMessage(
      handler: ToxCoreEventListener<S>,
      friendStatusMessage: List<FriendStatusMessage>,
      state: S
  ): S =
      friendStatusMessage.fold(
          state,
          { next, ev ->
            handler.friendStatusMessage(
                ToxFriendNumber(ev.getFriendNumber()),
                ToxStatusMessage(ev.getMessage().toByteArray()),
                next)
          })

  private fun <S> dispatchFriendStatus(
      handler: ToxCoreEventListener<S>,
      friendStatus: List<FriendStatus>,
      state: S
  ): S =
      friendStatus.fold(
          state,
          { next, ev ->
            handler.friendStatus(
                ToxFriendNumber(ev.getFriendNumber()), convert(ev.getStatus()), next)
          })

  private fun <S> dispatchFriendConnectionStatus(
      handler: ToxCoreEventListener<S>,
      friendConnectionStatus: List<FriendConnectionStatus>,
      state: S
  ): S =
      friendConnectionStatus.fold(
          state,
          { next, ev ->
            handler.friendConnectionStatus(
                ToxFriendNumber(ev.getFriendNumber()), convert(ev.getConnectionStatus()), next)
          })

  private fun <S> dispatchFriendTyping(
      handler: ToxCoreEventListener<S>,
      friendTyping: List<FriendTyping>,
      state: S
  ): S =
      friendTyping.fold(
          state,
          { next, ev ->
            handler.friendTyping(ToxFriendNumber(ev.getFriendNumber()), ev.getIsTyping(), next)
          })

  private fun <S> dispatchFriendReadReceipt(
      handler: ToxCoreEventListener<S>,
      friendReadReceipt: List<FriendReadReceipt>,
      state: S
  ): S =
      friendReadReceipt.fold(
          state,
          { next, ev ->
            handler.friendReadReceipt(
                ToxFriendNumber(ev.getFriendNumber()), ev.getMessageId(), next)
          })

  private fun <S> dispatchFriendRequest(
      handler: ToxCoreEventListener<S>,
      friendRequest: List<FriendRequest>,
      state: S
  ): S =
      friendRequest.fold(
          state,
          { next, ev ->
            handler.friendRequest(
                ToxPublicKey(ev.getPublicKey().toByteArray()),
                ev.getTimeDelta(),
                ToxFriendRequestMessage(ev.getMessage().toByteArray()),
                next)
          })

  private fun <S> dispatchFriendMessage(
      handler: ToxCoreEventListener<S>,
      friendMessage: List<FriendMessage>,
      state: S
  ): S =
      friendMessage.fold(
          state,
          { next, ev ->
            handler.friendMessage(
                ToxFriendNumber(ev.getFriendNumber()),
                convert(ev.getType()),
                ev.getTimeDelta(),
                ToxFriendMessage(ev.getMessage().toByteArray()),
                next)
          })

  private fun <S> dispatchFileRecvControl(
      handler: ToxCoreEventListener<S>,
      fileRecvControl: List<FileRecvControl>,
      state: S
  ): S =
      fileRecvControl.fold(
          state,
          { next, ev ->
            handler.fileRecvControl(
                ToxFriendNumber(ev.getFriendNumber()),
                ev.getFileNumber(),
                convert(ev.getControl()),
                next)
          })

  private fun <S> dispatchFileChunkRequest(
      handler: ToxCoreEventListener<S>,
      fileChunkRequest: List<FileChunkRequest>,
      state: S
  ): S =
      fileChunkRequest.fold(
          state,
          { next, ev ->
            handler.fileChunkRequest(
                ToxFriendNumber(ev.getFriendNumber()),
                ev.getFileNumber(),
                ev.getPosition(),
                ev.getLength(),
                next)
          })

  private fun <S> dispatchFileRecv(
      handler: ToxCoreEventListener<S>,
      fileRecv: List<FileRecv>,
      state: S
  ): S =
      fileRecv.fold(
          state,
          { next, ev ->
            handler.fileRecv(
                ToxFriendNumber(ev.getFriendNumber()),
                ev.getFileNumber(),
                ev.getKind(),
                ev.getFileSize(),
                ToxFilename(ev.getFilename().toByteArray()),
                next)
          })

  private fun <S> dispatchFileRecvChunk(
      handler: ToxCoreEventListener<S>,
      fileRecvChunk: List<FileRecvChunk>,
      state: S
  ): S =
      fileRecvChunk.fold(
          state,
          { next, ev ->
            handler.fileRecvChunk(
                ToxFriendNumber(ev.getFriendNumber()),
                ev.getFileNumber(),
                ev.getPosition(),
                ev.getData().toByteArray(),
                next)
          })

  private fun <S> dispatchFriendLossyPacket(
      handler: ToxCoreEventListener<S>,
      friendLossyPacket: List<FriendLossyPacket>,
      state: S
  ): S =
      friendLossyPacket.fold(
          state,
          { next, ev ->
            handler.friendLossyPacket(
                ToxFriendNumber(ev.getFriendNumber()),
                ToxLossyPacket(ev.getData().toByteArray()),
                next)
          })

  private fun <S> dispatchFriendLosslessPacket(
      handler: ToxCoreEventListener<S>,
      friendLosslessPacket: List<FriendLosslessPacket>,
      state: S
  ): S =
      friendLosslessPacket.fold(
          state,
          { next, ev ->
            handler.friendLosslessPacket(
                ToxFriendNumber(ev.getFriendNumber()),
                ToxLosslessPacket(ev.getData().toByteArray()),
                next)
          })

  private fun <S> dispatchEvents(
      handler: ToxCoreEventListener<S>,
      events: CoreEvents,
      state: S
  ): S =
      dispatchSelfConnectionStatus(
          handler,
          events.getSelfConnectionStatusList(),
          dispatchFriendName(
              handler,
              events.getFriendNameList(),
              dispatchFriendStatusMessage(
                  handler,
                  events.getFriendStatusMessageList(),
                  dispatchFriendStatus(
                      handler,
                      events.getFriendStatusList(),
                      dispatchFriendConnectionStatus(
                          handler,
                          events.getFriendConnectionStatusList(),
                          dispatchFriendTyping(
                              handler,
                              events.getFriendTypingList(),
                              dispatchFriendReadReceipt(
                                  handler,
                                  events.getFriendReadReceiptList(),
                                  dispatchFriendRequest(
                                      handler,
                                      events.getFriendRequestList(),
                                      dispatchFriendMessage(
                                          handler,
                                          events.getFriendMessageList(),
                                          dispatchFileRecvControl(
                                              handler,
                                              events.getFileRecvControlList(),
                                              dispatchFileChunkRequest(
                                                  handler,
                                                  events.getFileChunkRequestList(),
                                                  dispatchFileRecv(
                                                      handler,
                                                      events.getFileRecvList(),
                                                      dispatchFileRecvChunk(
                                                          handler,
                                                          events.getFileRecvChunkList(),
                                                          dispatchFriendLossyPacket(
                                                              handler,
                                                              events.getFriendLossyPacketList(),
                                                              dispatchFriendLosslessPacket(
                                                                  handler,
                                                                  events
                                                                      .getFriendLosslessPacketList(),
                                                                  state)))))))))))))))

  fun <S> dispatch(handler: ToxCoreEventListener<S>, eventData: ByteArray?, state: S): S =
      if (eventData == null) {
        state
      } else {
        dispatchEvents(handler, CoreEvents.parseFrom(eventData), state)
      }
}
