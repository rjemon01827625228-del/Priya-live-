package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.aistudio.neonparty.nxvqlr.R
import com.example.ui.components.AllCountriesList
import com.example.ui.components.AvatarFrameOverlay
import com.example.ui.components.CountryFilterDialog
import com.example.ui.components.CountryItem
import com.example.ui.components.GlobeWireframeIcon
import com.example.ui.components.GridTilesIcon
import com.example.ui.components.LiveSearchDialog
import com.example.ui.components.NeonCircleIconButton
import com.example.ui.components.NeonGoLivePill
import com.example.ui.components.NeonLiveBadge
import com.example.ui.components.NeonSubFilterPill
import com.example.ui.components.NeonWelcomeBanner
import com.example.ui.components.SlantedTopNavBar
import com.example.ui.components.StartVideoLiveDialog
import com.example.ui.components.StarIcon
import com.example.ui.components.SuperstarRankingDialog
import com.example.ui.components.TopNavTab
import com.example.ui.storage.ActiveLiveRoom
import com.example.ui.storage.LiveRoomStorage
import com.example.ui.storage.StoreCenterStorage
import com.example.ui.storage.UserProfileStorage
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPinkBright

data class LiveStreamItem(
  val id: String,
  val streamerName: String,
  val heartCount: String,
  val imageRes: Int,
  val isLive: Boolean = true,
  val countryCode: String = "BD",
  val countryFlag: String = "🇧🇩",
  val category: String = "Explore",
  val heartScoreNum: Long = 0L,
  val isPk: Boolean = false,
  val pkOpponentName: String? = null,
  val pkOpponentRes: Int? = null,
  val pkScoreLeft: String = "0",
  val pkScoreRight: String = "0",
  val isFollowed: Boolean = false,
  val rawRoom: ActiveLiveRoom? = null,
  val coverUri: String? = null
)

