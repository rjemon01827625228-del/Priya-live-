package com.example.data.repository

import android.content.Context
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import java.util.UUID
import java.util.concurrent.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

data class FirestoreUserProfile(
  val userId: String = "",
  val username: String = "",
  val gender: String = "Male",
  val country: String = "Bangladesh",
  val countryFlag: String = "🇧🇩",
  val bio: String? = null,
  val avatarUrl: String? = null,
  val coins: Long = 0L,
  val diamonds: Long = 0L,
  val createdAt: Timestamp? = null,
  val updatedAt: Timestamp? = null
)

data class FirestoreUserPost(
  val postId: String = "",
  val userId: String = "",
  val authorName: String = "",
  val content: String = "",
  val likesCount: Long = 0L,
  val createdAt: Timestamp? = null,
  val updatedAt: Timestamp? = null
)

data class FirestorePartyRoom(
  val roomId: String = "",
  val hostUid: String = "",
  val roomTitle: String = "Party Board",
  val seatCount: Int = 8,
  val isLocked: Boolean = false,
  val createdAt: Timestamp? = null,
  val updatedAt: Timestamp? = null
)

class PriyaFirestoreRepository(private val db: FirebaseFirestore) {

  // CRITICAL: Always initialize with R.string.firestore_database_id
  constructor(context: Context) : this(
    FirebaseFirestore.getInstance(
      context.applicationContext.getString(R.string.firestore_database_id)
    )
  )

  private val auth = Firebase.auth

  private fun requireUserId(): String {
    return auth.currentUser?.uid
      ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
  }

