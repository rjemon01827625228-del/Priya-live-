package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.AvatarFrameOverlay
import com.example.ui.storage.LocalAgency
import com.example.ui.storage.LocalHostCycleProgress
import com.example.ui.storage.LocalHostDailyProgress
import com.example.ui.storage.LocalHostRequest
import com.example.ui.storage.SocialAgencyStorage
import com.example.ui.storage.UserProfileStorage
import kotlinx.coroutines.launch

/**
 * Dedicated Full-Screen Host Center & Host Request Page.
 * Bottom navigation bar is completely hidden.
 *
 * - When hostStatus != "active": Displays the Host Request screen strictly following Reference 1.
 *   Shows User's real permanent PID, Agency Code input, Send Request button, and Country-based agencies.
 * - When hostStatus == "active": Displays the Official Host Center with 10-Day Earning System,
 *   1 Hour 23 Minutes daily requirement (4980s), 4,000 daily coins, 40,000 cycle coins, and verified claim system.
 */
@Composable
fun HostCenterScreen(
  onBack: () -> Unit,
  onStartStream: () -> Unit = {}
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  val coroutineScope = rememberCoroutineScope()

  // User state from local storage
  var fullProfile by remember { mutableStateOf(UserProfileStorage.getProfile(context)) }
  val userPid = fullProfile.userId

  // Host cycle and daily progress
  var cycleProgress by remember(userPid) {
    mutableStateOf(SocialAgencyStorage.getHostCycleProgress(context, userPid))
  }
  var dailyProgressList by remember(userPid) {
    mutableStateOf(SocialAgencyStorage.getHostDailyProgressList(context, userPid))
  }

  // Available agencies in user's country
  var countryAgencies by remember(fullProfile.country) {
    mutableStateOf(SocialAgencyStorage.getAgenciesForCountry(context, fullProfile.country))
  }
  var activeRequest by remember(fullProfile.pendingAgencyRequestId) {
    mutableStateOf(SocialAgencyStorage.getHostRequest(context, fullProfile.pendingAgencyRequestId))
  }

  // Form input state
  var agencyCodeInput by remember { mutableStateOf(fullProfile.agencyId) }
  var isSubmitting by remember { mutableStateOf(false) }
  var isClaiming by remember { mutableStateOf(false) }

  val isApprovedHost = fullProfile.hostStatus.equals("active", ignoreCase = true)
  val topSafePadding = com.example.ui.components.rememberResponsiveTopBarPadding(extraTopSpacing = 0.dp)
  val bottomSafePadding = com.example.ui.components.rememberResponsiveNavigationBarPadding(extraBottomSpacing = 0.dp)

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF0F081D))
      .padding(top = topSafePadding, bottom = bottomSafePadding)
      .testTag("host_center_screen")
  ) {
    if (isApprovedHost) {
      // =========================================================================
      // MODE B: OFFICIAL HOST CENTER & 10-DAY EARNING SYSTEM
      // =========================================================================
      ActiveHostCenterContent(
        profile = fullProfile,
        cycle = cycleProgress,
        days = dailyProgressList,
        isClaiming = isClaiming,
        onBack = onBack,
        onStartStream = onStartStream,
        onClaimRewards = {
          isClaiming = true
          SocialAgencyStorage.claimHostReward(context, userPid) { success, _, msg ->
            isClaiming = false
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            if (success) {
              fullProfile = UserProfileStorage.getProfile(context)
              cycleProgress = SocialAgencyStorage.getHostCycleProgress(context, userPid)
            }
          }
        }
      )
    } else {
      // =========================================================================
      // MODE A: HOST REQUEST / JOIN AGENCY (Follows Reference Image 1)
      // =========================================================================
      HostRequestApplicationContent(
        profile = fullProfile,
        userPid = userPid,
        agencyCodeInput = agencyCodeInput,
        onAgencyCodeChange = { agencyCodeInput = it },
        isSubmitting = isSubmitting,
        activeRequest = activeRequest,
        countryAgencies = countryAgencies,
        onBack = onBack,
        onSubmitRequest = {
          if (agencyCodeInput.trim().isBlank()) {
            Toast.makeText(context, "Please enter an Agency ID / Code", Toast.LENGTH_SHORT).show()
            return@HostRequestApplicationContent
          }
          isSubmitting = true
          SocialAgencyStorage.sendHostJoinRequest(context, agencyCodeInput.trim()) { success, msg ->
            isSubmitting = false
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            if (success) {
              fullProfile = UserProfileStorage.getProfile(context)
              activeRequest = SocialAgencyStorage.getHostRequest(context, fullProfile.pendingAgencyRequestId)
            }
          }
        },
        onSelectAgency = { agency ->
          agencyCodeInput = agency.agencyId
          Toast.makeText(context, "Selected Agency: ${agency.agencyName}", Toast.LENGTH_SHORT).show()
        }
      )
    }
  }
}

