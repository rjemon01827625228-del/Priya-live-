package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import com.example.ui.components.AvatarFrameOverlay
import com.example.ui.storage.StoreCenterStorage
import com.example.ui.storage.StoreItem
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.storage.LevelSystem
import com.example.ui.storage.SocialAgencyStorage
import com.example.ui.storage.UserProfileStorage

/**
 * User Profile UI State representation.
 * Dynamic data loaded directly from permanent UserProfileStorage.
 */
data class UserProfileData(
  val username: String = "MR MAX",
  val userId: String = "9221471",
  val avatarRes: Int = R.drawable.img_streamer_girl,
  val profileImageUri: String? = null,
  val isVerified: Boolean = true,
  val levelBadge: String = "Lv.1",
  val userLevel: Int = 1,
  val vipBadge: String = "V",
  val vipLevel: Int = 0,
  val isVipEnabled: Boolean = true,
  val isVipHidden: Boolean = false,
  val totalRecharge: Long = 0L,
  val genderAge: String = "♂ 18",
  val countryText: String = "🇧🇩",
  val bio: String = "Welcome to Priya Live • Live Stream & Audio Party!",
  val friendCount: String = "0",
  val followCount: String = "0",
  val followersCount: String = "0",
  val visitorsCount: String = "0",
  val coinBalance: String = "10,000",
  val rawCoins: Long = 10_000L,
  val diamondBalance: String = "2,500",
  val equippedFrame: StoreItem? = null,
  val hostStatus: String = "inactive",
  val agencyStatus: String = "inactive",
  val isAgencyJoined: Boolean = false,
  val agencyName: String? = null
)

