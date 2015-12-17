package im.tox.client

import java.io.{File, FileInputStream, FileOutputStream}

import com.typesafe.scalalogging.Logger
import im.tox.client.proto.Profile
import im.tox.tox4j.core.ToxCore
import im.tox.tox4j.core.data.{ToxNickname, ToxPublicKey, ToxStatusMessage}
import im.tox.tox4j.impl.jni.ToxCoreImpl
import im.tox.tox4j.testing.GetDisjunction._
import org.slf4j.LoggerFactory

import scala.util.Try

case object ProfileManager {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  private val savePath = Seq(new File("tools/toxsaves"), new File("projects/tox4j/tools/toxsaves")).find(_.exists)

  def saveProfile(tox: ToxCore, profile: Profile): Unit = {
    savePath.foreach { savePath =>
      val output = new FileOutputStream(new File(savePath, tox.getPublicKey.toHexString))
      try {
        profile.writeTo(output)
        logger.info(s"Saved profile for ${tox.getPublicKey}")
      } finally {
        output.close()
      }
    }
  }

  def saveOnChange(tox: ToxCore, oldProfile: Profile)(state: ToxClientState): ToxClientState = {
    if (oldProfile != state.profile) {
      saveProfile(tox, state.profile)
    }
    state
  }

  def loadProfile(id: Int, tox: ToxCore): Profile = {
    Try {
      val input = new FileInputStream(new File(savePath.get, tox.getPublicKey.toHexString))
      try {
        val profile = Profile.parseFrom(input)
        tox.setName(ToxNickname(profile.name.getBytes))
        tox.setStatusMessage(ToxStatusMessage(profile.statusMessage.getBytes))
        tox.setNospam(profile.nospam)
        tox.setStatus(ToxCoreImpl.convert(profile.status))
        logger.info(s"[$id] Adding ${profile.friendKeys.length} friends from saved friend list")
        profile.friendKeys.foreach(key => logger.debug(s"[$id] - $key"))
        profile.friendKeys.map(ToxPublicKey.fromHexString(_).get).foreach(tox.addFriendNorequest)
        logger.info(s"[$id] Successfully read profile for ${tox.getPublicKey}")
        profile
      } finally {
        input.close()
      }
    } getOrElse {
      val profile = Profile(
        name = tox.getName.toString,
        statusMessage = tox.getStatusMessage.toString,
        nospam = tox.getNospam,
        status = ToxCoreImpl.convert(tox.getStatus),
        friendKeys = tox.getFriendNumbers.map(tox.getFriendPublicKey).map(_.toHexString)
      )
      saveProfile(tox, profile)
      logger.info(s"[$id] Created new profile for ${tox.getPublicKey}")
      profile
    }
  }

}
