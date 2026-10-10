package com.example.ui.components

import android.content.Context
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import coil.compose.AsyncImage
import com.aistudio.neonparty.nxvqlr.R
import com.example.ui.storage.StoreCenterStorage
import com.example.ui.storage.StoreItem
import kotlin.math.cos
import kotlin.math.sin

/**
 * Resolves a store item frame given a frameId and/or frameUrl
 */
fun resolveStoreFrame(context: Context, frameId: String?, frameUrl: String?): StoreItem? {
  if (frameId.isNullOrBlank() && frameUrl.isNullOrBlank()) return null
  val all = StoreCenterStorage.getAllItems(context)
  val found = if (!frameId.isNullOrBlank()) all.find { it.id == frameId } else null
  if (found != null) {
    return if (!frameUrl.isNullOrBlank() && found.imageUrl.isNullOrBlank()) found.copy(imageUrl = frameUrl) else found
  }
  val preset = if (!frameId.isNullOrBlank()) StoreCenterStorage.presetTemplates.find { it.id == frameId } else null
  if (preset != null) {
    return if (!frameUrl.isNullOrBlank()) preset.copy(imageUrl = frameUrl) else preset
  }
  return StoreItem(
    id = frameId ?: "custom_frame",
    category = "FRAME",
    name = "Equipped Frame",
    durationText = "PERMANENT",
    durationDays = -1,
    coinPrice = 0L,
    imageUrl = frameUrl,
    presetStyle = frameId ?: "GOLDEN_CROWN"
  )
}

