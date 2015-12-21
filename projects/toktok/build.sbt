// General settings
organization := "im.tox"
name         := "toktok"
scalaVersion := "2.11.7"

import sbt.tox4j._
import sbt.tox4j.lint._
CodeFormat.moduleSettings
Scalastyle.moduleSettings
Findbugs.moduleSettings

// Tox4j library.
resolvers += "Tox4j snapshots" at "https://tox4j.github.io/repositories/snapshots/"
resolvers += Resolver.sonatypeRepo("snapshots")
resolvers += Resolver.bintrayRepo("realm", "maven")

// Dependencies.
libraryDependencies ++= Seq(
  "com.android.support" % "appcompat-v7" % "23.1.1",
  "com.android.support" % "recyclerview-v7" % "23.1.1",
  "com.android.support" % "cardview-v7" % "23.1.1",
  "com.android.support" % "palette-v7" % "23.1.1",
  "com.android.support" % "design" % "23.1.1",

  // db library
  "io.realm" % "realm-android" % "0.86.0",
  // tab bar library
  "com.jpardogo.materialtabstrip" % "library" % "1.1.0",
  // sticky header of the recycler views
  "com.tonicartos" % "superslim" % "0.4.13",
  // circular images support
  "de.hdodenhof" % "circleimageview" % "1.3.0",
  // image loading library
  "com.squareup.picasso" % "picasso" % "2.5.2",
  // ripple effect support for the older
  "com.balysv" % "material-ripple" % "1.0.2",

  "com.jayway.android.robotium" % "robotium-solo" % "5.5.3",

  "org.slf4j" % "slf4j-android" % "1.7.13",
  organization.value %% "tox4j" % version.value
)

proguardOptions in Android ++= Seq(
  "-keep @io.realm.annotations.RealmModule class *",
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
  "-dontwarn io.realm.**",
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
