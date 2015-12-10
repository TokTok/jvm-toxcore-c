package im.tox.tox4j.core

import im.tox.tox4j.core.exceptions.ToxNewException
import im.tox.tox4j.core.options.{ProxyOptions, SaveDataOptions, ToxOptions}
import im.tox.tox4j.impl.jni.ToxCoreImpl

import scala.collection.mutable.ArrayBuffer

@SuppressWarnings(Array("org.brianmckenna.wartremover.warts.Nothing"))
object ToxCoreFactory {

  private final val toxes = new ArrayBuffer[ToxCore[_]]

  private def make[ToxCoreState](options: ToxOptions = ToxOptions()): ToxCore[ToxCoreState] = {
    try {
      new ToxCoreImpl[ToxCoreState](options)
    } catch {
      case e: ToxNewException if e.code == ToxNewException.Code.PORT_ALLOC =>
        System.gc()
        new ToxCoreImpl[ToxCoreState](options)
    }
  }

  private def makeList(count: Int, options: ToxOptions = ToxOptions()): ToxList[Unit] = {
    new ToxList[Unit](() => { this(options) }, count)
  }

  def destroyAll(): Unit = {
    toxes.foreach(_.close())
    toxes.clear()
    System.gc()
  }

  def apply(options: ToxOptions): ToxCore[Unit] = {
    val tox = make[Unit](options)
    toxes += tox
    tox
  }

  def withTox[ToxCoreState, R](tox: ToxCore[ToxCoreState])(f: ToxCore[ToxCoreState] => R): R = {
    try {
      f(tox)
    } finally {
      tox.close()
    }
  }

  def withTox[R](options: ToxOptions)(f: ToxCore[Unit] => R): R = {
    withTox(make[Unit](options))(f)
  }

  def withTox[R](ipv6Enabled: Boolean, udpEnabled: Boolean, proxy: ProxyOptions)(f: ToxCore[Unit] => R): R = {
    withTox(ToxOptions(ipv6Enabled, udpEnabled, proxy))(f)
  }

  def withTox[R](ipv6Enabled: Boolean, udpEnabled: Boolean)(f: ToxCore[Unit] => R): R = {
    withTox(ToxOptions(ipv6Enabled, udpEnabled))(f)
  }

  def withTox[R](fatalErrors: Boolean)(f: ToxCore[Unit] => R): R = {
    withTox(ToxOptions(fatalErrors = fatalErrors))(f)
  }

  def withTox[R](saveData: SaveDataOptions)(f: ToxCore[Unit] => R): R = {
    withTox(new ToxOptions(saveData = saveData))(f)
  }

  def withTox[R](f: ToxCore[Unit] => R): R = {
    withTox(ipv6Enabled = true, udpEnabled = true)(f)
  }

  def withToxS[ToxCoreState, R](options: ToxOptions)(f: ToxCore[ToxCoreState] => R): R = {
    withTox(make[ToxCoreState](options))(f)
  }

  def withToxS[ToxCoreState, R](ipv6Enabled: Boolean, udpEnabled: Boolean)(f: ToxCore[ToxCoreState] => R): R = {
    withToxS(ToxOptions(ipv6Enabled, udpEnabled))(f)
  }

  def withToxS[ToxCoreState, R](ipv6Enabled: Boolean, udpEnabled: Boolean, proxy: ProxyOptions)(f: ToxCore[ToxCoreState] => R): R = {
    withToxS(ToxOptions(ipv6Enabled, udpEnabled, proxy))(f)
  }

  def withToxes[R](count: Int, options: ToxOptions)(f: ToxList[Unit] => R): R = {
    val toxes = makeList(count, options)
    try {
      f(toxes)
    } finally {
      toxes.close()
    }
  }

  def withToxes[R](count: Int)(f: ToxList[Unit] => R): R = {
    withToxes(count, ToxOptions())(f)
  }

}
