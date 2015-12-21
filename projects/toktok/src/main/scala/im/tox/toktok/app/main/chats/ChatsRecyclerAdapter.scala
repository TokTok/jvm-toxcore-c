package im.tox.toktok.app.main.chats

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.{CardView, RecyclerView}
import android.util.SparseBooleanArray
import android.view.{LayoutInflater, View, ViewGroup}
import im.tox.toktok.TR
import im.tox.toktok.TypedResource._
import im.tox.toktok.app.message_activity.MessageActivity

import scala.collection.mutable.ListBuffer

final class ChatsRecyclerAdapter(
    chatMessages: ListBuffer[ChatMessageObject],
    chatItemClick: ChatItemClick
) extends RecyclerView.Adapter[RecyclerView.ViewHolder] {

  private val selectedItems = new SparseBooleanArray()

  def onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder = {
    viewType match {
      case 0 =>
        val itemView = LayoutInflater.from(viewGroup.getContext).inflate(TR.layout.fragment_home_chats_item_user, viewGroup, false)
        new ChatsRecyclerViewHolderUser(itemView, chatMessages, chatItemClick)
      case 1 =>
        val itemView = LayoutInflater.from(viewGroup.getContext).inflate(TR.layout.fragment_home_chats_item_group, viewGroup, false)
        new ChatsRecyclerViewHolderGroup(itemView, chatMessages, chatItemClick)
    }
  }

  def onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) = {
    chatMessages(position) match {
      case FriendMessageObject(friend, lastMessage) =>
        val view = viewHolder.asInstanceOf[ChatsRecyclerViewHolderUser]

        view.mUserName.setText(friend.userName)
        view.mUserStatus.setText(friend.userMessage)
        view.mLastMessage.setText(lastMessage)
        view.mUserImage.setImageResource(friend.photoReference)
        view.mColor.setBackgroundColor(friend.color)

        if (isSelected(position)) {
          view.mSelectedBackground.setVisibility(View.VISIBLE)
        } else {
          view.mSelectedBackground.setVisibility(View.INVISIBLE)
        }

      case GroupMessageObject(group, lastMessage) =>
        val view = viewHolder.asInstanceOf[ChatsRecyclerViewHolderGroup]

        view.mUserName.setText(group.groupName)
        view.mLastMessage.setText(lastMessage)
        view.mColor.setBackgroundColor(group.primaryColor)

        if (isSelected(position)) {
          view.mSelectedBackground.setVisibility(View.VISIBLE)
        } else {
          view.mSelectedBackground.setVisibility(View.INVISIBLE)
        }

    }
  }

  override def getItemViewType(position: Int): Int = {
    ChatMessageObject.messageType(chatMessages(position))
  }

  def getItemCount: Int = {
    chatMessages.length
  }

  def toggleSelection(i: Int): Unit = {
    if (selectedItems.get(i, false)) {
      selectedItems.delete(i)
    } else {
      selectedItems.put(i, true)
    }

    notifyItemChanged(i)
  }

  def clearSelections(): Unit = {
    selectedItems.clear()
    notifyDataSetChanged()
  }

  def getSelectedItemCount: Int = {
    selectedItems.size()
  }

  def isSelected(position: Int): Boolean = {
    selectedItems.get(position, false)
  }

  def getSelectedItems: ListBuffer[Int] = {
    val selectedList = ListBuffer[Int](selectedItems.size)

    for (i <- 0 to selectedItems.size()) {
      selectedList += selectedItems.keyAt(i)
    }

    selectedList
  }

  def deleteSelected(): Unit = {
    for (a <- (chatMessages.size - 1) to 0 by -1) {
      if (selectedItems.get(a)) {
        chatMessages.remove(a)
        notifyItemRemoved(a)
      }
    }

    selectedItems.clear()
  }

}

final class ChatsRecyclerViewHolderUser(
  itemView: View,
  chatMessages: Seq[ChatMessageObject],
  clickListener: ChatItemClick
) extends RecyclerView.ViewHolder(itemView)
    with View.OnClickListener
    with View.OnLongClickListener {

  itemView.setOnClickListener(this)
  itemView.setOnLongClickListener(this)

  val mSelectedBackground = itemView.findView(TR.home_item_selected)
  val mUserName = itemView.findView(TR.home_item_name)
  val mUserStatus = itemView.findView(TR.home_item_status)
  val mLastMessage = itemView.findView(TR.home_item_last_message)
  val mUserImage = itemView.findView(TR.home_item_img)
  val mColor = itemView.findView(TR.home_item_color)

  def onClick(view: View) = {
    if (!clickListener.onClick(getLayoutPosition)) {
      val bundle = new Bundle()

      chatMessages(getLayoutPosition) match {
        case FriendMessageObject(friend, lastMessage) =>
          bundle.putInt("messageType", 0)
          bundle.putInt("contactColorPrimary", friend.color)
          bundle.putInt("contactColorStatus", friend.secondColor)
          bundle.putString("messageTitle", friend.userName)
          bundle.putInt("imgResource", friend.photoReference)
      }

      val messageIntent = new Intent(itemView.getContext, classOf[MessageActivity])
      messageIntent.putExtras(bundle)

      itemView.getContext.startActivity(messageIntent)
    }
  }

  def onLongClick(v: View): Boolean = {
    clickListener.onLongClick(getLayoutPosition)
  }

}

final class ChatsRecyclerViewHolderGroup(
  itemView: CardView,
  chatMessages: Seq[ChatMessageObject],
  clickListener: ChatItemClick
) extends RecyclerView.ViewHolder(itemView)
    with View.OnClickListener
    with View.OnLongClickListener {

  itemView.setOnClickListener(this)
  itemView.setOnLongClickListener(this)

  val mSelectedBackground = itemView.findView(TR.home_item_selected)
  val mUserName = itemView.findView(TR.home_item_name)
  val mLastMessage = itemView.findView(TR.home_item_last_message)
  val mColor = itemView.findView(TR.home_item_color)

  def onClick(view: View) = {
    if (!clickListener.onClick(getLayoutPosition)) {
      val bundle = new Bundle()

      chatMessages(getLayoutPosition) match {
        case GroupMessageObject(group, lastMessage) =>
          bundle.putInt("messageType", 1)
          bundle.putInt("contactColorPrimary", group.primaryColor)
          bundle.putInt("contactColorStatus", group.statusColor)
          bundle.putString("messageTitle", group.groupName)
      }

      val messageIntent = new Intent(itemView.getContext, classOf[MessageActivity])
      messageIntent.putExtras(bundle)

      itemView.getContext.startActivity(messageIntent)
    }
  }

  def onLongClick(v: View): Boolean = {
    clickListener.onLongClick(getLayoutPosition)
  }

}

trait ChatItemClick {
  def onLongClick(i: Int): Boolean
  def onClick(i: Int): Boolean
}
