package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPinkBright

// --- Custom Vector Composables matching the exact Screenshot Icons ---

/**
 * Compass icon in purple circle with 4-point star rose
 */
@Composable
fun CompassIcon(modifier: Modifier = Modifier) {
  Canvas(modifier = modifier.size(18.dp)) {
    val r = size.minDimension / 2f
    val center = Offset(size.width / 2f, size.height / 2f)

    // Outer circle
    drawCircle(
      color = Color(0xFF6B00B8),
      radius = r,
      center = center
    )
    drawCircle(
      color = Color.White.copy(alpha = 0.8f),
      radius = r,
      center = center,
      style = Stroke(width = 1.2f)
    )

    // Compass 4-point star
    val p = Path().apply {
      moveTo(center.x, center.y - r * 0.72f) // North
      lineTo(center.x + r * 0.18f, center.y - r * 0.18f)
      lineTo(center.x + r * 0.72f, center.y) // East
      lineTo(center.x + r * 0.18f, center.y + r * 0.18f)
      lineTo(center.x, center.y + r * 0.72f) // South
      lineTo(center.x - r * 0.18f, center.y + r * 0.18f)
      lineTo(center.x - r * 0.72f, center.y) // West
      lineTo(center.x - r * 0.18f, center.y - r * 0.18f)
      close()
    }
    drawPath(path = p, color = Color.White)

    // Center circle
    drawCircle(color = Color(0xFF6B00B8), radius = r * 0.22f, center = center)
    drawCircle(color = Color.White, radius = r * 0.1f, center = center)
  }
}

/**
 * Flame icon with vibrant Cyan-to-Magenta gradient
 */
@Composable
fun GradientFlameIcon(modifier: Modifier = Modifier) {
  Canvas(modifier = modifier.size(18.dp)) {
    val w = size.width
    val h = size.height

    val flamePath = Path().apply {
      moveTo(w * 0.5f, h * 0.05f)
      cubicTo(w * 0.72f, h * 0.25f, w * 0.95f, h * 0.55f, w * 0.75f, h * 0.85f)
      cubicTo(w * 0.62f, h * 1.0f, w * 0.38f, h * 1.0f, w * 0.25f, h * 0.85f)
      cubicTo(w * 0.05f, h * 0.55f, w * 0.28f, h * 0.25f, w * 0.5f, h * 0.05f)
      close()
    }
    drawPath(
      path = flamePath,
      brush = Brush.verticalGradient(
        colors = listOf(Color(0xFF00F0FF), Color(0xFF0088FF), Color(0xFFFF007F))
      )
    )

    // Inner flame tongue
    val innerPath = Path().apply {
      moveTo(w * 0.5f, h * 0.45f)
      cubicTo(w * 0.65f, h * 0.6f, w * 0.68f, h * 0.78f, w * 0.55f, h * 0.9f)
      cubicTo(w * 0.45f, h * 0.9f, w * 0.35f, h * 0.78f, w * 0.38f, h * 0.6f)
      close()
    }
    drawPath(
      path = innerPath,
      brush = Brush.verticalGradient(
        colors = listOf(Color(0xFFE0FFFF), Color(0xFF00F0FF))
      )
    )
  }
}

/**
 * PK badge with 3-pointed crown on top
 */
@Composable
fun PkCrownBadgeIcon(modifier: Modifier = Modifier) {
  Canvas(modifier = modifier.size(20.dp)) {
    val w = size.width
    val h = size.height

    // 3-point crown on top
    val crownPath = Path().apply {
      moveTo(w * 0.2f, h * 0.35f)
      lineTo(w * 0.1f, h * 0.12f) // left point
      lineTo(w * 0.35f, h * 0.24f)
      lineTo(w * 0.5f, h * 0.04f) // center tall point
      lineTo(w * 0.65f, h * 0.24f)
      lineTo(w * 0.9f, h * 0.12f) // right point
      lineTo(w * 0.8f, h * 0.35f)
      close()
    }
    drawPath(
      path = crownPath,
      color = Color.White
    )

    // Badge frame
    drawRoundRect(
      color = Color(0xFFB000FF),
      topLeft = Offset(w * 0.05f, h * 0.35f),
      size = Size(w * 0.9f, h * 0.6f),
      cornerRadius = CornerRadius(4f, 4f)
    )
    drawRoundRect(
      color = Color.White,
      topLeft = Offset(w * 0.05f, h * 0.35f),
      size = Size(w * 0.9f, h * 0.6f),
      cornerRadius = CornerRadius(4f, 4f),
      style = Stroke(width = 1.2f)
    )
  }
}

