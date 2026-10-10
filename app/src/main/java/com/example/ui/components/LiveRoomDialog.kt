package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.core.content.ContextCompat
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.storage.ActiveLiveRoom
import com.example.ui.storage.CallGuestInfo
import com.example.ui.storage.LiveRoomStorage
import com.example.ui.storage.LocalFeatureStorage
import com.example.ui.storage.StoreCenterStorage
import com.example.ui.storage.UserProfileStorage
import com.example.ui.storage.ViewerThumbnail
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPinkBright
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class LiveChatMessageItem(
  val id: String,
  val senderId: String,
  val senderName: String,
  val senderLevel: Int,
  val senderAvatarUri: String?,
  val senderAvatarRes: Int,
  val message: String,
  val isSystemNotice: Boolean = false
)

data class LiveGiftDef(
  val id: String,
  val name: String,
  val iconEmoji: String,
  val priceCoins: Long,
  val color: Color
)

val AvailableLiveGifts = listOf(
  LiveGiftDef("gift_rose", "Neon Rose", "🌹", 10L, Color(0xFFFF4081)),
  LiveGiftDef("gift_heart", "Heart Flare", "💖", 50L, Color(0xFFFF1744)),
  LiveGiftDef("gift_icecream", "Cyber Cone", "🍦", 100L, Color(0xFF00E5FF)),
  LiveGiftDef("gift_ring", "Diamond Ring", "💍", 300L, Color(0xFFFFD700)),
  LiveGiftDef("gift_car", "Cyber Roadster", "🏎️", 1000L, Color(0xFFFF9100)),
  LiveGiftDef("gift_crown", "Imperial Crown", "👑", 2500L, Color(0xFFFFD700)),
  LiveGiftDef("gift_rocket", "Galaxy Rocket", "🚀", 5000L, Color(0xFFBD00FF)),
  LiveGiftDef("gift_castle", "Neon Palace", "🏰", 10000L, Color(0xFFFF007F))
)

data class FloatingReactionData(
  val id: Long,
  val emoji: String,
  val xOffsetFraction: Float
)

/**
 * COMPLETE PRODUCTION-GRADE LIVE ROOM UI & FUNCTIONAL SYSTEM
 * 1:1 aligned with the reference HTML/CSS and specification:
 *
 * 1. Open in full-screen mode.
 * 2. Camera ON after permission with real-time video stream (CameraX).
 * 3. Top-left: Host profile picture, verified checkmark, host name, LIVE badge, viewer count, Top 1 rank pill.
 * 4. Center-top: Live duration timer updating in real time every second.
 * 5. Top-right: Viewer thumbnails with store frames, viewer count pill, close button.
 * 6. Under top-right: Beauty filter toggle pill (Beauty ON).
 * 7. Floating Call Window on right margin:
 *    - Sound waves equalizer animation flanking avatar.
 *    - Incoming Call (for Host) with Accept/Reject.
 *    - Connected Call with End Call & Mute.
 *    - Calling Host (for Viewer) with Cancel Call.
 * 8. Floating 3D Gift Box trigger on right side.
 * 9. Floating Emoji reaction particles rising up.
 * 10. Real-time chat messages with level badges and avatars.
 * 11. Quick input capsule: [💬 Say something... 😊] + active text field.
 * 12. Emoji reaction dock with quick reaction emojis.
 * 13. BOTTOM CONTROL BAR:
 *     - Message
 *     - Emoji
 *     - Mute / Unmute
 *     - Gift
 *     - For VIEWER: Call button (phone icon).
 *     - For HOST (Room Owner ONLY): Settings button (gear icon).
 * 14. Full Room Owner Settings dialog:
 *     - Room name, theme cover, room permissions (comments, gifts, calls, lock),
 *       approved guest/call permissions, user moderation (admin, mute, kick), end live.
 */
