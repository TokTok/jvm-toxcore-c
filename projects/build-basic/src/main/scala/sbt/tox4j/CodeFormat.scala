package sbt.tox4j

import com.typesafe.sbt.SbtScalariform.{ScalariformKeys, scalariformSettings}

import scalariform.formatter.preferences._

object CodeFormat extends OptionalPlugin {
  object Keys

  override val moduleSettings = scalariformSettings ++ Seq(
    ScalariformKeys.preferences := ScalariformKeys.preferences.value
      .setPreference(AlignSingleLineCaseStatements, true)
      .setPreference(DoubleIndentClassDeclaration, true)
  )
}
