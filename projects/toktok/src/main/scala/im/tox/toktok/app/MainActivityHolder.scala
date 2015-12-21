package im.tox.toktok.app

import android.os.{Bundle, Handler}
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.{WindowManager, View}
import android.view.View.OnClickListener
import android.widget.LinearLayout
import im.tox.toktok.TypedResource._
import im.tox.toktok.app.main.friends.SlideInContactsLayout
import im.tox.toktok.app.main.{HomeSearch, MainActivityFragment}
import im.tox.toktok.app.profile.ProfileActivity
import im.tox.toktok.{R, TR}

final class MainActivityHolder extends AppCompatActivity {

  private var activeTab: LinearLayout = null
  private var activeContacts: SlideInContactsLayout = null
  private var activeSearch: HomeSearch = null

  private def turnOnScreen(): Unit = {
    getWindow.addFlags(
      WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
        WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
    )
  }

  protected override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    turnOnScreen()

    val attachFragment = new MainActivityFragment

    getSupportFragmentManager.beginTransaction().add(R.id.home_frame, attachFragment).commit()

    val chatsTabButton = this.findView(TR.home_drawer_chats)
    val peopleTabButton = this.findView(TR.home_drawer_profile)
    val settingsTabButton = this.findView(TR.home_drawer_settings)

    activeTab = chatsTabButton

    activeTab.setBackgroundResource(R.color.drawerBackgroundSelected)

    peopleTabButton.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = {
        if (v != activeTab) {
          val profileFragment = new ProfileActivity
          val trans = getSupportFragmentManager.beginTransaction()
          trans.replace(R.id.home_frame, profileFragment, "Profile")
          trans.addToBackStack("Activity")
          trans.commit()
          changeTab(v)
        } else {
          MainActivityHolder.this.findView(TR.home_layout).closeDrawers()
        }
      }
    })

    chatsTabButton.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = {
        if (v != activeTab) {
          val fragment = new MainActivityFragment
          val trans = getSupportFragmentManager.beginTransaction()
          trans.replace(R.id.home_frame, fragment, "Chats")
          trans.addToBackStack("")
          trans.commit()
          changeTab(v)
        } else {
          MainActivityHolder.this.findView(TR.home_layout).closeDrawers()
        }
      }
    })

    def changeTab(v: View): Unit = {
      activeTab.setBackgroundResource(R.drawable.background_ripple)
      activeTab = v.asInstanceOf[LinearLayout]
      activeTab.setBackgroundResource(R.color.drawerBackgroundSelected)
      this.findView(TR.home_layout).closeDrawers()
    }

  }

  def setActiveActivity(contact: SlideInContactsLayout): Unit = {
    activeContacts = contact
  }

  def setSearch(homeSearch: HomeSearch): Unit = {
    activeSearch = homeSearch
  }

  override def onBackPressed(): Unit = {
    Log.d("asdasd", "asdasda")

    if (activeContacts != null) {
      activeContacts.finish()
      activeContacts = null
    }

    if (getSupportFragmentManager.findFragmentByTag("Profile") != null) {
      getSupportFragmentManager.popBackStack("Activity", FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    if (activeSearch != null) {
      activeSearch.finish()

      new Handler().postDelayed(new Runnable() {
        def run(): Unit = {
          activeSearch.setVisibility(View.GONE)
          getWindowManager.removeView(activeSearch)
          activeSearch = null
        }
      }, 500)
    }
  }

}