@Composable
fun LiveStreamingScreen(
  onStreamClick: (LiveStreamItem) -> Unit,
  onGoLiveClick: () -> Unit,
  onWhatsAppClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  // Top 4 Options state (Explore, New, PK, Follow)
  var selectedTopTab by remember { mutableStateOf(TopNavTab.EXPLORE) }

  // Second row options state (All, Popular, All Countries)
  var selectedSubTab by remember { mutableStateOf("All") }
  var selectedCountry by remember {
    mutableStateOf(AllCountriesList.first { it.code == "ALL" })
  }

  // Dialog states for Search, Country picker (45+ countries), and Superstar Ranking (Medal icon)
  var showCountryDialog by remember { mutableStateOf(false) }
  var showSearchDialog by remember { mutableStateOf(false) }
  var showRankingDialog by remember { mutableStateOf(false) }
  var showStartLiveDialog by remember { mutableStateOf(false) }

  val context = LocalContext.current
  val activeUserId = remember { UserProfileStorage.getActiveUserId(context) }
  val activeUserProfile = remember { UserProfileStorage.getProfileForUser(context, activeUserId) }
  val userCountryCode = remember(activeUserProfile.country) {
    if (activeUserProfile.country.length >= 2) activeUserProfile.country.take(2).uppercase() else "BD"
  }

  // Active Live Rooms from local storage, country prioritized
  var liveRoomsList by remember {
    mutableStateOf(LiveRoomStorage.getCountryPrioritizedRooms(context, userCountryCode))
  }

  DisposableEffect(showStartLiveDialog) {
    liveRoomsList = LiveRoomStorage.getCountryPrioritizedRooms(context, userCountryCode)
    onDispose { }
  }

  val allLiveStreams = remember(liveRoomsList) {
    liveRoomsList.map { room ->
      LiveStreamItem(
        id = room.roomId,
        streamerName = room.hostName,
        heartCount = if (room.heartScore > 0) "${room.heartScore}" else "0",
        imageRes = room.hostAvatarRes,
        isLive = true,
        countryCode = room.countryCode,
        countryFlag = room.flagEmoji,
        category = room.category,
        heartScoreNum = room.heartScore,
        rawRoom = room,
        coverUri = room.coverUri
      )
    }
  }

  // Filter streams based on selectedTopTab, selectedSubTab, and selectedCountry
  val displayedStreams = remember(allLiveStreams, selectedTopTab, selectedSubTab, selectedCountry) {
    var list = when (selectedTopTab) {
      TopNavTab.EXPLORE -> allLiveStreams.filter { it.category == "Explore" || it.category == "New" }
      TopNavTab.NEW -> allLiveStreams.filter { it.category == "New" }
      TopNavTab.PK -> allLiveStreams.filter { it.isPk }
      TopNavTab.FOLLOW -> allLiveStreams.filter { it.isFollowed }
    }

    // Country filter (if specific country selected)
    if (selectedCountry.code != "ALL") {
      val countryMatches = list.filter { it.countryCode.equals(selectedCountry.code, ignoreCase = true) }
      if (countryMatches.isNotEmpty()) {
        list = countryMatches
      }
    }

    // Sub-tab filter (All vs Popular)
    if (selectedSubTab == "Popular") {
      list = list.sortedByDescending { it.heartScoreNum }
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

    // 1. TOP NAVIGATION & FILTERS: 4 Slanted Glowing Pills (Explore, New, PK, Follow)
    SlantedTopNavBar(
      selectedTab = selectedTopTab,
      onTabSelected = { tab ->
        selectedTopTab = tab
      },
      modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(6.dp))

    // 2. SECONDARY FILTERS ROW: All, Popular, All Countries (45+), Search, Medal
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

      // "Popular" button
      NeonSubFilterPill(
        text = "Popular",
        iconContent = { StarIcon(tint = Color(0xFFFF4081)) },
        isSelected = selectedSubTab == "Popular",
        onClick = {
          selectedSubTab = "Popular"
        }
      )

      // "All Countries" (40+ countries modal sheet trigger)
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

      // Search Button (Opens Host & ID search dialog)
      NeonCircleIconButton(
        icon = Icons.Filled.Search,
        contentDescription = "Search Hosts and IDs",
        ringColor = NeonCyan,
        onClick = { showSearchDialog = true }
      )

      // Medal / Trophy Button (Opens Superstar Ranking dialog)
      NeonCircleIconButton(
        icon = Icons.Filled.EmojiEvents,
        contentDescription = "Superstar Rankings and Rewards",
        ringColor = NeonGold,
        onClick = { showRankingDialog = true }
      )
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Animated view switcher based on TopNavTab
    AnimatedContent(
      targetState = selectedTopTab,
      transitionSpec = { fadeIn() togetherWith fadeOut() },
      label = "tab_content_transition"
    ) { currentTab ->
      Column(modifier = Modifier.fillMaxWidth()) {
        when (currentTab) {
          TopNavTab.PK -> {
            // PK Battles View (Split screen vs cards)
            displayedStreams.forEach { pkItem ->
              PkBattleCard(
                item = pkItem,
                onClick = { onStreamClick(pkItem) }
              )
              Spacer(modifier = Modifier.height(12.dp))
            }

            // Promotional Welcome Banner
            NeonWelcomeBanner(
              modifier = Modifier
                .fillMaxWidth()
                .testTag("live_welcome_banner"),
              onClickWhatsApp = onWhatsAppClick
            )
          }

          else -> {
            // Standard 2-Column Grid (Explore, New, Follow)
            val chunkedStreams = displayedStreams.chunked(2)

            if (chunkedStreams.isEmpty()) {
              // Empty State (Strictly no fake demo rooms or fake users)
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 24.dp, horizontal = 12.dp)
                  .clip(RoundedCornerShape(20.dp))
                  .background(Color(0xFF140026))
                  .border(1.2.dp, Brush.horizontalGradient(listOf(NeonPink, NeonCyan)), RoundedCornerShape(20.dp))
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
                    text = if (selectedTopTab == TopNavTab.FOLLOW) "No Followed Streamers Live" else "No Live Broadcasters Right Now",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                  )
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = "Tap 'Go Live' below to start your real-time live video broadcast!",
                    color = Color(0xFFE088FF),
                    fontSize = 12.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                  )
                }
              }
            } else {
              // Row 1 of cards
              if (chunkedStreams.isNotEmpty()) {
                val row1 = chunkedStreams[0]
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  row1.forEach { stream ->
                    LiveStreamCard(
                      item = stream,
                      modifier = Modifier.weight(1f),
                      onClick = { onStreamClick(stream) }
                    )
                  }
                  if (row1.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                  }
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              // Center Promotional Banner Board
              NeonWelcomeBanner(
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("live_welcome_banner"),
                onClickWhatsApp = onWhatsAppClick
              )

              Spacer(modifier = Modifier.height(10.dp))

              // Remaining rows of cards
              if (chunkedStreams.size > 1) {
                chunkedStreams.drop(1).forEach { rowStreams ->
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    rowStreams.forEach { stream ->
                      LiveStreamCard(
                        item = stream,
                        modifier = Modifier.weight(1f),
                        onClick = { onStreamClick(stream) }
                      )
                    }
                    if (rowStreams.size == 1) {
                      Spacer(modifier = Modifier.weight(1f))
                    }
                  }
                  Spacer(modifier = Modifier.height(10.dp))
                }
              }
            }
          }
        }
      }
    }

    // Bottom padding to clear floating navigation bar & button
    Spacer(modifier = Modifier.height(110.dp))
    }

    // FLOATING GO LIVE ACTION BUTTON (Positioned at bottom-right right above bottom navigation bar)
    Box(
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(end = 14.dp, bottom = 18.dp)
    ) {
      NeonGoLivePill(
        onClick = { showStartLiveDialog = true },
        modifier = Modifier.testTag("live_floating_action_btn")
      )
    }
  }

  // -------------------------------------------------------------------------
  // INTERACTIVE DIALOGS (Start Live, 45+ Countries, Search, Rankings)
  // -------------------------------------------------------------------------

  if (showStartLiveDialog) {
    StartVideoLiveDialog(
      onDismiss = { showStartLiveDialog = false },
      onStartLive = { title, coverUri ->
        showStartLiveDialog = false
        val newRoom = ActiveLiveRoom(
          roomId = "live_${activeUserId}_${System.currentTimeMillis()}",
          hostUserId = activeUserId,
          hostName = activeUserProfile.username,
          hostAvatarUri = activeUserProfile.profileImageUri,
          hostAvatarRes = R.drawable.img_profile_max,
          roomTitle = title.ifBlank { "${activeUserProfile.username}'s Live" },
          coverUri = coverUri?.toString(),
          countryCode = userCountryCode,
          countryName = activeUserProfile.country.ifBlank { "Bangladesh" },
          flagEmoji = activeUserProfile.countryFlag.ifBlank { "🇧🇩" },
          category = "Explore",
          startTimeMs = System.currentTimeMillis(),
          viewerCount = 1,
          heartScore = 0L
        )
        LiveRoomStorage.saveOrUpdateRoom(context, newRoom)
        liveRoomsList = LiveRoomStorage.getCountryPrioritizedRooms(context, userCountryCode)
        onStreamClick(
          LiveStreamItem(
            id = newRoom.roomId,
            streamerName = newRoom.hostName,
            heartCount = "0",
            imageRes = newRoom.hostAvatarRes,
            isLive = true,
            countryCode = newRoom.countryCode,
            countryFlag = newRoom.flagEmoji,
            category = newRoom.category,
            heartScoreNum = 0L,
            rawRoom = newRoom,
            coverUri = newRoom.coverUri
          )
        )
      }
    )
  }

  if (showCountryDialog) {
    CountryFilterDialog(
      selectedCountryCode = selectedCountry.code,
      onCountrySelected = { country ->
        selectedCountry = country
        showCountryDialog = false
      },
      onDismiss = { showCountryDialog = false }
    )
  }

  if (showSearchDialog) {
    LiveSearchDialog(
      availableStreams = allLiveStreams,
      onStreamSelected = { stream ->
        onStreamClick(stream)
        showSearchDialog = false
      },
      onDismiss = { showSearchDialog = false }
    )
  }

  if (showRankingDialog) {
    SuperstarRankingDialog(
      onDismiss = { showRankingDialog = false }
    )
  }
}

