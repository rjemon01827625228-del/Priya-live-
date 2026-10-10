package com.example.ui.storage

import android.content.Context
import android.content.SharedPreferences
import com.example.R
import org.json.JSONArray
import org.json.JSONObject

/**
 * Model representing a user in the social/party ecosystem.
 */
data class SocialUser(
  val id: String,
  val username: String,
  val avatarRes: Int,
  val avatarUri: String? = null,
  val country: String = "Bangladesh",
  val flag: String = "🇧🇩",
  val gender: String = "Female",
  val age: Int = 19,
  val level: String = "Lv.7",
  val vipStatus: String = "VIP 1",
  val isOnline: Boolean = true,
  val isHost: Boolean = false,
  val agencyId: String? = null
)

/**
 * Model representing an Agency Host.
 */
data class AgencyHost(
  val hostId: String,
  val name: String,
  val avatarRes: Int,
  val avatarUri: String? = null,
  val country: String = "Bangladesh",
  val flag: String = "🇧🇩",
  val isOnline: Boolean = true,
  val status: String = "Host",
  val joinDate: String = "2026-09"
)

/**
 * Model representing an Agency Host Request / Pending Invitation.
 */
data class HostRequestItem(
  val requestId: String,
  val userId: String,
  val username: String,
  val avatarRes: Int,
  val avatarUri: String? = null,
  val country: String = "Bangladesh",
  val flag: String = "🇧🇩",
  val message: String = "Wants to join your Agency"
)

/**
 * Model representing an Agency Invitation received in User's Inbox.
 */
data class AgencyInviteMessage(
  val inviteId: String,
  val agencyId: String,
  val agencyName: String,
  val ownerName: String,
  val targetUserId: String,
  val timestamp: String = "Just now",
  val status: String = "PENDING" // PENDING, ACCEPTED, REJECTED
)

data class LocalAgency(
  val agencyId: String = "",
  val agencyName: String = "",
  val ownerUid: String = "",
  val ownerPid: String = "",
  val ownerName: String = "",
  val country: String = "Bangladesh",
  val countryCode: String = "BD",
  val countryFlag: String = "🇧🇩",
  val logoUrl: String? = null,
  val hostCount: Int = 0
)

data class LocalHostCycleProgress(
  val userPid: String = "",
  val completedDays: Int = 0,
  val pendingRewardCoins: Long = 0L
)

data class LocalHostDailyProgress(
  val dayNumber: Int = 1,
  val dateStamp: String = "",
  val eligibleSeconds: Long = 0L,
  val isCompleted: Boolean = false
)

data class LocalHostRequest(
  val requestId: String = "",
  val userPid: String = "",
  val userName: String = "",
  val userProfilePicture: String? = null,
  val userCountry: String = "Bangladesh",
  val agencyId: String = "",
  val agencyName: String = "",
  val status: String = "PENDING"
)

/**
 * Storage and manager for Follow/Followers, Multi-Account User Isolation, and Agency Host system.
 */
object SocialAgencyStorage {
  private const val PREFS_NAME = "priya_social_agency_prefs"
  private const val KEY_ACTIVE_USER_ID = "active_user_id"
  private const val KEY_FOLLOWING_PREFIX = "following_"
  private const val KEY_FOLLOWERS_PREFIX = "followers_"
  private const val KEY_BLOCKED_PREFIX = "blocked_"
  private const val KEY_HOSTS_PREFIX = "agency_hosts_"
  private const val KEY_REQUESTS_PREFIX = "agency_requests_"
  private const val KEY_INVITES = "agency_invites_json"

  // Pre-configured public community users for the ecosystem
  val communityUsers = listOf(
    SocialUser(
      id = "5487293",
      username = "Maya Queen 💜",
      avatarRes = R.drawable.img_streamer_girl,
      country = "Bangladesh",
      flag = "🇧🇩",
      gender = "Female",
      age = 20,
      level = "Lv.12",
      vipStatus = "VIP 3",
      isOnline = true,
      isHost = true
    ),
    SocialUser(
      id = "7129481",
      username = "Neon Diva ✨",
      avatarRes = R.drawable.img_profile_max,
      country = "Bangladesh",
      flag = "🇧🇩",
      gender = "Female",
      age = 21,
      level = "Lv.9",
      vipStatus = "VIP 2",
      isOnline = true,
      isHost = false
    ),
    SocialUser(
      id = "3819204",
      username = "Cyber Alex 🎧",
      avatarRes = R.drawable.img_profile_max,
      country = "India",
      flag = "🇮🇳",
      gender = "Male",
      age = 22,
      level = "Lv.15",
      vipStatus = "VIP 4",
      isOnline = true,
      isHost = true
    ),
    SocialUser(
      id = "9201482",
      username = "Priya Gold 👑",
      avatarRes = R.drawable.img_streamer_girl,
      country = "Bangladesh",
      flag = "🇧🇩",
      gender = "Female",
      age = 19,
      level = "Lv.8",
      vipStatus = "VIP 1",
      isOnline = false,
      isHost = false
    )
  )

