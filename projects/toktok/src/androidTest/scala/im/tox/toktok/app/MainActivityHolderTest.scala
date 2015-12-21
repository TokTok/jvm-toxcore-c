package im.tox.toktok.app

import android.test.ActivityInstrumentationTestCase2
import com.robotium.solo.Solo
import junit.framework.Assert.assertTrue

object MainActivityHolderTest extends ActivityInstrumentationTestCase2[MainActivityHolder](classOf[MainActivityHolder]) {

  private var solo: Solo = null

  override def setUp(): Unit = {
    super.setUp()
    solo = new Solo(getInstrumentation, getActivity)
  }

  override def tearDown(): Unit = {
    solo.finishOpenedActivities()
  }

  def testTypeMessage(): Unit = {
    // Click on friend Lorem Ipsum.
    solo.clickOnText("Lorem Ipsum")
    // Type a message.
    solo.typeText(0, "Nonexistent message")
    // Click the send button.
    solo.clickOnImageButton(2)
    // Check that the message field is now empty.
    val text = solo.getEditText(0).getText.toString
    assertTrue(s"Edit field should be empty, but contained '$text'", text.isEmpty)
    // Check that the typed message can still be found (as delivered message).
    assertTrue(solo.searchText("Nonexistent message"))
  }

}
