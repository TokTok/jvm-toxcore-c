package im.tox.tox4j.core.callbacks

import im.tox.tox4j.core.data.ToxFileNumber
import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.core.enums.ToxFileControl

interface FileRecvControlCallback<ToxCoreState> {
    /**
     * When receiving [ToxCoreConstants.FILE_CONTROL_CANCEL], the client should release the resources associated with the file number and consider the transfer failed.
     *
     * @param friendNumber The friend number of the friend who is sending the file.
     * @param fileNumber The friend-specific file number the data received is associated with.
     * @param control The file control command received.
     */
    fun fileRecvControl(
        friendNumber: ToxFriendNumber,
        fileNumber: ToxFileNumber,
        control: ToxFileControl,
        state: ToxCoreState,
    ): ToxCoreState = state
}
