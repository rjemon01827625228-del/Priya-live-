package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

enum class TopNavTab(val title: String) {
  EXPLORE("Explore"),
  NEW("New"),
  PK("PK"),
  FOLLOW("Follow")
}

/**
 * Top Navigation Bar with 4 horizontal options (Explore, New, PK, Follow).
 * Replicates the glowing, slanted pill design from the reference image:
 * - Parallelogram slant with smooth rounded corners
 * - Glassy multi-layer neon glow & bottom specular gleam
 * - Custom pixel-perfect vector icons for each tab
 */
@Composable
fun SlantedTopNavBar(
  selectedTab: TopNavTab,
  onTabSelected: (TopNavTab) -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 4.dp, vertical = 6.dp),
    horizontalArrangement = Arrangement.spacedBy(5.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // 1. Explore Tab
    SlantedNavPill(
      title = "Explore",
      isSelected = selectedTab == TopNavTab.EXPLORE,
      pillType = PillVisualType.EXPLORE,
      icon = { isSel -> SlantedExploreCompassIcon(isSelected = isSel) },
      modifier = Modifier.weight(1f),
      onClick = { onTabSelected(TopNavTab.EXPLORE) }
    )

    // 2. New Tab
    SlantedNavPill(
      title = "New",
      isSelected = selectedTab == TopNavTab.NEW,
      pillType = PillVisualType.NEW,
      icon = { isSel -> SlantedNewFlameIcon(isSelected = isSel) },
      modifier = Modifier.weight(1f),
      onClick = { onTabSelected(TopNavTab.NEW) }
    )

    // 3. PK Tab
    SlantedNavPill(
      title = "PK",
      isSelected = selectedTab == TopNavTab.PK,
      pillType = PillVisualType.PK,
      icon = { isSel -> SlantedPkCrownIcon(isSelected = isSel) },
      modifier = Modifier.weight(1f),
      onClick = { onTabSelected(TopNavTab.PK) }
    )

    // 4. Follow Tab
    SlantedNavPill(
      title = "Follow",
      isSelected = selectedTab == TopNavTab.FOLLOW,
      pillType = PillVisualType.FOLLOW,
      icon = { isSel -> SlantedFollowUserIcon(isSelected = isSel) },
      modifier = Modifier.weight(1f),
      onClick = { onTabSelected(TopNavTab.FOLLOW) }
    )
  }
}

enum class PillVisualType {
  EXPLORE,
  NEW,
  PK,
  FOLLOW
}

/**
 * Custom Slanted Pill Button matching the reference image.
 * Uses custom Canvas path rendering with specular shine, neon stroke, and glass highlights.
 */
@Composable
fun SlantedNavPill(
  title: String,
  isSelected: Boolean,
  pillType: PillVisualType,
  icon: @Composable (Boolean) -> Unit,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  val animAlpha by animateFloatAsState(
    targetValue = if (isSelected) 1f else 0.72f,
    animationSpec = tween(durationMillis = 220),
    label = "tab_alpha"
  )

  val interactionSource = remember { MutableInteractionSource() }

  Box(
    modifier = modifier
      .height(38.dp)
      .testTag("top_tab_${title.lowercase()}")
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
      )
      .drawBehind {
        drawSlantedPill(
          pillType = pillType,
          isSelected = isSelected,
          alpha = animAlpha
        )
      }
      .padding(horizontal = 6.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center,
      modifier = Modifier.fillMaxWidth()
    ) {
      icon(isSelected)

      Spacer(modifier = Modifier.width(4.dp))

      Text(
        text = title,
        color = if (isSelected) Color.White else Color(0xFFE2D6F5),
        fontSize = 12.5.sp,
        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
        letterSpacing = 0.2.sp
      )
    }
  }
}

/**
 * Draws the slanted capsule / rounded parallelogram matching the reference image:
 * Slanted angle, gradient border, glossy dark fill, top glass sheen, and bottom specular flare.
 */
