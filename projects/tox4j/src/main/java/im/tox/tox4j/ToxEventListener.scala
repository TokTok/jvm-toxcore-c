package im.tox.tox4j

import im.tox.tox4j.av.callbacks.ToxAvEventListener
import im.tox.tox4j.core.callbacks.ToxCoreEventListener

trait ToxEventListener[ToxCoreState]
  extends ToxCoreEventListener[ToxCoreState]
  with ToxAvEventListener[ToxCoreState]
