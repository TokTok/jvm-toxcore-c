package im.tox.tox4j.impl.jni

import java.io.File

import com.google.common.io.Files
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

import scala.language.postfixOps
import scala.tools.nsc.interpreter.InputStream

object ToxLoadJniLibrary {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  private val AlreadyLoaded = "Native Library (.+) already loaded in another classloader".r
  private val NotFoundDalvik = "Couldn't load .+ from loader .+ findLibrary returned null".r
  private val NotFoundJvm = "no .+ in java.library.path".r

  private def withTempFile(name: String)(block: File => Unit): Unit = {
    val (prefix, suffix) = name.splitAt(name.lastIndexOf("."))
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

  private def withResource(name: String)(block: InputStream => Unit): Unit = {
    val stream = getClass.getResourceAsStream(name)
    try {
      block(stream)
    } finally {
      stream.close()
    }
  }

  /**
   * Load a native library from an existing location by copying it to a new, temporary location and loading
   * that new library.
   *
   * @param location A [[File]] pointing to the existing library.
   */
  private def loadFromSystem(location: File): Unit = {
    withTempFile(location.getName) { libraryFile =>
      logger.info(s"Copying $location to $libraryFile")
      Files.copy(location, libraryFile)

      System.load(libraryFile.getPath)
    }
  }

  /**
   * Load a library from a linked resource jar by copying it to a temporary location and then loading that
   * temporary file.
   *
   * @param name The library name without "dll" suffix or "lib" prefix.
   */
  private def loadFromJar(name: String): Unit = {
    val osName = Map(
      "Mac OS X" -> "darwin"
    ).withDefault((x: String) => x.toLowerCase.split(" ").head)
    val archName = Map(
      "amd64" -> "x86_64"
    ).withDefault((x: String) => x.toLowerCase)

    val resourceName = "%s-%s/%s".format(
      osName(sys.props("os.name")),
      archName(sys.props("os.arch")),
      System.mapLibraryName(name)
    )
    logger.debug(s"Loading $name from resource: $resourceName")
    val location = new File(resourceName)
    withTempFile(location.getName) { libraryFile =>
      withResource(resourceName) { stream =>
        logger.debug(s"Copying $resourceName to ${libraryFile.getPath}")
        Files.asByteSink(libraryFile).writeFrom(stream)
      }
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
          case NotFoundJvm() =>
            loadFromJar(name)
          case NotFoundDalvik() =>
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
