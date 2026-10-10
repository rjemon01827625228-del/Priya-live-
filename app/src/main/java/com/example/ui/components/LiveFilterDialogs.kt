package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import android.content.ClipboardManager
import android.content.ClipData
import android.content.Context
import com.example.ui.storage.SocialAgencyStorage
import com.example.ui.storage.SocialUser
import com.example.ui.storage.UserProfileStorage
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.screens.LiveStreamItem
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPinkBright

data class CountryItem(
  val code: String,
  val name: String,
  val flagEmoji: String,
  val activeStreams: Int
)

/**
 * 45+ Countries for the "All Countries" filter
 */
val AllCountriesList = listOf(
  CountryItem("ALL", "All Countries", "🌐", 1250),
  CountryItem("BD", "Bangladesh", "🇧🇩", 342),
  CountryItem("IN", "India", "🇮🇳", 420),
  CountryItem("PK", "Pakistan", "🇵🇰", 188),
  CountryItem("SA", "Saudi Arabia", "🇸🇦", 215),
  CountryItem("AE", "United Arab Emirates", "🇦🇪", 194),
  CountryItem("US", "United States", "🇺🇸", 310),
  CountryItem("GB", "United Kingdom", "🇬🇧", 165),
  CountryItem("CA", "Canada", "🇨🇦", 98),
  CountryItem("MY", "Malaysia", "🇲🇾", 145),
  CountryItem("ID", "Indonesia", "🇮🇩", 280),
  CountryItem("SG", "Singapore", "🇸🇬", 86),
  CountryItem("TR", "Turkey", "🇹🇷", 205),
  CountryItem("EG", "Egypt", "🇪🇬", 130),
  CountryItem("QA", "Qatar", "🇶🇦", 94),
  CountryItem("KW", "Kuwait", "🇰🇼", 92),
  CountryItem("OM", "Oman", "🇴🇲", 64),
  CountryItem("BH", "Bahrain", "🇧🇭", 58),
  CountryItem("NP", "Nepal", "🇳🇵", 72),
  CountryItem("LK", "Sri Lanka", "🇱🇰", 65),
  CountryItem("PH", "Philippines", "🇵🇭", 195),
  CountryItem("TH", "Thailand", "🇹🇭", 155),
  CountryItem("VN", "Vietnam", "🇻🇳", 140),
  CountryItem("JP", "Japan", "🇯🇵", 230),
  CountryItem("KR", "South Korea", "🇰🇷", 210),
  CountryItem("BR", "Brazil", "🇧🇷", 270),
  CountryItem("DE", "Germany", "🇩🇪", 125),
  CountryItem("FR", "France", "🇫🇷", 118),
  CountryItem("IT", "Italy", "🇮🇹", 102),
  CountryItem("ES", "Spain", "🇪🇸", 120),
  CountryItem("AU", "Australia", "🇦🇺", 95),
  CountryItem("RU", "Russia", "🇷🇺", 160),
  CountryItem("MX", "Mexico", "🇲🇽", 148),
  CountryItem("AR", "Argentina", "🇦🇷", 88),
  CountryItem("ZA", "South Africa", "🇿🇦", 74),
  CountryItem("NG", "Nigeria", "🇳🇬", 110),
  CountryItem("MA", "Morocco", "🇲🇦", 89),
  CountryItem("IQ", "Iraq", "🇮🇶", 82),
  CountryItem("JO", "Jordan", "🇯🇴", 58),
  CountryItem("LB", "Lebanon", "🇱🇧", 52),
  CountryItem("PT", "Portugal", "🇵🇹", 49),
  CountryItem("NL", "Netherlands", "🇳🇱", 68),
  CountryItem("SE", "Sweden", "🇸🇪", 44),
  CountryItem("CH", "Switzerland", "🇨🇭", 46),
  CountryItem("NZ", "New Zealand", "🇳🇿", 38),
  CountryItem("CO", "Colombia", "🇨🇴", 62)
)

/**
 * All Countries Selection Modal (45+ Countries with live search)
 */