  private fun getPrefs(context: Context): SharedPreferences {
    return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
  }

  // ---------------------------------------------------------------------------
  // ACTIVE USER & ACCOUNT SWITCHING (Data Isolation)
  // ---------------------------------------------------------------------------

  fun getActiveUserId(context: Context): String {
    val current = getPrefs(context).getString(KEY_ACTIVE_USER_ID, null)
    if (!current.isNullOrBlank()) return current
    // Default to main user ID from UserProfileStorage
    val mainId = UserProfileStorage.getOrGenerateUniqueId(context)
    setActiveUserId(context, mainId)
    return mainId
  }

  fun setActiveUserId(context: Context, userId: String) {
    getPrefs(context).edit().putString(KEY_ACTIVE_USER_ID, userId).apply()
  }

  // ---------------------------------------------------------------------------
  // FOLLOW / FOLLOWER SYSTEM
  // ---------------------------------------------------------------------------

  fun isFollowing(context: Context, followerId: String, targetUserId: String): Boolean {
    val prefs = getPrefs(context)
    val set = prefs.getStringSet(KEY_FOLLOWING_PREFIX + followerId, emptySet()) ?: emptySet()
    return set.contains(targetUserId)
  }

  fun followUser(context: Context, followerId: String, targetUserId: String): Boolean {
    if (followerId == targetUserId) return false
    val prefs = getPrefs(context)

    // Add target to follower's following list
    val following = prefs.getStringSet(KEY_FOLLOWING_PREFIX + followerId, emptySet())?.toMutableSet() ?: mutableSetOf()
    following.add(targetUserId)
    prefs.edit().putStringSet(KEY_FOLLOWING_PREFIX + followerId, following).apply()

    // Add follower to target's followers list
    val followers = prefs.getStringSet(KEY_FOLLOWERS_PREFIX + targetUserId, emptySet())?.toMutableSet() ?: mutableSetOf()
    followers.add(followerId)
    prefs.edit().putStringSet(KEY_FOLLOWERS_PREFIX + targetUserId, followers).apply()

    return true
  }

  fun unfollowUser(context: Context, followerId: String, targetUserId: String): Boolean {
    val prefs = getPrefs(context)

    // Remove target from follower's following list
    val following = prefs.getStringSet(KEY_FOLLOWING_PREFIX + followerId, emptySet())?.toMutableSet() ?: mutableSetOf()
    following.remove(targetUserId)
    prefs.edit().putStringSet(KEY_FOLLOWING_PREFIX + followerId, following).apply()

    // Remove follower from target's followers list
    val followers = prefs.getStringSet(KEY_FOLLOWERS_PREFIX + targetUserId, emptySet())?.toMutableSet() ?: mutableSetOf()
    followers.remove(followerId)
    prefs.edit().putStringSet(KEY_FOLLOWERS_PREFIX + targetUserId, followers).apply()

    return true
  }

  fun getFollowingList(context: Context, userId: String): List<String> {
    val prefs = getPrefs(context)
    return (prefs.getStringSet(KEY_FOLLOWING_PREFIX + userId, emptySet()) ?: emptySet()).toList()
  }

  fun getFollowersList(context: Context, userId: String): List<String> {
    val prefs = getPrefs(context)
    return (prefs.getStringSet(KEY_FOLLOWERS_PREFIX + userId, emptySet()) ?: emptySet()).toList()
  }

  fun getFollowingCount(context: Context, userId: String): Int {
    return getFollowingList(context, userId).size
  }

  fun getFollowersCount(context: Context, userId: String): Int {
    return getFollowersList(context, userId).size
  }

  // ---------------------------------------------------------------------------
  // BLOCK SYSTEM
  // ---------------------------------------------------------------------------

