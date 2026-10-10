package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.dp

/**
 * 1. 3D TV Play Icon for LIVE tab
 * Exactly matching the screenshot:
 * - Cute pink/white diagonal antennas with round ball tips
 * - 3D rounded white TV monitor casing with soft lighting
 * - Magenta/violet play triangle in the center
 */
@Composable
fun Live3dTvIcon(
  modifier: Modifier = Modifier
) {
  Canvas(modifier = modifier.size(36.dp)) {
    val w = size.width
    val h = size.height

    // 1. Antennas (Left & Right diagonal rods with sphere tips)
    val antTopY = h * 0.08f
    val antBaseX = w * 0.5f
    val antBaseY = h * 0.30f

    // Left antenna
    val antLeftX = w * 0.28f
    drawLine(
      brush = Brush.linearGradient(
        listOf(Color(0xFFFFB2FF), Color.White)
      ),
      start = Offset(antBaseX - w * 0.04f, antBaseY),
      end = Offset(antLeftX, antTopY),
      strokeWidth = 2.4f,
      cap = StrokeCap.Round
    )
    // Left tip ball
    drawCircle(
      color = Color.White,
      radius = w * 0.052f,
      center = Offset(antLeftX, antTopY)
    )

    // Right antenna
    val antRightX = w * 0.72f
    drawLine(
      brush = Brush.linearGradient(
        listOf(Color(0xFFFFB2FF), Color.White)
      ),
      start = Offset(antBaseX + w * 0.04f, antBaseY),
      end = Offset(antRightX, antTopY),
      strokeWidth = 2.4f,
      cap = StrokeCap.Round
    )
    // Right tip ball
    drawCircle(
      color = Color.White,
      radius = w * 0.052f,
      center = Offset(antRightX, antTopY)
    )

    // 2. TV Monitor Body (3D White Casing with soft lavender-pink shadow)
    val tvLeft = w * 0.14f
    val tvTop = h * 0.26f
    val tvWidth = w * 0.72f
    val tvHeight = h * 0.62f
    val tvRadius = CornerRadius(tvWidth * 0.26f, tvWidth * 0.26f)

    // Outer subtle ambient shadow below TV
    drawRoundRect(
      color = Color(0x33000000),
      topLeft = Offset(tvLeft, tvTop + 2f),
      size = Size(tvWidth, tvHeight),
      cornerRadius = tvRadius
    )

    // White glossy 3D front surface
    drawRoundRect(
      brush = Brush.verticalGradient(
        colors = listOf(
          Color(0xFFFFFFFF),
          Color(0xFFF9F5FF),
          Color(0xFFE9DCFF)
        )
      ),
      topLeft = Offset(tvLeft, tvTop),
      size = Size(tvWidth, tvHeight),
      cornerRadius = tvRadius
    )

    // Inner bevel highlight
    drawRoundRect(
      color = Color.White.copy(alpha = 0.9f),
      topLeft = Offset(tvLeft + 1.2f, tvTop + 1.2f),
      size = Size(tvWidth - 2.4f, tvHeight - 2.4f),
      cornerRadius = CornerRadius(tvRadius.x - 1f, tvRadius.y - 1f),
      style = Stroke(width = 1.2f)
    )

    // 3. Play Triangle in Center (Magenta / Violet 3D button)
    val triCenterX = w * 0.52f
    val triCenterY = tvTop + tvHeight * 0.5f
    val triSize = w * 0.16f

    val triPath = Path().apply {
      moveTo(triCenterX - triSize * 0.8f, triCenterY - triSize)
      lineTo(triCenterX + triSize * 1.1f, triCenterY)
      lineTo(triCenterX - triSize * 0.8f, triCenterY + triSize)
      close()
    }

    drawPath(
      path = triPath,
      brush = Brush.verticalGradient(
        listOf(
          Color(0xFFFF2A99),
          Color(0xFFB500E0),
          Color(0xFF7B00CC)
        )
      )
    )
  }
}

/**
 * 2. 3D Party Popper Icon for PARTY tab
 * Exactly matching the screenshot:
 * - Striped party cone (orange & yellow stripes with purple base collar)
 * - Bursting confetti streamers: blue star, pink ribbon, purple twist, yellow dots
 */
