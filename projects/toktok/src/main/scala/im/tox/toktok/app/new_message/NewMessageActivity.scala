package im.tox.toktok.app.new_message

import android.graphics.drawable.BitmapDrawable
import android.graphics.{Bitmap, Canvas, Color}
import android.os.Bundle
import android.support.design.widget.{AppBarLayout, FloatingActionButton}
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.{LinearLayoutManager, RecyclerView, Toolbar}
import android.text._
import android.text.method.LinkMovementMethod
import android.text.style.{ClickableSpan, ImageSpan}
import android.view.View.{MeasureSpec, OnClickListener}
import android.view.ViewGroup.LayoutParams
import android.view._
import android.view.animation.{AccelerateInterpolator, Animation, DecelerateInterpolator, Transformation}
import android.widget.{EditText, ImageButton, LinearLayout, TextView}
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration
import de.hdodenhof.circleimageview.CircleImageView
import im.tox.toktok.TypedResource._
import im.tox.toktok.app.{Friend, MyRecyclerScroll}
import im.tox.toktok.{R, TR}

import scala.collection.mutable.ListBuffer

final class NewMessageActivity extends AppCompatActivity {

  private var mToolbar: Toolbar = null
  private var mAppBarLayout: AppBarLayout = null
  private var mRecycler: RecyclerView = null
  private var mSelectedFriends: LinearLayout = null
  private var mFriends_Recycler_Adapter: NewMessageRecyclerHeaderAdapter = null
  private var mFab: FloatingActionButton = null
  private var colorPrimary: Int = 0
  private var colorStatus: Int = 0
  private var mSearchField: EditText = null
  private var mSelectedFriendsText: TextView = null
  private var mSelectedFriendsImg: CircleImageView = null
  private var mSelectedFriendsCounter: TextView = null
  private var mSelectedFriendsButton: ImageButton = null
  private var mSelectedMini: TextView = null
  private var mSelectedMiniExtended: Boolean = false

