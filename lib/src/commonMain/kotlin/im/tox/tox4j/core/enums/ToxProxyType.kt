package im.tox.tox4j.core.enums

/**
 * Type of proxy used to connect to TCP relays.
 */
enum class ToxProxyType {
    /**
     * Don't use a proxy.
     */
    NONE,

    /**
     * HTTP proxy using CONNECT.
     */
    HTTP,

    /**
     * SOCKS proxy for simple socket pipes.
     */
    SOCKS5,
}
