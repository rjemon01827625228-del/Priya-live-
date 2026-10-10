package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.storage.LocalImageStorage
import com.example.ui.storage.SavedAudioRoom
import com.example.ui.storage.UserProfileStorage
import com.example.ui.storage.UserRoomStorage
import com.google.firebase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class PartyRoomChatMessage(
  val messageId: String = "",
  val senderUid: String = "",
  val senderName: String = "",
  val senderAvatarUri: String? = null,
  val senderLevel: Int = 1,
  val content: String = "",
  val timestamp: Long = System.currentTimeMillis()
)

data class PartyRoomSeatState(
  var seatNumber: Int = 1,
  val isOccupied: Boolean = false,
  val isLocked: Boolean = false,
  val userId: String? = null,
  val userName: String? = null,
  val userAvatarUri: String? = null,
  val userFrameId: String? = null,
  val userFrameUrl: String? = null,
  val userLevel: Int = 1,
  val userGender: String = "Female",
  val userCountry: String = "Bangladesh",
  val userCountryFlag: String = "🇧🇩",
  val isSpeaking: Boolean = false,
  val isMuted: Boolean = false
)

data class PartyRoomStateData(
  val roomId: String = "",
  val roomTitle: String = "Party Board",
  val hostUid: String = "",
  val hostName: String = "Host",
  val hostAvatarUri: String? = null,
  val hostUserId: String = "",
  val hostPID: String = "",
  val coverUri: String? = null,
  val isLocked: Boolean = false,
  val seatCount: Int = 8,
  val viewerCount: Int = 1,
  val coinPool: Long = 0L
)

/**
 * Visual Color Palettes for the Glowing Seats matching the reference image:
 * No.1: Royal Purple / Violet
 * No.2: Electric Blue / Cobalt
 * No.3: Golden Amber / Orange
 * No.4: Hot Pink / Magenta
 * No.5: Radiant Crimson / Red
 * No.6: Electric Aqua / Sky Cyan
 * No.7: Neon Violet / Purple
 * No.8: Vibrant Emerald Green
 */
data class SeatColorTheme(
  val primaryColor: Color,
  val secondaryColor: Color,
  val glowColor: Color,
  val borderBrush: Brush
)

fun getSeatTheme(seatNumber: Int): SeatColorTheme {
  return when ((seatNumber - 1) % 8) {
    0 -> SeatColorTheme(
      primaryColor = Color(0xFF9C27B0),
      secondaryColor = Color(0xFF6A1B9A),
      glowColor = Color(0xFFD500F9),
      borderBrush = Brush.sweepGradient(listOf(Color(0xFFD500F9), Color(0xFFBA68C8), Color(0xFF8E24AA), Color(0xFFD500F9)))
    )
    1 -> SeatColorTheme(
      primaryColor = Color(0xFF0070F3),
      secondaryColor = Color(0xFF0D47A1),
      glowColor = Color(0xFF00D9FF),
      borderBrush = Brush.sweepGradient(listOf(Color(0xFF00D9FF), Color(0xFF2979FF), Color(0xFF0052CC), Color(0xFF00D9FF)))
    )
    2 -> SeatColorTheme(
      primaryColor = Color(0xFFFF9100),
      secondaryColor = Color(0xFFE65100),
      glowColor = Color(0xFFFFD700),
      borderBrush = Brush.sweepGradient(listOf(Color(0xFFFFD700), Color(0xFFFF9100), Color(0xFFFF6D00), Color(0xFFFFD700)))
    )
    3 -> SeatColorTheme(
      primaryColor = Color(0xFFFF1493),
      secondaryColor = Color(0xFF880E4F),
      glowColor = Color(0xFFFF4081),
      borderBrush = Brush.sweepGradient(listOf(Color(0xFFFF4081), Color(0xFFFF007F), Color(0xFFC2185B), Color(0xFFFF4081)))
    )
    4 -> SeatColorTheme(
      primaryColor = Color(0xFFFF1744),
      secondaryColor = Color(0xFFB71C1C),
      glowColor = Color(0xFFFF5252),
      borderBrush = Brush.sweepGradient(listOf(Color(0xFFFF5252), Color(0xFFFF1744), Color(0xFFD50000), Color(0xFFFF5252)))
    )
    5 -> SeatColorTheme(
      primaryColor = Color(0xFF00E5FF),
      secondaryColor = Color(0xFF006064),
      glowColor = Color(0xFF00B0FF),
      borderBrush = Brush.sweepGradient(listOf(Color(0xFF00E5FF), Color(0xFF18FFFF), Color(0xFF0091EA), Color(0xFF00E5FF)))
    )
    6 -> SeatColorTheme(
      primaryColor = Color(0xFFA855F7),
      secondaryColor = Color(0xFF4A148C),
      glowColor = Color(0xFFC084FC),
      borderBrush = Brush.sweepGradient(listOf(Color(0xFFC084FC), Color(0xFFA855F7), Color(0xFF7C3AED), Color(0xFFC084FC)))
    )
    else -> SeatColorTheme(
      primaryColor = Color(0xFF00E676),
      secondaryColor = Color(0xFF1B5E20),
      glowColor = Color(0xFF10B981),
      borderBrush = Brush.sweepGradient(listOf(Color(0xFF10B981), Color(0xFF00E676), Color(0xFF00C853), Color(0xFF10B981)))
    )
  }
}

/**
 * Animated Floating Gift / Reaction on Party Board
 */
data class ActiveGiftDisplay(
  val id: Long,
  val emoji: String,
  val title: String,
  val sender: String
)

/**
 * Animated Emoji Reaction displayed centered on a User's Seat Avatar
 */
data class ActiveSeatEmojiReaction(
  val id: Long,
  val seatNumber: Int,
  val emoji: String,
  val label: String = ""
)

enum class PartyEventId {
  OFFICIAL_HELPLINE,
  MEGA_MANAGER_RECRUITMENT
}

data class PartyEventSlide(
  val eventId: PartyEventId,
  val badgeEmoji: String,
  val title: String,
  val heading: String,
  val subtitle: String,
  val gradientColors: List<Color>,
  val accentColor: Color
)

/**
 * FULL-SCREEN INTERACTIVE PARTY BOARD
 * Closely follows the reference image:
 * - Scenic mountain lake twilight balcony background
 * - Top header with Host capsule, user ID, audience count, share, more, exit, and PK button
 * - Center: 8 seats initially (expandable to 12 or 16 maximum) in a 2-row layout
 * - Exact glowing circular seat designs with ||| 🎤 ||| audio wave bars inside empty state
 * - Bottom action bar with exactly 6 options:
 *   Mute | Mic | Message | Gift | Game | Menu
 * - Menu button is at extreme bottom-right, opening a floating dark/neon glassmorphism popup:
 *   ⚙️ Settings, 🎵 Music, 🔒 Room Lock, 🔐 Seat Lock, 🔓 Seat Open
 * - Local room state synchronization: no fake users, no fake chats, no fake coins, no fake games.
 */