private fun DrawScope.drawSlantedPill(
  pillType: PillVisualType,
  isSelected: Boolean,
  alpha: Float
) {
  val w = size.width
  val h = size.height

  // Slant horizontal offset (approx 7-8dp tilt to the right)
  val s = (h * 0.22f).coerceAtMost(w * 0.16f)
  // Corner radius
  val r = (h * 0.46f).coerceAtMost(16f)

  // Construct the slanted rounded parallelogram path
  val path = Path().apply {
    // Top-left starting point after rounded corner
    moveTo(s + r, 0f)
    // Top edge
    lineTo(w - r, 0f)
    // Top-right corner
    cubicTo(w - r * 0.3f, 0f, w, r * 0.4f, w - s * 0.25f, r)
    // Slanted right edge
    lineTo(w - s + s * 0.25f, h - r)
    // Bottom-right corner
    cubicTo(w - s, h - r * 0.4f, w - s - r * 0.3f, h, w - s - r, h)
    // Bottom edge
    lineTo(r, h)
    // Bottom-left corner
    cubicTo(r * 0.3f, h, 0f, h - r * 0.4f, s * 0.25f, h - r)
    // Slanted left edge
    lineTo(s - s * 0.25f, r)
    // Top-left corner
    cubicTo(s, r * 0.4f, s + r * 0.3f, 0f, s + r, 0f)
    close()
  }

  // Color schemes according to the 4 buttons in the reference image
  val (fillGradient, borderGradient, flareColor) = when (pillType) {
    PillVisualType.EXPLORE -> Triple(
      Brush.verticalGradient(
        listOf(
          if (isSelected) Color(0xFF33004F) else Color(0xFF1E0030),
          if (isSelected) Color(0xFF1A002E) else Color(0xFF10001D),
          if (isSelected) Color(0xFF260040) else Color(0xFF0D0017)
        )
      ),
      Brush.horizontalGradient(
        listOf(
          Color(0xFFFF0099).copy(alpha = if (isSelected) 1f else 0.65f),
          Color(0xFFD500F9).copy(alpha = if (isSelected) 1f else 0.65f),
          Color(0xFFFF007F).copy(alpha = if (isSelected) 1f else 0.75f)
        )
      ),
      Color(0xFFFF4081)
    )

    PillVisualType.NEW -> Triple(
      Brush.verticalGradient(
        listOf(
          if (isSelected) Color(0xFF002244) else Color(0xFF00152B),
          if (isSelected) Color(0xFF00122A) else Color(0xFF000B1A),
          if (isSelected) Color(0xFF001C38) else Color(0xFF000E20)
        )
      ),
      Brush.horizontalGradient(
        listOf(
          Color(0xFF00E5FF).copy(alpha = if (isSelected) 1f else 0.65f),
          Color(0xFF0091EA).copy(alpha = if (isSelected) 1f else 0.65f),
          Color(0xFF00E5FF).copy(alpha = if (isSelected) 1f else 0.75f)
        )
      ),
      Color(0xFF00E5FF)
    )

    PillVisualType.PK -> Triple(
      Brush.verticalGradient(
        listOf(
          if (isSelected) Color(0xFF3B0046) else Color(0xFF200028),
          if (isSelected) Color(0xFF1D0026) else Color(0xFF110017),
          if (isSelected) Color(0xFF2E003A) else Color(0xFF0F0014)
        )
      ),
      Brush.horizontalGradient(
        listOf(
          Color(0xFFFF00A0).copy(alpha = if (isSelected) 1f else 0.65f),
          Color(0xFFAA00FF).copy(alpha = if (isSelected) 1f else 0.65f),
          Color(0xFFFF007F).copy(alpha = if (isSelected) 1f else 0.75f)
        )
      ),
      Color(0xFFFF1744)
    )

    PillVisualType.FOLLOW -> Triple(
      Brush.verticalGradient(
        listOf(
          if (isSelected) Color(0xFF300045) else Color(0xFF1A0029),
          if (isSelected) Color(0xFF160024) else Color(0xFF0E0018),
          if (isSelected) Color(0xFF26003A) else Color(0xFF0A0012)
        )
      ),
      Brush.horizontalGradient(
        listOf(
          Color(0xFFD500F9).copy(alpha = if (isSelected) 1f else 0.65f),
          Color(0xFFFF007F).copy(alpha = if (isSelected) 1f else 0.65f),
          Color(0xFF9C27B0).copy(alpha = if (isSelected) 1f else 0.75f)
        )
      ),
      Color(0xFFE040FB)
    )
  }

  // 1. Draw base filled path
  drawPath(path = path, brush = fillGradient)

  // 2. Draw upper half glassy sheen highlight
  val glassSheenPath = Path().apply {
    moveTo(s + r, 0f)
    lineTo(w - r, 0f)
    cubicTo(w - r * 0.3f, 0f, w, r * 0.4f, w - s * 0.25f, r)
    lineTo(w - s * 0.6f, h * 0.48f)
    lineTo(s * 0.6f, h * 0.48f)
    lineTo(s - s * 0.25f, r)
    cubicTo(s, r * 0.4f, s + r * 0.3f, 0f, s + r, 0f)
    close()
  }
  drawPath(
    path = glassSheenPath,
    brush = Brush.verticalGradient(
      listOf(
        Color.White.copy(alpha = if (isSelected) 0.18f else 0.08f),
        Color.White.copy(alpha = 0.01f)
      )
    )
  )

  // 3. Draw outer glowing neon border
  val strokeWidth = if (isSelected) 2.2f else 1.4f
  drawPath(
    path = path,
    brush = borderGradient,
    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
  )

  // 4. Specular gleam / lens flare along the bottom edge (matching reference image)
  if (isSelected) {
    val flareCenterX = (w - s) * 0.48f
    val flareCenterY = h - 0.8f
    val flareWidth = (w * 0.36f)

    // Horizontal flare beam
    drawLine(
      brush = Brush.horizontalGradient(
        colors = listOf(
          flareColor.copy(alpha = 0f),
          Color.White,
          flareColor.copy(alpha = 0.9f),
          flareColor.copy(alpha = 0f)
        ),
        startX = flareCenterX - flareWidth / 2f,
        endX = flareCenterX + flareWidth / 2f
      ),
      start = Offset(flareCenterX - flareWidth / 2f, flareCenterY),
      end = Offset(flareCenterX + flareWidth / 2f, flareCenterY),
      strokeWidth = 2.4f,
      cap = StrokeCap.Round
    )

    // Center bright hotspot
    drawCircle(
      color = Color.White,
      radius = 1.6f,
      center = Offset(flareCenterX, flareCenterY)
    )
  }
}

