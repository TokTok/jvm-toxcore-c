package im.tox.tox4j.bench.picklers

object Implicits {
  implicit def classPickler[T]: ClassPickler[T] = new ClassPickler[T]
}