/**
 * User icon with small + badge
 */
@Composable
fun FollowUserPlusIcon(modifier: Modifier = Modifier) {
  Canvas(modifier = modifier.size(18.dp)) {
    val w = size.width
    val h = size.height

    // Head
    drawCircle(
      color = Color.White,
      radius = w * 0.22f,
      center = Offset(w * 0.4f, h * 0.3f)
    )

    // Shoulders
    val bodyPath = Path().apply {
      moveTo(w * 0.1f, h * 0.9f)
      cubicTo(w * 0.1f, h * 0.62f, w * 0.7f, h * 0.62f, w * 0.7f, h * 0.9f)
      close()
    }
    drawPath(path = bodyPath, color = Color.White)

    // Plus sign on the right
    val plusCx = w * 0.78f
    val plusCy = h * 0.65f
    val s = w * 0.16f
    drawLine(
      color = Color(0xFF00F0FF),
      start = Offset(plusCx - s, plusCy),
      end = Offset(plusCx + s, plusCy),
      strokeWidth = 2.5f,
      cap = StrokeCap.Round
    )
    drawLine(
      color = Color(0xFF00F0FF),
      start = Offset(plusCx, plusCy - s),
      end = Offset(plusCx, plusCy + s),
      strokeWidth = 2.5f,
      cap = StrokeCap.Round
    )
  }
}

/**
 * 4 rounded square tiles (Grid view)
 */
@Composable
fun GridTilesIcon(modifier: Modifier = Modifier) {
  Canvas(modifier = modifier.size(16.dp)) {
    val s = size.width * 0.42f
    val gap = size.width * 0.16f
    val r = 2.5f

    // Top-left
    drawRoundRect(
      color = Color.White,
      topLeft = Offset(0f, 0f),
      size = Size(s, s),
      cornerRadius = CornerRadius(r, r)
    )
    // Top-right
    drawRoundRect(
      color = Color.White,
      topLeft = Offset(s + gap, 0f),
      size = Size(s, s),
      cornerRadius = CornerRadius(r, r)
    )
    // Bottom-left
    drawRoundRect(
      color = Color.White,
      topLeft = Offset(0f, s + gap),
      size = Size(s, s),
      cornerRadius = CornerRadius(r, r)
    )
    // Bottom-right
    drawRoundRect(
      color = Color.White,
      topLeft = Offset(s + gap, s + gap),
      size = Size(s, s),
      cornerRadius = CornerRadius(r, r)
    )
  }
}

/**
 * 5-pointed star icon
 */
@Composable
fun StarIcon(
  modifier: Modifier = Modifier,
  tint: Color = Color(0xFFFF4081)
) {
  Canvas(modifier = modifier.size(15.dp)) {
    val rOut = size.minDimension / 2f
    val rIn = rOut * 0.45f
    val cx = size.width / 2f
    val cy = size.height / 2f

    val starPath = Path()
    for (i in 0 until 10) {
      val r = if (i % 2 == 0) rOut else rIn
      val angle = Math.toRadians((i * 36 - 90).toDouble())
      val x = cx + (r * Math.cos(angle)).toFloat()
      val y = cy + (r * Math.sin(angle)).toFloat()
      if (i == 0) starPath.moveTo(x, y) else starPath.lineTo(x, y)
    }
    starPath.close()
    drawPath(path = starPath, color = tint)
  }
}

/**
 * Wireframe Globe with latitude & longitude lines
 */
@Composable
fun GlobeWireframeIcon(modifier: Modifier = Modifier) {
  Canvas(modifier = modifier.size(16.dp)) {
    val r = size.minDimension / 2f
    val center = Offset(size.width / 2f, size.height / 2f)

    // Outer circle
    drawCircle(
      color = Color.White,
      radius = r - 1f,
      center = center,
      style = Stroke(width = 1.4f)
    )

    // Equator line
    drawLine(
      color = Color.White,
      start = Offset(center.x - r + 1f, center.y),
      end = Offset(center.x + r - 1f, center.y),
      strokeWidth = 1.2f
    )

    // Central meridian line
    drawLine(
      color = Color.White,
      start = Offset(center.x, center.y - r + 1f),
      end = Offset(center.x, center.y + r - 1f),
      strokeWidth = 1.2f
    )

    // Elliptical longitude loop
    drawOval(
      color = Color.White,
      topLeft = Offset(center.x - r * 0.5f, center.y - r + 1f),
      size = Size(r, (r - 1f) * 2f),
      style = Stroke(width = 1.2f)
    )
  }
}