@Composable
fun CountryFilterDialog(
  selectedCountryCode: String,
  onCountrySelected: (CountryItem) -> Unit,
  onDismiss: () -> Unit
) {
  var searchQuery by remember { mutableStateOf("") }

  val filteredCountries = remember(searchQuery) {
    if (searchQuery.isBlank()) {
      AllCountriesList
    } else {
      AllCountriesList.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
          it.code.contains(searchQuery, ignoreCase = true)
      }
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
          .fillMaxHeight(0.82f)
          .clip(RoundedCornerShape(20.dp))
          .border(
            width = 1.6.dp,
            brush = Brush.verticalGradient(
              listOf(NeonCyan, Color(0xFF9D00FF), NeonPink)
            ),
            shape = RoundedCornerShape(20.dp)
          ),
        color = Color(0xFF0E001F)
      ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
      ) {
        // Dialog Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Filled.Public,
              contentDescription = null,
              tint = NeonCyan,
              modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "SELECT COUNTRY / REGION",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black
              )
              Text(
                text = "45+ Countries Available",
                color = NeonCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Filled.Close,
              contentDescription = "Close",
              tint = Color.White
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Input inside country dialog
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = {
            Text(
              "Search Country (e.g. Bangladesh, USA, India)",
              color = Color(0xFF9C8BB5),
              fontSize = 12.sp
            )
          },
          leadingIcon = {
            Icon(
              imageVector = Icons.Filled.Search,
              contentDescription = null,
              tint = NeonCyan,
              modifier = Modifier.size(18.dp)
            )
          },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonCyan,
            unfocusedBorderColor = Color(0xFF4A148C),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedContainerColor = Color(0xFF190033),
            unfocusedContainerColor = Color(0xFF190033)
          ),
          shape = RoundedCornerShape(12.dp),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("country_search_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Country List
        LazyColumn(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          items(filteredCountries, key = { it.code }) { country ->
            val isSelected = country.code == selectedCountryCode

            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(
                  if (isSelected) {
                    Brush.horizontalGradient(
                      listOf(Color(0xFF6A0080), Color(0xFF38006B))
                    )
                  } else {
                    Brush.horizontalGradient(
                      listOf(Color(0xFF1A0030), Color(0xFF120024))
                    )
                  }
                )
                .border(
                  width = 1.dp,
                  color = if (isSelected) NeonCyan else Color(0x33B388FF),
                  shape = RoundedCornerShape(12.dp)
                )
                .clickable {
                  onCountrySelected(country)
                  onDismiss()
                }
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .testTag("country_item_${country.code}"),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = country.flagEmoji,
                  fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                  Text(
                    text = country.name,
                    color = Color.White,
                    fontSize = 13.5.sp,
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold
                  )
                  Text(
                    text = "${country.activeStreams} Live Rooms",
                    color = if (isSelected) NeonCyan else Color(0xFFB388FF),
                    fontSize = 10.5.sp
                  )
                }
              }

              if (isSelected) {
                Box(
                  modifier = Modifier
                    .size(22.dp)
                    .background(NeonCyan, CircleShape),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Selected",
                    tint = Color(0xFF0A0017),
                    modifier = Modifier.size(14.dp)
                  )
                }
              }
            }
          }
        }
      }
    }
    }
  }
}

/**
 * Search Live Streams by Host Name or Host ID Dialog
 */
