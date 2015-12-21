package im.tox.toktok.app.main.friends

import android.support.v7.widget.RecyclerView
import android.view.{LayoutInflater, ViewGroup}
import android.widget.LinearLayout
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter
import im.tox.toktok.TR
import im.tox.toktok.TypedResource._
import im.tox.toktok.app.Friend

final class FriendsRecyclerHeaderAdapter(
  friends: Seq[Friend],
  friendPhotoOnClick: FriendItemClicks
) extends FriendsRecyclerAdapter(friends, friendPhotoOnClick)
    with StickyRecyclerHeadersAdapter[FriendsRecyclerHeaderViewHolder] {

  def getHeaderText(position: Int): String = {
    getItemPosition(position).charAt(0).toString
  }

  def getHeaderId(position: Int): Long = {
    getItemPosition(position).charAt(0)
  }

  def onCreateHeaderViewHolder(parent: ViewGroup): FriendsRecyclerHeaderViewHolder = {
    val view = LayoutInflater.from(parent.getContext).inflate(TR.layout.recyclerview_header, parent, false)
    new FriendsRecyclerHeaderViewHolder(view)
  }

  def onBindHeaderViewHolder(holder: FriendsRecyclerHeaderViewHolder, position: Int): Unit = {
    holder.headerText.setText(getHeaderText(position))
  }

}

final class FriendsRecyclerHeaderViewHolder(
    headerView: LinearLayout
) extends RecyclerView.ViewHolder(headerView) {
  val headerText = headerView.findView(TR.recyclerview_header_text)
}
