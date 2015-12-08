package sbt.tox4j.lint

import com.etsy.sbt.Checkstyle.CheckstyleTasks._
import com.etsy.sbt.Checkstyle.checkstyleSettings
import com.etsy.sbt.{NoExitException, NoExitSecurityManager}
import com.puppycrawl.tools.checkstyle.Main
import sbt.Def.Initialize
import sbt.Keys._
import sbt._
import sbt.tox4j.OptionalPlugin

object Checkstyle extends OptionalPlugin {

  object Keys {
    val checkstyleFatal = settingKey[Boolean]("Whether to fail the checkstyle task on Java code style violations.")
  }

  import Keys._

  /**
   * Wraps a block of code and executes it, preventing exits from the
   * JVM.  It does this by using a custom SecurityManager that throws
   * an exception if exitVM permission is checked.
   *
   * @param block The block of code to wrap and execute
   */
  private def noExit(block: => Unit): Unit = {
    val original = System.getSecurityManager
    System.setSecurityManager(new NoExitSecurityManager())

    try {
      block
    } catch {
      case _: NoExitException =>
      case e: Throwable       => throw e
    } finally {
      System.setSecurityManager(original)
    }
  }

  /**
   * Runs checkstyle
   *
   * @param config The configuration (Compile or Test) in which context to execute the checkstyle command
   */
  private def checkstyleTask(config: Configuration): Initialize[Task[Unit]] = Def.task {
    val log = streams.value.log

    // Only run checkstyle if there are Java sources.
    if (((javaSource in config).value ** "*.java").get.nonEmpty) {
      runCheckstyle(
        log,
        outputFile = (checkstyleTarget in config).value.getAbsolutePath,
        outputDir = target.value,
        javaSources = (javaSource in config).value,
        fatalErrors = (checkstyleFatal in config).value
      )
    }
  }

  def runCheckstyle(log: Logger, outputFile: String, outputDir: File, javaSources: File, fatalErrors: Boolean): Unit = {
    val checkstyleArgs = Seq(
      "-c", getClass.getResource("checkstyle-config.xml").toString, // checkstyle configuration file
      javaSources.getAbsolutePath, // location of Java source file
      "-f", "xml", // output format
      "-o", outputFile // output file
    )

    if (!outputDir.exists()) {
      outputDir.mkdirs()
    }
    // Checkstyle calls System.exit which would exit SBT
    // Thus we wrap the call to it with a special security policy
    // that forbids exiting the JVM
    noExit {
      Main.main(checkstyleArgs: _*)
    }

    val errors = {
      val results = scala.xml.XML.loadFile(outputFile)
      val errorFiles = results \\ "checkstyle" \\ "file"
      errorFiles flatMap errorsFromXml
    }

    if (errors.nonEmpty) {
      for ((name, line, error, source) <- errors) {
        log.error(s"$name:$line: $error (from $source)")
      }

      val message = s"Checkstyle failed with ${errors.size} errors"
      if (fatalErrors) {
        sys.error(message)
      } else {
        log.warn(message)
      }
    } else {
      log.info("No errors from checkstyle")
    }
  }

  private def errorsFromXml(fileNode: scala.xml.NodeSeq) = {
    val name = (fileNode \ "@name").text
    val errors = (fileNode \\ "error") map errorFromXml
    errors map { case (line, error, source) => (name, line, error, source) }
  }

  private def errorFromXml(node: scala.xml.NodeSeq) = {
    val line = (node \ "@line").text
    val msg = (node \ "@message").text
    val source = (node \ "@source").text
    (line, msg, source)
  }

  // Enable checkstyle.
  override val moduleSettings = checkstyleSettings ++ Seq(
    // Fail if production code violates the coding style.
    checkstyleFatal in Compile := true,
    checkstyleFatal in Test := false,

    checkstyle in Compile <<= checkstyleTask(Compile),
    checkstyle in Test <<= checkstyleTask(Test)
  )

}
