package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.ui.components.AvatarFrameOverlay
import com.example.ui.storage.StoreCenterStorage
import com.aistudio.neonparty.nxvqlr.R
import com.example.ui.components.AllCountriesList
import com.example.ui.components.CountryItem
import com.example.ui.storage.PlatformBadge
import com.example.ui.storage.UserFullProfile
import com.example.ui.storage.UserProfileStorage

@Composable
fun UserProfileDetailScreen(
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()

  // Load persistent user profile from UserProfileStorage
  var profile by remember { mutableStateOf(UserProfileStorage.getProfile(context)) }

  // Refresh profile whenever needed
  val refreshProfile = {
    profile = UserProfileStorage.getProfile(context)
  }

  // Dialog states
  var showEditDialog by remember { mutableStateOf(false) }
  var showCpDialog by remember { mutableStateOf(false) }
  var showAgencyDialog by remember { mutableStateOf(false) }
  var selectedBadgeForDetail by remember { mutableStateOf<PlatformBadge?>(null) }

  // Copy PID function
  val copyId = {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Unique User PID", profile.userId)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "PID: ${profile.userId} copied to clipboard!", Toast.LENGTH_SHORT).show()
  }

  // Dynamic official badges assigned by Admin (No fake, demo, or placeholder badges)
  val assignedBadges = remember(profile) {
    UserProfileStorage.getAssignedBadges(context, profile.userId)
  }

  val topSafePadding = com.example.ui.components.rememberResponsiveTopBarPadding(extraTopSpacing = 4.dp)
  val bottomSafePadding = com.example.ui.components.rememberResponsiveNavigationBarPadding(extraBottomSpacing = 60.dp)

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          listOf(
            Color(0xFF0B0014),
            Color(0xFF140026),
            Color(0xFF07000E)
          )
        )
      )
  ) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(top = topSafePadding, bottom = bottomSafePadding)
    ) {
      // 1. TOP HEADER BANNER & OVERLAPPING AVATAR (Matches screenshot 20260908_201618.png)
      item {
        TopCoverAndAvatarSection(
          profile = profile,
          onBack = onBack,
          onEditClick = { showEditDialog = true },
          onCopyId = copyId
        )
      }

      // 2. STATUS COUNTERS ROW (Friend: 0, Follow: 0, Followers: 0, Visitors: 0)
      item {
        Spacer(modifier = Modifier.height(14.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          CounterGlassBox(
            icon = Icons.Filled.PersonAdd,
            count = profile.friendCount,
            label = "Friend",
            modifier = Modifier.weight(1f),
            onClick = { Toast.makeText(context, "Friends: 0", Toast.LENGTH_SHORT).show() }
          )
          CounterGlassBox(
            icon = Icons.Filled.PersonAdd,
            count = profile.followCount,
            label = "Follow",
            modifier = Modifier.weight(1f),
            onClick = { Toast.makeText(context, "Follow: 0", Toast.LENGTH_SHORT).show() }
          )
          CounterGlassBox(
            icon = Icons.Filled.People,
            count = profile.followersCount,
            label = "Followers",
            modifier = Modifier.weight(1f),
            onClick = { Toast.makeText(context, "Followers: 0", Toast.LENGTH_SHORT).show() }
          )
          CounterGlassBox(
            icon = Icons.Filled.Visibility,
            count = profile.visitorsCount,
            label = "Visitors",
            modifier = Modifier.weight(1f),
            onClick = { Toast.makeText(context, "Recent Visitors: 0", Toast.LENGTH_SHORT).show() }
          )
        }
      }

      // 3. 'CP' BOX & AGENCY BOX (Matches screenshot 20260908_201618.png)
      item {
        Spacer(modifier = Modifier.height(14.dp))
        CpAndAgencyRow(
          profile = profile,
          onCpClick = { showCpDialog = true },
          onAgencyClick = { showAgencyDialog = true },
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
        )
      }

      // 4. BADGES GALLERY (Only badges assigned to user by Admin)
      if (assignedBadges.isNotEmpty()) {
        item {
          Spacer(modifier = Modifier.height(20.dp))
          SectionTitleRow(
            title = "OFFICIAL BADGES",
            subtitle = "Admin Assigned Honors & Badges",
            icon = Icons.Filled.WorkspacePremium,
            accentColor = Color(0xFFFFD700),
            modifier = Modifier.padding(horizontal = 16.dp)
          )

          Spacer(modifier = Modifier.height(10.dp))

          LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(assignedBadges) { badge ->
              BadgeCard(
                badge = badge,
                onClick = { selectedBadgeForDetail = badge }
              )
            }
          }
        }
      }
    }
  }

  // ---------------------------------------------------------------------------
  // INTERACTIVE DIALOGS
  // ---------------------------------------------------------------------------

  // Edit Profile Dialog (with Gallery photo pickers & data update)
  if (showEditDialog) {
    EditProfileFullDialog(
      profile = profile,
      onDismiss = { showEditDialog = false },
      onSave = { updatedUsername, updatedGender, updatedAge, updatedCountry, updatedFlag, updatedBio, profileUri, bgUri ->
        UserProfileStorage.updateBasicInfo(
          context = context,
          username = updatedUsername,
          gender = updatedGender,
          age = updatedAge,
          country = updatedCountry,
          countryFlag = updatedFlag,
          bio = updatedBio
        )
        if (profileUri != null) {
          UserProfileStorage.updateProfileImage(context, profileUri)
        }
        if (bgUri != null) {
          UserProfileStorage.updateBackgroundImage(context, bgUri)
        }
        refreshProfile()
        showEditDialog = false
        Toast.makeText(context, "Profile updated and permanently saved!", Toast.LENGTH_SHORT).show()
      }
    )
  }

  // CP Partner Dialog (Manage romantic CP connection & partner photo)
  if (showCpDialog) {
    CpPartnerManageDialog(
      profile = profile,
      onDismiss = { showCpDialog = false },
      onUpdateCp = { isConnected, partnerName, partnerAvatarUri ->
        UserProfileStorage.updateCpPartner(context, isConnected, partnerName, partnerAvatarUri)
        refreshProfile()
        showCpDialog = false
        Toast.makeText(context, if (isConnected) "CP Partner linked successfully! 💖" else "CP status updated.", Toast.LENGTH_SHORT).show()
      }
    )
  }

  // Agency Dialog (Manage agency affiliation, owner mode & logo)
  if (showAgencyDialog) {
    AgencyManageDialog(
      profile = profile,
      onDismiss = { showAgencyDialog = false },
      onUpdateAgency = { isJoined, isOwner, name, logoUri, level ->
        UserProfileStorage.updateAgency(context, isJoined, isOwner, name, logoUri, level)
        refreshProfile()
        showAgencyDialog = false
        Toast.makeText(context, "Agency details updated!", Toast.LENGTH_SHORT).show()
      }
    )
  }

  // Badge Detail Dialog
  if (selectedBadgeForDetail != null) {
    val b = selectedBadgeForDetail!!
    BadgeDetailDialog(
      badge = b,
      onDismiss = { selectedBadgeForDetail = null }
    )
  }
}

