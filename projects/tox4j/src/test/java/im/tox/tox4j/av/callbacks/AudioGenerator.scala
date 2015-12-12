package im.tox.tox4j.av.callbacks

final case class AudioGenerator(
    sample: Int => Int,
    length: Int = 128000
) {

  def nextFrame16(t: Int, frameSize: Int): Array[Short] = {
    val frame = Array.ofDim[Short](frameSize)
    for (i <- frame.indices) {
      frame(i) = sample(t + i).toShort
    }
    frame
  }

}

object AudioGenerator {

  private def generator(length: Int)(sample: (Int, Int) => Int): AudioGenerator = {
    AudioGenerator(t => sample(t, length), length)
  }

  private def generator(sample: Int => Int): AudioGenerator = {
    AudioGenerator(sample)
  }

  val ItCrowd = generator(96000 + 4000 * 8) { (t0, length) =>
    // Period
    val t = t0 % length

    val a = 1 / (128000 - t)
    val b = if (t > 96000) {
      t % 4000 * ("'&(&*$,*".charAt(t % 96000 / 4000) - 32)
    } else {
      {
        t % 2000 * {
          "$$$&%%%''''%%%'&".charAt(t % 32000 / 2000) - 32 - {
            if (t > 28000 && t < 32000) 2 else 0
          }
        }
      } / {
        if (t % 8000 < 4000) 2 else 1
      }
    }
    (a | b) << 8
  }

  val MortalKombat = generator { t =>
    val a = {
      2 * t % 4000 * {
        "!!#!$!%$".charAt(t % 16000 / 2000) - 32 + (if (t % 64000 > 32000) 7 else 0)
      }
    } * {
      if (t % 32000 > 16000) 2 else 1
    }
    val b = (if (t % 128000 > 64000) 2 * t else 0) * {
      "%%%'%+''%%%$%+))%%%%'+'%$%%%$%%%$%%".charAt(t % 70000 / 2000) - 36
    }
    (a | b) << 8
  }

  val Sine = generator { t =>
    (Math.sin(t / (10000d / t)) * 128).toInt << 8
  }

  val Sine2 = generator { t0 =>
    val t =
      if (t0 % 2 == 0) {
        1
      } else {
        t0
      }
    (Math.sin(t / (10000d / t)) * 128).toInt << 8
  }

  val Sine3 = generator { t =>
    ((t & 0xF) * ((0 - t) & 0xF) * (((t & 0x10) >> 3) - 1) * 0x80 / 0x41 + 0x80) << 7
  }

  // Selected audio generator for tests.
  val Selected = Sine3

}
