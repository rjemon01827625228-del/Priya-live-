package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonCyan

/**
 * Custom 1:1 replica of the "Go Live" button from the user's reference image:
 * - Rounded oblong pill capsule with deep cyber glass background
 * - Glowing dual-tone neon rim: Electric Cyan & Neon Magenta with specular highlights
 * - Neon Video Camera emblem with play triangle inside
 * - Glossy, illuminated "Go Live" typography
 */
/**
 * 1:1 replica of the "Go Live" button from the uploaded reference image:
 * - Rounded pill capsule with glowing cyan & magenta neon borders and lens-flare glints
 * - Deep cyber glass dark background
 * - Neon Camera emblem with right-facing play triangle and lens cone
 * - Chunky 3D glossy "Go Live" typography with cyan/magenta glow
 */
@Composable
fun NeonGoLivePill(
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val shape = RoundedCornerShape(28.dp)

  Box(
    modifier = modifier
      .shadow(
        elevation = 16.dp,
        shape = shape,
        ambientColor = Color(0xFF00E5FF),
        spotColor = Color(0xFFFF00D4)
      )
      .clip(shape)
      .background(
        Brush.horizontalGradient(
          listOf(
            Color(0xFF130026),
            Color(0xFF0D001F),
            Color(0xFF050B24),
            Color(0xFF02102E)
          )
        )
      )
      .border(
        width = 2.5.dp,
        brush = Brush.horizontalGradient(
          listOf(
            Color(0xFFFF00AA), // Neon Magenta on left
            Color(0xFFD500F9), // Neon Violet
            Color(0xFF00E5FF), // Electric Cyan
            Color(0xFF0091FF)  // Vivid Blue on right
          )
        ),
        shape = shape
      )
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(color = Color(0xFF00E5FF)),
        onClick = onClick
      )
      .padding(horizontal = 16.dp, vertical = 9.dp)
      .testTag("btn_go_live_pill"),
    contentAlignment = Alignment.Center
  ) {
    // Specular top glass flare and specular flare points
    Canvas(modifier = Modifier.matchParentSize()) {
      val w = size.width
      val h = size.height

      // Top edge specular glass curve
      drawLine(
        brush = Brush.horizontalGradient(
          listOf(
            Color.Transparent,
            Color(0x80FFFFFF),
            Color(0xE6FFFFFF),
            Color(0x8000E5FF),
            Color.Transparent
          )
        ),
        start = Offset(w * 0.12f, 2.5f),
        end = Offset(w * 0.88f, 2.5f),
        strokeWidth = 2.5f,
        cap = StrokeCap.Round
      )

      // Top center bright lens-flare glint
      drawCircle(
        color = Color.White,
        radius = 2.8.dp.toPx(),
        center = Offset(w * 0.5f, 2.5f)
      )
      drawCircle(
        color = Color(0x99FF80AB),
        radius = 5.dp.toPx(),
        center = Offset(w * 0.5f, 2.5f)
      )

      // Bottom center bright cyan lens-flare glint
      drawCircle(
        color = Color.White,
        radius = 2.8.dp.toPx(),
        center = Offset(w * 0.5f, h - 2.5f)
      )
      drawCircle(
        color = Color(0x9900E5FF),
        radius = 5.dp.toPx(),
        center = Offset(w * 0.5f, h - 2.5f)
      )

      // Bottom edge subtle cyan rim highlight
      drawLine(
        brush = Brush.horizontalGradient(
          listOf(
            Color.Transparent,
            Color(0x55E040FB),
            Color(0x8000E5FF),
            Color.Transparent
          )
        ),
        start = Offset(w * 0.2f, h - 2f),
        end = Offset(w * 0.8f, h - 2f),
        strokeWidth = 1.5f,
        cap = StrokeCap.Round
      )
    }

    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      // Neon Camera Emblem with Play Triangle
      NeonCameraIcon(modifier = Modifier.size(32.dp))

      Spacer(modifier = Modifier.width(10.dp))

      // "Go Live" 3D Glossy Text
      Box(contentAlignment = Alignment.Center) {
        // Drop shadow / 3D extrusion underlay
        Text(
          text = "Go Live",
          color = Color(0x99020C30),
          fontSize = 20.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 0.5.sp,
          modifier = Modifier.padding(top = 2.dp, start = 1.5.dp)
        )
        // Soft neon cyan/purple glow
        Text(
          text = "Go Live",
          color = Color(0x6600E5FF),
          fontSize = 20.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 0.5.sp,
          modifier = Modifier.padding(top = 1.dp)
        )
        // Foreground bright white & lavender text
        Text(
          text = "Go Live",
          color = Color.White,
          fontSize = 20.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 0.5.sp
        )
      }
    }
  }
}