@Composable
fun LiveSearchDialog(
  availableStreams: List<LiveStreamItem>,
  onStreamSelected: (LiveStreamItem) -> Unit,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  var searchQuery by remember { mutableStateOf("") }
  var isSearchingPid by remember { mutableStateOf(false) }
  var searchedUserProfile by remember { mutableStateOf<SocialUser?>(null) }
  var searchPidAttempted by remember { mutableStateOf(false) }

  val isDigitsOnly = remember(searchQuery) {
    val q = searchQuery.trim()
    q.length >= 3 && q.all { it.isDigit() }
  }

  LaunchedEffect(searchQuery) {
    val cleanQuery = searchQuery.trim()
    if (isDigitsOnly) {
      isSearchingPid = true
      searchPidAttempted = true
      val activeId = UserProfileStorage.getActiveUserId(context)
      val matched = if (cleanQuery == activeId) {
        val prof = UserProfileStorage.getProfileForUser(context, activeId)
        SocialUser(
          id = prof.userId,
          username = prof.username,
          avatarRes = com.aistudio.neonparty.nxvqlr.R.drawable.img_profile_max,
          avatarUri = prof.profileImageUri,
          country = prof.country,
          flag = prof.countryFlag,
          gender = prof.gender,
          age = prof.age,
          level = prof.levelBadge
        )
      } else {
        SocialAgencyStorage.searchUser(cleanQuery)
      }
      searchedUserProfile = matched
      isSearchingPid = false
    } else {
      searchedUserProfile = null
      searchPidAttempted = false
      isSearchingPid = false
    }
  }

  val searchResults = remember(searchQuery) {
    if (searchQuery.isBlank()) {
      availableStreams
    } else {
      availableStreams.filter {
        it.streamerName.contains(searchQuery, ignoreCase = true) ||
          it.id.contains(searchQuery, ignoreCase = true)
      }
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
          .fillMaxHeight(0.82f)
          .clip(RoundedCornerShape(20.dp))
          .border(
            width = 1.6.dp,
            brush = Brush.horizontalGradient(listOf(NeonCyan, NeonPinkBright)),
            shape = RoundedCornerShape(20.dp)
          ),
        color = Color(0xFF0C001C)
      ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Filled.Search,
              contentDescription = null,
              tint = NeonCyan,
              modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "SEARCH BY PID & ROOMS",
              color = Color.White,
              fontSize = 15.sp,
              fontWeight = FontWeight.Black
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Filled.Close,
              contentDescription = "Close",
              tint = Color.White
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Input
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = {
            Text(
              "Enter PID (e.g. 5827419) or Host Name",
              color = Color(0xFF9C8BB5),
              fontSize = 12.sp
            )
          },
          leadingIcon = {
            Icon(
              imageVector = Icons.Filled.Search,
              contentDescription = null,
              tint = NeonCyan
            )
          },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonCyan,
            unfocusedBorderColor = Color(0xFF370068),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedContainerColor = Color(0xFF17002C),
            unfocusedContainerColor = Color(0xFF17002C)
          ),
          shape = RoundedCornerShape(12.dp),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("host_search_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // PID Exact Search Result Section
        if (isSearchingPid) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            CircularProgressIndicator(
              modifier = Modifier.size(18.dp),
              color = NeonCyan,
              strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Searching PID ${searchQuery.trim()}...",
              color = Color(0xFF80D8FF),
              fontSize = 12.sp
            )
          }
        } else if (isDigitsOnly && searchPidAttempted) {
          if (searchedUserProfile != null) {
            val profile = searchedUserProfile!!
            val userPid = profile.id
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF170033))
                .border(1.2.dp, Brush.horizontalGradient(listOf(NeonCyan, Color(0xFFBD00FF))), RoundedCornerShape(14.dp))
                .padding(12.dp)
            ) {
              Text(
                text = "MATCHED USER (PID)",
                color = Color(0xFF69F0AE),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
              )
              Spacer(modifier = Modifier.height(6.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(44.dp)
                      .clip(CircleShape)
                      .background(Brush.sweepGradient(listOf(NeonCyan, NeonPinkBright, NeonCyan)))
                      .padding(2.dp)
                  ) {
                    Box(
                      modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color(0xFF26003E)),
                      contentAlignment = Alignment.Center
                    ) {
                      Text(
                        text = profile.username.take(1).ifBlank { "U" },
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                      )
                    }
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(
                      text = profile.username.ifBlank { "User" },
                      color = Color.White,
                      fontSize = 14.sp,
                      fontWeight = FontWeight.Bold
                    )
                    Text(
                      text = "PID: $userPid",
                      color = NeonCyan,
                      fontSize = 12.sp,
                      fontWeight = FontWeight.SemiBold
                    )
                    Text(
                      text = "${profile.level} • ${profile.country.ifBlank { "Global" }}",
                      color = Color(0xFFB39DDB),
                      fontSize = 10.sp
                    )
                  }
                }

                IconButton(
                  onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("User PID", userPid)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "PID $userPid copied", Toast.LENGTH_SHORT).show()
                  }
                ) {
                  Icon(
                    imageVector = Icons.Filled.ContentCopy,
                    contentDescription = "Copy PID",
                    tint = NeonCyan,
                    modifier = Modifier.size(20.dp)
                  )
                }
              }
            }
            Spacer(modifier = Modifier.height(10.dp))
          } else {
            // Clean "User not found" state
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x22FF1744))
                .border(1.dp, Color(0x66FF1744), RoundedCornerShape(12.dp))
                .padding(12.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "User not found (PID: ${searchQuery.trim()})",
                color = Color(0xFFFF5252),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
            Spacer(modifier = Modifier.height(10.dp))
          }
        }

        Text(
          text = "LIVE ROOMS (${searchResults.size})",
          color = Color(0xFFB388FF),
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(searchResults, key = { it.id }) { stream ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1A0033))
                .border(1.dp, Color(0x33FF007F), RoundedCornerShape(12.dp))
                .clickable {
                  onStreamSelected(stream)
                  onDismiss()
                }
                .padding(10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(
                      Brush.sweepGradient(listOf(NeonCyan, NeonPink, NeonCyan))
                    )
                    .padding(2.dp)
                ) {
                  Box(
                    modifier = Modifier
                      .fillMaxSize()
                      .clip(CircleShape)
                      .background(Color(0xFF26003E)),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = stream.streamerName.take(1),
                      color = Color.White,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                  Text(
                    text = stream.streamerName,
                    color = Color.White,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = "PID: ${stream.id}  •  ${stream.heartCount} ❤️",
                    color = NeonCyan,
                    fontSize = 11.sp
                  )
                }
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(14.dp))
                  .background(
                    Brush.horizontalGradient(listOf(Color(0xFFFF007F), Color(0xFF9D00FF)))
                  )
                  .padding(horizontal = 10.dp, vertical = 5.dp)
              ) {
                Text(
                  text = "Watch",
                  color = Color.White,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }
    }
    }
  }
}

