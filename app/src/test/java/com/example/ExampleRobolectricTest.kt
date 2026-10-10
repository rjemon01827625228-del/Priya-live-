package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.aistudio.neonparty.nxvqlr.MainActivity
import com.aistudio.neonparty.nxvqlr.R
import com.example.ui.storage.UserProfileStorage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Priya Live", appName)
  }

  @Test
  fun `verify permanent unique user id generation and persistence`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val id1 = UserProfileStorage.getActiveUserId(context)
    assertNotNull(id1)
    assertTrue("User ID should have length >= 6", id1.length >= 6)

    // Verify calling it again returns the exact same permanent ID
    val id2 = UserProfileStorage.getActiveUserId(context)
    assertEquals("Permanent User ID must remain identical across sessions", id1, id2)
  }

  @Test
  fun `verify MainActivity launches successfully`() {
    val controller = org.robolectric.Robolectric.buildActivity(MainActivity::class.java).setup()
    val activity = controller.get()
    assertNotNull(activity)
  }

  @Test
  fun `verify MainActivity launches successfully when logged in`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    UserProfileStorage.setLoggedIn(context, true)
    val controller = org.robolectric.Robolectric.buildActivity(MainActivity::class.java).setup()
    val activity = controller.get()
    assertNotNull(activity)
  }
}