@Composable
fun PartyPopper3dIcon(
  modifier: Modifier = Modifier
) {
  Canvas(modifier = modifier.size(36.dp)) {
    val w = size.width
    val h = size.height

    // 1. Confetti ribbons & sparkles bursting out to the top and right
    // Blue 4-point sparkle star (top-left)
    val starCx = w * 0.32f
    val starCy = h * 0.15f
    val starR = w * 0.08f
    val starPath = Path().apply {
      moveTo(starCx, starCy - starR)
      lineTo(starCx + starR * 0.28f, starCy - starR * 0.28f)
      lineTo(starCx + starR, starCy)
      lineTo(starCx + starR * 0.28f, starCy + starR * 0.28f)
      lineTo(starCx, starCy + starR)
      lineTo(starCx - starR * 0.28f, starCy + starR * 0.28f)
      lineTo(starCx - starR, starCy)
      lineTo(starCx - starR * 0.28f, starCy - starR * 0.28f)
      close()
    }
    drawPath(path = starPath, color = Color(0xFF00B0FF))

    // Pink / Coral curling streamer ribbon (center-top)
    val ribbonPink = Path().apply {
      moveTo(w * 0.52f, h * 0.40f)
      cubicTo(w * 0.50f, h * 0.22f, w * 0.68f, h * 0.22f, w * 0.64f, h * 0.12f)
    }
    drawPath(
      path = ribbonPink,
      color = Color(0xFFFF3377),
      style = Stroke(width = 3.6f, cap = StrokeCap.Round)
    )

    // Magenta / Violet ribbon (upper right)
    val ribbonViolet = Path().apply {
      moveTo(w * 0.62f, h * 0.46f)
      cubicTo(w * 0.72f, h * 0.36f, w * 0.88f, h * 0.36f, w * 0.84f, h * 0.20f)
    }
    drawPath(
      path = ribbonViolet,
      color = Color(0xFFD500F9),
      style = Stroke(width = 3.2f, cap = StrokeCap.Round)
    )

    // Cyan curve (far right)
    val ribbonCyan = Path().apply {
      moveTo(w * 0.68f, h * 0.54f)
      cubicTo(w * 0.84f, h * 0.52f, w * 0.90f, h * 0.42f, w * 0.86f, h * 0.36f)
    }
    drawPath(
      path = ribbonCyan,
      color = Color(0xFF00E5FF),
      style = Stroke(width = 3.0f, cap = StrokeCap.Round)
    )

    // Golden / yellow confetti dots
    drawCircle(color = Color(0xFFFFD600), radius = 2.4f, center = Offset(w * 0.42f, h * 0.18f))
    drawCircle(color = Color(0xFFFFAB00), radius = 2.0f, center = Offset(w * 0.78f, h * 0.26f))

    // Purple sparkle dot (right)
    val purpleStarCx = w * 0.82f
    val purpleStarCy = h * 0.62f
    val pR = w * 0.06f
    val pStar = Path().apply {
      moveTo(purpleStarCx, purpleStarCy - pR)
      lineTo(purpleStarCx + pR * 0.28f, purpleStarCy - pR * 0.28f)
      lineTo(purpleStarCx + pR, purpleStarCy)
      lineTo(purpleStarCx + pR * 0.28f, purpleStarCy + pR * 0.28f)
      lineTo(purpleStarCx, purpleStarCy + pR)
      lineTo(purpleStarCx - pR * 0.28f, purpleStarCy + pR * 0.28f)
      lineTo(purpleStarCx - pR, purpleStarCy)
      lineTo(purpleStarCx - pR * 0.28f, purpleStarCy - pR * 0.28f)
      close()
    }
    drawPath(path = pStar, color = Color(0xFF7C4DFF))

    // 2. Party Popper Cone (Tilted diagonally, Orange & Yellow Candy Stripes)
    // Cone tip at bottom-left: (w * 0.16, h * 0.86)
    // Cone opening at top-right: between (w * 0.46, h * 0.40) and (w * 0.72, h * 0.66)
    val tipX = w * 0.16f
    val tipY = h * 0.86f
    val openTopX = w * 0.46f
    val openTopY = h * 0.42f
    val openBottomX = w * 0.70f
    val openBottomY = h * 0.66f

    // Cone clipping path
    val conePath = Path().apply {
      moveTo(tipX, tipY)
      lineTo(openTopX, openTopY)
      lineTo(openBottomX, openBottomY)
      close()
    }

    clipPath(conePath) {
      // Base orange fill
      drawRect(
        brush = Brush.linearGradient(
          colors = listOf(Color(0xFFFF6D00), Color(0xFFFF9100), Color(0xFFFFAB00)),
          start = Offset(tipX, tipY),
          end = Offset(openBottomX, openTopY)
        )
      )

      // Alternating White/Yellow diagonal stripes across the cone
      for (i in 1..4) {
        val frac = i * 0.22f
        val sx = tipX + (openTopX - tipX) * frac
        val sy = tipY + (openTopY - tipY) * frac
        val ex = tipX + (openBottomX - tipX) * frac
        val ey = tipY + (openBottomY - tipY) * frac

        drawLine(
          color = Color(0xFFFFF9C4),
          start = Offset(sx, sy),
          end = Offset(ex, ey),
          strokeWidth = 4.2f,
          cap = StrokeCap.Square
        )
      }
    }

    // 3. Purple collar / rim cap at the mouth of the cone
    val collarPath = Path().apply {
      moveTo(openTopX - 1f, openTopY - 1f)
      lineTo(openTopX + w * 0.08f, openTopY - h * 0.06f)
      lineTo(openBottomX + w * 0.06f, openBottomY + h * 0.02f)
      lineTo(openBottomX, openBottomY)
      close()
    }
    drawPath(
      path = collarPath,
      brush = Brush.linearGradient(
        colors = listOf(Color(0xFF8C00FF), Color(0xFF5A00C8), Color(0xFF3B0088))
      )
    )
  }
}

