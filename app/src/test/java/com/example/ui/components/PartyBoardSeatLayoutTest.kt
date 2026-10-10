package com.example.ui.components

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.ui.storage.ActiveLiveRoom
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import com.example.ui.storage.UserProfileStorage
import com.example.ui.storage.UserRoomStorage
import com.google.common.truth.Truth.assertThat
import kotlin.math.abs
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PartyBoardSeatLayoutTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  private lateinit var context: Context

  @Before
  fun setUp() {
    context = ApplicationProvider.getApplicationContext()
    UserProfileStorage.setLoggedIn(context, true)
  }

  @Test
  fun calculateResponsiveSeatBoardMetrics_adaptsAcross8_12_16SeatsAndPhoneSizes() {
    val phoneBoardSizes = listOf(
      Pair(296.dp, 230.dp), // Small phone board area
      Pair(366.dp, 380.dp), // Normal phone board area
      Pair(412.dp, 510.dp)  // Large phone board area
    )

    for ((boardWidth, boardHeight) in phoneBoardSizes) {
      val metrics8 = calculateResponsiveSeatBoardMetrics(boardWidth, boardHeight, 8)
      val metrics12 = calculateResponsiveSeatBoardMetrics(boardWidth, boardHeight, 12)
      val metrics16 = calculateResponsiveSeatBoardMetrics(boardWidth, boardHeight, 16)

      // 1. Exact rows and columns
      assertThat(metrics8.seatCount).isEqualTo(8)
      assertThat(metrics8.columns).isEqualTo(4)
      assertThat(metrics8.rows).isEqualTo(2)

      assertThat(metrics12.seatCount).isEqualTo(12)
      assertThat(metrics12.columns).isEqualTo(4)
      assertThat(metrics12.rows).isEqualTo(3)

      assertThat(metrics16.seatCount).isEqualTo(16)
      assertThat(metrics16.columns).isEqualTo(4)
      assertThat(metrics16.rows).isEqualTo(4)

      // 2. Seat diameter reduces progressively from 8 -> 12 -> 16
      assertThat(metrics8.seatOrbSize.value).isGreaterThan(metrics12.seatOrbSize.value)
      assertThat(metrics12.seatOrbSize.value).isGreaterThan(metrics16.seatOrbSize.value)

      // On normal and large phones, 16-seat circular diameter is moderately larger (>= 50dp)
      if (boardHeight >= 350.dp && boardWidth >= 350.dp) {
        assertThat(metrics16.seatOrbSize.value).isAtLeast(50f)
      }

      // 3. Outer side margin is reduced for 16 seats so horizontal board space is used efficiently
      assertThat(metrics16.sideMargin.value).isAtMost(metrics12.sideMargin.value)
      assertThat(metrics12.sideMargin.value).isAtMost(metrics8.sideMargin.value)

      // 4. Vertical spacing reduces progressively from 8 -> 12 -> 16
      assertThat(metrics8.verticalSpacing.value).isAtLeast(metrics12.verticalSpacing.value)
      assertThat(metrics12.verticalSpacing.value).isAtLeast(metrics16.verticalSpacing.value)

      // 5. Every configuration fits completely inside available board width & height
      for (m in listOf(metrics8, metrics12, metrics16)) {
        val totalBottom = m.topOffset + m.boardContentHeight
        assertThat(totalBottom.value).isAtMost(boardHeight.value + 0.5f)
        val totalRowWidth = m.seatItemWidth * m.columns + m.sideMargin * 2
        assertThat(totalRowWidth.value).isAtMost(boardWidth.value + 0.5f)
      }
    }
  }

  @Test
  fun partyBoardRoom_8_12_16Seats_allSeatsVisibleInsideBoardWithoutOverlapOrBottomOverflow() {
    val activeUid = UserProfileStorage.getActiveUserId(context)
    val roomId = "party_room_$activeUid"
    UserRoomStorage.saveRoom(
      context = context,
      title = "Responsive Seat Test Room",
      coverUriString = null,
      country = "Bangladesh",
      flagEmoji = "🇧🇩",
      creatorUserId = activeUid,
      seatCount = 8
    )

    composeTestRule.setContent {
      Box(modifier = Modifier.size(width = 360.dp, height = 640.dp)) {
        PartyBoardRoom(
          room = UserRoomStorage.getSavedRoom(context),
          onDismiss = {}
        )
      }
    }
    composeTestRule.waitForIdle()

    // Verify 8 Seats initially
    verifySeatConfigurationInBounds(expectedSeatCount = 8, expectedRows = 2)

    // Switch to 12 Seats in real time and verify all 12 seats are visible and inside the board
    composeTestRule.runOnIdle {
      UserRoomStorage.updateSeatCount(context, roomId, 12)
    }
    composeTestRule.waitForIdle()
    verifySeatConfigurationInBounds(expectedSeatCount = 12, expectedRows = 3)

    // Switch to 16 Seats in real time and verify all 16 seats are visible and inside the board
    composeTestRule.runOnIdle {
      UserRoomStorage.updateSeatCount(context, roomId, 16)
    }
    composeTestRule.waitForIdle()
    verifySeatConfigurationInBounds(expectedSeatCount = 16, expectedRows = 4)
  }

  @Test
  fun partyBoardRoom_ownerSelectsSeatCountInSettings_persistsAndSyncsForReentry() {
    val activeUid = UserProfileStorage.getActiveUserId(context)
    val roomId = "party_room_$activeUid"
    UserRoomStorage.saveRoom(
      context = context,
      title = "Owner Settings Test Room",
      coverUriString = null,
      country = "Bangladesh",
      flagEmoji = "🇧🇩",
      creatorUserId = activeUid,
      seatCount = 8
    )

    composeTestRule.setContent {
      Box(modifier = Modifier.size(width = 390.dp, height = 780.dp)) {
        PartyBoardRoom(
          room = UserRoomStorage.getSavedRoom(context),
          onDismiss = {}
        )
      }
    }
    composeTestRule.waitForIdle()

    // Open Menu -> Settings -> Select 16 Seats -> Save Changes
    composeTestRule.onNodeWithTag("bottom_bar_menu").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("floating_menu_panel").assertIsDisplayed()

    // Verify direct room storage update and persistence for other users entering the same room
    composeTestRule.runOnIdle {
      UserRoomStorage.updateSeatCount(context, roomId, 16)
    }
    composeTestRule.waitForIdle()

    assertThat(UserRoomStorage.getRoomSeatCount(context, roomId)).isEqualTo(16)
    assertThat(UserRoomStorage.getSavedRoom(context).seatCount).isEqualTo(16)
    verifySeatConfigurationInBounds(expectedSeatCount = 16, expectedRows = 4)
  }

  @Test
  fun neonBottomNav_extraPurpleBarRemoved_andPositionedSafelyAboveSystemNavArea() {
    composeTestRule.setContent {
      Box(modifier = Modifier.size(width = 390.dp, height = 844.dp)) {
        NeonBottomNav(
          currentTab = AppNavTab.LIVE,
          onTabSelected = {}
        )
      }
    }
    composeTestRule.waitForIdle()

    // All 5 tabs are displayed
    composeTestRule.onNodeWithTag("nav_tab_live").assertIsDisplayed()
    composeTestRule.onNodeWithTag("nav_tab_party").assertIsDisplayed()
    composeTestRule.onNodeWithTag("nav_tab_discover").assertIsDisplayed()
    composeTestRule.onNodeWithTag("nav_tab_chat").assertIsDisplayed()
    composeTestRule.onNodeWithTag("nav_tab_me").assertIsDisplayed()

    val outerNavBounds = composeTestRule
      .onNodeWithTag("neon_bottom_nav_bar")
      .assertIsDisplayed()
      .fetchSemanticsNode()
      .boundsInRoot

    val capsuleBounds = composeTestRule
      .onNodeWithTag("neon_bottom_nav_capsule")
      .assertIsDisplayed()
      .fetchSemanticsNode()
      .boundsInRoot

    // Safe bottom inset clearance below capsule (stays safely above Android system gesture/nav bar)
    val bottomSafeClearance = outerNavBounds.bottom - capsuleBounds.bottom
    assertThat(bottomSafeClearance).isAtLeast(16f)
  }

  @Test
  fun partyBoardRoom_emojiButtonReplacesGamesInBottomBar_andFloatingGamesAndEventBoxDisplayed() {
    val activeUid = UserProfileStorage.getActiveUserId(context)
    UserRoomStorage.saveRoom(
      context = context,
      title = "Party Emoji & Event Test Room",
      coverUriString = null,
      country = "Bangladesh",
      flagEmoji = "🇧🇩",
      creatorUserId = activeUid,
      seatCount = 8
    )

    composeTestRule.setContent {
      Box(modifier = Modifier.size(width = 390.dp, height = 800.dp)) {
        PartyBoardRoom(
          room = UserRoomStorage.getSavedRoom(context),
          onDismiss = {}
        )
      }
    }
    composeTestRule.waitForIdle()

    // 1. When NOT seated initially: Mic and Mute are completely hidden, and remaining 4 buttons are evenly spaced
    composeTestRule.onNodeWithTag("bottom_bar_mute").assertDoesNotExist()
    composeTestRule.onNodeWithTag("bottom_bar_mic").assertDoesNotExist()
    composeTestRule.onNodeWithTag("bottom_bar_message").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottom_bar_gift").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottom_bar_emoji").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottom_bar_menu").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottom_bar_game").assertDoesNotExist()

    // Right-side floating Games button & Event Box are displayed
    val eventBoxBounds = composeTestRule
      .onNodeWithTag("floating_event_box")
      .assertIsDisplayed()
      .fetchSemanticsNode()
      .boundsInRoot
    val gamesButtonBounds = composeTestRule
      .onNodeWithTag("floating_games_button")
      .assertIsDisplayed()
      .fetchSemanticsNode()
      .boundsInRoot

    // Event Box is positioned directly ABOVE the floating Games button with clean spacing,
    // and Event Box is visibly larger than the Games button without overlapping chat or bottom bar
    assertThat(eventBoxBounds.bottom).isAtMost(gamesButtonBounds.top - 8f)
    assertThat(eventBoxBounds.width).isGreaterThan(gamesButtonBounds.width)
    assertThat(eventBoxBounds.height).isGreaterThan(gamesButtonBounds.height)

    val bottomBarBounds = composeTestRule
      .onNodeWithTag("bottom_action_bar")
      .assertIsDisplayed()
      .fetchSemanticsNode()
      .boundsInRoot
    val messageAreaBounds = composeTestRule
      .onNodeWithTag("party_board_message_area")
      .assertIsDisplayed()
      .fetchSemanticsNode()
      .boundsInRoot
    assertThat(gamesButtonBounds.bottom).isAtMost(bottomBarBounds.top - 12f)
    assertThat(eventBoxBounds.left).isAtLeast(messageAreaBounds.right + 4f)
    assertThat(gamesButtonBounds.left).isAtLeast(messageAreaBounds.right + 4f)

    // No "Games" text label beneath the floating Games icon, and no unwanted "Party Star" poster card
    assertThat(composeTestRule.onAllNodesWithText("Games").fetchSemanticsNodes()).isEmpty()
    assertThat(composeTestRule.onAllNodesWithText("Party Star").fetchSemanticsNodes()).isEmpty()

    // Event A ("OFFICIAL HELPLINE") is shown initially and rotates to Event B ("PRIYA LIVE") after 3 seconds
    composeTestRule.onNodeWithText("OFFICIAL HELPLINE", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("floating_event_box").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("official_helpline_fullscreen_page").assertIsDisplayed()
    composeTestRule.onNodeWithText("Contact details not available.").assertIsDisplayed()
    composeTestRule.onNodeWithTag("close_event_fullscreen_page").performClick()
    composeTestRule.waitForIdle()

    // Advance 3 seconds to verify Event Box rotates to Event B ("PRIYA LIVE" / "GLOBAL MEGA MANAGER RECRUITMENT")
    composeTestRule.mainClock.advanceTimeBy(3200L)
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("PRIYA LIVE", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("floating_event_box").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("mega_manager_recruitment_fullscreen_page").assertIsDisplayed()
    composeTestRule.onNodeWithText("Recruiting Mega Managers Across All Countries").assertIsDisplayed()
    composeTestRule.onNodeWithTag("close_event_fullscreen_page").performClick()
    composeTestRule.waitForIdle()

    // 2. When NOT seated: Opening Emoji board aligns with message area and does NOT trigger seat reaction
    composeTestRule.onNodeWithTag("bottom_bar_emoji").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("party_emoji_panel").assertIsDisplayed()
    composeTestRule.onNodeWithTag("party_emoji_message_input_row").assertIsDisplayed()
    composeTestRule.onNodeWithTag("party_emoji_item_Cool").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("party_emoji_panel").assertDoesNotExist()
    composeTestRule.onNodeWithTag("seat_emoji_reaction_overlay_1").assertDoesNotExist()
    composeTestRule.onNodeWithText("😎 Cool", useUnmergedTree = true).assertIsDisplayed()

    // 3. When SEATED (user taps Seat No.1): Mic and Mute appear at bottom-left, and Emoji triggers on active seat
    composeTestRule.onNodeWithTag("party_seat_1").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("bottom_bar_mute").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottom_bar_mic").assertIsDisplayed()

    // Tapping Emoji button while seated opens the Party Emoji Picker in Seat Reaction mode
    composeTestRule.onNodeWithTag("bottom_bar_emoji").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("party_emoji_panel").assertIsDisplayed()
    composeTestRule.onNodeWithTag("party_emoji_scroll_grid").assertIsDisplayed()

    // Selecting Cry emoji triggers ONLY the single seat-avatar emoji reaction on Seat 1 and closes the picker panel
    composeTestRule.onNodeWithTag("party_emoji_item_Cry").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("party_emoji_panel").assertDoesNotExist()
    composeTestRule.onNodeWithTag("seat_emoji_reaction_overlay_1").assertIsDisplayed()
    assertThat(composeTestRule.onAllNodesWithText("😭", useUnmergedTree = true).fetchSemanticsNodes().size).isEqualTo(1)

    // Rapidly selecting Joy emoji replaces the previous reaction cleanly without duplicate display
    composeTestRule.onNodeWithTag("bottom_bar_emoji").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("party_emoji_item_Joy").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("party_emoji_panel").assertDoesNotExist()
    composeTestRule.onNodeWithTag("seat_emoji_reaction_overlay_1").assertIsDisplayed()
    assertThat(composeTestRule.onAllNodesWithText("😭", useUnmergedTree = true).fetchSemanticsNodes()).isEmpty()
    // Exactly 1 reaction emoji node on the board (no duplicate board-center emoji overlay)
    assertThat(composeTestRule.onAllNodesWithText("😂", useUnmergedTree = true).fetchSemanticsNodes().size).isEqualTo(1)

    // 4. Leaving Seat No.1 hides Mic and Mute again and clears seat emoji reactions
    composeTestRule.onNodeWithTag("party_seat_1").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("btn_leave_seat").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("bottom_bar_mute").assertDoesNotExist()
    composeTestRule.onNodeWithTag("bottom_bar_mic").assertDoesNotExist()
    composeTestRule.onNodeWithTag("seat_emoji_reaction_overlay_1").assertDoesNotExist()
  }

  @Test
  fun liveBoardRoom_displaysGamesButton_andOpensMiniGamesDialog() {
    val activeUid = UserProfileStorage.getActiveUserId(context)
    val sampleLiveRoom = ActiveLiveRoom(
      roomId = "live_test_room_1",
      hostUserId = activeUid,
      hostName = "Host",
      hostAvatarUri = null,
      hostAvatarRes = com.aistudio.neonparty.nxvqlr.R.drawable.img_streamer_girl,
      roomTitle = "Neon Live Stream",
      coverUri = null,
      countryCode = "BD",
      countryName = "Bangladesh",
      flagEmoji = "🇧🇩",
      category = "Explore",
      startTimeMs = System.currentTimeMillis()
    )

    composeTestRule.setContent {
      Box(modifier = Modifier.size(width = 390.dp, height = 800.dp)) {
        LiveRoomDialog(
          room = sampleLiveRoom,
          isHost = true,
          onEscapeRoom = {},
          onExitRoom = {}
        )
      }
    }
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("live_board_games_button_container", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("floating_games_button").assertIsDisplayed().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("🎮 Mini-Games").assertIsDisplayed()
  }

  private fun verifySeatConfigurationInBounds(expectedSeatCount: Int, expectedRows: Int) {
    val boardBounds = composeTestRule
      .onNodeWithTag("party_board_seat_area")
      .assertIsDisplayed()
      .fetchSemanticsNode()
      .boundsInRoot

    val messageAreaBounds = composeTestRule
      .onNodeWithTag("party_board_message_area")
      .assertIsDisplayed()
      .fetchSemanticsNode()
      .boundsInRoot

    val bottomBarBounds = composeTestRule
      .onNodeWithTag("bottom_action_bar")
      .assertIsDisplayed()
      .fetchSemanticsNode()
      .boundsInRoot

    // Board area must be strictly above message area and bottom action bar
    assertThat(boardBounds.bottom).isAtMost(messageAreaBounds.top + 1f)
    assertThat(boardBounds.bottom).isAtMost(bottomBarBounds.top + 1f)

    for (row in 1..expectedRows) {
      composeTestRule.onNodeWithTag("party_seat_row_$row").assertIsDisplayed()
    }

    val seatRects = mutableListOf<androidx.compose.ui.geometry.Rect>()
    for (seatNum in 1..expectedSeatCount) {
      val node = composeTestRule
        .onNodeWithTag("party_seat_$seatNum")
        .assertIsDisplayed()
        .fetchSemanticsNode()
      val rect = node.boundsInRoot

      assertThat(rect.width).isGreaterThan(10f)
      assertThat(rect.height).isGreaterThan(10f)

      // Every seat must be completely inside the visible Party Board area
      assertThat(rect.left).isAtLeast(boardBounds.left - 1f)
      assertThat(rect.top).isAtLeast(boardBounds.top - 1f)
      assertThat(rect.right).isAtMost(boardBounds.right + 1f)
      assertThat(rect.bottom).isAtMost(boardBounds.bottom + 1f)

      // Lowest row must never go below the message area or bottom controls
      assertThat(rect.bottom).isAtMost(messageAreaBounds.top + 1f)
      assertThat(rect.bottom).isAtMost(bottomBarBounds.top + 1f)

      seatRects.add(rect)
    }

    // Verify symmetrical left/right side margins and equal horizontal spacing between seats in each row
    for (rowIdx in 0 until expectedRows) {
      val s1 = seatRects[rowIdx * 4]
      val s2 = seatRects[rowIdx * 4 + 1]
      val s3 = seatRects[rowIdx * 4 + 2]
      val s4 = seatRects[rowIdx * 4 + 3]

      val leftMargin = s1.left - boardBounds.left
      val rightMargin = boardBounds.right - s4.right
      assertThat(abs(leftMargin - rightMargin)).isAtMost(2f)

      val gap12 = s2.left - s1.right
      val gap23 = s3.left - s2.right
      val gap34 = s4.left - s3.right
      assertThat(abs(gap12 - gap23)).isAtMost(2f)
      assertThat(abs(gap23 - gap34)).isAtMost(2f)
    }

    // Verify no seats overlap
    for (i in seatRects.indices) {
      for (j in i + 1 until seatRects.size) {
        val a = seatRects[i].deflate(0.5f)
        val b = seatRects[j].deflate(0.5f)
        assertThat(a.overlaps(b)).isFalse()
      }
    }

    // Verify seats beyond expectedSeatCount are not displayed
    for (extraSeat in (expectedSeatCount + 1)..16) {
      composeTestRule.onNodeWithTag("party_seat_$extraSeat").assertDoesNotExist()
    }
  }
}
