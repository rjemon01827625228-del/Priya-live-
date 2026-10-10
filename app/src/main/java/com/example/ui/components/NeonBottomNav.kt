package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class AppNavTab(val title: String) {
  LIVE("LIVE"),
  PARTY("PARTY"),
  DISCOVER("DISCOVER"),
  CHAT("CHAT"),
  ME("ME")
}

/**
 * Premium 3D Glassmorphism Bottom Navigation Bar
 * Exact 1:1 replica of the provided image:
 * - Ambient multi-color outer neon bloom (magenta on left, cyan on right)
 * - Frosted milky white glassmorphism capsule with gradient hairline rim
 * - 5 dedicated 3D rendered icons:
 *   1. LIVE: 3D retro TV with cute antennas & play triangle in purple capsule
 *   2. PARTY: 3D striped candy party horn bursting with colorful confetti
 *   3. DISCOVER: 3D electric blue Saturn planet with tilted magenta orbit ring & sparkles
 *   4. CHAT: 3D hot-pink glossy smiley speech bubble with happy eyes & mouth
 *   5. ME: 3D glossy electric blue user avatar silhouette
 * - Bottom Android purple home indicator pill
 */
@Composable
fun NeonBottomNav(
  currentTab: AppNavTab,
  onTabSelected: (AppNavTab) -> Unit,
  modifier: Modifier = Modifier
) {
  val responsiveBottomPadding = rememberResponsiveNavigationBarPadding(extraBottomSpacing = 6.dp)
  Column(
    modifier = modifier
      .fillMaxWidth()
      .testTag("neon_bottom_nav_bar")
      .padding(bottom = responsiveBottomPadding, start = 8.dp, end = 8.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // 1. Container with Outer Multi-Color Glow Shadows
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(78.dp),
      contentAlignment = Alignment.Center
    ) {
      // Background multi-color ambient neon glow bloom
      Canvas(
        modifier = Modifier
          .fillMaxWidth()
          .height(78.dp)
      ) {
        val w = size.width
        val h = size.height

        // Left magenta/pink ambient blur glow
        drawRoundRect(
          brush = Brush.radialGradient(
            colors = listOf(
              Color(0xB3FF00A0),
              Color(0x66B000FF),
              Color.Transparent
            ),
            center = Offset(w * 0.12f, h * 0.5f),
            radius = w * 0.28f
          ),
          topLeft = Offset(0f, 0f),
          size = Size(w * 0.4f, h),
          cornerRadius = CornerRadius(38.dp.toPx(), 38.dp.toPx())
        )

        // Right electric-cyan ambient blur glow
        drawRoundRect(
          brush = Brush.radialGradient(
            colors = listOf(
              Color(0x9900E5FF),
              Color(0x550091EA),
              Color.Transparent
            ),
            center = Offset(w * 0.88f, h * 0.5f),
            radius = w * 0.28f
          ),
          topLeft = Offset(w * 0.6f, 0f),
          size = Size(w * 0.4f, h),
          cornerRadius = CornerRadius(38.dp.toPx(), 38.dp.toPx())
        )
      }

      // Main Frosted Glass Capsule Bar
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(74.dp)
          .clip(RoundedCornerShape(37.dp))
          // Multi-color gradient perimeter rim
          .border(
            width = 1.8.dp,
            brush = Brush.horizontalGradient(
              listOf(
                Color(0xFFFF2AA6),
                Color(0xFFE088FF),
                Color(0xFFE9DCFF),
                Color(0xFF80D8FF),
                Color(0xFF00E5FF)
              )
            ),
            shape = RoundedCornerShape(37.dp)
          )
          // Frosted milky white glass fill
          .background(
            Brush.verticalGradient(
              listOf(
                Color(0xFDFBF8FF),
                Color(0xF5F0E8FA)
              )
            )
          )
          .padding(horizontal = 6.dp, vertical = 6.dp)
          .testTag("neon_bottom_nav_capsule")
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(62.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Tab 1: LIVE
          LiveTabItem(
            isSelected = currentTab == AppNavTab.LIVE,
            onClick = { onTabSelected(AppNavTab.LIVE) },
            modifier = Modifier.weight(1f)
          )

          // Tab 2: PARTY
          StandardTabItem(
            label = "PARTY",
            isSelected = currentTab == AppNavTab.PARTY,
            onClick = { onTabSelected(AppNavTab.PARTY) },
            modifier = Modifier.weight(1f).testTag("nav_tab_party")
          ) {
            PartyPopper3dIcon(modifier = Modifier.size(34.dp))
          }

          // Tab 3: DISCOVER
          StandardTabItem(
            label = "DISCOVER",
            isSelected = currentTab == AppNavTab.DISCOVER,
            onClick = { onTabSelected(AppNavTab.DISCOVER) },
            modifier = Modifier.weight(1f).testTag("nav_tab_discover")
          ) {
            DiscoverPlanet3dIcon(modifier = Modifier.size(34.dp))
          }

          // Tab 4: CHAT
          StandardTabItem(
            label = "CHAT",
            isSelected = currentTab == AppNavTab.CHAT,
            onClick = { onTabSelected(AppNavTab.CHAT) },
            modifier = Modifier.weight(1f).testTag("nav_tab_chat")
          ) {
            ChatSmiley3dIcon(modifier = Modifier.size(34.dp))
          }

          // Tab 5: Me
          StandardTabItem(
            label = "Me",
            isSelected = currentTab == AppNavTab.ME,
            onClick = { onTabSelected(AppNavTab.ME) },
            modifier = Modifier.weight(1f).testTag("nav_tab_me")
          ) {
            ProfileMe3dIcon(modifier = Modifier.size(34.dp))
          }
        }
      }
    }
  }
}

