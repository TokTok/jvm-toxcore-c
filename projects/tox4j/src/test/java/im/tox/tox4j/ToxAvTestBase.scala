package im.tox.tox4j

import im.tox.tox4j.impl.jni.{ToxAvImpl, ToxCoreImpl, ToxCoreImplFactory}

object ToxAvTestBase {

  final val enabled = {
    try {
      ToxCoreImplFactory.withToxUnit { tox =>
        new ToxAvImpl[Unit](tox.asInstanceOf[ToxCoreImpl[Unit]]).close()
        true
      }
    } catch {
      case _: UnsatisfiedLinkError => false
    }
  }

}