// =============================================================================
// 1. HOST REQUEST APPLICATION VIEW (Matches Reference Image 1 Exactly)
// =============================================================================
@Composable
private fun HostRequestApplicationContent(
  profile: com.example.ui.storage.UserFullProfile,
  userPid: String,
  agencyCodeInput: String,
  onAgencyCodeChange: (String) -> Unit,
  isSubmitting: Boolean,
  activeRequest: LocalHostRequest?,
  countryAgencies: List<LocalAgency>,
  onBack: () -> Unit,
  onSubmitRequest: () -> Unit,
  onSelectAgency: (LocalAgency) -> Unit
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(bottom = 32.dp)
  ) {
    // Top Bar (Back button + "Host Request" Title)
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color(0xFF22133D))
            .clickable(onClick = onBack)
            .testTag("host_request_back_button"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Text(
          text = "Host Request",
          color = Color.White,
          fontSize = 19.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }

    // Top Hero Graphic Banner (Matches Reference Image 1)
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(210.dp)
      ) {
        Image(
          painter = painterResource(id = R.drawable.img_join_agency_banner),
          contentDescription = "Join Agency Banner",
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )

        // Gradient overlay for visual blend
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                listOf(
                  Color.Transparent,
                  Color(0x660F081D),
                  Color(0xFF0F081D)
                )
              )
            )
        )

        // Bold Banner Heading
        Column(
          modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 12.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "REQUEST TO JOIN HOST AGENCY",
            color = Color(0xFFFFE57F),
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
          )
          Text(
            text = "Broadcast • Earn Rewards • Official Verification",
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 11.5.sp
          )
        }
      }
    }

    // Main White / Clean Card Container (Reference Image 1)
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(22.dp),
          color = Color.White,
          shadowElevation = 8.dp,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(22.dp)
          ) {
            // "Join Agency" Gradient Title
            Text(
              text = "Join Agency",
              fontSize = 20.sp,
              fontWeight = FontWeight.Black,
              style = TextStyle(
                brush = Brush.horizontalGradient(
                  listOf(Color(0xFFE91E63), Color(0xFF9C27B0))
                )
              )
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Label 1: Unique Id
            Text(
              text = "Unique Id",
              color = Color(0xFF212121),
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Non-editable display of user's real permanent PID
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF7F9FC))
                .border(1.2.dp, Color(0xFF00E5FF), RoundedCornerShape(8.dp))
                .clickable {
                  clipboardManager.setText(AnnotatedString(userPid))
                  Toast.makeText(context, "Copied PID: $userPid", Toast.LENGTH_SHORT).show()
                }
                .padding(horizontal = 14.dp),
              contentAlignment = Alignment.CenterStart
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = userPid,
                  color = Color(0xFF00B0FF),
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Black,
                  letterSpacing = 1.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "Permanent PID",
                    color = Color.Gray,
                    fontSize = 10.5.sp
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Icon(
                    imageVector = Icons.Filled.ContentCopy,
                    contentDescription = "Copy PID",
                    tint = Color(0xFF00B0FF),
                    modifier = Modifier.size(15.dp)
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Label 2: Agency Code / Agency ID
            Text(
              text = "Agency Code",
              color = Color(0xFF212121),
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Agency Code Input Field
            OutlinedTextField(
              value = agencyCodeInput,
              onValueChange = onAgencyCodeChange,
              placeholder = {
                Text("Please enter the agency code", color = Color(0xFF9E9E9E), fontSize = 13.sp)
              },
              singleLine = true,
              keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
              ),
              keyboardActions = KeyboardActions(
                onDone = { onSubmitRequest() }
              ),
              colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFF212121),
                unfocusedTextColor = Color(0xFF212121),
                focusedBorderColor = Color(0xFF00B0FF),
                unfocusedBorderColor = Color(0xFFB0BEC5),
                focusedContainerColor = Color(0xFFFAFAFA),
                unfocusedContainerColor = Color(0xFFFAFAFA)
              ),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("agency_code_input")
            )

            // Pending request indicator
            if (activeRequest != null && activeRequest.status == "PENDING") {
              Spacer(modifier = Modifier.height(14.dp))
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0xFFFFF8E1))
                  .border(1.dp, Color(0xFFFFB300), RoundedCornerShape(8.dp))
                  .padding(10.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Filled.HourglassTop,
                    contentDescription = null,
                    tint = Color(0xFFFF8F00),
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Column {
                    Text(
                      text = "Pending Agency Approval",
                      color = Color(0xFFE65100),
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold
                    )
                    Text(
                      text = "Applied to ${activeRequest.agencyName} (ID: ${activeRequest.agencyId})",
                      color = Color(0xFF6D4C41),
                      fontSize = 11.sp
                    )
                  }
                }
              }
            } else if (activeRequest != null && activeRequest.status == "REJECTED") {
              Spacer(modifier = Modifier.height(14.dp))
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0xFFFFEBEE))
                  .border(1.dp, Color(0xFFEF5350), RoundedCornerShape(8.dp))
                  .padding(10.dp)
              ) {
                Text(
                  text = "Your previous request to ${activeRequest.agencyName} was declined. You may apply to another agency.",
                  color = Color(0xFFC62828),
                  fontSize = 11.5.sp
                )
              }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Button: "Send Request" (Gradient Pink/Purple per reference)
            Button(
              onClick = onSubmitRequest,
              enabled = !isSubmitting,
              shape = RoundedCornerShape(26.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
              contentPadding = PaddingValues(0.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("send_host_request_button")
            ) {
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .background(
                    Brush.horizontalGradient(
                      listOf(Color(0xFFE91E63), Color(0xFF9C27B0))
                    )
                  ),
                contentAlignment = Alignment.Center
              ) {
                if (isSubmitting) {
                  CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp
                  )
                } else {
                  Text(
                    text = "Send Request",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
          }
        }
      }
    }

    // Section 2: Active Agencies Available in User's Country
    item {
      Spacer(modifier = Modifier.height(18.dp))
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Active Agencies in ${profile.country.ifBlank { "Bangladesh" }}",
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "${countryAgencies.size} Available",
            color = Color(0xFF00E5FF),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (countryAgencies.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .background(Color(0xFF1B1130))
              .padding(24.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "No Available Agency",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "No active agency available in ${profile.country.ifBlank { "Bangladesh" }} right now.",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
              )
            }
          }
        } else {
          countryAgencies.forEach { agency ->
            AgencyDiscoverCard(
              agency = agency,
              onSelect = { onSelectAgency(agency) }
            )
            Spacer(modifier = Modifier.height(10.dp))
          }
        }
      }
    }
  }
}

