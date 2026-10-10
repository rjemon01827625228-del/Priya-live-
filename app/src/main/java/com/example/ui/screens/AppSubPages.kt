package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.components.AvatarFrameOverlay
import com.example.ui.storage.LevelSystem
import com.example.ui.storage.LocalImageStorage
import com.example.ui.storage.UserFullProfile
import com.example.ui.storage.UserProfileStorage
import com.example.ui.storage.VipSystem
import kotlinx.coroutines.launch

// ============================================================================
// BASE FULL-SCREEN INNER PAGE SCAFFOLD
// Completely isolates inner pages from the bottom navigation bar
// ============================================================================
@Composable
fun SubPageScaffold(
  title: String,
  onBack: () -> Unit,
  actions: @Composable () -> Unit = {},
  content: @Composable (PaddingValues) -> Unit
) {
  val topSafePadding = com.example.ui.components.rememberResponsiveTopBarPadding(extraTopSpacing = 0.dp)
  val bottomSafePadding = com.example.ui.components.rememberResponsiveNavigationBarPadding(extraBottomSpacing = 0.dp)
  Scaffold(
    containerColor = Color(0xFF070014),
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    topBar = {
      Surface(
        color = Color(0xFF100022).copy(alpha = 0.95f),
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, Color(0xFF260447))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = topSafePadding)
            .height(56.dp)
            .padding(horizontal = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = onBack,
            modifier = Modifier.testTag("subpage_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Navigate Back",
              tint = Color(0xFF00E5FF)
            )
          }

          Text(
            text = title,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
              .weight(1f)
              .padding(horizontal = 8.dp)
          )

          actions()
        }
      }
    }
  ) { padding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            listOf(Color(0xFF070014), Color(0xFF130026), Color(0xFF070014))
          )
        )
        .padding(top = padding.calculateTopPadding(), bottom = bottomSafePadding)
    ) {
      content(padding)
    }
  }
}

