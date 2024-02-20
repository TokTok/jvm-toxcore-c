package im.tox.tox4j

import im.tox.tox4j.av.callbacks.ToxAvEventListener
import im.tox.tox4j.core.callbacks.ToxCoreEventListener

interface ToxEventListener<ToxCoreState> :
    ToxCoreEventListener<ToxCoreState>, ToxAvEventListener<ToxCoreState>
