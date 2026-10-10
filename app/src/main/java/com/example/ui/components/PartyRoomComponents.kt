package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.aistudio.neonparty.nxvqlr.R
import com.example.ui.storage.SocialAgencyStorage

/**
 * Granular permissions assigned to an Admin by the Room Owner.
 */
data class AdminPermissions(
  val canMute: Boolean = true,
  val canUnmute: Boolean = true,
  val canManageSeats: Boolean = true,
  val canKickUsers: Boolean = false
)

/**
 * Representation of a room participant
 */
data class PartyParticipant(
  val userId: String,
  val username: String,
  val avatarUri: String? = null,
  val avatarRes: Int? = null,
  val frameId: String? = null,
  val frameUrl: String? = null,
  val level: Int = 1,
  val gender: String = "Male",
  val country: String = "Bangladesh",
  val countryFlag: String = "🇧🇩",
  val isHost: Boolean = false,
  val isAdmin: Boolean = false,
  val adminPermissions: AdminPermissions = AdminPermissions(),
  val isMutedOnSeat: Boolean = false,
  val seatNumber: Int? = null
)

/**
 * Custom Upward Arrow with a horizontal base bar beneath it,
 * perfectly matching the "Keep" button icon in the user's reference design.
 */
@Composable
fun KeepUploadIcon(
  modifier: Modifier = Modifier,
  tint: Color = Color.White
) {
  Canvas(modifier = modifier) {
    val w = size.width
    val h = size.height
    val strokeWidth = 3.2.dp.toPx()

    // Base horizontal bar at bottom
    drawLine(
      color = tint,
      start = Offset(w * 0.20f, h * 0.80f),
      end = Offset(w * 0.80f, h * 0.80f),
      strokeWidth = strokeWidth,
      cap = StrokeCap.Round
    )

    // Vertical arrow stem
    drawLine(
      color = tint,
      start = Offset(w * 0.50f, h * 0.68f),
      end = Offset(w * 0.50f, h * 0.20f),
      strokeWidth = strokeWidth,
      cap = StrokeCap.Round
    )

    // Left wing
    drawLine(
      color = tint,
      start = Offset(w * 0.25f, h * 0.44f),
      end = Offset(w * 0.50f, h * 0.18f),
      strokeWidth = strokeWidth,
      cap = StrokeCap.Round
    )

    // Right wing
    drawLine(
      color = tint,
      start = Offset(w * 0.75f, h * 0.44f),
      end = Offset(w * 0.50f, h * 0.18f),
      strokeWidth = strokeWidth,
      cap = StrokeCap.Round
    )
  }
}

/**
 * High-tech sci-fi glassmorphism button matching the exact styling
 * of the user's reference image for "Keep" and "Exit".
 */