/**
 * 3. 3D Planet Icon for DISCOVER tab
 * Exactly matching the screenshot:
 * - Electric blue / cyan glossy sphere with 3D spherical lighting & highlight
 * - Tilted vivid magenta / purple orbital ring looping around
 * - Sparkle stars flanking the planet
 */
@Composable
fun DiscoverPlanet3dIcon(
  modifier: Modifier = Modifier
) {
  Canvas(modifier = modifier.size(36.dp)) {
    val w = size.width
    val h = size.height
    val center = Offset(w * 0.50f, h * 0.50f)
    val planetRadius = w * 0.25f

    // 1. Background sparkle stars
    // Top-left small star
    drawSparkleStar(Offset(w * 0.16f, h * 0.22f), w * 0.065f, Color(0xFF00E5FF))
    // Bottom-left large cyan star
    drawSparkleStar(Offset(w * 0.18f, h * 0.68f), w * 0.09f, Color(0xFF00B0FF))
    // Bottom-right purple star
    drawSparkleStar(Offset(w * 0.84f, h * 0.72f), w * 0.075f, Color(0xFF9D00FF))

    // 2. Back half of the tilted orbital ring (drawn BEHIND the planet)
    val ringCenter = center
    val ringRx = w * 0.44f
    val ringRy = h * 0.17f
    val ringAngle = -28.0 // tilted ~28 degrees

    // We can draw the full ring with a clip or draw back then front
    // Drawing the tilted ellipse using Canvas transform
    drawContext.canvas.save()
    drawContext.canvas.translate(ringCenter.x, ringCenter.y)
    drawContext.canvas.rotate(ringAngle.toFloat())

    // Full 3D orbital ring
    drawOval(
      brush = Brush.horizontalGradient(
        colors = listOf(
          Color(0xFF9900E6),
          Color(0xFFE040FB),
          Color(0xFF00E5FF),
          Color(0xFFD500F9)
        )
      ),
      topLeft = Offset(-ringRx, -ringRy),
      size = Size(ringRx * 2f, ringRy * 2f),
      style = Stroke(width = 4.2f)
    )

    // Inner bright specular glow on the ring
    drawOval(
      color = Color.White.copy(alpha = 0.85f),
      topLeft = Offset(-ringRx + 0.5f, -ringRy + 0.5f),
      size = Size((ringRx - 0.5f) * 2f, (ringRy - 0.5f) * 2f),
      style = Stroke(width = 1.4f)
    )

    drawContext.canvas.restore()

    // 3. Planet Sphere (Drawn on top of the back half of the ring)
    // 3D spherical radial gradient: highlight at upper-left (center.x - r*0.3, center.y - r*0.3)
    drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(
          Color(0xFF00E5FF),
          Color(0xFF0091EA),
          Color(0xFF0277BD),
          Color(0xFF014175)
        ),
        center = Offset(center.x - planetRadius * 0.28f, center.y - planetRadius * 0.28f),
        radius = planetRadius * 1.05f
      ),
      radius = planetRadius,
      center = center
    )

    // Specular light spot on sphere
    drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(
          Color.White.copy(alpha = 0.85f),
          Color(0x8800E5FF),
          Color.Transparent
        ),
        center = Offset(center.x - planetRadius * 0.35f, center.y - planetRadius * 0.35f),
        radius = planetRadius * 0.45f
      ),
      radius = planetRadius * 0.45f,
      center = Offset(center.x - planetRadius * 0.35f, center.y - planetRadius * 0.35f)
    )

    // 4. Front half of the tilted orbital ring (cutting across the front of the sphere)
    drawContext.canvas.save()
    drawContext.canvas.translate(ringCenter.x, ringCenter.y)
    drawContext.canvas.rotate(ringAngle.toFloat())

    // Front arc of the ring (the lower half of the tilted ellipse: from 0° to 180°)
    drawArc(
      brush = Brush.horizontalGradient(
        colors = listOf(
          Color(0xFFB500FF),
          Color(0xFFFF33B0),
          Color(0xFFFFFFFF),
          Color(0xFF00F0FF)
        )
      ),
      startAngle = 0f,
      sweepAngle = 180f,
      useCenter = false,
      topLeft = Offset(-ringRx, -ringRy),
      size = Size(ringRx * 2f, ringRy * 2f),
      style = Stroke(width = 4.2f, cap = StrokeCap.Round)
    )

    drawContext.canvas.restore()
  }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSparkleStar(
  center: Offset,
  radius: Float,
  color: Color
) {
  val p = Path().apply {
    moveTo(center.x, center.y - radius)
    lineTo(center.x + radius * 0.28f, center.y - radius * 0.28f)
    lineTo(center.x + radius, center.y)
    lineTo(center.x + radius * 0.28f, center.y + radius * 0.28f)
    lineTo(center.x, center.y + radius)
    lineTo(center.x - radius * 0.28f, center.y + radius * 0.28f)
    lineTo(center.x - radius, center.y)
    lineTo(center.x - radius * 0.28f, center.y - radius * 0.28f)
    close()
  }
  drawPath(path = p, color = color)
}

