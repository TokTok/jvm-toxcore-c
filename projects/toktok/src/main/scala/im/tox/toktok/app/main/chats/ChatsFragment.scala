package im.tox.toktok.app.main.chats

import android.content.Intent
import android.content.res.ColorStateList
import android.os.{Bundle, Handler}
import android.support.design.widget.{AppBarLayout, FloatingActionButton}
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.support.v7.widget.{DefaultItemAnimator, LinearLayoutManager, RecyclerView}
import android.view.View.OnClickListener
import android.view._
import android.view.animation.{AccelerateInterpolator, DecelerateInterpolator}
import android.widget.FrameLayout
import im.tox.toktok.TypedResource._
import im.tox.toktok.app.new_message.NewMessageActivity
import im.tox.toktok.app.{CustomViewPager, MyRecyclerScroll}
import im.tox.toktok.{R, TR}

import scala.collection.mutable.ListBuffer

final class ChatsFragment extends Fragment with ChatItemClick {

  private var mChatsRecycler: RecyclerView = null
  private var mChatsRecyclerAdapter: ChatsRecyclerAdapter = null
  private var mActionMode: ActionMode = null

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedState: Bundle): FrameLayout = {
    val view = inflater.inflate(TR.layout.fragment_home_chats, container, false)
    val fab = getActivity.findView(TR.home_fab)

    // Recycler View

    mChatsRecycler = view.findView(TR.home_chats_recycler)

    val mLayoutManager = new LinearLayoutManager(getActivity.getBaseContext)
    mChatsRecycler.setLayoutManager(mLayoutManager)

    val chatMessages = ListBuffer[ChatMessageObject](
      ChatMessageObject.loremMessage,
      ChatMessageObject.johnMessage,
      ChatMessageObject.groupMessage
    )

    mChatsRecyclerAdapter = new ChatsRecyclerAdapter(chatMessages, this)

    mChatsRecycler.setAdapter(mChatsRecyclerAdapter)
    mChatsRecycler.setItemAnimator(new DefaultItemAnimator)
    mChatsRecycler.addOnScrollListener(new MyRecyclerScroll {
      override def hide(): Unit = {
        fab.animate().translationY(fab.getHeight + fab.getBottom).setInterpolator(new AccelerateInterpolator(2)).start()
      }

      override def show(): Unit = {
        fab.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start()
      }
    })

    view
  }

  private def toggleSelection(i: Int): Unit = {
    mChatsRecyclerAdapter.toggleSelection(i)

    mChatsRecyclerAdapter.getSelectedItemCount match {
      case 0 =>
        mActionMode.finish()
      case count @ 1 =>
        mActionMode.setTitle(count + " " + getResources.getString(R.string.action_mode_selected_single))
      case count =>
        mActionMode.setTitle(count + " " + getResources.getString(R.string.action_mode_selected_multi))
    }
  }

  def onClick(i: Int): Boolean = {
    if (mActionMode == null) {
      false
    } else {
      toggleSelection(i)
      true
    }
  }

  def onLongClick(i: Int): Boolean = {
    if (mActionMode == null) {
      mActionMode = getActivity.asInstanceOf[AppCompatActivity].startSupportActionMode(new ChatsActionModeCallback)
    }

    toggleSelection(i)
    true
  }

  final class ChatsActionModeCallback extends ActionMode.Callback {
    private var mAppLayout: AppBarLayout = null
    private var mFab: FloatingActionButton = null
    private var mCustomViewPager: CustomViewPager = null

    override def onCreateActionMode(mode: ActionMode, menu: Menu): Boolean = {
      mAppLayout = getActivity.findView(TR.appBarLayout)
      mAppLayout.setBackgroundColor(getResources.getColor(R.color.backgroundColor, null))

      mFab = getActivity.findView(TR.home_fab)
      mFab.setBackgroundTintList(ColorStateList.valueOf(getResources.getColor(R.color.textDarkColor, null)))
      mFab.setImageResource(R.drawable.ic_action_delete)
      mFab.setImageTintList(ColorStateList.valueOf(getResources.getColor(R.color.textWhiteColor, null)))

      mCustomViewPager = getActivity.findView(TR.home_tab_holder)
      mCustomViewPager.setSwipingEnabled(false)

      mFab.setOnClickListener(new OnClickListener {
        override def onClick(v: View): Unit = {
          mChatsRecyclerAdapter.deleteSelected()

          new Handler().postDelayed(new Runnable {
            override def run(): Unit = {
              mode.finish()
            }
          }, 500)

        }
      })

      true
    }

    override def onDestroyActionMode(mode: ActionMode): Unit = {
      mAppLayout.setBackgroundColor(getResources.getColor(R.color.homeColorToolbar, null))

      mFab.setImageResource(R.drawable.ic_content_add_home)
      mFab.setBackgroundTintList(ColorStateList.valueOf(getResources.getColor(R.color.basicFABColor, null)))
      mFab.setImageTintList(ColorStateList.valueOf(getResources.getColor(R.color.basicFABTint, null)))

      mFab.setOnClickListener(new OnClickListener {
        override def onClick(view: View): Unit = {
          startActivity(new Intent(getActivity, classOf[NewMessageActivity]))
        }
      })

      mCustomViewPager.setSwipingEnabled(true)

      mChatsRecyclerAdapter.clearSelections()
      mActionMode = null
      mAppLayout = null
      mFab = null
      mCustomViewPager = null
    }

    override def onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean = {
      true
    }

    override def onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean = {
      false
    }

  }

}
