package im.tox.sbt

import java.io.File

@SuppressWarnings(Array(
  "org.brianmckenna.wartremover.warts.AsInstanceOf",
  "org.brianmckenna.wartremover.warts.Null"
))
object JavaLibraryPath {

  /**
   * Appends a path to the java.library.path property and to [[ClassLoader]]'s private static
   * [[ClassLoader.usr_paths]] String array field.
   *
   * @param path Path to add to the property and ClassLoader search path.
   * @throws IllegalAccessException If the security context did not allow writing to private fields.
   */
  @throws[IllegalAccessException]
  def addLibraryPath(path: String): Unit = {
    val field =
      try {
        classOf[ClassLoader].getDeclaredField("usr_paths")
      } catch {
        case e: NoSuchFieldException =>
          throw new RuntimeException("Implementation of ClassLoader changed: usr_paths field no longer exists", e)
      }
    field.setAccessible(true)

    val paths = field.get(null).asInstanceOf[Array[String]]
    if (!paths.contains(path)) {
      val tmp = new Array[String](paths.length + 1)
      System.arraycopy(paths, 0, tmp, 0, paths.length)
      tmp(paths.length) = path
      field.set(null, tmp)

      System.setProperty("java.library.path", System.getProperty("java.library.path") + File.pathSeparator + path)
    }
  }

}