@Composable
fun ProfileMiScreen(
  onSettingsItemClick: (String) -> Unit,
  onCoinsClick: () -> Unit,
  onLogout: (() -> Unit)? = null,
  onBack: (() -> Unit)? = null,
  onNavigateToSubPage: ((com.example.navigation.AppRoute) -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  // Dedicated Screen Navigation States
  var showDetailProfileScreen by remember { mutableStateOf(false) }
  var showAgencyCentreScreen by remember { mutableStateOf(false) }
  var showFollowersDialog by remember { mutableStateOf(false) }
  var followersInitialTab by remember { mutableIntStateOf(0) }
  var showAccountSwitchDialog by remember { mutableStateOf(false) }

  // Load from permanent storage with dynamic live social stats & level formula
  fun loadProfileFromStorage(): UserProfileData {
    val full = UserProfileStorage.getProfile(context)
    val activeId = SocialAgencyStorage.getActiveUserId(context)
    val genderSymbol = if (full.gender == "Female") "♀" else "♂"
    val followCount = SocialAgencyStorage.getFollowingCount(context, activeId).toString()
    val followersCount = SocialAgencyStorage.getFollowersCount(context, activeId).toString()
    val friendsCount = SocialAgencyStorage.getFollowingList(context, activeId).filter {
      SocialAgencyStorage.getFollowersList(context, activeId).contains(it)
    }.size.toString()

    val rawCoins = UserProfileStorage.getCoinBalance(context, activeId)
    val calculatedLevel = LevelSystem.calculateLevel(rawCoins)
    val dynamicLevelBadge = "Lv.$calculatedLevel"
    val equippedFrame = StoreCenterStorage.getEquippedFrame(context, activeId)

    return UserProfileData(
      username = full.username,
      userId = full.userId,
      avatarRes = R.drawable.img_streamer_girl,
      profileImageUri = full.profileImageUri,
      isVerified = true,
      levelBadge = dynamicLevelBadge,
      userLevel = calculatedLevel,
      vipBadge = full.vipBadge,
      vipLevel = full.vipLevel,
      isVipEnabled = full.isVipEnabled,
      isVipHidden = full.isVipHidden,
      totalRecharge = full.totalRecharge,
      genderAge = "$genderSymbol ${full.age}",
      countryText = full.countryFlag,
      bio = full.bio,
      friendCount = friendsCount,
      followCount = followCount,
      followersCount = followersCount,
      visitorsCount = full.visitorsCount,
      coinBalance = LevelSystem.formatCoins(rawCoins),
      rawCoins = rawCoins,
      diamondBalance = "0",
      equippedFrame = equippedFrame,
      hostStatus = full.hostStatus,
      agencyStatus = full.agencyStatus,
      isAgencyJoined = full.isAgencyJoined,
      agencyName = full.agencyName
    )
  }

  val coroutineScope = rememberCoroutineScope()
  var userProfile by remember { mutableStateOf(loadProfileFromStorage()) }

  // Screen Navigation States
  var showStoreCenterScreen by remember { mutableStateOf(false) }
  var showVipCenterScreen by remember { mutableStateOf(false) }

  // If user opened the VIP Center screen
  if (showVipCenterScreen) {
    VipCenterScreen(
      onBack = {
        showVipCenterScreen = false
        userProfile = loadProfileFromStorage()
      },
      modifier = modifier
    )
    return
  }

  // If user opened the full profile detail screen
  if (showDetailProfileScreen) {
    UserProfileDetailScreen(
      onBack = {
        userProfile = loadProfileFromStorage()
        showDetailProfileScreen = false
      },
      modifier = modifier
    )
    return
  }

  // If user opened the Store Center screen
  if (showStoreCenterScreen) {
    StoreCenterScreen(
      onBack = {
        showStoreCenterScreen = false
        userProfile = loadProfileFromStorage()
      },
      modifier = modifier
    )
    return
  }

  // If user opened the Agency Centre screen
  if (showAgencyCentreScreen) {
    AgencyCentreScreen(
      userProfile = UserProfileStorage.getProfile(context),
      onBack = {
        showAgencyCentreScreen = false
        userProfile = loadProfileFromStorage()
      }
    )
    return
  }

  // Interactive Feature Dialog States
  var showEditProfileDialog by remember { mutableStateOf(false) }
  var showCommissionDialog by remember { mutableStateOf(false) }
  var showCoinDialog by remember { mutableStateOf(false) }
  var showStoreDialog by remember { mutableStateOf(false) }
  var showHostCenterDialog by remember { mutableStateOf(false) }
  var showSettingsDialog by remember { mutableStateOf(false) }
  var showVipDialog by remember { mutableStateOf(false) }
  var showCpDialog by remember { mutableStateOf(false) }
  var showHostRequestDialog by remember { mutableStateOf(false) }
  var showLevelDialog by remember { mutableStateOf(false) }
  var showQrDialog by remember { mutableStateOf(false) }
  var showHelpDialog by remember { mutableStateOf(false) }
  var showAboutDialog by remember { mutableStateOf(false) }
  var showLiveHistoryDialog by remember { mutableStateOf(false) }
  var showParallelBoxDialog by remember { mutableStateOf(false) }

  // Helper to copy user PID to clipboard
  val copyIdToClipboard = {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("User PID", userProfile.userId)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "PID: ${userProfile.userId} copied to clipboard!", Toast.LENGTH_SHORT).show()
  }

  val topSafePadding = com.example.ui.components.rememberResponsiveTopBarPadding(extraTopSpacing = 6.dp)

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("profile_mi_screen"),
    contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = topSafePadding, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // -------------------------------------------------------------------------
    // 1. TOP PROFILE CARD
    // Avatar, MR MAX (MR white, MAX cyan), ♂ Lv.X, 🔥 18, 🇧🇩, ID: 9221471, Copy, Arrow
    // -------------------------------------------------------------------------
    item {
      ProfileHeaderCard(
        userProfile = userProfile,
        onCopyId = copyIdToClipboard,
        onCardClick = {
          if (onNavigateToSubPage != null) {
            onNavigateToSubPage(com.example.navigation.AppRoute.ProfileDetail)
          } else {
            showDetailProfileScreen = true
          }
        },
        onAvatarClick = {
          if (onNavigateToSubPage != null) {
            onNavigateToSubPage(com.example.navigation.AppRoute.ProfileDetail)
          } else {
            showDetailProfileScreen = true
          }
        }
      )
    }

    // -------------------------------------------------------------------------
    // 2. STATUS COUNTERS ROW (Friend: 0, Follow: 0, Followers: 0, Visitors: 0)
    // -------------------------------------------------------------------------
    item {
      ProfileCountersRow(
        friendCount = userProfile.friendCount,
        followCount = userProfile.followCount,
        followersCount = userProfile.followersCount,
        visitorsCount = userProfile.visitorsCount,
        onCounterClick = { label ->
          if (onNavigateToSubPage != null) {
            onNavigateToSubPage(com.example.navigation.AppRoute.FollowersList)
          } else {
            when (label) {
              "Followers" -> {
                followersInitialTab = 0
                showFollowersDialog = true
              }
              "Follow" -> {
                followersInitialTab = 1
                showFollowersDialog = true
              }
              "Friend" -> {
                followersInitialTab = 2
                showFollowersDialog = true
              }
              else -> {
                Toast.makeText(context, "$label list: 0 visitors today", Toast.LENGTH_SHORT).show()
              }
            }
          }
        }
      )
    }

    // -------------------------------------------------------------------------
    // 3. VIP / CP BANNER ROW
    // STRICT VIP (ONLY "VIP", gold metallic, right diamond)
    // STRICT CP (ONLY "CP", gradient neon, right diamond)
    // -------------------------------------------------------------------------
    item {
      VipAndCpBannerRow(
        onVipClick = {
          if (onNavigateToSubPage != null) {
            onNavigateToSubPage(com.example.navigation.AppRoute.VipCentre)
          } else {
            showVipCenterScreen = true
          }
        },
        onCpClick = {
          if (onNavigateToSubPage != null) {
            onNavigateToSubPage(com.example.navigation.AppRoute.CpSpace)
          } else {
            showCpDialog = true
          }
        }
      )
    }

    // -------------------------------------------------------------------------
    // 4. MAIN MENU OPTIONS CONTAINER (3 rows of 2 buttons each)
    // Row 2: Commission | Coin
    // Row 3: Agency (One office building) | Store (One storefront with awning)
    // Row 4: Host Center (Mic) | Settings (Gear)
    // -------------------------------------------------------------------------
    item {
      MainMenuContainer(
        onCommissionClick = {
          if (onNavigateToSubPage != null) {
            onNavigateToSubPage(com.example.navigation.AppRoute.CommissionRecord)
          } else {
            showCommissionDialog = true
          }
        },
        onCoinClick = {
          if (onNavigateToSubPage != null) {
            onNavigateToSubPage(com.example.navigation.AppRoute.CoinTopUp)
          } else {
            showCoinDialog = true
          }
        },
        onAgencyClick = {
          if (onNavigateToSubPage != null) {
            onNavigateToSubPage(com.example.navigation.AppRoute.AgencyCentre)
          } else {
            showAgencyCentreScreen = true
          }
        },
        onStoreClick = {
          if (onNavigateToSubPage != null) {
            onNavigateToSubPage(com.example.navigation.AppRoute.Store)
          } else {
            showStoreCenterScreen = true
          }
        },
        onHostCenterClick = {
          if (onNavigateToSubPage != null) {
            onNavigateToSubPage(com.example.navigation.AppRoute.HostCentre)
          } else {
            showHostCenterDialog = true
          }
        },
        onSettingsClick = {
          if (onNavigateToSubPage != null) {
            onNavigateToSubPage(com.example.navigation.AppRoute.Settings)
          } else {
            showSettingsDialog = true
          }
        }
      )
    }

    // -------------------------------------------------------------------------
    // 5. UTILITY SERVICES GRID (Host Request, Level, QR Code, Help, About Us, Live History, Parallel Box)
    // -------------------------------------------------------------------------
    item {
      GridServicesContainer(
        userLevel = userProfile.userLevel,
        onLevelClick = {
          if (onNavigateToSubPage != null) {
            onNavigateToSubPage(com.example.navigation.AppRoute.LevelDetails)
          } else {
            showLevelDialog = true
          }
        },
        onQrCodeClick = {
          if (onNavigateToSubPage != null) {
            onNavigateToSubPage(com.example.navigation.AppRoute.MyQrCode)
          } else {
            showQrDialog = true
          }
        },
        onHelpClick = {
          if (onNavigateToSubPage != null) {
            onNavigateToSubPage(com.example.navigation.AppRoute.HelpCenter)
          } else {
            showHelpDialog = true
          }
        },
        onAboutUsClick = {
          if (onNavigateToSubPage != null) {
            onNavigateToSubPage(com.example.navigation.AppRoute.AboutUs)
          } else {
            showAboutDialog = true
          }
        },
        onLiveHistoryClick = {
          if (onNavigateToSubPage != null) {
            onNavigateToSubPage(com.example.navigation.AppRoute.LiveHistory)
          } else {
            showLiveHistoryDialog = true
          }
        },
        onParallelBoxClick = {
          if (onNavigateToSubPage != null) {
            onNavigateToSubPage(com.example.navigation.AppRoute.ParallelBox)
          } else {
            showParallelBoxDialog = true
          }
        }
      )
    }
  }

  // ---------------------------------------------------------------------------
  // INTERACTIVE FEATURE DIALOGS
  // ---------------------------------------------------------------------------

  // 1. Edit Profile Dialog with gallery photo picker, name, country
  if (showEditProfileDialog) {
    ProfileEditDialog(
      currentProfile = userProfile,
      onSave = { newName, newCountry, newFlag, newBio, newImageUri ->
        UserProfileStorage.updateBasicInfo(
          context = context,
          username = newName,
          gender = if (userProfile.genderAge.contains("♀")) "Female" else "Male",
          age = userProfile.genderAge.filter { it.isDigit() }.toIntOrNull() ?: 18,
          country = newCountry,
          countryFlag = newFlag,
          bio = newBio
        )
        if (newImageUri != null) {
          UserProfileStorage.updateProfileImage(context, newImageUri)
        }
        userProfile = loadProfileFromStorage()
        showEditProfileDialog = false

        Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
      },
      onOpenFullDetails = {
        showDetailProfileScreen = true
      },
      onDismiss = { showEditProfileDialog = false }
    )
  }

  // 2. Full 50-Level System Dialog with real-time level up and test coin buttons
  if (showLevelDialog) {
    LevelSystemDialog(
      currentCoins = userProfile.rawCoins,
      onAddCoins = { amount ->
        UserProfileStorage.addCoins(context, amount)
        userProfile = loadProfileFromStorage()
        Toast.makeText(context, "+${LevelSystem.formatCoins(amount)} Diamonds Added! Current Level: ${userProfile.levelBadge}", Toast.LENGTH_SHORT).show()
      },
      onDismiss = { showLevelDialog = false }
    )
  }

  // 3. Coin Top-Up Dialog connected to level system
  if (showCoinDialog) {
    CoinTopUpDialog(
      currentCoins = userProfile.rawCoins,
      onAddCoins = { amount ->
        val result = UserProfileStorage.recordVerifiedRecharge(context, amount, userProfile.userId)
        userProfile = loadProfileFromStorage()
        if (result.didLevelUpVip) {
          Toast.makeText(context, "VIP ${result.currentVip} Activated!", Toast.LENGTH_LONG).show()
        }
      },
      onDismiss = { showCoinDialog = false }
    )
  }

  // 4. Parallel Box Dialog
  if (showParallelBoxDialog) {
    ParallelBoxDialog(
      onDismiss = { showParallelBoxDialog = false }
    )
  }

  // 6. Host Request Dialog
  if (showHostRequestDialog) {
    ProfileInfoDialog(
      title = "Host Verification Request",
      emoji = "🎙️",
      glowColor = Color(0xFF00E676),
      details = listOf(
        "Requirements for Official Host Contract:",
        "1. Minimum 18 years old (Verified: 18)",
        "2. Clear audio & video camera test",
        "3. Live at least 15 days/month",
        "Earn up to $1,000+ monthly cash salary & PK bonuses!"
      ),
      actionButtonText = "Submit Application",
      onAction = {
        Toast.makeText(context, "Application submitted for review!", Toast.LENGTH_SHORT).show()
        showHostRequestDialog = false
      },
      onDismiss = { showHostRequestDialog = false }
    )
  }

  // 7. Commission Dialog
  if (showCommissionDialog) {
    ProfileInfoDialog(
      title = "Commission Center",
      emoji = "💰",
      glowColor = Color(0xFFFF007F),
      details = listOf(
        "Available Commission: 0.00 USD",
        "Total Cash Out: 0.00 USD",
        "Commission Rate: 70% Host Share",
        "Bind Payment Method: bKash / Nagad / Bank Account"
      ),
      actionButtonText = "Withdraw Funds",
      onAction = {
        Toast.makeText(context, "Minimum withdrawal limit is 10 USD.", Toast.LENGTH_SHORT).show()
      },
      onDismiss = { showCommissionDialog = false }
    )
  }

  // 8. Store Dialog
  if (showStoreDialog) {
    ProfileInfoDialog(
      title = "Neon Store & Mall",
      emoji = "🛍️",
      glowColor = Color(0xFFFF00A0),
      details = listOf(
        "🚗 Luxury Rides: Cyber Supercar, Neon Dragon",
        "Avatar Frames: Golden VIP, Diamond Halo",
        "💬 Chat Bubbles: Holographic Rainbow, Cyber Matrix",
        "✨ Entrance Effects: Meteor Shower, Fireworks"
      ),
      actionButtonText = "Browse Store",
      onAction = {
        Toast.makeText(context, "Opening Store catalog...", Toast.LENGTH_SHORT).show()
      },
      onDismiss = { showStoreDialog = false }
    )
  }

  // 9. Host Center Dialog
  if (showHostCenterDialog) {
    ProfileInfoDialog(
      title = "Host Center",
      emoji = "🎤",
      glowColor = Color(0xFFFFB300),
      details = listOf(
        "Broadcasting Level: Host Lv.1",
        "Monthly Live Hours: 0h 0m",
        "Target Beans: 0 / 100,000",
        "Host Task: Go live for 30 minutes today for +500 Diamonds"
      ),
      actionButtonText = "Start Streaming",
      onAction = {
        onSettingsItemClick("Go Live")
        showHostCenterDialog = false
      },
      onDismiss = { showHostCenterDialog = false }
    )
  }

  // 10. Settings Dialog
  if (showSettingsDialog) {
    ProfileInfoDialog(
      title = "App Settings",
      emoji = "⚙️",
      glowColor = Color(0xFF00E5FF),
      details = listOf(
        "Account Security: Protected (PID: ${userProfile.userId})",
        "Sound & Microphone: HD Stereo (Enabled)",
        "Video Quality: 1080p Ultra 60fps",
        "Notifications: Enabled",
        "App Version: v2.4.0 (Latest Release)"
      ),
      actionButtonText = "Log Out of Account",
      onAction = {
        showSettingsDialog = false
        onLogout?.invoke()
      },
      onDismiss = { showSettingsDialog = false }
    )
  }

  // 11. VIP Privilege Club Dialog
  if (showVipDialog) {
    ProfileInfoDialog(
      title = "VIP Privilege Club",
      emoji = "💎",
      glowColor = Color(0xFFFFD700),
      details = listOf(
        "Current Tier: VIP 1",
        "Exclusive Gold Crown Chat Badge",
        "Bullet Chat Screen Fly-Over Enabled",
        "Custom Entrance Animation",
        "Higher PK Multiplier (1.2x)"
      ),
      actionButtonText = "Upgrade VIP (Top Up)",
      onAction = {
        showVipDialog = false
        showCoinDialog = true
      },
      onDismiss = { showVipDialog = false }
    )
  }

  // 12. CP Couple Space Dialog
  if (showCpDialog) {
    ProfileInfoDialog(
      title = "CP Couple Space",
      emoji = "💖",
      glowColor = Color(0xFFFF4081),
      details = listOf(
        "CP Partner: None (Single)",
        "Send a CP Ring to your favorite partner",
        "Unlock Shared CP Room Banner & Profile Badge",
        "Weekly CP Ranking Rewards & Exclusive Effects"
      ),
      actionButtonText = "Invite Partner",
      onAction = {
        Toast.makeText(context, "Search user by ID or Name to propose CP!", Toast.LENGTH_SHORT).show()
      },
      onDismiss = { showCpDialog = false }
    )
  }

  // 13. QR Code Dialog
  if (showQrDialog) {
    QrCodeDialog(
      username = userProfile.username,
      userId = userProfile.userId,
      avatarRes = userProfile.avatarRes,
      onDismiss = { showQrDialog = false }
    )
  }

  // 14. 24/7 Help Dialog
  if (showHelpDialog) {
    ProfileInfoDialog(
      title = "24/7 Official Help Center",
      emoji = "🎧",
      glowColor = Color(0xFF00E5FF),
      details = listOf(
        "Official WhatsApp Support: +8801818022519",
        "Official Email: support@neonlive.com",
        "Live Room Troubleshooter",
        "Recharge & Refund Dispute Assistance"
      ),
      actionButtonText = "Contact Support",
      onAction = {
        Toast.makeText(context, "Opening Helpline Chat...", Toast.LENGTH_SHORT).show()
      },
      onDismiss = { showHelpDialog = false }
    )
  }

  // 15. About Us Dialog
  if (showAboutDialog) {
    ProfileInfoDialog(
      title = "About Priya Live",
      emoji = "ℹ️",
      glowColor = Color(0xFF00B0FF),
      details = listOf(
        "App: Priya Live HD Streaming & Audio Party",
        "Build: 2026.09 Official Stable",
        "Terms of Service & Community Guidelines",
        "Privacy Policy & Content Protection",
        "Copyright © 2026 Priya Live Global. All Rights Reserved."
      ),
      actionButtonText = "Community Guidelines",
      onAction = {
        Toast.makeText(context, "Community safety rules loaded.", Toast.LENGTH_SHORT).show()
      },
      onDismiss = { showAboutDialog = false }
    )
  }

  // 16. Live Broadcast History Dialog
  if (showLiveHistoryDialog) {
    ProfileInfoDialog(
      title = "Live Broadcast History",
      emoji = "📹",
      glowColor = Color(0xFF7C4DFF),
      details = listOf(
        "Total Streams Conducted: 0",
        "Total Live Duration: 0h 0m",
        "Peak Concurrent Viewers: 0",
        "Beans Received: 0",
        "Replays are archived for 30 days."
      ),
      actionButtonText = "Refresh History",
      onAction = {
        Toast.makeText(context, "Live history refreshed (0 records)", Toast.LENGTH_SHORT).show()
      },
      onDismiss = { showLiveHistoryDialog = false }
    )
  }

  // 17. Followers / Following / Friends Dialog
  if (showFollowersDialog) {
    FollowersListDialog(
      initialTab = followersInitialTab,
      currentUserId = SocialAgencyStorage.getActiveUserId(context),
      onDismiss = { showFollowersDialog = false },
      onCountsChanged = { userProfile = loadProfileFromStorage() }
    )
  }

  // 18. Account Switch Dialog
  if (showAccountSwitchDialog) {
    AccountSwitchDialog(
      currentActiveId = SocialAgencyStorage.getActiveUserId(context),
      onDismiss = { showAccountSwitchDialog = false },
      onAccountSwitched = { _ ->
        userProfile = loadProfileFromStorage()
      }
    )
  }
}