/**
 * 2-Column Live Stream Card component with neon borders,
 * top-left LIVE badge, country flag, and bottom streamer badge.
 */
@Composable
fun LiveStreamCard(
  item: LiveStreamItem,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  val context = LocalContext.current
  val equippedFrame = remember(item.rawRoom?.hostUserId) {
    item.rawRoom?.hostUserId?.let { StoreCenterStorage.getEquippedFrame(context, it) }
  }

  Box(
    modifier = modifier
      .aspectRatio(0.95f)
      .shadow(
        elevation = 10.dp,
        shape = RoundedCornerShape(14.dp),
        ambientColor = NeonCyan,
        spotColor = NeonPink
      )
      .clip(RoundedCornerShape(14.dp))
      .border(
        width = 1.8.dp,
        brush = Brush.linearGradient(
          listOf(
            NeonPinkBright,
            Color(0xFF8A00D4),
            NeonCyan,
            NeonPinkBright
          )
        ),
        shape = RoundedCornerShape(14.dp)
      )
      .background(Color(0xFF0F001E))
      .clickable(onClick = onClick)
      .testTag("card_stream_${item.id}")
  ) {
    // Background placeholder
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            listOf(Color(0xFF28183E), Color(0xFF180A2E))
          )
        )
    )

    // Streamer photo or cover
    if (!item.coverUri.isNullOrBlank()) {
      AsyncImage(
        model = item.coverUri,
        contentDescription = item.streamerName,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
      )
    } else if (!item.rawRoom?.hostAvatarUri.isNullOrBlank()) {
      AsyncImage(
        model = item.rawRoom?.hostAvatarUri,
        contentDescription = item.streamerName,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
      )
    } else {
      Image(
        painter = painterResource(id = item.imageRes),
        contentDescription = item.streamerName,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
      )
    }

    // Top-left "🎙 LIVE" badge
    NeonLiveBadge(
      modifier = Modifier
        .align(Alignment.TopStart)
        .padding(start = 6.dp, top = 6.dp)
    )

    // Top-right Country Flag
    Box(
      modifier = Modifier
        .align(Alignment.TopEnd)
        .padding(end = 6.dp, top = 6.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(Color(0xCC000000))
        .padding(horizontal = 5.dp, vertical = 2.dp)
    ) {
      Text(
        text = item.countryFlag,
        fontSize = 12.sp
      )
    }

    // Bottom streamer banner: Avatar with equipped Store Frame, name, and heart count
    Row(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth()
        .background(
          Brush.verticalGradient(
            listOf(Color.Transparent, Color(0xCC080016), Color(0xF005000F))
          )
        )
        .padding(horizontal = 6.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        AvatarFrameOverlay(
          size = 26.dp,
          frame = equippedFrame,
          profileImageUri = item.rawRoom?.hostAvatarUri,
          avatarRes = item.imageRes
        )

        Spacer(modifier = Modifier.width(5.dp))

        Text(
          text = item.streamerName,
          color = Color.White,
          fontSize = 11.5.sp,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Filled.Favorite,
          contentDescription = null,
          tint = Color(0xFFFF1744),
          modifier = Modifier.size(11.dp)
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
          text = item.heartCount,
          color = Color.White,
          fontSize = 10.5.sp,
          fontWeight = FontWeight.Black
        )
      }
    }
  }
}

