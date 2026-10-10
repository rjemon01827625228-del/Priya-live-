package com.example.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.aistudio.neonparty.nxvqlr.R
import com.example.ui.storage.UserProfileStorage

// -----------------------------------------------------------------------------
// DATA MODELS FOR REAL BOARD GIFT SYSTEM
// -----------------------------------------------------------------------------
data class RealGiftRecipient(
  val userId: String,
  val username: String,
  val avatarUri: String?,
  val frameId: String? = null,
  val frameUrl: String? = null,
  val isHost: Boolean = false,
  val seatNumber: Int? = null
)

data class RealBoardGift(
  val id: String,
  val name: String,
  val emoji: String,
  val priceDiamonds: Long,
  val formattedPrice: String,
  val category: String = "Popular"
)

// Real gifts catalog matching reference visual style & streaming standards
val RealBoardGiftsCatalog = listOf(
  RealBoardGift("g_rose", "Rose", "🌹", 10L, "10", "Popular"),
  RealBoardGift("g_heart", "Lucky Heart", "💖", 50L, "50", "Popular"),
  RealBoardGift("g_ring", "Crystal Ring", "💍", 100L, "100", "Popular"),
  RealBoardGift("g_balloon", "Love Balloon", "🎈", 500L, "500", "Popular"),
  RealBoardGift("g_car", "Sports Car", "🏎️", 1000L, "1,000", "Popular"),
  RealBoardGift("g_yacht", "Luxury Yacht", "🛥️", 5000L, "5,000", "Popular"),
  RealBoardGift("g_sunset", "Romantic Sunset", "🌅", 10000L, "10K", "Popular"),
  RealBoardGift("g_castle", "Fireworks Castle", "🏰", 50000L, "50K", "Popular"),

  // High-End Luxury & Romantic Gifts
  RealBoardGift("g_ily", "I LOVE YOU", "❤️‍🔥", 200000L, "200K", "Luxury"),
  RealBoardGift("g_couple", "Romantic Couple", "🌇", 300000L, "300K", "Romantic"),
  RealBoardGift("g_crown", "Imperial Crown", "👑", 500000L, "500K", "Luxury"),
  RealBoardGift("g_grand_castle", "Grand Castle", "🎆🏰", 1000000L, "1M", "Luxury"),
  RealBoardGift("g_winged_heart", "Winged Crown", "👑💖", 2000000L, "2M", "Luxury")
)

/**
 * HALF-SCREEN BOTTOM SHEET GIFT INTERFACE (Party Board & Live Stream Board)
 *
 * Rules:
 * - Half-screen height from bottom (~52% height). Current Board remains visible above.
 * - Top participant area shows ONLY real users present on the board.
 * - Real sender balance check & real deduction.
 * - Multiplier combo support (x1, x10, x66, x99, x520, x1314).
 * - Premium glowing neon style matching user reference image.
 */