// -------------------------------------------------------------------------
// HIGH-FIDELITY VECTOR ICONS MATCHING THE REFERENCE IMAGE
// -------------------------------------------------------------------------

/**
 * Explore Compass Icon:
 * Circular glowing frame with 3D faceted compass arrow (slanted pointing NE).
 */
@Composable
fun SlantedExploreCompassIcon(
  isSelected: Boolean,
  modifier: Modifier = Modifier
) {
  Canvas(modifier = modifier.size(19.dp)) {
    val r = size.minDimension / 2f
    val cx = size.width / 2f
    val cy = size.height / 2f

    // Outer circular badge
    drawCircle(
      brush = Brush.sweepGradient(
        listOf(
          Color(0xFFE040FB),
          Color(0xFFFF007F),
          Color(0xFF8A2BE2),
          Color(0xFFE040FB)
        )
      ),
      radius = r,
      center = Offset(cx, cy)
    )

    // Inner dark circle
    drawCircle(
      brush = Brush.verticalGradient(
        listOf(Color(0xFF24003E), Color(0xFF100020))
      ),
      radius = r * 0.82f,
      center = Offset(cx, cy)
    )

    // Slanted 3D faceted compass arrow pointing NE (45 degrees angle)
    val angle = -45.0 * (PI / 180.0)
    val cosA = cos(angle).toFloat()
    val sinA = sin(angle).toFloat()

    val tipDist = r * 0.65f
    val sideDist = r * 0.28f
    val tailDist = r * 0.40f

    // Tip point (North-East)
    val tipX = cx + tipDist * cosA
    val tipY = cy + tipDist * sinA

    // Right wing point
    val rightX = cx + sideDist * (-sinA)
    val rightY = cy + sideDist * cosA

    // Left wing point
    val leftX = cx - sideDist * (-sinA)
    val leftY = cy - sideDist * cosA

    // Tail point
    val tailX = cx - tailDist * cosA
    val tailY = cy - tailDist * sinA

    // Left Facet (Bright reflective 3D white/cyan)
    val leftFacet = Path().apply {
      moveTo(cx, cy)
      lineTo(tipX, tipY)
      lineTo(leftX, leftY)
      close()
    }
    drawPath(
      path = leftFacet,
      brush = Brush.linearGradient(
        listOf(Color.White, Color(0xFFE0F7FA), Color(0xFFB2EBF2))
      )
    )

    // Right Facet (Shaded violet/purple 3D side)
    val rightFacet = Path().apply {
      moveTo(cx, cy)
      lineTo(tipX, tipY)
      lineTo(rightX, rightY)
      close()
    }
    drawPath(
      path = rightFacet,
      brush = Brush.linearGradient(
        listOf(Color(0xFFAB47BC), Color(0xFF6A1B9A), Color(0xFF4A148C))
      )
    )

    // South Tail Left Facet (Shaded)
    val tailLeftFacet = Path().apply {
      moveTo(cx, cy)
      lineTo(tailX, tailY)
      lineTo(leftX, leftY)
      close()
    }
    drawPath(
      path = tailLeftFacet,
      brush = Brush.linearGradient(
        listOf(Color(0xFFBA68C8), Color(0xFF7B1FA2))
      )
    )

    // South Tail Right Facet (Darker Shaded)
    val tailRightFacet = Path().apply {
      moveTo(cx, cy)
      lineTo(tailX, tailY)
      lineTo(rightX, rightY)
      close()
    }
    drawPath(
      path = tailRightFacet,
      brush = Brush.linearGradient(
        listOf(Color(0xFF4A148C), Color(0xFF311B92))
      )
    )

    // Center pivot dot
    drawCircle(
      color = Color.White,
      radius = r * 0.12f,
      center = Offset(cx, cy)
    )
  }
}

