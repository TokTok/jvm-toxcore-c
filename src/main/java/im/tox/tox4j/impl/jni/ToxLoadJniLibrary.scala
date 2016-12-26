package im.tox.tox4j.impl.jni

import java.io.File
import java.net.URL

import com.google.common.io.Files
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

import scala.language.postfixOps
import scala.sys.process._

object ToxLoadJniLibrary {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  private val AlreadyLoaded = "Native Library (.+) already loaded in another classloader".r
  private val NotFoundDalvik = "Couldn't load .+ from loader .+ findLibrary returned null".r
  private val NotFoundJvm = "no .+ in java.library.path".r

  private def withTempFile(prefix: String, suffix: String)(block: File => Unit): Unit = {
    val file = File.createTempFile(prefix, suffix)
    file.deleteOnExit()
    try {
      block(file)
    } finally {
      // This may fail if the OS doesn't support deleting files that are in use, but deleteOnExit
      // will ensure that it is cleaned up on normal JVM termination.
      file.delete()
    }
  }

  /**
   * Load a native library from an existing location by copying it to a new, temporary location and loading
   * that new library.
   *
   * @param location A [[File]] pointing to the existing library.
   */
  def loadFromSystem(location: File): Unit = {
    withTempFile(location.getName, location.getName) { libraryFile =>
      logger.info(s"Copying $location to $libraryFile")
      Files.copy(location, libraryFile)

      System.load(libraryFile.getPath)
    }
  }

  def load(name: String): Unit = {
    try {
      System.loadLibrary(name)
    } catch {
      case exn: UnsatisfiedLinkError =>
        exn.getMessage match {
          case AlreadyLoaded(location) =>
            logger.warn(s"${exn.getMessage} copying file and loading again")
            loadFromSystem(new File(location))
          case NotFoundDalvik() | NotFoundJvm() =>
            logger.error(
              s"Could not load native library '$name' (${exn.getMessage}). " +
                s"java.library.path = ${sys.props("java.library.path")}."
            )
          case _ =>
            throw exn
        }
    }
  }

}
