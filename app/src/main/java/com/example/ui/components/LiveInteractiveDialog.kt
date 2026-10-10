package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.aistudio.neonparty.nxvqlr.R
import com.example.ui.theme.LiveRed
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPinkBright

data class LiveComment(
  val userName: String,
  val text: String,
  val level: Int = 1
)

@Composable
fun LiveInteractiveDialog(
  title: String,
  streamerName: String,
  avatarRes: Int,
  onDismiss: () -> Unit
) {
  var isMicMuted by remember { mutableStateOf(false) }
  var heartsCount by remember { mutableIntStateOf(1248) }
  var commentInput by remember { mutableStateOf("") }
  var giftBannerText by remember { mutableStateOf<String?>(null) }
  var showGameDialog by remember { mutableStateOf(false) }

  val comments = remember {
    mutableStateListOf(
      LiveComment("Mr Max", "Welcome everybody to the stream! 🚀"),
      LiveComment("CyberGirl", "Great vibe in here! 💖✨"),
      LiveComment("RockHost", "Playing new songs tonight! 🎸"),
      LiveComment("Priya", "Loving the sound quality 🎧")
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
    val topSafePadding = rememberResponsiveTopBarPadding(extraTopSpacing = 2.dp)
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF0A0018))
    ) {
      // Full background streamer media
      Image(
        painter = painterResource(id = avatarRes),
        contentDescription = "Live background",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
      )

      // Dark gradient overlay for cyber contrast
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.verticalGradient(
              listOf(
                Color(0xCC0A001A),
                Color(0x550A001A),
                Color(0xDD0A001A)
              )
            )
          )
      )

      // Content Column
      val liveInteractiveBottomPadding = rememberResponsiveNavigationBarPadding(extraBottomSpacing = 20.dp)
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(start = 14.dp, end = 14.dp, top = topSafePadding, bottom = liveInteractiveBottomPadding),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        // Top Bar: Streamer Header & Close button
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Streamer Info Pill
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(24.dp))
              .background(Color(0xAA140026))
              .border(
                1.5.dp,
                Brush.horizontalGradient(listOf(NeonPink, NeonCyan)),
                RoundedCornerShape(24.dp)
              )
              .padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Image(
              painter = painterResource(id = avatarRes),
              contentDescription = null,
              modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .border(1.dp, NeonPink, CircleShape),
              contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = streamerName,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
              )
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(6.dp)
                    .background(LiveRed, CircleShape)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "LIVE • 1.4k viewers",
                  color = NeonCyan,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Medium
                )
              }
            }
          }

          // Close button
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(Color(0xAA24003E))
              .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
              .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Filled.Close,
              contentDescription = "Close",
              tint = Color.White,
              modifier = Modifier.size(20.dp)
            )
          }
        }

        // Center: Gift Banner Notification (when sent)
        AnimatedVisibility(
          visible = giftBannerText != null,
          enter = fadeIn() + scaleIn(initialScale = 0.90f),
          exit = fadeOut() + scaleOut(targetScale = 0.90f)
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .background(
                Brush.horizontalGradient(listOf(Color(0xFFFF007F), Color(0xFFFFB703)))
              )
              .padding(horizontal = 16.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = giftBannerText ?: "",
              color = Color.White,
              fontSize = 14.sp,
              fontWeight = FontWeight.Black
            )
          }
        }

        // Bottom Section: Comments Stream + Actions
        Column(modifier = Modifier.fillMaxWidth()) {
          // Comments Stream
          LazyColumn(
            modifier = Modifier
              .fillMaxWidth()
              .height(180.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            items(comments) { item ->
              Row(
                modifier = Modifier
                  .clip(RoundedCornerShape(14.dp))
                  .background(Color(0x99110022))
                  .border(0.8.dp, Color(0xFF6B00B6), RoundedCornerShape(14.dp))
                  .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                // Level badge
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF8A00D4))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                  Text(
                    text = "Lv.${item.level}",
                    color = NeonGold,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "${item.userName}: ",
                  color = NeonCyan,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = item.text,
                  color = Color.White,
                  fontSize = 12.sp
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Bottom Action Controls: Input, Send, Gift, Hearts, Mic
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Chat Input
            OutlinedTextField(
              value = commentInput,
              onValueChange = { commentInput = it },
              placeholder = { Text("Say something...", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp) },
              modifier = Modifier
                .weight(1f)
                .height(48.dp),
              shape = RoundedCornerShape(24.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = NeonPink,
                focusedContainerColor = Color(0xCC180030),
                unfocusedContainerColor = Color(0x99120024)
              ),
              trailingIcon = {
                if (commentInput.isNotBlank()) {
                  IconButton(
                    onClick = {
                      comments.add(LiveComment("You", commentInput, 1))
                      commentInput = ""
                    }
                  ) {
                    Icon(
                      imageVector = Icons.AutoMirrored.Filled.Send,
                      contentDescription = "Send",
                      tint = NeonCyan,
                      modifier = Modifier.size(18.dp)
                    )
                  }
                }
              }
            )

            // Gift Button
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                  Brush.radialGradient(listOf(Color(0xFFFFB703), Color(0xFFFF6D00)))
                )
                .clickable {
                  giftBannerText = "🎉 You sent a Cyber Rocket 🚀 to $streamerName!"
                },
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Filled.CardGiftcard,
                contentDescription = "Send Gift",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
              )
            }

            // Hearts Button
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                  Brush.radialGradient(listOf(NeonPinkBright, Color(0xFFBD00FF)))
                )
                .clickable {
                  heartsCount += 1
                },
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                  imageVector = Icons.Filled.Favorite,
                  contentDescription = "Like",
                  tint = Color.White,
                  modifier = Modifier.size(18.dp)
                )
                Text(
                  text = "$heartsCount",
                  color = Color.White,
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            // Games Button (Matches Party Room Games button)
            FloatingPartyGamesButton(
              onClick = { showGameDialog = true }
            )

            // Mic Toggle
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (isMicMuted) Color(0xFF333333) else Color(0xAA0091FF))
                .clickable { isMicMuted = !isMicMuted },
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (isMicMuted) Icons.Filled.MicOff else Icons.Filled.Mic,
                contentDescription = "Mic",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }
      }

      if (showGameDialog) {
        PartyGameDialog(onDismiss = { showGameDialog = false })
      }
    }
  }
}
