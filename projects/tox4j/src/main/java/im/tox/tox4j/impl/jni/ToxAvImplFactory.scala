package im.tox.tox4j.impl.jni

import im.tox.tox4j.av.{ToxAv, ToxAvFactory}
import im.tox.tox4j.core.ToxCore

object ToxAvImplFactory extends ToxAvFactory {

  @SuppressWarnings(Array("org.brianmckenna.wartremover.warts.AsInstanceOf"))
  private def make[ToxCoreState](tox: ToxCore[ToxCoreState]): ToxAv[ToxCoreState] = {
    new ToxAvImpl[ToxCoreState](tox.asInstanceOf[ToxCoreImpl[ToxCoreState]])
  }

  def withToxAv[ToxCoreState, R](tox: ToxCore[ToxCoreState])(f: ToxAv[ToxCoreState] => R): R = {
    withToxAv(make[ToxCoreState](tox))(f)
  }

}
