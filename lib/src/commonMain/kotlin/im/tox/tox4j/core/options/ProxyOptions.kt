package im.tox.tox4j.core.options

import im.tox.tox4j.core.enums.ToxProxyType

/** Proxy options for [[ToxCore.load]] */
object ProxyOptions {
    /** Base type for all proxy kinds. */
    sealed interface Type {
        /** Low level enumeration value to pass to [[ToxCore.load]]. */
        val proxyType: ToxProxyType

        /**
         * The IP address or DNS name of the proxy to be used.
         *
         * If used, this must be a valid DNS name. The name must not exceed
         * [ [ToxCoreConstants.MaxHostnameLength]] characters. This member is ignored (it can be
         * anything) if [[proxyType]] is [[ToxProxyType.NONE]].
         */
        val proxyAddress: String

        /**
         * The port to use to connect to the proxy server.
         *
         * Ports must be in the range (1, 65535). The value is ignored if [[proxyType]] is
         * [ [ToxProxyType.NONE]].
         */
        val proxyPort: UShort
    }

    /** Don't use a proxy. Attempt to directly connect to other nodes. */
    object None : Type {
        override val proxyType: ToxProxyType = ToxProxyType.NONE
        override val proxyAddress: String = ""
        override val proxyPort: UShort = 0.toUShort()
    }

    /** Tunnel Tox TCP traffic over an HTTP proxy. The proxy must support CONNECT. */
    final data class Http(
        override val proxyAddress: String,
        override val proxyPort: UShort,
    ) : Type {
        override val proxyType: ToxProxyType = ToxProxyType.HTTP
    }

    /**
     * Use a SOCKS5 proxy to make TCP connections. Although some SOCKS5 servers support UDP sockets,
     * the main use case (Tor) does not, and Tox will not use the proxy for UDP connections.
     */
    final data class Socks5(
        override val proxyAddress: String,
        override val proxyPort: UShort,
    ) : Type {
        override val proxyType: ToxProxyType = ToxProxyType.SOCKS5
    }
}
