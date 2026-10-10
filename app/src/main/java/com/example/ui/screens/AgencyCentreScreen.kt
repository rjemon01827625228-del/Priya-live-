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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.storage.AgencyHost
import com.example.ui.storage.HostRequestItem
import com.example.ui.storage.LocalAgency
import com.example.ui.storage.SocialAgencyStorage
import com.example.ui.storage.SocialUser
import com.example.ui.storage.UserFullProfile
import com.example.ui.storage.UserProfileStorage

@Composable
fun AgencyCentreScreen(
  userProfile: UserFullProfile,
  onBack: () -> Unit,
  onOpenUserProfile: (String) -> Unit = {}
) {
  val context = LocalContext.current
  var liveProfile by remember { mutableStateOf(UserProfileStorage.getProfile(context)) }
  val activeUserId = liveProfile.userId

  // Agency Access check
  val hasAgencyAccess = liveProfile.hasAgencyPermission ||
    liveProfile.isAgencyOwner ||
    liveProfile.isPanelAuthorized ||
    liveProfile.agencyStatus.equals("owner", ignoreCase = true)

  var agencyName by remember(liveProfile.agencyName, liveProfile.username) {
    mutableStateOf(liveProfile.agencyName?.takeIf { it.isNotBlank() } ?: "${liveProfile.username} Agency")
  }
  var agencyId by remember(liveProfile.agencyId, activeUserId) {
    mutableStateOf(liveProfile.agencyId.ifBlank { activeUserId })
  }
  var agencyCountry by remember(liveProfile.country) { mutableStateOf(liveProfile.country.ifBlank { "Bangladesh" }) }
  var agencyFlag by remember(liveProfile.countryFlag) { mutableStateOf(liveProfile.countryFlag.ifBlank { "🇧🇩" }) }

  var hostsList by remember { mutableStateOf<List<AgencyHost>>(emptyList()) }
  var requestsList by remember { mutableStateOf<List<HostRequestItem>>(emptyList()) }
  var searchQuery by remember { mutableStateOf("") }
  var countryIdSearchQuery by remember { mutableStateOf("") }
  var activeTab by remember { mutableStateOf("hosts") } // "hosts" or "requests"

  // List of active agencies from local storage
  var allRealAgencies by remember { mutableStateOf<List<LocalAgency>>(emptyList()) }

  var showInviteDialog by remember { mutableStateOf(false) }
  var showEditAgencyDialog by remember { mutableStateOf(false) }
  var selectedHostForDetail by remember { mutableStateOf<AgencyHost?>(null) }

  fun reloadData() {
    liveProfile = UserProfileStorage.getProfile(context)
    hostsList = SocialAgencyStorage.getAgencyHosts(context, activeUserId)
    requestsList = SocialAgencyStorage.getAgencyHostRequests(context, activeUserId)
    allRealAgencies = SocialAgencyStorage.getAllLocalAgencies(context)
  }

  LaunchedEffect(activeUserId, hasAgencyAccess) {
    if (hasAgencyAccess) {
      SocialAgencyStorage.createOrUpdateAgency(
        context = context,
        agencyId = agencyId,
        agencyName = agencyName,
        ownerUid = activeUserId,
        ownerPid = activeUserId,
        ownerName = liveProfile.username,
        country = agencyCountry,
        countryFlag = agencyFlag,
        logoUrl = liveProfile.agencyLogoUri ?: liveProfile.profileImageUri
      )
    }
    reloadData()
  }

  // Filtered hosts based on search query (for Agency Management view)
  val filteredHosts = remember(hostsList, searchQuery) {
    if (searchQuery.isBlank()) hostsList
    else {
      val q = searchQuery.trim().lowercase()
      hostsList.filter {
        it.name.lowercase().contains(q) || it.hostId.contains(q)
      }
    }
  }

  // Filtered real agencies based on Country ID (for non-Agency users)
  val userCountryCode = remember(liveProfile.country) {
    SocialAgencyStorage.resolveCountryCode(liveProfile.country.ifBlank { "Bangladesh" })
  }
  val effectiveCountryFilter = remember(countryIdSearchQuery, liveProfile.country) {
    val q = countryIdSearchQuery.trim()
    if (q.isNotBlank()) q else liveProfile.country.ifBlank { "Bangladesh" }
  }
  val availableCountryAgencies = remember(allRealAgencies, effectiveCountryFilter) {
    allRealAgencies.filter { agency ->
      SocialAgencyStorage.matchesCountryQuery(agency, effectiveCountryFilter)
    }
  }

  val topSafePadding = com.example.ui.components.rememberResponsiveTopBarPadding(extraTopSpacing = 8.dp)
  val bottomSafePadding = com.example.ui.components.rememberResponsiveNavigationBarPadding(extraBottomSpacing = 72.dp)

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          listOf(
            Color(0xFF0F081D),
            Color(0xFF090412),
            Color(0xFF030107)
          )
        )
      )
  ) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = topSafePadding, bottom = bottomSafePadding),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 1. TOP HEADER (Back Button, "agency centre", Edit button)
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Left back arrow button in circular frame
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(Color(0x33B026FF))
              .border(1.2.dp, Color(0xFFB026FF).copy(alpha = 0.6f), CircleShape)
              .clickable(onClick = onBack)
              .testTag("agency_back_button"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = Color.White,
              modifier = Modifier.size(20.dp)
            )
          }

          // Center: Badge + "agency centre" in neon gradient
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Filled.WorkspacePremium,
              contentDescription = null,
              tint = Color(0xFFFFD700),
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "agency centre",
              fontSize = 22.sp,
              fontWeight = FontWeight.Black,
              letterSpacing = 0.5.sp,
              color = Color(0xFFE040FB)
            )
          }

          // Right: Edit pencil icon in rounded glowing box (ONLY visible if user has Admin-granted Agency Access)
          if (hasAgencyAccess) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x33B026FF))
                .border(1.2.dp, Color(0xFFFF26B9), RoundedCornerShape(12.dp))
                .clickable { showEditAgencyDialog = true }
                .testTag("agency_edit_button"),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = "Edit Agency",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }
          } else {
            Spacer(modifier = Modifier.size(40.dp))
          }
        }
      }

      if (!hasAgencyAccess) {
        // =====================================================================
        // USER DISCOVERY MODE (User does NOT have Admin-granted Agency Access)
        // Shows real available Agencies for User's Country ID or Searched Country ID.
        // Never displays Agency Management functions or fake/demo agencies.
        // =====================================================================
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(18.dp))
              .background(
                Brush.horizontalGradient(
                  listOf(Color(0xFF1B0C38), Color(0xFF27104D))
                )
              )
              .border(1.2.dp, Color(0xFF00E5FF).copy(alpha = 0.6f), RoundedCornerShape(18.dp))
              .padding(14.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "Your Country Agency Region",
                  color = Color.White.copy(alpha = 0.7f),
                  fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "${liveProfile.countryFlag.ifBlank { "🇧🇩" }} ${liveProfile.country.ifBlank { "Bangladesh" }} (Country ID: $userCountryCode)",
                  color = Color(0xFF00E5FF),
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold
                )
              }
              Icon(
                imageVector = Icons.Filled.WorkspacePremium,
                contentDescription = null,
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(28.dp)
              )
            }
          }
        }

        // Country ID Search Bar
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .clip(RoundedCornerShape(26.dp))
              .background(Color(0xFF140A26))
              .border(1.2.dp, Color(0xFF00E5FF).copy(alpha = 0.5f), RoundedCornerShape(26.dp))
              .padding(horizontal = 14.dp),
            contentAlignment = Alignment.CenterStart
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search Country ID",
                tint = Color(0xFF00E5FF),
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Box(modifier = Modifier.weight(1f)) {
                if (countryIdSearchQuery.isEmpty()) {
                  Text(
                    text = "Search by Country ID (e.g. BD, IN, SA, US)...",
                    color = Color.White.copy(alpha = 0.45f),
                    fontSize = 13.5.sp
                  )
                }
                OutlinedTextField(
                  value = countryIdSearchQuery,
                  onValueChange = { countryIdSearchQuery = it },
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFF00E5FF)
                  ),
                  singleLine = true,
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("agency_country_search_input")
                )
              }
              if (countryIdSearchQuery.isNotEmpty()) {
                Icon(
                  imageVector = Icons.Filled.Close,
                  contentDescription = "Clear Search",
                  tint = Color.White.copy(alpha = 0.6f),
                  modifier = Modifier
                    .size(18.dp)
                    .clickable { countryIdSearchQuery = "" }
                )
              }
            }
          }
        }

        // Available Real Agencies Header
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = if (countryIdSearchQuery.isBlank()) {
                "Available Agencies (${liveProfile.country.ifBlank { "Bangladesh" }})"
              } else {
                "Agencies for Country ID: ${countryIdSearchQuery.trim().uppercase()}"
              },
              color = Color.White,
              fontSize = 15.5.sp,
              fontWeight = FontWeight.Black
            )
            Text(
              text = "${availableCountryAgencies.size} Found",
              color = Color(0xFF00E5FF),
              fontSize = 12.5.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        if (availableCountryAgencies.isEmpty()) {
          item {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0x331B0C38))
                .border(1.dp, Color(0x33B026FF), RoundedCornerShape(20.dp))
                .padding(vertical = 42.dp, horizontal = 20.dp)
                .testTag("no_available_agency_box"),
              contentAlignment = Alignment.Center
            ) {
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
              ) {
                Icon(
                  imageVector = Icons.Filled.Group,
                  contentDescription = null,
                  tint = Color(0xFF00E5FF),
                  modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                  text = "No Available Agency",
                  color = Color.White,
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = if (countryIdSearchQuery.isNotBlank()) {
                    "No active agency found for Country ID '${countryIdSearchQuery.trim()}'."
                  } else {
                    "No active agency available in ${liveProfile.country.ifBlank { "your country" }} right now."
                  },
                  color = Color.White.copy(alpha = 0.6f),
                  fontSize = 12.5.sp,
                  textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
              }
            }
          }
        } else {
          items(availableCountryAgencies, key = { it.agencyId }) { agency ->
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF140C28))
                .border(
                  width = 1.2.dp,
                  brush = Brush.horizontalGradient(
                    listOf(Color(0xFFB026FF).copy(alpha = 0.6f), Color(0xFF00E5FF).copy(alpha = 0.4f))
                  ),
                  shape = RoundedCornerShape(18.dp)
                )
                .padding(horizontal = 14.dp, vertical = 12.dp)
                .testTag("real_agency_card_${agency.agencyId}")
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
                      .size(52.dp)
                      .clip(CircleShape)
                      .border(2.dp, Color(0xFFFF26B9), CircleShape)
                      .padding(2.dp)
                      .clip(CircleShape)
                  ) {
                    SafeAgencyAvatar(
                      avatarRes = R.drawable.img_profile_max,
                      avatarUri = agency.logoUrl,
                      contentDescription = agency.agencyName,
                      modifier = Modifier.fillMaxSize()
                    )
                  }
                  Spacer(modifier = Modifier.width(12.dp))
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = agency.agencyName,
                      color = Color.White,
                      fontSize = 15.sp,
                      fontWeight = FontWeight.Bold,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                      text = "Agency ID: ${agency.agencyId} • Owner: ${agency.ownerName.ifBlank { "Verified" }}",
                      color = Color(0xFF00E5FF),
                      fontSize = 11.5.sp,
                      fontWeight = FontWeight.Medium,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                      text = "${agency.countryFlag.ifBlank { "🇧🇩" }} ${agency.country} (${agency.countryCode.ifBlank { SocialAgencyStorage.resolveCountryCode(agency.country) }})",
                      color = Color.White.copy(alpha = 0.75f),
                      fontSize = 11.sp
                    )
                  }
                }
                Spacer(modifier = Modifier.width(8.dp))
                val isAlreadyJoined = liveProfile.agencyId == agency.agencyId && liveProfile.agencyStatus == "active"
                val isPendingThis = liveProfile.agencyId == agency.agencyId && liveProfile.agencyStatus == "pending"
                Button(
                  onClick = {
                    if (!isAlreadyJoined && !isPendingThis) {
                      SocialAgencyStorage.sendHostJoinRequest(context, agency.agencyId) { _, msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                        liveProfile = UserProfileStorage.getProfile(context)
                      }
                    }
                  },
                  enabled = !isAlreadyJoined && !isPendingThis,
                  colors = ButtonDefaults.buttonColors(
                    containerColor = when {
                      isAlreadyJoined -> Color(0xFF00E676)
                      isPendingThis -> Color(0xFFFF9100)
                      else -> Color(0xFFFF26B9)
                    }
                  ),
                  shape = RoundedCornerShape(14.dp),
                  contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                  Text(
                    text = when {
                      isAlreadyJoined -> "Joined"
                      isPendingThis -> "Pending"
                      else -> "Join"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                  )
                }
              }
            }
          }
        }
      } else {
      // 2. AGENCY OWNER / PROFILE CARD (Matches Screenshot exactly)
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(
              Brush.horizontalGradient(
                listOf(
                  Color(0xFF1B0C38),
                  Color(0xFF27104D),
                  Color(0xFF350E5E)
                )
              )
            )
            .border(
              width = 1.4.dp,
              brush = Brush.linearGradient(
                listOf(
                  Color(0xFFFF26B9).copy(alpha = 0.8f),
                  Color(0xFFB026FF).copy(alpha = 0.6f),
                  Color(0xFF00E5FF).copy(alpha = 0.4f)
                )
              ),
              shape = RoundedCornerShape(22.dp)
            )
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Left circular avatar with neon pink ring and pencil edit badge
            Box(
              modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .border(2.5.dp, Color(0xFFFF26B9), CircleShape)
                .padding(3.dp)
                .clip(CircleShape)
                .background(Color(0xFF2A0845))
                .clickable { showEditAgencyDialog = true }
            ) {
              if (!userProfile.agencyLogoUri.isNullOrBlank()) {
                AsyncImage(
                  model = userProfile.agencyLogoUri,
                  contentDescription = "Agency Logo",
                  modifier = Modifier.fillMaxSize(),
                  contentScale = ContentScale.Crop
                )
              } else if (!userProfile.profileImageUri.isNullOrBlank()) {
                AsyncImage(
                  model = userProfile.profileImageUri,
                  contentDescription = "Agency Owner Avatar",
                  modifier = Modifier.fillMaxSize(),
                  contentScale = ContentScale.Crop
                )
              } else {
                Image(
                  painter = painterResource(id = R.drawable.img_profile_max),
                  contentDescription = "Avatar",
                  modifier = Modifier.fillMaxSize(),
                  contentScale = ContentScale.Crop
                )
              }

              // Tiny edit pencil badge at bottom right of avatar
              Box(
                modifier = Modifier
                  .size(24.dp)
                  .align(Alignment.BottomEnd)
                  .clip(CircleShape)
                  .background(Color(0xFFB026FF))
                  .border(1.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Filled.Edit,
                  contentDescription = "Edit Avatar",
                  tint = Color.White,
                  modifier = Modifier.size(13.dp)
                )
              }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Details Column: ID, Country, Name
            Column(
              modifier = Modifier.weight(1f),
              verticalArrangement = Arrangement.Center
            ) {
              // ID
              Text(
                text = "ID  :  $agencyId",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
              )

              Spacer(modifier = Modifier.height(6.dp))

              // Country + Flag
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "Country  : ",
                  color = Color.White.copy(alpha = 0.85f),
                  fontSize = 13.5.sp,
                  fontWeight = FontWeight.Medium
                )
                Text(
                  text = "$agencyFlag $agencyCountry",
                  color = Color.White,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold
                )
              }

              Spacer(modifier = Modifier.height(6.dp))

              // Name
              Text(
                text = "Name  :  $agencyName",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }

            // Big shiny crest on the right
            Icon(
              imageVector = Icons.Filled.WorkspacePremium,
              contentDescription = null,
              tint = Color(0xFFFFD700),
              modifier = Modifier
                .size(38.dp)
                .padding(end = 4.dp)
            )
          }
        }
      }

      // 3. TWO ACTION PILLS: [👥 host section >] and [👤+ invite >]
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // [👥 host section >]
          Box(
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .clip(RoundedCornerShape(24.dp))
              .background(
                Brush.horizontalGradient(
                  if (activeTab == "hosts") listOf(Color(0xFFB026FF), Color(0xFF7928CA))
                  else listOf(Color(0xFF331652), Color(0xFF220C38))
                )
              )
              .border(
                1.5.dp,
                if (activeTab == "hosts") Color(0xFFE040FB) else Color(0x66B026FF),
                RoundedCornerShape(24.dp)
              )
              .clickable { activeTab = "hosts" }
              .testTag("agency_host_section_button"),
            contentAlignment = Alignment.Center
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Icon(
                imageVector = Icons.Filled.Group,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "host section",
                color = Color.White,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = ">",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          // [👤+ invite >]
          Box(
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .clip(RoundedCornerShape(24.dp))
              .background(
                Brush.horizontalGradient(
                  listOf(Color(0xFFFF26B9), Color(0xFFD81B60))
                )
              )
              .border(1.5.dp, Color(0xFFFF80DF), RoundedCornerShape(24.dp))
              .clickable { showInviteDialog = true }
              .testTag("agency_invite_button"),
            contentAlignment = Alignment.Center
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Icon(
                imageVector = Icons.Filled.PersonAdd,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "invite",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = ">",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      // 4. SEARCH BAR (Search host...)
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(Color(0xFF140A26))
            .border(1.2.dp, Color(0xFF00E5FF).copy(alpha = 0.5f), RoundedCornerShape(26.dp))
            .padding(horizontal = 14.dp),
          contentAlignment = Alignment.CenterStart
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Filled.Search,
              contentDescription = "Search",
              tint = Color(0xFF00E5FF),
              modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Box(modifier = Modifier.weight(1f)) {
              if (searchQuery.isEmpty()) {
                Text(
                  text = "Search host by ID or Name...",
                  color = Color.White.copy(alpha = 0.45f),
                  fontSize = 14.sp
                )
              }
              OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = Color.Transparent,
                  unfocusedBorderColor = Color.Transparent,
                  focusedTextColor = Color.White,
                  unfocusedTextColor = Color.White,
                  cursorColor = Color(0xFF00E5FF)
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
              )
            }

            if (searchQuery.isNotEmpty()) {
              Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Clear Search",
                tint = Color.White.copy(alpha = 0.6f),
                modifier = Modifier
                  .size(18.dp)
                  .clickable { searchQuery = "" }
              )
              Spacer(modifier = Modifier.width(8.dp))
            }

            Icon(
              imageVector = Icons.Filled.FilterList,
              contentDescription = "Filter",
              tint = Color(0xFF00E5FF),
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }

      // 5. HOST REQUESTS SECTION (From screenshot file_00000000f6f082099d544333b394a23e.png)
      if (requestsList.isNotEmpty()) {
        item {
          Spacer(modifier = Modifier.height(4.dp))
          // Neon slanted banner: < 👥 HOST REQUEST >
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(44.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(
                Brush.horizontalGradient(
                  listOf(
                    Color(0xFF26004C),
                    Color(0xFF5B0E8E),
                    Color(0xFF26004C)
                  )
                )
              )
              .border(
                1.4.dp,
                Brush.horizontalGradient(
                  listOf(Color(0xFF00E5FF), Color(0xFFFF26B9), Color(0xFF00E5FF))
                ),
                RoundedCornerShape(14.dp)
              ),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = "〈 ", color = Color(0xFF00E5FF), fontSize = 16.sp, fontWeight = FontWeight.Bold)
              Icon(
                imageVector = Icons.Filled.Group,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "HOST REQUEST (${requestsList.size})",
                color = Color.White,
                fontSize = 15.5.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
              )
              Text(text = " 〉", color = Color(0xFF00E5FF), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        // Host Request Cards with NO and YES buttons
        items(requestsList) { request ->
          HostRequestCard(
            request = request,
            onAccept = {
              SocialAgencyStorage.acceptHostRequest(context, activeUserId, request.requestId)
              reloadData()
              Toast.makeText(context, "${request.username} accepted as Host!", Toast.LENGTH_SHORT).show()
            },
            onReject = {
              SocialAgencyStorage.rejectHostRequest(context, activeUserId, request.requestId)
              reloadData()
              Toast.makeText(context, "Request rejected.", Toast.LENGTH_SHORT).show()
            }
          )
        }
      }

      // 6. HOSTS LIST HEADER & CARDS
      item {
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Official Hosts (${filteredHosts.size})",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black
          )

          Text(
            text = if (filteredHosts.isEmpty()) "0 Host" else "${filteredHosts.size} Total",
            color = Color(0xFF00E5FF),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      if (filteredHosts.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(20.dp))
              .background(Color(0x331B0C38))
              .border(1.dp, Color(0x33B026FF), RoundedCornerShape(20.dp))
              .padding(vertical = 36.dp, horizontal = 20.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              Text(text = "🎧", fontSize = 42.sp)
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = if (searchQuery.isNotEmpty()) "Host not found in this agency" else "0 Hosts in this Agency",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = if (searchQuery.isNotEmpty()) "Try searching with a different Priya ID or Name" else "Tap 'invite' above to recruit verified hosts to your agency!",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.5.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
            }
          }
        }
      } else {
        items(filteredHosts) { host ->
          HostItemCard(
            host = host,
            onClick = { selectedHostForDetail = host }
          )
        }
      }
      } // End of hasAgencyAccess else block
    }
  }

  // ---------------------------------------------------------------------------
  // DIALOGS
  // ---------------------------------------------------------------------------

  // 1. Invite Host Dialog (Search user by ID & send invitation)
  if (showInviteDialog) {
    InviteHostDialog(
      onDismiss = { showInviteDialog = false },
      onInviteSent = { targetUserId ->
        val user = SocialAgencyStorage.communityUsers.find { it.id == targetUserId }
        val targetName = user?.username ?: "User $targetUserId"
        val success = SocialAgencyStorage.sendAgencyInvitation(
          context = context,
          agencyId = agencyId,
          agencyName = agencyName,
          ownerName = userProfile.username,
          targetUserId = targetUserId
        )
        if (success) {
          Toast.makeText(context, "Invitation sent to $targetName's Inbox!", Toast.LENGTH_LONG).show()
        } else {
          Toast.makeText(context, "Invitation already pending for $targetName!", Toast.LENGTH_SHORT).show()
        }
        showInviteDialog = false
      }
    )
  }

  // 2. Edit Agency Dialog
  if (showEditAgencyDialog && hasAgencyAccess) {
    EditAgencyDialog(
      currentName = agencyName,
      currentCountry = agencyCountry,
      onDismiss = { showEditAgencyDialog = false },
      onSave = { newName, newCountry ->
        agencyName = newName
        agencyCountry = newCountry
        UserProfileStorage.updateAgency(
          context = context,
          isJoined = true,
          isOwner = true,
          name = newName,
          logoUri = liveProfile.agencyLogoUri,
          level = "Diamond Agency"
        )
        SocialAgencyStorage.createOrUpdateAgency(
          context = context,
          agencyId = agencyId,
          agencyName = newName,
          ownerUid = activeUserId,
          ownerPid = activeUserId,
          ownerName = liveProfile.username,
          country = newCountry,
          countryFlag = agencyFlag,
          logoUrl = liveProfile.agencyLogoUri ?: liveProfile.profileImageUri
        )
        reloadData()
        showEditAgencyDialog = false
        Toast.makeText(context, "Agency details updated!", Toast.LENGTH_SHORT).show()
      }
    )
  }

  // 3. Host Profile Detail Dialog
  if (selectedHostForDetail != null) {
    HostProfileDetailDialog(
      host = selectedHostForDetail!!,
      onDismiss = { selectedHostForDetail = null }
    )
  }
}