@Composable
fun PartyBoardRoom(
  room: SavedAudioRoom,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()

  // ---------------------------------------------------------------------------
  // AUTHENTICATED REAL USER DATA (From Profile Storage)
  // ---------------------------------------------------------------------------
  val currentUserId = remember { UserProfileStorage.getActiveUserId(context) }
  val userProfile = remember { UserProfileStorage.getProfileForUser(context, currentUserId) }
  var userWalletCoins by remember { mutableLongStateOf(UserProfileStorage.getCoinBalance(context, currentUserId)) }
  val parsedUserLevel = remember(userProfile.levelBadge) {
    userProfile.levelBadge.filter { it.isDigit() }.toIntOrNull() ?: 1
  }

  // Room Identifier
  val roomId = remember(room.id, room.title, room.creatorUserId) {
    if (room.id.isNotBlank()) {
      room.id
    } else if (room.creatorUserId.isNotBlank()) {
      "party_room_${room.creatorUserId}"
    } else {
      "party_${currentUserId}_${room.title.hashCode().toString().replace("-", "")}"
    }
  }

  // Determine if current user is owner (ONLY the host who created/opened this room!)
  val isOwner = remember(room.creatorUserId, currentUserId) {
    if (room.creatorUserId.isNotBlank()) {
      room.creatorUserId == currentUserId
    } else {
      true
    }
  }

  // ---------------------------------------------------------------------------
  // ROOM STATE (Synchronized in real time with UserRoomStorage & Firestore)
  // ---------------------------------------------------------------------------
  val initialSeatCount = remember(roomId, room.seatCount) {
    UserRoomStorage.getRoomSeatCount(context, roomId, room.seatCount)
  }

  var roomData by remember(roomId) {
    val cleanInitialTitle = room.title.replace("👑", "").replace("🎧", "").trim().ifBlank { "Cyber Sound Lounge" }
    mutableStateOf(
      PartyRoomStateData(
        roomId = roomId,
        roomTitle = cleanInitialTitle,
        hostUid = currentUserId,
        hostName = userProfile.username.ifBlank { "Host" },
        hostAvatarUri = userProfile.profileImageUri,
        hostUserId = if (room.creatorUserId.isNotBlank()) room.creatorUserId else currentUserId,
        coverUri = room.coverUriString,
        isLocked = false,
        seatCount = initialSeatCount,
        viewerCount = 1,
        coinPool = 0L
      )
    )
  }

  // Active seats map (seatNumber -> PartyRoomSeatState) initialized for all 1..16 supported seats
  var seatsState by remember(roomId) {
    mutableStateOf(
      (1..16).associateWith { num ->
        PartyRoomSeatState(seatNumber = num, isOccupied = false, isLocked = false)
      }
    )
  }

  // Real-time local/in-process synchronization of seatCount across room entry and connected observers
  val syncedSeatCountsMap by UserRoomStorage.roomSeatCounts.collectAsState()
  LaunchedEffect(syncedSeatCountsMap, roomId) {
    val syncedCount = syncedSeatCountsMap[roomId]
    if (syncedCount != null) {
      val validCount = UserRoomStorage.normalizeSeatCount(syncedCount)
      if (roomData.seatCount != validCount) {
        roomData = roomData.copy(seatCount = validCount)
      }
    }
  }

  // Real-time cloud synchronization of Party Room data (seatCount, roomTitle, isLocked) when signed in
  LaunchedEffect(roomId) {
    val hasFirebaseUser = try {
      com.google.firebase.Firebase.auth.currentUser != null
    } catch (_: Exception) {
      false
    }
    if (hasFirebaseUser) {
      val repo = com.example.data.repository.PriyaFirestoreRepository(context)
      if (isOwner) {
        repo.saveOrUpdatePartyRoom(
          roomId = roomId,
          roomTitle = roomData.roomTitle,
          seatCount = roomData.seatCount,
          isLocked = roomData.isLocked
        )
      }
      try {
        repo.observePartyRoom(roomId).collect { remoteRoom ->
          if (remoteRoom != null) {
            val validSeats = UserRoomStorage.normalizeSeatCount(remoteRoom.seatCount)
            if (roomData.seatCount != validSeats || roomData.roomTitle != remoteRoom.roomTitle || roomData.isLocked != remoteRoom.isLocked) {
              roomData = roomData.copy(
                roomTitle = remoteRoom.roomTitle.ifBlank { roomData.roomTitle },
                seatCount = validSeats,
                isLocked = remoteRoom.isLocked
              )
              UserRoomStorage.updateSeatCount(context, roomId, validSeats)
            }
          }
        }
      } catch (_: Exception) {
        // Ignore transient offline/unauthenticated snapshot errors
      }
    }
  }

  // Chat, Gifts & Emoji Reactions (Avatar-centered only; no duplicate board-level overlay)
  val chatMessages = remember { mutableStateListOf<PartyRoomChatMessage>() }
  val activeGifts = remember { mutableStateListOf<ActiveGiftDisplay>() }
  var seatEmojiReactions by remember { mutableStateOf<Map<Int, ActiveSeatEmojiReaction>>(emptyMap()) }

  // User mic & mute state
  var isLocalMuted by remember { mutableStateOf(false) } // Mic Voice Mute (User outgoing voice)
  var isLocalMicActive by remember { mutableStateOf(false) } // Microphone On/Off
  var isRoomPlaybackMuted by remember { mutableStateOf(false) } // Room Speaker Mute (Listening)
  val isLocalSpeaking = isLocalMicActive && !isLocalMuted

  // Music Player state
  var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
  var isMusicPlaying by remember { mutableStateOf(false) }
  var currentMusicTitle by remember { mutableStateOf<String?>(null) }
  var musicVolume by remember { mutableFloatStateOf(0.85f) }

  // Floating Popups & Dialogs
  var showMenuPanel by remember { mutableStateOf(false) }
  var showEmojiPanel by remember { mutableStateOf(false) }
  var showSettingsDialog by remember { mutableStateOf(false) }
  var showMusicDialog by remember { mutableStateOf(false) }
  var showSeatLockDialog by remember { mutableStateOf(false) }
  var showSeatOpenDialog by remember { mutableStateOf(false) }
  var showChatDialog by remember { mutableStateOf(false) }
  var showGiftDialog by remember { mutableStateOf(false) }
  var showGameDialog by remember { mutableStateOf(false) }
  var selectedEventForFullScreen by remember { mutableStateOf<PartyEventSlide?>(null) }
  var showExitConfirmDialog by remember { mutableStateOf(false) }
  var selectedParticipantProfile by remember { mutableStateOf<PartyParticipant?>(null) }

  // Find user's occupied seat number
  var myCurrentSeatNumber by remember { mutableStateOf<Int?>(null) }

  // ---------------------------------------------------------------------------
  // PERMISSION LAUNCHERS
  // ---------------------------------------------------------------------------
  val micPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted && myCurrentSeatNumber != null) {
      isLocalMicActive = true
      isLocalMuted = false
    } else {
      isLocalMicActive = false
    }
  }

  val audioPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri: Uri? ->
    uri?.let { selectedUri ->
      try {
        mediaPlayer?.stop()
        mediaPlayer?.release()

        var displayName = "Local Audio Track"
        context.contentResolver.query(selectedUri, null, null, null, null)?.use { cursor ->
          val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
          if (nameIndex != -1 && cursor.moveToFirst()) {
            displayName = cursor.getString(nameIndex) ?: "Local Audio Track"
          }
        }
        currentMusicTitle = displayName

        val player = MediaPlayer().apply {
          setDataSource(context, selectedUri)
          prepare()
          val vol = if (isLocalMuted) 0f else musicVolume
          setVolume(vol, vol)
          isLooping = true
          start()
        }
        mediaPlayer = player
        isMusicPlaying = true
        showMusicDialog = true

        Toast.makeText(context, "Playing: $displayName", Toast.LENGTH_SHORT).show()
      } catch (e: Exception) {
        Toast.makeText(context, "Could not load audio file: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
      }
    }
  }

  val boardWallpaperPicker = rememberLauncherForActivityResult(
    contract = PickVisualMedia()
  ) { uri: Uri? ->
    uri?.let { pickedUri ->
      coroutineScope.launch {
        val saved = LocalImageStorage.saveUriLocally(
          context = context,
          sourceUri = pickedUri,
          imageType = LocalImageStorage.ImageType.ROOM_COVER,
          identifier = roomId
        )
        val permanentUri = if (saved != null) "file://${saved.absolutePath}" else pickedUri.toString()
        roomData = roomData.copy(coverUri = permanentUri)
        try {
          context.getSharedPreferences("room_prefs", Context.MODE_PRIVATE).edit().putString("cover_$roomId", permanentUri).apply()
        } catch (_: Exception) {}
        Toast.makeText(context, "Room picture updated!", Toast.LENGTH_SHORT).show()
      }
    }
  }

  DisposableEffect(roomId) {
    onDispose {
      mediaPlayer?.stop()
      mediaPlayer?.release()
      mediaPlayer = null
    }
  }

  val myOccupiedSeat = remember(seatsState, currentUserId, roomData.seatCount) {
    seatsState.values.find {
      it.isOccupied && it.userId == currentUserId && it.seatNumber in 1..roomData.seatCount
    }
  }
  val isSeated = myOccupiedSeat != null

  // Keep seat-dependent state and emoji reactions strictly synchronized with occupied seats
  LaunchedEffect(isSeated, seatsState) {
    if (!isSeated) {
      myCurrentSeatNumber = null
      isLocalMicActive = false
      isLocalMuted = true
    } else {
      myCurrentSeatNumber = myOccupiedSeat?.seatNumber
    }
    val validSeatReactions = seatEmojiReactions.filterKeys { seatNum ->
      val s = seatsState[seatNum]
      s != null && s.isOccupied && !s.isLocked && seatNum <= roomData.seatCount
    }
    if (validSeatReactions.size != seatEmojiReactions.size) {
      seatEmojiReactions = validSeatReactions
    }
  }

  // ---------------------------------------------------------------------------
  // SEAT INTERACTION (Single-tap join & Seamless Seat Switching)
  // ---------------------------------------------------------------------------
  fun handleSeatClick(seatNumber: Int) {
    val seat = seatsState[seatNumber] ?: PartyRoomSeatState(seatNumber = seatNumber)

    if (seat.isLocked) {
      Toast.makeText(context, "Seat No.$seatNumber is currently locked", Toast.LENGTH_SHORT).show()
      return
    }

    val isMyCurrentSeat = seat.isOccupied && seat.userId == currentUserId

    if (isMyCurrentSeat) {
      // Tapping own seat: opens user's own profile card
      val fullProf = UserProfileStorage.getProfileForUser(context, currentUserId)
      val participant = PartyParticipant(
        userId = currentUserId,
        username = userProfile.username.ifBlank { "User" },
        avatarUri = userProfile.profileImageUri,
        frameId = seat.userFrameId,
        frameUrl = seat.userFrameUrl,
        level = parsedUserLevel,
        gender = userProfile.gender,
        country = userProfile.country,
        countryFlag = userProfile.countryFlag,
        isHost = isOwner,
        isMutedOnSeat = seat.isMuted,
        seatNumber = seat.seatNumber
      )
      selectedParticipantProfile = participant
      return
    }

    if (seat.isOccupied) {
      // Tapping another participant's occupied seat: opens that user's profile card
      val uid = seat.userId ?: currentUserId
      val fullProf = UserProfileStorage.getProfileForUser(context, uid)
      val participant = PartyParticipant(
        userId = uid,
        username = seat.userName?.ifBlank { fullProf.username } ?: fullProf.username,
        avatarUri = seat.userAvatarUri ?: fullProf.profileImageUri,
        frameId = seat.userFrameId,
        frameUrl = seat.userFrameUrl,
        level = if (seat.userLevel > 0) seat.userLevel else fullProf.userLevel.coerceAtLeast(1),
        gender = if (seat.userGender.isNotBlank()) seat.userGender else fullProf.gender,
        country = if (seat.userCountry.isNotBlank()) seat.userCountry else fullProf.country,
        countryFlag = if (seat.userCountryFlag.isNotBlank()) seat.userCountryFlag else fullProf.countryFlag,
        isHost = uid == roomData.hostUid || uid == roomData.hostUserId || (room.creatorUserId.isNotBlank() && uid == room.creatorUserId),
        isMutedOnSeat = seat.isMuted,
        seatNumber = seat.seatNumber
      )
      selectedParticipantProfile = participant
      return
    }

    // Target seat is EMPTY: take or switch directly to this seat
    if (roomData.isLocked && !isOwner) {
      Toast.makeText(context, "The room is currently locked by the host", Toast.LENGTH_SHORT).show()
      return
    }

    val equippedFrame = com.example.ui.storage.StoreCenterStorage.getEquippedFrame(context, currentUserId)
    val activeFrameId = equippedFrame?.id
    val activeFrameUrl = equippedFrame?.imageUrl

    // Activate mic & un-mute on seat take
    isLocalMicActive = true
    isLocalMuted = false

    val fromSeatNumber = myOccupiedSeat?.seatNumber ?: myCurrentSeatNumber
    myCurrentSeatNumber = seatNumber

    // 1. Optimistic Local State Update (Instant UI feedback, zero drop)
    val newSeat = PartyRoomSeatState(
      seatNumber = seatNumber,
      isOccupied = true,
      isLocked = false,
      userId = currentUserId,
      userName = userProfile.username.ifBlank { "User" },
      userAvatarUri = userProfile.profileImageUri,
      userFrameId = activeFrameId,
      userFrameUrl = activeFrameUrl,
      userLevel = parsedUserLevel,
      userGender = userProfile.gender,
      userCountry = userProfile.country,
      userCountryFlag = userProfile.countryFlag,
      isSpeaking = false,
      isMuted = false
    )

    val updatedMap = seatsState.toMutableMap()
    // Vacate current user's prior seat
    updatedMap.forEach { (num, s) ->
      if (s.userId == currentUserId || (fromSeatNumber != null && num == fromSeatNumber)) {
        updatedMap[num] = s.copy(
          isOccupied = false,
          userId = null,
          userName = null,
          userAvatarUri = null,
          userFrameId = null,
          userFrameUrl = null,
          isSpeaking = false,
          isMuted = false
        )
      }
    }
    if ( fromSeatNumber != null && fromSeatNumber != seatNumber) {
      seatEmojiReactions = seatEmojiReactions - fromSeatNumber
    }
    updatedMap[seatNumber] = newSeat
    seatsState = updatedMap

    // Check mic permission
    val hasMic = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    if (!hasMic) {
      try {
        micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
      } catch (e: Exception) {
        Log.w("PartyBoardRoom", "Mic launcher notice: ${e.message}")
      }
    }
  }

  // Keep microphone muted state in sync with local mic and mute toggles when seated
  LaunchedEffect(isLocalMicActive, isLocalMuted, isSeated) {
    if (isSeated) {
      val shouldMute = !isLocalMicActive || isLocalMuted
      val seatNum = myOccupiedSeat?.seatNumber
      if (seatNum != null) {
        val currentSeat = seatsState[seatNum]
        if (currentSeat != null && currentSeat.isOccupied) {
          val updatedMap = seatsState.toMutableMap()
          updatedMap[seatNum] = currentSeat.copy(isOccupied = true, isMuted = shouldMute, isSpeaking = false)
          seatsState = updatedMap
        }
      }
    }
  }

  // ---------------------------------------------------------------------------
  // MAIN FULL-SCREEN LAYOUT
  // ---------------------------------------------------------------------------
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF080014))
  ) {
    // 1. FULLSCREEN SCENIC BACKGROUND (Matches Reference Image)
    val wallpaperModel = LocalImageStorage.resolveImageModel(
      context = context,
      candidateUri = roomData.coverUri,
      imageType = LocalImageStorage.ImageType.ROOM_COVER,
      entityId = roomId,
      defaultFallbackRes = R.drawable.bg_party_scenic
    )
    AsyncImage(
      model = wallpaperModel,
      contentDescription = "Party Board Wallpaper",
      modifier = Modifier.fillMaxSize(),
      contentScale = ContentScale.Crop,
      error = painterResource(id = R.drawable.bg_party_scenic),
      fallback = painterResource(id = R.drawable.bg_party_scenic)
    )

    // Atmospheric dark gradient overlay for neon contrast
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            listOf(
              Color(0x80040010),
              Color(0x22000000),
              Color(0x11000000),
              Color(0x66000000),
              Color(0xEE050014)
            )
          )
        )
    )

    // MAIN CONTENT COLUMN (Starts immediately below status bar padding without inner card margins)
    val topSafePadding = rememberResponsiveTopBarPadding(extraTopSpacing = 2.dp)
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(top = topSafePadding, bottom = 6.dp, start = 12.dp, end = 12.dp)
    ) {
      // -----------------------------------------------------------------------
      // TOP HEADER: Board Profile Capsule (Left) and Circular Actions + PX (Right)
      // Strictly follows the layout & styling from the reference image
      // -----------------------------------------------------------------------
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        // TOP-LEFT: BOARD PROFILE CAPSULE (Requirements 3, 7, 8, 26)
        // Square Board Picture + Board Name + Room ID Number
        val hostUid = roomData.hostUserId.ifBlank { currentUserId }
        val hostProf = remember(hostUid) { UserProfileStorage.getProfileForUser(context, hostUid) }
        val hostParticipant = PartyParticipant(
          userId = hostUid,
          username = roomData.hostName.ifBlank { hostProf.username },
          avatarUri = roomData.hostAvatarUri ?: hostProf.profileImageUri,
          level = if (hostUid == currentUserId) parsedUserLevel else hostProf.userLevel.coerceAtLeast(1),
          gender = hostProf.gender,
          country = hostProf.country,
          countryFlag = hostProf.countryFlag,
          isHost = true
        )

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xD90E0024))
            .border(
              width = 1.4.dp,
              brush = Brush.horizontalGradient(
                listOf(Color(0xFF00E5FF), Color(0xFF7C4DFF), Color(0xFFFF007F))
              ),
              shape = RoundedCornerShape(16.dp)
            )
            .clickable {
              selectedParticipantProfile = hostParticipant
            }
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag("board_profile_header")
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            // Square Board Profile Picture (Must ALWAYS be Square with slight corner rounding, NEVER circular!)
            Box(
              modifier = Modifier
                .size(44.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF260447))
                .border(1.2.dp, Color(0xFF00E5FF), RoundedCornerShape(8.dp)),
              contentAlignment = Alignment.Center
            ) {
              val boardPictureCandidate = roomData.coverUri?.ifBlank { null } ?: roomData.hostAvatarUri
              val boardPicModel = LocalImageStorage.resolveImageModel(
                context = context,
                candidateUri = boardPictureCandidate,
                imageType = LocalImageStorage.ImageType.ROOM_COVER,
                entityId = roomId,
                defaultFallbackRes = R.drawable.img_profile_max
              )
              AsyncImage(
                model = boardPicModel,
                contentDescription = "Board Profile Picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.img_profile_max),
                fallback = painterResource(id = R.drawable.img_profile_max)
              )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.widthIn(max = 145.dp)) {
              // Real Board Name (Clean text without default crown/emoji icon)
              val cleanRoomTitle = roomData.roomTitle
                .replace("👑", "")
                .replace("🎧", "")
                .trim()
                .ifBlank { "Cyber Sound Lounge" }
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = cleanRoomTitle,
                  color = Color.White,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }

              Spacer(modifier = Modifier.height(2.dp))

              // Real Room / Board PID Number & Online Count
              Text(
                text = "PID: ${roomData.hostPID.ifBlank { roomData.hostUserId.ifBlank { roomId.takeLast(6) } }} • ${roomData.viewerCount} Online",
                color = Color(0xFF80D8FF),
                fontSize = 9.5.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }
        }

        // TOP-RIGHT: Circular Glass Actions (< , ··· , ⏻) and Diamond PX Pill Button
        Column(horizontalAlignment = Alignment.End) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Share Button
            HeaderCircularButton(
              icon = Icons.Filled.Share,
              contentDesc = "Share Room",
              onClick = {
                Toast.makeText(context, "Share link for ${roomData.roomTitle} copied!", Toast.LENGTH_SHORT).show()
              }
            )

            // More Options Button
            HeaderCircularButton(
              icon = Icons.Filled.MoreHoriz,
              contentDesc = "More Options",
              onClick = { showMenuPanel = !showMenuPanel }
            )

            // Top-Right X / Exit Button (Triggers Exit Confirm Dialog with Keep vs Exit options)
            HeaderCircularButton(
              icon = Icons.Filled.Close,
              contentDesc = "Exit Room",
              glowColor = Color(0xFFFF1744),
              onClick = { showExitConfirmDialog = true }
            )
          }

          Spacer(modifier = Modifier.height(4.dp))

          // Diamond PX Currency Pill (Matching reference image)
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .background(
                Brush.horizontalGradient(
                  listOf(Color(0xFF6A1B9A), Color(0xFFAB47BC))
                )
              )
              .border(
                1.4.dp,
                Brush.horizontalGradient(listOf(Color(0xFFE040FB), Color(0xFFFFD54F))),
                RoundedCornerShape(16.dp)
              )
              .clickable {
                showGiftDialog = true
              }
              .padding(horizontal = 9.dp, vertical = 4.dp)
              .testTag("diamond_px_pill")
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = "💎", fontSize = 11.sp)
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = "PX $userWalletCoins",
                color = Color.White,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
              )
            }
          }
        }
      }

      // Room Locked Notification banner if locked
      if (roomData.isLocked) {
        Spacer(modifier = Modifier.height(6.dp))
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xCCB71C1C))
            .padding(horizontal = 10.dp, vertical = 3.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "🔒 Room is currently locked by the Host",
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      val currentSeatCount = UserRoomStorage.normalizeSeatCount(roomData.seatCount)
      Spacer(
        modifier = Modifier.height(
          when (currentSeatCount) {
            16 -> 2.dp
            12 -> 4.dp
            else -> 6.dp
          }
        )
      )

      // -----------------------------------------------------------------------
      // RESPONSIVE CENTER SEAT SYSTEM (8, 12, or 16 Seats)
      // Automatically calculates available Party Board width & height,
      // seat diameter, horizontal/vertical spacing, and balanced rows (4 per row)
      // so every Seat is 100% visible inside the Party Board on all screen sizes.
      // -----------------------------------------------------------------------
      val seatsList = remember(currentSeatCount, seatsState) {
        (1..currentSeatCount).map { seatNum ->
          seatsState[seatNum] ?: PartyRoomSeatState(seatNumber = seatNum)
        }
      }

      BoxWithConstraints(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .testTag("party_board_seat_area"),
        contentAlignment = Alignment.TopCenter
      ) {
        val metrics = remember(maxWidth, maxHeight, currentSeatCount) {
          calculateResponsiveSeatBoardMetrics(
            availableWidth = maxWidth,
            availableHeight = maxHeight,
            selectedSeatCount = currentSeatCount
          )
        }
        val seatRows = remember(seatsList, metrics.columns) {
          seatsList.chunked(metrics.columns)
        }

        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = metrics.topOffset),
          verticalArrangement = Arrangement.spacedBy(metrics.verticalSpacing),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          seatRows.forEachIndexed { rowIndex, rowSeats ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .height(metrics.seatTotalHeight)
                .padding(horizontal = metrics.sideMargin)
                .testTag("party_seat_row_${rowIndex + 1}"),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              rowSeats.forEach { seat ->
                ReferencePartySeat(
                  seat = seat,
                  isCurrentUsersSeat = seat.isOccupied && seat.userId == currentUserId,
                  isLocalSpeaking = isLocalSpeaking,
                  isLocalMicActive = isLocalMicActive,
                  activeEmojiReaction = if (seat.isOccupied && !seat.isLocked) seatEmojiReactions[seat.seatNumber] else null,
                  seatOrbSize = metrics.seatOrbSize,
                  seatItemWidth = metrics.seatItemWidth,
                  labelTopGap = metrics.labelTopGap,
                  labelPillHeight = metrics.labelPillHeight,
                  onClick = { handleSeatClick(seat.seatNumber) }
                )
              }
            }
          }
        }

        // ---------------------------------------------------------------------
        // FLOATING GIFT ANIMATION DISPLAY AREA (Overlay inside board area)
        // ---------------------------------------------------------------------
        if (activeGifts.isNotEmpty()) {
          Column(
            modifier = Modifier
              .align(Alignment.BottomCenter)
              .fillMaxWidth()
              .padding(bottom = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            activeGifts.forEach { gift ->
              key(gift.id) {
                FloatingGiftBanner(gift = gift)
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(2.dp))

      // -----------------------------------------------------------------------
      // LOWER REAL-TIME MESSAGE AREA (Requirement 5)
      // Separate fixed section at bottom of Party Board. Leaves right-hand space
      // for the floating Games button and Event Box on the right side of the room.
      // -----------------------------------------------------------------------
      val messageAreaHeight = when (currentSeatCount) {
        16 -> 64.dp
        12 -> 80.dp
        else -> 92.dp
      }
      PartyBoardMessageArea(
        messages = chatMessages,
        onOpenMessageInput = { showChatDialog = true },
        modifier = Modifier
          .fillMaxWidth(0.70f)
          .height(messageAreaHeight)
          .padding(horizontal = 4.dp, vertical = 2.dp)
      )

      Spacer(modifier = Modifier.height(4.dp))

      // -----------------------------------------------------------------------
      // BOTTOM ACTION BAR:
      // 1. Speaker (Mute) | 2. Microphone (Mic) | 3. Chat (Message) | 4. Gift | 5. Emoji | 6. Menu
      // Container has strictly fixed height (68.dp) so seats NEVER move!
      // -----------------------------------------------------------------------
      val responsiveBottomPadding = rememberResponsiveNavigationBarPadding(extraBottomSpacing = 6.dp)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = responsiveBottomPadding)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .clip(RoundedCornerShape(36.dp))
            .background(Color(0xCC09001D))
            .border(
              width = 1.8.dp,
              brush = Brush.horizontalGradient(
                listOf(
                  Color(0xFF00E5FF),
                  Color(0xFFE040FB),
                  Color(0xFFFF007F),
                  Color(0xFF7C4DFF),
                  Color(0xFF00E5FF)
                )
              ),
              shape = RoundedCornerShape(36.dp)
            )
            .padding(horizontal = 6.dp, vertical = 6.dp)
            .testTag("bottom_action_bar")
        ) {
          Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
          ) {
            if (isSeated) {
              // 1. SPEAKER / MUTE (Visible ONLY when user is SEATED on a mic/seat)
              BottomBarItem(
                icon = if (isLocalMuted) Icons.AutoMirrored.Filled.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp,
                label = "Mute",
                orbBrush = if (isLocalMuted) Brush.linearGradient(listOf(Color(0xFFB71C1C), Color(0xFF7F0000)))
                else Brush.radialGradient(listOf(Color(0xFFA855F7), Color(0xFF4A148C))),
                borderColor = if (isLocalMuted) Color(0xFFFF5252) else Color(0xFFD500F9),
                onClick = {
                  val newMuted = !isLocalMuted
                  isLocalMuted = newMuted
                  val seatNum = myOccupiedSeat?.seatNumber
                  if (seatNum != null) {
                    val cur = seatsState[seatNum]
                    if (cur != null && cur.isOccupied) {
                      val updatedMap = seatsState.toMutableMap()
                      updatedMap[seatNum] = cur.copy(isOccupied = true, isMuted = newMuted, isSpeaking = false)
                      seatsState = updatedMap
                    }
                  }
                },
                modifier = Modifier.weight(1f)
              )

              // 2. MICROPHONE (Visible ONLY when user is SEATED on a mic/seat)
              BottomBarItem(
                icon = if (isLocalMicActive && !isLocalMuted) Icons.Filled.Mic else Icons.Filled.MicOff,
                label = "Mic",
                orbBrush = if (isLocalMicActive && !isLocalMuted) Brush.radialGradient(listOf(Color(0xFF00E5FF), Color(0xFF0070F3)))
                else Brush.linearGradient(listOf(Color(0xFF37474F), Color(0xFF263238))),
                borderColor = if (isLocalMicActive && !isLocalMuted) Color(0xFF00E5FF) else Color(0x88FFFFFF),
                onClick = {
                  val seatNum = myOccupiedSeat?.seatNumber
                  if (isLocalMicActive && !isLocalMuted) {
                    isLocalMicActive = false
                    isLocalMuted = true
                    if (seatNum != null) {
                      val cur = seatsState[seatNum]
                      if (cur != null && cur.isOccupied) {
                        val updatedMap = seatsState.toMutableMap()
                        updatedMap[seatNum] = cur.copy(isOccupied = true, isMuted = true, isSpeaking = false)
                        seatsState = updatedMap
                      }
                    }
                  } else {
                    val hasMic = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                    if (hasMic) {
                      isLocalMicActive = true
                      isLocalMuted = false
                      if (seatNum != null) {
                        val cur = seatsState[seatNum]
                        if (cur != null && cur.isOccupied) {
                          val updatedMap = seatsState.toMutableMap()
                          updatedMap[seatNum] = cur.copy(isOccupied = true, isMuted = false)
                          seatsState = updatedMap
                        }
                      }
                    } else {
                      micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                  }
                },
                modifier = Modifier.weight(1f)
              )
            }

            // 3. CHAT (Message)
            BottomBarItem(
              icon = Icons.AutoMirrored.Filled.Chat,
              label = "Message",
              orbBrush = Brush.radialGradient(listOf(Color(0xFF00E5FF), Color(0xFF00838F))),
              borderColor = Color(0xFF00E5FF),
              onClick = { showChatDialog = true },
              modifier = Modifier.weight(1f)
            )

            // 4. GIFT
            BottomBarItem(
              icon = Icons.Filled.CardGiftcard,
              label = "Gift",
              orbBrush = Brush.radialGradient(listOf(Color(0xFFFF007F), Color(0xFFC2185B))),
              borderColor = Color(0xFFFF4081),
              onClick = { showGiftDialog = true },
              modifier = Modifier.weight(1f)
            )

            // 5. EMOJI (Replaces Games in the 5th bottom-navigation slot, matching square shape, border & neon glow)
            BottomBarItem(
              icon = Icons.Filled.EmojiEmotions,
              label = "Emoji",
              orbBrush = Brush.radialGradient(listOf(Color(0xFFFFB300), Color(0xFFD81B60))),
              borderColor = Color(0xFFFFD54F),
              onClick = {
                showMenuPanel = false
                showEmojiPanel = !showEmojiPanel
              },
              isHighlighted = showEmojiPanel,
              modifier = Modifier.weight(1f)
            )

            // 6. MENU (Extreme bottom-right)
            BottomBarItem(
              icon = Icons.Filled.Menu,
              label = "Menu",
              orbBrush = Brush.radialGradient(listOf(Color(0xFFE040FB), Color(0xFF7B1FA2))),
              borderColor = Color(0xFFE040FB),
              onClick = {
                showEmojiPanel = false
                showMenuPanel = !showMenuPanel
              },
              isHighlighted = showMenuPanel,
              modifier = Modifier.weight(1f)
            )
          }
        }
      }
    }

    // -------------------------------------------------------------------------
    // RIGHT-SIDE FLOATING AREA: EVENT BOX ABOVE FLOATING GAMES BUTTON
    // - Top: Compact, almost-square Event Box rotating every 3 seconds between
    //        Event A (OFFICIAL HELPLINE) and Event B (PRIYA LIVE GLOBAL MEGA MANAGER RECRUITMENT).
    // - Bottom: Floating Games icon button (without "Games" text label underneath,
    //           and with no unwanted poster-like card beneath it).
    // -------------------------------------------------------------------------
    val floatingRightBottomOffset = rememberResponsiveNavigationBarPadding(extraBottomSpacing = 92.dp)
    Column(
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(end = 12.dp, bottom = floatingRightBottomOffset)
        .testTag("party_right_floating_area"),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // 1. COMPACT EVENT BOX DIRECTLY ABOVE GAMES BUTTON
      FloatingPartyEventBox(
        onClick = { tappedSlide ->
          selectedEventForFullScreen = tappedSlide
        }
      )

      // 2. FLOATING GAMES ICON BUTTON (No "Games" text label underneath, no poster card below)
      FloatingPartyGamesButton(
        onClick = { showGameDialog = true }
      )
    }

    // -------------------------------------------------------------------------
    // PARTY ROOM EMOJI PICKER — BOTTOM-SHEET INTERFACE
    // Opens from the bottom of the screen (~half-screen height), vertically scrollable
    // grid, supports close control and Android Back, renders reaction ONLY over
    // the sender's seat avatar.
    // -------------------------------------------------------------------------
    if (showEmojiPanel) {
      BackHandler { showEmojiPanel = false }

      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color(0x66000000))
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
          ) { showEmojiPanel = false }
      )

      PartyEmojiReactionSheet(
        isSeated = isSeated,
        activeSeatNumber = myOccupiedSeat?.seatNumber,
        onSelectEmoji = { emoji, label ->
          showEmojiPanel = false
          val reactionId = System.currentTimeMillis()
          val occupiedSeatNum = myOccupiedSeat?.seatNumber

          // When SEATED: Trigger single animated emoji reaction centered over the user's active seat avatar.
          // When NOT seated: Do NOT trigger any seat reaction; integrate with the bottom message area only.
          if (isSeated && occupiedSeatNum != null) {
            val seatReaction = ActiveSeatEmojiReaction(
              id = reactionId,
              seatNumber = occupiedSeatNum,
              emoji = emoji,
              label = label
            )
            seatEmojiReactions = seatEmojiReactions + (occupiedSeatNum to seatReaction)
            coroutineScope.launch {
              delay(2600)
              if (seatEmojiReactions[occupiedSeatNum]?.id == reactionId) {
                seatEmojiReactions = seatEmojiReactions - occupiedSeatNum
              }
            }
          }

          // Log emoji in bottom room message stream
          chatMessages.add(
            PartyRoomChatMessage(
              messageId = "emoji_$reactionId",
              senderUid = currentUserId,
              senderName = userProfile.username.ifBlank { "User" },
              senderAvatarUri = userProfile.profileImageUri,
              senderLevel = parsedUserLevel,
              content = "$emoji $label"
            )
          )
        },
        onSendMessageWithEmoji = { customText ->
          if (customText.isNotBlank()) {
            showEmojiPanel = false
            chatMessages.add(
              PartyRoomChatMessage(
                messageId = "msg_${System.currentTimeMillis()}",
                senderUid = currentUserId,
                senderName = userProfile.username.ifBlank { "User" },
                senderAvatarUri = userProfile.profileImageUri,
                senderLevel = parsedUserLevel,
                content = customText.trim()
              )
            )
          }
        },
        onDismiss = { showEmojiPanel = false },
        modifier = Modifier.align(Alignment.BottomCenter)
      )
    }

    // -------------------------------------------------------------------------
    // FLOATING MENU PANEL
    // Appears above the bottom action bar from the bottom-right corner.
    // Clean Material 3 vector icon system (No emojis)
    // -------------------------------------------------------------------------
    if (showMenuPanel) {
      // Backdrop click catcher to dismiss
      Box(
        modifier = Modifier
          .fillMaxSize()
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
          ) { showMenuPanel = false }
      )

      // Floating popup aligned above Menu button
      val menuResponsiveBottom = rememberResponsiveNavigationBarPadding(extraBottomSpacing = 80.dp)
      val menuOpenAnim = remember { Animatable(0f) }
      LaunchedEffect(Unit) {
        menuOpenAnim.animateTo(
          targetValue = 1f,
          animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing)
        )
      }
      val menuProgress = menuOpenAnim.value
      val menuScale = 0.90f + 0.10f * menuProgress
      val menuAlpha = 0.85f + 0.15f * menuProgress
      val menuOffsetY = (1f - menuProgress) * 14f

      Box(
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(end = 12.dp, bottom = menuResponsiveBottom)
          .graphicsLayer {
            scaleX = menuScale
            scaleY = menuScale
            alpha = menuAlpha
            translationY = menuOffsetY
          }
          .clip(RoundedCornerShape(20.dp))
          .background(Color(0xF514002B))
          .border(
            width = 1.6.dp,
            brush = Brush.verticalGradient(
              listOf(Color(0xFFE040FB), Color(0xFF00E5FF), Color(0xFFFF007F))
            ),
            shape = RoundedCornerShape(20.dp)
          )
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
          ) { /* Consume taps inside menu */ }
          .padding(10.dp)
          .width(190.dp)
          .testTag("floating_menu_panel")
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          // 1. Settings (Authorized for Owner only)
          if (isOwner) {
            MenuOptionRow(
              icon = Icons.Filled.Settings,
              title = "Settings",
              iconTint = Color(0xFFE040FB),
              onClick = {
                showMenuPanel = false
                showSettingsDialog = true
              }
            )
          }

          // 2. Music (Available for authorized music playback)
          MenuOptionRow(
            icon = Icons.Filled.MusicNote,
            title = "Music",
            iconTint = Color(0xFFFFD54F),
            onClick = {
              showMenuPanel = false
              showMusicDialog = true
            }
          )

          // 3. Room Lock / Open (Owner only)
          if (isOwner) {
            MenuOptionRow(
              icon = if (roomData.isLocked) Icons.Filled.LockOpen else Icons.Filled.Lock,
              title = if (roomData.isLocked) "Unlock Room" else "Room Lock",
              iconTint = if (roomData.isLocked) Color(0xFF00E676) else Color(0xFFFF5252),
              onClick = {
                showMenuPanel = false
                val newLockState = !roomData.isLocked
                roomData = roomData.copy(isLocked = newLockState)
                Toast.makeText(
                  context,
                  if (newLockState) "Room Locked" else "Room Unlocked",
                  Toast.LENGTH_SHORT
                ).show()
              }
            )

            // 4. Seat Lock (Owner only)
            MenuOptionRow(
              icon = Icons.Filled.Lock,
              title = "Seat Lock",
              iconTint = Color(0xFFFF5252),
              onClick = {
                showMenuPanel = false
                showSeatLockDialog = true
              }
            )

            // 5. Seat Open (Owner only)
            MenuOptionRow(
              icon = Icons.Filled.LockOpen,
              title = "Seat Open",
              iconTint = Color(0xFF00E676),
              onClick = {
                showMenuPanel = false
                showSeatOpenDialog = true
              }
            )
          }

          // 6. Mute / Unmute Room Audio (Available to all users)
          MenuOptionRow(
            icon = if (isRoomPlaybackMuted) Icons.AutoMirrored.Filled.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp,
            title = if (isRoomPlaybackMuted) "Unmute Audio" else "Mute Audio",
            iconTint = if (isRoomPlaybackMuted) Color(0xFFFF5252) else Color(0xFF00E5FF),
            onClick = {
              showMenuPanel = false
              val newPlaybackMuted = !isRoomPlaybackMuted
              isRoomPlaybackMuted = newPlaybackMuted
              val vol = if (newPlaybackMuted) 0f else musicVolume
              mediaPlayer?.setVolume(vol, vol)
            }
          )
        }
      }
    }

    // -------------------------------------------------------------------------
    // DIALOGS & PANELS
    // -------------------------------------------------------------------------

    // 1. SETTINGS DIALOG (Room Name, Room Picture, Seat Count: 8 -> 12 -> 16)
    if (showSettingsDialog) {
      PartySettingsDialog(
        currentTitle = roomData.roomTitle,
        currentSeatCount = roomData.seatCount,
        isOwner = isOwner,
        onDismiss = { showSettingsDialog = false },
        onChangeTitle = { newTitle ->
          roomData = roomData.copy(roomTitle = newTitle)
          UserRoomStorage.updateRoom(context, newTitle, null)
          coroutineScope.launch {
            try {
              if (com.google.firebase.Firebase.auth.currentUser != null) {
                com.example.data.repository.PriyaFirestoreRepository(context).saveOrUpdatePartyRoom(
                  roomId = roomId,
                  roomTitle = newTitle,
                  seatCount = roomData.seatCount,
                  isLocked = roomData.isLocked
                )
              }
            } catch (_: Exception) {}
          }
          Toast.makeText(context, "Room name updated!", Toast.LENGTH_SHORT).show()
        },
        onPickPicture = {
          boardWallpaperPicker.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        },
        onChangeSeatCount = { count ->
          val validCount = UserRoomStorage.normalizeSeatCount(count)
          roomData = roomData.copy(seatCount = validCount)
          UserRoomStorage.updateSeatCount(context, roomId, validCount)

          // Ensure all seats 1..validCount exist and vacate any seat > validCount if reduced
          val updated = seatsState.toMutableMap()
          for (num in 1..16) {
            val existingSeat = updated[num] ?: PartyRoomSeatState(seatNumber = num)
            if (num > validCount && existingSeat.isOccupied) {
              updated[num] = existingSeat.copy(
                isOccupied = false,
                userId = null,
                userName = null,
                userAvatarUri = null,
                userFrameId = null,
                userFrameUrl = null,
                isSpeaking = false,
                isMuted = false
              )
            } else {
              updated[num] = existingSeat
            }
          }
          seatsState = updated
          if (myCurrentSeatNumber != null && myCurrentSeatNumber!! > validCount) {
            myCurrentSeatNumber = null
          }

          coroutineScope.launch {
            try {
              if (com.google.firebase.Firebase.auth.currentUser != null) {
                com.example.data.repository.PriyaFirestoreRepository(context).saveOrUpdatePartyRoom(
                  roomId = roomId,
                  roomTitle = roomData.roomTitle,
                  seatCount = validCount,
                  isLocked = roomData.isLocked
                )
              }
            } catch (_: Exception) {}
          }
          Toast.makeText(context, "Seats updated to $validCount seats!", Toast.LENGTH_SHORT).show()
        }
      )
    }

    // 2. REAL PHONE MUSIC DIALOG
    if (showMusicDialog) {
      PartyMusicDialog(
        currentTrackTitle = currentMusicTitle,
        isPlaying = isMusicPlaying,
        volume = musicVolume,
        onDismiss = { showMusicDialog = false },
        onSelectTrack = {
          audioPickerLauncher.launch("audio/*")
        },
        onTogglePlayPause = {
          if (mediaPlayer == null) {
            audioPickerLauncher.launch("audio/*")
          } else {
            if (isMusicPlaying) {
              mediaPlayer?.pause()
              isMusicPlaying = false
            } else {
              mediaPlayer?.start()
              isMusicPlaying = true
            }
          }
        },
        onStop = {
          mediaPlayer?.stop()
          mediaPlayer?.prepare()
          isMusicPlaying = false
        },
        onVolumeChange = { vol ->
          musicVolume = vol
          if (!isLocalMuted) {
            mediaPlayer?.setVolume(vol, vol)
          }
        }
      )
    }

    // 3. SEAT LOCK DIALOG
    if (showSeatLockDialog) {
      SeatLockPickerModal(
        seats = (1..roomData.seatCount).map { seatsState[it] ?: PartyRoomSeatState(seatNumber = it) },
        title = "Lock a Seat",
        actionText = "Lock",
        onDismiss = { showSeatLockDialog = false },
        onSelectSeat = { seatNum ->
          val updated = seatsState.toMutableMap()
          val cur = updated[seatNum] ?: PartyRoomSeatState(seatNumber = seatNum)
          if (cur.userId == currentUserId || myCurrentSeatNumber == seatNum) {
            myCurrentSeatNumber = null
            isLocalMicActive = false
            isLocalMuted = true
          }
          seatEmojiReactions = seatEmojiReactions - seatNum
          updated[seatNum] = cur.copy(
            isLocked = true,
            isOccupied = false,
            userId = null,
            userName = null,
            userAvatarUri = null,
            userFrameId = null,
            userFrameUrl = null,
            isSpeaking = false,
            isMuted = false
          )
          seatsState = updated
          Toast.makeText(context, "Seat No.$seatNum is now locked", Toast.LENGTH_SHORT).show()
          showSeatLockDialog = false
        }
      )
    }

    // 4. SEAT OPEN DIALOG
    if (showSeatOpenDialog) {
      SeatLockPickerModal(
        seats = (1..roomData.seatCount).map { seatsState[it] ?: PartyRoomSeatState(seatNumber = it) }.filter { it.isLocked },
        title = "Unlock a Seat",
        actionText = "Open",
        emptyMessage = "No seats are currently locked",
        onDismiss = { showSeatOpenDialog = false },
        onSelectSeat = { seatNum ->
          val updated = seatsState.toMutableMap()
          val cur = updated[seatNum] ?: PartyRoomSeatState(seatNumber = seatNum)
          updated[seatNum] = cur.copy(isLocked = false)
          seatsState = updated
          Toast.makeText(context, "Seat No.$seatNum is now open", Toast.LENGTH_SHORT).show()
          showSeatOpenDialog = false
        }
      )
    }

    // 5. CHAT DIALOG (Message)
    if (showChatDialog) {
      PartyChatDialog(
        messages = chatMessages,
        onDismiss = { showChatDialog = false },
        onSendMessage = { text ->
          chatMessages.add(
            PartyRoomChatMessage(
              messageId = "msg_${System.currentTimeMillis()}",
              senderUid = currentUserId,
              senderName = userProfile.username.ifBlank { "User" },
              senderAvatarUri = userProfile.profileImageUri,
              senderLevel = parsedUserLevel,
              content = text
            )
          )
        }
      )
    }

    // 6. REAL GIFT BOTTOM SHEET (Half-Screen, Real Board Users Only)
    if (showGiftDialog) {
      val realRecipients = remember(roomData, seatsState, currentUserId) {
        val list = mutableListOf<RealGiftRecipient>()
        // 1. Host
        if (roomData.hostUserId.isNotBlank()) {
          list.add(
            RealGiftRecipient(
              userId = roomData.hostUserId,
              username = roomData.hostName.ifBlank { "Host" },
              avatarUri = roomData.hostAvatarUri ?: roomData.coverUri,
              isHost = true
            )
          )
        }
        // 2. Real seated users (excluding yourself if seated)
        seatsState.values
          .filter { it.isOccupied && !it.userId.isNullOrBlank() && it.userId != currentUserId && it.userId != roomData.hostUserId }
          .forEach { seat ->
            list.add(
              RealGiftRecipient(
                userId = seat.userId!!,
                username = seat.userName?.ifBlank { "Seat ${seat.seatNumber}" } ?: "Seat ${seat.seatNumber}",
                avatarUri = seat.userAvatarUri,
                frameId = seat.userFrameId,
                frameUrl = seat.userFrameUrl,
                isHost = false,
                seatNumber = seat.seatNumber
              )
            )
          }
        list
      }

      RealBoardGiftBottomSheet(
        realRecipients = realRecipients,
        currentUserBalance = userWalletCoins,
        onDismiss = { showGiftDialog = false },
        onSendGift = { recipient, gift, quantity, totalCost ->
          val curCoins = UserProfileStorage.getCoinBalance(context, currentUserId)
          val newBal = (curCoins - totalCost).coerceAtLeast(0L)
          UserProfileStorage.setCoinBalance(context, newBal, currentUserId)
          userWalletCoins = newBal

          val display = ActiveGiftDisplay(
            id = System.currentTimeMillis(),
            emoji = gift.emoji,
            title = gift.name,
            sender = userProfile.username.ifBlank { "User" }
          )
          activeGifts.add(display)
          coroutineScope.launch {
            delay(3200)
            activeGifts.remove(display)
          }

          chatMessages.add(
            PartyRoomChatMessage(
              messageId = "gift_${System.currentTimeMillis()}",
              senderUid = currentUserId,
              senderName = userProfile.username.ifBlank { "User" },
              senderAvatarUri = userProfile.profileImageUri,
              senderLevel = parsedUserLevel,
              content = "Sent ${gift.name} ${gift.emoji} x$quantity to ${recipient.username}!"
            )
          )

          showGiftDialog = false
        }
      )
    }

    // 7. REAL GAME DIALOG (Clean Empty State)
    if (showGameDialog) {
      PartyGameDialog(
        onDismiss = { showGameDialog = false }
      )
    }

    // 7B. DEDICATED FULL-SCREEN EVENT PAGE (Official Helpline or Global Mega Manager Recruitment)
    selectedEventForFullScreen?.let { activeEvent ->
      PartyEventFullScreenDialog(
        eventSlide = activeEvent,
        onDismiss = { selectedEventForFullScreen = null }
      )
    }

    // Intercept back button to show Keep vs Exit dialog
    BackHandler { showExitConfirmDialog = true }

    // 8. EXIT CONFIRMATION DIALOG (Keep vs Exit matching reference design)
    if (showExitConfirmDialog) {
      RoomKeepExitDialog(
        onKeep = {
          showExitConfirmDialog = false
        },
        onExit = {
          showExitConfirmDialog = false
          if (isOwner) {
            com.example.ui.storage.UserRoomStorage.closeRoom(context, roomId)
          }
          onDismiss()
        },
        onDismiss = { showExitConfirmDialog = false }
      )
    }

    // 9. USER PROFILE DIALOG (Shows real user ID, Level, Gender, Country and Leave Seat button)
    selectedParticipantProfile?.let { participant ->
      PartyUserProfileDialog(
        targetUser = participant,
        currentUserId = currentUserId,
        isViewerOwner = isOwner,
        viewerAdminPermissions = null,
        onDismiss = { selectedParticipantProfile = null },
        onSendMessage = { _ ->
          selectedParticipantProfile = null
          showChatDialog = true
        },
        onRemoveFromSeat = { p ->
          p.seatNumber?.let { seatNum ->
            val updatedMap = seatsState.toMutableMap()
            updatedMap[seatNum] = PartyRoomSeatState(seatNumber = seatNum, isOccupied = false, isLocked = false)
            seatsState = updatedMap
            seatEmojiReactions = seatEmojiReactions - seatNum
            if (p.userId == currentUserId) {
              myCurrentSeatNumber = null
              isLocalMicActive = false
              isLocalMuted = true
            }
          }
          selectedParticipantProfile = null
        },
        onMuteParticipant = { p, newMuteState ->
          p.seatNumber?.let { seatNum ->
            val updatedMap = seatsState.toMutableMap()
            seatsState[seatNum]?.let { s ->
              updatedMap[seatNum] = s.copy(isMuted = newMuteState)
              seatsState = updatedMap
            }
          }
        },
        onKickParticipant = { p ->
          p.seatNumber?.let { seatNum ->
            val updatedMap = seatsState.toMutableMap()
            updatedMap[seatNum] = PartyRoomSeatState(seatNumber = seatNum, isOccupied = false, isLocked = false)
            seatsState = updatedMap
          }
          Toast.makeText(context, "${p.username} removed", Toast.LENGTH_SHORT).show()
        }
      )
    }
  }
}

