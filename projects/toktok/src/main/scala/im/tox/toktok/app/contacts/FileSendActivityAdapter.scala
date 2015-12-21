package im.tox.toktok.app.contacts

import android.support.v7.widget.RecyclerView
import android.view.{LayoutInflater, ViewGroup}
import im.tox.toktok.TypedResource._
import im.tox.toktok.app.File
import im.tox.toktok.{R, TR}

import scala.collection.mutable.ListBuffer

final class FileSendActivityAdapter(files: ListBuffer[File]) extends RecyclerView.Adapter[FileSendActivityViewHolder] {

  def onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): FileSendActivityViewHolder = {
    val itemView = LayoutInflater.from(viewGroup.getContext).inflate(TR.layout.files_send_item, viewGroup, false)
    new FileSendActivityViewHolder(itemView)
  }

  def onBindViewHolder(viewHolder: FileSendActivityViewHolder, position: Int) = {
    val item = files(position)
    viewHolder.mFileDate.setText(item.date)
    viewHolder.mFileName.setText(item.name)
    viewHolder.mIcon.setImageResource(R.drawable.files_movie)

  }

  def getItemCount: Int = {
    files.length
  }

}
