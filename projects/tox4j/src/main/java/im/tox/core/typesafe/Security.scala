package im.tox.core.typesafe

sealed trait Security
object Security {

  final class Sensitive private extends Security
  final class NonSensitive private extends Security

  abstract class Evidence[+S <: Security]

  abstract class EvidenceCompanion[+S <: Security] {
    protected implicit object $SecurityProof extends Evidence[S]
  }

}
