package com.example.ui.storage

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

data class ViewerThumbnail(
  val userId: String,
  val username: String,
  val avatarUri: String?,
  val avatarRes: Int,
  val level: Int = 1
)

data class CallGuestInfo(
  val userId: String,
  val username: String,
  val avatarUri: String?,
  val avatarRes: Int,
  val isMuted: Boolean = false,
  val joinedTimestamp: Long = System.currentTimeMillis()
)

data class ActiveLiveRoom(
  val roomId: String,
  val hostUserId: String,
  val hostName: String,
  val hostAvatarUri: String?,
  val hostAvatarRes: Int,
  val roomTitle: String,
  val coverUri: String?,
  val countryCode: String,
  val countryName: String,
  val flagEmoji: String,
  val category: String, // "Explore", "New", "PK", "Music", "Chat", etc.
  val startTimeMs: Long,
  val viewerCount: Int = 1,
  val heartScore: Long = 0L,
  val adminUserIds: List<String> = emptyList(),
  val mutedUserIds: List<String> = emptyList(),
  val kickedUserIds: List<String> = emptyList(),
  val isLocked: Boolean = false,
  val activeCallGuest: CallGuestInfo? = null,
  val incomingCallRequester: ViewerThumbnail? = null,
  val viewers: List<ViewerThumbnail> = emptyList()
)

object LiveRoomStorage {
  private const val PREFS_NAME = "cyber_live_rooms_storage_v1"
  private const val KEY_ACTIVE_ROOMS_JSON = "key_active_live_rooms_json"
  private const val KEY_ESCAPED_SESSION_JSON = "key_escaped_live_session_json"

  private fun getPrefs(context: Context): SharedPreferences {
    return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
  }

  /**
   * Retrieves all active live rooms currently hosted.
   * Starts empty - no fake/mock users or rooms.
   */
  fun getActiveRooms(context: Context): List<ActiveLiveRoom> {
    val jsonStr = getPrefs(context).getString(KEY_ACTIVE_ROOMS_JSON, null) ?: return emptyList()
    return try {
      val arr = JSONArray(jsonStr)
      val list = mutableListOf<ActiveLiveRoom>()
      for (i in 0 until arr.length()) {
        val obj = arr.getJSONObject(i)
        list.add(parseRoom(obj))
      }
      list
    } catch (_: Exception) {
      emptyList()
    }
  }

  /**
   * Returns active rooms prioritized by the user's selected/current country first.
   * Empty if no active rooms exist. No fake rooms added.
   */
  fun getCountryPrioritizedRooms(context: Context, userCountryCode: String): List<ActiveLiveRoom> {
    val all = getActiveRooms(context)
    if (all.isEmpty()) return emptyList()

    val sameCountry = all.filter { it.countryCode.equals(userCountryCode, ignoreCase = true) }
    val otherCountries = all.filter { !it.countryCode.equals(userCountryCode, ignoreCase = true) }
    return sameCountry + otherCountries
  }

  /**
   * Finds a room by ID.
   */
  fun getRoomById(context: Context, roomId: String): ActiveLiveRoom? {
    return getActiveRooms(context).find { it.roomId == roomId }
  }

  /**
   * Creates or updates a live room for the host.
   */
  fun saveOrUpdateRoom(context: Context, room: ActiveLiveRoom) {
    val rooms = getActiveRooms(context).toMutableList()
    val index = rooms.indexOfFirst { it.roomId == room.roomId || it.hostUserId == room.hostUserId }
    if (index != -1) {
      rooms[index] = room
    } else {
      rooms.add(0, room)
    }
    saveRoomsList(context, rooms)
  }

  /**
   * Closes and removes a live room when the host ends the stream.
   */
  fun removeRoom(context: Context, roomId: String) {
    val rooms = getActiveRooms(context).toMutableList()
    rooms.removeAll { it.roomId == roomId }
    saveRoomsList(context, rooms)

    // Also clear escaped session if it was this room
    val escaped = getEscapedSession(context)
    if (escaped?.roomId == roomId) {
      clearEscapedSession(context)
    }
  }

  /**
   * Saves an active room state when the user selects ESCAPE.
   */
  fun saveEscapedSession(context: Context, room: ActiveLiveRoom) {
    val json = serializeRoom(room)
    getPrefs(context).edit().putString(KEY_ESCAPED_SESSION_JSON, json.toString()).apply()
  }

