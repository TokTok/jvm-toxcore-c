package im.tox.toktok.app.util

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import im.tox.toktok.TypedLayout

/**
 * Created by pippijn on 03/12/2015.
 */
abstract class ActivityAdapter[VH >: Null](layout: TypedLayout[_]) extends AppCompatActivity {

  private var holder: VH = null

  protected def onCreateViewHolder(): VH
  protected def onCreate(holder: VH): Unit

  protected final override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(layout.id)

    holder = onCreateViewHolder()
    onCreate(holder)
  }

}
