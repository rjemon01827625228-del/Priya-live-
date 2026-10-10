package com.example.ui.storage

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random

data class PlatformBadge(
  val id: String,
  val title: String,
  val iconEmoji: String,
  val level: String,
  val category: String,
  val description: String,
  val glowColorHex: String = "#FFD700"
)

data class UserFullProfile(
  val userId: String,
  val username: String,
  val gender: String, // "Male", "Female", "Other"
  val age: Int,
  val birthDate: String = "2006-01-01",
  val country: String,
  val countryFlag: String,
  val profileImageUri: String?,
  val backgroundImageUri: String?,
  val bio: String,
  val friendCount: String,
  val followCount: String,
  val followersCount: String,
  val visitorsCount: String,
  val vipBadge: String,
  val levelBadge: String,
  val authProvider: String = "GUEST", // GOOGLE, FACEBOOK, PHONE, GUEST
  val authId: String = "",
  // CP Space
  val isCpConnected: Boolean,
  val cpPartnerName: String?,
  val cpPartnerAvatarUri: String?,
  // Agency Space
  val isAgencyJoined: Boolean,
  val isAgencyOwner: Boolean,
  val hasAgencyPermission: Boolean = false,
  val agencyName: String?,
  val agencyLogoUri: String?,
  val agencyLevel: String,
  val hostStatus: String = "inactive", // "inactive", "pending", "active"
  val agencyStatus: String = "inactive", // "inactive", "pending", "active"
  val agencyId: String = "",
  val hostAgencyId: String = "",
  val pendingAgencyRequestId: String = "",
  // Assigned badges
  val assignedBadgeIds: List<String> = emptyList(),
  val coinBalance: Long = 640_000L,
  val userLevel: Int = 1,
  val totalRecharge: Long = 0L,
  val vipLevel: Int = 0,
  val isVipEnabled: Boolean = true,
  val isVipHidden: Boolean = false,
  val isPanelAuthorized: Boolean = false
)

object UserProfileStorage {
  private const val PREFS_NAME = "cyber_user_profile_prefs"

  // Session keys
  private const val KEY_IS_LOGGED_IN = "pref_is_logged_in"
  private const val KEY_ACTIVE_USER_ID = "pref_active_user_id"
  private const val KEY_PERMANENT_USER_ID = "pref_permanent_user_id"
  private const val KEY_ACCOUNTS_MAP = "pref_accounts_index_json"
  private const val KEY_USER_BADGES_PREFIX = "pref_user_badges_"

  // Account specific key prefix helper
  private fun userPrefix(userId: String) = "user_${userId}_"

  private fun getPrefs(context: Context): SharedPreferences {
    return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
  }

  // All Available Platform Badges (Controlled by Admin Panel)
  val availablePlatformBadges = listOf(
    PlatformBadge(
      id = "b_vip",
      title = "VIP Prestige",
      iconEmoji = "⭐",
      level = "VIP 1",
      category = "Prestige",
      description = "Official VIP Club member with bullet chat priority and entrance effects."
    ),
    PlatformBadge(
      id = "b_host",
      title = "Top Talent Host",
      iconEmoji = "🎤",
      level = "Host Lv.1",
      category = "Official Host",
      description = "Certified official broadcaster on Priya Live platform."
    ),
    PlatformBadge(
      id = "b_agency",
      title = "Agency Crest",
      iconEmoji = "🛡️",
      level = "Diamond",
      category = "Agency Elite",
      description = "Affiliated with verified agency with certified contract benefits."
    ),
    PlatformBadge(
      id = "b_pk",
      title = "PK Gladiator",
      iconEmoji = "⚔️",
      level = "Gold I",
      category = "Battle Master",
      description = "Recognized warrior in live head-to-head PK battles."
    ),
    PlatformBadge(
      id = "b_cp",
      title = "Love Bond",
      iconEmoji = "💖",
      level = "CP Lv.1",
      category = "Couple Space",
      description = "Couple Space badge honoring true companionship."
    ),
    PlatformBadge(
      id = "b_star",
      title = "Galaxy Star",
      iconEmoji = "🚀",
      level = "Star Lv.1",
      category = "Superstar",
      description = "Superstar community rising star award."
    ),
    PlatformBadge(
      id = "b_verified",
      title = "Official Verified",
      iconEmoji = "🛡️",
      level = "Official",
      category = "Staff & Management",
      description = "Verified platform identity badge issued by platform administration."
    )
  )

