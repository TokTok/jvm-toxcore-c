package im.tox.tox4j.core.options

import im.tox.tox4j.core.ToxCoreConstants
import im.tox.tox4j.core.data.ToxPort

data class ToxOptions(
    val ipv6Enabled: Boolean = true,
    val udpEnabled: Boolean = true,
    val localDiscoveryEnabled: Boolean = true,
    val dhtAnnouncementsEnabled: Boolean = true,
    val proxy: ProxyOptions.Type = ProxyOptions.None,
    val startPort: ToxPort = ToxPort(ToxCoreConstants.DEFAULT_START_PORT),
    val endPort: ToxPort = ToxPort(ToxCoreConstants.DEFAULT_END_PORT),
    val tcpPort: ToxPort = ToxPort(ToxCoreConstants.DEFAULT_TCP_PORT),
    val holePunchingEnabled: Boolean = true,
    val savedata: SavedataOptions.Type = SavedataOptions.None,
    val experimentalOwnedData: Boolean = false,
    val experimentalThreadSafety: Boolean = false,
    val experimentalGroupsPersistence: Boolean = false,
    val experimentalDisableDns: Boolean = false,
)
