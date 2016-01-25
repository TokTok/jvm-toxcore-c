package im.tox.core.error

sealed abstract class DecoderError {
  val exception = new Throwable(toString)
}
object DecoderError {
  final case class Unimplemented(message: String) extends DecoderError
  final case class InvalidFormat(message: String) extends DecoderError
  final case class DecryptionError() extends DecoderError
}