@Composable
private fun AgencyDiscoverCard(
  agency: LocalAgency,
  onSelect: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(Color(0xFF190F30))
      .border(1.2.dp, Color(0xFF7C4DFF).copy(alpha = 0.4f), RoundedCornerShape(16.dp))
      .clickable(onClick = onSelect)
      .padding(14.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(46.dp)
          .clip(CircleShape)
          .background(Color(0xFF2C1B4D)),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = agency.countryFlag.ifBlank { "🏢" },
          fontSize = 22.sp
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = agency.agencyName,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.width(6.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(Color(0xFF00E676).copy(alpha = 0.2f))
              .padding(horizontal = 5.dp, vertical = 1.dp)
          ) {
            Text("ACTIVE", color = Color(0xFF00E676), fontSize = 9.sp, fontWeight = FontWeight.Black)
          }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = "Agency ID: ${agency.agencyId} • ${agency.country}",
          color = Color(0xFF00E5FF),
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium
        )

        Text(
          text = "Owner: ${agency.ownerName} • ${agency.hostCount} Hosts",
          color = Color.White.copy(alpha = 0.65f),
          fontSize = 11.sp
        )
      }

      Button(
        onClick = onSelect,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF)),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
      ) {
        Text("Select", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}

// =============================================================================
// 2. ACTIVE HOST CENTER & 10-DAY REWARD SYSTEM (Modes for Verified Hosts)
// =============================================================================
@Composable
private fun ActiveHostCenterContent(
  profile: com.example.ui.storage.UserFullProfile,
  cycle: LocalHostCycleProgress,
  days: List<LocalHostDailyProgress>,
  isClaiming: Boolean,
  onBack: () -> Unit,
  onStartStream: () -> Unit,
  onClaimRewards: () -> Unit
) {
  val context = LocalContext.current
  val todayStamp = remember {
    java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
  }

  // Find today's daily progress
  val currentDayIndex = (cycle.completedDays + 1).coerceIn(1, 10)
  val todayProgress = days.find { it.dateStamp == todayStamp }
    ?: days.getOrNull(currentDayIndex - 1)
    ?: LocalHostDailyProgress(dayNumber = currentDayIndex)

  val requiredSec = SocialAgencyStorage.REQUIRED_DAILY_LIVE_SECONDS // 4980s = 1h 23m
  val streamedSec = todayProgress.eligibleSeconds
  val progressFraction = (streamedSec.toFloat() / requiredSec.toFloat()).coerceIn(0f, 1f)

  val streamedHours = streamedSec / 3600
  val streamedMinutes = (streamedSec % 3600) / 60

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 40.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Top Header
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color(0xFF22133D))
            .clickable(onClick = onBack)
            .testTag("host_center_back_button"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Filled.Verified,
            contentDescription = null,
            tint = Color(0xFF00B0FF),
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Host Center",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
          )
        }

        // Live shortcut
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color(0xFFFF007F).copy(alpha = 0.2f))
            .border(1.dp, Color(0xFFFF007F), CircleShape)
            .clickable(onClick = onStartStream),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Filled.Videocam,
            contentDescription = "Go Live",
            tint = Color(0xFFFF007F),
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }

    // 2. Official Host & Agency Identity Card
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(22.dp))
          .background(
            Brush.horizontalGradient(
              listOf(Color(0xFF1B0B38), Color(0xFF27104D), Color(0xFF15082C))
            )
          )
          .border(
            width = 1.4.dp,
            brush = Brush.linearGradient(
              listOf(Color(0xFF00E5FF), Color(0xFF7C4DFF), Color(0xFFFF00A0))
            ),
            shape = RoundedCornerShape(22.dp)
          )
          .padding(16.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          AvatarFrameOverlay(
            size = 62.dp,
            frame = null,
            profileImageUri = profile.profileImageUri,
            avatarRes = R.drawable.img_streamer_girl
          )

          Spacer(modifier = Modifier.width(14.dp))

          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = profile.username,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
              )
              Spacer(modifier = Modifier.width(6.dp))
              // Blue HOST Badge
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(Color(0xFF1565C0))
                  .border(1.dp, Color(0xFF42A5F5), RoundedCornerShape(6.dp))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text("HOST", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black)
              }
              Spacer(modifier = Modifier.width(4.dp))
              // Red AGENCY Badge
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(Color(0xFFC62828))
                  .border(1.dp, Color(0xFFEF5350), RoundedCornerShape(6.dp))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text("AGENCY", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black)
              }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = "PID: ${profile.userId} • Agency: ${profile.agencyName ?: "Official Partner"}",
              color = Color(0xFF00E5FF),
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )

            Text(
              text = "Status: Official Active Host • ${profile.country}",
              color = Color(0xFF00E676),
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }
    }

    // 3. DAILY LIVE REQUIREMENT (1h 23m / 4,000 Coins)
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(20.dp))
          .background(Color(0xFF140828))
          .border(1.2.dp, Color(0xFF00E5FF), RoundedCornerShape(20.dp))
          .padding(18.dp)
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "DAILY LIVE REQUIREMENT",
                color = Color(0xFF00E5FF),
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
              )
              Text(
                text = "Required: 1 Hour 23 Minutes",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (progressFraction >= 1f) Color(0xFF00E676).copy(alpha = 0.2f) else Color(0xFFFF9100).copy(alpha = 0.2f))
                .border(1.dp, if (progressFraction >= 1f) Color(0xFF00E676) else Color(0xFFFF9100), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = if (progressFraction >= 1f) "✓ Completed" else "In Progress",
                color = if (progressFraction >= 1f) Color(0xFF00E676) else Color(0xFFFF9100),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Progress Bar
          LinearProgressIndicator(
            progress = { progressFraction },
            modifier = Modifier
              .fillMaxWidth()
              .height(10.dp)
              .clip(RoundedCornerShape(5.dp)),
            color = Color(0xFF00E5FF),
            trackColor = Color(0xFF2A1647)
          )

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "Live Streamed: ${streamedHours}h ${streamedMinutes}m / 1h 23m",
              color = Color.White.copy(alpha = 0.85f),
              fontSize = 12.sp
            )
            Text(
              text = "Daily Reward: 4,000 Diamonds 💎",
              color = Color(0xFF00E5FF),
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "Only real verified live broadcast time is counted. Offline, background, or simulated time is ineligible.",
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 10.5.sp
          )
        }
      }
    }

    // 4. 10-DAY REWARD CYCLE & CLAIM SYSTEM
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(20.dp))
          .background(
            Brush.verticalGradient(
              listOf(Color(0xFF2E1B00), Color(0xFF1B0E00), Color(0xFF100700))
            )
          )
          .border(
            1.4.dp,
            Brush.linearGradient(listOf(Color(0xFFFFD700), Color(0xFFFF9100))),
            RoundedCornerShape(20.dp)
          )
          .padding(18.dp)
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "10-DAY HOST EARNING CYCLE",
                color = Color(0xFFFFD700),
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
              )
              Text(
                text = "10 Completed Days = 40,000 Diamonds",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
              )
            }

            Text(
              text = "${cycle.completedDays} / 10 Days",
              color = Color(0xFFFFD700),
              fontSize = 16.sp,
              fontWeight = FontWeight.Black
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 10-Day Progress Bar
          val cycleFraction = (cycle.completedDays.toFloat() / 10f).coerceIn(0f, 1f)
          LinearProgressIndicator(
            progress = { cycleFraction },
            modifier = Modifier
              .fillMaxWidth()
              .height(10.dp)
              .clip(RoundedCornerShape(5.dp)),
            color = Color(0xFFFFD700),
            trackColor = Color(0xFF422800)
          )

          Spacer(modifier = Modifier.height(14.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Pending Claimable Rewards",
                color = Color(0xFFFFE082),
                fontSize = 11.5.sp
              )
              Text(
                text = "${cycle.pendingRewardCoins} Diamonds",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
              )
            }

            // Real Claim Button
            Button(
              onClick = onClaimRewards,
              enabled = cycle.pendingRewardCoins > 0L && !isClaiming,
              shape = RoundedCornerShape(14.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFB300),
                disabledContainerColor = Color(0xFF5D4037)
              ),
              contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
              if (isClaiming) {
                CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
              } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Filled.MonetizationOn,
                    contentDescription = null,
                    tint = if (cycle.pendingRewardCoins > 0L) Color.Black else Color.Gray,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "CLAIM",
                    color = if (cycle.pendingRewardCoins > 0L) Color.Black else Color.Gray,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp
                  )
                }
              }
            }
          }
        }
      }
    }

    // 5. DAILY PROGRESS BREAKDOWN (Day 1 to Day 10)
    item {
      Text(
        text = "Daily Progress (10-Day Cycle)",
        color = Color.White,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
      )
    }

    items(days) { dayItem ->
      DailyCycleRow(day = dayItem)
    }

    // 6. Action CTA: Start Live Stream
    item {
      Spacer(modifier = Modifier.height(8.dp))
      Button(
        onClick = onStartStream,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
      ) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.horizontalGradient(
                listOf(Color(0xFFFF007F), Color(0xFF7C4DFF), Color(0xFF00E5FF))
              )
            ),
          contentAlignment = Alignment.Center
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Filled.Videocam, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "START LIVE BROADCAST",
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 15.sp
            )
          }
        }
      }
    }
  }
}