// =============================================================================
// RESPONSIVE SEAT BOARD METRICS & CALCULATOR
// Calculates Seat diameter, horizontal spacing, vertical spacing, rows, and
// columns from available board width, available board height, and seat count.
// =============================================================================
data class ResponsiveSeatBoardMetrics(
  val seatCount: Int,
  val columns: Int,
  val rows: Int,
  val seatOrbSize: Dp,
  val seatItemWidth: Dp,
  val labelTopGap: Dp,
  val labelPillHeight: Dp,
  val seatTotalHeight: Dp,
  val sideMargin: Dp,
  val horizontalSpacing: Dp,
  val verticalSpacing: Dp,
  val boardContentHeight: Dp,
  val topOffset: Dp
)

fun calculateResponsiveSeatBoardMetrics(
  availableWidth: Dp,
  availableHeight: Dp,
  selectedSeatCount: Int
): ResponsiveSeatBoardMetrics {
  val normalizedSeatCount = UserRoomStorage.normalizeSeatCount(selectedSeatCount)
  val columns = 4
  val rows = normalizedSeatCount / columns // 2 for 8 seats, 3 for 12 seats, 4 for 16 seats

  val safeWidth = availableWidth.coerceAtLeast(180.dp)
  val safeHeight = availableHeight.coerceAtLeast(140.dp)

  val baseLabelTopGap = when (normalizedSeatCount) {
    8 -> 4.dp
    12 -> 3.dp
    else -> 2.dp
  }
  val baseLabelPillHeight = when (normalizedSeatCount) {
    8 -> 19.dp
    12 -> 17.dp
    else -> 15.dp
  }

  val verticalPadding = 2.dp
  val rawVerticalSpacing = when (normalizedSeatCount) {
    8 -> (safeHeight * 0.052f).coerceIn(6.dp, 16.dp)
    12 -> (safeHeight * 0.030f).coerceIn(4.dp, 10.dp)
    else -> (safeHeight * 0.018f).coerceIn(3.dp, 7.dp)
  }

  val totalVerticalGaps = rawVerticalSpacing * (rows - 1) + (verticalPadding * 2)
  val maxRowHeight = (safeHeight - totalVerticalGaps) / rows

  val labelTopGap = if (maxRowHeight < 52.dp) 1.5.dp else baseLabelTopGap
  val labelPillHeight = if (maxRowHeight < 52.dp) 13.dp else baseLabelPillHeight
  val maxOrbByHeight = (maxRowHeight - labelTopGap - labelPillHeight).coerceAtLeast(22.dp)

  // Reduced outer side margins so outer left/right seats use horizontal board width efficiently
  val sideMargin = when (normalizedSeatCount) {
    8 -> 6.dp
    12 -> 4.dp
    else -> 2.dp
  }
  val minInterSeatGap = when (normalizedSeatCount) {
    8 -> 8.dp
    12 -> 6.dp
    else -> 5.dp
  }
  val usableWidth = (safeWidth - (sideMargin * 2)).coerceAtLeast(160.dp)
  val maxSlotWidth = ((usableWidth - minInterSeatGap * (columns - 1)) / columns).coerceAtLeast(36.dp)
  val maxOrbByWidth = (maxSlotWidth - 4.dp).coerceAtLeast(22.dp)

  // Responsive target circular diameter that scales with board width while keeping 8 > 12 > 16
  val preferredOrbSize = when (normalizedSeatCount) {
    8 -> (usableWidth * 0.182f).coerceIn(58.dp, 68.dp)
    12 -> (usableWidth * 0.168f).coerceIn(53.dp, 62.dp)
    else -> (usableWidth * 0.156f).coerceIn(49.dp, 58.dp)
  }

  val seatOrbSize = minOf(preferredOrbSize, maxOrbByHeight, maxOrbByWidth)
  val seatTotalHeight = seatOrbSize + labelTopGap + labelPillHeight
  val itemHorizontalAllowance = when (normalizedSeatCount) {
    8 -> 14.dp
    12 -> 10.dp
    else -> 8.dp
  }
  val seatItemWidth = minOf(maxSlotWidth, seatOrbSize + itemHorizontalAllowance).coerceAtLeast(seatOrbSize + 4.dp)

  val totalRowItemsWidth = seatItemWidth * columns
  val horizontalSpacing = ((usableWidth - totalRowItemsWidth) / (columns - 1)).coerceAtLeast(2.dp)

  val boardContentHeight = seatTotalHeight * rows + rawVerticalSpacing * (rows - 1)
  val remainingHeight = (safeHeight - boardContentHeight).coerceAtLeast(0.dp)
  val topOffset = (remainingHeight * 0.04f).coerceAtMost(4.dp).coerceAtLeast(0.dp)

  return ResponsiveSeatBoardMetrics(
    seatCount = normalizedSeatCount,
    columns = columns,
    rows = rows,
    seatOrbSize = seatOrbSize,
    seatItemWidth = seatItemWidth,
    labelTopGap = labelTopGap,
    labelPillHeight = labelPillHeight,
    seatTotalHeight = seatTotalHeight,
    sideMargin = sideMargin,
    horizontalSpacing = horizontalSpacing,
    verticalSpacing = rawVerticalSpacing,
    boardContentHeight = boardContentHeight,
    topOffset = topOffset
  )
}

