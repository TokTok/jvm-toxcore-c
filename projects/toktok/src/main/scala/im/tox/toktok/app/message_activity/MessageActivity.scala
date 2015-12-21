package im.tox.toktok.app.message_activity

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.PixelFormat
import android.os.Bundle
import android.support.design.widget.{FloatingActionButton, Snackbar}
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.support.v7.widget.{CardView, LinearLayoutManager, RecyclerView, Toolbar}
import android.text.{Editable, TextWatcher}
import android.util.{Log, TypedValue}
import android.view.View.OnClickListener
import android.view.ViewGroup.LayoutParams
import android.view._
import android.view.animation.Animation.AnimationListener
import android.view.animation.{Animation, AnimationUtils}
import android.widget._
import im.tox.toktok.TypedResource._
import im.tox.toktok.app.contacts.FileSendActivity
import im.tox.toktok.app.main.friends.SlideInContactsLayout
import im.tox.toktok.app.simple_dialogs.{SimpleDialogDesign, SimpleTextDialogDesign}
import im.tox.toktok.app.{Friend, Message, SizeAnimation}
import im.tox.toktok.{R, TContext, TR}

import scala.collection.mutable.ListBuffer

final class MessageActivity extends AppCompatActivity with MessageClick with MessageActionMode {

  var mMenu: Menu = null
  var mToolbar: Toolbar = null
  var mUserName: TextView = null
  var mStatus: ImageView = null
  var contactColorPrimary: Int = 0
  var contactColorStatus: Int = 0
  var typeOfMessage: Int = 0
  var header: RelativeLayout = null
  var mFab: FloatingActionButton = null
  var mSendButtonActive: Boolean = false
  var title: String = ""
  var imgSRC: Int = 0
  var mInputLayout: CardView = null
  var mInput: EditText = null
  var mRecyclerAdapter: MessageAdapter = null
  var mRecycler: RecyclerView = null
  var overlayContactsLayout: SlideInContactsLayout = null
  var overlay_attachments: SlideInAttachmentsLayout = null
  var mActionMode: ActionMode = null
  var actionModeCallback = new MessageActionModeCallback