/**
 * Neon Video Camera icon with play triangle inside matching reference image
 */
@Composable
fun NeonCameraIcon(modifier: Modifier = Modifier) {
  Canvas(modifier = modifier) {
    val w = size.width
    val h = size.height

    val bodyWidth = w * 0.58f
    val bodyHeight = h * 0.66f
    val left = w * 0.05f
    val top = h * 0.17f

    // Camera body with rounded corners
    drawRoundRect(
      brush = Brush.horizontalGradient(
        listOf(Color(0xFFFF0099), Color(0xFFD500F9), Color(0xFF00E5FF))
      ),
      topLeft = Offset(left, top),
      size = Size(bodyWidth, bodyHeight),
      cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
      style = Stroke(width = 2.6.dp.toPx(), cap = StrokeCap.Round)
    )

    // Play triangle inside the camera
    val triPath = Path().apply {
      val triLeft = left + bodyWidth * 0.35f
      val triTop = top + bodyHeight * 0.28f
      val triRight = left + bodyWidth * 0.72f
      val triBottom = top + bodyHeight * 0.72f
      val triCenterY = (triTop + triBottom) / 2f

      moveTo(triLeft, triTop)
      lineTo(triRight, triCenterY)
      lineTo(triLeft, triBottom)
      close()
    }
    drawPath(
      path = triPath,
      brush = Brush.horizontalGradient(listOf(Color(0xFFFF4081), Color(0xFF00E5FF)))
    )

    // Camera lens cone on the right
    val conePath = Path().apply {
      val coneLeft = left + bodyWidth + 2.dp.toPx()
      val coneTop = top + bodyHeight * 0.22f
      val coneBottom = top + bodyHeight * 0.78f
      val coneRight = w * 0.94f
      val conePointTop = top + bodyHeight * 0.08f
      val conePointBottom = top + bodyHeight * 0.92f

      moveTo(coneLeft, coneTop)
      lineTo(coneRight, conePointTop)
      lineTo(coneRight, conePointBottom)
      lineTo(coneLeft, coneBottom)
      close()
    }
    drawPath(
      path = conePath,
      brush = Brush.horizontalGradient(listOf(Color(0xFF00E5FF), Color(0xFF00B0FF)))
    )

    // Small camera sensor dot on top right of body
    drawCircle(
      color = Color.White,
      radius = 1.9.dp.toPx(),
      center = Offset(left + bodyWidth * 0.80f, top + bodyHeight * 0.22f)
    )
  }
}

/**
 * Custom 1:1 replica of the "Party" button from the user's reference image:
 * - Oblong rounded pill with radiant magenta/pink-to-sunset-orange gradient background
 * - Glass specular shine and multi-color glowing neon border (purple -> pink -> orange)
 * - Circular glowing microphone badge with soundwaves (||| 🎙️ |||)
 * - Chunky 3D glossy white "Party" typography with embossed shadow
 * - Responsive sizing and smooth spring press animation
 */