@Composable
fun RealBoardGiftBottomSheet(
  realRecipients: List<RealGiftRecipient>,
  currentUserBalance: Long,
  onDismiss: () -> Unit,
  onSendGift: (recipient: RealGiftRecipient, gift: RealBoardGift, quantity: Int, totalCost: Long) -> Unit,
  onOpenRecharge: () -> Unit = {}
) {
  val context = LocalContext.current
  var selectedRecipientId by remember(realRecipients) {
    mutableStateOf(realRecipients.firstOrNull()?.userId ?: "")
  }
  var selectedGiftId by remember { mutableStateOf(RealBoardGiftsCatalog.first().id) }
  var selectedCategory by remember { mutableStateOf("All") }
  var comboQuantity by remember { mutableIntStateOf(1) }
  var showComboDropdown by remember { mutableStateOf(false) }

  val comboOptions = listOf(1, 10, 66, 99, 520, 1314)
  val categories = listOf("All", "Popular", "Luxury", "Romantic")

  val filteredGifts = remember(selectedCategory) {
    if (selectedCategory == "All") RealBoardGiftsCatalog
    else RealBoardGiftsCatalog.filter { it.category.equals(selectedCategory, ignoreCase = true) }
  }

  val selectedGift = remember(selectedGiftId) {
    RealBoardGiftsCatalog.find { it.id == selectedGiftId } ?: RealBoardGiftsCatalog.first()
  }

  val selectedRecipient = remember(selectedRecipientId, realRecipients) {
    realRecipients.find { it.userId == selectedRecipientId } ?: realRecipients.firstOrNull()
  }

  val totalCost = selectedGift.priceDiamonds * comboQuantity

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(
      usePlatformDefaultWidth = false,
      decorFitsSystemWindows = false,
      dismissOnClickOutside = true
    )
  ) {
    BottomSheetModalDialogContainer(onDismiss = onDismiss) {
      // HALF-SCREEN SHEET CONTAINER
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .fillMaxHeight(0.58f)
          .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
          .border(
            width = 1.4.dp,
            brush = Brush.horizontalGradient(
              listOf(Color(0xFFFF007F), Color(0xFFFFD700), Color(0xFF00E5FF))
            ),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
          )
          .testTag("gift_bottom_sheet"),
        color = Color(0xFF120024)
      ) {
        val responsiveBottomPadding = rememberResponsiveNavigationBarPadding(extraBottomSpacing = 8.dp)
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(start = 14.dp, end = 14.dp, top = 8.dp, bottom = responsiveBottomPadding)
        ) {
          // TOP DRAG HANDLE & CLOSE BUTTON
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .width(36.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0x55FFFFFF))
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "💎 PREMIUM GIFT VAULT",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.8.sp
              )
            }

            IconButton(
              onClick = onDismiss,
              modifier = Modifier.size(26.dp)
            ) {
              Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Close Gifts",
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.size(18.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          // -------------------------------------------------------------------
          // 1. TOP PARTICIPANT AREA: REAL BOARD USERS ONLY (Requirement 8)
          // -------------------------------------------------------------------
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Send to:",
              color = Color(0xFFCE93D8),
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.width(8.dp))

            if (realRecipients.isEmpty()) {
              Text(
                text = "No other users on board",
                color = Color(0x88FFFFFF),
                fontSize = 11.sp
              )
            } else {
              LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                items(realRecipients, key = { it.userId }) { recipient ->
                  val isSelected = recipient.userId == selectedRecipientId
                  Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                      .clip(RoundedCornerShape(12.dp))
                      .clickable { selectedRecipientId = recipient.userId }
                      .padding(horizontal = 4.dp, vertical = 2.dp)
                  ) {
                    Box(
                      modifier = Modifier.size(42.dp),
                      contentAlignment = Alignment.Center
                    ) {
                      Box(
                        modifier = Modifier
                          .size(38.dp)
                          .clip(CircleShape)
                          .background(Color(0xFF260548))
                          .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) Color(0xFFFF007F) else Color(0x44FFFFFF),
                            shape = CircleShape
                          ),
                        contentAlignment = Alignment.Center
                      ) {
                        if (!recipient.avatarUri.isNullOrBlank()) {
                          AsyncImage(
                            model = recipient.avatarUri,
                            contentDescription = recipient.username,
                            modifier = Modifier
                              .fillMaxSize()
                              .clip(CircleShape)
                          )
                        } else {
                          Text(
                            text = recipient.username.take(1).uppercase(),
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                          )
                        }
                      }

                      // Badge: 👑 Host or Seat No.
                      Box(
                        modifier = Modifier
                          .align(Alignment.BottomCenter)
                          .clip(RoundedCornerShape(6.dp))
                          .background(
                            if (recipient.isHost) Brush.horizontalGradient(listOf(Color(0xFFFFB300), Color(0xFFFF6F00)))
                            else Brush.horizontalGradient(listOf(Color(0xFF00E5FF), Color(0xFF0091EA)))
                          )
                          .padding(horizontal = 3.dp, vertical = 0.5.dp)
                      ) {
                        Text(
                          text = if (recipient.isHost) "👑 Host" else "No.${recipient.seatNumber ?: 1}",
                          color = Color.Black,
                          fontSize = 7.5.sp,
                          fontWeight = FontWeight.Black
                        )
                      }
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                      text = recipient.username,
                      color = if (isSelected) Color(0xFFFFD700) else Color(0xCCFFFFFF),
                      fontSize = 9.5.sp,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis
                    )
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          // -------------------------------------------------------------------
          // 2. CATEGORY TABS & REAL SENDER BALANCE PILL
          // -------------------------------------------------------------------
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Category Tabs
            Row(
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              categories.forEach { cat ->
                val isCatSelected = selectedCategory == cat
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isCatSelected) Color(0x33FF007F) else Color(0x11FFFFFF))
                    .border(
                      1.dp,
                      if (isCatSelected) Color(0xFFFF007F) else Color(0x22FFFFFF),
                      RoundedCornerShape(12.dp)
                    )
                    .clickable { selectedCategory = cat }
                    .padding(horizontal = 9.dp, vertical = 3.dp)
                ) {
                  Text(
                    text = cat,
                    color = if (isCatSelected) Color.White else Color(0xAAFFFFFF),
                    fontSize = 10.5.sp,
                    fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Normal
                  )
                }
              }
            }

            // Real User Balance Pill with Recharge '+' Button
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF230040))
                .border(1.dp, Color(0xFFFFD700), RoundedCornerShape(14.dp))
                .clickable { onOpenRecharge() }
                .padding(horizontal = 7.dp, vertical = 3.dp)
            ) {
              Text("💎", fontSize = 11.sp)
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "$currentUserBalance",
                color = Color(0xFFFFD700),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black
              )
              Spacer(modifier = Modifier.width(4.dp))
              Box(
                modifier = Modifier
                  .size(16.dp)
                  .clip(CircleShape)
                  .background(Color(0xFFFFD700)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Filled.Add,
                  contentDescription = "Recharge",
                  tint = Color.Black,
                  modifier = Modifier.size(11.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // -------------------------------------------------------------------
          // 3. GIFTS GRID (8 items visible per view, scrollable)
          // -------------------------------------------------------------------
          LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp),
            modifier = Modifier.weight(1f)
          ) {
            items(filteredGifts, key = { it.id }) { gift ->
              val isSelected = gift.id == selectedGiftId
              val canAfford = currentUserBalance >= gift.priceDiamonds

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(14.dp))
                  .then(
                    if (isSelected) {
                      Modifier.background(
                        Brush.verticalGradient(
                          listOf(Color(0x55FF007F), Color(0x3328004B))
                        )
                      )
                    } else {
                      Modifier.background(Color(0x16FFFFFF))
                    }
                  )
                  .border(
                    width = if (isSelected) 1.8.dp else 0.8.dp,
                    color = if (isSelected) Color(0xFFFF007F) else Color(0x22FFFFFF),
                    shape = RoundedCornerShape(14.dp)
                  )
                  .clickable { selectedGiftId = gift.id }
                  .padding(vertical = 6.dp, horizontal = 4.dp),
                contentAlignment = Alignment.Center
              ) {
                Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.Center
                ) {
                  Text(
                    text = gift.emoji,
                    fontSize = 26.sp
                  )
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = gift.name,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                  )
                  Spacer(modifier = Modifier.height(1.dp))
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = "💎",
                      fontSize = 8.5.sp
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                      text = gift.formattedPrice,
                      color = if (canAfford) Color(0xFFFFD700) else Color(0x88FFD700),
                      fontSize = 9.5.sp,
                      fontWeight = FontWeight.Black
                    )
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          // -------------------------------------------------------------------
          // 4. BOTTOM ACTION ROW: BALANCE CHECK, COMBO SELECTOR & SEND BUTTON
          // -------------------------------------------------------------------
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Left: Total Cost Display
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "Total: ",
                color = Color(0xAAFFFFFF),
                fontSize = 11.sp
              )
              Text(
                text = "💎 $totalCost",
                color = if (currentUserBalance >= totalCost) Color(0xFFFFD700) else Color(0xFFFF5252),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black
              )
            }

            // Right: Multiplier Combo Selector + Send Button
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              // Combo Quantity Pill
              Box {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF260046))
                    .border(1.dp, Color(0xFF00E5FF), RoundedCornerShape(12.dp))
                    .clickable { showComboDropdown = true }
                    .padding(horizontal = 9.dp, vertical = 6.dp)
                ) {
                  Text(
                    text = "x$comboQuantity",
                    color = Color(0xFF00E5FF),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                  )
                  Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Combo",
                    tint = Color(0xFF00E5FF),
                    modifier = Modifier.size(16.dp)
                  )
                }

                DropdownMenu(
                  expanded = showComboDropdown,
                  onDismissRequest = { showComboDropdown = false }
                ) {
                  comboOptions.forEach { qty ->
                    DropdownMenuItem(
                      text = { Text("x$qty (${selectedGift.priceDiamonds * qty} 💎)") },
                      onClick = {
                        comboQuantity = qty
                        showComboDropdown = false
                      }
                    )
                  }
                }
              }

              // Send Action Button
              val canSend = currentUserBalance >= totalCost && selectedRecipient != null
              Button(
                onClick = {
                  val recipient = selectedRecipient
                  if (recipient == null) {
                    Toast.makeText(context, "Please select a recipient on the board!", Toast.LENGTH_SHORT).show()
                    return@Button
                  }

                  if (currentUserBalance < totalCost) {
                    val need = totalCost - currentUserBalance
                    Toast.makeText(context, "Insufficient diamonds! Need 💎 $need more.", Toast.LENGTH_SHORT).show()
                    return@Button
                  }

                  onSendGift(recipient, selectedGift, comboQuantity, totalCost)
                },
                modifier = Modifier
                  .height(38.dp)
                  .width(96.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp)
              ) {
                Box(
                  modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                      if (canSend) Brush.horizontalGradient(listOf(Color(0xFFFF007F), Color(0xFFFF5252)))
                      else Brush.horizontalGradient(listOf(Color(0xFF4A148C), Color(0xFF311B92)))
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "SEND",
                    color = Color.White,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
