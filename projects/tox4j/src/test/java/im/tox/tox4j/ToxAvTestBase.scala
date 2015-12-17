package im.tox.tox4j

import im.tox.tox4j.impl.jni.{ToxAvImpl, ToxCoreImpl, ToxCoreImplFactory}

object ToxAvTestBase {

  final val enabled = {
    try {
      ToxCoreImplFactory.withToxUnit { tox =>
        new ToxAvImpl(tox.asInstanceOf[ToxCoreImpl]).close()
        true
      }
    } catch {
      case _: UnsatisfiedLinkError => false
    }
  }

}