/**
 * Superstar Ranking / Rewards Medal Dialog
 * Opens when tapping the Medal/Trophy icon beside the search bar.
 */
@Composable
fun SuperstarRankingDialog(
  onDismiss: () -> Unit
) {
  var selectedTab by remember { mutableStateOf("Daily") }

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
          .fillMaxWidth(0.94f)
          .fillMaxHeight(0.86f)
          .clip(RoundedCornerShape(22.dp))
          .border(
            width = 1.8.dp,
            brush = Brush.verticalGradient(listOf(NeonGold, Color(0xFFFF007F), Color(0xFF7000FF))),
            shape = RoundedCornerShape(22.dp)
          ),
        color = Color(0xFF0F0022)
      ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Filled.EmojiEvents,
              contentDescription = null,
              tint = NeonGold,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "SUPERSTAR RANKINGS",
                color = Color.White,
                fontSize = 15.5.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
              )
              Text(
                text = "Leaderboard & Honor Rewards",
                color = NeonGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Filled.Close,
              contentDescription = "Close",
              tint = Color.White
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Time Period Tabs: Daily, Weekly, Monthly
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1E0038))
            .padding(4.dp),
          horizontalArrangement = Arrangement.SpaceEvenly
        ) {
          listOf("Daily", "Weekly", "Monthly").forEach { tab ->
            val isSelected = selectedTab == tab
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(
                  if (isSelected) {
                    Brush.horizontalGradient(
                      listOf(Color(0xFFFF9100), Color(0xFFFF007F))
                    )
                  } else {
                    Brush.horizontalGradient(
                      listOf(Color.Transparent, Color.Transparent)
                    )
                  }
                )
                .clickable { selectedTab = tab }
                .padding(vertical = 7.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = tab,
                color = if (isSelected) Color.White else Color(0xFFB388FF),
                fontSize = 12.5.sp,
                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // TOP 3 PODIUM (Rank 2, Rank 1, Rank 3)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceEvenly,
          verticalAlignment = Alignment.Bottom
        ) {
          // #2 Silver
          PodiumColumn(
            rank = 2,
            name = "Ayesha Queen",
            beans = "8.4M Beans",
            crownEmoji = "🥈",
            ringColor = Color(0xFFCFD8DC),
            height = 100.dp
          )

          // #1 Gold Champion
          PodiumColumn(
            rank = 1,
            name = "পিচ্ছি Live",
            beans = "14.8M Beans",
            crownEmoji = "👑",
            ringColor = NeonGold,
            height = 125.dp
          )

          // #3 Bronze
          PodiumColumn(
            rank = 3,
            name = "Cyber Max",
            beans = "6.1M Beans",
            crownEmoji = "🥉",
            ringColor = Color(0xFFCD7F32),
            height = 90.dp
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "HONOR RANKINGS",
          color = Color(0xFFB39DDB),
          fontSize = 11.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Ranks 4 - 8
        LazyColumn(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          val rankList = listOf(
            Triple(4, "Neon Star Jenny", "4.2M Beans"),
            Triple(5, "Rider Alex", "3.7M Beans"),
            Triple(6, "Guitar Legend", "2.9M Beans"),
            Triple(7, "Sweet Angel", "2.4M Beans"),
            Triple(8, "Vip Prince", "1.9M Beans")
          )

          items(rankList) { (rank, name, beans) ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF190030))
                .padding(horizontal = 12.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "#$rank",
                  color = Color(0xFFE040FB),
                  fontWeight = FontWeight.Black,
                  fontSize = 13.sp,
                  modifier = Modifier.width(28.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Column {
                  Text(
                    text = name,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = beans,
                    color = NeonGold,
                    fontSize = 10.5.sp
                  )
                }
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(12.dp))
                  .border(1.dp, NeonPink, RoundedCornerShape(12.dp))
                  .padding(horizontal = 10.dp, vertical = 4.dp)
              ) {
                Text(
                  text = "+ Follow",
                  color = NeonPinkBright,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }
    }
    }
  }
}

@Composable
private fun PodiumColumn(
  rank: Int,
  name: String,
  beans: String,
  crownEmoji: String,
  ringColor: Color,
  height: androidx.compose.ui.unit.Dp
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = crownEmoji,
      fontSize = if (rank == 1) 24.sp else 18.sp
    )

    Box(
      modifier = Modifier
        .size(if (rank == 1) 48.dp else 40.dp)
        .clip(CircleShape)
        .border(2.dp, ringColor, CircleShape)
        .background(Color(0xFF2B0048)),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = name.take(1),
        color = Color.White,
        fontWeight = FontWeight.Black,
        fontSize = if (rank == 1) 18.sp else 15.sp
      )
    }

    Spacer(modifier = Modifier.height(4.dp))

    Text(
      text = name,
      color = Color.White,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      maxLines = 1
    )

    Text(
      text = beans,
      color = NeonGold,
      fontSize = 9.5.sp,
      fontWeight = FontWeight.Medium
    )

    Spacer(modifier = Modifier.height(4.dp))

    // Podium block
    Box(
      modifier = Modifier
        .width(if (rank == 1) 76.dp else 66.dp)
        .height(height)
        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
        .background(
          Brush.verticalGradient(
            listOf(
              ringColor.copy(alpha = 0.8f),
              Color(0xFF1F0038)
            )
          )
        )
        .border(1.dp, ringColor.copy(alpha = 0.5f), RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "$rank",
        color = Color.White,
        fontSize = 22.sp,
        fontWeight = FontWeight.Black
      )
    }
  }
}
