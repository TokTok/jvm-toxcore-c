package im.tox.toktok.app.contacts

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.{LinearLayoutManager, RecyclerView, Toolbar}
import android.view.MenuItem
import im.tox.toktok.TypedResource._
import im.tox.toktok.app.File
import im.tox.toktok.{R, TR}

import scala.collection.mutable.ListBuffer

final class FileSendActivity extends AppCompatActivity {

  var mToolbar: Toolbar = null
  var mRecycler: RecyclerView = null
  var colorPrimary: Int = 0
  var colorStatus: Int = 0
  var userName: String = ""

  protected override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_files_send)

    val bundle = getIntent.getExtras

    colorPrimary = bundle.getInt("contactColorPrimary")
    colorStatus = bundle.getInt("contactColorStatus")
    userName = bundle.getString("contactName")

    getWindow.setStatusBarColor(colorStatus)

    mToolbar = this.findView(TR.files_send_toolbar)
    mToolbar.setBackgroundColor(colorPrimary)
    mToolbar.setTitle(getResources.getString(R.string.files_send_title) + " " + userName)

    setSupportActionBar(mToolbar)
    getSupportActionBar.setDisplayHomeAsUpEnabled(true)

    mRecycler = this.findView(TR.files_send_recycler)
    val list = ListBuffer(File.file)

    val mLayoutManager: LinearLayoutManager = new LinearLayoutManager(getBaseContext)
    mRecycler.setLayoutManager(mLayoutManager)

    mRecycler.setAdapter(new FileSendActivityAdapter(list))
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
