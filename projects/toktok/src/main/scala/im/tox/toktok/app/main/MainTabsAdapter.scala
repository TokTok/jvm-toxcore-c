package im.tox.toktok.app.main

import android.content.Context
import android.support.v4.app.{Fragment, FragmentManager, FragmentStatePagerAdapter}
import im.tox.toktok.R
import im.tox.toktok.app.main.chats.ChatsFragment
import im.tox.toktok.app.main.friends.FriendsFragment

final class MainTabsAdapter(fm: FragmentManager, context: Context) extends FragmentStatePagerAdapter(fm) {

  private val items = Map(
    0 -> ((new FriendsFragment, R.string.home_tabs_friends)),
    1 -> ((new ChatsFragment, R.string.home_tabs_chats))
  )

  override def getItem(i: Int): Fragment = items(i)._1
  override def getCount: Int = items.size
  override def getPageTitle(i: Int): CharSequence = context.getResources.getString(items(i)._2)

}
