package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Official Level Badge component conforming strictly to PRIYA LIVE design guidelines:
 * - Shows ONLY the level (e.g. LV.1, LV.10, LV.20, LV.35, LV.50)
 * - Automatic professional color tier styling (Tiers 1-5 and Level 50 Premium Gold)
 * - Professional vector-aligned aesthetics (no stickers, no emojis, no random icons)
 */
@Composable
fun OfficialLevelBadge(
  level: Int,
  modifier: Modifier = Modifier,
  fontSize: TextUnit = 10.sp
) {
  val clampedLevel = level.coerceIn(1, 50)

  // Color Tiers based on Level 1-50 Progression:
  // Tier 1: Level 1–10 (Teal / Emerald)
  // Tier 2: Level 11–20 (Sapphire / Ocean Blue)
  // Tier 3: Level 21–30 (Amethyst / Royal Purple)
  // Tier 4: Level 31–40 (Vivid Amber / Radiant Sunset)
  // Tier 5: Level 41–49 (Crimson Ruby / Emperor Red)
  // Tier 6: Level 50 (Supreme Gold & Red-Gold Celestial Tier)
  val (gradient, borderColor, textColor, glowColor) = when {
    clampedLevel >= 50 -> Quad(
      Brush.linearGradient(
        listOf(
          Color(0xFF8E0E00),
          Color(0xFFE52D27),
          Color(0xFFFFB300),
          Color(0xFFFFD700),
          Color(0xFFFFF9C4)
        )
      ),
      Color(0xFFFFF59D),
      Color(0xFF261200),
      Color(0xFFFFD700)
    )
    clampedLevel >= 41 -> Quad(
      Brush.linearGradient(
        listOf(Color(0xFF880E4F), Color(0xFFC2185B), Color(0xFFFF1744))
      ),
      Color(0xFFFF80AB),
      Color.White,
      Color(0xFFFF1744)
    )
    clampedLevel >= 31 -> Quad(
      Brush.linearGradient(
        listOf(Color(0xFFBF360C), Color(0xFFE65100), Color(0xFFFF8F00))
      ),
      Color(0xFFFFD54F),
      Color.White,
      Color(0xFFFF8F00)
    )
    clampedLevel >= 21 -> Quad(
      Brush.linearGradient(
        listOf(Color(0xFF4A148C), Color(0xFF7B1FA2), Color(0xFFAB47BC))
      ),
      Color(0xFFEA80FC),
      Color.White,
      Color(0xFFAB47BC)
    )
    clampedLevel >= 11 -> Quad(
      Brush.linearGradient(
        listOf(Color(0xFF0D47A1), Color(0xFF1976D2), Color(0xFF00B0FF))
      ),
      Color(0xFF80D8FF),
      Color.White,
      Color(0xFF00B0FF)
    )
    else -> Quad(
      Brush.linearGradient(
        listOf(Color(0xFF004D40), Color(0xFF00796B), Color(0xFF00BFA5))
      ),
      Color(0xFF64FFDA),
      Color.White,
      Color(0xFF00BFA5)
    )
  }

  val shape = RoundedCornerShape(6.dp)

  Box(
    modifier = modifier
      .shadow(
        elevation = if (clampedLevel >= 50) 6.dp else 2.dp,
        shape = shape,
        spotColor = glowColor,
        ambientColor = glowColor.copy(alpha = 0.4f)
      )
      .clip(shape)
      .background(gradient)
      .border(0.9.dp, borderColor, shape)
      .padding(horizontal = 6.dp, vertical = 2.dp)
      .testTag("official_level_badge_$clampedLevel"),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = "LV.$clampedLevel",
      color = textColor,
      fontSize = fontSize,
      fontWeight = FontWeight.Black,
      letterSpacing = 0.5.sp
    )
  }
}

private data class Quad<A, B, C, D>(
  val first: A,
  val second: B,
  val third: C,
  val fourth: D
)