/**
 * New Flame Icon:
 * Dynamic flame with purple-to-cyan vertical gradient and inner bright cyan/white core.
 */
@Composable
fun SlantedNewFlameIcon(
  isSelected: Boolean,
  modifier: Modifier = Modifier
) {
  Canvas(modifier = modifier.size(19.dp)) {
    val w = size.width
    val h = size.height

    // Outer flame silhouette
    val outerFlame = Path().apply {
      moveTo(w * 0.48f, h * 0.04f)
      cubicTo(w * 0.76f, h * 0.22f, w * 0.98f, h * 0.54f, w * 0.78f, h * 0.86f)
      cubicTo(w * 0.64f, h * 1.0f, w * 0.36f, h * 1.0f, w * 0.22f, h * 0.86f)
      cubicTo(w * 0.02f, h * 0.54f, w * 0.22f, h * 0.24f, w * 0.48f, h * 0.04f)
      close()
    }

    drawPath(
      path = outerFlame,
      brush = Brush.verticalGradient(
        listOf(
          Color(0xFFD500F9), // Neon magenta/purple tip
          Color(0xFF651FFF), // Electric blue-violet
          Color(0xFF00B0FF), // Vivid cyan-blue
          Color(0xFF00E5FF)  // Bright cyan base
        )
      )
    )

    // Inner flame tongue (teardrop core)
    val innerFlame = Path().apply {
      moveTo(w * 0.50f, h * 0.42f)
      cubicTo(w * 0.68f, h * 0.58f, w * 0.70f, h * 0.78f, w * 0.56f, h * 0.90f)
      cubicTo(w * 0.44f, h * 0.90f, w * 0.30f, h * 0.78f, w * 0.32f, h * 0.58f)
      close()
    }

    drawPath(
      path = innerFlame,
      brush = Brush.verticalGradient(
        listOf(
          Color.White,
          Color(0xFFE0F7FA),
          Color(0xFF00E5FF)
        )
      )
    )
  }
}