  /**
   * Gets the currently escaped live session, if any.
   */
  fun getEscapedSession(context: Context): ActiveLiveRoom? {
    val jsonStr = getPrefs(context).getString(KEY_ESCAPED_SESSION_JSON, null) ?: return null
    return try {
      parseRoom(JSONObject(jsonStr))
    } catch (_: Exception) {
      null
    }
  }

  /**
   * Clears the escaped session upon exit.
   */
  fun clearEscapedSession(context: Context) {
    getPrefs(context).edit().remove(KEY_ESCAPED_SESSION_JSON).apply()
  }

  private fun saveRoomsList(context: Context, rooms: List<ActiveLiveRoom>) {
    val arr = JSONArray()
    for (room in rooms) {
      arr.put(serializeRoom(room))
    }
    getPrefs(context).edit().putString(KEY_ACTIVE_ROOMS_JSON, arr.toString()).apply()
  }

  private fun serializeRoom(room: ActiveLiveRoom): JSONObject {
    return JSONObject().apply {
      put("roomId", room.roomId)
      put("hostUserId", room.hostUserId)
      put("hostName", room.hostName)
      put("hostAvatarUri", room.hostAvatarUri ?: JSONObject.NULL)
      put("hostAvatarRes", room.hostAvatarRes)
      put("roomTitle", room.roomTitle)
      put("coverUri", room.coverUri ?: JSONObject.NULL)
      put("countryCode", room.countryCode)
      put("countryName", room.countryName)
      put("flagEmoji", room.flagEmoji)
      put("category", room.category)
      put("startTimeMs", room.startTimeMs)
      put("viewerCount", room.viewerCount)
      put("heartScore", room.heartScore)
      put("isLocked", room.isLocked)

      val adminArr = JSONArray()
      room.adminUserIds.forEach { adminArr.put(it) }
      put("adminUserIds", adminArr)

      val mutedArr = JSONArray()
      room.mutedUserIds.forEach { mutedArr.put(it) }
      put("mutedUserIds", mutedArr)

      val kickedArr = JSONArray()
      room.kickedUserIds.forEach { kickedArr.put(it) }
      put("kickedUserIds", kickedArr)

      if (room.activeCallGuest != null) {
        val guestObj = JSONObject().apply {
          put("userId", room.activeCallGuest.userId)
          put("username", room.activeCallGuest.username)
          put("avatarUri", room.activeCallGuest.avatarUri ?: JSONObject.NULL)
          put("avatarRes", room.activeCallGuest.avatarRes)
          put("isMuted", room.activeCallGuest.isMuted)
          put("joinedTimestamp", room.activeCallGuest.joinedTimestamp)
        }
        put("activeCallGuest", guestObj)
      } else {
        put("activeCallGuest", JSONObject.NULL)
      }

      if (room.incomingCallRequester != null) {
        val reqObj = JSONObject().apply {
          put("userId", room.incomingCallRequester.userId)
          put("username", room.incomingCallRequester.username)
          put("avatarUri", room.incomingCallRequester.avatarUri ?: JSONObject.NULL)
          put("avatarRes", room.incomingCallRequester.avatarRes)
          put("level", room.incomingCallRequester.level)
        }
        put("incomingCallRequester", reqObj)
      } else {
        put("incomingCallRequester", JSONObject.NULL)
      }

      val viewersArr = JSONArray()
      room.viewers.forEach { viewer ->
        val vObj = JSONObject().apply {
          put("userId", viewer.userId)
          put("username", viewer.username)
          put("avatarUri", viewer.avatarUri ?: JSONObject.NULL)
          put("avatarRes", viewer.avatarRes)
          put("level", viewer.level)
        }
        viewersArr.put(vObj)
      }
      put("viewers", viewersArr)
    }
  }

