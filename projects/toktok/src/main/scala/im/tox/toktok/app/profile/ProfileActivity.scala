package im.tox.toktok.app.profile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View.OnClickListener
import android.view.ViewGroup.LayoutParams
import android.view.{LayoutInflater, View, ViewGroup}
import im.tox.toktok.TypedResource._
import im.tox.toktok.app.simple_dialogs.{SimpleShareDialogDesign, SimpleStatusDialogDesign, SimpleTextDialogDesign}
import im.tox.toktok.{R, TR}

final class ProfileActivity extends Fragment {

  var mToolbar: Toolbar = null
  var mDrawer: DrawerLayout = null

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedState: Bundle): CoordinatorLayout = {
    super.onCreate(savedState)
    val view = inflater.inflate(TR.layout.activity_profile, container, false)
    getActivity.getWindow.setStatusBarColor(Color.parseColor("#2b000000"))

    view.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

    initToolbar(view)

    val mCollapsingToolbar = view.findView(TR.profile_collapsing_toolbar)

    mDrawer = getActivity.findView(TR.home_layout)

    val mShareIDButton = view.findView(TR.profile_share_id)

    mShareIDButton.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = {
        val dial = new SimpleShareDialogDesign(getActivity)
        dial.show()
      }
    })

    val mChangeNickname = view.findView(TR.profile_change_nickname)

    mChangeNickname.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = {
        val dial = new SimpleTextDialogDesign(
          getActivity,
          "Edit your nickname",
          getResources.getColor(R.color.homeColorToolbar, null),
          R.drawable.ic_person_black_48dp,
          "André Almeida",
          null
        )
        dial.show()
      }
    })

    val mChangeStatusMessage = view.findView(TR.profile_change_status_text)

    mChangeStatusMessage.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = {
        val dial = new SimpleTextDialogDesign(
          getActivity,
          "Edit your status message",
          getResources.getColor(R.color.homeColorToolbar, null),
          R.drawable.ic_person_black_48dp,
          "Trying out the new Tox Android Clientww",
          null
        )
        dial.show()
      }
    })

    val mChangeStatus = view.findView(TR.profile_change_status)

    mChangeStatus.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = {
        val dial = new SimpleStatusDialogDesign(getActivity, 0)
        dial.show()
      }
    })

    val mChangeReject = view.findView(TR.profile_change_reject_call)

    mChangeReject.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = {
        val rejectIntent = new Intent(getActivity, classOf[RejectedCallMessages])
        startActivity(rejectIntent)
      }
    })

    view
  }

  def initToolbar(view: View): Unit = {
    mToolbar = view.findView(TR.profile_toolbar)
    val activity = getActivity.asInstanceOf[AppCompatActivity]
    activity.setSupportActionBar(mToolbar)
    activity.getSupportActionBar.setHomeAsUpIndicator(R.drawable.ic_navigation_menu)
    activity.getSupportActionBar.setDisplayHomeAsUpEnabled(true)

    mToolbar.setNavigationOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = {
        mDrawer.openDrawer(GravityCompat.START)
      }
    })
  }

}