@Composable
fun SciFiKeepExitButton(
  text: String,
  isKeep: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.94f else 1f,
    label = "scifi_btn_scale"
  )

  Box(
    modifier = modifier
      .scale(scale)
      .width(280.dp)
      .height(68.dp)
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
      )
  ) {
    // 1. Right Glass Body (Pill Container)
    Box(
      modifier = Modifier
        .align(Alignment.CenterEnd)
        .fillMaxWidth(0.88f)
        .height(58.dp)
        .clip(RoundedCornerShape(18.dp))
        .background(
          Brush.horizontalGradient(
            listOf(
              Color(0xF21A0033),
              Color(0xF2100428),
              Color(0xF2090B26)
            )
          )
        )
        .border(
          width = 1.8.dp,
          brush = Brush.linearGradient(
            listOf(
              Color(0xFF00E5FF),
              Color(0xFF00B0FF),
              Color(0xFF9C27B0),
              Color(0xFFFF007F)
            )
          ),
          shape = RoundedCornerShape(18.dp)
        )
    ) {
      // Top right 3 slanted glowing neon slashes: ///
      Canvas(
        modifier = Modifier
          .align(Alignment.TopEnd)
          .padding(top = 9.dp, end = 26.dp)
          .size(24.dp, 10.dp)
      ) {
        val stroke = 2.4.dp.toPx()
        val colors = listOf(Color(0xFFFF4081), Color(0xFFBA68C8), Color(0xFF00E5FF))
        for (i in 0..2) {
          val x = i * 7.dp.toPx()
          drawLine(
            color = colors[i],
            start = Offset(x + 4.dp.toPx(), 0f),
            end = Offset(x, 8.dp.toPx()),
            strokeWidth = stroke,
            cap = StrokeCap.Round
          )
        }
      }

      // Glowing pink accent LED dot on right
      Box(
        modifier = Modifier
          .align(Alignment.CenterEnd)
          .padding(end = 10.dp)
          .size(8.dp)
          .clip(CircleShape)
          .background(Color(0xFFFF4081))
          .border(1.dp, Color(0xFFFF80AB), CircleShape)
      )

      // Centered Text Label ("Keep" or "Exit")
      Text(
        text = text,
        color = Color.White,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp,
        modifier = Modifier
          .align(Alignment.Center)
          .padding(start = 16.dp)
      )
    }

    // 2. Left Rotated Diamond Badge (Rhombus with Rounded Corners)
    Box(
      modifier = Modifier
        .align(Alignment.CenterStart)
        .size(64.dp),
      contentAlignment = Alignment.Center
    ) {
      // Rotated diamond card
      Box(
        modifier = Modifier
          .size(48.dp)
          .rotate(45f)
          .shadow(
            elevation = 12.dp,
            shape = RoundedCornerShape(14.dp),
            spotColor = if (isKeep) Color(0xFF00E5FF) else Color(0xFFFF007F)
          )
          .clip(RoundedCornerShape(14.dp))
          .background(
            Brush.linearGradient(
              listOf(
                Color(0xFFD500F9),
                Color(0xFF304FFE),
                Color(0xFF00E5FF)
              )
            )
          )
          .border(
            width = 1.8.dp,
            brush = Brush.sweepGradient(
              listOf(
                Color(0xFFFF007F),
                Color(0xFF00E5FF),
                Color(0xFF00E5FF),
                Color(0xFFFF007F),
                Color(0xFFFF007F)
              )
            ),
            shape = RoundedCornerShape(14.dp)
          )
      )

      // Upright Icon inside (NOT rotated)
      Box(
        modifier = Modifier.size(64.dp),
        contentAlignment = Alignment.Center
      ) {
        if (isKeep) {
          KeepUploadIcon(
            modifier = Modifier.size(26.dp),
            tint = Color.White
          )
        } else {
          Icon(
            imageVector = Icons.Filled.PowerSettingsNew,
            contentDescription = "Exit",
            tint = Color.White,
            modifier = Modifier.size(28.dp)
          )
        }
      }
    }
  }
}

/**
 * Top-Level Exit Confirmation Dialog used by BOTH Audio Party Rooms and Video Live Rooms.
 * Displays the two futuristic neon buttons: "Keep" and "Exit", exactly matching the reference design.
 */
@Composable
fun RoomKeepExitDialog(
  onKeep: () -> Unit,
  onExit: () -> Unit,
  onDismiss: () -> Unit = onKeep
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
          .padding(horizontal = 24.dp)
          .clip(RoundedCornerShape(26.dp))
          .background(
            Brush.verticalGradient(
              listOf(
                Color(0xF018022E),
                Color(0xF00D0422),
                Color(0xF0070116)
              )
            )
          )
          .border(
            width = 1.2.dp,
            brush = Brush.verticalGradient(
              listOf(
                Color(0xFF00E5FF).copy(alpha = 0.5f),
                Color(0xFFFF007F).copy(alpha = 0.5f)
              )
            ),
            shape = RoundedCornerShape(26.dp)
          )
          .padding(horizontal = 20.dp, vertical = 26.dp)
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
          // 1. Keep Button (Top)
          SciFiKeepExitButton(
            text = "Keep",
            isKeep = true,
            onClick = onKeep
          )

          // 2. Exit Button (Bottom)
          SciFiKeepExitButton(
            text = "Exit",
            isKeep = false,
            onClick = onExit
          )
        }
      }
    }
  }
}

/**
 * 1. TOP-RIGHT CLOSE CONFIRMATION DIALOG
 * Retained for backwards compatibility across audio and video rooms.
 */
