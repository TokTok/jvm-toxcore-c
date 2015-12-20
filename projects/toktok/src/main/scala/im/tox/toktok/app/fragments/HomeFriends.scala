package im.tox.toktok.app.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.{LayoutInflater, View, ViewGroup}
import com.tonicartos.superslim.LayoutManager
import im.tox.toktok.R
import im.tox.toktok.app.activities.ContactDetails
import im.tox.toktok.app.models.Friend
import im.tox.toktok.app.views.adapters.{HomeFriendsAdapter, HomeFriendsClick}
import im.tox.toktok.app.views.widgets.FriendLayout
import io.realm.{Realm, Sort}

class HomeFriends(db: Realm) extends Fragment with HomeFriendsClick {

  var mRecylerView: RecyclerView = _
  var mFriendInfo: FriendLayout = _

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {

    val baseView = inflater.inflate(R.layout.fragment_home_friends, container, false)

    mRecylerView = baseView.findViewById(R.id.recycler_view).asInstanceOf[RecyclerView]
    mRecylerView.setLayoutManager(new LayoutManager(getActivity))

    val list_friends = db.where(classOf[Friend]).findAllSorted("name", Sort.ASCENDING)

    mRecylerView.setAdapter(new HomeFriendsAdapter(getContext, list_friends, this))

    baseView

  }

  def onClick(friend: Friend): Unit = {

    val intent = new Intent(getActivity, classOf[ContactDetails])
    intent.putExtra("friendID", friend.getToxID)
    getActivity.startActivity(intent)

  }

}
