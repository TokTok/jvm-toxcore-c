package im.tox.tox4j.av

import im.tox.tox4j.core.ToxCore
import im.tox.tox4j.impl.jni.{ToxAvImpl, ToxCoreImpl}

import scala.collection.mutable.ArrayBuffer

@SuppressWarnings(Array("org.brianmckenna.wartremover.warts.Any"))
object ToxAvFactory {

  private final val toxAvs = new ArrayBuffer[ToxAv[_]]

  @SuppressWarnings(Array("org.brianmckenna.wartremover.warts.AsInstanceOf"))
  private def make[ToxCoreState](tox: ToxCore[ToxCoreState]): ToxAv[ToxCoreState] = {
    new ToxAvImpl[ToxCoreState](tox.asInstanceOf[ToxCoreImpl[ToxCoreState]])
  }

  def destroyAll(): Unit = {
    toxAvs.foreach(_.close())
    toxAvs.clear()
    System.gc()
  }

  def apply(tox: ToxCore[Unit]): ToxAv[Unit] = {
    val toxAv = make[Unit](tox)
    toxAvs += toxAv
    toxAv
  }

  def withToxAv[ToxCoreState, R](toxAv: ToxAv[ToxCoreState])(f: ToxAv[ToxCoreState] => R): R = {
    try {
      f(toxAv)
    } finally {
      toxAv.close()
    }
  }

  def withToxAvS[ToxCoreState, R](tox: ToxCore[ToxCoreState])(f: ToxAv[ToxCoreState] => R): R = {
    withToxAv(make[ToxCoreState](tox))(f)
  }

}