/**
 * 4. 3D Smiley Chat Icon for CHAT tab
 * Exactly matching the screenshot:
 * - 3D glossy hot pink / magenta chat bubble with lower-left tail
 * - Sweet smiley face: two round white eyes and a curved smiling mouth
 * - Top specular highlight sheen
 */
@Composable
fun ChatSmiley3dIcon(
  modifier: Modifier = Modifier
) {
  Canvas(modifier = modifier.size(36.dp)) {
    val w = size.width
    val h = size.height

    // Main speech bubble body (Squircle) + tail at bottom-left
    val bubbleLeft = w * 0.12f
    val bubbleTop = h * 0.12f
    val bubbleWidth = w * 0.76f
    val bubbleHeight = h * 0.68f
    val cornerR = bubbleWidth * 0.32f

    // Soft drop shadow
    drawRoundRect(
      color = Color(0x33FF007F),
      topLeft = Offset(bubbleLeft, bubbleTop + 3f),
      size = Size(bubbleWidth, bubbleHeight),
      cornerRadius = CornerRadius(cornerR, cornerR)
    )

    // Tail pointing towards bottom-left
    val tailPath = Path().apply {
      moveTo(bubbleLeft + cornerR * 0.4f, bubbleTop + bubbleHeight - 2f)
      lineTo(bubbleLeft - w * 0.02f, bubbleTop + bubbleHeight + h * 0.12f)
      lineTo(bubbleLeft + cornerR * 1.2f, bubbleTop + bubbleHeight - 2f)
      close()
    }
    drawPath(
      path = tailPath,
      brush = Brush.verticalGradient(
        colors = listOf(Color(0xFFFF2276), Color(0xFFC20066))
      )
    )

    // 3D Hot Pink / Magenta rounded bubble
    drawRoundRect(
      brush = Brush.verticalGradient(
        colors = listOf(
          Color(0xFFFF4088),
          Color(0xFFFF1867),
          Color(0xFFD60060),
          Color(0xFF9E0045)
        )
      ),
      topLeft = Offset(bubbleLeft, bubbleTop),
      size = Size(bubbleWidth, bubbleHeight),
      cornerRadius = CornerRadius(cornerR, cornerR)
    )

    // Specular light sheen across top edge
    drawRoundRect(
      brush = Brush.verticalGradient(
        colors = listOf(
          Color.White.copy(alpha = 0.60f),
          Color.Transparent
        )
      ),
      topLeft = Offset(bubbleLeft + 2f, bubbleTop + 2f),
      size = Size(bubbleWidth - 4f, bubbleHeight * 0.40f),
      cornerRadius = CornerRadius(cornerR - 1f, cornerR - 1f)
    )

    // Smiley: Two distinct round white dot eyes
    val eyeY = bubbleTop + bubbleHeight * 0.40f
    val eyeRadius = w * 0.048f
    val leftEyeX = bubbleLeft + bubbleWidth * 0.35f
    val rightEyeX = bubbleLeft + bubbleWidth * 0.65f

    drawCircle(color = Color.White, radius = eyeRadius, center = Offset(leftEyeX, eyeY))
    drawCircle(color = Color.White, radius = eyeRadius, center = Offset(rightEyeX, eyeY))

    // Smiley: Sweet curved mouth arc
    val mouthY = bubbleTop + bubbleHeight * 0.62f
    val mouthPath = Path().apply {
      moveTo(leftEyeX - w * 0.02f, mouthY)
      cubicTo(
        leftEyeX + w * 0.05f, mouthY + h * 0.13f,
        rightEyeX - w * 0.05f, mouthY + h * 0.13f,
        rightEyeX + w * 0.02f, mouthY
      )
    }
    drawPath(
      path = mouthPath,
      color = Color.White,
      style = Stroke(width = 3.0f, cap = StrokeCap.Round)
    )
  }
}

