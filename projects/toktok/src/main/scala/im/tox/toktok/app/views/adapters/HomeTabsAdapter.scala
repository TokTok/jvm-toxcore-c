package im.tox.toktok.app.views.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.{Fragment, FragmentManager, FragmentPagerAdapter}
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.ImageView
import com.astuetz.PagerSlidingTabStrip.CustomTabProvider
import im.tox.toktok.R
import im.tox.toktok.app.fragments.{HomeFriends, HomeCallLog, HomeChats}
import io.realm.Realm

class HomeTabsAdapter(db: Realm, fm: FragmentManager, context: Context) extends FragmentPagerAdapter(fm) with CustomTabProvider {

  private final val ICONS = Array(R.drawable.menu_friends, R.drawable.menu_chats, R.drawable.menu_call)

  def getCount: Int = {
    ICONS.length
  }

  override def getCustomTabView(viewGroup: ViewGroup, position: Int): View = {
    val tab = LayoutInflater.from(context).inflate(R.layout.tab_bar_single, viewGroup, false)
    val logo_tab = tab.findViewById(R.id.tab_icon).asInstanceOf[ImageView]
    logo_tab.setImageResource(ICONS(position))
    tab
  }

  def getItem(position: Int): Fragment = {
    position match {
      case 0 => new HomeFriends(db)
      case 1 => new HomeChats
      case 2 => new HomeCallLog
    }
  }

  override def tabSelected(view: View): Unit = {
    val icon = view.findViewById(R.id.tab_icon).asInstanceOf[ImageView]
    icon.setAlpha(1.0f)
  }

  override def tabUnselected(view: View): Unit = {
    val icon = view.findViewById(R.id.tab_icon).asInstanceOf[ImageView]
    icon.setAlpha(0.37f)
  }

}