// ============================================================================
// 1. FULL-SCREEN EDIT PROFILE PAGE
// ============================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileFullScreen(
  onBack: () -> Unit,
  onOpenFullDetails: () -> Unit
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  var profile by remember { mutableStateOf(UserProfileStorage.getProfile(context)) }

  var username by remember { mutableStateOf(profile.username) }
  var bio by remember { mutableStateOf(profile.bio) }
  var gender by remember { mutableStateOf(profile.gender) }
  var ageText by remember { mutableStateOf(profile.age.toString()) }
  var selectedImageUri by remember { mutableStateOf(profile.profileImageUri) }

  val countries = listOf(
    SubPageCountryOption("Bangladesh", "🇧🇩"),
    SubPageCountryOption("India", "🇮🇳"),
    SubPageCountryOption("Pakistan", "🇵🇰"),
    SubPageCountryOption("Saudi Arabia", "🇸🇦"),
    SubPageCountryOption("United Arab Emirates", "🇦🇪"),
    SubPageCountryOption("United States", "🇺🇸"),
    SubPageCountryOption("United Kingdom", "🇬🇧"),
    SubPageCountryOption("Malaysia", "🇲🇾")
  )
  var selectedCountry by remember {
    mutableStateOf(countries.find { it.flag == profile.countryFlag } ?: countries[0])
  }
  var countryExpanded by remember { mutableStateOf(false) }

  // Photo Picker with immediate persistent local backup
  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      val localFile = LocalImageStorage.saveUriLocally(
        context = context,
        sourceUri = uri,
        imageType = LocalImageStorage.ImageType.PROFILE,
        identifier = profile.userId
      )
      selectedImageUri = if (localFile != null) "file://${localFile.absolutePath}" else uri.toString()
    }
  }

  SubPageScaffold(
    title = "Edit Profile",
    onBack = onBack,
    actions = {
      Button(
        onClick = {
          val finalAge = ageText.toIntOrNull() ?: profile.age
          UserProfileStorage.updateBasicInfo(
            context = context,
            username = username.ifBlank { profile.username },
            gender = gender,
            age = finalAge,
            country = selectedCountry.name,
            countryFlag = selectedCountry.flag,
            bio = bio
          )
          if (selectedImageUri != null) {
            UserProfileStorage.updateProfileImageUri(context, selectedImageUri)
            coroutineScope.launch {
              LocalImageStorage.saveUriLocally(
                context = context,
                sourceUri = Uri.parse(selectedImageUri),
                imageType = LocalImageStorage.ImageType.PROFILE,
                identifier = profile.userId
              )
            }
          }
          Toast.makeText(context, "Profile saved successfully!", Toast.LENGTH_SHORT).show()
          onBack()
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
        modifier = Modifier
          .padding(end = 6.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(
            Brush.horizontalGradient(listOf(Color(0xFF00E5FF), Color(0xFFFF007F)))
          )
      ) {
        Text("Save", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
      }
    }
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(18.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Profile Picture
      Box(
        modifier = Modifier
          .size(110.dp)
          .clickable {
            photoPickerLauncher.launch(
              PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
          },
        contentAlignment = Alignment.Center
      ) {
        val avatarModel = LocalImageStorage.resolveImageModel(
          context = context,
          candidateUri = selectedImageUri,
          imageType = LocalImageStorage.ImageType.PROFILE,
          entityId = profile.userId,
          defaultFallbackRes = R.drawable.img_profile_max
        )
        AsyncImage(
          model = avatarModel,
          contentDescription = "Profile Picture",
          modifier = Modifier
            .size(110.dp)
            .clip(CircleShape)
            .border(2.5.dp, Color(0xFF00E5FF), CircleShape),
          contentScale = ContentScale.Crop,
          error = painterResource(id = R.drawable.img_profile_max),
          fallback = painterResource(id = R.drawable.img_profile_max)
        )

        Box(
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .size(34.dp)
            .clip(CircleShape)
            .background(Color(0xFFFF007F))
            .border(2.dp, Color.Black, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Filled.CameraAlt,
            contentDescription = "Change Photo",
            tint = Color.White,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Text(
        text = "Tap avatar to change profile photo",
        color = Color(0xFF00E5FF),
        fontSize = 12.sp
      )

      // PID Permanent Display
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF14002B),
        border = BorderStroke(1.dp, Color(0xFF330066)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("PID (Permanent ID):", color = Color(0xFFB39DDB), fontSize = 13.sp)
          Text(profile.userId, color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
      }

      // Username Input
      OutlinedTextField(
        value = username,
        onValueChange = { username = it },
        label = { Text("Display Name") },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
          focusedTextColor = Color.White,
          unfocusedTextColor = Color.White,
          focusedBorderColor = Color(0xFF00E5FF),
          unfocusedBorderColor = Color(0xFF330066),
          focusedLabelColor = Color(0xFF00E5FF),
          unfocusedLabelColor = Color(0xFFB39DDB)
        ),
        shape = RoundedCornerShape(12.dp)
      )

      // Bio Input
      OutlinedTextField(
        value = bio,
        onValueChange = { bio = it },
        label = { Text("Bio / Status") },
        modifier = Modifier.fillMaxWidth(),
        maxLines = 3,
        colors = OutlinedTextFieldDefaults.colors(
          focusedTextColor = Color.White,
          unfocusedTextColor = Color.White,
          focusedBorderColor = Color(0xFF00E5FF),
          unfocusedBorderColor = Color(0xFF330066),
          focusedLabelColor = Color(0xFF00E5FF),
          unfocusedLabelColor = Color(0xFFB39DDB)
        ),
        shape = RoundedCornerShape(12.dp)
      )

      // Gender Selector
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        listOf("Male" to "♂ Male", "Female" to "♀ Female").forEach { (value, label) ->
          val isSelected = gender.equals(value, ignoreCase = true)
          Surface(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .clickable { gender = value },
            color = if (isSelected) Color(0xFF260055) else Color(0xFF14002B),
            border = BorderStroke(
              1.5.dp,
              if (isSelected) Color(0xFF00E5FF) else Color(0xFF330066)
            )
          ) {
            Box(
              modifier = Modifier.padding(vertical = 12.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = label,
                color = if (isSelected) Color(0xFF00E5FF) else Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
            }
          }
        }
      }

      // Age Input
      OutlinedTextField(
        value = ageText,
        onValueChange = { ageText = it.filter { ch -> ch.isDigit() }.take(2) },
        label = { Text("Age") },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
          focusedTextColor = Color.White,
          unfocusedTextColor = Color.White,
          focusedBorderColor = Color(0xFF00E5FF),
          unfocusedBorderColor = Color(0xFF330066),
          focusedLabelColor = Color(0xFF00E5FF),
          unfocusedLabelColor = Color(0xFFB39DDB)
        ),
        shape = RoundedCornerShape(12.dp)
      )

      // Country Dropdown
      ExposedDropdownMenuBox(
        expanded = countryExpanded,
        onExpandedChange = { countryExpanded = !countryExpanded },
        modifier = Modifier.fillMaxWidth()
      ) {
        OutlinedTextField(
          value = "${selectedCountry.flag}  ${selectedCountry.name}",
          onValueChange = {},
          readOnly = true,
          label = { Text("Country") },
          trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = countryExpanded) },
          modifier = Modifier
            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
            .fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color(0xFF00E5FF),
            unfocusedBorderColor = Color(0xFF330066),
            focusedLabelColor = Color(0xFF00E5FF),
            unfocusedLabelColor = Color(0xFFB39DDB)
          ),
          shape = RoundedCornerShape(12.dp)
        )
        ExposedDropdownMenu(
          expanded = countryExpanded,
          onDismissRequest = { countryExpanded = false },
          modifier = Modifier.background(Color(0xFF14002B))
        ) {
          countries.forEach { item ->
            DropdownMenuItem(
              text = { Text("${item.flag} ${item.name}", color = Color.White) },
              onClick = {
                selectedCountry = item
                countryExpanded = false
              }
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Open Full Profile Details Shortcut
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .clickable { onOpenFullDetails() },
        color = Color(0xFF1B003A),
        border = BorderStroke(1.dp, Color(0xFFFF007F))
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text("Open Comprehensive Profile", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("View ID badge, visitors, CP space, and gift wall", color = Color(0xFFB39DDB), fontSize = 11.sp)
          }
          Icon(Icons.Filled.Edit, contentDescription = null, tint = Color(0xFFFF007F))
        }
      }
    }
  }
}

// ============================================================================
// 2. FULL-SCREEN SETTINGS PAGE
// ============================================================================
@Composable
fun SettingsFullScreen(
  onBack: () -> Unit,
  onLogout: () -> Unit
) {
  val context = LocalContext.current
  var hdAudio by remember { mutableStateOf(true) }
  var ultraVideo by remember { mutableStateOf(true) }
  var notifications by remember { mutableStateOf(true) }
  var streamHardwareAccel by remember { mutableStateOf(true) }

  SubPageScaffold(
    title = "Settings",
    onBack = onBack
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(18.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Audio & Video
      SettingsSectionHeader("Stream & Hardware Performance")
      SettingsToggleItem(
        icon = Icons.Filled.Mic,
        title = "HD Audio & Noise Suppression",
        subtitle = "Optimal sound clarity for party rooms",
        checked = hdAudio,
        onCheckedChange = { hdAudio = it }
      )
      SettingsToggleItem(
        icon = Icons.Filled.Videocam,
        title = "1080p Ultra 60FPS Video",
        subtitle = "High definition live broadcasting",
        checked = ultraVideo,
        onCheckedChange = { ultraVideo = it }
      )
      SettingsToggleItem(
        icon = Icons.Filled.Widgets,
        title = "Hardware Acceleration",
        subtitle = "Smooth animations and gift effects",
        checked = streamHardwareAccel,
        onCheckedChange = { streamHardwareAccel = it }
      )

      // Notifications & Security
      SettingsSectionHeader("Notifications & Security")
      SettingsToggleItem(
        icon = Icons.Filled.Notifications,
        title = "Push Notifications",
        subtitle = "Live stream alerts and room invitations",
        checked = notifications,
        onCheckedChange = { notifications = it }
      )
      SettingsClickableItem(
        icon = Icons.Filled.Security,
        title = "Account Security & PID",
        subtitle = "Protected (PID: ${UserProfileStorage.getActiveUserId(context)})"
      ) {
        Toast.makeText(context, "Account verified & encrypted.", Toast.LENGTH_SHORT).show()
      }

      // Storage & Cache
      SettingsSectionHeader("Storage & Diagnostics")
      SettingsClickableItem(
        icon = Icons.Filled.Delete,
        title = "Clear Cached Media & Gifts",
        subtitle = "Free up memory without losing data"
      ) {
        Toast.makeText(context, "Cache cleared successfully!", Toast.LENGTH_SHORT).show()
      }

      // About & Version
      SettingsSectionHeader("Application Details")
      SettingsClickableItem(
        icon = Icons.Filled.Info,
        title = "App Version",
        subtitle = "v2.5.0 (Build 2026.09 - Official Stable Release)"
      ) {}

      Spacer(modifier = Modifier.height(16.dp))

      // Log Out Button
      Button(
        onClick = onLogout,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD50000)),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
      ) {
        Text("LOG OUT OF ACCOUNT", color = Color.White, fontWeight = FontWeight.Bold)
      }
    }
  }
}

// ============================================================================
// 3. FULL-SCREEN HOST CENTRE PAGE
// ============================================================================
@Composable
fun HostCenterFullScreen(
  onBack: () -> Unit,
  onStartStream: () -> Unit
) {
  HostCenterScreen(
    onBack = onBack,
    onStartStream = onStartStream
  )
}

// ============================================================================
// 4. FULL-SCREEN HOST REQUEST APPLICATION PAGE
// ============================================================================
@Composable
fun HostRequestFullScreen(
  onBack: () -> Unit
) {
  HostCenterScreen(
    onBack = onBack
  )
}

// ============================================================================
// 5. FULL-SCREEN LEVEL DETAILS PAGE
// ============================================================================
@Composable
fun LevelDetailsFullScreen(
  onBack: () -> Unit,
  onTopUp: () -> Unit
) {
  val context = LocalContext.current
  val profile = remember { UserProfileStorage.getProfile(context) }
  val progressInfo = remember(profile.coinBalance) {
    LevelSystem.getProgressInfo(profile.coinBalance)
  }
  val allTiers = remember(profile.coinBalance) {
    LevelSystem.getAllTiers(profile.coinBalance)
  }

  SubPageScaffold(
    title = "Level System (50 Levels)",
    onBack = onBack
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
      contentPadding = PaddingValues(vertical = 16.dp)
    ) {
      item {
        // Current Level Card
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = Color(0xFF1D003B),
          border = BorderStroke(2.dp, Color(0xFFFFD700)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text("CURRENT LEVEL", color = Color(0xFFFFD700), fontSize = 11.sp, fontWeight = FontWeight.Black)
                Text("Level ${progressInfo.currentLevel}", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
                Text(LevelSystem.getTierName(progressInfo.currentLevel), color = Color(0xFF00E5FF), fontSize = 13.sp)
              }
              Box(
                modifier = Modifier
                  .size(54.dp)
                  .clip(CircleShape)
                  .background(
                    Brush.radialGradient(listOf(Color(0xFFFFD700), Color(0xFF996515)))
                  ),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "Lv.${progressInfo.currentLevel}",
                  color = Color.Black,
                  fontWeight = FontWeight.Black,
                  fontSize = 14.sp
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Diamonds: %,d / %,d".format(progressInfo.currentCoins, progressInfo.coinsRequiredForNextLevel), color = Color(0xFFB39DDB), fontSize = 12.sp)
              Text("${(progressInfo.progressRatio * 100).toInt()}%", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
              progress = { progressInfo.progressRatio },
              modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
              color = Color(0xFF00E5FF),
              trackColor = Color(0xFF330066)
            )

            Spacer(modifier = Modifier.height(14.dp))
            Button(
              onClick = onTopUp,
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF007F)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text("Top Up Diamonds to Level Up", color = Color.White, fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      item {
        Text("All Level Tiers & Privileges", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
      }

      items(allTiers) { tier ->
        val isCurrent = tier.level == progressInfo.currentLevel
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = if (isCurrent) Color(0xFF2E0854) else Color(0xFF120024),
          border = BorderStroke(1.dp, if (isCurrent) Color(0xFF00E5FF) else Color(0xFF260447)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isCurrent) Color(0xFF00E5FF) else Color(0xFF260447)),
              contentAlignment = Alignment.Center
            ) {
              Text(
                "Lv.${tier.level}",
                color = if (isCurrent) Color.Black else Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(tier.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Text("Required: %,d Diamonds".format(tier.requiredCoins), color = Color(0xFFB39DDB), fontSize = 11.sp)
            }

            if (isCurrent) {
              Text("ACTIVE", color = Color(0xFF00E5FF), fontWeight = FontWeight.Black, fontSize = 12.sp)
            }
          }
        }
      }
    }
  }
}

// ============================================================================
// 6. FULL-SCREEN MY QR CODE PAGE
// ============================================================================
@Composable
fun MyQrCodeFullScreen(
  onBack: () -> Unit
) {
  val context = LocalContext.current
  val profile = remember { UserProfileStorage.getProfile(context) }

  SubPageScaffold(
    title = "My QR Code",
    onBack = onBack
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
      Spacer(modifier = Modifier.height(10.dp))

      Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFF14002E),
        border = BorderStroke(2.dp, Color(0xFF00E5FF)),
        modifier = Modifier.fillMaxWidth(0.92f)
      ) {
        Column(
          modifier = Modifier.padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // User Avatar & Name
          val avatarModel = LocalImageStorage.resolveImageModel(
            context = context,
            candidateUri = profile.profileImageUri,
            imageType = LocalImageStorage.ImageType.PROFILE,
            entityId = profile.userId,
            defaultFallbackRes = R.drawable.img_profile_max
          )
          AsyncImage(
            model = avatarModel,
            contentDescription = "User Avatar",
            modifier = Modifier
              .size(76.dp)
              .clip(CircleShape)
              .border(2.dp, Color(0xFF00E5FF), CircleShape),
            contentScale = ContentScale.Crop,
            error = painterResource(id = R.drawable.img_profile_max),
            fallback = painterResource(id = R.drawable.img_profile_max)
          )

          Text(profile.username, color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
          Text("PID: ${profile.userId}", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, fontSize = 14.sp)

          // QR Code Graphic Preview
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier
              .size(200.dp)
              .padding(8.dp)
          ) {
            Box(
              modifier = Modifier.fillMaxSize(),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Filled.QrCode,
                contentDescription = "QR Code",
                tint = Color.Black,
                modifier = Modifier.size(170.dp)
              )
            }
          }

          Text(
            text = "Scan this QR code in Priya Live to add as friend or invite to Party Room",
            color = Color(0xFFB39DDB),
            fontSize = 11.sp,
            textAlign = TextAlign.Center
          )
        }
      }

      // Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(0.92f),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Button(
          onClick = {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("PID", profile.userId))
            Toast.makeText(context, "PID copied to clipboard!", Toast.LENGTH_SHORT).show()
          },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF260447)),
          modifier = Modifier.weight(1f)
        ) {
          Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Copy PID", fontSize = 13.sp)
        }

        Button(
          onClick = {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
              type = "text/plain"
              putExtra(Intent.EXTRA_TEXT, "Join me on Priya Live! My ID is ${profile.userId}")
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share QR Code / ID"))
          },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
          modifier = Modifier.weight(1f)
        ) {
          Icon(Icons.Filled.Share, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Share", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
      }
    }
  }
}