// =============================================================================
// SEAT COMPOSABLE: CLASSIC BEAUTIFUL PARTY SEAT
// Circular seat ring with real user avatar & frame when occupied,
// glowing speaking pulse, mute/unmute indicator badge, and name/number pill.
// =============================================================================
@Composable
private fun ReferencePartySeat(
  seat: PartyRoomSeatState,
  isCurrentUsersSeat: Boolean,
  isLocalSpeaking: Boolean = false,
  isLocalMicActive: Boolean = false,
  activeEmojiReaction: ActiveSeatEmojiReaction? = null,
  seatOrbSize: Dp = 60.dp,
  seatItemWidth: Dp = 78.dp,
  labelTopGap: Dp = 4.dp,
  labelPillHeight: Dp = 20.dp,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val theme = getSeatTheme(seat.seatNumber)
  val infiniteTransition = rememberInfiniteTransition(label = "seat_pulse")

  val isActuallySpeaking = seat.isSpeaking || (isCurrentUsersSeat && isLocalSpeaking && isLocalMicActive && !seat.isMuted)

  val scaleFactor = (seatOrbSize / 60.dp).coerceIn(0.58f, 1.12f)
  val innerCircleSize = seatOrbSize * 0.92f
  val avatarSize = seatOrbSize * 0.85f
  val micBadgeSize = (seatOrbSize * 0.283f).coerceIn(12.dp, 18.dp)
  val micBadgeIconSize = (micBadgeSize * 0.58f).coerceIn(7.5.dp, 11.dp)
  val emptyMicIconSize = (seatOrbSize * 0.30f).coerceIn(12.dp, 19.dp)
  val lockIconSize = (seatOrbSize * 0.33f).coerceIn(13.dp, 21.dp)
  val labelFontSize = (9f * scaleFactor.coerceIn(0.84f, 1.0f)).sp
  val totalSeatHeight = seatOrbSize + labelTopGap + labelPillHeight

  // Glowing pulse border when speaking
  val blueGlowAlpha by infiniteTransition.animateFloat(
    initialValue = 0.35f,
    targetValue = 0.95f,
    animationSpec = infiniteRepeatable(
      animation = tween(650, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "blue_glow_alpha"
  )

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Top,
    modifier = modifier
      .width(seatItemWidth)
      .height(totalSeatHeight)
      .clip(RoundedCornerShape(12.dp))
      .clickable { onClick() }
      .testTag("party_seat_${seat.seatNumber}")
  ) {
    // -------------------------------------------------------------------------
    // CIRCULAR SEAT ORB (Responsive calculated diameter: seatOrbSize)
    // -------------------------------------------------------------------------
    Box(
      modifier = Modifier.size(seatOrbSize),
      contentAlignment = Alignment.Center
    ) {
      if (seat.isLocked) {
        // Locked Seat
        Box(
          modifier = Modifier
            .size(innerCircleSize)
            .clip(CircleShape)
            .background(Color(0x882A0808))
            .border(1.5.dp, Color(0xFFFF5252), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Filled.Lock,
            contentDescription = "Seat Locked",
            tint = Color(0xFFFF5252),
            modifier = Modifier.size(lockIconSize)
          )
        }
      } else if (seat.isOccupied) {
        // Occupied Seat: User Avatar + Frame Overlay inside the circle
        if (isActuallySpeaking) {
          // Audio speaking pulse ring
          Box(
            modifier = Modifier
              .size(seatOrbSize)
              .clip(CircleShape)
              .border(
                width = 2.dp,
                color = Color(0xFF00E5FF).copy(alpha = blueGlowAlpha),
                shape = CircleShape
              )
          )
        }

        Box(
          modifier = Modifier
            .size(innerCircleSize)
            .shadow(
              elevation = if (isActuallySpeaking) 8.dp else 4.dp,
              shape = CircleShape,
              spotColor = if (isActuallySpeaking) Color(0xFF00E5FF) else theme.glowColor
            )
            .clip(CircleShape)
            .background(Color(0xFF0D0022))
            .border(
              width = 1.8.dp,
              brush = if (isActuallySpeaking) {
                Brush.sweepGradient(listOf(Color(0xFF00E5FF), Color(0xFF00B0FF), Color(0xFF00E5FF)))
              } else {
                theme.borderBrush
              },
              shape = CircleShape
            ),
          contentAlignment = Alignment.Center
        ) {
          AvatarFrameOverlay(
            size = avatarSize,
            frameId = seat.userFrameId,
            frameUrl = seat.userFrameUrl,
            profileImageUri = seat.userAvatarUri
          )

          // Mute / Speaking Status Badge on bottom edge of avatar
          Box(
            modifier = Modifier
              .size(micBadgeSize)
              .align(Alignment.BottomEnd)
              .clip(CircleShape)
              .background(
                if (seat.isMuted) Color(0xFFD50000) else Color(0xFF00C853)
              )
              .border(1.1.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (seat.isMuted) Icons.Filled.MicOff else Icons.Filled.Mic,
              contentDescription = if (seat.isMuted) "Mic Muted" else "Mic On",
              tint = Color.White,
              modifier = Modifier.size(micBadgeIconSize)
            )
          }
        }
      } else {
        // Empty Seat: Glowing glass orb with audio wave bars & mic
        Box(
          modifier = Modifier
            .size(innerCircleSize)
            .shadow(4.dp, CircleShape, spotColor = theme.glowColor)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                colors = listOf(
                  theme.primaryColor.copy(alpha = 0.85f),
                  theme.secondaryColor.copy(alpha = 0.95f),
                  Color(0xFF0D0022)
                )
              )
            )
            .border(1.8.dp, theme.borderBrush, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 2.dp)
          ) {
            AudioWaveBars(color = Color.White.copy(alpha = 0.85f), scale = scaleFactor)
            Spacer(modifier = Modifier.width((3.dp * scaleFactor).coerceAtLeast(1.5.dp)))
            Icon(
              imageVector = Icons.Filled.Mic,
              contentDescription = "Empty Seat Mic",
              tint = Color.White,
              modifier = Modifier.size(emptyMicIconSize)
            )
            Spacer(modifier = Modifier.width((3.dp * scaleFactor).coerceAtLeast(1.5.dp)))
            AudioWaveBars(color = Color.White.copy(alpha = 0.85f), scale = scaleFactor)
          }
        }
      }

      // Animated On-Seat Emoji Reaction Overlay (Single avatar-centered display ONLY when seat is occupied)
      if (seat.isOccupied && !seat.isLocked && activeEmojiReaction != null) {
        key(activeEmojiReaction.id) {
          SeatEmojiReactionOverlay(
            emoji = activeEmojiReaction.emoji,
            seatNumber = seat.seatNumber,
            seatOrbSize = seatOrbSize
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(labelTopGap))

    // -------------------------------------------------------------------------
    // NAME / SEAT NUMBER PILL (Below the seat circle)
    // -------------------------------------------------------------------------
    if (seat.isOccupied) {
      Box(
        modifier = Modifier
          .height(labelPillHeight)
          .clip(RoundedCornerShape(8.dp))
          .background(Color(0xEE09001D))
          .border(
            width = 1.dp,
            brush = Brush.horizontalGradient(listOf(Color(0xFF00E5FF), Color(0xFFBD00FF))),
            shape = RoundedCornerShape(8.dp)
          )
          .padding(horizontal = 5.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = seat.userName?.ifBlank { "User_${seat.userId?.takeLast(4) ?: "00"}" } ?: "User",
          color = if (isCurrentUsersSeat) Color(0xFF00E5FF) else Color.White,
          fontSize = labelFontSize,
          lineHeight = labelFontSize,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    } else {
      Box(
        modifier = Modifier
          .height(labelPillHeight)
          .clip(RoundedCornerShape(10.dp))
          .background(Color(0xCC0D0022))
          .border(
            width = 1.dp,
            brush = if (seat.isLocked) Brush.linearGradient(listOf(Color(0xFFFF5252), Color(0xFFB71C1C))) else theme.borderBrush,
            shape = RoundedCornerShape(10.dp)
          )
          .padding(horizontal = 6.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = if (seat.isLocked) "Locked" else "No.${seat.seatNumber}",
          color = if (seat.isLocked) Color(0xFFFF5252) else Color.White,
          fontSize = labelFontSize,
          lineHeight = labelFontSize,
          fontWeight = FontWeight.Bold,
          maxLines = 1
        )
      }
    }
  }
}

/**
 * 3 Subtle Audio Wave Bars ||| inside empty seat circle
 */
@Composable
private fun AudioWaveBars(color: Color, scale: Float = 1f) {
  val barWidth = (2.dp * scale).coerceAtLeast(1.2.dp)
  val sideHeight = (8.dp * scale).coerceAtLeast(4.5.dp)
  val centerHeight = (13.dp * scale).coerceAtLeast(7.5.dp)
  val barGap = (1.5.dp * scale).coerceAtLeast(1.dp)
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(barGap)
  ) {
    Box(
      modifier = Modifier
        .width(barWidth)
        .height(sideHeight)
        .clip(RoundedCornerShape(1.dp))
        .background(color)
    )
    Box(
      modifier = Modifier
        .width(barWidth)
        .height(centerHeight)
        .clip(RoundedCornerShape(1.dp))
        .background(color)
    )
    Box(
      modifier = Modifier
        .width(barWidth)
        .height(sideHeight)
        .clip(RoundedCornerShape(1.dp))
        .background(color)
    )
  }
}

// =============================================================================
// BOTTOM BAR ITEM COMPOSABLE
// =============================================================================
@Composable
private fun BottomBarItem(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  label: String,
  orbBrush: Brush,
  borderColor: Color,
  onClick: () -> Unit,
  isHighlighted: Boolean = false,
  modifier: Modifier = Modifier
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(if (isPressed) 0.88f else 1f, label = "press_scale")

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
      .scale(scale)
      .clickable(
        interactionSource = interactionSource,
        indication = null
      ) { onClick() }
      .padding(horizontal = 4.dp, vertical = 2.dp)
      .testTag("bottom_bar_${label.lowercase()}")
  ) {
    // Faceted Glowing Cyber Button (Matching Reference Image)
    Box(
      modifier = Modifier
        .size(44.dp)
        .clip(RoundedCornerShape(13.dp))
        .background(orbBrush)
        .border(
          width = if (isHighlighted) 2.dp else 1.5.dp,
          color = if (isHighlighted) Color.White else borderColor,
          shape = RoundedCornerShape(13.dp)
        ),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = Color.White,
        modifier = Modifier.size(21.dp)
      )
    }

    Spacer(modifier = Modifier.height(3.dp))

    // Label
    Text(
      text = label,
      color = if (isHighlighted) Color(0xFF00E5FF) else Color.White,
      fontSize = 10.sp,
      fontWeight = FontWeight.Bold
    )
  }
}