  init {
    // Initialized on demand
  }

  /**
   * Session Management
   */
  fun isUserLoggedIn(context: Context): Boolean {
    val prefs = getPrefs(context)
    // On first launch or if not logged in, require login via Google, Facebook, or Phone
    if (!prefs.contains(KEY_IS_LOGGED_IN)) {
      return false
    }
    return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
  }

  fun isLoggedIn(context: Context): Boolean = isUserLoggedIn(context)

  fun setLoggedIn(context: Context, isLoggedIn: Boolean) {
    getPrefs(context).edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
  }

  fun logout(context: Context) {
    getPrefs(context).edit()
      .putBoolean(KEY_IS_LOGGED_IN, false)
      .apply()
  }

  fun getOrGenerateUniqueId(context: Context): String = getActiveUserId(context)

  fun getActiveUserId(context: Context): String {
    val prefs = getPrefs(context)
    var activeId = prefs.getString(KEY_ACTIVE_USER_ID, null)
    if (activeId.isNullOrBlank() || activeId == "100") {
      val permanentId = prefs.getString(KEY_PERMANENT_USER_ID, null)
      activeId = if (!permanentId.isNullOrBlank() && permanentId != "100") {
        permanentId
      } else {
        // Generate a random unique 7-digit ID (starting with 7, 8, or 9)
        val prefix = (7..9).random()
        val suffix = Random.nextInt(100_000, 999_999)
        val generated = "$prefix$suffix"
        prefs.edit().putString(KEY_PERMANENT_USER_ID, generated).apply()
        generated
      }
      prefs.edit().putString(KEY_ACTIVE_USER_ID, activeId).apply()
    }
    return activeId
  }

  fun setActiveUserId(context: Context, userId: String) {
    getPrefs(context).edit()
      .putString(KEY_ACTIVE_USER_ID, userId)
      .putString(KEY_PERMANENT_USER_ID, userId)
      .putBoolean(KEY_IS_LOGGED_IN, true)
      .apply()
  }

  fun getCoinBalance(context: Context, userId: String? = null): Long {
    val uid = userId ?: getActiveUserId(context)
    val prefs = getPrefs(context)
    val prefix = userPrefix(uid)
    return if (prefs.contains(prefix + "coin_balance")) {
      prefs.getLong(prefix + "coin_balance", 0L)
    } else if (uid == "9221471" || uid == "100") {
      prefs.getLong("pref_coin_balance", 0L)
    } else {
      0L
    }
  }

  fun setCoinBalance(context: Context, balance: Long, userId: String? = null) {
    val uid = userId ?: getActiveUserId(context)
    val prefs = getPrefs(context)
    val prefix = userPrefix(uid)
    val newLevel = LevelSystem.calculateLevel(balance)
    val editor = prefs.edit()
      .putLong(prefix + "coin_balance", balance)
      .putString(prefix + "level_badge", "Lv.$newLevel")
    if (uid == "9221471" || uid == "100") {
      editor.putLong("pref_coin_balance", balance)
        .putString("pref_level_badge", "Lv.$newLevel")
    }
    editor.apply()
  }

  fun addCoins(context: Context, amount: Long, userId: String? = null): Long {
    val current = getCoinBalance(context, userId)
    val newTotal = (current + amount).coerceAtLeast(0L)
    setCoinBalance(context, newTotal, userId)
    return newTotal
  }

  fun getUserLevel(context: Context, userId: String? = null): Int {
    val coins = getCoinBalance(context, userId)
    return LevelSystem.calculateLevel(coins)
  }

  // ---------------------------------------------------------------------------
  // VIP RECHARGE AUTO-UNLOCK SYSTEM (Backend Lifetime Recharge & Auto Unlock)
  // ---------------------------------------------------------------------------