@Composable
fun AvatarFrameOverlay(
  size: Dp = 76.dp,
  frame: StoreItem? = null,
  frameId: String? = null,
  frameUrl: String? = null,
  profileImageUri: String? = null,
  avatarRes: Int = 0,
  contentScale: ContentScale = ContentScale.Crop,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val activeFrame = remember(frame, frameId, frameUrl) {
    frame ?: resolveStoreFrame(context, frameId, frameUrl)
  }

  val hasRealAvatarRes = remember(avatarRes) {
    if (avatarRes <= 0 || avatarRes == R.drawable.img_streamer_girl) {
      false
    } else {
      try {
        context.resources.getResourceName(avatarRes)
        true
      } catch (e: Throwable) {
        false
      }
    }
  }

  val infiniteTransition = rememberInfiniteTransition(label = "frame_glow")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.98f,
    targetValue = 1.03f,
    animationSpec = infiniteRepeatable(
      animation = tween(1400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulseScale"
  )

  // Extra padding to accommodate crown, wings, or floral accents outside the avatar bounds
  val frameContainerSize = size * 1.35f

  Box(
    modifier = modifier.size(frameContainerSize),
    contentAlignment = Alignment.Center
  ) {
    // 1. BASE AVATAR CIRCLE
    Box(
      modifier = Modifier
        .size(size)
        .clip(CircleShape)
        .background(
          Brush.verticalGradient(
            listOf(Color(0xFF1F0838), Color(0xFF0F0022))
          )
        ),
      contentAlignment = Alignment.Center
    ) {
      val resolvedModel = com.example.ui.storage.LocalImageStorage.resolveImageModel(
        context = context,
        candidateUri = profileImageUri,
        imageType = com.example.ui.storage.LocalImageStorage.ImageType.PROFILE,
        defaultFallbackRes = if (hasRealAvatarRes) avatarRes else com.aistudio.neonparty.nxvqlr.R.drawable.img_profile_max
      )
      AsyncImage(
        model = resolvedModel,
        contentDescription = "Avatar",
        modifier = Modifier.fillMaxSize(),
        contentScale = contentScale,
        error = painterResource(id = if (hasRealAvatarRes) avatarRes else com.aistudio.neonparty.nxvqlr.R.drawable.img_profile_max),
        fallback = painterResource(id = if (hasRealAvatarRes) avatarRes else com.aistudio.neonparty.nxvqlr.R.drawable.img_profile_max)
      )
    }

    // 2. EQUIPPED FRAME LAYER
    if (activeFrame != null) {
      if (!activeFrame.imageUrl.isNullOrBlank()) {
        // Dynamic image frame uploaded/configured by admin
        AsyncImage(
          model = activeFrame.imageUrl,
          contentDescription = activeFrame.name,
          modifier = Modifier
            .fillMaxSize()
            .scale(pulseScale),
          contentScale = ContentScale.Fit
        )
      } else {
        // High-end procedural vector frame
        Box(
          modifier = Modifier
            .fillMaxSize()
            .scale(pulseScale),
          contentAlignment = Alignment.Center
        ) {
          RenderPresetFrame(preset = activeFrame.presetStyle, avatarSize = size)
        }
      }
    } else {
      // Default subtle cyan neon border when no frame equipped
      Box(
        modifier = Modifier
          .size(size)
          .border(1.5.dp, Color(0xFF00E5FF).copy(alpha = 0.8f), CircleShape)
      )
    }
  }
}

/**
 * Dedicated renderer for procedural animated/vector avatar frames
 */
@Composable
fun RenderPresetFrame(
  preset: String,
  avatarSize: Dp
) {
  val upper = preset.uppercase()
  when {
    upper.startsWith("VIP_") || upper == "VIP_FRAME" -> VipRoyalFrame(preset = upper, avatarSize = avatarSize)
    upper == "GOLDEN_CROWN" -> GoldenCrownFrame(avatarSize = avatarSize)
    upper == "ANGEL_WINGS" -> AngelWingsFrame(avatarSize = avatarSize)
    upper == "SPRING_BLOSSOM" -> SpringBlossomFrame(avatarSize = avatarSize)
    upper == "STARRY_MOON" -> StarryMoonFrame(avatarSize = avatarSize)
    upper == "RUBY_ROSES" -> RubyRosesFrame(avatarSize = avatarSize)
    upper == "ICE_LOTUS" -> IceLotusFrame(avatarSize = avatarSize)
    upper == "HEART_TIARA" -> HeartTiaraFrame(avatarSize = avatarSize)
    upper == "CYBER_MATRIX" -> CyberMatrixFrame(avatarSize = avatarSize)
    else -> GoldenCrownFrame(avatarSize = avatarSize)
  }
}

// -----------------------------------------------------------------------------
// 1. ROYAL GOLDEN CROWN FRAME
// -----------------------------------------------------------------------------
@Composable
private fun GoldenCrownFrame(avatarSize: Dp) {
  Box(
    modifier = Modifier.size(avatarSize * 1.35f),
    contentAlignment = Alignment.Center
  ) {
    // Outer Ring Canvas
    Canvas(modifier = Modifier.fillMaxSize()) {
      val center = Offset(size.width / 2f, size.height / 2f)
      val radius = (avatarSize.toPx() / 2f) + 3.dp.toPx()

      // Golden metallic glow rim
      drawCircle(
        brush = Brush.sweepGradient(
          listOf(
            Color(0xFFFFD700),
            Color(0xFFFFA000),
            Color(0xFFFFF8E1),
            Color(0xFFFFB300),
            Color(0xFFFFD700)
          )
        ),
        radius = radius,
        center = center,
        style = Stroke(width = 3.5.dp.toPx())
      )

      // Inner thin rim
      drawCircle(
        color = Color(0xFFFFECB3),
        radius = radius - 4.dp.toPx(),
        center = center,
        style = Stroke(width = 1.2.dp.toPx())
      )

      // Sparkling diamonds on corners
      val dotCount = 8
      for (i in 0 until dotCount) {
        val angle = (i * (360f / dotCount)) * (Math.PI.toFloat() / 180f)
        val dotX = center.x + (radius + 2.dp.toPx()) * cos(angle)
        val dotY = center.y + (radius + 2.dp.toPx()) * sin(angle)
        drawCircle(
          color = if (i % 2 == 0) Color(0xFFFFD700) else Color.White,
          radius = 2.dp.toPx(),
          center = Offset(dotX, dotY)
        )
      }
    }

    // Top Crown Ornament (Vector Canvas)
    Canvas(
      modifier = Modifier
        .align(Alignment.TopCenter)
        .offset(y = (-4).dp)
        .size((avatarSize.value * 0.36f).dp)
    ) {
      val w = size.width
      val h = size.height
      val path = Path().apply {
        moveTo(w * 0.05f, h * 0.88f)
        lineTo(w * 0.95f, h * 0.88f)
        lineTo(w * 0.90f, h * 0.28f)
        lineTo(w * 0.65f, h * 0.58f)
        lineTo(w * 0.50f, h * 0.08f)
        lineTo(w * 0.35f, h * 0.58f)
        lineTo(w * 0.10f, h * 0.28f)
        close()
      }
      drawPath(path, Brush.verticalGradient(listOf(Color(0xFFFFE082), Color(0xFFFFB300), Color(0xFFFF8F00))))
      drawCircle(Color(0xFFFF1744), radius = w * 0.07f, center = Offset(w * 0.5f, h * 0.08f))
      drawCircle(Color(0xFF00E5FF), radius = w * 0.05f, center = Offset(w * 0.10f, h * 0.28f))
      drawCircle(Color(0xFF00E5FF), radius = w * 0.05f, center = Offset(w * 0.90f, h * 0.28f))
    }

    // Bottom Laurel / Ribbon Ornament (Vector Gold Emblem)
    Canvas(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .offset(y = 2.dp)
        .size((avatarSize.value * 0.26f).dp)
    ) {
      val cx = size.width / 2f
      val cy = size.height / 2f
      val r = size.width / 2f
      drawCircle(Color(0xFFFFD700), radius = r * 0.5f, center = Offset(cx, cy))
      drawCircle(Color(0xFFFFA000), radius = r * 0.3f, center = Offset(cx, cy))
    }
  }
}

// -----------------------------------------------------------------------------
// 2. ANGEL WINGS FRAME
// -----------------------------------------------------------------------------
@Composable
private fun AngelWingsFrame(avatarSize: Dp) {
  Box(
    modifier = Modifier.size(avatarSize * 1.38f),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val center = Offset(size.width / 2f, size.height / 2f)
      val radius = (avatarSize.toPx() / 2f) + 3.dp.toPx()

      // Gradient Pink-Cyan Ring
      drawCircle(
        brush = Brush.sweepGradient(
          listOf(
            Color(0xFF00E5FF),
            Color(0xFFFF007F),
            Color(0xFFB026FF),
            Color(0xFF00E5FF)
          )
        ),
        radius = radius,
        center = center,
        style = Stroke(width = 3.5.dp.toPx())
      )

      // Halo ring above
      val haloCenter = Offset(center.x, center.y - radius - 6.dp.toPx())
      drawOval(
        brush = Brush.linearGradient(listOf(Color(0xFFFFD700), Color(0xFFFFF176))),
        topLeft = Offset(haloCenter.x - 16.dp.toPx(), haloCenter.y - 4.dp.toPx()),
        size = Size(32.dp.toPx(), 8.dp.toPx()),
        style = Stroke(width = 2.dp.toPx())
      )
    }

    // Left Wing
    Box(
      modifier = Modifier
        .align(Alignment.CenterStart)
        .offset(x = (-6).dp)
    ) {
      Text(
        text = "🪽",
        fontSize = (avatarSize.value * 0.38f).sp
      )
    }

    // Right Wing
    Box(
      modifier = Modifier
        .align(Alignment.CenterEnd)
        .offset(x = 6.dp)
        .scale(scaleX = -1f, scaleY = 1f) // Mirrored wing
    ) {
      Text(
        text = "🪽",
        fontSize = (avatarSize.value * 0.38f).sp
      )
    }

    // Top Sparkle
    Box(
      modifier = Modifier
        .align(Alignment.TopCenter)
        .offset(y = (-6).dp)
    ) {
      Text(
        text = "✨",
        fontSize = (avatarSize.value * 0.22f).sp
      )
    }
  }
}

