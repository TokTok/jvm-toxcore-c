package im.tox.tox4j.core

import im.tox.tox4j.core.options.ToxOptions

abstract class ToxCoreFactory {

  def withTox[R](options: ToxOptions)(f: ToxCore => R): R

  final def withTox[R](tox: ToxCore)(f: ToxCore => R): R = {
    try {
      f(tox)
    } finally {
      tox.close()
    }
  }

  final def withToxN[R](
    count: Int,
    options: ToxOptions = ToxOptions(),
    initial: List[ToxCore] = Nil
  )(
    f: List[ToxCore] => R
  ): R = {
    if (count == 0) {
      f(initial)
    } else {
      withTox[R](options) { tox =>
        withToxN(count - 1, options, tox :: initial)(f)
      }
    }
  }

  final def withToxN[R](
    options: List[ToxOptions]
  )(
    f: List[ToxCore] => R
  ): R = {
    def go(options: List[ToxOptions], toxes: List[ToxCore]): R = {
      options match {
        case Nil => f(toxes.reverse)
        case opts :: tail =>
          withTox[R](opts) { tox =>
            go(tail, tox :: toxes)
          }
      }
    }
    go(options, Nil)
  }

}
