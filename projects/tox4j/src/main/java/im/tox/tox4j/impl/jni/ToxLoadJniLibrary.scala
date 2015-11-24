package im.tox.tox4j.impl.jni

import java.io.File
import java.net.URL

import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

import scala.language.postfixOps
import scala.sys.process._

object ToxLoadJniLibrary {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  private val repoUrl = "https://raw.githubusercontent.com/tox4j/tox4j.github.io/master/native"

  private val target = {
    val osName =
      if (sys.props("java.vm.name") == "Dalvik") {
        "Android"
      } else {
        sys.props("os.name")
      }

    Map(
      "Android" -> Map(
        "armv7l" -> "arm-linux-androideabi",
        "i686" -> "i686-linux-android"
      ),
      "Linux" -> Map(
        "amd64" -> "x86_64-linux"
      ),
      "Mac OS X" -> Map(
        "x86_64" -> "x86_64-darwin"
      )
    )(osName)(sys.props("os.arch"))
  }

  def loadFromWeb(name: String): Unit = {
    val libraryName = System.mapLibraryName(name)
    val libraryFile = File.createTempFile(name, libraryName)
    libraryFile.deleteOnExit()

    val url = new URL(s"$repoUrl/$target/$libraryName")
    logger.info(s"Downloading $url to $libraryFile")
    val start = System.currentTimeMillis()
    url #> libraryFile !!
    val end = System.currentTimeMillis()
    logger.info(s"Downloading $libraryName took ${end - start}ms")

    System.load(libraryFile.getPath)
  }

  def load(name: String): Unit = {
    try {
      System.loadLibrary(name)
    } catch {
      case exn: UnsatisfiedLinkError =>
        logger.warn(s"Library '$name' not found (${exn.getMessage}); attempting to load from web")
        loadFromWeb(name)
    }
  }

}
