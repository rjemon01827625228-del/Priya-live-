package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.storage.AgencyInviteMessage
import com.example.ui.storage.LocalChatStorage
import com.example.ui.storage.LocalConversationSummary
import com.example.ui.storage.LocalImageStorage
import com.example.ui.storage.SocialAgencyStorage
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPinkBright
import com.example.ui.theme.TextMuted

data class ChatConversation(
  val id: String,
  val name: String,
  val avatarRes: Int = R.drawable.img_profile_max,
  val avatarUri: String? = null,
  val flagEmoji: String = "🇧🇩",
  val isItalic: Boolean = false,
  val lastMessage: String = "",
  val unreadCount: Int = 0
)

@Composable
fun MessageInboxScreen(
  onChatClick: (ChatConversation) -> Unit,
  onHelplineClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val activeId = SocialAgencyStorage.getActiveUserId(context)
  var pendingInvites by remember {
    mutableStateOf(SocialAgencyStorage.getPendingInvitesForUser(context, activeId))
  }
  var selectedFilter by remember { mutableStateOf("All") }
  var realConversations by remember {
    mutableStateOf<List<LocalConversationSummary>>(
      LocalChatStorage.getConversationSummaries(context, activeId)
    )
  }

  DisposableEffect(activeId) {
    realConversations = LocalChatStorage.getConversationSummaries(context, activeId)
    onDispose { }
  }

  val displayChats = remember(realConversations, selectedFilter) {
    when (selectedFilter) {
      "Unread" -> realConversations.filter { it.unreadCount > 0 }
      else -> realConversations
    }.map { summary ->
      ChatConversation(
        id = summary.peerUserId,
        name = summary.peerName.ifBlank { "User_${summary.peerUserId}" },
        avatarUri = summary.peerAvatarUri,
        flagEmoji = "🇧🇩",
        lastMessage = summary.lastMessage,
        unreadCount = summary.unreadCount
      )
    }
  }

  val topSafePadding = com.example.ui.components.rememberResponsiveTopBarPadding(extraTopSpacing = 12.dp)

  LazyColumn(
    modifier = modifier.fillMaxSize(),
    contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = topSafePadding, bottom = 90.dp)
  ) {
    // 1. "Message" Glowing Title
    item {
      Text(
        text = "Message",
        color = Color(0xFF64D2FF),
        fontSize = 28.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 0.5.sp,
        modifier = Modifier.padding(bottom = 12.dp)
      )
    }

    // 2. Status Filters: [All], [Online], [Unread]
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        StatusFilterPill(
          title = "All",
          isSelected = selectedFilter == "All",
          gradientBorder = Brush.horizontalGradient(listOf(Color(0xFF0077B6), NeonCyan)),
          onClick = { selectedFilter = "All" }
        )

        StatusFilterPill(
          title = "Online",
          isSelected = selectedFilter == "Online",
          gradientBorder = Brush.horizontalGradient(listOf(NeonCyan, Color(0xFF0091FF))),
          onClick = { selectedFilter = "Online" }
        )

        StatusFilterPill(
          title = "Unread",
          isSelected = selectedFilter == "Unread",
          gradientBorder = Brush.horizontalGradient(listOf(NeonPink, Color(0xFF7A00CC))),
          onClick = { selectedFilter = "Unread" }
        )
      }
    }

    // 3. Top Pinned Card: "OFFICIAL HELPLINE"
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(32.dp))
          .border(
            width = 2.dp,
            brush = Brush.horizontalGradient(
              listOf(Color(0xFFFF3366), Color(0xFF00C0FF), Color(0xFFFF3366))
            ),
            shape = RoundedCornerShape(32.dp)
          )
          .background(
            Brush.horizontalGradient(
              listOf(
                Color(0xFFCC1144),
                Color(0xFF225588),
                Color(0xFFCC1144)
              )
            )
          )
          .clickable(onClick = onHelplineClick)
          .padding(horizontal = 10.dp, vertical = 8.dp)
          .testTag("official_helpline_card")
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Image(
            painter = painterResource(id = R.drawable.img_streamer_girl),
            contentDescription = "Official Helpline",
            modifier = Modifier
              .size(46.dp)
              .clip(CircleShape)
              .border(1.5.dp, Color.White, CircleShape),
            contentScale = ContentScale.Crop
          )

          Spacer(modifier = Modifier.width(16.dp))

          Text(
            text = "OFFICIAL HELPLINE",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.5.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }

    // Agency Invitations (if any pending for active user)
    if (pendingInvites.isNotEmpty()) {
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = "AGENCY INVITATIONS (${pendingInvites.size})",
            color = Color(0xFFFFD700),
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.5.sp
          )

          pendingInvites.forEach { invite ->
            AgencyInviteCard(
              invite = invite,
              onAccept = {
                SocialAgencyStorage.respondToInvitation(context, invite.inviteId, true)
                pendingInvites = SocialAgencyStorage.getPendingInvitesForUser(context, activeId)
                Toast.makeText(context, "Joined ${invite.agencyName} as Official Host!", Toast.LENGTH_SHORT).show()
              },
              onReject = {
                SocialAgencyStorage.respondToInvitation(context, invite.inviteId, false)
                pendingInvites = SocialAgencyStorage.getPendingInvitesForUser(context, activeId)
                Toast.makeText(context, "Invitation declined", Toast.LENGTH_SHORT).show()
              }
            )
          }
        }
      }
    }

    // 4. Real-time Chats (No fake accounts)
    if (displayChats.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color(0xFF190033))
                .border(1.2.dp, NeonCyan.copy(alpha = 0.4f), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.Chat,
                contentDescription = null,
                tint = NeonCyan,
                modifier = Modifier.size(28.dp)
              )
            }

            Text(
              text = "No messages yet",
              color = Color.White,
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold
            )

            Text(
              text = "Direct messages from real users will appear here in real time.",
              color = TextMuted,
              fontSize = 12.sp
            )
          }
        }
      }
    } else {
      items(displayChats, key = { it.id }) { chat ->
        ChatRowCard(
          chat = chat,
          onClick = { onChatClick(chat) },
          modifier = Modifier.testTag("chat_row_${chat.id}")
        )
        Spacer(modifier = Modifier.height(12.dp))
      }
    }
  }
}