// -----------------------------------------------------------------------------
// 3. SPRING BLOSSOM FRAME
// -----------------------------------------------------------------------------
@Composable
private fun SpringBlossomFrame(avatarSize: Dp) {
  Box(
    modifier = Modifier.size(avatarSize * 1.35f),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val center = Offset(size.width / 2f, size.height / 2f)
      val radius = (avatarSize.toPx() / 2f) + 3.dp.toPx()

      // Pastel Pink & Emerald Flora Ring
      drawCircle(
        brush = Brush.sweepGradient(
          listOf(
            Color(0xFFFF80AB),
            Color(0xFF69F0AE),
            Color(0xFFFF4081),
            Color(0xFFB9F6CA),
            Color(0xFFFF80AB)
          )
        ),
        radius = radius,
        center = center,
        style = Stroke(width = 3.dp.toPx())
      )
    }

    // Top Blossom Cluster
    Box(
      modifier = Modifier
        .align(Alignment.TopCenter)
        .offset(y = (-6).dp)
    ) {
      Text(
        text = "🌸",
        fontSize = (avatarSize.value * 0.32f).sp
      )
    }

    // Bottom Leaves & Flower
    Box(
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .offset(x = (-2).dp, y = 2.dp)
    ) {
      Text(
        text = "🌺",
        fontSize = (avatarSize.value * 0.26f).sp
      )
    }

    Box(
      modifier = Modifier
        .align(Alignment.BottomStart)
        .offset(x = 2.dp, y = 2.dp)
    ) {
      Text(
        text = "🌿",
        fontSize = (avatarSize.value * 0.24f).sp
      )
    }
  }
}