// =============================================================================
// MENU OPTION ROW COMPOSABLE (Clean Vector Icon System, No Emojis)
// =============================================================================
@Composable
private fun MenuOptionRow(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  iconTint: Color = Color(0xFF00E5FF),
  onClick: () -> Unit
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .clickable { onClick() }
      .padding(horizontal = 8.dp, vertical = 7.dp)
  ) {
    Box(
      modifier = Modifier
        .size(28.dp)
        .clip(CircleShape)
        .background(iconTint.copy(alpha = 0.18f))
        .border(0.8.dp, iconTint.copy(alpha = 0.4f), CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = title,
        tint = iconTint,
        modifier = Modifier.size(15.dp)
      )
    }
    Spacer(modifier = Modifier.width(10.dp))
    Text(
      text = title,
      color = Color.White,
      fontSize = 13.sp,
      fontWeight = FontWeight.Medium
    )
  }
}

// =============================================================================
// TOP HEADER CIRCULAR ACTION BUTTON
// =============================================================================
@Composable
private fun HeaderCircularButton(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  contentDesc: String,
  glowColor: Color = Color(0x66FFFFFF),
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .size(34.dp)
      .clip(CircleShape)
      .background(Color(0x66000000))
      .border(1.2.dp, glowColor, CircleShape)
      .clickable { onClick() },
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = icon,
      contentDescription = contentDesc,
      tint = Color.White,
      modifier = Modifier.size(18.dp)
    )
  }
}

