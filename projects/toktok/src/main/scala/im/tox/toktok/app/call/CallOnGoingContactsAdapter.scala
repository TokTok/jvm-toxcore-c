package im.tox.toktok.app.call

import android.support.v7.widget.RecyclerView
import android.view.{LayoutInflater, ViewGroup}
import android.widget.RelativeLayout
import im.tox.toktok.TR
import im.tox.toktok.TypedResource._
import im.tox.toktok.app.Friend

final class CallOnGoingContactsAdapter(friends: Seq[Friend]) extends RecyclerView.Adapter[CallOnGoingContactsViewHolder] {

  def onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): CallOnGoingContactsViewHolder = {
    val itemView = LayoutInflater.from(viewGroup.getContext).inflate(TR.layout.call_ongoing_contact, viewGroup, false)
    new CallOnGoingContactsViewHolder(itemView)
  }

  def onBindViewHolder(viewHolder: CallOnGoingContactsViewHolder, position: Int): Unit = {
    val item = friends(position)
    viewHolder.mFriendName.setText(item.userName)
    viewHolder.mFriendImage.setImageResource(item.photoReference)
  }

  def getItemCount: Int = {
    friends.length
  }

}

final class CallOnGoingContactsViewHolder(itemView: RelativeLayout) extends RecyclerView.ViewHolder(itemView) {
  val mFriendImage = itemView.findView(TR.call_ongoing_contact_img)
  val mFriendName = itemView.findView(TR.call_ongoing_contact_name)
  val mFriendCallTime = itemView.findView(TR.call_ongoing_contact_calltime)
}
