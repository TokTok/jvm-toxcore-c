package im.tox.tox4j.core

import im.tox.tox4j.core.options.ToxOptions
import im.tox.tox4j.impl.cinterop.ToxCoreImpl

@kotlin.ExperimentalStdlibApi
actual fun newToxCore(options: ToxOptions): ToxCore = ToxCoreImpl(options)
