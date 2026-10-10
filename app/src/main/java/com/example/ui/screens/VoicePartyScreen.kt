package com.example.ui.screens

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import coil.compose.AsyncImage
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.AllCountriesList
import com.example.ui.components.AudioPartyActiveRoomDialog
import com.example.ui.components.AvatarFrameOverlay
import com.example.ui.components.CountryFilterDialog
import com.example.ui.components.CreateAudioPartyRoomDialog
import com.example.ui.components.EqualizerVisualizer
import com.example.ui.components.GlobeWireframeIcon
import com.example.ui.components.GridTilesIcon
import com.example.ui.components.LiveSearchDialog
import com.example.ui.components.NeonCircleIconButton
import com.example.ui.components.NeonPartyPill
import com.example.ui.components.NeonSubFilterPill
import com.example.ui.components.NeonWelcomeBanner
import com.example.ui.components.SlantedTopNavBar
import com.example.ui.components.StarIcon
import com.example.ui.components.SuperstarRankingDialog
import com.example.ui.components.TopNavTab
import com.example.ui.storage.SavedAudioRoom
import com.example.ui.storage.StoreCenterStorage
import com.example.ui.storage.UserProfileStorage
import com.example.ui.storage.UserRoomStorage
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPinkBright

data class DynamicAudioPartyRoom(
  val id: String,
  val title: String,
  val hostName: String,
  val hostAvatarRes: Int,
  val country: String,
  val countryCode: String,
  val flagEmoji: String,
  val onlineUserCount: Int,
  val displayCount: String,
  val category: String = "Explore",
  val tags: List<String> = listOf("Music", "Chill"),
  val boardBorderBrush: Brush,
  val headerColor: Color = Color.White,
  val isFollowed: Boolean = false,
  val isPk: Boolean = false,
  val hostAvatarUri: String? = null,
  val hostUserId: String = "",
  val hostPID: String = "",
  val seatCount: Int = 8
)

