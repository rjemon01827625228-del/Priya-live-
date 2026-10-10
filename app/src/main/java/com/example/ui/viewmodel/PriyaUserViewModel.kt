package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.R
import com.example.data.repository.FirestoreUserPost
import com.example.data.repository.FirestoreUserProfile
import com.example.data.repository.PriyaFirestoreRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
  data object Loading : UiState<Nothing>
  data class Success<T>(val data: T) : UiState<T>
  data class Error(val message: String) : UiState<Nothing>
}

class PriyaUserViewModel(
  private val repository: PriyaFirestoreRepository,
  private val currentUserId: String
) : ViewModel() {

  val profileState: StateFlow<UiState<FirestoreUserProfile?>> =
    repository.observeUserProfile(currentUserId)
      .map<FirestoreUserProfile?, UiState<FirestoreUserProfile?>> { UiState.Success(it) }
      .catch { error ->
        Log.w(TAG, "Error observing user profile", error)
        emit(UiState.Error(error.message ?: "Failed to observe user profile"))
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = UiState.Loading
      )

  val postsState: StateFlow<UiState<List<FirestoreUserPost>>> =
    repository.observeUserPosts(currentUserId)
      .map<List<FirestoreUserPost>, UiState<List<FirestoreUserPost>>> { UiState.Success(it) }
      .catch { error ->
        Log.w(TAG, "Error observing user posts", error)
        emit(UiState.Error(error.message ?: "Failed to observe user posts"))
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = UiState.Loading
      )

  fun syncUserProfile(
    username: String,
    gender: String = "Male",
    country: String = "Bangladesh",
    countryFlag: String = "🇧🇩",
    bio: String? = null,
    avatarUrl: String? = null,
    coins: Long = 0L,
    diamonds: Long = 0L
  ) {
    viewModelScope.launch {
      repository.saveUserProfile(
        username = username,
        gender = gender,
        country = country,
        countryFlag = countryFlag,
        bio = bio,
        avatarUrl = avatarUrl,
        coins = coins,
        diamonds = diamonds
      )
    }
  }

  fun publishPost(content: String, authorName: String) {
    viewModelScope.launch {
      repository.createPost(content = content, authorName = authorName)
    }
  }

  companion object {
    private const val TAG = "PriyaUserViewModel"
    private const val STOP_TIMEOUT_MILLIS = 5000L

    fun factory(currentUserId: String): ViewModelProvider.Factory = viewModelFactory {
      initializer {
        val app = checkNotNull(this[APPLICATION_KEY]) {
          "APPLICATION_KEY missing from CreationExtras"
        }
        val databaseId = app.getString(R.string.firestore_database_id)
        val db = FirebaseFirestore.getInstance(databaseId)
        PriyaUserViewModel(PriyaFirestoreRepository(db), currentUserId)
      }
    }
  }
}
