package com.example.ui.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.aistudio.neonparty.nxvqlr.R
import com.example.ui.screens.ChatConversation
import com.example.ui.storage.DirectMessage
import com.example.ui.storage.LocalChatStorage
import com.example.ui.storage.LocalImageStorage
import com.example.ui.storage.UserProfileStorage
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink

@Composable
fun ChatDetailDialog(
  chat: ChatConversation,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val activeUserId = remember { UserProfileStorage.getActiveUserId(context) }
  var messageInput by remember { mutableStateOf("") }
  var realMessages by remember {
    mutableStateOf(LocalChatStorage.getConversationMessages(activeUserId, chat.id))
  }

  val resolvedAvatar = remember(chat.avatarUri, chat.id) {
    LocalImageStorage.resolveImageModel(
      context = context,
      candidateUri = chat.avatarUri,
      imageType = LocalImageStorage.ImageType.PROFILE,
      entityId = chat.id,
      defaultFallbackRes = R.drawable.img_profile_max
    )
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(
      usePlatformDefaultWidth = false,
      decorFitsSystemWindows = false
    )
  ) {
    ConfigureFullScreenDialogWindow()
    val topSafePadding = rememberResponsiveTopBarPadding(extraTopSpacing = 10.dp)
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            listOf(Color(0xFF0F0024), Color(0xFF1B0038), Color(0xFF0A0018))
          )
        )
    ) {
      val responsiveBottomPadding = rememberResponsiveNavigationBarPadding(extraBottomSpacing = 8.dp)
      Column(
        modifier = Modifier
          .fillMaxSize()
          .windowInsetsPadding(WindowInsets.ime)
          .padding(bottom = responsiveBottomPadding)
      ) {
        // Top Header
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF140129))
            .border(
              1.dp,
              Brush.horizontalGradient(listOf(NeonCyan, NeonPink)),
              RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            )
            .padding(start = 12.dp, end = 12.dp, top = topSafePadding, bottom = 10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = onDismiss) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = Color.White
            )
          }

          Spacer(modifier = Modifier.width(6.dp))

          AsyncImage(
            model = resolvedAvatar,
            contentDescription = chat.name,
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .border(1.5.dp, NeonPink, CircleShape),
            contentScale = ContentScale.Crop,
            error = painterResource(id = R.drawable.img_profile_max),
            fallback = painterResource(id = R.drawable.img_profile_max)
          )

          Spacer(modifier = Modifier.width(10.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = chat.name,
              color = Color.White,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Online ${chat.flagEmoji}",
              color = NeonCyan,
              fontSize = 11.sp
            )
          }
        }

        // Messages List
        if (realMessages.isEmpty()) {
          Box(
            modifier = Modifier
              .weight(1f)
              .fillMaxWidth(),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "Say hello to start the conversation!",
              color = Color.White.copy(alpha = 0.5f),
              fontSize = 13.sp
            )
          }
        } else {
          LazyColumn(
            modifier = Modifier
              .weight(1f)
              .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(realMessages, key = { it.messageId }) { msg ->
              val isMe = msg.senderId == activeUserId
              Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
              ) {
                Box(
                  modifier = Modifier
                    .clip(
                      RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isMe) 16.dp else 4.dp,
                        bottomEnd = if (isMe) 4.dp else 16.dp
                      )
                    )
                    .border(
                      1.dp,
                      if (isMe) NeonCyan else NeonPink,
                      RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isMe) 16.dp else 4.dp,
                        bottomEnd = if (isMe) 4.dp else 16.dp
                      )
                    )
                    .background(
                      if (isMe) {
                        Brush.horizontalGradient(listOf(Color(0xFF0055AA), Color(0xFF0077B6)))
                      } else {
                        Brush.horizontalGradient(listOf(Color(0xFF330055), Color(0xFF550077)))
                      }
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                  Text(
                    text = msg.text,
                    color = Color.White,
                    fontSize = 14.sp
                  )
                }
              }
            }
          }
        }

        // Message Input Row
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = messageInput,
            onValueChange = { messageInput = it },
            placeholder = { Text("Type a message...", color = Color.White.copy(alpha = 0.5f)) },
            modifier = Modifier
              .weight(1f)
              .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White,
              focusedBorderColor = NeonCyan,
              unfocusedBorderColor = NeonPink,
              focusedContainerColor = Color(0xFF17002C),
              unfocusedContainerColor = Color(0xFF120024)
            )
          )

          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(CircleShape)
              .background(
                Brush.radialGradient(listOf(NeonCyan, Color(0xFF0077B6)))
              )
              .clickable {
                if (messageInput.isNotBlank()) {
                  val textToSend = messageInput.trim()
                  messageInput = ""

                  val myProfile = UserProfileStorage.getProfileForUser(context, activeUserId)
                  LocalChatStorage.sendMessage(
                    senderId = activeUserId,
                    senderName = myProfile.username.ifBlank { "User_$activeUserId" },
                    senderAvatar = myProfile.profileImageUri,
                    receiverId = chat.id,
                    receiverName = chat.name,
                    receiverAvatar = chat.avatarUri,
                    text = textToSend
                  ) {
                    realMessages = LocalChatStorage.getConversationMessages(activeUserId, chat.id)
                  }
                }
              },
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.Send,
              contentDescription = "Send",
              tint = Color.White,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }
    }
  }
}
