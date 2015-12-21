package im.tox.toktok.app.simple_dialogs

import android.graphics.Color
import android.support.v7.widget.{CardView, RecyclerView}
import android.util.Log
import android.view.View.OnClickListener
import android.view.{LayoutInflater, View, ViewGroup}
import im.tox.toktok.TR
import im.tox.toktok.TypedResource._

final class SimpleColorDialogDesignAdapter(items: List[String]) extends RecyclerView.Adapter[SimpleColorDialogDesignViewHolder] {

  def onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): SimpleColorDialogDesignViewHolder = {
    val itemView = LayoutInflater.from(viewGroup.getContext).inflate(TR.layout.simple_color_dialog_item, viewGroup, false)
    itemView.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = {
        Log.d("asd", "adsd")
        v.setZ(1000.0f)
      }
    })
    new SimpleColorDialogDesignViewHolder(itemView)
  }

  def onBindViewHolder(viewHolder: SimpleColorDialogDesignViewHolder, position: Int) = {
    val item = items(position)
    Log.d("asdasd", item)
    viewHolder.mColor.setBackgroundColor(Color.parseColor(item))
  }

  def getItemCount: Int = {
    items.length
  }

}

final class SimpleColorDialogDesignViewHolder(itemView: CardView) extends RecyclerView.ViewHolder(itemView) with OnClickListener {

  val mColor = itemView.findView(TR.simple_color_dialog_item)

  def onClick(v: View) = {
    Log.d("asd", "adsd")
  }

}
