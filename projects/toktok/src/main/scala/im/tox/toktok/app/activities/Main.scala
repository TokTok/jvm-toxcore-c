package im.tox.toktok.app.activities

import android.app.ActivityManager.TaskDescription
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.{BitmapFactory, Color}
import android.os.Bundle
import android.support.design.widget.{AppBarLayout, FloatingActionButton}
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.support.v4.view.{GravityCompat, ViewPager}
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.{ActionBarDrawerToggle, AppCompatActivity}
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.View.OnClickListener
import android.widget.{ImageView, LinearLayout, TextView}
import com.astuetz.PagerSlidingTabStrip
import im.tox.toktok.R
import im.tox.toktok.app.models.{Friend, ApplicationPreferences}
import im.tox.toktok.app.views.adapters.HomeTabsAdapter
import io.realm.Realm

class Main extends AppCompatActivity {

  private val PREFS_NAME = "TokTokPrefsFile"

  // UI Variables

  var db: Realm = _
  var ui_main_color: Int = _
  var ui_dark_mode: Boolean = _

  // UI Components

  var mToolbar: Toolbar = _
  var mTabs: PagerSlidingTabStrip = _
  var mViewPager: ViewPager = _
  var mDrawer: DrawerLayout = _
  var mActiveMenu: LinearLayout = _
  var mInactiveMenuTint: ColorStateList = _
  var mInactiveMenuTextColor: ColorStateList = _
  var mTaskDescription: TaskDescription = _
  var mFAB: FloatingActionButton = _

  override protected def onCreate(savedInstanceState: Bundle): Unit = {

    // Connection to the application database
    db = Realm.getInstance(this)

    // Loading the app preferences (First app run)
    val appSettings = getSharedPreferences(PREFS_NAME, 0)

    ui_main_color = Color.parseColor(appSettings.getString("ui_main_color", "#FF9800"))
    ui_dark_mode = appSettings.getBoolean("ui_dark_theme", false)

    // Loading the ui according with the DB

    if (ui_dark_mode) {
      setTheme(R.style.AppTheme_Dark)
    } else {
      setTheme(R.style.AppTheme)
    }

    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_toktok)

    val bitmap = BitmapFactory.decodeResource(getResources, R.mipmap.ic_launcher)

    mTaskDescription = new TaskDescription("TokTok", bitmap, ui_main_color)
    setTaskDescription(mTaskDescription)

    initToolbar()
    initDrawer()
    initFAB()
    initViewPager()

