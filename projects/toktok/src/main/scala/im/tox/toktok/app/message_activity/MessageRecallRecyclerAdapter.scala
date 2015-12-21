package im.tox.toktok.app.message_activity

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.text.TextUtils
import android.util.SparseBooleanArray
import android.view.View.OnClickListener
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.LinearLayout
import com.tonicartos.superslim.{GridSLM, LayoutManager, LinearSLM}
import im.tox.toktok.TypedResource._
import im.tox.toktok.app.Message
import im.tox.toktok.{R, TR}

import scala.collection.mutable.ListBuffer

final class MessageRecallRecyclerAdapter(
    context: Context,
    messages: Seq[Message],
    recallMessageListener: RecallMessageListener
) extends RecyclerView.Adapter[ViewHolder] {

  private val items = new ListBuffer[LineItem]
  private val selectedItems = new SparseBooleanArray()
  private var lastHeader: String = ""
  private var sectionManager: Int = -1
  private var headerCount: Int = 0
  private var sectionFirstPosition: Int = 0
  private var i = 0

  for (message <- messages) {
    val header = message.msgDetails

    if (!TextUtils.equals(lastHeader, header)) {
      sectionManager = (sectionManager + 1) % 2
      sectionFirstPosition = i + headerCount
      lastHeader = header
      headerCount += 1
      items += new LineItem(header, 1, sectionManager, sectionFirstPosition)
    }
    items += new LineItem(message, 0, sectionManager, sectionFirstPosition)
    i += 1
  }

  def onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder = {
    if (viewType == 1) {
      val itemView = LayoutInflater.from(viewGroup.getContext).inflate(TR.layout.recall_header, viewGroup, false)
      new MessageRecallRecyclerViewHolderHeader(itemView)
    } else {
      val itemView = LayoutInflater.from(viewGroup.getContext).inflate(TR.layout.recall_item, viewGroup, false)
      new MessageRecallRecyclerViewHolder(itemView)
    }
  }

  def onBindViewHolder(viewHolder: ViewHolder, position: Int) = {
    val item = items(position)

    val lp = GridSLM.LayoutParams.from(viewHolder.itemView.getLayoutParams)

    if (item.isItemHeader == 1) {

      val itemMessage = item.content.asInstanceOf[String]

      val view = viewHolder.asInstanceOf[MessageRecallRecyclerViewHolderHeader]

      view.mMessageText.setText(itemMessage)
      lp.headerDisplay = LayoutManager.LayoutParams.HEADER_STICKY | LayoutManager.LayoutParams.HEADER_INLINE
      lp.isHeader = true
      lp.headerEndMarginIsAuto = false
      lp.headerStartMarginIsAuto = false

    } else {

      val itemMessage = item.content.asInstanceOf[Message]

      val view = viewHolder.asInstanceOf[MessageRecallRecyclerViewHolder]

      view.mMessageText.setText(itemMessage.msgContent)
      view.mMessageDetails.setText(itemMessage.msgDetails)
      view.mMessageBase.setOnClickListener(new OnClickListener {
        override def onClick(v: View): Unit = {
          recallMessageListener.onClick(position)
        }
      })

      if (isSelected(position)) {
        view.mMessageBase.setBackgroundColor(Color.parseColor("#E0E0E0"))

      } else {
        view.mMessageBase.setBackgroundResource(R.color.cardBoardBackground)
      }

    }

    lp.setSlm(LinearSLM.ID)
    lp.setFirstPosition(item.sectionFirstPosition)
    viewHolder.itemView.setLayoutParams(lp)
  }

  def getItemCount: Int = {
    items.length
  }

  def isSelected(position: Int): Boolean = {
    selectedItems.get(position, false)
  }

  def getItemPosition(position: Int): Object = {
    items(position)
  }

  def toggleSelection(i: Int): Unit = {
    if (selectedItems.get(i, false)) {
      selectedItems.delete(i)
    } else {
      selectedItems.put(i, true)
    }
    notifyItemChanged(i)
  }

  override def getItemViewType(position: Int): Int = {
    items(position).isItemHeader
  }

  def getSelectedCount: Int = {
    selectedItems.size()
  }

}

final class MessageRecallRecyclerViewHolder(itemView: LinearLayout) extends RecyclerView.ViewHolder(itemView) {
  val mMessageText = itemView.findView(TR.message_item_text)
  val mMessageDetails = itemView.findView(TR.message_item_details)
  val mMessageBase = itemView.findView(TR.message_item_base)
}

final class MessageRecallRecyclerViewHolderHeader(itemView: LinearLayout) extends RecyclerView.ViewHolder(itemView) {
  val mMessageText = itemView.findView(TR.recall_header_text)
  val mMessageBase = itemView.findView(TR.recall_header_base)
}

final case class LineItem(
  content: Any,
  isItemHeader: Int,
  sectionManager: Int,
  sectionFirstPosition: Int
)

trait RecallMessageListener {
  def onClick(position: Int)
}