@Composable
fun ExitRoomConfirmDialog(
  onStay: () -> Unit,
  onExit: () -> Unit
) {
  RoomKeepExitDialog(
    onKeep = onStay,
    onExit = onExit,
    onDismiss = onStay
  )
}

/**
 * 2. PREMIUM LARGER GLOWING GIFT BOX
 * Features:
 * - Larger touch area (54.dp x 54.dp)
 * - Premium neon/glass visual language
 * - Hot-pink and golden glowing gradient
 * - Pulsing jewel animation
 * - Smooth tap animation & click handler
 */
@Composable
fun NeonGiftBoxButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.88f else 1f,
    animationSpec = tween(120),
    label = "gift_press"
  )

  val infiniteTransition = rememberInfiniteTransition(label = "gift_pulse")
  val borderPulse by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.08f,
    animationSpec = infiniteRepeatable(
      animation = tween(900, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "gift_glow"
  )

  Box(
    modifier = modifier
      .scale(scale)
      .size(54.dp)
      .shadow(elevation = 10.dp, shape = RoundedCornerShape(18.dp), spotColor = Color(0xFFFF007F))
      .clip(RoundedCornerShape(18.dp))
      .background(
        Brush.radialGradient(
          listOf(Color(0xFFFF007F), Color(0xFFC2185B), Color(0xFF4A0033))
        )
      )
      .border(
        width = 2.dp,
        brush = Brush.sweepGradient(
          listOf(
            Color(0xFFFFD700),
            Color(0xFFFF007F),
            Color(0xFFE040FB),
            Color(0xFFFFD700)
          )
        ),
        shape = RoundedCornerShape(18.dp)
      )
      .clickable(interactionSource = interactionSource, indication = null) { onClick() }
      .testTag("party_gift_box"),
    contentAlignment = Alignment.Center
  ) {
    // Inner glass shimmer
    Box(
      modifier = Modifier
        .size(44.dp)
        .scale(borderPulse)
        .clip(RoundedCornerShape(14.dp))
        .background(Color(0x22FFFFFF))
    )

    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Text(text = "🎁", fontSize = 26.sp)
      Text(
        text = "GIFT",
        color = Color(0xFFFFD700),
        fontSize = 8.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 0.5.sp
      )
    }
  }
}

/**
 * 3. DYNAMIC USER PROFILE CARD DIALOG (With Follow, Block, and Moderation)
 * Opened when another user's profile picture, username/ID, or seat is tapped.
 */