// --- Bottom Navigation Vector Icons ---

/**
 * TV / Play Monitor Icon for LIVE tab
 */
@Composable
fun TvPlayIcon(
  modifier: Modifier = Modifier,
  tint: Color = Color.White
) {
  Canvas(modifier = modifier.size(24.dp)) {
    val w = size.width
    val h = size.height

    // TV Rounded Screen
    val screenTop = h * 0.12f
    val screenHeight = h * 0.72f
    drawRoundRect(
      color = tint,
      topLeft = Offset(w * 0.1f, screenTop),
      size = Size(w * 0.8f, screenHeight),
      cornerRadius = CornerRadius(5f, 5f),
      style = Stroke(width = 2.2f)
    )

    // Play triangle in the center
    val triPath = Path().apply {
      moveTo(w * 0.42f, screenTop + screenHeight * 0.28f)
      lineTo(w * 0.65f, screenTop + screenHeight * 0.5f)
      lineTo(w * 0.42f, screenTop + screenHeight * 0.72f)
      close()
    }
    drawPath(path = triPath, color = tint)

    // Antenna legs on top
    drawLine(
      color = tint,
      start = Offset(w * 0.5f, screenTop),
      end = Offset(w * 0.32f, 0f),
      strokeWidth = 1.8f,
      cap = StrokeCap.Round
    )
    drawLine(
      color = tint,
      start = Offset(w * 0.5f, screenTop),
      end = Offset(w * 0.68f, 0f),
      strokeWidth = 1.8f,
      cap = StrokeCap.Round
    )
  }
}

/**
 * Festive Party Popper Cone with colorful confetti explosion
 */
@Composable
fun PartyPopperIcon(modifier: Modifier = Modifier) {
  Canvas(modifier = modifier.size(24.dp)) {
    val w = size.width
    val h = size.height

    // Party horn cone pointing upper-right
    val conePath = Path().apply {
      moveTo(w * 0.15f, h * 0.85f) // tip at bottom left
      lineTo(w * 0.55f, h * 0.55f)
      lineTo(w * 0.35f, h * 0.35f)
      close()
    }
    drawPath(
      path = conePath,
      brush = Brush.linearGradient(
        colors = listOf(Color(0xFFFFB300), Color(0xFFFF1744))
      )
    )

    // Confetti ribbons bursting out
    val ribbon1 = Path().apply {
      moveTo(w * 0.48f, h * 0.45f)
      cubicTo(w * 0.6f, h * 0.28f, w * 0.75f, h * 0.4f, w * 0.85f, h * 0.2f)
    }
    drawPath(path = ribbon1, color = Color(0xFF00E5FF), style = Stroke(width = 2.4f, cap = StrokeCap.Round))

    val ribbon2 = Path().apply {
      moveTo(w * 0.4f, h * 0.38f)
      cubicTo(w * 0.48f, h * 0.18f, w * 0.65f, h * 0.18f, w * 0.65f, h * 0.05f)
    }
    drawPath(path = ribbon2, color = Color(0xFFFF4081), style = Stroke(width = 2.2f, cap = StrokeCap.Round))

    // Sparkle dots
    drawCircle(color = Color(0xFFFFD600), radius = 2f, center = Offset(w * 0.78f, h * 0.35f))
    drawCircle(color = Color(0xFF00E676), radius = 1.8f, center = Offset(w * 0.88f, h * 0.12f))
    drawCircle(color = Color(0xFFFF3D00), radius = 2.2f, center = Offset(w * 0.52f, h * 0.12f))
  }
}

/**
 * Saturn-like ringed planet for DISCOVER tab
 */