// -----------------------------------------------------------------------------
// 4. STARRY MOON FRAME
// -----------------------------------------------------------------------------
@Composable
private fun StarryMoonFrame(avatarSize: Dp) {
  Box(
    modifier = Modifier.size(avatarSize * 1.35f),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val center = Offset(size.width / 2f, size.height / 2f)
      val radius = (avatarSize.toPx() / 2f) + 3.dp.toPx()

      drawCircle(
        brush = Brush.sweepGradient(
          listOf(
            Color(0xFFB388FF),
            Color(0xFFFFD700),
            Color(0xFF7C4DFF),
            Color(0xFFB388FF)
          )
        ),
        radius = radius,
        center = center,
        style = Stroke(width = 3.dp.toPx())
      )
    }

    // Crescent Moon on Top Right
    Box(
      modifier = Modifier
        .align(Alignment.TopEnd)
        .offset(x = 2.dp, y = (-4).dp)
    ) {
      Text(
        text = "🌙",
        fontSize = (avatarSize.value * 0.32f).sp
      )
    }

    // Hanging Stars
    Box(
      modifier = Modifier
        .align(Alignment.BottomStart)
        .offset(x = 2.dp, y = 2.dp)
    ) {
      Text(
        text = "⭐",
        fontSize = (avatarSize.value * 0.24f).sp
      )
    }

    Box(
      modifier = Modifier
        .align(Alignment.TopStart)
        .offset(x = 2.dp, y = (-2).dp)
    ) {
      Text(
        text = "✨",
        fontSize = (avatarSize.value * 0.22f).sp
      )
    }
  }
}

// -----------------------------------------------------------------------------
// 5. IMPERIAL RUBY ROSES FRAME
// -----------------------------------------------------------------------------
@Composable
private fun RubyRosesFrame(avatarSize: Dp) {
  Box(
    modifier = Modifier.size(avatarSize * 1.35f),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val center = Offset(size.width / 2f, size.height / 2f)
      val radius = (avatarSize.toPx() / 2f) + 3.dp.toPx()

      drawCircle(
        brush = Brush.sweepGradient(
          listOf(
            Color(0xFFFF1744),
            Color(0xFFFFD700),
            Color(0xFFD50000),
            Color(0xFFFF8A80),
            Color(0xFFFF1744)
          )
        ),
        radius = radius,
        center = center,
        style = Stroke(width = 3.5.dp.toPx())
      )
    }

    // Rose on Top Left
    Box(
      modifier = Modifier
        .align(Alignment.TopStart)
        .offset(x = (-2).dp, y = (-4).dp)
    ) {
      Text(
        text = "🌹",
        fontSize = (avatarSize.value * 0.30f).sp
      )
    }

    // Rose on Bottom Right
    Box(
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .offset(x = 2.dp, y = 2.dp)
    ) {
      Text(
        text = "🌹",
        fontSize = (avatarSize.value * 0.30f).sp
      )
    }

    // Ruby Gem in Bottom Center
    Box(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .offset(y = 2.dp)
    ) {
      Text(
        text = "💎",
        fontSize = (avatarSize.value * 0.20f).sp
      )
    }
  }
}