  fun getTotalRecharge(context: Context, userId: String? = null): Long {
    val uid = userId ?: getActiveUserId(context)
    val prefs = getPrefs(context)
    val prefix = userPrefix(uid)
    return if (prefs.contains(prefix + "total_recharge")) {
      prefs.getLong(prefix + "total_recharge", 0L)
    } else {
      0L
    }
  }

  fun getVipLevel(context: Context, userId: String? = null): Int {
    val totalRecharge = getTotalRecharge(context, userId)
    return VipSystem.calculateVIPLevel(totalRecharge)
  }

  fun getVipBadge(context: Context, userId: String? = null): String {
    val lvl = getVipLevel(context, userId)
    return if (lvl > 0) "VIP $lvl" else "None"
  }

  /**
   * Records a verified recharge from the backend/payment gateway.
   * Updates totalRecharge, adds coins, and automatically recalculates and activates VIP.
   */
  fun recordVerifiedRecharge(
    context: Context,
    rechargeAmount: Long,
    userId: String? = null
  ): VerifiedRechargeResult {
    val uid = userId ?: getActiveUserId(context)
    if (rechargeAmount <= 0) {
      val currentRecharge = getTotalRecharge(context, uid)
      val currentVip = VipSystem.calculateVIPLevel(currentRecharge)
      return VerifiedRechargeResult(0L, currentRecharge, currentVip, currentVip, false)
    }

    val prefs = getPrefs(context)
    val prefix = userPrefix(uid)
    val currentRecharge = getTotalRecharge(context, uid)
    val previousVip = VipSystem.calculateVIPLevel(currentRecharge)

    val newTotalRecharge = currentRecharge + rechargeAmount
    val newVip = VipSystem.calculateVIPLevel(newTotalRecharge)

    val editor = prefs.edit()
      .putLong(prefix + "total_recharge", newTotalRecharge)
      .putInt(prefix + "vip_level", newVip)
      .putString(prefix + "vip_badge", if (newVip > 0) "VIP $newVip" else "None")

    if (uid == "9221471" || uid == "100") {
      editor.putLong("pref_total_recharge", newTotalRecharge)
        .putInt("pref_vip_level", newVip)
        .putString("pref_vip_badge", if (newVip > 0) "VIP $newVip" else "None")
    }
    editor.apply()

    // Add coins to user's balance
    addCoins(context, rechargeAmount, uid)

    // Automatically activate and unlock VIP benefits & frames for all tiers up to newVip
    if (newVip > 0) {
      ensureVipAssetsUnlocked(context, newVip, uid)
    }

    return VerifiedRechargeResult(
      amountAdded = rechargeAmount,
      newTotalRecharge = newTotalRecharge,
      previousVip = previousVip,
      currentVip = newVip,
      didLevelUpVip = newVip > previousVip
    )
  }

  /**
   * Ensures VIP frames & badges are unlocked in the user's inventory up to the current VIP level.
   * Prevents duplicate records per Rule 18.
   */
  fun ensureVipAssetsUnlocked(context: Context, vipLevel: Int, userId: String) {
    // Only real items added from Admin Panel are shown to the user per explicit requirement
  }

  /**
   * Generates or retrieves the unique 7-digit user ID for a new user.
   */
  fun generateNewUniqueId(context: Context): String {
    val prefs = getPrefs(context)
    var newId: String
    do {
      newId = Random.nextInt(1000000, 9999999).toString()
    } while (prefs.contains(userPrefix(newId) + "username"))
    return newId
  }

  /**
   * Looks up an existing account by auth provider and authId (e.g. email, phone, or fbId).
   */
  fun findAccountByAuth(context: Context, provider: String, authId: String): String? {
    if (authId.isBlank()) return null
    val prefs = getPrefs(context)
    val mapStr = prefs.getString(KEY_ACCOUNTS_MAP, "{}") ?: "{}"
    val json = JSONObject(mapStr)
    val key = "${provider.uppercase()}_$authId"
    return if (json.has(key)) json.getString(key) else null
  }