  private fun parseRoom(obj: JSONObject): ActiveLiveRoom {
    val roomId = obj.getString("roomId")
    val hostUserId = obj.getString("hostUserId")
    val hostName = obj.getString("hostName")
    val hostAvatarUri = if (obj.isNull("hostAvatarUri")) null else obj.getString("hostAvatarUri")
    val hostAvatarRes = obj.optInt("hostAvatarRes", com.aistudio.neonparty.nxvqlr.R.drawable.img_profile_max)
    val roomTitle = obj.optString("roomTitle", "Live Stream")
    val coverUri = if (obj.isNull("coverUri")) null else obj.getString("coverUri")
    val countryCode = obj.optString("countryCode", "BD")
    val countryName = obj.optString("countryName", "Bangladesh")
    val flagEmoji = obj.optString("flagEmoji", "🇧🇩")
    val category = obj.optString("category", "Explore")
    val startTimeMs = obj.optLong("startTimeMs", System.currentTimeMillis())
    val viewerCount = obj.optInt("viewerCount", 1)
    val heartScore = obj.optLong("heartScore", 0L)
    val isLocked = obj.optBoolean("isLocked", false)

    val adminList = mutableListOf<String>()
    val adminArr = obj.optJSONArray("adminUserIds")
    if (adminArr != null) {
      for (i in 0 until adminArr.length()) {
        adminList.add(adminArr.getString(i))
      }
    }

    val mutedList = mutableListOf<String>()
    val mutedArr = obj.optJSONArray("mutedUserIds")
    if (mutedArr != null) {
      for (i in 0 until mutedArr.length()) {
        mutedList.add(mutedArr.getString(i))
      }
    }

    val kickedList = mutableListOf<String>()
    val kickedArr = obj.optJSONArray("kickedUserIds")
    if (kickedArr != null) {
      for (i in 0 until kickedArr.length()) {
        kickedList.add(kickedArr.getString(i))
      }
    }

    var activeCallGuest: CallGuestInfo? = null
    if (!obj.isNull("activeCallGuest")) {
      val gObj = obj.getJSONObject("activeCallGuest")
      activeCallGuest = CallGuestInfo(
        userId = gObj.getString("userId"),
        username = gObj.getString("username"),
        avatarUri = if (gObj.isNull("avatarUri")) null else gObj.getString("avatarUri"),
        avatarRes = gObj.optInt("avatarRes", com.aistudio.neonparty.nxvqlr.R.drawable.img_streamer_girl),
        isMuted = gObj.optBoolean("isMuted", false),
        joinedTimestamp = gObj.optLong("joinedTimestamp", System.currentTimeMillis())
      )
    }

    var incomingCallRequester: ViewerThumbnail? = null
    if (!obj.isNull("incomingCallRequester")) {
      val rObj = obj.getJSONObject("incomingCallRequester")
      incomingCallRequester = ViewerThumbnail(
        userId = rObj.getString("userId"),
        username = rObj.getString("username"),
        avatarUri = if (rObj.isNull("avatarUri")) null else rObj.getString("avatarUri"),
        avatarRes = rObj.optInt("avatarRes", com.aistudio.neonparty.nxvqlr.R.drawable.img_streamer_girl),
        level = rObj.optInt("level", 1)
      )
    }

    val viewersList = mutableListOf<ViewerThumbnail>()
    val vArr = obj.optJSONArray("viewers")
    if (vArr != null) {
      for (i in 0 until vArr.length()) {
        val vObj = vArr.getJSONObject(i)
        viewersList.add(
          ViewerThumbnail(
            userId = vObj.getString("userId"),
            username = vObj.getString("username"),
            avatarUri = if (vObj.isNull("avatarUri")) null else vObj.getString("avatarUri"),
            avatarRes = vObj.optInt("avatarRes", com.aistudio.neonparty.nxvqlr.R.drawable.img_streamer_girl),
            level = vObj.optInt("level", 1)
          )
        )
      }
    }

    return ActiveLiveRoom(
      roomId = roomId,
      hostUserId = hostUserId,
      hostName = hostName,
      hostAvatarUri = hostAvatarUri,
      hostAvatarRes = hostAvatarRes,
      roomTitle = roomTitle,
      coverUri = coverUri,
      countryCode = countryCode,
      countryName = countryName,
      flagEmoji = flagEmoji,
      category = category,
      startTimeMs = startTimeMs,
      viewerCount = viewerCount,
      heartScore = heartScore,
      adminUserIds = adminList,
      mutedUserIds = mutedList,
      kickedUserIds = kickedList,
      isLocked = isLocked,
      activeCallGuest = activeCallGuest,
      incomingCallRequester = incomingCallRequester,
      viewers = viewersList
    )
  }
}
