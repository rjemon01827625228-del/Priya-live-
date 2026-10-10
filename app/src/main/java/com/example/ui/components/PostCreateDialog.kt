package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Podcasts
import androidx.compose.material.icons.filled.VideoCameraFront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPinkBright

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostCreateDialog(
  onDismiss: () -> Unit,
  onActionComplete: (String) -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var postText by remember { mutableStateOf("") }
  var selectedType by remember { mutableStateOf("Post") }
  var selectedMediaUri by remember { mutableStateOf<Uri?>(null) }

  // Zero-permission Android Photo Picker for selecting photos from Gallery
  val galleryLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      selectedMediaUri = uri
    }
  }

  val context = androidx.compose.ui.platform.LocalContext.current
  val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()
  var isPublishing by remember { mutableStateOf(false) }

  val bottomSafePadding = rememberResponsiveNavigationBarPadding(extraBottomSpacing = 12.dp)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = Color(0xFF140129),
    scrimColor = Color(0x99000000),
    contentWindowInsets = { androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0) }
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .imePadding()
        .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = bottomSafePadding),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Create Cyber Post",
          color = Color.White,
          fontSize = 18.sp,
          fontWeight = FontWeight.Black
        )
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(Color(0xFF260144))
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

      Spacer(modifier = Modifier.height(14.dp))

      // 4 Quick action buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        CreateActionTile(
          title = "Video Live",
          icon = Icons.Filled.VideoCameraFront,
          color = NeonPinkBright,
          isSelected = selectedType == "Live",
          modifier = Modifier.weight(1f),
          onClick = { selectedType = "Live" }
        )
        CreateActionTile(
          title = "Audio Party",
          icon = Icons.Filled.Mic,
          color = NeonCyan,
          isSelected = selectedType == "Audio",
          modifier = Modifier.weight(1f),
          onClick = { selectedType = "Audio" }
        )
        CreateActionTile(
          title = "Square Feed",
          icon = Icons.Filled.EditNote,
          color = NeonGold,
          isSelected = selectedType == "Post",
          modifier = Modifier.weight(1f),
          onClick = { selectedType = "Post" }
        )
        CreateActionTile(
          title = "Broadcast",
          icon = Icons.Filled.Podcasts,
          color = Color(0xFFBD00FF),
          isSelected = selectedType == "Broadcast",
          modifier = Modifier.weight(1f),
          onClick = { selectedType = "Broadcast" }
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Input field
      OutlinedTextField(
        value = postText,
        onValueChange = { postText = it },
        placeholder = {
          Text(
            text = "Share something with the neon community...",
            color = Color.White.copy(alpha = 0.5f)
          )
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(100.dp)
          .testTag("input_post_text"),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedTextColor = Color.White,
          unfocusedTextColor = Color.White,
          focusedBorderColor = NeonCyan,
          unfocusedBorderColor = NeonPink.copy(alpha = 0.6f),
          focusedContainerColor = Color(0xFF1E013A),
          unfocusedContainerColor = Color(0xFF190130)
        )
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Gallery Image Picker Section
      if (selectedMediaUri != null) {
        // Thumbnail preview with delete button
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.5.dp, NeonCyan, RoundedCornerShape(14.dp))
        ) {
          AsyncImage(
            model = selectedMediaUri,
            contentDescription = "Selected Photo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
          )
          // Close / Remove Photo button
          IconButton(
            onClick = { selectedMediaUri = null },
            modifier = Modifier
              .align(Alignment.TopEnd)
              .padding(6.dp)
              .size(28.dp)
              .clip(CircleShape)
              .background(Color(0xCC000000))
          ) {
            Icon(Icons.Filled.Close, contentDescription = "Remove photo", tint = Color.White, modifier = Modifier.size(16.dp))
          }
        }
      } else {
        // "Select Photo from Gallery" Action Button
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.2.dp, Color(0x6600E5FF), RoundedCornerShape(14.dp))
            .background(Color(0xFF190033))
            .clickable {
              galleryLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
              )
            }
            .padding(vertical = 12.dp, horizontal = 14.dp),
          contentAlignment = Alignment.Center
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = Icons.Filled.AddPhotoAlternate,
              contentDescription = "Pick Image from Gallery",
              tint = NeonCyan,
              modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Add Photo from Gallery",
              color = Color.White,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Submit Button
      Button(
        onClick = {
          if (isPublishing) return@Button
          isPublishing = true
          coroutineScope.launch {
            val activeUserId = com.example.ui.storage.UserProfileStorage.getActiveUserId(context)
            val userProf = com.example.ui.storage.UserProfileStorage.getProfileForUser(context, activeUserId)
            var savedMediaUriString: String? = null

            if (selectedMediaUri != null) {
              val localFile = com.example.ui.storage.LocalImageStorage.saveUriLocally(
                context = context,
                sourceUri = selectedMediaUri!!,
                imageType = com.example.ui.storage.LocalImageStorage.ImageType.ROOM_COVER,
                identifier = "feed_${System.currentTimeMillis()}"
              )
              if (localFile != null) {
                savedMediaUriString = "file://${localFile.absolutePath}"
              } else {
                savedMediaUriString = selectedMediaUri.toString()
              }
            }

            val newPost = com.example.ui.storage.FeedPost(
              postId = "post_${System.currentTimeMillis()}",
              authorId = activeUserId,
              authorName = userProf.username.ifBlank { "User" },
              authorAvatarUri = userProf.profileImageUri,
              authorLevel = userProf.userLevel.coerceAtLeast(1),
              caption = postText.trim(),
              mediaUri = savedMediaUriString,
              postType = selectedType,
              timestamp = System.currentTimeMillis(),
              likeCount = 0,
              commentCount = 0
            )

            com.example.ui.storage.LocalFeedStorage.createPost(newPost) { success ->
              isPublishing = false
              if (success) {
                onActionComplete("Post published successfully!")
                onDismiss()
              } else {
                onActionComplete("Failed to publish post.")
              }
            }
          }
        },
        enabled = !isPublishing,
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("btn_publish_post"),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .border(
              1.5.dp,
              Brush.horizontalGradient(listOf(NeonCyan, NeonPinkBright)),
              RoundedCornerShape(24.dp)
            )
            .background(
              Brush.horizontalGradient(
                listOf(Color(0xFF0091FF), Color(0xFFFF007F))
              )
            ),
          contentAlignment = Alignment.Center
        ) {
          if (isPublishing) {
            androidx.compose.material3.CircularProgressIndicator(
              color = Color.White,
              modifier = Modifier.size(22.dp),
              strokeWidth = 2.dp
            )
          } else {
            Text(
              text = "Publish $selectedType",
              color = Color.White,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}

@Composable
private fun CreateActionTile(
  title: String,
  icon: ImageVector,
  color: Color,
  isSelected: Boolean,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .border(
        width = if (isSelected) 2.dp else 1.dp,
        color = if (isSelected) color else color.copy(alpha = 0.4f),
        shape = RoundedCornerShape(12.dp)
      )
      .background(
        if (isSelected) color.copy(alpha = 0.25f) else Color(0xFF1D0136)
      )
      .clickable(onClick = onClick)
      .padding(vertical = 10.dp, horizontal = 4.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = title,
        tint = color,
        modifier = Modifier.size(24.dp)
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = title,
        color = Color.White,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold
      )
    }
  }
}
