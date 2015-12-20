package im.tox.toktok.app.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.{View, ViewGroup, LayoutInflater}
import im.tox.toktok.R

class HomeChats extends Fragment {

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val baseView = inflater.inflate(R.layout.fragment_home_chats, container, false)
    baseView
  }
}