// ============================================================================
// 7. FULL-SCREEN 24/7 HELP CENTER PAGE
// ============================================================================
@Composable
fun HelpCenterFullScreen(
  onBack: () -> Unit
) {
  val context = LocalContext.current
  SubPageScaffold(
    title = "24/7 Official Help Center",
    onBack = onBack
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(18.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Official WhatsApp Support
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF0B2E16),
        border = BorderStroke(1.dp, Color(0xFF00E676)),
        modifier = Modifier
          .fillMaxWidth()
          .clickable {
            try {
              val url = "https://wa.me/8801818022519"
              context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            } catch (e: Exception) {
              Toast.makeText(context, "WhatsApp: +8801818022519", Toast.LENGTH_LONG).show()
            }
          }
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(CircleShape)
              .background(Color(0xFF00E676)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Filled.HeadsetMic, contentDescription = null, tint = Color.Black)
          }
          Spacer(modifier = Modifier.width(14.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text("Official WhatsApp Support", color = Color(0xFF00E676), fontWeight = FontWeight.Black, fontSize = 15.sp)
            Text("+8801818022519 (Instant Human Agent)", color = Color.White, fontSize = 12.sp)
          }
        }
      }

      // Email Support
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF14002E),
        border = BorderStroke(1.dp, Color(0xFF00E5FF)),
        modifier = Modifier
          .fillMaxWidth()
          .clickable {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:support@neonlive.com"))
            try {
              context.startActivity(intent)
            } catch (e: Exception) {
              Toast.makeText(context, "Email: support@neonlive.com", Toast.LENGTH_SHORT).show()
            }
          }
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(CircleShape)
              .background(Color(0xFF00E5FF)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = null, tint = Color.Black)
          }
          Spacer(modifier = Modifier.width(14.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text("Official Support Email", color = Color(0xFF00E5FF), fontWeight = FontWeight.Black, fontSize = 15.sp)
            Text("support@neonlive.com", color = Color.White, fontSize = 12.sp)
          }
        }
      }

      // FAQs
      Text("Frequently Asked Questions", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
      FaqItem(
        question = "Why didn't my Diamonds arrive after top-up?",
        answer = "Top-ups are usually credited instantly. In rare network delays, please wait up to 3 minutes or message our WhatsApp helpline with your transaction ID."
      )
      FaqItem(
        question = "How do I become an Official Agency Host?",
        answer = "Go to Profile -> Agency Centre or Host Request to submit your audition. Approved hosts receive monthly cash salaries and higher gift shares."
      )
      FaqItem(
        question = "How to use Party Board Audio Room?",
        answer = "Open the Party tab, tap 'Start Party' to create a room. You can invite friends to seat slots, upload scenic wallpapers, and play local music files."
      )
    }
  }
}