// =============================================================================
// FLOATING GIFT BANNER ANIMATION
// =============================================================================
@Composable
private fun FloatingGiftBanner(gift: ActiveGiftDisplay) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp)
      .clip(RoundedCornerShape(18.dp))
      .background(
        Brush.horizontalGradient(
          listOf(Color(0xEE880E4F), Color(0xEE4A148C), Color(0xEE006064))
        )
      )
      .border(
        1.5.dp,
        Brush.horizontalGradient(listOf(Color(0xFFFFD700), Color(0xFFFF007F))),
        RoundedCornerShape(18.dp)
      )
      .padding(horizontal = 14.dp, vertical = 8.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxWidth()
    ) {
      Text(text = gift.emoji, fontSize = 28.sp)
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(
          text = "${gift.sender} sent ${gift.title}!",
          color = Color(0xFFFFD700),
          fontSize = 13.sp,
          fontWeight = FontWeight.Black
        )
        Text(
          text = "Party celebration event 🌟",
          color = Color.White.copy(alpha = 0.85f),
          fontSize = 10.sp
        )
      }
    }
  }
}

// =============================================================================
// SETTINGS DIALOG (Real Room Name, Real Room Picture, 8 -> 12 -> 16 Seats)
// =============================================================================
@Composable
private fun PartySettingsDialog(
  currentTitle: String,
  currentSeatCount: Int,
  isOwner: Boolean,
  onDismiss: () -> Unit,
  onChangeTitle: (String) -> Unit,
  onPickPicture: () -> Unit,
  onChangeSeatCount: (Int) -> Unit
) {
  var titleText by remember { mutableStateOf(currentTitle) }
  var selectedSeats by remember { mutableIntStateOf(currentSeatCount) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(
      usePlatformDefaultWidth = false,
      decorFitsSystemWindows = false
    )
  ) {
    CenteredModalDialogContainer(onDismiss = onDismiss) {
      Box(
        modifier = Modifier
          .fillMaxWidth(0.94f)
          .clip(RoundedCornerShape(24.dp))
          .background(
            Brush.verticalGradient(listOf(Color(0xFF1B0033), Color(0xFF0C001C)))
          )
          .border(
            1.6.dp,
            Brush.horizontalGradient(listOf(Color(0xFF00E5FF), Color(0xFFE040FB))),
            RoundedCornerShape(24.dp)
          )
          .padding(18.dp)
      ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Filled.Settings,
              contentDescription = null,
              tint = Color(0xFF00E5FF),
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Room Settings",
              color = Color.White,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold
            )
          }
          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (!isOwner) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0x33FF5252))
              .padding(8.dp)
          ) {
            Text(
              text = "Only the room owner or admin can edit room settings.",
              color = Color(0xFFFF8A80),
              fontSize = 11.sp
            )
          }
          Spacer(modifier = Modifier.height(10.dp))
        }

        // 1. Room Name
        Text(text = "Room Name", color = Color(0xFFE0F7FA), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
          value = titleText,
          onValueChange = { if (isOwner) titleText = it },
          enabled = isOwner,
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color(0xFF00E5FF),
            unfocusedBorderColor = Color(0x66FFFFFF)
          )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Room Picture
        Text(text = "Room Wallpaper / Picture", color = Color(0xFFE0F7FA), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(4.dp))
        Button(
          onClick = { if (isOwner) onPickPicture() },
          enabled = isOwner,
          colors = ButtonDefaults.buttonColors(containerColor = Color(0x3300E5FF)),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E5FF)),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(Icons.Filled.AddPhotoAlternate, contentDescription = null, tint = Color(0xFF00E5FF))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Select Picture from Phone", color = Color.White, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Seat Count (8 -> 12 -> 16)
        Text(text = "Number of Seats", color = Color(0xFFE0F7FA), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf(8, 12, 16).forEach { count ->
            val isSelected = selectedSeats == count
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) Color(0xFF7C4DFF) else Color(0x22FFFFFF))
                .border(
                  width = if (isSelected) 1.5.dp else 1.dp,
                  color = if (isSelected) Color(0xFFE040FB) else Color(0x44FFFFFF),
                  shape = RoundedCornerShape(12.dp)
                )
                .clickable(enabled = isOwner) {
                  selectedSeats = count
                }
                .padding(vertical = 10.dp)
                .testTag("seat_count_option_$count"),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "$count Seats",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Save Button
        if (isOwner) {
          Button(
            onClick = {
              if (titleText.isNotBlank()) onChangeTitle(titleText.trim())
              if (selectedSeats != currentSeatCount) onChangeSeatCount(selectedSeats)
              onDismiss()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE040FB)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("btn_save_party_settings")
          ) {
            Text("Save Changes", color = Color.White, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
    }
  }
}

// =============================================================================
// REAL PHONE MUSIC DIALOG (Plays real audio file from device, volume, controls)
// =============================================================================
@Composable
private fun PartyMusicDialog(
  currentTrackTitle: String?,
  isPlaying: Boolean,
  volume: Float,
  onDismiss: () -> Unit,
  onSelectTrack: () -> Unit,
  onTogglePlayPause: () -> Unit,
  onStop: () -> Unit,
  onVolumeChange: (Float) -> Unit
) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(
      usePlatformDefaultWidth = false,
      decorFitsSystemWindows = false
    )
  ) {
    CenteredModalDialogContainer(onDismiss = onDismiss) {
      Box(
        modifier = Modifier
          .fillMaxWidth(0.94f)
          .clip(RoundedCornerShape(24.dp))
          .background(
            Brush.verticalGradient(listOf(Color(0xFF1B0033), Color(0xFF0C001C)))
          )
          .border(
            1.6.dp,
            Brush.horizontalGradient(listOf(Color(0xFFFFD54F), Color(0xFFFF6F00))),
            RoundedCornerShape(24.dp)
          )
          .padding(18.dp)
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Filled.MusicNote,
              contentDescription = null,
              tint = Color(0xFFFFD54F),
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Room Music", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
          }
          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Display real selected music track
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x33FFFFFF))
            .padding(12.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = currentTrackTitle ?: "No audio track selected yet",
            color = Color(0xFFFFD54F),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Button to pick real audio file from phone
        Button(
          onClick = onSelectTrack,
          colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FFB300)),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB300)),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(Icons.Filled.MusicNote, contentDescription = null, tint = Color(0xFFFFD54F))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Select Audio File from Phone", color = Color.White, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Play, Pause, Stop Controls
        Row(
          horizontalArrangement = Arrangement.spacedBy(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Play / Pause
          IconButton(
            onClick = onTogglePlayPause,
            modifier = Modifier
              .size(48.dp)
              .clip(CircleShape)
              .background(Color(0xFFFFB300))
          ) {
            Icon(
              imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
              contentDescription = if (isPlaying) "Pause" else "Play",
              tint = Color.Black,
              modifier = Modifier.size(28.dp)
            )
          }

          // Stop
          IconButton(
            onClick = onStop,
            modifier = Modifier
              .size(48.dp)
              .clip(CircleShape)
              .background(Color(0x33FFFFFF))
              .border(1.dp, Color(0x66FFFFFF), CircleShape)
          ) {
            Icon(Icons.Filled.Stop, contentDescription = "Stop", tint = Color.White, modifier = Modifier.size(24.dp))
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Volume Slider
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Volume", tint = Color(0xFFFFD54F), modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Slider(
            value = volume,
            onValueChange = onVolumeChange,
            valueRange = 0f..1f,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(
              thumbColor = Color(0xFFFFD54F),
              activeTrackColor = Color(0xFFFFD54F),
              inactiveTrackColor = Color(0x44FFFFFF)
            )
          )
        }
      }
    }
    }
  }
}

// =============================================================================
// SEAT LOCK / SEAT OPEN PICKER MODAL
// =============================================================================
@Composable
private fun SeatLockPickerModal(
  seats: List<PartyRoomSeatState>,
  title: String,
  actionText: String,
  emptyMessage: String = "No available seats",
  onDismiss: () -> Unit,
  onSelectSeat: (Int) -> Unit
) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(
      usePlatformDefaultWidth = false,
      decorFitsSystemWindows = false
    )
  ) {
    CenteredModalDialogContainer(onDismiss = onDismiss) {
      Box(
        modifier = Modifier
          .fillMaxWidth(0.94f)
          .clip(RoundedCornerShape(24.dp))
          .background(
            Brush.verticalGradient(listOf(Color(0xFF1B0033), Color(0xFF0C001C)))
          )
          .border(
            1.6.dp,
            Brush.horizontalGradient(listOf(Color(0xFF00E5FF), Color(0xFFFF007F))),
            RoundedCornerShape(24.dp)
          )
          .padding(18.dp)
      ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(text = title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (seats.isEmpty()) {
          Text(
            text = emptyMessage,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 12.sp,
            modifier = Modifier.padding(vertical = 12.dp)
          )
        } else {
          val chunked = seats.chunked(4)
          chunked.forEach { rowSeats ->
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              rowSeats.forEach { seat ->
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x33FFFFFF))
                    .border(
                      1.dp,
                      if (seat.isLocked) Color(0xFFFF5252) else Color(0xFF00E5FF),
                      RoundedCornerShape(12.dp)
                    )
                    .clickable { onSelectSeat(seat.seatNumber) }
                    .padding(vertical = 10.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                      text = "No.${seat.seatNumber}",
                      color = Color.White,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                      text = actionText,
                      color = if (actionText == "Lock") Color(0xFFFF5252) else Color(0xFF00E676),
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              }
              if (rowSeats.size < 4) {
                repeat(4 - rowSeats.size) {
                  Spacer(modifier = Modifier.weight(1f))
                }
              }
            }
            Spacer(modifier = Modifier.height(8.dp))
          }
        }
      }
    }
    }
  }
}

// =============================================================================
// LOWER REAL-TIME MESSAGE AREA
// =============================================================================
@Composable
private fun PartyBoardMessageArea(
  messages: List<PartyRoomChatMessage>,
  onOpenMessageInput: () -> Unit,
  modifier: Modifier = Modifier
) {
  val listState = rememberLazyListState()

  LaunchedEffect(messages.size) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  Box(
    modifier = modifier
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null
      ) { onOpenMessageInput() }
      .testTag("party_board_message_area"),
    contentAlignment = Alignment.BottomStart
  ) {
    if (messages.isEmpty()) {
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(Color(0x6609001D))
          .border(0.8.dp, Color(0x3300E5FF), RoundedCornerShape(12.dp))
          .padding(horizontal = 10.dp, vertical = 5.dp)
      ) {
        Text(
          text = "💬 Room messages appear here in real time. Tap to chat!",
          color = Color(0xAAFFFFFF),
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium
        )
      }
    } else {
      LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        items(
          items = messages,
          key = { it.messageId.ifBlank { "${it.timestamp}_${it.content}" } }
        ) { msg ->
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0x880E0024))
              .border(0.8.dp, Color(0x3300E5FF), RoundedCornerShape(12.dp))
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(
                  Brush.horizontalGradient(listOf(Color(0xFFFFB300), Color(0xFFFF6F00)))
                )
                .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
              Text(
                text = "Lv.${msg.senderLevel.coerceAtLeast(1)}",
                color = Color.Black,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Black
              )
            }
            Spacer(modifier = Modifier.width(5.dp))
            Text(
              text = "${msg.senderName.ifBlank { "User" }}: ",
              color = Color(0xFF00E5FF),
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier.widthIn(max = 88.dp)
            )
            Text(
              text = msg.content,
              color = Color.White,
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium,
              maxLines = 2,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier.testTag("party_chat_message_content")
            )
          }
        }
      }
    }
  }
}