@Composable
private fun DailyCycleRow(day: LocalHostDailyProgress) {
  val h = day.eligibleSeconds / 3600
  val m = (day.eligibleSeconds % 3600) / 60

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(Color(0xFF160B28))
      .border(
        1.dp,
        if (day.isCompleted) Color(0xFF00E676).copy(alpha = 0.5f) else Color(0xFF351C5E),
        RoundedCornerShape(12.dp)
      )
      .padding(horizontal = 14.dp, vertical = 10.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(
              if (day.isCompleted) Color(0xFF00E676).copy(alpha = 0.2f)
              else Color(0xFF281446)
            ),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "D${day.dayNumber}",
            color = if (day.isCompleted) Color(0xFF00E676) else Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Text(
            text = "DAY ${day.dayNumber}",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "${h}h ${m}m / 1h 23m",
            color = Color(0xFF00E5FF),
            fontSize = 11.sp
          )
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "+4,000 Diamonds",
          color = Color(0xFF00E5FF),
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(8.dp))
        if (day.isCompleted) {
          Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = "Completed",
            tint = Color(0xFF00E676),
            modifier = Modifier.size(18.dp)
          )
        } else {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(Color(0xFF2A1647))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = if (day.eligibleSeconds > 0) "In Progress" else "Pending",
              color = Color.White.copy(alpha = 0.6f),
              fontSize = 10.sp
            )
          }
        }
      }
    }
  }
}
