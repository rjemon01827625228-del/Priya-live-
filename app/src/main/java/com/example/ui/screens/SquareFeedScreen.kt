package com.example.ui.screens

import android.text.format.DateUtils
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
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import coil.compose.AsyncImage
import com.aistudio.neonparty.nxvqlr.R
import com.example.ui.storage.LocalFeatureStorage
import com.example.ui.storage.LocalFeedPost
import com.example.ui.storage.LocalImageStorage
import com.example.ui.storage.UserProfileStorage
import com.example.ui.theme.LiveRed
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPinkBright
import com.example.ui.theme.TextMuted

@Composable
fun SquareFeedScreen(
  onAddNewPost: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val activeUserId = remember { UserProfileStorage.getActiveUserId(context) }
  var selectedTab by remember { mutableStateOf("Square") }
  var posts by remember { mutableStateOf<List<LocalFeedPost>>(LocalFeatureStorage.getFeedPosts(context)) }
  var isLoading by remember { mutableStateOf(false) }

  DisposableEffect(Unit) {
    posts = LocalFeatureStorage.getFeedPosts(context)
    onDispose { }
  }

  val topSafePadding = com.example.ui.components.rememberResponsiveTopBarPadding(extraTopSpacing = 8.dp)

  LazyColumn(
    modifier = modifier.fillMaxSize(),
    contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = topSafePadding, bottom = 90.dp)
  ) {
    // 1. Top Header: Neon Pill bar containing Square, Follow, Topics, Search, and Add
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(24.dp))
          .border(
            width = 2.dp,
            brush = Brush.horizontalGradient(
              listOf(
                NeonPinkBright,
                NeonCyan,
                Color(0xFF9D00FF),
                NeonPinkBright
              )
            ),
            shape = RoundedCornerShape(24.dp)
          )
          .background(Color(0xFF140026))
          .padding(horizontal = 6.dp, vertical = 5.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          // Left Tabs: Square, Follow, Topics
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            // "Square" Active Neon Pill
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .border(
                  width = 1.2.dp,
                  brush = Brush.horizontalGradient(listOf(NeonCyan, Color(0xFF0091FF))),
                  shape = RoundedCornerShape(18.dp)
                )
                .background(
                  if (selectedTab == "Square") Color(0xFF0077B6).copy(alpha = 0.6f)
                  else Color.Transparent
                )
                .clickable { selectedTab = "Square" }
                .padding(horizontal = 14.dp, vertical = 6.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "Square",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
              )
            }

            // "Follow" Tab
            Text(
              text = "Follow",
              color = if (selectedTab == "Follow") Color.White else TextMuted,
              fontSize = 14.sp,
              fontWeight = if (selectedTab == "Follow") FontWeight.Bold else FontWeight.Medium,
              modifier = Modifier
                .clickable { selectedTab = "Follow" }
                .padding(horizontal = 6.dp, vertical = 4.dp)
            )

            // "Topics" Tab
            Text(
              text = "Topics",
              color = if (selectedTab == "Topics") Color.White else TextMuted,
              fontSize = 14.sp,
              fontWeight = if (selectedTab == "Topics") FontWeight.Bold else FontWeight.Medium,
              modifier = Modifier
                .clickable { selectedTab = "Topics" }
                .padding(horizontal = 6.dp, vertical = 4.dp)
            )
          }

          // Right Icons: Search & Add (+)
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFF1F013B))
                .clickable { },
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search",
                tint = NeonCyan,
                modifier = Modifier.size(18.dp)
              )
            }

            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.2.dp, NeonCyan, RoundedCornerShape(8.dp))
                .background(Color(0xFF1F013B))
                .clickable(onClick = onAddNewPost),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add Post",
                tint = NeonCyan,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))
    }

    // 2. Real-Time Feed Posts (Zero Fake Posts)
    if (posts.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            Box(
              modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E0038))
                .border(1.5.dp, NeonCyan.copy(alpha = 0.5f), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Filled.Image,
                contentDescription = null,
                tint = NeonCyan,
                modifier = Modifier.size(36.dp)
              )
            }

            Text(
              text = if (isLoading) "Loading community posts..." else "No posts yet!",
              color = Color.White,
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold
            )

            Text(
              text = "Be the first to share a moment with everyone!",
              color = TextMuted,
              fontSize = 13.sp
            )

            Button(
              onClick = onAddNewPost,
              colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
              contentPadding = PaddingValues(0.dp),
              shape = RoundedCornerShape(20.dp)
            ) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(20.dp))
                  .background(Brush.horizontalGradient(listOf(NeonCyan, NeonPinkBright)))
                  .padding(horizontal = 24.dp, vertical = 10.dp)
              ) {
                Text(
                  text = "Create Post",
                  color = Color.White,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }
    } else {
      items(posts, key = { it.postId }) { post ->
        SquareFeedCard(
          post = post,
          currentUserId = activeUserId,
          modifier = Modifier.testTag("feed_card_${post.postId}")
        )
        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }
}