@Composable
fun PartyUserProfileDialog(
  targetUser: PartyParticipant,
  currentUserId: String,
  isViewerOwner: Boolean,
  viewerAdminPermissions: AdminPermissions?,
  onDismiss: () -> Unit,
  onSendMessage: ((PartyParticipant) -> Unit)? = null,
  onMuteParticipant: ((PartyParticipant, Boolean) -> Unit)? = null,
  onRemoveFromSeat: ((PartyParticipant) -> Unit)? = null,
  onKickParticipant: ((PartyParticipant) -> Unit)? = null,
  onToggleAdmin: ((PartyParticipant) -> Unit)? = null,
  onUpdateAdminPermissions: ((PartyParticipant, AdminPermissions) -> Unit)? = null
) {
  val context = LocalContext.current
  var isFollowing by remember {
    mutableStateOf(SocialAgencyStorage.isFollowing(context, currentUserId, targetUser.userId))
  }
  var isBlocked by remember {
    mutableStateOf(SocialAgencyStorage.isBlocked(context, currentUserId, targetUser.userId))
  }
  var showAdminPermsDialog by remember { mutableStateOf(false) }

  val canMute = isViewerOwner || (viewerAdminPermissions?.canMute == true)
  val canManageSeats = isViewerOwner || (viewerAdminPermissions?.canManageSeats == true)
  val canKick = isViewerOwner || (viewerAdminPermissions?.canKickUsers == true)

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
            Brush.verticalGradient(listOf(Color(0xFF260042), Color(0xFF110024)))
          )
          .border(
            1.8.dp,
            Brush.horizontalGradient(listOf(Color(0xFFFF007F), Color(0xFF00E5FF))),
            RoundedCornerShape(24.dp)
          )
          .padding(18.dp)
      ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        // Top row with close
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Brush.horizontalGradient(listOf(Color(0xFF6200EA), Color(0xFF9C27B0))))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "Lv.${targetUser.level}",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black
              )
            }
            if (targetUser.isHost) {
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(Color(0xFFFF007F))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(10.dp)
                  )
                  Spacer(modifier = Modifier.width(3.dp))
                  Text(
                    text = "Host",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            } else if (targetUser.isAdmin) {
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(Color(0xFF00B0FF))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Filled.Security,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(11.dp)
                  )
                  Spacer(modifier = Modifier.width(3.dp))
                  Text(
                    text = "Admin",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
          }

          IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(18.dp))
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Profile Picture with Active Real Frame Overlay
        AvatarFrameOverlay(
          size = 78.dp,
          frameId = targetUser.frameId,
          frameUrl = targetUser.frameUrl,
          profileImageUri = targetUser.avatarUri,
          avatarRes = targetUser.avatarRes ?: 0
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Username
        Text(
          text = targetUser.username,
          color = Color.White,
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        // [ FRAME ] REAL USER ID [ FRAME ]
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0x551E0836))
            .border(
              width = 1.4.dp,
              brush = Brush.horizontalGradient(listOf(Color(0xFF00E5FF), Color(0xFFE040FB))),
              shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 14.dp, vertical = 5.dp)
            .clickable {
              val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
              val clip = android.content.ClipData.newPlainText("User PID", targetUser.userId)
              clipboard?.setPrimaryClip(clip)
              Toast.makeText(context, "PID copied to clipboard", Toast.LENGTH_SHORT).show()
            }
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "PID: ${targetUser.userId}",
              color = Color(0xFF00E5FF),
              fontSize = 12.5.sp,
              fontWeight = FontWeight.Black,
              letterSpacing = 0.5.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // REAL GENDER & COUNTRY PILLS (No fake data!)
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          val isFemale = targetUser.gender.equals("Female", ignoreCase = true)
          // Gender Badge: Male ♂ / Female ♀
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(if (isFemale) Color(0x33E91E63) else Color(0x3300B0FF))
              .border(0.8.dp, if (isFemale) Color(0xFFFF4081) else Color(0xFF00E5FF), RoundedCornerShape(8.dp))
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = if (isFemale) Icons.Filled.Female else Icons.Filled.Male,
                contentDescription = null,
                tint = if (isFemale) Color(0xFFFF4081) else Color(0xFF00E5FF),
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = if (isFemale) "Female" else "Male",
                color = if (isFemale) Color(0xFFFF80AB) else Color(0xFF80D8FF),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          // Country Badge
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0x332E7D32))
              .border(0.8.dp, Color(0xFF69F0AE), RoundedCornerShape(8.dp))
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = targetUser.countryFlag.ifBlank { "🇧🇩" },
                fontSize = 11.sp
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = targetUser.country.ifBlank { "Bangladesh" },
                color = Color(0xFFB9F6CA),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        if (targetUser.seatNumber != null) {
          Spacer(modifier = Modifier.height(8.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0x33FF4081))
              .border(0.8.dp, Color(0xFFFF4081), RoundedCornerShape(8.dp))
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = "Seated on Mic No. ${targetUser.seatNumber}",
              color = Color(0xFFFF80AB),
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // LEAVE SEAT BUTTON (if current user is viewing their own profile while on a seat!)
        if (targetUser.userId == currentUserId && targetUser.seatNumber != null) {
          Button(
            onClick = {
              onRemoveFromSeat?.invoke(targetUser)
              onDismiss()
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("btn_leave_seat"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD81B60)),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ExitToApp,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Leave Seat (সিট থেকে নামুন)",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
          Spacer(modifier = Modifier.height(10.dp))
        }

        // FOLLOW and MESSAGE Buttons (Strictly adhering to Requirement 13)
        if (targetUser.userId != currentUserId) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            // 1. REAL FOLLOW / UNFOLLOW Button
            Button(
              onClick = {
                if (isFollowing) {
                  SocialAgencyStorage.unfollowUser(context, currentUserId, targetUser.userId)
                  isFollowing = false
                  Toast.makeText(context, "Unfollowed ${targetUser.username}", Toast.LENGTH_SHORT).show()
                } else {
                  SocialAgencyStorage.followUser(context, currentUserId, targetUser.userId)
                  isFollowing = true
                  Toast.makeText(context, "Followed ${targetUser.username}!", Toast.LENGTH_SHORT).show()
                }
              },
              modifier = Modifier
                .weight(1f)
                .testTag("btn_profile_follow"),
              colors = ButtonDefaults.buttonColors(
                containerColor = if (isFollowing) Color(0xFF4A148C) else Color(0xFFFF007F)
              ),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(
                imageVector = if (isFollowing) Icons.Filled.Check else Icons.Filled.PersonAdd,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (isFollowing) "Following" else "Follow",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }

            // 2. REAL MESSAGE Button
            Button(
              onClick = {
                onDismiss()
                onSendMessage?.invoke(targetUser)
              },
              modifier = Modifier
                .weight(1f)
                .testTag("btn_profile_message"),
              colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00B0FF)
              ),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.Chat,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Message",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }
        }

        // Room Owner & Admin Moderation Section
        if ((isViewerOwner || viewerAdminPermissions != null) && targetUser.userId != currentUserId && !targetUser.isHost) {
          Spacer(modifier = Modifier.height(16.dp))
          HorizontalDivider(color = Color(0x33FFFFFF))
          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = "ROOM MODERATION",
            color = Color(0xFF00E5FF),
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(8.dp))

          Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Mute / Unmute Participant on Seat
            if (targetUser.seatNumber != null && canMute) {
              Button(
                onClick = {
                  val newMuteState = !targetUser.isMutedOnSeat
                  onMuteParticipant?.invoke(targetUser, newMuteState)
                  onDismiss()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (targetUser.isMutedOnSeat) Color(0xFF00897B) else Color(0xFFD81B60)
                ),
                shape = RoundedCornerShape(10.dp)
              ) {
                Icon(
                  imageVector = if (targetUser.isMutedOnSeat) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeMute,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = if (targetUser.isMutedOnSeat) "Unmute Participant Mic" else "Mute Participant Mic",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            // Remove user from seat
            if (targetUser.seatNumber != null && canManageSeats) {
              OutlinedButton(
                onClick = {
                  onRemoveFromSeat?.invoke(targetUser)
                  Toast.makeText(context, "Removed ${targetUser.username} from Seat ${targetUser.seatNumber}", Toast.LENGTH_SHORT).show()
                  onDismiss()
                },
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, Color(0xFFFFB300)),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFFB300))
              ) {
                Text(text = "Move / Remove From Seat", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }

            // Kick user from room
            if (canKick) {
              OutlinedButton(
                onClick = {
                  onKickParticipant?.invoke(targetUser)
                  Toast.makeText(context, "Kicked ${targetUser.username} from room", Toast.LENGTH_SHORT).show()
                  onDismiss()
                },
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, Color(0xFFFF5252)),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5252))
              ) {
                Icon(Icons.Filled.PersonRemove, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Kick From Room", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }

            // Room Owner: Assign/Revoke Admin role & Configure Permissions
            if (isViewerOwner) {
              Button(
                onClick = {
                  onToggleAdmin?.invoke(targetUser)
                  Toast.makeText(
                    context,
                    if (targetUser.isAdmin) "Admin role removed from ${targetUser.username}" else "${targetUser.username} is now an Admin!",
                    Toast.LENGTH_SHORT
                  ).show()
                  onDismiss()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (targetUser.isAdmin) Color(0xFF455A64) else Color(0xFF1E88E5)
                ),
                shape = RoundedCornerShape(10.dp)
              ) {
                Icon(Icons.Filled.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = if (targetUser.isAdmin) "Remove Admin Role" else "Assign Admin Role",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }

              if (targetUser.isAdmin) {
                OutlinedButton(
                  onClick = { showAdminPermsDialog = true },
                  modifier = Modifier.fillMaxWidth(),
                  border = BorderStroke(1.dp, Color(0xFF00E5FF)),
                  shape = RoundedCornerShape(10.dp),
                  colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00E5FF))
                ) {
                  Icon(Icons.Filled.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(text = "Configure Admin Permissions", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }
      }
    }
    }
  }

  // Admin Permissions Config Dialog
  if (showAdminPermsDialog) {
    AdminPermissionsDialog(
      adminName = targetUser.username,
      currentPermissions = targetUser.adminPermissions,
      onDismiss = { showAdminPermsDialog = false },
      onSave = { newPerms ->
        onUpdateAdminPermissions?.invoke(targetUser, newPerms)
        showAdminPermsDialog = false
        Toast.makeText(context, "Admin permissions updated for ${targetUser.username}", Toast.LENGTH_SHORT).show()
      }
    )
  }
}

/**
 * 4. ADMIN PERMISSIONS CONFIGURATION DIALOG
 * Granular toggles managed solely by Room Owner:
 * - Mute users
 * - Unmute users
 * - Manage seats
 * - Remove/kick users
 */
@Composable
fun AdminPermissionsDialog(
  adminName: String,
  currentPermissions: AdminPermissions,
  onDismiss: () -> Unit,
  onSave: (AdminPermissions) -> Unit
) {
  var canMute by remember { mutableStateOf(currentPermissions.canMute) }
  var canUnmute by remember { mutableStateOf(currentPermissions.canUnmute) }
  var canManageSeats by remember { mutableStateOf(currentPermissions.canManageSeats) }
  var canKickUsers by remember { mutableStateOf(currentPermissions.canKickUsers) }

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
          .clip(RoundedCornerShape(20.dp))
          .background(Color(0xFF1C0033))
          .border(1.5.dp, Color(0xFF00E5FF), RoundedCornerShape(20.dp))
          .padding(18.dp)
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
        Text(
          text = "🛡️ Admin Permissions",
          color = Color.White,
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Configure moderation privileges for $adminName",
          color = Color(0xFF80D8FF),
          fontSize = 11.sp,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Mute users
        PermissionCheckboxRow(
          title = "Mute Users",
          subtitle = "Allow admin to mute participants on seat",
          checked = canMute,
          onCheckedChange = { canMute = it }
        )

        // Unmute users
        PermissionCheckboxRow(
          title = "Unmute Users",
          subtitle = "Allow admin to unmute participants",
          checked = canUnmute,
          onCheckedChange = { canUnmute = it }
        )

        // Manage seats
        PermissionCheckboxRow(
          title = "Manage Seats",
          subtitle = "Allow admin to move or remove users from seats",
          checked = canManageSeats,
          onCheckedChange = { canManageSeats = it }
        )

        // Kick users
        PermissionCheckboxRow(
          title = "Remove / Kick Users",
          subtitle = "Allow admin to remove participants from the room",
          checked = canKickUsers,
          onCheckedChange = { canKickUsers = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.weight(1f),
            border = BorderStroke(1.dp, Color(0x44FFFFFF)),
            shape = RoundedCornerShape(10.dp)
          ) {
            Text("Cancel", color = Color.White, fontSize = 11.sp)
          }

          Button(
            onClick = {
              onSave(
                AdminPermissions(
                  canMute = canMute,
                  canUnmute = canUnmute,
                  canManageSeats = canManageSeats,
                  canKickUsers = canKickUsers
                )
              )
            },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
            shape = RoundedCornerShape(10.dp)
          ) {
            Text("Save", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }
        }
      }
    }
    }
  }
}

@Composable
private fun PermissionCheckboxRow(
  title: String,
  subtitle: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onCheckedChange(!checked) }
      .padding(vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Checkbox(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = CheckboxDefaults.colors(
        checkedColor = Color(0xFF00E5FF),
        uncheckedColor = Color(0x66FFFFFF),
        checkmarkColor = Color.Black
      )
    )
    Spacer(modifier = Modifier.width(6.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(text = title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
      Text(text = subtitle, color = Color(0x88FFFFFF), fontSize = 10.sp)
    }
  }
}