/**
 * Split PK Battle Card for the "PK" Tab
 * Shows 2 streamers facing off with PK scores, VS badge, and battle timer
 */
@Composable
fun PkBattleCard(
  item: LiveStreamItem,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(175.dp)
      .shadow(elevation = 12.dp, shape = RoundedCornerShape(18.dp), ambientColor = NeonPink)
      .clip(RoundedCornerShape(18.dp))
      .border(
        width = 2.dp,
        brush = Brush.horizontalGradient(
          listOf(Color(0xFFFF0055), Color(0xFF9D00FF), Color(0xFF00E5FF))
        ),
        shape = RoundedCornerShape(18.dp)
      )
      .background(Color(0xFF0A0017))
      .clickable(onClick = onClick)
      .testTag("pk_battle_card_${item.id}")
  ) {
    // 2 Streamers Split Screen
    Row(modifier = Modifier.fillMaxSize()) {
      // Left Streamer
      Box(modifier = Modifier.weight(1f).fillMaxSize()) {
        Image(
          painter = painterResource(id = item.imageRes),
          contentDescription = item.streamerName,
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )
        // Left name tag
        Box(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(8.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xCCFF0055))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = "${item.streamerName} (${item.pkScoreLeft})",
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black
          )
        }
      }

      // Right Streamer
      Box(modifier = Modifier.weight(1f).fillMaxSize()) {
        Image(
          painter = painterResource(id = item.pkOpponentRes ?: item.imageRes),
          contentDescription = item.pkOpponentName ?: "Opponent",
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )
        // Right name tag
        Box(
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(8.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xCC0091EA))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = "${item.pkOpponentName ?: "Max"} (${item.pkScoreRight})",
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black
          )
        }
      }
    }

    // Top Center PK Battle Timer Badge
    Row(
      modifier = Modifier
        .align(Alignment.TopCenter)
        .padding(top = 8.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(Color(0xEE0D0022))
        .border(1.dp, NeonGold, RoundedCornerShape(12.dp))
        .padding(horizontal = 10.dp, vertical = 3.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "⚡ PK BATTLE  03:42",
        color = NeonGold,
        fontSize = 10.sp,
        fontWeight = FontWeight.Black
      )
    }

    // Center Glowing "VS" Badge
    Box(
      modifier = Modifier
        .align(Alignment.Center)
        .size(46.dp)
        .shadow(12.dp, CircleShape, ambientColor = NeonPink)
        .clip(CircleShape)
        .background(
          Brush.linearGradient(listOf(Color(0xFFFF007F), Color(0xFF6A00FF)))
        )
        .border(2.dp, Color.White, CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "VS",
        color = Color.White,
        fontSize = 17.sp,
        fontWeight = FontWeight.Black
      )
    }
  }
}