/**
 * Host Card matching Screenshot file_00000000a84c82069a24f66ba46327ba.png exactly.
 */
@Composable
private fun HostItemCard(
  host: AgencyHost,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .background(Color(0xFF140C28))
      .border(
        width = 1.2.dp,
        brush = Brush.horizontalGradient(
          listOf(Color(0xFFB026FF).copy(alpha = 0.6f), Color(0xFF00E5FF).copy(alpha = 0.3f))
        ),
        shape = RoundedCornerShape(18.dp)
      )
      .clickable(onClick = onClick)
      .padding(horizontal = 12.dp, vertical = 10.dp)
      .testTag("host_card_${host.hostId}")
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Left: Host avatar with green online indicator dot
      Box(
        modifier = Modifier.size(56.dp)
      ) {
        Box(
          modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .border(2.dp, Color(0xFFFF26B9), CircleShape)
            .padding(2.dp)
            .clip(CircleShape)
        ) {
          SafeAgencyAvatar(
            avatarRes = host.avatarRes,
            avatarUri = host.avatarUri,
            contentDescription = "Host Avatar",
            modifier = Modifier.fillMaxSize()
          )
        }

        // Green Online Dot Indicator
        if (host.isOnline) {
          Box(
            modifier = Modifier
              .size(13.dp)
              .align(Alignment.BottomEnd)
              .clip(CircleShape)
              .background(Color(0xFF00E676))
              .border(1.5.dp, Color(0xFF0F081D), CircleShape)
          )
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      // Center Column: Name + Heart, ID, Flag + Country
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.Center
      ) {
        // Name
        Text(
          text = host.name,
          color = Color.White,
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(3.dp))

        // ID pill
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = null,
            tint = Color(0xFF00E5FF),
            modifier = Modifier.size(12.dp)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = "ID: ${host.hostId}",
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Medium
          )
        }

        Spacer(modifier = Modifier.height(3.dp))

        // Country + Flag
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = host.flag, fontSize = 12.sp)
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = host.country,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 11.sp
          )
        }
      }

      // Right: [👤 Host] pill + `>` chevron button
      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
              Brush.horizontalGradient(listOf(Color(0xFFFF26B9), Color(0xFFD81B60)))
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Filled.Person,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Host",
              color = Color.White,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        Spacer(modifier = Modifier.width(6.dp))

        Box(
          modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(Color(0x33B026FF)),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = ">",
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

/**
 * Host Request Card with [ NO ] and [ YES ] buttons matching Screenshot file_00000000f6f082099d544333b394a23e.png.
 */
@Composable
private fun HostRequestCard(
  request: HostRequestItem,
  onAccept: () -> Unit,
  onReject: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .background(Color(0xFF140A26))
      .border(
        width = 1.2.dp,
        brush = Brush.horizontalGradient(
          listOf(Color(0xFFFF26B9), Color(0xFF00E5FF))
        ),
        shape = RoundedCornerShape(18.dp)
      )
      .padding(horizontal = 12.dp, vertical = 10.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Avatar with green dot
      Box(modifier = Modifier.size(54.dp)) {
        Box(
          modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .border(2.dp, Color(0xFFFF26B9), CircleShape)
            .padding(2.dp)
            .clip(CircleShape)
        ) {
          SafeAgencyAvatar(
            avatarRes = request.avatarRes,
            avatarUri = request.avatarUri,
            contentDescription = "Requester Avatar",
            modifier = Modifier.fillMaxSize()
          )
        }

        Box(
          modifier = Modifier
            .size(12.dp)
            .align(Alignment.BottomEnd)
            .clip(CircleShape)
            .background(Color(0xFF00E676))
            .border(1.dp, Color(0xFF0F081D), CircleShape)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      // Info
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.Center
      ) {
        Text(
          text = request.username,
          color = Color.White,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "ID: ${request.userId}",
          color = Color.White.copy(alpha = 0.8f),
          fontSize = 11.5.sp
        )
        Text(
          text = "${request.flag} ${request.country}",
          color = Color.White.copy(alpha = 0.65f),
          fontSize = 11.sp
        )
      }

      // [ NO ] and [ YES ] Action Buttons
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // [ NO ] Red gradient button
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
              Brush.horizontalGradient(listOf(Color(0xFFFF1744), Color(0xFFD50000)))
            )
            .clickable(onClick = onReject)
            .padding(horizontal = 14.dp, vertical = 7.dp)
            .testTag("request_no_button_${request.requestId}"),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "NO",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black
          )
        }

        // [ YES ] Cyan/teal gradient button
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
              Brush.horizontalGradient(listOf(Color(0xFF00E5FF), Color(0xFF00B0FF)))
            )
            .clickable(onClick = onAccept)
            .padding(horizontal = 14.dp, vertical = 7.dp)
            .testTag("request_yes_button_${request.requestId}"),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "YES",
            color = Color.Black,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black
          )
        }
      }
    }
  }
}

