package im.tox.toktok.app.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.DisplayMetrics
import android.view.{View, ViewGroup, LayoutInflater}
import im.tox.toktok.R

class FriendProfile extends Fragment {

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val baseView = inflater.inflate(R.layout.fragment_friend_profile, container, false)
    baseView
  }

}
