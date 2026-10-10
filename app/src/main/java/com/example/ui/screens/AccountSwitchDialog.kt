package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.aistudio.neonparty.nxvqlr.R
import com.example.ui.storage.SocialAgencyStorage

data class SwitchableAccount(
  val id: String,
  val username: String,
  val avatarRes: Int,
  val role: String,
  val flag: String = "🇧🇩"
)

@Composable
fun AccountSwitchDialog(
  currentActiveId: String,
  onDismiss: () -> Unit,
  onAccountSwitched: (String) -> Unit
) {
  val context = LocalContext.current

  val accounts = listOf(
    SwitchableAccount(
      id = "100",
      username = "MR MAX",
      avatarRes = R.drawable.img_profile_max,
      role = "Agency Owner • Super VIP",
      flag = "🇧🇩"
    ),
    SwitchableAccount(
      id = "5487293",
      username = "Maya Queen 💜",
      avatarRes = R.drawable.img_streamer_girl,
      role = "Official Live Host",
      flag = "🇧🇩"
    ),
    SwitchableAccount(
      id = "7129481",
      username = "Neon Diva ✨",
      avatarRes = R.drawable.img_profile_max,
      role = "Candidate Host",
      flag = "🇧🇩"
    )
  )

  Dialog(
    onDismissRequest = onDismiss,
    properties = androidx.compose.ui.window.DialogProperties(
      usePlatformDefaultWidth = false,
      decorFitsSystemWindows = false
    )
  ) {
    com.example.ui.components.CenteredModalDialogContainer(onDismiss = onDismiss) {
      Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFF140A28),
        border = BorderStroke(1.4.dp, Color(0xFF00E5FF)),
        modifier = Modifier
          .fillMaxWidth(0.92f)
          .heightIn(max = 520.dp)
      ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Switch Account / Login",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black
          )

          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .background(Color(0x33B026FF))
              .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Filled.Close,
              contentDescription = "Close",
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "Switch between Agency Owner and Host accounts to test isolated profiles, host invitations, and follow systems.",
          color = Color.White.copy(alpha = 0.6f),
          fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
          modifier = Modifier.weight(1f, fill = false),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(accounts) { account ->
            val isCurrent = account.id == currentActiveId || (currentActiveId != "5487293" && currentActiveId != "7129481" && account.id == "100")

            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(if (isCurrent) Color(0xFF2E1354) else Color(0xFF1D0E36))
                .border(
                  1.2.dp,
                  if (isCurrent) Color(0xFF00E5FF) else Color(0x33B026FF),
                  RoundedCornerShape(16.dp)
                )
                .clickable {
                  SocialAgencyStorage.setActiveUserId(context, account.id)
                  onAccountSwitched(account.id)
                  Toast.makeText(context, "Switched to ${account.username}", Toast.LENGTH_SHORT).show()
                  onDismiss()
                }
                .padding(12.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Image(
                  painter = painterResource(id = account.avatarRes),
                  contentDescription = null,
                  modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .border(2.dp, if (isCurrent) Color(0xFF00E5FF) else Color(0xFFFF26B9), CircleShape)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = account.username, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = account.flag, fontSize = 12.sp)
                  }
                  Text(text = "ID: ${account.id}", color = Color(0xFF00E5FF), fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                  Text(text = account.role, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                }

                if (isCurrent) {
                  Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Active",
                    tint = Color(0xFF00E676),
                    modifier = Modifier.size(24.dp)
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = {
            Toast.makeText(context, "Google / Phone login initialized.", Toast.LENGTH_SHORT).show()
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB026FF)),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Log in with Another Account", fontWeight = FontWeight.Bold)
        }
      }
      }
    }
  }
}
