package im.tox.toktok.app.views.adapters

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.text.TextUtils
import android.util.Log
import android.view.View.OnClickListener
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.TextView
import com.squareup.picasso.{Callback, Picasso}
import com.tonicartos.superslim.{LayoutManager, LinearSLM, GridSLM}
import de.hdodenhof.circleimageview.CircleImageView
import im.tox.toktok.R
import im.tox.toktok.app.models.Friend
import io.realm.RealmResults

import scala.collection.mutable.ListBuffer

class HomeFriendsAdapter(context: Context, list: RealmResults[Friend], homeFriendsClick: HomeFriendsClick) extends RecyclerView.Adapter[RecyclerView.ViewHolder] {

  private var friendsList: ListBuffer[Item] = _
  indexList()

  def onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder = {

    if (viewType == 0) {
      val layout = LayoutInflater.from(viewGroup.getContext).inflate(R.layout.fragment_home_friends_item, viewGroup, false)
      val layoutViewHolder = new HomeFriendsViewHolder(layout, homeFriendsClick)
      layout.setOnClickListener(layoutViewHolder)
      layoutViewHolder

    } else {
      val layout = LayoutInflater.from(viewGroup.getContext).inflate(R.layout.fragment_home_friends_header, viewGroup, false)
      new HomeFriendsStickyHeader(layout)

    }

  }

  def onBindViewHolder(viewHolder: ViewHolder, position: Int) = {

    val item = friendsList(position)
    val lp = GridSLM.LayoutParams.from(viewHolder.itemView.getLayoutParams)

    if (item.isItemHeader == 1) {
      viewHolder.asInstanceOf[HomeFriendsStickyHeader].mHeaderText.setText(item.getContent.asInstanceOf[String])
      lp.headerDisplay = LayoutManager.LayoutParams.HEADER_STICKY
      lp.isHeader = true
    } else {
      val friendInfo = item.getContent.asInstanceOf[Friend]
      val viewHolderContact = viewHolder.asInstanceOf[HomeFriendsViewHolder]
      viewHolderContact.mItemText.setText(friendInfo.getName)
      viewHolderContact.itemID = friendInfo

      val picasso = new Picasso.Builder(context)
        .listener(new Picasso.Listener() {

          def onImageLoadFailed(picasso: Picasso, uri: Uri, exception: Exception) {
            Log.d("toktok", exception + "")
          }
        })
        .build()

      picasso.load("file://" + context.getFilesDir + "/" + friendInfo.getToxID + ".png").resize(144, 144).centerCrop().into(viewHolderContact.mItemImage)

    }

    lp.setSlm(LinearSLM.ID)
    lp.setFirstPosition(item.getSection())
    viewHolder.itemView.setLayoutParams(lp)

  }

  def getItemCount() = friendsList.size

  override def getItemViewType(position: Int) = friendsList(position).isItemHeader

  def indexList(): Unit = {

    friendsList = new ListBuffer[Item]

    var lastHeader = ""
    var section = -1
    var sectionFirstPosition = 0
    var headerCount = 0

    for (i <- 0 until list.size()) {

      val item = list.get(i)
      val friendInitial = item.getName.take(1)

      if (!TextUtils.equals(lastHeader, friendInitial)) {
        section = (section + 1) % 2
        sectionFirstPosition = i + headerCount
        lastHeader = friendInitial
        headerCount += 1
        friendsList += new Item(lastHeader, 1, section, sectionFirstPosition)
      }
      friendsList += new Item(item, 0, section, sectionFirstPosition)

    }
  }

}

private final class HomeFriendsViewHolder(itemView: View, homeFriendsClick: HomeFriendsClick) extends RecyclerView.ViewHolder(itemView) with OnClickListener {
  val mItemText = itemView.findViewById(R.id.item_title).asInstanceOf[TextView]
  val mItemImage = itemView.findViewById(R.id.item_photo).asInstanceOf[CircleImageView]
  var itemID: Friend = _

  def onClick(view: View) = {
    homeFriendsClick.onClick(itemID)
  }

}

trait HomeFriendsClick {
  def onClick(friend: Friend)
}

private final class HomeFriendsStickyHeader(itemView: View) extends RecyclerView.ViewHolder(itemView) {
  val mHeaderText = itemView.asInstanceOf[TextView]
}

private final class Item(content: Object, isHeader: Int, sectionManager: Int, sectionFirstPosition: Int) {

  def getContent: Object = content

  def isItemHeader: Int = isHeader

  def getSection(): Int = sectionFirstPosition

}

class MyClass extends Picasso.Listener {
  def onImageLoadFailed(picasso: Picasso, uri: Uri, exception: Exception): Unit = {
    Log.d("toktok", "" + exception.getStackTrace)
  }
}