  fun isBlocked(context: Context, blockerId: String, targetUserId: String): Boolean {
    val prefs = getPrefs(context)
    val set = prefs.getStringSet(KEY_BLOCKED_PREFIX + blockerId, emptySet()) ?: emptySet()
    return set.contains(targetUserId)
  }

  fun blockUser(context: Context, blockerId: String, targetUserId: String): Boolean {
    if (blockerId == targetUserId) return false
    val prefs = getPrefs(context)
    val blocked = prefs.getStringSet(KEY_BLOCKED_PREFIX + blockerId, emptySet())?.toMutableSet() ?: mutableSetOf()
    blocked.add(targetUserId)
    prefs.edit().putStringSet(KEY_BLOCKED_PREFIX + blockerId, blocked).apply()
    // Automatically unfollow when blocking
    unfollowUser(context, blockerId, targetUserId)
    return true
  }

  fun unblockUser(context: Context, blockerId: String, targetUserId: String): Boolean {
    val prefs = getPrefs(context)
    val blocked = prefs.getStringSet(KEY_BLOCKED_PREFIX + blockerId, emptySet())?.toMutableSet() ?: mutableSetOf()
    blocked.remove(targetUserId)
    prefs.edit().putStringSet(KEY_BLOCKED_PREFIX + blockerId, blocked).apply()
    return true
  }

  fun getBlockedList(context: Context, userId: String): List<String> {
    val prefs = getPrefs(context)
    return (prefs.getStringSet(KEY_BLOCKED_PREFIX + userId, emptySet()) ?: emptySet()).toList()
  }

  // ---------------------------------------------------------------------------
  // AGENCY & HOST MANAGEMENT
  // ---------------------------------------------------------------------------

  private fun validateDrawableRes(context: Context, resId: Int, defaultRes: Int = R.drawable.img_streamer_girl): Int {
    if (resId <= 0) return defaultRes
    return try {
      context.resources.getResourceName(resId)
      resId
    } catch (e: Exception) {
      defaultRes
    }
  }

  fun getAgencyHosts(context: Context, agencyOwnerId: String): List<AgencyHost> {
    val prefs = getPrefs(context)
    val rawJson = prefs.getString(KEY_HOSTS_PREFIX + agencyOwnerId, null)
    if (rawJson.isNullOrBlank()) {
      return emptyList()
    }
    return try {
      val array = JSONArray(rawJson)
      val list = mutableListOf<AgencyHost>()
      for (i in 0 until array.length()) {
        val obj = array.getJSONObject(i)
        list.add(
          AgencyHost(
            hostId = obj.getString("hostId"),
            name = obj.getString("name"),
            avatarRes = validateDrawableRes(context, obj.optInt("avatarRes", R.drawable.img_streamer_girl)),
            avatarUri = obj.optString("avatarUri").takeIf { it.isNotBlank() },
            country = obj.optString("country", "Bangladesh"),
            flag = obj.optString("flag", "🇧🇩"),
            isOnline = obj.optBoolean("isOnline", true),
            status = obj.optString("status", "Host"),
            joinDate = obj.optString("joinDate", "2026-09")
          )
        )
      }
      list
    } catch (e: Exception) {
      emptyList()
    }
  }

  fun saveAgencyHosts(context: Context, agencyOwnerId: String, hosts: List<AgencyHost>) {
    val array = JSONArray()
    for (host in hosts) {
      val obj = JSONObject().apply {
        put("hostId", host.hostId)
        put("name", host.name)
        put("avatarRes", host.avatarRes)
        put("avatarUri", host.avatarUri ?: "")
        put("country", host.country)
        put("flag", host.flag)
        put("isOnline", host.isOnline)
        put("status", host.status)
        put("joinDate", host.joinDate)
      }
      array.put(obj)
    }
    getPrefs(context).edit().putString(KEY_HOSTS_PREFIX + agencyOwnerId, array.toString()).apply()
  }

