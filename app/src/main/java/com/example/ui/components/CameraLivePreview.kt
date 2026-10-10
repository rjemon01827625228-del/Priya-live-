package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.aistudio.neonparty.nxvqlr.R
import com.example.ui.theme.NeonPink

/**
 * Real-time Video Stream view for Live Room (Host & Viewer).
 */
@Composable
fun CameraLivePreview(
  isHost: Boolean,
  fallbackAvatarRes: Int,
  fallbackAvatarUri: String?,
  isEnhancementEnabled: Boolean,
  onToggleEnhancement: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current

  var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_FRONT) }
  var hasCameraPermission by remember {
    mutableStateOf(
      ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    )
  }

  // Permission launcher for Camera & Mic
  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions()
  ) { permissions ->
    val cameraGranted = permissions[Manifest.permission.CAMERA] == true
    hasCameraPermission = cameraGranted
  }

  LaunchedEffect(isHost) {
    if (isHost && !hasCameraPermission) {
      permissionLauncher.launch(
        arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
      )
    }
  }

  Box(modifier = modifier.fillMaxSize()) {
    if (isHost) {
      if (hasCameraPermission) {
        // HOST MODE: Real-time CameraX Video Preview
        val previewView = remember {
          PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_CENTER
          }
        }
        DisposableEffect(lensFacing, lifecycleOwner) {
          val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
          var boundCameraProvider: ProcessCameraProvider? = null
          var boundPreview: Preview? = null
          val mainExecutor = ContextCompat.getMainExecutor(context)

          val listener = Runnable {
            try {
              val cameraProvider = cameraProviderFuture.get()
              boundCameraProvider = cameraProvider
              val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
              }
              boundPreview = preview
              val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build()
              cameraProvider.unbindAll()
              cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
            } catch (e: Exception) {
              Log.w("CameraLivePreview", "CameraX bind failed: ${e.message}")
            }
          }
          cameraProviderFuture.addListener(listener, mainExecutor)

          onDispose {
            try {
              boundPreview?.surfaceProvider = null
              boundCameraProvider?.unbindAll()
            } catch (e: Exception) {
              Log.w("CameraLivePreview", "CameraX unbind notice: ${e.message}")
            }
          }
        }
        AndroidView(
          factory = { previewView },
          modifier = Modifier.fillMaxSize(),
          onRelease = {
            try {
              ProcessCameraProvider.getInstance(context).get().unbindAll()
            } catch (_: Exception) {}
          }
        )
      } else {
        // Host without camera permission: prompt
        BroadcasterFallbackDisplay(
          fallbackAvatarUri = fallbackAvatarUri,
          fallbackAvatarRes = fallbackAvatarRes,
          context = context
        )

        Column(
          modifier = Modifier
            .align(Alignment.Center)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xCC110022))
            .border(1.dp, NeonPink, RoundedCornerShape(16.dp))
            .padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Icon(
            Icons.Filled.VideocamOff,
            contentDescription = null,
            tint = NeonPink,
            modifier = Modifier.size(36.dp)
          )
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            "Camera Access Required",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            "Grant camera permission to stream your live video to viewers.",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 12.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )
          Spacer(modifier = Modifier.height(12.dp))
          Button(
            onClick = {
              permissionLauncher.launch(
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
              )
            },
            colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
            shape = RoundedCornerShape(10.dp)
          ) {
            Text("Allow Camera", color = Color.White, fontWeight = FontWeight.Bold)
          }
        }
      }
    } else {
      // VIEWER MODE: Show broadcaster profile with live indicator
      BroadcasterFallbackDisplay(
        fallbackAvatarUri = fallbackAvatarUri,
        fallbackAvatarRes = fallbackAvatarRes,
        context = context
      )

      Row(
        modifier = Modifier
          .align(Alignment.Center)
          .clip(RoundedCornerShape(14.dp))
          .background(Color(0x99000000))
          .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f), RoundedCornerShape(14.dp))
          .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          Icons.Filled.LiveTv,
          contentDescription = null,
          tint = Color(0xFF00E5FF),
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Live stream active",
          color = Color.White,
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium
        )
      }
    }

    // 2. NATURAL BEAUTY ENHANCEMENT LAYER (Soft focus, warm lighting)
    if (isEnhancementEnabled && isHost) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
          brush = Brush.radialGradient(
            colors = listOf(
              Color(0x30FFF0E6),
              Color(0x25FEB4B4),
              Color(0x12FFE0B2),
              Color(0x05000000)
            ),
            center = center,
            radius = size.maxDimension * 0.75f
          ),
          blendMode = BlendMode.Screen
        )
      }
    }

    // Top Floating Quick Camera Switch for Host
    if (isHost && hasCameraPermission) {
      val topSafePadding = rememberResponsiveTopBarPadding(extraTopSpacing = 2.dp)
      Box(
        modifier = Modifier
          .align(Alignment.TopEnd)
          .padding(top = topSafePadding + 68.dp, end = 12.dp)
          .size(38.dp)
          .clip(CircleShape)
          .background(Color(0x88110022))
          .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape)
          .clickable {
            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
              CameraSelector.LENS_FACING_BACK
            } else {
              CameraSelector.LENS_FACING_FRONT
            }
          },
        contentAlignment = Alignment.Center
      ) {
        Icon(
          Icons.Filled.Cameraswitch,
          contentDescription = "Switch Camera",
          tint = Color.White,
          modifier = Modifier.size(20.dp)
        )
      }
    }
  }
}

@Composable
private fun BroadcasterFallbackDisplay(
  fallbackAvatarUri: String?,
  fallbackAvatarRes: Int,
  context: Context
) {
  Box(modifier = Modifier.fillMaxSize()) {
    val safeFallbackRes = remember(fallbackAvatarRes) {
      if (fallbackAvatarRes <= 0) {
        R.drawable.img_streamer_girl
      } else {
        try {
          context.resources.getResourceName(fallbackAvatarRes)
          fallbackAvatarRes
        } catch (_: Throwable) {
          R.drawable.img_streamer_girl
        }
      }
    }

    Image(
      painter = painterResource(id = safeFallbackRes),
      contentDescription = "Live Broadcaster",
      modifier = Modifier.fillMaxSize(),
      contentScale = ContentScale.Crop
    )

    if (!fallbackAvatarUri.isNullOrBlank()) {
      AsyncImage(
        model = fallbackAvatarUri,
        contentDescription = "Live Broadcaster",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
        placeholder = painterResource(id = safeFallbackRes),
        error = painterResource(id = safeFallbackRes),
        fallback = painterResource(id = safeFallbackRes)
      )
    }

    // Ambient dark overlay
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            listOf(
              Color(0x77080014),
              Color(0x22000000),
              Color(0x99080014)
            )
          )
        )
    )
  }
}