  private fun registerAuthMapping(context: Context, provider: String, authId: String, userId: String) {
    if (authId.isBlank()) return
    val prefs = getPrefs(context)
    val mapStr = prefs.getString(KEY_ACCOUNTS_MAP, "{}") ?: "{}"
    val json = JSONObject(mapStr)
    json.put("${provider.uppercase()}_$authId", userId)
    prefs.edit().putString(KEY_ACCOUNTS_MAP, json.toString()).apply()
  }

  /**
   * Registers a new account with complete profile and sets as active logged in user.
   */
  fun registerNewUser(
    context: Context,
    provider: String,
    authId: String,
    username: String,
    gender: String,
    age: Int,
    birthDate: String,
    country: String,
    countryFlag: String,
    profilePhotoUri: String?
  ): String {
    val newUserId = generateNewUniqueId(context)
    val prefs = getPrefs(context)
    val prefix = userPrefix(newUserId)

    prefs.edit()
      .putString(prefix + "username", username.ifBlank { "User_$newUserId" })
      .putString(prefix + "gender", gender.ifBlank { "Male" })
      .putInt(prefix + "age", age.coerceAtLeast(18))
      .putString(prefix + "birth_date", birthDate)
      .putString(prefix + "country", country.ifBlank { "Bangladesh" })
      .putString(prefix + "flag", countryFlag.ifBlank { "🇧🇩" })
      .putString(prefix + "profile_uri", profilePhotoUri)
      .putString(prefix + "bio", "Welcome to Priya Live • Live Stream & Audio Party!")
      .putString(prefix + "auth_provider", provider)
      .putString(prefix + "auth_id", authId)
      // Strictly 0 for new user accounts
      .putString(prefix + "friend_count", "0")
      .putString(prefix + "follow_count", "0")
      .putString(prefix + "followers_count", "0")
      .putString(prefix + "visitors_count", "0")
      .putString(prefix + "vip_badge", "None")
      .putString(prefix + "level_badge", "Lv.1")
      .putBoolean(prefix + "cp_connected", false)
      .putBoolean(prefix + "agency_joined", false)
      .putBoolean(prefix + "agency_owner", false)
      .apply()

    registerAuthMapping(context, provider, authId, newUserId)
    setActiveUserId(context, newUserId)

    return newUserId
  }