// -----------------------------------------------------------------------------
// NEON DIAMOND ACTION SYMBOL
// Recreates the small diamond/arrow action symbol shown in the reference image
// -----------------------------------------------------------------------------
@Composable
fun NeonDiamondActionSymbol(
  modifier: Modifier = Modifier,
  glowColor: Color = Color(0xFF00E5FF)
) {
  Canvas(modifier = modifier.size(16.dp)) {
    val w = size.width
    val h = size.height

    // Cyan diamond outline
    val diamond = Path().apply {
      moveTo(w * 0.5f, 0f)
      lineTo(w, h * 0.5f)
      lineTo(w * 0.5f, h)
      lineTo(0f, h * 0.5f)
      close()
    }
    drawPath(
      path = diamond,
      color = glowColor,
      style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    )

    // Inner chevron pointing right >
    val chevron = Path().apply {
      moveTo(w * 0.40f, h * 0.32f)
      lineTo(w * 0.62f, h * 0.50f)
      lineTo(w * 0.40f, h * 0.68f)
    }
    drawPath(
      path = chevron,
      color = Color.White,
      style = Stroke(width = 1.4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
  }
}

// -----------------------------------------------------------------------------
// 1. TOP PROFILE CARD (Matches Screenshot exactly)
// -----------------------------------------------------------------------------
@Composable
private fun ProfileHeaderCard(
  userProfile: UserProfileData,
  onCopyId: () -> Unit,
  onCardClick: () -> Unit,
  onAvatarClick: () -> Unit = onCardClick
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(24.dp))
      .background(
        Brush.verticalGradient(
          listOf(
            Color(0xFF14002C),
            Color(0xFF090018),
            Color(0xFF050010)
          )
        )
      )
      .border(
        width = 1.8.dp,
        brush = Brush.linearGradient(
          listOf(
            Color(0xFFFF00A0),
            Color(0xFF7C4DFF),
            Color(0xFF00E5FF),
            Color(0xFFFF00A0)
          )
        ),
        shape = RoundedCornerShape(24.dp)
      )
      .padding(horizontal = 14.dp, vertical = 14.dp)
      .testTag("top_profile_card")
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // 1. Avatar with equipped dynamic vector frame overlay
      Box(
        modifier = Modifier
          .clickable(onClick = onAvatarClick),
        contentAlignment = Alignment.Center
      ) {
        AvatarFrameOverlay(
          size = 68.dp,
          frame = userProfile.equippedFrame,
          profileImageUri = userProfile.profileImageUri,
          avatarRes = userProfile.avatarRes
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      // 2. Middle Content: Username, 3 Badges, ID: 9221471
      Column(
        modifier = Modifier
          .weight(1f)
          .clickable(onClick = onAvatarClick),
        verticalArrangement = Arrangement.Center
      ) {
        // Line 1: Username "MR MAX" ("MR " white, "MAX" cyan)
        val nameParts = userProfile.username.split(" ", limit = 2)
        Row(verticalAlignment = Alignment.CenterVertically) {
          if (nameParts.size == 2) {
            Text(
              text = nameParts[0].uppercase() + " ",
              color = Color.White,
              fontSize = 24.sp,
              fontWeight = FontWeight.Black,
              letterSpacing = 1.sp
            )
            Text(
              text = nameParts[1].uppercase(),
              color = Color(0xFF00E5FF),
              fontSize = 24.sp,
              fontWeight = FontWeight.Black,
              letterSpacing = 1.sp
            )
          } else {
            Text(
              text = userProfile.username.uppercase(),
              color = Color.White,
              fontSize = 24.sp,
              fontWeight = FontWeight.Black,
              letterSpacing = 1.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Line 2: 3 Badges (Gender/Level, Flame/Age, Flag)
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          // Pill 1: ♂ Lv.X
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFF021B3B))
              .border(1.2.dp, Color(0xFF00B0FF), RoundedCornerShape(12.dp))
              .padding(horizontal = 7.dp, vertical = 2.5.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = if (userProfile.genderAge.contains("♀")) "♀" else "♂",
                color = Color(0xFF00E5FF),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = "Lv.${userProfile.userLevel}",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          // Pill 2: 🔥 18
          val ageNum = userProfile.genderAge.filter { it.isDigit() }.ifBlank { "18" }
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(
                Brush.horizontalGradient(
                  listOf(Color(0xFF380816), Color(0xFF22050E))
                )
              )
              .border(1.2.dp, Color(0xFFFF3D00), RoundedCornerShape(12.dp))
              .padding(horizontal = 7.dp, vertical = 2.5.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = "🔥", fontSize = 10.sp)
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = ageNum,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          // Pill 3: Flag 🇧🇩
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(Color(0xFF082810))
              .border(1.dp, Color(0xFF00E676), RoundedCornerShape(6.dp))
              .padding(horizontal = 5.dp, vertical = 2.dp)
          ) {
            Text(
              text = userProfile.countryText.ifBlank { "🇧🇩" },
              fontSize = 13.sp
            )
          }

          // Pill 4: Royal VIP Badge
          if (userProfile.vipLevel > 0 && userProfile.isVipEnabled && !userProfile.isVipHidden) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(
                  Brush.horizontalGradient(
                    listOf(Color(0xFFFFD700), Color(0xFFFF9D00))
                  )
                )
                .border(1.dp, Color(0xFFFFF8E1), RoundedCornerShape(12.dp))
                .padding(horizontal = 7.dp, vertical = 2.5.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Filled.WorkspacePremium,
                  contentDescription = null,
                  tint = Color.Black,
                  modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                  text = "VIP ${userProfile.vipLevel}",
                  color = Color.Black,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Black
                )
              }
            }
          }

          // Pill 5: HOST Badge (Blue, professional, text: HOST, no emoji, driven by real hostStatus)
          if (userProfile.hostStatus.equals("active", ignoreCase = true)) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFF1565C0))
                .border(1.dp, Color(0xFF42A5F5), RoundedCornerShape(6.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "HOST",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
              )
            }
          }

          // Pill 6: AGENCY Badge (Red, professional, text: AGENCY, no emoji, driven by real agencyStatus)
          if (userProfile.agencyStatus.equals("active", ignoreCase = true) || userProfile.isAgencyJoined) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFC62828))
                .border(1.dp, Color(0xFFEF5350), RoundedCornerShape(6.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "AGENCY",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Line 3: ID:9221471 (Tap to copy)
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.clickable(onClick = onCopyId)
        ) {
          Text(
            text = "PID:${userProfile.userId}",
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
          )
          Spacer(modifier = Modifier.width(6.dp))
          Icon(
            imageVector = Icons.Filled.ContentCopy,
            contentDescription = "Copy PID",
            tint = Color(0xFF00E5FF).copy(alpha = 0.8f),
            modifier = Modifier.size(13.dp)
          )
        }
      }

      // 3. Right Circular Arrow Button
      Box(
        modifier = Modifier
          .size(44.dp)
          .clip(CircleShape)
          .background(Color(0xFF0A001C))
          .border(
            width = 1.8.dp,
            brush = Brush.linearGradient(
              listOf(Color(0xFF00E5FF), Color(0xFF7C4DFF))
            ),
            shape = CircleShape
          )
          .clickable(onClick = onCardClick),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowForward,
          contentDescription = "Edit Profile",
          tint = Color.White,
          modifier = Modifier.size(18.dp)
        )
      }
    }
  }
}

