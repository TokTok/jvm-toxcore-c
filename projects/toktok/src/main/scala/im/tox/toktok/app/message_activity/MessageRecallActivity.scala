package im.tox.toktok.app.message_activity

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.tonicartos.superslim.LayoutManager
import im.tox.toktok.TypedResource._
import im.tox.toktok.app.Message
import im.tox.toktok.{R, TR}

final class MessageRecallActivity extends AppCompatActivity with RecallMessageListener {

  var colorPrimary: Int = 0
  var colorStatus: Int = 0
  var adapter: MessageRecallRecyclerAdapter = null
  var mToolbar: Toolbar = null
  var mFAB: FloatingActionButton = null

  protected override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_recall_message)

    val bundle = getIntent.getExtras

    colorPrimary = bundle.getInt("colorPrimary")
    colorStatus = bundle.getInt("colorPrimaryStatus")

    mToolbar = this.findView(TR.recall_toolbar)
    mToolbar.setBackgroundColor(colorPrimary)
    getWindow.setStatusBarColor(colorStatus)

    mFAB = this.findView(TR.recall_fab)

    setSupportActionBar(mToolbar)
    getSupportActionBar.setDisplayHomeAsUpEnabled(true)

    val mRecycler = this.findView(TR.recall_recycler)

    val messages =
      Message(1, "Lorem ipsum dolor sit amet, consectetur adipiscing elit.", "23th of April", R.drawable.lorem) +: {
        for (a <- 0 to 10) yield {
          Message(1, "Lorem ipsum dolor sit amet, consectetur adipiscing elit.", "24th of April", R.drawable.lorem)
        }
      }

    mRecycler.setLayoutManager(new LayoutManager(this))

    adapter = new MessageRecallRecyclerAdapter(this, messages, this)

    mRecycler.setAdapter(adapter)
  }

  def onClick(position: Int): Unit = {
    adapter.toggleSelection(position)

    val i = adapter.getSelectedCount

    if (i == 0) {
      getSupportActionBar.setTitle("Recall Messages")
      mFAB.hide()
    } else if (i == 1) {
      getSupportActionBar.setTitle(adapter.getSelectedCount + " selected message")
      mFAB.show()
    } else {
      getSupportActionBar.setTitle(adapter.getSelectedCount + " selected messages")
    }

  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId match {
      case android.R.id.home =>
        finish()
        true
      case _ =>
        super.onOptionsItemSelected(item)
    }
  }

}
