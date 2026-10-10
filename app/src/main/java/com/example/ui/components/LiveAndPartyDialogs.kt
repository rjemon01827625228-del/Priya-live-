package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.aistudio.neonparty.nxvqlr.R
import com.example.ui.storage.SavedAudioRoom
import com.example.ui.storage.UserRoomStorage
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPinkBright

/**
 * 1. Start Video Live Dialog
 * - Allows setting Stream Room Title
 * - Allows picking Cover Picture from phone Gallery via Android Photo Picker
 * - Sets category and launches live stream
 */
@Composable
fun StartVideoLiveDialog(
  onDismiss: () -> Unit,
  onStartLive: (title: String, coverUri: Uri?) -> Unit
) {
  var streamTitle by remember { mutableStateOf("Music & Cyber Vibes Tonight 🔥") }
  var selectedCoverUri by remember { mutableStateOf<Uri?>(null) }
  var selectedCategory by remember { mutableStateOf("Music") }

  // Zero-permission Android Photo Picker for picking cover photo from device gallery
  val galleryLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      selectedCoverUri = uri
    }
  }

  val categories = listOf("Music", "PK Battle", "Chat", "Gaming", "Talent")

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(
      usePlatformDefaultWidth = false,
      decorFitsSystemWindows = false
    )
  ) {
    CenteredModalDialogContainer(onDismiss = onDismiss) {
      Surface(
        modifier = Modifier
          .fillMaxWidth(0.92f)
          .clip(RoundedCornerShape(24.dp))
          .border(
            width = 2.dp,
            brush = Brush.linearGradient(
              listOf(Color(0xFF00E5FF), Color(0xFFBD00FF), Color(0xFFFF007F))
            ),
            shape = RoundedCornerShape(24.dp)
          ),
        color = Color(0xFF0F0024)
      ) {
      Column(
        modifier = Modifier
          .padding(20.dp)
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Top Header with Close
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            NeonCameraIcon(modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "START VIDEO LIVE",
              color = Color.White,
              fontSize = 17.sp,
              fontWeight = FontWeight.Black,
              letterSpacing = 0.5.sp
            )
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Cover Picture Selection Card (Gallery Picker)
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(
              1.6.dp,
              Brush.horizontalGradient(listOf(NeonCyan, NeonPinkBright)),
              RoundedCornerShape(18.dp)
            )
            .background(Color(0xFF1B0038))
            .clickable {
              galleryLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
              )
            },
          contentAlignment = Alignment.Center
        ) {
          if (selectedCoverUri != null) {
            AsyncImage(
              model = selectedCoverUri,
              contentDescription = "Selected Cover",
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop
            )
            // Overlay badge
            Box(
              modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(10.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xCC000000))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(text = "📷 Change Photo", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          } else {
            // Default Cover Placeholder
            Image(
              painter = painterResource(id = R.drawable.img_streamer_girl),
              contentDescription = "Default Cover",
              modifier = Modifier
                .fillMaxSize()
                .background(Color(0x66000000)),
              contentScale = ContentScale.Crop,
              alpha = 0.45f
            )

            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier.padding(16.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(52.dp)
                  .clip(CircleShape)
                  .background(Brush.radialGradient(listOf(Color(0xFF00E5FF), Color(0xFF0052CC)))),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Filled.AddPhotoAlternate,
                  contentDescription = "Pick Cover from Gallery",
                  tint = Color.White,
                  modifier = Modifier.size(28.dp)
                )
              }
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "Tap to Pick Cover from Gallery",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Showcase your best photo for viewers",
                color = Color(0xFFB39DDB),
                fontSize = 11.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Room Title Input Field
        OutlinedTextField(
          value = streamTitle,
          onValueChange = { streamTitle = it },
          label = { Text("Stream Title / Room Name", color = Color(0xFFCE93D8)) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = NeonCyan,
            unfocusedBorderColor = Color(0xFF7C4DFF),
            focusedContainerColor = Color(0xFF1E0042),
            unfocusedContainerColor = Color(0xFF170033)
          )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // "Start Video Live" CTA Button
        Button(
          onClick = {
            val title = streamTitle.ifBlank { "Priya Live Stream" }
            onStartLive(title, selectedCoverUri)
            onDismiss()
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("btn_confirm_start_video_live"),
          colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
          contentPadding = PaddingValues(0.dp)
        ) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .clip(RoundedCornerShape(16.dp))
              .background(
                Brush.horizontalGradient(
                  listOf(Color(0xFF00E5FF), Color(0xFFBD00FF), Color(0xFFFF007F))
                )
              ),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Filled.Videocam, contentDescription = null, tint = Color.White)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "START VIDEO LIVE NOW",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
              )
            }
          }
        }
      }
    }
    }
  }
}