/**
 * PK Crown Badge Icon:
 * Rounded rectangular badge with crown on top, glowing outline, and bold "PK".
 */
@Composable
fun SlantedPkCrownIcon(
  isSelected: Boolean,
  modifier: Modifier = Modifier
) {
  Canvas(modifier = modifier.size(20.dp)) {
    val w = size.width
    val h = size.height

    // 1. 3-Point Crown on Top
    val crownPath = Path().apply {
      moveTo(w * 0.20f, h * 0.34f)
      lineTo(w * 0.10f, h * 0.12f) // left point
      lineTo(w * 0.34f, h * 0.22f)
      lineTo(w * 0.50f, h * 0.02f) // tall center point
      lineTo(w * 0.66f, h * 0.22f)
      lineTo(w * 0.90f, h * 0.12f) // right point
      lineTo(w * 0.80f, h * 0.34f)
      close()
    }
    drawPath(
      path = crownPath,
      brush = Brush.verticalGradient(
        listOf(Color.White, Color(0xFFFF80AB), Color(0xFFFF4081))
      )
    )

    // Crown jewel dots on points
    drawCircle(color = Color.White, radius = 1.1f, center = Offset(w * 0.10f, h * 0.12f))
    drawCircle(color = Color.White, radius = 1.3f, center = Offset(w * 0.50f, h * 0.02f))
    drawCircle(color = Color.White, radius = 1.1f, center = Offset(w * 0.90f, h * 0.12f))

    // 2. Rounded Rectangular Badge Frame
    val badgeCorner = 4f
    val badgeTop = h * 0.32f
    val badgeHeight = h * 0.64f
    val badgeWidth = w * 0.92f
    val badgeLeft = w * 0.04f

    // Dark badge background
    drawRoundRect(
      color = Color(0xFF260036),
      topLeft = Offset(badgeLeft, badgeTop),
      size = Size(badgeWidth, badgeHeight),
      cornerRadius = androidx.compose.ui.geometry.CornerRadius(badgeCorner, badgeCorner)
    )

    // Neon pink glowing stroke
    drawRoundRect(
      brush = Brush.horizontalGradient(
        listOf(Color(0xFFFF007F), Color(0xFFD500F9), Color(0xFFFF00A0))
      ),
      topLeft = Offset(badgeLeft, badgeTop),
      size = Size(badgeWidth, badgeHeight),
      cornerRadius = androidx.compose.ui.geometry.CornerRadius(badgeCorner, badgeCorner),
      style = Stroke(width = 1.4f)
    )

    // 3. Slanted "PK" Graphic letters inside
    // Letter 'P'
    val pPath = Path().apply {
      moveTo(w * 0.24f, h * 0.44f)
      lineTo(w * 0.24f, h * 0.86f)
    }
    drawPath(
      path = pPath,
      color = Color.White,
      style = Stroke(width = 1.8f, cap = StrokeCap.Round)
    )

    val pLoop = Path().apply {
      moveTo(w * 0.24f, h * 0.44f)
      cubicTo(w * 0.48f, h * 0.44f, w * 0.48f, h * 0.65f, w * 0.24f, h * 0.65f)
    }
    drawPath(
      path = pLoop,
      color = Color.White,
      style = Stroke(width = 1.8f, cap = StrokeCap.Round)
    )

    // Letter 'K'
    val kStem = Path().apply {
      moveTo(w * 0.58f, h * 0.44f)
      lineTo(w * 0.58f, h * 0.86f)
    }
    drawPath(
      path = kStem,
      color = Color.White,
      style = Stroke(width = 1.8f, cap = StrokeCap.Round)
    )

    val kArm1 = Path().apply {
      moveTo(w * 0.78f, h * 0.44f)
      lineTo(w * 0.58f, h * 0.65f)
    }
    drawPath(
      path = kArm1,
      color = Color.White,
      style = Stroke(width = 1.8f, cap = StrokeCap.Round)
    )

    val kArm2 = Path().apply {
      moveTo(w * 0.58f, h * 0.65f)
      lineTo(w * 0.80f, h * 0.86f)
    }
    drawPath(
      path = kArm2,
      color = Color.White,
      style = Stroke(width = 1.8f, cap = StrokeCap.Round)
    )
  }
}