// =============================================================================
// REAL CHAT DIALOG (Message)
// =============================================================================
@Composable
private fun PartyChatDialog(
  messages: List<PartyRoomChatMessage>,
  onDismiss: () -> Unit,
  onSendMessage: (String) -> Unit
) {
  var inputText by remember { mutableStateOf("") }
  val listState = rememberLazyListState()

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(
      usePlatformDefaultWidth = false,
      decorFitsSystemWindows = false
    )
  ) {
    CenteredModalDialogContainer(onDismiss = onDismiss) {
      Box(
        modifier = Modifier
          .fillMaxWidth(0.92f)
          .heightIn(min = 260.dp, max = 420.dp)
          .clip(RoundedCornerShape(24.dp))
          .background(
            Brush.verticalGradient(listOf(Color(0xFF140026), Color(0xFF0A0014)))
          )
          .border(
            1.5.dp,
            Brush.horizontalGradient(listOf(Color(0xFF00E5FF), Color(0xFFE040FB))),
            RoundedCornerShape(24.dp)
          )
          .padding(14.dp)
      ) {
      Column(modifier = Modifier.fillMaxSize()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(text = "💬 Room Chat", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
          IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Messages list
        LazyColumn(
          state = listState,
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          if (messages.isEmpty()) {
            item {
              Text(
                text = "No messages yet. Say hello to everyone!",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 16.dp)
              )
            }
          }
          items(messages) { msg ->
            Row(verticalAlignment = Alignment.Top) {
              Box(
                modifier = Modifier
                  .size(24.dp)
                  .clip(CircleShape)
                  .border(1.dp, Color(0xFF00E5FF), CircleShape)
              ) {
                if (msg.senderAvatarUri != null) {
                  AsyncImage(
                    model = msg.senderAvatarUri,
                    contentDescription = msg.senderName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                  )
                } else {
                  Image(
                    painter = painterResource(id = R.drawable.img_profile_max),
                    contentDescription = msg.senderName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                  )
                }
              }

              Spacer(modifier = Modifier.width(6.dp))

              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = msg.senderName.ifBlank { "User" },
                    color = Color(0xFF80D8FF),
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "Lv.${msg.senderLevel}",
                    color = Color(0xFFFFD54F),
                    fontSize = 8.5.sp
                  )
                }
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x33FFFFFF))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Text(text = msg.content, color = Color.White, fontSize = 11.sp)
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Input Field
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .height(40.dp)
              .clip(RoundedCornerShape(20.dp))
              .background(Color(0x33FFFFFF))
              .border(1.dp, Color(0x6600E5FF), RoundedCornerShape(20.dp))
              .padding(horizontal = 10.dp),
            contentAlignment = Alignment.CenterStart
          ) {
            BasicTextField(
              value = inputText,
              onValueChange = { inputText = it },
              textStyle = TextStyle(color = Color.White, fontSize = 12.sp),
              cursorBrush = SolidColor(Color(0xFF00E5FF)),
              modifier = Modifier.fillMaxWidth(),
              decorationBox = { inner ->
                if (inputText.isEmpty()) {
                  Text("Say something...", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                }
                inner()
              }
            )
          }

          Spacer(modifier = Modifier.width(6.dp))

          IconButton(
            onClick = {
              if (inputText.isNotBlank()) {
                onSendMessage(inputText.trim())
                inputText = ""
              }
            },
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(Color(0xFF00E5FF))
          ) {
            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.Black, modifier = Modifier.size(18.dp))
          }
        }
      }
    }
    }
  }
}

// =============================================================================
// REAL GIFT DIALOG
// Checks real coin balance, deducts real coins, and triggers real gift event
// =============================================================================
@Composable
private fun PartyRealGiftDialog(
  userCoinBalance: Long,
  onDismiss: () -> Unit,
  onSendGift: (name: String, emoji: String, cost: Int) -> Unit
) {
  val realGifts = listOf(
    Triple("Rose", "🌹", 10),
    Triple("Heart", "💖", 50),
    Triple("Crown", "👑", 100),
    Triple("Rocket", "🚀", 500),
    Triple("Sports Car", "🏎️", 1000),
    Triple("Galaxy Castle", "🏰", 2500)
  )

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(
      usePlatformDefaultWidth = false,
      decorFitsSystemWindows = false
    )
  ) {
    CenteredModalDialogContainer(onDismiss = onDismiss) {
      Box(
        modifier = Modifier
          .fillMaxWidth(0.92f)
          .clip(RoundedCornerShape(24.dp))
          .background(
            Brush.verticalGradient(listOf(Color(0xFF22003D), Color(0xFF0D001C)))
          )
          .border(
            1.6.dp,
            Brush.horizontalGradient(listOf(Color(0xFFFF007F), Color(0xFFFFD700))),
            RoundedCornerShape(24.dp)
          )
          .padding(18.dp)
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Filled.CardGiftcard,
              contentDescription = null,
              tint = Color(0xFFFF007F),
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Send Gifts", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
          }
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Filled.WorkspacePremium,
              contentDescription = null,
              tint = Color(0xFFFFD700),
              modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "$userCoinBalance",
              color = Color(0xFFFFD700),
              fontSize = 13.sp,
              fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.width(6.dp))
            IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
              Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(16.dp))
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        val chunked = realGifts.chunked(3)
        chunked.forEach { rowGifts ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            rowGifts.forEach { (name, emoji, cost) ->
              val canAfford = userCoinBalance >= cost
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(14.dp))
                  .background(if (canAfford) Color(0x33FFFFFF) else Color(0x11FFFFFF))
                  .border(
                    1.dp,
                    if (canAfford) Color(0x66FF007F) else Color(0x22FFFFFF),
                    RoundedCornerShape(14.dp)
                  )
                  .clickable { onSendGift(name, emoji, cost) }
                  .padding(8.dp),
                contentAlignment = Alignment.Center
              ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(text = emoji, fontSize = 28.sp)
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = name,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                  )
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = "🪙 $cost",
                    color = if (canAfford) Color(0xFFFFD700) else Color(0x88FFD700),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black
                  )
                }
              }
            }
          }
          Spacer(modifier = Modifier.height(8.dp))
        }
      }
    }
    }
  }
}

// =============================================================================
// REAL GAME DIALOG (Clean Empty State - No fake games, reusable in Party & Live)
// =============================================================================
@Composable
internal fun PartyGameDialog(onDismiss: () -> Unit) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(
      usePlatformDefaultWidth = false,
      decorFitsSystemWindows = false
    )
  ) {
    CenteredModalDialogContainer(onDismiss = onDismiss) {
      Box(
        modifier = Modifier
          .fillMaxWidth(0.9f)
          .clip(RoundedCornerShape(24.dp))
          .background(
            Brush.verticalGradient(listOf(Color(0xFF16002E), Color(0xFF080014)))
          )
          .border(
            1.5.dp,
            Brush.horizontalGradient(listOf(Color(0xFF00E5FF), Color(0xFF2979FF))),
            RoundedCornerShape(24.dp)
          )
          .padding(20.dp)
      ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(text = "🎮 Mini-Games", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
          IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(
          modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(Color(0x3300E5FF)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Filled.SportsEsports,
            contentDescription = "Games",
            tint = Color(0xFF00E5FF),
            modifier = Modifier.size(36.dp)
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "No mini-games currently hosted in this room.",
          color = Color.White,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "Interactive games and tournaments will appear here when configured by room administrators.",
          color = Color.White.copy(alpha = 0.6f),
          fontSize = 11.sp,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(18.dp))

        Button(
          onClick = onDismiss,
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2979FF)),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("Got It", color = Color.White, fontWeight = FontWeight.Bold)
        }
      }
    }
    }
  }
}

// =============================================================================
// RIGHT-SIDE FLOATING GAMES BUTTON & COMPACT PARTY EVENT BOX
// =============================================================================
@Composable
internal fun FloatingPartyGamesButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(if (isPressed) 0.90f else 1f, label = "games_press_scale")

  val infiniteTransition = rememberInfiniteTransition(label = "games_glow")
  val glowAlpha by infiniteTransition.animateFloat(
    initialValue = 0.55f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(900, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "games_glow_alpha"
  )

  Box(
    modifier = modifier
      .scale(scale)
      .size(50.dp)
      .shadow(
        elevation = 8.dp,
        shape = RoundedCornerShape(14.dp),
        spotColor = Color(0xFF00E5FF)
      )
      .clip(RoundedCornerShape(14.dp))
      .background(
        Brush.radialGradient(
          listOf(Color(0xFF2979FF), Color(0xFF1565C0), Color(0xFF0D0024))
        )
      )
      .border(
        width = 1.6.dp,
        color = Color(0xFF00E5FF).copy(alpha = glowAlpha),
        shape = RoundedCornerShape(14.dp)
      )
      .clickable(
        interactionSource = interactionSource,
        indication = null
      ) { onClick() }
      .testTag("floating_games_button"),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = Icons.Filled.SportsEsports,
      contentDescription = "Games",
      tint = Color.White,
      modifier = Modifier.size(25.dp)
    )
  }
}

internal val PARTY_EVENT_SLIDES = listOf(
  PartyEventSlide(
    eventId = PartyEventId.OFFICIAL_HELPLINE,
    badgeEmoji = "🎧",
    title = "OFFICIAL HELPLINE",
    heading = "OFFICIAL HELPLINE",
    subtitle = "Get Support Anytime",
    gradientColors = listOf(Color(0xFF004D61), Color(0xFF00838F), Color(0xFF0A192F)),
    accentColor = Color(0xFF00E5FF)
  ),
  PartyEventSlide(
    eventId = PartyEventId.MEGA_MANAGER_RECRUITMENT,
    badgeEmoji = "🌐",
    title = "PRIYA LIVE",
    heading = "GLOBAL MEGA MANAGER RECRUITMENT",
    subtitle = "Recruiting Mega Managers Across All Countries",
    gradientColors = listOf(Color(0xFF4A148C), Color(0xFF880E4F), Color(0xFF1A0033)),
    accentColor = Color(0xFFFFD54F)
  )
)

