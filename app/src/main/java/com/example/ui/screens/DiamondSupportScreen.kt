package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.storage.UserProfileStorage
import com.example.ui.components.rememberResponsiveNavigationBarPadding

/**
 * Official Bangladesh WhatsApp contact number for Priya Live Diamond Support.
 */
const val DIAMOND_WHATSAPP_DISPLAY_NUMBER = "01818022519"
const val DIAMOND_WHATSAPP_INTL_NUMBER = "8801818022519"

/**
 * Launches a direct WhatsApp chat using Click-to-Chat format.
 * Tries the official WhatsApp client, WhatsApp Business, and web fallback gracefully.
 */
fun openDiamondWhatsApp(context: Context, phoneNumber: String = DIAMOND_WHATSAPP_INTL_NUMBER) {
  val cleanPhone = phoneNumber.replace("+", "").replace(" ", "").trim()
  val clickToChatUrl = "https://api.whatsapp.com/send?phone=$cleanPhone"
  val webUrl = "https://wa.me/$cleanPhone"

  try {
    // 1. Standard WhatsApp Application
    val waIntent = Intent(Intent.ACTION_VIEW).apply {
      data = Uri.parse(clickToChatUrl)
      setPackage("com.whatsapp")
    }
    context.startActivity(waIntent)
  } catch (_: Exception) {
    try {
      // 2. WhatsApp Business Application
      val waBusinessIntent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(clickToChatUrl)
        setPackage("com.whatsapp.w4b")
      }
      context.startActivity(waBusinessIntent)
    } catch (_: Exception) {
      try {
        // 3. Fallback to web browser Click-to-Chat
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webUrl))
        context.startActivity(browserIntent)
      } catch (e: Exception) {
        // 4. Graceful message
        Toast.makeText(context, "WhatsApp Contact: $DIAMOND_WHATSAPP_DISPLAY_NUMBER", Toast.LENGTH_LONG).show()
      }
    }
  }
}

/**
 * Geometric, faceted Brilliant-Cut Diamond/Gem Logo.
 * Authentic diamond silhouette: flat table top, angled crown shoulders, wide girdle,
 * tapering pavilion facets converging at the culet tip, and inner facet lines.
 */