// -----------------------------------------------------------------------------
// 2. STATUS COUNTERS ROW (Friend, Follow, Followers, Visitors)
// -----------------------------------------------------------------------------
@Composable
private fun ProfileCountersRow(
  friendCount: String,
  followCount: String,
  followersCount: String,
  visitorsCount: String,
  onCounterClick: (String) -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(20.dp))
      .background(Color(0xFF080018))
      .border(
        width = 1.6.dp,
        brush = Brush.horizontalGradient(
          listOf(Color(0xFF00B0FF), Color(0xFF00E5FF), Color(0xFF00B0FF))
        ),
        shape = RoundedCornerShape(20.dp)
      )
      .padding(vertical = 10.dp, horizontal = 2.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // 1. Friend
      SocialStatColumn(
        icon = Icons.Filled.People,
        count = friendCount,
        label = "Friend",
        modifier = Modifier.weight(1f),
        onClick = { onCounterClick("Friend") }
      )

      StatVerticalDivider()

      // 2. Follow
      SocialStatColumn(
        icon = Icons.Filled.PersonAdd,
        count = followCount,
        label = "Follow",
        modifier = Modifier.weight(1f),
        onClick = { onCounterClick("Follow") }
      )

      StatVerticalDivider()

      // 3. Followers
      SocialStatColumn(
        icon = Icons.Filled.Group,
        count = followersCount,
        label = "Followers",
        modifier = Modifier.weight(1f),
        onClick = { onCounterClick("Followers") }
      )

      StatVerticalDivider()

      // 4. Visitors
      SocialStatColumn(
        icon = Icons.Filled.Visibility,
        count = visitorsCount,
        label = "Visitors",
        modifier = Modifier.weight(1f),
        onClick = { onCounterClick("Visitors") }
      )
    }
  }
}