@Composable
fun VoicePartyScreen(
  onRoomClick: (DynamicAudioPartyRoom) -> Unit,
  onWhatsAppClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  // Top 4 Navigation Tab state (Explore, New, PK, Follow)
  var selectedTopTab by remember { mutableStateOf(TopNavTab.EXPLORE) }

  // Secondary Filter states (All, Popular, All Countries)
  var selectedSubTab by remember { mutableStateOf("All") }
  var selectedCountry by remember {
    mutableStateOf(AllCountriesList.first { it.code == "ALL" })
  }

  // Dialog states for Search, Country Picker, and Superstar Ranking
  var showCountryDialog by remember { mutableStateOf(false) }
  var showSearchDialog by remember { mutableStateOf(false) }
  var showRankingDialog by remember { mutableStateOf(false) }

  // Party Room Creation & Active Room Dialogs
  var showCreatePartyRoomDialog by remember { mutableStateOf(false) }
  var activeUserRoom by remember { mutableStateOf<SavedAudioRoom?>(null) }
  var showActiveRoomDialog by remember { mutableStateOf(false) }

  val activeUserId = remember { UserProfileStorage.getActiveUserId(context) }
  val activeUserProfile = remember { UserProfileStorage.getProfileForUser(context, activeUserId) }
  var isAudioRoomCreated by remember { mutableStateOf(UserRoomStorage.isRoomCreated(context)) }
  var localPartyRooms by remember { mutableStateOf(UserRoomStorage.getAllActiveRooms(context)) }

  DisposableEffect(isAudioRoomCreated, showActiveRoomDialog, showCreatePartyRoomDialog) {
    localPartyRooms = UserRoomStorage.getAllActiveRooms(context)
    onDispose { }
  }

  // Dynamic Party Boards from local active rooms
  val allPartyBoards = remember(localPartyRooms, isAudioRoomCreated) {
    val boards = mutableListOf<DynamicAudioPartyRoom>()

    for (room in localPartyRooms) {
      val cCode = if (room.country.length >= 2) room.country.take(2).uppercase() else "BD"
      val flag = if (room.flagEmoji.isNotBlank()) room.flagEmoji else "🇧🇩"
      val count = 1
      val cleanTitle = room.title.replace("👑", "").replace("🎧", "").trim().ifBlank { "Cyber Sound Lounge" }

      boards.add(
        DynamicAudioPartyRoom(
          id = room.id,
          title = cleanTitle,
          hostName = activeUserProfile.username.ifBlank { "Host" },
          hostAvatarRes = R.drawable.img_profile_max,
          country = room.country,
          countryCode = cCode,
          flagEmoji = flag,
          onlineUserCount = count,
          displayCount = "$count",
          category = "Explore",
          tags = listOf("Voice", "Party"),
          boardBorderBrush = Brush.horizontalGradient(
            listOf(Color(0xFF00E676), Color(0xFF00B0FF), Color(0xFFBD00FF))
          ),
          headerColor = Color(0xFF69F0AE),
          isFollowed = true,
          hostAvatarUri = room.coverUriString ?: activeUserProfile.profileImageUri,
          hostUserId = room.creatorUserId,
          hostPID = room.creatorUserId,
          seatCount = room.seatCount
        )
      )
    }

    boards
  }

  // Filter party boards based on navigation tab, sub filter, and country
  val displayedBoards = remember(selectedTopTab, selectedSubTab, selectedCountry) {
    var list = when (selectedTopTab) {
      TopNavTab.EXPLORE -> allPartyBoards.filter { it.category == "Explore" || it.category == "New" }
      TopNavTab.NEW -> allPartyBoards.filter { it.category == "New" }
      TopNavTab.PK -> allPartyBoards.filter { it.isPk }
      TopNavTab.FOLLOW -> allPartyBoards.filter { it.isFollowed }
    }

    if (selectedCountry.code != "ALL") {
      val countryMatches = list.filter { it.countryCode.equals(selectedCountry.code, ignoreCase = true) }
      if (countryMatches.isNotEmpty()) {
        list = countryMatches
      }
    }

    if (selectedSubTab == "Popular") {
      list = list.sortedByDescending { it.onlineUserCount }
    }

    list
  }

  val scrollState = rememberScrollState()
  val topSafePadding = com.example.ui.components.rememberResponsiveTopBarPadding(extraTopSpacing = 2.dp)

  Box(
    modifier = modifier.fillMaxSize()
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(start = 8.dp, end = 8.dp, top = topSafePadding, bottom = 4.dp)
    ) {
    Spacer(modifier = Modifier.height(2.dp))

    // 1. TOP NAVIGATION TABS: Exact SlantedTopNavBar (Explore, New, PK, Follow)
    SlantedTopNavBar(
      selectedTab = selectedTopTab,
      onTabSelected = { tab ->
        selectedTopTab = tab
      },
      modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(6.dp))

    // 2. SECONDARY FILTERS ROW: All, Popular, All Countries (45+ countries), Search, Medal
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 4.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      // "All" button
      NeonSubFilterPill(
        text = "All",
        iconContent = { GridTilesIcon() },
        isSelected = selectedSubTab == "All" && selectedCountry.code == "ALL",
        onClick = {
          selectedSubTab = "All"
          selectedCountry = AllCountriesList.first { it.code == "ALL" }
        }
      )

      // "Popular" button (Sorts by highest online count)
      NeonSubFilterPill(
        text = "Popular",
        iconContent = { StarIcon(tint = Color(0xFFFF4081)) },
        isSelected = selectedSubTab == "Popular",
        onClick = {
          selectedSubTab = "Popular"
        }
      )

      // "All Countries" dropdown (45+ countries picker)
      val countryButtonText = if (selectedCountry.code == "ALL") {
        "All Countries"
      } else {
        "${selectedCountry.flagEmoji} ${selectedCountry.name.take(9)}"
      }

      NeonSubFilterPill(
        text = countryButtonText,
        iconContent = { GlobeWireframeIcon() },
        trailingContent = {
          Icon(
            imageVector = Icons.Filled.ArrowDropDown,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(16.dp)
          )
        },
        isSelected = selectedCountry.code != "ALL",
        onClick = {
          showCountryDialog = true
        }
      )

      Spacer(modifier = Modifier.weight(1f))

      // Search Button (Search Party Rooms and Hosts)
      NeonCircleIconButton(
        icon = Icons.Filled.Search,
        contentDescription = "Search Party Rooms",
        ringColor = NeonCyan,
        onClick = { showSearchDialog = true }
      )

      // Superstar Ranking Medal
      NeonCircleIconButton(
        icon = Icons.Filled.EmojiEvents,
        contentDescription = "Superstar Rankings",
        ringColor = NeonGold,
        onClick = { showRankingDialog = true }
      )
    }

    Spacer(modifier = Modifier.height(8.dp))

    // 3. ANIMATED PARTY BOARDS CONTENT
    AnimatedContent(
      targetState = selectedTopTab,
      transitionSpec = { fadeIn() togetherWith fadeOut() },
      label = "party_tab_transition"
    ) { currentTab ->
      Column(modifier = Modifier.fillMaxWidth()) {
        if (displayedBoards.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 24.dp, horizontal = 12.dp)
              .clip(RoundedCornerShape(20.dp))
              .background(Color(0xFF140026))
              .border(1.2.dp, Brush.horizontalGradient(listOf(NeonCyan, NeonPink)), RoundedCornerShape(20.dp))
              .padding(24.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                Icons.Filled.Mic,
                contentDescription = null,
                tint = NeonCyan,
                modifier = Modifier.size(42.dp)
              )
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = if (currentTab == TopNavTab.FOLLOW) "No Followed Party Rooms Active" else "No Active Voice Party Rooms",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "Tap 'Party' below to create and start your real-time audio voice room!",
                color = Color(0xFFE088FF),
                fontSize = 12.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
            }
          }
        } else {
          // Display the first 2 boards
          val firstBatch = displayedBoards.take(2)
          val remainingBatch = displayedBoards.drop(2)

          firstBatch.forEach { board ->
            DynamicAudioRoomCard(
              room = board,
              onClick = { onRoomClick(board) }
            )
            Spacer(modifier = Modifier.height(10.dp))
          }

          // High-End Banner Presentation in the middle
          NeonWelcomeBanner(
            modifier = Modifier
              .fillMaxWidth()
              .testTag("party_welcome_banner"),
            onClickWhatsApp = onWhatsAppClick
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Display the remaining boards
          remainingBatch.forEach { board ->
            DynamicAudioRoomCard(
              room = board,
              onClick = { onRoomClick(board) }
            )
            Spacer(modifier = Modifier.height(10.dp))
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(110.dp))
    }

    // FLOATING PARTY ACTION BUTTON (Positioned at bottom-right right above the bottom navigation bar, matching user red circle)
    Box(
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(end = 14.dp, bottom = 18.dp)
    ) {
      NeonPartyPill(
        onClick = { showCreatePartyRoomDialog = true },
        modifier = Modifier.testTag("party_floating_action_btn")
      )
    }
  }

  // DIALOGS:
  // 1. Country Filter Dialog
  if (showCountryDialog) {
    CountryFilterDialog(
      selectedCountryCode = selectedCountry.code,
      onCountrySelected = { chosenCountry ->
        selectedCountry = chosenCountry
        showCountryDialog = false
      },
      onDismiss = { showCountryDialog = false }
    )
  }

  // 2. Search Dialog
  if (showSearchDialog) {
    LiveSearchDialog(
      availableStreams = emptyList(),
      onStreamSelected = { _ ->
        showSearchDialog = false
      },
      onDismiss = { showSearchDialog = false }
    )
  }

  // 3. Superstar Ranking Dialog
  if (showRankingDialog) {
    SuperstarRankingDialog(
      onDismiss = { showRankingDialog = false }
    )
  }

  // Create Audio Party Room Dialog (with Gallery Photo Picker!)
  if (showCreatePartyRoomDialog) {
    CreateAudioPartyRoomDialog(
      onDismiss = { showCreatePartyRoomDialog = false },
      onCreateRoom = { title, coverUri, country, flag ->
        val cleanTitle = title.replace("👑", "").replace("🎧", "").trim().ifBlank { "Cyber Sound Lounge" }
        val savedRoom = UserRoomStorage.getSavedRoom(context).copy(
          title = cleanTitle,
          coverUriString = coverUri?.toString(),
          country = country,
          flagEmoji = flag
        )
        activeUserRoom = savedRoom
        isAudioRoomCreated = true
        val cCode = if (country.length >= 2) country.take(2).uppercase() else "BD"
        onRoomClick(
          DynamicAudioPartyRoom(
            id = savedRoom.id,
            title = cleanTitle,
            hostName = activeUserProfile.username.ifBlank { "Host" },
            hostAvatarRes = R.drawable.img_profile_max,
            country = savedRoom.country,
            countryCode = cCode,
            flagEmoji = savedRoom.flagEmoji.ifBlank { "🇧🇩" },
            onlineUserCount = 1,
            displayCount = "1",
            category = "Explore",
            tags = listOf("Voice", "Party"),
            boardBorderBrush = Brush.horizontalGradient(
              listOf(Color(0xFF00E676), Color(0xFF00B0FF), Color(0xFFBD00FF))
            ),
            headerColor = Color(0xFF69F0AE),
            isFollowed = true,
            hostAvatarUri = savedRoom.coverUriString ?: activeUserProfile.profileImageUri,
            hostUserId = savedRoom.creatorUserId,
            hostPID = savedRoom.creatorUserId,
            seatCount = savedRoom.seatCount
          )
        )
      }
    )
  }

  // Active User Audio Party Room (with ⚙️ Settings Option to change cover & title!)
  if (showActiveRoomDialog && activeUserRoom != null) {
    AudioPartyActiveRoomDialog(
      room = activeUserRoom!!,
      onDismiss = { showActiveRoomDialog = false }
    )
  }
}

