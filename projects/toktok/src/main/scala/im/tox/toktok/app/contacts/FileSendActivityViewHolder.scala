package im.tox.toktok.app.contacts

import android.support.v7.widget.RecyclerView
import android.widget.RelativeLayout
import im.tox.toktok.TR
import im.tox.toktok.TypedResource._

final class FileSendActivityViewHolder(itemView: RelativeLayout) extends RecyclerView.ViewHolder(itemView) {

  val mIcon = itemView.findView(TR.files_send_item_icon)
  val mFileName = itemView.findView(TR.files_send_item_name)
  val mFileDate = itemView.findView(TR.files_send_item_date)

}