@Composable
private fun FloatingPartyEventBox(
  onClick: (PartyEventSlide) -> Unit,
  modifier: Modifier = Modifier
) {
  var currentSlideIndex by remember { mutableIntStateOf(0) }
  LaunchedEffect(Unit) {
    while (true) {
      delay(3000L)
      currentSlideIndex = (currentSlideIndex + 1) % PARTY_EVENT_SLIDES.size
    }
  }

  val slide = PARTY_EVENT_SLIDES[currentSlideIndex]

  Box(
    modifier = modifier
      .size(width = 64.dp, height = 60.dp)
      .shadow(9.dp, RoundedCornerShape(14.dp), spotColor = slide.accentColor)
      .clip(RoundedCornerShape(14.dp))
      .background(Brush.linearGradient(slide.gradientColors))
      .border(
        width = 1.6.dp,
        brush = Brush.horizontalGradient(listOf(slide.accentColor, Color.White.copy(alpha = 0.75f))),
        shape = RoundedCornerShape(14.dp)
      )
      .clickable { onClick(slide) }
      .padding(horizontal = 5.dp, vertical = 4.dp)
      .testTag("floating_event_box"),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.fillMaxSize()
    ) {
      AnimatedContent(
        targetState = slide,
        transitionSpec = {
          fadeIn(animationSpec = tween(260)) togetherWith fadeOut(animationSpec = tween(260))
        },
        label = "event_box_slide_transition",
        modifier = Modifier.weight(1f)
      ) { currentSlide ->
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
          modifier = Modifier.fillMaxSize()
        ) {
          Text(
            text = currentSlide.title,
            color = Color.White,
            fontSize = 7.5.sp,
            lineHeight = 8.5.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )
          Spacer(modifier = Modifier.height(1.5.dp))
          Text(
            text = if (currentSlide.eventId == PartyEventId.MEGA_MANAGER_RECRUITMENT) {
              currentSlide.heading
            } else {
              currentSlide.subtitle
            },
            color = currentSlide.accentColor,
            fontSize = 6.5.sp,
            lineHeight = 7.5.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      // 2-event rotation indicator dots
      Row(
        horizontalArrangement = Arrangement.spacedBy(3.5.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        PARTY_EVENT_SLIDES.forEachIndexed { idx, _ ->
          Box(
            modifier = Modifier
              .size(if (idx == currentSlideIndex) 5.dp else 3.5.dp)
              .clip(CircleShape)
              .background(
                if (idx == currentSlideIndex) slide.accentColor else Color.White.copy(alpha = 0.4f)
              )
          )
        }
      }
    }
  }
}

@Composable
private fun PartyEventFullScreenDialog(
  eventSlide: PartyEventSlide,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val adminPrefs = remember(context) {
    context.getSharedPreferences("admin_event_config", Context.MODE_PRIVATE)
  }
  val helplineSupportDetails = remember(adminPrefs) {
    adminPrefs.getString("official_helpline_contact", null)?.trim()?.takeIf { it.isNotEmpty() }
  }
  val recruitmentDestination = remember(adminPrefs) {
    adminPrefs.getString("mega_manager_recruitment_destination", null)?.trim()?.takeIf { it.isNotEmpty() }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(
      usePlatformDefaultWidth = false,
      decorFitsSystemWindows = false
    )
  ) {
    ConfigureFullScreenDialogWindow()
    BackHandler { onDismiss() }

    val topSafePadding = rememberResponsiveTopBarPadding(extraTopSpacing = 8.dp)
    val bottomSafePadding = rememberResponsiveNavigationBarPadding(extraBottomSpacing = 16.dp)

    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            listOf(Color(0xFF140028), Color(0xFF090016), Color(0xFF05000E))
          )
        )
        .padding(top = topSafePadding, bottom = bottomSafePadding, start = 18.dp, end = 18.dp)
        .testTag(
          if (eventSlide.eventId == PartyEventId.OFFICIAL_HELPLINE) {
            "official_helpline_fullscreen_page"
          } else {
            "mega_manager_recruitment_fullscreen_page"
          }
        )
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Top Header Bar
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = eventSlide.title,
            color = eventSlide.accentColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.8.sp
          )
          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(Color(0x22FFFFFF))
              .testTag("close_event_fullscreen_page")
          ) {
            Icon(
              imageVector = Icons.Filled.Close,
              contentDescription = "Close",
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (eventSlide.eventId == PartyEventId.OFFICIAL_HELPLINE) {
          // EVENT A — OFFICIAL HELPLINE FULL-SCREEN PAGE
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(24.dp))
              .background(Brush.verticalGradient(eventSlide.gradientColors))
              .border(1.5.dp, eventSlide.accentColor.copy(alpha = 0.8f), RoundedCornerShape(24.dp))
              .padding(24.dp)
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
              Text(
                text = "OFFICIAL HELPLINE",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "Get Support Anytime",
                color = eventSlide.accentColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
              )
              Spacer(modifier = Modifier.height(20.dp))
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(16.dp))
                  .background(Color(0x55000000))
                  .border(1.dp, Color(0x4400E5FF), RoundedCornerShape(16.dp))
                  .padding(18.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = helplineSupportDetails ?: "Contact details not available.",
                  color = Color.White,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Medium,
                  textAlign = TextAlign.Center
                )
              }
            }
          }
        } else {
          // EVENT B — PRIYA LIVE GLOBAL MEGA MANAGER RECRUITMENT FULL-SCREEN PAGE
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(24.dp))
              .background(Brush.verticalGradient(eventSlide.gradientColors))
              .border(1.5.dp, eventSlide.accentColor.copy(alpha = 0.85f), RoundedCornerShape(24.dp))
              .padding(24.dp)
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
              Text(
                text = "PRIYA LIVE",
                color = eventSlide.accentColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.2.sp,
                textAlign = TextAlign.Center
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "GLOBAL MEGA MANAGER RECRUITMENT",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "Recruiting Mega Managers Across All Countries",
                color = Color(0xFFE0F7FA),
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
              )
              Spacer(modifier = Modifier.height(18.dp))
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(16.dp))
                  .background(Color(0x55000000))
                  .border(1.dp, Color(0x44FFD54F), RoundedCornerShape(16.dp))
                  .padding(18.dp)
              ) {
                Text(
                  text = "Recruitment is open across Asian countries and other eligible countries worldwide. " +
                    "Qualified agency leaders and regional community builders can partner with Priya Live as Mega Managers.",
                  color = Color.White.copy(alpha = 0.92f),
                  fontSize = 13.sp,
                  lineHeight = 19.sp,
                  textAlign = TextAlign.Center
                )
              }

              if (recruitmentDestination != null) {
                Spacer(modifier = Modifier.height(18.dp))
                Button(
                  onClick = {
                    Toast.makeText(context, recruitmentDestination, Toast.LENGTH_SHORT).show()
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = eventSlide.accentColor),
                  shape = RoundedCornerShape(14.dp),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Text(
                    text = "Apply / Contact",
                    color = Color.Black,
                    fontWeight = FontWeight.Black
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

// =============================================================================
// PARTY ROOM EMOJI SYSTEM (Bottom-Sheet Picker + Single Avatar-Centered Overlay)
// =============================================================================
private val PARTY_EMOJI_ITEMS = listOf(
  Pair("😂", "Joy"),
  Pair("😍", "Love"),
  Pair("😘", "Kiss"),
  Pair("😎", "Cool"),
  Pair("🤩", "Star"),
  Pair("🥳", "Party"),
  Pair("😭", "Cry"),
  Pair("😡", "Angry"),
  Pair("👏", "Clap"),
  Pair("🔥", "Fire"),
  Pair("🎉", "Celebrate"),
  Pair("👑", "Crown"),
  Pair("💖", "Heart"),
  Pair("👍", "Good"),
  Pair("🤔", "Think"),
  Pair("😱", "Shock"),
  Pair("😉", "Wink"),
  Pair("🥺", "Please"),
  Pair("💃", "Dance"),
  Pair("🌹", "Rose"),
  Pair("🥂", "Cheers"),
  Pair("🤭", "Giggle"),
  Pair("😮", "Wow"),
  Pair("😈", "Cheeky")
)

@Composable
private fun PartyEmojiReactionSheet(
  isSeated: Boolean,
  activeSeatNumber: Int?,
  onSelectEmoji: (emoji: String, label: String) -> Unit,
  onSendMessageWithEmoji: (String) -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  var messageDraft by remember { mutableStateOf("") }
  val tabs = listOf("All", "Popular", "Party", "Fun")

  val displayedEmojis = remember(selectedTab) {
    when (selectedTab) {
      1 -> PARTY_EMOJI_ITEMS.take(12)
      2 -> PARTY_EMOJI_ITEMS.filter { it.first in setOf("🥳", "🎉", "👑", "🔥", "💃", "🥂", "🌹", "👏", "🤩", "😎", "💖", "👍") }
      3 -> PARTY_EMOJI_ITEMS.filter { it.first in setOf("😂", "🤭", "🤔", "😱", "🥺", "😭", "😡", "😮", "😉", "😈", "😍", "😘") }
      else -> PARTY_EMOJI_ITEMS
    }
  }

  val openAnim = remember { Animatable(0f) }
  LaunchedEffect(Unit) {
    openAnim.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)
    )
  }
  val sheetAlpha = 0.85f + 0.15f * openAnim.value
  val sheetOffsetY = (1f - openAnim.value) * 24f
  val bottomInsetPadding = rememberResponsiveNavigationBarPadding(
    extraBottomSpacing = if (isSeated) 12.dp else 78.dp
  )
  val sheetShape = if (isSeated) {
    RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
  } else {
    RoundedCornerShape(22.dp)
  }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(
        start = if (isSeated) 0.dp else 12.dp,
        end = if (isSeated) 0.dp else 12.dp,
        bottom = if (isSeated) 0.dp else bottomInsetPadding
      )
      .fillMaxHeight(if (isSeated) 0.48f else 0.42f)
      .heightIn(min = 230.dp, max = 360.dp)
      .graphicsLayer {
        alpha = sheetAlpha
        translationY = sheetOffsetY
      }
      .clip(sheetShape)
      .background(
        Brush.verticalGradient(
          listOf(Color(0xF81A0033), Color(0xFC0B001A))
        )
      )
      .border(
        width = 1.6.dp,
        brush = Brush.horizontalGradient(
          listOf(Color(0xFFFFD54F), Color(0xFFE040FB), Color(0xFF00E5FF))
        ),
        shape = sheetShape
      )
      .pointerInput(Unit) {
        detectTapGestures { /* Consume taps inside sheet without merging child semantics */ }
      }
      .padding(
        start = 14.dp,
        end = 14.dp,
        top = 10.dp,
        bottom = if (isSeated) bottomInsetPadding else 12.dp
      )
      .testTag("party_emoji_panel")
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // Top Drag Handle + Mode Context Pill (Seated vs Message Area Mode)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSeated) Color(0x3300E5FF) else Color(0x33E040FB))
            .border(
              0.8.dp,
              if (isSeated) Color(0xFF00E5FF) else Color(0xFFE040FB),
              RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 8.dp, vertical = 3.dp)
            .testTag("party_emoji_mode_badge")
        ) {
          Text(
            text = if (isSeated && activeSeatNumber != null) {
              "🎤 Seat No.$activeSeatNumber Reaction"
            } else {
              "💬 Message Area Emoji"
            },
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Box(
          modifier = Modifier
            .width(38.dp)
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(Color(0x55FFFFFF))
        )

        Spacer(modifier = Modifier.width(48.dp))
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Header + Category Tabs + Close Button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          tabs.forEachIndexed { idx, tabTitle ->
            val isSelected = selectedTab == idx
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(
                  if (isSelected) Brush.horizontalGradient(listOf(Color(0xFFE040FB), Color(0xFF7C4DFF)))
                  else Brush.horizontalGradient(listOf(Color(0x22FFFFFF), Color(0x22FFFFFF)))
                )
                .border(
                  width = 1.dp,
                  color = if (isSelected) Color(0xFFFFD54F) else Color(0x33FFFFFF),
                  shape = RoundedCornerShape(12.dp)
                )
                .clickable { selectedTab = idx }
                .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Text(
                text = tabTitle,
                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f),
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
              )
            }
          }
        }

        IconButton(
          onClick = onDismiss,
          modifier = Modifier
            .size(28.dp)
            .testTag("close_emoji_panel")
        ) {
          Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = "Close Emoji Panel",
            tint = Color.White.copy(alpha = 0.85f),
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Vertically Scrollable Emoji Grid
      val rows = displayedEmojis.chunked(6)
      Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .verticalScroll(rememberScrollState())
          .testTag("party_emoji_scroll_grid")
      ) {
        rows.forEach { rowItems ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            rowItems.forEach { (emoji, label) ->
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                  .width(48.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(Color(0x18FFFFFF))
                  .border(0.8.dp, Color(0x3300E5FF), RoundedCornerShape(12.dp))
                  .clickable { onSelectEmoji(emoji, label) }
                  .padding(vertical = 5.dp)
                  .testTag("party_emoji_item_$label")
              ) {
                Text(text = emoji, fontSize = 23.sp)
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                  text = label,
                  color = Color.White.copy(alpha = 0.85f),
                  fontSize = 8.5.sp,
                  fontWeight = FontWeight.SemiBold,
                  maxLines = 1
                )
              }
            }
            if (rowItems.size < 6) {
              repeat(6 - rowItems.size) {
                Spacer(modifier = Modifier.width(48.dp))
              }
            }
          }
        }
      }

      // When NOT seated: Integrated bottom message input bar aligned with the room's message area
      if (!isSeated) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("party_emoji_message_input_row"),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .height(36.dp)
              .clip(RoundedCornerShape(18.dp))
              .background(Color(0x26FFFFFF))
              .border(1.dp, Color(0x6600E5FF), RoundedCornerShape(18.dp))
              .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
          ) {
            BasicTextField(
              value = messageDraft,
              onValueChange = { messageDraft = it },
              textStyle = TextStyle(color = Color.White, fontSize = 12.sp),
              cursorBrush = SolidColor(Color(0xFF00E5FF)),
              modifier = Modifier.fillMaxWidth(),
              decorationBox = { inner ->
                if (messageDraft.isEmpty()) {
                  Text(
                    text = "Tap an emoji above or type a message...",
                    color = Color.White.copy(alpha = 0.55f),
                    fontSize = 11.sp
                  )
                }
                inner()
              }
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          IconButton(
            onClick = {
              if (messageDraft.isNotBlank()) {
                onSendMessageWithEmoji(messageDraft)
                messageDraft = ""
              }
            },
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(Color(0xFF00E5FF))
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.Send,
              contentDescription = "Send Message",
              tint = Color.Black,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
private fun SeatEmojiReactionOverlay(
  emoji: String,
  seatNumber: Int,
  seatOrbSize: Dp
) {
  val entryProgress = remember(emoji) { Animatable(0f) }
  LaunchedEffect(emoji) {
    entryProgress.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing)
    )
  }

  val isCrying = emoji in setOf("😭", "🥺")
  val isLaughing = emoji in setOf("😂", "🤭", "🥳")
  val isHeartOrLove = emoji in setOf("💖", "😍", "😘", "🌹")

  val infiniteTransition = rememberInfiniteTransition(label = "seat_emoji_emotion")
  val emotionPulse by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(
        durationMillis = when {
          isLaughing -> 340
          isHeartOrLove -> 520
          else -> 700
        },
        easing = FastOutSlowInEasing
      ),
      repeatMode = RepeatMode.Reverse
    ),
    label = "emotion_pulse"
  )

  val tearDropProgress by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 850, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "tear_drop_progress"
  )

  val p = entryProgress.value
  val entryScale = 0.88f + p * 0.12f
  val activeScale = when {
    isHeartOrLove -> 0.97f + 0.10f * emotionPulse
    isLaughing -> 0.98f + 0.06f * emotionPulse
    isCrying -> 0.99f + 0.03f * emotionPulse
    else -> 0.98f + 0.05f * emotionPulse
  }
  val uniformScale = entryScale * activeScale
  val verticalOffsetPx = if (isLaughing) (emotionPulse - 0.5f) * 5f else 0f
  val alpha = 0.85f + 0.15f * p

  val glowColor = when {
    isHeartOrLove -> Color(0xFFFF4081)
    isCrying -> Color(0xFF00E5FF)
    isLaughing -> Color(0xFFFFD54F)
    else -> Color(0xFF00E5FF)
  }

  val emojiFontSize = (seatOrbSize.value * 0.54f).coerceIn(22f, 36f).sp

  Box(
    modifier = Modifier
      .size(seatOrbSize)
      .graphicsLayer {
        scaleX = uniformScale
        scaleY = uniformScale
        translationY = verticalOffsetPx
        this.alpha = alpha
      }
      .clip(CircleShape)
      .background(
        Brush.radialGradient(
          listOf(
            glowColor.copy(alpha = 0.24f + 0.10f * emotionPulse),
            Color.Transparent
          )
        )
      )
      .semantics(mergeDescendants = true) {}
      .testTag("seat_emoji_reaction_overlay_$seatNumber"),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = emoji,
      fontSize = emojiFontSize
    )

    if (isCrying) {
      // Subtle crying tear droplets descending gently without side particles or distortion
      Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val dropRadius = (w * 0.042f).coerceIn(2f, 4.5f)
        val leftX = w * 0.36f
        val rightX = w * 0.64f
        val startY = h * 0.52f
        val travel = h * 0.22f

        val leftY = startY + travel * tearDropProgress
        val leftAlpha = (1f - tearDropProgress).coerceIn(0f, 0.85f)
        drawCircle(
          color = Color(0xFF40C4FF).copy(alpha = leftAlpha),
          radius = dropRadius,
          center = Offset(leftX, leftY)
        )

        val rightPhase = (tearDropProgress + 0.5f) % 1f
        val rightY = startY + travel * rightPhase
        val rightAlpha = (1f - rightPhase).coerceIn(0f, 0.85f)
        drawCircle(
          color = Color(0xFF40C4FF).copy(alpha = rightAlpha),
          radius = dropRadius,
          center = Offset(rightX, rightY)
        )
      }
    }
  }
}
