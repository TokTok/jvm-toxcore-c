package im.tox.tox4j.core.options

import im.tox.tox4j.core.ToxCore
import im.tox.tox4j.core.ToxCoreConstants
import im.tox.tox4j.core.data.ToxPort
import im.tox.tox4j.core.enums.ToxProxyType

/** Proxy options for [ToxCore]. */
object ProxyOptions {
    /** Base type for all proxy kinds. */
    sealed interface Type {
        /** Low level enumeration value to pass to [ToxCore]. */
        val proxyType: ToxProxyType

        /**
         * The IP address or DNS name of the proxy to be used.
         *
         * If used, this must be a valid DNS name. The name must not exceed
         * [ToxCoreConstants.MAX_HOSTNAME_LENGTH] characters. This member is ignored (it can be
         * anything) if [proxyType] is [ToxProxyType.NONE].
         */
        val proxyHost: String

        /**
         * The port to use to connect to the proxy server.
         *
         * Ports must be in the range (1, 65535). The value is ignored if [proxyType] is
         * [ToxProxyType.NONE].
         */
        val proxyPort: ToxPort
    }

    /** Don't use a proxy. Attempt to directly connect to other nodes. */
    object None : Type {
        override val proxyType: ToxProxyType = ToxProxyType.NONE
        override val proxyHost: String = ""
        override val proxyPort: ToxPort = ToxPort(0)
    }

    /** Tunnel Tox TCP traffic over an HTTP proxy. The proxy must support CONNECT. */
    data class Http(
        override val proxyHost: String,
        override val proxyPort: ToxPort,
    ) : Type {
        override val proxyType: ToxProxyType = ToxProxyType.HTTP
    }

    /**
     * Use a SOCKS5 proxy to make TCP connections. Although some SOCKS5 servers support UDP sockets,
     * the main use case (Tor) does not, and Tox will not use the proxy for UDP connections.
     */
    data class Socks5(
        override val proxyHost: String,
        override val proxyPort: ToxPort,
    ) : Type {
        override val proxyType: ToxProxyType = ToxProxyType.SOCKS5
    }
}
