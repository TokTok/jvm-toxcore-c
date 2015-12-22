package im.tox.client

import java.net.URL

import com.typesafe.scalalogging.Logger
import im.tox.client.proto.Profile
import im.tox.tox4j.core.ToxCore
import im.tox.tox4j.core.data.{ToxNickname, ToxPublicKey, ToxStatusMessage}
import im.tox.tox4j.impl.jni.ToxCoreEventDispatch
import im.tox.tox4j.testing.GetDisjunction._
import org.slf4j.LoggerFactory

case object ProfileManager {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  val profileBaseUrl = sys.env.get("PROFILE_URL").map(new URL(_))

  def loadProfile(id: Int, tox: ToxCore): Profile = {
    profileBaseUrl match {
      case None =>
        logger.info(s"[$id] Created new profile for ${tox.getPublicKey}")
        Profile(
          name = tox.getName.toString,
          statusMessage = tox.getStatusMessage.toString,
          nospam = tox.getNospam,
          status = ToxCoreEventDispatch.convert(tox.getStatus),
          friendKeys = tox.getFriendNumbers.map(tox.getFriendPublicKey).map(_.toHexString)
        )

      case Some(baseUrl) =>
        val url = new URL(baseUrl, tox.getPublicKey.toHexString)
        logger.info(s"[$id] Loading profile from $url")
        val input = url.openStream()
        try {
          val profile = Profile.parseFrom(input)
          tox.setName(ToxNickname(profile.name.getBytes))
          tox.setStatusMessage(ToxStatusMessage(profile.statusMessage.getBytes))
          tox.setNospam(profile.nospam)
          tox.setStatus(ToxCoreEventDispatch.convert(profile.status))
          logger.info(s"[$id] Adding ${profile.friendKeys.length} friends from saved friend list")
          profile.friendKeys.foreach(key => logger.debug(s"[$id] - $key"))
          profile.friendKeys.map(ToxPublicKey.fromHexString(_).get).foreach(tox.addFriendNorequest)
          logger.info(s"[$id] Successfully read profile for ${tox.getPublicKey}")
          profile
        } finally {
          input.close()
        }
    }
  }

}
