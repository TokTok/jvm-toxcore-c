package im.tox.client.callbacks

import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

abstract class IdLogging(id: Int) {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  protected def logInfo(message: String): Unit = {
    logger.info(s"[$id] $message")
  }

  protected def logWarn(message: String): Unit = {
    logger.warn(s"[$id] $message")
  }

}
