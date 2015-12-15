package im.tox.client

import im.tox.tox4j.av.ToxAv
import im.tox.tox4j.core.ToxCore

final case class ToxClient(
  tox: ToxCore[ToxClientState],
  av: ToxAv[ToxClientState],
  state: ToxClientState
)
