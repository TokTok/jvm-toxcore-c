package im.tox.toktok.app.call

import android.support.v7.widget.RecyclerView
import android.view.{LayoutInflater, ViewGroup}
import android.widget.LinearLayout
import im.tox.toktok.TR
import im.tox.toktok.TypedResource._

final class CallMessageAdapter(excuses: Seq[String]) extends RecyclerView.Adapter[CallMessageViewHolder] {

  def onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): CallMessageViewHolder = {
    val itemView = LayoutInflater.from(viewGroup.getContext).inflate(TR.layout.call_item, viewGroup, false)
    new CallMessageViewHolder(itemView)
  }

  def onBindViewHolder(viewHolder: CallMessageViewHolder, position: Int) = {
    val item = excuses(position)
    viewHolder.mMessage.setText(item)
  }

  def getItemCount: Int = {
    excuses.length
  }

}

final class CallMessageViewHolder(itemView: LinearLayout) extends RecyclerView.ViewHolder(itemView) {

  val mMessage = itemView.findView(TR.call_item_message)

}
