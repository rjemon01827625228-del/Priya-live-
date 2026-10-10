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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.aistudio.neonparty.nxvqlr.R
import com.example.ui.storage.SocialAgencyStorage
import com.example.ui.storage.SocialUser

@Composable
fun FollowersListDialog(
  initialTab: Int = 0, // 0: Followers, 1: Following, 2: Friends
  currentUserId: String,
  onDismiss: () -> Unit,
  onCountsChanged: () -> Unit
) {
  val context = LocalContext.current
  var selectedTab by remember { mutableIntStateOf(initialTab) }
  var searchQuery by remember { mutableStateOf("") }

  var followingIds by remember { mutableStateOf<List<String>>(emptyList()) }
  var followersIds by remember { mutableStateOf<List<String>>(emptyList()) }

  fun reloadData() {
    followingIds = SocialAgencyStorage.getFollowingList(context, currentUserId)
    followersIds = SocialAgencyStorage.getFollowersList(context, currentUserId)
  }

  LaunchedEffect(currentUserId) {
    reloadData()
  }

  // Friends are mutual followers
  val friendsIds = remember(followingIds, followersIds) {
    followingIds.filter { followersIds.contains(it) }
  }

  val activeListIds = when (selectedTab) {
    0 -> followersIds
    1 -> followingIds
    else -> friendsIds
  }

  // Map IDs to SocialUsers or platform community pool
  val displayedUsers = remember(activeListIds, searchQuery) {
    val users = SocialAgencyStorage.communityUsers.filter { activeListIds.contains(it.id) }
    if (searchQuery.isBlank()) users
    else {
      val q = searchQuery.trim().lowercase()
      users.filter { it.username.lowercase().contains(q) || it.id.contains(q) }
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = androidx.compose.ui.window.DialogProperties(
      usePlatformDefaultWidth = false,
      decorFitsSystemWindows = false
    )
  ) {
    com.example.ui.components.ConfigureFullScreenDialogWindow()
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = Color(0xFF120824),
      border = BorderStroke(1.4.dp, Color(0xFF00E5FF).copy(alpha = 0.7f)),
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .height(540.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
      ) {
        // Top Header with Close
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Social Connections",
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

        Spacer(modifier = Modifier.height(12.dp))

        // Tabs: Followers, Following, Friends
        TabRow(
          selectedTabIndex = selectedTab,
          containerColor = Color.Transparent,
          contentColor = Color(0xFF00E5FF),
          indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
              Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
              color = Color(0xFF00E5FF),
              height = 3.dp
            )
          }
        ) {
          Tab(
            selected = selectedTab == 0,
            onClick = { selectedTab = 0 },
            text = {
              Text(
                "Followers (${followersIds.size})",
                fontSize = 12.sp,
                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
              )
            }
          )
          Tab(
            selected = selectedTab == 1,
            onClick = { selectedTab = 1 },
            text = {
              Text(
                "Following (${followingIds.size})",
                fontSize = 12.sp,
                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
              )
            }
          )
          Tab(
            selected = selectedTab == 2,
            onClick = { selectedTab = 2 },
            text = {
              Text(
                "Friends (${friendsIds.size})",
                fontSize = 12.sp,
                fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
              )
            }
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFF1F0E3D))
            .padding(horizontal = 12.dp),
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
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
              value = searchQuery,
              onValueChange = { searchQuery = it },
              placeholder = { Text("Search by name or ID...", fontSize = 12.sp, color = Color.White.copy(alpha = 0.4f)) },
              singleLine = true,
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color(0xFF00E5FF)
              ),
              modifier = Modifier.fillMaxWidth()
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Users List or Empty State
        if (displayedUsers.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f),
            contentAlignment = Alignment.Center
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              Text(text = "👥", fontSize = 36.sp)
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = when (selectedTab) {
                  0 -> "No followers yet"
                  1 -> "Not following anyone yet"
                  else -> "No mutual friends yet"
                },
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Join live streams & audio parties to connect!",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp
              )
            }
          }
        } else {
          LazyColumn(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            items(displayedUsers) { user ->
              val isFollowingTarget = followingIds.contains(user.id)
              SocialUserItem(
                user = user,
                isFollowing = isFollowingTarget,
                onToggleFollow = {
                  if (isFollowingTarget) {
                    SocialAgencyStorage.unfollowUser(context, currentUserId, user.id)
                    Toast.makeText(context, "Unfollowed ${user.username}", Toast.LENGTH_SHORT).show()
                  } else {
                    SocialAgencyStorage.followUser(context, currentUserId, user.id)
                    Toast.makeText(context, "Following ${user.username}", Toast.LENGTH_SHORT).show()
                  }
                  reloadData()
                  onCountsChanged()
                }
              )
            }
          }
        }

        // Suggested Users / Discover Section when list is small
        if (selectedTab == 1 && followingIds.size < SocialAgencyStorage.communityUsers.size) {
          val discoverUsers = SocialAgencyStorage.communityUsers.filter { !followingIds.contains(it.id) }
          if (discoverUsers.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Discover New People",
              color = Color(0xFFFF26B9),
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              discoverUsers.take(2).forEach { user ->
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF22113D))
                    .border(1.dp, Color(0xFFB026FF).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(8.dp)
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                      painter = painterResource(id = user.avatarRes),
                      contentDescription = null,
                      modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column(modifier = Modifier.weight(1f)) {
                      Text(text = user.username, color = Color.White, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                      Text(text = user.flag, fontSize = 10.sp)
                    }
                    Box(
                      modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF00E5FF))
                        .clickable {
                          SocialAgencyStorage.followUser(context, currentUserId, user.id)
                          reloadData()
                          onCountsChanged()
                          Toast.makeText(context, "Followed ${user.username}", Toast.LENGTH_SHORT).show()
                        },
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(
                        imageVector = Icons.Filled.PersonAdd,
                        contentDescription = "Follow",
                        tint = Color.Black,
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
}

@Composable
private fun SocialUserItem(
  user: SocialUser,
  isFollowing: Boolean,
  onToggleFollow: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(Color(0xFF1C0D36))
      .border(1.dp, Color(0x33B026FF), RoundedCornerShape(14.dp))
      .padding(horizontal = 12.dp, vertical = 8.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(modifier = Modifier.size(44.dp)) {
        Image(
          painter = painterResource(id = user.avatarRes),
          contentDescription = user.username,
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .border(1.5.dp, Color(0xFFFF26B9), CircleShape),
          contentScale = ContentScale.Crop
        )
        if (user.isOnline) {
          Box(
            modifier = Modifier
              .size(10.dp)
              .align(Alignment.BottomEnd)
              .clip(CircleShape)
              .background(Color(0xFF00E676))
              .border(1.dp, Color(0xFF1C0D36), CircleShape)
          )
        }
      }

      Spacer(modifier = Modifier.width(10.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = user.username,
          color = Color.White,
          fontSize = 13.5.sp,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = "ID: ${user.id} • ${user.flag} ${user.country}",
          color = Color.White.copy(alpha = 0.6f),
          fontSize = 11.sp
        )
      }

      Button(
        onClick = onToggleFollow,
        colors = ButtonDefaults.buttonColors(
          containerColor = if (isFollowing) Color(0xFF331652) else Color(0xFF00E5FF)
        ),
        shape = RoundedCornerShape(14.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
        modifier = Modifier.testTag("follow_toggle_btn_${user.id}")
      ) {
        Text(
          text = if (isFollowing) "Following" else "Follow",
          color = if (isFollowing) Color.White.copy(alpha = 0.8f) else Color.Black,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}