@Composable
fun DiscoverPlanetIcon(modifier: Modifier = Modifier) {
  Canvas(modifier = modifier.size(24.dp)) {
    val w = size.width
    val h = size.height
    val center = Offset(w * 0.5f, h * 0.5f)

    // Planet sphere
    drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(Color(0xFF40C4FF), Color(0xFF0288D1), Color(0xFF01579B)),
        center = Offset(center.x - w * 0.1f, center.y - h * 0.1f),
        radius = w * 0.35f
      ),
      radius = w * 0.28f,
      center = center
    )

    // Tilted ring looping around
    val ringPath = Path().apply {
      addOval(
        androidx.compose.ui.geometry.Rect(
          center.x - w * 0.46f,
          center.y - h * 0.18f,
          center.x + w * 0.46f,
          center.y + h * 0.18f
        )
      )
    }
    drawOval(
      brush = Brush.horizontalGradient(
        colors = listOf(Color(0xFF00E5FF), Color(0xFFE1F5FE), Color(0xFF00E5FF))
      ),
      topLeft = Offset(center.x - w * 0.46f, center.y - h * 0.16f),
      size = Size(w * 0.92f, h * 0.32f),
      style = Stroke(width = 2.2f)
    )

    // Star sparkles
    drawCircle(color = Color(0xFF00E5FF), radius = 1.6f, center = Offset(w * 0.18f, h * 0.28f))
    drawCircle(color = Color(0xFF00E5FF), radius = 1.6f, center = Offset(w * 0.85f, h * 0.72f))
  }
}

/**
 * Magenta Smiling Chat Bubble for CHAT tab
 */
@Composable
fun SmileyChatIcon(
  modifier: Modifier = Modifier,
  hasNotification: Boolean = false
) {
  Canvas(modifier = modifier.size(24.dp)) {
    val w = size.width
    val h = size.height

    // Rounded square chat body
    drawRoundRect(
      brush = Brush.linearGradient(
        colors = listOf(Color(0xFFFF1493), Color(0xFFFF007F))
      ),
      topLeft = Offset(w * 0.1f, h * 0.1f),
      size = Size(w * 0.8f, h * 0.8f),
      cornerRadius = CornerRadius(10f, 10f)
    )

    // Smiley eyes
    drawCircle(color = Color.White, radius = 1.6f, center = Offset(w * 0.38f, h * 0.42f))
    drawCircle(color = Color.White, radius = 1.6f, center = Offset(w * 0.62f, h * 0.42f))

    // Smiling mouth arc
    val smilePath = Path().apply {
      moveTo(w * 0.34f, h * 0.58f)
      cubicTo(w * 0.42f, h * 0.72f, w * 0.58f, h * 0.72f, w * 0.66f, h * 0.58f)
    }
    drawPath(
      path = smilePath,
      color = Color.White,
      style = Stroke(width = 1.8f, cap = StrokeCap.Round)
    )

    // Red notification dot
    if (hasNotification) {
      drawCircle(
        color = Color(0xFFFF1744),
        radius = 3.5f,
        center = Offset(w * 0.86f, h * 0.16f)
      )
      drawCircle(
        color = Color.White,
        radius = 3.5f,
        center = Offset(w * 0.86f, h * 0.16f),
        style = Stroke(width = 1f)
      )
    }
  }
}

/**
 * User Silhouette Avatar for ME tab
 */
@Composable
fun ProfileMeIcon(modifier: Modifier = Modifier) {
  Canvas(modifier = modifier.size(24.dp)) {
    val w = size.width
    val h = size.height

    // Head circle
    drawCircle(
      brush = Brush.verticalGradient(
        colors = listOf(Color(0xFF2979FF), Color(0xFF1565C0))
      ),
      radius = w * 0.22f,
      center = Offset(w * 0.5f, h * 0.34f)
    )

    // Shoulders
    val bodyPath = Path().apply {
      moveTo(w * 0.18f, h * 0.9f)
      cubicTo(w * 0.22f, h * 0.64f, w * 0.78f, h * 0.64f, w * 0.82f, h * 0.9f)
      close()
    }
    drawPath(
      path = bodyPath,
      brush = Brush.verticalGradient(
        colors = listOf(Color(0xFF2979FF), Color(0xFF1565C0))
      )
    )
  }
}

// --- Top Navigation & Filter Bar Components ---

/**
 * Top pill filters: Explore, New, PK, Follow
 */