  fun getAgencyHostRequests(context: Context, agencyOwnerId: String): List<HostRequestItem> {
    val prefs = getPrefs(context)
    val rawJson = prefs.getString(KEY_REQUESTS_PREFIX + agencyOwnerId, null)
    if (rawJson.isNullOrBlank()) {
      return emptyList()
    }
    return try {
      val array = JSONArray(rawJson)
      val list = mutableListOf<HostRequestItem>()
      for (i in 0 until array.length()) {
        val obj = array.getJSONObject(i)
        list.add(
          HostRequestItem(
            requestId = obj.getString("requestId"),
            userId = obj.getString("userId"),
            username = obj.getString("username"),
            avatarRes = validateDrawableRes(context, obj.optInt("avatarRes", R.drawable.img_streamer_girl)),
            avatarUri = obj.optString("avatarUri").takeIf { it.isNotBlank() },
            country = obj.optString("country", "Bangladesh"),
            flag = obj.optString("flag", "🇧🇩"),
            message = obj.optString("message", "Wants to join Agency")
          )
        )
      }
      list
    } catch (e: Exception) {
      emptyList()
    }
  }

  fun saveAgencyHostRequests(context: Context, agencyOwnerId: String, requests: List<HostRequestItem>) {
    val array = JSONArray()
    for (req in requests) {
      val obj = JSONObject().apply {
        put("requestId", req.requestId)
        put("userId", req.userId)
        put("username", req.username)
        put("avatarRes", req.avatarRes)
        put("avatarUri", req.avatarUri ?: "")
        put("country", req.country)
        put("flag", req.flag)
        put("message", req.message)
      }
      array.put(obj)
    }
    getPrefs(context).edit().putString(KEY_REQUESTS_PREFIX + agencyOwnerId, array.toString()).apply()
  }

  fun acceptHostRequest(context: Context, agencyOwnerId: String, requestId: String): Boolean {
    val requests = getAgencyHostRequests(context, agencyOwnerId).toMutableList()
    val match = requests.find { it.requestId == requestId } ?: return false
    requests.remove(match)
    saveAgencyHostRequests(context, agencyOwnerId, requests)

    // Add to hosts list
    val currentHosts = getAgencyHosts(context, agencyOwnerId).toMutableList()
    if (currentHosts.none { it.hostId == match.userId }) {
      currentHosts.add(
        AgencyHost(
          hostId = match.userId,
          name = match.username,
          avatarRes = match.avatarRes,
          avatarUri = match.avatarUri,
          country = match.country,
          flag = match.flag,
          isOnline = true,
          status = "Host",
          joinDate = "2026-09"
        )
      )
      saveAgencyHosts(context, agencyOwnerId, currentHosts)
    }
    return true
  }

  fun rejectHostRequest(context: Context, agencyOwnerId: String, requestId: String): Boolean {
    val requests = getAgencyHostRequests(context, agencyOwnerId).toMutableList()
    val match = requests.find { it.requestId == requestId } ?: return false
    requests.remove(match)
    saveAgencyHostRequests(context, agencyOwnerId, requests)
    return true
  }

  // ---------------------------------------------------------------------------
  // AGENCY INVITATION MESSAGES (Inbox)
  // ---------------------------------------------------------------------------

  fun getPendingInvitesForUser(context: Context, targetUserId: String): List<AgencyInviteMessage> {
    val prefs = getPrefs(context)
    val rawJson = prefs.getString(KEY_INVITES, null) ?: return emptyList()
    return try {
      val array = JSONArray(rawJson)
      val list = mutableListOf<AgencyInviteMessage>()
      for (i in 0 until array.length()) {
        val obj = array.getJSONObject(i)
        if (obj.getString("targetUserId") == targetUserId && obj.getString("status") == "PENDING") {
          list.add(
            AgencyInviteMessage(
              inviteId = obj.getString("inviteId"),
              agencyId = obj.getString("agencyId"),
              agencyName = obj.getString("agencyName"),
              ownerName = obj.getString("ownerName"),
              targetUserId = obj.getString("targetUserId"),
              timestamp = obj.optString("timestamp", "Just now"),
              status = obj.optString("status", "PENDING")
            )
          )
        }
      }
      list
    } catch (e: Exception) {
      emptyList()
    }
  }

