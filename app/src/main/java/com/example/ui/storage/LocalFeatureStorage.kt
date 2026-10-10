package com.example.ui.storage

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class FeedPost(
  var postId: String = "",
  var authorId: String = "",
  var authorName: String = "",
  var authorAvatarUri: String? = null,
  var authorLevel: Int = 1,
  var caption: String = "",
  var mediaUri: String? = null,
  var postType: String = "Post",
  var timestamp: Long = System.currentTimeMillis(),
  var likeCount: Int = 0,
  var commentCount: Int = 0,
  var likedUsers: Map<String, Boolean> = emptyMap()
)

typealias LocalFeedPost = FeedPost

object LocalFeedStorage {
  val posts = mutableStateListOf<FeedPost>()

  fun createPost(post: FeedPost, onComplete: (Boolean) -> Unit) {
    val key = post.postId.ifBlank { "post_${System.currentTimeMillis()}" }
    val saved = post.copy(postId = key)
    posts.add(0, saved)
    onComplete(true)
  }

  fun toggleLikePost(postId: String, userId: String) {
    val index = posts.indexOfFirst { it.postId == postId }
    if (index != -1) {
      val current = posts[index]
      val currentlyLiked = current.likedUsers[userId] == true
      val updatedLikes = current.likedUsers.toMutableMap()
      if (currentlyLiked) {
        updatedLikes.remove(userId)
      } else {
        updatedLikes[userId] = true
      }
      val newCount = if (currentlyLiked) (current.likeCount - 1).coerceAtLeast(0) else current.likeCount + 1
      posts[index] = current.copy(likedUsers = updatedLikes, likeCount = newCount)
    }
  }
}

data class DirectMessage(
  var messageId: String = "",
  var senderId: String = "",
  var senderName: String = "",
  var receiverId: String = "",
  var receiverName: String = "",
  var text: String = "",
  var timestamp: Long = System.currentTimeMillis()
)

data class UserConversationSummary(
  var peerUserId: String = "",
  var peerName: String = "",
  var peerAvatarUri: String? = null,
  var peerFlag: String = "🇧🇩",
  var lastMessage: String = "",
  var timestamp: Long = 0L,
  var unreadCount: Int = 0
)

typealias LocalConversationSummary = UserConversationSummary

object LocalChatStorage {
  private val conversations = mutableMapOf<String, MutableList<DirectMessage>>()
  private val inboxes = mutableMapOf<String, MutableMap<String, UserConversationSummary>>()

  fun getConversationSummaries(context: Context, userId: String): List<UserConversationSummary> {
    return getInboxes(userId)
  }

  fun getConversationKey(u1: String, u2: String): String {
    return if (u1 <= u2) "${u1}___${u2}" else "${u2}___${u1}"
  }

  fun getConversationMessages(u1: String, u2: String): List<DirectMessage> {
    val key = getConversationKey(u1, u2)
    return conversations[key]?.toList() ?: emptyList()
  }

  fun getInboxes(userId: String): List<UserConversationSummary> {
    return inboxes[userId]?.values?.sortedByDescending { it.timestamp } ?: emptyList()
  }

  fun sendMessage(
    senderId: String,
    senderName: String,
    senderAvatar: String?,
    receiverId: String,
    receiverName: String,
    receiverAvatar: String?,
    text: String,
    onComplete: ((Boolean) -> Unit)? = null
  ) {
    if (text.isBlank() || senderId.isBlank() || receiverId.isBlank()) {
      onComplete?.invoke(false)
      return
    }
    val convKey = getConversationKey(senderId, receiverId)
    val now = System.currentTimeMillis()
    val msg = DirectMessage(
      messageId = "msg_$now",
      senderId = senderId,
      senderName = senderName,
      receiverId = receiverId,
      receiverName = receiverName,
      text = text.trim(),
      timestamp = now
    )
    val list = conversations.getOrPut(convKey) { mutableListOf() }
    list.add(msg)

    val senderInbox = inboxes.getOrPut(senderId) { mutableMapOf() }
    senderInbox[receiverId] = UserConversationSummary(
      peerUserId = receiverId,
      peerName = receiverName.ifBlank { "User_$receiverId" },
      peerAvatarUri = receiverAvatar,
      peerFlag = "🇧🇩",
      lastMessage = text.trim(),
      timestamp = now,
      unreadCount = 0
    )

    val receiverInbox = inboxes.getOrPut(receiverId) { mutableMapOf() }
    receiverInbox[senderId] = UserConversationSummary(
      peerUserId = senderId,
      peerName = senderName.ifBlank { "User_$senderId" },
      peerAvatarUri = senderAvatar,
      peerFlag = "🇧🇩",
      lastMessage = text.trim(),
      timestamp = now,
      unreadCount = 1
    )

    onComplete?.invoke(true)
  }
}

data class RoomChatMessage(
  var messageId: String = "",
  var roomId: String = "",
  var senderUserId: String = "",
  var senderName: String = "",
  var senderAvatarUri: String? = null,
  var senderLevel: Int = 1,
  var senderVipLevel: Int = 0,
  var content: String = "",
  var timestamp: Long = System.currentTimeMillis()
)