@Composable
fun NeonTopPill(
  text: String,
  modifier: Modifier = Modifier,
  icon: ImageVector? = null,
  iconContent: (@Composable () -> Unit)? = null,
  isSelected: Boolean = false,
  customBorderBrush: Brush? = null,
  borderBrush: Brush? = null,
  onClick: () -> Unit = {}
) {
  val resolvedBorderBrush = borderBrush ?: customBorderBrush ?: if (isSelected) {
    Brush.horizontalGradient(listOf(Color(0xFF00F0FF), Color(0xFFFF007F)))
  } else {
    Brush.horizontalGradient(listOf(Color(0xFF9D00FF), Color(0xFF520099)))
  }

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(20.dp))
      .border(width = 1.6.dp, brush = resolvedBorderBrush, shape = RoundedCornerShape(20.dp))
      .background(Color(0xEE16002B))
      .clickable(onClick = onClick)
      .padding(horizontal = 10.dp, vertical = 6.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      if (iconContent != null) {
        iconContent()
        Spacer(modifier = Modifier.width(5.dp))
      } else if (icon != null) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = if (isSelected) NeonCyan else Color.White,
          modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
      }
      Text(
        text = text,
        color = Color.White,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold
      )
    }
  }
}

/**
 * Secondary row filters: All, Popular, All Countries, Search, Trophy
 */
@Composable
fun NeonSubFilterPill(
  text: String,
  modifier: Modifier = Modifier,
  icon: ImageVector? = null,
  iconContent: (@Composable () -> Unit)? = null,
  trailingIcon: ImageVector? = null,
  trailingContent: (@Composable () -> Unit)? = null,
  isSelected: Boolean = false,
  onClick: () -> Unit = {}
) {
  val backgroundBrush = if (isSelected) {
    Brush.horizontalGradient(
      listOf(Color(0xFF8B00F2), Color(0xFFA800FF), Color(0xFF7000E0))
    )
  } else {
    Brush.verticalGradient(
      listOf(Color(0xFF22003D), Color(0xFF140024))
    )
  }

  val borderBrush = if (isSelected) {
    Brush.horizontalGradient(listOf(Color(0xFFFF00A0), Color(0xFFB000FF)))
  } else {
    Brush.horizontalGradient(listOf(Color(0xFF4D1480), Color(0xFF7A00CC)))
  }

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(18.dp))
      .border(
        width = if (isSelected) 1.6.dp else 1.2.dp,
        brush = borderBrush,
        shape = RoundedCornerShape(18.dp)
      )
      .background(backgroundBrush)
      .clickable(onClick = onClick)
      .padding(horizontal = 11.dp, vertical = 6.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      if (iconContent != null) {
        iconContent()
        Spacer(modifier = Modifier.width(5.dp))
      } else if (icon != null) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = if (isSelected) Color(0xFFFFD600) else Color(0xFFB39DDB),
          modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
      }
      Text(
        text = text,
        color = Color.White,
        fontSize = 12.sp,
        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold
      )
      if (trailingContent != null) {
        Spacer(modifier = Modifier.width(4.dp))
        trailingContent()
      } else if (trailingIcon != null) {
        Spacer(modifier = Modifier.width(2.dp))
        Icon(
          imageVector = trailingIcon,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(12.dp)
        )
      }
    }
  }
}

/**
 * Equalizer sound visualizer bars [ılılı 12]
 */