/**
 * Invite Host Dialog: Allows search by Priya ID / User ID and sending host invitation.
 */
@Composable
private fun InviteHostDialog(
  onDismiss: () -> Unit,
  onInviteSent: (String) -> Unit
) {
  var inputId by remember { mutableStateOf("") }
  var searchedUser by remember { mutableStateOf<SocialUser?>(null) }
  var hasSearched by remember { mutableStateOf(false) }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = Color(0xFF140A28),
      border = BorderStroke(1.4.dp, Color(0xFFFF26B9)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "Invite Host to Agency",
          color = Color.White,
          fontSize = 18.sp,
          fontWeight = FontWeight.Black
        )
        Text(
          text = "Enter candidate's 7-digit Priya ID",
          color = Color.White.copy(alpha = 0.6f),
          fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
          value = inputId,
          onValueChange = {
            inputId = it
            hasSearched = false
          },
          label = { Text("Priya ID / User ID") },
          placeholder = { Text("e.g. 5487293 or 7129481") },
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF00E5FF),
            unfocusedBorderColor = Color(0xFFB026FF),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedLabelColor = Color(0xFF00E5FF)
          ),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Search Button
        Button(
          onClick = {
            val cleanId = inputId.trim().filter { it.isDigit() }
            if (cleanId.isNotBlank()) {
              searchedUser = SocialAgencyStorage.searchUser(cleanId)
              hasSearched = true
            } else {
              searchedUser = null
              hasSearched = true
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB026FF)),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("Search User", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (hasSearched) {
          if (searchedUser != null) {
            val user = searchedUser!!
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF22113D))
                .border(1.dp, Color(0xFF00E5FF), RoundedCornerShape(16.dp))
                .padding(12.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
              ) {
                SafeAgencyAvatar(
                  avatarRes = user.avatarRes,
                  avatarUri = null,
                  contentDescription = null,
                  modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                  Text(text = user.username, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                  Text(text = "ID: ${user.id} • ${user.flag} ${user.country}", color = Color.White.copy(alpha = 0.7f), fontSize = 11.5.sp)
                  Text(text = if (user.isOnline) "🟢 Online Now" else "⚪ Offline", color = if (user.isOnline) Color(0xFF00E676) else Color.Gray, fontSize = 10.5.sp)
                }

                Button(
                  onClick = { onInviteSent(user.id) },
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF26B9)),
                  shape = RoundedCornerShape(14.dp),
                  contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                  Text("Invite", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          } else {
            Text(
              text = "No user found with ID: $inputId",
              color = Color(0xFFFF5252),
              fontSize = 12.5.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          Button(
            onClick = onDismiss,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
          ) {
            Text("Cancel", color = Color.White.copy(alpha = 0.7f))
          }
        }
      }
    }
  }
}