@Composable
private fun SocialStatColumn(
  icon: ImageVector,
  count: String,
  label: String,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Column(
    modifier = modifier
      .clickable(onClick = onClick)
      .padding(vertical = 2.dp),
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
        modifier = Modifier.size(16.dp)
      )
      Spacer(modifier = Modifier.width(5.dp))
      Text(
        text = count,
        color = Color.White,
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold
      )
    }
    Spacer(modifier = Modifier.height(3.dp))
    Text(
      text = label,
      color = Color(0xFFD1C4E9),
      fontSize = 11.5.sp,
      fontWeight = FontWeight.Medium
    )
  }
}

@Composable
private fun StatVerticalDivider() {
  Box(
    modifier = Modifier
      .width(1.dp)
      .height(26.dp)
      .background(Color(0xFF00E5FF).copy(alpha = 0.35f))
  )
}

// -----------------------------------------------------------------------------
// 3. VIP / CP BANNER ROW
// VIP: ONLY "VIP", bold metallic gold gradient, right diamond symbol
// CP: ONLY "CP", bold neon gradient (cyan/purple/pink), right diamond symbol
// -----------------------------------------------------------------------------
@Composable
private fun VipAndCpBannerRow(
  onVipClick: () -> Unit,
  onCpClick: () -> Unit
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // Left: VIP Button Card
    Box(
      modifier = Modifier
        .weight(1f)
        .height(68.dp)
        .clip(RoundedCornerShape(22.dp))
        .background(
          Brush.verticalGradient(
            listOf(Color(0xFF221500), Color(0xFF120B00), Color(0xFF060300))
          )
        )
        .border(
          width = 1.8.dp,
          brush = Brush.linearGradient(
            listOf(Color(0xFFFFD700), Color(0xFFFF9100), Color(0xFFFFEA00))
          ),
          shape = RoundedCornerShape(22.dp)
        )
        .clickable(onClick = onVipClick)
        .padding(horizontal = 14.dp),
      contentAlignment = Alignment.Center
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Spacer(modifier = Modifier.width(16.dp))

        // Large bold gold metallic "SVIP" - ONLY text SVIP
        Text(
          text = "SVIP",
          fontSize = 30.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 1.5.sp,
          style = TextStyle(
            brush = Brush.linearGradient(
              listOf(
                Color(0xFFFFF9C4),
                Color(0xFFFFD700),
                Color(0xFFFFA000)
              )
            )
          )
        )

        // Right small diamond action symbol
        NeonDiamondActionSymbol(glowColor = Color(0xFF00E5FF))
      }
    }

    // Right: CP Button Card
    Box(
      modifier = Modifier
        .weight(1f)
        .height(68.dp)
        .clip(RoundedCornerShape(22.dp))
        .background(
          Brush.verticalGradient(
            listOf(Color(0xFF0F022E), Color(0xFF080018), Color(0xFF04000D))
          )
        )
        .border(
          width = 1.8.dp,
          brush = Brush.linearGradient(
            listOf(Color(0xFF00E5FF), Color(0xFFD500F9), Color(0xFF00E5FF))
          ),
          shape = RoundedCornerShape(22.dp)
        )
        .clickable(onClick = onCpClick)
        .padding(horizontal = 14.dp),
      contentAlignment = Alignment.Center
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Spacer(modifier = Modifier.width(16.dp))

        // Large bold neon gradient "CP" - ONLY text CP
        Text(
          text = "CP",
          fontSize = 34.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 2.sp,
          style = TextStyle(
            brush = Brush.linearGradient(
              listOf(
                Color(0xFF00E5FF),
                Color(0xFFE040FB),
                Color(0xFFFF4081)
              )
            )
          )
        )

        // Right small diamond action symbol
        NeonDiamondActionSymbol(glowColor = Color(0xFF00E5FF))
      }
    }
  }
}