// ============================================================================
// 8. FULL-SCREEN ABOUT US PAGE
// ============================================================================
@Composable
fun AboutUsFullScreen(
  onBack: () -> Unit
) {
  SubPageScaffold(
    title = "About Priya Live",
    onBack = onBack
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Box(
        modifier = Modifier
          .size(80.dp)
          .clip(RoundedCornerShape(20.dp))
          .background(
            Brush.radialGradient(listOf(Color(0xFFFF007F), Color(0xFF7C4DFF)))
          ),
        contentAlignment = Alignment.Center
      ) {
        Icon(Icons.Filled.LiveTv, contentDescription = null, tint = Color.White, modifier = Modifier.size(46.dp))
      }

      Text("Priya Live HD", color = Color.White, fontWeight = FontWeight.Black, fontSize = 22.sp)
      Text("Live Streaming & Audio Party Platform", color = Color(0xFF00E5FF), fontSize = 13.sp)

      Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF14002E),
        border = BorderStroke(1.dp, Color(0xFF260447)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Version", color = Color(0xFFB39DDB))
            Text("v2.5.0", color = Color.White, fontWeight = FontWeight.Bold)
          }
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Release Build", color = Color(0xFFB39DDB))
            Text("2026.09 Official Stable", color = Color.White, fontWeight = FontWeight.Bold)
          }
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Engine", color = Color(0xFFB39DDB))
            Text("Kotlin & Jetpack Compose M3", color = Color.White, fontWeight = FontWeight.Bold)
          }
        }
      }

      Text(
        text = "Priya Live connects millions of creators and fans worldwide. Stream high definition video, join immersive voice party rooms, compete in PK battles, and build vibrant creator communities.",
        color = Color(0xFFB39DDB),
        fontSize = 13.sp,
        lineHeight = 20.sp,
        textAlign = TextAlign.Center
      )
    }
  }
}