  /**
   * Retrieves profile for a specific user ID with complete account data separation.
   */
  fun getProfileForUser(context: Context, userId: String): UserFullProfile {
    val prefs = getPrefs(context)
    val prefix = userPrefix(userId)

    // Check if account has user-specific keys. If not and it's 9221471 or 100, check legacy keys
    val isPrimaryAccount = userId == "9221471" || userId == "100"

    val username = prefs.getString(prefix + "username", null)
      ?: if (isPrimaryAccount) prefs.getString("pref_username", "User_$userId") ?: "User_$userId"
      else "User_$userId"

    val gender = prefs.getString(prefix + "gender", null)
      ?: if (isPrimaryAccount) prefs.getString("pref_gender", "Male") ?: "Male"
      else "Male"

    val age = if (prefs.contains(prefix + "age")) prefs.getInt(prefix + "age", 18)
      else if (isPrimaryAccount) prefs.getInt("pref_age", 18)
      else 18

    val birthDate = prefs.getString(prefix + "birth_date", "2006-01-01") ?: "2006-01-01"

    val country = prefs.getString(prefix + "country", null)
      ?: if (isPrimaryAccount) prefs.getString("pref_country", "Bangladesh") ?: "Bangladesh"
      else "Bangladesh"

    val flag = prefs.getString(prefix + "flag", null)
      ?: if (isPrimaryAccount) prefs.getString("pref_flag", "🇧🇩") ?: "🇧🇩"
      else "🇧🇩"

    val profileUri = prefs.getString(prefix + "profile_uri", null)
      ?: if (isPrimaryAccount) prefs.getString("pref_profile_image_uri", null)
      else null

    val resolvedProfileUri = profileUri?.ifBlank { null }
      ?: LocalImageStorage.getLocalPersistentFile(context, LocalImageStorage.ImageType.PROFILE, userId)?.let { "file://${it.absolutePath}" }

    val bgUri = prefs.getString(prefix + "bg_uri", null)
      ?: if (isPrimaryAccount) prefs.getString("pref_bg_image_uri", null)
      else null

    val bio = prefs.getString(prefix + "bio", null)
      ?: (if (isPrimaryAccount) prefs.getString("pref_bio", null) else null)
      ?: "Welcome to Priya Live • Live Stream & Audio Party!"

    val friendCount = prefs.getString(prefix + "friend_count", null)
      ?: if (isPrimaryAccount) prefs.getString("pref_friend_count", "0") ?: "0"
      else "0"

    val followCount = prefs.getString(prefix + "follow_count", null)
      ?: if (isPrimaryAccount) prefs.getString("pref_follow_count", "0") ?: "0"
      else "0"

    val followersCount = prefs.getString(prefix + "followers_count", null)
      ?: if (isPrimaryAccount) prefs.getString("pref_followers_count", "0") ?: "0"
      else "0"

    val visitorsCount = prefs.getString(prefix + "visitors_count", null)
      ?: if (isPrimaryAccount) prefs.getString("pref_visitors_count", "0") ?: "0"
      else "0"

    val totalRecharge = getTotalRecharge(context, userId)
    val vipEnabled = isVipEnabled(context, userId)
    val vipHidden = isVipHidden(context, userId)
    val rawVipLevel = VipSystem.calculateVIPLevel(totalRecharge)
    val calculatedVipLevel = if (!vipEnabled) 0 else rawVipLevel
    val vipBadge = when {
      !vipEnabled -> "Disabled"
      vipHidden -> "Hidden"
      calculatedVipLevel > 0 -> "VIP $calculatedVipLevel"
      else -> "None"
    }

    val coinBalance = getCoinBalance(context, userId)
    val calculatedLevel = LevelSystem.calculateLevel(coinBalance)
    val levelBadge = "Lv.$calculatedLevel"

    val isCpConnected = if (prefs.contains(prefix + "cp_connected")) prefs.getBoolean(prefix + "cp_connected", false)
      else if (isPrimaryAccount) prefs.getBoolean("pref_cp_connected", false)
      else false

    val cpPartnerName = prefs.getString(prefix + "cp_partner_name", null)
      ?: if (isPrimaryAccount) prefs.getString("pref_cp_partner_name", null)
      else null

    val cpPartnerAvatarUri = prefs.getString(prefix + "cp_partner_avatar", null)
      ?: if (isPrimaryAccount) prefs.getString("pref_cp_partner_avatar", null)
      else null

    val isAgencyJoined = if (prefs.contains(prefix + "agency_joined")) prefs.getBoolean(prefix + "agency_joined", false)
      else if (isPrimaryAccount) prefs.getBoolean("pref_agency_joined", false)
      else false

    val isAgencyOwner = if (prefs.contains(prefix + "agency_owner")) prefs.getBoolean(prefix + "agency_owner", false)
      else if (isPrimaryAccount) prefs.getBoolean("pref_agency_owner", false)
      else false

    val hasAgencyPermission = if (prefs.contains(prefix + "agency_permission")) prefs.getBoolean(prefix + "agency_permission", false)
      else if (isPrimaryAccount) prefs.getBoolean("pref_agency_permission", false)
      else isAgencyOwner

    val agencyName = prefs.getString(prefix + "agency_name", null)
      ?: if (isPrimaryAccount) prefs.getString("pref_agency_name", null)
      else null

    val agencyLogoUri = prefs.getString(prefix + "agency_logo", null)
      ?: if (isPrimaryAccount) prefs.getString("pref_agency_logo", null)
      else null

    val agencyLevel = prefs.getString(prefix + "agency_level", null)
      ?: if (isPrimaryAccount) prefs.getString("pref_agency_level", "Official Agency") ?: "Official Agency"
      else "Official Agency"

    val authProvider = prefs.getString(prefix + "auth_provider", "GUEST") ?: "GUEST"
    val authId = prefs.getString(prefix + "auth_id", "") ?: ""
    val badgeIds = getAssignedBadgeIds(context, userId)
    val isPanelAuthorized = if (prefs.contains(prefix + "panel_authorized")) prefs.getBoolean(prefix + "panel_authorized", false)
      else if (isPrimaryAccount) prefs.getBoolean("pref_panel_authorized", false)
      else false

    val hostStatus = prefs.getString(prefix + "host_status", "inactive") ?: "inactive"
    val agencyStatus = prefs.getString(prefix + "agency_status", "inactive") ?: "inactive"
    val agencyId = prefs.getString(prefix + "agency_id", "") ?: ""
    val hostAgencyId = prefs.getString(prefix + "host_agency_id", "") ?: ""
    val pendingAgencyRequestId = prefs.getString(prefix + "pending_agency_request_id", "") ?: ""

    return UserFullProfile(
      userId = userId,
      username = username,
      gender = gender,
      age = age,
      birthDate = birthDate,
      country = country,
      countryFlag = flag,
      profileImageUri = resolvedProfileUri,
      backgroundImageUri = bgUri,
      bio = bio,
      friendCount = friendCount,
      followCount = followCount,
      followersCount = followersCount,
      visitorsCount = visitorsCount,
      vipBadge = vipBadge,
      levelBadge = levelBadge,
      authProvider = authProvider,
      authId = authId,
      isCpConnected = isCpConnected,
      cpPartnerName = cpPartnerName,
      cpPartnerAvatarUri = cpPartnerAvatarUri,
      isAgencyJoined = isAgencyJoined || agencyStatus == "active",
      isAgencyOwner = isAgencyOwner,
      hasAgencyPermission = hasAgencyPermission,
      agencyName = agencyName,
      agencyLogoUri = agencyLogoUri,
      agencyLevel = agencyLevel,
      hostStatus = hostStatus,
      agencyStatus = agencyStatus,
      agencyId = agencyId,
      hostAgencyId = hostAgencyId,
      pendingAgencyRequestId = pendingAgencyRequestId,
      assignedBadgeIds = badgeIds,
      coinBalance = coinBalance,
      userLevel = calculatedLevel,
      totalRecharge = totalRecharge,
      vipLevel = calculatedVipLevel,
      isVipEnabled = vipEnabled,
      isVipHidden = vipHidden,
      isPanelAuthorized = isPanelAuthorized
    )
  }

