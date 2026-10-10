package com.example.ui.storage

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream

/**
 * Local persistent image storage manager for profile avatars and room covers.
 */
object LocalImageStorage {
  private const val TAG = "LocalImageStorage"
  private const val LOCAL_PROFILE_DIR = "persistent_profile_images"
  private const val LOCAL_ROOM_DIR = "persistent_room_images"

  enum class ImageType {
    PROFILE,
    ROOM_COVER
  }

  /**
   * Saves a picked Gallery or Camera URI into app internal filesDir.
   */
  fun saveUriLocally(
    context: Context,
    sourceUri: Uri,
    imageType: ImageType,
    identifier: String
  ): File? {
    return try {
      val safeId = identifier.ifBlank { "default" }.replace("[^a-zA-Z0-9_-]".toRegex(), "_")
      try {
        context.contentResolver.takePersistableUriPermission(
          sourceUri,
          Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
      } catch (_: Exception) {
        // Not all URIs support persistable permissions
      }

      val subDirName = when (imageType) {
        ImageType.PROFILE -> LOCAL_PROFILE_DIR
        ImageType.ROOM_COVER -> LOCAL_ROOM_DIR
      }
      val dir = File(context.filesDir, subDirName)
      if (!dir.exists()) {
        dir.mkdirs()
      }

      val targetFile = File(dir, "${imageType.name.lowercase()}_${safeId}.jpg")

      val inputStream = context.contentResolver.openInputStream(sourceUri) ?: return null
      val decodedBitmap = inputStream.use { BitmapFactory.decodeStream(it) }

      if (decodedBitmap != null) {
        val maxDim = 1080
        val scaledBitmap = if (decodedBitmap.width > maxDim || decodedBitmap.height > maxDim) {
          val ratio = minOf(
            maxDim.toFloat() / decodedBitmap.width,
            maxDim.toFloat() / decodedBitmap.height
          )
          val newW = (decodedBitmap.width * ratio).toInt().coerceAtLeast(1)
          val newH = (decodedBitmap.height * ratio).toInt().coerceAtLeast(1)
          Bitmap.createScaledBitmap(decodedBitmap, newW, newH, true)
        } else {
          decodedBitmap
        }

        FileOutputStream(targetFile).use { out ->
          scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 88, out)
          out.flush()
        }
      } else {
        context.contentResolver.openInputStream(sourceUri)?.use { input ->
          FileOutputStream(targetFile).use { output ->
            input.copyTo(output)
          }
        }
      }

      if (targetFile.exists() && targetFile.length() > 0L) {
        targetFile
      } else {
        null
      }
    } catch (e: Exception) {
      Log.e(TAG, "Failed to save URI locally ($sourceUri): ${e.message}")
      null
    }
  }

  /**
   * Returns the local persistent file if it exists and is non-empty.
   */
  fun getLocalPersistentFile(
    context: Context,
    imageType: ImageType,
    identifier: String
  ): File? {
    val safeId = identifier.ifBlank { "default" }.replace("[^a-zA-Z0-9_-]".toRegex(), "_")
    val subDirName = when (imageType) {
      ImageType.PROFILE -> LOCAL_PROFILE_DIR
      ImageType.ROOM_COVER -> LOCAL_ROOM_DIR
    }
    val file = File(File(context.filesDir, subDirName), "${imageType.name.lowercase()}_${safeId}.jpg")
    return if (file.exists() && file.length() > 0L) file else null
  }

  /**
   * Persists a room picture locally and invokes [onComplete] with the permanent file URI.
   */
  fun persistRoomPicture(
    context: Context,
    roomId: String,
    sourceUri: Uri,
    onComplete: (String) -> Unit
  ) {
    val localFile = saveUriLocally(context, sourceUri, ImageType.ROOM_COVER, roomId)
    val localUriStr = localFile?.let { "file://${it.absolutePath}" } ?: sourceUri.toString()
    UserRoomStorage.updateRoom(context, "", localUriStr)
    onComplete(localUriStr)
  }

  /**
   * Resolves a potentially expired or missing URI string into a valid Coil model.
   */
  fun resolveImageModel(
    context: Context,
    candidateUri: String?,
    imageType: ImageType = ImageType.PROFILE,
    entityId: String? = null,
    defaultFallbackRes: Int
  ): Any {
    if (candidateUri.isNullOrBlank()) {
      if (!entityId.isNullOrBlank()) {
        val localFallback = getLocalPersistentFile(context, imageType, entityId)
        if (localFallback != null) return localFallback
      }
      return defaultFallbackRes
    }

    val trimmed = candidateUri.trim()

    if (trimmed.startsWith("https://") || trimmed.startsWith("http://")) {
      return trimmed
    }

    if (trimmed.startsWith("file://")) {
      val path = trimmed.removePrefix("file://")
      val file = File(path)
      if (file.exists() && file.length() > 0L) {
        return file
      }
    } else if (trimmed.startsWith("/")) {
      val file = File(trimmed)
      if (file.exists() && file.length() > 0L) {
        return file
      }
    }

    if (trimmed.startsWith("content://")) {
      return try {
        val uri = Uri.parse(trimmed)
        val canRead = context.contentResolver.openInputStream(uri)?.use { true } ?: false
        if (canRead) {
          if (!entityId.isNullOrBlank()) {
            val saved = saveUriLocally(context, uri, imageType, entityId)
            if (saved != null) return saved
          }
          uri
        } else {
          if (!entityId.isNullOrBlank()) {
            val localFallback = getLocalPersistentFile(context, imageType, entityId)
            if (localFallback != null) return localFallback
          }
          defaultFallbackRes
        }
      } catch (_: Exception) {
        if (!entityId.isNullOrBlank()) {
          val localFallback = getLocalPersistentFile(context, imageType, entityId)
          if (localFallback != null) return localFallback
        }
        defaultFallbackRes
      }
    }

    return defaultFallbackRes
  }
}