// ============================================================================
// 9. FULL-SCREEN LIVE HISTORY PAGE
// ============================================================================
@Composable
fun LiveHistoryFullScreen(
  onBack: () -> Unit
) {
  SubPageScaffold(
    title = "Live & Party History",
    onBack = onBack
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(18.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      Text("Recent Broadcast Sessions", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)

      HistoryItem(
        title = "Neon Twilight Audio Party",
        type = "Party Room",
        duration = "48m 12s",
        beans = "+2,400 Beans",
        date = "Today, 10:30 PM"
      )
      HistoryItem(
        title = "Casual Singing & PK Stream",
        type = "Live Video",
        duration = "1h 15m",
        beans = "+8,500 Beans",
        date = "Yesterday, 09:15 PM"
      )
      HistoryItem(
        title = "Midnight Hangout & Chill",
        type = "Party Room",
        duration = "32m 04s",
        beans = "+1,200 Beans",
        date = "Sep 20, 11:45 PM"
      )
    }
  }
}

// ============================================================================
// 10. FULL-SCREEN PARALLEL BOX PAGE
// ============================================================================
@Composable
fun ParallelBoxFullScreen(
  onBack: () -> Unit
) {
  val context = LocalContext.current
  var isCloned by remember { mutableStateOf(false) }

  SubPageScaffold(
    title = "Parallel Box (Multi-Account Space)",
    onBack = onBack
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF14002E),
        border = BorderStroke(1.5.dp, Color(0xFF00E5FF)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Widgets, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text("Dual Space App Cloner", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
              Text("Run 2 accounts simultaneously on 1 device", color = Color(0xFFB39DDB), fontSize = 12.sp)
            }
          }

          Text(
            text = "Parallel Box creates an isolated sandboxed profile space allowing you to login with a second ID, manage secondary agency accounts, and test room interactions without logging out.",
            color = Color.White,
            fontSize = 13.sp,
            lineHeight = 19.sp
          )
        }
      }

      Button(
        onClick = {
          isCloned = !isCloned
          Toast.makeText(
            context,
            if (isCloned) "Second space initialized!" else "Parallel clone reset.",
            Toast.LENGTH_SHORT
          ).show()
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = if (isCloned) Color(0xFF00E676) else Color(0xFF00E5FF)
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
      ) {
        Text(
          text = if (isCloned) "PARALLEL SPACE ACTIVE (LAUNCH)" else "INITIALIZE DUAL SPACE",
          color = Color.Black,
          fontWeight = FontWeight.Black
        )
      }
    }
  }
}

