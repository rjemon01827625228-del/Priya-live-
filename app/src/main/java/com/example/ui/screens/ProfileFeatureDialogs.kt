package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.filled.WorkspacePremium
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.storage.LevelSystem
import com.example.ui.storage.UserProfileStorage
import com.example.ui.storage.VipSystem

// -----------------------------------------------------------------------------
// PARALLEL BOX DIALOG
// -----------------------------------------------------------------------------
@Composable
fun ParallelBoxDialog(
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  var isCloned by remember { mutableStateOf(false) }

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
        .fillMaxWidth(0.90f)
        .clip(RoundedCornerShape(26.dp))
        .border(1.8.dp, Color(0xFF00E5FF), RoundedCornerShape(26.dp))
        .testTag("parallel_box_dialog"),
      color = Color(0xFF0E0121)
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Box(
          modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(Color(0xFF00E5FF).copy(alpha = 0.2f))
            .border(1.5.dp, Color(0xFF00E5FF), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Filled.Widgets, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(28.dp))
        }

        Text(
          text = "PARALLEL BOX",
          color = Color.White,
          fontSize = 18.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 1.sp
        )

        Text(
          text = "Dual Space & Multi-Account Sandbox",
          color = Color(0xFF80DEEA),
          fontSize = 12.sp
        )

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1B0538))
            .padding(14.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = "• Run 2 Priya Live streams simultaneously", color = Color(0xFFEDE7F6), fontSize = 12.sp)
            Text(text = "• Dual-Account instant switching without logout", color = Color(0xFFEDE7F6), fontSize = 12.sp)
            Text(text = "• Isolated security sandbox for payment keys", color = Color(0xFFEDE7F6), fontSize = 12.sp)
            Text(
              text = if (isCloned) "Status: Secondary Space Active" else "Status: Single Instance Active",
              color = if (isCloned) Color(0xFF00E676) else Color(0xFFFFD700),
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        Button(
          onClick = {
            isCloned = !isCloned
            val msg = if (isCloned) "Secondary Parallel Space initialized!" else "Parallel Space suspended."
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text(
            text = if (isCloned) "SUSPEND SECONDARY SPACE" else "CLONE NEON LIVE SPACE",
            color = Color.Black,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Black
          )
        }

        Button(
          onClick = onDismiss,
          modifier = Modifier
            .fillMaxWidth()
            .height(38.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FFFFFF)),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("Done", color = Color.White)
        }
      }
    }
  }
}