// -----------------------------------------------------------------------------
// 1. TOP COVER & AVATAR SECTION (Exact replica of 20260908_201618.png)
// -----------------------------------------------------------------------------

@Composable
private fun TopCoverAndAvatarSection(
  profile: UserFullProfile,
  onBack: () -> Unit,
  onEditClick: () -> Unit,
  onCopyId: () -> Unit
) {
  val context = LocalContext.current
  val equippedFrame = remember(profile.userId) { StoreCenterStorage.getEquippedFrame(context, profile.userId) }

  Column(modifier = Modifier.fillMaxWidth()) {
    // 1. Cover Card / Background Banner
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 6.dp)
        .height(180.dp)
        .clip(RoundedCornerShape(24.dp))
        .background(
          Brush.verticalGradient(
            listOf(
              Color(0xFF2A004C),
              Color(0xFF140026),
              Color(0xFF0E001B)
            )
          )
        )
        .border(
          width = 1.4.dp,
          brush = Brush.horizontalGradient(
            listOf(Color(0xFFE040FB), Color(0xFF00E5FF), Color(0xFFFF007F))
          ),
          shape = RoundedCornerShape(24.dp)
        )
        .clickable(onClick = onEditClick)
    ) {
      if (!profile.backgroundImageUri.isNullOrBlank()) {
        AsyncImage(
          model = profile.backgroundImageUri,
          contentDescription = "Cover Banner",
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )
      } else {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.radialGradient(
                listOf(
                  Color(0xFF4A148C).copy(alpha = 0.5f),
                  Color(0xFF0D0020)
                )
              )
            )
        )
      }

      // Bottom-right Cover Change Pill
      Box(
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(end = 12.dp, bottom = 12.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(Color(0xBB000000))
          .border(1.dp, Color(0xFF00E5FF), RoundedCornerShape(12.dp))
          .clickable(onClick = onEditClick)
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Filled.AddAPhoto,
            contentDescription = "Change Cover",
            tint = Color(0xFF00E5FF),
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Cover",
            color = Color.White,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      // Top Actions Bar inside Banner: Back Button (Left) & Pencil Edit Icon (Right)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color(0x77000000))
            .border(1.dp, Color.White.copy(alpha = 0.6f), CircleShape)
            .clickable(onClick = onBack),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
        }

        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color(0x99000000))
            .border(1.2.dp, Color(0xFF00E5FF), CircleShape)
            .clickable(onClick = onEditClick)
            .testTag("pencil_edit_profile_button"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Filled.Edit,
            contentDescription = "Edit Profile",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }

    // 2. Overlapping User Avatar & Profile Information Block
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .offset(y = (-42).dp)
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        // Avatar + ID Row (Correct placement, perfectly separated, no overlap)
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.Bottom,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          // Circular Avatar with Frame Overlay & Camera Badge
          Box(
            modifier = Modifier
              .size(92.dp)
              .clickable(onClick = onEditClick),
            contentAlignment = Alignment.Center
          ) {
            AvatarFrameOverlay(
              size = 72.dp,
              frame = equippedFrame,
              profileImageUri = profile.profileImageUri
            )

            // Camera Badge
            Box(
              modifier = Modifier
                .size(26.dp)
                .align(Alignment.BottomEnd)
                .offset(x = (-4).dp, y = (-4).dp)
                .clip(CircleShape)
                .background(Color(0xFF00E5FF))
                .border(1.5.dp, Color(0xFF140026), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Filled.AddAPhoto,
                contentDescription = "Change Profile Picture",
                tint = Color.Black,
                modifier = Modifier.size(13.dp)
              )
            }
          }

          // User ID + Copy Pill (Positioned cleanly on the right side)
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF1D0838),
            border = BorderStroke(1.dp, Color(0xFF00E5FF)),
            modifier = Modifier.padding(bottom = 6.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .clickable(onClick = onCopyId)
                .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Text(
                text = "PID: ",
                color = Color(0xFF00E5FF),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = profile.userId,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black
              )
              Spacer(modifier = Modifier.width(8.dp))
              Icon(
                imageVector = Icons.Filled.ContentCopy,
                contentDescription = "Copy ID",
                tint = Color(0xFF00E5FF),
                modifier = Modifier.size(13.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // User Name (Prominently placed, spacious, no overlap)
        Text(
          text = profile.username,
          color = Color.White,
          fontSize = 22.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 0.4.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Professional Metadata Chips Row: Gender/Age + Level + Country + VIP
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Gender & Age Pill
          val genderSymbol = if (profile.gender == "Female") "♀" else "♂"
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(
                Brush.horizontalGradient(
                  if (profile.gender == "Female") listOf(Color(0xFFFF4081), Color(0xFFD81B60))
                  else listOf(Color(0xFF00B0FF), Color(0xFF0091EA))
                )
              )
              .border(0.6.dp, Color(0xFF80D8FF), RoundedCornerShape(10.dp))
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = "$genderSymbol ${profile.age}",
              color = Color.White,
              fontSize = 11.sp,
              fontWeight = FontWeight.Black
            )
          }

          // Level Badge (Official PRIYA LIVE Level Badge)
          com.example.ui.components.OfficialLevelBadge(
            level = profile.userLevel,
            fontSize = 11.sp
          )

          // Country Chip (Flag + Full Country Name)
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFF1E0A3C))
              .border(0.8.dp, Color(0xFF00E5FF).copy(alpha = 0.7f), RoundedCornerShape(10.dp))
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = profile.countryFlag, fontSize = 12.sp)
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = profile.country,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }

          if (profile.vipBadge.isNotBlank() && profile.vipBadge != "None" && profile.vipBadge != "Disabled" && profile.vipBadge != "Hidden") {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Brush.horizontalGradient(listOf(Color(0xFFFF8F00), Color(0xFFFF6F00))))
                .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Text(
                text = profile.vipBadge,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black
              )
            }
          }
        }

        // Bio section if present
        if (profile.bio.isNotBlank()) {
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = profile.bio,
            color = Color(0xFFD1C4E9),
            fontSize = 12.5.sp,
            lineHeight = 17.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )
        }
      }
    }
  }
}