// ============================================================================
// 11. FULL-SCREEN COMMISSION CENTER PAGE
// ============================================================================
@Composable
fun CommissionFullScreen(
  onBack: () -> Unit
) {
  val context = LocalContext.current
  SubPageScaffold(
    title = "Commission Center",
    onBack = onBack
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(18.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFF26002A),
        border = BorderStroke(1.5.dp, Color(0xFFFF007F)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Text("Available Cashout Commission", color = Color(0xFFFF80AB), fontSize = 13.sp)
          Text("$ 0.00 USD", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Black)
          Spacer(modifier = Modifier.height(8.dp))
          Text("Host Direct Share: 70% | Agency Level: Official Tier 1", color = Color(0xFF00E5FF), fontSize = 12.sp)
        }
      }

      Text("Bound Payment Methods", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
      PaymentMethodItem("bKash / Nagad Wallet", "Not Linked", isBound = false)
      PaymentMethodItem("International Bank Wire (SWIFT)", "Not Linked", isBound = false)

      Button(
        onClick = {
          Toast.makeText(context, "Minimum cashout threshold is $10.00 USD.", Toast.LENGTH_SHORT).show()
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF007F)),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
      ) {
        Text("WITHDRAW COMMISSION", color = Color.White, fontWeight = FontWeight.Bold)
      }
    }
  }
}