  private fun DocumentSnapshot.toUserProfile(): FirestoreUserProfile? {
    if (!exists()) return null
    val data = data ?: return null
    return FirestoreUserProfile(
      userId = getString("userId") ?: id,
      username = getString("username") ?: "",
      gender = getString("gender") ?: "Male",
      country = getString("country") ?: "Bangladesh",
      countryFlag = getString("countryFlag") ?: "🇧🇩",
      bio = getString("bio"),
      avatarUrl = getString("avatarUrl"),
      coins = (data["coins"] as? Number)?.toLong() ?: 0L,
      diamonds = (data["diamonds"] as? Number)?.toLong() ?: 0L,
      createdAt = getTimestamp("createdAt", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE),
      updatedAt = getTimestamp("updatedAt", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
    )
  }

  private fun DocumentSnapshot.toUserPost(): FirestoreUserPost? {
    if (!exists()) return null
    val data = data ?: return null
    return FirestoreUserPost(
      postId = getString("postId") ?: id,
      userId = getString("userId") ?: "",
      authorName = getString("authorName") ?: "",
      content = getString("content") ?: "",
      likesCount = (data["likesCount"] as? Number)?.toLong() ?: 0L,
      createdAt = getTimestamp("createdAt", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE),
      updatedAt = getTimestamp("updatedAt", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
    )
  }

  private fun DocumentSnapshot.toPartyRoom(): FirestorePartyRoom? {
    if (!exists()) return null
    val data = data ?: return null
    val rawSeats = (data["seatCount"] as? Number)?.toInt() ?: 8
    val validSeats = when (rawSeats) {
      12 -> 12
      16 -> 16
      else -> 8
    }
    return FirestorePartyRoom(
      roomId = getString("roomId") ?: id,
      hostUid = getString("hostUid") ?: "",
      roomTitle = getString("roomTitle") ?: "Party Board",
      seatCount = validSeats,
      isLocked = getBoolean("isLocked") ?: false,
      createdAt = getTimestamp("createdAt", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE),
      updatedAt = getTimestamp("updatedAt", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
    )
  }

  fun observeUserProfile(userId: String = auth.currentUser?.uid ?: ""): Flow<FirestoreUserProfile?> = flow {
    val targetUid = userId.ifBlank {
      auth.currentUser?.uid ?: "unauthenticated"
    }
    val path = "users/$targetUid"
    try {
      emitAll(
        db.collection("users").document(targetUid)
          .snapshots()
          .map { snapshot -> snapshot.toUserProfile() }
          .catch { error ->
            if (error is Exception) handleFirestoreError(error, OperationType.GET, path)
            throw error
          }
      )
    } catch (e: CancellationException) {
      val firestoreEx = generateSequence(e as Throwable?) { it.cause }
        .filterIsInstance<FirebaseFirestoreException>()
        .firstOrNull()
      if (firestoreEx != null) {
        handleFirestoreError(firestoreEx, OperationType.GET, path)
        throw firestoreEx
      }
      throw e
    }
  }

  suspend fun saveUserProfile(
    username: String,
    gender: String = "Male",
    country: String = "Bangladesh",
    countryFlag: String = "🇧🇩",
    bio: String? = null,
    avatarUrl: String? = null,
    coins: Long = 0L,
    diamonds: Long = 0L
  ): Result<String> = runCatching {
    val uid = requireUserId()
    val docRef = db.collection("users").document(uid)
    val existingSnap = try {
      docRef.get().await()
    } catch (_: Exception) {
      null
    }

    if (existingSnap != null && existingSnap.exists()) {
      val updatePayload = mapOf<String, Any?>(
        "username" to username.take(50).ifBlank { "PriyaUser" },
        "gender" to if (gender in listOf("Male", "Female", "Other")) gender else "Male",
        "country" to country.take(60).ifBlank { "Bangladesh" },
        "countryFlag" to countryFlag.take(20).ifBlank { "🇧🇩" },
        "bio" to bio?.take(300),
        "avatarUrl" to avatarUrl?.take(1000),
        "coins" to coins.coerceAtLeast(0L),
        "diamonds" to diamonds.coerceAtLeast(0L),
        "updatedAt" to FieldValue.serverTimestamp()
      ).filterValues { it != null } as Map<String, Any>

      try {
        docRef.update(updatePayload).await()
      } catch (e: Exception) {
        handleFirestoreError(e, OperationType.UPDATE, docRef.path)
        throw e
      }
    } else {
      val createPayload = mapOf<String, Any?>(
        "userId" to uid,
        "username" to username.take(50).ifBlank { "PriyaUser" },
        "gender" to if (gender in listOf("Male", "Female", "Other")) gender else "Male",
        "country" to country.take(60).ifBlank { "Bangladesh" },
        "countryFlag" to countryFlag.take(20).ifBlank { "🇧🇩" },
        "bio" to bio?.take(300),
        "avatarUrl" to avatarUrl?.take(1000),
        "coins" to coins.coerceAtLeast(0L),
        "diamonds" to diamonds.coerceAtLeast(0L),
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
      ).filterValues { it != null } as Map<String, Any>

      try {
        docRef.set(createPayload).await()
      } catch (e: Exception) {
        handleFirestoreError(e, OperationType.CREATE, docRef.path)
        throw e
      }
    }
    uid
  }

  suspend fun getUserProfile(targetUserId: String = auth.currentUser?.uid ?: ""): Result<FirestoreUserProfile?> = runCatching {
    val uid = targetUserId.ifBlank { requireUserId() }
    val docRef = db.collection("users").document(uid)
    try {
      val snapshot = docRef.get().await()
      snapshot.toUserProfile()
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.GET, docRef.path)
      throw e
    }
  }

  fun observeUserPosts(userId: String = auth.currentUser?.uid ?: ""): Flow<List<FirestoreUserPost>> = flow {
    val targetUid = userId.ifBlank {
      auth.currentUser?.uid ?: "unauthenticated"
    }
    val path = "users/$targetUid/posts"
    try {
      emitAll(
        db.collection("users").document(targetUid).collection("posts")
          .whereEqualTo("userId", targetUid)
          .snapshots()
          .map { querySnapshot ->
            querySnapshot.documents
              .mapNotNull { it.toUserPost() }
              .sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
          }
          .catch { error ->
            if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
            throw error
          }
      )
    } catch (e: CancellationException) {
      val firestoreEx = generateSequence(e as Throwable?) { it.cause }
        .filterIsInstance<FirebaseFirestoreException>()
        .firstOrNull()
      if (firestoreEx != null) {
        handleFirestoreError(firestoreEx, OperationType.LIST, path)
        throw firestoreEx
      }
      throw e
    }
  }

  suspend fun createPost(
    content: String,
    authorName: String = "Priya User"
  ): Result<String> = runCatching {
    val uid = requireUserId()
    val postId = "post_${UUID.randomUUID().toString().replace("-", "").take(16)}"
    val postRef = db.collection("users").document(uid).collection("posts").document(postId)
    val payload = mapOf<String, Any>(
      "postId" to postId,
      "userId" to uid,
      "authorName" to authorName.take(50).ifBlank { "Priya User" },
      "content" to content.take(500).ifBlank { "Hello Priya Live!" },
      "likesCount" to 0L,
      "createdAt" to FieldValue.serverTimestamp(),
      "updatedAt" to FieldValue.serverTimestamp()
    )
    try {
      postRef.set(payload).await()
      postId
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, postRef.path)
      throw e
    }
  }

  suspend fun getUserPosts(targetUserId: String = auth.currentUser?.uid ?: ""): Result<List<FirestoreUserPost>> = runCatching {
    val uid = targetUserId.ifBlank { requireUserId() }
    val postsRef = db.collection("users").document(uid).collection("posts")
    try {
      val snapshot = postsRef.whereEqualTo("userId", uid).get().await()
      snapshot.documents
        .mapNotNull { it.toUserPost() }
        .sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.LIST, postsRef.path)
      throw e
    }
  }