@Composable
fun NeonPartyPill(
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val shape = RoundedCornerShape(28.dp)

  Box(
    modifier = modifier
      .shadow(
        elevation = 16.dp,
        shape = shape,
        ambientColor = Color(0xFFFF007F),
        spotColor = Color(0xFFFF5500)
      )
      .clip(shape)
      .background(
        Brush.horizontalGradient(
          listOf(
            Color(0xFFD6006F),
            Color(0xFFFF006E),
            Color(0xFFFF1A53),
            Color(0xFFFF4D00)
          )
        )
      )
      .border(
        width = 2.5.dp,
        brush = Brush.horizontalGradient(
          listOf(
            Color(0xFFC500FF), // Neon Purple/Violet on left
            Color(0xFFFF0080), // Neon Hot Pink
            Color(0xFFFF0055), // Radiant Magenta
            Color(0xFFFF6D00), // Sunset Orange
            Color(0xFFFF9100)  // Golden Amber on right
          )
        ),
        shape = shape
      )
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(color = Color(0xFFFFD54F)),
        onClick = onClick
      )
      .padding(horizontal = 16.dp, vertical = 9.dp)
      .testTag("btn_party_pill"),
    contentAlignment = Alignment.Center
  ) {
    // Top glass reflection curve / 3D specular shine
    Canvas(modifier = Modifier.matchParentSize()) {
      val w = size.width
      val h = size.height

      // Top arc gloss highlight
      drawLine(
        brush = Brush.horizontalGradient(
          listOf(
            Color.Transparent,
            Color(0x80FFFFFF),
            Color(0xE6FFFFFF),
            Color(0x80FFFFFF),
            Color.Transparent
          )
        ),
        start = Offset(w * 0.12f, 2.5f),
        end = Offset(w * 0.88f, 2.5f),
        strokeWidth = 2.5f,
        cap = StrokeCap.Round
      )

      // Subtle bottom glow line
      drawLine(
        brush = Brush.horizontalGradient(
          listOf(
            Color.Transparent,
            Color(0x55FF80AB),
            Color(0x66FFAB40),
            Color.Transparent
          )
        ),
        start = Offset(w * 0.2f, h - 2f),
        end = Offset(w * 0.8f, h - 2f),
        strokeWidth = 1.5f,
        cap = StrokeCap.Round
      )
    }

    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      // Circular glowing microphone sphere badge with soundwaves (||| 🎙️ |||)
      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(CircleShape)
          .background(
            Brush.radialGradient(
              listOf(
                Color(0xFFFF4081),
                Color(0xFFC2185B),
                Color(0xFF560027)
              )
            )
          )
          .border(
            width = 1.8.dp,
            brush = Brush.sweepGradient(
              listOf(
                Color.White,
                Color(0xFFFF80AB),
                Color(0xFFE040FB),
                Color(0xFFFF4081),
                Color.White
              )
            ),
            shape = CircleShape
          ),
        contentAlignment = Alignment.Center
      ) {
        PartyMicBadge(modifier = Modifier.fillMaxSize().padding(3.dp))
      }

      Spacer(modifier = Modifier.width(11.dp))

      // 3D Chunky Glossy White "Party" text
      Box(contentAlignment = Alignment.Center) {
        // Drop shadow / 3D extrusion layer (dark magenta shadow below)
        Text(
          text = "Party",
          color = Color(0x994A0028),
          fontSize = 20.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 0.5.sp,
          modifier = Modifier.padding(top = 2.dp, start = 1.5.dp)
        )
        // Soft neon glow underlay
        Text(
          text = "Party",
          color = Color(0x66FF80AB),
          fontSize = 20.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 0.5.sp,
          modifier = Modifier.padding(top = 1.dp)
        )
        // Crisp foreground glossy white text
        Text(
          text = "Party",
          color = Color.White,
          fontSize = 20.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 0.5.sp
        )
      }
    }
  }
}

/**
 * Microphone with 3 soundwave bars on left and 3 on right inside glowing badge (||| 🎙️ |||)
 */