/**
 * 2. Create Audio Party Room Dialog (Shown on first time room creation)
 * - Allows picking room cover image from Gallery
 * - Allows entering Room Name & Country
 * - Automatically saves so user never has to re-enter!
 */
@Composable
fun CreateAudioPartyRoomDialog(
  onDismiss: () -> Unit,
  onCreateRoom: (title: String, coverUri: Uri?, country: String, flagEmoji: String) -> Unit
) {
  val context = LocalContext.current
  var roomTitle by remember { mutableStateOf("Cyber Sound Lounge") }
  var selectedCoverUri by remember { mutableStateOf<Uri?>(null) }
  var selectedCountry by remember { mutableStateOf("Bangladesh") }
  var selectedFlag by remember { mutableStateOf("🇧🇩") }

  val countries = listOf(
    Pair("Bangladesh", "🇧🇩"),
    Pair("Saudi Arabia", "🇸🇦"),
    Pair("UAE", "🇦🇪"),
    Pair("India", "🇮🇳"),
    Pair("Pakistan", "🇵🇰"),
    Pair("United States", "🇺🇸"),
    Pair("Turkey", "🇹🇷")
  )

  // Gallery Picker
  val galleryLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      selectedCoverUri = uri
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(
      usePlatformDefaultWidth = false,
      decorFitsSystemWindows = false
    )
  ) {
    CenteredModalDialogContainer(onDismiss = onDismiss) {
      Surface(
        modifier = Modifier
          .fillMaxWidth(0.92f)
          .clip(RoundedCornerShape(24.dp))
          .border(
            width = 2.dp,
            brush = Brush.linearGradient(
              listOf(Color(0xFFFF007F), Color(0xFFFF5500), Color(0xFF00E5FF))
            ),
            shape = RoundedCornerShape(24.dp)
          ),
        color = Color(0xFF140026)
      ) {
      Column(
        modifier = Modifier
          .padding(20.dp)
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Brush.horizontalGradient(listOf(Color(0xFFFF007F), Color(0xFFFF5500)))),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Filled.Mic, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "CREATE PARTY ROOM",
              color = Color.White,
              fontSize = 17.sp,
              fontWeight = FontWeight.Black,
              letterSpacing = 0.5.sp
            )
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "Set up your room once. Next time, tap 'Party' to enter directly!",
          color = Color(0xFFB39DDB),
          fontSize = 12.sp,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Cover Picture Selection from Gallery
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(
              1.6.dp,
              Brush.horizontalGradient(listOf(Color(0xFFFF007F), Color(0xFFFF8500))),
              RoundedCornerShape(18.dp)
            )
            .background(Color(0xFF22003D))
            .clickable {
              galleryLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
              )
            },
          contentAlignment = Alignment.Center
        ) {
          if (selectedCoverUri != null) {
            AsyncImage(
              model = selectedCoverUri,
              contentDescription = "Party Cover",
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop
            )
            Box(
              modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xCC000000))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(text = "📷 Change Photo", color = NeonGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          } else {
            Image(
              painter = painterResource(id = R.drawable.img_guitarist),
              contentDescription = "Placeholder",
              modifier = Modifier
                .fillMaxSize()
                .background(Color(0x66000000)),
              contentScale = ContentScale.Crop,
              alpha = 0.4f
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Box(
                modifier = Modifier
                  .size(50.dp)
                  .clip(CircleShape)
                  .background(Brush.radialGradient(listOf(Color(0xFFFF007F), Color(0xFFFF5500)))),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Filled.PhotoCamera,
                  contentDescription = "Pick Cover from Gallery",
                  tint = Color.White,
                  modifier = Modifier.size(26.dp)
                )
              }
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "Pick Room Cover from Gallery",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Tap to choose any picture",
                color = Color(0xFFFF80AB),
                fontSize = 11.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Room Title Input
        OutlinedTextField(
          value = roomTitle,
          onValueChange = { roomTitle = it },
          label = { Text("Party Room Name", color = Color(0xFFCE93D8)) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color(0xFFFF007F),
            unfocusedBorderColor = Color(0xFF7C4DFF),
            focusedContainerColor = Color(0xFF1E0042),
            unfocusedContainerColor = Color(0xFF170033)
          )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Create Room Button
        Button(
          onClick = {
            val title = roomTitle.ifBlank { "My Neon Party Room" }
            UserRoomStorage.saveRoom(
              context = context,
              title = title,
              coverUriString = selectedCoverUri?.toString(),
              country = selectedCountry,
              flagEmoji = selectedFlag
            )
            onCreateRoom(title, selectedCoverUri, selectedCountry, selectedFlag)
            onDismiss()
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("btn_confirm_create_party_room"),
          colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
          contentPadding = PaddingValues(0.dp)
        ) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .clip(RoundedCornerShape(16.dp))
              .background(
                Brush.horizontalGradient(
                  listOf(Color(0xFFFF007F), Color(0xFFFF3300), Color(0xFFFF8500))
                )
              ),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "OPEN PARTY ROOM NOW",
              color = Color.White,
              fontSize = 14.sp,
              fontWeight = FontWeight.Black,
              letterSpacing = 0.5.sp
            )
          }
        }
      }
    }
    }
  }
}