@Composable
fun EqualizerVisualizer(
  modifier: Modifier = Modifier,
  count: Int = 12
) {
  val infiniteTransition = rememberInfiniteTransition(label = "equalizer")
  val bar1 by infiniteTransition.animateFloat(
    initialValue = 0.3f, targetValue = 1f,
    animationSpec = infiniteRepeatable(tween(350, easing = FastOutSlowInEasing), RepeatMode.Reverse),
    label = "bar1"
  )
  val bar2 by infiniteTransition.animateFloat(
    initialValue = 0.8f, targetValue = 0.2f,
    animationSpec = infiniteRepeatable(tween(420, easing = FastOutSlowInEasing), RepeatMode.Reverse),
    label = "bar2"
  )
  val bar3 by infiniteTransition.animateFloat(
    initialValue = 0.2f, targetValue = 0.9f,
    animationSpec = infiniteRepeatable(tween(280, easing = FastOutSlowInEasing), RepeatMode.Reverse),
    label = "bar3"
  )
  val bar4 by infiniteTransition.animateFloat(
    initialValue = 0.6f, targetValue = 0.4f,
    animationSpec = infiniteRepeatable(tween(380, easing = FastOutSlowInEasing), RepeatMode.Reverse),
    label = "bar4"
  )

  Row(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(Color(0xFF1B0133))
      .border(
        width = 1.dp,
        brush = Brush.horizontalGradient(listOf(NeonPink, NeonCyan)),
        shape = RoundedCornerShape(12.dp)
      )
      .padding(horizontal = 7.dp, vertical = 3.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(
      horizontalArrangement = Arrangement.spacedBy(2.dp),
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.height(13.dp)
    ) {
      Box(
        modifier = Modifier
          .width(2.5.dp)
          .height(13.dp * bar1)
          .background(NeonPink, RoundedCornerShape(1.dp))
      )
      Box(
        modifier = Modifier
          .width(2.5.dp)
          .height(13.dp * bar2)
          .background(NeonCyan, RoundedCornerShape(1.dp))
      )
      Box(
        modifier = Modifier
          .width(2.5.dp)
          .height(13.dp * bar3)
          .background(Color(0xFFD279FF), RoundedCornerShape(1.dp))
      )
      Box(
        modifier = Modifier
          .width(2.5.dp)
          .height(13.dp * bar4)
          .background(NeonPink, RoundedCornerShape(1.dp))
      )
    }
    Spacer(modifier = Modifier.width(4.dp))
    Text(
      text = "$count",
      color = Color.White,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold
    )
  }
}

/**
 * Circular neon button (Search with Cyan glow, Trophy with Magenta glow)
 */
@Composable
fun NeonCircleIconButton(
  icon: ImageVector,
  contentDescription: String,
  ringColor: Color = NeonCyan,
  modifier: Modifier = Modifier,
  onClick: () -> Unit = {}
) {
  Box(
    modifier = modifier
      .size(34.dp)
      .shadow(
        elevation = 6.dp,
        shape = CircleShape,
        ambientColor = ringColor,
        spotColor = ringColor
      )
      .clip(CircleShape)
      .border(
        width = 1.8.dp,
        brush = Brush.sweepGradient(
          listOf(ringColor, Color.White, ringColor)
        ),
        shape = CircleShape
      )
      .background(Color(0xFF140029))
      .clickable(onClick = onClick),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = icon,
      contentDescription = contentDescription,
      tint = Color.White,
      modifier = Modifier.size(17.dp)
    )
  }
}

/**
 * Top-left "LIVE" pill badge inside live card: 🎙 LIVE
 */
@Composable
fun NeonLiveBadge(
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .shadow(elevation = 6.dp, shape = RoundedCornerShape(12.dp), ambientColor = Color(0xFFFF007F))
      .clip(RoundedCornerShape(12.dp))
      .background(
        Brush.horizontalGradient(
          listOf(Color(0xFFFF0055), Color(0xFF990044))
        )
      )
      .border(0.8.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
      .padding(horizontal = 7.dp, vertical = 3.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = Icons.Filled.Mic,
      contentDescription = null,
      tint = Color.White,
      modifier = Modifier.size(11.dp)
    )
    Spacer(modifier = Modifier.width(3.dp))
    Text(
      text = "LIVE",
      color = Color.White,
      fontSize = 10.sp,
      fontWeight = FontWeight.Black,
      letterSpacing = 0.5.sp
    )
  }
}

/**
 * Neon Banner Card:
 * "WELCOME EVERYBODY" (Extruded 3D white text with intense hot pink glow)
 * "এই অ্যাপটি বিক্রি করা হবে"
 * "WhatsApp+8801818-022519"
 */