/**
 * Edit Agency Dialog: Update agency name & country.
 */
@Composable
private fun EditAgencyDialog(
  currentName: String,
  currentCountry: String,
  onDismiss: () -> Unit,
  onSave: (name: String, country: String) -> Unit
) {
  var name by remember { mutableStateOf(currentName) }
  var country by remember { mutableStateOf(currentCountry) }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = Color(0xFF140A28),
      border = BorderStroke(1.4.dp, Color(0xFF00E5FF)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        Text(
          text = "Edit Agency Profile",
          color = Color.White,
          fontSize = 18.sp,
          fontWeight = FontWeight.Black
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Agency Name") },
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF00E5FF),
            unfocusedBorderColor = Color(0xFFB026FF),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedLabelColor = Color(0xFF00E5FF)
          ),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = country,
          onValueChange = { country = it },
          label = { Text("Country") },
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF00E5FF),
            unfocusedBorderColor = Color(0xFFB026FF),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedLabelColor = Color(0xFF00E5FF)
          ),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          Button(
            onClick = onDismiss,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
          ) {
            Text("Cancel", color = Color.White.copy(alpha = 0.7f))
          }

          Spacer(modifier = Modifier.width(8.dp))

          Button(
            onClick = { onSave(name, country) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("Save Changes", color = Color.Black, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

/**
 * Host Profile Detail Dialog: Displays verified host public details.
 */
@Composable
private fun HostProfileDetailDialog(
  host: AgencyHost,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = Color(0xFF140A28),
      border = BorderStroke(1.4.dp, Color(0xFFFF26B9)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(
          modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .border(2.5.dp, Color(0xFFFF26B9), CircleShape)
            .padding(3.dp)
            .clip(CircleShape)
        ) {
          SafeAgencyAvatar(
            avatarRes = host.avatarRes,
            avatarUri = host.avatarUri,
            contentDescription = "Host Avatar",
            modifier = Modifier.fillMaxSize()
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = host.name,
          color = Color.White,
          fontSize = 18.sp,
          fontWeight = FontWeight.Black
        )

        Text(
          text = "ID: ${host.hostId} • ${host.flag} ${host.country}",
          color = Color.White.copy(alpha = 0.7f),
          fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Meta Box
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF22113D))
            .padding(14.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Status", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
              Text("🟢 Official Live Host", color = Color(0xFF00E676), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Online Status", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
              Text(if (host.isOnline) "Active Now" else "Offline", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Joined Agency", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
              Text(host.joinDate, color = Color(0xFF00E5FF), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
          onClick = onDismiss,
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB026FF)),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("Close", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
private fun SafeAgencyAvatar(
  avatarRes: Int,
  avatarUri: String?,
  contentDescription: String?,
  modifier: Modifier = Modifier,
  contentScale: ContentScale = ContentScale.Crop
) {
  val context = LocalContext.current
  val safeRes = remember(avatarRes) {
    if (avatarRes <= 0) {
      R.drawable.img_streamer_girl
    } else {
      try {
        context.resources.getResourceName(avatarRes)
        avatarRes
      } catch (e: Throwable) {
        R.drawable.img_streamer_girl
      }
    }
  }

  if (!avatarUri.isNullOrBlank()) {
    AsyncImage(
      model = avatarUri,
      contentDescription = contentDescription,
      modifier = modifier,
      contentScale = contentScale
    )
  } else {
    Image(
      painter = painterResource(id = safeRes),
      contentDescription = contentDescription,
      modifier = modifier,
      contentScale = contentScale
    )
  }
}