@Composable
fun FacetedDiamondGemIcon(
  modifier: Modifier = Modifier.size(22.dp),
  tint: Color = Color(0xFF00E5FF),
  showSparkle: Boolean = true
) {
  Canvas(modifier = modifier) {
    val w = size.width
    val h = size.height

    // Key geometric vertices of a brilliant-cut diamond
    val xTableLeft = w * 0.24f
    val xTableRight = w * 0.76f
    val yTable = h * 0.16f

    val xGirdleLeft = w * 0.05f
    val xGirdleRight = w * 0.95f
    val yGirdle = h * 0.44f

    val xCrownLeft = w * 0.36f
    val xCrownRight = w * 0.64f

    val xCulet = w * 0.50f
    val yCulet = h * 0.94f

    // 1. Shaded Facet fills (3D crystal depth)
    // Center Table
    val tablePath = Path().apply {
      moveTo(xTableLeft, yTable)
      lineTo(xTableRight, yTable)
      lineTo(xCrownRight, yGirdle)
      lineTo(xCrownLeft, yGirdle)
      close()
    }
    drawPath(tablePath, Color(0xFFE0F7FA).copy(alpha = 0.50f))

    // Left Crown Facet
    val leftCrownPath = Path().apply {
      moveTo(xTableLeft, yTable)
      lineTo(xCrownLeft, yGirdle)
      lineTo(xGirdleLeft, yGirdle)
      close()
    }
    drawPath(leftCrownPath, Color(0xFF00E5FF).copy(alpha = 0.35f))

    // Right Crown Facet
    val rightCrownPath = Path().apply {
      moveTo(xTableRight, yTable)
      lineTo(xGirdleRight, yGirdle)
      lineTo(xCrownRight, yGirdle)
      close()
    }
    drawPath(rightCrownPath, Color(0xFF00B0FF).copy(alpha = 0.35f))

    // Center Lower Pavilion Facet
    val centerPavilionPath = Path().apply {
      moveTo(xCrownLeft, yGirdle)
      lineTo(xCrownRight, yGirdle)
      lineTo(xCulet, yCulet)
      close()
    }
    drawPath(centerPavilionPath, Color(0xFF80D8FF).copy(alpha = 0.40f))

    // Left Pavilion Facet
    val leftPavilionPath = Path().apply {
      moveTo(xGirdleLeft, yGirdle)
      lineTo(xCrownLeft, yGirdle)
      lineTo(xCulet, yCulet)
      close()
    }
    drawPath(leftPavilionPath, Color(0xFF0091EA).copy(alpha = 0.30f))

    // Right Pavilion Facet
    val rightPavilionPath = Path().apply {
      moveTo(xCrownRight, yGirdle)
      lineTo(xGirdleRight, yGirdle)
      lineTo(xCulet, yCulet)
      close()
    }
    drawPath(rightPavilionPath, Color(0xFF00B4D8).copy(alpha = 0.30f))

    // 2. Crisp Facet Outlines
    val strokeWidth = 1.4.dp.toPx()
    val strokeColor = tint

    // Outer Diamond Silhouette
    val outerSilhouette = Path().apply {
      moveTo(xTableLeft, yTable)
      lineTo(xTableRight, yTable)
      lineTo(xGirdleRight, yGirdle)
      lineTo(xCulet, yCulet)
      lineTo(xGirdleLeft, yGirdle)
      close()
    }
    drawPath(
      path = outerSilhouette,
      color = strokeColor,
      style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )

    // Girdle horizontal dividing line
    drawLine(
      color = strokeColor.copy(alpha = 0.9f),
      start = Offset(xGirdleLeft, yGirdle),
      end = Offset(xGirdleRight, yGirdle),
      strokeWidth = strokeWidth * 0.85f,
      cap = StrokeCap.Round
    )

    // Upper crown diagonal facet lines
    drawLine(
      color = strokeColor,
      start = Offset(xTableLeft, yTable),
      end = Offset(xCrownLeft, yGirdle),
      strokeWidth = strokeWidth * 0.85f,
      cap = StrokeCap.Round
    )
    drawLine(
      color = strokeColor,
      start = Offset(xTableRight, yTable),
      end = Offset(xCrownRight, yGirdle),
      strokeWidth = strokeWidth * 0.85f,
      cap = StrokeCap.Round
    )

    // Lower pavilion facet lines converging to culet tip
    drawLine(
      color = strokeColor.copy(alpha = 0.95f),
      start = Offset(xCrownLeft, yGirdle),
      end = Offset(xCulet, yCulet),
      strokeWidth = strokeWidth * 0.85f,
      cap = StrokeCap.Round
    )
    drawLine(
      color = strokeColor.copy(alpha = 0.95f),
      start = Offset(xCrownRight, yGirdle),
      end = Offset(xCulet, yCulet),
      strokeWidth = strokeWidth * 0.85f,
      cap = StrokeCap.Round
    )

    // 3. Sparkle Glint Accent
    if (showSparkle) {
      drawCircle(
        color = Color.White,
        radius = strokeWidth * 0.85f,
        center = Offset(xTableLeft, yTable)
      )
    }
  }
}

/**
 * Crisp, authentic WhatsApp Brand Icon.
 * Features the signature WhatsApp vibrant green circle with a speech bubble pointer
 * and clean white telephone handset.
 */
@Composable
fun WhatsAppBrandIcon(
  modifier: Modifier = Modifier.size(24.dp)
) {
  Box(
    modifier = modifier
      .clip(CircleShape)
      .background(Color(0xFF25D366)),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = Icons.Filled.Call,
      contentDescription = "WhatsApp",
      tint = Color.White,
      modifier = Modifier.size((14.dp))
    )
  }
}

/**
 * Clean, professional Diamond Contact & Support Dialog.
 * Replaces the old package/purchase/dollar top-up UI completely.
 */