// -----------------------------------------------------------------------------
// 2. COUNTER GLASS BOX (Status counters: Friend, Follow, Followers, Visitors)
// -----------------------------------------------------------------------------

@Composable
private fun CounterGlassBox(
  icon: ImageVector,
  count: String,
  label: String,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(16.dp))
      .background(
        Brush.verticalGradient(
          listOf(Color(0xFF14002E), Color(0xFF0A0018))
        )
      )
      .border(
        width = 1.6.dp,
        brush = Brush.horizontalGradient(
          listOf(Color(0xFF00E5FF), Color(0xFFE040FB), Color(0xFF00E5FF))
        ),
        shape = RoundedCornerShape(16.dp)
      )
      .clickable(onClick = onClick)
      .padding(vertical = 8.dp, horizontal = 2.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = Color(0xFF00E5FF),
          modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
          text = count,
          color = Color.White,
          fontSize = 15.sp,
          fontWeight = FontWeight.Black
        )
      }
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = label,
        color = Color(0xFFD1C4E9),
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center
      )
    }
  }
}

// -----------------------------------------------------------------------------
// 3. 'CP' BOX & AGENCY BOX (Side by side as in screenshot 20260908_201618.png)
// -----------------------------------------------------------------------------

@Composable
private fun CpAndAgencyRow(
  profile: UserFullProfile,
  onCpClick: () -> Unit,
  onAgencyClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // 1. CP Box (Left Box with Double Rose Heart Frame and CP Logo in middle)
    Box(
      modifier = Modifier
        .weight(1.5f)
        .height(118.dp)
        .clip(RoundedCornerShape(18.dp))
        .background(Color.White)
        .border(1.6.dp, Color(0xFFFF4081), RoundedCornerShape(18.dp))
        .clickable(onClick = onCpClick)
        .padding(8.dp),
      contentAlignment = Alignment.Center
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Left Heart Frame: Current User Avatar
        HeartAvatarFrame(
          imageUri = profile.profileImageUri,
          defaultDrawable = R.drawable.img_streamer_girl,
          isUser = true,
          label = "You"
        )

        // Center CP Romantic Silhouette & Logo
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Text(
            text = "💑",
            fontSize = 22.sp
          )
          Text(
            text = "CP",
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            style = androidx.compose.ui.text.TextStyle(
              brush = Brush.linearGradient(
                listOf(Color(0xFFFF0055), Color(0xFF7C4DFF), Color(0xFF00E5FF))
              )
            )
          )
        }

        // Right Heart Frame: Partner Avatar or Plus Icon (+)
        HeartPartnerFrame(
          isConnected = profile.isCpConnected,
          partnerAvatarUri = profile.cpPartnerAvatarUri,
          partnerName = profile.cpPartnerName ?: "Add Partner"
        )
      }
    }

    // 2. Agency Box (Right Box next to CP box)
    Box(
      modifier = Modifier
        .weight(1f)
        .height(118.dp)
        .clip(RoundedCornerShape(18.dp))
        .background(Color.White)
        .border(1.6.dp, Color(0xFF00E5FF), RoundedCornerShape(18.dp))
        .clickable(onClick = onAgencyClick)
        .padding(8.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        // Agency Crest Logo or Picture
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                listOf(Color(0xFF7C4DFF), Color(0xFF00E5FF))
              )
            )
            .border(1.2.dp, Color(0xFFFFD700), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          if (!profile.agencyLogoUri.isNullOrBlank()) {
            AsyncImage(
              model = profile.agencyLogoUri,
              contentDescription = "Agency Logo",
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop
            )
          } else {
            Icon(
              imageVector = if (profile.isAgencyOwner) Icons.Filled.WorkspacePremium else Icons.Filled.Shield,
              contentDescription = null,
              tint = Color(0xFFFFD700),
              modifier = Modifier.size(24.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = if (profile.isAgencyJoined || profile.isAgencyOwner) {
            profile.agencyName ?: "Neon Star"
          } else {
            "Join Agency"
          },
          color = Color(0xFF140026),
          fontSize = 11.5.sp,
          fontWeight = FontWeight.Black,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Text(
          text = if (profile.isAgencyOwner) "Agency Owner" else if (profile.isAgencyJoined) "Host Official" else "Tap to Join",
          color = Color(0xFF7C4DFF),
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

/**
 * Left Heart Frame for Current User
 */
@Composable
private fun HeartAvatarFrame(
  imageUri: String?,
  defaultDrawable: Int,
  isUser: Boolean,
  label: String
) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Box(
      modifier = Modifier
        .size(44.dp)
        .clip(CircleShape)
        .background(Color(0xFFFFD700))
        .padding(2.dp)
        .clip(CircleShape)
    ) {
      if (!imageUri.isNullOrBlank()) {
        AsyncImage(
          model = imageUri,
          contentDescription = label,
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )
      } else {
        Image(
          painter = painterResource(id = defaultDrawable),
          contentDescription = label,
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )
      }
    }
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = "🌹 You 🌹",
      color = Color(0xFFD81B60),
      fontSize = 8.5.sp,
      fontWeight = FontWeight.Bold
    )
  }
}

/**
 * Right Heart Frame for Partner (Displays Partner or glowing + connect button)
 */
@Composable
private fun HeartPartnerFrame(
  isConnected: Boolean,
  partnerAvatarUri: String?,
  partnerName: String
) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Box(
      modifier = Modifier
        .size(44.dp)
        .clip(CircleShape)
        .background(
          if (isConnected) Color(0xFFFFD700)
          else Color(0xFFFFEBEE)
        )
        .border(1.5.dp, Color(0xFFFF4081), CircleShape)
        .padding(2.dp)
        .clip(CircleShape),
      contentAlignment = Alignment.Center
    ) {
      if (isConnected && !partnerAvatarUri.isNullOrBlank()) {
        AsyncImage(
          model = partnerAvatarUri,
          contentDescription = partnerName,
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )
      } else if (isConnected) {
        Image(
          painter = painterResource(id = R.drawable.img_guitarist),
          contentDescription = partnerName,
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )
      } else {
        // Glowing Plus (+) Icon for adding CP partner!
        Icon(
          imageVector = Icons.Filled.Add,
          contentDescription = "Add CP Partner",
          tint = Color(0xFFFF007F),
          modifier = Modifier.size(24.dp)
        )
      }
    }
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = if (isConnected) "🌹 CP 🌹" else "🌹 Connect 🌹",
      color = Color(0xFFD81B60),
      fontSize = 8.5.sp,
      fontWeight = FontWeight.Bold
    )
  }
}

