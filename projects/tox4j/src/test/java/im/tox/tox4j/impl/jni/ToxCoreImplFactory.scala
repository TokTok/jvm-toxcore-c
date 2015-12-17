package im.tox.tox4j.impl.jni

import im.tox.tox4j.core.exceptions.ToxNewException
import im.tox.tox4j.core.options.{ProxyOptions, SaveDataOptions, ToxOptions}
import im.tox.tox4j.core.{ToxCore, ToxCoreFactory, ToxList}

import scala.collection.mutable.ArrayBuffer

@SuppressWarnings(Array("org.brianmckenna.wartremover.warts.Nothing"))
object ToxCoreImplFactory extends ToxCoreFactory {

  private final val toxes = new ArrayBuffer[ToxCore]

  private def make(options: ToxOptions = ToxOptions()): ToxCore = {
    try {
      new ToxCoreImpl(options)
    } catch {
      case e: ToxNewException if e.code == ToxNewException.Code.PORT_ALLOC =>
        System.gc()
        new ToxCoreImpl(options)
    }
  }

  private def makeList(count: Int, options: ToxOptions = ToxOptions()): ToxList = {
    new ToxList(() => { this(options) }, count)
  }

  def destroyAll(): Unit = {
    toxes.foreach(_.close())
    toxes.clear()
    System.gc()
  }

  def apply(options: ToxOptions): ToxCore = {
    val tox = make(options)
    toxes += tox
    tox
  }

  def withToxUnit[R](options: ToxOptions)(f: ToxCore => R): R = {
    withTox(make(options))(f)
  }

  def withToxUnit[R](ipv6Enabled: Boolean, udpEnabled: Boolean, proxy: ProxyOptions)(f: ToxCore => R): R = {
    withToxUnit(ToxOptions(ipv6Enabled, udpEnabled, proxy))(f)
  }

  def withToxUnit[R](ipv6Enabled: Boolean, udpEnabled: Boolean)(f: ToxCore => R): R = {
    withToxUnit(ToxOptions(ipv6Enabled, udpEnabled))(f)
  }

  def withToxUnit[R](fatalErrors: Boolean)(f: ToxCore => R): R = {
    withToxUnit(ToxOptions(fatalErrors = fatalErrors))(f)
  }

  def withToxUnit[R](saveData: SaveDataOptions)(f: ToxCore => R): R = {
    withToxUnit(new ToxOptions(saveData = saveData))(f)
  }

  def withToxUnit[R](f: ToxCore => R): R = {
    withToxUnit(ipv6Enabled = true, udpEnabled = true)(f)
  }

  def withTox[R](options: ToxOptions)(f: ToxCore => R): R = {
    withTox(make(options))(f)
  }

  def withToxS[R](options: ToxOptions)(f: ToxCore => R): R = {
    withTox(make(options))(f)
  }

  def withToxS[R](ipv6Enabled: Boolean, udpEnabled: Boolean)(f: ToxCore => R): R = {
    withToxS(ToxOptions(ipv6Enabled, udpEnabled))(f)
  }

  def withToxS[R](ipv6Enabled: Boolean, udpEnabled: Boolean, proxy: ProxyOptions)(f: ToxCore => R): R = {
    withToxS(ToxOptions(ipv6Enabled, udpEnabled, proxy))(f)
  }

  def withToxes[R](count: Int, options: ToxOptions)(f: ToxList => R): R = {
    val toxes = makeList(count, options)
    try {
      f(toxes)
    } finally {
      toxes.close()
    }
  }

  def withToxes[R](count: Int)(f: ToxList => R): R = {
    withToxes(count, ToxOptions())(f)
  }

}