  fun sendAgencyInvitation(
    context: Context,
    agencyId: String,
    agencyName: String,
    ownerName: String,
    targetUserId: String
  ): Boolean {
    val prefs = getPrefs(context)
    val rawJson = prefs.getString(KEY_INVITES, null)
    val array = if (rawJson.isNullOrBlank()) JSONArray() else JSONArray(rawJson)

    // Check if already invited
    for (i in 0 until array.length()) {
      val obj = array.getJSONObject(i)
      if (obj.getString("agencyId") == agencyId &&
        obj.getString("targetUserId") == targetUserId &&
        obj.getString("status") == "PENDING"
      ) {
        return false // Already pending
      }
    }

    val newInvite = JSONObject().apply {
      put("inviteId", "inv_" + System.currentTimeMillis())
      put("agencyId", agencyId)
      put("agencyName", agencyName)
      put("ownerName", ownerName)
      put("targetUserId", targetUserId)
      put("timestamp", "Just now")
      put("status", "PENDING")
    }
    array.put(newInvite)
    prefs.edit().putString(KEY_INVITES, array.toString()).apply()
    return true
  }

  fun respondToInvitation(
    context: Context,
    inviteId: String,
    accept: Boolean
  ): Boolean {
    val prefs = getPrefs(context)
    val rawJson = prefs.getString(KEY_INVITES, null) ?: return false
    val array = JSONArray(rawJson)
    var foundInvite: JSONObject? = null

    for (i in 0 until array.length()) {
      val obj = array.getJSONObject(i)
      if (obj.getString("inviteId") == inviteId) {
        obj.put("status", if (accept) "ACCEPTED" else "REJECTED")
        foundInvite = obj
        break
      }
    }
    prefs.edit().putString(KEY_INVITES, array.toString()).apply()

    if (accept && foundInvite != null) {
      val agencyOwnerId = foundInvite.getString("agencyId")
      val targetUserId = foundInvite.getString("targetUserId")

      // Find user details
      val user = communityUsers.find { it.id == targetUserId }
      val username = user?.username ?: "Host $targetUserId"
      val avatarRes = user?.avatarRes ?: R.drawable.img_streamer_girl

      val currentHosts = getAgencyHosts(context, agencyOwnerId).toMutableList()
      if (currentHosts.none { it.hostId == targetUserId }) {
        currentHosts.add(
          AgencyHost(
            hostId = targetUserId,
            name = username,
            avatarRes = avatarRes,
            country = "Bangladesh",
            flag = "🇧🇩",
            isOnline = true,
            status = "Host",
            joinDate = "2026-09"
          )
        )
        saveAgencyHosts(context, agencyOwnerId, currentHosts)
      }
    }
    return true
  }

  /**
   * Searches known users across the platform by ID or Name.
   */
  fun searchUser(query: String): SocialUser? {
    val clean = query.trim().lowercase()
    if (clean.isBlank()) return null
    return communityUsers.find {
      it.id.equals(clean, ignoreCase = true) || it.username.lowercase().contains(clean)
    }
  }

  // ---------------------------------------------------------------------------
  // LOCAL AGENCY REGISTRY & HOST CYCLE PROGRESS HELPERS
  // ---------------------------------------------------------------------------

  const val REQUIRED_DAILY_LIVE_SECONDS = 4980L // 1h 23m
  private const val KEY_LOCAL_AGENCIES = "local_agencies_json"
  private const val KEY_HOST_REQUESTS_MAP = "local_host_requests_json"
  private const val KEY_HOST_CYCLE_PREFIX = "local_host_cycle_"
  private const val KEY_HOST_DAILY_PREFIX = "local_host_daily_"

  fun resolveCountryCode(country: String): String {
    val c = country.trim().lowercase()
    return when {
      c.contains("bangladesh") || c == "bd" -> "BD"
      c.contains("india") || c == "in" -> "IN"
      c.contains("pakistan") || c == "pk" -> "PK"
      c.contains("saudi") || c == "sa" -> "SA"
      c.contains("united states") || c == "usa" || c == "us" -> "US"
      c.contains("united kingdom") || c == "uk" || c == "gb" -> "UK"
      c.contains("emirates") || c == "uae" || c == "ae" -> "AE"
      c.length >= 2 -> c.take(2).uppercase()
      else -> "BD"
    }
  }

  fun matchesCountryQuery(agency: LocalAgency, query: String): Boolean {
    val q = query.trim().lowercase()
    if (q.isBlank()) return true
    val agencyCode = agency.countryCode.ifBlank { resolveCountryCode(agency.country) }.lowercase()
    val queryCode = resolveCountryCode(query).lowercase()
    return agency.country.lowercase().contains(q) ||
      agencyCode == q ||
      agencyCode == queryCode ||
      agency.agencyId.lowercase().contains(q) ||
      agency.agencyName.lowercase().contains(q)
  }