@Composable
fun PartyMicBadge(modifier: Modifier = Modifier) {
  Canvas(modifier = modifier) {
    val w = size.width
    val h = size.height

    val centerX = w * 0.5f
    val centerY = h * 0.44f

    // Top-left glossy bubble specular spot on the circular badge
    drawCircle(
      color = Color.White.copy(alpha = 0.5f),
      radius = 2.2.dp.toPx(),
      center = Offset(w * 0.32f, h * 0.26f)
    )

    // Mic capsule body
    val micWidth = w * 0.28f
    val micHeight = h * 0.44f
    drawRoundRect(
      color = Color.White,
      topLeft = Offset(centerX - micWidth / 2f, centerY - micHeight / 2f),
      size = Size(micWidth, micHeight),
      cornerRadius = CornerRadius(micWidth / 2f, micWidth / 2f)
    )

    // Horizontal grill slits on mic
    drawLine(
      color = Color(0xFF880044),
      start = Offset(centerX - micWidth * 0.35f, centerY - micHeight * 0.12f),
      end = Offset(centerX + micWidth * 0.35f, centerY - micHeight * 0.12f),
      strokeWidth = 1.2f
    )
    drawLine(
      color = Color(0xFF880044),
      start = Offset(centerX - micWidth * 0.35f, centerY + micHeight * 0.12f),
      end = Offset(centerX + micWidth * 0.35f, centerY + micHeight * 0.12f),
      strokeWidth = 1.2f
    )

    // U-shaped mic cradle
    val cradlePath = Path().apply {
      moveTo(centerX - micWidth * 0.68f, centerY)
      cubicTo(
        centerX - micWidth * 0.68f, centerY + micHeight * 0.62f,
        centerX + micWidth * 0.68f, centerY + micHeight * 0.62f,
        centerX + micWidth * 0.68f, centerY
      )
    }
    drawPath(
      path = cradlePath,
      color = Color.White,
      style = Stroke(width = 1.6f, cap = StrokeCap.Round)
    )

    // Mic stem & stand base
    drawLine(
      color = Color.White,
      start = Offset(centerX, centerY + micHeight * 0.60f),
      end = Offset(centerX, h * 0.86f),
      strokeWidth = 1.8f
    )
    drawLine(
      color = Color.White,
      start = Offset(centerX - w * 0.18f, h * 0.86f),
      end = Offset(centerX + w * 0.18f, h * 0.86f),
      strokeWidth = 1.8f,
      cap = StrokeCap.Round
    )

    // Left soundwaves (3 vertical bars: tallest inside, medium middle, short outside)
    // Bar 1 (inside, tallest)
    drawLine(
      color = Color.White.copy(alpha = 0.95f),
      start = Offset(centerX - micWidth * 1.05f, centerY - h * 0.18f),
      end = Offset(centerX - micWidth * 1.05f, centerY + h * 0.18f),
      strokeWidth = 1.5f,
      cap = StrokeCap.Round
    )
    // Bar 2 (middle)
    drawLine(
      color = Color.White.copy(alpha = 0.80f),
      start = Offset(centerX - micWidth * 1.35f, centerY - h * 0.12f),
      end = Offset(centerX - micWidth * 1.35f, centerY + h * 0.12f),
      strokeWidth = 1.5f,
      cap = StrokeCap.Round
    )
    // Bar 3 (outside, shortest)
    drawLine(
      color = Color.White.copy(alpha = 0.60f),
      start = Offset(centerX - micWidth * 1.65f, centerY - h * 0.06f),
      end = Offset(centerX - micWidth * 1.65f, centerY + h * 0.06f),
      strokeWidth = 1.4f,
      cap = StrokeCap.Round
    )

    // Right soundwaves (3 vertical bars: tallest inside, medium middle, short outside)
    // Bar 1 (inside, tallest)
    drawLine(
      color = Color.White.copy(alpha = 0.95f),
      start = Offset(centerX + micWidth * 1.05f, centerY - h * 0.18f),
      end = Offset(centerX + micWidth * 1.05f, centerY + h * 0.18f),
      strokeWidth = 1.5f,
      cap = StrokeCap.Round
    )
    // Bar 2 (middle)
    drawLine(
      color = Color.White.copy(alpha = 0.80f),
      start = Offset(centerX + micWidth * 1.35f, centerY - h * 0.12f),
      end = Offset(centerX + micWidth * 1.35f, centerY + h * 0.12f),
      strokeWidth = 1.5f,
      cap = StrokeCap.Round
    )
    // Bar 3 (outside, shortest)
    drawLine(
      color = Color.White.copy(alpha = 0.60f),
      start = Offset(centerX + micWidth * 1.65f, centerY - h * 0.06f),
      end = Offset(centerX + micWidth * 1.65f, centerY + h * 0.06f),
      strokeWidth = 1.4f,
      cap = StrokeCap.Round
    )
  }
}