  protected override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_new_message)

    val bundle: Bundle = getIntent.getExtras

    if (bundle != null) {
      colorPrimary = bundle.getInt("colorPrimary")
      colorStatus = bundle.getInt("colorPrimaryStatus")
    } else {
      colorPrimary = Color.parseColor("#F5A623")
      colorStatus = Color.parseColor("#8C5F14")
    }

    mFab = this.findView(TR.new_message_fab)

    initToolbar(colorPrimary, colorStatus)
    initRecyclerView()
  }

  def initRecyclerView(): Unit = {
    mRecycler = this.findView(TR.new_message_recycler)

    val mLayoutManager = new LinearLayoutManager(this)

    val friends = ListBuffer(Friend.bart, Friend.jane, Friend.john, Friend.lorem)

    mFriends_Recycler_Adapter = new NewMessageRecyclerHeaderAdapter(friends, null)

    mFriends_Recycler_Adapter.listener = new FriendAddOnClick {
      override def onClickListener(position: Int): Unit = {
        mFriends_Recycler_Adapter.selectItem(position)
        selectItem(position)
      }
    }

    mRecycler.setAdapter(mFriends_Recycler_Adapter)
    mRecycler.setLayoutManager(mLayoutManager)
    mRecycler.addItemDecoration(new StickyRecyclerHeadersDecoration(mFriends_Recycler_Adapter))

    mRecycler.addOnScrollListener(new MyRecyclerScroll {

      override def hide(): Unit = {
        mFab.animate().translationY(mFab.getHeight + mFab.getBottom).setInterpolator(new AccelerateInterpolator(2)).start()
      }

      override def show(): Unit = {
        mFab.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start()
      }

    })
  }

  def initToolbar(colour: Int, secondColour: Int): Unit = {
    mSelectedFriends = this.findView(TR.new_message_selected_base)
    mSelectedFriendsImg = this.findView(TR.new_message_selected_img)
    mSelectedFriendsText = this.findView(TR.new_message_toolbar_selected_text)
    mSelectedFriendsButton = this.findView(TR.new_message_toolbar_selected_button)

    mToolbar = this.findView(TR.newMessage_toolbar)
    mToolbar.setBackgroundColor(colour)

    this.findView(TR.new_message_app_bar_layout).setBackgroundColor(colour)

    setSupportActionBar(mToolbar)
    getSupportActionBar.setTitle(getResources.getString(R.string.new_message_title))
    getSupportActionBar.setDisplayHomeAsUpEnabled(true)

    mAppBarLayout = this.findView(TR.new_message_app_bar_layout)

    val window = getWindow
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.setStatusBarColor(secondColour)

    mSearchField = this.findView(TR.new_message_search_field)

    mSearchField.addTextChangedListener(new TextWatcher {

      override def beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int): Unit = {}

      override def onTextChanged(s: CharSequence, start: Int, before: Int, count: Int): Unit = {
        mFriends_Recycler_Adapter.getFilter.filter(s)
      }

      override def afterTextChanged(s: Editable): Unit = {}
    })

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

  def initFirstSelectedContacts(adapter: NewMessageRecyclerAdapter): Unit = {
    mSelectedFriends.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    val height = mSelectedFriends.getMeasuredHeight

    mSelectedFriends.setVisibility(View.VISIBLE)
    mSelectedFriends.getLayoutParams.height = 1

    val inAnimation = new Animation() {
      override def applyTransformation(time: Float, transf: Transformation) {
        if (time == 1) {
          mSelectedFriends.getLayoutParams.height = LayoutParams.MATCH_PARENT
        } else {
          mSelectedFriends.getLayoutParams.height = (time * height).toInt
        }
        mSelectedFriends.requestLayout()
      }

      override def willChangeBounds(): Boolean = {
        true
      }
    }

    setOneSelectedContact(adapter)

    mFab.show()

    inAnimation.setDuration(4 * (height / mSelectedFriends.getContext.getResources.getDisplayMetrics.density).toInt)
    mSelectedFriends.startAnimation(inAnimation)
  }

  def destroySelectedContacts(): Unit = {
    val height = mSelectedFriends.getMeasuredHeight

    val outAnimation = new Animation() {
      override def applyTransformation(time: Float, transf: Transformation) {
        if (time == 1) {
          mSelectedFriends.setVisibility(View.GONE)
        } else {
          mSelectedFriends.getLayoutParams.height = height - (time * height).toInt
          mSelectedFriends.requestLayout()
        }
      }

      override def willChangeBounds(): Boolean = {
        true
      }
    }

    outAnimation.setDuration(4 * (height / mSelectedFriends.getContext.getResources.getDisplayMetrics.density).toInt)
    mSelectedFriends.startAnimation(outAnimation)

    mFab.hide()
  }

  def destroySelectedContactsMini(): Unit = {
    mSelectedMini.setVisibility(View.GONE)
    mSelectedMini.setText("")
    mSelectedMiniExtended = false
  }

  def setOneSelectedContact(adapter: NewMessageRecyclerAdapter): Unit = {
    val first = adapter.getFirstSelected

    if (mSelectedMiniExtended) {
      destroySelectedContactsMini()
    }

    mSelectedFriendsImg.setImageResource(first.photoReference)
    mSelectedFriendsText.setText(first.userName)
    mSelectedFriendsButton.setImageResource(R.drawable.ic_content_clear)
    mSelectedFriendsButton.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = {
        adapter.clearSelectedList()
        destroySelectedContacts()
      }
    })
  }

  def setMultiSelectedContact(adapter: NewMessageRecyclerAdapter, selectedFriends: Int): Unit = {
    mSelectedFriendsCounter = this.findView(TR.new_message_selected_counter)
    mSelectedFriendsCounter.setText(selectedFriends + "")

    mSelectedFriendsText.setText(getResources.getString(R.string.new_message_selected_friends))
    mSelectedFriendsImg.setImageResource(R.color.backgroundColor)

    mSelectedFriendsButton.setImageResource(R.drawable.ic_hardware_keyboard_arrow_down)
    mSelectedFriendsButton.setOnClickListener(new OnClickListener {

      override def onClick(v: View): Unit = {
        if (mSelectedMiniExtended) {
          destroySelectedContactsMini()
          mSelectedFriendsButton.setImageResource(R.drawable.ic_hardware_keyboard_arrow_down)
        } else {
          mSelectedMini.setVisibility(View.VISIBLE)
          mSelectedMiniExtended = true
          mSelectedFriendsButton.setImageResource(R.drawable.ic_hardware_keyboard_arrow_up)
          createMiniContact(adapter)
        }
      }
    })
  }

  def createMiniContact(adapter: NewMessageRecyclerAdapter): Unit = {
    var friendsList: CharSequence = ""

    for (friend <- adapter.getSelectedFriends) {
      val sb = new SpannableStringBuilder()
      val miniContact = createContactTextView(friend.userName)
      val bd = convertViewToDrawable(miniContact)
      bd.setBounds(0, 0, bd.getIntrinsicWidth * 3, bd.getIntrinsicHeight * 3)

      sb.append(friend.userName + " ")
      sb.setSpan(new ImageSpan(bd), sb.length - (friend.userName.length + 1), sb.length - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
      mSelectedMini.setMovementMethod(LinkMovementMethod.getInstance())
      sb.setSpan(new ClickableSpan {
        override def onClick(widget: View): Unit = {
          var i = 0

          for (item <- adapter.getItems) {
            if (item.id == friend.id) {
              adapter.selectItem(i)
              selectItem(i)
            }
            i += 1
          }
        }
      }, sb.length - (friend.userName.length + 1), sb.length - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

      friendsList = TextUtils.concat(friendsList, sb)
    }

    mSelectedMini.setText(friendsList)
  }

  def convertViewToDrawable(view: View): BitmapDrawable = {
    val spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)

    view.measure(spec, spec)
    view.layout(0, 0, view.getMeasuredWidth, view.getMeasuredHeight)

    val bitmap = Bitmap.createBitmap(view.getMeasuredWidth, view.getMeasuredHeight, Bitmap.Config.ARGB_8888)

    val canvas = new Canvas(bitmap)
    canvas.translate(-view.getScrollX, -view.getScrollY)
    view.draw(canvas)

    view.setDrawingCacheEnabled(true)

    val cacheBmp = view.getDrawingCache()
    val viewBmp = cacheBmp.copy(Bitmap.Config.ARGB_8888, true)

    view.destroyDrawingCache()
    new BitmapDrawable(null, viewBmp)
  }

  def createContactTextView(text: String): LinearLayout = {
    val tv = getLayoutInflater.inflate(TR.layout.new_message_toolbar_friend_mini, null)
    tv.findView(TR.new_message_friends_mini_text).setText(text)

    tv
  }

  def selectItem(position: Int): Unit = {
    if (mSelectedFriends.getVisibility == View.GONE) {
      initFirstSelectedContacts(mFriends_Recycler_Adapter)
    } else {
      val selectedFriends = mFriends_Recycler_Adapter.selectedCount

      if (selectedFriends == 0) {

        destroySelectedContacts()

      } else if (selectedFriends == 1) {

        mSelectedFriendsCounter.setText("")
        mSelectedFriendsCounter = null

        setOneSelectedContact(mFriends_Recycler_Adapter)

      } else {

        setMultiSelectedContact(mFriends_Recycler_Adapter, selectedFriends)
        mSelectedMini = this.findView(TR.new_message_toolbar_mini)

        val friend = mFriends_Recycler_Adapter.getItem(position)

        if (mSelectedMiniExtended) {
          createMiniContact(mFriends_Recycler_Adapter)
        }
      }
    }
  }

}
