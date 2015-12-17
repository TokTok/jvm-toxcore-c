package im.tox.tox4j.av

import im.tox.tox4j.core.ToxCore

abstract class ToxAvFactory {

  def withToxAv[R](tox: ToxCore)(f: ToxAv => R): R

  final def withToxAv[R](av: ToxAv)(f: ToxAv => R): R = {
    try {
      f(av)
    } finally {
      av.close()
    }
  }

  def withToxAvN[R](
    toxes: List[ToxCore],
    initial: List[(ToxCore, ToxAv)] = Nil
  )(
    f: List[(ToxCore, ToxAv)] => R
  ): R = {
    toxes match {
      case Nil => f(initial)
      case tox :: tail =>
        withToxAv(tox) { av =>
          withToxAvN(tail, (tox, av) :: initial)(f)
        }
    }
  }

}