// -----------------------------------------------------------------------------
// 6. ICE CRYSTAL LOTUS FRAME
// -----------------------------------------------------------------------------
@Composable
private fun IceLotusFrame(avatarSize: Dp) {
  Box(
    modifier = Modifier.size(avatarSize * 1.35f),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val center = Offset(size.width / 2f, size.height / 2f)
      val radius = (avatarSize.toPx() / 2f) + 3.dp.toPx()

      drawCircle(
        brush = Brush.sweepGradient(
          listOf(
            Color(0xFF00E5FF),
            Color(0xFF80D8FF),
            Color(0xFFE0F7FA),
            Color(0xFF00B0FF),
            Color(0xFF00E5FF)
          )
        ),
        radius = radius,
        center = center,
        style = Stroke(width = 3.5.dp.toPx())
      )
    }

    // Lotus Flower on Top
    Box(
      modifier = Modifier
        .align(Alignment.TopCenter)
        .offset(y = (-6).dp)
    ) {
      Text(
        text = "🪷",
        fontSize = (avatarSize.value * 0.34f).sp
      )
    }

    // Snowflake at Bottom
    Box(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .offset(y = 4.dp)
    ) {
      Text(
        text = "❄️",
        fontSize = (avatarSize.value * 0.24f).sp
      )
    }
  }
}

// -----------------------------------------------------------------------------
// 7. GOLDEN HEART TIARA FRAME
// -----------------------------------------------------------------------------
@Composable
private fun HeartTiaraFrame(avatarSize: Dp) {
  Box(
    modifier = Modifier.size(avatarSize * 1.35f),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val center = Offset(size.width / 2f, size.height / 2f)
      val radius = (avatarSize.toPx() / 2f) + 3.dp.toPx()

      drawCircle(
        brush = Brush.sweepGradient(
          listOf(
            Color(0xFFFF4081),
            Color(0xFFFFD700),
            Color(0xFFFF80AB),
            Color(0xFFFFB300),
            Color(0xFFFF4081)
          )
        ),
        radius = radius,
        center = center,
        style = Stroke(width = 3.dp.toPx())
      )
    }

    // Tiara + Heart at Top
    Box(
      modifier = Modifier
        .align(Alignment.TopCenter)
        .offset(y = (-6).dp)
    ) {
      Text(
        text = "💖",
        fontSize = (avatarSize.value * 0.32f).sp
      )
    }

    // Pearls at Bottom
    Box(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .offset(y = 2.dp)
    ) {
      Text(
        text = "✨",
        fontSize = (avatarSize.value * 0.22f).sp
      )
    }
  }
}

// -----------------------------------------------------------------------------
// 8. CYBER MATRIX FRAME
// -----------------------------------------------------------------------------
@Composable
private fun CyberMatrixFrame(avatarSize: Dp) {
  Box(
    modifier = Modifier.size(avatarSize * 1.35f),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val center = Offset(size.width / 2f, size.height / 2f)
      val radius = (avatarSize.toPx() / 2f) + 4.dp.toPx()

      // High-tech segmented arcs
      drawArc(
        brush = Brush.sweepGradient(listOf(Color(0xFF00E5FF), Color(0xFF76FF03), Color(0xFF00E5FF))),
        startAngle = 45f,
        sweepAngle = 90f,
        useCenter = false,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = Size(radius * 2, radius * 2),
        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
      )

      drawArc(
        brush = Brush.sweepGradient(listOf(Color(0xFFFF007F), Color(0xFFB026FF), Color(0xFFFF007F))),
        startAngle = 225f,
        sweepAngle = 90f,
        useCenter = false,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = Size(radius * 2, radius * 2),
        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
      )
    }

    // Cyber HUD Accents
    Box(
      modifier = Modifier
        .align(Alignment.TopEnd)
        .offset(x = 2.dp, y = (-2).dp)
    ) {
      Text(
        text = "⚡",
        fontSize = (avatarSize.value * 0.26f).sp
      )
    }

    Box(
      modifier = Modifier
        .align(Alignment.BottomStart)
        .offset(x = (-2).dp, y = 2.dp)
    ) {
      Text(
        text = "💠",
        fontSize = (avatarSize.value * 0.22f).sp
      )
    }
  }
}