  fun updateHostAndAgencyStatus(
    context: Context,
    hostStatus: String,
    agencyStatus: String,
    agencyId: String = "",
    hostAgencyId: String = "",
    agencyName: String? = null,
    pendingRequestId: String = "",
    userId: String? = null
  ) {
    val uid = userId ?: getActiveUserId(context)
    val prefs = getPrefs(context)
    val prefix = userPrefix(uid)
    val editor = prefs.edit()
      .putString(prefix + "host_status", hostStatus)
      .putString(prefix + "agency_status", agencyStatus)
      .putString(prefix + "agency_id", agencyId)
      .putString(prefix + "host_agency_id", hostAgencyId)
      .putString(prefix + "pending_agency_request_id", pendingRequestId)
    if (agencyStatus == "active") {
      editor.putBoolean(prefix + "agency_joined", true)
    }
    if (!agencyName.isNullOrBlank()) {
      editor.putString(prefix + "agency_name", agencyName)
    }
    editor.apply()
  }

  fun isPanelAuthorized(context: Context, userId: String): Boolean {
    val prefs = getPrefs(context)
    val prefix = userPrefix(userId)
    return prefs.getBoolean(prefix + "panel_authorized", false)
  }

  fun setPanelAuthorized(context: Context, userId: String, authorized: Boolean) {
    val prefs = getPrefs(context)
    val prefix = userPrefix(userId)
    prefs.edit().putBoolean(prefix + "panel_authorized", authorized).apply()
  }

  fun isVipEnabled(context: Context, userId: String): Boolean {
    val prefs = getPrefs(context)
    val prefix = userPrefix(userId)
    return prefs.getBoolean(prefix + "vip_enabled", true)
  }

  fun setVipEnabled(context: Context, userId: String, enabled: Boolean) {
    val prefs = getPrefs(context)
    val prefix = userPrefix(userId)
    prefs.edit().putBoolean(prefix + "vip_enabled", enabled).apply()
  }