/**
 * Enhanced Dynamic Audio Party Card
 * Features:
 * - Distinct country flag & country pill
 * - Distinct styling & custom title for each board
 * - Dynamic online user count outside/inside with live indicator
 * - Animated soundwave visualizer
 */
@Composable
fun DynamicAudioRoomCard(
  room: DynamicAudioPartyRoom,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  val context = LocalContext.current
  val activeUserId = remember { UserProfileStorage.getActiveUserId(context) }
  val equippedFrame = remember(activeUserId) {
    StoreCenterStorage.getEquippedFrame(context, activeUserId)
  }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .shadow(elevation = 8.dp, shape = RoundedCornerShape(18.dp), ambientColor = Color(0x3300E5FF))
      .clip(RoundedCornerShape(18.dp))
      .border(
        width = 1.8.dp,
        brush = room.boardBorderBrush,
        shape = RoundedCornerShape(18.dp)
      )
      .background(
        Brush.horizontalGradient(
          listOf(
            Color(0xFF140026),
            Color(0xFF1D0036),
            Color(0xFF120021)
          )
        )
      )
      .clickable(onClick = onClick)
      .padding(10.dp)
      .testTag("audio_card_${room.id}")
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Left: Square Board Picture with slightly rounded corners (Requirements 8 & 9)
      Box(
        modifier = Modifier
          .size(72.dp)
          .aspectRatio(1f)
          .clip(RoundedCornerShape(12.dp))
          .background(Color(0xFF1E0A3C))
          .border(
            width = 1.6.dp,
            brush = room.boardBorderBrush,
            shape = RoundedCornerShape(12.dp)
          )
          .testTag("room_avatar_${room.id}"),
        contentAlignment = Alignment.Center
      ) {
        if (!room.hostAvatarUri.isNullOrBlank()) {
          AsyncImage(
            model = room.hostAvatarUri,
            contentDescription = "Board Picture",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
          )
        } else if (room.hostAvatarRes != 0) {
          Image(
            painter = painterResource(id = room.hostAvatarRes),
            contentDescription = "Board Picture",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
          )
        } else {
          Icon(
            imageVector = Icons.Filled.MusicNote,
            contentDescription = "Board Icon",
            tint = Color(0xFF00E5FF),
            modifier = Modifier.size(32.dp)
          )
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      // Middle: Room Details (Title, Country, Host, Tags)
      Column(modifier = Modifier.weight(1f)) {
        // Room Name with distinctive header style & color
        Text(
          text = room.title,
          color = room.headerColor,
          fontSize = 15.sp,
          fontWeight = FontWeight.Black,
          maxLines = 1
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Host name & Country with Flag
        Text(
          text = "${room.flagEmoji} Host: ${room.hostName} • ${room.country}",
          color = Color(0xFFB39DDB),
          fontSize = 11.sp,
          maxLines = 1
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Category & tags
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          room.tags.forEach { tag ->
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0x33BD00FF))
                .border(0.6.dp, Color(0x66BD00FF), RoundedCornerShape(6.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "#$tag",
                color = Color(0xFFE1BEE7),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

      // Right: Dynamic live user count badge & Equalizer
      Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Center
      ) {
        // Dynamic User Count Badge (e.g. 👥 3.45K Online with green live dot)
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF260047))
            .border(1.dp, Color(0x5500E5FF), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .clip(CircleShape)
              .background(Color(0xFF00E676))
          )
          Spacer(modifier = Modifier.width(4.dp))
          Icon(
            imageVector = Icons.Filled.People,
            contentDescription = null,
            tint = NeonCyan,
            modifier = Modifier.size(12.dp)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = room.displayCount,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Soundwave Equalizer
        EqualizerVisualizer(count = 10, modifier = Modifier.height(16.dp))
      }
    }
  }
}