// -----------------------------------------------------------------------------
// 9. VIP ROYAL LAUREL & JEWELED FRAME (Matches Reference Image 20260915_103914)
// -----------------------------------------------------------------------------
@Composable
private fun VipRoyalFrame(preset: String, avatarSize: Dp) {
  // Extract level (e.g. VIP_3_MAJESTIC -> 3, VIP_1_IMPERIAL -> 1)
  val level = remember(preset) {
    val regex = "VIP_(\\d+)".toRegex()
    regex.find(preset)?.groupValues?.get(1)?.toIntOrNull() ?: 1
  }

  val gemColor = when (level) {
    1 -> Color(0xFFFFD700) // Imperial Gold
    2 -> Color(0xFF00E5FF) // Royal Cyan Diamond
    3 -> Color(0xFFFF0055) // Majestic Ruby
    4 -> Color(0xFF9D00FF) // Sovereign Amethyst
    5 -> Color(0xFF00FF88) // Monarch Emerald
    6 -> Color(0xFFFF3D00) // Celestial Solar Flame
    7 -> Color(0xFFFF00A0) // Overlord Magenta
    8 -> Color(0xFFFFEA00) // Emperor Gold
    9 -> Color(0xFF00F0FF) // Supreme Cosmic
    else -> Color(0xFFFF1744) // Godlike Crimson
  }

  Box(
    modifier = Modifier.size(avatarSize * 1.35f),
    contentAlignment = Alignment.Center
  ) {
    // Outer Laurel Wreath Ring & Medallion Glow Canvas
    Canvas(modifier = Modifier.fillMaxSize()) {
      val center = Offset(size.width / 2f, size.height / 2f)
      val radius = (avatarSize.toPx() / 2f) + 3.dp.toPx()

      // 1. Radiant outer aura
      drawCircle(
        brush = Brush.radialGradient(
          listOf(gemColor.copy(alpha = 0.35f), Color.Transparent),
          center = center,
          radius = radius * 1.25f
        ),
        radius = radius * 1.25f,
        center = center
      )

      // 2. Thick Golden Laurel Rim
      drawCircle(
        brush = Brush.sweepGradient(
          listOf(
            Color(0xFFFFD700),
            Color(0xFFFFA000),
            Color(0xFFFFF8E1),
            Color(0xFFFFB300),
            Color(0xFFFF8F00),
            Color(0xFFFFD700)
          )
        ),
        radius = radius,
        center = center,
        style = Stroke(width = 4.dp.toPx())
      )

      // 3. Inner polished accent ring
      drawCircle(
        color = Color(0xFFFFECB3),
        radius = radius - 3.5.dp.toPx(),
        center = center,
        style = Stroke(width = 1.2.dp.toPx())
      )

      // 4. Laurel leaf pairs along left and right flanks
      val leafPairs = 7
      for (i in 1..leafPairs) {
        // Left side leaves (from 110 deg to 250 deg)
        val angleDegLeft = 110f + (i * (140f / (leafPairs + 1)))
        val angleRadLeft = angleDegLeft * (Math.PI.toFloat() / 180f)
        val lx = center.x + (radius + 2.5.dp.toPx()) * cos(angleRadLeft)
        val ly = center.y + (radius + 2.5.dp.toPx()) * sin(angleRadLeft)
        drawCircle(
          brush = Brush.radialGradient(listOf(Color(0xFFFFE082), Color(0xFFFFB300))),
          radius = 3.dp.toPx(),
          center = Offset(lx, ly)
        )

        // Right side leaves (from 70 deg to -70 deg)
        val angleDegRight = 70f - (i * (140f / (leafPairs + 1)))
        val angleRadRight = angleDegRight * (Math.PI.toFloat() / 180f)
        val rx = center.x + (radius + 2.5.dp.toPx()) * cos(angleRadRight)
        val ry = center.y + (radius + 2.5.dp.toPx()) * sin(angleRadRight)
        drawCircle(
          brush = Brush.radialGradient(listOf(Color(0xFFFFE082), Color(0xFFFFB300))),
          radius = 3.dp.toPx(),
          center = Offset(rx, ry)
        )
      }
    }

    // Top Imperial Crown with Ruby/Gem Apex (Vector Canvas)
    Canvas(
      modifier = Modifier
        .align(Alignment.TopCenter)
        .offset(y = (-4).dp)
        .size((avatarSize.value * 0.38f).dp)
    ) {
      val w = size.width
      val h = size.height
      val crownPath = Path().apply {
        moveTo(w * 0.05f, h * 0.88f)
        lineTo(w * 0.95f, h * 0.88f)
        lineTo(w * 0.90f, h * 0.28f)
        lineTo(w * 0.65f, h * 0.58f)
        lineTo(w * 0.50f, h * 0.06f)
        lineTo(w * 0.35f, h * 0.58f)
        lineTo(w * 0.10f, h * 0.28f)
        close()
      }
      drawPath(
        crownPath,
        Brush.verticalGradient(
          listOf(Color(0xFFFFE082), Color(0xFFFFD700), Color(0xFFFF8F00))
        )
      )
      // Imperial Jewels
      drawCircle(Color(0xFFFF1744), radius = w * 0.08f, center = Offset(w * 0.5f, h * 0.06f))
      drawCircle(Color(0xFF00E5FF), radius = w * 0.05f, center = Offset(w * 0.10f, h * 0.28f))
      drawCircle(Color(0xFF00E5FF), radius = w * 0.05f, center = Offset(w * 0.90f, h * 0.28f))
    }

    // Bottom-Left Royal Star Crest Medallion (Vector Canvas, No Emoji)
    Canvas(
      modifier = Modifier
        .align(Alignment.BottomStart)
        .offset(x = (-1).dp, y = (-2).dp)
        .size((avatarSize.value * 0.34f).dp)
    ) {
      val cx = size.width / 2f
      val cy = size.height / 2f
      val r = size.width / 2f

      // Outer gold rim
      drawCircle(
        Brush.radialGradient(listOf(Color(0xFFFFD700), Color(0xFFFFA000), Color(0xFF8D6E63))),
        radius = r,
        center = Offset(cx, cy)
      )
      // Inner royal metallic disc
      drawCircle(
        Brush.radialGradient(listOf(Color(0xFF4A0072), Color(0xFF1F0038))),
        radius = r * 0.76f,
        center = Offset(cx, cy)
      )
      // 5-Pointed Star
      val starPath = Path().apply {
        val points = 5
        val outerR = r * 0.60f
        val innerR = r * 0.28f
        for (step in 0 until points * 2) {
          val radiusVal = if (step % 2 == 0) outerR else innerR
          val angle = (step * (360f / (points * 2)) - 90f) * (Math.PI.toFloat() / 180f)
          val px = cx + radiusVal * kotlin.math.cos(angle)
          val py = cy + radiusVal * kotlin.math.sin(angle)
          if (step == 0) moveTo(px, py) else lineTo(px, py)
        }
        close()
      }
      drawPath(starPath, Brush.linearGradient(listOf(Color(0xFFFFE082), Color(0xFFFFD700))))
    }

    // Bottom Ornate "VIP X" Ribbon Banner
    Box(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .offset(y = 6.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(
          Brush.horizontalGradient(
            listOf(Color(0xFF880E4F), Color(0xFFC2185B), Color(0xFF880E4F))
          )
        )
        .border(1.2.dp, Brush.horizontalGradient(listOf(Color(0xFFFFD700), Color(0xFFFFA000))), RoundedCornerShape(8.dp))
        .padding(horizontal = 6.dp, vertical = 1.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "VIP $level",
        color = Color(0xFFFFD700),
        fontSize = (avatarSize.value * 0.14f).coerceAtLeast(8f).sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 0.5.sp
      )
    }
  }
}

