package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.example.ui.components.rememberResponsiveNavigationBarPadding
import com.example.ui.components.rememberResponsiveTopBarPadding
import com.example.ui.storage.LiveRoomStorage
import com.example.ui.storage.UserProfileStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

private data class StarParticle(
  val angleOffset: Float,
  val radiusFactor: Float,
  val speed: Float,
  val sizeDp: Float,
  val color: Color,
  val verticalDrift: Float
)

/**
 * Premium Cinematic 3D Panda Entry / Startup Experience for Priya Live (~4.5 seconds).
 *
 * Features a cute, glossy 3D panda in the center against a dark-purple background
 * with smooth pink and cyan-blue neon lighting, holding and magically presenting
 * the exact text "Priya Live" in its hands, accompanied by a straight bottom
 * horizontal loading bar synchronized with real app startup initialization.
 */
@Composable
fun SplashEntryScreen(
  onAnimationFinished: () -> Unit
) {
  val context = LocalContext.current
  val topSafePadding = rememberResponsiveTopBarPadding(extraTopSpacing = 12.dp)
  val bottomSafePadding = rememberResponsiveNavigationBarPadding(extraBottomSpacing = 18.dp)

  val screenAlpha = remember { Animatable(1f) }
  val pandaEntryScale = remember { Animatable(0.76f) }
  val pandaEntryAlpha = remember { Animatable(0f) }
  val titleRevealProgress = remember { Animatable(0f) }

  // Guard to prevent duplicate navigation or repeated callback triggers
  var hasNavigated by remember { mutableStateOf(false) }

  // Synchronized startup progress state (0f..1f) driven by both real app init & 4.5s timeline
  var targetProgress by remember { mutableFloatStateOf(0.02f) }
  val animatedProgress by animateFloatAsState(
    targetValue = targetProgress,
    animationSpec = tween(durationMillis = 180, easing = LinearEasing),
    label = "real_init_progress"
  )

  val infiniteTransition = rememberInfiniteTransition(label = "priya_panda_3d_anim")

  val phase by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = (2f * PI).toFloat(),
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 3600, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "wave_phase"
  )

  val orbitRotation by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 8000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "orbit_rotation"
  )

  val shimmerFlow by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1500, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "shimmer_flow"
  )

  val blinkCycle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 2800, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "panda_blink"
  )

  val particles = remember {
    listOf(
      StarParticle(0.0f, 0.20f, 1.0f, 3.4f, Color(0xFFE040FB), 0.14f),
      StarParticle(0.7f, 0.32f, 0.8f, 2.5f, Color(0xFF00E5FF), -0.18f),
      StarParticle(1.5f, 0.25f, 1.2f, 3.0f, Color(0xFFFF2A8D), 0.22f),
      StarParticle(2.2f, 0.39f, 0.85f, 2.2f, Color(0xFFB388FF), -0.16f),
      StarParticle(3.0f, 0.18f, 1.15f, 3.6f, Color(0xFF00E5FF), 0.12f),
      StarParticle(3.8f, 0.35f, 0.9f, 2.7f, Color(0xFFFF4081), -0.20f),
      StarParticle(4.5f, 0.29f, 1.25f, 2.4f, Color(0xFF7C4DFF), 0.24f),
      StarParticle(5.2f, 0.43f, 0.8f, 3.1f, Color(0xFFE040FB), -0.14f),
      StarParticle(1.1f, 0.15f, 1.35f, 2.6f, Color(0xFF80D8FF), 0.18f),
      StarParticle(2.7f, 0.34f, 1.05f, 2.9f, Color(0xFFEA80FC), -0.21f),
      StarParticle(4.1f, 0.23f, 0.95f, 2.3f, Color(0xFF00B0FF), 0.15f),
      StarParticle(5.8f, 0.38f, 1.1f, 3.3f, Color(0xFFFF80AB), -0.16f)
    )
  }

  // ~4.5-Second Total Entry Animation & Real Application Initialization Pipeline
  LaunchedEffect(Unit) {
    coroutineScope {
      // Stage 1: Panda 3D entrance (0.0s -> 0.65s)
      launch {
        pandaEntryAlpha.animateTo(1f, animationSpec = tween(500, easing = FastOutSlowInEasing))
      }
      launch {
        pandaEntryScale.animateTo(1f, animationSpec = tween(680, easing = FastOutSlowInEasing))
      }
      // Stage 2: Panda paws lift & magically present "Priya Live" (0.35s -> 1.55s)
      launch {
        delay(350)
        titleRevealProgress.animateTo(1f, animationSpec = tween(1200, easing = FastOutSlowInEasing))
      }

      // Real background initialization tasks running off the main thread
      val initDeferred = async(Dispatchers.IO) {
        UserProfileStorage.isLoggedIn(context)
        UserProfileStorage.getActiveUserId(context)
        UserProfileStorage.getProfile(context)
        LiveRoomStorage.getActiveRooms(context)
        true
      }

      // Smooth left-to-right progress synchronized across ~4.15s + 0.35s fade = ~4.5s total
      val totalSteps = 83
      val stepDelayMs = 50L // 83 * 50ms = 4150ms
      for (step in 1..totalSteps) {
        delay(stepDelayMs)
        val timelineFraction = step.toFloat() / totalSteps.toFloat()
        // Hold at 0.96f until real initialization is confirmed complete
        targetProgress = if (initDeferred.isCompleted) {
          timelineFraction
        } else {
          timelineFraction.coerceAtMost(0.94f)
        }
      }

      // Ensure real startup work has finished before completing 100%
      withContext(Dispatchers.IO) {
        initDeferred.await()
      }
      targetProgress = 1.0f
      delay(120)
    }

    // Smooth fade-out into the existing App (~300ms, total ~4.5s)
    screenAlpha.animateTo(0f, animationSpec = tween(280, easing = FastOutSlowInEasing))
    if (!hasNavigated) {
      hasNavigated = true
      onAnimationFinished()
    }
  }

  BoxWithConstraints(
    modifier = Modifier
      .fillMaxSize()
      .alpha(screenAlpha.value)
      .background(
        Brush.verticalGradient(
          colors = listOf(
            Color(0xFF05010E),
            Color(0xFF120428),
            Color(0xFF1B073B),
            Color(0xFF0C021E),
            Color(0xFF030008)
          )
        )
      )
      .testTag("splash_entry_screen")
  ) {
    val minDim = min(maxWidth, maxHeight)
    val pandaStageSize = (minDim * 0.78f).coerceIn(240.dp, 360.dp)
    val titleFontSize = if (minDim < 360.dp) 34.sp else 40.sp

    // 1. Background 3D Volumetric Neon Lighting, Sparkles & Energy Waves
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height
      val cx = w * 0.5f
      val cy = h * 0.45f

      // Left-side Pink/Magenta volumetric neon spotlight
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            Color(0xFFFF2A8D).copy(alpha = 0.26f + 0.06f * sin(phase)),
            Color(0xFF9C27B0).copy(alpha = 0.14f),
            Color.Transparent
          ),
          center = Offset(cx - w * 0.18f, cy - h * 0.06f),
          radius = w * 0.68f
        ),
        radius = w * 0.68f,
        center = Offset(cx - w * 0.18f, cy - h * 0.06f)
      )

      // Right-side Cyan/Blue volumetric neon spotlight
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            Color(0xFF00E5FF).copy(alpha = 0.24f + 0.06f * cos(phase)),
            Color(0xFF651FFF).copy(alpha = 0.14f),
            Color.Transparent
          ),
          center = Offset(cx + w * 0.18f, cy + h * 0.04f),
          radius = w * 0.66f
        ),
        radius = w * 0.66f,
        center = Offset(cx + w * 0.18f, cy + h * 0.04f)
      )

      // Center warm-violet magical core aura behind the 3D Panda
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            Color(0xFFE040FB).copy(alpha = 0.22f * titleRevealProgress.value),
            Color(0xFF4A0072).copy(alpha = 0.14f),
            Color.Transparent
          ),
          center = Offset(cx, cy),
          radius = w * 0.58f
        ),
        radius = w * 0.58f,
        center = Offset(cx, cy)
      )

      // 3D Holographic Light Rings orbiting the Panda & "Priya Live"
      rotate(degrees = orbitRotation * 0.3f, pivot = Offset(cx, cy + h * 0.05f)) {
        drawOval(
          brush = Brush.sweepGradient(
            colors = listOf(
              Color.Transparent,
              Color(0xFFE040FB).copy(alpha = 0.38f),
              Color(0xFF00E5FF).copy(alpha = 0.52f),
              Color.Transparent,
              Color(0xFFFF2A8D).copy(alpha = 0.36f),
              Color.Transparent
            ),
            center = Offset(cx, cy + h * 0.05f)
          ),
          topLeft = Offset(cx - w * 0.43f, cy + h * 0.05f - h * 0.065f),
          size = Size(w * 0.86f, h * 0.13f),
          style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )
      }

      // Floating 3D Sparkling Particles & Four-Point Star Gleams
      particles.forEachIndexed { index, p ->
        val currentAngle = p.angleOffset + phase * p.speed
        val depthZ = (sin(currentAngle) + 1.4f) / 2.4f
        val rx = w * p.radiusFactor * cos(currentAngle)
        val ry = h * (p.radiusFactor * 0.48f) * sin(currentAngle * 0.85f) +
          h * p.verticalDrift * sin(phase * 0.7f + p.angleOffset)
        val px = cx + rx
        val py = cy + ry
        val radiusPx = (p.sizeDp * (0.65f + 0.65f * depthZ)).dp.toPx()

        // Outer neon particle aura
        drawCircle(
          color = p.color.copy(alpha = (0.26f * depthZ).coerceIn(0.06f, 0.5f)),
          radius = radiusPx * 2.8f,
          center = Offset(px, py)
        )
        // Bright sparkle core
        drawCircle(
          color = Color.White.copy(alpha = (0.82f * depthZ).coerceIn(0.25f, 0.98f)),
          radius = radiusPx * 0.85f,
          center = Offset(px, py)
        )

        // Subtle 4-point star flare on every 3rd particle
        if (index % 3 == 0 && depthZ > 0.55f) {
          val flareLen = radiusPx * 3.4f
          drawLine(
            color = Color.White.copy(alpha = 0.55f * depthZ),
            start = Offset(px - flareLen, py),
            end = Offset(px + flareLen, py),
            strokeWidth = 1.1.dp.toPx(),
            cap = StrokeCap.Round
          )
          drawLine(
            color = Color.White.copy(alpha = 0.55f * depthZ),
            start = Offset(px, py - flareLen),
            end = Offset(px, py + flareLen),
            strokeWidth = 1.1.dp.toPx(),
            cap = StrokeCap.Round
          )
        }
      }
    }

    // 2. Center Stage: Cute Glossy 3D Panda Holding & Presenting "Priya Live"
    val floatOffsetY = 5f * sin(phase)
    val tiltX = 4.5f * sin(phase * 0.9f)
    val tiltY = 6.5f * cos(phase * 0.75f)
    // Smooth eye blink when blinkCycle passes through 0.92..0.98
    val eyeOpenFraction = remember(blinkCycle) {
      if (blinkCycle in 0.91f..0.99f) {
        val mid = 0.95f
        val dist = abs(blinkCycle - mid) / 0.04f
        dist.coerceIn(0.08f, 1f)
      } else {
        1f
      }
    }

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(top = topSafePadding, bottom = bottomSafePadding + 42.dp, start = 20.dp, end = 20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Box(
        modifier = Modifier
          .size(pandaStageSize)
          .graphicsLayer {
            scaleX = pandaEntryScale.value
            scaleY = pandaEntryScale.value
            alpha = pandaEntryAlpha.value
            translationY = floatOffsetY.dp.toPx()
            rotationX = tiltX
            rotationY = tiltY
            cameraDistance = 18f * density
          },
        contentAlignment = Alignment.Center
      ) {
        // 3D Glossy Panda Character Canvas (Body, Head, Ears, Eyes, and Cupped Paws)
        Canvas(modifier = Modifier.fillMaxSize()) {
          drawGlossy3DPanda(
            phase = phase,
            revealProgress = titleRevealProgress.value,
            eyeOpenFraction = eyeOpenFraction,
            shimmerFlow = shimmerFlow
          )
        }

        // Exact "Priya Live" 3D Magical Text Held & Presented in the Panda's Hands
        val reveal = titleRevealProgress.value
        val textLiftY = (1f - reveal) * 18f
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .align(Alignment.Center)
            .offset(y = (pandaStageSize * 0.14f) + textLiftY.dp)
            .graphicsLayer {
              val scale = 0.72f + 0.28f * reveal
              scaleX = scale
              scaleY = scale
              alpha = reveal
              rotationX = tiltX * 0.6f
              rotationY = tiltY * 0.7f
              cameraDistance = 14f * density
            }
        ) {
          // Deep 3D extruded dark-magenta shadow layer
          Text(
            text = "Priya Live",
            fontSize = titleFontSize,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.5.sp,
            textAlign = TextAlign.Center,
            color = Color(0xFF1E0238).copy(alpha = 0.88f),
            modifier = Modifier.graphicsLayer {
              translationX = 3.5.dp.toPx()
              translationY = 5.dp.toPx()
            }
          )

          // Mid 3D neon pink/cyan outer bloom layer
          Text(
            text = "Priya Live",
            fontSize = titleFontSize,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.5.sp,
            textAlign = TextAlign.Center,
            style = TextStyle(
              color = Color(0xFFFF2A8D).copy(alpha = 0.65f),
              shadow = Shadow(
                color = Color(0xFF00E5FF).copy(alpha = 0.9f),
                offset = Offset(0f, 0f),
                blurRadius = 38f
              )
            )
          )

          // Primary foreground luminous 3D "Priya Live" text with animated specular sheen
          Text(
            text = "Priya Live",
            fontSize = titleFontSize,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.5.sp,
            textAlign = TextAlign.Center,
            style = TextStyle(
              brush = Brush.linearGradient(
                colors = listOf(
                  Color(0xFFFFFFFF),
                  Color(0xFFFFB2EE),
                  Color(0xFFFF2A8D),
                  Color(0xFF00E5FF),
                  Color(0xFFFFFFFF)
                ),
                start = Offset(360f * shimmerFlow - 120f, 0f),
                end = Offset(360f * shimmerFlow + 240f, 90f)
              ),
              shadow = Shadow(
                color = Color(0xFFE040FB).copy(alpha = 0.9f),
                offset = Offset(0f, 3f),
                blurRadius = 22f
              )
            ),
            modifier = Modifier.testTag("splash_title_text")
          )
        }
      }
    }

    // 3. Bottom Straight Horizontal Loading Bar (Respecting Safe Area)
    Box(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth()
        .padding(bottom = bottomSafePadding, start = 32.dp, end = 32.dp),
      contentAlignment = Alignment.Center
    ) {
      Canvas(
        modifier = Modifier
          .fillMaxWidth()
          .height(16.dp)
          .alpha(pandaEntryAlpha.value)
          .testTag("splash_spinner")
      ) {
        val barWidth = size.width
        val barHeight = 5.dp.toPx()
        val centerY = size.height / 2f
        val cornerRadius = CornerRadius(barHeight / 2f, barHeight / 2f)

        // Straight dark-purple glass track
        drawRoundRect(
          color = Color(0xFF260A45).copy(alpha = 0.72f),
          topLeft = Offset(0f, centerY - barHeight / 2f),
          size = Size(barWidth, barHeight),
          cornerRadius = cornerRadius
        )

        val activeWidth = (barWidth * animatedProgress.coerceIn(0.02f, 1f))

        // Subtle outer purple-pink-cyan neon glow along active bar
        drawRoundRect(
          brush = Brush.horizontalGradient(
            colors = listOf(
              Color(0xFF7C4DFF).copy(alpha = 0.36f),
              Color(0xFFE040FB).copy(alpha = 0.52f),
              Color(0xFFFF2A8D).copy(alpha = 0.56f),
              Color(0xFF00E5FF).copy(alpha = 0.65f)
            ),
            startX = 0f,
            endX = activeWidth.coerceAtLeast(1f)
          ),
          topLeft = Offset(0f, centerY - barHeight * 1.35f),
          size = Size(activeWidth, barHeight * 2.7f),
          cornerRadius = CornerRadius(barHeight * 1.35f, barHeight * 1.35f)
        )

        // Straight left-to-right filling bar with purple, pink, and cyan gradient
        drawRoundRect(
          brush = Brush.horizontalGradient(
            colors = listOf(
              Color(0xFF7C4DFF),
              Color(0xFFE040FB),
              Color(0xFFFF2A8D),
              Color(0xFF00E5FF)
            ),
            startX = 0f,
            endX = activeWidth.coerceAtLeast(1f)
          ),
          topLeft = Offset(0f, centerY - barHeight / 2f),
          size = Size(activeWidth, barHeight),
          cornerRadius = cornerRadius
        )

        // Subtle leading-edge glow spark at the current fill position
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(
              Color.White,
              Color(0xFF00E5FF).copy(alpha = 0.85f),
              Color.Transparent
            ),
            center = Offset(activeWidth, centerY),
            radius = 8.dp.toPx()
          ),
          radius = 8.dp.toPx(),
          center = Offset(activeWidth, centerY)
        )
      }
    }
  }
}

