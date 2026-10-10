package com.example.ui.storage

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SavedAudioRoom(
  val title: String,
  val coverUriString: String?,
  val country: String,
  val flagEmoji: String,
  val id: String = "",
  val creatorUserId: String = "",
  val seatCount: Int = 8
)

object UserRoomStorage {
  private const val PREFS_NAME = "cyber_party_room_prefs"
  private const val KEY_IS_CREATED = "is_audio_room_created"
  private const val KEY_TITLE = "audio_room_title"
  private const val KEY_COVER_URI = "audio_room_cover_uri"
  private const val KEY_COUNTRY = "audio_room_country"
  private const val KEY_FLAG = "audio_room_flag"
  private const val KEY_CREATOR_UID = "audio_room_creator_uid"
  private const val KEY_SEAT_COUNT = "audio_room_seat_count"

  private val _roomSeatCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
  val roomSeatCounts: StateFlow<Map<String, Int>> = _roomSeatCounts.asStateFlow()

  fun normalizeSeatCount(count: Int): Int {
    return when (count) {
      12 -> 12
      16 -> 16
      else -> 8
    }
  }

  private fun getPrefs(context: Context): SharedPreferences {
    return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
  }

  fun isRoomCreated(context: Context): Boolean {
    return getPrefs(context).getBoolean(KEY_IS_CREATED, false)
  }

  fun getRoomSeatCount(context: Context, roomId: String, fallback: Int = 8): Int {
    val inMemory = _roomSeatCounts.value[roomId]
    if (inMemory != null) {
      return normalizeSeatCount(inMemory)
    }
    val prefs = getPrefs(context)
    val roomKey = "seat_count_$roomId"
    val stored = if (roomId.isNotBlank() && prefs.contains(roomKey)) {
      prefs.getInt(roomKey, fallback)
    } else if (prefs.contains(KEY_SEAT_COUNT)) {
      prefs.getInt(KEY_SEAT_COUNT, fallback)
    } else {
      fallback
    }
    val normalized = normalizeSeatCount(stored)
    if (roomId.isNotBlank()) {
      _roomSeatCounts.value = _roomSeatCounts.value + (roomId to normalized)
    }
    return normalized
  }

  fun updateSeatCount(context: Context, roomId: String, seatCount: Int) {
    val normalized = normalizeSeatCount(seatCount)
    val editor = getPrefs(context).edit()
      .putInt(KEY_SEAT_COUNT, normalized)
    if (roomId.isNotBlank()) {
      editor.putInt("seat_count_$roomId", normalized)
    }
    editor.apply()
    if (roomId.isNotBlank()) {
      _roomSeatCounts.value = _roomSeatCounts.value + (roomId to normalized)
    }
  }

  fun getSavedRoom(context: Context): SavedAudioRoom {
    val prefs = getPrefs(context)
    val rawTitle = prefs.getString(KEY_TITLE, "Cyber Sound Lounge") ?: "Cyber Sound Lounge"
    val title = rawTitle.replace("👑", "").replace("🎧", "").trim().ifBlank { "Cyber Sound Lounge" }
    val coverUri = prefs.getString(KEY_COVER_URI, null)
    val country = prefs.getString(KEY_COUNTRY, "Bangladesh") ?: "Bangladesh"
    val flag = prefs.getString(KEY_FLAG, "🇧🇩") ?: "🇧🇩"
    val creatorUid = prefs.getString(KEY_CREATOR_UID, UserProfileStorage.getActiveUserId(context)) ?: ""
    val roomId = "party_room_$creatorUid"
    val seatCount = getRoomSeatCount(context, roomId, prefs.getInt(KEY_SEAT_COUNT, 8))
    return SavedAudioRoom(
      id = roomId,
      title = title,
      coverUriString = coverUri,
      country = country,
      flagEmoji = flag,
      creatorUserId = creatorUid,
      seatCount = seatCount
    )
  }

  fun saveRoom(
    context: Context,
    title: String,
    coverUriString: String?,
    country: String,
    flagEmoji: String,
    creatorUserId: String = "",
    seatCount: Int? = null
  ) {
    val uid = if (creatorUserId.isNotBlank()) creatorUserId else UserProfileStorage.getActiveUserId(context)
    val roomId = "party_room_$uid"
    val prefs = getPrefs(context)
    val resolvedSeatCount = normalizeSeatCount(
      seatCount ?: getRoomSeatCount(context, roomId, prefs.getInt(KEY_SEAT_COUNT, 8))
    )
    prefs.edit()
      .putBoolean(KEY_IS_CREATED, true)
      .putString(KEY_TITLE, title.ifBlank { "My Neon Party Room" })
      .putString(KEY_COVER_URI, coverUriString)
      .putString(KEY_COUNTRY, country)
      .putString(KEY_FLAG, flagEmoji)
      .putString(KEY_CREATOR_UID, uid)
      .putInt(KEY_SEAT_COUNT, resolvedSeatCount)
      .putInt("seat_count_$roomId", resolvedSeatCount)
      .apply()
    _roomSeatCounts.value = _roomSeatCounts.value + (roomId to resolvedSeatCount)
  }

  fun updateRoom(context: Context, newTitle: String, newCoverUriString: String?, newSeatCount: Int? = null) {
    val editor = getPrefs(context).edit()
    if (newTitle.isNotBlank()) {
      editor.putString(KEY_TITLE, newTitle)
    }
    if (newCoverUriString != null) {
      editor.putString(KEY_COVER_URI, newCoverUriString)
    }
    if (newSeatCount != null) {
      val normalized = normalizeSeatCount(newSeatCount)
      editor.putInt(KEY_SEAT_COUNT, normalized)
      val creatorUid = getPrefs(context).getString(KEY_CREATOR_UID, UserProfileStorage.getActiveUserId(context)) ?: ""
      val roomId = "party_room_$creatorUid"
      editor.putInt("seat_count_$roomId", normalized)
      _roomSeatCounts.value = _roomSeatCounts.value + (roomId to normalized)
    }
    editor.apply()
  }

  fun closeRoom(context: Context, roomId: String? = null) {
    getPrefs(context).edit().putBoolean(KEY_IS_CREATED, false).apply()
  }

  fun getAllActiveRooms(context: Context): List<SavedAudioRoom> {
    return if (isRoomCreated(context)) listOf(getSavedRoom(context)) else emptyList()
  }
}
