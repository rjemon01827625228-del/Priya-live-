package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.ui.theme.CyberBgDark
import com.example.ui.theme.CyberBgDarker
import com.example.ui.theme.CyberBgGradientEnd
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink

/**
 * CyberBackground renders the deep dark purple/black gradient background
 * with flowing glowing neon pink and cyan curved light waves.
 */
@Composable
fun CyberBackground(
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit
) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            CyberBgDark,
            CyberBgGradientEnd,
            CyberBgDarker
          )
        )
      )
  ) {
    // Canvas drawing ambient glowing waves and neon accent curves
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height

      // Top-left glowing arc
      val pathTopLeft = Path().apply {
        moveTo(0f, h * 0.15f)
        cubicTo(
          w * 0.45f, h * 0.02f,
          w * 0.25f, -h * 0.08f,
          w * 0.55f, 0f
        )
      }
      drawPath(
        path = pathTopLeft,
        brush = Brush.radialGradient(
          colors = listOf(NeonPink.copy(alpha = 0.55f), Color.Transparent),
          center = Offset(w * 0.2f, h * 0.05f),
          radius = w * 0.6f
        ),
        style = Stroke(width = 8f)
      )
      drawPath(
        path = pathTopLeft,
        brush = Brush.horizontalGradient(
          listOf(NeonPink.copy(alpha = 0.8f), NeonCyan.copy(alpha = 0.3f), Color.Transparent)
        ),
        style = Stroke(width = 3f)
      )

      // Top decorative glowing circle slice
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(Color(0xFF8A00D4).copy(alpha = 0.35f), Color.Transparent),
          center = Offset(0f, 0f),
          radius = w * 0.65f
        ),
        radius = w * 0.65f,
        center = Offset(0f, 0f)
      )

      // Bottom-left to bottom-right glowing cyber swoosh
      val pathBottom = Path().apply {
        moveTo(0f, h * 0.72f)
        cubicTo(
          w * 0.3f, h * 0.82f,
          w * 0.7f, h * 0.95f,
          w, h * 0.82f
        )
      }
      drawPath(
        path = pathBottom,
        brush = Brush.horizontalGradient(
          colors = listOf(
            NeonPink.copy(alpha = 0.85f),
            Color(0xFFB026FF).copy(alpha = 0.6f),
            NeonCyan.copy(alpha = 0.5f)
          )
        ),
        style = Stroke(width = 3.5f)
      )

      // Secondary soft glow path
      val pathBottomGlow = Path().apply {
        moveTo(0f, h * 0.72f)
        cubicTo(
          w * 0.3f, h * 0.82f,
          w * 0.7f, h * 0.95f,
          w, h * 0.82f
        )
      }
      drawPath(
        path = pathBottomGlow,
        brush = Brush.horizontalGradient(
          colors = listOf(
            NeonPink.copy(alpha = 0.3f),
            Color(0xFFB026FF).copy(alpha = 0.2f),
            NeonCyan.copy(alpha = 0.1f)
          )
        ),
        style = Stroke(width = 12f)
      )

      // Lower corner ambient glow
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(NeonPink.copy(alpha = 0.25f), Color.Transparent),
          center = Offset(0f, h * 0.75f),
          radius = w * 0.45f
        ),
        radius = w * 0.45f,
        center = Offset(0f, h * 0.75f)
      )
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(Color(0xFF8A00D4).copy(alpha = 0.25f), Color.Transparent),
          center = Offset(w, h * 0.85f),
          radius = w * 0.5f
        ),
        radius = w * 0.5f,
        center = Offset(w, h * 0.85f)
      )
    }

    content()
  }
}