/**
 * Renders a cute, glossy 3D-shaded Panda character with studio lighting,
 * pink & blue neon rim reflections, soft 3D depth, and cupped paws holding
 * and presenting the "Priya Live" emblem in its hands.
 */
private fun DrawScope.drawGlossy3DPanda(
  phase: Float,
  revealProgress: Float,
  eyeOpenFraction: Float,
  shimmerFlow: Float
) {
  val w = size.width
  val h = size.height
  val cx = w * 0.5f
  val breathScale = 1f + 0.015f * sin(phase)
  val headCenterY = h * 0.36f
  val bodyCenterY = h * 0.65f

  // 1. 3D Floor Pedestal / Ambient Shadow & Neon Reflection Pool under the Panda
  drawOval(
    brush = Brush.radialGradient(
      colors = listOf(
        Color(0xFF00E5FF).copy(alpha = 0.28f),
        Color(0xFFE040FB).copy(alpha = 0.18f),
        Color.Transparent
      ),
      center = Offset(cx, h * 0.88f),
      radius = w * 0.38f
    ),
    topLeft = Offset(cx - w * 0.36f, h * 0.82f),
    size = Size(w * 0.72f, h * 0.11f)
  )

  // 2. 3D Glossy Round Panda Ears (Left & Right) with Pink & Cyan Neon Rim Highlights
  val earRadius = w * 0.115f
  val leftEarCenter = Offset(cx - w * 0.19f, headCenterY - h * 0.165f)
  val rightEarCenter = Offset(cx + w * 0.19f, headCenterY - h * 0.165f)

  // Left Ear Outer 3D Sphere (Obsidian Velvet + Pink Neon Rim)
  drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(
        Color(0xFF3A2D4C),
        Color(0xFF181124),
        Color(0xFF090510)
      ),
      center = Offset(leftEarCenter.x - earRadius * 0.25f, leftEarCenter.y - earRadius * 0.3f),
      radius = earRadius * 1.3f
    ),
    radius = earRadius,
    center = leftEarCenter
  )
  // Left Ear Pink Rim Light
  drawCircle(
    brush = Brush.linearGradient(
      colors = listOf(Color(0xFFFF2A8D).copy(alpha = 0.7f), Color.Transparent),
      start = Offset(leftEarCenter.x - earRadius, leftEarCenter.y - earRadius),
      end = Offset(leftEarCenter.x + earRadius * 0.4f, leftEarCenter.y + earRadius * 0.4f)
    ),
    radius = earRadius,
    center = leftEarCenter,
    style = Stroke(width = 2.2.dp.toPx())
  )
  // Left Inner Ear Glowing Concave Recess
  drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(
        Color(0xFFFF6AC1).copy(alpha = 0.55f),
        Color(0xFF431C53).copy(alpha = 0.7f),
        Color(0xFF140B20)
      ),
      center = Offset(leftEarCenter.x + earRadius * 0.1f, leftEarCenter.y + earRadius * 0.1f),
      radius = earRadius * 0.58f
    ),
    radius = earRadius * 0.54f,
    center = Offset(leftEarCenter.x + earRadius * 0.06f, leftEarCenter.y + earRadius * 0.08f)
  )

  // Right Ear Outer 3D Sphere (Obsidian Velvet + Cyan Neon Rim)
  drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(
        Color(0xFF343A52),
        Color(0xFF161326),
        Color(0xFF090510)
      ),
      center = Offset(rightEarCenter.x - earRadius * 0.2f, rightEarCenter.y - earRadius * 0.3f),
      radius = earRadius * 1.3f
    ),
    radius = earRadius,
    center = rightEarCenter
  )
  // Right Ear Cyan Rim Light
  drawCircle(
    brush = Brush.linearGradient(
      colors = listOf(Color(0xFF00E5FF).copy(alpha = 0.75f), Color.Transparent),
      start = Offset(rightEarCenter.x + earRadius, rightEarCenter.y - earRadius),
      end = Offset(rightEarCenter.x - earRadius * 0.4f, rightEarCenter.y + earRadius * 0.4f)
    ),
    radius = earRadius,
    center = rightEarCenter,
    style = Stroke(width = 2.2.dp.toPx())
  )
  // Right Inner Ear Glowing Concave Recess
  drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(
        Color(0xFF70F3FF).copy(alpha = 0.48f),
        Color(0xFF3B1E54).copy(alpha = 0.7f),
        Color(0xFF140B20)
      ),
      center = Offset(rightEarCenter.x - earRadius * 0.1f, rightEarCenter.y + earRadius * 0.1f),
      radius = earRadius * 0.58f
    ),
    radius = earRadius * 0.54f,
    center = Offset(rightEarCenter.x - earRadius * 0.06f, rightEarCenter.y + earRadius * 0.08f)
  )

  // 3. 3D Chubby Torso / Body (Porcelain White with Lavender-Violet 3D Shading & Dark Shoulder Band)
  val bodyWidth = w * 0.46f * breathScale
  val bodyHeight = h * 0.36f * breathScale
  val bodyTopLeft = Offset(cx - bodyWidth * 0.5f, bodyCenterY - bodyHeight * 0.48f)

  // Main 3D White Torso Sphere/Oval
  drawOval(
    brush = Brush.radialGradient(
      colors = listOf(
        Color(0xFFFFFFFF),
        Color(0xFFF6F0FF),
        Color(0xFFD9C9F8),
        Color(0xFF9C82D4),
        Color(0xFF5D4199)
      ),
      center = Offset(cx - bodyWidth * 0.1f, bodyCenterY - bodyHeight * 0.08f),
      radius = bodyWidth * 0.65f
    ),
    topLeft = bodyTopLeft,
    size = Size(bodyWidth, bodyHeight)
  )

  // Glossy Dark 3D Shoulder/Upper-Chest Band (Classic Panda Markings)
  val chestBandHeight = bodyHeight * 0.44f
  drawOval(
    brush = Brush.verticalGradient(
      colors = listOf(
        Color(0xFF2F2342),
        Color(0xFF160E24),
        Color(0xFF0A0612)
      ),
      startY = bodyTopLeft.y,
      endY = bodyTopLeft.y + chestBandHeight
    ),
    topLeft = Offset(cx - bodyWidth * 0.47f, bodyTopLeft.y + bodyHeight * 0.02f),
    size = Size(bodyWidth * 0.94f, chestBandHeight)
  )

  // Rounded White Belly Patch Overlapping Lower Torso
  drawOval(
    brush = Brush.radialGradient(
      colors = listOf(
        Color(0xFFFFFFFF),
        Color(0xFFF5EEFF),
        Color(0xFFD7C6F6),
        Color(0xFFA088D6)
      ),
      center = Offset(cx - bodyWidth * 0.06f, bodyCenterY + bodyHeight * 0.05f),
      radius = bodyWidth * 0.46f
    ),
    topLeft = Offset(cx - bodyWidth * 0.40f, bodyCenterY - bodyHeight * 0.16f),
    size = Size(bodyWidth * 0.80f, bodyHeight * 0.60f)
  )

  // Cute Lower Seated Feet / Paws (Left & Right Bottom)
  val footWidth = w * 0.15f
  val footHeight = h * 0.105f
  val leftFootCenter = Offset(cx - w * 0.155f, h * 0.80f)
  val rightFootCenter = Offset(cx + w * 0.155f, h * 0.80f)
  listOf(leftFootCenter, rightFootCenter).forEachIndexed { idx, footCenter ->
    drawOval(
      brush = Brush.radialGradient(
        colors = listOf(Color(0xFF392E4E), Color(0xFF181124), Color(0xFF08040E)),
        center = Offset(footCenter.x - footWidth * 0.15f, footCenter.y - footHeight * 0.2f),
        radius = footWidth * 0.7f
      ),
      topLeft = Offset(footCenter.x - footWidth * 0.5f, footCenter.y - footHeight * 0.5f),
      size = Size(footWidth, footHeight)
    )
    // Soft glowing pink sole pad
    drawOval(
      brush = Brush.radialGradient(
        colors = listOf(
          if (idx == 0) Color(0xFFFF77C8) else Color(0xFF6CEEFF),
          Color(0xFFB33C86).copy(alpha = 0.7f)
        ),
        center = footCenter,
        radius = footWidth * 0.28f
      ),
      topLeft = Offset(footCenter.x - footWidth * 0.22f, footCenter.y - footHeight * 0.22f),
      size = Size(footWidth * 0.44f, footHeight * 0.44f)
    )
  }

  // 4. Ambient Occlusion Neck Shadow under the 3D Head
  drawOval(
    brush = Brush.radialGradient(
      colors = listOf(
        Color(0xFF070210).copy(alpha = 0.65f),
        Color.Transparent
      ),
      center = Offset(cx, headCenterY + h * 0.15f),
      radius = w * 0.24f
    ),
    topLeft = Offset(cx - w * 0.22f, headCenterY + h * 0.10f),
    size = Size(w * 0.44f, h * 0.10f)
  )

  // 5. Large Cute 3D Head (Volumetric Porcelain Sphere + Pink/Blue Neon Rim Reflections)
  val headWidth = w * 0.54f
  val headHeight = h * 0.40f
  val headTopLeft = Offset(cx - headWidth * 0.5f, headCenterY - headHeight * 0.5f)

  drawOval(
    brush = Brush.radialGradient(
      colors = listOf(
        Color(0xFFFFFFFF),
        Color(0xFFFAF6FF),
        Color(0xFFE7DCFC),
        Color(0xFFC2AEEB),
        Color(0xFF8368C2)
      ),
      center = Offset(cx - headWidth * 0.14f, headCenterY - headHeight * 0.18f),
      radius = headWidth * 0.68f
    ),
    topLeft = headTopLeft,
    size = Size(headWidth, headHeight)
  )

  // Left Pink Neon Studio Rim Reflection on 3D Head
  drawOval(
    brush = Brush.linearGradient(
      colors = listOf(
        Color(0xFFFF2A8D).copy(alpha = 0.78f),
        Color(0xFFE040FB).copy(alpha = 0.25f),
        Color.Transparent
      ),
      start = Offset(headTopLeft.x, headCenterY),
      end = Offset(cx, headCenterY)
    ),
    topLeft = headTopLeft,
    size = Size(headWidth, headHeight),
    style = Stroke(width = 3.dp.toPx())
  )

  // Right Electric-Cyan Neon Studio Rim Reflection on 3D Head
  drawOval(
    brush = Brush.linearGradient(
      colors = listOf(
        Color(0xFF00E5FF).copy(alpha = 0.82f),
        Color(0xFF651FFF).copy(alpha = 0.25f),
        Color.Transparent
      ),
      start = Offset(headTopLeft.x + headWidth, headCenterY),
      end = Offset(cx, headCenterY)
    ),
    topLeft = headTopLeft,
    size = Size(headWidth, headHeight),
    style = Stroke(width = 3.dp.toPx())
  )

  // Top Glossy Vinyl Specular Coat Highlight on Forehead
  drawOval(
    brush = Brush.verticalGradient(
      colors = listOf(
        Color.White.copy(alpha = 0.72f),
        Color.White.copy(alpha = 0.0f)
      ),
      startY = headTopLeft.y + headHeight * 0.05f,
      endY = headTopLeft.y + headHeight * 0.32f
    ),
    topLeft = Offset(cx - headWidth * 0.28f, headTopLeft.y + headHeight * 0.05f),
    size = Size(headWidth * 0.56f, headHeight * 0.24f)
  )

  // 6. Iconic Panda Eye Patches & Expressive 3D Glossy Eyes
  val patchWidth = headWidth * 0.27f
  val patchHeight = headHeight * 0.34f
  val leftEyeCenter = Offset(cx - headWidth * 0.20f, headCenterY - headHeight * 0.02f)
  val rightEyeCenter = Offset(cx + headWidth * 0.20f, headCenterY - headHeight * 0.02f)

  // Left Eye Patch (Tilted -18 deg)
  rotate(degrees = -18f, pivot = leftEyeCenter) {
    drawOval(
      brush = Brush.radialGradient(
        colors = listOf(Color(0xFF2D2240), Color(0xFF140D20), Color(0xFF07040C)),
        center = Offset(leftEyeCenter.x - patchWidth * 0.1f, leftEyeCenter.y - patchHeight * 0.15f),
        radius = patchWidth * 0.7f
      ),
      topLeft = Offset(leftEyeCenter.x - patchWidth * 0.5f, leftEyeCenter.y - patchHeight * 0.5f),
      size = Size(patchWidth, patchHeight)
    )
  }

  // Right Eye Patch (Tilted +18 deg)
  rotate(degrees = 18f, pivot = rightEyeCenter) {
    drawOval(
      brush = Brush.radialGradient(
        colors = listOf(Color(0xFF2D2240), Color(0xFF140D20), Color(0xFF07040C)),
        center = Offset(rightEyeCenter.x - patchWidth * 0.1f, rightEyeCenter.y - patchHeight * 0.15f),
        radius = patchWidth * 0.7f
      ),
      topLeft = Offset(rightEyeCenter.x - patchWidth * 0.5f, rightEyeCenter.y - patchHeight * 0.5f),
      size = Size(patchWidth, patchHeight)
    )
  }

  // Glossy 3D Irises & Catchlights (with smooth blink support)
  val eyeRadiusX = patchWidth * 0.33f
  val eyeRadiusY = (patchWidth * 0.35f) * eyeOpenFraction
  listOf(leftEyeCenter, rightEyeCenter).forEachIndexed { idx, eyeCenter ->
    if (eyeOpenFraction > 0.15f) {
      // Outer sclera/iris rim glow
      drawOval(
        brush = Brush.radialGradient(
          colors = listOf(
            Color(0xFF3B1D6E),
            Color(0xFF120629),
            Color(0xFF04010A)
          ),
          center = Offset(eyeCenter.x, eyeCenter.y + eyeRadiusY * 0.15f),
          radius = eyeRadiusX * 1.1f
        ),
        topLeft = Offset(eyeCenter.x - eyeRadiusX, eyeCenter.y - eyeRadiusY),
        size = Size(eyeRadiusX * 2f, eyeRadiusY * 2f)
      )

      // Lower luminous cyan/pink iris crescent reflection
      drawOval(
        brush = Brush.verticalGradient(
          colors = listOf(
            Color.Transparent,
            if (idx == 0) Color(0xFFFF4081).copy(alpha = 0.55f) else Color(0xFF00E5FF).copy(alpha = 0.55f)
          ),
          startY = eyeCenter.y - eyeRadiusY * 0.3f,
          endY = eyeCenter.y + eyeRadiusY * 0.85f
        ),
        topLeft = Offset(eyeCenter.x - eyeRadiusX * 0.78f, eyeCenter.y - eyeRadiusY * 0.78f),
        size = Size(eyeRadiusX * 1.56f, eyeRadiusY * 1.56f)
      )

      // Primary bright studio catchlight (Top-Left)
      drawCircle(
        color = Color.White,
        radius = eyeRadiusX * 0.36f,
        center = Offset(eyeCenter.x - eyeRadiusX * 0.30f, eyeCenter.y - eyeRadiusY * 0.32f)
      )
      // Secondary neon catchlight (Bottom-Right)
      drawCircle(
        color = Color(0xFF80F8FF),
        radius = eyeRadiusX * 0.18f,
        center = Offset(eyeCenter.x + eyeRadiusX * 0.32f, eyeCenter.y + eyeRadiusY * 0.30f)
      )
    } else {
      // Cute happy closed-eye arc during blink
      drawLine(
        color = Color(0xFFF3E5F5),
        start = Offset(eyeCenter.x - eyeRadiusX * 0.8f, eyeCenter.y),
        end = Offset(eyeCenter.x + eyeRadiusX * 0.8f, eyeCenter.y),
        strokeWidth = 2.4.dp.toPx(),
        cap = StrokeCap.Round
      )
    }
  }

  // 7. Rosy Neon Pink 3D Blush Cheeks
  val cheekRadiusX = headWidth * 0.095f
  val cheekRadiusY = headHeight * 0.06f
  val cheekY = headCenterY + headHeight * 0.11f
  listOf(cx - headWidth * 0.29f, cx + headWidth * 0.29f).forEach { cheekX ->
    drawOval(
      brush = Brush.radialGradient(
        colors = listOf(
          Color(0xFFFF409A).copy(alpha = 0.55f),
          Color(0xFFFF409A).copy(alpha = 0.15f),
          Color.Transparent
        ),
        center = Offset(cheekX, cheekY),
        radius = cheekRadiusX * 1.3f
      ),
      topLeft = Offset(cheekX - cheekRadiusX, cheekY - cheekRadiusY),
      size = Size(cheekRadiusX * 2f, cheekRadiusY * 2f)
    )
  }

  // 8. 3D Snout / Muzzle Sphere, Glossy Nose & Cute Smile
  val snoutWidth = headWidth * 0.33f
  val snoutHeight = headHeight * 0.25f
  val snoutCenter = Offset(cx, headCenterY + headHeight * 0.12f)
  drawOval(
    brush = Brush.radialGradient(
      colors = listOf(
        Color(0xFFFFFFFF),
        Color(0xFFF3EAFF),
        Color(0xFFD0BEEF)
      ),
      center = Offset(snoutCenter.x - snoutWidth * 0.08f, snoutCenter.y - snoutHeight * 0.2f),
      radius = snoutWidth * 0.65f
    ),
    topLeft = Offset(snoutCenter.x - snoutWidth * 0.5f, snoutCenter.y - snoutHeight * 0.5f),
    size = Size(snoutWidth, snoutHeight)
  )

  // Cute Glossy 3D Button Nose
  val noseWidth = snoutWidth * 0.38f
  val noseHeight = snoutHeight * 0.38f
  val noseCenter = Offset(cx, snoutCenter.y - snoutHeight * 0.14f)
  drawOval(
    brush = Brush.radialGradient(
      colors = listOf(Color(0xFF433659), Color(0xFF150E22), Color(0xFF05020A)),
      center = Offset(noseCenter.x - noseWidth * 0.12f, noseCenter.y - noseHeight * 0.2f),
      radius = noseWidth * 0.7f
    ),
    topLeft = Offset(noseCenter.x - noseWidth * 0.5f, noseCenter.y - noseHeight * 0.5f),
    size = Size(noseWidth, noseHeight)
  )
  // Crisp White Specular Highlight on Nose
  drawOval(
    color = Color.White.copy(alpha = 0.78f),
    topLeft = Offset(noseCenter.x - noseWidth * 0.24f, noseCenter.y - noseHeight * 0.34f),
    size = Size(noseWidth * 0.36f, noseHeight * 0.22f)
  )

  // Cheerful W-Smile below Nose
  val smilePath = Path().apply {
    val startY = noseCenter.y + noseHeight * 0.55f
    moveTo(cx, noseCenter.y + noseHeight * 0.45f)
    lineTo(cx, startY)
    moveTo(cx - snoutWidth * 0.18f, startY + snoutHeight * 0.04f)
    quadraticTo(
      cx - snoutWidth * 0.09f,
      startY + snoutHeight * 0.22f,
      cx,
      startY
    )
    quadraticTo(
      cx + snoutWidth * 0.09f,
      startY + snoutHeight * 0.22f,
      cx + snoutWidth * 0.18f,
      startY + snoutHeight * 0.04f
    )
  }
  drawPath(
    path = smilePath,
    color = Color(0xFF28193D),
    style = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round)
  )

  // 9. Magical Glowing Pedestal / Energy Cradle Held Between the Panda's Hands
  val handLiftOffset = (1f - revealProgress) * h * 0.03f
  val handsCenterY = h * 0.64f + handLiftOffset
  val leftPawCenter = Offset(cx - w * 0.21f, handsCenterY + h * 0.015f)
  val rightPawCenter = Offset(cx + w * 0.21f, handsCenterY + h * 0.015f)

  // Luminous Pink-Cyan Energy Aura Radiating from the Panda's Cupped Hands
  drawOval(
    brush = Brush.radialGradient(
      colors = listOf(
        Color(0xFFFFFFFF).copy(alpha = 0.38f * revealProgress),
        Color(0xFFFF2A8D).copy(alpha = 0.42f * revealProgress),
        Color(0xFF00E5FF).copy(alpha = 0.35f * revealProgress),
        Color.Transparent
      ),
      center = Offset(cx, handsCenterY - h * 0.01f),
      radius = w * 0.36f
    ),
    topLeft = Offset(cx - w * 0.34f, handsCenterY - h * 0.11f),
    size = Size(w * 0.68f, h * 0.20f)
  )

  // Magical Light Cradle Arc connecting Left Paw to Right Paw underneath "Priya Live"
  val cradleArc = Path().apply {
    moveTo(leftPawCenter.x, leftPawCenter.y + h * 0.01f)
    quadraticTo(
      cx,
      handsCenterY + h * 0.085f,
      rightPawCenter.x,
      rightPawCenter.y + h * 0.01f
    )
  }
  drawPath(
    path = cradleArc,
    brush = Brush.horizontalGradient(
      colors = listOf(
        Color(0xFFFF2A8D).copy(alpha = 0.85f * revealProgress),
        Color(0xFFFFFFFF).copy(alpha = 0.95f * revealProgress),
        Color(0xFF00E5FF).copy(alpha = 0.85f * revealProgress)
      )
    ),
    style = Stroke(width = 3.2.dp.toPx(), cap = StrokeCap.Round)
  )

  // 10. 3D Glossy Panda Arms & Cupped Hands Presenting "Priya Live"
  val armWidth = w * 0.15f
  val armHeight = h * 0.18f

  // Left Arm & Cupped Paw (Angled inward to hold the left side of "Priya Live")
  rotate(degrees = -28f, pivot = leftPawCenter) {
    drawOval(
      brush = Brush.radialGradient(
        colors = listOf(
          Color(0xFF3F3156),
          Color(0xFF1A1228),
          Color(0xFF090510)
        ),
        center = Offset(leftPawCenter.x - armWidth * 0.15f, leftPawCenter.y - armHeight * 0.15f),
        radius = armHeight * 0.65f
      ),
      topLeft = Offset(leftPawCenter.x - armWidth * 0.55f, leftPawCenter.y - armHeight * 0.62f),
      size = Size(armWidth, armHeight)
    )
    // Pink Neon Rim Highlight on Left Paw
    drawOval(
      brush = Brush.linearGradient(
        colors = listOf(Color(0xFFFF2A8D).copy(alpha = 0.85f), Color.Transparent),
        start = Offset(leftPawCenter.x - armWidth * 0.5f, leftPawCenter.y),
        end = Offset(leftPawCenter.x + armWidth * 0.4f, leftPawCenter.y)
      ),
      topLeft = Offset(leftPawCenter.x - armWidth * 0.55f, leftPawCenter.y - armHeight * 0.62f),
      size = Size(armWidth, armHeight),
      style = Stroke(width = 2.dp.toPx())
    )
    // Cute Glowing Pink Paw Pad
    drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(Color(0xFFFF8AD4), Color(0xFFD81B60).copy(alpha = 0.8f)),
        center = leftPawCenter,
        radius = armWidth * 0.25f
      ),
      radius = armWidth * 0.22f,
      center = Offset(leftPawCenter.x + armWidth * 0.04f, leftPawCenter.y + armHeight * 0.05f)
    )
  }

  // Right Arm & Cupped Paw (Angled inward to hold the right side of "Priya Live")
  rotate(degrees = 28f, pivot = rightPawCenter) {
    drawOval(
      brush = Brush.radialGradient(
        colors = listOf(
          Color(0xFF363D59),
          Color(0xFF18142A),
          Color(0xFF090510)
        ),
        center = Offset(rightPawCenter.x + armWidth * 0.15f, rightPawCenter.y - armHeight * 0.15f),
        radius = armHeight * 0.65f
      ),
      topLeft = Offset(rightPawCenter.x - armWidth * 0.45f, rightPawCenter.y - armHeight * 0.62f),
      size = Size(armWidth, armHeight)
    )
    // Cyan Neon Rim Highlight on Right Paw
    drawOval(
      brush = Brush.linearGradient(
        colors = listOf(Color(0xFF00E5FF).copy(alpha = 0.85f), Color.Transparent),
        start = Offset(rightPawCenter.x + armWidth * 0.5f, rightPawCenter.y),
        end = Offset(rightPawCenter.x - armWidth * 0.4f, rightPawCenter.y)
      ),
      topLeft = Offset(rightPawCenter.x - armWidth * 0.45f, rightPawCenter.y - armHeight * 0.62f),
      size = Size(armWidth, armHeight),
      style = Stroke(width = 2.dp.toPx())
    )
    // Cute Glowing Cyan-Pink Paw Pad
    drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(Color(0xFF84FFFF), Color(0xFF00ACC1).copy(alpha = 0.8f)),
        center = rightPawCenter,
        radius = armWidth * 0.25f
      ),
      radius = armWidth * 0.22f,
      center = Offset(rightPawCenter.x - armWidth * 0.04f, rightPawCenter.y + armHeight * 0.05f)
    )
  }

  // 11. Sparkling Magic Orbs rising from the Panda's Hands around "Priya Live"
  for (i in 0..5) {
    val progress = (shimmerFlow + i * 0.17f) % 1f
    val spreadX = (i - 2.5f) * (w * 0.085f) + sin(phase + i) * 6.dp.toPx()
    val orbX = cx + spreadX
    val orbY = handsCenterY + h * 0.03f - progress * (h * 0.14f)
    val orbAlpha = (sin(progress * PI).toFloat() * revealProgress).coerceIn(0f, 1f)
    val orbColor = if (i % 2 == 0) Color(0xFFFF4081) else Color(0xFF00E5FF)

    drawCircle(
      color = orbColor.copy(alpha = 0.38f * orbAlpha),
      radius = 5.5.dp.toPx(),
      center = Offset(orbX, orbY)
    )
    drawCircle(
      color = Color.White.copy(alpha = 0.9f * orbAlpha),
      radius = 2.dp.toPx(),
      center = Offset(orbX, orbY)
    )
  }
}