@Composable
fun NeonWelcomeBanner(
  modifier: Modifier = Modifier,
  onClickWhatsApp: () -> Unit = {}
) {
  val infiniteTransition = rememberInfiniteTransition(label = "banner_glow")
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.7f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(2200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_alpha"
  )

  Box(
    modifier = modifier
      .fillMaxWidth()
      .shadow(
        elevation = 20.dp,
        shape = RoundedCornerShape(20.dp),
        ambientColor = NeonCyan,
        spotColor = NeonPink
      )
      .clip(RoundedCornerShape(20.dp))
      .border(
        width = 2.dp,
        brush = Brush.horizontalGradient(
          listOf(
            NeonCyan,
            Color(0xFFFF00A0),
            Color(0xFFFFD700),
            Color(0xFF7A00FF),
            NeonCyan
          )
        ),
        shape = RoundedCornerShape(20.dp)
      )
      .background(
        Brush.verticalGradient(
          listOf(
            Color(0xFF0C002B),
            Color(0xFF19003B),
            Color(0xFF0A001F)
          )
        )
      )
  ) {
    // Holographic background particles & laser streak accents
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height

      // Dual holographic streaks
      val streakPath = Path().apply {
        moveTo(w * 0.70f, 0f)
        lineTo(w * 0.92f, 0f)
        lineTo(w * 0.30f, h)
        lineTo(w * 0.08f, h)
        close()
      }
      drawPath(
        path = streakPath,
        brush = Brush.linearGradient(
          colors = listOf(
            Color(0x33FF00A0),
            Color(0x66FF007F),
            Color(0x10FF00A0)
          )
        )
      )

      val cyanStreak = Path().apply {
        moveTo(w * 0.20f, 0f)
        lineTo(w * 0.35f, 0f)
        lineTo(w * 0.05f, h)
        lineTo(0f, h)
        close()
      }
      drawPath(
        path = cyanStreak,
        brush = Brush.linearGradient(
          colors = listOf(
            Color(0x2200F0FF),
            Color(0x4400E5FF),
            Color(0x0500F0FF)
          )
        )
      )
    }

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 16.dp, horizontal = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // Top Glowing Tag: GLOBAL TALENT & RECRUITMENT
      Row(
        modifier = Modifier
          .clip(RoundedCornerShape(10.dp))
          .background(Color(0x40FF007F))
          .border(0.8.dp, Color(0x80FF007F), RoundedCornerShape(10.dp))
          .padding(horizontal = 12.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "GLOBAL TALENT & AGENCY HUB",
          color = Color(0xFFFF80AB),
          fontSize = 10.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 1.sp
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Exact Main Heading: "Welcome everybody for Priya Live" (Requirement 11)
      Box(contentAlignment = Alignment.Center) {
        // Glowing shadow layer
        Text(
          text = "Welcome everybody for Priya Live",
          color = Color(0xFFFF007F).copy(alpha = pulseAlpha),
          fontSize = 19.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 0.5.sp,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(top = 2.dp, start = 2.dp)
        )
        // Foreground text
        Text(
          text = "Welcome everybody for Priya Live",
          color = Color.White,
          fontSize = 19.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 0.5.sp,
          textAlign = TextAlign.Center
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Clean, professional arrangement of approximately 20 country flags (Requirement 11)
      val flagsRow1 = listOf("🇺🇸", "🇬🇧", "🇮🇳", "🇧🇩", "🇵🇰", "🇦🇪", "🇸🇦", "🇨🇦", "🇦🇺", "🇩🇪")
      val flagsRow2 = listOf("🇫🇷", "🇮🇹", "🇪🇸", "🇧🇷", "🇯🇵", "🇰🇷", "🇮🇩", "🇲🇾", "🇹🇷", "🇪🇬")

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(Color(0x55000000))
          .border(0.8.dp, Color(0x3300E5FF), RoundedCornerShape(12.dp))
          .padding(vertical = 6.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceEvenly,
          verticalAlignment = Alignment.CenterVertically
        ) {
          flagsRow1.forEach { flag ->
            Text(text = flag, fontSize = 16.sp)
          }
        }
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceEvenly,
          verticalAlignment = Alignment.CenterVertically
        ) {
          flagsRow2.forEach { flag ->
            Text(text = flag, fontSize = 16.sp)
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // "MEGA MANAGER RECRUITMENT" (Requirement 11 & 12)
      Row(
        modifier = Modifier
          .clip(RoundedCornerShape(14.dp))
          .background(
            Brush.horizontalGradient(
              listOf(Color(0xFF28004D), Color(0xFF4A0072), Color(0xFF28004D))
            )
          )
          .border(
            width = 1.2.dp,
            brush = Brush.horizontalGradient(
              listOf(Color(0xFFFFD700), Color(0xFFFF007F), Color(0xFF00E5FF))
            ),
            shape = RoundedCornerShape(14.dp)
          )
          .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "MEGA MANAGER RECRUITMENT",
          color = Color(0xFFFFD700),
          fontSize = 13.5.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 1.2.sp,
          textAlign = TextAlign.Center
        )
      }
    }
  }
}
