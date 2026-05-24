package im.tox.tox4j.core

import kotlin.test.Test
import kotlin.test.assertEquals

class ToxCoreConstantsTest {
    @Test
    fun publicKeySize() = assertEquals(32, ToxCoreConstants.PUBLIC_KEY_SIZE)

    @Test
    fun secretKeySize() = assertEquals(32, ToxCoreConstants.SECRET_KEY_SIZE)

    @Test
    fun addressSize() = assertEquals(38, ToxCoreConstants.ADDRESS_SIZE)

    @Test
    fun maxNameLength() = assertEquals(128, ToxCoreConstants.MAX_NAME_LENGTH)

    @Test
    fun maxStatusMessageLength() = assertEquals(1007, ToxCoreConstants.MAX_STATUS_MESSAGE_LENGTH)

    @Test
    fun maxFriendRequestLength() = assertEquals(921, ToxCoreConstants.MAX_FRIEND_REQUEST_LENGTH)

    @Test
    fun maxMessageLength() = assertEquals(1372, ToxCoreConstants.MAX_MESSAGE_LENGTH)

    @Test
    fun maxCustomPacketSize() = assertEquals(1373, ToxCoreConstants.MAX_CUSTOM_PACKET_SIZE)

    @Test
    fun maxFilenameLength() = assertEquals(255, ToxCoreConstants.MAX_FILENAME_LENGTH)

    @Test
    fun maxHostnameLength() = assertEquals(255, ToxCoreConstants.MAX_HOSTNAME_LENGTH)

    @Test
    fun fileIdLength() = assertEquals(32, ToxCoreConstants.FILE_ID_LENGTH)

    @Test
    fun defaultProxyPort() = assertEquals(8080, ToxCoreConstants.DEFAULT_PROXY_PORT)

    @Test
    fun defaultStartPort() = assertEquals(33445, ToxCoreConstants.DEFAULT_START_PORT)

    @Test
    fun defaultEndPort() = assertEquals(33545, ToxCoreConstants.DEFAULT_END_PORT)

    @Test
    fun defaultTcpPort() = assertEquals(0, ToxCoreConstants.DEFAULT_TCP_PORT)
}