@Composable
fun DiamondSupportDialog(
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  val profile = remember { UserProfileStorage.getProfile(context) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .clip(RoundedCornerShape(26.dp))
        .border(
          width = 1.8.dp,
          brush = Brush.horizontalGradient(listOf(Color(0xFF00E5FF), Color(0xFF25D366))),
          shape = RoundedCornerShape(26.dp)
        )
        .testTag("diamond_support_dialog"),
      color = Color(0xFF100124)
    ) {
      Column(
        modifier = Modifier
          .padding(22.dp)
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Top Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color(0xFF002238))
                .border(1.2.dp, Color(0xFF00E5FF), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              FacetedDiamondGemIcon(modifier = Modifier.size(24.dp), tint = Color(0xFF00E5FF))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "Diamond Support",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
              )
              Text(
                text = "Official Verified Assistance",
                color = Color(0xFF80D8FF),
                fontSize = 11.sp
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
          }
        }

        // WhatsApp Contact Card
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = Color(0xFF0B001A),
          border = androidx.compose.foundation.BorderStroke(
            1.4.dp,
            Brush.horizontalGradient(listOf(Color(0xFF25D366), Color(0xFF00E5FF)))
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color(0xFF25D366).copy(alpha = 0.16f))
                .border(1.5.dp, Color(0xFF25D366), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              WhatsAppBrandIcon(modifier = Modifier.size(34.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
              text = "WHATSAPP CONTACT",
              color = Color(0xFF25D366),
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Display Number: 01818022519
            Text(
              text = DIAMOND_WHATSAPP_DISPLAY_NUMBER,
              color = Color.White,
              fontSize = 22.sp,
              fontWeight = FontWeight.Black,
              letterSpacing = 1.sp
            )

            Text(
              text = "Bangladesh (+880) • 24/7 Fast Support",
              color = Color(0xFF80DEEA),
              fontSize = 11.5.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Contact on WhatsApp Button
            Button(
              onClick = {
                openDiamondWhatsApp(context)
              },
              shape = RoundedCornerShape(16.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
              modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("contact_on_whatsapp_button")
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
              ) {
                WhatsAppBrandIcon(modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                  text = "Contact on WhatsApp",
                  color = Color.White,
                  fontWeight = FontWeight.Black,
                  fontSize = 14.5.sp
                )
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Copy Number Option
            OutlinedButton(
              onClick = {
                clipboardManager.setText(AnnotatedString(DIAMOND_WHATSAPP_DISPLAY_NUMBER))
                Toast.makeText(context, "Number copied: $DIAMOND_WHATSAPP_DISPLAY_NUMBER", Toast.LENGTH_SHORT).show()
              },
              shape = RoundedCornerShape(14.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x55FFFFFF)),
              modifier = Modifier.fillMaxWidth().height(40.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
              ) {
                Icon(
                  imageVector = Icons.Filled.ContentCopy,
                  contentDescription = "Copy Number",
                  tint = Color(0xFF80D8FF),
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Copy $DIAMOND_WHATSAPP_DISPLAY_NUMBER",
                  color = Color(0xFFE0E0FF),
                  fontSize = 12.sp
                )
              }
            }
          }
        }

        // User ID Reference Card
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = Color(0xFF18032F),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.4f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .padding(horizontal = 14.dp, vertical = 10.dp)
              .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "YOUR PRIYA LIVE ID",
                color = Color(0xFF80D8FF),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = profile.userId.ifBlank { "User" },
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black
              )
            }

            Icon(
              imageVector = Icons.Filled.Verified,
              contentDescription = "Verified User",
              tint = Color(0xFF00E5FF),
              modifier = Modifier.size(20.dp)
            )
          }
        }

        // Close / Done button
        Button(
          onClick = onDismiss,
          modifier = Modifier
            .fillMaxWidth()
            .height(42.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0x2AFFFFFF)),
          shape = RoundedCornerShape(14.dp)
        ) {
          Text("Done", color = Color.White, fontSize = 13.sp)
        }
      }
    }
  }
}

/**
 * Clean, dedicated full-screen Diamond Support & WhatsApp Contact Page.
 * Used for AppRoute.CoinTopUp. Replaces the old package/pricing/top-up screen entirely.
 */