data class PartySeatData(
  var seatNumber: Int = 1,
  var isOccupied: Boolean = false,
  var isLocked: Boolean = false,
  var userId: String? = null,
  var userName: String? = null,
  var userAvatarUri: String? = null,
  var userLevel: Int = 1,
  var userGender: String = "Male",
  var userCountry: String = "Bangladesh",
  var userCountryFlag: String = "🇧🇩",
  var userFrameId: String? = null,
  var userFrameUrl: String? = null,
  var isSpeaking: Boolean = false,
  var isMuted: Boolean = false
)

data class RoomMusicState(
  var title: String = "",
  var audioUrl: String = "",
  var isPlaying: Boolean = false,
  var position: Long = 0L,
  var updatedAt: Long = 0L,
  var startedBy: String = ""
)

data class PartyRoomData(
  var roomId: String = "",
  var roomTitle: String = "Party Board",
  var hostUid: String = "",
  var hostPID: String = "",
  var hostName: String = "",
  var hostAvatarUri: String? = null,
  var hostUserId: String = "",
  var coverUri: String? = null,
  var country: String = "Bangladesh",
  var countryCode: String = "BD",
  var flagEmoji: String = "🇧🇩",
  var isLocked: Boolean = false,
  var seatCount: Int = 8,
  var seats: Map<String, PartySeatData> = emptyMap(),
  var music: RoomMusicState? = null,
  var viewerCount: Int = 1,
  var coinPool: Long = 0L,
  var active: Boolean = true,
  var lastActivityTimestamp: Long = System.currentTimeMillis()
)

data class GiftEventData(
  var id: String = "",
  var eventId: String = "",
  var roomId: String = "",
  var senderUid: String = "",
  var senderUserId: String = "",
  var senderName: String = "",
  var senderAvatar: String? = null,
  var senderAvatarUri: String? = null,
  var receiverUserId: String? = null,
  var receiverName: String? = null,
  var giftId: String = "",
  var giftName: String = "",
  var giftEmoji: String = "",
  var coinCost: Long = 0L,
  var coinValue: Long = 0L,
  var timestamp: Long = System.currentTimeMillis()
)

data class AgencyItem(
  var agencyId: String = "",
  var agencyName: String = "",
  var ownerUid: String = "",
  var ownerPid: String = "",
  var ownerName: String = "",
  var logoUrl: String? = null,
  var country: String = "Bangladesh",
  var countryFlag: String = "🇧🇩",
  var status: String = "active",
  var level: String = "Lv.1",
  var hostCount: Int = 0,
  var notice: String = "Official verified broadcasting agency",
  var createdAt: Long = System.currentTimeMillis(),
  var updatedAt: Long = System.currentTimeMillis()
)

data class HostJoinRequest(
  var requestId: String = "",
  var userPid: String = "",
  var userUid: String = "",
  var userName: String = "",
  var userProfilePicture: String? = null,
  var userCountry: String = "Bangladesh",
  var agencyId: String = "",
  var agencyName: String = "",
  var agencyOwnerUid: String = "",
  var agencyOwnerPid: String = "",
  var status: String = "PENDING",
  var timestamp: Long = System.currentTimeMillis(),
  var reviewedAt: Long = 0L
)

data class HostDailyProgress(
  var dayNumber: Int = 1,
  var dateStamp: String = "",
  var eligibleSeconds: Long = 0L,
  var requiredSeconds: Long = 4980L,
  var isCompleted: Boolean = false,
  var rewardCoins: Long = 4000L,
  var isClaimed: Boolean = false,
  var claimedAt: Long = 0L
)

data class HostCycleProgress(
  var userPid: String = "",
  var userUid: String = "",
  var completedDays: Int = 0,
  var totalDaysRequired: Int = 10,
  var dailyRequiredSeconds: Long = 4980L,
  var dailyRewardCoins: Long = 4000L,
  var totalCycleRewardCoins: Long = 40000L,
  var pendingRewardCoins: Long = 0L,
  var claimedRewardCoins: Long = 0L,
  var isCycleCompleted: Boolean = false,
  var lastStreamingDateStamp: String = "",
  var updatedAt: Long = System.currentTimeMillis()
)

object LocalHostAgencyStorage {
  const val REQUIRED_DAILY_LIVE_SECONDS = 4980L
  const val DAILY_REWARD_COINS = 4000L

  private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
  private val agencies = mutableListOf<AgencyItem>()
  private val requests = mutableMapOf<String, HostJoinRequest>()
  private val cycleMap = mutableMapOf<String, HostCycleProgress>()
  private val daysMap = mutableMapOf<String, MutableList<HostDailyProgress>>()

  fun getAgenciesForCountry(country: String): List<AgencyItem> {
    val trimmed = country.trim()
    return agencies.filter {
      it.country.trim().equals(trimmed, ignoreCase = true) &&
        it.status.trim().equals("active", ignoreCase = true)
    }
  }