/**
 * LIVE Tab: Rich 3D Purple/Violet Capsule with 3D TV icon and bold white text
 */
@Composable
private fun LiveTabItem(
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .height(62.dp)
      .padding(horizontal = 2.dp)
      .then(
        if (isSelected) {
          Modifier
            .clip(RoundedCornerShape(28.dp))
            .border(
              width = 1.6.dp,
              brush = Brush.verticalGradient(
                listOf(
                  Color(0xFFFF94F2),
                  Color(0xFFC040FF),
                  Color(0xFF6B00D7)
                )
              ),
              shape = RoundedCornerShape(28.dp)
            )
            .background(
              Brush.verticalGradient(
                listOf(
                  Color(0xFF9E10FA),
                  Color(0xFF7800E0),
                  Color(0xFF4C00AC)
                )
              )
            )
        } else {
          Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.50f))
        }
      )
      .clickable(onClick = onClick)
      .testTag("nav_tab_live"),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Live3dTvIcon(modifier = Modifier.size(32.dp))
      Spacer(modifier = Modifier.height(1.dp))
      Text(
        text = "LIVE",
        color = if (isSelected) Color.White else Color(0xFF1E164D),
        fontSize = 11.5.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 0.5.sp
      )
    }
  }
}

/**
 * Standard Navigation Capsule Tab (PARTY, DISCOVER, CHAT, ME)
 * Set in soft frosted squircle with rich 3D icon and dark indigo typography
 */
@Composable
private fun StandardTabItem(
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  iconContent: @Composable () -> Unit
) {
  val interactionSource = remember { MutableInteractionSource() }

  Box(
    modifier = modifier
      .height(62.dp)
      .padding(horizontal = 2.dp)
      .then(
        if (isSelected) {
          Modifier
            .clip(RoundedCornerShape(26.dp))
            .border(
              width = 1.5.dp,
              brush = Brush.horizontalGradient(
                listOf(Color(0xFF00E5FF), Color(0xFF8B00F2))
              ),
              shape = RoundedCornerShape(26.dp)
            )
            .background(
              Brush.verticalGradient(
                listOf(Color(0xFFF3EAFF), Color(0xFFE4D2FC))
              )
            )
        } else {
          Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.45f))
        }
      )
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
      ),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      iconContent()
      Spacer(modifier = Modifier.height(1.dp))
      Text(
        text = label,
        color = Color(0xFF1E164D),
        fontSize = 10.5.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 0.2.sp
      )
    }
  }
}