@Composable
fun DiamondSupportFullScreen(
  onBack: () -> Unit
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  val profile = remember { UserProfileStorage.getProfile(context) }
  val topSafePadding = com.example.ui.components.rememberResponsiveTopBarPadding(extraTopSpacing = 0.dp)
  val responsiveBottomPadding = rememberResponsiveNavigationBarPadding(extraBottomSpacing = 16.dp)

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          listOf(
            Color(0xFF0D001E),
            Color(0xFF17002E),
            Color(0xFF090014)
          )
        )
      )
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(top = topSafePadding, bottom = responsiveBottomPadding)
    ) {
      // Top Navigation App Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onBack,
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(Color(0x33FFFFFF))
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = Color.White
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(Color(0xFF002238))
              .border(1.2.dp, Color(0xFF00E5FF), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            FacetedDiamondGemIcon(modifier = Modifier.size(19.dp), tint = Color(0xFF00E5FF))
          }
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Diamond Support",
            color = Color.White,
            fontSize = 19.sp,
            fontWeight = FontWeight.Black
          )
        }
      }

      // Content Column
      Column(
        modifier = Modifier
          .weight(1f)
          .padding(horizontal = 18.dp)
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Spacer(modifier = Modifier.height(6.dp))

        // Hero Diamond Badge
        Box(
          modifier = Modifier
            .size(90.dp)
            .shadow(20.dp, CircleShape, spotColor = Color(0xFF00E5FF))
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                listOf(Color(0xFF003855), Color(0xFF0F0024))
              )
            )
            .border(2.dp, Brush.horizontalGradient(listOf(Color(0xFF00E5FF), Color(0xFF25D366))), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          FacetedDiamondGemIcon(modifier = Modifier.size(48.dp), tint = Color(0xFF00E5FF))
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "Diamond Support",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Official Customer Service for Priya Live",
            color = Color(0xFF80D8FF),
            fontSize = 12.5.sp,
            textAlign = TextAlign.Center
          )
        }

        // WhatsApp Main Card
        Surface(
          shape = RoundedCornerShape(22.dp),
          color = Color(0xFF100028),
          border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            Brush.horizontalGradient(listOf(Color(0xFF25D366), Color(0xFF00E5FF)))
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color(0xFF25D366).copy(alpha = 0.15f))
                .border(1.6.dp, Color(0xFF25D366), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              WhatsAppBrandIcon(modifier = Modifier.size(36.dp))
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
              text = "WHATSAPP NUMBER",
              color = Color(0xFF25D366),
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = DIAMOND_WHATSAPP_DISPLAY_NUMBER,
              color = Color.White,
              fontSize = 24.sp,
              fontWeight = FontWeight.Black,
              letterSpacing = 1.5.sp
            )

            Text(
              text = "Official Bangladesh Line (+880)",
              color = Color(0xFF80DEEA),
              fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Primary WhatsApp Direct Click-To-Chat Button
            Button(
              onClick = {
                openDiamondWhatsApp(context)
              },
              shape = RoundedCornerShape(16.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
              modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("contact_on_whatsapp_fullscreen_button")
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
              ) {
                WhatsAppBrandIcon(modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                  text = "Contact on WhatsApp",
                  color = Color.White,
                  fontWeight = FontWeight.Black,
                  fontSize = 15.sp
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Copy Number Action
            OutlinedButton(
              onClick = {
                clipboardManager.setText(AnnotatedString(DIAMOND_WHATSAPP_DISPLAY_NUMBER))
                Toast.makeText(context, "Number copied: $DIAMOND_WHATSAPP_DISPLAY_NUMBER", Toast.LENGTH_SHORT).show()
              },
              shape = RoundedCornerShape(14.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x44FFFFFF)),
              modifier = Modifier.fillMaxWidth().height(42.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
              ) {
                Icon(
                  imageVector = Icons.Filled.ContentCopy,
                  contentDescription = "Copy Number",
                  tint = Color(0xFF80D8FF),
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Copy $DIAMOND_WHATSAPP_DISPLAY_NUMBER",
                  color = Color(0xFFE0E0FF),
                  fontSize = 12.5.sp
                )
              }
            }
          }
        }

        // Account Info Verification Card
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = Color(0xFF140228),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF7C4DFF).copy(alpha = 0.5f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .padding(16.dp)
              .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "YOUR PRIYA LIVE ACCOUNT",
                color = Color(0xFF00E5FF),
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "ID: ${profile.userId.ifBlank { "8318713" }}",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black
              )
              Text(
                text = profile.username.ifBlank { "User" },
                color = Color(0xFFB388FF),
                fontSize = 11.5.sp
              )
            }

            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color(0xFF00E5FF).copy(alpha = 0.15f))
                .border(1.dp, Color(0xFF00E5FF), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Filled.Shield,
                contentDescription = "Security Verified",
                tint = Color(0xFF00E5FF),
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }
}