// -----------------------------------------------------------------------------
// COIN TOP-UP DIALOG (Connected to Level System)
// -----------------------------------------------------------------------------
@Composable
fun CoinTopUpDialog(
  currentCoins: Long,
  onAddCoins: (Long) -> Unit,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val activeUserId = remember { UserProfileStorage.getActiveUserId(context) }
  val currentLevel = remember(currentCoins) { LevelSystem.calculateLevel(currentCoins) }
  val nextLevelCoins = remember(currentCoins) { LevelSystem.getRequiredCoins(currentLevel + 1) }
  val totalRecharge = remember(currentCoins) { UserProfileStorage.getTotalRecharge(context, activeUserId) }
  val currentVip = remember(totalRecharge) { VipSystem.calculateVIPLevel(totalRecharge) }

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
        .fillMaxWidth(0.92f)
        .clip(RoundedCornerShape(26.dp))
        .border(
          width = 2.dp,
          brush = Brush.horizontalGradient(listOf(Color(0xFFFFD700), Color(0xFF00E5FF))),
          shape = RoundedCornerShape(26.dp)
        )
        .testTag("coin_top_up_dialog"),
      color = Color(0xFF100124)
    ) {
      Column(
        modifier = Modifier
          .padding(20.dp)
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFF00E5FF).copy(alpha = 0.2f))
                .border(1.2.dp, Color(0xFF00E5FF), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Filled.AutoAwesome,
                contentDescription = "Diamond",
                tint = Color(0xFF00E5FF),
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(text = "DIAMOND & VIP STORE", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
              Text(text = "Recharge auto-unlocks VIP 1 to VIP 10!", color = Color(0xFFFFD700), fontSize = 10.5.sp)
            }
          }

          IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
          }
        }

        // Current Status Card
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF22053D))
            .border(1.dp, Color(0xFFFFD700), RoundedCornerShape(16.dp))
            .padding(14.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(text = "CURRENT BALANCE", color = Color(0xFF80DEEA), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(
                  text = "${LevelSystem.formatCoins(currentCoins)} Diamonds 💎",
                  color = Color(0xFF00E5FF),
                  fontSize = 18.sp,
                  fontWeight = FontWeight.Black
                )
              }
              Column(horizontalAlignment = Alignment.End) {
                Text(text = "VIP STATUS", color = Color(0xFF80DEEA), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(
                  text = if (currentVip > 0) "VIP $currentVip" else "Unranked",
                  color = if (currentVip > 0) Color(0xFFFFD700) else Color.White,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Black
                )
                Text(
                  text = "Lifetime: ${VipSystem.formatCoins(totalRecharge)} 💎",
                  color = Color(0xFFCE93D8),
                  fontSize = 9.5.sp
                )
              }
            }
          }
        }

        // OFFICIAL COIN SELLER Section (Requirements 9 & 10)
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F0024))
            .border(1.2.dp, Color(0xFFFFD700), RoundedCornerShape(16.dp))
            .padding(14.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(Color(0xFFFFD700).copy(alpha = 0.15f))
                  .border(1.dp, Color(0xFFFFD700), CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Filled.Shield,
                  contentDescription = "Official Coin Seller",
                  tint = Color(0xFFFFD700),
                  modifier = Modifier.size(20.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "OFFICIAL COIN SELLER",
                  color = Color(0xFFFFD700),
                  fontSize = 13.5.sp,
                  fontWeight = FontWeight.Black,
                  letterSpacing = 0.5.sp
                )
                Text(
                  text = "Verified Official Seller • Instant Delivery",
                  color = Color(0xFF80DEEA),
                  fontSize = 10.5.sp
                )
              }
            }

            Spacer(modifier = Modifier.width(6.dp))

            Button(
              onClick = {
                try {
                  val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://api.whatsapp.com/send?phone=919225108371")
                  }
                  context.startActivity(intent)
                } catch (_: Exception) {
                  Toast.makeText(context, "Official Seller WhatsApp: +91 92251 08371", Toast.LENGTH_SHORT).show()
                }
              },
              shape = RoundedCornerShape(18.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Text("💬", fontSize = 12.sp)
                Text(
                  text = "WhatsApp",
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.5.sp
                )
              }
            }
          }
        }

        Text(
          text = "OFFICIAL RECHARGE PACKAGES",
          color = Color(0xFF00E5FF),
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp,
          modifier = Modifier.align(Alignment.Start)
        )

        // Diamond packages
        listOf(
          Triple(100_000L, "$9.99", "Auto-Unlocks VIP 1"),
          Triple(200_000L, "$19.99", "Auto-Unlocks VIP 2"),
          Triple(400_000L, "$39.99", "Auto-Unlocks VIP 3"),
          Triple(800_000L, "$79.99", "Auto-Unlocks VIP 4"),
          Triple(1_600_000L, "$149.99", "Auto-Unlocks VIP 5")
        ).forEach { (amount, price, hint) ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFF1B0438))
              .border(1.dp, Color(0xFF7C4DFF).copy(alpha = 0.6f), RoundedCornerShape(12.dp))
              .clickable {
                onAddCoins(amount)
                val newLifetime = totalRecharge + amount
                val newVip = VipSystem.calculateVIPLevel(newLifetime)
                val vipNotice = if (newVip > currentVip) " [VIP $newVip Activated!]" else ""
                Toast.makeText(
                  context,
                  "Recharge verified! +${LevelSystem.formatCoins(amount)} Diamonds.$vipNotice",
                  Toast.LENGTH_LONG
                ).show()
              }
              .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("💎", fontSize = 18.sp)
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "${LevelSystem.formatCoins(amount)} Diamonds",
                  color = Color.White,
                  fontSize = 13.5.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(text = hint, color = Color(0xFFFFD700), fontSize = 10.sp, fontWeight = FontWeight.Bold)
              }
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFFFD700))
                .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
              Text(text = price, color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Black)
            }
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Button(
          onClick = onDismiss,
          modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FFFFFF)),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("Done", color = Color.White)
        }
      }
    }
  }
}