// -----------------------------------------------------------------------------
// 4. BADGE CARD COMPONENT (Official Badges Gallery)
// -----------------------------------------------------------------------------

@Composable
private fun BadgeCard(
  badge: PlatformBadge,
  onClick: () -> Unit
) {
  val badgeColor = remember(badge.glowColorHex) {
    try {
      Color(android.graphics.Color.parseColor(badge.glowColorHex))
    } catch (e: Exception) {
      Color(0xFFFFD700)
    }
  }

  Box(
    modifier = Modifier
      .width(110.dp)
      .height(115.dp)
      .clip(RoundedCornerShape(16.dp))
      .background(
        Brush.verticalGradient(
          listOf(Color(0xFF1B0038), Color(0xFF0C001C))
        )
      )
      .border(
        width = 1.4.dp,
        brush = Brush.horizontalGradient(
          listOf(badgeColor, Color(0xFF00E5FF))
        ),
        shape = RoundedCornerShape(16.dp)
      )
      .clickable(onClick = onClick)
      .padding(8.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.fillMaxSize()
    ) {
      // Glow Ring & Icon
      Box(
        modifier = Modifier
          .size(42.dp)
          .clip(CircleShape)
          .background(badgeColor.copy(alpha = 0.2f))
          .border(1.2.dp, badgeColor, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Text(text = badge.iconEmoji, fontSize = 22.sp)
      }

      Text(
        text = badge.title,
        color = Color.White,
        fontSize = 11.sp,
        fontWeight = FontWeight.Black,
        textAlign = TextAlign.Center,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      // Pill level
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(6.dp))
        .background(badgeColor.copy(alpha = 0.25f))
        .border(0.6.dp, badgeColor, RoundedCornerShape(6.dp))
        .padding(horizontal = 6.dp, vertical = 1.5.dp)
      ) {
        Text(
          text = badge.level,
          color = badgeColor,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

// -----------------------------------------------------------------------------
// SECTION TITLE HEADER
// -----------------------------------------------------------------------------

@Composable
private fun SectionTitleRow(
  title: String,
  subtitle: String,
  icon: ImageVector,
  accentColor: Color,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(32.dp)
        .clip(CircleShape)
        .background(accentColor.copy(alpha = 0.2f))
        .border(1.dp, accentColor, CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = accentColor,
        modifier = Modifier.size(18.dp)
      )
    }

    Spacer(modifier = Modifier.width(8.dp))

    Column {
      Text(
        text = title,
        color = Color.White,
        fontSize = 14.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 0.5.sp
      )
      Text(
        text = subtitle,
        color = Color(0xFFB39DDB),
        fontSize = 10.sp
      )
    }
  }
}

// -----------------------------------------------------------------------------
// EDIT PROFILE FULL DIALOG (Gallery photo pickers, name, gender, age, country)
// -----------------------------------------------------------------------------

@Composable
private fun EditProfileFullDialog(
  profile: UserFullProfile,
  onDismiss: () -> Unit,
  onSave: (
    username: String,
    gender: String,
    age: Int,
    country: String,
    flag: String,
    bio: String,
    profileUri: String?,
    bgUri: String?
  ) -> Unit
) {
  var name by remember { mutableStateOf(profile.username) }
  var gender by remember { mutableStateOf(profile.gender) }
  var ageText by remember { mutableStateOf(profile.age.toString()) }
  var selectedCountry by remember { mutableStateOf(profile.country) }
  var selectedFlag by remember { mutableStateOf(profile.countryFlag) }
  var bio by remember { mutableStateOf(profile.bio) }

  var pickedProfileUri by remember { mutableStateOf<Uri?>(null) }
  var pickedBgUri by remember { mutableStateOf<Uri?>(null) }

  // Photo Pickers using Android standard zero-permission Photo Picker
  val profilePhotoPicker = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    if (uri != null) {
      pickedProfileUri = uri
    }
  }

  val backgroundPhotoPicker = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    if (uri != null) {
      pickedBgUri = uri
    }
  }

  // Country Selection state
  var showCountryPicker by remember { mutableStateOf(false) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.94f)
        .clip(RoundedCornerShape(24.dp))
        .border(
          width = 1.8.dp,
          brush = Brush.horizontalGradient(
            listOf(Color(0xFF00E5FF), Color(0xFFFF007F), Color(0xFF7C4DFF))
          ),
          shape = RoundedCornerShape(24.dp)
        ),
      color = Color(0xFF140028)
    ) {
      LazyColumn(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "EDIT PROFILE (প্রোফাইল এডিট)",
              color = Color.White,
              fontSize = 16.sp,
              fontWeight = FontWeight.Black
            )
            IconButton(onClick = onDismiss) {
              Icon(imageVector = Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
            }
          }
        }

        // 1. Photos Section: Profile Avatar & Background Cover from Gallery
        item {
          Text(
            text = "Photos from Phone Gallery (গ্যালারি থেকে ছবি)",
            color = Color(0xFF00E5FF),
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(6.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            // Pick Profile Photo Button
            Button(
              onClick = {
                profilePhotoPicker.launch(
                  PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
              },
              modifier = Modifier.weight(1f),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26004C)),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E5FF)),
              shape = RoundedCornerShape(12.dp)
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                  imageVector = Icons.Filled.AddAPhoto,
                  contentDescription = null,
                  tint = Color(0xFF00E5FF),
                  modifier = Modifier.size(20.dp)
                )
                Text(
                  text = if (pickedProfileUri != null) "✓ Profile Set" else "Change Profile",
                  fontSize = 11.sp,
                  color = Color.White
                )
              }
            }

            // Pick Background Banner Button
            Button(
              onClick = {
                backgroundPhotoPicker.launch(
                  PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
              },
              modifier = Modifier.weight(1f),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26004C)),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF007F)),
              shape = RoundedCornerShape(12.dp)
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                  imageVector = Icons.Filled.Image,
                  contentDescription = null,
                  tint = Color(0xFFFF007F),
                  modifier = Modifier.size(20.dp)
                )
                Text(
                  text = if (pickedBgUri != null) "✓ Banner Set" else "Change Banner",
                  fontSize = 11.sp,
                  color = Color.White
                )
              }
            }
          }
        }

        // 2. Display Name
        item {
          OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Display Name (নাম)", color = Color(0xFFCE93D8)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White,
              focusedBorderColor = Color(0xFF00E5FF),
              unfocusedBorderColor = Color(0xFF7C4DFF)
            )
          )
        }

        // 3. Gender (ছেলে/মেয়ে) Selector
        item {
          Text(
            text = "Gender (জেন্ডার নির্বাচন)",
            color = Color(0xFFCE93D8),
            fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth()
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            val isMale = gender == "Male"
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isMale) Color(0xFF0091EA) else Color(0xFF22003B))
                .border(1.dp, if (isMale) Color(0xFF80D8FF) else Color(0x55FFFFFF), RoundedCornerShape(12.dp))
                .clickable { gender = "Male" }
                .padding(vertical = 10.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "♂ Male (ছেলে)",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
            }

            val isFemale = gender == "Female"
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isFemale) Color(0xFFD81B60) else Color(0xFF22003B))
                .border(1.dp, if (isFemale) Color(0xFFFF80AB) else Color(0x55FFFFFF), RoundedCornerShape(12.dp))
                .clickable { gender = "Female" }
                .padding(vertical = 10.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "♀ Female (মেয়ে)",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
            }
          }
        }

        // 4. Age Input
        item {
          OutlinedTextField(
            value = ageText,
            onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 2) ageText = it },
            label = { Text("Age (বয়স)", color = Color(0xFFCE93D8)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White,
              focusedBorderColor = Color(0xFF00E5FF),
              unfocusedBorderColor = Color(0xFF7C4DFF)
            )
          )
        }

        // 5. Country Selector
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFF22003B))
              .border(1.dp, Color(0xFF7C4DFF), RoundedCornerShape(12.dp))
              .clickable { showCountryPicker = !showCountryPicker }
              .padding(14.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = selectedFlag, fontSize = 22.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Country: $selectedCountry",
                  color = Color.White,
                  fontSize = 13.5.sp,
                  fontWeight = FontWeight.Bold
                )
              }
              Text(text = "Change ▼", color = Color(0xFF00E5FF), fontSize = 11.sp)
            }
          }

          AnimatedVisibility(visible = showCountryPicker) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF1B0033))
                .padding(6.dp)
            ) {
              LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(AllCountriesList.filter { it.code != "ALL" }) { c ->
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clickable {
                        selectedCountry = c.name
                        selectedFlag = c.flagEmoji
                        showCountryPicker = false
                      }
                      .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(text = c.flagEmoji, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = c.name, color = Color.White, fontSize = 12.5.sp)
                  }
                }
              }
            }
          }
        }

        // 6. Non-Editable Permanent Unique ID Notice
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFF0D001C))
              .border(1.dp, Color(0x44FFFFFF), RoundedCornerShape(12.dp))
              .padding(10.dp)
          ) {
            Column {
              Text(
                text = "System Unique PID: ${profile.userId} (Permanent & Immutable)",
                color = Color(0xFFFFD700),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black
              )
              Text(
                text = "ইউনিক পিআইডি (PID) স্বয়ংক্রিয়ভাবে সংরক্ষিত এবং পরিবর্তনযোগ্য নয়।",
                color = Color(0xFFB39DDB),
                fontSize = 9.5.sp
              )
            }
          }
        }

        // 7. Save CTA Button
        item {
          Spacer(modifier = Modifier.height(8.dp))
          Button(
            onClick = {
              val finalAge = ageText.toIntOrNull() ?: 18
              onSave(
                name.ifBlank { "MR MAX" },
                gender,
                finalAge,
                selectedCountry,
                selectedFlag,
                bio,
                pickedProfileUri?.toString(),
                pickedBgUri?.toString()
              )
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(0.dp)
          ) {
            Box(
              modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(14.dp))
                .background(
                  Brush.horizontalGradient(
                    listOf(Color(0xFFFF007F), Color(0xFF00E5FF))
                  )
                ),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "SAVE PERMANENTLY (সেভ করুন)",
                color = Color.White,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Black
              )
            }
          }
        }
      }
    }
  }
}

