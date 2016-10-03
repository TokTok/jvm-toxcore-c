package im.tox.documentation

sealed trait Doc {
  def reference: String
}

object Doc {
  final case class Ref(reference: String) extends Doc
  final case class Text(reference: String, body: String) extends Doc
}
