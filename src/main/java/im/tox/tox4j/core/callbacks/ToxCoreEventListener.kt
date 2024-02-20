package im.tox.tox4j.core.callbacks

interface ToxCoreEventListener<ToxCoreState> :
    SelfConnectionStatusCallback<ToxCoreState>,
    FileRecvControlCallback<ToxCoreState>,
    FileRecvCallback<ToxCoreState>,
    FileRecvChunkCallback<ToxCoreState>,
    FileChunkRequestCallback<ToxCoreState>,
    FriendConnectionStatusCallback<ToxCoreState>,
    FriendMessageCallback<ToxCoreState>,
    FriendNameCallback<ToxCoreState>,
    FriendRequestCallback<ToxCoreState>,
    FriendStatusCallback<ToxCoreState>,
    FriendStatusMessageCallback<ToxCoreState>,
    FriendTypingCallback<ToxCoreState>,
    FriendLosslessPacketCallback<ToxCoreState>,
    FriendLossyPacketCallback<ToxCoreState>,
    FriendReadReceiptCallback<ToxCoreState>
