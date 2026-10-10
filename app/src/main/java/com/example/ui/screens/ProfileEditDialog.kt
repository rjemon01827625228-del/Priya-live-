package com.example.ui.screens

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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage

data class CountryOption(val name: String, val flag: String)

val countryList = listOf(
  CountryOption("Bangladesh", "🇧🇩"),
  CountryOption("United States", "🇺🇸"),
  CountryOption("United Kingdom", "🇬🇧"),
  CountryOption("India", "🇮🇳"),
  CountryOption("Pakistan", "🇵🇰"),
  CountryOption("United Arab Emirates", "🇦🇪"),
  CountryOption("Saudi Arabia", "🇸🇦"),
  CountryOption("Canada", "🇨🇦"),
  CountryOption("Malaysia", "🇲🇾")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditDialog(
  currentProfile: UserProfileData,
  onSave: (newName: String, newCountry: String, newFlag: String, newBio: String, newImageUri: String?) -> Unit,
  onOpenFullDetails: () -> Unit,
  onDismiss: () -> Unit
) {
  var name by remember { mutableStateOf(currentProfile.username) }
  var bio by remember { mutableStateOf(currentProfile.bio) }
  var selectedImageUri by remember { mutableStateOf(currentProfile.profileImageUri) }

  val context = androidx.compose.ui.platform.LocalContext.current
  var selectedCountry by remember {
    mutableStateOf(
      countryList.find { it.flag == currentProfile.countryText } ?: countryList[0]
    )
  }
  var countryDropdownExpanded by remember { mutableStateOf(false) }

  // Official Android Photo Picker launcher with immediate persistent local backup
  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      val localFile = com.example.ui.storage.LocalImageStorage.saveUriLocally(
        context = context,
        sourceUri = uri,
        imageType = com.example.ui.storage.LocalImageStorage.ImageType.PROFILE,
        identifier = currentProfile.userId
      )
      selectedImageUri = if (localFile != null) "file://${localFile.absolutePath}" else uri.toString()
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(
      usePlatformDefaultWidth = false,
      decorFitsSystemWindows = false
    )
  ) {
    com.example.ui.components.ConfigureFullScreenDialogWindow()
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .clip(RoundedCornerShape(26.dp))
        .border(
          width = 1.8.dp,
          brush = Brush.horizontalGradient(listOf(Color(0xFF00E5FF), Color(0xFFFF007F))),
          shape = RoundedCornerShape(26.dp)
        )
        .testTag("profile_edit_dialog"),
      color = Color(0xFF100022)
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
          Text(
            text = "EDIT PROFILE",
            color = Color.White,
            fontSize = 17.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
          )
          IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 1. Profile Picture with Gallery Picker Button
        Box(
          modifier = Modifier
            .size(90.dp)
            .clickable {
              photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
              )
            },
          contentAlignment = Alignment.Center
        ) {
          val resolvedAvatarModel = com.example.ui.storage.LocalImageStorage.resolveImageModel(
            context = context,
            candidateUri = selectedImageUri,
            imageType = com.example.ui.storage.LocalImageStorage.ImageType.PROFILE,
            entityId = currentProfile.userId,
            defaultFallbackRes = currentProfile.avatarRes
          )
          AsyncImage(
            model = resolvedAvatarModel,
            contentDescription = "Profile Picture",
            modifier = Modifier
              .size(90.dp)
              .clip(CircleShape)
              .border(2.dp, Color(0xFF00E5FF), CircleShape),
            contentScale = ContentScale.Crop,
            error = painterResource(id = currentProfile.avatarRes),
            fallback = painterResource(id = currentProfile.avatarRes)
          )

          // Camera Badge Overlay
          Box(
            modifier = Modifier
              .align(Alignment.BottomEnd)
              .size(30.dp)
              .clip(CircleShape)
              .background(Color(0xFFFF007F))
              .border(1.5.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Filled.CameraAlt,
              contentDescription = "Change Photo",
              tint = Color.White,
              modifier = Modifier.size(16.dp)
            )
          }
        }

        Text(
          text = "Tap avatar to select photo from gallery",
          color = Color(0xFF80DEEA),
          fontSize = 11.sp,
          modifier = Modifier.padding(top = 6.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Name Field
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("User Name", color = Color(0xFFCE93D8)) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color(0xFF00E5FF),
            unfocusedBorderColor = Color(0xFF7C4DFF),
            cursorColor = Color(0xFF00E5FF)
          )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Country Dropdown Selector
        ExposedDropdownMenuBox(
          expanded = countryDropdownExpanded,
          onExpandedChange = { countryDropdownExpanded = !countryDropdownExpanded },
          modifier = Modifier.fillMaxWidth()
        ) {
          OutlinedTextField(
            value = "${selectedCountry.flag}  ${selectedCountry.name}",
            onValueChange = {},
            readOnly = true,
            label = { Text("Country / Region", color = Color(0xFFCE93D8)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = countryDropdownExpanded) },
            modifier = Modifier
              .fillMaxWidth()
              .menuAnchor(MenuAnchorType.PrimaryNotEditable),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White,
              focusedBorderColor = Color(0xFF00E5FF),
              unfocusedBorderColor = Color(0xFF7C4DFF)
            )
          )

          ExposedDropdownMenu(
            expanded = countryDropdownExpanded,
            onDismissRequest = { countryDropdownExpanded = false },
            modifier = Modifier.background(Color(0xFF1D0338))
          ) {
            countryList.forEach { option ->
              DropdownMenuItem(
                text = {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = option.flag, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = option.name, color = Color.White, fontSize = 14.sp)
                  }
                },
                onClick = {
                  selectedCountry = option
                  countryDropdownExpanded = false
                }
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 4. Bio Field
        OutlinedTextField(
          value = bio,
          onValueChange = { bio = it },
          label = { Text("Bio Status", color = Color(0xFFCE93D8)) },
          maxLines = 3,
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color(0xFF00E5FF),
            unfocusedBorderColor = Color(0xFF7C4DFF),
            cursorColor = Color(0xFF00E5FF)
          )
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Save Button
        Button(
          onClick = {
            onSave(
              name.trim().ifBlank { currentProfile.username },
              selectedCountry.name,
              selectedCountry.flag,
              bio,
              selectedImageUri
            )
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(46.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
          contentPadding = PaddingValues(0.dp)
        ) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .clip(RoundedCornerShape(14.dp))
              .background(
                Brush.horizontalGradient(listOf(Color(0xFFFF007F), Color(0xFF00E5FF)))
              ),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "SAVE CHANGES",
              color = Color.White,
              fontSize = 13.5.sp,
              fontWeight = FontWeight.Black,
              letterSpacing = 1.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Open Full Details Button
        Button(
          onClick = {
            onDismiss()
            onOpenFullDetails()
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF260447)),
          shape = RoundedCornerShape(12.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Filled.Edit,
              contentDescription = null,
              tint = Color(0xFF00E5FF),
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Open Full Profile Details Screen", color = Color(0xFFEDE7F6), fontSize = 12.sp)
          }
        }
      }
    }
  }
}