// -----------------------------------------------------------------------------
// 4. MAIN MENU OPTIONS CONTAINER (Commission, Coin, Agency, Store, Host Center, Settings)
// -----------------------------------------------------------------------------
@Composable
private fun MainMenuContainer(
  onCommissionClick: () -> Unit,
  onCoinClick: () -> Unit,
  onAgencyClick: () -> Unit,
  onStoreClick: () -> Unit,
  onHostCenterClick: () -> Unit,
  onSettingsClick: () -> Unit
) {
  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // Row 2: Commission & Coin
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      MainMenuCard(
        title = "Commission",
        subtitle = "Earn More",
        icon = Icons.Filled.MonetizationOn,
        iconTint = Color(0xFFFFD700),
        circleBackground = Color(0xFF380024),
        circleBorder = Color(0xFFFF007F),
        borderGradient = listOf(Color(0xFFFF007F), Color(0xFF9C27B0)),
        subtitleColor = Color(0xFFD8B4FE),
        modifier = Modifier.weight(1f),
        onClick = onCommissionClick
      )

      MainMenuCard(
        title = "Diamond",
        subtitle = "Top Up & Get More",
        icon = Icons.Filled.Diamond,
        iconTint = Color(0xFF00E5FF),
        circleBackground = Color(0xFF002238),
        circleBorder = Color(0xFF00E5FF),
        borderGradient = listOf(Color(0xFF00E5FF), Color(0xFF0091EA)),
        subtitleColor = Color(0xFF80D8FF),
        modifier = Modifier.weight(1f),
        onClick = onCoinClick
      )
    }

    // Row 3: Agency & Store
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Agency: ONE clean corporate office building icon in cyan circle
      MainMenuCard(
        title = "Agency",
        subtitle = "Join Agency",
        icon = Icons.Filled.Domain,
        iconTint = Color(0xFF00E5FF),
        circleBackground = Color(0xFF002238),
        circleBorder = Color(0xFF00E5FF),
        borderGradient = listOf(Color(0xFF00E5FF), Color(0xFF00B4D8)),
        subtitleColor = Color(0xFF80DEEA),
        modifier = Modifier.weight(1f),
        onClick = onAgencyClick
      )

      // Store: ONE clean storefront with awning icon in pink circle
      MainMenuCard(
        title = "Store",
        subtitle = "Premium Gifts",
        icon = Icons.Filled.Store,
        iconTint = Color(0xFFFF4081),
        circleBackground = Color(0xFF380024),
        circleBorder = Color(0xFFFF007F),
        borderGradient = listOf(Color(0xFFFF007F), Color(0xFFFF4081)),
        subtitleColor = Color(0xFFFF80AB),
        modifier = Modifier.weight(1f),
        onClick = onStoreClick
      )
    }

    // Row 4: Host Center & Settings
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Host Center: Clean microphone in gold circle
      MainMenuCard(
        title = "Host Center",
        subtitle = "Manage Your Host",
        icon = Icons.Filled.Mic,
        iconTint = Color(0xFFFFD700),
        circleBackground = Color(0xFF332000),
        circleBorder = Color(0xFFFFB300),
        borderGradient = listOf(Color(0xFFFFB300), Color(0xFFFF6F00)),
        subtitleColor = Color(0xFFFFE082),
        modifier = Modifier.weight(1f),
        onClick = onHostCenterClick
      )

      // Settings: Clean gear in purple circle
      MainMenuCard(
        title = "Settings",
        subtitle = "App Settings",
        icon = Icons.Filled.Settings,
        iconTint = Color(0xFFB388FF),
        circleBackground = Color(0xFF1C0838),
        circleBorder = Color(0xFF7C4DFF),
        borderGradient = listOf(Color(0xFF7C4DFF), Color(0xFF3D5AFE)),
        subtitleColor = Color(0xFFB388FF),
        modifier = Modifier.weight(1f),
        onClick = onSettingsClick
      )
    }
  }
}