/**
 * 3. Room Settings Dialog
 * - Allows changing room title, announcement, and picking a NEW cover from Gallery!
 */
@Composable
fun RoomSettingsDialog(
  currentTitle: String,
  currentCoverUri: String?,
  onDismiss: () -> Unit,
  onSave: (newTitle: String, newCoverUri: Uri?) -> Unit
) {
  val context = LocalContext.current
  var title by remember { mutableStateOf(currentTitle) }
  var pickedUri by remember { mutableStateOf<Uri?>(if (currentCoverUri != null) Uri.parse(currentCoverUri) else null) }

  val galleryLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      pickedUri = uri
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(
      usePlatformDefaultWidth = false,
      decorFitsSystemWindows = false
    )
  ) {
    CenteredModalDialogContainer(onDismiss = onDismiss) {
      Surface(
        modifier = Modifier
          .fillMaxWidth(0.92f)
          .clip(RoundedCornerShape(22.dp))
          .border(1.6.dp, Brush.horizontalGradient(listOf(NeonCyan, NeonPinkBright)), RoundedCornerShape(22.dp)),
        color = Color(0xFF16002E)
      ) {
        Column(
          modifier = Modifier
            .padding(18.dp)
            .verticalScroll(rememberScrollState()),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(text = "⚙️ ROOM SETTINGS", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
          IconButton(onClick = onDismiss) {
            Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Change Cover Picture from Gallery
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, NeonCyan, RoundedCornerShape(14.dp))
            .background(Color(0xFF220042))
            .clickable {
              galleryLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
              )
            },
          contentAlignment = Alignment.Center
        ) {
          if (pickedUri != null) {
            AsyncImage(
              model = pickedUri,
              contentDescription = "New Cover",
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop
            )
            Box(
              modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(6.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xCC000000))
                .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
              Text(text = "📷 Tap to Choose from Gallery", color = NeonCyan, fontSize = 11.sp)
            }
          } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(Icons.Filled.AddPhotoAlternate, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(32.dp))
              Spacer(modifier = Modifier.height(4.dp))
              Text(text = "Change Cover from Gallery", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Room Name", color = Color(0xFFCE93D8)) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = NeonCyan,
            unfocusedBorderColor = Color(0xFF7C4DFF)
          )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = {
            UserRoomStorage.updateRoom(context, title, pickedUri?.toString())
            onSave(title, pickedUri)
            onDismiss()
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
          contentPadding = PaddingValues(0.dp)
        ) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .clip(RoundedCornerShape(12.dp))
              .background(Brush.horizontalGradient(listOf(NeonCyan, NeonPinkBright))),
            contentAlignment = Alignment.Center
          ) {
            Text(text = "Save Changes", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
    }
  }
}

/**
 * 4. Active Audio Party Room Dialog
 * - Includes Room Title, Cover, Settings Option ⚙️ to change info from Gallery!
 * - 8 Mic seats with soundwaves & guest interaction
 * - Chat stream & reactions
 */
@Composable
fun AudioPartyActiveRoomDialog(
  room: SavedAudioRoom,
  onDismiss: () -> Unit
) {
  Box(modifier = Modifier.fillMaxSize()) {
    PartyBoardRoom(
      room = room,
      onDismiss = onDismiss,
      modifier = Modifier.fillMaxSize()
    )
  }
}
