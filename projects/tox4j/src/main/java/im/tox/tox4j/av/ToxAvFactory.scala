package im.tox.tox4j.av

import im.tox.tox4j.core.ToxCore

abstract class ToxAvFactory {

  def withToxAv[ToxCoreState, R](tox: ToxCore[ToxCoreState])(f: ToxAv[ToxCoreState] => R): R

  final def withToxAv[ToxCoreState, R](av: ToxAv[ToxCoreState])(f: ToxAv[ToxCoreState] => R): R = {
    try {
      f(av)
    } finally {
      av.close()
    }
  }

  def withToxAvN[ToxCoreState, R](
    toxes: List[ToxCore[ToxCoreState]],
    initial: List[(ToxCore[ToxCoreState], ToxAv[ToxCoreState])] = Nil
  )(
    f: List[(ToxCore[ToxCoreState], ToxAv[ToxCoreState])] => R
  ): R = {
    toxes match {
      case Nil => f(initial)
      case tox :: tail =>
        withToxAv[ToxCoreState, R](tox) { av =>
          withToxAvN(tail, (tox, av) :: initial)(f)
        }
    }
  }

}