@Composable
private fun MainMenuCard(
  title: String,
  subtitle: String,
  icon: ImageVector,
  iconTint: Color,
  circleBackground: Color,
  circleBorder: Color,
  borderGradient: List<Color>,
  subtitleColor: Color,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Box(
    modifier = modifier
      .height(58.dp)
      .clip(RoundedCornerShape(24.dp))
      .background(
        Brush.verticalGradient(
          listOf(Color(0xFF15012B), Color(0xFF0A0018))
        )
      )
      .border(
        width = 1.6.dp,
        brush = Brush.horizontalGradient(borderGradient),
        shape = RoundedCornerShape(24.dp)
      )
      .clickable(onClick = onClick)
      .padding(horizontal = 10.dp),
    contentAlignment = Alignment.CenterStart
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Circular glowing icon container
      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(CircleShape)
          .background(circleBackground)
          .border(1.2.dp, circleBorder, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = title,
          tint = iconTint,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.width(8.dp))

      // Title & Subtitle Column
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.Center
      ) {
        Text(
          text = title,
          color = Color.White,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          maxLines = 1
        )
        Text(
          text = subtitle,
          color = subtitleColor,
          fontSize = 9.5.sp,
          fontWeight = FontWeight.Normal,
          maxLines = 1
        )
      }

      // Small diamond action symbol
      NeonDiamondActionSymbol(glowColor = Color(0xFF00E5FF))
    }
  }
}

