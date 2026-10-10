package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.google.common.truth.Truth.assertThat
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlin.test.assertFailsWith
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Test

class PriyaFirestoreRepositoryRuleTest : FirestoreEmulatorTestBase() {

  @Test
  fun saveAndGetUserProfile_authenticatedOwner_succeeds() = runBlocking {
    val aliceUid = signInTestUser(ALICE_EMAIL)
    val repository = PriyaFirestoreRepository(firestore)

    val saveResult = withTimeout(DEFAULT_TIMEOUT_MS) {
      repository.saveUserProfile(
        username = "Alice Star",
        gender = "Female",
        country = "Bangladesh",
        countryFlag = "🇧🇩",
        bio = "Priya Live Host"
      )
    }
    assertThat(saveResult.isSuccess).isTrue()
    assertThat(saveResult.getOrThrow()).isEqualTo(aliceUid)

    val profileResult = withTimeout(DEFAULT_TIMEOUT_MS) {
      repository.getUserProfile(aliceUid)
    }
    assertThat(profileResult.isSuccess).isTrue()
    assertThat(profileResult.getOrThrow()?.username).isEqualTo("Alice Star")
  }

  @Test
  fun createAndObservePost_authenticatedOwner_emitsRealtimeUpdates() = runBlocking {
    signInTestUser(ALICE_EMAIL)
    val repository = PriyaFirestoreRepository(firestore)

    val postId = withTimeout(DEFAULT_TIMEOUT_MS) {
      repository.createPost(content = "Streaming live now!", authorName = "Alice Star").getOrThrow()
    }
    assertThat(postId).isNotEmpty()

    val emittedPosts = withTimeout(FLOW_TIMEOUT_MS) {
      repository.observeUserPosts().first { list -> list.any { it.postId == postId } }
    }
    assertThat(emittedPosts.map { it.postId }).contains(postId)
  }

  @Test
  fun getPostById_crossUserAccess_failsWithPermissionDenied() = runBlocking {
    val aliceUid = signInTestUser(ALICE_EMAIL)
    val aliceRepo = PriyaFirestoreRepository(firestore)
    val postId = withTimeout(DEFAULT_TIMEOUT_MS) {
      aliceRepo.createPost(content = "Alice's private post", authorName = "Alice").getOrThrow()
    }

    signInTestUser(BOB_EMAIL)
    val bobRepo = PriyaFirestoreRepository(firestore)
    val exception = assertFailsWith<FirebaseFirestoreException> {
      withTimeout(DEFAULT_TIMEOUT_MS) {
        bobRepo.getPostById(aliceUid, postId).getOrThrow()
      }
    }
    assertThat(exception.code).isEqualTo(FirebaseFirestoreException.Code.PERMISSION_DENIED)
  }

  @Test
  fun observeUserPosts_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
    val aliceUid = signInTestUser(ALICE_EMAIL)
    auth.signOut()
    val repository = PriyaFirestoreRepository(firestore)

    val exception = assertFailsWith<FirebaseFirestoreException> {
      withTimeout(FLOW_TIMEOUT_MS) {
        repository.observeUserPosts(aliceUid).first()
      }
    }
    assertThat(exception.code).isEqualTo(FirebaseFirestoreException.Code.PERMISSION_DENIED)
  }

  @Test
  fun partyRoomSeatCountSync_ownerUpdates8To16To12_connectedUsersReceiveRealtimeLayout() = runBlocking {
    val aliceUid = signInTestUser(ALICE_EMAIL)
    val aliceRepo = PriyaFirestoreRepository(firestore)
    val roomId = "party_room_$aliceUid"

    // Owner creates room with 8 seats
    val createRes = withTimeout(DEFAULT_TIMEOUT_MS) {
      aliceRepo.saveOrUpdatePartyRoom(
        roomId = roomId,
        roomTitle = "Alice Voice Party",
        seatCount = 8,
        isLocked = false
      )
    }
    assertThat(createRes.isSuccess).isTrue()

    // Owner updates room to 16 seats
    val update16Res = withTimeout(DEFAULT_TIMEOUT_MS) {
      aliceRepo.saveOrUpdatePartyRoom(
        roomId = roomId,
        roomTitle = "Alice Voice Party",
        seatCount = 16,
        isLocked = false
      )
    }
    assertThat(update16Res.isSuccess).isTrue()

    // Another user (Bob) enters the room and receives the 16-seat layout in real time
    signInTestUser(BOB_EMAIL)
    val bobRepo = PriyaFirestoreRepository(firestore)
    val bobObserved16 = withTimeout(FLOW_TIMEOUT_MS) {
      bobRepo.observePartyRoom(roomId).first { it != null && it.seatCount == 16 }
    }
    assertThat(bobObserved16?.seatCount).isEqualTo(16)

    // Owner changes seatCount to 12 seats and connected user receives 12-seat layout
    signInTestUser(ALICE_EMAIL)
    withTimeout(DEFAULT_TIMEOUT_MS) {
      aliceRepo.saveOrUpdatePartyRoom(
        roomId = roomId,
        roomTitle = "Alice Voice Party",
        seatCount = 12,
        isLocked = false
      ).getOrThrow()
    }

    signInTestUser(BOB_EMAIL)
    val bobObserved12 = withTimeout(FLOW_TIMEOUT_MS) {
      bobRepo.observePartyRoom(roomId).first { it != null && it.seatCount == 12 }
    }
    assertThat(bobObserved12?.seatCount).isEqualTo(12)
  }

  private companion object {
    const val ALICE_EMAIL = "alice@test.com"
    const val BOB_EMAIL = "bob@test.com"
    const val DEFAULT_TIMEOUT_MS = 5000L
    const val FLOW_TIMEOUT_MS = 3000L
  }
}