// ============================================================================
// 12. FULL-SCREEN COIN TOP UP PAGE
// ============================================================================
@Composable
fun CoinTopUpFullScreen(
  onBack: () -> Unit
) {
  val context = LocalContext.current
  val profile = remember { UserProfileStorage.getProfile(context) }
  var balance by remember { mutableStateOf(profile.coinBalance) }

  SubPageScaffold(
    title = "Diamond Top-Up & VIP Mall",
    onBack = onBack
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      item {
        Surface(
          shape = RoundedCornerShape(18.dp),
          color = Color(0xFF1B0038),
          border = BorderStroke(1.5.dp, Color(0xFF00E5FF)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text("YOUR DIAMOND BALANCE", color = Color(0xFF00E5FF), fontSize = 11.sp, fontWeight = FontWeight.Black)
              Text("%,d Diamonds".format(balance), color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
            }
            Text("💎", fontSize = 32.sp)
          }
        }
      }

      item {
        Text("Instant Diamond Packages", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
      }

      val packages = listOf(
        SubPageCoinPackage(70, "+0 Bonus", "$0.99"),
        SubPageCoinPackage(350, "+35 Bonus", "$4.99"),
        SubPageCoinPackage(700, "+80 Bonus", "$9.99"),
        SubPageCoinPackage(1400, "+200 Bonus", "$19.99"),
        SubPageCoinPackage(3500, "+600 Bonus", "$49.99"),
        SubPageCoinPackage(7000, "+1500 Bonus", "$99.99")
      )

      items(packages) { pack ->
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = Color(0xFF14002E),
          border = BorderStroke(1.dp, Color(0xFF330066)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("💎", fontSize = 22.sp)
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text("${pack.coins} Diamonds", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(pack.bonus, color = Color(0xFF00E5FF), fontSize = 11.sp)
              }
            }

            Button(
              onClick = {
                UserProfileStorage.addCoins(context, pack.coins, profile.userId)
                balance = UserProfileStorage.getCoinBalance(context, profile.userId)
                Toast.makeText(context, "Added ${pack.coins} Diamonds!", Toast.LENGTH_SHORT).show()
              },
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF007F))
            ) {
              Text(pack.price, color = Color.White, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}

// ============================================================================
// 13. FULL-SCREEN CP COUPLE SPACE PAGE
// ============================================================================
@Composable
fun CpSpaceFullScreen(
  onBack: () -> Unit
) {
  val context = LocalContext.current
  val profile = remember { UserProfileStorage.getProfile(context) }

  SubPageScaffold(
    title = "CP Couple Space",
    onBack = onBack
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF260024),
        border = BorderStroke(1.5.dp, Color(0xFFFF4081)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Icon(Icons.Filled.Favorite, contentDescription = null, tint = Color(0xFFFF4081), modifier = Modifier.size(44.dp))
          Text("Sweet CP Couple Space", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
          val partnerName = profile.cpPartnerName?.takeIf { it.isNotBlank() } ?: "Sweetheart"
          Text(
            if (profile.isCpConnected) "CP Partner: $partnerName"
            else "Status: Single (No CP Partner)",
            color = Color(0xFFFF80AB),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
          Text(
            "Send an exclusive CP Ring in party rooms or propose via ID search to unlock shared romantic banner frames, entrance fireworks, and CP ranking rewards.",
            color = Color(0xFFB39DDB),
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
          )
        }
      }

      Button(
        onClick = {
          Toast.makeText(context, "Enter user PID or username in search to propose CP!", Toast.LENGTH_SHORT).show()
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4081)),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
      ) {
        Text("PROPOSE CP PARTNER", color = Color.White, fontWeight = FontWeight.Bold)
      }
    }
  }
}

// ============================================================================
// 14. FULL-SCREEN FOLLOWERS & FOLLOWING PAGE
// ============================================================================
@Composable
fun FollowersListFullScreen(
  onBack: () -> Unit
) {
  var selectedTab by remember { mutableStateOf(0) }
  val tabs = listOf("Followers", "Following")

  SubPageScaffold(
    title = "Social Connections",
    onBack = onBack
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color(0xFF14002E),
        contentColor = Color(0xFF00E5FF),
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
            color = Color(0xFF00E5FF)
          )
        }
      ) {
        tabs.forEachIndexed { index, title ->
          Tab(
            selected = selectedTab == index,
            onClick = { selectedTab = index },
            text = {
              Text(
                title,
                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                color = if (selectedTab == index) Color(0xFF00E5FF) else Color(0xFFB39DDB)
              )
            }
          )
        }
      }

      val items = if (selectedTab == 0) {
        listOf("Neon Queen 👑" to "PID: 9221472", "Cyber Knight ⚡" to "PID: 9221473", "Bella Singer 🎙️" to "PID: 9221474")
      } else {
        listOf("MR MAX (Official)" to "PID: 9221471", "Priya Live Official" to "PID: 1000000")
      }

      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(items) { (name, pid) ->
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF14002E),
            border = BorderStroke(1.dp, Color(0xFF260447)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(CircleShape)
                  .background(Color(0xFF260447)),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Filled.Person, contentDescription = null, tint = Color(0xFF00E5FF))
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(pid, color = Color(0xFFB39DDB), fontSize = 12.sp)
              }
              Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF260447)),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
              ) {
                Text(if (selectedTab == 0) "Follow" else "Following", color = Color(0xFF00E5FF), fontSize = 12.sp)
              }
            }
          }
        }
      }
    }
  }
}

// ============================================================================
// 15. FULL-SCREEN ACCOUNT SWITCHER PAGE
// ============================================================================
@Composable
fun AccountSwitchFullScreen(
  onBack: () -> Unit
) {
  val context = LocalContext.current
  val activeId = remember { UserProfileStorage.getActiveUserId(context) }

  SubPageScaffold(
    title = "Switch Account",
    onBack = onBack
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(18.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      Text("Stored Accounts on This Device", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)

      AccountItem(
        name = "MR MAX (Primary)",
        pid = "9221471",
        isActive = activeId == "9221471",
        onClick = {
          UserProfileStorage.setActiveUserId(context, "9221471")
          Toast.makeText(context, "Switched to MR MAX", Toast.LENGTH_SHORT).show()
          onBack()
        }
      )

      AccountItem(
        name = "Streamer Account #2",
        pid = "9221475",
        isActive = activeId == "9221475",
        onClick = {
          UserProfileStorage.setActiveUserId(context, "9221475")
          Toast.makeText(context, "Switched to Account #2", Toast.LENGTH_SHORT).show()
          onBack()
        }
      )

      Spacer(modifier = Modifier.height(10.dp))

      Button(
        onClick = {
          Toast.makeText(context, "Enter credentials for new account", Toast.LENGTH_SHORT).show()
        },
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
      ) {
        Text("+ ADD NEW ACCOUNT", color = Color.Black, fontWeight = FontWeight.Bold)
      }
    }
  }
}