/**
 * 5. 3D Profile / User Avatar Icon for ME tab
 * Exactly matching the screenshot:
 * - 3D vibrant electric blue glossy sphere head with top light sheen
 * - 3D smooth curved blue torso / shoulders below
 */
@Composable
fun ProfileMe3dIcon(
  modifier: Modifier = Modifier
) {
  Canvas(modifier = modifier.size(36.dp)) {
    val w = size.width
    val h = size.height
    val cx = w * 0.50f

    // 1. Head Sphere (3D Shaded Blue)
    val headRadius = w * 0.22f
    val headCy = h * 0.32f

    // Ambient drop shadow
    drawCircle(
      color = Color(0x330055AA),
      radius = headRadius,
      center = Offset(cx, headCy + 2f)
    )

    // Sphere radial gradient
    drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(
          Color(0xFF40C4FF),
          Color(0xFF0091EA),
          Color(0xFF0277BD),
          Color(0xFF014578)
        ),
        center = Offset(cx - headRadius * 0.25f, headCy - headRadius * 0.25f),
        radius = headRadius * 1.05f
      ),
      radius = headRadius,
      center = Offset(cx, headCy)
    )

    // Specular highlight spot on head
    drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(
          Color.White.copy(alpha = 0.75f),
          Color(0x5540C4FF),
          Color.Transparent
        ),
        center = Offset(cx - headRadius * 0.30f, headCy - headRadius * 0.30f),
        radius = headRadius * 0.45f
      ),
      radius = headRadius * 0.45f,
      center = Offset(cx - headRadius * 0.30f, headCy - headRadius * 0.30f)
    )

    // 2. Torso / Shoulders (3D Curved Bust)
    val torsoTop = h * 0.54f
    val torsoBottom = h * 0.88f
    val torsoWidth = w * 0.68f
    val torsoHeight = torsoBottom - torsoTop
    val torsoLeft = cx - torsoWidth / 2f

    val torsoPath = Path().apply {
      moveTo(torsoLeft + torsoWidth * 0.16f, torsoBottom)
      cubicTo(
        torsoLeft + torsoWidth * 0.05f, torsoTop + torsoHeight * 0.35f,
        torsoLeft + torsoWidth * 0.25f, torsoTop,
        cx, torsoTop
      )
      cubicTo(
        torsoLeft + torsoWidth * 0.75f, torsoTop,
        torsoLeft + torsoWidth * 0.95f, torsoTop + torsoHeight * 0.35f,
        torsoLeft + torsoWidth * 0.84f, torsoBottom
      )
      close()
    }

    // Shadow
    drawRoundRect(
      color = Color(0x330055AA),
      topLeft = Offset(torsoLeft + 2f, torsoTop + 2f),
      size = Size(torsoWidth - 4f, torsoHeight),
      cornerRadius = CornerRadius(torsoWidth * 0.3f, torsoWidth * 0.3f)
    )

    // 3D Blue Shading
    drawPath(
      path = torsoPath,
      brush = Brush.verticalGradient(
        colors = listOf(
          Color(0xFF00B0FF),
          Color(0xFF0091EA),
          Color(0xFF0277BD),
          Color(0xFF0D47A1)
        )
      )
    )

    // Top shoulder specular rim
    drawPath(
      path = torsoPath,
      color = Color.White.copy(alpha = 0.40f),
      style = Stroke(width = 1.4f)
    )
  }
}
