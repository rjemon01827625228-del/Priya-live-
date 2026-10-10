package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.aistudio.neonparty.nxvqlr.R
import com.example.ui.components.AvatarFrameOverlay
import com.example.ui.storage.StoreCenterStorage
import com.example.ui.storage.StoreItem
import com.example.ui.storage.UserProfileStorage
import com.example.ui.storage.VipSystem
import com.example.ui.storage.VipTier

/**
 * SVIP CENTER SCREEN
 *
 * Implements the SVIP specifications:
 * - Top Header: Back button, Title "SVIP", Help (?), Settings (⚙)
 * - Thin Banners: "Welcome to Party" & "Welcome to Live"
 * - Horizontal SVIP level tabs (VIP+, SVIP1 - SVIP10) with diamond indicator
 * - Grand Golden Medallion Showcase with SVIP ribbon and live avatar preview
 * - Dynamic Admin Items: Strictly dynamic only (No fake frame/tag/bubble/emoji).
 *   If no items added from admin panel: shows clean "No SVIP items available yet."
 * - 5 Exclusive Privileges list with professional vector icons
 * - Fixed Bottom Wealth Card: Avatar, status, wealth value, gradient progress bar,
 *   required amount, SVIP badge, and golden "Recharge" button
 * - Verified recharge modal (100K - 51.2M) with instant VIP level unlock
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VipCenterScreen(
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val activeUserId = remember { UserProfileStorage.getActiveUserId(context) }
  val userProfile = remember(activeUserId) { UserProfileStorage.getProfileForUser(context, activeUserId) }

  var totalRecharge by remember {
    mutableLongStateOf(UserProfileStorage.getTotalRecharge(context, activeUserId))
  }
  var currentVipLevel by remember {
    mutableIntStateOf(VipSystem.calculateVIPLevel(totalRecharge))
  }

  // Selected level: 0 = VIP+ (overview), 1..10 = SVIP1..SVIP10
  var selectedVipLevel by remember {
    mutableIntStateOf(if (currentVipLevel > 0) currentVipLevel else 1)
  }

  var equippedFrameId by remember {
    mutableStateOf(StoreCenterStorage.getEquippedFrame(context, activeUserId)?.id)
  }

  var showRechargeSheet by remember { mutableStateOf(false) }
  var showHelpDialog by remember { mutableStateOf(false) }
  var showSettingsDialog by remember { mutableStateOf(false) }
  var celebrationVipTier by remember { mutableStateOf<VipTier?>(null) }

  var isVipEnabled by remember {
    mutableStateOf(UserProfileStorage.isVipEnabled(context, activeUserId))
  }
  var isVipHidden by remember {
    mutableStateOf(UserProfileStorage.isVipHidden(context, activeUserId))
  }

  var adminItemsList by remember {
    mutableStateOf(listOf<StoreItem>())
  }

  fun reloadData() {
    val updatedRecharge = UserProfileStorage.getTotalRecharge(context, activeUserId)
    totalRecharge = updatedRecharge
    isVipEnabled = UserProfileStorage.isVipEnabled(context, activeUserId)
    isVipHidden = UserProfileStorage.isVipHidden(context, activeUserId)
    val rawVip = VipSystem.calculateVIPLevel(updatedRecharge)
    currentVipLevel = if (!isVipEnabled) 0 else rawVip
    equippedFrameId = StoreCenterStorage.getEquippedFrame(context, activeUserId)?.id

    // Strictly dynamic items added from Admin Panel (No fake items or fake rewards)
    val allCatalog = StoreCenterStorage.getAllItems(context)
    adminItemsList = allCatalog.filter { item ->
      item.category == "SVIP" ||
          (item.presetStyle.startsWith("SVIP_") && item.presetStyle.contains("${selectedVipLevel}"))
    }
  }

  LaunchedEffect(activeUserId, selectedVipLevel) {
    reloadData()
  }

  val effectiveLevel = if (selectedVipLevel == 0) 1 else selectedVipLevel
  val selectedTier = remember(effectiveLevel) {
    VipSystem.getTier(effectiveLevel) ?: VipSystem.VIP_LEVELS.first()
  }

  val isUnlocked = currentVipLevel >= effectiveLevel
  val isCurrentEquipped = equippedFrameId == selectedTier.frameId
  val topSafePadding = com.example.ui.components.rememberResponsiveTopBarPadding(extraTopSpacing = 0.dp)
  val bottomSafePadding = com.example.ui.components.rememberResponsiveNavigationBarPadding(extraBottomSpacing = 0.dp)

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.radialGradient(
          colors = listOf(
            Color(0xFF38204F),
            Color(0xFF140822),
            Color(0xFF09040E)
          ),
          center = Offset(Float.POSITIVE_INFINITY, 0f),
          radius = 1800f
        )
      )
      .padding(top = topSafePadding, bottom = bottomSafePadding)
      .testTag("vip_center_screen")
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // 1. TOP HEADER: [‹] SVIP [?][⚙]
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        // Back button
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color(0x2EFFFFFF))
            .border(1.dp, Color(0x3DFFFFFF), CircleShape)
            .clickable(onClick = onBack)
            .testTag("svip_back_button"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
        }

        // Center Title: SVIP
        Text(
          text = "SVIP",
          color = Color(0xFFFFD45C),
          fontSize = 22.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 2.sp
        )

        // Right actions: [?] [⚙]
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(Color(0x2EFFFFFF))
              .border(1.dp, Color(0x3DFFFFFF), CircleShape)
              .clickable { showHelpDialog = true }
              .testTag("svip_help_button"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.HelpOutline,
              contentDescription = "Help",
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
          }

          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(Color(0x2EFFFFFF))
              .border(1.dp, Color(0x3DFFFFFF), CircleShape)
              .clickable { showSettingsDialog = true }
              .testTag("svip_settings_button"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Filled.Settings,
              contentDescription = "Settings",
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      // 2. THIN BANNERS: "Welcome to Party" & "Welcome to Live"
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Party thin banner
        Box(
          modifier = Modifier
            .weight(1f)
            .height(30.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(Color(0xB8140A23))
            .border(1.dp, Color(0x8CB450FF), RoundedCornerShape(15.dp))
            .padding(horizontal = 10.dp),
          contentAlignment = Alignment.Center
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.VolumeUp,
              contentDescription = null,
              tint = Color(0xFFD580FF),
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Welcome to Party",
              color = Color(0xFFE8C8FF),
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }

        // Live thin banner
        Box(
          modifier = Modifier
            .weight(1f)
            .height(30.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(Color(0xB8140A23))
            .border(1.dp, Color(0x8CB450FF), RoundedCornerShape(15.dp))
            .padding(horizontal = 10.dp),
          contentAlignment = Alignment.Center
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = Icons.Filled.Star,
              contentDescription = null,
              tint = Color(0xFFFF80DF),
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Welcome to Live",
              color = Color(0xFFFFD4F0),
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      }

      // 3. HORIZONTAL SVIP LEVELS TRACK (VIP+, SVIP1 - SVIP10)
      val tabScrollState = rememberScrollState()
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(tabScrollState)
          .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Tab 0: VIP+
        val isVipPlusActive = selectedVipLevel == 0
        SvipLevelTabItem(
          label = "VIP+",
          isActive = isVipPlusActive,
          isUnlocked = currentVipLevel > 0,
          onClick = { selectedVipLevel = 0 }
        )

        // Tabs 1..10: SVIP1..SVIP10
        for (lvl in 1..10) {
          val isActive = selectedVipLevel == lvl
          val isTabUnlocked = currentVipLevel >= lvl
          SvipLevelTabItem(
            label = "SVIP$lvl",
            isActive = isActive,
            isUnlocked = isTabUnlocked,
            onClick = { selectedVipLevel = lvl }
          )
        }
      }

      // 4. MAIN SCROLLABLE BODY
      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 4.dp, bottom = 120.dp)
      ) {
        // A. GRAND MEDALLION SHOWCASE (Live Avatar + Emblem)
        item {
          SvipMedallionShowcase(
            levelLabel = if (!isVipEnabled) "VIP OFF" else if (selectedVipLevel == 0) "VIP+" else "SVIP$effectiveLevel",
            isVipEnabled = isVipEnabled,
            userProfilePhotoUri = userProfile.profileImageUri,
            userAvatarRes = R.drawable.img_streamer_girl,
            equippedFrame = StoreCenterStorage.getEquippedFrame(context, activeUserId)
          )
        }

        // B. DYNAMIC ADMIN ITEMS SECTION (Strictly dynamic, no fake items or fake rewards)
        item {
          SvipDynamicItemsSection(
            items = adminItemsList,
            isTierUnlocked = isUnlocked,
            onItemAction = { item ->
              StoreCenterStorage.equipItem(context, item.category, item.id, activeUserId)
              reloadData()
              Toast.makeText(context, "${item.name} Equipped!", Toast.LENGTH_SHORT).show()
            }
          )
        }
      }
    }

    // 5. PINNED BOTTOM WEALTH / RECHARGE CARD (Matches HTML/CSS & Screenshot)
    SvipBottomWealthCard(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(horizontal = 14.dp, vertical = 10.dp),
      tier = selectedTier,
      levelLabel = if (!isVipEnabled) "VIP OFF" else if (selectedVipLevel == 0) "VIP+" else "SVIP$effectiveLevel",
      totalRecharge = totalRecharge,
      currentVip = currentVipLevel,
      userProfilePhotoUri = userProfile.profileImageUri,
      userAvatarRes = R.drawable.img_streamer_girl,
      onRechargeClick = { showRechargeSheet = true }
    )

    // 6. VERIFIED RECHARGE MODAL
    if (showRechargeSheet) {
      VerifiedRechargeSheet(
        currentRecharge = totalRecharge,
        targetVipTier = selectedTier,
        onDismiss = { showRechargeSheet = false },
        onRechargeSuccess = { addedCoins, result ->
          showRechargeSheet = false
          reloadData()
          if (result.didLevelUpVip) {
            celebrationVipTier = VipSystem.getTier(result.currentVip)
          }
          Toast.makeText(
            context,
            "Recharge Verified! +${VipSystem.formatCoins(addedCoins)} Coins. Total: ${VipSystem.formatCoins(result.newTotalRecharge)}",
            Toast.LENGTH_LONG
          ).show()
        }
      )
    }

    // 7. HELP & RULES DIALOG (?)
    if (showHelpDialog) {
      SvipHelpDialog(onDismiss = { showHelpDialog = false })
    }

    // 8. SETTINGS & ADMIN DIALOG (⚙)
    if (showSettingsDialog) {
      SvipSettingsDialog(
        isVipEnabled = isVipEnabled,
        onToggleVipEnabled = { enabled ->
          UserProfileStorage.setVipEnabled(context, activeUserId, enabled)
          reloadData()
          val msg = if (enabled) "VIP Status Activated" else "VIP Status Turned Off"
          Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        },
        isVipHidden = isVipHidden,
        onToggleVipHidden = { hidden ->
          UserProfileStorage.setVipHidden(context, activeUserId, hidden)
          reloadData()
          val msg = if (hidden) "VIP Status Hidden from profile & chat" else "VIP Status Visible"
          Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        },
        onDismiss = { showSettingsDialog = false }
      )
    }

    // 9. LEVEL UP CELEBRATION MODAL
    celebrationVipTier?.let { unlockedTier ->
      SvipCelebrationDialog(
        tier = unlockedTier,
        onEquipNow = {
          StoreCenterStorage.equipItem(context, "FRAME", unlockedTier.frameId, activeUserId)
          reloadData()
          celebrationVipTier = null
          Toast.makeText(context, "${unlockedTier.frameName} Equipped!", Toast.LENGTH_SHORT).show()
        },
        onDismiss = { celebrationVipTier = null }
      )
    }
  }
}

// -----------------------------------------------------------------------------
// SVIP LEVEL TAB ITEM
// -----------------------------------------------------------------------------
@Composable
private fun SvipLevelTabItem(
  label: String,
  isActive: Boolean,
  isUnlocked: Boolean,
  onClick: () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.clickable(onClick = onClick)
  ) {
    // Diamond Indicator above active tab
    Box(
      modifier = Modifier
        .size(10.dp)
        .padding(bottom = 2.dp),
      contentAlignment = Alignment.Center
    ) {
      if (isActive) {
        Icon(
          imageVector = Icons.Filled.Diamond,
          contentDescription = null,
          tint = Color(0xFFFFD45C),
          modifier = Modifier.size(9.dp)
        )
      }
    }

    val tabBackground = if (isActive) {
      Brush.linearGradient(
        listOf(
          Color(0xFFFFB300),
          Color(0xFFFF4FD8),
          Color(0xFF7047FF)
        )
      )
    } else {
      Brush.linearGradient(
        listOf(
          Color(0x1AFFFFFF),
          Color(0x10FFFFFF)
        )
      )
    }

    val borderColor = if (isActive) Color(0xFFFFD45C) else Color(0x2EFFFFFF)

    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(18.dp))
        .background(tabBackground)
        .border(
          width = if (isActive) 1.6.dp else 1.dp,
          color = borderColor,
          shape = RoundedCornerShape(18.dp)
        )
        .padding(horizontal = 14.dp, vertical = 8.dp)
        .testTag("svip_tab_$label"),
      contentAlignment = Alignment.Center
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        if (!isUnlocked && label != "VIP+") {
          Icon(
            imageVector = Icons.Filled.Lock,
            contentDescription = null,
            tint = if (isActive) Color.White.copy(alpha = 0.8f) else Color(0xFF8E8E93),
            modifier = Modifier.size(11.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
          text = label,
          color = if (isActive) Color.White else if (isUnlocked) Color(0xFFFFD45C) else Color(0xFFAAAAAA),
          fontSize = 12.5.sp,
          fontWeight = if (isActive) FontWeight.Black else FontWeight.Bold
        )
      }
    }
  }
}

// -----------------------------------------------------------------------------
// SVIP MEDALLION SHOWCASE (Grand Royal Medallion & Live Avatar)
// -----------------------------------------------------------------------------
@Composable
private fun SvipMedallionShowcase(
  levelLabel: String,
  isVipEnabled: Boolean,
  userProfilePhotoUri: String?,
  userAvatarRes: Int,
  equippedFrame: StoreItem?
) {
  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.985f,
    targetValue = 1.015f,
    animationSpec = infiniteRepeatable(
      animation = tween(2000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulseScale"
  )

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(26.dp))
      .background(
        Brush.radialGradient(
          colors = listOf(
            Color(0x35FFB43C),
            Color(0x18140822),
            Color(0x00000000)
          )
        )
      )
      .padding(vertical = 14.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Large Royal Medallion Emblem with concentric glowing rings
      Box(
        modifier = Modifier
          .size(190.dp)
          .scale(pulseScale),
        contentAlignment = Alignment.Center
      ) {
        // Outer concentric glowing rings
        Canvas(modifier = Modifier.fillMaxSize()) {
          val center = Offset(size.width / 2f, size.height / 2f)
          val radius = size.minDimension / 2f - 12f

          // Outer halo
          drawCircle(
            brush = Brush.radialGradient(
              colors = listOf(
                if (isVipEnabled) Color(0x55FFD700) else Color(0x33BDBDBD),
                Color(0x00000000)
              ),
              center = center,
              radius = radius + 10f
            ),
            radius = radius + 10f,
            center = center
          )

          // Golden Laurel / Bezel Ring
          drawCircle(
            brush = Brush.sweepGradient(
              colors = if (isVipEnabled) listOf(
                Color(0xFFFFE082),
                Color(0xFFFFB300),
                Color(0xFFFF8F00),
                Color(0xFFFFE082)
              ) else listOf(
                Color(0xFF9E9E9E),
                Color(0xFF616161),
                Color(0xFF757575),
                Color(0xFF9E9E9E)
              ),
              center = center
            ),
            radius = radius,
            center = center,
            style = Stroke(width = 4f)
          )

          // Inner rim
          drawCircle(
            color = Color(0x33FFFFFF),
            radius = radius - 8f,
            center = center,
            style = Stroke(width = 1.5f)
          )
        }

        // Center Live Avatar with user's equipped frame (if any)
        Box(
          modifier = Modifier.size(130.dp),
          contentAlignment = Alignment.Center
        ) {
          AvatarFrameOverlay(
            size = 92.dp,
            frame = equippedFrame,
            profileImageUri = userProfilePhotoUri,
            avatarRes = userAvatarRes
          )
        }

        // Curved Ribbon Banner displaying "SVIP 1" / levelLabel / "VIP OFF" at the bottom
        Box(
          modifier = Modifier
            .align(Alignment.BottomCenter)
            .offset(y = (-6).dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
              Brush.horizontalGradient(
                if (isVipEnabled) listOf(
                  Color(0xFF5D3800),
                  Color(0xFFFFB300),
                  Color(0xFFFFE082),
                  Color(0xFFFFB300),
                  Color(0xFF5D3800)
                ) else listOf(
                  Color(0xFF37474F),
                  Color(0xFF546E7A),
                  Color(0xFF78909C),
                  Color(0xFF546E7A),
                  Color(0xFF37474F)
                )
              )
            )
            .border(
              1.2.dp,
              if (isVipEnabled) Color(0xFFFFF176) else Color(0xFFB0BEC5),
              RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 22.dp, vertical = 5.dp)
        ) {
          Text(
            text = levelLabel,
            color = if (isVipEnabled) Color(0xFF261300) else Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.2.sp
          )
        }
      }
    }
  }
}

// -----------------------------------------------------------------------------
// DYNAMIC ADMIN ITEMS SECTION
// Strictly shows items added from Admin Panel.
// If none added: displays "No SVIP items available yet."
// -----------------------------------------------------------------------------
@Composable
private fun SvipDynamicItemsSection(
  items: List<StoreItem>,
  isTierUnlocked: Boolean,
  onItemAction: (StoreItem) -> Unit
) {
  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "SVIP EXCLUSIVE ITEMS",
        color = Color(0xFFFFD45C),
        fontSize = 12.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 1.sp
      )

      if (items.isNotEmpty()) {
        Text(
          text = "${items.size} Available",
          color = Color(0xFFCE93D8),
          fontSize = 11.sp
        )
      }
    }

    if (items.isEmpty()) {
      // Clean HTML empty-state box matching user specification
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(Color(0x0AFFFFFF))
          .border(
            width = 1.dp,
            color = Color(0x24FFFFFF),
            shape = RoundedCornerShape(14.dp)
          )
          .padding(vertical = 24.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(
            imageVector = Icons.Filled.Info,
            contentDescription = null,
            tint = Color(0xFF9F8AA8),
            modifier = Modifier.size(22.dp)
          )
          Text(
            text = "No SVIP items available yet.",
            color = Color(0xFF9F8AA8),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
          )
        }
      }
    } else {
      // Dynamic Admin Items Grid (2 columns)
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.chunked(2).forEach { rowItems ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            rowItems.forEach { item ->
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(14.dp))
                  .background(Color(0xFF1E0A30))
                  .border(1.dp, Color(0x40FFD45C), RoundedCornerShape(14.dp))
                  .padding(12.dp)
              ) {
                Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.spacedBy(6.dp),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Box(
                    modifier = Modifier
                      .size(46.dp)
                      .clip(CircleShape)
                      .background(Color(0x33FFB300)),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = Icons.Filled.WorkspacePremium,
                      contentDescription = null,
                      tint = Color(0xFFFFD45C),
                      modifier = Modifier.size(24.dp)
                    )
                  }

                  Text(
                    text = item.name,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )

                  Text(
                    text = item.durationText,
                    color = Color(0xFFCE93D8),
                    fontSize = 10.sp
                  )

                  if (isTierUnlocked) {
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFFB300))
                        .clickable { onItemAction(item) }
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                      Text(
                        text = "EQUIP",
                        color = Color.Black,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                      )
                    }
                  }
                }
              }
            }
            if (rowItems.size == 1) {
              Spacer(modifier = Modifier.weight(1f))
            }
          }
        }
      }
    }
  }
}


// -----------------------------------------------------------------------------
// PINNED BOTTOM WEALTH / RECHARGE CARD (Matches HTML/CSS & Screenshot)
// -----------------------------------------------------------------------------
@Composable
private fun SvipBottomWealthCard(
  modifier: Modifier = Modifier,
  tier: VipTier,
  levelLabel: String,
  totalRecharge: Long,
  currentVip: Int,
  userProfilePhotoUri: String?,
  userAvatarRes: Int,
  onRechargeClick: () -> Unit
) {
  val isUnlocked = currentVip >= tier.level
  val progress = if (tier.requiredRecharge > 0) {
    (totalRecharge.toFloat() / tier.requiredRecharge.toFloat()).coerceIn(0f, 1f)
  } else 1f

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(22.dp))
      .background(Color(0xFF140822).copy(alpha = 0.96f))
      .border(1.2.dp, Color(0x55FFD45C), RoundedCornerShape(22.dp))
      .padding(14.dp)
      .testTag("svip_wealth_card")
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      // Header Row: Avatar + Status + Wealth Value
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // User Avatar with Gold Rim
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .border(1.5.dp, Color(0xFFFFD45C), CircleShape)
        ) {
          if (!userProfilePhotoUri.isNullOrBlank()) {
            AsyncImage(
              model = userProfilePhotoUri,
              contentDescription = "User Avatar",
              modifier = Modifier.fillMaxSize()
            )
          } else {
            androidx.compose.foundation.Image(
              painter = androidx.compose.ui.res.painterResource(id = userAvatarRes),
              contentDescription = "User Avatar",
              modifier = Modifier.fillMaxSize()
            )
          }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = if (isUnlocked) "$levelLabel Activated" else "You don't have SVIP",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Current Wealth Value: ${VipSystem.formatCoins(totalRecharge)}",
            color = Color(0xFFD1C4E9),
            fontSize = 11.sp
          )
        }

        // Golden Rounded Recharge Button
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(
              Brush.horizontalGradient(
                listOf(Color(0xFFFFD45C), Color(0xFFFF9D00))
              )
            )
            .border(1.dp, Color(0xFFFFE082), RoundedCornerShape(18.dp))
            .clickable(onClick = onRechargeClick)
            .padding(horizontal = 16.dp, vertical = 7.dp)
            .testTag("svip_recharge_button"),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "Recharge",
            color = Color(0xFF301800),
            fontSize = 12.sp,
            fontWeight = FontWeight.Black
          )
        }
      }

      // Smooth Custom Gradient Progress Bar
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(7.dp)
          .clip(RoundedCornerShape(4.dp))
          .background(Color(0xFF2B1440))
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth(progress)
            .height(7.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(
              Brush.horizontalGradient(
                listOf(Color(0xFFFFB300), Color(0xFFFF4FCF))
              )
            )
        )
      }

      // Wealth Footer: Required Amount & SVIP Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Required: ${VipSystem.formatCoins(tier.requiredRecharge)}",
          color = Color(0xFFCE93D8),
          fontSize = 11.sp
        )

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0x33FFB300))
            .border(1.dp, Color(0x66FFD45C), RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
          Text(
            text = levelLabel,
            color = Color(0xFFFFD45C),
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

// -----------------------------------------------------------------------------
// VERIFIED RECHARGE BOTTOM SHEET (Packages 100K - 51.2M)
// -----------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VerifiedRechargeSheet(
  currentRecharge: Long,
  targetVipTier: VipTier,
  onDismiss: () -> Unit,
  onRechargeSuccess: (Long, com.example.ui.storage.VerifiedRechargeResult) -> Unit
) {
  val context = LocalContext.current
  val activeUserId = remember { UserProfileStorage.getActiveUserId(context) }
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  val rechargeOptions = remember {
    listOf(
      Triple(100_000L, "$9.99", "Unlocks SVIP 1"),
      Triple(200_000L, "$19.99", "Unlocks SVIP 2"),
      Triple(400_000L, "$39.99", "Unlocks SVIP 3"),
      Triple(800_000L, "$79.99", "Unlocks SVIP 4"),
      Triple(1_600_000L, "$149.99", "Unlocks SVIP 5"),
      Triple(3_200_000L, "$299.99", "Unlocks SVIP 6"),
      Triple(6_400_000L, "$599.99", "Unlocks SVIP 7"),
      Triple(12_800_000L, "$1,199.99", "Unlocks SVIP 8"),
      Triple(25_600_000L, "$2,399.99", "Unlocks SVIP 9"),
      Triple(51_200_000L, "$4,799.99", "Unlocks SVIP 10")
    )
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = Color(0xFF140822),
    scrimColor = Color.Black.copy(alpha = 0.75f)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "VERIFIED RECHARGE",
            color = Color(0xFFFFD45C),
            fontSize = 18.sp,
            fontWeight = FontWeight.Black
          )
          Text(
            text = "Current: ${VipSystem.formatCoins(currentRecharge)} Coins (SVIP ${VipSystem.calculateVIPLevel(currentRecharge)})",
            color = Color(0xFFCE93D8),
            fontSize = 11.sp
          )
        }

        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
          Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
        }
      }

      Text(
        text = "Select an official package to verify payment and automatically unlock SVIP privileges:",
        color = Color(0xFFE0E0E0),
        fontSize = 12.sp
      )

      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .height(340.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(rechargeOptions) { (coins, price, note) ->
          val willReachVip = VipSystem.calculateVIPLevel(currentRecharge + coins)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(Color(0xFF220A38))
              .border(1.dp, Color(0x55FFD45C), RoundedCornerShape(14.dp))
              .clickable {
                val result = UserProfileStorage.recordVerifiedRecharge(context, coins, activeUserId)
                onRechargeSuccess(coins, result)
              }
              .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(CircleShape)
                  .background(Color(0x33FFD45C)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Filled.MonetizationOn,
                  contentDescription = null,
                  tint = Color(0xFFFFD45C),
                  modifier = Modifier.size(20.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "${VipSystem.formatCoins(coins)} Diamonds",
                  color = Color.White,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = if (willReachVip > 0) "Advances to SVIP $willReachVip" else note,
                  color = Color(0xFFFFD45C),
                  fontSize = 11.sp
                )
              }
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Brush.horizontalGradient(listOf(Color(0xFFFFD45C), Color(0xFFFF9D00))))
                .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
              Text(
                text = price,
                color = Color(0xFF301800),
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Black
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

// -----------------------------------------------------------------------------
// HELP & RULES DIALOG (?)
// -----------------------------------------------------------------------------
@Composable
private fun SvipHelpDialog(onDismiss: () -> Unit) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(
      usePlatformDefaultWidth = false,
      decorFitsSystemWindows = false
    )
  ) {
    com.example.ui.components.CenteredModalDialogContainer(onDismiss = onDismiss) {
      Surface(
        modifier = Modifier
          .fillMaxWidth(0.90f)
          .clip(RoundedCornerShape(22.dp))
          .border(1.5.dp, Color(0xFFFFD45C), RoundedCornerShape(22.dp)),
        color = Color(0xFF140822)
      ) {
        Column(
          modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "SVIP RULES & GUIDE",
            color = Color(0xFFFFD45C),
            fontSize = 16.sp,
            fontWeight = FontWeight.Black
          )
          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
          }
        }

        Text(
          text = "• SVIP is automatically unlocked based on your cumulative recharge wealth value.\n" +
              "• SVIP 1: 100K Diamonds\n" +
              "• SVIP 2: 200K Diamonds\n" +
              "• SVIP 3: 400K Diamonds\n" +
              "• SVIP 4: 800K Diamonds\n" +
              "• SVIP 5: 1.6M Diamonds\n" +
              "• SVIP 6: 3.2M Diamonds\n" +
              "• SVIP 7: 6.4M Diamonds\n" +
              "• SVIP 8: 12.8M Diamonds\n" +
              "• SVIP 9: 25.6M Diamonds\n" +
              "• SVIP 10: 51.2M Diamonds\n\n" +
              "• Real items added by administrators appear instantly in your SVIP rewards catalog.",
          color = Color(0xFFE0E0E0),
          fontSize = 12.sp,
          lineHeight = 18.sp
        )

        Button(
          onClick = onDismiss,
          modifier = Modifier
            .fillMaxWidth()
            .height(42.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD45C)),
          shape = RoundedCornerShape(14.dp)
        ) {
          Text("UNDERSTOOD", color = Color(0xFF301800), fontWeight = FontWeight.Bold)
        }
      }
      }
    }
  }
}

// -----------------------------------------------------------------------------
// SETTINGS DIALOG (⚙)
// -----------------------------------------------------------------------------
@Composable
private fun SvipSettingsDialog(
  isVipEnabled: Boolean,
  onToggleVipEnabled: (Boolean) -> Unit,
  isVipHidden: Boolean,
  onToggleVipHidden: (Boolean) -> Unit,
  onDismiss: () -> Unit
) {
  var showEntranceEffect by remember { mutableStateOf(true) }
  var showVipBadgeInChat by remember { mutableStateOf(true) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(
      usePlatformDefaultWidth = false,
      decorFitsSystemWindows = false
    )
  ) {
    com.example.ui.components.CenteredModalDialogContainer(onDismiss = onDismiss) {
      Surface(
        modifier = Modifier
          .fillMaxWidth(0.90f)
          .clip(RoundedCornerShape(22.dp))
          .border(1.5.dp, Color(0xFFFFD45C), RoundedCornerShape(22.dp)),
        color = Color(0xFF140822)
      ) {
        Column(
          modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "SVIP SETTINGS",
            color = Color(0xFFFFD45C),
            fontSize = 16.sp,
            fontWeight = FontWeight.Black
          )
          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
          }
        }

        // 1. VIP Active Status (চালু / বন্ধ)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f).padding(end = 10.dp)) {
            Text(
              text = "VIP Status (চালু / বন্ধ)",
              color = Color.White,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = if (isVipEnabled) "ভিআইপি বর্তমানে চালু আছে" else "ভিআইপি বর্তমানে বন্ধ (Inactive)",
              color = if (isVipEnabled) Color(0xFFFFD45C) else Color(0xFFEF9A9A),
              fontSize = 11.sp
            )
          }
          Switch(
            checked = isVipEnabled,
            onCheckedChange = onToggleVipEnabled,
            colors = SwitchDefaults.colors(
              checkedThumbColor = Color(0xFFFFD45C),
              checkedTrackColor = Color(0xFF5D3800),
              uncheckedThumbColor = Color(0xFF888888),
              uncheckedTrackColor = Color(0xFF333333)
            )
          )
        }

        HorizontalDivider(color = Color(0x22FFFFFF), thickness = 0.8.dp)

        // 2. Hide VIP (ভিআইপি হাইড)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f).padding(end = 10.dp)) {
            Text(
              text = "Hide VIP (ভিআইপি হাইড)",
              color = Color.White,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = if (isVipHidden) "প্রোফাইল ও চ্যাটে ভিআইপি ব্যাজ হাইড থাকবে" else "ভিআইপি ব্যাজ সবার কাছে দৃশ্যমান",
              color = if (isVipHidden) Color(0xFFFFD45C) else Color(0xFFCE93D8),
              fontSize = 11.sp
            )
          }
          Switch(
            checked = isVipHidden,
            onCheckedChange = onToggleVipHidden,
            colors = SwitchDefaults.colors(
              checkedThumbColor = Color(0xFFFFD45C),
              checkedTrackColor = Color(0xFF5D3800),
              uncheckedThumbColor = Color(0xFF888888),
              uncheckedTrackColor = Color(0xFF333333)
            )
          )
        }

        HorizontalDivider(color = Color(0x22FFFFFF), thickness = 0.8.dp)

        // 3. Room Entrance Banner
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f).padding(end = 10.dp)) {
            Text("Show Room Entrance Banner", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text("Displays grand entry in live/party rooms", color = Color(0xFFCE93D8), fontSize = 11.sp)
          }
          Switch(
            checked = showEntranceEffect,
            onCheckedChange = { showEntranceEffect = it },
            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFFD45C))
          )
        }

        // 4. Show SVIP Badge in Chat
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f).padding(end = 10.dp)) {
            Text("Show SVIP Badge in Chat", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text("Displays your SVIP status badge beside your name", color = Color(0xFFCE93D8), fontSize = 11.sp)
          }
          Switch(
            checked = showVipBadgeInChat,
            onCheckedChange = { showVipBadgeInChat = it },
            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFFD45C))
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

      }
      }
    }
  }
}

// -----------------------------------------------------------------------------
// SVIP LEVEL UP CELEBRATION MODAL
// -----------------------------------------------------------------------------
@Composable
private fun SvipCelebrationDialog(
  tier: VipTier,
  onEquipNow: () -> Unit,
  onDismiss: () -> Unit
) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(
      usePlatformDefaultWidth = false,
      decorFitsSystemWindows = false
    )
  ) {
    com.example.ui.components.CenteredModalDialogContainer(onDismiss = onDismiss) {
      Surface(
        modifier = Modifier
          .fillMaxWidth(0.90f)
          .clip(RoundedCornerShape(26.dp))
          .border(2.dp, Color(0xFFFFD45C), RoundedCornerShape(26.dp)),
        color = Color(0xFF140822)
      ) {
        Column(
          modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
        Box(
          modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(Brush.radialGradient(listOf(Color(0xFFFFD45C), Color(0xFFFF9D00))))
            .border(2.dp, Color(0xFFFFE082), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Filled.WorkspacePremium,
            contentDescription = null,
            tint = Color(0xFF261300),
            modifier = Modifier.size(40.dp)
          )
        }

        Text(
          text = "SVIP UNLOCKED!",
          color = Color(0xFFFFD45C),
          fontSize = 20.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 1.2.sp
        )

        Text(
          text = "You have unlocked SVIP ${tier.level} (${tier.title})!",
          color = Color.White,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Center
        )

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF220A38))
            .border(1.dp, Color(0x66FFD45C), RoundedCornerShape(14.dp))
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF00E676),
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = tier.frameName,
              color = Color.White,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Permanent Frame Added to Backpack",
              color = Color(0xFFFFD45C),
              fontSize = 11.sp
            )
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier
              .weight(1f)
              .height(44.dp),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x66FFFFFF))
          ) {
            Text("DONE", color = Color.White, fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = onEquipNow,
            modifier = Modifier
              .weight(1.3f)
              .height(44.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD45C)),
            shape = RoundedCornerShape(14.dp)
          ) {
            Text("EQUIP NOW", color = Color(0xFF301800), fontWeight = FontWeight.Black)
          }
        }
      }
      }
    }
  }
}