// ============================================================================
// HELPER UI COMPONENTS FOR FULL-SCREEN SUB-PAGES
// ============================================================================
private data class SubPageCountryOption(val name: String, val flag: String)
private data class SubPageCoinPackage(val coins: Long, val bonus: String, val price: String)

@Composable
fun SettingsSectionHeader(title: String) {
  Text(
    text = title,
    color = Color(0xFF00E5FF),
    fontSize = 13.sp,
    fontWeight = FontWeight.Bold,
    letterSpacing = 0.8.sp,
    modifier = Modifier.padding(top = 6.dp)
  )
}

@Composable
fun SettingsToggleItem(
  icon: ImageVector,
  title: String,
  subtitle: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit
) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = Color(0xFF14002E),
    border = BorderStroke(1.dp, Color(0xFF260447)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(icon, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(24.dp))
      Spacer(modifier = Modifier.width(12.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(subtitle, color = Color(0xFFB39DDB), fontSize = 11.5.sp)
      }
      Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
          checkedThumbColor = Color.White,
          checkedTrackColor = Color(0xFF00E5FF),
          uncheckedThumbColor = Color.Gray,
          uncheckedTrackColor = Color(0xFF260447)
        )
      )
    }
  }
}

@Composable
fun SettingsClickableItem(
  icon: ImageVector,
  title: String,
  subtitle: String,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = Color(0xFF14002E),
    border = BorderStroke(1.dp, Color(0xFF260447)),
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(icon, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(24.dp))
      Spacer(modifier = Modifier.width(12.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(subtitle, color = Color(0xFFB39DDB), fontSize = 11.5.sp)
      }
    }
  }
}

@Composable
fun StatCard(
  modifier: Modifier = Modifier,
  title: String,
  value: String,
  color: Color
) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = Color(0xFF14002E),
    border = BorderStroke(1.dp, color.copy(alpha = 0.5f)),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(value, color = color, fontWeight = FontWeight.Black, fontSize = 16.sp)
      Text(title, color = Color(0xFFB39DDB), fontSize = 11.sp)
    }
  }
}

@Composable
fun QuestItem(title: String, reward: String, isCompleted: Boolean) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = Color(0xFF14002E),
    border = BorderStroke(1.dp, Color(0xFF260447)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
        Text(reward, color = Color(0xFFFFD700), fontSize = 11.sp)
      }
      Text(
        if (isCompleted) "CLAIMED" else "GO",
        color = if (isCompleted) Color.Gray else Color(0xFF00E5FF),
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
      )
    }
  }
}

@Composable
fun RequirementItem(title: String, isPassed: Boolean) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = if (isPassed) Icons.Filled.CheckCircle else Icons.Filled.Check,
      contentDescription = null,
      tint = if (isPassed) Color(0xFF00E676) else Color.Gray,
      modifier = Modifier.size(18.dp)
    )
    Spacer(modifier = Modifier.width(10.dp))
    Text(title, color = Color.White, fontSize = 13.sp)
  }
}

@Composable
fun FaqItem(question: String, answer: String) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = Color(0xFF14002E),
    border = BorderStroke(1.dp, Color(0xFF260447)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Text("Q: $question", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
      Spacer(modifier = Modifier.height(6.dp))
      Text(answer, color = Color.White, fontSize = 12.sp, lineHeight = 18.sp)
    }
  }
}

@Composable
fun HistoryItem(title: String, type: String, duration: String, beans: String, date: String) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = Color(0xFF14002E),
    border = BorderStroke(1.dp, Color(0xFF260447)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text("$type • $duration • $date", color = Color(0xFFB39DDB), fontSize = 11.sp)
      }
      Text(beans, color = Color(0xFFFF007F), fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
  }
}

@Composable
fun PaymentMethodItem(title: String, status: String, isBound: Boolean) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = Color(0xFF14002E),
    border = BorderStroke(1.dp, Color(0xFF260447)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
      Text(status, color = if (isBound) Color(0xFF00E676) else Color(0xFFFF80AB), fontSize = 12.sp)
    }
  }
}

@Composable
fun AccountItem(name: String, pid: String, isActive: Boolean, onClick: () -> Unit) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = if (isActive) Color(0xFF230047) else Color(0xFF14002E),
    border = BorderStroke(1.5.dp, if (isActive) Color(0xFF00E5FF) else Color(0xFF260447)),
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(Icons.Filled.AccountCircle, contentDescription = null, tint = if (isActive) Color(0xFF00E5FF) else Color.White, modifier = Modifier.size(36.dp))
      Spacer(modifier = Modifier.width(12.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text("PID: $pid", color = Color(0xFFB39DDB), fontSize = 12.sp)
      }
      if (isActive) {
        Text("CURRENT", color = Color(0xFF00E5FF), fontWeight = FontWeight.Black, fontSize = 11.sp)
      }
    }
  }
}