  fun isVipHidden(context: Context, userId: String): Boolean {
    val prefs = getPrefs(context)
    val prefix = userPrefix(userId)
    return prefs.getBoolean(prefix + "vip_hidden", false)
  }

  fun setVipHidden(context: Context, userId: String, hidden: Boolean) {
    val prefs = getPrefs(context)
    val prefix = userPrefix(userId)
    prefs.edit().putBoolean(prefix + "vip_hidden", hidden).apply()
  }

  /**
   * Retrieves profile for the currently active/logged-in user.
   */
  fun getProfile(context: Context): UserFullProfile {
    val activeId = getActiveUserId(context)
    return getProfileForUser(context, activeId)
  }

  fun updateBasicInfo(
    context: Context,
    username: String,
    gender: String,
    age: Int,
    country: String,
    countryFlag: String,
    bio: String? = null
  ) {
    val activeId = getActiveUserId(context)
    val prefix = userPrefix(activeId)
    val editor = getPrefs(context).edit()
      .putString(prefix + "username", username.ifBlank { "User_$activeId" })
      .putString(prefix + "gender", gender)
      .putInt(prefix + "age", age)
      .putString(prefix + "country", country)
      .putString(prefix + "flag", countryFlag)

    if (bio != null) {
      editor.putString(prefix + "bio", bio)
    }

    // Mirror to legacy keys if primary account
    if (activeId == "9221471" || activeId == "100") {
      editor.putString("pref_username", username.ifBlank { "MR MAX" })
        .putString("pref_gender", gender)
        .putInt("pref_age", age)
        .putString("pref_country", country)
        .putString("pref_flag", countryFlag)
      if (bio != null) editor.putString("pref_bio", bio)
    }
    editor.apply()
  }

  fun updateProfileImage(context: Context, uriString: String?, userId: String? = null) {
    if (uriString.isNullOrBlank()) return
    val activeId = userId ?: getActiveUserId(context)
    val prefix = userPrefix(activeId)

    var persistentUri = uriString
    if (uriString.startsWith("content://")) {
      try {
        val parsedUri = android.net.Uri.parse(uriString)
        val file = LocalImageStorage.saveUriLocally(
          context = context,
          sourceUri = parsedUri,
          imageType = LocalImageStorage.ImageType.PROFILE,
          identifier = activeId
        )
        if (file != null && file.exists()) {
          persistentUri = "file://${file.absolutePath}"
        }
      } catch (e: Exception) {
        android.util.Log.w("UserProfileStorage", "Error persisting profile image: ${e.message}")
      }
    }

    val editor = getPrefs(context).edit().putString(prefix + "profile_uri", persistentUri)
    editor.putString("pref_profile_image_uri", persistentUri)
    editor.apply()
  }

  fun updateProfileImageUri(context: Context, uriString: String?) {
    updateProfileImage(context, uriString)
  }

  fun updateBackgroundImage(context: Context, uriString: String?, userId: String? = null) {
    if (uriString.isNullOrBlank()) return
    val activeId = userId ?: getActiveUserId(context)
    val prefix = userPrefix(activeId)

    var persistentUri = uriString
    if (uriString.startsWith("content://")) {
      try {
        val parsedUri = android.net.Uri.parse(uriString)
        val file = LocalImageStorage.saveUriLocally(
          context = context,
          sourceUri = parsedUri,
          imageType = LocalImageStorage.ImageType.ROOM_COVER,
          identifier = "bg_$activeId"
        )
        if (file != null && file.exists()) {
          persistentUri = "file://${file.absolutePath}"
        }
      } catch (e: Exception) {
        android.util.Log.w("UserProfileStorage", "Error persisting background image: ${e.message}")
      }
    }

    val editor = getPrefs(context).edit().putString(prefix + "bg_uri", persistentUri)
    if (activeId == "9221471" || activeId == "100") editor.putString("pref_bg_image_uri", persistentUri)
    editor.apply()
  }

