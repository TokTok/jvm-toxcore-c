package im.tox.tox4j.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * Exception to be thrown when a method is invoked on a tox instance that has
 * been closed.
 *
 * @author Simon Levermann (sonOfRa)
 */
public final class ToxKilledException extends RuntimeException {
  public ToxKilledException(@NotNull String message) {
    super(message);
  }
}
