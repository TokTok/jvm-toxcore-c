package im.tox.toktok.app.activities

import android.app.ActivityManager.TaskDescription
import android.content.res.ColorStateList
import android.graphics.drawable.TransitionDrawable
import android.graphics.{BitmapFactory, Color}
import android.os.Bundle
import android.support.design.widget.{CollapsingToolbarLayout, FloatingActionButton, TabLayout}
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.transition.Fade
import android.util.TypedValue
import android.view.{Window, View}
import android.view.View.OnClickListener
import android.widget.{ImageView, TextView}
import im.tox.toktok.R
import im.tox.toktok.app.models.Friend
import im.tox.toktok.app.utils.ColorUtils
import im.tox.toktok.app.view.widgets.LayoutOnTop
import im.tox.toktok.app.view.widgets.LayoutOnTop.LayoutOnTopCallback
import im.tox.toktok.app.views.adapters.ProfileTabsAdapter
import io.realm.Realm

class ContactDetails extends AppCompatActivity {

  var db: Realm = _
  var mLayoutOnTop: LayoutOnTop = _
  var mTaskDescription: TaskDescription = _

  override protected def onCreate(savedInstanceState: Bundle): Unit = {

    super.onCreate(savedInstanceState)
    setContentView(R.layout.fragment_contact)

    getWindow.setStatusBarColor(Color.TRANSPARENT);
    val friendID = getIntent.getStringExtra("friendID")

    db = Realm.getInstance(this)
    val friend = db.where(classOf[Friend]).contains("toxID", friendID).findAll().first()

    // Setup the Layout on Top

    mLayoutOnTop = findViewById(R.id.layout_on_top).asInstanceOf[LayoutOnTop]

    // Setup Header and Toolbars

    mLayoutOnTop.setScrollView(findViewById(R.id.viewpager))
    mLayoutOnTop.setLayoutOnTopCallback(new LayoutOnTopCallback {
      override def onClose(): Unit = {
        finish()
      }
    })

    val mToolbar = findViewById(R.id.toolbar).asInstanceOf[Toolbar]
    val mCollapsing = findViewById(R.id.collapsingLayout).asInstanceOf[CollapsingToolbarLayout]
    val mShadowView = findViewById(R.id.toolbar_shadow)
    val mHeaderImage = findViewById(R.id.header_image).asInstanceOf[ImageView]

    mToolbar.inflateMenu(R.menu.contact_profile)

    val bitmap = BitmapFactory.decodeFile(getFilesDir + "/" + friendID + ".png")
    mHeaderImage.setImageBitmap(bitmap)

    var toolbarHeight = 0

    val tv = new TypedValue()
    if (getTheme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
      toolbarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources.getDisplayMetrics)
    }

    val statusBarHeight = getStatusBarHeight

    mToolbar.getLayoutParams.height = toolbarHeight + statusBarHeight
    mToolbar.setPadding(0, statusBarHeight, 0, 0)

    // Setting Up the ViewPager

    val mViewPager = findViewById(R.id.viewpager).asInstanceOf[ViewPager]
    val mTabs = findViewById(R.id.tabs).asInstanceOf[TabLayout]
    mViewPager.setAdapter(new ProfileTabsAdapter(getSupportFragmentManager, 0))

    mTabs.setupWithViewPager(mViewPager)

    // Setting Up the UI Colors

    val friendColor = friend.getColor

    mShadowView.setBackgroundColor(friendColor)
    mCollapsing.setContentScrimColor(friendColor)
    mTabs.setSelectedTabIndicatorColor(friendColor)

    // Setting Up the Titles and Subtitles

    val mTitle = findViewById(R.id.title).asInstanceOf[TextView]
    mTitle.setText(friend.getName)

    val mSubTitle = findViewById(R.id.subtitle).asInstanceOf[TextView]
    mSubTitle.setText(friend.getStatusMessage)

    // Setting Up the Favorite Friend Button

    val mFav = findViewById(R.id.fab).asInstanceOf[FloatingActionButton]

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

    val logoBitmap = BitmapFactory.decodeResource(getResources, R.mipmap.ic_launcher)

    mTaskDescription = new TaskDescription("TokTok - " + friend.getName, logoBitmap, friendColor)
    setTaskDescription(mTaskDescription)

  }

  def getStatusBarHeight: Int = {
    var result = 0
    val resourceId = getResources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
      result = getResources.getDimensionPixelSize(resourceId)
    }
    result
  }

}