/**
 * Follow User Icon:
 * 3D glossy avatar silhouette (head + shoulders) with circular (+) badge at bottom-right.
 */
@Composable
fun SlantedFollowUserIcon(
  isSelected: Boolean,
  modifier: Modifier = Modifier
) {
  Canvas(modifier = modifier.size(19.dp)) {
    val w = size.width
    val h = size.height

    // 1. Head circle with 3D gradient
    val headRadius = w * 0.22f
    val headCenter = Offset(w * 0.44f, h * 0.26f)
    drawCircle(
      brush = Brush.linearGradient(
        colors = listOf(Color.White, Color(0xFFFF80AB), Color(0xFFBA68C8)),
        start = Offset(headCenter.x - headRadius, headCenter.y - headRadius),
        end = Offset(headCenter.x + headRadius, headCenter.y + headRadius)
      ),
      radius = headRadius,
      center = headCenter
    )

    // 2. Torso / Shoulders silhouette
    val bodyPath = Path().apply {
      moveTo(w * 0.12f, h * 0.86f)
      cubicTo(w * 0.14f, h * 0.56f, w * 0.74f, h * 0.56f, w * 0.76f, h * 0.86f)
      close()
    }
    drawPath(
      path = bodyPath,
      brush = Brush.linearGradient(
        colors = listOf(Color(0xFFFF80AB), Color(0xFFAB47BC), Color(0xFF6A1B9A))
      )
    )

    // 3. Circular (+) Plus Badge at bottom right
    val badgeRadius = w * 0.20f
    val badgeCenter = Offset(w * 0.75f, h * 0.74f)

    // Dark badge background
    drawCircle(
      color = Color(0xFF26003B),
      radius = badgeRadius,
      center = badgeCenter
    )

    // Glowing magenta border
    drawCircle(
      color = Color(0xFFFF007F),
      radius = badgeRadius,
      center = badgeCenter,
      style = Stroke(width = 1.3f)
    )

    // White plus sign (+)
    val plusArm = badgeRadius * 0.58f
    drawLine(
      color = Color.White,
      start = Offset(badgeCenter.x - plusArm, badgeCenter.y),
      end = Offset(badgeCenter.x + plusArm, badgeCenter.y),
      strokeWidth = 1.6f,
      cap = StrokeCap.Round
    )
    drawLine(
      color = Color.White,
      start = Offset(badgeCenter.x, badgeCenter.y - plusArm),
      end = Offset(badgeCenter.x, badgeCenter.y + plusArm),
      strokeWidth = 1.6f,
      cap = StrokeCap.Round
    )
  }
}
