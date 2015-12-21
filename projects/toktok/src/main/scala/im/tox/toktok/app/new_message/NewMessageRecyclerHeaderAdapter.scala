package im.tox.toktok.app.new_message

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.{LayoutInflater, ViewGroup}
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter
import im.tox.toktok.TR
import im.tox.toktok.TypedResource._
import im.tox.toktok.app.Friend

import scala.collection.mutable.ListBuffer

final class NewMessageRecyclerHeaderAdapter(friends: ListBuffer[Friend], clickListener: FriendAddOnClick)
    extends NewMessageRecyclerAdapter(friends, clickListener)
    with StickyRecyclerHeadersAdapter[RecyclerView.ViewHolder] {

  def getHeaderId(position: Int): Long = {
    getItem(position).userName.charAt(0)
  }

  def onCreateHeaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = {
    val view = LayoutInflater.from(parent.getContext).inflate(TR.layout.recyclerview_header, parent, false)
    new ViewHolder(view) {}
  }

  def onBindHeaderViewHolder(holder: RecyclerView.ViewHolder, position: Int) = {
    holder.itemView.findView(TR.recyclerview_header_text).setText(String.valueOf(getItem(position).userName.charAt(0)))
  }

}