@Composable
private fun StatusFilterPill(
  title: String,
  isSelected: Boolean,
  gradientBorder: Brush,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(20.dp))
      .border(
        width = if (isSelected) 2.dp else 1.2.dp,
        brush = gradientBorder,
        shape = RoundedCornerShape(20.dp)
      )
      .background(
        if (isSelected) {
          Brush.verticalGradient(listOf(Color(0xFF1E0A44), Color(0xFF0C0220)))
        } else {
          Brush.verticalGradient(listOf(Color(0xFF14022A), Color(0xFF090014)))
        }
      )
      .clickable(onClick = onClick)
      .padding(horizontal = 24.dp, vertical = 7.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = title,
      color = Color.White,
      fontSize = 14.sp,
      fontWeight = FontWeight.Bold
    )
  }
}

@Composable
fun ChatRowCard(
  chat: ChatConversation,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  val context = LocalContext.current
  val resolvedAvatar = remember(chat.avatarUri, chat.id) {
    LocalImageStorage.resolveImageModel(
      context = context,
      candidateUri = chat.avatarUri,
      imageType = LocalImageStorage.ImageType.PROFILE,
      entityId = chat.id,
      defaultFallbackRes = R.drawable.img_profile_max
    )
  }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(28.dp))
      .border(
        width = 1.6.dp,
        brush = Brush.horizontalGradient(
          listOf(
            NeonCyan.copy(alpha = 0.8f),
            Color(0xFF8A00D4),
            NeonPink.copy(alpha = 0.8f)
          )
        ),
        shape = RoundedCornerShape(28.dp)
      )
      .background(
        Brush.horizontalGradient(
          listOf(
            Color(0xFF16012B),
            Color(0xFF260144),
            Color(0xFF140024)
          )
        )
      )
      .clickable(onClick = onClick)
      .padding(horizontal = 12.dp, vertical = 8.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Left avatar
      AsyncImage(
        model = resolvedAvatar,
        contentDescription = chat.name,
        modifier = Modifier
          .size(44.dp)
          .clip(CircleShape)
          .border(1.5.dp, NeonPink, CircleShape),
        contentScale = ContentScale.Crop,
        error = painterResource(id = R.drawable.img_profile_max),
        fallback = painterResource(id = R.drawable.img_profile_max)
      )

      Spacer(modifier = Modifier.width(14.dp))

      // Center: Name & Last message
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = chat.name,
          color = Color.White,
          fontSize = 16.sp,
          fontWeight = FontWeight.Black,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        if (chat.lastMessage.isNotBlank()) {
          Text(
            text = chat.lastMessage,
            color = TextMuted,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      // Unread Badge or Flag
      if (chat.unreadCount > 0) {
        Box(
          modifier = Modifier
            .clip(CircleShape)
            .background(NeonPink)
            .padding(horizontal = 7.dp, vertical = 2.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "${chat.unreadCount}",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black
          )
        }
      } else {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF120022))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = chat.flagEmoji,
            fontSize = 22.sp
          )
        }
      }
    }
  }
}

@Composable
private fun AgencyInviteCard(
  invite: AgencyInviteMessage,
  onAccept: () -> Unit,
  onReject: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .border(
        width = 1.6.dp,
        brush = Brush.horizontalGradient(
          listOf(Color(0xFFFFD700), Color(0xFFFF8800), Color(0xFFFF3366))
        ),
        shape = RoundedCornerShape(18.dp)
      )
      .background(
        Brush.verticalGradient(
          listOf(Color(0xFF2E1500), Color(0xFF150824))
        )
      )
      .padding(14.dp)
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color(0xFFFFD700).copy(alpha = 0.2f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Filled.WorkspacePremium,
            contentDescription = "Agency Invitation",
            tint = Color(0xFFFFD700),
            modifier = Modifier.size(24.dp)
          )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = invite.agencyName,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Invited by Agency Owner • ID: ${invite.agencyId}",
            color = Color.White.copy(alpha = 0.65f),
            fontSize = 11.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedButton(
          onClick = onReject,
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(16.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
        ) {
          Text("Decline", fontSize = 12.sp)
        }

        Button(
          onClick = onAccept,
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8800))
        ) {
          Text("Accept", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
      }
    }
  }
}