  suspend fun getPostById(ownerUserId: String, postId: String): Result<FirestoreUserPost?> = runCatching {
    val docRef = db.collection("users").document(ownerUserId).collection("posts").document(postId)
    try {
      val snapshot = docRef.get().await()
      snapshot.toUserPost()
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.GET, docRef.path)
      throw e
    }
  }

  suspend fun incrementPostLikes(postId: String): Result<Unit> = runCatching {
    val uid = requireUserId()
    val docRef = db.collection("users").document(uid).collection("posts").document(postId)
    val updatePayload = mapOf<String, Any>(
      "likesCount" to FieldValue.increment(1L),
      "updatedAt" to FieldValue.serverTimestamp()
    )
    try {
      docRef.update(updatePayload).await()
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, docRef.path)
      throw e
    }
  }

  fun observePartyRoom(roomId: String): Flow<FirestorePartyRoom?> = flow {
    val cleanId = roomId.replace(Regex("[^a-zA-Z0-9_\\-]"), "_").take(128).ifBlank { "default_party_room" }
    val path = "party_rooms/$cleanId"
    try {
      emitAll(
        db.collection("party_rooms").document(cleanId)
          .snapshots()
          .map { snapshot -> snapshot.toPartyRoom() }
          .catch { error ->
            if (error is Exception) handleFirestoreError(error, OperationType.GET, path)
            throw error
          }
      )
    } catch (e: CancellationException) {
      val firestoreEx = generateSequence(e as Throwable?) { it.cause }
        .filterIsInstance<FirebaseFirestoreException>()
        .firstOrNull()
      if (firestoreEx != null) {
        handleFirestoreError(firestoreEx, OperationType.GET, path)
        throw firestoreEx
      }
      throw e
    }
  }

  suspend fun saveOrUpdatePartyRoom(
    roomId: String,
    roomTitle: String,
    seatCount: Int,
    isLocked: Boolean
  ): Result<String> = runCatching {
    val uid = requireUserId()
    val cleanId = roomId.replace(Regex("[^a-zA-Z0-9_\\-]"), "_").take(128).ifBlank { "party_room_$uid" }
    val validSeats = when (seatCount) {
      12 -> 12L
      16 -> 16L
      else -> 8L
    }
    val docRef = db.collection("party_rooms").document(cleanId)
    val existingSnap = try {
      docRef.get().await()
    } catch (_: Exception) {
      null
    }

    if (existingSnap != null && existingSnap.exists()) {
      val updatePayload = mapOf<String, Any>(
        "roomTitle" to roomTitle.take(100).ifBlank { "Party Board" },
        "seatCount" to validSeats,
        "isLocked" to isLocked,
        "updatedAt" to FieldValue.serverTimestamp()
      )
      try {
        docRef.update(updatePayload).await()
      } catch (e: Exception) {
        handleFirestoreError(e, OperationType.UPDATE, docRef.path)
        throw e
      }
    } else {
      val createPayload = mapOf<String, Any>(
        "roomId" to cleanId,
        "hostUid" to uid,
        "roomTitle" to roomTitle.take(100).ifBlank { "Party Board" },
        "seatCount" to validSeats,
        "isLocked" to isLocked,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
      )
      try {
        docRef.set(createPayload).await()
      } catch (e: Exception) {
        handleFirestoreError(e, OperationType.CREATE, docRef.path)
        throw e
      }
    }
    cleanId
  }
}