// -----------------------------------------------------------------------------
// CP PARTNER MANAGE DIALOG
// -----------------------------------------------------------------------------

@Composable
private fun CpPartnerManageDialog(
  profile: UserFullProfile,
  onDismiss: () -> Unit,
  onUpdateCp: (isConnected: Boolean, partnerName: String?, partnerAvatarUri: String?) -> Unit
) {
  var partnerName by remember { mutableStateOf(profile.cpPartnerName ?: "") }
  var isConnected by remember { mutableStateOf(profile.isCpConnected) }
  var pickedAvatarUri by remember { mutableStateOf<Uri?>(null) }

  val partnerPhotoPicker = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    if (uri != null) {
      pickedAvatarUri = uri
    }
  }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(22.dp))
        .border(1.5.dp, Color(0xFFFF4081), RoundedCornerShape(22.dp)),
      color = Color(0xFF1D002C)
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Text(
          text = "CP COUPLE SPACE (সিপি স্পেস)",
          color = Color.White,
          fontSize = 16.sp,
          fontWeight = FontWeight.Black
        )

        Text(
          text = "সিপি বক্সে পার্টনার কানেক্ট করতে পার্টনারের নাম ও ছবি যোগ করুন।",
          color = Color(0xFFFF80AB),
          fontSize = 11.sp,
          textAlign = TextAlign.Center
        )

        Button(
          onClick = {
            partnerPhotoPicker.launch(
              PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B004F)),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF4081))
        ) {
          Icon(imageVector = Icons.Filled.AddAPhoto, contentDescription = null, tint = Color(0xFFFF4081))
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = if (pickedAvatarUri != null) "✓ Photo Selected" else "Partner Photo from Gallery", fontSize = 11.sp)
        }

        OutlinedTextField(
          value = partnerName,
          onValueChange = { partnerName = it },
          label = { Text("Partner Name / ID", color = Color(0xFFCE93D8)) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color(0xFFFF4081),
            unfocusedBorderColor = Color(0xFF7C4DFF)
          )
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Button(
            onClick = {
              onUpdateCp(
                true,
                partnerName.ifBlank { "My Partner" },
                pickedAvatarUri?.toString() ?: profile.cpPartnerAvatarUri
              )
            },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF007F))
          ) {
            Text("Bind CP Partner", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }

          if (profile.isCpConnected) {
            Button(
              onClick = { onUpdateCp(false, null, null) },
              modifier = Modifier.weight(1f),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FFFFFF))
            ) {
              Text("Unbind", color = Color.White, fontSize = 11.sp)
            }
          }
        }
      }
    }
  }
}

