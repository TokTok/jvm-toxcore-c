package sbt.tox4j.lint

import java.io.InputStream
import java.net.URLClassLoader
import java.util.Properties

import sbt.Keys._
import sbt._
import wartremover.WartRemover.autoImport._

import scala.collection.JavaConverters._

object WartLoader extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements

  private def withInputStream[R](url: URL)(f: InputStream => R): R = {
    val inputStream = url.openStream()
    try {
      f(inputStream)
    } finally {
      inputStream.close()
    }
  }

  private def loadProperties(propertiesFile: URL): Properties = {
    withInputStream(propertiesFile) { input =>
      val properties = new Properties()
      properties.load(input)
      properties
    }
  }

  private def asFile(propertiesFile: URL): (URI, Seq[String]) = {
    val path = propertiesFile.getPath
    val end = path.indexOf('!')
    val jarPath =
      if (end == -1) {
        path
      } else {
        path.substring(0, end)
      }

    val properties = loadProperties(propertiesFile)
    val customWarts = properties.asScala.values.toSeq

    (new URI(jarPath), customWarts)
  }

  private def filterWartLibraries(classpath: Seq[File]): Seq[(URI, Seq[String])] = {
    val loader = new URLClassLoader(Path.toURLs(classpath))
    loader.getResources("warts.properties").asScala.map(asFile).toSeq
  }

  private def ignoredCompile = Seq(
    // This is too useful to disallow.
    Wart.DefaultArguments,
    // https://github.com/puffnfresh/wartremover/issues/192
    Wart.NoNeedForMonad,
    // Scala typechecker deficiencies cause Nothing to be inferred in
    // polymorphic functions.
    Wart.Nothing,
    // Seq(TyCon1, TyCon2) infers Product instead of the ADT the TyCons
    // are a part of.
    Wart.Product,
    // Too many false positives.
    Wart.NonUnitStatements,
    // The same reason as Product.
    Wart.Serializable,
    // https://github.com/puffnfresh/wartremover/issues/182
    Wart.Throw,
    // Already checked by scalastyle.
    Wart.Var
  )

  private def ignoredTest = ignoredCompile ++ Seq(
    Wart.Any,
    Wart.AsInstanceOf,
    Wart.IsInstanceOf,
    Wart.Null,
    Wart.OptionPartial,
    Wart.Throw
  )

  // Enable wart removers.
  override val projectSettings = Seq(
    scalacOptions in (Compile, compile) ++= {
      val pluginClasspath = update.value.matching(configurationFilter(Configurations.CompilerPlugin.name))
      filterWartLibraries(pluginClasspath) flatMap {
        case (cp, customWarts) =>
          customWarts.map { clazz =>
            "-P:wartremover:traverser:" + clazz
          } :+ "-P:wartremover:cp:" + cp
      }
    },
    wartremoverErrors in (Compile, compile) := Warts.allBut(ignoredCompile: _*),
    wartremoverErrors in (Test, compile) := Warts.allBut(ignoredTest: _*)
  )

}
