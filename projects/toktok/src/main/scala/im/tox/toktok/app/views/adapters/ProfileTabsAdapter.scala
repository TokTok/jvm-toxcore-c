package im.tox.toktok.app.views.adapters

import android.support.v4.app.{Fragment, FragmentManager, FragmentPagerAdapter}
import im.tox.toktok.app.fragments.{FriendProfile, HomeChats}

class ProfileTabsAdapter(fm: FragmentManager, statusBarHeight: Int) extends FragmentPagerAdapter(fm) {

  private final val TITLES = Array("Profile", "Call log")

  override def getCount: Int = {
    TITLES.length
  }

  override def getItem(position: Int): Fragment = {
    position match {
      case 0 => new FriendProfile
      case 1 => new HomeChats

    }
  }

  override def getPageTitle(position: Int): CharSequence = {
    TITLES(position)
  }

}