@Composable
fun LiveRoomDialog(
  room: ActiveLiveRoom,
  isHost: Boolean,
  onExitRoom: () -> Unit,
  onEscapeRoom: () -> Unit,
  onOpenUserProfile: (userId: String) -> Unit = {}
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()

  val activeUserId = remember { UserProfileStorage.getActiveUserId(context) }
  val activeUserProfile = remember { UserProfileStorage.getProfileForUser(context, activeUserId) }

  // Room state
  var currentRoom by remember { mutableStateOf(room) }
  var roomTitle by remember { mutableStateOf(room.roomTitle) }
  var isRoomLocked by remember { mutableStateOf(room.isLocked) }
  var roomCoinsScore by remember { mutableLongStateOf(room.heartScore) }

  // Permissions state
  val isOwner = isHost || currentRoom.hostUserId == activeUserId
  var adminList by remember { mutableStateOf(currentRoom.adminUserIds.toMutableList()) }
  val isAdmin = isOwner || adminList.contains(activeUserId)
  var mutedList by remember { mutableStateOf(currentRoom.mutedUserIds.toMutableList()) }
  var kickedList by remember { mutableStateOf(currentRoom.kickedUserIds.toMutableList()) }

  // Additional room moderation permissions
  var allowComments by remember { mutableStateOf(true) }
  var allowGifts by remember { mutableStateOf(true) }
  var allowCallRequests by remember { mutableStateOf(true) }
  var autoApproveVipCalls by remember { mutableStateOf(false) }

  // UI state
  var showExitConfirmDialog by remember { mutableStateOf(false) }
  var isMicMuted by remember { mutableStateOf(false) }
  var isBeautyFilterEnabled by remember { mutableStateOf(true) }

  // Local Voice channel state
  val isLocalSpeaking = !isMicMuted && (isOwner || currentRoom.activeCallGuest?.userId == activeUserId)
  val speakingUsers = emptySet<Int>()

  val micPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      if (isOwner || currentRoom.activeCallGuest?.userId == activeUserId) {
        Toast.makeText(context, "Microphone enabled & broadcasting", Toast.LENGTH_SHORT).show()
      }
    } else {
      if (isOwner || currentRoom.activeCallGuest?.userId == activeUserId) {
        Toast.makeText(context, "Microphone permission is required for live audio", Toast.LENGTH_SHORT).show()
      }
    }
  }

  // Automatically request and acquire Runtime Microphone Permissions (RECORD_AUDIO) before joining a voice channel
  LaunchedEffect(Unit) {
    val hasMicPerm = ContextCompat.checkSelfPermission(
      context,
      Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED
    if (!hasMicPerm) {
      micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }
  }

  // Timer: real-time duration updates every second
  var elapsedSeconds by remember {
    val initial = ((System.currentTimeMillis() - room.startTimeMs) / 1000).coerceAtLeast(0L)
    mutableLongStateOf(initial)
  }

  LaunchedEffect(room.startTimeMs, isOwner) {
    var accumulatedSeconds = 0L
    while (true) {
      delay(1000L)
      elapsedSeconds++
      if (isOwner) {
        accumulatedSeconds++
        if (accumulatedSeconds >= 10L) {
          LocalFeatureStorage.recordEligibleLiveStreamingTime(context, activeUserId, accumulatedSeconds)
          accumulatedSeconds = 0L
        }
      }
    }
  }

  val formattedDuration = remember(elapsedSeconds) {
    val hrs = elapsedSeconds / 3600
    val mins = (elapsedSeconds % 3600) / 60
    val secs = elapsedSeconds % 60
    if (hrs > 0) {
      String.format(java.util.Locale.US, "%02d:%02d:%02d", hrs, mins, secs)
    } else {
      String.format(java.util.Locale.US, "%02d:%02d", mins, secs)
    }
  }

  // Real-time Chat
  val chatMessages = remember {
    mutableStateListOf<LiveChatMessageItem>(
      LiveChatMessageItem(
        id = "sys_welcome",
        senderId = "system",
        senderName = "SYSTEM",
        senderLevel = 99,
        senderAvatarUri = null,
        senderAvatarRes = R.drawable.img_profile_max,
        message = "Welcome to ${room.roomTitle}! Follow community rules & enjoy.",
        isSystemNotice = true
      ),
      LiveChatMessageItem(
        id = "chat_1",
        senderId = "user_201",
        senderName = "Ratul Khan",
        senderLevel = 12,
        senderAvatarUri = null,
        senderAvatarRes = R.drawable.img_streamer_girl,
        message = "Send Rose 🌹 x1"
      ),
      LiveChatMessageItem(
        id = "chat_2",
        senderId = "user_202",
        senderName = "Tanha",
        senderLevel = 8,
        senderAvatarUri = null,
        senderAvatarRes = R.drawable.img_streamer_girl,
        message = "You are so beautiful ❤️"
      ),
      LiveChatMessageItem(
        id = "chat_3",
        senderId = "user_203",
        senderName = "Shanto",
        senderLevel = 15,
        senderAvatarUri = null,
        senderAvatarRes = R.drawable.img_streamer_girl,
        message = "Nice voice ❤️"
      ),
      LiveChatMessageItem(
        id = "chat_4",
        senderId = "user_204",
        senderName = "Nodi",
        senderLevel = 6,
        senderAvatarUri = null,
        senderAvatarRes = R.drawable.img_streamer_girl,
        message = "Favorite live ❤️"
      )
    )
  }
  var chatInputText by remember { mutableStateOf("") }
  var isChatInputFocused by remember { mutableStateOf(false) }
  var showEmojiPanel by remember { mutableStateOf(false) }
  val chatListState = rememberLazyListState()

  // Gift & Game states
  var showGiftDialog by remember { mutableStateOf(false) }
  var showGameDialog by remember { mutableStateOf(false) }
  var userCoins by remember { mutableLongStateOf(UserProfileStorage.getCoinBalance(context, activeUserId)) }
  var activeGiftBanner by remember { mutableStateOf<String?>(null) }
  var activeGiftEmoji by remember { mutableStateOf<String?>(null) }

  // Settings Dialog (Host only)
  var showHostSettingsDialog by remember { mutableStateOf(false) }

  // Profile Preview dialog
  var selectedUserForProfile by remember {
    mutableStateOf<Triple<String, String, String?>?>(null)
  }

  // Call System states
  var activeCallGuest by remember { mutableStateOf(currentRoom.activeCallGuest) }
  var incomingCallRequest by remember { mutableStateOf(currentRoom.incomingCallRequester) }
  var isCallRequestPending by remember { mutableStateOf(false) }
  var isCallGuestMuted by remember { mutableStateOf(false) }

  // Viewers list
  val roomViewers = remember {
    mutableStateListOf<ViewerThumbnail>().apply {
      addAll(room.viewers)
      if (!isHost && none { it.userId == activeUserId }) {
        add(
          ViewerThumbnail(
            userId = activeUserId,
            username = activeUserProfile.username,
            avatarUri = activeUserProfile.profileImageUri,
            avatarRes = R.drawable.img_streamer_girl,
            level = activeUserProfile.userLevel
          )
        )
      }
    }
  }

  // Floating emoji reaction items
  val floatingReactions = remember { mutableStateListOf<FloatingReactionData>() }
  val centerFloatingEmojis = remember { mutableStateListOf<FloatingReactionData>() }

  fun showFloatingEmoji(emoji: String) {
    val id = System.currentTimeMillis() + (0..1000).random()
    centerFloatingEmojis.add(FloatingReactionData(id, emoji, 0.5f))
    // Also add to chat stream
    chatMessages.add(
      LiveChatMessageItem(
        id = "rx_$id",
        senderId = activeUserId,
        senderName = activeUserProfile.username,
        senderLevel = activeUserProfile.userLevel,
        senderAvatarUri = activeUserProfile.profileImageUri,
        senderAvatarRes = R.drawable.img_streamer_girl,
        message = "Reacted $emoji"
      )
    )
    coroutineScope.launch {
      chatListState.animateScrollToItem((chatMessages.size - 1).coerceAtLeast(0))
      delay(1300L)
      centerFloatingEmojis.removeAll { it.id == id }
    }
  }

  fun triggerReaction(emoji: String) {
    showFloatingEmoji(emoji)
  }

  // Local room synchronization
  DisposableEffect(currentRoom.roomId) {
    LiveRoomStorage.saveOrUpdateRoom(context, currentRoom)
    onDispose {
      if (isOwner) {
        LiveRoomStorage.removeRoom(context, currentRoom.roomId)
      }
    }
  }

  // Equipped store frames
  val hostEquippedFrame = remember(currentRoom.hostUserId) {
    StoreCenterStorage.getEquippedFrame(context, currentRoom.hostUserId)
  }

  // Check if current user is kicked
  if (kickedList.contains(activeUserId)) {
    Toast.makeText(context, "You have been removed from this live room.", Toast.LENGTH_SHORT).show()
    onExitRoom()
    return
  }

  BackHandler { showExitConfirmDialog = true }

  val topSafePadding = rememberResponsiveTopBarPadding(extraTopSpacing = 2.dp)
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF060012))
      .pointerInput(Unit) {
        detectTapGestures { /* Consume background touches */ }
      }
  ) {
      // 1. REAL-TIME CAMERA / STREAM VIDEO FEED (CameraX)
      CameraLivePreview(
        isHost = isHost,
        fallbackAvatarRes = currentRoom.hostAvatarRes,
        fallbackAvatarUri = currentRoom.hostAvatarUri,
        isEnhancementEnabled = isBeautyFilterEnabled,
        onToggleEnhancement = {
          isBeautyFilterEnabled = !isBeautyFilterEnabled
          Toast.makeText(
            context,
            if (isBeautyFilterEnabled) "Beauty Enhancement ON" else "Beauty Enhancement OFF",
            Toast.LENGTH_SHORT
          ).show()
        },
        modifier = Modifier.fillMaxSize()
      )

      // Cyber ambient gradient overlay
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.verticalGradient(
              colors = listOf(
                Color(0x99080016),
                Color(0x22000000),
                Color(0x10000000),
                Color(0xCC060014)
              )
            )
          )
      )

      // 2. LIVE DURATION TIMER PILL (Center Top)
      Box(
        modifier = Modifier
          .align(Alignment.TopCenter)
          .padding(top = topSafePadding + 4.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(Color(0xAA110026))
          .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
          .padding(horizontal = 10.dp, vertical = 4.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(7.dp)
              .clip(CircleShape)
              .background(Color(0xFFFF1744))
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = formattedDuration,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      // 3. TOP-LEFT: HOST CARD & TOP-RIGHT: AUDIENCE & EXIT
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = topSafePadding, bottom = 4.dp, start = 12.dp, end = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        // TOP-LEFT: HOST CARD + RANK PILL
        val isHostSpeaking = if (isOwner) {
          isLocalSpeaking && !isMicMuted
        } else {
          speakingUsers.isNotEmpty()
        }

        Column {
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(26.dp))
              .background(Color(0xDD120028))
              .border(
                if (isHostSpeaking) 2.dp else 1.dp,
                if (isHostSpeaking) Brush.horizontalGradient(listOf(Color(0xFF00E676), Color(0xFF00B0FF)))
                else Brush.horizontalGradient(listOf(NeonPink, NeonCyan)),
                RoundedCornerShape(26.dp)
              )
              .clickable {
                selectedUserForProfile = Triple(
                  currentRoom.hostUserId,
                  currentRoom.hostName,
                  currentRoom.hostAvatarUri
                )
              }
              .padding(start = 4.dp, end = 10.dp, top = 3.dp, bottom = 3.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Host Avatar with verified checkmark
            Box(contentAlignment = Alignment.BottomEnd) {
              AvatarFrameOverlay(
                size = 36.dp,
                frame = hostEquippedFrame,
                profileImageUri = currentRoom.hostAvatarUri,
                avatarRes = currentRoom.hostAvatarRes
              )
              // Blue verified check badge
              Box(
                modifier = Modifier
                  .size(13.dp)
                  .clip(CircleShape)
                  .background(Color(0xFF00B0FF))
                  .border(0.8.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  Icons.Filled.Check,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(9.dp)
                )
              }
            }

            Spacer(modifier = Modifier.width(6.dp))

            Column {
              Text(
                text = currentRoom.hostName,
                color = Color.White,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.width(96.dp)
              )

              Row(verticalAlignment = Alignment.CenterVertically) {
                // LIVE Badge
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFFF1744))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                  Text(
                    text = "LIVE",
                    color = Color.White,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Black
                  )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // View counter
                Icon(
                  Icons.Filled.Visibility,
                  contentDescription = null,
                  tint = Color.White.copy(alpha = 0.7f),
                  modifier = Modifier.size(10.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                  text = "${(roomViewers.size * 942).coerceAtLeast(1)}",
                  color = Color.White.copy(alpha = 0.85f),
                  fontSize = 9.5.sp,
                  fontWeight = FontWeight.Medium
                )
              }
            }

            // Follow button for viewers
            if (!isOwner) {
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .size(24.dp)
                  .clip(CircleShape)
                  .background(NeonPink)
                  .clickable {
                    Toast.makeText(context, "Followed ${currentRoom.hostName}!", Toast.LENGTH_SHORT).show()
                  },
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  Icons.Filled.Add,
                  contentDescription = "Follow",
                  tint = Color.White,
                  modifier = Modifier.size(14.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(4.dp))

          // Top 1 Ranking Pill
          Box(
            modifier = Modifier
              .padding(start = 6.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0x99000000))
              .border(0.8.dp, NeonGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
              .clickable {
                Toast.makeText(context, "Hourly Superstar Ranking: Top 1 🔥", Toast.LENGTH_SHORT).show()
              }
              .padding(horizontal = 8.dp, vertical = 2.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("🔥 Top 1", color = NeonGold, fontSize = 10.sp, fontWeight = FontWeight.Black)
              Spacer(modifier = Modifier.width(3.dp))
              Text("›", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
            }
          }
        }

        // TOP-RIGHT: AUDIENCE AVATARS + COUNT + EXIT BUTTON
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          // Audience wrapper
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(Color(0xAA120025))
              .border(0.8.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
              .padding(horizontal = 6.dp, vertical = 3.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              // Avatars (overlapping)
              LazyRow(
                horizontalArrangement = Arrangement.spacedBy((-6).dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                val topViewers = roomViewers.take(3)
                items(topViewers) { viewer ->
                  val viewerFrame = StoreCenterStorage.getEquippedFrame(context, viewer.userId)
                  Box(
                    modifier = Modifier.clickable {
                      selectedUserForProfile = Triple(viewer.userId, viewer.username, viewer.avatarUri)
                    }
                  ) {
                    AvatarFrameOverlay(
                      size = 24.dp,
                      frame = viewerFrame,
                      profileImageUri = viewer.avatarUri,
                      avatarRes = viewer.avatarRes
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.width(5.dp))

              // Count
              Icon(
                Icons.Filled.Person,
                contentDescription = null,
                tint = NeonCyan,
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(2.dp))
              Text(
                text = "${roomViewers.size.coerceAtLeast(1)}",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          // CLOSE / EXIT BUTTON (X)
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(Color(0xCC200038))
              .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
              .clickable { showExitConfirmDialog = true },
            contentAlignment = Alignment.Center
          ) {
            Icon(
              Icons.Filled.Close,
              contentDescription = "Exit Room",
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      // BEAUTY FILTER TOGGLE PILL (Right below Top-Right)
      Box(
        modifier = Modifier
          .align(Alignment.TopEnd)
          .padding(top = topSafePadding + 38.dp, end = 12.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(
            if (isBeautyFilterEnabled) Brush.horizontalGradient(listOf(Color(0xFFFF007F), Color(0xFFFFD700)))
            else Brush.horizontalGradient(listOf(Color(0x66110022), Color(0x66110022)))
          )
          .border(
            1.dp,
            if (isBeautyFilterEnabled) NeonGold else Color.White.copy(alpha = 0.4f),
            RoundedCornerShape(16.dp)
          )
          .clickable {
            isBeautyFilterEnabled = !isBeautyFilterEnabled
            Toast.makeText(
              context,
              if (isBeautyFilterEnabled) "Beauty Enhancement Active" else "Beauty Filter Disabled",
              Toast.LENGTH_SHORT
            ).show()
          }
          .padding(horizontal = 9.dp, vertical = 4.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            Icons.Filled.AutoAwesome,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            if (isBeautyFilterEnabled) "Beauty ON" else "Beauty",
            color = Color.White,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      // 4. CONNECTED LIVE CALL TILE (Upper-Right)
      if (activeCallGuest != null) {
        Box(
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(top = topSafePadding + 74.dp, end = 10.dp)
            .width(168.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xF212002A))
            .border(
              1.2.dp,
              Brush.verticalGradient(listOf(NeonCyan, NeonPink)),
              RoundedCornerShape(18.dp)
            )
            .padding(10.dp)
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  Icons.Filled.PhoneInTalk,
                  contentDescription = null,
                  tint = NeonCyan,
                  modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Live Call",
                  color = Color.White,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Black
                )
              }
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(Color(0xFF00E676))
              )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center,
              modifier = Modifier.fillMaxWidth()
            ) {
              AudioEqualizerWaves(color = NeonCyan)
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(CircleShape)
                  .border(1.5.dp, NeonPink, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                AvatarFrameOverlay(
                  size = 44.dp,
                  profileImageUri = activeCallGuest?.avatarUri,
                  avatarRes = activeCallGuest?.avatarRes ?: R.drawable.img_streamer_girl
                )
              }
              Spacer(modifier = Modifier.width(6.dp))
              AudioEqualizerWaves(color = NeonPink)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = activeCallGuest?.username ?: "Guest",
              color = Color.White,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Button(
                onClick = {
                  isCallGuestMuted = !isCallGuestMuted
                  Toast.makeText(
                    context,
                    if (isCallGuestMuted) "Guest audio muted" else "Guest audio unmuted",
                    Toast.LENGTH_SHORT
                  ).show()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (isCallGuestMuted) Color(0xFF550011) else Color(0xFF220044)
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp, horizontal = 2.dp)
              ) {
                Text(if (isCallGuestMuted) "Unmute" else "Mute", color = Color.White, fontSize = 10.sp)
              }

              Button(
                onClick = {
                  activeCallGuest = null
                  currentRoom = currentRoom.copy(activeCallGuest = null)
                  LiveRoomStorage.saveOrUpdateRoom(context, currentRoom)
                  Toast.makeText(context, "Call ended.", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF1744)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp, horizontal = 2.dp)
              ) {
                Text("End", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
              }
            }
          }
        }
      } else if (!isOwner && isCallRequestPending) {
        // Viewer Calling Host Pill
        Box(
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(top = topSafePadding + 74.dp, end = 10.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xEE12002A))
            .border(1.dp, NeonGold, RoundedCornerShape(14.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.PhoneInTalk, contentDescription = null, tint = NeonGold, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Calling Host...", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              "Cancel",
              color = Color(0xFFFF5252),
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.clickable {
                isCallRequestPending = false
                incomingCallRequest = null
                currentRoom = currentRoom.copy(incomingCallRequester = null)
                LiveRoomStorage.saveOrUpdateRoom(context, currentRoom)
                Toast.makeText(context, "Call request canceled.", Toast.LENGTH_SHORT).show()
              }
            )
          }
        }
      }

      // 5. CENTER FLOATING EMOJI ANIMATION (Exact match to prompt JS: center 50%, 55%, 65px font, rises -180px, scale .7 -> 1.2 -> 1, opacity 0 -> 1 -> 0, 1200ms)
      centerFloatingEmojis.forEach { item ->
        val animProgress = remember(item.id) { Animatable(0f) }
        LaunchedEffect(item.id) {
          animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing)
          )
        }

        val progress = animProgress.value
        val translateY = if (progress <= 0.35f) {
          -80f * (progress / 0.35f)
        } else {
          -80f - 100f * ((progress - 0.35f) / 0.65f)
        }
        val scale = if (progress <= 0.35f) {
          0.7f + 0.5f * (progress / 0.35f)
        } else {
          1.2f - 0.2f * ((progress - 0.35f) / 0.65f)
        }
        val alpha = if (progress <= 0.25f) {
          (progress / 0.25f).coerceIn(0f, 1f)
        } else if (progress <= 0.65f) {
          1f
        } else {
          (1f - (progress - 0.65f) / 0.35f).coerceIn(0f, 1f)
        }

        Box(
          modifier = Modifier
            .align(Alignment.Center)
            .offset(y = (20 + translateY).dp)
            .graphicsLayer {
              scaleX = scale
              scaleY = scale
              this.alpha = alpha
            }
        ) {
          Text(text = item.emoji, fontSize = 65.sp)
        }
      }

      // 6. FLOATING EMOJI REACTIONS CASCADE
      floatingReactions.forEach { item ->
        val progress = remember { Animatable(1f) }
        LaunchedEffect(item.id) {
          progress.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 2200, easing = LinearEasing)
          )
        }
        val yOffset = 500.dp * progress.value
        val alphaVal = progress.value.coerceIn(0f, 1f)

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomStart)
            .padding(bottom = 80.dp)
            .offset(x = (280 * item.xOffsetFraction).dp, y = -yOffset)
            .alpha(alphaVal)
        ) {
          Text(text = item.emoji, fontSize = 28.sp)
        }
      }

      // 7. GIFT ANIMATION BANNER (When any user sends a gift)
      AnimatedVisibility(
        visible = activeGiftBanner != null,
        enter = fadeIn() + scaleIn(initialScale = 0.90f),
        exit = fadeOut() + scaleOut(targetScale = 0.90f),
        modifier = Modifier
          .align(Alignment.Center)
          .padding(horizontal = 24.dp)
      ) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.horizontalGradient(listOf(Color(0xFFFF007F), Color(0xFFFF9100))))
            .border(2.dp, Color(0xFFFFD700), RoundedCornerShape(20.dp))
            .padding(horizontal = 20.dp, vertical = 14.dp),
          contentAlignment = Alignment.Center
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = activeGiftEmoji ?: "🎁",
              fontSize = 32.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
              text = activeGiftBanner ?: "",
              color = Color.White,
              fontSize = 15.sp,
              fontWeight = FontWeight.Black
            )
          }
        }
      }

      // 8. BOTTOM AREA: CHAT STREAM + QUICK INPUT + EMOJI DOCK + CONTROL BAR
      val responsiveBottomPadding = rememberResponsiveNavigationBarPadding(extraBottomSpacing = 6.dp)
      Column(
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .fillMaxWidth()
          .padding(bottom = responsiveBottomPadding, start = 12.dp, end = 12.dp)
      ) {
        // Real-Time Chat Stream (Displays live messages)
        LazyColumn(
          state = chatListState,
          modifier = Modifier
            .fillMaxWidth(0.82f)
            .height(170.dp),
          verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
          items(chatMessages) { msg ->
            Row(
              modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0x990F0022))
                .border(0.7.dp, Color(0xFF4A0082), RoundedCornerShape(14.dp))
                .clickable {
                  if (!msg.isSystemNotice) {
                    selectedUserForProfile = Triple(msg.senderId, msg.senderName, msg.senderAvatarUri)
                  }
                }
                .padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              if (msg.isSystemNotice) {
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(NeonGold)
                    .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                  Text("NOTICE", color = Color.Black, fontSize = 8.sp, fontWeight = FontWeight.Black)
                }
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                  text = msg.message,
                  color = NeonGold,
                  fontSize = 11.5.sp,
                  fontWeight = FontWeight.Medium
                )
              } else {
                // User avatar thumbnail in chat
                val userFrame = StoreCenterStorage.getEquippedFrame(context, msg.senderId)
                AvatarFrameOverlay(
                  size = 18.dp,
                  frame = userFrame,
                  profileImageUri = msg.senderAvatarUri,
                  avatarRes = msg.senderAvatarRes
                )
                Spacer(modifier = Modifier.width(5.dp))

                // Level badge
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF8A00D4))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                  Text(
                    text = "Lv.${msg.senderLevel}",
                    color = NeonGold,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
                Spacer(modifier = Modifier.width(5.dp))

                Text(
                  text = "${msg.senderName}: ",
                  color = NeonCyan,
                  fontSize = 11.5.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = msg.message,
                  color = Color.White,
                  fontSize = 11.5.sp
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // EMOJI REACTION DOCK (Toggled by emoji button)
        AnimatedVisibility(
          visible = showEmojiPanel,
          enter = fadeIn() + scaleIn(initialScale = 0.95f),
          exit = fadeOut() + scaleOut(targetScale = 0.95f)
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .widthIn(max = 420.dp)
              .clip(RoundedCornerShape(20.dp))
              .background(Color(0xF514082D))
              .border(1.dp, Color(0x8CDC5AFF), RoundedCornerShape(20.dp))
              .shadow(22.dp, RoundedCornerShape(20.dp), ambientColor = Color(0x73B43CFF), spotColor = Color(0x73B43CFF))
              .padding(horizontal = 12.dp, vertical = 10.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              listOf("🧐", "🤭", "😍", "🤔", "🙄", "😮", "😂", "🥺").forEach { emoji ->
                Box(
                  modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0x14FFFFFF))
                    .clickable(
                      interactionSource = remember { MutableInteractionSource() },
                      indication = null,
                      onClick = {
                        showFloatingEmoji(emoji)
                      }
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  Text(text = emoji, fontSize = 20.sp)
                }
              }
            }
          }
        }

        if (showEmojiPanel) {
          Spacer(modifier = Modifier.height(8.dp))
        }

        // QUICK INPUT CAPSULE: [💬 Say something...] (Small smiley removed per prompt instruction)
        if (!isChatInputFocused) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(22.dp))
              .background(Color(0x880C001C))
              .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(22.dp))
              .clickable {
                if (allowComments) {
                  isChatInputFocused = true
                } else {
                  Toast.makeText(context, "Comments are currently disabled by the room owner.", Toast.LENGTH_SHORT).show()
                }
              }
              .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              Icons.Filled.ChatBubble,
              contentDescription = null,
              tint = Color.White.copy(alpha = 0.6f),
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (allowComments) "Say something..." else "Comments turned off by host",
              color = Color.White.copy(alpha = 0.6f),
              fontSize = 12.5.sp
            )
          }
        } else {
          // ACTIVE CHAT INPUT FIELD
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(24.dp))
              .background(Color(0xEE160032))
              .border(1.dp, NeonCyan, RoundedCornerShape(24.dp))
              .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            BasicTextField(
              value = chatInputText,
              onValueChange = { chatInputText = it },
              textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
              cursorBrush = SolidColor(NeonCyan),
              modifier = Modifier.weight(1f),
              decorationBox = { innerTextField ->
                if (chatInputText.isEmpty()) {
                  Text("Send a message...", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
                }
                innerTextField()
              }
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
              onClick = {
                if (chatInputText.isNotBlank()) {
                  if (mutedList.contains(activeUserId)) {
                    Toast.makeText(context, "You are muted in this room.", Toast.LENGTH_SHORT).show()
                  } else {
                    val trimmedText = chatInputText.trim()
                    chatMessages.add(
                      LiveChatMessageItem(
                        id = "msg_${System.currentTimeMillis()}",
                        senderId = activeUserId,
                        senderName = activeUserProfile.username,
                        senderLevel = activeUserProfile.userLevel,
                        senderAvatarUri = activeUserProfile.profileImageUri,
                        senderAvatarRes = R.drawable.img_streamer_girl,
                        message = trimmedText
                      )
                    )
                    chatInputText = ""
                    isChatInputFocused = false
                    coroutineScope.launch {
                      chatListState.animateScrollToItem((chatMessages.size - 1).coerceAtLeast(0))
                    }
                  }
                }
              },
              modifier = Modifier.size(32.dp)
            ) {
              Icon(
                Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = NeonPinkBright,
                modifier = Modifier.size(20.dp)
              )
            }

            IconButton(
              onClick = { isChatInputFocused = false },
              modifier = Modifier.size(28.dp)
            ) {
              Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White.copy(alpha = 0.6f))
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // CALL REQUEST PANEL (Owner Only - when incoming call request is pending)
        // 4. CALL PANEL: If nobody is currently calling, the incoming-call panel MUST NOT appear.
        AnimatedVisibility(
          visible = isOwner && incomingCallRequest != null,
          enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
          exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
        ) {
          val requester = incomingCallRequest
          if (requester != null) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(
                  Brush.linearGradient(
                    listOf(Color(0xFA0A1437), Color(0xFA190A41))
                  )
                )
                .border(1.dp, Color(0xA628BEFF), RoundedCornerShape(22.dp))
                .shadow(25.dp, RoundedCornerShape(22.dp), ambientColor = Color(0x731E96FF), spotColor = Color(0x731E96FF))
                .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                  // caller-avatar
                  Box(
                    modifier = Modifier
                      .size(46.dp)
                      .clip(CircleShape)
                      .border(2.dp, Color.White, CircleShape)
                      .background(Brush.linearGradient(listOf(Color(0xFFB92CFF), Color(0xFF26D9FF)))),
                    contentAlignment = Alignment.Center
                  ) {
                    AvatarFrameOverlay(
                      size = 46.dp,
                      profileImageUri = requester.avatarUri,
                      avatarRes = requester.avatarRes
                    )
                  }

                  Spacer(modifier = Modifier.width(10.dp))

                  // caller-info
                  Column {
                    Text(
                      text = requester.username,
                      color = Color.White,
                      fontWeight = FontWeight.Bold,
                      fontSize = 13.5.sp,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                      text = "Incoming Call",
                      color = Color.White.copy(alpha = 0.7f),
                      fontSize = 11.sp
                    )
                  }
                }

                // call-actions: Accept (#20b86b), Reject (#d93668)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  Button(
                    onClick = {
                      activeCallGuest = CallGuestInfo(
                        userId = requester.userId,
                        username = requester.username,
                        avatarUri = requester.avatarUri,
                        avatarRes = requester.avatarRes
                      )
                      incomingCallRequest = null
                      currentRoom = currentRoom.copy(
                        activeCallGuest = activeCallGuest,
                        incomingCallRequester = null
                      )
                      LiveRoomStorage.saveOrUpdateRoom(context, currentRoom)
                      Toast.makeText(context, "${requester.username} joined the call!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF20B86B)),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 11.dp, vertical = 7.dp)
                  ) {
                    Text("Accept", color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                  }

                  Button(
                    onClick = {
                      val name = requester.username
                      incomingCallRequest = null
                      currentRoom = currentRoom.copy(incomingCallRequester = null)
                      LiveRoomStorage.saveOrUpdateRoom(context, currentRoom)
                      Toast.makeText(context, "Call request from $name rejected.", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD93668)),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 11.dp, vertical = 7.dp)
                  ) {
                    Text("Reject", color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
            Spacer(modifier = Modifier.height(8.dp))
          }
        }

        // 9. LIVE ROOM BOTTOM CONTROLS
        // Order:
        // Live Owner: Call, Emoji, Mute, Gift, Settings
        // Normal Viewer: Call, Emoji, Mute, Gift (Settings MUST NOT BE VISIBLE)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 520.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(
              Brush.linearGradient(
                listOf(Color(0xF50F082D), Color(0xF01E0841))
              )
            )
            .border(1.dp, Color(0xA6BE5AFF), RoundedCornerShape(26.dp))
            .shadow(25.dp, RoundedCornerShape(26.dp), ambientColor = Color(0x73A03CFF), spotColor = Color(0x73A03CFF))
            .padding(horizontal = 10.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // 1. Call: In place of old Message button
          val isInCall = activeCallGuest?.userId == activeUserId
          val isCalling = isCallRequestPending

          LiveControlBarButton(
            iconEmoji = "📞",
            label = when {
              isInCall -> "In Call"
              isCalling -> "Calling"
              else -> "Call"
            },
            borderColor = Color(0x593CB4FF),
            isActive = isInCall || isCalling,
            onClick = {
              if (isOwner) {
                if (incomingCallRequest != null) {
                  Toast.makeText(context, "Incoming call from ${incomingCallRequest?.username}", Toast.LENGTH_SHORT).show()
                } else if (activeCallGuest != null) {
                  Toast.makeText(context, "In call with ${activeCallGuest?.username}", Toast.LENGTH_SHORT).show()
                } else {
                  Toast.makeText(context, "No active incoming calls", Toast.LENGTH_SHORT).show()
                }
              } else {
                if (isInCall) {
                  activeCallGuest = null
                  currentRoom = currentRoom.copy(activeCallGuest = null)
                  LiveRoomStorage.saveOrUpdateRoom(context, currentRoom)
                  Toast.makeText(context, "You left the call.", Toast.LENGTH_SHORT).show()
                } else if (isCalling) {
                  isCallRequestPending = false
                  incomingCallRequest = null
                  currentRoom = currentRoom.copy(incomingCallRequester = null)
                  LiveRoomStorage.saveOrUpdateRoom(context, currentRoom)
                  Toast.makeText(context, "Call request cancelled.", Toast.LENGTH_SHORT).show()
                } else {
                  if (!allowCallRequests) {
                    Toast.makeText(context, "Call requests are disabled by the host.", Toast.LENGTH_SHORT).show()
                  } else {
                    isCallRequestPending = true
                    val req = ViewerThumbnail(
                      userId = activeUserId,
                      username = activeUserProfile.username,
                      avatarUri = activeUserProfile.profileImageUri,
                      avatarRes = R.drawable.img_streamer_girl,
                      level = activeUserProfile.userLevel
                    )
                    incomingCallRequest = req
                    currentRoom = currentRoom.copy(incomingCallRequester = req)
                    LiveRoomStorage.saveOrUpdateRoom(context, currentRoom)
                    Toast.makeText(context, "Call request sent to live owner.", Toast.LENGTH_SHORT).show()
                  }
                }
              }
            },
            modifier = Modifier.weight(1f)
          )

          // 2. Emoji
          LiveControlBarButton(
            iconEmoji = "😊",
            label = "Emoji",
            borderColor = Color(0x59FFBE3C),
            isActive = showEmojiPanel,
            onClick = { showEmojiPanel = !showEmojiPanel },
            modifier = Modifier.weight(1f)
          )

          // 3. Mute / Unmute
          LiveControlBarButton(
            iconEmoji = if (isMicMuted) "🔇" else "🎙️",
            label = if (isMicMuted) "Unmute" else "Mute",
            borderColor = Color(0x5946DCBE),
            isActive = !isMicMuted,
            onClick = {
              val willMute = !isMicMuted
              isMicMuted = willMute
              val hasMic = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
              if (!hasMic && !willMute) {
                micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
              } else if (willMute) {
                Toast.makeText(context, "Microphone Muted", Toast.LENGTH_SHORT).show()
              } else {
                Toast.makeText(context, "Microphone Active & Streaming", Toast.LENGTH_SHORT).show()
              }
            },
            modifier = Modifier.weight(1f)
          )

          // 4. Gift
          LiveControlBarButton(
            iconEmoji = "🎁",
            label = "Gift",
            borderColor = Color(0x66FF46BE),
            onClick = {
              if (allowGifts) {
                showGiftDialog = true
              } else {
                Toast.makeText(context, "Gift sending is disabled in this room.", Toast.LENGTH_SHORT).show()
              }
            },
            modifier = Modifier.weight(1f)
          )

          // 5. Settings: ONLY LIVE OWNER
          if (isOwner) {
            LiveControlBarButton(
              iconEmoji = "⚙️",
              label = "Settings",
              borderColor = Color(0x665096FF),
              onClick = { showHostSettingsDialog = true },
              modifier = Modifier.weight(1f)
            )
          }
        }
      }

      // 8B. RIGHT-SIDE FLOATING GAMES BUTTON ON LIVE BOARD (Matches Party Room Games button)
      Box(
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(end = 12.dp, bottom = responsiveBottomPadding + 136.dp)
          .testTag("live_board_games_button_container")
      ) {
        FloatingPartyGamesButton(
          onClick = { showGameDialog = true }
        )
      }

      // -----------------------------------------------------------------------
      // MINI-GAMES DIALOG (Reused from Party Room)
      // -----------------------------------------------------------------------
      if (showGameDialog) {
        PartyGameDialog(
          onDismiss = { showGameDialog = false }
        )
      }

      // -----------------------------------------------------------------------
      // EXIT CONFIRMATION DIALOG (Keep vs Exit)
      // -----------------------------------------------------------------------
      if (showExitConfirmDialog) {
        RoomKeepExitDialog(
          onKeep = {
            showExitConfirmDialog = false
            Toast.makeText(context, "Live room kept running in background", Toast.LENGTH_SHORT).show()
            onEscapeRoom()
          },
          onExit = {
            showExitConfirmDialog = false
            if (isHost) {
              LiveRoomStorage.removeRoom(context, currentRoom.roomId)
            }
            Toast.makeText(context, "Left live room", Toast.LENGTH_SHORT).show()
            onExitRoom()
          },
          onDismiss = { showExitConfirmDialog = false }
        )
      }

      // -----------------------------------------------------------------------
      // REAL GIFT BOTTOM SHEET (Half-Screen, Real Board Users Only)
      // -----------------------------------------------------------------------
      if (showGiftDialog) {
        val realLiveRecipients = remember(currentRoom, roomViewers, activeUserId) {
          val list = mutableListOf<RealGiftRecipient>()
          // 1. Host
          if (currentRoom.hostUserId.isNotBlank()) {
            list.add(
              RealGiftRecipient(
                userId = currentRoom.hostUserId,
                username = currentRoom.hostName.ifBlank { "Host" },
                avatarUri = currentRoom.hostAvatarUri,
                isHost = true
              )
            )
          }
          // 2. Active call guest if any
          currentRoom.activeCallGuest?.let { guest ->
            if (guest.userId != activeUserId && guest.userId != currentRoom.hostUserId) {
              list.add(
                RealGiftRecipient(
                  userId = guest.userId,
                  username = guest.username,
                  avatarUri = guest.avatarUri,
                  isHost = false
                )
              )
            }
          }
          // 3. Real viewers in the room (excluding host and active user)
          roomViewers.filter { it.userId.isNotBlank() && it.userId != activeUserId && it.userId != currentRoom.hostUserId }
            .forEach { v ->
              if (list.none { it.userId == v.userId }) {
                list.add(
                  RealGiftRecipient(
                    userId = v.userId,
                    username = v.username,
                    avatarUri = v.avatarUri,
                    isHost = false
                  )
                )
              }
            }
          list
        }

        RealBoardGiftBottomSheet(
          realRecipients = realLiveRecipients,
          currentUserBalance = userCoins.toLong(),
          onDismiss = { showGiftDialog = false },
          onSendGift = { recipient, gift, quantity, totalCost ->
            val newBal = UserProfileStorage.addCoins(context, -totalCost, activeUserId)
            userCoins = newBal
            roomCoinsScore += totalCost.toInt()
            currentRoom = currentRoom.copy(heartScore = roomCoinsScore)
            LiveRoomStorage.saveOrUpdateRoom(context, currentRoom)

            // Trigger floating banner
            activeGiftBanner = "${activeUserProfile.username} sent ${gift.name} x$quantity to ${recipient.username}!"
            activeGiftEmoji = gift.emoji
            showGiftDialog = false

            // Add gift event to chat
            chatMessages.add(
              LiveChatMessageItem(
                id = "gift_${System.currentTimeMillis()}",
                senderId = activeUserId,
                senderName = activeUserProfile.username,
                senderLevel = activeUserProfile.userLevel,
                senderAvatarUri = activeUserProfile.profileImageUri,
                senderAvatarRes = R.drawable.img_streamer_girl,
                message = "Sent ${gift.name} ${gift.emoji} x$quantity to ${recipient.username}"
              )
            )

            // Clear banner after 3 seconds
            coroutineScope.launch {
              delay(3000L)
              activeGiftBanner = null
              activeGiftEmoji = null
            }
          }
        )
      }

      // -----------------------------------------------------------------------
      // ROOM OWNER SETTINGS DIALOG (Only available to Host/Creator)
      // -----------------------------------------------------------------------
      if (showHostSettingsDialog && isOwner) {
        Dialog(
          onDismissRequest = { showHostSettingsDialog = false },
          properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
          )
        ) {
          CenteredModalDialogContainer(onDismiss = { showHostSettingsDialog = false }) {
            Surface(
              modifier = Modifier
                .fillMaxWidth(0.92f)
                .heightIn(max = 560.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, Brush.horizontalGradient(listOf(NeonCyan, NeonPink)), RoundedCornerShape(24.dp)),
              color = Color(0xFF140029)
            ) {
            LazyColumn(
              modifier = Modifier.padding(18.dp)
            ) {
              item {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      Icons.Filled.Security,
                      contentDescription = null,
                      tint = NeonCyan
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      "HOST & ROOM POWERS",
                      color = Color.White,
                      fontWeight = FontWeight.Black,
                      fontSize = 16.sp
                    )
                  }
                  IconButton(onClick = { showHostSettingsDialog = false }) {
                    Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // SECTION 1: CHANGE ROOM NAME
                var editTitleText by remember { mutableStateOf(roomTitle) }
                OutlinedTextField(
                  value = editTitleText,
                  onValueChange = { editTitleText = it },
                  label = { Text("Live Room Name", color = NeonCyan) },
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = Color(0xFF6B00B6)
                  ),
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                  onClick = {
                    roomTitle = editTitleText.trim().ifBlank { "Live Stream" }
                    currentRoom = currentRoom.copy(roomTitle = roomTitle)
                    LiveRoomStorage.saveOrUpdateRoom(context, currentRoom)
                    Toast.makeText(context, "Room name updated!", Toast.LENGTH_SHORT).show()
                  },
                  modifier = Modifier.fillMaxWidth(),
                  colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                  shape = RoundedCornerShape(10.dp)
                ) {
                  Text("Save Room Name", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                HorizontalDivider(color = Color(0xFF2C0054), modifier = Modifier.padding(vertical = 12.dp))

                // SECTION 2: ROOM PERMISSIONS
                Text("Room Permissions", color = NeonGold, fontWeight = FontWeight.Black, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))

                // 2a. Allow Comments
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text("Allow Comments", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                    Text("Enable audience real-time chat messages", color = Color.White.copy(alpha = 0.6f), fontSize = 10.5.sp)
                  }
                  Switch(
                    checked = allowComments,
                    onCheckedChange = { allowComments = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = NeonPink)
                  )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // 2b. Allow Gifts
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text("Allow Gifts", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                    Text("Accept virtual gifts and tips from viewers", color = Color.White.copy(alpha = 0.6f), fontSize = 10.5.sp)
                  }
                  Switch(
                    checked = allowGifts,
                    onCheckedChange = { allowGifts = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = NeonGold)
                  )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // 2c. Allow Call Requests
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text("Allow Audio Calls", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                    Text("Viewers can send call requests to join live", color = Color.White.copy(alpha = 0.6f), fontSize = 10.5.sp)
                  }
                  Switch(
                    checked = allowCallRequests,
                    onCheckedChange = { allowCallRequests = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = NeonCyan)
                  )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // 2d. Lock Room (Private)
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text("Lock Room (Private)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                    Text("Prevent unauthorized users from entering", color = Color.White.copy(alpha = 0.6f), fontSize = 10.5.sp)
                  }
                  Switch(
                    checked = isRoomLocked,
                    onCheckedChange = {
                      isRoomLocked = it
                      currentRoom = currentRoom.copy(isLocked = isRoomLocked)
                      LiveRoomStorage.saveOrUpdateRoom(context, currentRoom)
                    },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFFFF1744))
                  )
                }

                HorizontalDivider(color = Color(0xFF2C0054), modifier = Modifier.padding(vertical = 12.dp))

                // SECTION 3: APPROVED GUEST & CALL POWERS
                Text("Approved Guest & Call Permissions", color = NeonGold, fontWeight = FontWeight.Black, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text("Auto-Approve VIP Callers", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                    Text("Automatically connect verified/VIP guests", color = Color.White.copy(alpha = 0.6f), fontSize = 10.5.sp)
                  }
                  Switch(
                    checked = autoApproveVipCalls,
                    onCheckedChange = { autoApproveVipCalls = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF00E676))
                  )
                }

                if (activeCallGuest != null) {
                  Spacer(modifier = Modifier.height(6.dp))
                  Button(
                    onClick = {
                      activeCallGuest = null
                      currentRoom = currentRoom.copy(activeCallGuest = null)
                      LiveRoomStorage.saveOrUpdateRoom(context, currentRoom)
                      Toast.makeText(context, "Active guest disconnected.", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF1744)),
                    shape = RoundedCornerShape(10.dp)
                  ) {
                    Text("Disconnect Active Guest (${activeCallGuest?.username})", color = Color.White, fontWeight = FontWeight.Bold)
                  }
                }

                HorizontalDivider(color = Color(0xFF2C0054), modifier = Modifier.padding(vertical = 12.dp))

                // SECTION 4: USER MANAGEMENT (Viewers List + Moderation)
                Text("Manage Users in Room", color = NeonGold, fontWeight = FontWeight.Black, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
              }

              // List current room viewers
              items(roomViewers.filter { it.userId != activeUserId }) { viewer ->
                val isTargetAdmin = adminList.contains(viewer.userId)
                val isTargetMuted = mutedList.contains(viewer.userId)

                Card(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                  colors = CardDefaults.cardColors(containerColor = Color(0xFF1E0038)),
                  border = BorderStroke(0.8.dp, Color(0xFF4A0082))
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      AvatarFrameOverlay(
                        size = 30.dp,
                        profileImageUri = viewer.avatarUri,
                        avatarRes = viewer.avatarRes
                      )
                      Spacer(modifier = Modifier.width(6.dp))
                      Column {
                        Text(viewer.username, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("ID: ${viewer.userId}", color = NeonCyan, fontSize = 9.sp)
                      }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                      // Admin toggle
                      Button(
                        onClick = {
                          if (isTargetAdmin) {
                            adminList.remove(viewer.userId)
                            Toast.makeText(context, "Revoked admin from ${viewer.username}", Toast.LENGTH_SHORT).show()
                          } else {
                            adminList.add(viewer.userId)
                            Toast.makeText(context, "Granted admin to ${viewer.username}", Toast.LENGTH_SHORT).show()
                          }
                          currentRoom = currentRoom.copy(adminUserIds = adminList)
                          LiveRoomStorage.saveOrUpdateRoom(context, currentRoom)
                        },
                        colors = ButtonDefaults.buttonColors(
                          containerColor = if (isTargetAdmin) Color(0xFF880E4F) else Color(0xFF00B0FF)
                        ),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                      ) {
                        Text(if (isTargetAdmin) "Revoke" else "Admin", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                      }

                      // Mute toggle
                      Button(
                        onClick = {
                          if (isTargetMuted) {
                            mutedList.remove(viewer.userId)
                            Toast.makeText(context, "Unmuted ${viewer.username}", Toast.LENGTH_SHORT).show()
                          } else {
                            mutedList.add(viewer.userId)
                            Toast.makeText(context, "Muted ${viewer.username}", Toast.LENGTH_SHORT).show()
                          }
                          currentRoom = currentRoom.copy(mutedUserIds = mutedList)
                          LiveRoomStorage.saveOrUpdateRoom(context, currentRoom)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF424242)),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                      ) {
                        Text(if (isTargetMuted) "Unmute" else "Mute", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                      }

                      // Kick
                      Button(
                        onClick = {
                          kickedList.add(viewer.userId)
                          roomViewers.removeAll { it.userId == viewer.userId }
                          currentRoom = currentRoom.copy(kickedUserIds = kickedList)
                          LiveRoomStorage.saveOrUpdateRoom(context, currentRoom)
                          Toast.makeText(context, "${viewer.username} was kicked.", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD50000)),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                      ) {
                        Text("Kick", fontSize = 9.sp, fontWeight = FontWeight.Black)
                      }
                    }
                  }
                }
              }

              item {
                HorizontalDivider(color = Color(0xFF2C0054), modifier = Modifier.padding(vertical = 12.dp))

                // SECTION 5: END LIVE STREAM BUTTON
                Button(
                  onClick = {
                    showHostSettingsDialog = false
                    LiveRoomStorage.removeRoom(context, currentRoom.roomId)
                    onExitRoom()
                  },
                  modifier = Modifier.fillMaxWidth(),
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD50000)),
                  shape = RoundedCornerShape(12.dp)
                ) {
                  Text("End Live Stream", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                }
              }
            }
          }
          }
        }
      }

      // -----------------------------------------------------------------------
      // USER PROFILE PREVIEW DIALOG (Follow, Block, Moderation)
      // -----------------------------------------------------------------------
      selectedUserForProfile?.let { (uId, uName, uAvatarUri) ->
        val userFrame = StoreCenterStorage.getEquippedFrame(context, uId)
        val profileData = UserProfileStorage.getProfileForUser(context, uId)
        val isTargetAdmin = adminList.contains(uId)
        val isTargetMuted = mutedList.contains(uId)

        Dialog(
          onDismissRequest = { selectedUserForProfile = null },
          properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
          )
        ) {
          CenteredModalDialogContainer(onDismiss = { selectedUserForProfile = null }) {
            Surface(
              modifier = Modifier
                .fillMaxWidth(0.88f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, Brush.horizontalGradient(listOf(NeonPink, NeonGold)), RoundedCornerShape(24.dp)),
              color = Color(0xFF140026)
            ) {
            Column(
              modifier = Modifier.padding(20.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              AvatarFrameOverlay(
                size = 64.dp,
                frame = userFrame,
                profileImageUri = uAvatarUri,
                avatarRes = R.drawable.img_streamer_girl
              )

              Spacer(modifier = Modifier.height(10.dp))

              Text(
                text = uName,
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black
              )

              Text(
                text = "Unique ID: $uId",
                color = NeonCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )

              Row(
                modifier = Modifier.padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Lv.${profileData.userLevel}",
                  color = NeonGold,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "${profileData.country} ${profileData.countryFlag}",
                  color = Color.White.copy(alpha = 0.8f),
                  fontSize = 11.sp
                )
              }

              Spacer(modifier = Modifier.height(12.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Button(
                  onClick = {
                    Toast.makeText(context, "Followed $uName!", Toast.LENGTH_SHORT).show()
                  },
                  modifier = Modifier.weight(1f),
                  colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                  shape = RoundedCornerShape(10.dp)
                ) {
                  Text("Follow", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Button(
                  onClick = {
                    Toast.makeText(context, "$uName Blocked.", Toast.LENGTH_SHORT).show()
                    selectedUserForProfile = null
                  },
                  modifier = Modifier.weight(1f),
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF380020)),
                  shape = RoundedCornerShape(10.dp)
                ) {
                  Text("Block", color = Color(0xFFFF80AB), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              Button(
                onClick = {
                  selectedUserForProfile = null
                  onOpenUserProfile(uId)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF220042)),
                shape = RoundedCornerShape(10.dp)
              ) {
                Text("View Full Profile", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
              }

              // MODERATION POWERS (if viewer is Owner or Admin)
              if ((isOwner || isAdmin) && uId != activeUserId && uId != currentRoom.hostUserId) {
                HorizontalDivider(color = Color(0xFF2C0054), modifier = Modifier.padding(vertical = 10.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  // Admin toggle
                  if (isOwner) {
                    Button(
                      onClick = {
                        if (isTargetAdmin) {
                          adminList.remove(uId)
                          Toast.makeText(context, "Removed admin from $uName", Toast.LENGTH_SHORT).show()
                        } else {
                          adminList.add(uId)
                          Toast.makeText(context, "Granted admin to $uName", Toast.LENGTH_SHORT).show()
                        }
                        currentRoom = currentRoom.copy(adminUserIds = adminList)
                        LiveRoomStorage.saveOrUpdateRoom(context, currentRoom)
                      },
                      modifier = Modifier.weight(1f),
                      colors = ButtonDefaults.buttonColors(
                        containerColor = if (isTargetAdmin) Color(0xFF880E4F) else Color(0xFF00B0FF)
                      ),
                      shape = RoundedCornerShape(8.dp)
                    ) {
                      Text(if (isTargetAdmin) "Revoke" else "Admin", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                  }

                  // Mute toggle
                  Button(
                    onClick = {
                      if (isTargetMuted) {
                        mutedList.remove(uId)
                        Toast.makeText(context, "Unmuted $uName", Toast.LENGTH_SHORT).show()
                      } else {
                        mutedList.add(uId)
                        Toast.makeText(context, "Muted $uName", Toast.LENGTH_SHORT).show()
                      }
                      currentRoom = currentRoom.copy(mutedUserIds = mutedList)
                      LiveRoomStorage.saveOrUpdateRoom(context, currentRoom)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3E2723)),
                    shape = RoundedCornerShape(8.dp)
                  ) {
                    Text(if (isTargetMuted) "Unmute" else "Mute", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                  }

                  // Kick
                  Button(
                    onClick = {
                      kickedList.add(uId)
                      roomViewers.removeAll { it.userId == uId }
                      currentRoom = currentRoom.copy(kickedUserIds = kickedList)
                      LiveRoomStorage.saveOrUpdateRoom(context, currentRoom)
                      selectedUserForProfile = null
                      Toast.makeText(context, "$uName kicked.", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD50000)),
                    shape = RoundedCornerShape(8.dp)
                  ) {
                    Text("Kick", fontSize = 10.sp, fontWeight = FontWeight.Black)
                  }
                }
              }
            }
          }
          }
        }
      }
  }
}

/**
 * Animated sound waves equalizer bars.
 * Flanks caller avatar to create live audio stream effect matching reference HTML & image.
 */
@Composable
fun AudioEqualizerWaves(
  modifier: Modifier = Modifier,
  color: Color = NeonCyan
) {
  val infiniteTransition = rememberInfiniteTransition(label = "audio_waves")
  val h1 by infiniteTransition.animateFloat(
    initialValue = 6f,
    targetValue = 22f,
    animationSpec = infiniteRepeatable(
      animation = tween(400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "h1"
  )
  val h2 by infiniteTransition.animateFloat(
    initialValue = 18f,
    targetValue = 7f,
    animationSpec = infiniteRepeatable(
      animation = tween(340, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "h2"
  )
  val h3 by infiniteTransition.animateFloat(
    initialValue = 10f,
    targetValue = 26f,
    animationSpec = infiniteRepeatable(
      animation = tween(460, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "h3"
  )

  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(2.5.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .width(3.dp)
        .height(h1.dp)
        .clip(RoundedCornerShape(2.dp))
        .background(color)
    )
    Box(
      modifier = Modifier
        .width(3.dp)
        .height(h3.dp)
        .clip(RoundedCornerShape(2.dp))
        .background(color)
    )
    Box(
      modifier = Modifier
        .width(3.dp)
        .height(h2.dp)
        .clip(RoundedCornerShape(2.dp))
        .background(color)
    )
  }
}

@Composable
private fun LiveControlBarButton(
  iconEmoji: String,
  label: String,
  borderColor: Color,
  isActive: Boolean = false,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  var isPressed by remember { mutableStateOf(false) }
  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.92f else 1.0f,
    label = "control_press_scale"
  )

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
    modifier = modifier
      .graphicsLayer {
        scaleX = scale
        scaleY = scale
      }
      .height(58.dp)
      .clip(RoundedCornerShape(18.dp))
      .background(
        if (isActive) Brush.verticalGradient(listOf(Color(0xFF6B18B5), Color(0xFF2E0D5A)))
        else Brush.verticalGradient(listOf(Color(0xF2321E5A), Color(0xFA140F32)))
      )
      .border(1.dp, borderColor, RoundedCornerShape(18.dp))
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = {
          isPressed = true
          onClick()
        }
      )
      .padding(vertical = 4.dp, horizontal = 2.dp)
  ) {
    LaunchedEffect(isPressed) {
      if (isPressed) {
        delay(120L)
        isPressed = false
      }
    }

    Box(
      modifier = Modifier
        .size(30.dp)
        .clip(CircleShape)
        .background(
          Brush.radialGradient(
            listOf(Color(0x33FFFFFF), Color(0x0AFFFFFF))
          )
        )
        .border(1.dp, Color(0x40AA50FF), CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Text(iconEmoji, fontSize = 16.sp)
    }

    Spacer(modifier = Modifier.height(2.dp))

    Text(
      text = label,
      color = Color.White,
      fontSize = 10.5.sp,
      fontWeight = FontWeight.SemiBold,
      letterSpacing = 0.2.sp,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
  }
}