  fun getAllLocalAgencies(context: Context): List<LocalAgency> {
    val raw = getPrefs(context).getString(KEY_LOCAL_AGENCIES, null) ?: return emptyList()
    return try {
      val arr = JSONArray(raw)
      val list = mutableListOf<LocalAgency>()
      for (i in 0 until arr.length()) {
        val obj = arr.getJSONObject(i)
        list.add(
          LocalAgency(
            agencyId = obj.optString("agencyId"),
            agencyName = obj.optString("agencyName"),
            ownerUid = obj.optString("ownerUid"),
            ownerPid = obj.optString("ownerPid"),
            ownerName = obj.optString("ownerName"),
            country = obj.optString("country", "Bangladesh"),
            countryCode = obj.optString("countryCode", "BD"),
            countryFlag = obj.optString("countryFlag", "🇧🇩"),
            logoUrl = obj.optString("logoUrl").takeIf { it.isNotBlank() },
            hostCount = obj.optInt("hostCount", 0)
          )
        )
      }
      list
    } catch (_: Exception) {
      emptyList()
    }
  }

  fun getAgenciesForCountry(context: Context, country: String): List<LocalAgency> {
    val all = getAllLocalAgencies(context)
    if (country.isBlank()) return all
    return all.filter { matchesCountryQuery(it, country) }
  }

  fun createOrUpdateAgency(
    context: Context,
    agencyId: String,
    agencyName: String,
    ownerUid: String,
    ownerPid: String,
    ownerName: String,
    country: String,
    countryFlag: String,
    logoUrl: String?
  ) {
    val current = getAllLocalAgencies(context).toMutableList()
    val hostCount = getAgencyHosts(context, ownerPid).size
    val updated = LocalAgency(
      agencyId = agencyId,
      agencyName = agencyName,
      ownerUid = ownerUid,
      ownerPid = ownerPid,
      ownerName = ownerName,
      country = country,
      countryCode = resolveCountryCode(country),
      countryFlag = countryFlag,
      logoUrl = logoUrl,
      hostCount = hostCount
    )
    val index = current.indexOfFirst { it.agencyId == agencyId }
    if (index >= 0) {
      current[index] = updated
    } else {
      current.add(updated)
    }
    val arr = JSONArray()
    for (a in current) {
      arr.put(
        JSONObject().apply {
          put("agencyId", a.agencyId)
          put("agencyName", a.agencyName)
          put("ownerUid", a.ownerUid)
          put("ownerPid", a.ownerPid)
          put("ownerName", a.ownerName)
          put("country", a.country)
          put("countryCode", a.countryCode)
          put("countryFlag", a.countryFlag)
          put("logoUrl", a.logoUrl ?: "")
          put("hostCount", a.hostCount)
        }
      )
    }
    getPrefs(context).edit().putString(KEY_LOCAL_AGENCIES, arr.toString()).apply()
  }

  fun sendHostJoinRequest(
    context: Context,
    agencyId: String,
    onResult: (Boolean, String) -> Unit
  ) {
    val profile = UserProfileStorage.getProfile(context)
    val agency = getAllLocalAgencies(context).find { it.agencyId == agencyId }
    val agencyName = agency?.agencyName ?: "Agency $agencyId"
    val reqId = "req_${profile.userId}_$agencyId"
    val req = LocalHostRequest(
      requestId = reqId,
      userPid = profile.userId,
      userName = profile.username,
      userProfilePicture = profile.profileImageUri,
      userCountry = profile.country,
      agencyId = agencyId,
      agencyName = agencyName,
      status = "PENDING"
    )

    val prefs = getPrefs(context)
    val rawMap = prefs.getString(KEY_HOST_REQUESTS_MAP, "{}") ?: "{}"
    val mapObj = try { JSONObject(rawMap) } catch (_: Exception) { JSONObject() }
    mapObj.put(
      reqId,
      JSONObject().apply {
        put("requestId", req.requestId)
        put("userPid", req.userPid)
        put("userName", req.userName)
        put("userProfilePicture", req.userProfilePicture ?: "")
        put("userCountry", req.userCountry)
        put("agencyId", req.agencyId)
        put("agencyName", req.agencyName)
        put("status", req.status)
      }
    )
    prefs.edit().putString(KEY_HOST_REQUESTS_MAP, mapObj.toString()).apply()

    // Also add to agency owner's local host requests
    val ownerReqs = getAgencyHostRequests(context, agencyId).toMutableList()
    if (ownerReqs.none { it.userId == profile.userId }) {
      ownerReqs.add(
        HostRequestItem(
          requestId = reqId,
          userId = profile.userId,
          username = profile.username,
          avatarRes = R.drawable.img_streamer_girl,
          avatarUri = profile.profileImageUri,
          country = profile.country,
          flag = profile.countryFlag,
          message = "Wants to join your Agency"
        )
      )
      saveAgencyHostRequests(context, agencyId, ownerReqs)
    }

    UserProfileStorage.updateHostAndAgencyStatus(
      context = context,
      hostStatus = "pending",
      agencyStatus = "pending",
      agencyId = agencyId,
      hostAgencyId = agencyId,
      agencyName = agencyName,
      pendingRequestId = reqId,
      userId = profile.userId
    )
    onResult(true, "Request sent to $agencyName!")
  }