@Composable
fun SquareFeedCard(
  post: LocalFeedPost,
  currentUserId: String,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val isLikedByMe = post.likedUsers[currentUserId] == true
  var localLiked by remember(post.postId, isLikedByMe) { mutableStateOf(isLikedByMe) }
  var localLikesCount by remember(post.postId, post.likeCount) { mutableIntStateOf(post.likeCount) }
  var isBookmarked by remember { mutableStateOf(false) }

  val relativeTime = remember(post.timestamp) {
    try {
      DateUtils.getRelativeTimeSpanString(
        post.timestamp,
        System.currentTimeMillis(),
        DateUtils.MINUTE_IN_MILLIS,
        DateUtils.FORMAT_ABBREV_RELATIVE
      ).toString()
    } catch (_: Exception) {
      "Recent"
    }
  }

  val resolvedAvatar = remember(post.authorAvatarUri, post.authorId) {
    LocalImageStorage.resolveImageModel(
      context = context,
      candidateUri = post.authorAvatarUri,
      imageType = LocalImageStorage.ImageType.PROFILE,
      entityId = post.authorId,
      defaultFallbackRes = R.drawable.img_profile_max
    )
  }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .border(
        width = 1.8.dp,
        brush = Brush.verticalGradient(
          listOf(
            NeonPink,
            Color(0xFF8A00D4),
            NeonCyan.copy(alpha = 0.8f)
          )
        ),
        shape = RoundedCornerShape(18.dp)
      )
      .background(
        Brush.verticalGradient(
          listOf(
            Color(0xFF1A0133),
            Color(0xFF0F0022)
          )
        )
      )
      .padding(14.dp)
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      // Header: Profile picture + Username + Level + Timestamp
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        AsyncImage(
          model = resolvedAvatar,
          contentDescription = post.authorName,
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .border(1.5.dp, NeonCyan, CircleShape),
          contentScale = ContentScale.Crop,
          error = painterResource(id = R.drawable.img_profile_max),
          fallback = painterResource(id = R.drawable.img_profile_max)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = post.authorName.ifBlank { "Cyber User" },
              color = Color.White,
              fontSize = 15.sp,
              fontWeight = FontWeight.Black,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )

            // Level Pill
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Brush.horizontalGradient(listOf(NeonGold, Color(0xFFFF9100))))
                .padding(horizontal = 6.dp, vertical = 1.dp)
            ) {
              Text(
                text = "Lv.${post.authorLevel.coerceAtLeast(1)}",
                color = Color.Black,
                fontSize = 9.sp,
                fontWeight = FontWeight.Black
              )
            }
          }

          Text(
            text = relativeTime,
            color = TextMuted,
            fontSize = 11.sp
          )
        }

        // Post Type Tag
        if (post.postType.isNotBlank() && post.postType != "Post") {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .border(1.dp, NeonCyan.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
              .background(Color(0xFF1B0033))
              .padding(horizontal = 8.dp, vertical = 2.dp)
          ) {
            Text(
              text = post.postType,
              color = NeonCyan,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      // Caption Text
      if (post.caption.isNotBlank()) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = post.caption,
          color = Color.White.copy(alpha = 0.95f),
          fontSize = 14.sp,
          lineHeight = 20.sp
        )
      }

      // Media Preview Box (If image attached)
      if (!post.mediaUri.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(12.dp))
        val resolvedMedia = remember(post.mediaUri) {
          LocalImageStorage.resolveImageModel(
            context = context,
            candidateUri = post.mediaUri,
            imageType = LocalImageStorage.ImageType.ROOM_COVER,
            entityId = post.postId,
            defaultFallbackRes = R.drawable.bg_party_scenic
          )
        }
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.35f)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1E013A))
        ) {
          AsyncImage(
            model = resolvedMedia,
            contentDescription = "Post image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            error = painterResource(id = R.drawable.bg_party_scenic)
          )
        }
      }

      // Interactive Action Bar below preview: Like, Comment, Share
      Spacer(modifier = Modifier.height(12.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
          // Heart / Like
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {
              val willLike = !localLiked
              localLiked = willLike
              localLikesCount = if (willLike) localLikesCount + 1 else (localLikesCount - 1).coerceAtLeast(0)
              LocalFeatureStorage.toggleLikePost(context, post.postId, currentUserId)
            }
          ) {
            Icon(
              imageVector = if (localLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
              contentDescription = "Like",
              tint = if (localLiked) LiveRed else Color.White,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
              text = "$localLikesCount",
              color = Color.White,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }

          // Comment
          Row(
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Filled.ChatBubbleOutline,
              contentDescription = "Comments",
              tint = NeonCyan,
              modifier = Modifier.size(19.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
              text = "${post.commentCount}",
              color = Color.White,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
          }

          // Share
          Icon(
            imageVector = Icons.Filled.Share,
            contentDescription = "Share",
            tint = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.size(19.dp)
          )
        }

        // Bookmark
        Icon(
          imageVector = Icons.Filled.Bookmark,
          contentDescription = "Bookmark",
          tint = if (isBookmarked) NeonPinkBright else Color.White.copy(alpha = 0.5f),
          modifier = Modifier
            .size(20.dp)
            .clickable { isBookmarked = !isBookmarked }
        )
      }
    }
  }
}
