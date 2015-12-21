package im.tox.toktok.app.profile

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.v7.widget.helper.ItemTouchHelper.Callback
import android.view.View.OnTouchListener
import android.view.{LayoutInflater, MotionEvent, View, ViewGroup}
import android.widget.RelativeLayout
import im.tox.toktok.TR
import im.tox.toktok.TypedResource._

import scala.collection.mutable.ListBuffer

final class RejectedCallAdapter(items: ListBuffer[String], dragStart: DragStart)
    extends RecyclerView.Adapter[RejectedCallViewHolder]
    with DragInterface {

  def onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RejectedCallViewHolder = {
    val itemView = LayoutInflater.from(viewGroup.getContext).inflate(TR.layout.rejected_call_item, viewGroup, false)
    new RejectedCallViewHolder(itemView)
  }

  def onBindViewHolder(viewHolder: RejectedCallViewHolder, position: Int) = {
    val item = items(position)
    viewHolder.mMessage.setText(item)

    viewHolder.itemView.setOnTouchListener(new OnTouchListener {
      override def onTouch(v: View, event: MotionEvent): Boolean = {
        if (event.getAction == MotionEvent.ACTION_DOWN) {
          dragStart.onDragStart(viewHolder)
        }

        false
      }
    })

  }

  def getItemCount: Int = {
    items.length
  }

  def onItemMove(originalPosition: Int, newPosition: Int) {
    val originalItem = items(originalPosition)
    items(originalPosition) = items(newPosition)
    items(newPosition) = originalItem
    notifyItemMoved(originalPosition, newPosition)
  }

}

final class RejectedCallViewHolder(itemView: RelativeLayout) extends RecyclerView.ViewHolder(itemView) {
  val mMessage = itemView.findView(TR.reject_item_message)
}

final class DragHelperCallback(adapter: RejectedCallAdapter) extends ItemTouchHelper.Callback {

  override def isLongPressDragEnabled: Boolean = {
    true
  }

  override def isItemViewSwipeEnabled: Boolean = {
    false
  }

  override def getMovementFlags(recyclerView: RecyclerView, viewHolder: ViewHolder): Int = {
    val dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN
    Callback.makeMovementFlags(dragFlags, 0)
  }

  override def onMove(recyclerView: RecyclerView, viewHolder: ViewHolder, target: ViewHolder): Boolean = {
    adapter.onItemMove(viewHolder.getAdapterPosition, target.getAdapterPosition)
    true
  }

  def onSwiped(viewHolder: ViewHolder, direction: Int): Unit = {
  }

}

trait DragInterface {
  def onItemMove(originalPosition: Int, newPosition: Int): Unit
}

trait DragStart {
  def onDragStart(viewHolder: ViewHolder): Unit
}
