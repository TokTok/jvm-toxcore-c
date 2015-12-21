package im.tox.toktok.app.new_message

import android.support.v7.widget.RecyclerView
import android.util.SparseBooleanArray
import android.view.animation.AlphaAnimation
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.Filter.FilterResults
import android.widget.{RelativeLayout, Filter, Filterable}
import im.tox.toktok.TR
import im.tox.toktok.TypedResource._
import im.tox.toktok.app.Friend

import scala.collection.mutable.ListBuffer

abstract class NewMessageRecyclerAdapter(
    var friends: ListBuffer[Friend],
    var listener: FriendAddOnClick
) extends RecyclerView.Adapter[NewMessageRecyclerViewHolder] with Filterable {

  private val selectedItems = new SparseBooleanArray()
  private val selectedContacts = new ListBuffer[Friend]
  val savedContacts = friends.clone()

  def onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): NewMessageRecyclerViewHolder = {
    val itemView = LayoutInflater.from(viewGroup.getContext).inflate(TR.layout.new_message_item, viewGroup, false)
    new NewMessageRecyclerViewHolder(itemView, listener)
  }

  def onBindViewHolder(viewHolder: NewMessageRecyclerViewHolder, position: Int) = {
    val item = friends(position)
    viewHolder.mUserName.setText(item.userName)
    viewHolder.mUserImage.setImageResource(item.photoReference)

    val animation =
      if (selectedItems.get(item.id, false)) {
        val animation = new AlphaAnimation(1, 0.3F)
        animation.setDuration(0)
        animation.setFillAfter(true)
        animation
      } else {
        val animation = new AlphaAnimation(0.3F, 1)
        animation.setDuration(0)
        animation.setFillAfter(true)
        animation
      }

    viewHolder.mBase.startAnimation(animation)
  }

  def getItemCount: Int = {
    friends.length
  }

  def getItem(i: Int): Friend = {
    friends(i)
  }

  def getFilter: Filter = {
    new FriendFilter(this, savedContacts)
  }

  def selectItem(position: Int): Unit = {
    if (selectedItems.get(friends(position).id, false)) {
      selectedItems.delete(friends(position).id)
      selectedContacts -= friends(position)
    } else {
      selectedItems.put(friends(position).id, true)
      selectedContacts += friends(position)
    }

    notifyItemChanged(position)
  }

  def selectedCount: Int = {
    selectedItems.size
  }

  def getFirstSelected: Friend = {
    selectedContacts.head
  }

  def clearSelectedList(): Unit = {
    selectedItems.clear()
    selectedContacts.clear()

    notifyDataSetChanged()
  }

  def getItems: Seq[Friend] = {
    friends
  }

  def getSelectedFriends: Seq[Friend] = {
    selectedContacts
  }

}

final class NewMessageRecyclerViewHolder(
  itemView: RelativeLayout,
  clickListener: FriendAddOnClick
) extends RecyclerView.ViewHolder(itemView)
    with View.OnClickListener {

  val mUserName = itemView.findView(TR.new_message_item_name)
  val mUserImage = itemView.findView(TR.new_message_item_img)
  val mBase = itemView.findView(TR.new_message_item)

  itemView.setOnClickListener(this)
  itemView.setTag(this)

  def onClick(v: View): Unit = {
    clickListener.onClickListener(itemView.getTag.asInstanceOf[NewMessageRecyclerViewHolder].getLayoutPosition)
  }

}

trait FriendAddOnClick {
  def onClickListener(position: Int): Unit
}

final class FriendFilter(
    adapter: NewMessageRecyclerAdapter,
    friendsList: ListBuffer[Friend]
) extends Filter {

  private val original = friendsList
  private var filteredResults = new ListBuffer[Friend]

  override protected def performFiltering(constraint: CharSequence): FilterResults = {
    filteredResults.clear()
    val results = new FilterResults

    if (constraint.length() == 0) {
      filteredResults = original.clone
    } else {
      val trimmedString = constraint.toString.toLowerCase.trim

      for (a <- original) {
        if (a.userName.toLowerCase.trim.contains(trimmedString)) {
          filteredResults += a
        }
      }
    }

    results.values = filteredResults
    results.count = filteredResults.size

    results
  }

  override protected def publishResults(constraint: CharSequence, results: FilterResults): Unit = {
    adapter.friends.clear
    adapter.friends = results.values.asInstanceOf[ListBuffer[Friend]]
    adapter.notifyDataSetChanged()
  }

}