  fun getHostRequest(context: Context, requestId: String): LocalHostRequest? {
    if (requestId.isBlank()) return null
    val rawMap = getPrefs(context).getString(KEY_HOST_REQUESTS_MAP, "{}") ?: "{}"
    return try {
      val mapObj = JSONObject(rawMap)
      if (!mapObj.has(requestId)) return null
      val obj = mapObj.getJSONObject(requestId)
      LocalHostRequest(
        requestId = obj.optString("requestId", requestId),
        userPid = obj.optString("userPid"),
        userName = obj.optString("userName"),
        userProfilePicture = obj.optString("userProfilePicture").takeIf { it.isNotBlank() },
        userCountry = obj.optString("userCountry", "Bangladesh"),
        agencyId = obj.optString("agencyId"),
        agencyName = obj.optString("agencyName"),
        status = obj.optString("status", "PENDING")
      )
    } catch (_: Exception) {
      null
    }
  }

  fun getHostCycleProgress(context: Context, userPid: String): LocalHostCycleProgress {
    val raw = getPrefs(context).getString(KEY_HOST_CYCLE_PREFIX + userPid, null)
      ?: return LocalHostCycleProgress(userPid = userPid)
    return try {
      val obj = JSONObject(raw)
      LocalHostCycleProgress(
        userPid = userPid,
        completedDays = obj.optInt("completedDays", 0),
        pendingRewardCoins = obj.optLong("pendingRewardCoins", 0L)
      )
    } catch (_: Exception) {
      LocalHostCycleProgress(userPid = userPid)
    }
  }

  fun getHostDailyProgressList(context: Context, userPid: String): List<LocalHostDailyProgress> {
    val raw = getPrefs(context).getString(KEY_HOST_DAILY_PREFIX + userPid, null)
    if (raw.isNullOrBlank()) {
      return (1..10).map { day -> LocalHostDailyProgress(dayNumber = day) }
    }
    return try {
      val arr = JSONArray(raw)
      val list = mutableListOf<LocalHostDailyProgress>()
      for (i in 0 until arr.length()) {
        val obj = arr.getJSONObject(i)
        list.add(
          LocalHostDailyProgress(
            dayNumber = obj.optInt("dayNumber", i + 1),
            dateStamp = obj.optString("dateStamp"),
            eligibleSeconds = obj.optLong("eligibleSeconds", 0L),
            isCompleted = obj.optBoolean("isCompleted", false)
          )
        )
      }
      if (list.isEmpty()) (1..10).map { day -> LocalHostDailyProgress(dayNumber = day) } else list
    } catch (_: Exception) {
      (1..10).map { day -> LocalHostDailyProgress(dayNumber = day) }
    }
  }

  fun claimHostReward(
    context: Context,
    userPid: String,
    onResult: (Boolean, Long, String) -> Unit
  ) {
    val cycle = getHostCycleProgress(context, userPid)
    if (cycle.pendingRewardCoins <= 0L) {
      onResult(false, 0L, "No claimable rewards available yet.")
      return
    }
    val reward = cycle.pendingRewardCoins
    UserProfileStorage.addCoins(context, reward, userPid)
    val updatedObj = JSONObject().apply {
      put("completedDays", cycle.completedDays)
      put("pendingRewardCoins", 0L)
    }
    getPrefs(context).edit()
      .putString(KEY_HOST_CYCLE_PREFIX + userPid, updatedObj.toString())
      .apply()
    onResult(true, reward, "Claimed $reward Diamonds!")
  }
}