    //Launching the chats by default
    /*
    db.beginTransaction()

    val friend = db.createObject(classOf[Friend])

    friend.setFav(true)
    friend.setName("John Doe")
    friend.setToxID("56A1ADE4B65B86BCD51CC73E2CD4E542179F47959FE3E0E21B4B0ACDADE51855D34D34D37CB7")
    friend.setStatus(0)
    friend.setStatusMessage("Experimenting TokTok")

    db.commitTransaction()



    db.beginTransaction()

    db.where(classOf[Friend]).contains("name","Daniel").findAll().first().setColor(Color.parseColor("#F4511E"))

    db.commitTransaction()

*/
    mViewPager.setCurrentItem(1)

  }

  def initToolbar(): Unit = {

    findViewById(R.id.appbar).asInstanceOf[AppBarLayout].setBackgroundColor(ui_main_color)
    mToolbar = findViewById(R.id.toolbar).asInstanceOf[Toolbar]
    setSupportActionBar(mToolbar)

  }

  def initViewPager(): Unit = {

    mViewPager = findViewById(R.id.pager).asInstanceOf[ViewPager]
    mTabs = findViewById(R.id.tabs).asInstanceOf[PagerSlidingTabStrip]
    mViewPager.setAdapter(new HomeTabsAdapter(db, getSupportFragmentManager, this))

    mTabs.setIndicatorColor(Color.WHITE)

    mTabs.setViewPager(mViewPager)
    mViewPager.addOnPageChangeListener(new OnPageChangeListener {

      override def onPageScrollStateChanged(state: Int): Unit = {}

      override def onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int): Unit = {}

      override def onPageSelected(position: Int): Unit = {
        if (position == 1) {
          mFAB.show()
        } else {
          mFAB.hide()
        }
      }
    })
  }

  def initDrawer(): Unit = {

    mDrawer = findViewById(R.id.drawer).asInstanceOf[DrawerLayout]
    mDrawer.setStatusBarBackgroundColor(ui_main_color)
    val drawerToogle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.abc_action_bar_home_description, R.string.abc_action_bar_home_description) {

      override def onDrawerStateChanged(newState: Int): Unit = {
        if (newState == DrawerLayout.STATE_SETTLING && !mDrawer.isDrawerOpen(GravityCompat.START)) {

          if (mActiveMenu != null) {
            val mActiveMenuIcon = mActiveMenu.getChildAt(0).asInstanceOf[ImageView]
            mActiveMenuIcon.setImageTintList(mInactiveMenuTint)
            mActiveMenuIcon.setAlpha(0.37f)

            mActiveMenu.getChildAt(1).asInstanceOf[TextView].setTextColor(mInactiveMenuTextColor)
            mActiveMenu.setElevation(0)
            mActiveMenu.setBackgroundColor(ContextCompat.getColor(Main.this, R.color.transparent))
          }

          var newMenu: LinearLayout = null

          mViewPager.getCurrentItem match {
            case 0 => newMenu = findViewById(R.id.drawer_list_friends).asInstanceOf[LinearLayout]

            case 1 => newMenu = findViewById(R.id.drawer_list_chats).asInstanceOf[LinearLayout]

            case 2 => newMenu = findViewById(R.id.drawer_list_call_log).asInstanceOf[LinearLayout]

          }

          mInactiveMenuTint = newMenu.getChildAt(0).asInstanceOf[ImageView].getImageTintList
          mInactiveMenuTextColor = newMenu.getChildAt(1).asInstanceOf[TextView].getTextColors

          mActiveMenu = newMenu
          if (ui_dark_mode) {
            mActiveMenu.setElevation(2)
            mActiveMenu.setBackgroundColor(ContextCompat.getColor(Main.this, R.color.transparent_black))
          }

          val mActiveMenuIcon = mActiveMenu.getChildAt(0).asInstanceOf[ImageView]
          mActiveMenuIcon.setImageTintList(ColorStateList.valueOf(ui_main_color))
          mActiveMenuIcon.setAlpha(1f)

          mActiveMenu.getChildAt(1).asInstanceOf[TextView].setTextColor(ui_main_color)

        }
      }

    }

    val drawerAccent = findViewById(R.id.drawer_accent_color)
    drawerAccent.setBackgroundColor(ui_main_color)

    mDrawer.setDrawerListener(drawerToogle)
    drawerToogle.syncState()

    val drawerList = findViewById(R.id.drawer_list).asInstanceOf[LinearLayout]

    val viewListener = new OnClickListener {
      override def onClick(v: View): Unit = {

        mDrawer.closeDrawers()

        v.getId match {

          case R.id.drawer_list_friends  => mViewPager.setCurrentItem(0)
          case R.id.drawer_list_chats    => mViewPager.setCurrentItem(1)
          case R.id.drawer_list_call_log => mViewPager.setCurrentItem(2)
          case R.id.drawer_list_settings => startActivity(new Intent(Main.this, classOf[Settings]))

        }
      }
    }

    for (i <- 0 until drawerList.getChildCount) {
      drawerList.getChildAt(i).setOnClickListener(viewListener)
    }
  }

  def initFAB(): Unit = {
    mFAB = findViewById(R.id.fab).asInstanceOf[FloatingActionButton]
    mFAB.setImageTintList(ColorStateList.valueOf(ui_main_color))
  }

}