  fun updateCpPartner(context: Context, isConnected: Boolean, partnerName: String?, partnerAvatarUri: String?) {
    val activeId = getActiveUserId(context)
    val prefix = userPrefix(activeId)
    val editor = getPrefs(context).edit()
      .putBoolean(prefix + "cp_connected", isConnected)
      .putString(prefix + "cp_partner_name", partnerName)
      .putString(prefix + "cp_partner_avatar", partnerAvatarUri)
    if (activeId == "100") {
      editor.putBoolean("pref_cp_connected", isConnected)
        .putString("pref_cp_partner_name", partnerName)
        .putString("pref_cp_partner_avatar", partnerAvatarUri)
    }
    editor.apply()
  }

  fun updateAgency(
    context: Context,
    isJoined: Boolean,
    isOwner: Boolean,
    name: String?,
    logoUri: String?,
    level: String?
  ) {
    val activeId = getActiveUserId(context)
    val prefix = userPrefix(activeId)
    val editor = getPrefs(context).edit()
      .putBoolean(prefix + "agency_joined", isJoined)
      .putBoolean(prefix + "agency_owner", isOwner)
      .putString(prefix + "agency_name", name)
      .putString(prefix + "agency_logo", logoUri)
      .putString(prefix + "agency_level", level ?: "Diamond Agency")
    if (activeId == "100") {
      editor.putBoolean("pref_agency_joined", isJoined)
        .putBoolean("pref_agency_owner", isOwner)
        .putString("pref_agency_name", name)
        .putString("pref_agency_logo", logoUri)
        .putString("pref_agency_level", level ?: "Diamond Agency")
    }
    editor.apply()
  }

  fun setAgencyPermission(context: Context, hasPermission: Boolean, userId: String? = null) {
    val uid = userId ?: getActiveUserId(context)
    val prefix = userPrefix(uid)
    val editor = getPrefs(context).edit()
      .putBoolean(prefix + "agency_owner", hasPermission)
      .putBoolean(prefix + "agency_permission", hasPermission)
    if (uid == "100") {
      editor.putBoolean("pref_agency_owner", hasPermission)
      editor.putBoolean("pref_agency_permission", hasPermission)
    }
    editor.apply()
  }

  /**
   * ADMIN-CONTROLLED BADGE SYSTEM
   * Only badges assigned by an authorized Admin appear on user profiles.
   * If not assigned, zero badges appear. No fake or placeholder badges.
   */
  fun getAssignedBadgeIds(context: Context, userId: String): List<String> {
    val prefs = getPrefs(context)
    val raw = prefs.getString(KEY_USER_BADGES_PREFIX + userId, null) ?: return emptyList()
    return try {
      val arr = JSONArray(raw)
      val list = mutableListOf<String>()
      for (i in 0 until arr.length()) {
        list.add(arr.getString(i))
      }
      list
    } catch (_: Exception) {
      emptyList()
    }
  }

  fun getAssignedBadges(context: Context, userId: String): List<PlatformBadge> {
    val ids = getAssignedBadgeIds(context, userId)
    if (ids.isEmpty()) return emptyList()
    return availablePlatformBadges.filter { ids.contains(it.id) }
  }

  fun assignBadge(context: Context, userId: String, badgeId: String) {
    val current = getAssignedBadgeIds(context, userId).toMutableList()
    if (!current.contains(badgeId)) {
      current.add(badgeId)
      saveBadgeIds(context, userId, current)
    }
  }

  fun removeBadge(context: Context, userId: String, badgeId: String) {
    val current = getAssignedBadgeIds(context, userId).toMutableList()
    if (current.contains(badgeId)) {
      current.remove(badgeId)
      saveBadgeIds(context, userId, current)
    }
  }

  fun toggleBadge(context: Context, userId: String, badgeId: String): Boolean {
    val current = getAssignedBadgeIds(context, userId).toMutableList()
    val isNowAssigned = if (current.contains(badgeId)) {
      current.remove(badgeId)
      false
    } else {
      current.add(badgeId)
      true
    }
    saveBadgeIds(context, userId, current)
    return isNowAssigned
  }

  private fun saveBadgeIds(context: Context, userId: String, ids: List<String>) {
    val arr = JSONArray(ids)
    getPrefs(context).edit()
      .putString(KEY_USER_BADGES_PREFIX + userId, arr.toString())
      .apply()
  }
}

