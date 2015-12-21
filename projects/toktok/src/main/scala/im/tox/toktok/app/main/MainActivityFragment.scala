package im.tox.toktok.app.main

import android.content.Intent
import android.graphics.PixelFormat
import android.os.Bundle
import android.support.design.widget.{FloatingActionButton, TabLayout}
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View.OnClickListener
import android.view.ViewGroup.LayoutParams
import android.view._
import android.widget.FrameLayout
import im.tox.toktok.TypedResource._
import im.tox.toktok.app.new_message.NewMessageActivity
import im.tox.toktok.app.simple_dialogs.SimpleAddFriendDialogDesign
import im.tox.toktok.app.{CustomViewPager, MainActivityHolder}
import im.tox.toktok.{R, TContext, TR}

final class MainActivityFragment extends Fragment {

  private var mViewPaper: CustomViewPager = null
  private var mMenu: Menu = null
  private var mToolbar: Toolbar = null
  private var mTabs: TabLayout = null
  private var mFab: FloatingActionButton = null
  private var mPagerAdapter: MainTabsAdapter = null
  private var mDrawer: DrawerLayout = null

  override def onCreate(savedState: Bundle): Unit = {
    super.onCreate(savedState)
    setHasOptionsMenu(true)
  }

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedState: Bundle): FrameLayout = {
    super.onCreate(savedState)
    val view = inflater.inflate(TR.layout.activity_main_fragment, container, false)

    initToolbar(view)
    initFAB(view)
    initViewPaper(view)

    mDrawer = getActivity.findView(TR.home_layout)

    view
  }

  override def onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) = {
    mMenu = menu
    inflater.inflate(R.menu.menu_main, menu)
    super.onCreateOptionsMenu(menu, inflater)
  }

  def initViewPaper(view: FrameLayout): Unit = {
    mViewPaper = view.findView(TR.home_tab_holder)
    mPagerAdapter = new MainTabsAdapter(getChildFragmentManager, getActivity)
    mViewPaper.setAdapter(mPagerAdapter)

    mViewPaper.addOnPageChangeListener(new OnPageChangeListener {
      override def onPageScrollStateChanged(state: Int): Unit = {}

      override def onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int): Unit = {}

      override def onPageSelected(position: Int): Unit = {
        if (position == 0) {
          mFab.hide()
          if (mMenu != null) {
            mMenu.findItem(R.id.action_add_friend).setVisible(true)
          }
        } else {
          mFab.show()
          if (mMenu != null) {
            mMenu.findItem(R.id.action_add_friend).setVisible(false)
          }
        }
      }
    })

    mTabs = view.findView(TR.home_tabs)

    mTabs.setupWithViewPager(mViewPaper)
    mViewPaper.setCurrentItem(1)
  }

  def initToolbar(view: FrameLayout): Unit = {
    mToolbar = view.findView(TR.home_toolbar)

    getActivity.getWindow.setStatusBarColor(getResources.getColor(R.color.homeColorStatusBar, null))

    mToolbar.setNavigationOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = {
        mDrawer.openDrawer(GravityCompat.START)
      }
    })

    getActivity.asInstanceOf[AppCompatActivity].setSupportActionBar(mToolbar)

    val mActionBar = getActivity.asInstanceOf[AppCompatActivity].getSupportActionBar
    mActionBar.setTitle(getResources.getString(R.string.app_name))
    mActionBar.setHomeAsUpIndicator(R.drawable.ic_navigation_menu)
    mActionBar.setDisplayHomeAsUpEnabled(true)
  }

  def initFAB(view: FrameLayout): Unit = {
    mFab = view.findView(TR.home_fab)
    mFab.setOnClickListener(new OnClickListener {
      override def onClick(view: View): Unit = {
        startActivity(new Intent(getActivity, classOf[NewMessageActivity]))
      }
    })
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId match {
      case android.R.id.home =>
        mDrawer.openDrawer(GravityCompat.START)
        true

      case R.id.action_add_friend =>
        val dial = new SimpleAddFriendDialogDesign(getActivity, null)
        dial.show()
        true

      case R.id.action_search =>
        val searchLayout = getActivity.getLayoutInflater.inflate(TR.layout.home_search, null)
        val params = new WindowManager.LayoutParams(
          LayoutParams.MATCH_PARENT,
          LayoutParams.MATCH_PARENT,
          WindowManager.LayoutParams.TYPE_INPUT_METHOD,
          WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
          PixelFormat.TRANSLUCENT
        )
        val window = getActivity.getSystemService(TContext.WINDOW_SERVICE)

        window.addView(searchLayout, params)

        getActivity.asInstanceOf[MainActivityHolder].setSearch(searchLayout)

        true

      case _ =>
        super.onOptionsItemSelected(item)
    }
  }

}

