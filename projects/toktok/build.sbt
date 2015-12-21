// General settings
organization := "im.tox"
name         := "toktok"
scalaVersion := "2.11.7"

import sbt.tox4j._
import sbt.tox4j.lint._
CodeFormat.moduleSettings
Scalastyle.moduleSettings

// Tox4j library.
resolvers += "Tox4j snapshots" at "https://tox4j.github.io/repositories/snapshots/"
resolvers += Resolver.sonatypeRepo("snapshots")

// Dependencies.
libraryDependencies ++= Seq(
  "com.android.support" % "appcompat-v7" % "23.1.1",
  "com.android.support" % "recyclerview-v7" % "23.1.1",
  "com.android.support" % "cardview-v7" % "23.1.1",
  "com.android.support" % "palette-v7" % "23.1.1",
  "com.android.support" % "design" % "23.1.1",

  "de.hdodenhof" % "circleimageview" % "1.3.0",
  "com.sothree.slidinguppanel" % "library" % "3.0.0",
  "com.tonicartos" % "superslim" % "0.4.13",
  "com.timehop.stickyheadersrecyclerview" % "library" % "0.4.1",

  "com.jayway.android.robotium" % "robotium-solo" % "5.5.3",

  "org.slf4j" % "slf4j-android" % "1.7.13",
  organization.value %% "tox4j" % version.value
)

proguardOptions in Android ++= Seq(
  "-keep class * extends android.support.design.widget.CoordinatorLayout$Behavior { <init>(...); }",
  "-keep class * extends android.test.ActivityInstrumentationTestCase2 { public *; }",

  "-keepattributes EnclosingMethod",
  "-keepattributes InnerClasses",
  "-keepattributes Signature",

  "-optimizationpasses 3",
  "-optimizations *",
  "-allowaccessmodification",

  "-dontwarn com.google.common.**",
  "-dontwarn com.squareup.picasso.OkHttpDownloader",
  "-dontwarn org.threeten.bp.chrono.JapaneseEra",
  "-dontwarn org.xmlpull.v1.**",
  "-dontwarn scala.xml.**",
  "-dontwarn scalaz.**",
  "-dontwarn scodec.**"
)

proguardCache ++= Seq(
  "com.google.common.collect",
  "com.google.protobuf",
  "scalaz",
  "scodec.bits",
  "scodec.codecs",
  "shapeless"
)

if (sys.env.contains("PROTIFY")) {
  new Def.SettingList(protifySettings)
} else {
  sys.props("maximum.inlined.code.length") = "8"

  // Enable optimisation by removing default proguard options that were added
  // by android-sdk-plugin.
  proguardConfig ~= (_.filterNot(Seq(
    "-dontoptimize",
    "-verbose"
  ).contains))
}
