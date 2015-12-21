package im.tox.toktok.app.message_activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.{LayoutInflater, ViewGroup}
import android.widget.LinearLayout
import im.tox.toktok.TR
import im.tox.toktok.TypedResource._

final class MessageAttachments extends Fragment {

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): LinearLayout = {
    inflater.inflate(TR.layout.overlay_attachments, container, false)
  }

}
