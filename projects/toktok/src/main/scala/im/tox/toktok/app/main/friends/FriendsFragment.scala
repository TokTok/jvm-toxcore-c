package im.tox.toktok.app.main.friends

import android.content.Intent
import android.graphics.PixelFormat
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.TypedValue
import android.view.ViewGroup.LayoutParams
import android.view.{LayoutInflater, ViewGroup, WindowManager}
import android.widget.LinearLayout
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration
import im.tox.toktok.TypedResource._
import im.tox.toktok.app.call.CallActivity
import im.tox.toktok.app.message_activity.MessageActivity
import im.tox.toktok.app.{Friend, MainActivityHolder}
import im.tox.toktok.{TContext, TR}

final class FriendsFragment extends Fragment with FriendItemClicks {

  private var mFriends_Recycler_Adapter: FriendsRecyclerHeaderAdapter = null

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedState: Bundle): LinearLayout = {
    val view = inflater.inflate(TR.layout.fragment_home_friends, container, false)
    val activity = getActivity.asInstanceOf[AppCompatActivity]

    //Recycler View

    val mFriends_Recycler = view.findView(TR.home_friends_recycler)

    val mLayoutManager = new LinearLayoutManager(activity.getBaseContext)
    mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL)
    mFriends_Recycler.setLayoutManager(mLayoutManager)

    val friends = for {
      _ <- 0 to 5
      friend <- Seq(
        Friend.bart,
        Friend.lorem,
        Friend.jane,
        Friend.john
      )
    } yield {
      friend
    }

    mFriends_Recycler_Adapter = new FriendsRecyclerHeaderAdapter(friends, this)

    mFriends_Recycler.setAdapter(mFriends_Recycler_Adapter)
    mFriends_Recycler.setHasFixedSize(true)
    mFriends_Recycler.addItemDecoration(new StickyRecyclerHeadersDecoration(mFriends_Recycler_Adapter))

    view
  }

  def startOverLayFriend(friendPosition: Int): Unit = {
    val layout = getActivity.getLayoutInflater.inflate(TR.layout.overlay_contacts, null)
    val params = new WindowManager.LayoutParams(
      LayoutParams.MATCH_PARENT,
      LayoutParams.MATCH_PARENT,
      WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG,
      WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
      PixelFormat.TRANSLUCENT
    )
    val window = getActivity.getSystemService(TContext.WINDOW_SERVICE)

    getActivity.asInstanceOf[MainActivityHolder].setActiveActivity(layout)

    window.addView(layout, params)

    var actionBarHeight = 0

    val tv = new TypedValue()
    if (getActivity.getTheme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
      actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources.getDisplayMetrics)
    }

    layout.start(getActivity, mFriends_Recycler_Adapter.getItem(friendPosition), actionBarHeight)
  }

  def startCall(friendPosition: Int): Unit = {
    val friend = mFriends_Recycler_Adapter.getItem(friendPosition)

    val bundle = new Bundle
    bundle.putString("contactName", friend.userName)
    bundle.putInt("contactColorPrimary", friend.color)
    bundle.putInt("contactPhotoReference", friend.photoReference)

    val newIntent = new Intent(getActivity, classOf[CallActivity])
    newIntent.putExtras(bundle)
    getActivity.startActivity(newIntent)
  }

  def startMessage(friendPosition: Int): Unit = {
    val friend = mFriends_Recycler_Adapter.getItem(friendPosition)

    val bundle = new Bundle
    bundle.putString("messageTitle", friend.userName)
    bundle.putInt("contactColorPrimary", friend.color)
    bundle.putInt("contactColorStatus", friend.secondColor)
    bundle.putInt("imgResource", friend.photoReference)
    bundle.putInt("messageType", 0)

    val newIntent = new Intent(getActivity, classOf[MessageActivity])
    newIntent.putExtras(bundle)
    getActivity.startActivity(newIntent)
  }

}
