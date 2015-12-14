package im.tox.tox4j.core

import im.tox.tox4j.core.options.ToxOptions

abstract class ToxCoreFactory {

  def withTox[ToxCoreState, R](options: ToxOptions)(f: ToxCore[ToxCoreState] => R): R

  final def withTox[ToxCoreState, R](tox: ToxCore[ToxCoreState])(f: ToxCore[ToxCoreState] => R): R = {
    try {
      f(tox)
    } finally {
      tox.close()
    }
  }

  final def withToxN[ToxCoreState, R](
    count: Int,
    options: ToxOptions = ToxOptions(),
    initial: List[ToxCore[ToxCoreState]] = Nil
  )(
    f: List[ToxCore[ToxCoreState]] => R
  ): R = {
    if (count == 0) {
      f(initial)
    } else {
      withTox[ToxCoreState, R](options) { tox =>
        withToxN(count - 1, options, tox :: initial)(f)
      }
    }
  }

  final def withToxN[ToxCoreState, R](
    options: List[ToxOptions]
  )(
    f: List[ToxCore[ToxCoreState]] => R
  ): R = {
    def go(options: List[ToxOptions], toxes: List[ToxCore[ToxCoreState]]): R = {
      options match {
        case Nil => f(toxes.reverse)
        case opts :: tail =>
          withTox[ToxCoreState, R](opts) { tox =>
            go(tail, tox :: toxes)
          }
      }
    }
    go(options, Nil)
  }

}
