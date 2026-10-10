package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.storage.LevelSystem

@Composable
fun LevelSystemDialog(
  currentCoins: Long,
  onAddCoins: (Long) -> Unit,
  onDismiss: () -> Unit
) {
  val progressInfo = remember(currentCoins) {
    LevelSystem.getProgressInfo(currentCoins)
  }
  val allTiers = remember(currentCoins) {
    LevelSystem.getAllTiers(currentCoins)
  }
  var showAllTiers by remember { mutableStateOf(false) }

  val animatedProgress by animateFloatAsState(
    targetValue = progressInfo.progressRatio,
    animationSpec = tween(durationMillis = 600),
    label = "level_progress"
  )

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(
      usePlatformDefaultWidth = false,
      decorFitsSystemWindows = false
    )
  ) {
    com.example.ui.components.ConfigureFullScreenDialogWindow()
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.94f)
        .fillMaxHeight(0.88f)
        .clip(RoundedCornerShape(26.dp))
        .border(
          width = 2.dp,
          brush = Brush.linearGradient(
            listOf(Color(0xFF00E5FF), Color(0xFFFFD700), Color(0xFFE040FB), Color(0xFF00E5FF))
          ),
          shape = RoundedCornerShape(26.dp)
        )
        .testTag("level_system_dialog"),
      color = Color(0xFF0C001C)
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(18.dp)
      ) {
        // 1. Header with Close Button
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(
                  Brush.radialGradient(
                    listOf(Color(0xFFFFD700).copy(alpha = 0.4f), Color(0xFF261800))
                  )
                )
                .border(1.5.dp, Color(0xFFFFD700), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Filled.MilitaryTech,
                contentDescription = null,
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(24.dp)
              )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
              Text(
                text = "LEVEL SYSTEM",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                style = TextStyle(
                  brush = Brush.linearGradient(
                    listOf(Color.White, Color(0xFF80DEEA), Color(0xFF00E5FF))
                  )
                )
              )
              Text(
                text = "Levels 1 — 50 • Required Diamonds = Level × 2,000",
                color = Color(0xFFB388FF),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(34.dp)
          ) {
            Icon(
              imageVector = Icons.Filled.Close,
              contentDescription = "Close",
              tint = Color.White
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Scrollable Body
        LazyColumn(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // Current Level Spotlight Card
          item {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                  Brush.verticalGradient(
                    listOf(Color(0xFF220542), Color(0xFF130126))
                  )
                )
                .border(
                  width = 1.5.dp,
                  brush = Brush.horizontalGradient(
                    listOf(Color(0xFF00E5FF), Color(0xFFFFD700))
                  ),
                  shape = RoundedCornerShape(20.dp)
                )
                .padding(16.dp)
            ) {
              Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text(
                      text = "CURRENT LEVEL",
                      color = Color(0xFF80DEEA),
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      letterSpacing = 1.sp
                    )
                    Text(
                      text = "Level ${progressInfo.currentLevel}",
                      color = Color.White,
                      fontSize = 28.sp,
                      fontWeight = FontWeight.Black
                    )
                  }

                  // Official Badge (Strictly shows ONLY the level with professional tier colors)
                  com.example.ui.components.OfficialLevelBadge(
                    level = progressInfo.currentLevel,
                    fontSize = 13.sp
                  )
                }

                // Balance row
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0A0016))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Filled.Diamond,
                      contentDescription = null,
                      tint = Color(0xFF00E5FF),
                      modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "Diamond Balance:",
                      color = Color(0xFFEDE7F6),
                      fontSize = 12.sp
                    )
                  }
                  Text(
                    text = "${LevelSystem.formatCoins(progressInfo.currentCoins)} Diamonds 💎",
                    color = Color(0xFF00E5FF),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                  )
                }

                // Progress Bar Section
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(
                      text = "Progress to Level ${progressInfo.nextLevel}",
                      color = Color(0xFFD1C4E9),
                      fontSize = 11.5.sp
                    )
                    Text(
                      text = "${(animatedProgress * 100).toInt()}%",
                      color = Color(0xFF00E5FF),
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }

                  // Gradient Progress Track
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(12.dp)
                      .clip(RoundedCornerShape(6.dp))
                      .background(Color(0xFF1B0336))
                  ) {
                    Box(
                      modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                          Brush.horizontalGradient(
                            listOf(Color(0xFF00E5FF), Color(0xFFE040FB), Color(0xFFFFD700))
                          )
                        )
                    )
                  }

                  // Requirements summary
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(
                      text = if (progressInfo.currentLevel >= 50) "Max Level Reached"
                             else "Needed: ${LevelSystem.formatCoins(progressInfo.coinsRemaining)} diamonds",
                      color = Color(0xFFFF80AB),
                      fontSize = 10.5.sp,
                      fontWeight = FontWeight.Medium
                    )
                    Text(
                      text = "Target: ${LevelSystem.formatCoins(progressInfo.coinsRequiredForNextLevel)}",
                      color = Color(0xFFB0BEC5),
                      fontSize = 10.5.sp
                    )
                  }
                }
              }
            }
          }

          // Quick Diamond Top-Up & Live Level Test
          item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Text(
                text = "TEST DIAMOND PROGRESSION (INSTANT LEVEL UP)",
                color = Color(0xFF00E5FF),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              )

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                QuickCoinChip(
                  label = "+10K",
                  amount = 10_000L,
                  modifier = Modifier.weight(1f),
                  onClick = { onAddCoins(10_000L) }
                )
                QuickCoinChip(
                  label = "+20K",
                  amount = 20_000L,
                  modifier = Modifier.weight(1f),
                  onClick = { onAddCoins(20_000L) }
                )
                QuickCoinChip(
                  label = "+40K",
                  amount = 40_000L,
                  modifier = Modifier.weight(1f),
                  onClick = { onAddCoins(40_000L) }
                )
                QuickCoinChip(
                  label = "+80K",
                  amount = 80_000L,
                  modifier = Modifier.weight(1f),
                  onClick = { onAddCoins(80_000L) }
                )
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                QuickCoinChip(
                  label = "+160K",
                  amount = 160_000L,
                  modifier = Modifier.weight(1f),
                  onClick = { onAddCoins(160_000L) }
                )
                QuickCoinChip(
                  label = "+320K",
                  amount = 320_000L,
                  modifier = Modifier.weight(1f),
                  onClick = { onAddCoins(320_000L) }
                )
                QuickCoinChip(
                  label = "+640K",
                  amount = 640_000L,
                  modifier = Modifier.weight(1f),
                  onClick = { onAddCoins(640_000L) }
                )
                QuickCoinChip(
                  label = "+1M",
                  amount = 1_000_000L,
                  modifier = Modifier.weight(1f),
                  onClick = { onAddCoins(1_000_000L) }
                )
              }
            }
          }

          // Unlocked Perks Card
          item {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF180230))
                .border(1.2.dp, Color(0xFF7C4DFF), RoundedCornerShape(16.dp))
                .padding(14.dp)
            ) {
              Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Filled.Stars,
                    contentDescription = null,
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "LEVEL ${progressInfo.currentLevel} PRIVILEGES & REWARDS",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                  )
                }

                progressInfo.rewards.forEach { reward ->
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                  ) {
                    Text(text = "✦ ", color = Color(0xFF00E5FF), fontSize = 12.sp)
                    Text(
                      text = reward,
                      color = Color(0xFFEDE7F6),
                      fontSize = 12.sp
                    )
                  }
                }
              }
            }
          }

          // Mathematical Formula Notice
          item {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF060B1F))
                .border(1.dp, Color(0xFF00B0FF).copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                .padding(12.dp)
            ) {
              Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                  text = "Progression Rule: Required Diamonds = Level × 2,000",
                  color = Color(0xFF80D8FF),
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "Base: Level 1 = 2,000 • Level 10 = 20,000 • Level 50 = 100,000 diamonds",
                  color = Color(0xFFB0BEC5),
                  fontSize = 10.sp
                )
              }
            }
          }

          // Toggle 50-Level Directory Table
          item {
            Button(
              onClick = { showAllTiers = !showAllTiers },
              modifier = Modifier.fillMaxWidth(),
              colors = ButtonDefaults.buttonColors(
                containerColor = if (showAllTiers) Color(0xFF7C4DFF) else Color(0xFF260447)
              ),
              shape = RoundedCornerShape(14.dp)
            ) {
              Text(
                text = if (showAllTiers) "Hide 50-Level Tier Table ▲" else "View All 50 Level Requirements (1—50) ▼",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          // 50 Tiers list
          if (showAllTiers) {
            items(allTiers) { tier ->
              LevelTierRow(tier = tier)
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Done Button
        Button(
          onClick = onDismiss,
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
          contentPadding = PaddingValues(0.dp)
        ) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .clip(RoundedCornerShape(14.dp))
              .background(
                Brush.horizontalGradient(listOf(Color(0xFF00E5FF), Color(0xFF7C4DFF)))
              ),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "GOT IT",
              color = Color.White,
              fontSize = 13.5.sp,
              fontWeight = FontWeight.Black,
              letterSpacing = 1.sp
            )
          }
        }
      }
    }
  }
}

