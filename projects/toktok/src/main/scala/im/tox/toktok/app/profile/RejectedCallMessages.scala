package im.tox.toktok.app.profile

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView.ViewHolder
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.MenuItem
import im.tox.toktok.TypedResource._
import im.tox.toktok.{R, TR}

import scala.collection.mutable.ListBuffer

final class RejectedCallMessages extends AppCompatActivity with DragStart {

  var itemDrag: ItemTouchHelper = null

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_reject_call)

    val mToolbar = this.findView(TR.reject_call_toolbar)
    mToolbar.setTitle("Reject call messages")
    mToolbar.setNavigationIcon(R.drawable.ic_navigation_arrow_back_white)
    setSupportActionBar(mToolbar)
    getSupportActionBar.setDisplayHomeAsUpEnabled(true)

    val mRecycler = this.findView(TR.reject_recycler)
    val mLayoutManager = new LinearLayoutManager(this)

    mRecycler.setLayoutManager(mLayoutManager)

    var a = ListBuffer[String]()

    a += "Sorry I’m In Class, Call you later"
    a += "I’m at a meeting, can’t talk"
    a += "Sorry I’m In Class, Call you later"
    a += "I’m at a meeting, can’t talk"
    a += "Sorry I’m In Class, Call you later"
    a += "I’m at a meeting, can’t talk"

    val mAdapter = new RejectedCallAdapter(a, this)
    mRecycler.setAdapter(mAdapter)

    val itemDragCallback = new DragHelperCallback(mAdapter)
    itemDrag = new ItemTouchHelper(itemDragCallback)

    itemDrag.attachToRecyclerView(mRecycler)
  }

  def onDragStart(viewHolder: ViewHolder): Unit = {
    itemDrag.startDrag(viewHolder)
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId match {
      case android.R.id.home =>
        finish()
        true
    }
  }

}

