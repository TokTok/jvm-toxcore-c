package im.tox.toktok.app.views.widgets

import android.content.res.ColorStateList
import android.graphics.{BitmapFactory, Color, PixelFormat}
import android.support.design.widget.{CollapsingToolbarLayout, FloatingActionButton, TabLayout}
import android.support.v4.app.FragmentActivity
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.util.TypedValue
import android.view.View.OnClickListener
import android.view.ViewGroup.LayoutParams
import android.view.{View, WindowManager}
import android.widget.{ImageView, TextView}
import im.tox.toktok.R
import im.tox.toktok.app.models.Friend
import im.tox.toktok.app.utils.ColorUtils
import im.tox.toktok.app.view.widgets.LayoutOnTop
import im.tox.toktok.app.views.adapters.ProfileTabsAdapter
import io.realm.Realm

class FriendLayout(activity: FragmentActivity, friend: Friend, db: Realm, callback: FriendLayoutCallback) {

  var layoutOnTop = new LayoutOnTop(activity)

  val params = new WindowManager.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, PixelFormat.TRANSLUCENT)
  val contactLayout = activity.getLayoutInflater.inflate(R.layout.fragment_contact, layoutOnTop)

  // Set Toolbar Height in order to get the status bar painted as well

  val mToolbar = contactLayout.findViewById(R.id.toolbar).asInstanceOf[Toolbar]
  val mCollapsing = contactLayout.findViewById(R.id.collapsingLayout).asInstanceOf[CollapsingToolbarLayout]
  val mShadowView = contactLayout.findViewById(R.id.toolbar_shadow)
  contactLayout.findViewById(R.id.collapsingLayout).asInstanceOf[CollapsingToolbarLayout].setStatusBarScrimColor(Color.BLACK)

  var actionBarHeight = 0

  val tv = new TypedValue()

  if (activity.getTheme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
    actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, activity.getResources.getDisplayMetrics)
  }

  val statusbarHeight = getStatusBarHeight

  mToolbar.getLayoutParams.height = actionBarHeight + statusbarHeight
  mToolbar.setPadding(0, statusbarHeight, 0, 0)

  mToolbar.inflateMenu(R.menu.contact_profile)

  val friendColor = friend.getColor

  mShadowView.setBackgroundColor(friendColor)
  mCollapsing.setContentScrimColor(friendColor)

  // Setting Up the ViewPager

  var mViewPager = contactLayout.findViewById(R.id.viewpager).asInstanceOf[ViewPager]
  val mTabs = contactLayout.findViewById(R.id.tabs).asInstanceOf[TabLayout]
  mViewPager.setAdapter(new ProfileTabsAdapter(activity.getSupportFragmentManager, statusbarHeight))
  mTabs.setupWithViewPager(mViewPager)
  mTabs.setSelectedTabIndicatorColor(friendColor)

  // Setting the Header Image

  val mHeaderImage = contactLayout.findViewById(R.id.header_image).asInstanceOf[ImageView]

  val id = friend.getToxID

  var bitmap = BitmapFactory.decodeFile(activity.getFilesDir + "/" + id + ".png")

  mHeaderImage.setImageBitmap(bitmap)

  // Setting the Toolbar Information

  val mTitle = contactLayout.findViewById(R.id.title).asInstanceOf[TextView]
  mTitle.setText(friend.getName)

  val mSubTitle = contactLayout.findViewById(R.id.subtitle).asInstanceOf[TextView]
  mSubTitle.setText(friend.getStatusMessage)

  // Setup the Fav Button

  var mFav = contactLayout.findViewById(R.id.fab).asInstanceOf[FloatingActionButton]

  if (friend.isFav) {
    mFav.setBackgroundTintList(ColorStateList.valueOf(ColorUtils.mixTwoColors(friend.getColor, Color.WHITE, 0.75f)))
    mFav.setImageResource(R.drawable.fab_fav)
  }

  mFav.setOnClickListener(new OnClickListener {
    override def onClick(v: View): Unit = {

      db.beginTransaction()

      if (friend.isFav) {
        friend.setFav(false)
        mFav.setBackgroundTintList(null)
        mFav.setImageResource(R.drawable.fab_fav_line)
      } else {
        friend.setFav(true)
        mFav.setBackgroundTintList(ColorStateList.valueOf(ColorUtils.mixTwoColors(friend.getColor, Color.WHITE, 0.75f)))
        mFav.setImageResource(R.drawable.fab_fav)
      }

      db.commitTransaction()

    }
  })

  //

  val mNestedScroll = contactLayout.findViewById(R.id.nested_layout)

  activity.addContentView(layoutOnTop, params)

  def getStatusBarHeight: Int = {
    var result = 0
    val resourceId = activity.getResources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
      result = activity.getResources.getDimensionPixelSize(resourceId)
    }
    result
  }

}

trait FriendLayoutCallback {
  def onClose()
}