@Composable
private fun QuickCoinChip(
  label: String,
  amount: Long,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Box(
    modifier = modifier
      .height(34.dp)
      .clip(RoundedCornerShape(10.dp))
      .background(Color(0xFF1F0838))
      .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.6f), RoundedCornerShape(10.dp))
      .clickable(onClick = onClick),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = label,
      color = Color(0xFFFFD700),
      fontSize = 11.5.sp,
      fontWeight = FontWeight.ExtraBold
    )
  }
}

@Composable
private fun LevelTierRow(tier: com.example.ui.storage.LevelTier) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .background(if (tier.isUnlocked) Color(0xFF142416) else Color(0xFF120321))
      .border(
        width = 1.dp,
        color = if (tier.isUnlocked) Color(0xFF00E676) else Color(0xFF3B1759),
        shape = RoundedCornerShape(10.dp)
      )
      .padding(horizontal = 10.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      com.example.ui.components.OfficialLevelBadge(level = tier.level, fontSize = 9.sp)
      Spacer(modifier = Modifier.width(8.dp))
      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = tier.title,
            color = if (tier.isUnlocked) Color(0xFF00E676) else Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }
        Text(
          text = tier.perks,
          color = Color(0xFF78909C),
          fontSize = 10.sp
        )
      }
    }

    Column(horizontalAlignment = Alignment.End) {
      Text(
        text = "${LevelSystem.formatCoins(tier.requiredCoins)} 💎",
        color = if (tier.isUnlocked) Color(0xFFFFD700) else Color(0xFF90A4AE),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold
      )
      if (tier.isUnlocked) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF00E676),
            modifier = Modifier.size(11.dp)
          )
          Spacer(modifier = Modifier.width(2.dp))
          Text(
            text = "Unlocked",
            color = Color(0xFF00E676),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
          )
        }
      } else {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Filled.Lock,
            contentDescription = null,
            tint = Color(0xFF78909C),
            modifier = Modifier.size(11.dp)
          )
          Spacer(modifier = Modifier.width(2.dp))
          Text(
            text = "Locked",
            color = Color(0xFF78909C),
            fontSize = 9.sp
          )
        }
      }
    }
  }
}