// -----------------------------------------------------------------------------
// AGENCY MANAGE DIALOG
// -----------------------------------------------------------------------------

@Composable
private fun AgencyManageDialog(
  profile: UserFullProfile,
  onDismiss: () -> Unit,
  onUpdateAgency: (isJoined: Boolean, isOwner: Boolean, name: String?, logoUri: String?, level: String?) -> Unit
) {
  var agencyName by remember { mutableStateOf(profile.agencyName ?: "Neon Star Agency") }
  var isOwner by remember { mutableStateOf(profile.isAgencyOwner) }
  var pickedLogoUri by remember { mutableStateOf<Uri?>(null) }

  val agencyLogoPicker = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    if (uri != null) {
      pickedLogoUri = uri
    }
  }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(22.dp))
        .border(1.5.dp, Color(0xFF00E5FF), RoundedCornerShape(22.dp)),
      color = Color(0xFF140026)
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Text(
          text = "AGENCY CENTER (এজেন্সি সেটিংস)",
          color = Color.White,
          fontSize = 16.sp,
          fontWeight = FontWeight.Black
        )

        Text(
          text = "আপনি হোস্ট হিসেবে যুক্ত থাকলে এজেন্সির পিকচার শো করবে, আবার নিজের এজেন্সির ওনার হলে তা শো করবে।",
          color = Color(0xFFB39DDB),
          fontSize = 11.sp,
          textAlign = TextAlign.Center
        )

        // Upload Agency Logo from Gallery
        Button(
          onClick = {
            agencyLogoPicker.launch(
              PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22003B)),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E5FF))
        ) {
          Icon(imageVector = Icons.Filled.AddAPhoto, contentDescription = null, tint = Color(0xFF00E5FF))
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = if (pickedLogoUri != null) "✓ Logo Selected" else "Agency Logo from Gallery", fontSize = 11.sp)
        }

        OutlinedTextField(
          value = agencyName,
          onValueChange = { agencyName = it },
          label = { Text("Agency Name (এজেন্সির নাম)", color = Color(0xFFCE93D8)) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color(0xFF00E5FF),
            unfocusedBorderColor = Color(0xFF7C4DFF)
          )
        )

        // Toggle Owner vs Host role
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Button(
            onClick = { isOwner = false },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
              containerColor = if (!isOwner) Color(0xFF7C4DFF) else Color(0x33FFFFFF)
            )
          ) {
            Text("Joined Host", fontSize = 11.sp)
          }

          Button(
            onClick = { isOwner = true },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
              containerColor = if (isOwner) Color(0xFFFFD700) else Color(0x33FFFFFF)
            )
          ) {
            Text("Agency Owner", color = if (isOwner) Color.Black else Color.White, fontSize = 11.sp)
          }
        }

        Button(
          onClick = {
            onUpdateAgency(
              true,
              isOwner,
              agencyName.ifBlank { "Neon Star Agency" },
              pickedLogoUri?.toString() ?: profile.agencyLogoUri,
              "Diamond Agency"
            )
          },
          modifier = Modifier.fillMaxWidth(),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF))
        ) {
          Text("Save Agency Settings", color = Color.Black, fontWeight = FontWeight.Black)
        }
      }
    }
  }
}

// -----------------------------------------------------------------------------
// BADGE DETAIL DIALOG
// -----------------------------------------------------------------------------

@Composable
private fun BadgeDetailDialog(
  badge: PlatformBadge,
  onDismiss: () -> Unit
) {
  val badgeColor = remember(badge.glowColorHex) {
    try {
      Color(android.graphics.Color.parseColor(badge.glowColorHex))
    } catch (e: Exception) {
      Color(0xFFFFD700)
    }
  }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.85f)
        .clip(RoundedCornerShape(20.dp))
        .border(1.6.dp, badgeColor, RoundedCornerShape(20.dp)),
      color = Color(0xFF1A0033)
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(text = badge.iconEmoji, fontSize = 48.sp)
        Text(text = badge.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
        Text(text = "${badge.category} • ${badge.level}", color = badgeColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text(text = badge.description, color = Color(0xFFEDE7F6), fontSize = 11.5.sp, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(8.dp))
        Button(
          onClick = onDismiss,
          modifier = Modifier.fillMaxWidth(),
          colors = ButtonDefaults.buttonColors(containerColor = badgeColor)
        ) {
          Text("Close", color = Color.Black, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
