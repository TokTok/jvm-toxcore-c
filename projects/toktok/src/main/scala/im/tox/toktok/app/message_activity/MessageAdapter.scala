package im.tox.toktok.app.message_activity

import android.support.v7.widget.{CardView, RecyclerView}
import android.util.{Log, SparseBooleanArray}
import android.view.View.OnClickListener
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.RelativeLayout
import im.tox.toktok.TypedResource._
import im.tox.toktok.app.Message
import im.tox.toktok.{TypedLayout, TR, TypedResource}

import scala.collection.mutable.ListBuffer

final class MessageAdapter(
    messages: ListBuffer[Message],
    messageClick: MessageClick,
    messageActionMode: MessageActionMode
) extends RecyclerView.Adapter[MessageViewHolder] {

  private val selectedItems = new SparseBooleanArray()
  private var actionModeActive = false

  override def getItemViewType(position: Int): Int = {
    messages(position).msgType
  }

  private def inflate[A <: View](layout: TypedLayout[A], viewGroup: ViewGroup): A = {
    LayoutInflater.from(viewGroup.getContext).inflate(layout, viewGroup, false)
  }

  override def onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MessageViewHolder = {
    viewType match {
      case 1 =>
        val itemView = inflate(TR.layout.message_item_user_simple, viewGroup)
        new MessageViewHolderSimple(itemView, messageActionMode)
      case 2 =>
        val itemView = inflate(TR.layout.message_item_friend_simple, viewGroup)
        new MessageViewHolderSimple(itemView, messageActionMode)
      case 3 =>
        val itemView = inflate(TR.layout.message_item_action, viewGroup)
        new MessageViewHolder(itemView, messageActionMode, TR.message_item_action_cardview)
    }
  }

  def toggleSelection(i: Int): Unit = {
    if (selectedItems.get(i, false)) {
      selectedItems.delete(i)
    } else {
      selectedItems.put(i, true)
    }
    notifyItemChanged(i)
  }

  def getSelectedItemCount: Int = {
    selectedItems.size()
  }

  def isSelected(position: Int): Boolean = {
    selectedItems.get(position, false)
  }

  def onBindViewHolder(viewHolder: MessageViewHolder, position: Int) = {
    val message = messages(position)

    getItemViewType(position) match {
      case 1 =>
        val view = viewHolder.asInstanceOf[MessageViewHolderSimple]

        view.mUserText.setText(message.msgContent)
        view.mUserDetails.setText(message.msgDetails)
        view.mUserImg.setImageResource(message.imageSrc)
        view.mUserImg.setOnClickListener(new OnClickListener {
          override def onClick(v: View): Unit = {
            messageClick.onImgClick()
          }
        })

        if (actionModeActive && !isSelected(position)) {
          view.mBase.setAlpha(0.5f)
        } else {
          view.mBase.setAlpha(1)
        }

      case 2 =>
        val view = viewHolder.asInstanceOf[MessageViewHolderSimple]

        view.mUserText.setText(message.msgContent)
        view.mUserDetails.setText(message.msgDetails)
        view.mUserImg.setImageResource(message.imageSrc)

        if (actionModeActive && !isSelected(position)) {
          view.mBase.setAlpha(0.5f)
        } else {
          view.mBase.setAlpha(1)
        }

      case 3 =>
        val view = viewHolder

        view.mUserText.setText(message.msgContent)
        view.mUserImg.setImageResource(message.imageSrc)
        view.mUserImg.setOnClickListener(new OnClickListener {
          override def onClick(v: View): Unit = {
            messageClick.onImgClick()
          }
        })

        if (actionModeActive && !isSelected(position)) {
          view.mBase.setAlpha(0.5f)
        } else {
          view.mBase.setAlpha(1)
        }
    }
  }

  def getItemCount: Int = {
    messages.length
  }

  def addItem(newMsg: Message): Unit = {
    messages.insert(0, newMsg)
    notifyItemInserted(0)
  }

  def setActionModeActive(state: Boolean): Unit = {
    actionModeActive = state
    Log.d("asdasd", actionModeActive.toString)
  }

  def clearSelections(): Unit = {
    selectedItems.clear()
    notifyDataSetChanged()
  }

  def deleteSelected(): Unit = {
    for (a <- (messages.size - 1) to 0 by -1) {
      if (selectedItems.get(a)) {
        messages.remove(a)
        notifyItemRemoved(a)
      }
    }

    selectedItems.clear()
  }

}

sealed class MessageViewHolder(
  itemView: RelativeLayout,
  messageActionMode: MessageActionMode,
  base: TypedResource[CardView]
) extends RecyclerView.ViewHolder(itemView)
    with View.OnLongClickListener
    with View.OnClickListener {

  final val mBase = itemView.findView(base)
  final val mUserText = itemView.findView(TR.message_item_text)
  final val mUserImg = itemView.findView(TR.message_item_img)

  itemView.setOnLongClickListener(this)
  itemView.setOnClickListener(this)

  def onLongClick(v: View): Boolean = {
    messageActionMode.onLongClick(getLayoutPosition)
  }

  def onClick(v: View): Unit = {
    messageActionMode.onClick(getLayoutPosition)
  }

}

final class MessageViewHolderSimple(
    itemView: RelativeLayout,
    messageActionMode: MessageActionMode
) extends MessageViewHolder(itemView, messageActionMode, TR.message_item_base) {
  val mUserDetails = itemView.findView(TR.message_item_details)
}

trait MessageClick {
  def onImgClick(): Unit
}

trait MessageActionMode {
  def onLongClick(i: Int): Boolean
  def onClick(i: Int): Unit
}
