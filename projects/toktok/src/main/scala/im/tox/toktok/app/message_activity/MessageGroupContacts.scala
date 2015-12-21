package im.tox.toktok.app.message_activity

import android.content.Intent
import android.graphics.PixelFormat
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.TypedValue
import android.view.View.OnClickListener
import android.view.ViewGroup.LayoutParams
import android.view.{MenuItem, View, WindowManager}
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration
import im.tox.toktok.TypedResource._
import im.tox.toktok.app.Friend
import im.tox.toktok.app.call.CallActivity
import im.tox.toktok.app.main.friends.{FriendItemClicks, FriendsRecyclerHeaderAdapter}
import im.tox.toktok.app.new_message.NewMessageActivity
import im.tox.toktok.{R, TContext, TR}

import scala.collection.mutable.ListBuffer

final class MessageGroupContacts extends AppCompatActivity with FriendItemClicks {

  var adapter: FriendsRecyclerHeaderAdapter = null
  var colorPrimary: Int = 0
  var colorStatus: Int = 0

  protected override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_message_group_members)

    val bundle = getIntent.getExtras

    colorPrimary = bundle.getInt("colorPrimary")
    colorStatus = bundle.getInt("colorPrimaryStatus")

    val mToolbar = this.findView(TR.message_group_members_toolbar)
    mToolbar.setTitle(getResources.getString(R.string.message_group_contacts))
    mToolbar.setBackgroundColor(colorPrimary)
    getWindow.setStatusBarColor(colorStatus)

    setSupportActionBar(mToolbar)
    getSupportActionBar.setDisplayHomeAsUpEnabled(true)

    val mFAB = this.findView(TR.message_group_members_fab)

    mFAB.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = {

        val addContactView: Intent = new Intent(MessageGroupContacts.this, classOf[NewMessageActivity])
        addContactView.putExtras(bundle)
        startActivity(addContactView)

      }
    })

    val mRecycler = this.findView(TR.message_group_members_recycler)

    val friends = ListBuffer(Friend.lorem, Friend.john)

    val mLayoutManager = new LinearLayoutManager(getBaseContext)
    mRecycler.setLayoutManager(mLayoutManager)

    adapter = new FriendsRecyclerHeaderAdapter(friends, this)
    mRecycler.setAdapter(adapter)
    mRecycler.addItemDecoration(new StickyRecyclerHeadersDecoration(adapter))

  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId match {
      case android.R.id.home =>
        finish()
        true
      case _ =>
        super.onOptionsItemSelected(item)
    }
  }

  def startOverLayFriend(friendPosition: Int): Unit = {
    val layout = getLayoutInflater.inflate(TR.layout.overlay_contacts, null)
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
    val window = this.getSystemService(TContext.WINDOW_SERVICE)

    window.addView(layout, params)

    val tv = new TypedValue()
    val actionBarHeight =
      if (getTheme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
        TypedValue.complexToDimensionPixelSize(tv.data, getResources.getDisplayMetrics)
      } else {
        0
      }

    layout.start(this, adapter.getItem(friendPosition), actionBarHeight)
  }

  def startCall(friendPosition: Int): Unit = {
    val friend = adapter.getItem(friendPosition)

    val bundle = new Bundle
    bundle.putString("contactName", friend.userName)
    bundle.putInt("contactColorPrimary", friend.color)
    bundle.putInt("contactPhotoReference", friend.photoReference)

    val newIntent = new Intent(this, classOf[CallActivity])
    newIntent.putExtras(bundle)
    startActivity(newIntent)
  }

  def startMessage(friendPosition: Int): Unit = {
    val friend = adapter.getItem(friendPosition)

    val bundle = new Bundle
    bundle.putString("messageTitle", friend.userName)
    bundle.putInt("contactColorPrimary", friend.color)
    bundle.putInt("contactColorStatus", friend.secondColor)
    bundle.putInt("imgResource", friend.photoReference)
    bundle.putInt("messageType", 0)

    val newIntent = new Intent(this, classOf[MessageActivity])
    newIntent.putExtras(bundle)
    startActivity(newIntent)
  }

}