  fun getHostProgress(userPid: String): Pair<HostCycleProgress, List<HostDailyProgress>> {
    val cycle = cycleMap.getOrPut(userPid) { HostCycleProgress(userPid = userPid, userUid = userPid) }
    val days = daysMap.getOrPut(userPid) {
      (1..10).map { HostDailyProgress(dayNumber = it) }.toMutableList()
    }
    return Pair(cycle, days.toList())
  }

  fun getHostRequest(requestId: String): HostJoinRequest? {
    return requests[requestId]
  }

  fun sendHostJoinRequest(
    context: Context,
    agencyId: String,
    onResult: (Boolean, String) -> Unit
  ) {
    val trimmedId = agencyId.trim()
    if (trimmedId.isBlank()) {
      onResult(false, "Please enter an Agency ID.")
      return
    }
    val userProfile = UserProfileStorage.getProfile(context)
    val userPid = userProfile.userId
    val agency = agencies.find { it.agencyId == trimmedId }
    val agencyName = agency?.agencyName ?: "Agency $trimmedId"
    val requestId = "req_${userPid}_${trimmedId}"
    val req = HostJoinRequest(
      requestId = requestId,
      userPid = userPid,
      userUid = userPid,
      userName = userProfile.username,
      userProfilePicture = userProfile.profileImageUri,
      userCountry = userProfile.country.ifBlank { "Bangladesh" },
      agencyId = trimmedId,
      agencyName = agencyName,
      status = "PENDING"
    )
    requests[requestId] = req
    UserProfileStorage.updateHostAndAgencyStatus(
      context = context,
      hostStatus = "pending",
      agencyStatus = "pending",
      agencyId = trimmedId,
      agencyName = agencyName,
      pendingRequestId = requestId,
      userId = userPid
    )
    onResult(true, "Request sent to $agencyName! Awaiting approval.")
  }

  fun recordEligibleLiveStreamingTime(
    context: Context,
    userPid: String,
    elapsedSeconds: Long
  ) {
    if (elapsedSeconds <= 0L) return
    val userProfile = UserProfileStorage.getProfile(context)
    if (!userProfile.hostStatus.equals("active", ignoreCase = true)) return

    val todayStamp = dateFormat.format(Date())
    val (cycle, currentDays) = getHostProgress(userPid)
    val mutableDays = currentDays.toMutableList()

    var targetIndex = (cycle.completedDays).coerceIn(0, 9)
    val existingTodayIdx = mutableDays.indexOfFirst { it.dateStamp == todayStamp }
    if (existingTodayIdx != -1) {
      targetIndex = existingTodayIdx
    }

    val targetDay = mutableDays[targetIndex]
    val newSeconds = targetDay.eligibleSeconds + elapsedSeconds
    val isNowCompleted = newSeconds >= REQUIRED_DAILY_LIVE_SECONDS
    var completedDays = cycle.completedDays
    var pendingCoins = cycle.pendingRewardCoins

    if (isNowCompleted && !targetDay.isCompleted) {
      completedDays = (completedDays + 1).coerceAtMost(10)
      pendingCoins += DAILY_REWARD_COINS
    }

    mutableDays[targetIndex] = targetDay.copy(
      dateStamp = todayStamp,
      eligibleSeconds = newSeconds,
      isCompleted = isNowCompleted || targetDay.isCompleted
    )
    daysMap[userPid] = mutableDays
    cycleMap[userPid] = cycle.copy(
      completedDays = completedDays,
      pendingRewardCoins = pendingCoins,
      isCycleCompleted = completedDays >= 10,
      lastStreamingDateStamp = todayStamp,
      updatedAt = System.currentTimeMillis()
    )
  }

  fun claimHostReward(
    context: Context,
    userPid: String,
    onResult: (Boolean, Long, String) -> Unit
  ) {
    val (cycle, currentDays) = getHostProgress(userPid)
    val pending = cycle.pendingRewardCoins
    if (pending <= 0L) {
      onResult(false, 0L, "No pending rewards available to claim.")
      return
    }
    val updatedDays = currentDays.map {
      if (it.isCompleted && !it.isClaimed) {
        it.copy(isClaimed = true, claimedAt = System.currentTimeMillis())
      } else {
        it
      }
    }.toMutableList()
    daysMap[userPid] = updatedDays
    cycleMap[userPid] = cycle.copy(
      pendingRewardCoins = 0L,
      claimedRewardCoins = cycle.claimedRewardCoins + pending,
      updatedAt = System.currentTimeMillis()
    )
    UserProfileStorage.addCoins(context, pending, userPid)
    onResult(true, pending, "Successfully claimed $pending Diamonds!")
  }
}

object LocalFeatureStorage {
  fun getFeedPosts(context: Context): List<FeedPost> {
    return LocalFeedStorage.posts.toList()
  }

  fun toggleLikePost(context: Context, postId: String, userId: String) {
    LocalFeedStorage.toggleLikePost(postId, userId)
  }

  fun recordEligibleLiveStreamingTime(context: Context, userPid: String, elapsedSeconds: Long) {
    LocalHostAgencyStorage.recordEligibleLiveStreamingTime(context, userPid, elapsedSeconds)
  }
}