  protected override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_message)

    val bundle = getIntent.getExtras

    contactColorPrimary = bundle.getInt("contactColorPrimary")
    contactColorStatus = bundle.getInt("contactColorStatus")
    typeOfMessage = bundle.getInt("messageType")
    title = bundle.getString("messageTitle")
    imgSRC = bundle.getInt("imgResource")

    initToolbar()
    initRecyclerView()
    initInput()
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId match {
      case android.R.id.home =>
        finish()
        true

      case R.id.action_recall_message =>
        val recallMessageIntent = new Intent(this, classOf[MessageRecallActivity])
        val bundle = new Bundle()
        bundle.putInt("colorPrimary", contactColorPrimary)
        bundle.putInt("colorPrimaryStatus", contactColorStatus)
        recallMessageIntent.putExtras(bundle)
        startActivity(recallMessageIntent)
        true

      case R.id.action_group_members =>
        val contactListIntent = new Intent(this, classOf[MessageGroupContacts])
        val bundle = new Bundle()
        bundle.putInt("colorPrimary", contactColorPrimary)
        bundle.putInt("colorPrimaryStatus", contactColorStatus)
        contactListIntent.putExtras(bundle)
        startActivity(contactListIntent)
        true

      case R.id.action_see_files_list =>
        val bundle = new Bundle

        bundle.putString("contactName", title)
        bundle.putInt("contactColorPrimary", contactColorPrimary)
        bundle.putInt("contactColorStatus", contactColorStatus)

        val newIntent = new Intent(this, classOf[FileSendActivity])
        newIntent.putExtras(bundle)
        startActivity(newIntent)

        true

      case R.id.action_leave_conversation =>
        val dial = new SimpleDialogDesign(
          this,
          getResources.getString(R.string.dialog_leave_group),
          contactColorPrimary,
          R.drawable.ic_delete_black_48dp,
          null
        )
        dial.show()
        true

      case R.id.action_rename_conversation =>
        val dial = new SimpleTextDialogDesign(
          this,
          getResources.getString(R.string.dialog_edit_group_name),
          contactColorPrimary,
          R.drawable.ic_editor_mode_edit_48,
          title,
          null
        )
        dial.show()
        true

      case R.id.action_mute_conversation_group =>
        val snack = Snackbar.make(
          this.findView(TR.coordinatorLayout),
          getResources.getString(R.string.message_snackbar_group_muted),
          Snackbar.LENGTH_LONG
        )
        snack.setAction("Undo", new OnClickListener {
          override def onClick(v: View): Unit = {
            Log.d("asdasd", "asdsad")
          }
        })
        snack.setActionTextColor(contactColorPrimary)
        val snackView = snack.getView
        snackView.setBackgroundResource(R.color.snackBarColor)
        snackView.setElevation(10)
        val snackText = snackView.findView(TR.snackbar_text)
        snackText.setTextColor(getResources.getColor(R.color.textDarkColor, null))
        snack.show()
        true

      case R.id.action_delete_conversation =>
        val dial = new SimpleDialogDesign(
          this,
          getResources.getString(R.string.dialog_delete_conversion),
          contactColorPrimary,
          R.drawable.ic_delete_black_48dp,
          null
        )
        dial.show()
        true

      case R.id.action_mute_conversation_single =>
        val snack = Snackbar.make(
          this.findView(TR.coordinatorLayout),
          getResources.getString(R.string.message_snackbar_friend_muted),
          Snackbar.LENGTH_LONG
        )
        snack.setAction("Undo", new OnClickListener {
          override def onClick(v: View): Unit = {
            Log.d("asdasd", "asdsad")
          }
        })
        snack.setActionTextColor(contactColorPrimary)
        val snackView = snack.getView
        snackView.setBackgroundResource(R.color.snackBarColor)
        snackView.setElevation(10)
        val snackText = snackView.findView(TR.snackbar_text)
        snackText.setTextColor(getResources.getColor(R.color.textDarkColor, null))
        snack.show()
        true

      case _ =>
        super.onOptionsItemSelected(item)
    }
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    mMenu = menu

    if (typeOfMessage == 0) {
      getMenuInflater.inflate(R.menu.menu_single_message, menu)
    } else {
      getMenuInflater.inflate(R.menu.menu_group_message, menu)
    }

    super.onCreateOptionsMenu(menu)
    true
  }

  def initToolbar(): Unit = {
    mToolbar = this.findView(TR.message_toolbar)
    mToolbar.setBackgroundColor(contactColorPrimary)

    setSupportActionBar(mToolbar)
    getSupportActionBar.setDisplayHomeAsUpEnabled(true)
    getSupportActionBar.setDisplayShowTitleEnabled(false)

    getWindow.setStatusBarColor(contactColorStatus)

    overlay_attachments = this.findView(TR.fragment_attachments_slide)
    header = this.findView(TR.message_header)

    val params = new RelativeLayout.LayoutParams(
      ViewGroup.LayoutParams.WRAP_CONTENT,
      ViewGroup.LayoutParams.WRAP_CONTENT
    )
    params.addRule(RelativeLayout.CENTER_VERTICAL)

    if (typeOfMessage == 0) {

      header.addView(getLayoutInflater.inflate(TR.layout.message_header_user, null, true), params)
      header.findView(TR.message_header_user_img).setImageResource(imgSRC)
      header.setOnClickListener(new OnClickListener {
        override def onClick(v: View): Unit = {
          startOverLayFriend()
        }
      })

    } else {

      header.addView(getLayoutInflater.inflate(TR.layout.message_header_group, null, true), params)
      header.setOnClickListener(new OnClickListener {
        override def onClick(v: View): Unit = {
          val contactListIntent = new Intent(MessageActivity.this, classOf[MessageGroupContacts])
          val bundle = new Bundle()
          bundle.putInt("colorPrimary", contactColorPrimary)
          bundle.putInt("colorPrimaryStatus", contactColorStatus)
          contactListIntent.putExtras(bundle)
          startActivity(contactListIntent)
        }
      })

    }

    mUserName = header.findView(TR.message_header_title)
    mUserName.setText(title)

  }

  def initRecyclerView(): Unit = {
    mRecycler = this.findView(TR.message_recycler)
    val list = new ListBuffer[Message]

    list += Message(3, "Smiled ", "", imgSRC)
    list += Message(2, "Thanks André Almeida, let's hope soo.", "14:30 Delivered", imgSRC)
    list += Message(1, "Welcome to TokTok " + title + ", I hope you love it, as much as I do \uD83D\uDE00", "14:30 Delivered", R.drawable.user)

    if (imgSRC == 0) {
      imgSRC = R.drawable.lorem
      list += Message(3, "The Amazing Group was created", "", R.drawable.user)
    }

    val mLayoutManager = new LinearLayoutManager(getBaseContext)
    mLayoutManager.setReverseLayout(true)
    mRecycler.setHasFixedSize(true)
    mRecyclerAdapter = new MessageAdapter(list, this, this)

    mRecycler.setAdapter(mRecyclerAdapter)
    mRecycler.setLayoutManager(mLayoutManager)

    mRecycler.setItemAnimator(new MessageItemAnimator)

    val attachButton = this.findView(TR.message_attachments_button)

    attachButton.setOnClickListener(new OnClickListener {

      override def onClick(v: View): Unit = {
        val imm = MessageActivity.this.getSystemService(TContext.INPUT_METHOD_SERVICE)
        imm.hideSoftInputFromWindow(mInput.getApplicationWindowToken, 0)

        overlay_attachments.start()
      }

    })

  }

  def initInput(): Unit = {
    mInput = this.findView(TR.message_input)
    mInputLayout = this.findView(TR.message_input_cardview)
    mFab = this.findView(TR.message_fab)
    mFab.setBackgroundTintList(ColorStateList.valueOf(contactColorPrimary))

    mFab.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = {
        mRecyclerAdapter.addItem(Message(1, mInput.getText.toString, "14:30 Delivered", R.drawable.user))
        mRecycler.smoothScrollToPosition(0)
        mInput.setText("")
      }
    })

    if (typeOfMessage == 0) {
      mInput.setHint(getResources.getString(R.string.message_hint_single) + " " + title)
    }

    mInput.addTextChangedListener(new TextWatcher {

      override def beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int): Unit = {
      }

      override def onTextChanged(s: CharSequence, start: Int, before: Int, count: Int): Unit = {
        val countVal = mInput.getText.length()

        if (countVal > 0 && !mSendButtonActive) {
          shrinkInputBar()
        } else if (countVal == 0 && mSendButtonActive) {
          expandInputBar()
        }
      }

      override def afterTextChanged(s: Editable): Unit = {
      }

    })
  }

  def shrinkInputBar(): Unit = {
    val animationInput = new SizeAnimation(mInputLayout, mInputLayout.getWidth - mFab.getWidth - mFab.getPaddingRight, 0)
    animationInput.setDuration(250)
    mInputLayout.startAnimation(animationInput)

    val animationButton = AnimationUtils.loadAnimation(this, R.anim.fab_from_right)
    animationButton.setDuration(250)

    animationButton.setAnimationListener(new AnimationListener {
      override def onAnimationEnd(animation: Animation): Unit = {}

      override def onAnimationStart(animation: Animation): Unit = {
        mFab.setVisibility(View.VISIBLE)
      }

      override def onAnimationRepeat(animation: Animation): Unit = {}
    })

    mFab.startAnimation(animationButton)

    mSendButtonActive = true
  }

  def expandInputBar(): Unit = {
    val animationInput = new SizeAnimation(mInputLayout, mInputLayout.getWidth + mFab.getWidth + mFab.getPaddingRight, 0)
    animationInput.setDuration(250)
    mInputLayout.startAnimation(animationInput)

    val animationButton = AnimationUtils.loadAnimation(this, R.anim.fab_to_right)
    animationButton.setDuration(250)

    animationButton.setAnimationListener(new AnimationListener {
      override def onAnimationEnd(animation: Animation): Unit = {
        mFab.setVisibility(View.INVISIBLE)
      }

      override def onAnimationStart(animation: Animation): Unit = {}

      override def onAnimationRepeat(animation: Animation): Unit = {}
    })

    mFab.startAnimation(animationButton)

    mSendButtonActive = false
  }

  def onImgClick(): Unit = {
    startOverLayFriend()
  }

  def startOverLayFriend(): Unit = {
    overlayContactsLayout = getLayoutInflater.inflate(TR.layout.overlay_contacts, null)
    val params = new WindowManager.LayoutParams(
      LayoutParams.MATCH_PARENT,
      LayoutParams.MATCH_PARENT,
      WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG,
      WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
      PixelFormat.TRANSLUCENT
    )
    val window = this.getSystemService(TContext.WINDOW_SERVICE)

    window.addView(overlayContactsLayout, params)

    var actionBarHeight = 0

    val tv = new TypedValue()
    if (getTheme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
      actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources.getDisplayMetrics)
    }

    overlayContactsLayout.start(this, Friend.lorem, actionBarHeight)
  }

  override def onBackPressed(): Unit = {
    if (overlay_attachments.getVisibility == View.INVISIBLE) {
      finish()
    } else {
      overlay_attachments.finish()
    }
  }

  def onClick(i: Int) = {
    if (mActionMode != null) {
      mRecyclerAdapter.toggleSelection(i)

      val count = mRecyclerAdapter.getSelectedItemCount

      if (count == 0) {
        mActionMode.finish()
      } else if (count == 1) {
        mActionMode.setTitle(count + " " + getResources.getString(R.string.action_mode_selected_single))
      } else {
        mActionMode.setTitle(count + " " + getResources.getString(R.string.action_mode_selected_multi))
      }
    }
  }

  def onLongClick(i: Int): Boolean = {
    if (mActionMode == null) {
      mActionMode = startSupportActionMode(actionModeCallback)
    }

    mRecyclerAdapter.toggleSelection(i)

    val count = mRecyclerAdapter.getSelectedItemCount

    if (count == 0) {
      mActionMode.finish()
    } else if (count == 1) {
      mActionMode.setTitle(count + " " + getResources.getString(R.string.action_mode_selected_single))
    } else {
      mActionMode.setTitle(count + " " + getResources.getString(R.string.action_mode_selected_multi))
    }

    true
  }

  final class MessageActionModeCallback() extends ActionMode.Callback {

    override def onDestroyActionMode(mode: ActionMode): Unit = {
      mActionMode = null
      mRecyclerAdapter.setActionModeActive(false)
      mRecyclerAdapter.clearSelections()
      val animationIn = AnimationUtils.loadAnimation(mInputLayout.getContext, R.anim.abc_fade_in)
      mInputLayout.setVisibility(View.VISIBLE)
      mInputLayout.startAnimation(animationIn)
    }

    override def onCreateActionMode(mode: ActionMode, menu: Menu): Boolean = {
      val menuInflater = mode.getMenuInflater
      menuInflater.inflate(R.menu.message_action_mode, menu)

      mRecyclerAdapter.setActionModeActive(true)
      mRecyclerAdapter.notifyDataSetChanged()

      val animationOut = AnimationUtils.loadAnimation(mInputLayout.getContext, R.anim.abc_slide_out_bottom)
      mInputLayout.startAnimation(animationOut)
      mInputLayout.setVisibility(View.GONE)

      true
    }

    override def onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean = {
      item.getItemId match {
        case R.id.action_message_delete =>
          mRecyclerAdapter.deleteSelected()
          mode.finish()
      }

      true
    }

    override def onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean = {
      false
    }

  }

}