// -----------------------------------------------------------------------------
// 5. GRID SERVICES CONTAINER (Level, QR Code, Help, About Us, Live History, Parallel Box)
// -----------------------------------------------------------------------------
@Composable
private fun GridServicesContainer(
  userLevel: Int,
  onLevelClick: () -> Unit,
  onQrCodeClick: () -> Unit,
  onHelpClick: () -> Unit,
  onAboutUsClick: () -> Unit,
  onLiveHistoryClick: () -> Unit,
  onParallelBoxClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(22.dp))
      .background(Color(0xFF080018))
      .border(
        width = 1.6.dp,
        brush = Brush.linearGradient(
          listOf(Color(0xFF00B0FF), Color(0xFF7C4DFF), Color(0xFF00E5FF))
        ),
        shape = RoundedCornerShape(22.dp)
      )
      .padding(10.dp)
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      // Row 1: 3 Items (Level, My QR Code, Help)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        GridSquareCard(
          icon = Icons.Filled.MilitaryTech,
          label = "Level",
          subLabel = "◇ Lv.$userLevel",
          borderColor = Color(0xFF00E5FF),
          iconTint = Color(0xFF00E5FF),
          modifier = Modifier.weight(1f),
          onClick = onLevelClick
        )

        GridSquareCard(
          icon = Icons.Filled.QrCode2,
          label = "My QR Code",
          borderColor = Color(0xFF00E5FF),
          iconTint = Color(0xFF00E5FF),
          modifier = Modifier.weight(1f),
          onClick = onQrCodeClick
        )

        GridSquareCard(
          icon = Icons.Filled.HeadsetMic,
          label = "Help",
          borderColor = Color(0xFFFF007F),
          iconTint = Color(0xFFFF4081),
          modifier = Modifier.weight(1f),
          onClick = onHelpClick
        )
      }

      // Row 2: 3 Items (About Us, Live History, Parallel Box)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        GridSquareCard(
          icon = Icons.Filled.Info,
          label = "About Us",
          borderColor = Color(0xFF00B0FF),
          iconTint = Color(0xFF00E5FF),
          modifier = Modifier.weight(1f),
          onClick = onAboutUsClick
        )

        GridSquareCard(
          icon = Icons.Filled.Videocam,
          label = "Live History",
          borderColor = Color(0xFFE040FB),
          iconTint = Color(0xFFE040FB),
          modifier = Modifier.weight(1f),
          onClick = onLiveHistoryClick
        )

        GridSquareCard(
          icon = Icons.Filled.Widgets,
          label = "Parallel Box",
          borderColor = Color(0xFF00E5FF),
          iconTint = Color(0xFF00E5FF),
          modifier = Modifier.weight(1f),
          onClick = onParallelBoxClick
        )
      }
    }
  }
}

@Composable
private fun GridSquareCard(
  icon: ImageVector,
  label: String,
  subLabel: String? = null,
  borderColor: Color,
  iconTint: Color,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Column(
    modifier = modifier
      .height(76.dp)
      .clip(RoundedCornerShape(16.dp))
      .background(
        Brush.verticalGradient(
          listOf(Color(0xFF15012B), Color(0xFF080016))
        )
      )
      .border(
        width = 1.4.dp,
        color = borderColor,
        shape = RoundedCornerShape(16.dp)
      )
      .clickable(onClick = onClick)
      .padding(horizontal = 4.dp, vertical = 6.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceEvenly
  ) {
    Icon(
      imageVector = icon,
      contentDescription = label,
      tint = iconTint,
      modifier = Modifier.size(24.dp)
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = label,
        color = Color.White,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        lineHeight = 11.sp,
        maxLines = 2
      )
      if (subLabel != null) {
        Text(
          text = subLabel,
          color = Color(0xFF00E5FF),
          fontSize = 9.sp,
          fontWeight = FontWeight.ExtraBold,
          textAlign = TextAlign.Center
        )
      }
    }
  }
}

// -----------------------------------------------------------------------------
// DIALOGS: QR CODE & GENERAL FEATURE DIALOG
// -----------------------------------------------------------------------------

@Composable
private fun QrCodeDialog(
  username: String,
  userId: String,
  avatarRes: Int,
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
          .fillMaxWidth(0.85f)
          .clip(RoundedCornerShape(22.dp))
          .border(
            width = 2.dp,
            brush = Brush.horizontalGradient(listOf(Color(0xFF00E5FF), Color(0xFFFF007F))),
            shape = RoundedCornerShape(22.dp)
          ),
        color = Color(0xFF140026)
      ) {
        Column(
          modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
        Text(
          text = "MY PROFILE QR CODE",
          color = Color.White,
          fontSize = 16.sp,
          fontWeight = FontWeight.Black
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Avatar + Name Header
        Row(verticalAlignment = Alignment.CenterVertically) {
          Image(
            painter = painterResource(id = avatarRes),
            contentDescription = null,
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .border(1.5.dp, Color.White, CircleShape),
            contentScale = ContentScale.Crop
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(text = username, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black)
            Text(text = "PID: $userId", color = Color(0xFF00E5FF), fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // QR Canvas Box
        Box(
          modifier = Modifier
            .size(190.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(12.dp),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Filled.QrCode2,
            contentDescription = "QR Code",
            tint = Color.Black,
            modifier = Modifier.fillMaxSize()
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "Scan this QR code in Priya Live to follow instantly",
          color = Color(0xFFB39DDB),
          fontSize = 11.sp,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = onDismiss,
          modifier = Modifier
            .fillMaxWidth()
            .height(42.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF))
        ) {
          Text("Done", color = Color.White, fontWeight = FontWeight.Bold)
        }
      }
      }
    }
  }
}

@Composable
private fun ProfileInfoDialog(
  title: String,
  emoji: String,
  glowColor: Color,
  details: List<String>,
  actionButtonText: String,
  onAction: () -> Unit,
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
          .fillMaxWidth(0.9f)
          .clip(RoundedCornerShape(22.dp))
          .border(
            width = 1.6.dp,
            brush = Brush.horizontalGradient(listOf(glowColor, Color(0xFF00E5FF))),
            shape = RoundedCornerShape(22.dp)
          ),
        color = Color(0xFF140028)
      ) {
        Column(
          modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
        // Emoji Header
        Box(
          modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(glowColor.copy(alpha = 0.2f))
            .border(1.5.dp, glowColor, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Text(text = emoji, fontSize = 26.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = title,
          color = Color.White,
          fontSize = 17.sp,
          fontWeight = FontWeight.Black
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Details list
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF22033D))
            .padding(12.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          details.forEach { line ->
            Text(
              text = line,
              color = Color(0xFFEDE7F6),
              fontSize = 12.sp,
              lineHeight = 16.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Action CTA
        Button(
          onClick = onAction,
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
          contentPadding = PaddingValues(0.dp)
        ) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .clip(RoundedCornerShape(12.dp))
              .background(
                Brush.horizontalGradient(listOf(glowColor, Color(0xFF00E5FF)))
              ),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = actionButtonText,
              color = Color.White,
              fontSize = 13.sp,
              fontWeight = FontWeight.Black
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
          onClick = onDismiss,
          modifier = Modifier
            .fillMaxWidth()
            .height(38.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FFFFFF))
        ) {
          Text("Close", color = Color.White, fontSize = 12.sp)
        }
      }
      }
    }
  }
}
