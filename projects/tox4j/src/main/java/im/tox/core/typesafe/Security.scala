package im.tox.core.typesafe

sealed trait Security
object Security {
  final class Sensitive private extends Security
  final class NonSensitive private extends Security
}
